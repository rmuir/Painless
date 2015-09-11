package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import static painless.PainlessParser.*;

class PainlessValidator extends PainlessBaseVisitor<PainlessValidator.Extracted> {
    static class Argument {
        final String name;
        final Type type;

        Argument(final String name, final Type type) {
            this.name = name;
            this.type = type;
        }
    }

    static class Cast {
        final Type from;
        final Type to;

        Cast(final Type from, final Type to) {
            this.from = from;
            this.to = to;
        }
    }

    private static class External {
        final Map<String, Type> members;
        final Map<String, Method> methods;

        External() {
            members = new HashMap<>();
            methods = new HashMap<>();
        }
    }

    private static class Externals {
        final Map<String, Type> types;
        final Map<Type, External> externals;
        final External array;

        Externals() {
            types = new HashMap<>();
            externals = new HashMap<>();
            array = new External();
        }

        Type getType(final String type, int dimensions) {
            Type rtn = types.get(type);

            if (rtn == null) {
                throw new IllegalArgumentException();
            }

            if (dimensions > 0) {
                String descriptor = "";

                while (dimensions > 0) {
                    descriptor += "[";
                }

                descriptor += rtn.getDescriptor();
                rtn = Type.getType(descriptor);
            }

            return rtn;
        }

        Type getMember(final Type type, final String name) {
            External external;

            if (type.getSort() == Type.ARRAY) {
                if (externals.get(type.getElementType()) == null) {
                    throw new IllegalArgumentException();
                }

                external = array;
            } else {
                external = externals.get(type);
            }

            if (external == null) {
                throw new IllegalArgumentException();
            }

            Type rtn = external.members.get(name);

            if (rtn == null) {
                throw new IllegalArgumentException();
            }

            return rtn;
        }

        Method getMethod(final Type type, final String name) {
            External external;

            if (type.getSort() == Type.ARRAY) {
                if (externals.get(type.getElementType()) == null) {
                    throw new IllegalArgumentException();
                }

                external = array;
            } else {
                external = externals.get(type);
            }

            if (external == null) {
                throw new IllegalArgumentException();
            }

            Method rtn = external.methods.get(name);

            if (rtn == null) {
                throw new IllegalArgumentException();
            }

            return rtn;
        }
    }

    private static class Variable {
        final String name;
        final Type type;
        final int slot;

        Variable(final String name, final Type type, final int slot) {
            this.name = name;
            this.type = type;
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

        void add(final String name, final Type type) {
            if (get(name) != null) {
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

    private final Variables variables;
    private final Externals externals;
    private final Deque<Type> types;
    private int loop;

    private final Map<ParseTree, Cast> casts;

    private PainlessValidator(ParseTree root, Deque<Argument> arguments) {
        variables = new Variables();

        Iterator<Argument> itr = arguments.iterator();

        while (itr.hasNext()) {
            Argument argument = itr.next();
            variables.add(argument.name, argument.type);
        }

        externals = new Externals();
        types = new ArrayDeque<>();
        loop = 0;

        casts = new HashMap<>();

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
        extexpr.markCast(externals.getType("object", 0), false);

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
        /*String descriptor;

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
        }*/

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
        return visit(ctx.external());
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
    public Extracted visitExternal(PainlessParser.ExternalContext ctx) {
        PainlessExternals.parse(this, ctx, null, null);

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitArguments(PainlessParser.ArgumentsContext ctx) {
        return null;
    }
}
