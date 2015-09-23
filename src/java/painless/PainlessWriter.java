package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import static painless.PainlessAnalyzer.*;
import static painless.PainlessParser.*;
import static painless.PainlessTypes.*;

class PainlessWriter extends PainlessBaseVisitor<Void>{
    private static class PJump {
        private final ParseTree source;

        private Label abegin;
        private Label aend;
        private Label atrue;
        private Label afalse;

        PJump(final ParseTree source) {
            this.source = source;

            abegin = null;
            aend = null;
            atrue = null;
            afalse = null;
        }
    }

    final static String BASE_CLASS_NAME = PainlessExecutable.class.getName();
    final static String CLASS_NAME = BASE_CLASS_NAME + "$CompiledPainlessExecutable";
    final static String BASE_CLASS_INTERNAL = Type.getType(PainlessExecutable.class).getInternalName();
    final static String CLASS_INTERNAL = BASE_CLASS_INTERNAL + "$CompiledPainlessExecutable";

    static byte[] write(final PTypes ptypes, final String source,
                        final ParseTree tree, final Map<ParseTree, PMetadata> pmetadata) {
        PainlessWriter writer = new PainlessWriter(ptypes, source, tree, pmetadata);

        return writer.getBytes();
    }

    private final PTypes ptypes;
    private final Map<ParseTree, PMetadata> pmetadata;

    private final HashMap<ParseTree, PJump> pbranches;
    private final Deque<PJump> ploops;

    private ClassWriter writer;
    private MethodVisitor execute;

    private PainlessWriter(final PTypes ptypes, final String source,
                           final ParseTree root, final Map<ParseTree, PMetadata> pmetadata) {
        this.ptypes = ptypes;
        this.pmetadata = pmetadata;

        this.pbranches = new HashMap<>();
        this.ploops = new ArrayDeque<>();

        writeBegin(source);
        writeConstructor();
        writeExecute(root);
        writeEnd();
    }

    private void writeBegin(final String source) {
        final int compute = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        final int version = Opcodes.V1_7;
        final int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC;
        final String base = BASE_CLASS_INTERNAL;
        final String name = CLASS_INTERNAL;

        writer = new ClassWriter(compute);
        writer.visit(version, access, name, null, base, null);
        writer.visitSource(source, null);
    }

    private void writeConstructor() {
        final int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC;
        final Method method = Method.getMethod("void <init>(String, String)");
        final String base = Type.getType(PainlessExecutable.class).getInternalName();

        final MethodVisitor constructor = writer.visitMethod(access, method.getName(), method.getDescriptor(), null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitVarInsn(Opcodes.ALOAD, 1);
        constructor.visitVarInsn(Opcodes.ALOAD, 2);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, base, method.getName(), method.getDescriptor(), false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();
    }

    private void writeExecute(final ParseTree root) {
        final int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC;
        final Method method = Method.getMethod("Object execute(java.util.Map);");
        final String signature = "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)Ljava/lang/Object;";

        execute = writer.visitMethod(access, method.getName(), method.getDescriptor(), signature, null);

        execute.visitCode();

        visit(root);

        execute.visitMaxs(0, 0);
        execute.visitEnd();
    }

    private PMetadata getPMetadata(final ParseTree node) {
        final PMetadata nodemd = pmetadata.get(node);

        if (nodemd == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return nodemd;
    }

    private void writeCast(PCast pcast) {

    }

    private void writeTransform(PTransform ptransform) {

    }

    @Override
    public Void visitSource(SourceContext ctx) {
        PMetadata sourcemd = getPMetadata(ctx);

        for (StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }

        if (!sourcemd.allrtn) {
            execute.visitInsn(Opcodes.ACONST_NULL);
            execute.visitInsn(Opcodes.ARETURN);
        }

        return null;
    }

    @Override
    public Void visitIf(IfContext ctx) {
        final ExpressionContext ectx = ctx.expression();
        final boolean pelse = ctx.ELSE() != null;

        final PJump pjump = new PJump(ctx);
        pjump.aend = new Label();
        pjump.afalse = pelse ? new Label() : pjump.aend;

        pbranches.put(ectx, pjump);
        visit(ectx);
        visit(ctx.block(0));

        if (pelse) {
            execute.visitJumpInsn(Opcodes.GOTO, pjump.aend);
            execute.visitLabel(pjump.afalse);
            visit(ctx.block(1));
        }

        execute.visitLabel(pjump.aend);

        return null;
    }

    @Override
    public Void visitWhile(WhileContext ctx) {
        final ExpressionContext ectx = ctx.expression();

        final PJump pjump = new PJump(ctx);
        pjump.abegin = new Label();
        pjump.aend = new Label();
        pjump.afalse = pjump.aend;

        pbranches.put(ectx, pjump);
        ploops.push(pjump);
        execute.visitLabel(pjump.abegin);
        visit(ectx);
        visit(ctx.block());
        execute.visitJumpInsn(Opcodes.GOTO, pjump.abegin);
        execute.visitLabel(pjump.aend);
        ploops.pop();

        return null;
    }

    @Override
    public Void visitDo(DoContext ctx) {
        final ExpressionContext ectx = ctx.expression();

        final PJump pjump = new PJump(ctx);
        pjump.abegin = new Label();
        pjump.atrue = pjump.abegin;
        pjump.aend = new Label();

        pbranches.put(ectx, pjump);
        ploops.push(pjump);
        execute.visitLabel(pjump.atrue);
        visit(ctx.block());
        visit(ctx.expression());
        execute.visitLabel(pjump.aend);
        ploops.pop();

        return null;
    }

    @Override
    public Void visitFor(ForContext ctx) {
        final ExpressionContext ectx0 = ctx.expression(0);

        final PJump pjump = new PJump(ctx);
        pjump.abegin = new Label();
        pjump.aend = new Label();
        pjump.afalse = pjump.aend;

        ploops.push(pjump);

        if (ctx.declaration() != null) {
            visit(ctx.declaration());
        }

        execute.visitLabel(pjump.abegin);

        if (ectx0 != null) {
            pbranches.put(ectx0, pjump);
            visit(ectx0);
        }

        visit(ctx.block());

        if (ctx.expression(1) != null) {
            visit(ctx.expression(1));
        }

        execute.visitJumpInsn(Opcodes.GOTO, pjump.abegin);
        execute.visitLabel(pjump.aend);
        ploops.pop();

        return null;
    }

    @Override
    public Void visitDecl(DeclContext ctx) {
        visit(ctx.declaration());

        return null;
    }

    @Override
    public Void visitContinue(ContinueContext ctx) {
        final PJump pjump = ploops.peek();
        execute.visitJumpInsn(Opcodes.GOTO, pjump.abegin);

        return null;
    }

    @Override
    public Void visitBreak(BreakContext ctx) {
        final PJump pjump = ploops.peek();
        execute.visitJumpInsn(Opcodes.GOTO, pjump.aend);

        return null;
    }

    @Override
    public Void visitReturn(ReturnContext ctx) {
        visit(ctx.expression());
        execute.visitInsn(Opcodes.ARETURN);

        return null;
    }

    @Override
    public Void visitExpr(ExprContext ctx) {
        visit(ctx.expression());

        final PMetadata exprmd = getPMetadata(ctx);

        if (exprmd.constant instanceof PType) {
            final int asize = ((PType)exprmd.constant).getPSort().getASize();

            if (asize == 1) {
                execute.visitInsn(Opcodes.POP);
            } else if (asize == 2) {
                execute.visitInsn(Opcodes.POP2);
            }
        }

        return null;
    }

    @Override
    public Void visitMultiple(MultipleContext ctx) {
        for (StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }

        return null;
    }

    @Override
    public Void visitSingle(SingleContext ctx) {
        visit(ctx.statement());

        return null;
    }

    @Override
    public Void visitEmpty(EmptyContext ctx) {
        return null;
    }

    @Override
    public Void visitDeclaration(DeclarationContext ctx) {
        return null;
    }

    @Override
    public Void visitDecltype(DecltypeContext ctx) {
        return null;
    }

    @Override
    public Void visitExt(ExtContext ctx) {
        return null;
    }

    @Override
    public Void visitComp(CompContext ctx) {
        return null;
    }

    @Override
    public Void visitString(StringContext ctx) {
        return null;
    }

    @Override
    public Void visitBool(BoolContext ctx) {
        return null;
    }

    @Override
    public Void visitConditional(ConditionalContext ctx) {
        return null;
    }

    @Override
    public Void visitAssignment(AssignmentContext ctx) {
        return null;
    }

    @Override
    public Void visitFalse(FalseContext ctx) {
        return null;
    }

    @Override
    public Void visitNumeric(NumericContext ctx) {
        return null;
    }

    @Override
    public Void visitUnary(UnaryContext ctx) {
        return null;
    }

    @Override
    public Void visitPrecedence(PrecedenceContext ctx) {
        return null;
    }

    @Override
    public Void visitCast(CastContext ctx) {
        return null;
    }

    @Override
    public Void visitNull(NullContext ctx) {
        return null;
    }

    @Override
    public Void visitBinary(BinaryContext ctx) {
        return null;
    }

    @Override
    public Void visitChar(CharContext ctx) {
        return null;
    }

    @Override
    public Void visitTrue(TrueContext ctx) {
        return null;
    }

    @Override
    public Void visitExtstart(ExtstartContext ctx) {
        return null;
    }

    @Override
    public Void visitExtprec(ExtprecContext ctx) {
        return null;
    }

    @Override
    public Void visitExtcast(ExtcastContext ctx) {
        return null;
    }

    @Override
    public Void visitExtarray(ExtarrayContext ctx) {
        return null;
    }

    @Override
    public Void visitExtdot(ExtdotContext ctx) {
        return null;
    }

    @Override
    public Void visitExttype(ExttypeContext ctx) {
        return null;
    }

    @Override
    public Void visitExtcall(ExtcallContext ctx) {
        return null;
    }

    @Override
    public Void visitExtmember(ExtmemberContext ctx) {
        return null;
    }

    @Override
    public Void visitArguments(ArgumentsContext ctx) {
        return null;
    }

    private void writeEnd() {
        writer.visitEnd();
    }

    private byte[] getBytes() {
        return writer.toByteArray();
    }
}
