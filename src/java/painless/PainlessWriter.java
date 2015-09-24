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

    private final HashMap<ParseTree, PJump> abranches;
    private final Deque<PJump> ploops;

    private ClassWriter writer;
    private MethodVisitor execute;

    private PainlessWriter(final PTypes ptypes, final String source,
                           final ParseTree root, final Map<ParseTree, PMetadata> pmetadata) {
        this.ptypes = ptypes;
        this.pmetadata = pmetadata;

        this.abranches = new HashMap<>();
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

    private void checkWritePCast(final PMetadata pmetadata) {
        if (pmetadata.getPCast() != null) {
            writePCast(pmetadata.getPCast());
        } else if (pmetadata.getPTransform() != null) {
            writePTransform(pmetadata.getPTransform());
        }
    }

    private void writePCast(final PCast pcast) {

    }

    private void writePTransform(final PTransform ptransform) {

    }

    private void checkWritePBranch(final PJump abranch) {
        if (abranch != null) {
            if (abranch.atrue != null) {
                execute.visitJumpInsn(Opcodes.IF_ICMPNE, abranch.atrue);
            } else if (abranch.afalse != null) {
                execute.visitJumpInsn(Opcodes.IF_ICMPEQ, abranch.afalse);
            }
        }
    }

    private void pushPConstant(final Object constant) {
        if (constant instanceof Number) {
            writePNumeric(constant);
        } else if (constant instanceof Character) {
            writePNumeric((int)(char)constant);
        } else if (constant instanceof String) {
            writePString(constant);
        } else if (constant instanceof Boolean) {
            writePBoolean(constant);
        } else if (constant != null) {
            throw new IllegalStateException(); // TODO: message
        }
    }

    private void writePNumeric(final Object numeric) {
        if (numeric instanceof Double) {
            final long bits = Double.doubleToLongBits((Double)numeric);

            if (bits == 0L) {
                execute.visitInsn(Opcodes.DCONST_0);
            } else if (bits == 0x3ff0000000000000L) {
                execute.visitInsn(Opcodes.DCONST_1);
            } else {
                execute.visitLdcInsn(numeric);
            }
        } else if (numeric instanceof Float) {
            final int bits = Float.floatToIntBits((Float)numeric);

            if (bits == 0L) {
                execute.visitInsn(Opcodes.FCONST_0);
            } else if (bits == 0x3f800000) {
                execute.visitInsn(Opcodes.FCONST_1);
            } else if (bits == 0x40000000) {
                execute.visitInsn(Opcodes.FCONST_2);
            } else {
                execute.visitLdcInsn(numeric);
            }
        } else if (numeric instanceof Long) {
            final long value = (long)numeric;

            if (value == 0L) {
                execute.visitInsn(Opcodes.LCONST_0);
            } else if (value == 1L) {
                execute.visitInsn(Opcodes.LCONST_1);
            } else {
                execute.visitLdcInsn(value);
            }
        } else if (numeric instanceof Number) {
            final int value = ((Number)numeric).intValue();

            if (value == -1) {
                execute.visitInsn(Opcodes.ICONST_M1);
            } else if (value == 0) {
                execute.visitInsn(Opcodes.ICONST_0);
            } else if (value == 1) {
                execute.visitInsn(Opcodes.ICONST_1);
            } else if (value == 2) {
                execute.visitInsn(Opcodes.ICONST_2);
            } else if (value == 3) {
                execute.visitInsn(Opcodes.ICONST_3);
            } else if (value == 4) {
                execute.visitInsn(Opcodes.ICONST_4);
            } else if (value == 5) {
                execute.visitInsn(Opcodes.ICONST_5);
            } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                execute.visitIntInsn(Opcodes.BIPUSH, value);
            } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                execute.visitIntInsn(Opcodes.SIPUSH, value);
            } else {
                execute.visitLdcInsn(value);
            }
        } else {
            throw new IllegalStateException(); // TODO: message
        }
    }

    private void writePString(final Object constant) {
        if (constant instanceof String) {
            execute.visitLdcInsn(constant);
        } else {
            throw new IllegalStateException(); // TODO: message
        }
    }

    private void writePBoolean(final Object constant) {
        if (constant instanceof Boolean) {
            boolean value = (boolean)constant;

            if (value) {
                execute.visitInsn(Opcodes.ICONST_1);
            } else {
                execute.visitInsn(Opcodes.ICONST_0);
            }
        } else {
            throw new IllegalStateException(); // TODO: message
        }
    }

    @Override
    public Void visitSource(SourceContext ctx) {
        final PMetadata sourcemd = getPMetadata(ctx);

        for (final StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }

        if (!sourcemd.doAllReturn()) {
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

        abranches.put(ectx, pjump);
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
        final PMetadata whildmd = getPMetadata(ctx);
        final Object constant = whildmd.getConstant();

        final ExpressionContext ectx = ctx.expression();
        final BlockContext bctx = ctx.block();

        final PJump pjump = new PJump(ctx);
        pjump.abegin = new Label();
        pjump.aend = new Label();
        pjump.afalse = pjump.aend;

        ploops.push(pjump);
        execute.visitLabel(pjump.abegin);

        if (!(constant instanceof Boolean && (boolean)constant)) {
            abranches.put(ectx, pjump);
            visit(ectx);
        }

        if (bctx != null) {
            visit(ctx.block());
        }

        execute.visitJumpInsn(Opcodes.GOTO, pjump.abegin);
        execute.visitLabel(pjump.aend);
        ploops.pop();

        return null;
    }

    @Override
    public Void visitDo(DoContext ctx) {
        final PMetadata domd = getPMetadata(ctx);
        final Object constant = domd.getConstant();

        final ExpressionContext ectx = ctx.expression();
        final BlockContext bctx = ctx.block();

        final PJump pjump = new PJump(ctx);
        pjump.abegin = new Label();
        pjump.aend = new Label();
        pjump.atrue = pjump.abegin;

        ploops.push(pjump);
        execute.visitLabel(pjump.abegin);
        visit(bctx);

        if (!(constant instanceof Boolean && (boolean)constant)) {
            abranches.put(ectx, pjump);
            visit(ectx);
        }

        execute.visitJumpInsn(Opcodes.GOTO, pjump.abegin);
        execute.visitLabel(pjump.aend);
        ploops.pop();

        return null;
    }

    @Override
    public Void visitFor(ForContext ctx) {
        final PMetadata formd = getPMetadata(ctx);
        final Object constant = formd.getConstant();

        final ExpressionContext ectx0 = ctx.expression(0);
        final ExpressionContext ectx1 = ctx.expression(1);
        final BlockContext bctx = ctx.block();

        final PJump pjump = new PJump(ctx);
        pjump.abegin = new Label();
        pjump.aend = new Label();
        pjump.afalse = pjump.aend;

        ploops.push(pjump);

        if (ctx.declaration() != null) {
            visit(ctx.declaration());
        }

        execute.visitLabel(pjump.abegin);

        if (!(constant instanceof Boolean && (boolean)constant)) {
            abranches.put(ectx0, pjump);
            visit(ectx0);
        }

        if (bctx != null) {
            visit(bctx);
        }

        if (ectx1 != null) {
            visit(ectx1);
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

        if (exprmd.getConstant() instanceof PType) {
            final int asize = ((PType)exprmd.getConstant()).getPSort().getASize();

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
        for (final StatementContext sctx : ctx.statement()) {
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
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitDeclaration(DeclarationContext ctx) {
        PMetadata declarationmd = getPMetadata(ctx);
        Map pvariables = (Map)declarationmd.getConstant();

        for (final ExpressionContext ectx : ctx.expression()) {
            final PVariable pvariable = (PVariable)pvariables.get(ctx);

            visit(ectx);
            //TODO: write to slot
        }

        return null;
    }

    @Override
    public Void visitDecltype(DecltypeContext ctx) {
        throw new UnsupportedOperationException(); //TODO: message
    }

    @Override
    public Void visitPrecedence(PrecedenceContext ctx) {
        final PJump abranch = abranches.get(ctx);
        final ExpressionContext ectx = ctx.expression();

        if (abranch != null) {
            abranches.put(ectx, abranch);
        }

        visit(ectx);

        return null;
    }

    @Override
    public Void visitNumeric(NumericContext ctx) {
        final PMetadata numericmd = getPMetadata(ctx);
        final Object numeric = numericmd.getConstant();
        final PJump abranch = abranches.get(ctx);

        writePNumeric(numeric);
        checkWritePCast(numericmd);
        checkWritePBranch(abranch);

        return null;
    }

    @Override
    public Void visitString(StringContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);
        final Object string = stringmd.getConstant();
        final PJump abranch = abranches.get(ctx);

        writePString(string);
        checkWritePCast(stringmd);
        checkWritePBranch(abranch);

        return null;
    }

    @Override
    public Void visitChar(CharContext ctx) {
        final PMetadata charmd = getPMetadata(ctx);
        final Object character = charmd.getConstant();
        final PJump abranch = abranches.get(ctx);

        writePNumeric((int)(char)character);
        checkWritePCast(charmd);
        checkWritePBranch(abranch);

        return null;
    }

    @Override
    public Void visitTrue(TrueContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);
        final PJump abranch = abranches.get(ctx);

        if (abranch == null) {
            writePBoolean(true);
            checkWritePCast(truemd);
        } else if (abranch.atrue != null) {
            execute.visitJumpInsn(Opcodes.GOTO, abranch.atrue);
        }

        return null;
    }

    @Override
    public Void visitFalse(FalseContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);
        final PJump abranch = abranches.get(ctx);

        if (abranch == null) {
            writePBoolean(false);
            checkWritePCast(truemd);
        } else if (abranch.afalse != null) {
            execute.visitJumpInsn(Opcodes.GOTO, abranch.afalse);
        }

        return null;
    }

    @Override
    public Void visitNull(NullContext ctx) {
        final PMetadata nullmd = getPMetadata(ctx);
        final PJump abranch = abranches.get(ctx);

        execute.visitInsn(Opcodes.ACONST_NULL);
        checkWritePCast(nullmd);
        checkWritePBranch(abranch);

        return null;
    }

    @Override
    public Void visitExt(ExtContext ctx) {
        final PJump abranch = abranches.get(ctx);
        final ExtstartContext ectx = ctx.extstart();

        if (abranch != null) {
            abranches.put(ectx, abranch);
        }

        visit(ectx);

        return null;
    }

    @Override
    public Void visitUnary(UnaryContext ctx) {
        final PMetadata unarymd = getPMetadata(ctx);
        final Object constant = unarymd.getConstant();
        PJump abranch = abranches.get(ctx);

        final ExpressionContext ectx = ctx.expression();

        if (ctx.BOOLNOT() != null) {
            if (abranch == null) {

            } else {
                PJump localabranch = new PJump(ctx);
                localabranch.atrue = abranch.afalse;
                localabranch.afalse = abranch.atrue;
                abranches.put(ectx, localabranch);
                visit(ectx);
                abranch = null;
            }
        } else if (ctx.BWNOT() != null) {

        }

        checkWritePCast(unarymd);
        checkWritePBranch(abranch);

        return null;
    }

    @Override
    public Void visitCast(CastContext ctx) {
        return null;
    }

    @Override
    public Void visitBinary(BinaryContext ctx) {
        return null;
    }

    @Override
    public Void visitComp(CompContext ctx) {
        return null;
    }

    @Override
    public Void visitBool(BoolContext ctx) {
        final PMetadata unarymd = getPMetadata(ctx);
        final Object constant = unarymd.getConstant();
        final PJump abranch = abranches.get(ctx);

        if (constant == null) {
            final ExpressionContext ectx0 = ctx.expression(0);
            final ExpressionContext ectx1 = ctx.expression(1);

            if (abranch == null) {
                if (ctx.BOOLAND() != null) {
                    final PJump localabranch = new PJump(ctx);
                    localabranch.aend = new Label();
                    localabranch.afalse = new Label();

                    abranches.put(ectx0, localabranch);
                    visit(ectx0);
                    abranches.put(ectx1, localabranch);
                    visit(ectx1);

                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitJumpInsn(Opcodes.GOTO, localabranch.aend);
                    execute.visitLabel(localabranch.afalse);
                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitLabel(localabranch.aend);

                    checkWritePCast(unarymd);
                } else if (ctx.BOOLOR() != null) {
                    final PJump abranch0 = new PJump(ctx);
                    abranch0.atrue = new Label();
                    final PJump abranch1 = new PJump(ctx);
                    abranch1.afalse = new Label();
                    abranch1.aend = new Label();

                    abranches.put(ectx0, abranch0);
                    visit(ectx0);
                    abranches.put(ectx1, abranch1);
                    visit(ectx1);

                    execute.visitLabel(abranch0.atrue);
                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitJumpInsn(Opcodes.GOTO, abranch1.aend);
                    execute.visitLabel(abranch1.afalse);
                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitLabel(abranch1.aend);
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                if (ctx.BOOLAND() != null) {

                } else if (ctx.BOOLOR() != null) {

                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            if (abranch == null) {
                pushPConstant(constant);
                checkWritePCast(unarymd);
            } else {
                if ((boolean)constant && abranch.atrue != null) {
                    execute.visitLabel(abranch.atrue);
                } else if (!(boolean)constant && abranch.afalse != null) {
                    execute.visitLabel(abranch.afalse);
                }
            }
        }

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
    public Void visitExtstart(ExtstartContext ctx) {
        return null;
    }

    @Override
    public Void visitExtprec(ExtprecContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtcast(ExtcastContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtarray(ExtarrayContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtdot(ExtdotContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExttype(ExttypeContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtcall(ExtcallContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtmember(ExtmemberContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitArguments(ArgumentsContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    private void writeEnd() {
        writer.visitEnd();
    }

    private byte[] getBytes() {
        return writer.toByteArray();
    }
}
