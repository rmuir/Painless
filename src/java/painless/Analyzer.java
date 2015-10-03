package painless;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;

import javax.swing.plaf.nimbus.State;

import static painless.Adapter.*;
import static painless.Default.*;
import static painless.PainlessParser.*;
import static painless.Types.*;

class Analyzer extends PainlessBaseVisitor<Void> {
    static void analyze(final Adapter adapter) {
        new Analyzer(adapter);
    }

    final Adapter adapter;
    final Types types;
    final Standard standard;

    private Analyzer(final Adapter adapter) {
        this.adapter = adapter;
        this.types = adapter.types;
        this.standard = adapter.standard;

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

        final boolean emptyallowed = expremd.statement;
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
        } else if (!emptyallowed) {
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
        boolean emptyallowed = false;
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

            emptyallowed = expremd0.statement;

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
            visit(exprctx0);

            if (!expremd1.statement) {
                throw new IllegalStateException(); // TODO: message
            }

            emptyallowed = true;
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
        } else if (!emptyallowed) {
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
            final ExpressionMetadata declvaremd = adapter.createExpressionMetadata(decltypectx);
            declvaremd.to = decltypeemd.from;
            visit(declvarctx);
        }

        return null;
    }

    @Override
    public Void visitDecltype(final DecltypeContext ctx) {
        final ExpressionMetadata decltypeemd = adapter.getExpressionMetadata(ctx);

        final String pnamestr = ctx.getText();
        decltypeemd.from = getTypeFromCanonicalName(types, pnamestr);

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
            final ExpressionMetadata expremd = adapter.getExpressionMetadata(ctx);
            expremd.to = declvaremd.to;
            visit(exprctx);
        }

        return null;
    }

    /*@Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        final PMetadata precedencemd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        passPMetadata(ectx, precedencemd);
        visit(ectx);

        return null;
    }

    @Override
    public Void visitNumeric(final NumericContext ctx) {
        final PMetadata numericmd = getPMetadata(ctx);

        if (ctx.DECIMAL() != null) {
            final String svalue = ctx.DECIMAL().getText();

            if (svalue.endsWith("f") || svalue.endsWith("F")) {
                try {
                    numericmd.fromptype = pstandard.pfloat;
                    numericmd.constpre = Float.parseFloat(svalue.substring(0, svalue.length() - 1));
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    numericmd.fromptype = pstandard.pdouble;
                    numericmd.constpre = Double.parseDouble(svalue);
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
                    numericmd.fromptype = pstandard.plong;
                    numericmd.constpre = Long.parseLong(svalue.substring(0, svalue.length() - 1), radix);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    final PType ptype = numericmd.getToPType();
                    final PSort psort = ptype == null ? PSort.INT : ptype.getPSort();
                    final int value = Integer.parseInt(svalue, radix);
                    numericmd.constpre = value;

                    if (psort == PSort.BYTE && value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                        numericmd.fromptype = pstandard.pbyte;
                    } else if (psort == PSort.CHAR && value >= Character.MIN_VALUE && value <= Character.MAX_VALUE) {
                        numericmd.fromptype = pstandard.pchar;
                    } else if (psort == PSort.SHORT && value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                        numericmd.fromptype = pstandard.pshort;
                    } else {
                        numericmd.fromptype = pstandard.pint;
                    }
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        markCast(numericmd);

        return null;
    }

    @Override
    public Void visitString(final StringContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);

        if (ctx.STRING() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        final int length = ctx.STRING().getText().length();
        stringmd.constpre = ctx.STRING().getText().substring(1, length - 1);
        stringmd.fromptype = pstandard.pstring;

        markCast(stringmd);

        return null;
    }

    @Override
    public Void visitChar(final CharContext ctx) {
        final PMetadata charmd = getPMetadata(ctx);

        if (ctx.CHAR() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        if (ctx.CHAR().getText().length() != 3) {
            throw new IllegalStateException(); // TODO: message
        }

        charmd.constpre = ctx.CHAR().getText().charAt(1);
        charmd.fromptype = pstandard.pchar;

        markCast(charmd);

        return null;
    }*/

    @Override
    public Void visitTrue(final TrueContext ctx) {
        final ExpressionMetadata trueemd = adapter.getExpressionMetadata(ctx);

        if (ctx.TRUE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        trueemd.preConst = true;
        trueemd.from = standard.boolType;

        adapter.markCast(trueemd);

        return null;
    }

    /*@Override
    public Void visitFalse(final FalseContext ctx) {
        final PMetadata falsemd = getPMetadata(ctx);

        if (ctx.FALSE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        falsemd.constpre = false;
        falsemd.fromptype = pstandard.pbool;

        markCast(falsemd);

        return null;
    }

    @Override
    public Void visitNull(final NullContext ctx) {
        final PMetadata nullmd = getPMetadata(ctx);

        if (ctx.NULL() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        nullmd.isnull = true;
        nullmd.fromptype = pstandard.pobject;

        markCast(nullmd);

        return null;
    }

    @Override
    public Void visitExt(final ExtContext ctx) {
        final PMetadata extmd = getPMetadata(ctx);

        final ExtstartContext ectx = ctx.extstart();
        passPMetadata(ectx, extmd);
        visit(ectx);

        return null;
    }*/

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
    }

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final PMetadata unarymd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);

        if (ctx.BOOLNOT() != null) {
            expressionmd.toptype = pstandard.pbool;
            visit(ectx);

            if (expressionmd.constpost != null) {
                unarymd.constpre = !(boolean)expressionmd.constpost;
            }

            unarymd.fromptype = pstandard.pbool;
        } else if (ctx.BWNOT() != null || ctx.ADD() != null || ctx.SUB() != null) {
            expressionmd.anypnumeric = true;
            visit(ectx);

            final boolean decimal = ctx.ADD() != null || ctx.SUB() != null;
            final PType promoteptype = getUnaryNumericPromotion(expressionmd.fromptype, decimal);

            if (promoteptype == null) {
                throw new ClassCastException(); // TODO: message
            }

            expressionmd.toptype = promoteptype;
            markCast(expressionmd);

            if (expressionmd.constpost != null) {
                final PSort promotepsort = promoteptype.getPSort();

                if (ctx.BWNOT() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constpre = ~(int)expressionmd.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constpre = ~(long)expressionmd.constpost;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.SUB() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constpre = -(int)expressionmd.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constpre = -(long)expressionmd.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        unarymd.constpre = -(float)expressionmd.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        unarymd.constpre = -(double)expressionmd.constpost;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.ADD() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constpre = +(int)expressionmd.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constpre = +(long)expressionmd.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        unarymd.constpre = +(float)expressionmd.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        unarymd.constpre = +(double)expressionmd.constpost;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }

            unarymd.fromptype = promoteptype;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        markCast(unarymd);

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final PMetadata castmd = getPMetadata(ctx);

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        decltypemd.anyptype = true;
        visit(dctx);

        final PType declptype = decltypemd.fromptype;
        castmd.fromptype = declptype;

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.toptype = declptype;
        expressionmd.explicit = true;
        visit(ectx);

        if (expressionmd.constpost != null) {
            castmd.constpre = expressionmd.constpost;
        }

        markCast(castmd);

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final PMetadata binarymd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        expressionmd0.anypnumeric = true;
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        expressionmd1.anypnumeric = true;
        visit(ectx1);

        final boolean decimal = ctx.ADD() != null || ctx.SUB() != null ||
                ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null;
        final PType promoteptype = getBinaryNumericPromotion(expressionmd0.fromptype, expressionmd1.fromptype, decimal);

        if (promoteptype == null) {
            throw new ClassCastException(); // TODO: message
        }

        expressionmd0.toptype = promoteptype;
        markCast(expressionmd0);
        expressionmd1.toptype = promoteptype;
        markCast(expressionmd1);

        if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
            final PSort promotepsort = promoteptype.getPSort();
            
            if (ctx.MUL() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost * (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost * (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost * (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost * (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.DIV() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost / (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost / (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost / (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost / (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.REM() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost % (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost % (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost % (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost % (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.ADD() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost + (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost + (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost + (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost + (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.SUB() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost - (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost - (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost - (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost - (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.LSH() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost << (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost << (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.RSH() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost >> (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost >> (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.USH() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost >>> (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost >>> (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWAND() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost & (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost & (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWXOR() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost ^ (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost ^ (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWOR() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost | (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost | (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }

        binarymd.fromptype = promoteptype;
        markCast(binarymd);

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final PMetadata compmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);

        if (expressionmd0.isnull && expressionmd1.isnull) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (ctx.EQ() != null || ctx.NE() != null) {
            expressionmd0.anyptype = true;
            visit(ectx0);
            expressionmd1.anyptype = true;
            visit(ectx1);

            final PType promoteptype = getBinaryAnyPromotion(expressionmd0.fromptype, expressionmd1.fromptype);

            if (promoteptype == null) {
                throw new ClassCastException(); // TODO: message
            }

            expressionmd0.toptype = promoteptype;
            markCast(expressionmd0);
            expressionmd1.toptype = promoteptype;
            markCast(expressionmd1);

            if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
                final PSort promotepsort = promoteptype.getPSort();

                if (ctx.EQ() != null) {
                    if (promotepsort == PSort.BOOL) {
                        compmd.constpre = (boolean)expressionmd0.constpost == (boolean)expressionmd1.constpost;
                    } else if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost == (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost == (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost == (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost == (double)expressionmd1.constpost;
                    } else {
                        compmd.constpre = expressionmd0.constpost == expressionmd1.constpost;
                    }
                } else if (ctx.NE() != null) {
                    if (promotepsort == PSort.BOOL) {
                        compmd.constpre = (boolean)expressionmd0.constpost != (boolean)expressionmd1.constpost;
                    } else if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost != (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost != (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost != (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost != (double)expressionmd1.constpost;
                    } else {
                        compmd.constpre = expressionmd0.constpost != expressionmd1.constpost;
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else if (ctx.GT() != null || ctx.GTE() != null || ctx.LT() != null || ctx.LTE() != null) {
            expressionmd0.anypnumeric = true;
            visit(ectx0);
            expressionmd1.anypnumeric = true;
            visit(ectx1);

            final PType promoteptype = getBinaryNumericPromotion(expressionmd0.fromptype, expressionmd1.fromptype, true);

            if (promoteptype == null) {
                throw new ClassCastException(); // TODO: message
            }

            expressionmd0.toptype = promoteptype;
            markCast(expressionmd0);
            expressionmd1.toptype = promoteptype;
            markCast(expressionmd1);

            if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
                final PSort promotepsort = promoteptype.getPSort();

                if (ctx.GTE() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost >= (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost >= (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost >= (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost >= (double)expressionmd1.constpost;
                    }
                } else if (ctx.GT() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost > (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost > (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost > (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost > (double)expressionmd1.constpost;
                    }
                } else if (ctx.LTE() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost <= (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost <= (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost <= (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost <= (double)expressionmd1.constpost;
                    }
                } else if (ctx.LT() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost < (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost < (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost < (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost < (double)expressionmd1.constpost;
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            throw new IllegalStateException();
        }

        compmd.fromptype = pstandard.pbool;
        markCast(compmd);

        return null;
    }

    @Override
    public Void visitBool(final BoolContext ctx) {
        final PMetadata boolmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        expressionmd0.toptype = pstandard.pbool;
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        expressionmd1.toptype = pstandard.pbool;
        visit(ectx1);

        if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
            if (ctx.BOOLAND() != null) {
                boolmd.constpre = (boolean)expressionmd0.constpost && (boolean)expressionmd1.constpost;
            } else if (ctx.BOOLOR() != null) {
                boolmd.constpre = (boolean)expressionmd0.constpost || (boolean)expressionmd1.constpost;
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }

        boolmd.fromptype = pstandard.pbool;
        markCast(boolmd);

        return null;
    }

    @Override
    public Void visitConditional(final ConditionalContext ctx) {
        final PMetadata conditionalmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        expressionmd0.toptype = pstandard.pbool;
        visit(ectx0);

        if (expressionmd0.constpost != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        expressionmd1.toptype = conditionalmd.toptype;
        expressionmd1.anyptype = conditionalmd.anyptype;
        expressionmd1.anypnumeric = conditionalmd.anypnumeric;
        visit(ectx1);

        final ExpressionContext ectx2 = ctx.expression(2);
        final PMetadata expressionmd2 = createPMetadata(ectx2);
        expressionmd2.toptype = conditionalmd.toptype;
        expressionmd1.anyptype = conditionalmd.anyptype;
        expressionmd1.anypnumeric = conditionalmd.anypnumeric;
        visit(ectx2);

        if (conditionalmd.toptype != null) {
            conditionalmd.fromptype = conditionalmd.toptype;
        } else if (conditionalmd.anyptype || conditionalmd.anypnumeric) {
            PType promoteptype = conditionalmd.anyptype ?
                    getBinaryAnyPromotion(expressionmd1.fromptype, expressionmd2.fromptype) :
                    getBinaryNumericPromotion(expressionmd1.fromptype, expressionmd2.fromptype, true);

            if (promoteptype == null) {
                throw new ClassCastException();
            }

            expressionmd0.toptype = promoteptype;
            markCast(expressionmd1);
            expressionmd1.toptype = promoteptype;
            markCast(expressionmd2);

            conditionalmd.fromptype = promoteptype;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        markCast(conditionalmd);

        return null;
    }

    @Override
    public Void visitAssignment(final AssignmentContext ctx) {
        final PMetadata assignmentmd = getPMetadata(ctx);

        final ExtstartContext ectx0 = ctx.extstart();
        final PMetadata extstartmd = createPMetadata(ectx0);
        extstartmd.anyptype = true;
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx1);
        expressionmd.toptype = extstartmd.pexternal.getPType();
        visit(ectx1);

        extstartmd.pexternal.addSegment(SType.WRITE, ectx1, null);

        assignmentmd.fromptype = extstartmd.fromptype;
        assignmentmd.statement = true;
        markCast(assignmentmd);

        return null;
    }

    @Override
    public Void visitExtstart(final ExtstartContext ctx) {
        final PMetadata extstartmd = getPMetadata(ctx);

        extstartmd.pexternal = new PExternal(ptypes);

        final ExtprecContext ectx0 = ctx.extprec();
        final ExtcastContext ectx1 = ctx.extcast();
        final ExttypeContext ectx2 = ctx.exttype();
        final ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extprecmd = createPMetadata(ectx0);
            extprecmd.pexternal = extstartmd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extcastmd = createPMetadata(ectx1);
            extcastmd.pexternal = extstartmd.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata exttypemd = createPMetadata(ectx2);
            exttypemd.pexternal = extstartmd.pexternal;
            visit(ectx2);
        } else if (ectx3 != null) {
            final PMetadata extmembermd = createPMetadata(ectx3);
            extmembermd.pexternal = extstartmd.pexternal;
            visit(ectx3);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        extstartmd.statement = extstartmd.pexternal.isCall();

        if (pstandard.pvoid.equals(extstartmd.toptype)) {
            extstartmd.fromptype = pstandard.pvoid;
            extstartmd.pexternal.addSegment(SType.POP, extstartmd.pexternal.getPType(), null);
        } else {
            extstartmd.fromptype = extstartmd.pexternal.getPType();
        }

        markCast(extstartmd);

        return null;
    }

    @Override
    public Void visitExtprec(final ExtprecContext ctx) {
        final PMetadata extprecmd0 = getPMetadata(ctx);

        final ExtprecContext ectx0 = ctx.extprec();
        final ExtcastContext ectx1 = ctx.extcast();
        final ExttypeContext ectx2 = ctx.exttype();
        final ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extprecmd1 = createPMetadata(ectx0);
            extprecmd1.pexternal = extprecmd0.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extcastmd = createPMetadata(ectx1);
            extcastmd.pexternal = extprecmd0.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata exttypemd = createPMetadata(ectx2);
            exttypemd.pexternal = extprecmd0.pexternal;
            visit(ectx2);
        } else if (ectx3 != null) {
            final PMetadata extmembermd = createPMetadata(ectx3);
            extmembermd.pexternal = extprecmd0.pexternal;
            visit(ectx3);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        final ExtdotContext ectx4 = ctx.extdot();
        final ExtbraceContext ectx5 = ctx.extbrace();

        if (ectx4 != null) {
            final PMetadata extdotmd = createPMetadata(ectx4);
            extdotmd.pexternal = extprecmd0.pexternal;
            visit(ectx4);
        } else if (ectx5 != null) {
            final PMetadata extarraymd = createPMetadata(ectx5);
            extarraymd.pexternal = extprecmd0.pexternal;
            visit(ectx5);
        }

        return null;
    }

    @Override
    public Void visitExtcast(final ExtcastContext ctx) {
        final PMetadata extcastmd0 = getPMetadata(ctx);

        final ExtprecContext ectx0 = ctx.extprec();
        final ExtcastContext ectx1 = ctx.extcast();
        final ExttypeContext ectx2 = ctx.exttype();
        final ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extprecmd1 = createPMetadata(ectx0);
            extprecmd1.pexternal = extcastmd0.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extcastmd1 = createPMetadata(ectx1);
            extcastmd1.pexternal = extcastmd0.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata exttypemd = createPMetadata(ectx2);
            exttypemd.pexternal = extcastmd0.pexternal;
            visit(ectx2);
        } else if (ectx3 != null) {
            final PMetadata extmembermd = createPMetadata(ectx3);
            extmembermd.pexternal = extcastmd0.pexternal;
            visit(ectx3);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        decltypemd.anyptype = true;
        visit(dctx);

        final PType pfrom = extcastmd0.pexternal.getPType();
        final PType pto = decltypemd.fromptype;

        final Object object = getLegalCast(pfrom, pto, true, true);

        if (object instanceof PCast) {
            extcastmd0.pexternal.addSegment(SType.CAST, object, null);
        } else if (object instanceof PTransform) {
            extcastmd0.pexternal.addSegment(SType.TRANSFORM, object, null);
        }

        return null;
    }

    @Override
    public Void visitExtbrace(final ExtbraceContext ctx) {
        final PMetadata extbrace = getPMetadata(ctx);
        final PExternal pexternal = extbrace.getPExternal();
        final PType ptype = pexternal.getPType();

        final ExpressionContext ectx0 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx0);

        final ExtdotContext ectx1 = ctx.extdot();
        final ExtbraceContext ectx2 = ctx.extbrace();

        if (ptype.getPDimensions() > 0) {
            expressionmd.toptype = pstandard.pint;
            visit(ectx0);

            pexternal.addSegment(SType.NODE, ectx0, null);
            pexternal.addSegment(SType.ARRAY, null, null);
        } else {
            expressionmd.anyptype = true;
            visit(ectx0);

            final PSort psort = expressionmd.getFromPType().getPSort();
            final boolean numeric = psort.isPNumeric();
            expressionmd.toptype = numeric ? pstandard.pint : expressionmd.toptype;
            markCast(expressionmd);

            final Object object = getLegalCast(ptype, numeric ? pstandard.plist : pstandard.pmap, true, false);

            if (object instanceof PCast) {
                pexternal.addSegment(SType.CAST, object, null);
            } else if (object instanceof PTransform) {
                pexternal.addSegment(SType.TRANSFORM, object, null);
            }

            pexternal.addSegment(SType.NODE, ectx0, null);

            if (numeric) {
                final Struct pclass = object == null ? ptype.getPClass() : pstandard.plist.getPClass();
                final PMethod read = pclass.getPMethod("get");
                final PMethod write = pclass.getPMethod("add");

                if (read == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                if (write == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                pexternal.addSegment(SType.SHORTCUT, read, write);
            } else {
                final Struct pclass = object == null ? ptype.getPClass() : pstandard.pmap.getPClass();
                final PMethod read = pclass.getPMethod("get");
                final PMethod write = pclass.getPMethod("put");

                if (read == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                if (write == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                pexternal.addSegment(SType.SHORTCUT, read, write);
            }
        }

        if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extbrace.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata extarraymd1 = createPMetadata(ectx2);
            extarraymd1.pexternal = extbrace.pexternal;
            visit(ectx2);
        }

        return null;
    }

    @Override
    public Void visitExtdot(final ExtdotContext ctx) {
        final PMetadata extdotmd = getPMetadata(ctx);

        final ExtcallContext ectx0 = ctx.extcall();
        final ExtmemberContext ectx1 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extcallmd = createPMetadata(ectx0);
            extcallmd.pexternal = extdotmd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extmembermd = createPMetadata(ectx1);
            extmembermd.pexternal = extdotmd.pexternal;
            visit(ectx1);
        }

        return null;
    }

    @Override
    public Void visitExttype(final ExttypeContext ctx) {
        final PMetadata exttypemd = getPMetadata(ctx);
        final String ptypestr = ctx.ID().getText();
        final PType ptype = getPTypeFromCanonicalPName(ptypes, ptypestr);

        if (ptype == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        exttypemd.pexternal.addSegment(SType.TYPE, ptype, null);

        final ExtdotContext ectx = ctx.extdot();
        final PMetadata extdotmd = createPMetadata(ectx);
        extdotmd.pexternal = exttypemd.pexternal;
        visit(ectx);

        return null;
    }

    @Override
    public Void visitExtcall(final ExtcallContext ctx) {
        final PMetadata extcallmd = getPMetadata(ctx);
        final PType declptype = extcallmd.pexternal.getPType();
        final Struct pclass = declptype.getPClass();

        if (pclass == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pname = ctx.ID().getText();
        final boolean statik = extcallmd.pexternal.isStatic();
        final List<ExpressionContext> arguments = ctx.arguments().expression();
        SType stype;
        Object svalue;
        PType[] argumentsptypes;

        if (statik && "makearray".equals(pname)) {
            stype = SType.AMAKE;
            svalue = arguments.size();
            argumentsptypes = new PType[arguments.size()];
            Arrays.fill(argumentsptypes, pstandard.pint);
        } else {
            final Constructor pconstructor = statik ? pclass.getPConstructor(pname) : null;
            final PMethod pmethod = statik ? pclass.getPFunction(pname) : pclass.getPMethod(pname);

            if (pconstructor != null) {
                stype = SType.CONSTRUCTOR;
                svalue = pconstructor;
                argumentsptypes = new PType[pconstructor.getPArguments().size()];
                pconstructor.getPArguments().toArray(argumentsptypes);
            } else if (pmethod != null) {
                stype = SType.METHOD;
                svalue = pmethod;
                argumentsptypes = new PType[pmethod.getPArguments().size()];
                pmethod.getPArguments().toArray(argumentsptypes);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        if (arguments.size() != argumentsptypes.length) {
            throw new IllegalArgumentException(); // TODO: message
        }

        for (int argument = 0; argument < arguments.size(); ++argument) {
            final ParseTree ectx = arguments.get(argument);
            final PMetadata expressionmd = createPMetadata(ectx);
            expressionmd.toptype = argumentsptypes[argument];
            visit(ectx);

            extcallmd.pexternal.addSegment(SType.NODE, ectx, null);
        }

        extcallmd.pexternal.addSegment(stype, svalue, null);

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtbraceContext ectx1 = ctx.extbrace();

        if (ectx0 != null) {
            final PMetadata extdotmd = createPMetadata(ectx0);
            extdotmd.pexternal = extcallmd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extcallmd.pexternal;
            visit(ectx1);
        }

        return null;
    }

    @Override
    public Void visitExtmember(final ExtmemberContext ctx) {
        final PMetadata extmembermd = getPMetadata(ctx);
        final PType ptype = extmembermd.pexternal.getPType();
        final String pname = ctx.ID().getText();

        if (ptype == null) {
            final PVariable pvariable = getPVariable(pname);

            if (pvariable == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            extmembermd.pexternal.addSegment(SType.VARIABLE, pvariable, false);
        } else {
            if (ptype.getPSort() == PSort.ARRAY) {
                if ("length".equals(pname)) {
                    extmembermd.pexternal.addSegment(SType.ALENGTH, null, null);
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                final Struct pclass = ptype.getPClass();

                if (pclass == null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                final boolean statik = extmembermd.pexternal.isStatic();
                final PField pmember = statik ? pclass.getPStatic(pname) : pclass.getPMember(pname);

                if (pmember == null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                extmembermd.pexternal.addSegment(SType.FIELD, pmember, false);
            }
        }

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtbraceContext ectx1 = ctx.extbrace();

        if (ectx0 != null) {
            final PMetadata extdotmd = createPMetadata(ectx0);
            extdotmd.pexternal = extmembermd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extmembermd.pexternal;
            visit(ectx1);
        }

        return null;
    }

    @Override
    public Void visitArguments(final ArgumentsContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }*/
}
