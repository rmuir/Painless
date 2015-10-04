package painless;


import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayDeque;
import java.util.Arrays;
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

    private class FieldSegment extends Segment {
        private final Field field;

        FieldSegment(final Field field) {
            this.field = field;
        }

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

    private class NodeSegment extends Segment {
        private final ParseTree node;

        NodeSegment(final ParseTree node) {
            this.node = node;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class ArraySegment extends Segment {
        private final Type type;

        ArraySegment(final Type type) {
            this.type = type;
        }

        @Override
        void write(MethodVisitor visitor) {

        }
    }

    private class MakeSegment extends Segment {
        private final Type type;

        MakeSegment(final Type type) {
            this.type = type;
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

        current = to;

        if (object instanceof Cast) {
            segments.add(new CastSegment((Cast)object));
        } else if (object instanceof Transform) {
            segments.add(new TransformSegment((Transform)object));
        } else {
            throw new IllegalStateException(); // TODO: message
        }
    }

    public void brace(final ExtbraceContext ctx) {
        final ExpressionContext exprctx = ctx.expression();
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        final Struct struct = current.struct;

        if (current.dimensions > 0) {
            expremd.to = standard.intType;
            analyzer.visit(exprctx);
            current = getTypeWithArrayDimensions(current.struct, current.dimensions - 1);
            segments.add(new NodeSegment(exprctx));
            segments.add(new ArraySegment(current));
        } else {
            expremd.promotions = caster.brace;
            analyzer.visit(exprctx);

            final boolean list = expremd.from.metadata.numeric;
            expremd.to = list ? standard.intType : standard.objectType;
            caster.markCast(expremd);
            segments.add(new NodeSegment(exprctx));

            final Object object = caster.getLegalCast(current, list ? standard.listType : standard.mapType, true, true);

            if (object instanceof Cast) {
                segments.add(new CastSegment((Cast)object));
            } else if (object instanceof Transform) {
                segments.add(new CastSegment((Cast)object));
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (list) {
                /*final Struct pclass = object == null ? current : pstandard.plist.getPClass();
                final PMethod read = pclass.getPMethod("get");
                final PMethod write = pclass.getPMethod("add");

                if (read == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                if (write == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                pexternal.addSegment(SType.SHORTCUT, read, write);*/
            } else {
                /*final Struct pclass = object == null ? ptype.getPClass() : pstandard.pmap.getPClass();
                final PMethod read = pclass.getPMethod("get");
                final PMethod write = pclass.getPMethod("put");

                if (read == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                if (write == null) {
                    throw new IllegalArgumentException(); // TOOD: message
                }

                pexternal.addSegment(SType.SHORTCUT, read, write);*/
            }
        }

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        if (dotctx != null) {
            dot(dotctx);
        } else if (bracectx != null) {
            brace(bracectx);
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
        final String name = ctx.ID().getText();
        final Struct struct = current.struct;

        final List<ExpressionContext> arguments = ctx.arguments().expression();
        Type[] types;

        Segment segment;

        if (current.metadata == TypeMetadata.ARRAY) {
            throw new IllegalArgumentException(); // TODO : message
        }

        if (statik && "makearray".equals(name)) {
            types = new Type[arguments.size()];
            Arrays.fill(types, standard.intType);
            current = getTypeWithArrayDimensions(struct, arguments.size());
            segment = new MakeSegment(current);
        } else {
            final Constructor constructor = statik ? struct.constructors.get(name) : null;
            final Method method = statik ? struct.functions.get(name) : struct.methods.get(name);

            if (constructor != null) {
                types = new Type[constructor.arguments.size()];
                constructor.arguments.toArray(types);
                segment = new ConstructorSegment(constructor);
            } else if (method != null) {
                types = new Type[method.arguments.size()];
                method.arguments.toArray(types);
                current = method.rtn;
                segment = new MethodSegment(method);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        if (arguments.size() != types.length) {
            throw new IllegalArgumentException(); // TODO: message
        }

        for (int argument = 0; argument < arguments.size(); ++argument) {
            final ParseTree exprctx = arguments.get(argument);
            final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
            expremd.to = types[argument];
            analyzer.visit(exprctx);

            segments.add(new NodeSegment(exprctx));
        }

        segments.add(segment);

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        if (dotctx != null) {
            dot(dotctx);
        } else if (bracectx != null) {
            brace(bracectx);
        }
    }

    public void member(final ExtmemberContext ctx) {
        final String name = ctx.ID().getText();

        if (current == null) {
            final Variable variable = adapter.getVariable(name);

            if (variable == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            current = variable.type;
            segments.add(new VariableSegment(variable));
        } else {
            if (current.metadata == TypeMetadata.ARRAY) {
                if ("length".equals(name)) {
                    current = standard.intType;
                    segments.add(new LengthSegment());
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                final Struct struct = current.struct;
                final Field field = statik ? struct.statics.get(name) : struct.members.get(name);

                if (field == null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                current = field.type;
                segments.add(new FieldSegment(field));
            }
        }

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        if (dotctx != null) {
            dot(dotctx);
        } else if (bracectx != null) {
            brace(bracectx);
        }
    }
}
