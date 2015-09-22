package painless;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import static painless.PainlessExternal.*;
import static painless.PainlessParser.*;
import static painless.PainlessTypes.*;

class PainlessAnalyzer extends PainlessBaseVisitor<Void> {
    static class PArgument {
        private final String pname;
        private final PType ptype;

        PArgument(final String pname, final PType ptype) {
            this.pname = pname;
            this.ptype = ptype;
        }

        String getPName() {
            return pname;
        }

        PType getPType() {
            return ptype;
        }
    }

    static class PVariable {
        private final String pname;
        private final PType ptype;
        private final int aslot;

        PVariable(final String pname, final PType ptype, final int aslot) {
            this.pname = pname;
            this.ptype = ptype;
            this.aslot = aslot;
        }

        String getPName() {
            return pname;
        }

        PType getPType() {
            return ptype;
        }

        int getASlot() {
            return aslot;
        }
    }

    static class PMetadata {
        final ParseTree node;

        boolean rtn;
        boolean jump;
        boolean statement;
        boolean conditional;

        boolean righthand;
        PType declptype;

        ParseTree castnodes[];
        PType castptypes[];

        Object constant;
        PExternal pexternal;

        PCast pcast;

        PMetadata(final ParseTree node) {
            this.node = node;

            rtn = false;
            jump = false;
            statement = false;
            conditional = false;

            righthand = false;
            declptype = null;

            castnodes = null;
            castptypes = null;

            constant = null;
            pexternal = null;

            pcast = null;
        }
    }

    static Map<ParseTree, PMetadata> analyze(
            final PTypes ptypes, final ParseTree root, final Deque<PArgument> arguments) {
        return new PainlessAnalyzer(ptypes, root, arguments).pmetadata;
    }

    private final PTypes ptypes;

    private final PType boolptype;
    private final PType byteptype;
    private final PType shortptype;
    private final PType charptype;
    private final PType intptype;
    private final PType longptype;
    private final PType floatptype;
    private final PType doubleptype;
    private final PType objectptype;
    private final PType stringptype;

    private int loop;
    private final Deque<Integer> ascopes;
    private final Deque<PVariable> pvariables;

    private final Map<ParseTree, PMetadata> pmetadata;

    private PainlessAnalyzer(final PTypes ptypes, final ParseTree root, final Deque<PArgument> parguments) {
        this.ptypes = ptypes;

        boolptype = getPTypeFromCanonicalPName(ptypes, PSort.BOOL.getPName());
        byteptype = getPTypeFromCanonicalPName(ptypes, PSort.BYTE.getPName());
        shortptype = getPTypeFromCanonicalPName(ptypes, PSort.SHORT.getPName());
        charptype = getPTypeFromCanonicalPName(ptypes, PSort.CHAR.getPName());
        intptype = getPTypeFromCanonicalPName(ptypes, PSort.INT.getPName());
        longptype = getPTypeFromCanonicalPName(ptypes, PSort.LONG.getPName());
        floatptype = getPTypeFromCanonicalPName(ptypes, PSort.FLOAT.getPName());
        doubleptype = getPTypeFromCanonicalPName(ptypes, PSort.DOUBLE.getPName());
        objectptype = getPTypeFromCanonicalPName(ptypes, PSort.OBJECT.getPName());
        stringptype = getPTypeFromCanonicalPName(ptypes, PSort.STRING.getPName());

        loop = 0;
        pvariables = new ArrayDeque<>();
        ascopes = new ArrayDeque<>();

        pmetadata = new HashMap<>();

        incrementScope();

        for (final PArgument argument : parguments) {
            addPVariable(argument.pname, argument.ptype);
        }

        createPMetadata(root);
        visit(root);

        decrementScope();
    }

    private void incrementScope() {
        ascopes.push(0);
    }

    private void decrementScope() {
        int remove = ascopes.pop();

        while (remove > 0) {
            pvariables.pop();
            --remove;
        }
    }

    private PVariable getPVariable(final String name) {
        final Iterator<PVariable> itr = pvariables.descendingIterator();

        while (itr.hasNext()) {
            final PVariable variable = itr.next();

            if (variable.pname.equals(name)) {
                return variable;
            }
        }

        return null;
    }

    private void addPVariable(final String name, final PType ptype) {
        if (getPVariable(name) != null) {
            throw new IllegalArgumentException();
        }

        final PVariable previous = pvariables.peek();
        int aslot = 0;

        if (previous != null) {
            aslot += previous.ptype.getPSort().getASize();
        }

        final PVariable pvariable = new PVariable(name, ptype, aslot);
        pvariables.push(pvariable);

        final int update = ascopes.pop() + 1;
        ascopes.push(update);
    }

    private PMetadata createPMetadata(final ParseTree node) {
        final PMetadata nodemd = new PMetadata(node);
        pmetadata.put(node, nodemd);

        return nodemd;
    }

    private PMetadata getPMetadata(ParseTree node) {
        final PMetadata nodemd = pmetadata.get(node);

        if (nodemd == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return nodemd;
    }

    private boolean isPType(final PType ptypes[], final PType ptype) {
        for (PType plocal : ptypes) {
           if (!ptype.equals(plocal)) {
                return false;
           }
        }

        return true;
    }

    private boolean isPType(final PType ptypes[], final PSort psort) {
        for (PType ptype : ptypes) {
            if (!ptype.getPSort().equals(psort)) {
                return false;
            }
        }

        return true;
    }

    private boolean isLegalCast(final PMetadata metadata, final PType pto, final boolean explicit) {
        //TODO: check legality of cast
        return false;
    }

    private PType getPromotion(final PMetadata[] metadatas, final boolean decimal) {
        boolean promote = true;
        Deque<PType> ptypes = new ArrayDeque<>();
        Deque<PSort> psorts = new ArrayDeque<>();

        ptypes.push(doubleptype);
        ptypes.push(floatptype);
        ptypes.push(longptype);
        ptypes.push(intptype);

        psorts.push(PSort.DOUBLE);
        psorts.push(PSort.FLOAT);
        psorts.push(PSort.LONG);

        while (true) {
            for (final PMetadata metadata : metadatas) {
                for (final PType ptype : metadata.castptypes) {
                    final PSort psort = ptype.getPSort();
                    final PCast pcast = new PCast(ptype, ptypes.peek());

                    promote = psorts.contains(psort);

                    if (promote) {
                        break;
                    }

                    promote = this.ptypes.isPDisallowed(pcast);

                    if (promote) {
                        break;
                    }

                    promote = this.ptypes.getPExplicit(pcast) != null;

                    if (promote) {
                        break;
                    }

                    promote = !psort.isPNumeric() && this.ptypes.getPImplicit(pcast) == null;

                    if (promote) {
                        break;
                    }
                }

                if (promote) {
                    break;
                }
            }

            if (!promote) {
                break;
            }

            if (promote && psorts.isEmpty()) {
                throw new IllegalArgumentException(); // TODO: message
            }

            ptypes.pop();
            psorts.pop();
        }

        final PType ptype = ptypes.peek();

        if (!decimal && ptype.getPSort() == PSort.FLOAT || ptype.getPSort() == PSort.DOUBLE) {
            throw new IllegalArgumentException(); // TODO: message
        }

        return ptype;
    }

    private Object invokeTransform(final PType pfrom, final PType pto, final Object object, final boolean explicit) {
        final PCast pcast = new PCast(pfrom, pto);

        if (ptypes.isPDisallowed(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        PTransform ptransfrom = null;

        if (explicit) {
            ptransfrom = ptypes.getPExplicit(pcast);
        }

        if (ptransfrom == null) {
            ptransfrom = ptypes.getPImplicit(pcast);
        }

        if (ptransfrom == null) {
            return null;
        }

        final PMethod pmethod = ptransfrom.getPMethod();
        final Method jmethod = pmethod.getJMethod();
        final int modifiers = jmethod.getModifiers();

        try {
            if (Modifier.isStatic(modifiers)) {
                return jmethod.invoke(null, object);
            } else {
                final Class jclass = pmethod.getPOwner().getJClass();
                final Constructor constructor = jclass.getConstructor(object.getClass());
                final Object instance = constructor.newInstance(object);

                return jmethod.invoke(instance);
            }
        } catch (NoSuchMethodException | InstantiationException |
                IllegalAccessException | IllegalArgumentException |
                InvocationTargetException | NullPointerException |
                ExceptionInInitializerError exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private Number getNumericFromChar(final char character, final PType pnumeric) {
        final PSort psort = pnumeric.getPSort();

        if (psort == PSort.BYTE) {
            Number number = (Number)invokeTransform(charptype, byteptype, character, false);

            if (number == null) {
                return (byte)character;
            } else {
                return number;
            }
        } else if (psort == PSort.SHORT) {
            Number number = (Number)invokeTransform(charptype, shortptype, character, false);

            if (number == null) {
                return (short)character;
            } else {
                return number;
            }
        } else if (psort == PSort.INT) {
            Number number = (Number)invokeTransform(charptype, intptype, character, false);

            if (number == null) {
                return (int)character;
            } else {
                return number;
            }
        } else if (psort == PSort.LONG) {
            Number number = (Number)invokeTransform(charptype, longptype, character, false);

            if (number == null) {
                return (long)character;
            } else {
                return number;
            }
        } else if (psort == PSort.FLOAT) {
            Number number = (Number)invokeTransform(charptype, longptype, character, false);

            if (number == null) {
                return (long)character;
            } else {
                return number;
            }
        } else if (psort == PSort.DOUBLE) {
            Number number = (Number)invokeTransform(charptype, longptype, character, false);

            if (number == null) {
                return (long)character;
            } else {
                return number;
            }
        }

        throw new IllegalArgumentException(); // TODO: message
    }

    @Override
    public Void visitSource(final SourceContext ctx) {
        final PMetadata sourcemd = pmetadata.get(ctx);

        incrementScope();

        for (final StatementContext sctx : ctx.statement()) {
            if (sourcemd.rtn) {
                throw new IllegalStateException();
            }

            final PMetadata statementmd = createPMetadata(sctx);
            visit(sctx);

            if (!statementmd.statement) {
                throw new IllegalStateException(); // TODO: message
            }

            sourcemd.rtn = statementmd.rtn;
        }

        sourcemd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitIf(IfContext ctx) {
        final PMetadata ifmd = getPMetadata(ctx);

        incrementScope();

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.righthand = true;
        visit(ectx);
        // TODO: cast

        final BlockContext bctx0 = ctx.block(0);
        final PMetadata blockmd0 = createPMetadata(bctx0);
        visit(ctx.block(0));

        if (ctx.ELSE() != null) {
            final BlockContext bctx1 = ctx.block(1);
            final PMetadata blockmd1 = createPMetadata(bctx1);
            visit(ctx.block(1));
            ifmd.rtn = blockmd0.rtn && blockmd1.rtn;
        }

        ifmd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitWhile(final WhileContext ctx) {
        final PMetadata whilemd = getPMetadata(ctx);

        incrementScope();
        ++loop;

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.righthand = true;
        visit(ectx);
        // TODO: cast

        final BlockContext bctx = ctx.block();
        createPMetadata(bctx);
        visit(bctx);

        whilemd.statement = true;

        --loop;
        decrementScope();

        return null;
    }

    @Override
    public Void visitDo(final DoContext ctx) {
        final PMetadata domd = getPMetadata(ctx);

        incrementScope();
        ++loop;

        final BlockContext bctx = ctx.block();
        createPMetadata(bctx);
        visit(bctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.righthand = true;
        visit(ectx);
        // TODO: cast

        domd.statement = true;

        --loop;
        decrementScope();

        return null;
    }

    @Override
    public Void visitFor(final ForContext ctx) {
        final PMetadata formd = getPMetadata(ctx);

        incrementScope();
        ++loop;

        final DeclarationContext dctx = ctx.declaration();
        final PMetadata declarationmd = createPMetadata(dctx);
        visit(dctx);

        if (!declarationmd.statement) {
            throw new IllegalStateException(); // TODO: message
        }

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        expressionmd0.righthand = true;
        visit(ectx0);
        // TODO: cast

        final ExpressionContext ectx1 = ctx.expression(0);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        if (!expressionmd1.statement) {
            throw new IllegalStateException(); // TODO: message
        }

        final BlockContext bctx = ctx.block();
        createPMetadata(bctx);
        visit(bctx);

        formd.statement = true;

        --loop;
        decrementScope();

        return null;
    }

    @Override
    public Void visitDecl(final DeclContext ctx) {
        final PMetadata declmd = getPMetadata(ctx);

        final DeclarationContext dctx = ctx.declaration();
        final PMetadata declarationmd = createPMetadata(dctx);
        visit(ctx.declaration());

        declmd.statement = declarationmd.statement;

        return null;
    }

    @Override
    public Void visitContinue(final ContinueContext ctx) {
        final PMetadata continuemd = getPMetadata(ctx);

        if (loop == 0) {
            throw new IllegalStateException(); // TODO: message
        }

        continuemd.jump = true;
        continuemd.statement = true;

        return null;
    }

    @Override
    public Void visitBreak(final BreakContext ctx) {
        final PMetadata breakmd = getPMetadata(ctx);

        if (loop == 0) {
            throw new IllegalStateException();
        }

        breakmd.jump = true;
        breakmd.statement = true;

        return null;
    }

    @Override
    public Void visitReturn(final ReturnContext ctx) {
        final PMetadata returnmd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.righthand = true;
        visit(ectx);
        // TODO: cast

        returnmd.rtn = true;
        returnmd.statement = true;

        return null;
    }

    @Override
    public Void visitExpr(final ExprContext ctx) {
        final PMetadata exprmd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        visit(ectx);

        exprmd.statement = expressionmd.statement;

        return null;
    }

    @Override
    public Void visitMultiple(final MultipleContext ctx) {
        final PMetadata multiplemd = getPMetadata(ctx);

        for (StatementContext sctx : ctx.statement()) {
            if (multiplemd.rtn || multiplemd.jump) {
                throw new IllegalStateException();
            }

            final PMetadata statementmd = new PMetadata(sctx);
            visit(sctx);

            if (!statementmd.statement) {
                throw new IllegalStateException(); // TODO: message
            }

            multiplemd.rtn = statementmd.rtn;
            multiplemd.jump = statementmd.jump;
        }

        multiplemd.statement = true;

        return null;
    }

    @Override
    public Void visitSingle(final SingleContext ctx) {
        final PMetadata singlemd = getPMetadata(ctx);

        final StatementContext sctx = ctx.statement();
        final PMetadata statementmd = new PMetadata(sctx);
        visit(sctx);

        if (!statementmd.statement) {
            throw new IllegalStateException(); // TODO: message
        }

        singlemd.statement = true;

        return null;
    }

    @Override
    public Void visitEmpty(final EmptyContext ctx) {
        final PMetadata emptymd = getPMetadata(ctx);
        emptymd.statement = true;

        return null;
    }

    @Override
    public Void visitDeclaration(final DeclarationContext ctx) {
        final PMetadata declarationmd = getPMetadata(ctx);

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        visit(dctx);
        final PType pdecltype = decltypemd.declptype;

        if (pdecltype == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            final ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof TerminalNode) {
                final TerminalNode tctx = (TerminalNode)cctx;

                if (tctx.getSymbol().getType() == PainlessLexer.ID) {
                    final String name = tctx.getText();
                    addPVariable(name, pdecltype);
                }
            } else if (cctx instanceof ExpressionContext) {
                final ExpressionContext ectx = (ExpressionContext)cctx;
                final PMetadata expressiondmd = createPMetadata(ectx);
                expressiondmd.righthand = true;
                visit(ectx);
                // TODO: cast
            }
        }

        declarationmd.statement = true;

        return null;
    }

    @Override
    public Void visitDecltype(final DecltypeContext ctx) {
        final PMetadata decltypemd = getPMetadata(ctx);

        final String pnamestr = ctx.getText();
        decltypemd.declptype = getPTypeFromCanonicalPName(ptypes, pnamestr);

        return null;
    }

    @Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        final PMetadata precedencemd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.righthand = precedencemd.righthand;
        visit(ectx);

        precedencemd.castnodes = expressionmd.castnodes;
        precedencemd.castptypes = expressionmd.castptypes;
        precedencemd.constant = expressionmd.constant;

        return null;
    }

    @Override
    public Void visitNumeric(final NumericContext ctx) {
        final PMetadata numericmd = getPMetadata(ctx);

        numericmd.castnodes = new ParseTree[] {ctx};
        numericmd.castptypes = new PType[1];

        if (ctx.DECIMAL() != null) {
            final String svalue = ctx.DECIMAL().getText();

            if (svalue.endsWith("f") || svalue.endsWith("F")) {
                try {
                    numericmd.constant = Float.parseFloat(svalue);
                    numericmd.castptypes[0] = boolptype;
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    numericmd.constant = Double.parseDouble(svalue);
                    numericmd.castptypes[0] = doubleptype;
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
                    numericmd.constant = Long.parseLong(svalue, radix);
                    numericmd.castptypes[0] = longptype;
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    numericmd.constant = Integer.parseInt(svalue, radix);
                    numericmd.castptypes[0] = intptype;
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        return null;
    }

    @Override
    public Void visitString(final StringContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);

        if (ctx.STRING() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        stringmd.constant = ctx.STRING().getText();
        stringmd.castnodes = new ParseTree[] {ctx};
        stringmd.castptypes = new PType[] {stringptype};

        return null;
    }

    @Override
    public Void visitChar(final CharContext ctx) {
        final PMetadata charmd = getPMetadata(ctx);

        if (ctx.CHAR() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        if (ctx.CHAR().getText().length() > 1) {
            throw new IllegalStateException(); // TODO: message
        }

        charmd.constant = ctx.CHAR().getText().charAt(0);
        charmd.castnodes = new ParseTree[] {ctx};
        charmd.castptypes = new PType[] {charptype};

        return null;
    }
    @Override
    public Void visitTrue(final TrueContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);

        if (ctx.TRUE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        truemd.constant = true;
        truemd.castnodes = new ParseTree[] {ctx};
        truemd.castptypes = new PType[] {boolptype};

        return null;
    }

    @Override
    public Void visitFalse(final FalseContext ctx) {
        final PMetadata falsemd = getPMetadata(ctx);

        if (ctx.FALSE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        falsemd.constant = false;
        falsemd.castnodes = new ParseTree[] {ctx};
        falsemd.castptypes = new PType[] {boolptype};

        return null;
    }

    @Override
    public Void visitNull(final NullContext ctx) {
        final PMetadata nullmd = getPMetadata(ctx);

        if (ctx.NULL() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        nullmd.castnodes = new ParseTree[] {ctx};
        nullmd.castptypes = new PType[] {objectptype};

        return null;
    }

    @Override
    public Void visitExt(final ExtContext ctx) {
        final PMetadata extmd = getPMetadata(ctx);

        final ExtstartContext ectx = ctx.extstart();
        final PMetadata extstartmd = createPMetadata(ectx);
        visit(ectx);

        extmd.statement = extstartmd.statement;
        extmd.castnodes = extstartmd.castnodes;
        extmd.castptypes = extstartmd.castptypes;

        return null;
    }

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final PMetadata unarymd = getPMetadata(ctx);
        unarymd.castnodes = new ParseTree[] {ctx};

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        visit(ectx);

        if (expressionmd.constant != null) {
            final PType ptype = expressionmd.castptypes[0];

            if (ctx.BOOLNOT() != null) {
                unarymd.castptypes = new PType[] {boolptype};

                final Object constant = invokeTransform(ptype, boolptype, expressionmd.constant, false);

                if (constant != null) {
                    unarymd.constant = !((Boolean)constant);
                } else if (expressionmd.constant instanceof Boolean) {
                    unarymd.constant = !((Boolean)expressionmd.constant);
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                final boolean decimal = ctx.ADD() != null || ctx.SUB() != null;
                final PType ppromote = getPromotion(new PMetadata[] {expressionmd}, decimal);
                final PSort psort = ppromote.getPSort();
                unarymd.castptypes = new PType[] {ppromote};

                Number number = null;

                if (expressionmd.constant instanceof Byte) {
                    Object object = invokeTransform(byteptype, ppromote, expressionmd.constant, false);
                    number = object == null ? (Number)expressionmd.constant : (Number)object;
                } else if (expressionmd.constant instanceof Short) {
                    Object object = invokeTransform(shortptype, ppromote, expressionmd.constant, false);
                    number = object == null ? (Number)expressionmd.constant : (Number)object;
                } else if (expressionmd.constant instanceof Character) {
                    number = getNumericFromChar((char)expressionmd.constant, ppromote);
                } else if (expressionmd.constant instanceof Integer) {
                    Object object = invokeTransform(intptype, ppromote, expressionmd.constant, false);
                    number = object == null ? (Number)expressionmd.constant : (Number)object;
                } else if (expressionmd.constant instanceof Long) {
                    Object object = invokeTransform(longptype, ppromote, expressionmd.constant, false);
                    number = object == null ? (Number)expressionmd.constant : (Number)object;
                } else if (expressionmd.constant instanceof Float) {
                    Object object = invokeTransform(floatptype, ppromote, expressionmd.constant, false);
                    number = object == null ? (Number)expressionmd.constant : (Number)object;
                } else if (expressionmd.constant instanceof Double) {
                    Object object = invokeTransform(doubleptype, ppromote, expressionmd.constant, false);
                    number = object == null ? (Number)expressionmd.constant : (Number)object;
                }

                if (number == null) {
                    throw new IllegalStateException(); // TODO: message
                }

                if (ctx.BWNOT() != null) {
                    if (psort == PSort.INT) {
                        unarymd.constant = ~number.intValue();
                    } else if (psort == PSort.LONG) {
                        unarymd.constant = ~number.longValue();
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.SUB() != null) {
                    if (psort == PSort.INT) {
                        unarymd.constant = -number.intValue();
                    } else if (psort == PSort.LONG) {
                        unarymd.constant = -number.longValue();
                    } else if (psort == PSort.FLOAT) {
                        unarymd.constant = -number.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        unarymd.constant = -number.doubleValue();
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.ADD() != null) {
                    if (psort == PSort.INT) {
                        unarymd.constant = -number.intValue();
                    } else if (psort == PSort.LONG) {
                        unarymd.constant = -number.longValue();
                    } else if (psort == PSort.FLOAT) {
                        unarymd.constant = -number.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        unarymd.constant = -number.doubleValue();
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            // TODO: cast
        }

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final PMetadata castmd = getPMetadata(ctx);
        castmd.castnodes = new ParseTree[] {ctx};

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        final PType declptype = decltypemd.declptype;
        final PSort declpsort = declptype.getPSort();
        castmd.castptypes = new PType[] {declptype};

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.righthand = castmd.righthand;
        visit(ectx);

        if (expressionmd.constant != null) {
            if (expressionmd.constant instanceof Boolean) {
                castmd.constant = invokeTransform(boolptype, declptype, expressionmd.constant, true);

                if (castmd.constant == null && declpsort == PSort.BOOL) {
                    castmd.constant = expressionmd.constant;
                }
            } else if (expressionmd.constant instanceof Number) {
                if (declpsort == PSort.BYTE) {
                    castmd.constant = invokeTransform(byteptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        castmd.constant = ((Number)expressionmd.constant).byteValue();
                    }
                } else if (declpsort == PSort.SHORT) {
                    castmd.constant = invokeTransform(shortptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        castmd.constant = ((Number)expressionmd.constant).shortValue();
                    }
                } else if (declpsort == PSort.CHAR) {
                    castmd.constant = invokeTransform(charptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        final PType promote = getPromotion(new PMetadata[]{expressionmd}, true);

                        if (promote.equals(intptype)) {
                            castmd.constant = (char)((Number)expressionmd.constant).intValue();
                        } else if (promote.equals(longptype)) {
                            castmd.constant = (char)((Number)expressionmd.constant).longValue();
                        } else if (promote.equals(floatptype)) {
                            castmd.constant = (char)((Number)expressionmd.constant).floatValue();
                        } else if (promote.equals(doubleptype)) {
                            castmd.constant = (char)((Number)expressionmd.constant).doubleValue();
                        }
                    }
                } else if (declpsort == PSort.INT) {
                    castmd.constant = invokeTransform(intptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        castmd.constant = ((Number)expressionmd.constant).intValue();
                    }
                } else if (declpsort == PSort.LONG) {
                    castmd.constant = invokeTransform(longptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        castmd.constant = ((Number)expressionmd.constant).longValue();
                    }
                } else if (declpsort == PSort.FLOAT) {
                    castmd.constant = invokeTransform(floatptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        castmd.constant = ((Number)expressionmd.constant).floatValue();
                    }
                } else if (declpsort == PSort.DOUBLE) {
                    castmd.constant = invokeTransform(doubleptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        castmd.constant = ((Number)expressionmd.constant).doubleValue();
                    }
                }
            } else if (expressionmd.constant instanceof Character) {
                castmd.constant = invokeTransform(charptype, declptype, expressionmd.constant, true);

                if (castmd.constant == null) {
                    if (declpsort == PSort.BYTE) {
                        castmd.constant = (byte)(char)expressionmd.constant;
                    } else if (declpsort == PSort.SHORT) {
                        castmd.constant = (short)(char)expressionmd.constant;
                    } else if (declpsort == PSort.CHAR) {
                        castmd.constant = expressionmd.constant;
                    } else if (declpsort == PSort.INT) {
                        castmd.constant = (int)(char)expressionmd.constant;
                    } else if (declpsort == PSort.LONG) {
                        castmd.constant = (long)(char)expressionmd.constant;
                    } else if (declpsort == PSort.FLOAT) {
                        castmd.constant = (float)(char)expressionmd.constant;
                    } else if (declpsort == PSort.DOUBLE) {
                        castmd.constant = (double)(char)expressionmd.constant;
                    }
                }
            } else if (expressionmd.constant instanceof String) {
                castmd.constant = invokeTransform(boolptype, declptype, expressionmd.constant, true);

                if (castmd.constant == null && declpsort == PSort.BOOL) {
                    castmd.constant = expressionmd.constant;
                }
            }
        }

        if (castmd.constant == null) {
            //TODO : cast
        }

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final PMetadata binarymd = getPMetadata(ctx);
        binarymd.castnodes = new ParseTree[] {ctx};

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        final boolean decimal = ctx.ADD() != null || ctx.SUB() != null ||
                ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null;
        final PType ppromote = getPromotion(new PMetadata[] {expressionmd0, expressionmd1}, decimal);
        final PSort psort = ppromote.getPSort();
        binarymd.castptypes = new PType[] {ppromote};

        if (expressionmd0.constant != null && expressionmd1.constant != null) {
            Number number0 = null;
            Number number1 = null;

            if (expressionmd0.constant instanceof Byte) {
                Object number = invokeTransform(byteptype, ppromote, expressionmd0.constant, false);
                number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
            } else if (expressionmd0.constant instanceof Short) {
                Object number = invokeTransform(shortptype, ppromote, expressionmd0.constant, false);
                number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
            } else if (expressionmd0.constant instanceof Character) {
                number0 = getNumericFromChar((char)expressionmd0.constant, ppromote);
            } else if (expressionmd0.constant instanceof Integer) {
                Object number = invokeTransform(intptype, ppromote, expressionmd0.constant, false);
                number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
            } else if (expressionmd0.constant instanceof Long) {
                Object number = invokeTransform(longptype, ppromote, expressionmd0.constant, false);
                number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
            } else if (expressionmd0.constant instanceof Float) {
                Object number = invokeTransform(floatptype, ppromote, expressionmd0.constant, false);
                number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
            } else if (expressionmd0.constant instanceof Double) {
                Object number = invokeTransform(doubleptype, ppromote, expressionmd0.constant, false);
                number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
            }

            if (expressionmd1.constant instanceof Byte) {
                Object number = invokeTransform(byteptype, ppromote, expressionmd1.constant, false);
                number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
            } else if (expressionmd1.constant instanceof Short) {
                Object number = invokeTransform(shortptype, ppromote, expressionmd1.constant, false);
                number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
            } else if (expressionmd1.constant instanceof Character) {
                number1 = getNumericFromChar((char)expressionmd1.constant, ppromote);
            } else if (expressionmd1.constant instanceof Integer) {
                Object number = invokeTransform(intptype, ppromote, expressionmd1.constant, false);
                number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
            } else if (expressionmd1.constant instanceof Long) {
                Object number = invokeTransform(longptype, ppromote, expressionmd1.constant, false);
                number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
            } else if (expressionmd1.constant instanceof Float) {
                Object number = invokeTransform(floatptype, ppromote, expressionmd1.constant, false);
                number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
            } else if (expressionmd1.constant instanceof Double) {
                Object number = invokeTransform(doubleptype, ppromote, expressionmd1.constant, false);
                number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
            }

            if (number0 == null || number1 == null) {
                throw new IllegalStateException(); // TODO: message
            }

            if (ctx.MUL() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() * number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() * number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() * number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() * number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.DIV() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() / number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() / number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() / number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() / number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.REM() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() % number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() % number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() % number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() % number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.ADD() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() + number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() + number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() + number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() + number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.SUB() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() - number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() - number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() - number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() - number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.LSH() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() << number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() << number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.RSH() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() >> number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() >> number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.USH() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() >>> number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() >>> number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWAND() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() & number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() & number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWXOR() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() ^ number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() ^ number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWOR() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() | number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() | number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        } else {
            // TODO: cast
        }

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final PMetadata compmd = getPMetadata(ctx);
        compmd.castnodes = new ParseTree[] {ctx};
        compmd.castptypes = new PType[] {boolptype};

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        if (expressionmd0.constant != null && expressionmd1.constant != null) {
            PSort psort0 = expressionmd0.castptypes[0].getPSort();
            PSort psort1 = expressionmd1.castptypes[0].getPSort();

            if (psort0 == PSort.BOOL && psort1 == PSort.BOOL) {
                Object constant0 = invokeTransform(boolptype, boolptype, expressionmd0.constant, false);
                Object constant1 = invokeTransform(boolptype, boolptype, expressionmd1.constant, false);

                if (constant0 == null) {
                    constant0 = expressionmd0.constant;
                }

                if (constant1 == null) {
                    constant1 = expressionmd1.constant;
                }

                if (ctx.EQ() != null) {
                    compmd.constant = (boolean)constant0 == (boolean)constant1;
                } else if (ctx.NE() != null) {
                    compmd.constant = (boolean)constant0 != (boolean)constant1;
                } else {
                    throw new IllegalStateException();
                }
            } else if (psort0.isPNumeric() && psort1.isPNumeric()) {
                final PType ppromote = getPromotion(new PMetadata[]{expressionmd0, expressionmd1}, true);
                final PSort psort = ppromote.getPSort();
                Number number0 = null;
                Number number1 = null;

                if (expressionmd0.constant instanceof Byte) {
                    Object number = invokeTransform(byteptype, ppromote, expressionmd0.constant, false);
                    number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
                } else if (expressionmd0.constant instanceof Short) {
                    Object number = invokeTransform(shortptype, ppromote, expressionmd0.constant, false);
                    number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
                } else if (expressionmd0.constant instanceof Character) {
                    number0 = getNumericFromChar((char)expressionmd0.constant, ppromote);
                } else if (expressionmd0.constant instanceof Integer) {
                    Object number = invokeTransform(intptype, ppromote, expressionmd0.constant, false);
                    number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
                } else if (expressionmd0.constant instanceof Long) {
                    Object number = invokeTransform(longptype, ppromote, expressionmd0.constant, false);
                    number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
                } else if (expressionmd0.constant instanceof Float) {
                    Object number = invokeTransform(floatptype, ppromote, expressionmd0.constant, false);
                    number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
                } else if (expressionmd0.constant instanceof Double) {
                    Object number = invokeTransform(doubleptype, ppromote, expressionmd0.constant, false);
                    number0 = number == null ? (Number)expressionmd0.constant : (Number)number;
                }

                if (expressionmd1.constant instanceof Byte) {
                    Object number = invokeTransform(byteptype, ppromote, expressionmd1.constant, false);
                    number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
                } else if (expressionmd1.constant instanceof Short) {
                    Object number = invokeTransform(shortptype, ppromote, expressionmd1.constant, false);
                    number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
                } else if (expressionmd1.constant instanceof Character) {
                    number1 = getNumericFromChar((char)expressionmd1.constant, ppromote);
                } else if (expressionmd1.constant instanceof Integer) {
                    Object number = invokeTransform(intptype, ppromote, expressionmd1.constant, false);
                    number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
                } else if (expressionmd1.constant instanceof Long) {
                    Object number = invokeTransform(longptype, ppromote, expressionmd1.constant, false);
                    number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
                } else if (expressionmd1.constant instanceof Float) {
                    Object number = invokeTransform(floatptype, ppromote, expressionmd1.constant, false);
                    number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
                } else if (expressionmd1.constant instanceof Double) {
                    Object number = invokeTransform(doubleptype, ppromote, expressionmd1.constant, false);
                    number1 = number == null ? (Number)expressionmd1.constant : (Number)number;
                }

                if (number0 == null || number1 == null) {
                    throw new IllegalStateException(); // TODO: message
                }

                if (ctx.EQ() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() == number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() == number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() == number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() == number1.doubleValue();
                    }
                } else if (ctx.NE() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() != number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() != number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() != number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() != number1.doubleValue();
                    }
                } else if (ctx.GTE() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() >= number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() >= number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() >= number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() >= number1.doubleValue();
                    }
                } else if (ctx.GT() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() > number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() > number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() > number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() > number1.doubleValue();
                    }
                } else if (ctx.LTE() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() <= number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() <= number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() <= number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() <= number1.doubleValue();
                    }
                } else if (ctx.LT() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() < number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() < number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() < number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() < number1.doubleValue();
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                if (ctx.EQ() != null) {
                    compmd.constant = expressionmd0.constant == expressionmd1.constant;
                } else if (ctx.NE() != null) {
                    compmd.constant = expressionmd0.constant != expressionmd1.constant;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            // TODO: cast
        }

        return null;
    }

    @Override
    public Void visitBool(BoolContext ctx) {
        final PMetadata boolmd = getPMetadata(ctx);
        boolmd.castnodes = new ParseTree[] {ctx};
        boolmd.castptypes = new PType[] {boolptype};

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        if (expressionmd0.constant != null && expressionmd1.constant != null) {
            Object constant0 = invokeTransform(expressionmd0.castptypes[0], boolptype, expressionmd0.constant, false);
            Object constant1 = invokeTransform(expressionmd1.castptypes[0], boolptype, expressionmd1.constant, false);

            if (constant0 == null) {
                constant0 = expressionmd0.constant;
            }

            if (constant1 == null) {
                constant1 = expressionmd1.constant;
            }

            if (ctx.BOOLAND() != null) {
                boolmd.constant = (boolean)constant0 && (boolean)constant1;
            } else if (ctx.BOOLOR() != null) {
                boolmd.constant = (boolean)constant0 || (boolean)constant1;
            } else {
                throw new IllegalStateException();
            }
        } else {
            // TODO: cast
        }

        return null;
    }

    @Override
    public Void visitConditional(ConditionalContext ctx) {
        final PMetadata conditionalmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);

        if (expressionmd0.constant != null) {
            Object constant = invokeTransform(expressionmd0.castptypes[0], boolptype, expressionmd0.constant, false);

            if (constant == null) {
                constant = expressionmd0.constant;
            }

            conditionalmd.constant = constant;
        }

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        final ExpressionContext ectx2 = ctx.expression(2);
        final PMetadata expressionmd2 = createPMetadata(ectx2);
        visit(ectx2);

        boolean constant0 = false;
        boolean constant1 = false;

        if (conditionalmd.constant != null) {
            if ((boolean)conditionalmd.constant && expressionmd1.constant != null) {
                constant0 = true;
            } else if (expressionmd2.constant != null) {
                constant1 = true;
            }
        }

        if (constant0) {
            conditionalmd.constant = expressionmd1.constant;
            conditionalmd.castptypes[0] = expressionmd1.castptypes[0];
        } else if (constant1) {
            conditionalmd.constant = expressionmd2.constant;
            conditionalmd.castptypes[0] = expressionmd2.castptypes[0];
        } else {
            final int nodeslen1 = expressionmd1.castnodes.length;
            final int nodeslen2 = expressionmd2.castnodes.length;
            conditionalmd.castnodes = new ParseTree[nodeslen1 + nodeslen2];
            System.arraycopy(expressionmd1.castnodes, 0, conditionalmd.castnodes, 0, nodeslen1);
            System.arraycopy(expressionmd2.castnodes, 0, conditionalmd.castnodes, nodeslen1, nodeslen2);

            final int castslen1 = expressionmd1.castptypes.length;
            final int castslen2 = expressionmd2.castptypes.length;
            conditionalmd.castnodes = new ParseTree[castslen1 + castslen2];
            System.arraycopy(expressionmd1.castptypes, 0, conditionalmd.castptypes, 0, castslen1);
            System.arraycopy(expressionmd2.castptypes, 0, conditionalmd.castptypes, castslen1, castslen2);

            conditionalmd.conditional = true;
        }

        return null;
    }

    @Override
    public Void visitAssignment(final AssignmentContext ctx) {
        final PMetadata assignmentmd = getPMetadata(ctx);

        final ExtstartContext ectx0 = ctx.extstart();
        PMetadata extstartmd = createPMetadata(ectx0);
        visit(ectx0);

        if (extstartmd.pexternal == null) {
            throw new IllegalArgumentException();
        }

        if (extstartmd.pexternal.isReadOnly()) {
            throw new IllegalArgumentException();
        }

        if (assignmentmd.righthand) {
            assignmentmd.castnodes = new ParseTree[] {ctx};
            assignmentmd.castptypes = new PType[] {extstartmd.pexternal.getPType()};
        }

        final ExpressionContext ectx1 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx1);
        expressionmd.righthand = true;
        visit(ectx1);
        // TODO: cast

        assignmentmd.statement = true;

        return null;
    }

    @Override
    public Void visitExtstart(final ExtstartContext ctx) {
        final PMetadata extstartmd = getPMetadata(ctx);
        extstartmd.pexternal = new PExternal();

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
            throw new IllegalStateException();
        }

        extstartmd.statement = extstartmd.pexternal.isCall();
        extstartmd.castnodes = new ParseTree[] {ctx};
        extstartmd.castptypes = new PType[] {extstartmd.pexternal.getPType()};

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
            throw new IllegalStateException();
        }

        final ExtdotContext ectx4 = ctx.extdot();
        final ExtarrayContext ectx5 = ctx.extarray();

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
            throw new IllegalStateException();
        }

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        visit(dctx);

        final PType pfrom = extcastmd0.pexternal.getPType();
        final PType pto = decltypemd.declptype;

        //TODO: check cast legality
        extcastmd0.pexternal.addSegment(CAST, new PCast(pfrom, pto));

        return null;
    }

    @Override
    public Void visitExtarray(final ExtarrayContext ctx) {
        final PMetadata extarraymd0 = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx0);
        visit(ectx0);
        // TODO: cast

        extarraymd0.pexternal.addSegment(PainlessExternal.ARRAY, ectx0);

        final ExtdotContext ectx1 = ctx.extdot();
        final ExtarrayContext ectx2 = ctx.extarray();

        if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extarraymd0.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata extarraymd1 = createPMetadata(ectx2);
            extarraymd1.pexternal = extarraymd0.pexternal;
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
        final String ptypestr = ctx.TYPE().getText();
        final PType ptype = getPTypeFromCanonicalPName(ptypes, ptypestr);

        if (ptype == null) {
            throw new IllegalArgumentException();
        }

        exttypemd.pexternal.addSegment(PainlessExternal.TYPE, ptype);

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
        final PClass pclass = declptype.getPClass();

        if (pclass == null) {
            throw new IllegalArgumentException();
        }

        final String pname = ctx.ID().getText();
        final boolean statik = extcallmd.pexternal.isStatic();

        final ArgumentsContext actx = ctx.arguments();
        final PMetadata argumentsmd = createPMetadata(actx);
        final List<ExpressionContext> arguments = ctx.arguments().expression();
        argumentsmd.pexternal = extcallmd.pexternal;
        argumentsmd.castnodes = new ParseTree[arguments.size()];
        arguments.toArray(argumentsmd.castnodes);

        if (statik && "makearray".equals(pname)) {
            extcallmd.pexternal.addSegment(AMAKE, arguments.size());

            PType[] ptypes = new PType[arguments.size()];
            Arrays.fill(ptypes, intptype);
            argumentsmd.castptypes = ptypes;
        } else {
            final PConstructor pconstructor = statik ? pclass.getPConstructor(pname) : null;
            final PMethod pmethod = statik ? pclass.getPFunction(pname) : pclass.getPMethod(pname);

            if (pconstructor != null) {
                extcallmd.pexternal.addSegment(CONSTRUCTOR, pconstructor);
                argumentsmd.castptypes = new PType[arguments.size()];
                pconstructor.getPArguments().toArray(argumentsmd.castptypes);
            } else if (pmethod != null) {
                extcallmd.pexternal.addSegment(METHOD, pmethod);
                argumentsmd.castptypes = new PType[arguments.size()];
                pmethod.getPArguments().toArray(argumentsmd.castptypes);
            } else {
                throw new IllegalArgumentException();
            }

            visit(actx);
        }

        visit(actx);

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtarrayContext ectx1 = ctx.extarray();

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
                throw new IllegalArgumentException();
            }

            extmembermd.pexternal.addSegment(VARIABLE, pvariable);
        } else {
            if (ptype.getPSort() == PSort.ARRAY) {
                if ("length".equals(pname)) {
                    extmembermd.pexternal.addSegment(ALENGTH, intptype);
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                final PClass pclass = ptype.getPClass();

                if (pclass == null) {
                    throw new IllegalArgumentException();
                }

                final boolean statik = extmembermd.pexternal.isStatic();
                final PField pmember = statik ? pclass.getPStatic(pname) : pclass.getPMember(pname);

                if (pmember == null) {
                    throw new IllegalArgumentException();
                }

                extmembermd.pexternal.addSegment(FIELD, pmember);
            }
        }

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtarrayContext ectx1 = ctx.extarray();

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
        final PMetadata argumentsmd = getPMetadata(ctx);
        final ParseTree[] nodes = argumentsmd.castnodes;
        final PType[] atypes = argumentsmd.castptypes;

        if (nodes == null || atypes == null) {
            throw new IllegalArgumentException();
        }

        if (nodes.length != atypes.length) {
            throw new IllegalArgumentException();
        }

        final int arguments = nodes.length;

        for (int argument = 0; argument < arguments; ++argument) {
            final ParseTree ectx = nodes[argument];
            final PMetadata nodemd = createPMetadata(ectx);
            visit(ectx);
            // TODO: cast

            argumentsmd.pexternal.addSegment(ARGUMENT, ectx);
        }

        return null;
    }
}
