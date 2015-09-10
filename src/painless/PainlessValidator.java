package painless;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Type;

import static painless.PainlessParser.*;

class PainlessValidator extends PainlessBaseVisitor<PainlessValidator.Extracted> {
    static final Type OBJECT_TYPE = Type.getType("Ljava/lang/Object;");
    static final Type STRING_TYPE = Type.getType("Ljava/lang/String;");

    static class Cast {
        Type from;
        Type to;
    }

    static void validate(ParseTree root) {
        new PainlessValidator(root);
    }

    class Extracted {
        ParseTree source;

        ParseTree node0 = null;
        ParseTree node1 = null;

        Type type0 = null;
        Type type1 = null;

        boolean statement = false;
        boolean jump = false;
        boolean rtn = false;

        Extracted(ParseTree source) {
            this.source = source;
        }

        void markCast(Type to, boolean explicit) {
        }
    }

    private int scope;
    private int loop;

    private Map<ParseTree, Cast> casts;

    private PainlessValidator(ParseTree root) {
        scope = 0;
        loop = 0;

        casts = new HashMap<>();

        visit(root);
    }

    @Override
    public Extracted visitSource(SourceContext ctx) {
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

        return extracted;
    }

    @Override
    public Extracted visitIf(IfContext ctx) {
        ++scope;

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

        --scope;

        return extracted;
    }

    @Override
    public Extracted visitWhile(WhileContext ctx) {
        ++scope;
        ++loop;

        ExpressionContext ectx = ctx.expression();
        Extracted extexpr = visit(ectx);
        extexpr.markCast(Type.BOOLEAN_TYPE, false);

        visit(ctx.block());

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        --loop;
        --scope;

        return extracted;
    }

    @Override
    public Extracted visitDo(DoContext ctx) {
        ++scope;
        ++loop;

        visit(ctx.block());

        ExpressionContext ectx = ctx.expression();
        Extracted extexpr = visit(ectx);
        extexpr.markCast(Type.BOOLEAN_TYPE, false);

        Extracted extracted = new Extracted(ctx);
        extracted.statement = true;

        --loop;
        --scope;

        return extracted;
    }

    @Override
    public Extracted visitFor(ForContext ctx) {
        ++scope;
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
        --scope;

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
    public Object visitDeclaration(PainlessParser.DeclarationContext ctx) {
        return null;
    }

    @Override
    public Object visitDecltype(PainlessParser.DecltypeContext ctx) {
        return null;
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
