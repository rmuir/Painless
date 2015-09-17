package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Type.*;

import static painless.PainlessParser.*;
import static painless.PainlessTypes.*;

class PainlessValidator extends PainlessBaseVisitor<Void> {
    static class Argument {
        final String name;
        final Type atype;

        Argument(final String name, final Type atype) {
            this.name = name;
            this.atype = atype;
        }
    }

    static class Variable {
        final String name;
        final Type atype;
        final int slot;

        Variable(final String name, final Type atype, final int slot) {
            this.name = name;
            this.atype = atype;
            this.slot = slot;
        }
    }

    static class Metadata {
        final ParseTree node;

        boolean rtn;
        boolean jump;
        boolean statement;

        Type aexpected;

        Metadata(final ParseTree node) {
            this.node = node;

            rtn = false;
            jump = false;
            statement = false;

            aexpected = null;
        }
    }

    static void validate(final PainlessTypes ptypes, final ParseTree root, final Deque<Argument> arguments) {
        new PainlessValidator(ptypes, root, arguments);
    }

    private final PainlessTypes ptypes;

    private int loop;
    private final Deque<Integer> scopes;
    private final Deque<Variable> variables;

    private final Map<ParseTree, Metadata> metadata;

    private PainlessValidator(final PainlessTypes ptypes, final ParseTree root, final Deque<Argument> arguments) {
        this.ptypes = ptypes;

        loop = 0;
        variables = new ArrayDeque<>();
        scopes = new ArrayDeque<>();

        metadata = new HashMap<>();

        incrementScope();

        for (final Argument argument : arguments) {
            addVariable(argument.name, argument.atype);
        }

        createMetadata(root);
        visit(root);

        decrementScope();
    }

    private void incrementScope() {
        scopes.push(0);
    }

    private void decrementScope() {
        int remove = scopes.pop();

        while (remove > 0) {
            variables.pop();
            --remove;
        }
    }

    private Variable getVariable(final String name) {
        final Iterator<Variable> itr = variables.descendingIterator();

        while (itr.hasNext()) {
            final Variable variable = itr.next();

            if (variable.name.equals(name)) {
                return variable;
            }
        }

        return null;
    }

    private void addVariable(final String name, final Type atype) {
        if (getVariable(name) != null) {
            throw new IllegalArgumentException();
        }

        final Variable previous = variables.peek();
        int slot = 0;

        if (previous != null) {
            slot += previous.atype.getSize();
        }

        final Variable pvariable = new Variable(name, atype, slot);
        variables.push(pvariable);

        final int update = scopes.pop() + 1;
        scopes.push(update);
    }

    private Metadata createMetadata(final ParseTree node) {
        final Metadata nodemd = new Metadata(node);
        metadata.put(node, nodemd);

        return nodemd;
    }

    private Metadata getMetadata(ParseTree node) {
        final Metadata nodemd = metadata.get(node);

        if (nodemd == null) {
            throw new IllegalStateException();
        }

        return nodemd;
    }

    private boolean isNumeric(final Type atype) {
        final int asort = atype.getSort();

        //TODO: check implicit transforms

        return asort == BYTE || asort == SHORT || asort == Type.CHAR ||
                asort == INT || asort == LONG || asort == FLOAT || asort == DOUBLE;
    }

    private void markCast(final Metadata metadata, final Type afrom, final Type ato, final boolean explicit) {
        //TODO: check legality of cast
    }

    private void markPromotion(final Metadata metadata, final Type amax, final Type aone, final Type atwo) {
        //TODO: do necessary promotion
    }

    @Override
    public Void visitSource(final SourceContext ctx) {
        final Metadata sourcemd = metadata.get(ctx);

        incrementScope();

        for (final StatementContext sctx : ctx.statement()) {
            if (sourcemd.rtn) {
                throw new IllegalStateException();
            }

            final Metadata statementmd = createMetadata(sctx);
            visit(sctx);

            if (!statementmd.statement) {
                throw new IllegalStateException();
            }

            sourcemd.rtn = statementmd.rtn;
        }

        sourcemd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitIf(IfContext ctx) {
        final Metadata ifmd = getMetadata(ctx);

        incrementScope();

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ectx);
        expressionmd.aexpected = BOOLEAN_TYPE;
        visit(ectx);

        final BlockContext bctx0 = ctx.block(0);
        final Metadata blockmd0 = createMetadata(bctx0);
        visit(ctx.block(0));

        if (ctx.ELSE() != null) {
            final BlockContext bctx1 = ctx.block(1);
            final Metadata blockmd1 = createMetadata(bctx1);
            visit(ctx.block(1));
            ifmd.rtn = blockmd0.rtn && blockmd1.rtn;
        }

        ifmd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitWhile(final WhileContext ctx) {
        final Metadata whilemd = getMetadata(ctx);

        incrementScope();
        ++loop;

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ctx);
        expressionmd.aexpected = BOOLEAN_TYPE;
        visit(ectx);

        final BlockContext bctx = ctx.block();
        createMetadata(bctx);
        visit(bctx);

        whilemd.statement = true;

        --loop;
        decrementScope();

        return null;
    }

    @Override
    public Void visitDo(final DoContext ctx) {
        final Metadata domd = getMetadata(ctx);

        incrementScope();
        ++loop;

        final BlockContext bctx = ctx.block();
        createMetadata(bctx);
        visit(bctx);

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ctx);
        expressionmd.aexpected = BOOLEAN_TYPE;
        visit(ectx);

        domd.statement = true;

        --loop;
        decrementScope();

        return null;
    }

    @Override
    public Void visitFor(final ForContext ctx) {
        final Metadata formd = getMetadata(ctx);

        incrementScope();
        ++loop;

        final DeclarationContext dctx = ctx.declaration();
        final Metadata declarationmd = createMetadata(dctx);
        visit(dctx);

        if (!declarationmd.statement) {
            throw new IllegalStateException();
        }

        final ExpressionContext ectx0 = ctx.expression(0);
        final Metadata expressionmd0 = createMetadata(ectx0);
        expressionmd0.aexpected = BOOLEAN_TYPE;
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(0);
        final Metadata expressionmd1 = createMetadata(ectx1);
        visit(ectx1);

        if (!expressionmd1.statement) {
            throw new IllegalStateException();
        }

        final BlockContext bctx = ctx.block();
        createMetadata(bctx);
        visit(bctx);

        formd.statement = true;

        --loop;
        decrementScope();

        return null;
    }

    @Override
    public Void visitDecl(final DeclContext ctx) {
        final Metadata declmd = getMetadata(ctx);

        final DeclarationContext dctx = ctx.declaration();
        final Metadata declarationmd = createMetadata(dctx);
        visit(ctx.declaration());

        declmd.statement = declarationmd.statement;

        return null;
    }

    @Override
    public Void visitContinue(final ContinueContext ctx) {
        final Metadata continuemd = getMetadata(ctx);

        if (loop == 0) {
            throw new IllegalStateException();
        }

        continuemd.jump = true;
        continuemd.statement = true;

        return null;
    }

    @Override
    public Void visitBreak(final BreakContext ctx) {
        final Metadata breakmd = getMetadata(ctx);

        if (loop == 0) {
            throw new IllegalStateException();
        }

        breakmd.jump = true;
        breakmd.statement = true;

        return null;
    }

    @Override
    public Void visitReturn(final ReturnContext ctx) {
        final Metadata returnmd = getMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ctx);
        expressionmd.aexpected = Type.getType("Ljava/lang/Object;");
        visit(ectx);

        returnmd.rtn = true;
        returnmd.statement = true;

        return null;
    }

    @Override
    public Void visitExpr(final ExprContext ctx) {
        final Metadata exprmd = getMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ctx);
        visit(ectx);

        exprmd.statement = expressionmd.statement;

        return null;
    }

    @Override
    public Extracted visitMultiple(MultipleContext ctx) {
        Extracted extracted = new Extracted();

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

        Extracted extracted = new Extracted();
        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitEmpty(EmptyContext ctx) {
        Extracted extracted = new Extracted();
        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitDeclaration(PainlessParser.DeclarationContext ctx) {
        final DecltypeContext decltype = ctx.decltype();
        final Type atype = ptypes.getATypeFromPClass(decltype.getText());

        if (atype == null) {
            throw new IllegalArgumentException();
        }

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            final ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof TerminalNode) {
                final TerminalNode tctx = (TerminalNode)cctx;

                if (tctx.getSymbol().getType() == PainlessLexer.ID) {
                    final String vname = tctx.getText();
                    variables.add(vname, atype);
                }
            } else if (cctx instanceof ExpressionContext) {
                final Extracted extcctx = visit(cctx);
                markCast(extcctx, atype, false);
                //TODO: mark write variable?
            }
        }

        final Extracted extracted = new Extracted();
        extracted.statement = true;

        return extracted;
    }

    @Override
    public Extracted visitDecltype(DecltypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Extracted visitExt(ExtContext ctx) {
        return visit(ctx.extstart());
    }

    @Override
    public Extracted visitPrecedence(PrecedenceContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Extracted visitNumeric(NumericContext ctx) {
        Type atype;

        if (ctx.DECIMAL() != null) {
            final String svalue = ctx.OCTAL().getText();
            final double dvalue = Double.parseDouble(svalue);

            if (dvalue > Float.MIN_VALUE && dvalue < Float.MAX_VALUE) {
                atype = Type.FLOAT_TYPE;
            } else {
                atype = Type.DOUBLE_TYPE;
            }
        } else {
            String svalue;
            long lvalue;

            if (ctx.OCTAL() != null) {
                svalue = ctx.OCTAL().getText();
                lvalue = Long.parseLong(svalue, 8);
            } else if (ctx.INTEGER() != null) {
                svalue = ctx.INTEGER().getText();
                lvalue = Long.parseLong(svalue, 16);
            } else if (ctx.HEX() != null) {
                svalue = ctx.HEX().getText();
                lvalue = Long.parseLong(svalue);
            } else {
                throw new IllegalArgumentException();
            }

            if (lvalue > Byte.MIN_VALUE && lvalue < Byte.MAX_VALUE) {
                atype = Type.BYTE_TYPE;
            } else if (lvalue > Short.MIN_VALUE && lvalue < Short.MAX_VALUE) {
                atype = Type.SHORT_TYPE;
            } else if (lvalue > Integer.MIN_VALUE && lvalue < Integer.MAX_VALUE) {
                atype = Type.INT_TYPE;
            } else {
                atype = Type.LONG_TYPE;
            }

            //TODO: store value for single parse?
        }

        if (ptypes.getPClass(atype) == null) {
            throw new IllegalArgumentException();
        }

        final Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitString(StringContext ctx) {
        final Type atype = Type.getType("Ljava/lang/String;");

        if (ptypes.getPClass(atype) == null) {
            throw new IllegalArgumentException();
        }

        final Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitChar(CharContext ctx) {
        final Type atype = Type.CHAR_TYPE;

        if (ptypes.getPClass(atype) == null) {
            throw new IllegalArgumentException();
        }

        final Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }
    @Override
    public Extracted visitTrue(TrueContext ctx) {
        final Type atype = Type.BOOLEAN_TYPE;

        if (ptypes.getPClass(atype) == null) {
            throw new IllegalArgumentException();
        }

        final Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitFalse(FalseContext ctx) {
        final Type atype = Type.BOOLEAN_TYPE;

        if (ptypes.getPClass(atype) == null) {
            throw new IllegalArgumentException();
        }

        final Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitNull(NullContext ctx) {
        final Type atype = Type.getType("Ljava/lang/Object;");

        if (ptypes.getPClass(atype) == null) {
            throw new IllegalArgumentException();
        }

        final Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitUnary(UnaryContext ctx) {
        Type atype;

        if (ctx.BOOLNOT() != null) {
            final Extracted extexpr = visit(ctx.expression());
            markCast(extexpr, Type.BOOLEAN_TYPE, false);
            atype = Type.BOOLEAN_TYPE;
        } else {
            final Extracted extexpr = visit(ctx.expression());
            atype = markPromotion(new Extracted[] {extexpr});
        }

        Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitCast(CastContext ctx) {
        String ptype = ctx.decltype().getText();
        Type atype = ptypes.getATypeFromPClass(ptype);

        if (atype == null) {
            throw new IllegalArgumentException();
        }

        Extracted extexpr = visit(ctx.expression());
        markCast(extexpr, atype, true);

        Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitBinary(BinaryContext ctx) {
        final Extracted[] extexpr = new Extracted[2];

        extexpr[0] = visit(ctx.expression(0));
        extexpr[1] = visit(ctx.expression(0));
        Type atype = markPromotion(extexpr);

        Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitComp(PainlessParser.CompContext ctx) {
        final Extracted[] extexpr = new Extracted[2];

        extexpr[0] = visit(ctx.expression(0));
        extexpr[1] = visit(ctx.expression(0));

        if (isNumeric(extexpr[0]) && isNumeric(extexpr[1])) {
            markPromotion(extexpr);
        } else if (ctx.EQ() != null || ctx.NE() != null) {
            if (isBoolean(extexpr[0]) && isBoolean(extexpr[1])) {
                markCast(extexpr[0], Type.BOOLEAN_TYPE, false);
                markCast(extexpr[0], Type.BOOLEAN_TYPE, false);
            } else if (isReference(extexpr[0]) && isReference(extexpr[1])) {
                //TODO: mark comparison type?
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }

        Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(Type.BOOLEAN_TYPE);

        return extracted;
    }

    @Override
    public Extracted visitBool(BoolContext ctx) {
        final Extracted[] extexpr = new Extracted[2];

        extexpr[0] = visit(ctx.expression(0));
        extexpr[1] = visit(ctx.expression(0));
        markCast(extexpr[0], Type.BOOLEAN_TYPE, false);
        markCast(extexpr[1], Type.BOOLEAN_TYPE, false);

        final Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(Type.BOOLEAN_TYPE);

        return extracted;
    }

    @Override
    public Extracted visitConditional(PainlessParser.ConditionalContext ctx) {
        final Extracted extexpr = visit(ctx.expression(0));
        markCast(extexpr, Type.BOOLEAN_TYPE, false);

        final Extracted extcond0 = visit(ctx.expression(1));
        final Extracted extcond1 = visit(ctx.expression(2));

        Extracted extracted = new Extracted();
        extracted.nodes.addAll(extcond0.nodes);
        extracted.atypes.addAll(extcond0.atypes);
        extracted.nodes.addAll(extcond1.nodes);
        extracted.atypes.addAll(extcond1.atypes);
        extracted.statement = extcond0.statement && extcond1.statement;

        return extracted;
    }

    @Override
    public Extracted visitAssignment(PainlessParser.AssignmentContext ctx) {
        Extracted extext = visit(ctx.extstart());

        if (!extext.writeable) {
            throw new IllegalArgumentException();
        }

        Extracted extexpr = visit(ctx.expression());
        markCast(extexpr, extext.atypes.get(0), false);

        Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(extext.atypes.get(0));
        extracted.statement = true;

        //TODO: mark read/write?

        return extracted;
    }

    @Override
    public Extracted visitExtstart(ExtstartContext ctx) {
        Extracted extracted;

        if (ctx.extprec() != null) {
            extracted = visitExtprec(ctx.extprec());
        } else if (ctx.extcast() != null) {
            extracted = visitExtcast(ctx.extcast());
        } else if (ctx.exttype() != null) {
            extracted = visitExttype(ctx.exttype());
        } else if (ctx.extmember() != null) {
            extracted = visitExtmember(ctx.extmember(), null, false);
        } else {
            throw new IllegalStateException();
        }

        return extracted;
    }

    @Override
    public Extracted visitExtprec(ExtprecContext ctx) {
        Extracted extracted;

        if (ctx.extprec() != null) {
            extracted = visit(ctx.extprec());
        } else if (ctx.extcast() != null) {
            extracted = visit(ctx.extcast());
        } else if (ctx.exttype() != null) {
            extracted = visit(ctx.exttype());
        } else if (ctx.extmember() != null) {
            extracted = visitExtmember(ctx.extmember(), null, false);
        } else {
            throw new IllegalStateException();
        }

        if (ctx.extdot() != null) {
            extracted = visitExtdot(ctx.extdot(), extracted.atypes.get(0), false);
        } else if (ctx.extarray() != null) {
            extracted = visitExtarray(ctx.extarray(), extracted.atypes.get(0));
        }

        return extracted;
    }

    @Override
    public Extracted visitExtcast(ExtcastContext ctx) {
        String ptype = ctx.decltype().getText();
        Type atype = ptypes.getATypeFromPClass(ptype);

        if (atype == null) {
            throw new IllegalArgumentException();
        }

        Extracted cast;

        if (ctx.extprec() != null) {
            cast = visit(ctx.extprec());
        } else if (ctx.extcast() != null) {
            cast = visit(ctx.extcast());
        } else if (ctx.exttype() != null) {
            cast = visit(ctx.exttype());
        } else if (ctx.extmember() != null) {
            cast = visitExtmember(ctx.extmember(), null, false);
        } else {
            throw new IllegalStateException();
        }

        markCast(cast, atype, true);

        Extracted extracted = new Extracted();
        extracted.nodes.add(ctx);
        extracted.atypes.add(atype);

        return extracted;
    }

    @Override
    public Extracted visitExtarray(ExtarrayContext ctx) {
        throw new UnsupportedOperationException();
    }

    public Extracted visitExtarray(final ExtarrayContext ctx, final Type parentatype) {
        if (parentatype.getSort() != Type.ARRAY) {
            throw new IllegalArgumentException();
        }

        Extracted extexpr = visit(ctx.expression());
        markCast(extexpr, Type.INT_TYPE, false);

        final Type atype = Type.getType(parentatype.getDescriptor().substring(1));

        Extracted extracted;

        if (ctx.extdot() != null) {
            extracted = visitExtdot(ctx.extdot(), atype, false);
        } else if (ctx.extarray() != null) {
            extracted = visitExtarray(ctx.extarray(), atype);
        } else {
            extracted = new Extracted();
            extracted.nodes.add(ctx);
            extracted.atypes.add(atype);
            extracted.writeable = true;
        }

        return extracted;
    }

    @Override
    public Extracted visitExtdot(ExtdotContext ctx) {
        throw new UnsupportedOperationException();
    }

    public Extracted visitExtdot(ExtdotContext ctx, Type parentatype, final boolean statik) {
        if (ctx.extcall() != null) {
            return visitExtcall(ctx.extcall(), parentatype, statik);
        } else if (ctx.extmember() != null) {
            return visitExtmember(ctx.extmember(), parentatype, statik);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Extracted visitExttype(ExttypeContext ctx) {
        final String ptype = ctx.TYPE().getText();
        final Type atype = ptypes.getATypeFromPClass(ptype);

        if (atype == null) {
            throw new IllegalArgumentException();
        }

        if (atype.getSort() == Type.ARRAY) {
            throw new IllegalArgumentException();
        }

        Extracted extracted = visitExtdot(ctx.extdot(), atype, true);
        extracted.writeable = false;

        return extracted;
    }

    @Override
    public Extracted visitExtcall(ExtcallContext ctx) {
        throw new UnsupportedOperationException();
    }

    private Extracted visitExtcall(ExtcallContext ctx, Type parentatype, final boolean statik) {
        final String pname = ctx.ID().getText();
        final PClass pclass = ptypes.getPClass(parentatype);

        if (pclass == null) {
            throw new IllegalArgumentException();
        }

        Type atype;

        if (statik && "makearray".equals(pname)) {
            int arguments = ctx.arguments().expression().size();
            visitArguments(ctx.arguments(), new Type[]{Type.INT_TYPE}, true);
            String arraytype = "";

            for (int bracket = 0; bracket < arguments; ++bracket) {
                arraytype += "[";
            }

            arraytype += parentatype.getDescriptor();
            atype = Type.getType(arraytype);
        }else {
            PMethod pmethod;

            if (statik) {
                pmethod = pclass.getPConstructor(pname);

                if (pmethod == null) {
                    pmethod = pclass.getPFunction(pname);
                }

                if (pmethod == null) {
                    throw new IllegalArgumentException();
                }
            } else {
                pmethod = pclass.getPMethod(pname);

                if (pmethod == null) {
                    throw new IllegalArgumentException();
                }
            }

            visitArguments(ctx.arguments(), pmethod.amethod.getArgumentTypes(), pmethod.variadic);
            atype = pmethod.amethod.getReturnType();
        }

        Extracted extracted;

        if (ctx.extdot() != null) {
            extracted = visitExtdot(ctx.extdot(), atype, false);
        } else if (ctx.extarray() != null) {
            extracted = visitExtarray(ctx.extarray(), atype);
        } else {
            extracted = new Extracted();
            extracted.nodes.add(ctx);
            extracted.atypes.add(atype);
            extracted.statement = true;
        }

        return extracted;
    }

    @Override
    public Extracted visitExtmember(final ExtmemberContext ctx) {
        throw new UnsupportedOperationException();
    }

    private Extracted visitExtmember(final ExtmemberContext ctx, final Type parentatype, final boolean statik) {
        final String vname = ctx.ID().getText();
        boolean writeable = !statik;
        Type atype;

        if (parentatype == null) {
            final Variable variable = variables.get(vname);

            if (variable == null) {
                throw new IllegalArgumentException();
            }

            atype = variable.atype;
        } else {
            if (parentatype.getSort() == Type.ARRAY) {
                if ("length".equals(vname)) {
                    writeable = false;
                    atype = Type.INT_TYPE;
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                final PClass pclass = ptypes.getPClass(parentatype);

                if (pclass == null) {
                    throw new IllegalArgumentException();
                }

                final PMember pmember = statik ? pclass.getPStatic(vname) : pclass.getPMember(vname);

                if (pmember == null) {
                    throw new IllegalArgumentException();
                }

                atype = pmember.atype;
            }
        }

        Extracted extracted;

        if (ctx.extdot() != null) {
            extracted = visitExtdot(ctx.extdot(), atype, false);
        } else if (ctx.extarray() != null) {
            extracted = visitExtarray(ctx.extarray(), atype);
        } else {
            extracted = new Extracted();
            extracted.nodes.add(ctx);
            extracted.atypes.add(atype);
            extracted.writeable = writeable;
        }

        return extracted;
    }

    @Override
    public Extracted visitArguments(final ArgumentsContext ctx) {
        throw new UnsupportedOperationException();
    }

    public void visitArguments(final ArgumentsContext ctx, final Type[] argatypes, final boolean variadic) {
        final List<ExpressionContext> expressions = ctx.expression();
        final int length = expressions.size();

        if (argatypes.length != length || variadic && argatypes.length > length) {
            throw new IllegalArgumentException();
        }

        for (int expression = 0; expression < length; ++expression) {
            Extracted extexpr = visit(expressions.get(expression));

            if (expression < argatypes.length) {
                markCast(extexpr, argatypes[expression], false);
            } else if (variadic) {
                markCast(extexpr, argatypes[argatypes.length], false);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
