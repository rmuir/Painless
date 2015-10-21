package org.elasticsearch.plan.a;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import static org.elasticsearch.plan.a.Adapter.*;
import static org.elasticsearch.plan.a.Caster.*;
import static org.elasticsearch.plan.a.Default.*;
import static org.elasticsearch.plan.a.Definition.*;
import static org.elasticsearch.plan.a.PlanAParser.*;
import static org.elasticsearch.plan.a.Utility.*;

class Analyzer extends PlanABaseVisitor<Void> {
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
                throw new IllegalArgumentException(error(statectx) +
                        "Statement will never be executed because all prior paths exit.");
            }

            final StatementMetadata statesmd = adapter.createStatementMetadata(statectx);
            visit(statectx);

            if (statesmd.anyContinue) {
                throw new IllegalArgumentException(error(statectx) +
                        "Cannot have a continue statement outside of a loop.");
            }

            if (statesmd.anyBreak) {
                throw new IllegalArgumentException(error(statectx) +
                        "Cannot have a break statement outside of a loop.");
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

        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.boolType;
        visit(exprctx);

        if (expremd.postConst != null) {
            throw new IllegalArgumentException(error(ctx) + "If statement is not necessary.");
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

        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.boolType;
        visit(exprctx);

        boolean exitrequired = false;

        if (expremd.postConst != null) {
            boolean constant = (boolean)expremd.postConst;

            if (!constant) {
                throw new IllegalArgumentException(error(ctx) + "The loop will never be executed.");
            }

            exitrequired = true;
        }

        final BlockContext blockctx = ctx.block();

        if (blockctx != null) {
            final StatementMetadata blocksmd = adapter.createStatementMetadata(blockctx);
            visit(blockctx);

            if (blocksmd.allReturn) {
                throw new IllegalArgumentException(error(ctx) + "All paths return so the loop is not necessary.");
            }

            if (blocksmd.allBreak) {
                throw new IllegalArgumentException(error(ctx) + "All paths break so the loop is not necessary.");
            }

            if (exitrequired && !blocksmd.anyReturn && !blocksmd.anyBreak) {
                throw new IllegalArgumentException(error(ctx) + "The loop will never exit.");
            }

            if (exitrequired && blocksmd.anyReturn && !blocksmd.anyBreak) {
                whilesmd.allExit = true;
                whilesmd.allReturn = true;
            }
        } else if (exitrequired) {
            throw new IllegalArgumentException(error(ctx) + "The loop will never exit.");
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
            throw new IllegalArgumentException(error(ctx) + "All paths return so the loop is not necessary.");
        }

        if (blocksmd.allBreak) {
            throw new IllegalArgumentException(error(ctx) + "All paths break so the loop is not necessary.");
        }

        if (blocksmd.allContinue) {
            throw new IllegalArgumentException(error(ctx) + "The loop will never exit.");
        }

        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.boolType;
        visit(exprctx);

        if (expremd.postConst != null) {
            final boolean exitrequired = (boolean)expremd.postConst;

            if (exitrequired && !blocksmd.anyReturn && !blocksmd.anyBreak) {
                throw new IllegalArgumentException(error(ctx) + "The loop will never exit.");
            }

            if (exitrequired && blocksmd.anyReturn && !blocksmd.anyBreak) {
                dosmd.allExit = true;
                dosmd.allReturn = true;
            }

            if (!exitrequired && !blocksmd.anyContinue) {
                throw new IllegalArgumentException(error(ctx) + "All paths exit so the loop is not necessary.");
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

        final ExpressionContext exprctx0 = adapter.getExpressionContext(ctx.expression(0));

        if (exprctx0 != null) {
            final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
            expremd0.to = standard.boolType;
            visit(exprctx0);

            if (expremd0.postConst != null) {
                boolean constant = (boolean)expremd0.postConst;

                if (!constant) {
                    throw new IllegalArgumentException(error(ctx) + "The loop will never be executed.");
                }

                exitrequired = true;
            }
        } else {
            exitrequired = true;
        }

        final ExpressionContext exprctx1 = adapter.getExpressionContext(ctx.expression(1));

        if (exprctx1 != null) {
            final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
            expremd1.to = standard.voidType;
            visit(exprctx1);

            if (!expremd1.statement) {
                throw new IllegalArgumentException(error(exprctx1) +
                        "The afterthought of a for loop must be a statement.");
            }
        }

        final BlockContext blockctx = ctx.block();

        if (blockctx != null) {
            final StatementMetadata blocksmd = adapter.createStatementMetadata(blockctx);
            visit(blockctx);

            if (blocksmd.allReturn) {
                throw new IllegalArgumentException(error(ctx) + "All paths return so the loop is not necessary.");
            }

            if (blocksmd.allBreak) {
                throw new IllegalArgumentException(error(ctx) + "All paths break so the loop is not necessary.");
            }

            if (exitrequired && !blocksmd.anyReturn && !blocksmd.anyBreak) {
                throw new IllegalArgumentException(error(ctx) + "The loop will never exit.");
            }

            if (exitrequired && blocksmd.anyReturn && !blocksmd.anyBreak) {
                forsmd.allExit = true;
                forsmd.allReturn = true;
            }
        } else if (exitrequired) {
            throw new IllegalArgumentException(error(ctx) + "The loop will never exit.");
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

        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
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
        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.voidType;
        visit(exprctx);

        if (!expremd.statement) {
            throw new IllegalArgumentException(error(ctx) + "Not a statement.");
        }

        return null;
    }

    @Override
    public Void visitMultiple(final MultipleContext ctx) {
        final StatementMetadata multiplesmd = adapter.getStatementMetadata(ctx);

        for (StatementContext statectx : ctx.statement()) {
            if (multiplesmd.allExit) {
                throw new IllegalArgumentException(error(statectx) +
                        "Statement will never be executed because all prior paths exit.");
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
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
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
        declvaremd.postConst = adapter.addVariable(ctx, name, declvaremd.to);

        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());

        if (exprctx != null) {
            final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
            expremd.to = declvaremd.to;
            visit(exprctx);
        }

        return null;
    }

    @Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
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
                    throw new IllegalArgumentException(error(ctx) + "Invalid float constant.");
                }
            } else {
                try {
                    numericemd.from = standard.doubleType;
                    numericemd.preConst = Double.parseDouble(svalue);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(error(ctx) + "Invalid double constant.");
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
                throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
            }

            if (svalue.endsWith("l") || svalue.endsWith("L")) {
                try {
                    numericemd.from = standard.longType;
                    numericemd.preConst = Long.parseLong(svalue.substring(0, svalue.length() - 1), radix);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(error(ctx) + "Invalid long constant.");
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
                    throw new IllegalArgumentException(error(ctx) + "Invalid int constant.");
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
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        if (ctx.CHAR().getText().length() != 3) {
            throw new IllegalStateException(error(ctx) + "Invalid character constant.");
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
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        nullemd.isNull = true;
        nullemd.from = standard.objectType;

        caster.markCast(nullemd);

        return null;
    }

    @Override
    public Void visitCat(CatContext ctx) {
        ExpressionMetadata catemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx0 = adapter.getExpressionContext(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = caster.equality;
        visit(exprctx0);
        expremd0.to = expremd0.from;
        caster.markCast(expremd0);

        final ExpressionContext exprctx1 = adapter.getExpressionContext(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotion = caster.equality;
        visit(exprctx1);
        expremd1.to = expremd1.from;
        caster.markCast(expremd1);

        if (expremd0.postConst != null && expremd1.postConst != null) {
            catemd.postConst = expremd0.postConst.toString() + expremd1.postConst.toString();
        }

        catemd.from = standard.stringType;
        caster.markCast(catemd);

        return null;
    }

    @Override
    public Void visitExt(final ExtContext ctx) {
        External external = new External(adapter, this);
        external.ext(ctx);
        adapter.putExternal(ctx, external);

        return null;
    }

    @Override
    public Void visitPostinc(final PostincContext ctx) {
        External external = new External(adapter, this);
        external.postinc(ctx);
        adapter.putExternal(ctx, external);

        return null;
    }

    @Override
    public Void visitPreinc(final PreincContext ctx) {
        External external = new External(adapter, this);
        external.preinc(ctx);
        adapter.putExternal(ctx, external);

        return null;
    }

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final ExpressionMetadata unaryemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);

        if (ctx.BOOLNOT() != null) {
            expremd.to = standard.boolType;
            visit(exprctx);

            if (expremd.postConst != null) {
                unaryemd.preConst = !(boolean)expremd.postConst;
            }

            unaryemd.from = standard.boolType;
        } else if (ctx.BWNOT() != null || ctx.ADD() != null || ctx.SUB() != null) {
            final Promotion promotion = ctx.BWNOT() != null ? caster.numeric : caster.decimal;
            expremd.promotion = promotion;
            visit(exprctx);

            final Type promote = caster.getTypePromotion(ctx, expremd.from, null, promotion);

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
                        throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
                        throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
                        throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                    }
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            }

            unaryemd.from = promote;
        } else {
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        caster.markCast(unaryemd);

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final ExpressionMetadata castemd = adapter.getExpressionMetadata(ctx);

        final DecltypeContext decltypectx = ctx.decltype();
        final ExpressionMetadata decltypemd = adapter.createExpressionMetadata(decltypectx);
        visit(decltypectx);

        final Type type = decltypemd.from;
        castemd.from = type;

        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
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

        Promotion promotion;

        if (ctx.ADD() != null || ctx.SUB() != null || ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null) {
            promotion = caster.decimal;
        } else {
            promotion = caster.numeric;
        }

        final ExpressionContext exprctx0 = adapter.getExpressionContext(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = promotion;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.getExpressionContext(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotion = promotion;
        visit(exprctx1);

        final Type promote = caster.getTypePromotion(ctx, expremd0.from, expremd1.from, promotion);

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
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.LSH() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst << (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst << (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.RSH() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst >> (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst >> (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.USH() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst >>> (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst >>> (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.BWAND() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst & (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst & (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.BWXOR() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst ^ (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst ^ (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.BWOR() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst | (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst | (long)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else {
                throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
            }
        }

        binaryemd.from = promote;
        caster.markCast(binaryemd);

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final ExpressionMetadata compemd = adapter.getExpressionMetadata(ctx);
        final Promotion promotion = ctx.EQ() != null || ctx.NE() != null ? caster.equality : caster.decimal;

        final ExpressionContext exprctx0 = adapter.getExpressionContext(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = promotion;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.getExpressionContext(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotion = promotion;
        visit(exprctx1);

        final Type promote = caster.getTypePromotion(ctx, expremd0.from, expremd1.from, promotion);

        if (expremd0.isNull && expremd1.isNull) {
            throw new IllegalArgumentException(error(ctx) + "Unnecessary comparison of null constants.");
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
                throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
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
                throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
            }
        }

        compemd.from = standard.boolType;
        caster.markCast(compemd);

        return null;
    }

    @Override
    public Void visitBool(final BoolContext ctx) {
        final ExpressionMetadata boolemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx0 = adapter.getExpressionContext(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.to = standard.boolType;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.getExpressionContext(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.to = standard.boolType;
        visit(exprctx1);

        if (expremd0.postConst != null && expremd1.postConst != null) {
            if (ctx.BOOLAND() != null) {
                boolemd.preConst = (boolean)expremd0.postConst && (boolean)expremd1.postConst;
            } else if (ctx.BOOLOR() != null) {
                boolemd.preConst = (boolean)expremd0.postConst || (boolean)expremd1.postConst;
            } else {
                throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
            }
        }

        boolemd.from = standard.boolType;
        caster.markCast(boolemd);

        return null;
    }

    @Override
    public Void visitConditional(final ConditionalContext ctx) {
        final ExpressionMetadata condemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx0 = adapter.getExpressionContext(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.to = standard.boolType;
        visit(exprctx0);

        if (expremd0.postConst != null) {
            throw new IllegalArgumentException(error(ctx) + "Unnecessary conditional statement.");
        }

        final ExpressionContext exprctx1 = adapter.getExpressionContext(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.to = condemd.to;
        expremd1.promotion = condemd.promotion;
        visit(exprctx1);

        final ExpressionContext exprctx2 = adapter.getExpressionContext(ctx.expression(2));
        final ExpressionMetadata expremd2 = adapter.createExpressionMetadata(exprctx2);
        expremd2.to = condemd.to;
        expremd2.promotion = condemd.promotion;
        visit(exprctx2);

        if (condemd.to != null) {
            condemd.from = condemd.to;
        } else if (condemd.promotion != null) {
            final Type promote = caster.getTypePromotion(ctx, expremd1.from, expremd2.from, condemd.promotion);

            expremd0.to = promote;
            caster.markCast(expremd1);
            expremd1.to = promote;
            caster.markCast(expremd2);

            condemd.from = promote;
        } else {
            throw new IllegalStateException(error(ctx) + "No cast or promotion specified.");
        }

        caster.markCast(condemd);

        return null;
    }

    @Override
    public Void visitAssignment(final AssignmentContext ctx) {
        External external = new External(adapter, this);
        external.assignment(ctx);
        adapter.putExternal(ctx, external);

        return null;
    }

    @Override
    public Void visitExtstart(final ExtstartContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitExtprec(final ExtprecContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitExtcast(final ExtcastContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitExtbrace(final ExtbraceContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitExtdot(final ExtdotContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitExttype(final ExttypeContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitExtcall(final ExtcallContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitExtmember(final ExtmemberContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitArguments(final ArgumentsContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitIncrement(IncrementContext ctx) {
        final ExpressionMetadata incremd = adapter.getExpressionMetadata(ctx);
        final TypeMetadata metadata = incremd.to == null ? null : incremd.to.metadata;
        final boolean positive = ctx.INCR() != null;

        if (incremd.to == null) {
            incremd.preConst = positive ? 1 : -1;
            incremd.from = standard.intType;
        } else {
            switch (metadata) {
                case LONG:
                    incremd.preConst = positive ? 1L : -1L;
                    incremd.from = standard.longType;
                case FLOAT:
                    incremd.preConst = positive ? 1.0F : -1.0F;
                    incremd.from = standard.floatType;
                case DOUBLE:
                    incremd.preConst = positive ? 1.0 : -1.0;
                    incremd.from = standard.doubleType;
                default:
                    incremd.preConst = positive ? 1 : -1;
                    incremd.from = standard.intType;
            }
        }

        caster.markCast(incremd);

        return null;
    }
}
