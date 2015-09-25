package painless;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import static painless.PainlessAnalyzer.*;
import static painless.PainlessExternal.*;
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

    private static Type getDescriptorFromJClass(final Class jclass) {
        final String jnamestr = jclass.getName();

        switch (jnamestr) {
            case "void":
                return Type.VOID_TYPE;
            case "boolean":
                return Type.BOOLEAN_TYPE;
            case "byte":
                return Type.BYTE_TYPE;
            case "char":
                return Type.CHAR_TYPE;
            case "short":
                return Type.SHORT_TYPE;
            case "int":
                return Type.INT_TYPE;
            case "long":
                return Type.LONG_TYPE;
            case "float":
                return Type.FLOAT_TYPE;
            case "double":
                return Type.DOUBLE_TYPE;
            default:
                return Type.getType(jclass);
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

    private void checkWritePCast(final PMetadata pmetadata) {
        if (pmetadata.getPCast() != null) {
            writePCast(pmetadata.getPCast());
        } else if (pmetadata.getPTransform() != null) {
            writePTransform(pmetadata.getPTransform());
        }
    }

    private void writePCast(final PCast pcast) {
        final PType fromptype = pcast.getPFrom();
        final PType toptype = pcast.getPTo();
        final PSort frompsort = fromptype.getPSort();
        final PSort topsort = toptype.getPSort();

        if (frompsort.isPNumeric() && topsort.isPNumeric()) {
            switch (frompsort) {
                case BYTE:
                    switch (topsort) {
                        case SHORT:
                            execute.visitInsn(Opcodes.I2S);
                            break;
                        case CHAR:
                            execute.visitInsn(Opcodes.I2C);
                            break;
                        case LONG:
                            execute.visitInsn(Opcodes.I2L);
                            break;
                        case FLOAT:
                            execute.visitInsn(Opcodes.I2F);
                            break;
                        case DOUBLE:
                            execute.visitInsn(Opcodes.I2D);
                            break;
                    }
                    break;
                case SHORT:
                    switch (topsort) {
                        case BYTE:
                            execute.visitInsn(Opcodes.I2B);
                            break;
                        case CHAR:
                            execute.visitInsn(Opcodes.I2C);
                            break;
                        case LONG:
                            execute.visitInsn(Opcodes.I2L);
                            break;
                        case FLOAT:
                            execute.visitInsn(Opcodes.I2F);
                            break;
                        case DOUBLE:
                            execute.visitInsn(Opcodes.I2D);
                            break;
                    }
                    break;
                case CHAR:
                    switch (topsort) {
                        case BYTE:
                            execute.visitInsn(Opcodes.I2B);
                            break;
                        case SHORT:
                            execute.visitInsn(Opcodes.I2S);
                            break;
                        case LONG:
                            execute.visitInsn(Opcodes.I2L);
                            break;
                        case FLOAT:
                            execute.visitInsn(Opcodes.I2F);
                            break;
                        case DOUBLE:
                            execute.visitInsn(Opcodes.I2D);
                            break;
                    }
                    break;
                case INT:
                    switch (topsort) {
                        case BYTE:
                            execute.visitInsn(Opcodes.I2B);
                            break;
                        case SHORT:
                            execute.visitInsn(Opcodes.I2S);
                            break;
                        case CHAR:
                            execute.visitInsn(Opcodes.I2C);
                            break;
                        case LONG:
                            execute.visitInsn(Opcodes.I2L);
                            break;
                        case FLOAT:
                            execute.visitInsn(Opcodes.I2F);
                            break;
                        case DOUBLE:
                            execute.visitInsn(Opcodes.I2D);
                            break;
                    }
                    break;
                case LONG:
                    switch (topsort) {
                        case BYTE:
                            execute.visitInsn(Opcodes.L2I);
                            execute.visitInsn(Opcodes.I2B);
                            break;
                        case SHORT:
                            execute.visitInsn(Opcodes.L2I);
                            execute.visitInsn(Opcodes.I2S);
                            break;
                        case CHAR:
                            execute.visitInsn(Opcodes.L2I);
                            execute.visitInsn(Opcodes.I2C);
                            break;
                        case INT:
                            execute.visitInsn(Opcodes.L2I);
                            break;
                        case FLOAT:
                            execute.visitInsn(Opcodes.L2F);
                            break;
                        case DOUBLE:
                            execute.visitInsn(Opcodes.L2D);
                            break;
                    }
                    break;
                case FLOAT:
                    switch (topsort) {
                        case BYTE:
                            execute.visitInsn(Opcodes.F2I);
                            execute.visitInsn(Opcodes.I2B);
                            break;
                        case SHORT:
                            execute.visitInsn(Opcodes.F2I);
                            execute.visitInsn(Opcodes.I2S);
                            break;
                        case CHAR:
                            execute.visitInsn(Opcodes.F2I);
                            execute.visitInsn(Opcodes.I2C);
                            break;
                        case INT:
                            execute.visitInsn(Opcodes.F2I);
                            break;
                        case LONG:
                            execute.visitInsn(Opcodes.F2L);
                            break;
                        case DOUBLE:
                            execute.visitInsn(Opcodes.F2D);
                            break;
                    }
                    break;
                case DOUBLE:
                    switch (topsort) {
                        case BYTE:
                            execute.visitInsn(Opcodes.D2I);
                            execute.visitInsn(Opcodes.I2B);
                            break;
                        case SHORT:
                            execute.visitInsn(Opcodes.D2I);
                            execute.visitInsn(Opcodes.I2S);
                            break;
                        case CHAR:
                            execute.visitInsn(Opcodes.D2I);
                            execute.visitInsn(Opcodes.I2C);
                            break;
                        case INT:
                            execute.visitInsn(Opcodes.D2I);
                            break;
                        case LONG:
                            execute.visitInsn(Opcodes.D2L);
                            break;
                        case FLOAT:
                            execute.visitInsn(Opcodes.D2F);
                            break;
                    }
                    break;
            }
        } else {
            execute.visitTypeInsn(Opcodes.CHECKCAST, toptype.getADescriptor());
        }
    }

    private void writePTransform(final PTransform ptransform) {
        final PMethod pmethod = ptransform.getPMethod();
        final java.lang.reflect.Method jmethod = pmethod.getJMethod();
        final int modifiers = jmethod.getModifiers();
        Class jowner = pmethod.getPOwner().getJClass();

        final PType pcastfrom = ptransform.getJCastFrom();
        final PType pcastto = ptransform.getJCastTo();

        if (pcastfrom != null) {
            execute.visitTypeInsn(Opcodes.CHECKCAST, pcastfrom.getADescriptor());
        }



        if (Modifier.isStatic(modifiers)) {
            //execute.visitMethodInsn(Opcodes.INVOKESTATIC, jowner.getName(), pmethod.getPName(), "", jmethod.isDefault());
        } else if (Modifier.isInterface(modifiers)) {

        } else {

        }

        if (pcastto != null) {
            execute.visitTypeInsn(Opcodes.CHECKCAST, pcastto.getADescriptor());
        }
    }

    private void checkWritePBranch(final PJump pbranch) {
        if (pbranch != null) {
            if (pbranch.atrue != null) {
                execute.visitJumpInsn(Opcodes.IF_ICMPNE, pbranch.atrue);
            } else if (pbranch.afalse != null) {
                execute.visitJumpInsn(Opcodes.IF_ICMPEQ, pbranch.afalse);
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
            pbranches.put(ectx, pjump);
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
            pbranches.put(ectx, pjump);
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
            pbranches.put(ectx0, pjump);
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

        // TODO : assign variables

        return null;
    }

    @Override
    public Void visitDecltype(DecltypeContext ctx) {
        throw new UnsupportedOperationException(); //TODO: message
    }

    @Override
    public Void visitPrecedence(PrecedenceContext ctx) {
        final PJump pbranch = pbranches.get(ctx);
        final ExpressionContext ectx = ctx.expression();

        if (pbranch != null) {
            pbranches.put(ectx, pbranch);
        }

        visit(ectx);

        return null;
    }

    @Override
    public Void visitNumeric(NumericContext ctx) {
        final PMetadata numericmd = getPMetadata(ctx);
        final Object numeric = numericmd.getConstant();
        final PJump pbranch = pbranches.get(ctx);

        writePNumeric(numeric);
        checkWritePCast(numericmd);
        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitString(StringContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);
        final Object string = stringmd.getConstant();
        final PJump pbranch = pbranches.get(ctx);

        writePString(string);
        checkWritePCast(stringmd);
        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitChar(CharContext ctx) {
        final PMetadata charmd = getPMetadata(ctx);
        final Object character = charmd.getConstant();
        final PJump pbranch = pbranches.get(ctx);

        writePNumeric((int)(char)character);
        checkWritePCast(charmd);
        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitTrue(TrueContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);
        final PJump pbranch = pbranches.get(ctx);

        if (pbranch == null) {
            writePBoolean(true);
            checkWritePCast(truemd);
        } else if (pbranch.atrue != null) {
            execute.visitJumpInsn(Opcodes.GOTO, pbranch.atrue);
        }

        return null;
    }

    @Override
    public Void visitFalse(FalseContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);
        final PJump pbranch = pbranches.get(ctx);

        if (pbranch == null) {
            writePBoolean(false);
            checkWritePCast(truemd);
        } else if (pbranch.afalse != null) {
            execute.visitJumpInsn(Opcodes.GOTO, pbranch.afalse);
        }

        return null;
    }

    @Override
    public Void visitNull(NullContext ctx) {
        final PMetadata nullmd = getPMetadata(ctx);
        final PJump pbranch = pbranches.get(ctx);

        execute.visitInsn(Opcodes.ACONST_NULL);
        checkWritePCast(nullmd);
        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitExt(ExtContext ctx) {
        final PJump pbranch = pbranches.get(ctx);
        final ExtstartContext ectx = ctx.extstart();

        if (pbranch != null) {
            pbranches.put(ectx, pbranch);
        }

        visit(ectx);

        return null;
    }

    @Override
    public Void visitUnary(UnaryContext ctx) {
        final PMetadata unarymd = getPMetadata(ctx);
        final Object constant = unarymd.getConstant();
        final PJump pbranch = pbranches.get(ctx);

        if (constant == null) {
            final ExpressionContext ectx = ctx.expression();

            if (ctx.BOOLNOT() != null) {
                if (pbranch == null) {
                    final PJump localpbranch = new PJump(ctx);
                    localpbranch.afalse = new Label();
                    final Label aend = new Label();

                    pbranches.put(ectx, localpbranch);
                    visit(ectx);

                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitJumpInsn(Opcodes.GOTO, aend);
                    execute.visitLabel(localpbranch.afalse);
                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitLabel(aend);
                } else {
                    final PJump localpbranch = new PJump(ctx);
                    localpbranch.atrue = pbranch.afalse;
                    localpbranch.afalse = pbranch.atrue;

                    pbranches.put(ectx, localpbranch);
                    visit(ectx);

                    checkWritePCast(unarymd);
                }
            } else {
                final PType ptype = unarymd.getCastPTypes()[0];
                final PSort psort = ptype.getPSort();

                visit(ectx);

                if (ctx.BWNOT() != null) {
                    if (psort == PSort.INT) {
                        pushPConstant(-1);
                        execute.visitInsn(Opcodes.IXOR);
                    } else {
                        pushPConstant(-1L);
                        execute.visitInsn(Opcodes.LXOR);
                    }
                } else if (ctx.SUB() != null) {
                    if (psort == PSort.INT) {
                        execute.visitInsn(Opcodes.INEG);
                    } else if (psort == PSort.LONG) {
                        execute.visitInsn(Opcodes.LNEG);
                    } else if (psort == PSort.FLOAT) {
                        execute.visitInsn(Opcodes.FNEG);
                    } else {
                        execute.visitInsn(Opcodes.DNEG);
                    }
                }

                checkWritePCast(unarymd);
                checkWritePBranch(pbranch);
            }
        } else {
            if (ctx.BOOLNOT() != null) {
                if (pbranch == null) {
                    pushPConstant(constant);
                    checkWritePCast(unarymd);
                } else {
                    if ((boolean)constant && pbranch.atrue != null) {
                        execute.visitLabel(pbranch.atrue);
                    } else if (!(boolean)constant && pbranch.afalse != null) {
                        execute.visitLabel(pbranch.afalse);
                    }
                }
            } else {
                pushPConstant(constant);
                checkWritePCast(unarymd);
                checkWritePBranch(pbranch);
            }
        }

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
        final PJump pbranch = pbranches.get(ctx);

        if (constant == null) {
            final ExpressionContext ectx0 = ctx.expression(0);
            final ExpressionContext ectx1 = ctx.expression(1);

            if (pbranch == null) {
                if (ctx.BOOLAND() != null) {
                    final PJump localpbranch = new PJump(ctx);
                    localpbranch.afalse = new Label();
                    final Label aend = new Label();

                    pbranches.put(ectx0, localpbranch);
                    visit(ectx0);
                    pbranches.put(ectx1, localpbranch);
                    visit(ectx1);

                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitJumpInsn(Opcodes.GOTO, aend);
                    execute.visitLabel(localpbranch.afalse);
                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitLabel(aend);
                } else if (ctx.BOOLOR() != null) {
                    final PJump pbranch0 = new PJump(ctx);
                    pbranch0.atrue = new Label();
                    final PJump pbranch1 = new PJump(ctx);
                    pbranch1.afalse = new Label();
                    final Label aend = new Label();

                    pbranches.put(ectx0, pbranch0);
                    visit(ectx0);
                    pbranches.put(ectx1, pbranch1);
                    visit(ectx1);

                    execute.visitLabel(pbranch0.atrue);
                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitJumpInsn(Opcodes.GOTO, aend);
                    execute.visitLabel(pbranch1.afalse);
                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitLabel(aend);
                } else {
                    throw new IllegalStateException(); // TODO: message
                }

                checkWritePCast(unarymd);
            } else {
                if (ctx.BOOLAND() != null) {
                    final PJump pbranch0 = new PJump(ctx);
                    pbranch0.afalse = pbranch.afalse == null ? new Label() : pbranch.afalse;
                    final PJump pbranch1 = new PJump(ctx);
                    pbranch1.atrue = pbranch.atrue;
                    pbranch1.afalse = pbranch.afalse;

                    pbranches.put(ectx0, pbranch0);
                    visit(ectx0);
                    pbranches.put(ectx1, pbranch1);
                    visit(ectx1);

                    if (pbranch.afalse == null) {
                        execute.visitLabel(pbranch0.afalse);
                    }
                } else if (ctx.BOOLOR() != null) {
                    final PJump pbranch0 = new PJump(ctx);
                    pbranch0.atrue = pbranch.atrue == null ? new Label() : pbranch.atrue;
                    final PJump pbranch1 = new PJump(ctx);
                    pbranch1.atrue = pbranch.atrue;
                    pbranch1.afalse = pbranch.afalse;

                    pbranches.put(ectx0, pbranch0);
                    visit(ectx0);
                    pbranches.put(ectx1, pbranch1);
                    visit(ectx1);

                    if (pbranch.atrue == null) {
                        execute.visitLabel(pbranch0.atrue);
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            if (pbranch == null) {
                pushPConstant(constant);
                checkWritePCast(unarymd);
            } else {
                if ((boolean)constant && pbranch.atrue != null) {
                    execute.visitLabel(pbranch.atrue);
                } else if (!(boolean)constant && pbranch.afalse != null) {
                    execute.visitLabel(pbranch.afalse);
                }
            }
        }

        return null;
    }

    @Override
    public Void visitConditional(ConditionalContext ctx) {
        return null;
    }

    /*@Override
    public Void visitAssignment(AssignmentContext ctx) {
        final PMetadata assignmentmd = getPMetadata(ctx);
        final PJump pbranch = pbranches.get(ctx);
        final boolean righthand = assignmentmd.isRightHand();

        final ExpressionContext ectx1 = ctx.expression();
        final ExtstartContext ectx0 = ctx.extstart();

        visit(ectx1);
        visit(ectx0);

        if (righthand) {
            checkWritePCast(assignmentmd);
            checkWritePBranch(pbranch);
        }

        return null;
    }

    @Override
    public Void visitExtstart(ExtstartContext ctx) {
        final PMetadata extstartmd = getPMetadata(ctx);
        final PExternal pexternal = extstartmd.getPExternal();
        boolean lefthand = extstartmd.isLeftHand();
        boolean righthand = extstartmd.isRightHand();

        while (lefthand || righthand) {
            final Iterator<PSegment> psegments = pexternal.getIterator();

            while (psegments.hasNext()) {
                final PSegment psegment = psegments.next();
                final SType stype = psegment.getSType();
                final Object svalue = psegment.getSValue();
                final boolean store = lefthand && pexternal.isLast(psegment);

                switch (stype) {
                    case TYPE:
                        break;
                    case VARIABLE:
                        final PVariable pvariable = (PVariable)svalue;
                        final int aslot = pvariable.getASlot();
                        final PType ptype = pvariable.getPType();
                        final PSort psort = ptype.getPSort();

                        switch (psort) {
                            case BOOL:
                            case BYTE:
                            case SHORT:
                            case CHAR:
                            case INT:
                                execute.visitVarInsn(store ? Opcodes.ISTORE : Opcodes.ILOAD, aslot);
                                break;
                            case LONG:
                                execute.visitVarInsn(store ? Opcodes.LSTORE : Opcodes.LLOAD, aslot);
                            case FLOAT:
                                execute.visitVarInsn(store ? Opcodes.FSTORE : Opcodes.FLOAD, aslot);
                            case DOUBLE:
                                execute.visitVarInsn(store ? Opcodes.DSTORE : Opcodes.DLOAD, aslot);
                            default:
                                execute.visitVarInsn(store ? Opcodes.ASTORE : Opcodes.ALOAD, aslot);
                        }

                        break;
                    case CONSTRUCTOR:
                        break;
                    case METHOD:
                        break;
                    case FIELD:
                        break;
                    case ARRAY:
                        break;
                    case ARGUMENT:
                        break;
                    case CAST:
                        break;
                    case TRANSFORM:
                        break;
                    case AMAKE:
                        break;
                    case ALENGTH:
                        break;
                    default:
                        throw new IllegalStateException(); // TODO: message
                }
            }

            if (lefthand) {
                checkWritePCast(extstartmd);

                lefthand = false;
            } else {
                righthand = false;
            }
        }

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
    }*/

    private void writeEnd() {
        writer.visitEnd();
    }

    private byte[] getBytes() {
        return writer.toByteArray();
    }
}
