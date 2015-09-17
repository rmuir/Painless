package painless;

import com.sun.org.apache.xpath.internal.operations.Variable;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import static painless.PainlessParser.*;

class PainlessWriter extends PainlessBaseVisitor<Object>{
    final static String BASE_CLASS_NAME = PainlessExecutable.class.getName();
    final static String CLASS_NAME = BASE_CLASS_NAME + "$CompiledPainlessExecutable";
    final static String BASE_CLASS_INTERNAL = Type.getType(PainlessExecutable.class).getInternalName();
    final static String CLASS_INTERNAL = BASE_CLASS_INTERNAL + "$CompiledPainlessExecutable";

    static byte[] write(String source, ParseTree tree) {
        PainlessWriter writer = new PainlessWriter(source, tree);
        return writer.getBytes();
    }

    private ClassWriter writer;
    //private PainlessAdapter execute;

    private PainlessWriter(String source, ParseTree tree) {
        writeBegin(source);
        writeConstructor();
        writeExecute(tree);
        writeEnd();
    }

    private void writeBegin(String source) {
        final int compute = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        final int version = Opcodes.V1_8;
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

    private void writeExecute(ParseTree tree) {
        final int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC;
        final Method method = Method.getMethod("Object execute(java.util.Map);");
        final String signature = "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)Ljava/lang/Object;";

        final MethodVisitor visitor = writer.visitMethod(access, method.getName(), method.getDescriptor(), signature, null);
        /*execute = new PainlessAdapter(visitor);
        execute.visitCode();

        execute.newVariable("this", "executable", 0, false);
        execute.newVariable("input", "map", 0, false);

        visit(tree);

        execute.visitMaxs(0, 0);
        execute.visitEnd();*/
    }

    @Override
    public Object visitSource(SourceContext ctx) {
        //execute.incrementScope();

        for (StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }

        //execute.decrementScope();

        return null;
    }

    @Override
    public Object visitIf(IfContext ctx) {
        return null;
    }

    @Override
    public Object visitWhile(WhileContext ctx) {
        return null;
    }

    @Override
    public Object visitDo(DoContext ctx) {
        return null;
    }

    @Override
    public Object visitFor(ForContext ctx) {
        return null;
    }

    @Override
    public Object visitContinue(ContinueContext ctx) {
        return null;
    }

    @Override
    public Object visitBreak(BreakContext ctx) {
        return null;
    }

    @Override
    public Object visitDecl(DeclContext ctx) {
        visit(ctx.declaration());

        return null;
    }

    @Override
    public Object visitExpr(ExprContext ctx) {
        return null;
    }

    @Override
    public Object visitMultiple(MultipleContext ctx) {
        return null;
    }

    @Override
    public Object visitSingle(SingleContext ctx) {
        return null;
    }

    @Override
    public Object visitDeclaration(DeclarationContext ctx) {
        /*PainlessVariable variable = null;
        String declaration = ctx.TYPE().getText();

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof VariableContext) {
                VariableContext vctx = (VariableContext)cctx;

                String name = vctx.ID().getText();
                int dimensions = vctx.LBRACE().size();

                variable = execute.newVariable(name, declaration, dimensions, true);
            } else if (cctx instanceof ExpressionContext) {
                execute.pushExpectedType(variable.getType());
                visit(cctx);
                execute.storeVariable(variable);
                execute.popExpectedType();
            }
        }*/

        return null;
    }

    @Override
    public Object visitExt(ExtContext ctx) {
        return null;
    }

    @Override
    public Object visitString(StringContext ctx) {
        return null;
    }

    @Override
    public Object visitConditional(ConditionalContext ctx) {
        return null;
    }

    @Override
    public Object visitAssignment(AssignmentContext ctx) {
        return null;
    }

    @Override
    public Object visitFalse(FalseContext ctx) {
        return null;
    }

    @Override
    public Object visitNumeric(NumericContext ctx) {
        /*if (!PainlessTypeUtility.isDescriptorNumeric(aexpected.peek())) {
            throw new IllegalStateException();
        }

        if (ctx.DECIMAL() != null) {
            String value = ctx.DECIMAL().getText();
            double push = Double.parseDouble(value);

            if (push == 0.0) {
                execute.visitInsn(Opcodes.DCONST_0);
            } else if (push == 1.0) {
                execute.visitInsn(Opcodes.DCONST_1);
            } else {
                execute.visitLdcInsn(push);
            }

        } else {
            String value;
            int base;

            if (ctx.HEX() != null) {
                value = ctx.HEX().getText().substring(2);
                base = 16;
            } else if (ctx.INTEGER() != null) {
                value = ctx.INTEGER().getText();
                base = 10;
            } else if (ctx.OCTAL() != null) {
                value = ctx.OCTAL().getText().substring(1);
                base = 8;
            } else {
                throw new IllegalStateException();
            }

            long push = Long.parseLong(value, base);

            if (push == -1) {
                execute.visitInsn(Opcodes.ICONST_M1);
            } else if (push == 0) {
                execute.visitInsn(Opcodes.ICONST_0);
            } else if (push == 1) {
                execute.visitInsn(Opcodes.ICONST_1);
            } else if (push == 2) {
                execute.visitInsn(Opcodes.ICONST_2);
            } else if (push == 3) {
                execute.visitInsn(Opcodes.ICONST_3);
            } else if (push == 4) {
                execute.visitInsn(Opcodes.ICONST_4);
            } else if (push == 5) {
                execute.visitInsn(Opcodes.ICONST_5);
            } else if (push >= Byte.MIN_VALUE && push <= Byte.MAX_VALUE) {
                execute.visitIntInsn(Opcodes.BIPUSH, (int) push);
            } else if (push >= Short.MIN_VALUE && push <= Short.MAX_VALUE) {
                execute.visitIntInsn(Opcodes.SIPUSH, (int) push);
            } else {
                execute.visitLdcInsn(push);
            }
        }*/

        return null;
    }

    @Override
    public Object visitUnary(UnaryContext ctx) {
        return null;
    }

    @Override
    public Object visitPrecedence(PrecedenceContext ctx) {
        return null;
    }

    @Override
    public Object visitNull(NullContext ctx) {
        return null;
    }

    @Override
    public Object visitChar(CharContext ctx) {
        return null;
    }

    @Override
    public Object visitTrue(TrueContext ctx) {
        return null;
    }

    @Override
    public Object visitArguments(ArgumentsContext ctx) {
        return null;
    }

    @Override public Object visitCast(PainlessParser.CastContext ctx) {
        return visitChildren(ctx);
    }

    private void writeEnd() {
        writer.visitEnd();
    }

    private byte[] getBytes() {
        return writer.toByteArray();
    }
}
