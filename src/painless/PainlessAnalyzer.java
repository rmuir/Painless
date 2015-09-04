package painless;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

import static painless.PainlessCast.*;
import static painless.PainlessParser.*;

class PainlessAnalyzer extends PainlessBaseVisitor<Object> {
    final PainlessAdapter adapter;
    final Map<ParseTree, Type> expected;
    int loop;

    PainlessAnalyzer(final ParseTree tree, final PainlessAdapter adapter) {
        this.adapter = adapter;
        expected = new HashMap<>();
        loop = 0;

        this.adapter.newVariable("this", Type.getType("Lpainless/PainlessExecutable$CompiledPainlessExecutable;"));
        this.adapter.newVariable("input", Type.getType("Ljava/util/Map;"));

        visit(tree);
    }

    @Override
    public Object visitSource(PainlessParser.SourceContext ctx) {
        boolean last = false;

        adapter.incrementScope(ctx);

        for (StatementContext sctx : ctx.statement()) {
            if (last) {
                throw new IllegalArgumentException();
            }

            last = (Boolean)visit(sctx);
        }

        adapter.decrementScope();

        return null;
    }

    @Override
    public Object visitIf(PainlessParser.IfContext ctx) {
        ExpressionContext expression = ctx.expression();
        boolean last;

        adapter.incrementScope(ctx);
        expected.put(expression, Type.BOOLEAN_TYPE);
        visit(expression);
        Type from = expected.get(expression);
        adapter.markCast(expression, from, Type.BOOLEAN_TYPE, false);
        last = (Boolean)visit(ctx.block(0));

        if (ctx.ELSE() != null) {
            last &= (Boolean)visit(ctx.block(1));
        } else {
            last = false;
        }

        adapter.decrementScope();

        return last;
    }

    @Override
    public Object visitWhile(PainlessParser.WhileContext ctx) {
        ExpressionContext expression = ctx.expression();

        ++loop;
        adapter.incrementScope(ctx);
        expected.put(expression, Type.BOOLEAN_TYPE);
        visit(expression);
        Type from = expected.get(expression);
        adapter.markCast(expression, from, Type.BOOLEAN_TYPE, false);
        visit(ctx.block());
        adapter.decrementScope();
        --loop;

        return false;
    }

    @Override
    public Object visitDo(PainlessParser.DoContext ctx) {
        ExpressionContext expression = ctx.expression();

        ++loop;
        adapter.incrementScope(ctx);
        visit(ctx.block());
        expected.put(expression, Type.BOOLEAN_TYPE);
        visit(expression);
        Type from = expected.get(expression);
        adapter.markCast(expression, from, Type.BOOLEAN_TYPE, false);
        adapter.decrementScope();
        --loop;

        return false;
    }

    @Override
    public Object visitFor(PainlessParser.ForContext ctx) {
        ExpressionContext expression = ctx.expression(0);

        ++loop;
        adapter.incrementScope(ctx);
        visit(ctx.declaration());
        visit(expression);
        expected.put(expression, Type.BOOLEAN_TYPE);
        visit(ctx.expression(1));
        Type from = expected.get(expression);
        adapter.markCast(expression, from, Type.BOOLEAN_TYPE, false);
        visit(ctx.block());
        adapter.decrementScope();
        --loop;

        return false;
    }

    @Override
    public Object visitDecl(PainlessParser.DeclContext ctx) {
        visit(ctx.declaration());

        return false;
    }

    @Override
    public Object visitContinue(PainlessParser.ContinueContext ctx) {
        if (loop == 0) {
            throw new IllegalStateException();
        }

        return true;
    }

    @Override
    public Object visitBreak(PainlessParser.BreakContext ctx) {
        if (loop == 0) {
            throw new IllegalStateException();
        }

        return true;
    }

    @Override
    public Object visitReturn(PainlessParser.ReturnContext ctx) {
        ExpressionContext expression = ctx.expression();
        expected.put(expression, OBJECT_TYPE);
        visit(expression);
        Type from = expected.get(expression);
        adapter.markCast(ctx.expression(), from, OBJECT_TYPE, false);

        return true;
    }

    @Override
    public Object visitExpr(PainlessParser.ExprContext ctx) {
        visit(ctx.expression());

        return false;
    }

    @Override
    public Object visitMultiple(PainlessParser.MultipleContext ctx) {
        boolean last = false;

        for (StatementContext sctx : ctx.statement()) {
            if (last) {
                throw new IllegalArgumentException();
            }

            last = (Boolean)visit(sctx);
        }

        return last;
    }

    @Override
    public Object visitSingle(PainlessParser.SingleContext ctx) {
        return visit(ctx.statement());
    }

    @Override
    public Object visitEmpty(PainlessParser.EmptyContext ctx) {
        return false;
    }

    @Override
    public Object visitDeclaration(PainlessParser.DeclarationContext ctx) {
        DecltypeContext decltype = ctx.decltype();
        visit(decltype);
        Type to = expected.get(decltype);

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof TerminalNode) {
                TerminalNode tctx = (TerminalNode)cctx;

                if (tctx.getSymbol().getType() == PainlessLexer.ID) {
                    String name = tctx.getText();
                    adapter.newVariable(name, to);
                }
            } else if (cctx instanceof ExpressionContext) {
                expected.put(cctx, to);
                visit(cctx);
                Type from = expected.get(cctx);
                adapter.markCast(cctx, from, to, false);
            }
        }

        return null;
    }

    @Override
    public Object visitDecltype(PainlessParser.DecltypeContext ctx) {
        String type = ctx.TYPE().getText();
        int dimensions = ctx.LBRACE().size();
        String descriptor;

        switch (type) {
            case "bool":
                descriptor = "Z";
                break;
            case "byte":
                descriptor = "B";
                break;
            case "char":
                descriptor = "C";
                break;
            case "short":
                descriptor = "S";
                break;
            case "int":
                descriptor = "I";
                break;
            case "long":
                descriptor = "J";
                break;
            case "float":
                descriptor = "F";
                break;
            case "double":
                descriptor = "D";
                break;
            case "object":
                descriptor = "Ljava/lang/Object;";
                break;
            case "string":
                descriptor = "Ljava/lang/String;";
                break;
            case "list":
                descriptor = "Ljava/util/List;";
                break;
            case "map":
                descriptor = "Ljava/util/Map;";
                break;
            case "executable":
                descriptor = "Lpainless/PainlessExecutable$CompiledPainlessExecutable;";
                break;
            default:
                throw new IllegalArgumentException();
        }

        for (int dimension = 0; dimension < dimensions; ++dimension) {
            descriptor = '[' + descriptor;
        }

        expected.put(ctx, Type.getType(descriptor));

        return null;
    }

    @Override
    public Object visitPrecedence(PainlessParser.PrecedenceContext ctx) {
        ExpressionContext expression = ctx.expression();
        Type to = expected.get(ctx);

        if (to == null) {
            visit(expression);
        } else {
            expected.put(expression, to);
            visit(expression);
            Type from = expected.get(expression);
            adapter.markCast(expression, from, to, false);
        }

        return null;
    }

    @Override
    public Object visitNumeric(PainlessParser.NumericContext ctx) {
        Type type = expected.get(ctx);

        if (type == null || !isNumeric(type)) {
            throw new IllegalArgumentException();
        }

        if (ctx.DECIMAL() != null) {
            String value = ctx.OCTAL().getText();
            char last = value.charAt(value.length() - 1);
            type = last == 'f' || last == 'F'? Type.FLOAT_TYPE : Type.DOUBLE_TYPE;
        } else {
            String value;

            if (ctx.OCTAL() != null) {
                value = ctx.OCTAL().getText();
            } else if (ctx.INTEGER() != null) {
                value = ctx.INTEGER().getText();
            } else if (ctx.HEX() != null) {
                value = ctx.HEX().getText();
            } else {
                throw new IllegalArgumentException();
            }

            char last = value.charAt(value.length() - 1);
            type = last == 'l' || last == 'L'? Type.LONG_TYPE : Type.INT_TYPE;
        }

        expected.put(ctx, type);

        return null;
    }

    @Override
    public Object visitString(PainlessParser.StringContext ctx) {
        Type type = expected.get(ctx);

        if (type == null) {
            throw new IllegalArgumentException();
        }

        expected.put(ctx, STRING_TYPE);

        return null;
    }

    @Override
    public Object visitChar(PainlessParser.CharContext ctx) {
        Type type = expected.get(ctx);

        if (type == null) {
            throw new IllegalArgumentException();
        }

        expected.put(ctx, Type.CHAR_TYPE);

        return null;
    }

    @Override
    public Object visitTrue(PainlessParser.TrueContext ctx) {
        Type type = expected.get(ctx);

        if (type == null) {
            throw new IllegalArgumentException();
        }

        expected.put(ctx, Type.BOOLEAN_TYPE);

        return null;
    }

    @Override
    public Object visitFalse(PainlessParser.FalseContext ctx) {
        Type type = expected.get(ctx);

        if (type == null) {
            throw new IllegalArgumentException();
        }

        expected.put(ctx, Type.BOOLEAN_TYPE);

        return null;
    }

    @Override
    public Object visitNull(PainlessParser.NullContext ctx) {
        Type type = expected.get(ctx);

        if (type.getSort() != Type.OBJECT && type.getSort() != Type.ARRAY) {
            throw new IllegalArgumentException();
        }

        expected.put(ctx, OBJECT_TYPE);

        return null;
    }

    @Override
    public Object visitUnary(PainlessParser.UnaryContext ctx) {
        ExpressionContext expression = ctx.expression();
        Type to = expected.get(ctx);

        if (to == null) {
            throw new IllegalArgumentException();
        }

        if (ctx.BOOLNOT() != null) {
            expected.put(expression, Type.BOOLEAN_TYPE);
            visit(expression);
            Type from = expected.get(expression);
            adapter.markCast(expression, from, Type.BOOLEAN_TYPE, false);
            expected.put(ctx, Type.BOOLEAN_TYPE);
        } else if (isNumeric(to)) {
            expected.put(expression, to);
            visit(expression);
            Type from = expected.get(expression);
            int sort = from.getSort();

            if (sort != Type.DOUBLE && sort != Type.FLOAT && sort != Type.LONG) {
                adapter.markCast(expression, from, Type.INT_TYPE, false);
                expected.put(ctx, Type.INT_TYPE);
            } else {
                expected.put(ctx, from);
            }
        } else {
            throw new IllegalArgumentException();
        }

        return null;
    }

    @Override
    public Object visitCast(PainlessParser.CastContext ctx) {
        DecltypeContext decltype = ctx.decltype();
        visit(decltype);
        Type to = expected.get(decltype);

        ExpressionContext expression = ctx.expression();
        expected.put(expression, to);
        visit(expression);
        Type from = expected.get(expression);
        expected.get(from);
        adapter.markCast(expression, from, to, true);
        expected.put(ctx, to);

        return null;
    }

    @Override
    public Object visitBinary(PainlessParser.BinaryContext ctx) {
        Type to = expected.get(ctx);

        if (!isNumeric(to)) {
            throw new IllegalArgumentException();
        }

        ExpressionContext expr0 = ctx.expression(0);
        ExpressionContext expr1 = ctx.expression(1);

        expected.put(expr0, NUMBER_TYPE);
        expected.put(expr1, NUMBER_TYPE);

        visit(expr0);
        visit(expr1);

        Type type0 = expected.get(expr0);
        Type type1 = expected.get(expr1);

        if (!isNumeric(type0) || !isNumeric(type1)) {
            throw new IllegalArgumentException();
        }

        int sort0 = type0.getSort();
        int sort1 = type1.getSort();

        Type promote;

        if (sort0 == Type.DOUBLE) {
            adapter.markCast(expr1, type1, Type.DOUBLE_TYPE, false);
            promote = Type.DOUBLE_TYPE;
        } else if (sort1 == Type.DOUBLE) {
            adapter.markCast(expr0, type0, Type.DOUBLE_TYPE, false);
            promote = Type.DOUBLE_TYPE;
        } else if (sort0 == Type.FLOAT) {
            adapter.markCast(expr1, type1, Type.FLOAT_TYPE, false);
            promote = Type.FLOAT_TYPE;
        } else if (sort1 == Type.FLOAT) {
            adapter.markCast(expr0, type0, Type.FLOAT_TYPE, false);
            promote = Type.FLOAT_TYPE;
        } else if (sort0 == Type.LONG) {
            adapter.markCast(expr1, type1, Type.LONG_TYPE, false);
            promote = Type.LONG_TYPE;
        } else if (sort1 == Type.LONG) {
            adapter.markCast(expr0, type0, Type.LONG_TYPE, false);
            promote = Type.LONG_TYPE;
        } else {
            adapter.markCast(expr0, type0, Type.INT_TYPE, false);
            adapter.markCast(expr1, type1, Type.INT_TYPE, false);
            promote = Type.INT_TYPE;
        }

        expected.put(ctx, promote);

        return null;
    }

    @Override
    public Object visitComp(PainlessParser.CompContext ctx) {
        Type to = expected.get(ctx);

        if (to == null) {
            throw new IllegalArgumentException();
        }

        ExpressionContext expr0 = ctx.expression(0);
        ExpressionContext expr1 = ctx.expression(1);

        if (ctx.EQ() != null || ctx.NE() != null) {
            expected.put(expr0, COMPARATOR_TYPE);
            expected.put(expr1, COMPARATOR_TYPE);
        } else if (ctx.GT() != null || ctx.GTE() != null || ctx.LT() != null || ctx.LTE() != null) {
            expected.put(expr0, NUMBER_TYPE);
            expected.put(expr1, NUMBER_TYPE);
        } else {
            throw new IllegalStateException();
        }

        visit(expr0);
        visit(expr1);

        Type type0 = expected.get(expr0);
        Type type1 = expected.get(expr1);

        int sort0 = type0.getSort();
        int sort1 = type1.getSort();

        if (isNumeric(type0) && isNumeric(type1)) {
            if (sort0 == Type.DOUBLE) {
                adapter.markCast(expr1, type1, Type.DOUBLE_TYPE, false);
            } else if (sort1 == Type.DOUBLE) {
                adapter.markCast(expr0, type0, Type.DOUBLE_TYPE, false);
            } else if (sort0 == Type.FLOAT) {
                adapter.markCast(expr1, type1, Type.FLOAT_TYPE, false);
            } else if (sort1 == Type.FLOAT) {
                adapter.markCast(expr0, type0, Type.FLOAT_TYPE, false);
            } else if (sort0 == Type.LONG) {
                adapter.markCast(expr1, type1, Type.LONG_TYPE, false);
            } else if (sort1 == Type.LONG) {
                adapter.markCast(expr0, type0, Type.LONG_TYPE, false);
            } else {
                adapter.markCast(expr0, type0, Type.INT_TYPE, false);
                adapter.markCast(expr1, type1, Type.INT_TYPE, false);
            }
        } else if (!((sort0 == Type.OBJECT || sort0 == Type.ARRAY) &&
                (sort1 == Type.OBJECT || sort1 == Type.ARRAY) ||
                sort0 == Type.BOOLEAN && sort1 == Type.BOOLEAN)) {
            throw new IllegalArgumentException();
        }

        expected.put(ctx, Type.BOOLEAN_TYPE);

        return null;
    }

    @Override
    public Object visitBool(PainlessParser.BoolContext ctx) {
        Type to = expected.get(ctx);

        if (to == null) {
            throw new IllegalArgumentException();
        }

        ExpressionContext expr0 = ctx.expression(0);
        ExpressionContext expr1 = ctx.expression(1);

        expected.put(expr0, Type.BOOLEAN_TYPE);
        expected.put(expr1, Type.BOOLEAN_TYPE);

        visit(expr0);
        visit(expr1);

        Type type0 = expected.get(expr0);
        Type type1 = expected.get(expr1);

        adapter.markCast(expr0, type0, Type.BOOLEAN_TYPE, false);
        adapter.markCast(expr1, type1, Type.BOOLEAN_TYPE, false);

        expected.put(ctx, Type.BOOLEAN_TYPE);

        return null;
    }

    @Override
    public Object visitConditional(PainlessParser.ConditionalContext ctx) {
        Type to = expected.get(ctx);

        ExpressionContext expr0 = ctx.expression(0);
        ExpressionContext expr1 = ctx.expression(1);
        ExpressionContext expr2 = ctx.expression(1);

        expected.put(expr0, Type.BOOLEAN_TYPE);

        if (to != null) {
            expected.put(expr1, to);
            expected.put(expr2, to);
        }

        visit(expr0);
        visit(expr1);
        visit(expr2);

        Type type0 = expected.get(expr0);
        Type type1 = expected.get(expr1);
        Type type2 = expected.get(expr2);

        adapter.markCast(expr0, type0, Type.BOOLEAN_TYPE, false);

        if (to != null) {
            adapter.markCast(expr1, type1, to, false);
            adapter.markCast(expr2, type2, to, false);
        }

        return null;
    }

    @Override
    public Object visitArguments(PainlessParser.ArgumentsContext ctx) {
        return null;
    }
}
