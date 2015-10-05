package painless;


import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
        abstract void write();
    }

    private class VariableSegment extends Segment {
        private final Variable variable;
        private final boolean store;

        VariableSegment(final Variable variable, final boolean store) {
            this.variable = variable;
            this.store = store;
        }

        @Override
        void write() {
            final TypeMetadata metadata = variable.type.metadata;
            final int slot = variable.slot;

            switch (metadata) {
                case VOID:   throw new IllegalStateException();
                case BOOL:
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:    visitor.visitVarInsn(store ? Opcodes.ISTORE : Opcodes.ILOAD, slot); break;
                case LONG:   visitor.visitVarInsn(store ? Opcodes.LSTORE : Opcodes.LLOAD, slot); break;
                case FLOAT:  visitor.visitVarInsn(store ? Opcodes.FSTORE : Opcodes.FLOAD, slot); break;
                case DOUBLE: visitor.visitVarInsn(store ? Opcodes.DSTORE : Opcodes.DLOAD, slot); break;
                default:     visitor.visitVarInsn(store ? Opcodes.ASTORE : Opcodes.ALOAD, slot);
            }
        }
    }

    private class FieldSegment extends Segment {
        private final Field field;
        private final boolean store;

        FieldSegment(final Field field, final boolean store) {
            this.field = field;
            this.store = store;
        }

        @Override
        void write() {
            final String internal = field.owner.internal;
            final String name = field.field.getName();
            final String descriptor = field.type.descriptor;

            int opcode;

            if (java.lang.reflect.Modifier.isStatic(field.field.getModifiers())) {
                opcode = store ? Opcodes.PUTSTATIC : Opcodes.GETSTATIC;
            } else {
                opcode = store ? Opcodes.PUTFIELD : Opcodes.GETFIELD;
            }

            visitor.visitFieldInsn(opcode, internal, name, descriptor);
        }
    }

    private class NewSegment extends Segment {
        private final Struct struct;

        NewSegment(final Struct struct) {
            this.struct = struct;
        }

        @Override
        void write() {
            final String internal = struct.internal;
            visitor.visitTypeInsn(Opcodes.NEW, internal);
        }
    }

    private class ConstructorSegment extends Segment {
        private final Constructor constructor;

        ConstructorSegment(final Constructor constructor) {
            this.constructor = constructor;
        }

        @Override
        void write() {
            final String internal = constructor.owner.internal;
            final String descriptor = constructor.descriptor;
            visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, internal, "<init>", descriptor, false);
        }
    }

    private class MethodSegment extends Segment {
        private final Method method;

        MethodSegment(final Method method) {
            this.method = method;
        }

        @Override
        void write() {
            final String internal = method.owner.internal;
            final String name = method.method.getName();
            final String descriptor = method.descriptor;

            if (java.lang.reflect.Modifier.isStatic(method.method.getModifiers())) {
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internal, name, descriptor, false);
            } else if (java.lang.reflect.Modifier.isInterface(method.owner.clazz.getModifiers())) {
                visitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, internal, name, descriptor, true);
            } else {
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, internal, name, descriptor, false);
            }
        }
    }

    private class ShortcutSegment extends Segment {
        private final Struct struct;
        private final org.objectweb.asm.commons.Method method;
        private final boolean statik;

        ShortcutSegment(final Struct struct, final java.lang.reflect.Method method) {
            this.struct = struct;
            this.method = org.objectweb.asm.commons.Method.getMethod(method);
            this.statik = java.lang.reflect.Modifier.isStatic(method.getModifiers());
        }

        @Override
        void write() {
            final String internal = struct.internal;
            final String name = method.getName();
            final String descriptor = method.getDescriptor();

            if (statik) {
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internal, name, descriptor, false);
            } else if (java.lang.reflect.Modifier.isInterface(struct.clazz.getModifiers())) {
                visitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, internal, name, descriptor, true);
            } else {
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, internal, name, descriptor, false);
            }
        }
    }

    private class NodeSegment extends Segment {
        private final ParseTree node;

        NodeSegment(final ParseTree node) {
            this.node = node;
        }

        @Override
        void write() {
            writer.visit(node);
        }
    }

    private class ArraySegment extends Segment {
        private final Type type;
        private final boolean store;

        ArraySegment(final Type type, final boolean store) {
            this.type = type;
            this.store = store;
        }

        @Override
        void write() {
            switch (type.metadata) {
                case BOOL:
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:    visitor.visitInsn(store ? Opcodes.IASTORE : Opcodes.IALOAD); break;
                case LONG:   visitor.visitInsn(store ? Opcodes.LASTORE : Opcodes.LALOAD); break;
                case FLOAT:  visitor.visitInsn(store ? Opcodes.FASTORE : Opcodes.FALOAD); break;
                case DOUBLE: visitor.visitInsn(store ? Opcodes.DASTORE : Opcodes.DALOAD); break;
                default:     visitor.visitInsn(store ? Opcodes.AASTORE : Opcodes.AALOAD); break;
            }
        }
    }

    private class MakeSegment extends Segment {
        private final Type type;
        private final int dimensions;

        MakeSegment(final Type type, final int dimensions) {
            this.type = type;
            this.dimensions = dimensions;
        }

        @Override
        void write() {
            if (dimensions == 1) {
                switch (type.metadata) {
                    case VOID:   throw new IllegalStateException(); // TODO: message
                    case BOOL:   visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN); break;
                    case SHORT:  visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_SHORT);   break;
                    case CHAR:   visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_CHAR);    break;
                    case INT:    visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);     break;
                    case LONG:   visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_LONG);    break;
                    case FLOAT:  visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_FLOAT);   break;
                    case DOUBLE: visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_DOUBLE);  break;
                    default:     visitor.visitTypeInsn(Opcodes.ANEWARRAY, type.internal);
                }
            } else if (dimensions > 0) {
                final String descriptor = getTypeWithArrayDimensions(type.struct, dimensions).descriptor;
                visitor.visitMultiANewArrayInsn(descriptor, dimensions);
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }
    }

    private class LengthSegment extends Segment {
        @Override
        void write() {
            visitor.visitInsn(Opcodes.ARRAYLENGTH);
        }
    }

    private class CastSegment extends Segment {
        private final Cast cast;

        CastSegment(final Cast cast) {
            this.cast = cast;
        }

        @Override
        void write() {
            caster.writeCast(visitor, cast);
        }
    }

    private class TransformSegment extends Segment {
        private final Transform transform;

        TransformSegment(final Transform transform) {
            this.transform = transform;
        }

        @Override
        void write() {
            caster.writeTransform(visitor, transform);
        }
    }

    private class InstructionSegment extends Segment {
        private final int instruction;

        InstructionSegment(final int instruction) {
            this.instruction = instruction;
        }

        @Override
        void write() {
            visitor.visitInsn(instruction);
        }
    }

    private final Adapter adapter;
    private final Definition definition;
    private final Standard standard;
    private final Caster caster;
    private final Analyzer analyzer;
    private Writer writer;
    private MethodVisitor visitor;

    private boolean read;
    private ParseTree write;
    private int token;
    private boolean post;
    private boolean pre;

    private int prec;
    private Type current;
    private boolean statik;
    private boolean statement;

    private final Deque<Segment> segments;

    External(final Adapter adapter, final Analyzer analyzer) {
        this.adapter = adapter;
        definition = adapter.definition;
        standard = adapter.standard;
        caster = adapter.caster;
        this.analyzer = analyzer;

        read = false;
        write = null;
        post = false;
        pre = false;

        prec = 0;
        current = null;
        statik = false;
        statement = false;

        segments = new ArrayDeque<>();
    }

    void setWriter(final Writer writer, final MethodVisitor visitor) {
        this.writer = writer;
        this.visitor = visitor;
    }

    void write(final ParseTree ctx) {
        final ExpressionMetadata writeemd = adapter.getExpressionMetadata(ctx);

        for (Segment segment : segments) {
            segment.write();
        }

        caster.checkWriteCast(visitor, writeemd);
        adapter.checkWriteBranch(visitor, ctx);
    }

    void ext(ExtContext ctx) {
        final ExpressionMetadata extemd = adapter.getExpressionMetadata(ctx);

        read = extemd.promotions != null || extemd.to.metadata != TypeMetadata.VOID;
        start(ctx.extstart());

        extemd.statement = statement;
        extemd.from = read ? current : standard.voidType;
        caster.markCast(extemd);
    }

    void assignment(AssignmentContext ctx) {
        final ExpressionMetadata assignemd = adapter.getExpressionMetadata(ctx);

        read = assignemd.promotions != null || assignemd.to.metadata != TypeMetadata.VOID;
        write = ctx.expression();

        start(ctx.extstart());

        assignemd.from = current;
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
            --prec;
            dot(dotctx);
        } else if (bracectx != null) {
            --prec;
            brace(bracectx);
        }

        statement = false;
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
        final Type to = declemd.from;

        final Object object = caster.getLegalCast(from, to, true, false);

        if (object instanceof Cast) {
            segments.add(new CastSegment((Cast)object));
        } else if (object instanceof Transform) {
            segments.add(new TransformSegment((Transform)object));
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        statement = false;
        current = to;
    }

    public void brace(final ExtbraceContext ctx) {
        final ExpressionContext exprctx = ctx.expression();

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        final boolean last = prec == 0 && dotctx == null && bracectx == null;

        if (current.dimensions > 0) {
            array(exprctx, last);
        } else {
            shortcut(exprctx, last);
        }

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

        dot(ctx.extdot());
    }

    public void call(final ExtcallContext ctx) {
        final String name = ctx.ID().getText();
        final List<ExpressionContext> arguments = ctx.arguments().expression();

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        final boolean last = prec == 0 && dotctx == null && bracectx == null;

        method(name, arguments, last);

        if (dotctx != null) {
            dot(dotctx);
        } else if (bracectx != null) {
            brace(bracectx);
        }
    }

    public void member(final ExtmemberContext ctx) {
        final String name = ctx.ID().getText();

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        final boolean last = prec == 0 && dotctx == null && bracectx == null;

        if (current == null) {
            variable(name, last);
        } else {
            field(name, last);
        }

        if (dotctx != null) {
            dot(dotctx);
        } else if (bracectx != null) {
            brace(bracectx);
        }
    }

    private void variable(final String name, final boolean last) {
        final Variable variable = adapter.getVariable(name);

        if (variable == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (last && write != null) {
            if (token)

            final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);
            writeemd.to = variable.type;
            analyzer.visit(write);
            segments.add(new NodeSegment(write));

            if (read) {
                if (variable.type.metadata.size == 1) {
                    segments.add(new InstructionSegment(Opcodes.DUP));
                } else if (variable.type.metadata.size == 2) {
                    segments.add(new InstructionSegment(Opcodes.DUP2));
                } else {
                    throw new IllegalStateException(); // TODO: message
                }

                current = variable.type;
            } else {
                current = standard.voidType;
            }

            segments.add(new VariableSegment(variable, true));
        } else if (read) {
            segments.add(new VariableSegment(variable, false));
            current = variable.type;
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private void field(final String name, final boolean last) {
        if (current.metadata == TypeMetadata.ARRAY) {
            if ("length".equals(name)) {
                if (last && write != null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                segments.add(new LengthSegment());
                current = standard.intType;
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        } else {
            final Struct struct = current.struct;
            final Field field = statik ? struct.statics.get(name) : struct.members.get(name);

            if (field == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (last && write != null) {
                if (java.lang.reflect.Modifier.isFinal(field.field.getModifiers())) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);
                writeemd.to = field.type;
                analyzer.visit(write);
                segments.add(new NodeSegment(write));

                if (read) {
                    if (field.type.metadata.size == 1) {
                        segments.add(new InstructionSegment(Opcodes.DUP_X1));
                    } else if (field.type.metadata.size == 2) {
                        segments.add(new InstructionSegment(Opcodes.DUP2_X1));
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }

                    current = field.type;
                } else {
                    current = standard.voidType;
                }

                segments.add(new FieldSegment(field, true));
            } else if (read) {
                segments.add(new FieldSegment(field, false));
                current = field.type;
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }
    }

    private void method(final String name, final List<ExpressionContext> arguments, final boolean last) {
        final Struct struct = current.struct;

        Type[] types;
        Segment segment0 = null;
        Segment segment1;

        if (current.dimensions > 0) {
            throw new IllegalArgumentException(); // TODO: message
        } else if (last && write != null) {
            throw new IllegalArgumentException(); // TODO: message
        } else if (statik && "makearray".equals(name)) {
            if (!read) {
                throw new IllegalArgumentException(); // TODO: message
            }

            types = new Type[arguments.size()];
            Arrays.fill(types, standard.intType);
            segment1 = new MakeSegment(current, arguments.size());
            current = getTypeWithArrayDimensions(struct, arguments.size());
        } else {
            final Constructor constructor = statik ? struct.constructors.get(name) : null;
            final Method method = statik ? struct.functions.get(name) : struct.methods.get(name);

            if (constructor != null) {
                types = new Type[constructor.arguments.size()];
                constructor.arguments.toArray(types);

                segments.add(new NewSegment(constructor.owner));

                if (read) {
                    segments.add(new InstructionSegment(Opcodes.DUP));
                } else {
                    current = standard.voidType;
                    statement = true;
                }

                segment1 = new ConstructorSegment(constructor);
            } else if (method != null) {
                types = new Type[method.arguments.size()];
                method.arguments.toArray(types);

                if (!read) {
                    final int size = method.rtn.metadata.size;

                    if (size == 1) {
                        segment0 = new InstructionSegment(Opcodes.POP);
                    } else if (size == 2) {
                        segment0 = new InstructionSegment(Opcodes.POP2);
                    }

                    current = standard.voidType;
                    statement = true;
                } else {
                    current = method.rtn;
                }

                segment1 = new MethodSegment(method);
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

        if (segment0 != null) {
            segments.add(segment0);
        }

        segments.add(segment1);
    }

    private void array(final ExpressionContext exprctx, final boolean last) {
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.intType;
        segments.add(new NodeSegment(exprctx));

        current = getTypeWithArrayDimensions(current.struct, current.dimensions - 1);

        if (last && write != null) {
            final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);
            writeemd.to = current;
            analyzer.visit(write);
            segments.add(new NodeSegment(write));

            if (read) {
                if (current.metadata.size == 1) {
                    segments.add(new InstructionSegment(Opcodes.DUP_X2));
                } else if (current.metadata.size == 2) {
                    segments.add(new InstructionSegment(Opcodes.DUP2_X2));
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }

            segments.add(new ArraySegment(current, true));

            if (!read) {
                current = standard.voidType;
            }
        } else if (read) {
            segments.add(new ArraySegment(current, false));
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }

        analyzer.visit(exprctx);
    }

    private void shortcut(final ExpressionContext exprctx, final boolean last) {
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);

        expremd.promotions = caster.brace;
        analyzer.visit(exprctx);

        final boolean list = expremd.from.metadata.numeric;
        expremd.to = list ? standard.intType : standard.objectType;
        caster.markCast(expremd);

        try {
            current.clazz.asSubclass(list ? standard.listType.clazz : standard.mapType.clazz);
        } catch (ClassCastException exception) {
            final Object object = caster.getLegalCast(current, list ? standard.listType : standard.mapType, true, true);

            if (object instanceof Cast) {
                final Cast cast = (Cast)object;
                segments.add(new CastSegment(cast));
                current = cast.to;
            } else if (object instanceof Transform) {
                final Transform transform = (Transform)object;
                segments.add(new TransformSegment(transform));
                current = transform.to == null ? transform.cast.to : transform.to;
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        segments.add(new NodeSegment(exprctx));

        if (list) {
            if (last && write != null) {
                final Struct struct = current.struct;
                java.lang.reflect.Method method;

                try {
                    method = current.clazz.getMethod("add", int.class, Object.class);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalStateException(); // TODO: message
                }

                final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);
                writeemd.to = standard.objectType;
                analyzer.visit(write);
                segments.add(new NodeSegment(write));

                if (read) {
                    segments.add(new InstructionSegment(Opcodes.DUP_X1));
                    current = standard.objectType;
                } else {
                    current = standard.voidType;
                }

                segments.add(new ShortcutSegment(struct, method));
            } else if (read) {
                java.lang.reflect.Method method;

                try {
                    method = current.clazz.getMethod("get", Object.class);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalStateException(); // TODO: message
                }

                segments.add(new ShortcutSegment(current.struct, method));

                if (!read) {
                    segments.add(new InstructionSegment(Opcodes.POP));
                    current = standard.voidType;
                } else {
                    current = standard.objectType;
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        } else {
            if (last && write != null) {
                java.lang.reflect.Method method;

                try {
                    method = current.clazz.getMethod("put", Object.class, Object.class);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalStateException(); // TODO: message
                }

                final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);
                writeemd.to = standard.objectType;
                analyzer.visit(write);
                segments.add(new NodeSegment(write));

                segments.add(new ShortcutSegment(current.struct, method));

                if (!read) {
                    segments.add(new InstructionSegment(Opcodes.POP));
                    current = standard.voidType;
                } else {
                    current = standard.objectType;
                }
            } else if (read) {
                java.lang.reflect.Method method;

                try {
                    method = current.clazz.getMethod("get", Object.class);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalStateException(); // TODO: message
                }

                segments.add(new ShortcutSegment(current.struct, method));
                current = standard.objectType;
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }
    }
}
