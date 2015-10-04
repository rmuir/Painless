package painless;


import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static painless.Adapter.*;
import static painless.Default.*;
import static painless.Definition.*;
import static painless.PainlessParser.*;

class External {
    private abstract class Segment {
        abstract void write(MethodVisitor vistor);
    }

    private class VariableSegment extends Segment {
        private final Variable variable;

        VariableSegment(final Variable variable) {
            this.variable = variable;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class ArraySegment extends Segment {
        private final Variable variable;

        ArraySegment(final Variable variable) {
            this.variable = variable;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class LengthSegment extends Segment {
        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class ConstructorSegment extends Segment {
        private final Constructor constructor;

        ConstructorSegment(final Constructor constructor) {
            this.constructor = constructor;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class MethodSegment extends Segment {
        private final Method method;

        MethodSegment(final Method method) {
            this.method = method;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class CastSegment extends Segment {
        private final Cast cast;

        CastSegment(final Cast cast) {
            this.cast = cast;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class TransformSegment extends Segment {
        private final Transform transform;

        TransformSegment(final Transform transform) {
            this.transform = transform;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }
    
    private class Pop extends Segment {
        private final int size;

        Pop(final int size) {
            this.size = size;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private final Analyzer analyzer;
    private final Adapter adapter;
    private final Definition definition;
    private final Standard standard;
    private final Caster caster;

    private boolean read;
    private ParseTree write;
    private boolean post;
    private boolean pre;

    private int prec;
    private Type current;
    private boolean statik;

    private final Deque<Segment> segments;

    External(final Analyzer analyzer, final Adapter adapter) {
        this.analyzer = analyzer;
        this.adapter = adapter;
        definition = adapter.definition;
        standard = adapter.standard;
        caster = adapter.caster;

        read = false;
        write = null;
        post = false;
        pre = false;

        prec = 0;
        current = null;
        statik = false;

        segments = new ArrayDeque<>();
    }

    void write(MethodVisitor visitor) {
        for (Segment segment : segments) {
            segment.write(visitor);
        }
    }

    void ext(ExtContext ctx) {
        final ExpressionMetadata extemd = adapter.getExpressionMetadata(ctx);

        read = extemd.to.metadata != TypeMetadata.VOID;
        start(ctx.extstart());

        final Segment last = segments.getLast();
        extemd.statement = last instanceof ConstructorSegment || last instanceof MethodSegment;
        extemd.from = read ? current : standard.voidType;
        caster.markCast(extemd);
    }

    void assignment(AssignmentContext ctx) {
        final ExpressionMetadata assignemd = adapter.getExpressionMetadata(ctx);

        read = assignemd.to.metadata != TypeMetadata.VOID;
        write = ctx.expression();

        start(ctx.extstart());

        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata epxremd = adapter.createExpressionMetadata(exprctx);
        epxremd.to = current;
        analyzer.visit(exprctx);

        assignemd.from = read ? current : standard.voidType;
        assignemd.statement = true;
        caster.markCast(assignemd);
    }

    private void start(final ExtstartContext startctx) {
        final ExtprecContext precctx = startctx.extprec();
        final ExtcastContext castctx = startctx.extcast();
        final ExttypeContext typectx = startctx.exttype();
        final ExtmemberContext memberctx = startctx.extmember();

        if (precctx != null) {
            prec(precctx);
        } else if (castctx != null) {
            cast(castctx);
        } else if (typectx != null) {
            type(typectx);
        } else if (memberctx != null) {
            member(memberctx);
        } else {
            throw new IllegalStateException(); // TODO: message
        }
    }

    public void prec(final ExtprecContext ctx) {
        final ExtprecContext precctx = ctx.extprec();
        final ExtcastContext castctx = ctx.extcast();
        final ExttypeContext typectx = ctx.exttype();
        final ExtmemberContext memberctx = ctx.extmember();
        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        if (dotctx != null || bracectx != null) {
            ++prec;
        }

        if (precctx != null) {
            prec(precctx);
        } else if (castctx != null) {
            cast(castctx);
        } else if (typectx != null) {
            type(typectx);
        } else if (memberctx != null) {
            member(memberctx);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        if (dotctx != null) {
            dot(dotctx);
            --prec;
        } else if (bracectx != null) {
            brace(bracectx);
            --prec;
        }
    }

    public void cast(final ExtcastContext ctx) {
        final ExtprecContext precctx = ctx.extprec();
        final ExtcastContext castctx = ctx.extcast();
        final ExttypeContext typectx = ctx.exttype();
        final ExtmemberContext memberctx = ctx.extmember();

        if (precctx != null) {
            prec(precctx);
        } else if (castctx != null) {
            cast(castctx);
        } else if (typectx != null) {
            type(typectx);
        } else if (memberctx != null) {
            member(memberctx);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        final DecltypeContext declctx = ctx.decltype();
        final ExpressionMetadata declemd = adapter.createExpressionMetadata(declctx);
        analyzer.visit(declctx);

        final Type from = current;
        final Type to = declemd.to;

        final Object object = caster.getLegalCast(from, to, true, false);

        if (object instanceof Cast) {
            segments.add(new CastSegment((Cast)object));
        } else if (object instanceof Transform) {
            segments.add(new TransformSegment((Transform)object));
        } else {
            throw new IllegalStateException(); // TODO: message
        }
    }

    public void brace(final ExtbraceContext ctx) {
        final PMetadata extbrace = getPMetadata(ctx);
        final PExternal pexternal = extbrace.getPExternal();
        final PType ptype = pexternal.getPType();

        final ExpressionContext ectx0 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx0);

        final ExtdotContext ectx1 = ctx.extdot();
        final ExtbraceContext ectx2 = ctx.extbrace();

        if (ptype.getPDimensions() > 0) {
            expressionmd.toptype = pstandard.pint;
            visit(ectx0);

            pexternal.addSegment(SType.NODE, ectx0, null);
            pexternal.addSegment(SType.ARRAY, null, null);
        } else {
            expressionmd.anyptype = true;
            visit(ectx0);

            final PSort psort = expressionmd.getFromPType().getPSort();
            final boolean numeric = psort.isPNumeric();
            expressionmd.toptype = numeric ? pstandard.pint : expressionmd.toptype;
            markCast(expressionmd);

            final Object object = getLegalCast(ptype, numeric ? pstandard.plist : pstandard.pmap, true, false);

            if (object instanceof PCast) {
                pexternal.addSegment(SType.CAST, object, null);
            } else if (object instanceof PTransform) {
                pexternal.addSegment(SType.TRANSFORM, object, null);
            }

            pexternal.addSegment(SType.NODE, ectx0, null);

            if (numeric) {
                final Struct pclass = object == null ? ptype.getPClass() : pstandard.plist.getPClass();
                final PMethod read = pclass.getPMethod("get");
                final PMethod write = pclass.getPMethod("add");

                if (read == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                if (write == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                pexternal.addSegment(SType.SHORTCUT, read, write);
            } else {
                final Struct pclass = object == null ? ptype.getPClass() : pstandard.pmap.getPClass();
                final PMethod read = pclass.getPMethod("get");
                final PMethod write = pclass.getPMethod("put");

                if (read == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                if (write == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                pexternal.addSegment(SType.SHORTCUT, read, write);
            }
        }

        if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extbrace.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata extarraymd1 = createPMetadata(ectx2);
            extarraymd1.pexternal = extbrace.pexternal;
            visit(ectx2);
        }
    }

    public void dot(final ExtdotContext ctx) {
        final ExtcallContext callctx = ctx.extcall();
        final ExtmemberContext memberctx = ctx.extmember();

        if (callctx != null) {
            call(callctx);
        } else if (memberctx != null) {
            member(memberctx);
        }
    }

    public void type(final ExttypeContext ctx) {
        if (current != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String typestr = ctx.ID().getText();
        current = getTypeFromCanonicalName(definition, typestr);
        statik = true;

        final ExtdotContext dotctx = ctx.extdot();
        dot(dotctx);
    }

    public void call(final ExtcallContext ctx) {
        final PMetadata extcallmd = getPMetadata(ctx);
        final PType declptype = extcallmd.pexternal.getPType();
        final Struct pclass = declptype.getPClass();

        if (pclass == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pname = ctx.ID().getText();
        final boolean statik = extcallmd.pexternal.isStatic();
        final List<ExpressionContext> arguments = ctx.arguments().expression();
        SType stype;
        Object svalue;
        PType[] argumentsptypes;

        if (statik && "makearray".equals(pname)) {
            stype = SType.AMAKE;
            svalue = arguments.size();
            argumentsptypes = new PType[arguments.size()];
            Arrays.fill(argumentsptypes, pstandard.pint);
        } else {
            final Constructor pconstructor = statik ? pclass.getPConstructor(pname) : null;
            final PMethod pmethod = statik ? pclass.getPFunction(pname) : pclass.getPMethod(pname);

            if (pconstructor != null) {
                stype = SType.CONSTRUCTOR;
                svalue = pconstructor;
                argumentsptypes = new PType[pconstructor.getPArguments().size()];
                pconstructor.getPArguments().toArray(argumentsptypes);
            } else if (pmethod != null) {
                stype = SType.METHOD;
                svalue = pmethod;
                argumentsptypes = new PType[pmethod.getPArguments().size()];
                pmethod.getPArguments().toArray(argumentsptypes);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        if (arguments.size() != argumentsptypes.length) {
            throw new IllegalArgumentException(); // TODO: message
        }

        for (int argument = 0; argument < arguments.size(); ++argument) {
            final ParseTree ectx = arguments.get(argument);
            final PMetadata expressionmd = createPMetadata(ectx);
            expressionmd.toptype = argumentsptypes[argument];
            visit(ectx);

            extcallmd.pexternal.addSegment(SType.NODE, ectx, null);
        }

        extcallmd.pexternal.addSegment(stype, svalue, null);

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtbraceContext ectx1 = ctx.extbrace();

        if (ectx0 != null) {
            final PMetadata extdotmd = createPMetadata(ectx0);
            extdotmd.pexternal = extcallmd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extcallmd.pexternal;
            visit(ectx1);
        }
    }

    public void member(final ExtmemberContext ctx) {
        final String pname = ctx.ID().getText();

        if (current == null) {
            final Variable variable = adapter.getVariable(pname);

            if (variable == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            current = variable.type;
            segments.add(new VariableSegment(variable));
        } else {
            if (current.metadata == TypeMetadata.ARRAY) {
                if ("length".equals(pname)) {
                    segments.add(new LengthSegment());
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                final Struct struct = current.struct;

                final boolean statik = extmembermd.pexternal.isStatic();
                final PField pmember = statik ? pclass.getPStatic(pname) : pclass.getPMember(pname);

                if (pmember == null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                extmembermd.pexternal.addSegment(SType.FIELD, pmember, false);
            }
        }

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtbraceContext ectx1 = ctx.extbrace();

        if (ectx0 != null) {
            final PMetadata extdotmd = createPMetadata(ectx0);
            extdotmd.pexternal = extmembermd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extmembermd.pexternal;
            visit(ectx1);
        } else if (prec == 0) {

        }
    }
}
