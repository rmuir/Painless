package painless;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.Type;

import static painless.PainlessParser.*;
import static painless.PainlessTypes.*;

class PainlessValidator extends PainlessBaseVisitor<PainlessValidator.Extracted> {
    static class Argument {
        final String vname;
        final Type atype;

        Argument(final String vname, final Type atype) {
            this.vname = vname;
            this.atype = atype;
        }
    }

    private static class Variable {
        final String vname;
        final Type atype;
        final int vslot;

        Variable(final String vname, final Type atype, final int vslot) {
            this.vname = vname;
            this.atype = atype;
            this.vslot = vslot;
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

        void add(final String vname, final Type atype) {
            if (get(vname) != null) {
                throw new IllegalArgumentException();
            }

            Variable previous = variables.peek();
            int slot = 0;

            if (previous != null) {
                slot += previous.atype.getSize();
            }

            Variable variable = new Variable(vname, atype, slot);
            variables.push(variable);

            int update = scopes.pop();
            ++update;
            scopes.push(update);
        }

        Variable get(String vname) {
            Iterator<Variable> itr = variables.descendingIterator();

            while (itr.hasNext()) {
                Variable variable = itr.next();

                if (variable.vname.equals(vname)) {
                    return variable;
                }
            }

            return null;
        }
    }

    static class Extracted {
        final ParseTree source;

        final List<ParseTree> nodes;
        final List<Type> atypes;

        boolean statement = false;
        boolean jump = false;
        boolean rtn = false;

        Extracted(final ParseTree source) {
            this.source = source;

            nodes = new ArrayList<>();
            atypes = new ArrayList<>();
        }
    }

    static void validate(PainlessTypes ptypes, ParseTree root, Deque<Argument> arguments) {
        new PainlessValidator(ptypes, root, arguments);
    }

    private final PainlessTypes ptypes;

    private int loop;
    private final Variables variables;
    private final Map<ParseTree, Type> atypes;

    private final Map<ParseTree, PCast> pcasts;

    private PainlessValidator(PainlessTypes ptypes, ParseTree root, Deque<Argument> arguments) {
        this.ptypes = ptypes;

        loop = 0;
        variables = new Variables();
        atypes = new HashMap<>();

        pcasts = new HashMap<>();

        Iterator<Argument> itr = arguments.iterator();

        while (itr.hasNext()) {
            final Argument argument = itr.next();
            variables.add(argument.vname, argument.atype);
        }

        visit(root);
    }

    private void markCast(final Extracted extracted, final Type to, final boolean explicit) {
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
        markCast(extexpr, Type.BOOLEAN_TYPE, false);

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
        markCast(extexpr, Type.BOOLEAN_TYPE, false);

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
        markCast(extexpr, Type.BOOLEAN_TYPE, false);

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
        markCast(extexpr0, Type.BOOLEAN_TYPE, false);

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
        markCast(extexpr, Type.BOOLEAN_TYPE, false);;

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
        Type atype = ptypes.getATypeFromPClass(decltype.getText());

        if (atype == null) {
            throw new IllegalArgumentException();
        }

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof TerminalNode) {
                TerminalNode tctx = (TerminalNode)cctx;

                if (tctx.getSymbol().getType() == PainlessLexer.ID) {
                    String vname = tctx.getText();
                    variables.add(vname, atype);
                }
            } else if (cctx instanceof ExpressionContext) {
                Extracted extcctx = visit(cctx);
                markCast(extcctx, atype, false);
                //TODO: mark write variable?
            }
        }

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitExt(PainlessParser.ExtContext ctx) {
        return visit(ctx.extstart());
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
    public Extracted visitExtprec(PainlessParser.ExtprecContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtcast(PainlessParser.ExtcastContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtarray(PainlessParser.ExtarrayContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtdot(PainlessParser.ExtdotContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtfunc(PainlessParser.ExtfuncContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtstatic(PainlessParser.ExtstaticContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtcall(PainlessParser.ExtcallContext ctx) {
        return null;
    }

    @Override
    public Extracted visitExtmember(final PainlessParser.ExtmemberContext ctx) {
        final Type parentatype = atypes.get(ctx.getParent());
        final String vname = ctx.ID().getText();
        Type atype;

        if (parentatype == null) {
            final Variable variable = variables.get(vname);

            if (variable == null) {
                throw new IllegalArgumentException();
            }

            atype = variable.atype;
        } else {
            final PClass pclass = ptypes.getPClass(parentatype);
            final PMember pmember = pclass.getPMember(vname);

            if (pmember == null) {
                throw new IllegalArgumentException();
            }

            atype = pmember.atype;
        }

        if (ctx.extdot() != null) {
            atypes.put(ctx, atype);
            visit(ctx.extdot());
        } else if (ctx.extarray() != null) {
            atypes.put(ctx, atype);
            visit(ctx.extarray());
        }

        final Extracted extracted = new Extracted(ctx);
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitArguments(PainlessParser.ArgumentsContext ctx) {
        return null;
    }
}
