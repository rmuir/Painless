package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import static painless.Default.*;
import static painless.Types.*;

public class Adapter {
    static class Variable {
        final String name;
        final Type type;
        final int slot;

        Variable(final String name, final Type type, final int slot) {
            this.name = name;
            this.type = type;
            this.slot = slot;
        }
    }

    static class StatementMetadata {
        final ParseTree source;

        boolean allExit;
        boolean allReturn;
        boolean anyReturn;
        boolean allBreak;
        boolean anyBreak;
        boolean allContinue;
        boolean anyContinue;

        StatementMetadata(final ParseTree source) {
            this.source = source;

            allExit = false;
            allReturn = false;
            anyReturn = false;
            allBreak = false;
            anyBreak = false;
            allContinue = false;
            anyContinue = false;
        }
    }

    static class ExpressionMetadata {
        final ParseTree source;

        boolean statement;

        Object preConst;
        Object postConst;
        boolean isNull;

        Type to;
        Type from;

        boolean anyNumeric;
        boolean anyType;
        boolean explicit;

        Cast cast;
        Transform transform;

        ExpressionMetadata(final ParseTree source) {
            this.source = source;

            statement = false;

            preConst = null;
            postConst = null;
            isNull = false;

            to = null;
            from = null;

            anyNumeric = false;
            anyType = false;
            explicit = false;

            cast = null;
            transform = null;
        }
    }

    final Types types;
    final Standard standard;
    final ParseTree root;

    private final Deque<Integer> scopes;
    private final Deque<Variable> variables;

    private final Map<ParseTree, StatementMetadata> statementMetadata;
    private final Map<ParseTree, ExpressionMetadata> expressionMetadata;

    Adapter(final Types types, final Standard standard, final ParseTree root) {
        this.types = types;
        this.standard = standard;
        this.root = root;

        scopes = new ArrayDeque<>();
        variables = new ArrayDeque<>();

        statementMetadata = new HashMap<>();
        expressionMetadata = new HashMap<>();
    }

    void incrementScope() {
        scopes.push(0);
    }

    void decrementScope() {
        int remove = scopes.pop();

        while (remove > 0) {
            variables.pop();
            --remove;
        }
    }

    Variable getVariable(final String name) {
        final Iterator<Variable> itr = variables.descendingIterator();

        while (itr.hasNext()) {
            final Variable variable = itr.next();

            if (variable.name.equals(name)) {
                return variable;
            }
        }

        return null;
    }

     void addVariable(final String name, final Type ptype) {
        if (getVariable(name) != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Variable previous = variables.peekLast();
        int aslot = 0;

        if (previous != null) {
            aslot += previous.slot + previous.type.metadata.size;
        }

        final Variable pvariable = new Variable(name, ptype, aslot);
        variables.add(pvariable);

        final int update = scopes.pop() + 1;
        scopes.push(update);
    }

    StatementMetadata createStatementMetadata(final ParseTree source) {
        final StatementMetadata sourceSMD = new StatementMetadata(source);
        statementMetadata.put(source, sourceSMD);

        return sourceSMD;
    }

    StatementMetadata getStatementMetadata(final ParseTree source) {
        final StatementMetadata sourceSMD = statementMetadata.get(source);

        if (sourceSMD == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return sourceSMD;
    }

    ExpressionMetadata createExpressionMetadata(final ParseTree source) {
        final ExpressionMetadata sourceSMD = new ExpressionMetadata(source);
        expressionMetadata.put(source, sourceSMD);

        return sourceSMD;
    }

    ExpressionMetadata getExpressionMetadata(final ParseTree source) {
        final ExpressionMetadata sourceSMD = expressionMetadata.get(source);

        if (sourceSMD == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return sourceSMD;
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
        } else if (!emd.anyNumeric && emd.anyType) {
            throw new IllegalStateException(); // TODO: message
        }
    }

    Object getLegalCast(final Type from, final Type to, final boolean force, final boolean ignore) {
        final Cast cast = new Cast(from, to);

        if (from.equals(to)) {
            return cast;
        }

        if (!ignore && types.disallowed.contains(cast)) {
            throw new ClassCastException(); // TODO: message
        }

        final Transform explicit = types.explicits.get(cast);

        if (force && explicit != null) {
            return explicit;
        }

        final Transform implicit = types.implicits.get(cast);

        if (implicit != null) {
            return implicit;
        }

        if (types.upcasts.contains(cast)) {
            return cast;
        }

        if (from.metadata.numeric && to.metadata.numeric && (force || types.numerics.contains(cast))) {
            return cast;
        }

        try {
            from.clazz.asSubclass(to.clazz);

            return null;
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

    void constCast(final ExpressionMetadata emd) {
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
                            emd.postConst = (char)number.longValue();
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

    Object invokeTransform(final Transform transform, final Object object) {
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

    Type getUnaryNumericPromotion(final Type pfrom, boolean decimal) {
        return getBinaryNumericPromotion(pfrom, null , decimal);
    }

    Type getBinaryNumericPromotion(final Type from0, final Type from1, boolean decimal) {
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

            if (types.disallowed.contains(cast0)) continue;
            if (from0.metadata.numeric && from0.metadata != to.metadata &&
                    !types.numerics.contains(cast0)) continue;
            if (upcast.contains(from0)) continue;
            if (downcast.contains(from0) && !types.numerics.contains(cast0) &&
                    !types.implicits.containsKey(cast0)) continue;
            if (!from0.metadata.numeric && !types.implicits.containsKey(cast0)) continue;

            if (from1 != null) {
                final Cast cast1 = new Cast(from1, to);

                if (types.disallowed.contains(cast1)) continue;
                if (from1.metadata.numeric && from1.metadata != to.metadata &&
                        !types.numerics.contains(cast1)) continue;
                if (upcast.contains(from1)) continue;
                if (downcast.contains(from1) && !types.numerics.contains(cast1) &&
                        !types.implicits.containsKey(cast1)) continue;
                if (!from1.metadata.numeric && !types.implicits.containsKey(cast1)) continue;
            }

            return to;
        }

        return null;
    }

    Type getBinaryAnyPromotion(final Type from0, final Type from1) {
        if (from0.equals(from1)) {
            return from0;
        }

        final TypeMetadata tmd0 = from0.metadata;
        final TypeMetadata tmd1 = from1.metadata;

        if (tmd0 == TypeMetadata.BOOL || tmd1 == TypeMetadata.BOOL) {
            final Cast cast = new Cast(tmd0 == TypeMetadata.BOOL ? from1 : from0, standard.boolType);

            if (!types.disallowed.contains(cast) && types.implicits.containsKey(cast)) {
                return standard.boolType;
            }
        }

        if (tmd0.numeric || tmd1.numeric) {
            Type type = getBinaryNumericPromotion(from0, from1, true);

            if (type != null) {
                return type;
            }
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
                if (!types.disallowed.contains(cast0) && types.implicits.containsKey(cast0)) {
                    return cast0.to;
                }

                if (!types.disallowed.contains(cast1) && types.implicits.containsKey(cast1)) {
                    return cast1.to;
                }
            }

            return standard.objectType;
        }

        try {
            from0.clazz.asSubclass(from1.clazz);
            final Cast cast = new Cast(from0, from1);

            if (!types.disallowed.contains(cast)) {
                return from1;
            }
        } catch (ClassCastException cce0) {
            // Do nothing.
        }

        try {
            from1.clazz.asSubclass(from1.clazz);
            final Cast cast = new Cast(from1, from0);

            if (!types.disallowed.contains(cast)) {
                return from0;
            }
        } catch (ClassCastException cce0) {
            // Do nothing.
        }

        if (tmd0.object && tmd1.object) {
            return standard.objectType;
        }

        return null;
    }
}
