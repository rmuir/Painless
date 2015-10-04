package painless;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import static painless.Adapter.*;
import static painless.Caster.*;
import static painless.Default.*;
import static painless.PainlessParser.*;
import static painless.Definition.*;

class Analyzer extends PainlessBaseVisitor<Void> {
    static void analyze(final Adapter adapter) {
        new Analyzer(adapter);
    }

    private final Adapter adapter;
    private final Definition definition;
    private final Standard standard;
    private final Caster caster;

    private Analyzer(final Adapter adapter) {
        this.adapter = adapter;
        definition = adapter.definition;
        standard = adapter.standard;
        caster = adapter.caster;

        adapter.createStatementMetadata(adapter.root);
        visit(adapter.root);
    }

    @Override
    public Void visitSource(final SourceContext ctx) {
        final StatementMetadata sourcesmd = adapter.getStatementMetadata(ctx);

        adapter.incrementScope();

        for (final StatementContext statectx : ctx.statement()) {
            if (sourcesmd.allExit) {
                throw new IllegalArgumentException(); // TODO: message
            }

            final StatementMetadata statesmd = adapter.createStatementMetadata(statectx);
            visit(statectx);

            if (statesmd.anyContinue) {
                throw new IllegalArgumentException();
            }

            if (statesmd.anyBreak) {
                throw new IllegalArgumentException();
            }

            sourcesmd.allExit = statesmd.allExit;
            sourcesmd.allReturn = statesmd.allReturn;
        }

        adapter.decrementScope();

        return null;
    }

    @Override
    public Void visitIf(final IfContext ctx) {
        final StatementMetadata ifsmd = adapter.getStatementMetadata(ctx);

        adapter.incrementScope();

        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.boolType;
        visit(exprctx);

        if (expremd.postConst != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final BlockContext blockctx0 = ctx.block(0);
        final StatementMetadata blocksmd0 = adapter.createStatementMetadata(blockctx0);
        visit(blockctx0);

        ifsmd.anyReturn = blocksmd0.anyReturn;
        ifsmd.anyBreak = blocksmd0.anyBreak;
        ifsmd.anyContinue = blocksmd0.anyContinue;

        if (ctx.ELSE() != null) {
            final BlockContext blockctx1 = ctx.block(1);
            final StatementMetadata blocksmd1 = adapter.createStatementMetadata(blockctx1);
            visit(blockctx1);

            ifsmd.allExit = blocksmd0.allExit && blocksmd1.allExit;
            ifsmd.allReturn = blocksmd0.allReturn && blocksmd1.allReturn;
            ifsmd.anyReturn |= blocksmd1.anyReturn;
            ifsmd.allBreak = blocksmd0.allBreak && blocksmd1.allBreak;
            ifsmd.anyBreak |= blocksmd1.anyBreak;
            ifsmd.allContinue = blocksmd0.allContinue && blocksmd1.allContinue;
            ifsmd.anyContinue |= blocksmd1.anyContinue;
        }

        adapter.decrementScope();

        return null;
    }

    @Override
    public Void visitWhile(final WhileContext ctx) {
        final StatementMetadata whilesmd = adapter.getStatementMetadata(ctx);

        adapter.incrementScope();

        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.boolType;
        visit(exprctx);

        boolean exitrequired = false;

        if (expremd.postConst != null) {
            boolean constant = (boolean)expremd.postConst;

            if (!constant) {
                throw new IllegalArgumentException(); // TODO: message
            }

            exitrequired = true;
        }

        final BlockContext blockctx = ctx.block();

        if (blockctx != null) {
            final StatementMetadata blocksmd = adapter.createStatementMetadata(blockctx);
            visit(blockctx);

            if (blocksmd.allReturn) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (blocksmd.allBreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (exitrequired && !blocksmd.anyReturn && !blocksmd.anyBreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (exitrequired && blocksmd.anyReturn && !blocksmd.anyBreak) {
                whilesmd.allExit = true;
                whilesmd.allReturn = true;
            }
        } else if (exitrequired) {
            throw new IllegalArgumentException(); // TODO: message
        }

        adapter.decrementScope();

        return null;
    }

    @Override
    public Void visitDo(final DoContext ctx) {
        final StatementMetadata dosmd = adapter.getStatementMetadata(ctx);

        adapter.incrementScope();

        final BlockContext blockctx = ctx.block();
        final StatementMetadata blocksmd = adapter.createStatementMetadata(blockctx);
        visit(blockctx);

        if (blocksmd.allReturn) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (blocksmd.allBreak) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (blocksmd.allContinue) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.boolType;
        visit(exprctx);

        if (expremd.postConst != null) {
            final boolean exitrequired = (boolean)expremd.postConst;

            if (exitrequired && !blocksmd.anyReturn && !blocksmd.anyBreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (exitrequired && blocksmd.anyReturn && !blocksmd.anyBreak) {
                dosmd.allExit = true;
                dosmd.allReturn = true;
            }

            if (!exitrequired && !blocksmd.anyContinue) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        adapter.decrementScope();

        return null;
    }

    @Override
    public Void visitFor(final ForContext ctx) {
        final StatementMetadata forsmd = adapter.getStatementMetadata(ctx);
        boolean exitrequired = false;

        adapter.incrementScope();

        final DeclarationContext declctx = ctx.declaration();

        if (declctx != null) {
            adapter.createStatementMetadata(declctx);
            visit(declctx);
        }

        final ExpressionContext exprctx0 = ctx.expression(0);

        if (exprctx0 != null) {
            final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
            expremd0.to = standard.boolType;
            visit(exprctx0);

            if (expremd0.postConst != null) {
                boolean constant = (boolean)expremd0.postConst;

                if (!constant) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                exitrequired = true;
            }
        } else {
            exitrequired = true;
        }

        final ExpressionContext exprctx1 = ctx.expression(1);

        if (exprctx1 != null) {
            final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
            expremd1.to = standard.voidType;
            visit(exprctx1);

            if (!expremd1.statement) {
                throw new IllegalStateException(); // TODO: message
            }
        }

        final BlockContext blockctx = ctx.block();

        if (blockctx != null) {
            final StatementMetadata blocksmd = adapter.createStatementMetadata(blockctx);
            visit(blockctx);

            if (blocksmd.allReturn) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (blocksmd.allBreak) {
                throw new IllegalArgumentException(); //TODO: message
            }

            if (exitrequired && !blocksmd.anyReturn && !blocksmd.anyBreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (exitrequired && blocksmd.anyReturn && !blocksmd.anyBreak) {
                forsmd.allExit = true;
                forsmd.allReturn = true;
            }
        } else if (exitrequired) {
            throw new IllegalArgumentException(); // TODO: message
        }

        adapter.decrementScope();

        return null;
    }

    @Override
    public Void visitDecl(final DeclContext ctx) {
        final DeclarationContext declctx = ctx.declaration();
        adapter.createStatementMetadata(declctx);
        visit(declctx);

        return null;
    }

    @Override
    public Void visitContinue(final ContinueContext ctx) {
        final StatementMetadata continuesmd = adapter.getStatementMetadata(ctx);

        continuesmd.allExit = true;
        continuesmd.allContinue = true;
        continuesmd.anyContinue = true;

        return null;
    }

    @Override
    public Void visitBreak(final BreakContext ctx) {
        final StatementMetadata breaksmd = adapter.getStatementMetadata(ctx);

        breaksmd.allExit = true;
        breaksmd.allBreak = true;
        breaksmd.anyBreak = true;

        return null;
    }

    @Override
    public Void visitReturn(final ReturnContext ctx) {
        final StatementMetadata returnsmd = adapter.getStatementMetadata(ctx);

        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.objectType;
        visit(exprctx);

        returnsmd.allExit = true;
        returnsmd.allReturn = true;
        returnsmd.anyReturn = true;

        return null;
    }

    @Override
    public Void visitExpr(final ExprContext ctx) {
        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.voidType;
        visit(exprctx);

        if (!expremd.statement) {
            throw new IllegalArgumentException(); // TODO: message
        }

        return null;
    }

    @Override
    public Void visitMultiple(final MultipleContext ctx) {
        final StatementMetadata multiplesmd = adapter.getStatementMetadata(ctx);

        for (StatementContext statectx : ctx.statement()) {
            if (multiplesmd.allExit) {
                throw new IllegalStateException();  // TODO: message
            }

            final StatementMetadata statesmd = adapter.createStatementMetadata(statectx);
            visit(statectx);

            multiplesmd.allExit = statesmd.allExit;
            multiplesmd.allReturn = statesmd.allReturn && !statesmd.anyBreak && !statesmd.anyContinue;
            multiplesmd.anyReturn |= statesmd.anyReturn;
            multiplesmd.allBreak = !statesmd.anyReturn && statesmd.allBreak && !statesmd.anyContinue;
            multiplesmd.anyBreak |= statesmd.anyBreak;
            multiplesmd.allContinue = !statesmd.anyReturn && !statesmd.anyBreak && !statesmd.allContinue;
            multiplesmd.anyContinue |= statesmd.anyContinue;
        }

        return null;
    }

    @Override
    public Void visitSingle(final SingleContext ctx) {
        final StatementMetadata singlesmd = adapter.getStatementMetadata(ctx);

        final StatementContext statectx = ctx.statement();
        final StatementMetadata statesmd = adapter.createStatementMetadata(statectx);
        visit(statectx);

        singlesmd.allExit = statesmd.allExit;
        singlesmd.allReturn = statesmd.allReturn;
        singlesmd.anyReturn = statesmd.anyReturn;
        singlesmd.allBreak = statesmd.allBreak;
        singlesmd.anyBreak = statesmd.anyBreak;
        singlesmd.allContinue = statesmd.allContinue;
        singlesmd.anyContinue = statesmd.anyContinue;

        return null;
    }

    @Override
    public Void visitEmpty(final EmptyContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitDeclaration(final DeclarationContext ctx) {
        final DecltypeContext decltypectx = ctx.decltype();
        final ExpressionMetadata decltypeemd = adapter.createExpressionMetadata(decltypectx);
        visit(decltypectx);

        for (final DeclvarContext declvarctx : ctx.declvar()) {
            final ExpressionMetadata declvaremd = adapter.createExpressionMetadata(declvarctx);
            declvaremd.to = decltypeemd.from;
            visit(declvarctx);
        }

        return null;
    }

    @Override
    public Void visitDecltype(final DecltypeContext ctx) {
        final ExpressionMetadata decltypeemd = adapter.getExpressionMetadata(ctx);

        final String pnamestr = ctx.getText();
        decltypeemd.from = getTypeFromCanonicalName(definition, pnamestr);

        return null;
    }

    @Override
    public Void visitDeclvar(final DeclvarContext ctx) {
        final ExpressionMetadata declvaremd = adapter.getExpressionMetadata(ctx);

        final String name = ctx.ID().getText();
        declvaremd.postConst = name;
        adapter.addVariable(name, declvaremd.to);

        final ExpressionContext exprctx = ctx.expression();

        if (exprctx != null) {
            final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
            expremd.to = declvaremd.to;
            visit(exprctx);
        }

        return null;
    }

    @Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        final ExpressionMetadata precemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx = ctx.expression();
        final ParserRuleContext parent = (ParserRuleContext)ctx.parent;
        int index = 0;

        for (ParseTree child : parent.children) {
            if (child == ctx) {
                parent.children.set(index, exprctx);
            }

            ++index;
        }

        adapter.updateExpressionMetadata(exprctx, precemd);

        return null;
    }

    @Override
    public Void visitNumeric(final NumericContext ctx) {
        final ExpressionMetadata numericemd = adapter.getExpressionMetadata(ctx);

        if (ctx.DECIMAL() != null) {
            final String svalue = ctx.DECIMAL().getText();

            if (svalue.endsWith("f") || svalue.endsWith("F")) {
                try {
                    numericemd.from = standard.floatType;
                    numericemd.preConst = Float.parseFloat(svalue.substring(0, svalue.length() - 1));
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    numericemd.from = standard.doubleType;
                    numericemd.preConst = Double.parseDouble(svalue);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        } else {
            String svalue;
            int radix;

            if (ctx.OCTAL() != null) {
                svalue = ctx.OCTAL().getText();
                radix = 8;
            } else if (ctx.INTEGER() != null) {
                svalue = ctx.INTEGER().getText();
                radix = 10;
            } else if (ctx.HEX() != null) {
                svalue = ctx.HEX().getText();
                radix = 16;
            } else {
                throw new IllegalStateException(); // TODO: message
            }

            if (svalue.endsWith("l") || svalue.endsWith("L")) {
                try {
                    numericemd.from = standard.longType;
                    numericemd.preConst = Long.parseLong(svalue.substring(0, svalue.length() - 1), radix);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    final Type type = numericemd.to;
                    final TypeMetadata tmd = type == null ? TypeMetadata.INT : type.metadata;
                    final int value = Integer.parseInt(svalue, radix);
                    numericemd.preConst = value;

                    if (tmd == TypeMetadata.BYTE && value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                        numericemd.from = standard.byteType;
                    } else if (tmd == TypeMetadata.CHAR && value >= Character.MIN_VALUE && value <= Character.MAX_VALUE) {
                        numericemd.from = standard.charType;
                    } else if (tmd == TypeMetadata.SHORT && value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                        numericemd.from = standard.shortType;
                    } else {
                        numericemd.from = standard.intType;
                    }
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        caster.markCast(numericemd);

        return null;
    }

    @Override
    public Void visitString(final StringContext ctx) {
        final ExpressionMetadata stringemd = adapter.getExpressionMetadata(ctx);

        if (ctx.STRING() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        final int length = ctx.STRING().getText().length();
        stringemd.preConst = ctx.STRING().getText().substring(1, length - 1);
        stringemd.from = standard.stringType;

        caster.markCast(stringemd);

        return null;
    }

    @Override
    public Void visitChar(final CharContext ctx) {
        final ExpressionMetadata charemd = adapter.getExpressionMetadata(ctx);

        if (ctx.CHAR() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        if (ctx.CHAR().getText().length() != 3) {
            throw new IllegalStateException(); // TODO: message
        }

        charemd.preConst = ctx.CHAR().getText().charAt(1);
        charemd.from = standard.charType;

        caster.markCast(charemd);

        return null;
    }

    @Override
    public Void visitTrue(final TrueContext ctx) {
        final ExpressionMetadata trueemd = adapter.getExpressionMetadata(ctx);

        if (ctx.TRUE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        trueemd.preConst = true;
        trueemd.from = standard.boolType;

        caster.markCast(trueemd);

        return null;
    }

    @Override
    public Void visitFalse(final FalseContext ctx) {
        final ExpressionMetadata falseemd = adapter.getExpressionMetadata(ctx);

        if (ctx.FALSE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        falseemd.preConst = false;
        falseemd.from = standard.boolType;

        caster.markCast(falseemd);

        return null;
    }

    @Override
    public Void visitNull(final NullContext ctx) {
        final ExpressionMetadata nullemd = adapter.getExpressionMetadata(ctx);

        if (ctx.NULL() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        nullemd.isNull = true;
        nullemd.from = standard.objectType;

        caster.markCast(nullemd);

        return null;
    }

    @Override
    public Void visitExt(final ExtContext ctx) {
        External external = new External(this, adapter);
        external.ext(ctx);

        return null;
    }

    /*@Override
    public Void visitPostinc(final PostincContext ctx) {
        PMetadata postincmd = getPMetadata(ctx);

        int increment;

        if (ctx.INCR() != null) {
            increment = 1;
        } else if (ctx.DECR() != null) {
            increment = -1;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        final ExtstartContext ectx = ctx.extstart();
        final PMetadata extstartmd = createPMetadata(ectx);
        extstartmd.anypnumeric = true;
        visit(ectx);

        PType ptype = extstartmd.pexternal.getPType();
        PType promoteptype = getUnaryNumericPromotion(ptype, true);

        if (promoteptype == null) {
            throw new ClassCastException(); // TODO: message
        }

        Object castpre = getLegalCast(ptype, promoteptype, false, true);
        Object castpost = getLegalCast(promoteptype, ptype, true, true);

        if (castpre != null && castpost == null || castpre == null && castpost != null) {
            throw new ClassCastException();
        }

        if (pstandard.pvoid.equals(postincmd.toptype)) {
            postincmd.fromptype = pstandard.pvoid;
        }

        if (castpre instanceof PCast) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        } else if (castpre instanceof PTransform) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        }

        extstartmd.pexternal.addSegment(SType.INCREMENT, extstartmd.pexternal.getPType(), increment);

        if (castpost instanceof PCast) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        } else if (castpost instanceof PTransform) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        }

        extstartmd.pexternal.addSegment(SType.WRITE, null, null);

        if (!pstandard.pvoid.equals(postincmd.toptype)) {
            postincmd.fromptype = extstartmd.pexternal.getPType();
        }

        postincmd.statement = true;
        markCast(postincmd);

        return null;
    }

    @Override
    public Void visitPreinc(final PreincContext ctx) {
        PMetadata preincmd = getPMetadata(ctx);

        int increment;

        if (ctx.INCR() != null) {
            increment = 1;
        } else if (ctx.DECR() != null) {
            increment = -1;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        final ExtstartContext ectx = ctx.extstart();
        final PMetadata extstartmd = createPMetadata(ectx);
        extstartmd.anypnumeric = true;
        visit(ectx);

        PType ptype = extstartmd.pexternal.getPType();
        PType promoteptype = getUnaryNumericPromotion(ptype, true);

        if (promoteptype == null) {
            throw new ClassCastException(); // TODO: message
        }

        Object castpre = getLegalCast(ptype, promoteptype, false, true);
        Object castpost = getLegalCast(promoteptype, ptype, true, true);

        if (castpre != null && castpost == null || castpre == null && castpost != null) {
            throw new ClassCastException();
        }

        if (pstandard.pvoid.equals(preincmd.toptype)) {
            preincmd.fromptype = pstandard.pvoid;
        }

        if (castpre instanceof PCast) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        } else if (castpre instanceof PTransform) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        }

        extstartmd.pexternal.addSegment(SType.INCREMENT, extstartmd.pexternal.getPType(), increment);

        if (castpost instanceof PCast) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        } else if (castpost instanceof PTransform) {
            extstartmd.pexternal.addSegment(SType.CAST, castpre, null);
        }

        extstartmd.pexternal.addSegment(SType.WRITE, null, null);

        if (!pstandard.pvoid.equals(preincmd.toptype)) {
            preincmd.fromptype = extstartmd.pexternal.getPType();
        }

        preincmd.statement = true;
        markCast(preincmd);

        return null;
    }*/

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final ExpressionMetadata unaryemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.getExpressionMetadata(exprctx);

        if (ctx.BOOLNOT() != null) {
            expremd.to = standard.boolType;
            visit(exprctx);

            if (expremd.postConst != null) {
                unaryemd.preConst = !(boolean)expremd.postConst;
            }

            unaryemd.from = standard.boolType;
        } else if (ctx.BWNOT() != null || ctx.ADD() != null || ctx.SUB() != null) {
            final Promotions promotions = ctx.BWNOT() != null ? caster.numeric : caster.decimal;
            expremd.promotions = promotions;
            visit(exprctx);

            final Type promote = caster.getTypePromotion(expremd.from, null, promotions);

            expremd.to = promote;
            caster.markCast(expremd);

            if (expremd.postConst != null) {
                final TypeMetadata tmd = promote.metadata;

                if (ctx.BWNOT() != null) {
                    if (tmd == TypeMetadata.INT) {
                        unaryemd.preConst = ~(int)expremd.postConst;
                    } else if (tmd == TypeMetadata.LONG) {
                        unaryemd.preConst = ~(long)expremd.postConst;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.SUB() != null) {
                    if (tmd == TypeMetadata.INT) {
                        unaryemd.preConst = -(int)expremd.postConst;
                    } else if (tmd == TypeMetadata.LONG) {
                        unaryemd.preConst = -(long)expremd.postConst;
                    } else if (tmd == TypeMetadata.FLOAT) {
                        unaryemd.preConst = -(float)expremd.postConst;
                    } else if (tmd == TypeMetadata.DOUBLE) {
                        unaryemd.preConst = -(double)expremd.postConst;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.ADD() != null) {
                    if (tmd == TypeMetadata.INT) {
                        unaryemd.preConst = +(int)expremd.postConst;
                    } else if (tmd == TypeMetadata.LONG) {
                        unaryemd.preConst = +(long)expremd.postConst;
                    } else if (tmd == TypeMetadata.FLOAT) {
                        unaryemd.preConst = +(float)expremd.postConst;
                    } else if (tmd == TypeMetadata.DOUBLE) {
                        unaryemd.preConst = +(double)expremd.postConst;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }

            unaryemd.from = promote;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        caster.markCast(unaryemd);

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final ExpressionMetadata castemd = adapter.getExpressionMetadata(ctx);

        final DecltypeContext decltypectx = ctx.decltype();
        final ExpressionMetadata decltypemd = adapter.getExpressionMetadata(decltypectx);
        visit(decltypectx);

        final Type type = decltypemd.from;
        castemd.from = type;

        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.getExpressionMetadata(exprctx);
        expremd.to = type;
        expremd.explicit = true;
        visit(exprctx);

        if (expremd.postConst != null) {
            castemd.preConst = expremd.postConst;
        }

        caster.markCast(castemd);

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final ExpressionMetadata binaryemd = adapter.getExpressionMetadata(ctx);

        Promotions promotions;

        if (ctx.ADD() != null) {
            promotions = caster.add;
        } else if (ctx.SUB() != null || ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null) {
            promotions = caster.decimal;
        } else {
            promotions = caster.numeric;
        }

        final ExpressionContext exprctx0 = ctx.expression(0);
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotions = promotions;
        visit(exprctx0);

        final ExpressionContext exprctx1 = ctx.expression(1);
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotions = promotions;
        visit(exprctx1);

        final Type promote = caster.getTypePromotion(expremd0.from, expremd1.from, promotions);

        expremd0.to = promote;
        caster.markCast(expremd0);
        expremd1.to = promote;
        caster.markCast(expremd1);

        if (expremd0.postConst != null && expremd1.postConst != null) {
            final TypeMetadata tmd = promote.metadata;
            
            if (ctx.MUL() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst * (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst * (long)expremd1.postConst;
                } else if (tmd == TypeMetadata.FLOAT) {
                    binaryemd.preConst = (float)expremd0.postConst * (float)expremd1.postConst;
                } else if (tmd == TypeMetadata.DOUBLE) {
                    binaryemd.preConst = (double)expremd0.postConst * (double)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.DIV() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst / (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst / (long)expremd1.postConst;
                } else if (tmd == TypeMetadata.FLOAT) {
                    binaryemd.preConst = (float)expremd0.postConst / (float)expremd1.postConst;
                } else if (tmd == TypeMetadata.DOUBLE) {
                    binaryemd.preConst = (double)expremd0.postConst / (double)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.REM() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst % (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst % (long)expremd1.postConst;
                } else if (tmd == TypeMetadata.FLOAT) {
                    binaryemd.preConst = (float)expremd0.postConst % (float)expremd1.postConst;
                } else if (tmd == TypeMetadata.DOUBLE) {
                    binaryemd.preConst = (double)expremd0.postConst % (double)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.ADD() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst + (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst + (long)expremd1.postConst;
                } else if (tmd == TypeMetadata.FLOAT) {
                    binaryemd.preConst = (float)expremd0.postConst + (float)expremd1.postConst;
                } else if (tmd == TypeMetadata.DOUBLE) {
                    binaryemd.preConst = (double)expremd0.postConst + (double)expremd1.postConst;
                } else if (tmd == TypeMetadata.STRING) {
                    binaryemd.preConst = (String)expremd0.postConst + expremd1.postConst;
                }else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.SUB() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst - (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst - (long)expremd1.postConst;
                } else if (tmd == TypeMetadata.FLOAT) {
                    binaryemd.preConst = (float)expremd0.postConst - (float)expremd1.postConst;
                } else if (tmd == TypeMetadata.DOUBLE) {
                    binaryemd.preConst = (double)expremd0.postConst - (double)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.LSH() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst << (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst << (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.RSH() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst >> (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst >> (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.USH() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst >>> (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst >>> (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWAND() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst & (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst & (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWXOR() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst ^ (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst ^ (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWOR() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst | (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst | (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }

        binaryemd.from = promote;
        caster.markCast(binaryemd);

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final ExpressionMetadata compemd = adapter.getExpressionMetadata(ctx);
        final Promotions promotions = ctx.EQ() != null || ctx.NE() != null ? caster.equality : caster.decimal;

        final ExpressionContext exprctx0 = ctx.expression(0);
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotions = promotions;
        visit(exprctx0);

        final ExpressionContext exprctx1 = ctx.expression(1);
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotions = promotions;
        visit(exprctx1);

        final Type promote = caster.getTypePromotion(expremd0.from, expremd1.from, promotions);

        if (expremd0.isNull && expremd1.isNull) {
            throw new IllegalArgumentException(); // TODO: message
        }

        expremd0.to = promote;
        caster.markCast(expremd0);
        expremd1.to = promote;
        caster.markCast(expremd1);

        if (expremd0.postConst != null && expremd1.postConst != null) {
            final TypeMetadata metadata = promote.metadata;

            if (ctx.EQ() != null) {
                if (metadata == TypeMetadata.BOOL) {
                    compemd.preConst = (boolean)expremd0.postConst == (boolean)expremd1.postConst;
                } else if (metadata == TypeMetadata.INT) {
                    compemd.preConst = (int)expremd0.postConst == (int)expremd1.postConst;
                } else if (metadata == TypeMetadata.LONG) {
                    compemd.preConst = (long)expremd0.postConst == (long)expremd1.postConst;
                } else if (metadata == TypeMetadata.FLOAT) {
                    compemd.preConst = (float)expremd0.postConst == (float)expremd1.postConst;
                } else if (metadata == TypeMetadata.DOUBLE) {
                    compemd.preConst = (double)expremd0.postConst == (double)expremd1.postConst;
                } else {
                    compemd.preConst = expremd0.postConst == expremd1.postConst;
                }
            } else if (ctx.NE() != null) {
                if (metadata == TypeMetadata.BOOL) {
                    compemd.preConst = (boolean)expremd0.postConst != (boolean)expremd1.postConst;
                } else if (metadata == TypeMetadata.INT) {
                    compemd.preConst = (int)expremd0.postConst != (int)expremd1.postConst;
                } else if (metadata == TypeMetadata.LONG) {
                    compemd.preConst = (long)expremd0.postConst != (long)expremd1.postConst;
                } else if (metadata == TypeMetadata.FLOAT) {
                    compemd.preConst = (float)expremd0.postConst != (float)expremd1.postConst;
                } else if (metadata == TypeMetadata.DOUBLE) {
                    compemd.preConst = (double)expremd0.postConst != (double)expremd1.postConst;
                } else {
                    compemd.preConst = expremd0.postConst != expremd1.postConst;
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }

            if (ctx.GTE() != null) {
                if (metadata == TypeMetadata.INT) {
                    compemd.preConst = (int)expremd0.postConst >= (int)expremd1.postConst;
                } else if (metadata == TypeMetadata.LONG) {
                    compemd.preConst = (long)expremd0.postConst >= (long)expremd1.postConst;
                } else if (metadata == TypeMetadata.FLOAT) {
                    compemd.preConst = (float)expremd0.postConst >= (float)expremd1.postConst;
                } else if (metadata == TypeMetadata.DOUBLE) {
                    compemd.preConst = (double)expremd0.postConst >= (double)expremd1.postConst;
                }
            } else if (ctx.GT() != null) {
                if (metadata == TypeMetadata.INT) {
                    compemd.preConst = (int)expremd0.postConst > (int)expremd1.postConst;
                } else if (metadata == TypeMetadata.LONG) {
                    compemd.preConst = (long)expremd0.postConst > (long)expremd1.postConst;
                } else if (metadata == TypeMetadata.FLOAT) {
                    compemd.preConst = (float)expremd0.postConst > (float)expremd1.postConst;
                } else if (metadata == TypeMetadata.DOUBLE) {
                    compemd.preConst = (double)expremd0.postConst > (double)expremd1.postConst;
                }
            } else if (ctx.LTE() != null) {
                if (metadata == TypeMetadata.INT) {
                    compemd.preConst = (int)expremd0.postConst <= (int)expremd1.postConst;
                } else if (metadata == TypeMetadata.LONG) {
                    compemd.preConst = (long)expremd0.postConst <= (long)expremd1.postConst;
                } else if (metadata == TypeMetadata.FLOAT) {
                    compemd.preConst = (float)expremd0.postConst <= (float)expremd1.postConst;
                } else if (metadata == TypeMetadata.DOUBLE) {
                    compemd.preConst = (double)expremd0.postConst <= (double)expremd1.postConst;
                }
            } else if (ctx.LT() != null) {
                if (metadata == TypeMetadata.INT) {
                    compemd.preConst = (int)expremd0.postConst < (int)expremd1.postConst;
                } else if (metadata == TypeMetadata.LONG) {
                    compemd.preConst = (long)expremd0.postConst < (long)expremd1.postConst;
                } else if (metadata == TypeMetadata.FLOAT) {
                    compemd.preConst = (float)expremd0.postConst < (float)expremd1.postConst;
                } else if (metadata == TypeMetadata.DOUBLE) {
                    compemd.preConst = (double)expremd0.postConst < (double)expremd1.postConst;
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }

        compemd.from = standard.boolType;
        caster.markCast(compemd);

        return null;
    }

    @Override
    public Void visitBool(final BoolContext ctx) {
        final ExpressionMetadata boolemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx0 = ctx.expression(0);
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.to = standard.boolType;
        visit(exprctx0);

        final ExpressionContext exprctx1 = ctx.expression(1);
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.to = standard.boolType;
        visit(exprctx1);

        if (expremd0.postConst != null && expremd1.postConst != null) {
            if (ctx.BOOLAND() != null) {
                boolemd.preConst = (boolean)expremd0.postConst && (boolean)expremd1.postConst;
            } else if (ctx.BOOLOR() != null) {
                boolemd.preConst = (boolean)expremd0.postConst || (boolean)expremd1.postConst;
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }

        boolemd.from = standard.boolType;
        caster.markCast(boolemd);

        return null;
    }

    @Override
    public Void visitConditional(final ConditionalContext ctx) {
        final ExpressionMetadata condemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx0 = ctx.expression(0);
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.to = standard.boolType;
        visit(exprctx0);

        if (expremd0.postConst != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExpressionContext exprctx1 = ctx.expression(1);
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.to = condemd.to;
        expremd1.promotions = condemd.promotions;
        visit(exprctx1);

        final ExpressionContext exprctx2 = ctx.expression(2);
        final ExpressionMetadata expremd2 = adapter.createExpressionMetadata(exprctx2);
        expremd2.to = condemd.to;
        expremd2.promotions = condemd.promotions;
        visit(exprctx2);

        if (condemd.to != null) {
            condemd.from = condemd.to;
        } else if (condemd.promotions != null) {
            Type promote = caster.getTypePromotion(expremd1.from, expremd2.from, condemd.promotions);

            expremd0.to = promote;
            caster.markCast(expremd1);
            expremd1.to = promote;
            caster.markCast(expremd2);

            condemd.from = promote;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        caster.markCast(condemd);

        return null;
    }

    @Override
    public Void visitAssignment(final AssignmentContext ctx) {
        External external = new External(this, adapter);
        external.assignment(ctx);

        return null;
    }

    @Override
    public Void visitExtstart(final ExtstartContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
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
}
