package painless;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

class Types {
    enum TypeMetadata {
        VOID(   void.class    , 0 , false , false , false ),
        BOOL(   boolean.class , 1 , false , true  , false ),
        BYTE(   byte.class    , 1 , true  , true  , false ),
        SHORT(  short.class   , 1 , true  , true  , false ),
        CHAR(   char.class    , 1 , true  , true  , false ),
        INT(    int.class     , 1 , true  , true  , false ),
        LONG(   long.class    , 2 , true  , true  , false ),
        FLOAT(  float.class   , 1 , true  , true  , false ),
        DOUBLE( double.class  , 2 , true  , true  , false ),
        OBJECT( null          , 1 , false , false , true  ),
        STRING( String.class  , 1 , false , true  , true  ),
        ARRAY(  null          , 1 , false , false , true  );

        final Class clazz;
        final int size;
        final boolean numeric;
        final boolean constant;
        final boolean object;

        TypeMetadata(final Class clazz, final int size, final boolean numeric,
                     final boolean constant, final boolean object) {
            this.clazz = clazz;
            this.size = size;
            this.numeric = numeric;
            this.constant = constant;
            this.object = object;
        }
    }

    static class Type {
        final Struct struct;
        final Class clazz;
        final String internal;
        final String descriptor;
        final int dimensions;
        final TypeMetadata metadata;

        Type(final Struct struct, final Class clazz, final String internal, final String descriptor,
             final int dimensions, final TypeMetadata metadata) {
            this.struct = struct;
            this.clazz = clazz;
            this.internal = internal;
            this.descriptor = descriptor;
            this.dimensions = dimensions;
            this.metadata = metadata;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final Type type = (Type)object;

            return dimensions == type.dimensions && struct.equals(type.struct);
        }

        @Override
        public int hashCode() {
            int result = struct.hashCode();
            result = 31 * result + dimensions;

            return result;
        }
    }

    static class Constructor {
        final String name;
        final Struct owner;
        final List<Type> arguments;
        final List<Type> originals;
        final java.lang.reflect.Constructor constructor;
        final String descriptor;

        private Constructor(final String name, final Struct owner,
                            final List<Type> arguments, final List<Type> originals,
                            final java.lang.reflect.Constructor constructor, final String descriptor) {
            this.name = name;
            this.owner = owner;
            this.arguments = Collections.unmodifiableList(arguments);
            this.originals = Collections.unmodifiableList(originals);
            this.constructor = constructor;
            this.descriptor = descriptor;
        }
    }

    static class Method {
        final String name;
        final Struct owner;
        final Type rtn;
        final Type oreturn;
        final List<Type> arguments;
        final List<Type> originals;
        final java.lang.reflect.Method method;
        final String descriptor;

        private Method(final String name, final Struct owner, final Type rtn, final Type oreturn,
                       final List<Type> arguments, final List<Type> originals,
                       final java.lang.reflect.Method method, final String descriptor) {
            this.name = name;
            this.owner = owner;
            this.rtn = rtn;
            this.oreturn = oreturn;
            this.arguments = Collections.unmodifiableList(arguments);
            this.originals = Collections.unmodifiableList(originals);
            this.method = method;
            this.descriptor = descriptor;
    }
    }

    static class Field {
        final String name;
        final Struct owner;
        final Type type;
        final java.lang.reflect.Field field;

        private Field(final String name, final Struct owner, final Type type, final java.lang.reflect.Field field) {
            this.name = name;
            this.owner = owner;
            this.type = type;
            this.field = field;
        }
    }

    static class Struct {
        final String name;
        final Class clazz;
        final String internal;
        final boolean generic;

        final Map<String, Constructor> constructors;
        final Map<String, Method> functions;
        final Map<String, Method> methods;

        final Map<String, Field> statics;
        final Map<String, Field> members;

        private Struct(final String name, final Class clazz, final String internal, final boolean generic) {
            this.name = name;
            this.clazz = clazz;
            this.internal = internal;
            this.generic = generic;

            constructors = new HashMap<>();
            functions = new HashMap<>();
            methods = new HashMap<>();

            statics = new HashMap<>();
            members = new HashMap<>();
        }

        private Struct(final Struct struct) {
            name = struct.name;
            clazz = struct.clazz;
            internal = struct.internal;
            generic = struct.generic;

            constructors = Collections.unmodifiableMap(struct.constructors);
            functions = Collections.unmodifiableMap(struct.functions);
            methods = Collections.unmodifiableMap(struct.methods);

            statics = Collections.unmodifiableMap(struct.statics);
            members = Collections.unmodifiableMap(struct.members);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            Struct struct = (Struct)object;

            return name.equals(struct.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    static class Cast {
        final Type from;
        final Type to;

        Cast(final Type from, final Type to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final Cast pcast = (Cast)object;

            if (!from.equals(pcast.from)) {
                return false;
            }

            return to.equals(pcast.to);
        }

        @Override
        public int hashCode() {
            int result = from.hashCode();
            result = 31 * result + to.hashCode();

            return result;
        }
    }

    static class Transform {
        final Cast cast;
        final Method method;
        final Type from;
        final Type to;

        private Transform(final Cast cast, Method method, final Type from, final Type to) {
            this.cast = cast;
            this.method = method;
            this.from = from;
            this.to = to;
        }
    }

    private static final String PROPERTIES_FILE = Types.class.getSimpleName() + ".properties";

    static Types loadFromProperties() {
        final Properties properties = new Properties();

        try (final InputStream stream = Types.class.getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException(); // TODO: message
        }

        return loadFromProperties(properties);
    }

    static Types loadFromProperties(final Properties properties) {
        final Types types = new Types();

        for (String key : properties.stringPropertyNames()) {
            final String property = properties.getProperty(key);

            if (key.startsWith("class")) {
                loadStructFromProperty(types, property, false);
            } else if (key.startsWith("generic")) {
                loadStructFromProperty(types, property, true);
            } else {
                boolean valid = key.startsWith("constructor") ||
                                key.startsWith("function")    || key.startsWith("method") ||
                                key.startsWith("static")      || key.startsWith("member") ||
                                key.startsWith("transform")   || key.startsWith("upcast") ||
                                key.startsWith("disallow");

                if (!valid) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        for (String key : properties.stringPropertyNames()) {
            final String property = properties.getProperty(key);

            if (key.startsWith("constructor")) {
                loadConstructorFromProperty(types, property);
            } else if (key.startsWith("function")) {
                loadMethodFromProperty(types, property, true);
            } else if (key.startsWith("method")) {
                loadMethodFromProperty(types, property, false);
            } else if (key.startsWith("static")) {
                loadFieldFromProperty(types, property, true);
            } else if (key.startsWith("member")) {
                loadFieldFromProperty(types, property, false);
            }
        }

        for (String key : properties.stringPropertyNames()) {
            final String property = properties.getProperty(key);

            if (key.startsWith("transform")) {
                loadTransformFromProperty(types, property);
            } else if (key.startsWith("upcast")) {
                loadUpcastFromProperty(types, property);
            } else if (key.startsWith("disallow")) {
                loadDisallowFromProperty(types, property);
            }
        }

        validateMethods(types);

        return new Types(types);
    }

    private static void loadStructFromProperty(final Types types, final String property, final boolean generic) {
        final String[] split = property.split("\\s+");

        if (split.length != 2) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String namestr = split[0];
        final String clazzstr = split[1];

        loadStruct(types, namestr, clazzstr, generic);
    }

    private static void loadConstructorFromProperty(final Types types, final String property) {
        String parsed = property;
        int index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String ownerstr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String namestr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();

        final String[][] argumentsstrs = parseArgumentsStr(parsed);

        loadConstructor(types, ownerstr, namestr, argumentsstrs);
    }

    private static void loadMethodFromProperty(final Types types, final String property, final boolean statik) {
        String parsed = property;
        int index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String ownerstr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String namestr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String returnstr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf('(');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String clazzstr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();

        final String[][] argumentsstrs = parseArgumentsStr(parsed);

        loadMethod(types, ownerstr, namestr, returnstr, clazzstr, argumentsstrs, statik);
    }

    private static void loadFieldFromProperty(final Types types, final String property, final boolean statik) {
        final String[] split = property.split("\\s+");

        if (split.length != 4) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String ownerstr = split[0];
        final String namestr = split[1];
        final String typestr = split[2];
        final String clazzstr = split[3];

        loadField(types, ownerstr, namestr, typestr, clazzstr, statik);
    }

    private static void loadTransformFromProperty(final Types types, final String property) {
        final String[] split = property.split("\\s+");

        if (split.length != 6) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String typestr = split[0];
        final String fromstr = split[1];
        final String tostr = split[2];
        final String ownerstr = split[3];
        final String staticstr = split[4];
        final String methodstr = split[5];

        loadTransform(types, typestr, fromstr, tostr, ownerstr, staticstr, methodstr);
    }

    private static void loadUpcastFromProperty(final Types types, final String property) {
        final String[] split = property.split("\\s+");

        if (split.length != 2) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String fromstr = split[0];
        final String tostr = split[1];

        loadUpcast(types, fromstr, tostr);
    }

    private static void loadDisallowFromProperty(final Types types, final String property) {
        final String[] split = property.split("\\s+");

        if (split.length != 2) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String fromstr = split[0];
        final String tostr = split[1];

        loadDisallow(types, fromstr, tostr);
    }

    private static void loadStruct(final Types types, final String namestr,
                                   final String clazzstr, final boolean generic) {
        if (!namestr.matches("^[_a-zA-Z][_a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.structs.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Class clazz = getClassFromCanonicalName(clazzstr);
        final String internal = clazz.getName().replace('.', '/');
        final Struct struct = new Struct(namestr, clazz, internal, generic);

        types.structs.put(namestr, struct);
    }

    private static void loadConstructor(final Types types, final String ownerstr,
                                         final String namestr, final String[][] argumentsstrs) {
        final Struct owner = types.structs.get(ownerstr);

        if (owner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!namestr.matches("^[a-zA-Z][a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.constructors.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.statics.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.methods.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final int length = argumentsstrs.length;
        final Type[] arguments = new Type[length];
        final Type[] originals = new Type[length];
        final Class[] jarguments = new Class[length];

        String descriptor = "(";

        for (int argumentindex = 0; argumentindex < length; ++ argumentindex) {
            final String[] argumentstrs = argumentsstrs[argumentindex];

            if (argumentstrs.length == 1) {
                arguments[argumentindex] = getTypeFromCanonicalName(types, argumentstrs[0]);
                originals[argumentindex] = arguments[argumentindex];
            } else if (argumentstrs.length == 2) {
                arguments[argumentindex] = getTypeFromCanonicalName(types, argumentstrs[1]);
                originals[argumentindex] = getTypeFromCanonicalName(types, argumentstrs[0]);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }

            jarguments[argumentindex] = originals[argumentindex].clazz;
            descriptor += originals[argumentindex].descriptor;
        }

        descriptor += ")V";

        final java.lang.reflect.Constructor jconstructor = getJConstructorFromJClass(owner.clazz, jarguments);
        final Constructor constructor = new Constructor(namestr, owner,
                Arrays.asList(arguments), Arrays.asList(originals), jconstructor, descriptor);

        owner.constructors.put(namestr, constructor);
    }

    private static void loadMethod(final Types types, final String ownerstr, final String namestr,
                                    final String returnstr, final String clazzstr,
                                    final String[][] argumentsstrs, boolean statik) {
        final Struct owner = types.structs.get(ownerstr);

        if (owner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!namestr.matches("^[_a-zA-Z][_a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.constructors.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.statics.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.methods.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String[] returnstrs = parseArgumentStr(returnstr);
        Type rtn;
        Type oreturn;
        Class jreturn;

        if (returnstrs.length == 1) {
            rtn = getTypeFromCanonicalName(types, returnstrs[0]);
            oreturn = rtn;
        } else if (returnstrs.length == 2) {
            rtn = getTypeFromCanonicalName(types, returnstrs[1]);
            oreturn = getTypeFromCanonicalName(types, returnstrs[0]);
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }

        jreturn = oreturn.clazz;

        final int length = argumentsstrs.length;
        final Type[] arguments = new Type[length];
        final Type[] originals = new Type[length];
        final Class[] jarguments = new Class[length];

        String descriptor = "(";

        for (int argumentindex = 0; argumentindex < length; ++ argumentindex) {
            final String[] argumentstrs = argumentsstrs[argumentindex];

            if (argumentstrs.length == 1) {
                arguments[argumentindex] = getTypeFromCanonicalName(types, argumentstrs[0]);
                originals[argumentindex] = arguments[argumentindex];
            } else if (argumentstrs.length == 2) {
                arguments[argumentindex] = getTypeFromCanonicalName(types, argumentstrs[1]);
                originals[argumentindex] = getTypeFromCanonicalName(types, argumentstrs[0]);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }

            jarguments[argumentindex] = originals[argumentindex].clazz;

            descriptor += originals[argumentindex].descriptor;
        }

        descriptor += ")" + oreturn.descriptor;

        final java.lang.reflect.Method jmethod = getJMethodFromJClass(owner.clazz, clazzstr, jarguments);

        if (!jreturn.equals(jmethod.getReturnType())) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Method method = new Method(namestr, owner, rtn, oreturn,
                Arrays.asList(arguments), Arrays.asList(originals), jmethod, descriptor);
        final int modifiers = jmethod.getModifiers();

        if (statik) {
            if (!java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            owner.functions.put(namestr, method);
        } else {
            if (java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            owner.methods.put(namestr, method);
        }
    }

    private static void loadField(final Types types, final String ownerstr, final String namestr,
                                  final String typestr, final String jnamestr, final boolean statik) {
        final Struct owner = types.structs.get(ownerstr);

        if (owner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!namestr.matches("^[a-zA-Z][a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.statics.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (owner.members.containsKey(namestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Type type = getTypeFromCanonicalName(types, typestr);
        final java.lang.reflect.Field jfield = getJFieldFromJClass(owner.clazz, jnamestr);
        final Field field = new Field(namestr, owner, type, jfield);
        final int modifiers = jfield.getModifiers();

        if (statik) {
            if (!java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (!java.lang.reflect.Modifier.isFinal(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            owner.statics.put(namestr, field);
        } else {
            if (java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            owner.members.put(namestr, field);
        }
    }

    private static void loadTransform(final Types types, final String typestr,
                                      final String fromstr, final String tostr, final String ownerstr,
                                      final String staticstr, final String methodstr) {
        final Struct owner = types.structs.get(ownerstr);

        if (owner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Type from = getTypeFromCanonicalName(types, fromstr);
        final Type to = getTypeFromCanonicalName(types, tostr);

        if (from.equals(to)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Cast cast = new Cast(from, to);

        if (types.disallowed.contains(cast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.upcasts.contains(cast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        Method method;
        Type castfrom = null;
        Type castto = null;

        if ("function".equals(staticstr)) {
            method = owner.functions.get(methodstr);

            if (method == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (method.arguments.size() != 1) {
                throw new IllegalArgumentException(); // TODO: message
            }

            Type argument = method.arguments.get(0);

            try {
                from.clazz.asSubclass(argument.clazz);
            } catch (ClassCastException cce0) {
                try {
                    argument.clazz.asSubclass(from.clazz);
                    castfrom = argument;
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }

            final Type rtn = method.rtn;

            try {
                rtn.clazz.asSubclass(to.clazz);
            } catch (ClassCastException cce0) {
                try {
                    to.clazz.asSubclass(rtn.clazz);
                    castto = to;
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        } else if ("method".equals(staticstr)) {
            method = owner.methods.get(methodstr);

            if (method == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (!method.arguments.isEmpty()) {
                throw new IllegalArgumentException(); // TODO: message
            }

            try {
                from.clazz.asSubclass(owner.clazz);
            } catch (ClassCastException cce0) {
                try {
                    owner.clazz.asSubclass(from.clazz);
                    castfrom = getTypeFromCanonicalName(types, owner.name);
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }

            final Type rtn = method.rtn;

            try {
                rtn.clazz.asSubclass(to.clazz);
            } catch (ClassCastException cce0) {
                try {
                    to.clazz.asSubclass(rtn.clazz);
                    castto = to;
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Transform transform = new Transform(cast, method, castfrom, castto);

        if ("explicit".equals(typestr)) {
            if (types.explicits.containsKey(cast)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            types.explicits.put(cast, transform);
        } else if ("implicit".equals(typestr)) {
            if (types.implicits.containsKey(cast)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            types.implicits.put(cast, transform);
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static void loadUpcast(final Types types, final String fromstr, final String tostr) {
        final Type from = getTypeFromCanonicalName(types, fromstr);
        final Type to = getTypeFromCanonicalName(types, tostr);

        if (from.equals(to)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Cast cast = new Cast(from, to);

        if (types.disallowed.contains(cast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.upcasts.contains(cast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.explicits.containsKey(cast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.implicits.containsKey(cast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        try {
            to.clazz.asSubclass(from.clazz);
        } catch (ClassCastException expcetion) {
            throw new IllegalArgumentException();
        }

        types.upcasts.add(cast);
    }

    private static void loadDisallow(final Types types, final String fromstr, final String tostr) {
        final Type from = getTypeFromCanonicalName(types, fromstr);
        final Type to = getTypeFromCanonicalName(types, tostr);

        if (from.equals(to)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Cast pcast = new Cast(from, to);

        if (types.disallowed.contains(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.upcasts.contains(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.explicits.containsKey(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (types.implicits.containsKey(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        types.disallowed.add(pcast);
    }

    private static String[][] parseArgumentsStr(final String argumentstr) {
        if (!argumentstr.startsWith("(") || !argumentstr.endsWith(")")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String tidy = argumentstr.substring(1, argumentstr.length() - 1).replace(" ", "");

        if ("".equals(tidy)) {
            return new String[0][];
        }

        final String[] argumentsstr = tidy.split(",");
        final String[][] argumentsstrs = new String[argumentsstr.length][];

        for (int argumentindex = 0; argumentindex < argumentsstr.length; ++argumentindex) {
            argumentsstrs[argumentindex] = parseArgumentStr(argumentsstr[argumentindex]);
        }

        return argumentsstrs;
    }

    private static String[] parseArgumentStr(final String argumentstr) {
        final String[] argumentstrs = argumentstr.split("\\^");

        if (argumentstrs.length == 1) {
            return new String[] {argumentstrs[0]};
        } else if (argumentstrs.length == 2) {
            return new String[] {argumentstrs[0], argumentstrs[1]};
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    static Type getTypeFromCanonicalName(final Types types, final String namestr) {
        final int dimensions = getArrayDimensionsFromCanonicalName(namestr);
        final String pclassstr = dimensions == 0 ? namestr : namestr.substring(0, namestr.indexOf('['));
        final Struct pclass = types.structs.get(pclassstr);

        if (pclass == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        return getTypeWithArrayDimensions(pclass, dimensions);
    }

    static Type getTypeWithArrayDimensions(final Struct struct, final int dimensions) {
        if (dimensions == 0) {
            final Class clazz = struct.clazz;
            final String internal = struct.internal;
            final String descriptor = getDescriptorFromClass(clazz);

            TypeMetadata metadata = TypeMetadata.OBJECT;

            for (TypeMetadata value : TypeMetadata.values()) {
                if (value.clazz == null) {
                    continue;
                }

                if (value.clazz.equals(metadata.clazz)) {
                    metadata = value;

                    break;
                }
            }

            return new Type(struct, clazz, internal, descriptor, 0, metadata);
        } else {
            final char[] brackets = new char[dimensions*2];

            for (int index = 0; index < brackets.length; ++index) {
                brackets[index] = '[';
                brackets[++index] = ']';
            }

            final String namestr = struct.clazz.getCanonicalName() + new String(brackets);
            final Class clazz = getClassFromCanonicalName(namestr);
            final String internal = clazz.getName().replace('.', '/');
            final String descriptor = getDescriptorFromClass(clazz);

            return new Type(struct, clazz, internal, descriptor, 0, TypeMetadata.ARRAY);
        }
    }

    private static Class getClassFromCanonicalName(final String namestr) {
        try {
            final int dimensions = getArrayDimensionsFromCanonicalName(namestr);

            if (dimensions == 0) {
                switch (namestr) {
                    case "void":
                        return void.class;
                    case "boolean":
                        return boolean.class;
                    case "byte":
                        return byte.class;
                    case "char":
                        return char.class;
                    case "short":
                        return short.class;
                    case "int":
                        return int.class;
                    case "long":
                        return long.class;
                    case "float":
                        return float.class;
                    case "double":
                        return double.class;
                    default:
                        return Class.forName(namestr);
                }
            } else {
                String jclassstr = namestr.substring(0, namestr.indexOf('['));

                char[] brackets = new char[dimensions];
                Arrays.fill(brackets, '[');
                String descriptor = new String(brackets);

                switch (jclassstr) {
                    case "void":
                        descriptor += "V";
                        break;
                    case "boolean":
                        descriptor += "Z";
                        break;
                    case "byte":
                        descriptor += "B";
                        break;
                    case "char":
                        descriptor += "C";
                        break;
                    case "short":
                        descriptor += "S";
                        break;
                    case "int":
                        descriptor += "I";
                        break;
                    case "long":
                        descriptor += "J";
                        break;
                    case "float":
                        descriptor += "F";
                        break;
                    case "double":
                        descriptor += "D";
                        break;
                    default:
                        descriptor += "L" + jclassstr + ";";
                }

                return Class.forName(descriptor);
            }
        } catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static int getArrayDimensionsFromCanonicalName(final String name) {
        int dimensions = 0;
        int index = name.indexOf('[');

        if (index != -1) {
            final int length = name.length();

            while (index < length) {
                if (name.charAt(index) == '[' && ++index < length && name.charAt(index++) == ']') {
                    ++dimensions;
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        return dimensions;
    }

    private static String getDescriptorFromClass(final Class clazz) {
        final String namestr = clazz.getName();

        switch (namestr) {
            case "void":
                return "V";
            case "boolean":
                return "Z";
            case "byte":
                return "B";
            case "char":
                return "C";
            case "short":
                return "S";
            case "int":
                return "I";
            case "long":
                return "J";
            case "float":
                return "F";
            case "double":
                return "D";
            default:
                String descriptor = namestr.replace('.', '/');

                if (!descriptor.startsWith("[")) {
                    descriptor = "L" + descriptor + ";";
                }

                return descriptor;
        }
    }

    private static java.lang.reflect.Constructor getJConstructorFromJClass(final Class clazz, final Class[] arguments) {
        try {
            return clazz.getConstructor(arguments);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static java.lang.reflect.Method getJMethodFromJClass(final Class clazz, final String namestr,
                                                                 final Class[] arguments) {
        try {
            return clazz.getMethod(namestr, arguments);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static java.lang.reflect.Field getJFieldFromJClass(final Class clazz, final String namestr) {
        try {
            return clazz.getField(namestr);
        } catch (NoSuchFieldException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static void validateMethods(final Types tyes) {
        for (final Struct struct : tyes.structs.values()) {
            for (final Constructor constructor : struct.constructors.values()) {
                final int length = constructor.arguments.size();
                final List<Type> arguments = constructor.arguments;
                final List<Type> originals = constructor.originals;

                for (int argument = 0; argument < length; ++argument) {
                    validateArgument(tyes, arguments.get(argument), originals.get(argument));
                }
            }

            for (final Method function : struct.functions.values()) {
                final int length = function.arguments.size();
                final List<Type> arguments = function.arguments;
                final List<Type> originals = function.originals;

                for (int argument = 0; argument < length; ++argument) {
                    validateArgument(tyes, arguments.get(argument), originals.get(argument));
                }

                validateArgument(tyes, function.rtn, function.oreturn);
            }

            for (final Method method : struct.methods.values()) {
                final int length = method.arguments.size();
                final List<Type> arguments = method.arguments;
                final List<Type> originals = method.originals;

                for (int argument = 0; argument < length; ++argument) {
                    validateArgument(tyes, arguments.get(argument), originals.get(argument));
                }

                validateArgument(tyes, method.rtn, method.oreturn);
            }
        }
    }

    private static void validateArgument(final Types types, final Type argument, final Type original) {
        final Cast pcast = new Cast(argument, original);

        if (types.disallowed.contains(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!types.implicits.containsKey(pcast)) {
            try {
                argument.clazz.asSubclass(original.clazz);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }
    }

    final Map<String, Struct> structs;

    final Map<Cast, Transform> explicits;
    final Map<Cast, Transform> implicits;
    final Set<Cast> upcasts;
    final Set<Cast> disallowed;

    private Types() {
        structs = new HashMap<>();

        explicits = new HashMap<>();
        implicits = new HashMap<>();
        upcasts = new HashSet<>();
        disallowed = new HashSet<>();
    }

    private Types(Types types) {
        final Map<String, Struct> ummodifiable = new HashMap<>();

        for (final Struct struct : types.structs.values()) {
            ummodifiable.put(struct.name, new Struct(struct));
        }

        structs = Collections.unmodifiableMap(ummodifiable);

        explicits = Collections.unmodifiableMap(types.explicits);
        implicits = Collections.unmodifiableMap(types.implicits);
        upcasts = Collections.unmodifiableSet(types.upcasts);
        disallowed = Collections.unmodifiableSet(types.disallowed);
    }
}
