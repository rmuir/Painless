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

import org.antlr.v4.runtime.ParserRuleContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.elasticsearch.plan.a.Adapter.*;
import static org.elasticsearch.plan.a.Caster.*;
import static org.elasticsearch.plan.a.Default.*;
import static org.elasticsearch.plan.a.Definition.*;
import static org.elasticsearch.plan.a.PlanAParser.*;

class External {
    private abstract class Segment {
        final ParserRuleContext source;

        Segment(final ParserRuleContext source) {
            this.source = source;
        }

        abstract void write();
    }

    private class VariableSegment extends Segment {
        private final Variable variable;
        private final boolean store;

        VariableSegment(final ParserRuleContext source, final Variable variable, final boolean store) {
            super(source);

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

        FieldSegment(final ParserRuleContext source, final Field field, final boolean store) {
            super(source);

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

        NewSegment(final ParserRuleContext source, final Struct struct) {
            super(source);

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

        ConstructorSegment(final ParserRuleContext source, final Constructor constructor) {
            super(source);

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

        MethodSegment(final ParserRuleContext source, final Method method) {
            super(source);

            this.internal = method.owner.internal;
            this.name = method.method.getName();
            this.descriptor = method.descriptor;
            statik = java.lang.reflect.Modifier.isStatic(method.method.getModifiers());
            iface = java.lang.reflect.Modifier.isInterface(method.owner.clazz.getModifiers());
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
        NodeSegment(final ParserRuleContext source) {
            super(source);
        }

        @Override
        void write() {
            writer.visit(source);
        }
    }

    private class ArraySegment extends Segment {
        private final Type type;
        private final boolean store;

        ArraySegment(final ParserRuleContext source, final Type type, final boolean store) {
            super(source);

            this.type = type;
            this.store = store;
        }

        @Override
        void write() {
            switch (type.metadata) {
                case VOID:   throw new IllegalStateException(error(source) + "Unexpected state during write.");
                case BOOL:
                case BYTE:   visitor.visitInsn(store ? Opcodes.BASTORE : Opcodes.BALOAD); break;
                case SHORT:  visitor.visitInsn(store ? Opcodes.SASTORE : Opcodes.SALOAD); break;
                case CHAR:   visitor.visitInsn(store ? Opcodes.CASTORE : Opcodes.CALOAD); break;
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

        MakeSegment(final ParserRuleContext source, final Type type, final int dimensions) {
            super(source);

            this.type = type;
            this.dimensions = dimensions;
        }

        @Override
        void write() {
            if (dimensions == 1) {
                switch (type.metadata) {
                    case VOID:   throw new IllegalStateException(error(source) + "Unexpected state during write.");
                    case BOOL:   visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN); break;
                    case BYTE:   visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BYTE);    break;
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
                throw new IllegalStateException(error(source) + "Unexpected state during write.");
            }
        }
    }

    private class LengthSegment extends Segment {
        LengthSegment(final ParserRuleContext source) {
            super(source);
        }

        @Override
        void write() {
            visitor.visitInsn(Opcodes.ARRAYLENGTH);
        }
    }

    private class CastSegment extends Segment {
        private final Cast cast;

        CastSegment(final ParserRuleContext source, final Cast cast) {
            super(source);

            this.cast = cast;
        }

        @Override
        void write() {
            caster.checkWriteCast(visitor, source, cast);
        }
    }

    private class TokenSegment extends Segment {
        private final Type originalType;
        private final Type promotedType;
        private final int token;

        TokenSegment(ParserRuleContext source, Type originalType, Type promotedType, int token) {
            super(source);
            this.originalType = originalType;
            this.promotedType = promotedType;
            this.token = token;
        }

        @Override
        void write() {

            // If the type is bool, we don't need special range checks
            // so we go straight to writing the binary instruction.

            if (promotedType.metadata == TypeMetadata.BOOL) {
                writer.writeBinaryInstruction(source, TypeMetadata.INT, token);
            } else {
                writer.writeCompoundAssignmentInstruction(source, originalType.metadata, promotedType.metadata, token);
            }
        }
    }

    private class NewStringsSegment extends Segment {
        NewStringsSegment(final ParserRuleContext source) {
            super(source);
        }

        @Override
        void write() {
            writer.writeNewStrings();
            adapter.markStrings(source);
        }
    }

    private class AppendStringsSegment extends Segment {
        private final Type type;
        private final boolean force;

        AppendStringsSegment(final ParserRuleContext source, final Type type, final boolean force) {
            super(source);

            this.type = type;
            this.force = force;
        }

        @Override
        void write() {
            if (force || adapter.getStrings(source)) {
                writer.writeAppendStrings(source, type.metadata);
                adapter.unmarkStrings(source);
            }
        }
    }

    private class ToStringsSegment extends Segment {
        ToStringsSegment(final ParserRuleContext source) {
            super(source);
        }

        @Override
        void write() {
            writer.writeToStrings();
        }
    }

    private class InstructionSegment extends Segment {
        private final int instruction;

        InstructionSegment(final ParserRuleContext source, final int instruction) {
            super(source);

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
    private ParserRuleContext write;
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

    void write(final ParserRuleContext ctx) {
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
        write = adapter.getExpressionContext(ctx.expression());
        
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

        assignemd.statement = true;

        start(ctx.extstart());

        assignemd.from = current;
        caster.markCast(assignemd);
    }

    void postinc(PostincContext ctx) {
        final ExpressionMetadata postincemd = adapter.getExpressionMetadata(ctx);

        read = postincemd.promotion != null || postincemd.to.metadata != TypeMetadata.VOID;
        write = ctx.increment();
        token = ADD;
        post = true;

        postincemd.statement = true;

        start(ctx.extstart());

        postincemd.from = current;
        caster.markCast(postincemd);
    }

    void preinc(PreincContext ctx) {
        final ExpressionMetadata preincemd = adapter.getExpressionMetadata(ctx);

        read = preincemd.promotion != null || preincemd.to.metadata != TypeMetadata.VOID;
        write = ctx.increment();
        token = ADD;

        preincemd.statement = true;

        start(ctx.extstart());

        preincemd.from = current;
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
            throw new IllegalStateException();
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
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        statement = false;

        if (dotctx != null) {
            --prec;
            dot(dotctx);
        } else if (bracectx != null) {
            --prec;
            brace(bracectx);
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
            throw new IllegalStateException(error(ctx) + "Unexpected parser state.");
        }

        final DecltypeContext declctx = ctx.decltype();
        final ExpressionMetadata declemd = adapter.createExpressionMetadata(declctx);
        analyzer.visit(declctx);

        final Cast cast = caster.getLegalCast(ctx, current, declemd.from, true);
        segments.add(new CastSegment(ctx, cast));

        current = declemd.from;
        statement = false;
    }

    public void brace(final ExtbraceContext ctx) {
        final ExpressionContext exprctx = adapter.getExpressionContext(ctx.expression());
        final ExtdotContext dotctx = ctx.extdot();
        final ExtbraceContext bracectx = ctx.extbrace();

        final boolean last = prec == 0 && dotctx == null && bracectx == null;

        array(ctx, exprctx, last);

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
            throw new IllegalArgumentException(error(ctx) + "Unexpected static type.");
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

        method(ctx, name, arguments, last);

        statik = false;

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
            variable(ctx, name, last);
        } else {
            field(ctx, name, last);
        }

        statik = false;

        if (dotctx != null) {
            dot(dotctx);
        } else if (bracectx != null) {
            brace(bracectx);
        }
    }

    private void variable(final ParserRuleContext source, final String name, final boolean last) {
        final Variable variable = adapter.getVariable(name);

        if (variable == null) {
            throw new IllegalArgumentException(error(source) + "Unknown variable [" + name + "].");
        }

        final Type type = variable.type;

        if (last && write != null) {
            final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);

            if (token == CAT) {
                writeemd.promotion = caster.concat;
                analyzer.visit(write);
                writeemd.to = writeemd.from;
                caster.markCast(writeemd);

                final Cast cast = caster.getLegalCast(source, standard.stringType, type, false);

                segments.add(new VariableSegment(source, variable, false));
                segments.add(new AppendStringsSegment(source, type, true));
                segments.add(new NodeSegment(write));
                segments.add(new AppendStringsSegment(write, writeemd.to, false));
                segments.add(new ToStringsSegment(source));
                segments.add(new CastSegment(source, cast));

                if (read) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }

                segments.add(new VariableSegment(source, variable, true));
            } else if (token > 0) {
                current = type;
                final Cast[] casts = toNumericCasts(source);
                writeemd.to = current;
                analyzer.visit(write);

                segments.add(new VariableSegment(source, variable, false));
                
                if (read && post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }
                
                segments.add(new CastSegment(source, casts[0]));
                segments.add(new NodeSegment(write));
                segments.add(new TokenSegment(source, variable.type, current, token));
                segments.add(new CastSegment(source, casts[1]));
                
                if (read && !post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }
                
                segments.add(new VariableSegment(source, variable, true));
            } else {
                writeemd.to = type;
                analyzer.visit(write);

                segments.add(new NodeSegment(write));

                if (read && !post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }

                segments.add(new VariableSegment(source, variable, true));
            }

            current = read ? type : standard.voidType;
        } else {
            segments.add(new VariableSegment(source, variable, false));
            current = variable.type;
        }
    }

    private void field(final ParserRuleContext source, final String name, final boolean last) {
        if (current.metadata == TypeMetadata.ARRAY) {
            if ("length".equals(name)) {
                if (!read || last && write != null) {
                    throw new IllegalArgumentException(error(source) + "Cannot write to read-only field [length].");
                }

                segments.add(new LengthSegment(source));
                current = standard.intType;
            } else {
                throw new IllegalArgumentException(error(source) + "Unexpected array field [" + name + "].");
            }
        } else {
            final Struct struct = current.struct;
            final Field field = statik ? struct.statics.get(name) : struct.members.get(name);

            if (field == null) {
                throw new IllegalArgumentException(
                        error(source) + "Unknown field [" + name + "] for type [" + struct.name + "].");
            }

            if (last && write != null) {
                if (java.lang.reflect.Modifier.isFinal(field.field.getModifiers())) {
                    throw new IllegalArgumentException(error(source) + "Cannot write to read-only" +
                            " field [" + name + "] for type [" + struct.name + "].");
                }

                final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);
                final Type type = field.type;

                if (token == CAT) {
                    writeemd.promotion = caster.concat;
                    analyzer.visit(write);
                    writeemd.to = writeemd.from;
                    caster.markCast(writeemd);

                    final Cast cast = caster.getLegalCast(source, standard.stringType, type, false);

                    segments.add(new InstructionSegment(source, Opcodes.DUP_X1));
                    segments.add(new FieldSegment(source, field, false));
                    segments.add(new AppendStringsSegment(source, type, true));
                    segments.add(new NodeSegment(write));
                    segments.add(new AppendStringsSegment(write, writeemd.to, false));
                    segments.add(new ToStringsSegment(source));
                    segments.add(new CastSegment(source, cast));

                    if (read) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(error(source) + "Unexpected type size.");
                        }
                    }

                    segments.add(new FieldSegment(source, field, true));
                } else if (token > 0) {
                    current = type;
                    final Cast[] casts = toNumericCasts(source);
                    writeemd.to = current;
                    analyzer.visit(write);

                    segments.add(new InstructionSegment(source, Opcodes.DUP));
                    segments.add(new FieldSegment(source, field, false));

                    if (read && post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(error(source) + "Unexpected type size.");
                        }
                    }

                    segments.add(new CastSegment(source, casts[0]));
                    segments.add(new NodeSegment(write));
                    segments.add(new TokenSegment(source, field.type, current, token));
                    segments.add(new CastSegment(source, casts[1]));

                    if (read && !post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(error(source) + "Unexpected type size.");
                        }
                    }

                    segments.add(new FieldSegment(source, field, true));
                } else {
                    writeemd.to = type;
                    analyzer.visit(write);

                    segments.add(new NodeSegment(write));

                    if (read && !post) {
                        if (type.metadata.size == 1) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP_X1));
                        } else if (type.metadata.size == 2) {
                            segments.add(new InstructionSegment(source, Opcodes.DUP2_X1));
                        } else {
                            throw new IllegalStateException(error(source) + "Unexpected type size.");
                        }
                    }

                    segments.add(new FieldSegment(source, field, true));
                }

                current = read ? type : standard.voidType;
            } else {
                segments.add(new FieldSegment(source, field, false));
                current = field.type;
            }
        }
    }

    private void method(final ParserRuleContext source, final String name,
                        final List<ExpressionContext> arguments, final boolean last) {
        final Struct struct = current.struct;

        Type[] types;
        Segment segment0;
        Segment segment1 = null;

        if (current.dimensions > 0) {
            throw new IllegalArgumentException(error(source) + "Unexpected call [" + name + "] on an array.");
        } else if (last && write != null) {
            throw new IllegalArgumentException(error(source) + "Cannot assign a value to a call [" + name + "].");
        } else if (statik && "makearray".equals(name)) {
            if (!read) {
                throw new IllegalArgumentException(error(source) + "A newly created array must be assigned.");
            }

            types = new Type[arguments.size()];
            Arrays.fill(types, standard.intType);
            segment0 = new MakeSegment(source, current, arguments.size());
            current = getTypeWithArrayDimensions(struct, arguments.size());
        } else {
            final Constructor constructor = statik ? struct.constructors.get(name) : null;
            final Method method = statik ? struct.functions.get(name) : struct.methods.get(name);

            if (constructor != null) {
                types = new Type[constructor.arguments.size()];
                constructor.arguments.toArray(types);

                segments.add(new NewSegment(source, constructor.owner));

                if (read) {
                    segments.add(new InstructionSegment(source, Opcodes.DUP));
                } else {
                    current = standard.voidType;
                    statement = true;
                }

                segment0 = new ConstructorSegment(source, constructor);
            } else if (method != null) {
                types = new Type[method.arguments.size()];
                method.arguments.toArray(types);

                if (!read) {
                    final int size = method.rtn.metadata.size;

                    if (size == 1) {
                        segment1 = new InstructionSegment(source, Opcodes.POP);
                    } else if (size == 2) {
                        segment1 = new InstructionSegment(source, Opcodes.POP2);
                    } else if (size != 0) {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }

                    current = standard.voidType;
                    statement = true;
                } else {
                    current = method.rtn;
                }

                segment0 = new MethodSegment(source, method);
            } else {
                throw new IllegalArgumentException(
                        error(source) + "Unknown call [" + name + "] on type [" + struct.name + "].");
            }
        }

        if (arguments.size() != types.length) {
            throw new IllegalArgumentException(error(source) + "When calling [" + name + "] on type " +
                    "[" + struct.name + "] expected [" + types.length + "] arguments," +
                    " but found [" + arguments.size() + "].");
        }

        for (int argument = 0; argument < arguments.size(); ++argument) {
            final ExpressionContext exprctx = adapter.getExpressionContext(arguments.get(argument));
            final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
            expremd.to = types[argument];
            analyzer.visit(exprctx);

            segments.add(new NodeSegment(exprctx));
        }

        segments.add(segment0);

        if (segment1 != null) {
            segments.add(segment1);
        }
    }

    private void array(final ParserRuleContext source, final ExpressionContext exprctx, final boolean last) {
        if (current.dimensions == 0) {
            throw new IllegalArgumentException(
                    error(source) + "Attempting to address a non-array type [" + current.name + "] as an array.");
        }

        final ExpressionMetadata expremd = adapter.createExpressionMetadata(exprctx);
        expremd.to = standard.intType;
        analyzer.visit(exprctx);
        segments.add(new NodeSegment(exprctx));

        final Type type = getTypeWithArrayDimensions(current.struct, current.dimensions - 1);

        if (last && write != null) {
            final ExpressionMetadata writeemd = adapter.createExpressionMetadata(write);

            if (token == CAT) {
                writeemd.promotion = caster.concat;
                analyzer.visit(write);
                writeemd.to = writeemd.from;
                caster.markCast(writeemd);

                final Cast cast = caster.getLegalCast(source, standard.stringType, type, false);

                segments.add(new InstructionSegment(source, Opcodes.DUP2_X1));
                segments.add(new ArraySegment(source, type, false));
                segments.add(new AppendStringsSegment(source, type, true));
                segments.add(new NodeSegment(write));
                segments.add(new AppendStringsSegment(write, writeemd.to, false));
                segments.add(new ToStringsSegment(source));
                segments.add(new CastSegment(source, cast));

                if (read) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }

                segments.add(new ArraySegment(source, type, true));
            } else if (token > 0) {
                current = type;
                final Cast[] casts = toNumericCasts(source);
                writeemd.to = current;
                analyzer.visit(write);

                segments.add(new InstructionSegment(source, Opcodes.DUP2));
                segments.add(new ArraySegment(source, type, false));

                if (read && post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }

                segments.add(new CastSegment(source, casts[0]));
                segments.add(new NodeSegment(write));
                segments.add(new TokenSegment(source, type, current, token));
                segments.add(new CastSegment(source, casts[1]));

                if (read && !post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }

                segments.add(new ArraySegment(source, type, true));
            } else {
                writeemd.to = type;
                analyzer.visit(write);

                segments.add(new NodeSegment(write));

                if (read && !post) {
                    if (type.metadata.size == 1) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP_X2));
                    } else if (type.metadata.size == 2) {
                        segments.add(new InstructionSegment(source, Opcodes.DUP2_X2));
                    } else {
                        throw new IllegalStateException(error(source) + "Unexpected type size.");
                    }
                }

                segments.add(new ArraySegment(source, type, true));
            }

            current = read ? type : standard.voidType;
        } else {
            segments.add(new ArraySegment(source, type, false));
            current = type;
        }
    }

    private Cast[] toNumericCasts(final ParserRuleContext source) {
        final Cast[] casts = new Cast[2];

        // We have to first check if the type is a boolean under certain operators.

        if (token == BWAND || token == BWXOR || token == BWOR) {
            try {
                casts[0] = caster.getLegalCast(source, current, standard.boolType, false);
                casts[1] = caster.getLegalCast(source, standard.boolType, current, true);
                current = standard.boolType;

                return casts;
            } catch (ClassCastException exception) {
                // Do nothing.
            }
        }

        // If the type is not a boolean then it must be numeric. Promote the single type.

        final boolean decimal = token == MUL || token == DIV || token == REM || token == ADD || token == SUB;
        final Promotion promotion = decimal ? caster.decimal : caster.numeric;
        final Type promote = caster.getTypePromotion(source, current, null, promotion);

        casts[0] = caster.getLegalCast(source, current, promote, false);
        casts[1] = caster.getLegalCast(source, promote, current, true);
        current = promote;

        return casts;
    }
}
