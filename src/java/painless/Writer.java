package painless;

import java.util.Deque;
import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.swing.plaf.nimbus.State;

import static painless.Adapter.*;
import static painless.Definition.*;
import static painless.PainlessParser.*;

class Writer extends PainlessBaseVisitor<Void>{
    final static String BASE_CLASS_NAME = Executable.class.getName();
    final static String CLASS_NAME = BASE_CLASS_NAME + "$CompiledPainlessExecutable";
    final static String BASE_CLASS_INTERNAL = Executable.class.getName().replace('.', '/');
    final static String CLASS_INTERNAL = BASE_CLASS_INTERNAL + "$CompiledPainlessExecutable";

    static byte[] write(Adapter adapter) {
        Writer writer = new Writer(adapter);

        return writer.getBytes();
    }

    private final Adapter adapter;
    private final Caster caster;
    private final ParseTree root;
    private final String source;

    private ClassWriter writer;
    private MethodVisitor execute;

    private Writer(final Adapter adapter) {
        this.adapter = adapter;
        caster = adapter.caster;
        root = adapter.root;
        source = adapter.source;

        writeBegin();
        writeConstructor();
        writeExecute();
        writeEnd();
    }

    private void checkWriteCast(final ExpressionMetadata pmetadata) {
        if (pmetadata.getCast() != null) {
            writeCast(pmetadata.getCast());
        } else if (pmetadata.getPTransform() != null) {
            writePTransform(pmetadata.getPTransform());
        }
    }

    private void writeCast(final Cast pcast) {
        final Type fromptype = pcast.getPFrom();
        final Type toptype = pcast.getPTo();
        final TypeMetadata frompsort = fromptype.getTypeMetadata();
        final TypeMetadata topsort = toptype.getTypeMetadata();

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
            execute.visitTypeInsn(Opcodes.CHECKCAST, toptype.getJInternal());
        }
    }

    private void writePTransform(final PTransform ptransform) {
        final PMethod pmethod = ptransform.getPMethod();
        final Method jmethod = pmethod.getJMethod();
        final Struct powner = pmethod.getPOwner();
        final Class jowner = powner.getJClass();

        final Type pcastfrom = ptransform.getJCastFrom();
        final Type pcastto = ptransform.getJCastTo();

        if (pcastfrom != null) {
            execute.visitTypeInsn(Opcodes.CHECKCAST, pcastfrom.getJInternal());
        }

        if (Modifier.isStatic(jmethod.getModifiers())) {
            execute.visitMethodInsn(Opcodes.INVOKESTATIC,
                    powner.getJInternal(), jmethod.getName(), pmethod.getADescriptor(), false);
        } else if (Modifier.isInterface(jowner.getModifiers())) {
          execute.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                  powner.getJInternal(), jmethod.getName(), pmethod.getADescriptor(), true);
        } else {
            execute.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    powner.getJInternal(), jmethod.getName(), pmethod.getADescriptor(), false);
        }

        if (pcastto != null) {
            execute.visitTypeInsn(Opcodes.CHECKCAST, pcastto.getJInternal());
        }
    }

    private void checkWritePBranch(final PBranch pbranch) {
        if (pbranch != null) {
            if (pbranch.tru != null) {
                execute.visitJumpInsn(Opcodes.IFNE, pbranch.tru);
            } else if (pbranch.fals != null) {
                execute.visitJumpInsn(Opcodes.IFEQ, pbranch.fals);
            }
        }
    }

    private void writePConstant(final Object constant) {
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

    private void writeBegin() {
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
        final String aname = "<init>";
        final String adescriptor = "(Ljava/lang/String;Ljava/lang/String;)V";

        final MethodVisitor constructor = writer.visitMethod(access, aname, adescriptor, null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitVarInsn(Opcodes.ALOAD, 1);
        constructor.visitVarInsn(Opcodes.ALOAD, 2);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, BASE_CLASS_INTERNAL, aname, adescriptor, false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();
    }

    private void writeExecute() {
        final int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC;
        final String aname = "execute";
        final String adescriptor = "(Ljava/util/Map;)Ljava/lang/Object;";
        final String signature = "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)Ljava/lang/Object;";

        execute = writer.visitMethod(access, aname, adescriptor, signature, null);
        execute.visitCode();
        visit(root);
        execute.visitMaxs(0, 0);
        execute.visitEnd();
    }

    @Override
    public Void visitSource(final SourceContext ctx) {
        final StatementMetadata sourcesmd = adapter.getStatementMetadata(ctx);

        for (final StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }

        if (!sourcesmd.allReturn) {
            execute.visitInsn(Opcodes.ACONST_NULL);
            execute.visitInsn(Opcodes.ARETURN);
        }

        return null;
    }

    @Override
    public Void visitIf(final IfContext ctx) {
        final boolean els = ctx.ELSE() != null;
        final Branch branch = adapter.createBranch(ctx, false);
        branch.end = new Label();
        branch.fals = els ? new Label() : branch.end;

        pbranches.put(ectx, branch);
        visit(ctx.expression());

        final BlockContext blockctx0 = ctx.block(0);
        final StatementMetadata blockmd0 = adapter.getStatementMetadata(blockctx0);
        visit(blockctx0);

        if (els) {
            if (!blockmd0.allExit) { // TODO: this needs to check fo all paths exit
                execute.visitJumpInsn(Opcodes.GOTO, branch.end);
            }

            execute.visitLabel(branch.fals);
            visit(ctx.block(1));
        }

        execute.visitLabel(branch.end);
        adapter.removeBranch(ctx, false);

        return null;
    }

    @Override
    public Void visitWhile(final WhileContext ctx) {
        final Branch branch = adapter.createBranch(ctx, true);
        branch.begin = new Label();
        branch.end = new Label();
        branch.fals = branch.end;

        execute.visitLabel(branch.begin);


        pbranches.put(ectx, branch);
        visit(ctx.expression());

        final BlockContext bctx = ctx.block();
        boolean allexit = false;

        if (bctx != null) {
            StatementMetadata blocksmd = adapter.getStatementMetadata(bctx);
            allexit = blocksmd.allExit;
            visit(bctx);
        }

        if (!allexit) {
            execute.visitJumpInsn(Opcodes.GOTO, branch.begin);
        }

        execute.visitLabel(branch.end);
        adapter.removeBranch(ctx, true);

        return null;
    }

    @Override
    public Void visitDo(final DoContext ctx) {
        final Branch branch = adapter.createBranch(ctx, true);
        branch.begin = new Label();
        branch.end = new Label();
        branch.fals = branch.end;

        final ExpressionContext ectx = ctx.expression();
        final BlockContext bctx = ctx.block();
        final StatementMetadata blocksmd = adapter.getStatementMetadata(bctx);

        execute.visitLabel(branch.begin);
        visit(bctx);

            pbranches.put(ectx, branch);
            visit(ectx);
        }

        if (!blockmd.getAllExit()) {
            execute.visitJumpInsn(Opcodes.GOTO, branch.begin);
        }

        execute.visitLabel(branch.end);
        adapter.removeBranch(ctx, true);

        return null;
    }

    @Override
    public Void visitFor(final ForContext ctx) {
        final PMetadata formd = getPMetadata(ctx);
        final Object constant = formd.getConstPost();

        final ExpressionContext ectx0 = ctx.expression(0);
        final ExpressionContext ectx1 = ctx.expression(1);
        final BlockContext bctx = ctx.block();

        final PBranch pjump = new PBranch(ctx);
        final Label astart = new Label();
        pjump.begin = ectx1 == null ? astart : new Label();
        pjump.end = new Label();
        pjump.fals = pjump.end;

        ploops.push(pjump);

        if (ctx.declaration() != null) {
            visit(ctx.declaration());
        }

        execute.visitLabel(astart);

        if (!(constant instanceof Boolean && (boolean)constant)) {
            pbranches.put(ectx0, pjump);
            visit(ectx0);
        }

        boolean allexit = false;

        if (bctx != null) {
            PMetadata blockmd = getPMetadata(bctx);
            allexit = blockmd.getAllExit();
            visit(bctx);
        }

        if (ectx1 != null) {
            execute.visitLabel(pjump.begin);

            visit(ectx1);
        }

        if (ectx1 != null || !allexit) {
            execute.visitJumpInsn(Opcodes.GOTO, astart);
        }

        execute.visitLabel(pjump.end);
        ploops.pop();

        return null;
    }

    @Override
    public Void visitDecl(final DeclContext ctx) {
        visit(ctx.declaration());

        return null;
    }

    @Override
    public Void visitContinue(final ContinueContext ctx) {
        final PBranch pjump = ploops.peek();
        execute.visitJumpInsn(Opcodes.GOTO, pjump.begin);

        return null;
    }

    @Override
    public Void visitBreak(final BreakContext ctx) {
        final PBranch pjump = ploops.peek();
        execute.visitJumpInsn(Opcodes.GOTO, pjump.end);

        return null;
    }

    @Override
    public Void visitReturn(final ReturnContext ctx) {
        visit(ctx.expression());
        execute.visitInsn(Opcodes.ARETURN);

        return null;
    }

    @Override
    public Void visitExpr(final ExprContext ctx) {
        visit(ctx.expression());

        return null;
    }

    @Override
    public Void visitMultiple(final MultipleContext ctx) {
        for (final StatementContext sctx : ctx.statement()) {
            visit(sctx);
        }

        return null;
    }

    @Override
    public Void visitSingle(final SingleContext ctx) {
        visit(ctx.statement());

        return null;
    }

    @Override
    public Void visitEmpty(final EmptyContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitDeclaration(DeclarationContext ctx) {
        for (final DeclvarContext dctx1 : ctx.declvar()) {
            visit(dctx1);
        }

        return null;
    }

    @Override
    public Void visitDecltype(final DecltypeContext ctx) {
        throw new UnsupportedOperationException(); //TODO: message
    }

    @Override
    public Void visitDeclvar(final DeclvarContext ctx) {
        final PMetadata declvarmd = getPMetadata(ctx);
        final PVariable pvariable = (PVariable)declvarmd.getConstPost();

        final ExpressionContext ectx = ctx.expression();
        final boolean def = ectx == null;

        if (!def) {
            visit(ectx);
        }

        final TypeMetadata psort = pvariable.getType().getTypeMetadata();
        final int slot = pvariable.getASlot();

        switch (psort) {
            case VOID:
                throw new IllegalStateException(); // TODO: message
            case BOOL:
            case BYTE:
            case SHORT:
            case CHAR:
            case INT:
                if (def) {
                    writePNumeric(0);
                }

                execute.visitVarInsn(Opcodes.ISTORE, slot);
                break;
            case LONG:
                if (def) {
                    writePNumeric(0L);
                }

                execute.visitVarInsn(Opcodes.LSTORE, slot);
                break;
            case FLOAT:
                if (def) {
                    writePNumeric(0.0F);
                }

                execute.visitVarInsn(Opcodes.FSTORE, slot);
                break;
            case DOUBLE:
                if (def) {
                    writePNumeric(0.0);
                }

                execute.visitVarInsn(Opcodes.DSTORE, slot);
                break;
            default:
                if (def) {
                    execute.visitInsn(Opcodes.ACONST_NULL);
                }

                execute.visitVarInsn(Opcodes.ASTORE, slot);
        }

        return null;
    }

    @Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        final PBranch pbranch = pbranches.get(ctx);
        final ExpressionContext ectx = ctx.expression();

        if (pbranch != null) {
            pbranches.put(ectx, pbranch);
        }

        visit(ectx);

        return null;
    }

    @Override
    public Void visitNumeric(final NumericContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);
        final Object constpost = stringmd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost == null) {
            final Object constpre = stringmd.getConstPre();

            writePNumeric(constpre);
            checkWriteCast(stringmd);
        } else {
            writePConstant(constpost);
        }

        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitString(final StringContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);
        final Object constpost = stringmd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost == null) {
            final Object constpre = stringmd.getConstPre();

            writePString(constpre);
            checkWriteCast(stringmd);
        } else {
            writePConstant(constpost);
        }

        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitChar(final CharContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);
        final Object constpost = stringmd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost != null) {
            final Object constpre = stringmd.getConstPre();

            writePNumeric((int)(char)constpre);
            checkWriteCast(stringmd);
        } else {
            writePConstant(constpost);
        }

        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitTrue(final TrueContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);
        final Object constpost = truemd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (pbranch == null) {
            if (constpost == null) {
                writePBoolean(true);
                checkWriteCast(truemd);
            } else {
                writePConstant(constpost);
            }
        } else if (pbranch.tru != null) {
            execute.visitJumpInsn(Opcodes.GOTO, pbranch.tru);
        }

        return null;
    }

    @Override
    public Void visitFalse(final FalseContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);
        final Object constpost = truemd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (pbranch == null) {
            if (constpost == null) {
                writePBoolean(false);
                checkWriteCast(truemd);
            } else {
                writePConstant(constpost);
            }
        } else if (pbranch.tru != null) {
            execute.visitJumpInsn(Opcodes.GOTO, pbranch.tru);
        }

        return null;
    }

    @Override
    public Void visitNull(final NullContext ctx) {
        final PMetadata nullmd = getPMetadata(ctx);
        final PBranch pbranch = pbranches.get(ctx);

        execute.visitInsn(Opcodes.ACONST_NULL);
        checkWriteCast(nullmd);
        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitExt(final ExtContext ctx) {
        final PBranch pbranch = pbranches.get(ctx);
        final ExtstartContext ectx = ctx.extstart();

        if (pbranch != null) {
            pbranches.put(ectx, pbranch);
        }

        visit(ectx);

        return null;
    }

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final PMetadata unarymd = getPMetadata(ctx);
        final Object constpost = unarymd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost == null) {
            final ExpressionContext ectx = ctx.expression();

            if (ctx.BOOLNOT() != null) {
                if (pbranch == null) {
                    final PBranch localpbranch = new PBranch(ctx);
                    localpbranch.fals = new Label();
                    final Label aend = new Label();

                    pbranches.put(ectx, localpbranch);
                    visit(ectx);

                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitJumpInsn(Opcodes.GOTO, aend);
                    execute.visitLabel(localpbranch.fals);
                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitLabel(aend);

                    checkWriteCast(unarymd);
                } else {
                    final PBranch localpbranch = new PBranch(ctx);
                    localpbranch.tru = pbranch.fals;
                    localpbranch.fals = pbranch.tru;

                    pbranches.put(ectx, localpbranch);
                    visit(ectx);
                }
            } else {
                final TypeMetadata psort = unarymd.getFromType().getTypeMetadata();

                visit(ectx);

                if (ctx.BWNOT() != null) {
                    if (psort == TypeMetadata.INT) {
                        writePConstant(-1);
                        execute.visitInsn(Opcodes.IXOR);
                    } else {
                        writePConstant(-1L);
                        execute.visitInsn(Opcodes.LXOR);
                    }
                } else if (ctx.SUB() != null) {
                    if (psort == TypeMetadata.INT) {
                        execute.visitInsn(Opcodes.INEG);
                    } else if (psort == TypeMetadata.LONG) {
                        execute.visitInsn(Opcodes.LNEG);
                    } else if (psort == TypeMetadata.FLOAT) {
                        execute.visitInsn(Opcodes.FNEG);
                    } else {
                        execute.visitInsn(Opcodes.DNEG);
                    }
                }

                checkWriteCast(unarymd);
                checkWritePBranch(pbranch);
            }
        } else {
            if (ctx.BOOLNOT() != null) {
                if (pbranch == null) {
                    writePConstant(constpost);
                } else {
                    if ((boolean)constpost && pbranch.tru != null) {
                        execute.visitJumpInsn(Opcodes.GOTO, pbranch.tru);
                    } else if (!(boolean)constpost && pbranch.fals != null) {
                        execute.visitJumpInsn(Opcodes.GOTO, pbranch.fals);
                    }
                }
            } else {
                writePConstant(constpost);
                checkWritePBranch(pbranch);
            }
        }

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final PMetadata castmd = getPMetadata(ctx);
        final Object constpost = castmd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost == null) {
            visit(ctx.expression());
            checkWriteCast(castmd);
        } else {
            writePConstant(constpost);
        }

        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final PMetadata compmd = getPMetadata(ctx);
        final Object constpost = compmd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost == null) {
            final ExpressionContext expr0 = ctx.expression(0);
            final ExpressionContext expr1 = ctx.expression(1);

            visit(expr0);
            visit(expr1);

            final TypeMetadata psort = compmd.getFromType().getTypeMetadata();

            switch (psort) {
                case INT:
                    if (ctx.MUL() != null) {
                        execute.visitInsn(Opcodes.IMUL);
                    } else if (ctx.DIV() != null) {
                        execute.visitInsn(Opcodes.IDIV);
                    } else if (ctx.REM() != null) {
                        execute.visitInsn(Opcodes.IREM);
                    } else if (ctx.ADD() != null) {
                        execute.visitInsn(Opcodes.IADD);
                    } else if (ctx.SUB() != null) {
                        execute.visitInsn(Opcodes.ISUB);
                    } else if (ctx.LSH() != null) {
                        execute.visitInsn(Opcodes.ISHL);
                    } else if (ctx.USH() != null) {
                        execute.visitInsn(Opcodes.IUSHR);
                    } else if (ctx.RSH() != null) {
                        execute.visitInsn(Opcodes.ISHR);
                    } else if (ctx.BWAND() != null) {
                        execute.visitInsn(Opcodes.IAND);
                    } else if (ctx.BWXOR() != null) {
                        execute.visitInsn(Opcodes.IXOR);
                    } else if (ctx.BWOR() != null) {
                        execute.visitInsn(Opcodes.IOR);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                case LONG:
                    if (ctx.MUL() != null) {
                        execute.visitInsn(Opcodes.LMUL);
                    } else if (ctx.DIV() != null) {
                        execute.visitInsn(Opcodes.LDIV);
                    } else if (ctx.REM() != null) {
                        execute.visitInsn(Opcodes.LREM);
                    } else if (ctx.ADD() != null) {
                        execute.visitInsn(Opcodes.LADD);
                    } else if (ctx.SUB() != null) {
                        execute.visitInsn(Opcodes.LSUB);
                    } else if (ctx.LSH() != null) {
                        execute.visitInsn(Opcodes.LSHL);
                    } else if (ctx.USH() != null) {
                        execute.visitInsn(Opcodes.LUSHR);
                    } else if (ctx.RSH() != null) {
                        execute.visitInsn(Opcodes.LSHR);
                    } else if (ctx.BWAND() != null) {
                        execute.visitInsn(Opcodes.LAND);
                    } else if (ctx.BWXOR() != null) {
                        execute.visitInsn(Opcodes.LXOR);
                    } else if (ctx.BWOR() != null) {
                        execute.visitInsn(Opcodes.LOR);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                case FLOAT:
                    if (ctx.MUL() != null) {
                        execute.visitInsn(Opcodes.FMUL);
                    } else if (ctx.DIV() != null) {
                        execute.visitInsn(Opcodes.FDIV);
                    } else if (ctx.REM() != null) {
                        execute.visitInsn(Opcodes.FREM);
                    } else if (ctx.ADD() != null) {
                        execute.visitInsn(Opcodes.FADD);
                    } else if (ctx.SUB() != null) {
                        execute.visitInsn(Opcodes.FSUB);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                case DOUBLE:
                    if (ctx.MUL() != null) {
                        execute.visitInsn(Opcodes.DMUL);
                    } else if (ctx.DIV() != null) {
                        execute.visitInsn(Opcodes.DDIV);
                    } else if (ctx.REM() != null) {
                        execute.visitInsn(Opcodes.DREM);
                    } else if (ctx.ADD() != null) {
                        execute.visitInsn(Opcodes.DADD);
                    } else if (ctx.SUB() != null) {
                        execute.visitInsn(Opcodes.DSUB);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                default:
                    throw new IllegalStateException(); // TODO: message
            }

            checkWriteCast(compmd);
            checkWritePBranch(pbranch);
        } else {
            writePConstant(constpost);
            checkWritePBranch(pbranch);
        }

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final PMetadata compmd = getPMetadata(ctx);
        final Object constpost = compmd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost == null) {
            final ExpressionContext ectx0 = ctx.expression(0);
            final ExpressionContext ectx1 = ctx.expression(1);
            final PMetadata expressionmd1 = getPMetadata(ectx1);
            final TypeMetadata psort = expressionmd1.getToType().getTypeMetadata();

            visit(ectx0);

            if (!expressionmd1.getIsNull()) {
                visit(ectx1);
            }

            final boolean ptrue = pbranch != null && pbranch.tru != null;
            final boolean pfalse = pbranch != null && pbranch.fals != null;
            final Label ajump = ptrue ? pbranch.tru : pfalse ? pbranch.fals : new Label();
            final Label aend = new Label();

            final boolean eq  = ctx.EQ()  != null && (ptrue || !pfalse) || ctx.NE()  != null && pfalse;
            final boolean ne  = ctx.NE()  != null && (ptrue || !pfalse) || ctx.EQ()  != null && pfalse;
            final boolean lt  = ctx.LT()  != null && (ptrue || !pfalse) || ctx.GTE() != null && pfalse;
            final boolean lte = ctx.LTE() != null && (ptrue || !pfalse) || ctx.GT()  != null && pfalse;
            final boolean gt  = ctx.GT()  != null && (ptrue || !pfalse) || ctx.LTE() != null && pfalse;
            final boolean gte = ctx.GTE() != null && (ptrue || !pfalse) || ctx.LT()  != null && pfalse;

            switch (psort) {
                case VOID:
                    throw new IllegalStateException(); // TODO: message
                case BOOL:
                    if (eq) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPEQ, ajump);
                    } else if (ne) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPNE, ajump);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                case BYTE:
                case SHORT:
                case CHAR:
                    throw new IllegalStateException(); // TODO: message
                case INT:
                    if (eq) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPEQ, ajump);
                    } else if (ne) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPNE, ajump);
                    } else if (lt) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPLT, ajump);
                    } else if (lte) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPLE, ajump);
                    } else if (gt) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPGT, ajump);
                    } else if (gte) {
                        execute.visitJumpInsn(Opcodes.IF_ICMPGE, ajump);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                case LONG:
                    execute.visitInsn(Opcodes.LCMP);

                    if (eq) {
                        execute.visitJumpInsn(Opcodes.IFEQ, ajump);
                    } else if (ne) {
                        execute.visitJumpInsn(Opcodes.IFNE, ajump);
                    } else if (lt) {
                        execute.visitJumpInsn(Opcodes.IFLT, ajump);
                    } else if (lte) {
                        execute.visitJumpInsn(Opcodes.IFLE, ajump);
                    } else if (gt) {
                        execute.visitJumpInsn(Opcodes.IFGT, ajump);
                    } else if (gte) {
                        execute.visitJumpInsn(Opcodes.IFGE, ajump);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                case FLOAT:
                    if (eq) {
                        execute.visitInsn(Opcodes.FCMPL);
                        execute.visitJumpInsn(Opcodes.IFEQ, ajump);
                    } else if (ne) {
                        execute.visitInsn(Opcodes.FCMPL);
                        execute.visitJumpInsn(Opcodes.IFNE, ajump);
                    } else if (lt) {
                        execute.visitInsn(Opcodes.FCMPG);
                        execute.visitJumpInsn(Opcodes.IFLT, ajump);
                    } else if (lte) {
                        execute.visitInsn(Opcodes.FCMPG);
                        execute.visitJumpInsn(Opcodes.IFLE, ajump);
                    } else if (gt) {
                        execute.visitInsn(Opcodes.FCMPL);
                        execute.visitJumpInsn(Opcodes.IFGT, ajump);
                    } else if (gte) {
                        execute.visitInsn(Opcodes.FCMPL);
                        execute.visitJumpInsn(Opcodes.IFGE, ajump);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                case DOUBLE:
                    if (eq) {
                        execute.visitInsn(Opcodes.DCMPL);
                        execute.visitJumpInsn(Opcodes.IFEQ, ajump);
                    } else if (ne) {
                        execute.visitInsn(Opcodes.DCMPL);
                        execute.visitJumpInsn(Opcodes.IFNE, ajump);
                    } else if (lt) {
                        execute.visitInsn(Opcodes.DCMPG);
                        execute.visitJumpInsn(Opcodes.IFLT, ajump);
                    } else if (lte) {
                        execute.visitInsn(Opcodes.DCMPG);
                        execute.visitJumpInsn(Opcodes.IFLE, ajump);
                    } else if (gt) {
                        execute.visitInsn(Opcodes.DCMPL);
                        execute.visitJumpInsn(Opcodes.IFGT, ajump);
                    } else if (gte) {
                        execute.visitInsn(Opcodes.DCMPL);
                        execute.visitJumpInsn(Opcodes.IFGE, ajump);
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    break;
                default:
                    if (eq) {
                        if (expressionmd1.getIsNull()) {
                            execute.visitJumpInsn(Opcodes.IFNULL, ajump);
                        } else {
                            execute.visitJumpInsn(Opcodes.IF_ACMPEQ, ajump);
                        }
                    } else if (ne) {
                        if (expressionmd1.getIsNull()) {
                            execute.visitJumpInsn(Opcodes.IFNONNULL, ajump);
                        } else {
                            execute.visitJumpInsn(Opcodes.IF_ACMPNE, ajump);
                        }
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
            }

            if (pbranch == null) {
                execute.visitInsn(Opcodes.ICONST_0);
                execute.visitJumpInsn(Opcodes.GOTO, aend);
                execute.visitLabel(ajump);
                execute.visitInsn(Opcodes.ICONST_1);
                execute.visitLabel(aend);

                checkWriteCast(compmd);
            }
        } else {
            if (pbranch == null) {
                writePConstant(constpost);
            } else {
                if ((boolean)constpost && pbranch.tru != null) {
                    execute.visitLabel(pbranch.tru);
                } else if (!(boolean)constpost && pbranch.fals != null) {
                    execute.visitLabel(pbranch.fals);
                }
            }
        }

        return null;
    }

    @Override
    public Void visitBool(final BoolContext ctx) {
        final PMetadata boolmd = getPMetadata(ctx);
        final Object constpost = boolmd.getConstPost();
        final PBranch pbranch = pbranches.get(ctx);

        if (constpost == null) {
            final ExpressionContext ectx0 = ctx.expression(0);
            final ExpressionContext ectx1 = ctx.expression(1);

            if (pbranch == null) {
                if (ctx.BOOLAND() != null) {
                    final PBranch localpbranch = new PBranch(ctx);
                    localpbranch.fals = new Label();
                    final Label aend = new Label();

                    pbranches.put(ectx0, localpbranch);
                    visit(ectx0);
                    pbranches.put(ectx1, localpbranch);
                    visit(ectx1);

                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitJumpInsn(Opcodes.GOTO, aend);
                    execute.visitLabel(localpbranch.fals);
                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitLabel(aend);
                } else if (ctx.BOOLOR() != null) {
                    final PBranch pbranch0 = new PBranch(ctx);
                    pbranch0.tru = new Label();
                    final PBranch pbranch1 = new PBranch(ctx);
                    pbranch1.fals = new Label();
                    final Label aend = new Label();

                    pbranches.put(ectx0, pbranch0);
                    visit(ectx0);
                    pbranches.put(ectx1, pbranch1);
                    visit(ectx1);

                    execute.visitLabel(pbranch0.tru);
                    execute.visitInsn(Opcodes.ICONST_1);
                    execute.visitJumpInsn(Opcodes.GOTO, aend);
                    execute.visitLabel(pbranch1.fals);
                    execute.visitInsn(Opcodes.ICONST_0);
                    execute.visitLabel(aend);
                } else {
                    throw new IllegalStateException(); // TODO: message
                }

                checkWriteCast(boolmd);
            } else {
                if (ctx.BOOLAND() != null) {
                    final PBranch pbranch0 = new PBranch(ctx);
                    pbranch0.fals = pbranch.fals == null ? new Label() : pbranch.fals;
                    final PBranch pbranch1 = new PBranch(ctx);
                    pbranch1.tru = pbranch.tru;
                    pbranch1.fals = pbranch.fals;

                    pbranches.put(ectx0, pbranch0);
                    visit(ectx0);
                    pbranches.put(ectx1, pbranch1);
                    visit(ectx1);

                    if (pbranch.fals == null) {
                        execute.visitLabel(pbranch0.fals);
                    }
                } else if (ctx.BOOLOR() != null) {
                    final PBranch pbranch0 = new PBranch(ctx);
                    pbranch0.tru = pbranch.tru == null ? new Label() : pbranch.tru;
                    final PBranch pbranch1 = new PBranch(ctx);
                    pbranch1.tru = pbranch.tru;
                    pbranch1.fals = pbranch.fals;

                    pbranches.put(ectx0, pbranch0);
                    visit(ectx0);
                    pbranches.put(ectx1, pbranch1);
                    visit(ectx1);

                    if (pbranch.tru == null) {
                        execute.visitLabel(pbranch0.tru);
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            if (pbranch == null) {
                writePConstant(constpost);
            } else {
                if ((boolean)constpost && pbranch.tru != null) {
                    execute.visitLabel(pbranch.tru);
                } else if (!(boolean)constpost && pbranch.fals != null) {
                    execute.visitLabel(pbranch.fals);
                }
            }
        }

        return null;
    }

    @Override
    public Void visitConditional(final ConditionalContext ctx) {
        final PMetadata conditionalmd = getPMetadata(ctx);
        final PBranch pbranch = pbranches.get(ctx);

        final ExpressionContext expr0 = ctx.expression(0);
        final ExpressionContext expr1 = ctx.expression(1);
        final ExpressionContext expr2 = ctx.expression(2);

        final PBranch localpbranch = new PBranch(ctx);
        localpbranch.fals = new Label();
        localpbranch.end = new Label();
        pbranches.put(expr0, localpbranch);

        visit(expr0);
        visit(expr1);
        execute.visitJumpInsn(Opcodes.GOTO, localpbranch.end);
        execute.visitLabel(localpbranch.fals);
        visit(expr2);
        execute.visitLabel(localpbranch.end);

        if (pbranch == null) {
            checkWriteCast(conditionalmd);
        }

        return null;
    }

    /*@Override
    public Void visitAssignment(final AssignmentContext ctx) {
        final PMetadata assignmentmd = getPMetadata(ctx);
        final PBranch pbranch = pbranches.get(ctx);

        visit(ctx.extstart());

        checkWriteCast(assignmentmd);
        checkWritePBranch(pbranch);

        return null;
    }

    @Override
    public Void visitExtstart(ExtstartContext ctx) {
        final PMetadata extstartmd = getPMetadata(ctx);
        final PBranch pbranch = pbranches.get(ctx);
        final PExternal pexternal = extstartmd.getPExternal();
        final Iterator<PSegment> psegments = pexternal.getIterator();

        while (psegments.hasNext()) {
            final PSegment psegment = psegments.next();
            final SType stype = psegment.getSType();
            final Object svalue0 = psegment.getSValue0();
            final Object svalue1 = psegment.getSValue1();

            switch (stype) {
                case TYPE: {
                    break;
                } case VARIABLE: {
                    final PVariable pvariable = (PVariable)svalue0;
                    final boolean write = (Boolean)svalue1;
                    final TypeMetadata psort = pvariable.getType().getTypeMetadata();
                    final int aslot = pvariable.getASlot();

                    switch (psort) {
                        case VOID:
                            throw new IllegalStateException();
                        case BOOL:
                        case BYTE:
                        case SHORT:
                        case CHAR:
                        case INT:
                            execute.visitVarInsn(write ? Opcodes.ISTORE : Opcodes.ILOAD, aslot);
                            break;
                        case LONG:
                            execute.visitVarInsn(write ? Opcodes.LSTORE : Opcodes.LLOAD, aslot);
                            break;
                        case FLOAT:
                            execute.visitVarInsn(write ? Opcodes.FSTORE : Opcodes.FLOAD, aslot);
                            break;
                        case DOUBLE:
                            execute.visitVarInsn(write ? Opcodes.DSTORE : Opcodes.DLOAD, aslot);
                            break;
                        default:
                            execute.visitVarInsn(write ? Opcodes.ASTORE : Opcodes.ALOAD, aslot);
                    }

                    break;
                } case CONSTRUCTOR: {
                    final Constructor pconstructor = (Constructor)svalue0;
                    final String jinternal = pconstructor.getPOwner().getJInternal();

                    execute.visitTypeInsn(Opcodes.NEW, jinternal);
                    execute.visitInsn(Opcodes.DUP);
                    execute.visitMethodInsn(Opcodes.INVOKESPECIAL, jinternal, "<init>",
                            pconstructor.getADescriptor(), false);

                    break;
                } case METHOD:
                  case SHORTCUT: {
                    final PMethod pmethod = (PMethod)svalue0;
                    final String jinternal = pmethod.getPOwner().getJInternal();
                    final Method jmethod = pmethod.getJMethod();
                    final Class jowner = pmethod.getPOwner().getJClass();

                    if (Modifier.isStatic(jmethod.getModifiers())) {
                        execute.visitMethodInsn(Opcodes.INVOKESTATIC,
                                jinternal, jmethod.getName(), pmethod.getADescriptor(), false);
                    } else if (Modifier.isInterface(jowner.getModifiers())) {
                        execute.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                                jinternal, jmethod.getName(), pmethod.getADescriptor(), true);
                    } else {
                        execute.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                jinternal, jmethod.getName(), pmethod.getADescriptor(), false);
                    }

                    break;
                } case FIELD: {
                    final PField pfield = (PField)svalue0;
                    final Boolean write = (Boolean)svalue1;
                    final TypeMetadata psort = pfield.getType().getTypeMetadata();
                    final String jinternal = pfield.getPOwner().getJInternal();
                    final String jname = pfield.getJField().getName();
                    final String adescriptor = pfield.getType().getADescriptor();

                    int opcode;

                    if (write && !pop) {
                        if (psort.getASize() == 1) {
                            execute.visitInsn(Opcodes.DUP_X1);
                        } else if (psort.getASize() == 2) {
                            execute.visitInsn(Opcodes.DUP2_X1);
                        }
                    }

                    if (Modifier.isStatic(pfield.getJField().getModifiers())) {
                        opcode = write ? Opcodes.PUTSTATIC : Opcodes.GETSTATIC;
                    } else {
                        opcode = write ? Opcodes.PUTFIELD : Opcodes.GETFIELD;
                    }

                    execute.visitFieldInsn(opcode, jinternal, jname, adescriptor);

                    break;
                } case ARRAY: {
                    final TypeMetadata psort = ((Type)svalue0).getTypeMetadata();
                    final boolean write = (Boolean)svalue1;

                    if (write && !pop) {
                        if (psort.getASize() == 1) {
                            execute.visitInsn(Opcodes.DUP_X2);
                        } else if (psort.getASize() == 2) {
                            execute.visitInsn(Opcodes.DUP2_X2);
                        }
                    }

                    switch (psort) {
                        case BOOL:
                        case BYTE:
                        case SHORT:
                        case CHAR:
                        case INT:
                            execute.visitInsn(write ? Opcodes.IASTORE : Opcodes.IALOAD);
                            break;
                        case LONG:
                            execute.visitInsn(write ? Opcodes.LASTORE : Opcodes.LALOAD);
                            break;
                        case FLOAT:
                            execute.visitInsn(write ? Opcodes.FASTORE : Opcodes.FALOAD);
                            break;
                        case DOUBLE:
                            execute.visitInsn(write ? Opcodes.DASTORE : Opcodes.DALOAD);
                            break;
                        default:
                            execute.visitInsn(write ? Opcodes.AASTORE : Opcodes.AALOAD);
                            break;
                    }

                    break;
                } case WRITE: {
                    final ParseTree ectx = (ParseTree)svalue0;
                    visit(ectx);

                    break;
                } case POP: {
                    final Type ptype = (Type)svalue0;
                    final int asize = ptype.getTypeMetadata().getASize();

                    if (asize == 1) {
                        execute.visitInsn(Opcodes.POP);
                    } else if (asize == 2) {
                        execute.visitInsn(Opcodes.POP2);
                    }

                    break;
                } case NODE: {
                    final ParseTree ectx = (ParseTree)svalue0;
                    visit(ectx);

                    break;
                } case CAST: {
                    final Cast pcast = (Cast)svalue0;
                    writeCast(pcast);

                    break;
                } case TRANSFORM: {
                    final PTransform ptransform = (PTransform)svalue0;
                    writePTransform(ptransform);

                    break;
                }
                case AMAKE: {
                    final Type ptype = (Type)svalue0;

                    if (ptype.getPDimensions() > 1) {
                        final String adescriptor = ptype.getADescriptor();
                        execute.visitMultiANewArrayInsn(adescriptor, ptype.getPDimensions());
                    } else {
                        TypeMetadata psort = getTypeWithArrayDimensions(ptype.getPClass(), 0).getTypeMetadata();

                        switch (psort) {
                            case VOID:
                                throw new IllegalStateException(); // TODO: message
                            case BOOL:
                                execute.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN);
                                break;
                            case SHORT:
                                execute.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_SHORT);
                                break;
                            case CHAR:
                                execute.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_CHAR);
                                break;
                            case INT:
                                execute.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
                                break;
                            case LONG:
                                execute.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_LONG);
                                break;
                            case FLOAT:
                                execute.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_FLOAT);
                                break;
                            case DOUBLE:
                                execute.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_DOUBLE);
                                break;
                            default:
                                final String jinternal = ptype.getPClass().getJInternal();
                                execute.visitTypeInsn(Opcodes.ANEWARRAY, jinternal);
                        }
                    }

                    break;
                } case ALENGTH: {
                    execute.visitInsn(Opcodes.ARRAYLENGTH);

                    break;
                } default: {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        }

        checkWriteCast(extstartmd);
        checkWritePBranch(pbranch);

        return null;
    }*/

    /*@Override
    public Void visitExtprec(final ExtprecContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtcast(final ExtcastContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtbrace(final ExtbraceContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtdot(final ExtdotContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExttype(final ExttypeContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtcall(final ExtcallContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitExtmember(final ExtmemberContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitArguments(final ArgumentsContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    private void writeEnd() {
        writer.visitEnd();
    }

    private byte[] getBytes() {
        return writer.toByteArray();
    }
}
