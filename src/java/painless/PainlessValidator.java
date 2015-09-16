package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import static painless.PainlessParser.*;
import static painless.PainlessTypes.*;

class PainlessValidator /*extends PainlessBaseVisitor<PainlessValidator.Extracted>*/ {
    /*static class Argument {
        final String name;
        final VType vtype;

        Argument(final String name, final VType vtype) {
            this.name = name;
            this.vtype = vtype;
        }
    }

    private static class Variable {
        final String name;
        final VType vtype;
        final int slot;

        Variable(final String name, final VType type, final int slot) {
            this.name = name;
            this.vtype = type;
            this.slot = slot;
        }
    }

    private static class Variables {
        private final Deque<Variable> variables;
        private final Deque<Integer> scopes;

        Variables() {
            variables = new ArrayDeque<>();
            scopes = new ArrayDeque<>();
            scopes.push(0);
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

        void add(final String name, final VType vtype) {
            if (get(name) != null) {
                throw new IllegalArgumentException();
            }

            Variable previous = variables.peek();
            int slot = 0;

            if (previous != null) {
                slot += previous.vtype.size;
            }

            Variable variable = new Variable(name, vtype, slot);
            variables.push(variable);

            int update = scopes.pop();
            ++update;
            scopes.push(update);
        }

        Variable get(String name) {
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

    static class Extracted {
        final ParseTree source;

        final Deque<ParseTree> nodes;
        final Deque<VType> vtypes;

        boolean statement = false;
        boolean jump = false;
        boolean rtn = false;

        Extracted(final ParseTree source) {
            this.source = source;

            nodes = new ArrayDeque<>();
            vtypes = new ArrayDeque<>();
        }
    }

    static void validate(ParseTree root, PainlessTypes types, Deque<Argument> arguments) {
        new PainlessValidator(root, types, arguments);
    }

    private final PainlessTypes types;

    private final Variables variables;
    private int loop;

    private final Map<ParseTree, PCast> casts;

    private PainlessValidator(ParseTree root, PainlessTypes types, Deque<Argument> arguments) {
        this.types = types;

        variables = new Variables();

        Iterator<Argument> itr = arguments.iterator();

        while (itr.hasNext()) {
            final Argument argument = itr.next();
            variables.add(argument.name, argument.vtype);
        }

        loop = 0;

        casts = new HashMap<>();

        visit(root);
    }

    private void markCast(final Extracted extracted, final VType to, final boolean explicit) {
        final Iterator<ParseTree> nodes = extracted.nodes.iterator();
        final Iterator<VType> vtypes = extracted.vtypes.iterator();

        while (nodes.hasNext() && vtypes.hasNext()) {
            final ParseTree node = nodes.next();
            final VType from = vtypes.next();

            if (from.dimensions != to.dimensions) {

            }
        }

        if (nodes.hasNext() && !vtypes.hasNext() || !nodes.hasNext() && vtypes.hasNext()) {
            throw new IllegalStateException();
        }
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

    /*@Override
    public Extracted visitIf(IfContext ctx) {
        variables.incrementScope();

        Extracted extexpr = visit(ctx.expression());
        markCast(extexpr, types.getPType(boolean.class).name, false);

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
        markCast(extexpr, types.getPType(boolean.class).name, false);

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
        markCast(extexpr, types.getPType(boolean.class).name, false);

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
        markCast(extexpr0, types.getPType(boolean.class).name, false);

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
        markCast(extexpr, types.getPType(Object.class).name, false);;

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
    public Extracted visitDeclaration(PainlessParser.DeclarationContext ctx) {
        DecltypeContext decltype = ctx.decltype();
        Type type = visit(decltype).type0;

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof TerminalNode) {
                TerminalNode tctx = (TerminalNode)cctx;

                if (tctx.getSymbol().getType() == PainlessLexer.ID) {
                    String name = tctx.getText();
                    variables.add(name, type);
                }
            } else if (cctx instanceof ExpressionContext) {
                Extracted extcctx = visit(cctx);
                extcctx.markCast(type, false);
                //TODO: mark write variable?
            }
        }

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        return extracted;
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
        extracted.type0 = externals.getType(type, dimensions);

        return extracted;
    }

    @Override
    public Extracted visitExt(PainlessParser.ExtContext ctx) {
        return null;
    }

    @Override
    public Extracted visitComp(PainlessParser.CompContext ctx) {
        return null;
    }

    @Override
    public Extracted visitString(PainlessParser.StringContext ctx) {
        return null;
    }

    @Override
    public Extracted visitBool(PainlessParser.BoolContext ctx) {
        return null;
    }

    @Override
    public Extracted visitConditional(PainlessParser.ConditionalContext ctx) {
        return null;
    }

    @Override
    public Extracted visitAssignment(PainlessParser.AssignmentContext ctx) {
        return null;
    }

    @Override
    public Extracted visitFalse(PainlessParser.FalseContext ctx) {
        return null;
    }

    @Override
    public Extracted visitNumeric(PainlessParser.NumericContext ctx) {
        return null;
    }

    @Override
    public Extracted visitUnary(PainlessParser.UnaryContext ctx) {
        return null;
    }

    @Override
    public Extracted visitPrecedence(PainlessParser.PrecedenceContext ctx) {
        return null;
    }

    @Override
    public Extracted visitCast(PainlessParser.CastContext ctx) {
        return null;
    }

    @Override
    public Extracted visitNull(PainlessParser.NullContext ctx) {
        return null;
    }

    @Override
    public Extracted visitBinary(PainlessParser.BinaryContext ctx) {
        return null;
    }

    @Override
    public Extracted visitChar(PainlessParser.CharContext ctx) {
        return null;
    }

    @Override
    public Extracted visitTrue(PainlessParser.TrueContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtstart(ExtstartContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtprec(ExtprecContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtcast(ExtcastContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtarray(ExtarrayContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtdot(ExtdotContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtfunc(ExtfuncContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtstatic(ExtstaticContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtcall(ExtcallContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtmember(ExtmemberContext ctx) {
        return null;
    }

    @Override
    public Extracted visitArguments(PainlessParser.ArgumentsContext ctx) {
        return null;
    }*/
}
