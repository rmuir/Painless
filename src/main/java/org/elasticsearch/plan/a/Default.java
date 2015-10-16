package org.elasticsearch.plan.a;

import java.util.List;
import java.util.Map;

import static org.elasticsearch.plan.a.Definition.*;

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

        Standard(final Definition definition) {
            validateExact(definition, "void", void.class);
            validateExact(definition, "bool", boolean.class);
            validateExact(definition, "byte", byte.class);
            validateExact(definition, "short", short.class);
            validateExact(definition, "char", char.class);
            validateExact(definition, "int", int.class);
            validateExact(definition, "long", long.class);
            validateExact(definition, "float", float.class);
            validateExact(definition, "double", double.class);
            validateExact(definition, "object", Object.class);
            validateExact(definition, "string", String.class);
            validateSubclass(definition, "exec", Executable.class);
            validateSubclass(definition, "list", List.class);
            validateSubclass(definition, "map", Map.class);
            validateSubclass(definition, "smap", Map.class);

            voidType = getTypeFromCanonicalName(definition, "void");
            boolType = getTypeFromCanonicalName(definition, "bool");
            byteType = getTypeFromCanonicalName(definition, "byte");
            shortType = getTypeFromCanonicalName(definition, "short");
            charType = getTypeFromCanonicalName(definition, "char");
            intType = getTypeFromCanonicalName(definition, "int");
            longType = getTypeFromCanonicalName(definition, "long");
            floatType = getTypeFromCanonicalName(definition, "float");
            doubleType = getTypeFromCanonicalName(definition, "double");
            objectType = getTypeFromCanonicalName(definition, "object");
            stringType = getTypeFromCanonicalName(definition, "string");
            execType = getTypeFromCanonicalName(definition, "exec");
            listType = getTypeFromCanonicalName(definition, "list");
            mapType = getTypeFromCanonicalName(definition, "map");
            smapType = getTypeFromCanonicalName(definition, "smap");

            //validateMapsAndLists(definition);
        }

        private void validateExact(final Definition definition, final String name, final Class clazz) {
            final Definition.Struct struct = definition.structs.get(name);

            if (struct == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (!clazz.equals(struct.clazz)) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        private void validateSubclass(final Definition definition, final String name, final Class clazz) {
            final Definition.Struct struct = definition.structs.get(name);

            if (struct == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            try {
                struct.clazz.asSubclass(clazz);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        /*private void validateMapsAndLists(final Definition definition) {
            for (final Struct struct : definition.structs.values()) {
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
        }*/
    }

    final static Definition DEFAULT_DEFINITION;
    final static Standard DEFAULT_STANDARD;
    final static Caster DEFAULT_CASTER;

    static {
        DEFAULT_DEFINITION = loadFromProperties();
        DEFAULT_STANDARD = new Standard(DEFAULT_DEFINITION);
        DEFAULT_CASTER = new Caster(DEFAULT_DEFINITION, DEFAULT_STANDARD);
    }

    private Default() {}
}
