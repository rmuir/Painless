package painless;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import static painless.Adapter.*;
import static painless.Default.*;
import static painless.Definition.*;

class Caster {
    private enum PromotionType {
        SAME_TYPE,
        ANY_TYPE,
        TO_TYPE,
        ANY_NUMERIC,
        ANY_DECIMAL,
        TO_NUMERIC,
        TO_DECIMAL,
        TO_SUPERCLASS,
        TO_SUBCLASS
    }

    private static class Promotion {
        private final PromotionType promotion;
        private final Type type;

        Promotion(final PromotionType promotion, final Type type) {
            this.promotion = promotion;
            this.type = type;
        }
    }

    static class Promotions {
        private final List<Promotion> promotions;

        Promotions(final List<Promotion> promotions) {
            this.promotions = Collections.unmodifiableList(promotions);
        }
    }

    private final Definition definition;
    private final Standard standard;

    final Promotions equality;
    final Promotions add;
    final Promotions decimal;
    final Promotions numeric;
    final Promotions brace;

    Caster(final Definition definition, final Standard standard) {
        this.definition = definition;
        this.standard = standard;

        final List<Promotion> promotions = new ArrayList<>();
        promotions.add(new Promotion(PromotionType.SAME_TYPE, null));
        promotions.add(new Promotion(PromotionType.ANY_TYPE, standard.boolType));
        promotions.add(new Promotion(PromotionType.ANY_DECIMAL, null));
        promotions.add(new Promotion(PromotionType.TO_SUPERCLASS, null));
        promotions.add(new Promotion(PromotionType.TO_SUBCLASS, null));
        equality = new Promotions(promotions);

        promotions.clear();
        promotions.add(new Promotion(PromotionType.ANY_TYPE, standard.stringType));
        promotions.add(new Promotion(PromotionType.ANY_DECIMAL, null));
        promotions.add(new Promotion(PromotionType.TO_TYPE, standard.stringType));
        add = new Promotions(promotions);

        promotions.clear();
        promotions.add(new Promotion(PromotionType.TO_DECIMAL, null));
        decimal = new Promotions(promotions);

        promotions.clear();
        promotions.add(new Promotion(PromotionType.TO_NUMERIC, null));
        numeric = new Promotions(promotions);

        promotions.clear();
        promotions.add(new Promotion(PromotionType.TO_NUMERIC, null));
        promotions.add(new Promotion(PromotionType.TO_TYPE, standard.objectType));
        brace = new Promotions(promotions);
    }

    void markCast(final ExpressionMetadata emd) {
        if (emd.from == null) {
            throw new IllegalStateException(); // TODO: message
        }

        if (emd.to != null) {
            final Object object = getLegalCast(emd.to, emd.from, emd.explicit, false);

            if (object instanceof Cast) {
                emd.cast = (Cast)object;
            } else if (object instanceof Transform) {
                emd.transform = (Transform)object;
            }

            if (emd.to.metadata.constant) {
                constCast(emd);
            }
        } else if (emd.promotions == null) {
            throw new IllegalStateException(); // TODO: message
        }
    }

    Object getLegalCast(final Type from, final Type to,
                               final boolean force, final boolean ignore) {
        final Cast cast = new Cast(from, to);

        if (from.equals(to)) {
            return cast;
        }

        if (!ignore && definition.disallowed.contains(cast)) {
            throw new ClassCastException(); // TODO: message
        }

        final Transform explicit = definition.explicits.get(cast);

        if (force && explicit != null) {
            return explicit;
        }

        final Transform implicit = definition.implicits.get(cast);

        if (implicit != null) {
            return implicit;
        }

        if (definition.upcasts.contains(cast)) {
            return cast;
        }

        if (from.metadata.numeric && to.metadata.numeric && (force || definition.numerics.contains(cast))) {
            return cast;
        }

        try {
            from.clazz.asSubclass(to.clazz);

            return cast;
        } catch (ClassCastException cce0) {
            try {
                if (force) {
                    to.clazz.asSubclass(from.clazz);

                    return cast;
                } else {
                    throw new ClassCastException(); // TODO: message
                }
            } catch (ClassCastException cce1) {
                throw new ClassCastException(); // TODO: message
            }
        }
    }

    private void constCast(final ExpressionMetadata emd) {
        if (emd.preConst != null) {
            if (emd.transform != null) {
                emd.postConst = invokeTransform(emd.transform, emd.preConst);
            } else if (emd.cast != null) {
                final TypeMetadata fromTMD = emd.cast.from.metadata;
                final TypeMetadata toTMD = emd.cast.to.metadata;

                if (fromTMD == toTMD) {
                    emd.postConst = emd.preConst;
                } else if (fromTMD.numeric && toTMD.numeric) {
                    Number number;

                    if (fromTMD == TypeMetadata.CHAR) {
                        number = (int)(char)emd.preConst;
                    } else {
                        number = (Number)emd.preConst;
                    }

                    switch (toTMD) {
                        case BYTE:
                            emd.postConst = number.byteValue();
                            break;
                        case SHORT:
                            emd.postConst = number.shortValue();
                            break;
                        case CHAR:
                            emd.postConst = (char)number.intValue();
                            break;
                        case INT:
                            emd.postConst = number.intValue();
                            break;
                        case LONG:
                            emd.postConst = number.longValue();
                            break;
                        case FLOAT:
                            emd.postConst = number.floatValue();
                            break;
                        case DOUBLE:
                            emd.postConst = number.doubleValue();
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }
    }

    private Object invokeTransform(final Transform transform, final Object object) {
        final Method method = transform.method;
        final java.lang.reflect.Method jmethod = method.method;
        final int modifiers = jmethod.getModifiers();

        try {
            if (java.lang.reflect.Modifier.isStatic(modifiers)) {
                return jmethod.invoke(null, object);
            } else {
                return jmethod.invoke(object);
            }
        } catch (IllegalAccessException | IllegalArgumentException |
                java.lang.reflect.InvocationTargetException | NullPointerException |
                ExceptionInInitializerError exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    Type getTypePromotion(final Type from0, final Type from1, final Promotions promotions) {
        for (final Promotion promotion : promotions.promotions) {
            switch (promotion.promotion) {
                case SAME_TYPE: {
                    if (from0.equals(from1)) {
                        return from0;
                    }

                    break;
                } case ANY_TYPE: {
                    final Type to = promotion.type;
                    boolean eq0 = from0.equals(to);
                    boolean eq1 = from1.equals(to);

                    if (eq0 && eq1) {
                        return to;
                    }

                    if (getLegalCast(eq0 ? from1 : from0, to, false, false) != null) {
                        return to;
                    }

                    break;
                } case TO_TYPE: {
                    final Type to = promotion.type;
                    boolean eq0 = from0.equals(to);
                    boolean eq1 = from1.equals(to);

                    if (eq0 && eq1) {
                        return to;
                    }

                    boolean castable = true;

                    if (!eq0) {
                        try {
                            getLegalCast(from0, to, false, false);
                        } catch (ClassCastException exception) {
                            castable = false;
                        }
                    }

                    if (!eq1) {
                        try {
                            getLegalCast(from1, to, false, false);
                        } catch (ClassCastException exception) {
                            castable = false;
                        }
                    }

                    if (castable) {
                        return to;
                    }

                    break;
                } case ANY_NUMERIC: {
                    if (from0.metadata.numeric || from1.metadata.numeric) {
                        final Type type = getNumericPromotion(from0, from1, false);

                        if (type != null) {
                            return type;
                        }
                    }

                    break;
                } case ANY_DECIMAL: {
                    if (from0.metadata.numeric || from1.metadata.numeric) {
                        final Type type = getNumericPromotion(from0, from1, true);

                        if (type != null) {
                            return type;
                        }
                    }

                    break;
                } case TO_NUMERIC: {
                    final Type type = getNumericPromotion(from0, from1, false);

                    if (type != null) {
                        return type;
                    }

                    break;
                } case TO_DECIMAL: {
                    final Type type = getNumericPromotion(from0, from1, true);

                    if (type != null) {
                        return type;
                    }

                    break;
                } case TO_SUPERCLASS: {
                    if (from0.equals(from1)) {
                        return from0;
                    }

                    final Cast cast0 = new Cast(from0, from1);
                    final Cast cast1 = new Cast(from1, from0);

                    if (definition.upcasts.contains(cast0)) {
                        return from1;
                    }

                    if (definition.upcasts.contains(cast1)) {
                        return from0;
                    }

                    break;
                } case TO_SUBCLASS: {
                    if (from0.equals(from1)) {
                        return from0;
                    }

                    if (from0.clazz.equals(from1.clazz)) {
                        Cast cast0 = null;
                        Cast cast1 = null;

                        if (from0.struct.generic && !from1.struct.generic) {
                            cast0 = new Cast(from0, from1);
                            cast1 = new Cast(from1, from0);
                        } else if (!from0.struct.generic && from1.struct.generic) {
                            cast0 = new Cast(from1, from0);
                            cast1 = new Cast(from0, from1);
                        }

                        if (cast0 != null && cast1 != null) {
                            if (!definition.disallowed.contains(cast0) && definition.implicits.containsKey(cast0)) {
                                return cast0.to;
                            }

                            if (!definition.disallowed.contains(cast1) && definition.implicits.containsKey(cast1)) {
                                return cast1.to;
                            }
                        }

                        return standard.objectType;
                    }

                    try {
                        from0.clazz.asSubclass(from1.clazz);
                        final Cast cast = new Cast(from0, from1);

                        if (!definition.disallowed.contains(cast)) {
                            return from1;
                        }
                    } catch (ClassCastException cce0) {
                        // Do nothing.
                    }

                    try {
                        from1.clazz.asSubclass(from1.clazz);
                        final Cast cast = new Cast(from1, from0);

                        if (!definition.disallowed.contains(cast)) {
                            return from0;
                        }
                    } catch (ClassCastException cce0) {
                        // Do nothing.
                    }

                    if (from0.metadata.object && from1.metadata.object) {
                        return standard.objectType;
                    }

                    break;
                } default: {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        }

        return null;
    }

    private Type getNumericPromotion(final Type from0, final Type from1, boolean decimal) {
        final Deque<Type> upcast = new ArrayDeque<>();
        final Deque<Type> downcast = new ArrayDeque<>();

        if (decimal) {
            upcast.push(standard.doubleType);
            upcast.push(standard.floatType);
        } else {
            downcast.push(standard.doubleType);
            downcast.push(standard.floatType);
        }

        upcast.push(standard.longType);
        upcast.push(standard.intType);

        while (!upcast.isEmpty()) {
            final Type to = upcast.pop();
            final Cast cast0 = new Cast(from0, to);

            if (definition.disallowed.contains(cast0))                               continue;
            if (from0.metadata.numeric && from0.metadata != to.metadata &&
                    !definition.numerics.contains(cast0))                            continue;
            if (upcast.contains(from0))                                              continue;
            if (downcast.contains(from0) && !definition.numerics.contains(cast0) &&
                    !definition.implicits.containsKey(cast0))                        continue;
            if (!from0.metadata.numeric && !definition.implicits.containsKey(cast0)) continue;

            if (from1 != null) {
                final Cast cast1 = new Cast(from1, to);

                if (definition.disallowed.contains(cast1))                               continue;
                if (from1.metadata.numeric && from1.metadata != to.metadata &&
                        !definition.numerics.contains(cast1))                            continue;
                if (upcast.contains(from1))                                              continue;
                if (downcast.contains(from1) && !definition.numerics.contains(cast1) &&
                        !definition.implicits.containsKey(cast1))                        continue;
                if (!from1.metadata.numeric && !definition.implicits.containsKey(cast1)) continue;
            }

            return to;
        }

        throw new ClassCastException(); // TODO: message
    }

    void checkWriteCast(final MethodVisitor visitor, final ExpressionMetadata metadata) {
        if (metadata.cast != null) {
            writeCast(visitor, metadata.cast);
        } else if (metadata.transform != null) {
            writeTransform(visitor, metadata.transform);
        } else {
            throw new IllegalStateException(); // TODO: message
        }
    }

    void writeCast(final MethodVisitor visitor, final Cast cast) {
        final Type from = cast.from;
        final Type to = cast.to;

        if (from.equals(to)) {
            return;
        }

        if (from.metadata.numeric && to.metadata.numeric) {
            switch (from.metadata) {
                case BYTE:
                    switch (to.metadata) {
                        case SHORT:  visitor.visitInsn(Opcodes.I2S); break;
                        case CHAR:   visitor.visitInsn(Opcodes.I2C); break;
                        case LONG:   visitor.visitInsn(Opcodes.I2L); break;
                        case FLOAT:  visitor.visitInsn(Opcodes.I2F); break;
                        case DOUBLE: visitor.visitInsn(Opcodes.I2D); break;
                    }
                    break;
                case SHORT:
                    switch (to.metadata) {
                        case BYTE:   visitor.visitInsn(Opcodes.I2B); break;
                        case CHAR:   visitor.visitInsn(Opcodes.I2C); break;
                        case LONG:   visitor.visitInsn(Opcodes.I2L); break;
                        case FLOAT:  visitor.visitInsn(Opcodes.I2F); break;
                        case DOUBLE: visitor.visitInsn(Opcodes.I2D); break;
                    }
                    break;
                case CHAR:
                    switch (to.metadata) {
                        case BYTE:   visitor.visitInsn(Opcodes.I2B); break;
                        case SHORT:  visitor.visitInsn(Opcodes.I2S); break;
                        case LONG:   visitor.visitInsn(Opcodes.I2L); break;
                        case FLOAT:  visitor.visitInsn(Opcodes.I2F); break;
                        case DOUBLE: visitor.visitInsn(Opcodes.I2D); break;
                    }
                    break;
                case INT:
                    switch (to.metadata) {
                        case BYTE:   visitor.visitInsn(Opcodes.I2B); break;
                        case SHORT:  visitor.visitInsn(Opcodes.I2S); break;
                        case CHAR:   visitor.visitInsn(Opcodes.I2C); break;
                        case LONG:   visitor.visitInsn(Opcodes.I2L); break;
                        case FLOAT:  visitor.visitInsn(Opcodes.I2F); break;
                        case DOUBLE: visitor.visitInsn(Opcodes.I2D); break;
                    }
                    break;
                case LONG:
                    switch (to.metadata) {
                        case BYTE:   visitor.visitInsn(Opcodes.L2I); visitor.visitInsn(Opcodes.I2B); break;
                        case SHORT:  visitor.visitInsn(Opcodes.L2I); visitor.visitInsn(Opcodes.I2S); break;
                        case CHAR:   visitor.visitInsn(Opcodes.L2I); visitor.visitInsn(Opcodes.I2C); break;
                        case INT:    visitor.visitInsn(Opcodes.L2I); break;
                        case FLOAT:  visitor.visitInsn(Opcodes.L2F); break;
                        case DOUBLE: visitor.visitInsn(Opcodes.L2D); break;
                    }
                    break;
                case FLOAT:
                    switch (to.metadata) {
                        case BYTE:   visitor.visitInsn(Opcodes.F2I); visitor.visitInsn(Opcodes.I2B); break;
                        case SHORT:  visitor.visitInsn(Opcodes.F2I); visitor.visitInsn(Opcodes.I2S); break;
                        case CHAR:   visitor.visitInsn(Opcodes.F2I); visitor.visitInsn(Opcodes.I2C); break;
                        case INT:    visitor.visitInsn(Opcodes.F2I); break;
                        case LONG:   visitor.visitInsn(Opcodes.F2L); break;
                        case DOUBLE: visitor.visitInsn(Opcodes.F2D); break;
                    }
                    break;
                case DOUBLE:
                    switch (to.metadata) {
                        case BYTE:  visitor.visitInsn(Opcodes.D2I); visitor.visitInsn(Opcodes.I2B); break;
                        case SHORT: visitor.visitInsn(Opcodes.D2I); visitor.visitInsn(Opcodes.I2S); break;
                        case CHAR:  visitor.visitInsn(Opcodes.D2I); visitor.visitInsn(Opcodes.I2C); break;
                        case INT:   visitor.visitInsn(Opcodes.D2I); break;
                        case LONG:  visitor.visitInsn(Opcodes.D2L); break;
                        case FLOAT: visitor.visitInsn(Opcodes.D2F); break;
                    }
                    break;
            }
        } else {
            try {
                from.clazz.asSubclass(to.clazz);
            } catch (ClassCastException exception) {
                visitor.visitTypeInsn(Opcodes.CHECKCAST, to.internal);
            }
        }
    }

    void writeTransform(final MethodVisitor visitor, final Transform transform) {
        final Class clazz = transform.method.owner.clazz;
        final java.lang.reflect.Method method = transform.method.method;

        final String name = method.getName();
        final String internal = transform.method.owner.internal;
        final String descriptor = transform.method.descriptor;

        final Type from = transform.from;
        final Type to = transform.to;

        if (from != null) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, from.internal);
        }

        if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internal, name, descriptor, false);
        } else if (java.lang.reflect.Modifier.isInterface(clazz.getModifiers())) {
            visitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, internal, name, descriptor, true);
        } else {
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, internal, name, descriptor, false);
        }

        if (to != null) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, to.internal);
        }
    }
}
