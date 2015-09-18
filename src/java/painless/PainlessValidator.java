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
        boolean conditional;

        boolean righthand;
        Type adecltype;

        ParseTree castnodes[];
        Type castatypes[];
        Object constant;

        boolean statik;
        Deque<ParseTree> external;

        Metadata(final ParseTree node) {
            this.node = node;

            rtn = false;
            jump = false;
            statement = false;
            conditional = false;

            righthand = false;
            adecltype = null;

            castnodes = null;
            castatypes = null;
            constant = null;

            statik = false;
            external = null;
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

    private boolean isType(final Type atypes[], final Type aistype, final boolean asort) {
        boolean istype = true;

        for (Type atype : atypes) {
           istype &= asort ? atype.getSort() == aistype.getSort() : atype.equals(aistype);
        }

        return istype || atypes.length > 0;
    }

    private boolean isNumeric(final Type atypes[]) {
        boolean numeric = true;

        for (Type atype : atypes) {
            final int asort = atype.getSort();

            //TODO: check implicit transforms

            numeric &= asort == BYTE || asort == SHORT || asort == Type.CHAR ||
                    asort == INT || asort == LONG || asort == FLOAT || asort == DOUBLE;
        }

        return numeric || atypes.length > 0;
    }

    private void markCast(final Metadata metadata, final Type ato, final boolean explicit) {
        //TODO: check legality of cast
    }

    private Type getPromotion(final Metadata[] metadatas, final boolean decimal) {
        Type apromote = INT_TYPE;

        for (final Metadata metadata : metadatas) {
            if (!isNumeric(metadata.castatypes)) {
                throw new IllegalArgumentException();
            }

            for (final Type atype : metadata.castatypes) {
                if (atype.getSort() == LONG && apromote.getSort() == INT) {
                    apromote = LONG_TYPE;
                } else if (atype.getSort() == FLOAT && apromote.getSort() != DOUBLE) {
                    if (!decimal) {
                        throw new IllegalArgumentException();
                    }

                    apromote = FLOAT_TYPE;
                } else if (atype.getSort() == DOUBLE) {
                    if (!decimal) {
                        throw new IllegalArgumentException();
                    }

                    apromote = DOUBLE_TYPE;
                }
            }
        }

        return apromote;
    }

    private Number getNFromC(final char character, final Type anumeric) {
        final int asort = anumeric.getSort();

        if (asort == BYTE) {
            return new Byte((byte)character);
        } else if (asort == SHORT) {
            return new Short((short)character);
        } else if (asort == INTEGER) {
            return new Integer(character);
        } else if (asort == LONG) {
            return new Long(character);
        } else if (asort == FLOAT) {
            return new Float(character);
        } else if (asort == DOUBLE) {
            return new Double(character);
        }

        throw new IllegalArgumentException();
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
        expressionmd.righthand = true;
        visit(ectx);
        markCast(expressionmd, BOOLEAN_TYPE, false);

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
        final Metadata expressionmd = createMetadata(ectx);
        expressionmd.righthand = true;
        visit(ectx);
        markCast(expressionmd, BOOLEAN_TYPE, false);

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
        final Metadata expressionmd = createMetadata(ectx);
        expressionmd.righthand = true;
        visit(ectx);
        markCast(expressionmd, BOOLEAN_TYPE, false);

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
        expressionmd0.righthand = true;
        visit(ectx0);
        markCast(expressionmd0, BOOLEAN_TYPE, false);

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
        final Metadata expressionmd = createMetadata(ectx);
        expressionmd.righthand = true;
        visit(ectx);
        markCast(expressionmd, getType("Ljava/lang/Object;"), false);

        returnmd.rtn = true;
        returnmd.statement = true;

        return null;
    }

    @Override
    public Void visitExpr(final ExprContext ctx) {
        final Metadata exprmd = getMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ectx);
        visit(ectx);

        exprmd.statement = expressionmd.statement;

        return null;
    }

    @Override
    public Void visitMultiple(final MultipleContext ctx) {
        final Metadata multiplemd = getMetadata(ctx);

        for (StatementContext sctx : ctx.statement()) {
            if (multiplemd.rtn || multiplemd.jump) {
                throw new IllegalStateException();
            }

            final Metadata statementmd = new Metadata(sctx);
            visit(sctx);

            if (!statementmd.statement) {
                throw new IllegalStateException();
            }

            multiplemd.rtn = statementmd.rtn;
            multiplemd.jump = statementmd.jump;
        }

        multiplemd.statement = true;

        return null;
    }

    @Override
    public Void visitSingle(final SingleContext ctx) {
        final Metadata singlemd = getMetadata(ctx);

        final StatementContext sctx = ctx.statement();
        final Metadata statementmd = new Metadata(sctx);
        visit(sctx);

        if (!statementmd.statement) {
            throw new IllegalStateException();
        }

        singlemd.statement = true;

        return null;
    }

    @Override
    public Void visitEmpty(final EmptyContext ctx) {
        final Metadata emptymd = getMetadata(ctx);
        emptymd.statement = true;

        return null;
    }

    @Override
    public Void visitDeclaration(final DeclarationContext ctx) {
        final Metadata declarationmd = getMetadata(ctx);

        final DecltypeContext dctx = ctx.decltype();
        final Metadata decltypemd = createMetadata(dctx);
        final Type adecltype = decltypemd.adecltype;

        if (adecltype == null) {
            throw new IllegalArgumentException();
        }

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            final ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof TerminalNode) {
                final TerminalNode tctx = (TerminalNode)cctx;

                if (tctx.getSymbol().getType() == PainlessLexer.ID) {
                    final String name = tctx.getText();
                    addVariable(name, adecltype);
                }
            } else if (cctx instanceof ExpressionContext) {
                final ExpressionContext ectx = (ExpressionContext)cctx;
                final Metadata expressiondmd = createMetadata(ectx);
                expressiondmd.righthand = true;
                visit(ectx);
                markCast(expressiondmd, adecltype, false);
            }
        }

        declarationmd.statement = true;

        return null;
    }

    @Override
    public Void visitDecltype(final DecltypeContext ctx) {
        final Metadata decltypemd = getMetadata(ctx);

        final String ptype = ctx.getText();
        decltypemd.adecltype = ptypes.getATypeFromPClass(ptype);

        return null;
    }

    @Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        final Metadata precedencemd = getMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ectx);
        expressionmd.righthand = precedencemd.righthand;
        visit(ectx);

        precedencemd.castnodes = expressionmd.castnodes;
        precedencemd.castatypes = expressionmd.castatypes;
        precedencemd.constant = expressionmd.constant;

        return null;
    }

    @Override
    public Void visitNumeric(final NumericContext ctx) {
        final Metadata numericmd = getMetadata(ctx);

        numericmd.castnodes = new ParseTree[] {ctx};
        numericmd.castatypes = new Type[1];

        if (ctx.DECIMAL() != null) {
            final String svalue = ctx.DECIMAL().getText();
            final double dvalue = Double.parseDouble(svalue);

            if (svalue.endsWith("f") || svalue.endsWith("F") && dvalue < Float.MAX_VALUE && dvalue > Float.MIN_VALUE) {
                numericmd.constant = (float)dvalue;
                numericmd.castatypes[0] = FLOAT_TYPE;
            } else {
                numericmd.constant = Double.parseDouble(svalue);
                numericmd.castatypes[0] = DOUBLE_TYPE;
            }
        } else {
            String svalue;
            long lvalue;

            if (ctx.OCTAL() != null) {
                svalue = ctx.OCTAL().getText();
                lvalue = Long.parseLong(svalue, 8);
            } else if (ctx.INTEGER() != null) {
                svalue = ctx.INTEGER().getText();
                lvalue = Long.parseLong(svalue);
            } else if (ctx.HEX() != null) {
                svalue = ctx.HEX().getText();
                lvalue = Long.parseLong(svalue, 16);
            } else {
                throw new IllegalStateException();
            }

            if (svalue.endsWith("l") || svalue.endsWith("L")) {
                numericmd.constant = lvalue;
                numericmd.castatypes[0] = LONG_TYPE;
            } else if (lvalue > Integer.MIN_VALUE && lvalue < Integer.MAX_VALUE) {
                numericmd.constant = (int)lvalue;
                numericmd.castatypes[0] = INT_TYPE;
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (ptypes.getPClass(numericmd.castatypes[0]) == null) {
            throw new IllegalArgumentException();
        }

        return null;
    }

    @Override
    public Void visitString(final StringContext ctx) {
        final Metadata stringmd = getMetadata(ctx);

        if (ctx.STRING() == null) {
            throw new IllegalStateException();
        }

        stringmd.constant = ctx.STRING().getText();
        stringmd.castnodes = new ParseTree[] {ctx};
        stringmd.castatypes = new Type[] {getType("Ljava/lang/String;")};

        if (ptypes.getPClass(stringmd.castatypes[0]) == null) {
            throw new IllegalArgumentException();
        }

        return null;
    }

    @Override
    public Void visitChar(final CharContext ctx) {
        final Metadata charmd = getMetadata(ctx);

        if (ctx.CHAR() == null) {
            throw new IllegalStateException();
        }

        if (ctx.CHAR().getText().length() > 1) {
            throw new IllegalStateException();
        }

        charmd.constant = ctx.CHAR().getText().charAt(0);
        charmd.castnodes = new ParseTree[] {ctx};
        charmd.castatypes = new Type[] {CHAR_TYPE};

        if (ptypes.getPClass(charmd.castatypes[0]) == null) {
            throw new IllegalArgumentException();
        }

        return null;
    }
    @Override
    public Void visitTrue(final TrueContext ctx) {
        final Metadata truemd = getMetadata(ctx);

        if (ctx.TRUE() == null) {
            throw new IllegalStateException();
        }

        truemd.constant = true;
        truemd.castnodes = new ParseTree[] {ctx};
        truemd.castatypes = new Type[] {BOOLEAN_TYPE};

        if (ptypes.getPClass(truemd.castatypes[0]) == null) {
            throw new IllegalArgumentException();
        }

        return null;
    }

    @Override
    public Void visitFalse(final FalseContext ctx) {
        final Metadata falsemd = getMetadata(ctx);

        if (ctx.FALSE() == null) {
            throw new IllegalStateException();
        }

        falsemd.constant = false;
        falsemd.castnodes = new ParseTree[] {ctx};
        falsemd.castatypes = new Type[] {BOOLEAN_TYPE};

        if (ptypes.getPClass(falsemd.castatypes[0]) == null) {
            throw new IllegalArgumentException();
        }

        return null;
    }

    @Override
    public Void visitNull(final NullContext ctx) {
        final Metadata nullmd = getMetadata(ctx);

        if (ctx.NULL() == null) {
            throw new IllegalStateException();
        }

        nullmd.castnodes = new ParseTree[] {ctx};
        nullmd.castatypes = new Type[] {getType("Ljava/lang/Object;")};

        if (ptypes.getPClass(nullmd.castatypes[0]) == null) {
            throw new IllegalArgumentException();
        }

        return null;
    }

    @Override
    public Void visitExt(final ExtContext ctx) {
        final Metadata extmd = getMetadata(ctx);

        final ExtstartContext ectx = ctx.extstart();
        final Metadata extstartmd = createMetadata(ectx);
        visit(ectx);
        //TODO: mark read variable

        extmd.statement = extstartmd.statement;
        extmd.castnodes = extstartmd.castnodes;
        extmd.castatypes = extstartmd.castatypes;

        return null;
    }

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final Metadata unarymd = getMetadata(ctx);
        unarymd.castnodes = new ParseTree[] {ctx};
        unarymd.castatypes = new Type[1];

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ectx);
        visit(ectx);

        if (ctx.BOOLNOT() != null) {
            if (expressionmd.constant instanceof Boolean) {
                unarymd.constant = !((Boolean)expressionmd.constant);
            } else {
                markCast(expressionmd, BOOLEAN_TYPE, false);
            }

            unarymd.castatypes[0] = BOOLEAN_TYPE;
        } else if (ctx.BWNOT() != null) {
            if (expressionmd.constant instanceof Byte) {
                unarymd.constant = ~((byte)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Character) {
                unarymd.constant = ~((char)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Short) {
                unarymd.constant = ~((short)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Integer) {
                unarymd.constant = ~((int)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Long) {
                unarymd.constant = ~((long)expressionmd.constant);
                unarymd.castatypes[0] = LONG_TYPE;
            } else if (expressionmd.constant instanceof Float) {
                throw new IllegalArgumentException();
            } else if (expressionmd.constant instanceof Double) {
                throw new IllegalArgumentException();
            } else {
                unarymd.castatypes[0] = getPromotion(new Metadata[] {expressionmd}, false);
                markCast(expressionmd, unarymd.castatypes[0], false);
            }
        } else if (ctx.SUB() != null) {
            if (expressionmd.constant instanceof Byte) {
                unarymd.constant = -((byte)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Character) {
                unarymd.constant = -((char)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Short) {
                unarymd.constant = -((short)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Integer) {
                unarymd.constant = -((int)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Long) {
                unarymd.constant = -((long)expressionmd.constant);
                unarymd.castatypes[0] = LONG_TYPE;
            } else if (expressionmd.constant instanceof Float) {
                unarymd.constant = -((float)expressionmd.constant);
                unarymd.castatypes[0] = FLOAT_TYPE;
            } else if (expressionmd.constant instanceof Double) {
                unarymd.constant = -((double)expressionmd.constant);
                unarymd.castatypes[0] = DOUBLE_TYPE;
            } else {
                unarymd.castatypes[0] = getPromotion(new Metadata[] {expressionmd}, true);
                markCast(expressionmd, unarymd.castatypes[0], false);
            }
        } else if (ctx.ADD() != null) {
            if (expressionmd.constant instanceof Byte) {
                unarymd.constant = +((byte)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Character) {
                unarymd.constant = +((char)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Short) {
                unarymd.constant = +((short)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Integer) {
                unarymd.constant = +((int)expressionmd.constant);
                unarymd.castatypes[0] = INT_TYPE;
            } else if (expressionmd.constant instanceof Long) {
                unarymd.constant = +((long)expressionmd.constant);
                unarymd.castatypes[0] = LONG_TYPE;
            } else if (expressionmd.constant instanceof Float) {
                unarymd.constant = +((float)expressionmd.constant);
                unarymd.castatypes[0] = FLOAT_TYPE;
            } else if (expressionmd.constant instanceof Double) {
                unarymd.constant = +((double)expressionmd.constant);
                unarymd.castatypes[0] = DOUBLE_TYPE;
            } else {
                unarymd.castatypes[0] = getPromotion(new Metadata[] {expressionmd}, true);
                markCast(expressionmd, unarymd.castatypes[0], false);
            }
        } else {
            throw new IllegalStateException();
        }

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final Metadata castmd = getMetadata(ctx);
        castmd.castnodes = new ParseTree[] {ctx};
        castmd.castatypes = new Type[1];

        final DecltypeContext dctx = ctx.decltype();
        final Metadata decltypemd = createMetadata(dctx);
        final Type adecltype = decltypemd.adecltype;
        castmd.castatypes = new Type[] {adecltype};

        final ExpressionContext ectx = ctx.expression();
        final Metadata expressionmd = createMetadata(ectx);
        expressionmd.righthand = castmd.righthand;
        visit(ectx);

        if (expressionmd.constant instanceof Number) {
            final int adeclsort = adecltype.getSort();

            if (adeclsort == BYTE) {
                castmd.constant = ((Number)expressionmd.constant).byteValue();
            } else if (adeclsort == SHORT) {
                castmd.constant = ((Number)expressionmd.constant).shortValue();
            } else if (adeclsort == Type.CHAR) {
                castmd.constant = (char)((Number)expressionmd.constant).longValue();
            }else if (adeclsort == INT) {
                castmd.constant = ((Number)expressionmd.constant).intValue();
            } else if (adeclsort == LONG) {
                castmd.constant = ((Number)expressionmd.constant).longValue();
            } else if (adeclsort == FLOAT) {
                castmd.constant = ((Number)expressionmd.constant).floatValue();
            } else if (adeclsort == DOUBLE) {
                castmd.constant = ((Number)expressionmd.constant).doubleValue();
            }
        } else if (expressionmd.constant instanceof Character) {
            final int adeclsort = adecltype.getSort();

            if (adeclsort == BYTE) {
                castmd.constant = (byte)(char)expressionmd.constant;
            } else if (adeclsort == SHORT) {
                castmd.constant = (short)(char)expressionmd.constant;
            } else if (adeclsort == Type.CHAR) {
                castmd.constant = expressionmd.constant;
            }else if (adeclsort == INT) {
                castmd.constant = (int)(char)expressionmd.constant;
            } else if (adeclsort == LONG) {
                castmd.constant = (long)(char)expressionmd.constant;
            } else if (adeclsort == FLOAT) {
                castmd.constant = (float)(char)expressionmd.constant;
            } else if (adeclsort == DOUBLE) {
                castmd.constant = (double)(char)expressionmd.constant;
            }
        } else {
            markCast(expressionmd, adecltype, true);
        }

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final Metadata binarymd = getMetadata(ctx);
        binarymd.castnodes = new ParseTree[] {ctx};
        binarymd.castatypes = new Type[1];

        final ExpressionContext ectx0 = ctx.expression(0);
        final Metadata expressionmd0 = createMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final Metadata expressionmd1 = createMetadata(ectx1);
        visit(ectx1);

        final boolean decimal = ctx.ADD() != null || ctx.SUB() != null ||
                ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null;
        binarymd.castatypes[0] = getPromotion(new Metadata[] {expressionmd0, expressionmd1}, decimal);
        final int asort = binarymd.castatypes[0].getSort();
        Number number0 = null;
        Number number1 = null;

        if (expressionmd0.constant instanceof Number) {
            number0 = (Number)expressionmd0.constant;
        } else if (expressionmd0.constant instanceof Character) {
            number0 = getNFromC((char)expressionmd0.constant, binarymd.castatypes[0]);
        }

        if (expressionmd1.constant instanceof Number) {
            number1 = (Number)expressionmd1.constant;
        } else if (expressionmd0.constant instanceof Character) {
            number1 = getNFromC((char)expressionmd1.constant, binarymd.castatypes[0]);
        }

        if (number0 != null && number1 != null) {
            if (ctx.MUL() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() * number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() * number1.longValue();
                } else if (asort == FLOAT) {
                    binarymd.constant = number0.floatValue() * number1.floatValue();
                } else if (asort == DOUBLE) {
                    binarymd.constant = number0.doubleValue() * number1.doubleValue();
                }
            } else if (ctx.DIV() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() / number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() / number1.longValue();
                } else if (asort == FLOAT) {
                    binarymd.constant = number0.floatValue() / number1.floatValue();
                } else if (asort == DOUBLE) {
                    binarymd.constant = number0.doubleValue() / number1.doubleValue();
                }
            } else if (ctx.REM() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() % number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() % number1.longValue();
                } else if (asort == FLOAT) {
                    binarymd.constant = number0.floatValue() % number1.floatValue();
                } else if (asort == DOUBLE) {
                    binarymd.constant = number0.doubleValue() % number1.doubleValue();
                }
            } else if (ctx.ADD() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() + number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() + number1.longValue();
                } else if (asort == FLOAT) {
                    binarymd.constant = number0.floatValue() + number1.floatValue();
                } else if (asort == DOUBLE) {
                    binarymd.constant = number0.doubleValue() + number1.doubleValue();
                }
            }  else if (ctx.SUB() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() - number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() - number1.longValue();
                } else if (asort == FLOAT) {
                    binarymd.constant = number0.floatValue() - number1.floatValue();
                } else if (asort == DOUBLE) {
                    binarymd.constant = number0.doubleValue() - number1.doubleValue();
                }
            } else if (ctx.LSH() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() << number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() << number1.longValue();
                }
            } else if (ctx.RSH() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() >> number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() >> number1.longValue();
                }
            } else if (ctx.USH() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() >>> number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() >>> number1.longValue();
                }
            } else if (ctx.BWAND() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() & number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() & number1.longValue();
                }
            } else if (ctx.BWXOR() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() ^ number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() ^ number1.longValue();
                }
            } else if (ctx.BWOR() != null) {
                if (asort == INT) {
                    binarymd.constant = number0.intValue() | number1.intValue();
                } else if (asort == LONG) {
                    binarymd.constant = number0.longValue() | number1.longValue();
                }
            } else {
                throw new IllegalStateException();
            }
        } else {
            markCast(expressionmd0, binarymd.castatypes[0], false);
            markCast(expressionmd1, binarymd.castatypes[0], false);
        }

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final Metadata compmd = getMetadata(ctx);
        compmd.castnodes = new ParseTree[] {ctx};
        compmd.castatypes = new Type[] {Type.BOOLEAN_TYPE};

        final ExpressionContext ectx0 = ctx.expression(0);
        final Metadata expressionmd0 = createMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final Metadata expressionmd1 = createMetadata(ectx1);
        visit(ectx1);

        if (isNumeric(expressionmd0.castatypes) && isNumeric(expressionmd1.castatypes)) {
            final Type apromote = getPromotion(new Metadata[]{expressionmd0, expressionmd1}, true);
            final int asort = apromote.getSort();
            Number number0 = null;
            Number number1 = null;

            if (expressionmd0.constant instanceof Number) {
                number0 = (Number) expressionmd0.constant;
            } else if (expressionmd0.constant instanceof Character) {
                number0 = getNFromC((char)expressionmd0.constant, apromote);
            }

            if (expressionmd1.constant instanceof Number) {
                number1 = (Number) expressionmd1.constant;
            } else if (expressionmd0.constant instanceof Character) {
                number1 = getNFromC((char)expressionmd1.constant, apromote);
            }

            if (number0 != null && number1 != null) {
                if (ctx.EQ() != null) {
                    if (asort == INT) {
                        compmd.constant = number0.intValue() == number1.intValue();
                    } else if (asort == LONG) {
                        compmd.constant = number0.longValue() == number1.longValue();
                    } else if (asort == FLOAT) {
                        compmd.constant = number0.floatValue() == number1.floatValue();
                    } else if (asort == DOUBLE) {
                        compmd.constant = number0.doubleValue() == number1.doubleValue();
                    }
                } else if (ctx.NE() != null) {
                    if (asort == INT) {
                        compmd.constant = number0.intValue() != number1.intValue();
                    } else if (asort == LONG) {
                        compmd.constant = number0.longValue() != number1.longValue();
                    } else if (asort == FLOAT) {
                        compmd.constant = number0.floatValue() != number1.floatValue();
                    } else if (asort == DOUBLE) {
                        compmd.constant = number0.doubleValue() != number1.doubleValue();
                    }
                } else if (ctx.GTE() != null) {
                    if (asort == INT) {
                        compmd.constant = number0.intValue() >= number1.intValue();
                    } else if (asort == LONG) {
                        compmd.constant = number0.longValue() >= number1.longValue();
                    } else if (asort == FLOAT) {
                        compmd.constant = number0.floatValue() >= number1.floatValue();
                    } else if (asort == DOUBLE) {
                        compmd.constant = number0.doubleValue() >= number1.doubleValue();
                    }
                } else if (ctx.GT() != null) {
                    if (asort == INT) {
                        compmd.constant = number0.intValue() > number1.intValue();
                    } else if (asort == LONG) {
                        compmd.constant = number0.longValue() > number1.longValue();
                    } else if (asort == FLOAT) {
                        compmd.constant = number0.floatValue() > number1.floatValue();
                    } else if (asort == DOUBLE) {
                        compmd.constant = number0.doubleValue() > number1.doubleValue();
                    }
                } else if (ctx.LTE() != null) {
                    if (asort == INT) {
                        compmd.constant = number0.intValue() <= number1.intValue();
                    } else if (asort == LONG) {
                        compmd.constant = number0.longValue() <= number1.longValue();
                    } else if (asort == FLOAT) {
                        compmd.constant = number0.floatValue() <= number1.floatValue();
                    } else if (asort == DOUBLE) {
                        compmd.constant = number0.doubleValue() <= number1.doubleValue();
                    }
                } else if (ctx.LT() != null) {
                    if (asort == INT) {
                        compmd.constant = number0.intValue() < number1.intValue();
                    } else if (asort == LONG) {
                        compmd.constant = number0.longValue() < number1.longValue();
                    } else if (asort == FLOAT) {
                        compmd.constant = number0.floatValue() < number1.floatValue();
                    } else if (asort == DOUBLE) {
                        compmd.constant = number0.doubleValue() < number1.doubleValue();
                    }
                } else {
                    throw new IllegalStateException();
                }
            } else {
                markCast(expressionmd0, Type.BOOLEAN_TYPE, false);
                markCast(expressionmd1, Type.BOOLEAN_TYPE, false);
            }

        } else if (isType(expressionmd0.castatypes, Type.BOOLEAN_TYPE, true) &&
                isType(expressionmd1.castatypes, Type.BOOLEAN_TYPE, true)) {
            if (expressionmd0.constant != null && expressionmd1.constant != null) {
                if (ctx.EQ() != null) {
                    compmd.constant = (boolean)expressionmd0.constant == (boolean)expressionmd1.constant;
                } else if (ctx.NE() != null) {
                    compmd.constant = (boolean)expressionmd0.constant != (boolean)expressionmd1.constant;
                } else {
                    throw new IllegalStateException();
                }
            } else {
                markCast(expressionmd0, Type.BOOLEAN_TYPE, false);
                markCast(expressionmd1, Type.BOOLEAN_TYPE, false);
            }
        } else {
            if (expressionmd0.constant != null && expressionmd1.constant != null) {
                if (ctx.EQ() != null) {
                    compmd.constant = expressionmd0.constant == expressionmd1.constant;
                } else if (ctx.NE() != null) {
                    compmd.constant = expressionmd0.constant != expressionmd1.constant;
                } else {
                    throw new IllegalStateException();
                }
            } else {
                markCast(expressionmd0, Type.BOOLEAN_TYPE, false);
                markCast(expressionmd1, Type.BOOLEAN_TYPE, false);
            }
        }

        return null;
    }

    @Override
    public Void visitBool(BoolContext ctx) {
        final Metadata boolmd = getMetadata(ctx);
        boolmd.castnodes = new ParseTree[] {ctx};
        boolmd.castatypes = new Type[] {Type.BOOLEAN_TYPE};

        final ExpressionContext ectx0 = ctx.expression(0);
        final Metadata expressionmd0 = createMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final Metadata expressionmd1 = createMetadata(ectx1);
        visit(ectx1);

        if (isType(expressionmd0.castatypes, Type.BOOLEAN_TYPE, true) &&
                isType(expressionmd1.castatypes, Type.BOOLEAN_TYPE, true)) {
            if (expressionmd0.constant != null && expressionmd1.constant != null) {
                if (ctx.BOOLAND() != null) {
                    boolmd.constant = (boolean)expressionmd0.constant && (boolean)expressionmd1.constant;
                } else if (ctx.BOOLOR() != null) {
                    boolmd.constant = (boolean)expressionmd0.constant || (boolean)expressionmd1.constant;
                } else {
                    throw new IllegalStateException();
                }
            } else {
                markCast(expressionmd0, Type.BOOLEAN_TYPE, false);
                markCast(expressionmd1, Type.BOOLEAN_TYPE, false);
            }
        }

        return null;
    }

    @Override
    public Void visitConditional(PainlessParser.ConditionalContext ctx) {
        final Metadata conditionalmd = getMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final Metadata expressionmd0 = createMetadata(ectx0);
        visit(ectx0);

        if (expressionmd0.constant instanceof Boolean) {
            conditionalmd.constant = expressionmd0.constant;
        }

        final ExpressionContext ectx1 = ctx.expression(1);
        final Metadata expressionmd1 = createMetadata(ectx1);
        visit(ectx1);

        final ExpressionContext ectx2 = ctx.expression(2);
        final Metadata expressionmd2 = createMetadata(ectx2);
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
            conditionalmd.castatypes[0] = expressionmd1.castatypes[0];
        } else if (constant1) {
            conditionalmd.constant = expressionmd2.constant;
            conditionalmd.castatypes[0] = expressionmd2.castatypes[0];
        } else {
            final int nodeslen1 = expressionmd1.castnodes.length;
            final int nodeslen2 = expressionmd2.castnodes.length;
            conditionalmd.castnodes = new ParseTree[nodeslen1 + nodeslen2];
            System.arraycopy(expressionmd1.castnodes, 0, conditionalmd.castnodes, 0, nodeslen1);
            System.arraycopy(expressionmd2.castnodes, 0, conditionalmd.castnodes, nodeslen1, nodeslen2);

            final int castslen1 = expressionmd1.castatypes.length;
            final int castslen2 = expressionmd2.castatypes.length;
            conditionalmd.castnodes = new ParseTree[castslen1 + castslen2];
            System.arraycopy(expressionmd1.castatypes, 0, conditionalmd.castatypes, 0, castslen1);
            System.arraycopy(expressionmd2.castatypes, 0, conditionalmd.castatypes, castslen1, castslen2);

            conditionalmd.conditional = true;
        }

        return null;
    }

    @Override
    public Void visitAssignment(final AssignmentContext ctx) {
        final Metadata assignmentmd = getMetadata(ctx);

        final ExtstartContext ectx0 = ctx.extstart();
        Metadata extstartmd = createMetadata(ectx0);
        visit(ectx0);

        if (extstartmd.statik) {
            throw new IllegalArgumentException();
        }

        if (extstartmd.statement) {
            throw new IllegalArgumentException();
        }

        if (assignmentmd.righthand) {
            assignmentmd.castnodes = new ParseTree[] {extstartmd.castnodes[0]};
            assignmentmd.castatypes = new Type[] {extstartmd.castatypes[0]};
        }

        final ExpressionContext ectx1 = ctx.expression();
        final Metadata expressionmd = createMetadata(ectx1);
        expressionmd.righthand = true;
        visit(ectx1);

        assignmentmd.statement = true;

        return null;
    }

    @Override
    public Void visitExtstart(ExtstartContext ctx) {
        final Metadata extstartmd = getMetadata(ctx);
        extstartmd.external = new ArrayDeque<>();

        Metadata extmd;

        ExtprecContext ectx0 = ctx.extprec();
        ExtcastContext ectx1 = ctx.extcast();
        ExttypeContext ectx2 = ctx.exttype();
        ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            extmd = createMetadata(ectx0);
            visit(ectx0);
        } else if (ectx1 != null) {
            extmd = createMetadata(ectx1);
            visit(ectx1);
        } else if (ectx2 != null) {
            extmd = createMetadata(ectx2);
            visit(ectx2);
        } else if (ectx3 != null) {
            extmd = createMetadata(ectx3);
            visit(ectx3);
        } else {
            throw new IllegalStateException();
        }

        extstartmd.statement = extmd.statement;
        extstartmd.statik = extmd.statik;
        extstartmd.castnodes = new ParseTree[] {extmd.castnodes[0]};
        extstartmd.castatypes = new Type[] {extmd.castatypes[0]};

        return null;
    }

    /*@Override
    public Void visitExtprec(ExtprecContext ctx) {
        final Metadata extprecmd = getMetadata(ctx);

        Metadata extmd0;

        ExtprecContext ectx0 = ctx.extprec();
        ExtcastContext ectx1 = ctx.extcast();
        ExttypeContext ectx2 = ctx.exttype();
        ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            extmd0 = createMetadata(ectx0);
            visit(ectx0);
        } else if (ectx1 != null) {
            extmd0 = createMetadata(ectx1);
            visit(ectx1);
        } else if (ectx2 != null) {
            extmd0 = createMetadata(ectx2);
            visit(ectx2);
        } else if (ectx3 != null) {
            extmd0 = createMetadata(ectx3);
            visit(ectx3);
        } else {
            throw new IllegalStateException();
        }

        Metadata extmd1;

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
        if (parentatype.getSort() != ARRAY) {
            throw new IllegalArgumentException();
        }

        Extracted extexpr = visit(ctx.expression());
        markCast(extexpr, INT_TYPE, false);

        final Type atype = getType(parentatype.getDescriptor().substring(1));

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

        if (atype.getSort() == ARRAY) {
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
            visitArguments(ctx.arguments(), new Type[]{INT_TYPE}, true);
            String arraytype = "";

            for (int bracket = 0; bracket < arguments; ++bracket) {
                arraytype += "[";
            }

            arraytype += parentatype.getDescriptor();
            atype = getType(arraytype);
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
            if (parentatype.getSort() == ARRAY) {
                if ("length".equals(vname)) {
                    writeable = false;
                    atype = INT_TYPE;
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
    }*/
}
