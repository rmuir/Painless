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

package org.elasticsearch.plan.a;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import static org.elasticsearch.plan.a.Adapter.*;
import static org.elasticsearch.plan.a.Caster.*;
import static org.elasticsearch.plan.a.Default.*;
import static org.elasticsearch.plan.a.Definition.*;
import static org.elasticsearch.plan.a.PlanAParser.*;

class Analyzer extends PlanABaseVisitor<Void> {
    static void analyze(final Adapter adapter) {
        new Analyzer(adapter);
    }

    private final Adapter adapter;
    private final Definition definition;
    private final Standard standard;
    private final Caster caster;
    private final CompilerSettings settings;

    private Analyzer(final Adapter adapter) {
        this.adapter = adapter;
        definition = adapter.definition;
        standard = adapter.standard;
        caster = adapter.caster;
        settings = adapter.settings;

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

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
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

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
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

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
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

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));

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

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));

        if (exprctx1 != null) {
            final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
            expremd1.to = standard.voidType;

            try {
                visit(exprctx1);
            } catch (ClassCastException exception) {
                if (expremd1.statement) {
                    throw exception;
                }
            }

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

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
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
        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.voidType;

        try {
            visit(exprctx);
        } catch (ClassCastException exception) {
            if (expremd.statement) {
                throw exception;
            }
        }

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
            multiplesmd.allContinue = !statesmd.anyReturn && !statesmd.anyBreak && statesmd.allContinue;
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
        declvaremd.postConst = adapter.addVariable(ctx, name, declvaremd.to).slot;

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());

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

                    if (tmd == TypeMetadata.BYTE && value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                        numericemd.from = standard.byteType;
                        numericemd.preConst = (byte)value;
                    } else if (tmd == TypeMetadata.CHAR && value >= Character.MIN_VALUE && value <= Character.MAX_VALUE) {
                        numericemd.from = standard.charType;
                        numericemd.preConst = (char)value;
                    } else if (tmd == TypeMetadata.SHORT && value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                        numericemd.from = standard.shortType;
                        numericemd.preConst = (short)value;
                    } else {
                        numericemd.from = standard.intType;
                        numericemd.preConst = value;
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

        if (nullemd.to != null && nullemd.to.metadata.object) {
            nullemd.from = nullemd.to;
        } else {
            nullemd.from = standard.objectType;
        }

        caster.markCast(nullemd);

        return null;
    }

    @Override
    public Void visitCat(CatContext ctx) {
        ExpressionMetadata catemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = caster.concat;
        visit(exprctx0);
        expremd0.to = expremd0.from;
        caster.markCast(expremd0);

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotion = caster.concat;
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
    public Void visitExternal(final ExternalContext ctx) {
        final ExpressionMetadata extemd = adapter.getExpressionMetadata(ctx);

        final ExtstartContext extstartctx = ctx.extstart();
        final ExternalMetadata extstartemd = adapter.createExternalMetadata(extstartctx);
        extstartemd.read = extemd.promotion != null || extemd.to.metadata != TypeMetadata.VOID;
        visit(extstartctx);

        extemd.statement = extstartemd.statement;
        extemd.from = extstartemd.current;
        caster.markCast(extemd);

        return null;
    }

    @Override
    public Void visitPostinc(final PostincContext ctx) {
        final ExpressionMetadata postincemd = adapter.getExpressionMetadata(ctx);

        final ExtstartContext extstartctx = ctx.extstart();
        final ExternalMetadata extstartemd = adapter.createExternalMetadata(extstartctx);
        extstartemd.read = postincemd.promotion != null || postincemd.to.metadata != TypeMetadata.VOID;
        extstartemd.storeExpr = ctx.increment();
        extstartemd.token = ADD;
        extstartemd.post = true;
        postincemd.statement = true;
        visit(extstartctx);

        postincemd.from = extstartemd.current;
        caster.markCast(postincemd);

        return null;
    }

    @Override
    public Void visitPreinc(final PreincContext ctx) {
        final ExpressionMetadata preincemd = adapter.getExpressionMetadata(ctx);

        final ExtstartContext extstartctx = ctx.extstart();
        final ExternalMetadata extstartemd = adapter.createExternalMetadata(extstartctx);
        extstartemd.read = preincemd.promotion != null || preincemd.to.metadata != TypeMetadata.VOID;
        extstartemd.storeExpr = ctx.increment();
        extstartemd.token = ADD;
        extstartemd.pre = true;
        preincemd.statement = true;
        visit(extstartctx);

        preincemd.from = extstartemd.current;
        caster.markCast(preincemd);

        return null;
    }

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final ExpressionMetadata unaryemd = adapter.getExpressionMetadata(ctx);

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
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
                        if (settings.getIntegerOverflow()) {
                            unaryemd.preConst = -(int)expremd.postConst;
                        } else {
                            unaryemd.preConst = Math.negateExact((int)expremd.postConst);
                        }
                    } else if (tmd == TypeMetadata.LONG) {
                        if (settings.getIntegerOverflow()) {
                            unaryemd.preConst = -(long)expremd.postConst;
                        } else {
                            unaryemd.preConst = Math.negateExact((long)expremd.postConst);
                        }
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

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
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
        } else if (ctx.BWXOR() != null) {
            promotion = caster.binary;
        } else {
            promotion = caster.numeric;
        }

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = promotion;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
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
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst * (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.multiplyExact((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst * (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.multiplyExact((long)expremd0.postConst, (long)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.FLOAT) {
                    binaryemd.preConst = (float)expremd0.postConst * (float)expremd1.postConst;
                } else if (tmd == TypeMetadata.DOUBLE) {
                    binaryemd.preConst = (double)expremd0.postConst * (double)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.DIV() != null) {
                if (tmd == TypeMetadata.INT) {
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst / (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.divideWithoutOverflow((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst / (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.divideWithoutOverflow((long)expremd0.postConst, (long)expremd1.postConst);
                    }
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
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst + (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.addExact((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst + (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.addExact((long)expremd0.postConst, (long)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.FLOAT) {
                    binaryemd.preConst = (float)expremd0.postConst + (float)expremd1.postConst;
                } else if (tmd == TypeMetadata.DOUBLE) {
                    binaryemd.preConst = (double)expremd0.postConst + (double)expremd1.postConst;
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.SUB() != null) {
                if (tmd == TypeMetadata.INT) {
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst - (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.subtractExact((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getIntegerOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst - (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.subtractExact((long)expremd0.postConst, (long)expremd1.postConst);
                    }
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
                if (tmd == TypeMetadata.BOOL) {
                    binaryemd.preConst = (boolean)expremd0.postConst ^ (boolean)expremd1.postConst;
                } else if (tmd == TypeMetadata.INT) {
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
        final Promotion promotion =
                ctx.EQ() != null || ctx.EQR() != null || ctx.NE() != null || ctx.NER() != null ?
                caster.equality : caster.decimal;

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = promotion;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
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

            if (ctx.EQ() != null || ctx.EQR() != null) {
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
                    if (ctx.EQ() != null && !expremd0.isNull && !expremd1.isNull) {
                        compemd.preConst = expremd0.postConst.equals(expremd1.postConst);
                    } else if (ctx.EQR() != null) {
                        compemd.preConst = expremd0.postConst == expremd1.postConst;
                    }
                }
            } else if (ctx.NE() != null || ctx.NER() != null) {
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
                    if (ctx.NE() != null && !expremd0.isNull && !expremd1.isNull) {
                        compemd.preConst = expremd0.postConst.equals(expremd1.postConst);
                    } else if (ctx.NER() != null) {
                        compemd.preConst = expremd0.postConst == expremd1.postConst;
                    }
                }
            } else if (ctx.GTE() != null) {
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

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.to = standard.boolType;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
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

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.to = standard.boolType;
        visit(exprctx0);

        if (expremd0.postConst != null) {
            throw new IllegalArgumentException(error(ctx) + "Unnecessary conditional statement.");
        }

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.to = condemd.to;
        expremd1.promotion = condemd.promotion;
        expremd1.explicit = condemd.explicit;
        visit(exprctx1);

        final ExpressionContext exprctx2 = adapter.updateExpressionTree(ctx.expression(2));
        final ExpressionMetadata expremd2 = adapter.createExpressionMetadata(exprctx2);
        expremd2.to = condemd.to;
        expremd2.promotion = condemd.promotion;
        expremd2.explicit = condemd.explicit;
        visit(exprctx2);

        if (condemd.to != null) {
            condemd.from = condemd.to;
        } else if (condemd.promotion != null) {
            final Type promote = caster.getTypePromotion(ctx, expremd1.from, expremd2.from, condemd.promotion);

            expremd1.to = promote;
            caster.markCast(expremd1);
            expremd2.to = promote;
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
        final ExpressionMetadata assignemd = adapter.getExpressionMetadata(ctx);

        final ExtstartContext extstartctx = ctx.extstart();
        final ExternalMetadata extstartemd = adapter.createExternalMetadata(extstartctx);

        extstartemd.read = assignemd.promotion != null || assignemd.to.metadata != TypeMetadata.VOID;
        extstartemd.storeExpr = adapter.updateExpressionTree(ctx.expression());

        if (ctx.AMUL() != null) {
            extstartemd.token = MUL;
        } else if (ctx.ADIV() != null) {
            extstartemd.token = DIV;
        } else if (ctx.AREM() != null) {
            extstartemd.token = REM;
        } else if (ctx.AADD() != null) {
            extstartemd.token = ADD;
        } else if (ctx.ASUB() != null) {
            extstartemd.token = SUB;
        } else if (ctx.ALSH() != null) {
            extstartemd.token = LSH;
        } else if (ctx.AUSH() != null) {
            extstartemd.token = USH;
        } else if (ctx.ARSH() != null) {
            extstartemd.token = RSH;
        } else if (ctx.AAND() != null) {
            extstartemd.token = BWAND;
        } else if (ctx.AXOR() != null) {
            extstartemd.token = BWXOR;
        } else if (ctx.AOR() != null) {
            extstartemd.token = BWOR;
        } else if (ctx.ACAT() != null) {
            extstartemd.token = CAT;
        }

        assignemd.statement = true;

        visit(extstartctx);

        assignemd.from = extstartemd.current;
        caster.markCast(assignemd);

        return null;
    }

    @Override
    public Void visitExtstart(final ExtstartContext ctx) {
        final ExtprecContext precctx = ctx.extprec();
        final ExtcastContext castctx = ctx.extcast();
        final ExttypeContext typectx = ctx.exttype();
        final ExtmemberContext memberctx = ctx.extmember();

        if (precctx != null) {
            adapter.createExtNodeMetadata(ctx, precctx);
            visit(precctx);
        } else if (castctx != null) {
            adapter.createExtNodeMetadata(ctx, castctx);
            visit(castctx);
        } else if (typectx != null) {
            adapter.createExtNodeMetadata(ctx, typectx);
            visit(typectx);
        } else if (memberctx != null) {
            adapter.createExtNodeMetadata(ctx, memberctx);
            visit(memberctx);
        } else {
            throw new IllegalStateException();
        }

        return null;
    }

    @Override
    public Void visitExtprec(final ExtprecContext ctx) {
        final ExtNodeMetadata precenmd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = precenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        final ExtprecContext precctx = ctx.extprec();
        final ExtcastContext castctx = ctx.extcast();
        final ExttypeContext typectx = ctx.exttype();
        final ExtmemberContext memberctx = ctx.extmember();
        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        if (dotctx != null || bracectx != null) {
            ++parentemd.scope;
        }

        if (precctx != null) {
            adapter.createExtNodeMetadata(parent, precctx);
            visit(precctx);
        } else if (castctx != null) {
            adapter.createExtNodeMetadata(parent, castctx);
            visit(castctx);
        } else if (typectx != null) {
            adapter.createExtNodeMetadata(parent, typectx);
            visit(typectx);
        } else if (memberctx != null) {
            adapter.createExtNodeMetadata(parent, memberctx);
            visit(memberctx);
        } else {
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        parentemd.statement = false;

        if (dotctx != null) {
            --parentemd.scope;

            adapter.createExtNodeMetadata(parent, dotctx);
            visit(dotctx);
        } else if (bracectx != null) {
            --parentemd.scope;

            adapter.createExtNodeMetadata(parent, bracectx);
            visit(bracectx);
        }

        return null;
    }

    @Override
    public Void visitExtcast(final ExtcastContext ctx) {
        final ExtNodeMetadata castenmd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = castenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        final ExtprecContext precctx = ctx.extprec();
        final ExtcastContext castctx = ctx.extcast();
        final ExttypeContext typectx = ctx.exttype();
        final ExtmemberContext memberctx = ctx.extmember();

        if (precctx != null) {
            adapter.createExtNodeMetadata(parent, precctx);
            visit(precctx);
        } else if (castctx != null) {
            adapter.createExtNodeMetadata(parent, castctx);
            visit(castctx);
        } else if (typectx != null) {
            adapter.createExtNodeMetadata(parent, typectx);
            visit(typectx);
        } else if (memberctx != null) {
            adapter.createExtNodeMetadata(parent, memberctx);
            visit(memberctx);
        } else {
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        final DecltypeContext declctx = ctx.decltype();
        final ExpressionMetadata declemd = adapter.createExpressionMetadata(declctx);
        visit(declctx);

        castenmd.castTo = caster.getLegalCast(ctx, parentemd.current, declemd.from, true);
        castenmd.type = declemd.from;
        parentemd.current = declemd.from;
        parentemd.statement = false;

        return null;
    }

    @Override
    public Void visitExtbrace(final ExtbraceContext ctx) {
        final ExtNodeMetadata braceenmd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = braceenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        if (parentemd.current.dimensions == 0) {
            throw new IllegalArgumentException(error(ctx) +
                    "Attempting to address a non-array type [" + parentemd.current.name + "] as an array.");
        }

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        braceenmd.last = parentemd.scope == 0 && dotctx == null && bracectx == null;

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.intType;
        visit(exprctx);

        braceenmd.target = "#brace";
        braceenmd.type = getTypeWithArrayDimensions(parentemd.current.struct, parentemd.current.dimensions - 1);
        parentemd.current = analyzeLoadStoreExternal(ctx);

        if (dotctx != null) {
            adapter.createExtNodeMetadata(parent, dotctx);
            visit(dotctx);
        } else if (bracectx != null) {
            adapter.createExtNodeMetadata(parent, bracectx);
            visit(bracectx);
        }

        return null;
    }

    @Override
    public Void visitExtdot(final ExtdotContext ctx) {
        final ExtNodeMetadata dotemnd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = dotemnd.parent;

        final ExtcallContext callctx = ctx.extcall();
        final ExtmemberContext memberctx = ctx.extmember();

        if (callctx != null) {
            adapter.createExtNodeMetadata(parent, callctx);
            visit(callctx);
        } else if (memberctx != null) {
            adapter.createExtNodeMetadata(parent, memberctx);
            visit(memberctx);
        }

        return null;
    }

    @Override
    public Void visitExttype(final ExttypeContext ctx) {
        final ExtNodeMetadata typeenmd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = typeenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        if (parentemd.current != null) {
            throw new IllegalArgumentException(error(ctx) + "Unexpected static type.");
        }

        final String typestr = ctx.ID().getText();
        typeenmd.type = getTypeFromCanonicalName(definition, typestr);
        parentemd.current = typeenmd.type;
        parentemd.statik = true;

        final ExtdotContext dotctx = ctx.extdot();
        adapter.createExtNodeMetadata(parent, dotctx);
        visit(dotctx);

        return null;
    }

    @Override
    public Void visitExtcall(final ExtcallContext ctx) {
        final ExtNodeMetadata callenmd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = callenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        callenmd.last = parentemd.scope == 0 && dotctx == null && bracectx == null;

        final String name = ctx.ID().getText();

        if (parentemd.current.dimensions > 0) {
            throw new IllegalArgumentException(error(ctx) + "Unexpected call [" + name + "] on an array.");
        } else if (callenmd.last && parentemd.storeExpr != null) {
            throw new IllegalArgumentException(error(ctx) + "Cannot assign a value to a call [" + name + "].");
        }

        final Struct struct = parentemd.current.struct;
        final List<ExpressionContext> arguments = ctx.arguments().expression();
        final int size = arguments.size();

        Type[] types;

        if (parentemd.statik && "makearray".equals(name)) {
            if (!parentemd.read) {
                throw new IllegalArgumentException(error(ctx) + "A newly created array must be assigned.");
            }

            types = new Type[size];
            Arrays.fill(types, standard.intType);

            callenmd.target = "#makearray";

            if (size > 1) {
                callenmd.type = getTypeWithArrayDimensions(struct, size);
                parentemd.current = callenmd.type;
            } else if (size == 1) {
                callenmd.type = parentemd.current;
                parentemd.current = getTypeWithArrayDimensions(struct, 1);
            } else {
                throw new IllegalArgumentException(error(ctx) + "A newly created array cannot have zeor dimensions.");
            }
        } else {
            final Constructor constructor = parentemd.statik ? struct.constructors.get(name) : null;
            final Method method = parentemd.statik ? struct.functions.get(name) : struct.methods.get(name);

            if (constructor != null) {
                types = new Type[constructor.arguments.size()];
                constructor.arguments.toArray(types);

                callenmd.target = constructor;
                callenmd.type = getTypeWithArrayDimensions(struct, 0);

                if (!parentemd.read) {
                    parentemd.current = standard.voidType;
                    parentemd.statement = true;
                } else {
                    parentemd.current = callenmd.type;
                }
            } else if (method != null) {
                types = new Type[method.arguments.size()];
                method.arguments.toArray(types);

                callenmd.target = method;
                callenmd.type = method.rtn;

                if (!parentemd.read) {
                    parentemd.current = standard.voidType;
                    parentemd.statement = true;
                } else {
                    parentemd.current = method.rtn;
                }
            } else {
                throw new IllegalArgumentException(
                        error(ctx) + "Unknown call [" + name + "] on type [" + struct.name + "].");
            }
        }

        if (size != types.length) {
            throw new IllegalArgumentException(error(ctx) + "When calling [" + name + "] on type " +
                    "[" + struct.name + "] expected [" + types.length + "] arguments," +
                    " but found [" + arguments.size() + "].");
        }

        for (int argument = 0; argument < size; ++argument) {
            final ExpressionContext exprctx = adapter.updateExpressionTree(arguments.get(argument));
            final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
            expremd.to = types[argument];
            visit(exprctx);
        }

        parentemd.statik = false;

        if (dotctx != null) {
            adapter.createExtNodeMetadata(parent, dotctx);
            visit(dotctx);
        } else if (bracectx != null) {
            adapter.createExtNodeMetadata(parent, bracectx);
            visit(bracectx);
        }

        return null;
    }

    @Override
    public Void visitExtmember(final ExtmemberContext ctx) {
        final ExtNodeMetadata memberenmd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = memberenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        final String name = ctx.ID().getText();

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        memberenmd.last = parentemd.scope == 0 && dotctx == null && bracectx == null;
        final boolean store = memberenmd.last && parentemd.storeExpr != null;

        if (parentemd.current == null) {
            final Variable variable = adapter.getVariable(name);

            if (variable == null) {
                throw new IllegalArgumentException(error(ctx) + "Unknown variable [" + name + "].");
            }

            memberenmd.target = variable.slot;
            memberenmd.type = variable.type;
            parentemd.current = analyzeLoadStoreExternal(ctx);
        } else {
            if (parentemd.current.dimensions > 0) {
                if ("length".equals(name)) {
                    if (!parentemd.read) {
                        throw new IllegalArgumentException(error(ctx) + "Must read array field [length].");
                    } else if (store) {
                        throw new IllegalArgumentException(
                                error(ctx) + "Cannot write to read-only array field [length].");
                    }

                    memberenmd.target = "#length";
                    memberenmd.type = standard.intType;
                    parentemd.current = standard.intType;
                } else {
                    throw new IllegalArgumentException(error(ctx) + "Unexpected array field [" + name + "].");
                }
            } else {
                final Struct struct = parentemd.current.struct;
                final Field field = parentemd.statik ? struct.statics.get(name) : struct.members.get(name);

                if (field == null) {
                    throw new IllegalArgumentException(
                            error(ctx) + "Unknown field [" + name + "] for type [" + struct.name + "].");
                }

                if (store && java.lang.reflect.Modifier.isFinal(field.field.getModifiers())) {
                        throw new IllegalArgumentException(error(ctx) + "Cannot write to read-only" +
                                " field [" + name + "] for type [" + struct.name + "].");
                }

                memberenmd.target = field;
                memberenmd.type = field.type;
                parentemd.current = analyzeLoadStoreExternal(ctx);
            }
        }

        parentemd.statik = false;

        if (dotctx != null) {
            adapter.createExtNodeMetadata(parent, dotctx);
            visit(dotctx);
        } else if (bracectx != null) {
            adapter.createExtNodeMetadata(parent, bracectx);
            visit(bracectx);
        }

        return null;
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

    private Type analyzeLoadStoreExternal(final ParserRuleContext source) {
        final ExtNodeMetadata extenmd = adapter.getExtNodeMetadata(source);
        final ParserRuleContext parent = extenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        if (extenmd.last && parentemd.storeExpr != null) {
            final ParserRuleContext store = parentemd.storeExpr;
            final ExpressionMetadata storeemd = adapter.createExpressionMetadata(parentemd.storeExpr);
            final int token = parentemd.token;

            if (token == CAT) {
                storeemd.promotion = caster.concat;
                visit(store);
                storeemd.to = storeemd.from;
                caster.markCast(storeemd);

                extenmd.castTo = caster.getLegalCast(source, standard.stringType, extenmd.type, false);
            } else if (token > 0) {
                final boolean compound = token == BWAND || token == BWXOR || token == BWOR;
                final boolean decimal = token == MUL || token == DIV || token == REM || token == ADD || token == SUB;
                Promotion promotion = compound ? caster.compound : decimal ? caster.decimal : caster.numeric;
                extenmd.promote = caster.getTypePromotion(source, extenmd.type, null, promotion);

                extenmd.castFrom = caster.getLegalCast(source, extenmd.type, extenmd.promote, false);
                extenmd.castTo = caster.getLegalCast(source, extenmd.promote, extenmd.type, true);

                storeemd.to = extenmd.promote;
                visit(store);
            } else {
                storeemd.to = extenmd.type;
                visit(store);
            }

            return parentemd.read ? extenmd.type : standard.voidType;
        } else {
            return extenmd.type;
        }
    }
}
