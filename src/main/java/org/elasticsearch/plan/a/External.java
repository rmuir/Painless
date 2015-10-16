package org.elasticsearch.plan.a;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.elasticsearch.plan.a.Adapter.*;
import static org.elasticsearch.plan.a.Caster.*;
import static org.elasticsearch.plan.a.Default.*;
import static org.elasticsearch.plan.a.Definition.*;
import static org.elasticsearch.plan.a.PlanAParser.*;

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
        private final String internal;
        private final String name;
        private final String descriptor;
        private final boolean statik;
        private final boolean iface;

        MethodSegment(final Method method) {
            this.internal = method.owner.internal;
            this.name = method.method.getName();
            this.descriptor = method.descriptor;
            statik = java.lang.reflect.Modifier.isStatic(method.method.getModifiers());
            iface = java.lang.reflect.Modifier.isInterface(method.owner.clazz.getModifiers());
        }

        MethodSegment(final String internal, final Class clazz, final java.lang.reflect.Method method) {
            this.internal = internal;
            this.name = method.getName();
            this.descriptor = org.objectweb.asm.commons.Method.getMethod(method).getDescriptor();
            statik = java.lang.reflect.Modifier.isStatic(method.getModifiers());
            iface = java.lang.reflect.Modifier.isInterface(clazz.getModifiers());
        }

        @Override
        void write() {
            if (statik) {
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internal, name, descriptor, false);
            } else if (iface) {
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
                case VOID:   throw new IllegalStateException(); // TODO: message
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
            caster.checkWriteCast(visitor, cast);
        }
    }

    private class TokenSegment extends Segment {
        private final Type type;
        private final int token;

        TokenSegment(final Type type, final int token) {
            this.type = type;
            this.token = token;
        }

        @Override
        void write() {
            writer.writeBinaryInstruction(type.metadata, token);
        }
    }

    private class NewStringsSegment extends Segment {
        private final ParseTree mark;

        NewStringsSegment(final ParseTree mark) {
            this.mark = mark;
        }

        @Override
        void write() {
            writer.writeNewStrings();
            adapter.markStrings(mark);
        }
    }

    private class AppendStringsSegment extends Segment {
        private final ParseTree mark;
        private final Type type;

        AppendStringsSegment(final Type type) {
            mark = null;
            this.type = type;
        }

        AppendStringsSegment(final ParseTree mark, final Type type) {
            this.mark = mark;
            this.type = type;
        }

        @Override
        void write() {
            if (mark == null || adapter.getStrings(mark)) {
                writer.writeAppendStrings(type.metadata);
            }
        }
    }

    private class ToStringsSegment extends Segment {
        @Override
        void write() {
            writer.writeToStrings();
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

    private class IncrementSegment extends Segment {
        private final Variable variable;
        private final int value;

        IncrementSegment(final Variable variable, final int value) {
            this.variable = variable;
            this.value = value;
        }

        @Override
        void write() {
            visitor.visitIincInsn(variable.slot, value);
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
        writer = null;
        visitor = null;

        read = false;
        write = null;
        token = 0;
        post = false;

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

        read = extemd.promotion != null || extemd.to.metadata != TypeMetadata.VOID;
        start(ctx.extstart());

        extemd.statement = statement;
        extemd.from = read ? current : standard.voidType;
        caster.markCast(extemd);
    }

    void assignment(AssignmentContext ctx) {
        final ExpressionMetadata assignemd = adapter.getExpressionMetadata(ctx);

        read = assignemd.promotion != null || assignemd.to.metadata != TypeMetadata.VOID;
        write = ctx.expression();
        
        if      (ctx.AMUL() != null) token = MUL;
        else if (ctx.ADIV() != null) token = DIV;
        else if (ctx.AREM() != null) token = REM;
        else if (ctx.AADD() != null) token = ADD;
        else if (ctx.ASUB() != null) token = SUB;
        else if (ctx.ALSH() != null) token = LSH;
        else if (ctx.AUSH() != null) token = USH;
        else if (ctx.ARSH() != null) token = RSH;
        else if (ctx.AAND() != null) token = BWAND;
        else if (ctx.AXOR() != null) token = BWXOR;
        else if (ctx.AOR()  != null) token = BWOR;
        else if (ctx.ACAT() != null) {
            token = CAT;
            segments.add(new NewStringsSegment(write));
        }

        start(ctx.extstart());

        assignemd.from = current;
        assignemd.statement = true;
        caster.markCast(assignemd);
    }

    void postinc(PostincContext ctx) {
        final ExpressionMetadata postincemd = adapter.getExpressionMetadata(ctx);

        read = postincemd.promotion != null || postincemd.to.metadata != TypeMetadata.VOID;
        write = ctx.increment();
        token = ADD;
        post = true;

        start(ctx.extstart());

        postincemd.from = current;
        postincemd.statement = true;
        caster.markCast(postincemd);
    }

    void preinc(PreincContext ctx) {
        final ExpressionMetadata preincemd = adapter.getExpressionMetadata(ctx);

        read = preincemd.promotion != null || preincemd.to.metadata != TypeMetadata.VOID;
        write = ctx.increment();
        token = ADD;

        start(ctx.extstart());

        preincemd.from = current;
        preincemd.statement = true;
        caster.markCast(preincemd);
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

        final Cast cast = caster.getLegalCast(current, declemd.from, true);
        segments.add(new CastSegment(cast));

        current = declemd.from;
        statement = false;
    }

    public void brace(final ExtbraceContext ctx) {
        final ExpressionContext exprctx0 = ctx.expression(0);
        final ExpressionContext exprctx1 = ctx.expression(1);
        final boolean colon = ctx.COLON() != null;

        if (!colon && exprctx1 != null) {
            throw new IllegalArgumentException(); // TODO: message
        } else if (colon && exprctx0 == null && exprctx1 == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        final boolean last = prec == 0 && dotctx == null && bracectx == null;

        if (colon) {
            if (ctx.getChild(1) instanceof ExpressionContext) {
                substring(exprctx0, exprctx1, last);
            } else {
                substring(null, exprctx0, last);
            }
        } else if (current.dimensions > 0) {
            array(exprctx0, last);
        } else {
            shortcut(exprctx0, last);
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

        final Type type = variable.type;

        if (last && write != null) {
            final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);

            if (token == CAT) {
                writeemd.promotion = caster.equality;
                analyzer.visit(write);
                writeemd.to = writeemd.from;
                caster.markCast(writeemd);

                final Cast cast = caster.getLegalCast(standard.stringType, type, false);

                segments.add(new VariableSegment(variable, false));
                segments.add(new AppendStringsSegment(type));
                segments.add(new NodeSegment(write));
                segments.add(new AppendStringsSegment(write, writeemd.to));
                segments.add(new ToStringsSegment());
                segments.add(new CastSegment(cast));

                if (read) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(Opcodes.DUP));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(Opcodes.DUP2));
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                }

                segments.add(new VariableSegment(variable, true));
            } else if (token > 0) {
                final boolean increment = type.metadata == TypeMetadata.INT && (token == ADD || token == SUB);
                current = type;
                final Cast[] casts = toNumericCasts();
                writeemd.to = current;
                analyzer.visit(write);

                if (increment && writeemd.postConst != null) {
                    if (read && post) {
                        segments.add(new VariableSegment(variable, false));
                    }

                    final int value = token == SUB ? -1*(int)writeemd.postConst : (int)writeemd.postConst;
                    segments.add(new IncrementSegment(variable, value));

                    if (read && !post) {
                        segments.add(new VariableSegment(variable, false));
                    }
                } else {
                    segments.add(new VariableSegment(variable, false));

                    if (read && post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(Opcodes.DUP));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(Opcodes.DUP2));
                        } else {
                            throw new IllegalStateException(); // TODO: message
                        }
                    }

                    segments.add(new CastSegment(casts[0]));
                    segments.add(new NodeSegment(write));
                    segments.add(new TokenSegment(current, token));
                    segments.add(new CastSegment(casts[1]));

                    if (read && !post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(Opcodes.DUP));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(Opcodes.DUP2));
                        } else {
                            throw new IllegalStateException(); // TODO: message
                        }
                    }

                    segments.add(new VariableSegment(variable, true));
                }
            } else {
                writeemd.to = type;
                analyzer.visit(write);

                segments.add(new NodeSegment(write));

                if (read && !post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(Opcodes.DUP));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(Opcodes.DUP2));
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                }

                segments.add(new VariableSegment(variable, true));
            }

            current = read ? type : standard.voidType;
        } else {
            segments.add(new VariableSegment(variable, false));
            current = variable.type;
        }
    }

    private void field(final String name, final boolean last) {
        if (current.metadata == TypeMetadata.ARRAY) {
            if ("length".equals(name)) {
                if (!read || last && write != null) {
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
                final Type type = field.type;

                if (token == CAT) {
                    writeemd.promotion = caster.equality;
                    analyzer.visit(write);
                    writeemd.to = writeemd.from;
                    caster.markCast(writeemd);

                    final Cast cast = caster.getLegalCast(standard.stringType, type, false);

                    segments.add(new InstructionSegment(Opcodes.DUP_X1));
                    segments.add(new FieldSegment(field, false));
                    segments.add(new AppendStringsSegment(type));
                    segments.add(new NodeSegment(write));
                    segments.add(new AppendStringsSegment(write, writeemd.to));
                    segments.add(new ToStringsSegment());
                    segments.add(new CastSegment(cast));

                    if (read) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(); // TODO: message
                        }
                    }

                    segments.add(new FieldSegment(field, true));
                } else if (token > 0) {
                    current = type;
                    final Cast[] casts = toNumericCasts();
                    writeemd.to = current;
                    analyzer.visit(write);

                    segments.add(new InstructionSegment(Opcodes.DUP));
                    segments.add(new FieldSegment(field, false));

                    if (read && post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(); // TODO: message
                        }
                    }

                    segments.add(new CastSegment(casts[0]));
                    segments.add(new NodeSegment(write));
                    segments.add(new TokenSegment(current, token));
                    segments.add(new CastSegment(casts[1]));

                    if (read && !post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(); // TODO: message
                        }
                    }

                    segments.add(new FieldSegment(field, true));
                } else {
                    writeemd.to = type;
                    analyzer.visit(write);

                    segments.add(new NodeSegment(write));

                    if (read && !post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(); // TODO: message
                        }
                    }

                    segments.add(new FieldSegment(field, true));
                }

                current = read ? type : standard.voidType;
            } else {
                segments.add(new FieldSegment(field, false));
                current = field.type;
            }
        }
    }

    private void method(final String name, final List<ExpressionContext> arguments, final boolean last) {
        final Struct struct = current.struct;

        Type[] types;
        Segment segment0 = null;
        Segment segment1 = null;

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
            segment0 = new MakeSegment(current, arguments.size());
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

                segment0 = new ConstructorSegment(constructor);
            } else if (method != null) {
                types = new Type[method.arguments.size()];
                method.arguments.toArray(types);

                if (!read) {
                    final int size = method.rtn.metadata.size;

                    if (size == 1) {
                        segment1 = new InstructionSegment(Opcodes.POP);
                    } else if (size == 2) {
                        segment1 = new InstructionSegment(Opcodes.POP2);
                    }

                    current = standard.voidType;
                    statement = true;
                } else {
                    current = method.rtn;
                }

                segment0 = new MethodSegment(method);
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

        if (segment1 != null) {
            segments.add(segment1);
        }
    }

    private void substring(final ExpressionContext exprctx0, final ExpressionContext exprctx1, final boolean last) {
        if (last && write != null) {
            throw new IllegalArgumentException(); // TODO: message
        } else if (!read) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Cast cast0 = caster.getLegalCast(current, standard.stringType, false);

        segments.add(new CastSegment(cast0));

        java.lang.reflect.Method method;

        try {
            method = Integer.class.getMethod("valueOf", int.class);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }

        Segment box = new MethodSegment("java/lang/Integer", Integer.class, method);

        if (exprctx0 != null) {
            final ExpressionMetadata expremd0 = adapter.createExpressionMetadata(exprctx0);
            expremd0.to = standard.intType;
            analyzer.visit(exprctx0);
            segments.add(new NodeSegment(exprctx0));
            segments.add(box);
        } else {
            segments.add(new InstructionSegment(Opcodes.ACONST_NULL));
        }

        if (exprctx1 != null) {
            final ExpressionMetadata expremd1 = adapter.createExpressionMetadata(exprctx1);
            expremd1.to = standard.intType;
            analyzer.visit(exprctx1);
            segments.add(new NodeSegment(exprctx1));
            segments.add(box);
        } else {
            segments.add(new InstructionSegment(Opcodes.ACONST_NULL));
        }

        try {
            method = Utility.class.getMethod("substring", String.class, Integer.class, Integer.class);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }

        segments.add(new MethodSegment("org/elasticsearch/plan/a/Utility", Utility.class, method));
        current = standard.stringType;
    }

    private void array(final ExpressionContext exprctx, final boolean last) {
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.intType;
        analyzer.visit(exprctx);
        segments.add(new NodeSegment(exprctx));

        final Type type = getTypeWithArrayDimensions(current.struct, current.dimensions - 1);

        if (last && write != null) {
            final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);

            if (token == CAT) {
                writeemd.promotion = caster.equality;
                analyzer.visit(write);
                writeemd.to = writeemd.from;
                caster.markCast(writeemd);

                final Cast cast = caster.getLegalCast(standard.stringType, type, false);

                segments.add(new InstructionSegment(Opcodes.DUP2_X1));
                segments.add(new ArraySegment(type, false));
                segments.add(new AppendStringsSegment(type));
                segments.add(new NodeSegment(write));
                segments.add(new AppendStringsSegment(write, writeemd.to));
                segments.add(new ToStringsSegment());
                segments.add(new CastSegment(cast));

                if (read) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                }

                segments.add(new ArraySegment(type, true));
            } else if (token > 0) {
                current = type;
                final Cast[] casts = toNumericCasts();
                writeemd.to = current;
                analyzer.visit(write);

                segments.add(new InstructionSegment(Opcodes.DUP2));
                segments.add(new ArraySegment(type, false));

                if (read && post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                }

                segments.add(new CastSegment(casts[0]));
                segments.add(new NodeSegment(write));
                segments.add(new TokenSegment(current, token));
                segments.add(new CastSegment(casts[1]));

                if (read && !post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                }

                segments.add(new ArraySegment(type, true));
            } else {
                writeemd.to = type;
                analyzer.visit(write);

                segments.add(new NodeSegment(write));

                if (read && !post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                }

                segments.add(new ArraySegment(type, true));
            }

            current = read ? type : standard.voidType;
        } else {
            segments.add(new ArraySegment(current, false));
            current = type;
        }
    }

    @SuppressWarnings("unchecked")
    private void shortcut(final ExpressionContext exprctx, final boolean last) {
        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.promotion = caster.shortcut;
        analyzer.visit(exprctx);

        final Type promote = caster.getTypePromotion(expremd.from, null, expremd.promotion);
        final boolean list = promote.metadata == TypeMetadata.INT;
        expremd.to = promote;
        caster.markCast(expremd);

        final Cast cast = caster.getLegalCast(current, list ? standard.listType : standard.mapType, true);
        segments.add(new CastSegment(cast));
        current = cast.to;
        final Struct struct = current.struct;
        segments.add(new NodeSegment(exprctx));

        if (list) {
            if (last && write != null) {
                if (token > 0) {
                    throw new IllegalArgumentException(); // TODO: message
                }

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

                segments.add(new MethodSegment(struct.internal, struct.clazz, method));
            } else {
                java.lang.reflect.Method method;

                try {
                    method = current.clazz.getMethod("get", int.class);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalStateException(); // TODO: message
                }

                segments.add(new MethodSegment(struct.internal, struct.clazz, method));

                if (!read) {
                    segments.add(new InstructionSegment(Opcodes.POP));
                    current = standard.voidType;
                } else {
                    current = standard.objectType;
                }
            }
        } else {
            if (last && write != null) {
                if (token > 0) {
                    throw new IllegalArgumentException(); // TODO: message
                }

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

                segments.add(new MethodSegment(struct.internal, struct.clazz, method));

                if (!read) {
                    segments.add(new InstructionSegment(Opcodes.POP));
                    current = standard.voidType;
                } else {
                    current = standard.objectType;
                }
            } else {
                java.lang.reflect.Method method;

                try {
                    method = current.clazz.getMethod("get", Object.class);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalStateException(); // TODO: message
                }

                segments.add(new MethodSegment(struct.internal, struct.clazz, method));
                current = standard.objectType;
            }
        }
    }

    private Cast[] toNumericCasts() {
        final boolean decimal = token == MUL || token == DIV || token == REM || token == ADD || token == SUB;
        final Promotion promotion = decimal ? caster.decimal : caster.numeric;
        final Type promote = caster.getTypePromotion(current, null, promotion);
        final Cast[] casts = new Cast[2];

        casts[0] = caster.getLegalCast(current, promote, false);
        casts[1] = caster.getLegalCast(promote, current, true);
        current = promote;

        return casts;
    }
}
