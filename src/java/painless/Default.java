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
    }

    final static Types DEFAULT_TYPES;
    final static Standard DEFAULT_STANDARD;

    static {
        DEFAULT_TYPES = loadFromProperties();
        DEFAULT_STANDARD = new Standard(DEFAULT_TYPES);
    }

    private Default() {}
}
