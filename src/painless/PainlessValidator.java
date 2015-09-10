package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Type;

import static painless.PainlessParser.*;

class PainlessValidator extends PainlessBaseVisitor<PainlessValidator.Extracted> {
    static final Type OBJECT_TYPE = Type.getType("Ljava/lang/Object;");
    static final Type STRING_TYPE = Type.getType("Ljava/lang/String;");

    static class Cast {
        final Type from;
        final Type to;

        Cast(final Type from, final Type to) {
            this.from = from;
            this.to = to;
        }
    }

    static class Variable {
        final String name;
        final Type type;
        final int slot;

        Variable(final String name, final Type type, final int slot) {
            this.name = name;
            this.type = type;
            this.slot = slot;
        }
    }

    static class Argument {
        final String name;
        final Type type;

        Argument(final String name, final Type type) {
            this.name = name;
            this.type = type;
        }
    }

    static void validate(ParseTree root, Deque<Argument> arguments) {
        new PainlessValidator(root, arguments);
    }

    class Extracted {
        final ParseTree source;

        ParseTree node0 = null;
        ParseTree node1 = null;

        Type type0 = null;
        Type type1 = null;

        boolean statement = false;
        boolean jump = false;
        boolean rtn = false;

        Extracted(final ParseTree source) {
            this.source = source;
        }

        void markCast(Type to, boolean explicit) {
        }
    }

    private class Variables {
        private final Deque<Variable> variables;
        private final Deque<Integer> scopes;

        Variables() {
            variables = new ArrayDeque<>();
            scopes = new ArrayDeque<>();
        }

        void incrementScope() {
            scopes.push(0);
        }

        void decrementScope() {
            int remove = scopes.pop();

            while (remove > 0) {
                variables.pop();
                --remove;
            }
        }

        void addVariable(final String name, final Type type) {
            if (getVariable(name) != null) {
                throw new IllegalArgumentException();
            }

            Variable previous = variables.peek();
            int slot = 0;

            if (previous != null) {
                slot = previous.type.equals(Type.DOUBLE_TYPE) || previous.type.equals(Type.LONG_TYPE) ? slot + 2 : slot + 1;
            }

            Variable variable = new Variable(name, type, slot);
            variables.push(variable);

            int update = scopes.pop();
            ++update;
            scopes.push(update);
        }

        Variable getVariable(String name) {
            Iterator<Variable> itr = variables.descendingIterator();

            while (itr.hasNext()) {
                Variable variable = itr.next();

                if (variable.name.equals(name)) {
                    return variable;
                }
            }

            return null;
        }
    }

    private Variables variables;
    private int loop;

    private Map<ParseTree, Cast> casts;
    private Map<ParseTree, Variable> reads;
    private Map<ParseTree, Variable> writes;

    private PainlessValidator(ParseTree root, Deque<Argument> arguments) {
        variables = new Variables();

        Iterator<Argument> itr = arguments.iterator();

        while (itr.hasNext()) {
            Argument argument = itr.next();
            variables.addVariable(argument.name, argument.type);
        }

        loop = 0;

        casts = new HashMap<>();
        reads = new HashMap<>();
        writes = new HashMap<>();

        visit(root);
    }

    @Override
    public Extracted visitSource(SourceContext ctx) {
        variables.incrementScope();

        Extracted extracted = new Extracted(ctx);

        for (StatementContext sctx : ctx.statement()) {
            if (extracted.rtn) {
                throw new IllegalStateException();
            }

            Extracted extstate = visit(sctx);

            if (!extstate.statement) {
                throw new IllegalStateException();
            }

            extracted.rtn = extstate.rtn;
        }

        extracted.statement = true;

        variables.decrementScope();

        return extracted;
    }

    @Override
    public Extracted visitIf(IfContext ctx) {
        variables.incrementScope();

        Extracted extexpr = visit(ctx.expression());
        extexpr.markCast(Type.BOOLEAN_TYPE, false);

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;
        extracted.rtn = visit(ctx.block(0)).rtn;

        if (ctx.ELSE() != null) {
            extracted.rtn &= visit(ctx.block(1)).rtn;
        } else {
            extracted.rtn = false;
        }

        variables.decrementScope();

        return extracted;
    }

    @Override
    public Extracted visitWhile(WhileContext ctx) {
        variables.incrementScope();
        ++loop;

        ExpressionContext ectx = ctx.expression();
        Extracted extexpr = visit(ectx);
        extexpr.markCast(Type.BOOLEAN_TYPE, false);

        visit(ctx.block());

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        --loop;
        variables.decrementScope();

        return extracted;
    }

    @Override
    public Extracted visitDo(DoContext ctx) {
        variables.incrementScope();
        ++loop;

        visit(ctx.block());

        ExpressionContext ectx = ctx.expression();
        Extracted extexpr = visit(ectx);
        extexpr.markCast(Type.BOOLEAN_TYPE, false);

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        --loop;
        variables.decrementScope();

        return extracted;
    }

    @Override
    public Extracted visitFor(ForContext ctx) {
        variables.incrementScope();
        ++loop;

        Extracted extdecl = visit(ctx.declaration());

        if (!extdecl.statement) {
            throw new IllegalStateException();
        }

        Extracted extexpr0 = visit(ctx.expression(0));
        extexpr0.markCast(Type.BOOLEAN_TYPE, false);

        Extracted extexpr1 = visit(ctx.expression(1));

        if (!extexpr1.statement) {
            throw new IllegalStateException();
        }

        visit(ctx.block());

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        --loop;
        variables.decrementScope();

        return extracted;
    }

    @Override
    public Extracted visitDecl(DeclContext ctx) {
        return visit(ctx.declaration());
    }

    @Override
    public Extracted visitContinue(ContinueContext ctx) {
        if (loop == 0) {
            throw new IllegalStateException();
        }

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;
        extracted.jump = true;

        return extracted;
    }

    @Override
    public Extracted visitBreak(BreakContext ctx) {
        if (loop == 0) {
            throw new IllegalStateException();
        }

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;
        extracted.jump = true;

        return extracted;
    }

    @Override
    public Extracted visitReturn(ReturnContext ctx) {
        Extracted extexpr = visit(ctx.expression());
        extexpr.markCast(OBJECT_TYPE, false);

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;
        extracted.rtn = true;

        return extracted;
    }

    @Override
    public Extracted visitExpr(ExprContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Extracted visitMultiple(MultipleContext ctx) {
        Extracted extracted = new Extracted(ctx);

        for (StatementContext sctx : ctx.statement()) {
            if (extracted.rtn || extracted.jump) {
                throw new IllegalStateException();
            }

            Extracted extstate = visit(sctx);

            if (!extstate.statement) {
                throw new IllegalStateException();
            }

            extracted.rtn = extstate.rtn;
            extracted.jump = extstate.jump;
        }


        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitSingle(SingleContext ctx) {
        Extracted extstate = visit(ctx.statement());

        if (!extstate.statement) {
            throw new IllegalStateException();
        }

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitEmpty(EmptyContext ctx) {
        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitDeclaration(DeclarationContext ctx) {
        DecltypeContext decltype = ctx.decltype();
        Type to = visit(decltype).type0;

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
    public Extracted visitDecltype(DecltypeContext ctx) {
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

        Extracted extracted = new Extracted(ctx);
        extracted.node0 = ctx;
        extracted.type0 = Type.getType(descriptor);

        return extracted;
    }

    @Override
    public Object visitExt(PainlessParser.ExtContext ctx) {
        return null;
    }

    @Override
    public Object visitComp(PainlessParser.CompContext ctx) {
        return null;
    }

    @Override
    public Object visitString(PainlessParser.StringContext ctx) {
        return null;
    }

    @Override
    public Object visitBool(PainlessParser.BoolContext ctx) {
        return null;
    }

    @Override
    public Object visitConditional(PainlessParser.ConditionalContext ctx) {
        return null;
    }

    @Override
    public Object visitAssignment(PainlessParser.AssignmentContext ctx) {
        return null;
    }

    @Override
    public Object visitFalse(PainlessParser.FalseContext ctx) {
        return null;
    }

    @Override
    public Object visitNumeric(PainlessParser.NumericContext ctx) {
        return null;
    }

    @Override
    public Object visitUnary(PainlessParser.UnaryContext ctx) {
        return null;
    }

    @Override
    public Object visitPrecedence(PainlessParser.PrecedenceContext ctx) {
        return null;
    }

    @Override
    public Object visitCast(PainlessParser.CastContext ctx) {
        return null;
    }

    @Override
    public Object visitNull(PainlessParser.NullContext ctx) {
        return null;
    }

    @Override
    public Object visitBinary(PainlessParser.BinaryContext ctx) {
        return null;
    }

    @Override
    public Object visitChar(PainlessParser.CharContext ctx) {
        return null;
    }

    @Override
    public Object visitTrue(PainlessParser.TrueContext ctx) {
        return null;
    }

    @Override
    public Object visitExtdot(PainlessParser.ExtdotContext ctx) {
        return null;
    }

    @Override
    public Object visitExtprec(PainlessParser.ExtprecContext ctx) {
        return null;
    }

    @Override
    public Object visitExtargs(PainlessParser.ExtargsContext ctx) {
        return null;
    }

    @Override
    public Object visitExtarray(PainlessParser.ExtarrayContext ctx) {
        return null;
    }

    @Override
    public Object visitExtcast(PainlessParser.ExtcastContext ctx) {
        return null;
    }

    @Override
    public Object visitExtvar(PainlessParser.ExtvarContext ctx) {
        return null;
    }

    @Override
    public Object visitArguments(PainlessParser.ArgumentsContext ctx) {
        return null;
    }
}
