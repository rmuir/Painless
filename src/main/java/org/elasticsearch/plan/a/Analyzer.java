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

import java.lang.invoke.MethodHandles;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import static org.elasticsearch.plan.a.Adapter.*;
import static org.elasticsearch.plan.a.Default.*;
import static org.elasticsearch.plan.a.Definition.*;
import static org.elasticsearch.plan.a.PlanAParser.*;

class Analyzer extends PlanABaseVisitor<Void> {
    private abstract static class Promotion {
        protected final ParserRuleContext source;

        protected Promotion(final ParserRuleContext source) {
            this.source = source;
        }

        Type promote(final Type from) {
            return promote(from, null);
        }

        abstract Type promote(final Type from0, final Type from1);

        protected void exception(final Type from0, final Type from1) {
            if (from1 == null) {
                throw new ClassCastException(error(source) +
                        "Cannot find valid promotion for type [" + from0.name + "].");
            } else {
                throw new ClassCastException(error(source) + "Cannot find valid promotion for types [" +
                        from0.name + "] and [" + from1.name + "].");
            }
        }
    }

    private class NumericPromotion extends Promotion {
        NumericPromotion(final ParserRuleContext source) {
            super(source);
        }

        Type promote(final Type from0, final Type from1) {
            final Type promote = promoteToNumeric(from0, from1, false);

            if (promote == null) {
                exception(from0, from1);
            }

            return promote;
        }
    }

    private class DecimalPromotion extends Promotion {
        DecimalPromotion(final ParserRuleContext source) {
            super(source);
        }

        Type promote(final Type from0, final Type from1) {
            final Type promote = promoteToNumeric(from0, from1, true);

            if (promote == null) {
                exception(from0, from1);
            }

            return promote;
        }
    }

    private class BinaryPromotion extends Promotion {
        BinaryPromotion(final ParserRuleContext source) {
            super(source);
        }

        Type promote(final Type from0, final Type from1) {
            Type promote = promoteToType(source, from0, from1, standard.boolType);

            if (promote == null) {
                promote = promoteToNumeric(from0, from1, false);
            }

            if (promote == null) {
                exception(from0, from1);
            }

            return promote;
        }
    }

    private class CatPromotion extends Promotion {
        CatPromotion(final ParserRuleContext source) {
            super(source);
        }

        Type promote(final Type from0, final Type from1) {
            Type promote = promoteSameType(from0, from1);

            if (promote == null) {
                promote = promoteAnyType(source, from0, from1, standard.boolType);
            }

            if (promote == null) {
                promote = promoteAnyNumeric(from0, from1, true);
            }

            if (promote == null) {
                promote = promoteToSubclass(from0, from1);
            }

            if (promote == null) {
                promote = promoteToImplicit(from0, from1);
            }

            if (promote == null) {
                exception(from0, from1);
            }

            return promote;
        }
    }

    private class EqualityPromotion extends Promotion {
        EqualityPromotion(final ParserRuleContext source) {
            super(source);
        }

        Type promote(final Type from0, final Type from1) {
            Type promote = promoteAnyType(source, from0, from1, standard.boolType);

            if (promote == null) {
                promote = promoteAnyNumeric(from0, from1, true);
            }

            if (promote == null) {
                promote = promoteToSubclass(from0, from1);
            }

            if (promote == null) {
                promote = promoteToImplicit(from0, from1);
            }

            if (promote == null) {
                exception(from0, from1);
            }

            return promote;
        }
    }

    static void analyze(final Adapter adapter) {
        new Analyzer(adapter);
    }

    private final Adapter adapter;
    private final Definition definition;
    private final Standard standard;
    private final CompilerSettings settings;

    private Analyzer(final Adapter adapter) {
        this.adapter = adapter;
        definition = adapter.definition;
        standard = adapter.standard;
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

        final InitializerContext initctx = ctx.initializer();

        if (initctx != null) {
            adapter.createStatementMetadata(initctx);
            visit(initctx);
        }

        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());

        if (exprctx != null) {
            final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
            expremd.to = standard.boolType;
            visit(exprctx);

            if (expremd.postConst != null) {
                boolean constant = (boolean)expremd.postConst;

                if (!constant) {
                    throw new IllegalArgumentException(error(ctx) + "The loop will never be executed.");
                }

                exitrequired = true;
            }
        } else {
            exitrequired = true;
        }

        final AfterthoughtContext atctx = ctx.afterthought();

        if (atctx != null) {
            adapter.createStatementMetadata(atctx);
            visit(atctx);
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
    public Void visitInitializer(InitializerContext ctx) {
        final DeclarationContext declctx = ctx.declaration();
        final ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());

        if (declctx != null) {
            adapter.createStatementMetadata(declctx);
            visit(declctx);
        } else if (exprctx != null) {
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
                throw new IllegalArgumentException(error(exprctx) +
                        "The intializer of a for loop must be a statement.");
            }
        } else {
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        return null;
    }

    @Override
    public Void visitAfterthought(AfterthoughtContext ctx) {
        ExpressionContext exprctx = adapter.updateExpressionTree(ctx.expression());

        if (exprctx != null) {
            final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx);
            expremd1.to = standard.voidType;

            try {
                visit(exprctx);
            } catch (ClassCastException exception) {
                if (expremd1.statement) {
                    throw exception;
                }
            }

            if (!expremd1.statement) {
                throw new IllegalArgumentException(error(exprctx) +
                        "The afterthought of a for loop must be a statement.");
            }
        }

        return null;
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

        final String name = ctx.id().getText();
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
    public Void visitType(final TypeContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitId(final IdContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        throw new UnsupportedOperationException(error(ctx) + "Unexpected parser state.");
    }

    @Override
    public Void visitNumeric(final NumericContext ctx) {
        final ExpressionMetadata numericemd = adapter.getExpressionMetadata(ctx);
        final boolean negate = ctx.parent instanceof UnaryContext && ((UnaryContext)ctx.parent).SUB() != null;

        if (ctx.DECIMAL() != null) {
            final String svalue = (negate ? "-" : "") + ctx.DECIMAL().getText();

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
            String svalue = negate ? "-" : "";
            int radix;

            if (ctx.OCTAL() != null) {
                svalue += ctx.OCTAL().getText();
                radix = 8;
            } else if (ctx.INTEGER() != null) {
                svalue += ctx.INTEGER().getText();
                radix = 10;
            } else if (ctx.HEX() != null) {
                svalue += ctx.HEX().getText();
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

        markCast(numericemd);

        return null;
    }

    @Override
    public Void visitString(final StringContext ctx) {
        final ExpressionMetadata stringemd = adapter.getExpressionMetadata(ctx);

        if (ctx.STRING() == null) {
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        stringemd.preConst = ctx.STRING().getText();
        stringemd.from = standard.stringType;

        markCast(stringemd);

        return null;
    }

    @Override
    public Void visitChar(final CharContext ctx) {
        final ExpressionMetadata charemd = adapter.getExpressionMetadata(ctx);

        if (ctx.CHAR() == null) {
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        charemd.preConst = ctx.CHAR().getText().charAt(0);
        charemd.from = standard.charType;

        markCast(charemd);

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

        markCast(trueemd);

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

        markCast(falseemd);

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

        markCast(nullemd);

        return null;
    }

    @Override
    public Void visitCat(CatContext ctx) {
        final ExpressionMetadata catemd = adapter.getExpressionMetadata(ctx);
        final Promotion promotion = new CatPromotion(ctx);

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = promotion;
        visit(exprctx0);
        expremd0.to = expremd0.from;
        markCast(expremd0);

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotion = promotion;
        visit(exprctx1);
        expremd1.to = expremd1.from;
        markCast(expremd1);

        if (expremd0.postConst != null && expremd1.postConst != null) {
            catemd.postConst = expremd0.postConst.toString() + expremd1.postConst.toString();
        }

        catemd.from = standard.stringType;
        markCast(catemd);

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
        markCast(extemd);

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
        markCast(postincemd);

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
        markCast(preincemd);

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
            final Promotion promotion = ctx.BWNOT() != null ? new NumericPromotion(ctx) : new DecimalPromotion(ctx);
            expremd.promotion = promotion;
            visit(exprctx);

            final Type promote = promotion.promote(expremd.from);

            expremd.to = promote;
            markCast(expremd);

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
                    if (exprctx instanceof NumericContext) {
                        unaryemd.preConst = expremd.postConst;
                    } else {
                        if (tmd == TypeMetadata.INT) {
                            if (settings.getNumericOverflow()) {
                                unaryemd.preConst = -(int)expremd.postConst;
                            } else {
                                unaryemd.preConst = Math.negateExact((int)expremd.postConst);
                            }
                        } else if (tmd == TypeMetadata.LONG) {
                            if (settings.getNumericOverflow()) {
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

        markCast(unaryemd);

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

        markCast(castemd);

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final ExpressionMetadata binaryemd = adapter.getExpressionMetadata(ctx);

        Promotion promotion;

        if (ctx.ADD() != null || ctx.SUB() != null || ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null) {
            promotion = new DecimalPromotion(ctx);
        } else if (ctx.BWXOR() != null) {
            promotion = new BinaryPromotion(ctx);
        } else {
            promotion = new NumericPromotion(ctx);
        }

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = promotion;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotion = promotion;
        visit(exprctx1);

        final Type promote = promotion.promote(expremd0.from, expremd1.from);

        expremd0.to = promote;
        markCast(expremd0);
        expremd1.to = promote;
        markCast(expremd1);

        if (expremd0.postConst != null && expremd1.postConst != null) {
            final TypeMetadata tmd = promote.metadata;
            
            if (ctx.MUL() != null) {
                if (tmd == TypeMetadata.INT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst * (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.multiplyExact((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst * (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.multiplyExact((long)expremd0.postConst, (long)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.FLOAT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (float)expremd0.postConst * (float)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.multiplyWithoutOverflow((float)expremd0.postConst, (float)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.DOUBLE) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (double)expremd0.postConst * (double)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.multiplyWithoutOverflow((double)expremd0.postConst, (double)expremd1.postConst);
                    }
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.DIV() != null) {
                if (tmd == TypeMetadata.INT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst / (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.divideWithoutOverflow((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst / (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.divideWithoutOverflow((long)expremd0.postConst, (long)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.FLOAT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (float)expremd0.postConst / (float)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.divideWithoutOverflow((float)expremd0.postConst, (float)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.DOUBLE) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (double)expremd0.postConst / (double)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.divideWithoutOverflow((double)expremd0.postConst, (double)expremd1.postConst);
                    }
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.REM() != null) {
                if (tmd == TypeMetadata.INT) {
                    binaryemd.preConst = (int)expremd0.postConst % (int)expremd1.postConst;
                } else if (tmd == TypeMetadata.LONG) {
                    binaryemd.preConst = (long)expremd0.postConst % (long)expremd1.postConst;
                } else if (tmd == TypeMetadata.FLOAT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (float)expremd0.postConst % (float)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.remainderWithoutOverflow((float)expremd0.postConst, (float)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.DOUBLE) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (double)expremd0.postConst % (double)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.remainderWithoutOverflow((double)expremd0.postConst, (double)expremd1.postConst);
                    }
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.ADD() != null) {
                if (tmd == TypeMetadata.INT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst + (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.addExact((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst + (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.addExact((long)expremd0.postConst, (long)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.FLOAT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (float)expremd0.postConst + (float)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.addWithoutOverflow((float)expremd0.postConst, (float)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.DOUBLE) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (double)expremd0.postConst + (double)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.addWithoutOverflow((double)expremd0.postConst, (double)expremd1.postConst);
                    }
                } else {
                    throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
                }
            } else if (ctx.SUB() != null) {
                if (tmd == TypeMetadata.INT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (int)expremd0.postConst - (int)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.subtractExact((int)expremd0.postConst, (int)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.LONG) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (long)expremd0.postConst - (long)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Math.subtractExact((long)expremd0.postConst, (long)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.FLOAT) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (float)expremd0.postConst - (float)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.subtractWithoutOverflow((float)expremd0.postConst, (float)expremd1.postConst);
                    }
                } else if (tmd == TypeMetadata.DOUBLE) {
                    if (settings.getNumericOverflow()) {
                        binaryemd.preConst = (double)expremd0.postConst - (double)expremd1.postConst;
                    } else {
                        binaryemd.preConst = Utility.subtractWithoutOverflow((double)expremd0.postConst, (double)expremd1.postConst);
                    }
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
        markCast(binaryemd);

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final ExpressionMetadata compemd = adapter.getExpressionMetadata(ctx);
        final Promotion promotion =
                ctx.EQ() != null || ctx.EQR() != null || ctx.NE() != null || ctx.NER() != null ?
                new EqualityPromotion(ctx) : new DecimalPromotion(ctx);

        final ExpressionContext exprctx0 = adapter.updateExpressionTree(ctx.expression(0));
        final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
        expremd0.promotion = promotion;
        visit(exprctx0);

        final ExpressionContext exprctx1 = adapter.updateExpressionTree(ctx.expression(1));
        final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
        expremd1.promotion = promotion;
        visit(exprctx1);

        final Type promote = promotion.promote(expremd0.from, expremd1.from);

        if (expremd0.isNull && expremd1.isNull) {
            throw new IllegalArgumentException(error(ctx) + "Unnecessary comparison of null constants.");
        }

        expremd0.to = promote;
        markCast(expremd0);
        expremd1.to = promote;
        markCast(expremd1);

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
        markCast(compemd);

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
        markCast(boolemd);

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
            final Type promote = ((Promotion)condemd.promotion).promote(expremd1.from, expremd2.from);

            expremd1.to = promote;
            markCast(expremd1);
            expremd2.to = promote;
            markCast(expremd2);

            condemd.from = promote;
        } else {
            throw new IllegalStateException(error(ctx) + "No cast or promotion specified.");
        }

        markCast(condemd);

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
        markCast(assignemd);

        return null;
    }

    @Override
    public Void visitExtstart(final ExtstartContext ctx) {
        final ExtprecContext precctx = ctx.extprec();
        final ExtcastContext castctx = ctx.extcast();
        final ExttypeContext typectx = ctx.exttype();
        final ExtmemberContext memberctx = ctx.extmember();
        final ExtnewContext newctx = ctx.extnew();

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
        } else if (newctx != null) {
            adapter.createExtNodeMetadata(ctx, newctx);
            visit(newctx);
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
        final ExtnewContext newctx = ctx.extnew();

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
        } else if (newctx != null) {
            adapter.createExtNodeMetadata(parent, newctx);
            visit(newctx);
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
        final ExtnewContext newctx = ctx.extnew();

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

        } else if (newctx != null) {
            adapter.createExtNodeMetadata(parent, newctx);
            visit(newctx);
        } else {
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        final DecltypeContext declctx = ctx.decltype();
        final ExpressionMetadata declemd = adapter.createExpressionMetadata(declctx);
        visit(declctx);

        castenmd.castTo = getLegalCast(ctx, parentemd.current, declemd.from, true);
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

        final String typestr = ctx.type().getText();
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

        final String name = ctx.id().getText();

        if (parentemd.current.dimensions > 0) {
            throw new IllegalArgumentException(error(ctx) + "Unexpected call [" + name + "] on an array.");
        } else if (callenmd.last && parentemd.storeExpr != null) {
            throw new IllegalArgumentException(error(ctx) + "Cannot assign a value to a call [" + name + "].");
        }

        final Struct struct = parentemd.current.struct;
        final List<ExpressionContext> arguments = ctx.arguments().expression();
        final int size = arguments.size();

        final Method method = parentemd.statik ? struct.functions.get(name) : struct.methods.get(name);

        if (method == null) {
            throw new IllegalArgumentException(
                    error(ctx) + "Unknown call [" + name + "] on type [" + struct.name + "].");
        }

        final Type[] types = new Type[method.arguments.size()];
        method.arguments.toArray(types);

        callenmd.target = method;
        callenmd.type = method.rtn;

        if (!parentemd.read) {
            parentemd.current = standard.voidType;
            parentemd.statement = true;
        } else {
            parentemd.current = method.rtn;
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

        final String name = ctx.id().getText();

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
    public Void visitExtnew(ExtnewContext ctx) {
        final ExtNodeMetadata callenmd = adapter.getExtNodeMetadata(ctx);
        final ParserRuleContext parent = callenmd.parent;
        final ExternalMetadata parentemd = adapter.getExternalMetadata(parent);

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        callenmd.last = parentemd.scope == 0 && dotctx == null && bracectx == null;

        final String name = ctx.type().getText();
        final Struct struct = definition.structs.get(name);

        if (parentemd.current != null) {
            throw new IllegalArgumentException(error(ctx) + "Unexpected new call.");
        } else if (struct == null) {
            throw new IllegalArgumentException(error(ctx) + "Specified type [" + name + "] not found.");
        } else if (callenmd.last && parentemd.storeExpr != null) {
            throw new IllegalArgumentException(error(ctx) + "Cannot assign a value to a new call.");
        }

        final boolean newclass = ctx.arguments() != null;
        final boolean newarray = !ctx.expression().isEmpty();

        final List<ExpressionContext> arguments = newclass ? ctx.arguments().expression() : ctx.expression();
        final int size = arguments.size();

        Type[] types;

        if (newarray) {
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
                callenmd.type = getTypeWithArrayDimensions(struct, 0);
                parentemd.current = getTypeWithArrayDimensions(struct, 1);
            } else {
                throw new IllegalArgumentException(error(ctx) + "A newly created array cannot have zero dimensions.");
            }
        } else if (newclass) {
            final Constructor constructor = struct.constructors.get("new");

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
            } else {
                throw new IllegalArgumentException(
                        error(ctx) + "Unknown new call on type [" + struct.name + "].");
            }
        } else {
            throw new IllegalArgumentException(error(ctx) + "Unknown parser state.");
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

        markCast(incremd);

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
                storeemd.promotion = new CatPromotion(source);
                visit(store);
                storeemd.to = storeemd.from;
                markCast(storeemd);

                extenmd.castTo = getLegalCast(source, standard.stringType, extenmd.type, false);
            } else if (token > 0) {
                final boolean binary = token == BWAND || token == BWXOR || token == BWOR;
                final boolean decimal = token == MUL || token == DIV || token == REM || token == ADD || token == SUB;
                Promotion promotion = binary ? new BinaryPromotion(source) :
                        decimal ? new DecimalPromotion(source) : new NumericPromotion(source);
                extenmd.promote = promotion.promote(extenmd.type);

                extenmd.castFrom = getLegalCast(source, extenmd.type, extenmd.promote, false);
                extenmd.castTo = getLegalCast(source, extenmd.promote, extenmd.type, true);

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

    private void markCast(final ExpressionMetadata emd) {
        if (emd.from == null) {
            throw new IllegalStateException(error(emd.source) + "From cast type should never be null.");
        }

        if (emd.to != null) {
            emd.cast = getLegalCast(emd.source, emd.from, emd.to, emd.explicit);

            if (emd.preConst != null && emd.to.metadata.constant) {
                emd.postConst = constCast(emd.source, emd.preConst, emd.cast);
            }
        } else if (emd.promotion == null) {
            throw new IllegalStateException(error(emd.source) + "No cast or promotion specified.");
        }
    }

    private Cast getLegalCast(final ParserRuleContext source, final Type from, final Type to, final boolean force) {
        final Cast cast = new Cast(from, to);

        if (from.equals(to)) {
            return cast;
        }

        final Transform explicit = definition.explicits.get(cast);

        if (force && explicit != null) {
            return explicit;
        }

        final Transform implicit = definition.implicits.get(cast);

        if (implicit != null) {
            return implicit;
        }

        if (definition.upcasts.contains(cast)) {
            return cast;
        }

        if (from.metadata.numeric && to.metadata.numeric && (force || definition.numerics.contains(cast))) {
            return cast;
        }

        try {
            from.clazz.asSubclass(to.clazz);

            return cast;
        } catch (ClassCastException cce0) {
            try {
                if (force) {
                    to.clazz.asSubclass(from.clazz);

                    return cast;
                } else {
                    throw new ClassCastException(
                            error(source) + "Cannot cast from [" + from.name + "] to [" + to.name + "].");
                }
            } catch (ClassCastException cce1) {
                throw new ClassCastException(
                        error(source) + "Cannot cast from [" + from.name + "] to [" + to.name + "].");
            }
        }
    }

    private Object constCast(final ParserRuleContext source, final Object constant, final Cast cast) {
        if (cast instanceof Transform) {
            final Transform transform = (Transform)cast;
            return invokeTransform(source, transform, constant);
        } else {
            final TypeMetadata fromTMD = cast.from.metadata;
            final TypeMetadata toTMD = cast.to.metadata;

            if (fromTMD == toTMD) {
                return constant;
            } else if (fromTMD.numeric && toTMD.numeric) {
                Number number;

                if (fromTMD == TypeMetadata.CHAR) {
                    number = (int)(char)constant;
                } else {
                    number = (Number)constant;
                }

                switch (toTMD) {
                    case BYTE:   return number.byteValue();
                    case SHORT:  return number.shortValue();
                    case CHAR:   return (char)number.intValue();
                    case INT:    return number.intValue();
                    case LONG:   return number.longValue();
                    case FLOAT:  return number.floatValue();
                    case DOUBLE: return number.doubleValue();
                    default:
                        throw new IllegalStateException(error(source) + "Expected numeric type for cast.");
                }
            } else {
                throw new IllegalStateException(error(source) + "No valid constant cast from " +
                        "[" + cast.from.clazz.getCanonicalName() + "] to " +
                        "[" + cast.to.clazz.getCanonicalName() + "].");
            }
        }
    }

    private Object invokeTransform(final ParserRuleContext source, final Transform transform, final Object object) {
        final Method method = transform.method;
        final java.lang.reflect.Method jmethod = method.method;
        final int modifiers = jmethod.getModifiers();

        try {
            if (java.lang.reflect.Modifier.isStatic(modifiers)) {
                return jmethod.invoke(null, object);
            } else {
                return jmethod.invoke(object);
            }
        } catch (IllegalAccessException | IllegalArgumentException |
                java.lang.reflect.InvocationTargetException | NullPointerException |
                ExceptionInInitializerError exception) {
            throw new IllegalStateException(error(source) + "Unable to invoke transform to cast constant from " +
                    "[" + transform.from.name + "] to [" + transform.to.name + "].");
        }
    }

    private Type promoteSameType(final Type from0, final Type from1) {
        if (from1 != null && from0.equals(from1)) {
            return from0;
        }

        return null;
    }

    private Type promoteAnyType(final ParserRuleContext source, final Type from0, final Type from1, final Type to) {
        final boolean eq0 = from0.equals(to);
        final boolean eq1 = from1 != null && from1.equals(to);

        if (eq0 && (from1 == null || eq1)) {
            return to;
        }

        if (eq0 || eq1) {
            try {
                getLegalCast(source, eq0 ? from1 : from0, to, false);

                return to;
            } catch (ClassCastException exception) {
                // Do nothing.
            }
        }

        return null;
    }


    private Type promoteToType(final ParserRuleContext source, final Type from0, final Type from1, final Type to) {
        final boolean eq0 = from0.equals(to);
        final boolean eq1 = from1 == null || from1.equals(to);

        if (eq0 && eq1) {
            return to;
        }

        boolean castable = true;

        if (!eq0) {
            try {
                getLegalCast(source, from0, to, false);
            } catch (ClassCastException exception) {
                castable = false;
            }
        }

        if (!eq1) {
            try {
                getLegalCast(source, from1, to, false);
            } catch (ClassCastException exception) {
                castable = false;
            }
        }

        if (castable) {
            return to;
        }

        return null;
    }

    private Type promoteAnyNumeric(final Type from0, final Type from1, final boolean decimal) {
        if (from0.metadata.numeric || from1 != null && from1.metadata.numeric) {
            try {
                return promoteToNumeric(from0, from1, decimal);
            } catch (ClassCastException exception) {
                // Do nothing.
            }
        }

        return null;
    }

    private Type promoteToNumeric(final Type from0, final Type from1, boolean decimal) {
        final Deque<Type> upcast = new ArrayDeque<>();
        final Deque<Type> downcast = new ArrayDeque<>();

        if (decimal) {
            upcast.push(standard.doubleType);
            upcast.push(standard.floatType);
        } else {
            downcast.push(standard.doubleType);
            downcast.push(standard.floatType);
        }

        upcast.push(standard.longType);
        upcast.push(standard.intType);

        while (!upcast.isEmpty()) {
            final Type to = upcast.pop();
            final Cast cast0 = new Cast(from0, to);

            if (from0.metadata.numeric && from0.metadata != to.metadata &&
                    !definition.numerics.contains(cast0))                            continue;
            if (upcast.contains(from0))                                              continue;
            if (downcast.contains(from0) && !definition.numerics.contains(cast0) &&
                    !definition.implicits.containsKey(cast0))                        continue;
            if (!from0.metadata.numeric && !definition.implicits.containsKey(cast0)) continue;

            if (from1 != null) {
                final Cast cast1 = new Cast(from1, to);

                if (from1.metadata.numeric && from1.metadata != to.metadata &&
                        !definition.numerics.contains(cast1))                            continue;
                if (upcast.contains(from1))                                              continue;
                if (downcast.contains(from1) && !definition.numerics.contains(cast1) &&
                        !definition.implicits.containsKey(cast1))                        continue;
                if (!from1.metadata.numeric && !definition.implicits.containsKey(cast1)) continue;
            }

            return to;
        }

        return null;
    }

    private Type promoteToImplicit(final Type from0, final Type from1) {
        final Cast cast0 = new Cast(from0, from1);
        final Cast cast1 = new Cast(from1, from0);
        final Transform transform0 = definition.implicits.get(cast0);
        final Transform transform1 = definition.implicits.get(cast1);

        if (!from0.metadata.object && from1.metadata.object && transform0 != null) {
            return from1;
        }

        if (from0.metadata.object && !from1.metadata.object && transform1 != null) {
            return from0;
        }

        if (transform0 != null && transform1 == null) {
            return from0;
        }

        if (transform1 != null && transform0 == null) {
            return from1;
        }

        return null;
    }

    private Type promoteToSubclass(final Type from0, final Type from1) {
        if (from0.equals(from1)) {
            return from0;
        }

        if (from0.metadata.object && from1.metadata.object) {
            if (from0.clazz.equals(from1.clazz)) {
                if (from0.struct.generic && !from1.struct.generic) {
                    return from1;
                } else if (!from0.struct.generic && from1.struct.generic) {
                    return from0;
                }

                return standard.objectType;
            }

            try {
                from0.clazz.asSubclass(from1.clazz);

                return from1;
            } catch (ClassCastException cce0) {
                // Do nothing.
            }

            try {
                from1.clazz.asSubclass(from0.clazz);

                return from0;
            } catch (ClassCastException cce0) {
                // Do nothing.
            }

            return standard.objectType;
        }

        return null;
    }
}
