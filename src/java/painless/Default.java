package painless;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static painless.Types.*;

public class Default {
    static class Standard {
        final Type voidType;
        final Type boolType;
        final Type byteType;
        final Type shortType;
        final Type charType;
        final Type intType;
        final Type longType;
        final Type floatType;
        final Type doubleType;
        final Type objectType;
        final Type stringType;
        final Type execType;
        final Type listType;
        final Type mapType;
        final Type smapType;

        Standard(final Types types) {
            validateExact(types, "void", void.class);
            validateExact(types, "bool", boolean.class);
            validateExact(types, "byte", byte.class);
            validateExact(types, "short", short.class);
            validateExact(types, "char", char.class);
            validateExact(types, "int", int.class);
            validateExact(types, "long", long.class);
            validateExact(types, "float", float.class);
            validateExact(types, "double", double.class);
            validateExact(types, "object", Object.class);
            validateExact(types, "string", String.class);
            validateSubclass(types, "exec", Executable.class);
            validateSubclass(types, "list", List.class);
            validateSubclass(types, "map", Map.class);
            validateSubclass(types, "smap", Map.class);

            voidType = getTypeFromCanonicalName(types, "void");
            boolType = getTypeFromCanonicalName(types, "bool");
            byteType = getTypeFromCanonicalName(types, "byte");
            shortType = getTypeFromCanonicalName(types, "short");
            charType = getTypeFromCanonicalName(types, "char");
            intType = getTypeFromCanonicalName(types, "int");
            longType = getTypeFromCanonicalName(types, "long");
            floatType = getTypeFromCanonicalName(types, "float");
            doubleType = getTypeFromCanonicalName(types, "double");
            objectType = getTypeFromCanonicalName(types, "object");
            stringType = getTypeFromCanonicalName(types, "string");
            execType = getTypeFromCanonicalName(types, "exec");
            listType = getTypeFromCanonicalName(types, "list");
            mapType = getTypeFromCanonicalName(types, "map");
            smapType = getTypeFromCanonicalName(types, "smap");

            validateMapsAndLists(types);
        }

        private void validateExact(final Types types, final String name, final Class clazz) {
            final Types.Struct struct = types.structs.get(name);

            if (struct == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (!clazz.equals(struct.clazz)) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        private void validateSubclass(final Types types, final String name, final Class clazz) {
            final Types.Struct struct = types.structs.get(name);

            if (struct == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            try {
                struct.clazz.asSubclass(clazz);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        private void validateMapsAndLists(final Types types) {
            for (final Struct struct : types.structs.values()) {
                try {
                    struct.clazz.asSubclass(List.class);

                    final Method get = struct.methods.get("get");
                    final Method add = struct.methods.get("add");

                    if (get == null) {
                        throw new IllegalArgumentException();
                    }

                    if (get.method.getParameterCount() != 1 ||
                            !Object.class.equals(get.method.getReturnType()) ||
                            !int.class.equals(get.method.getParameterTypes()[0])) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (add.method.getParameterCount() != 2 ||
                            !void.class.equals(add.method.getReturnType()) ||
                            !int.class.equals(add.method.getParameterTypes()[0]) ||
                            !Object.class.equals(add.method.getParameterTypes()[1])) {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                } catch (ClassCastException exception) {
                    // Do nothing.
                }

                try {
                    struct.clazz.asSubclass(Map.class);

                    final Method get = struct.methods.get("get");
                    final Method put = struct.methods.get("put");

                    if (get == null) {
                        throw new IllegalArgumentException();
                    }

                    if (get.method.getParameterCount() != 1 ||
                            !Object.class.equals(get.method.getReturnType()) ||
                            !Object.class.equals(get.method.getParameterTypes()[0])) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (put.method.getParameterCount() != 2 ||
                            !Object.class.equals(put.method.getReturnType()) ||
                            !Object.class.equals(put.method.getParameterTypes()[0]) ||
                            !Object.class.equals(put.method.getParameterTypes()[1])) {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                } catch (ClassCastException exception) {
                    // Do nothing.
                }
            }
        }
    }

    final static Types DEFAULT_TYPES;
    final static Standard DEFAULT_STANDARD;

    static {
        DEFAULT_TYPES = loadFromProperties();
        DEFAULT_STANDARD = new Standard(DEFAULT_TYPES);
    }

    private Default() {}
}
