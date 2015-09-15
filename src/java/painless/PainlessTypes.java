package painless;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.objectweb.asm.Type;

class PainlessTypes {
    static class PMethod {
        final String name;
        final PType owner;
        final String method;
        final PType rtn;
        final PType[] arguments;

        private PMethod(final String name, final PType owner, final String method, final PType rtn, final PType... arguments) {
            this.name = name;
            this.owner = owner;
            this.method = method;
            this.rtn = rtn;
            this.arguments = arguments;
        }
    }

    static class PMember {
        final String name;
        final PType owner;
        final String field;
        final PType type;

        private PMember(final String name, final PType owner, final String field, final PType type) {
            this.name = name;
            this.owner = owner;
            this.field = field;
            this.type = type;
        }
    }

    static class PType {
        final String name;
        final Class clazz;
        final Type type;

        private final Map<String, PMethod> functions;
        private final Map<String, PMember> statics;

        private final Map<String, PMethod> constructors;
        private final Map<String, PMethod> methods;
        private final Map<String, PMember> members;

        private PType(final String name, final Class clazz, final Type type) {
            this.name = name;
            this.clazz = clazz;
            this.type = type;

            this.functions = new HashMap<>();
            this.statics = new HashMap<>();

            this.constructors = new HashMap<>();
            this.methods = new HashMap<>();
            this.members = new HashMap<>();
        }

        PMethod getFunction(String function) {
            return functions.get(function);
        }

        PMember getStatic(String statik) {
            return statics.get(statik);
        }

        PMethod getConstructor(String constructor) {
            return constructors.get(constructor);
        }

        PMethod getMethod(String method) {
            return methods.get(method);
        }

        PMember getMember(String member) {
            return members.get(member);
        }
    }

    static class PCast {
        final String from;
        final String to;

        PCast(final String from, final String to) {
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

            final PCast cast = (PCast)object;

            if (!from.equals(cast.from)) {
                return false;
            }

            return to.equals(cast.to);
        }

        @Override
        public int hashCode() {
            int result = from.hashCode();
            result = 31 * result + to.hashCode();

            return result;
        }
    }

    private static Class getClass(final String clazz) throws ClassNotFoundException {
        switch (clazz) {
            case "void":
                return void.class;
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "char":
                return char.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            default:
                return Class.forName(clazz);
        }
    }

    private static Type getType(final String clazz) throws ClassNotFoundException {
        switch (clazz) {
            case "void":
                return Type.VOID_TYPE;
            case "boolean":
                return Type.BOOLEAN_TYPE;
            case "byte":
                return Type.BYTE_TYPE;
            case "short":
                return Type.SHORT_TYPE;
            case "char":
                return Type.CHAR_TYPE;
            case "int":
                return Type.INT_TYPE;
            case "long":
                return Type.LONG_TYPE;
            case "float":
                return Type.FLOAT_TYPE;
            case "double":
                return Type.DOUBLE_TYPE;
            default:
                return Type.getType(Class.forName(clazz).getName());
        }
    }

    private static boolean isNumeric(final Class clazz) {
        return  clazz.equals(byte.class) || clazz.equals(short.class) ||
                clazz.equals(char.class) || clazz.equals(int.class) ||
                clazz.equals(long.class) || clazz.equals(float.class) ||
                clazz.equals(double.class);
    }

    private static final String PROPERTIES_FILE = PainlessTypes.class.getSimpleName() + ".properties";

    private final Map<String, PType> ptypes;
    private final Set<PCast> implicits;
    private final Set<PCast> explicits;
    private final Map<PCast, PMethod> boxes;
    private final Map<PCast, PMethod> unboxes;

    PainlessTypes() {
        ptypes = new HashMap<>();
        implicits = new HashSet<>();
        explicits = new HashSet<>();
        boxes = new HashMap<>();
        unboxes = new HashMap<>();

        final Properties properties = new Properties();

        try (final InputStream stream = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }

        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith("type")) {
                final String[] keysplit = key.split("\\.");
                final String name = keysplit[1];

                if (ptypes.containsKey(name)) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);

                Class clazz;
                Type type;

                try {
                    clazz = getClass(property);
                    type = getType(property);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                }

                final PType ptype = new PType(name, clazz, type);
                ptypes.put(name, ptype);
            } else {
                boolean valid = key.startsWith("constructor") || key.startsWith("function") ||
                                key.startsWith("static")      || key.startsWith("method")   ||
                                key.startsWith("member")      || key.startsWith("cast")     ||
                                key.startsWith("box")         || key.startsWith("unbox")    ||
                                key.startsWith("cross");

                if (!valid) {
                    throw new IllegalArgumentException();
                }
            }
        }

        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith("constructor") || key.startsWith("function") || key.startsWith("method")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String type = keysplit[1];
                final String name = keysplit[2];
                final PType owner = ptypes.get(type);

                if (owner == null) {
                    throw new IllegalArgumentException();
                }

                String property = properties.getProperty(key);
                property = property.trim();

                int index = property.indexOf(" ");
                final PType rtn = ptypes.get(property.substring(0, index));

                if (rtn == null) {
                    throw new IllegalArgumentException();
                }

                property = property.substring(index + 1);
                index = property.indexOf("(");
                final String call = property.substring(0, index);

                if ("".equals(call)) {
                    throw new IllegalArgumentException();
                } else if ("constructor".equals(keysplit[0]) && !"<init>".equals(call)) {
                    throw new IllegalArgumentException();
                }

                property = property.substring(index);

                if (property.charAt(0) != '(' || property.charAt(property.length() - 1) != ')') {
                    throw new IllegalArgumentException();
                }

                property = property.replace("(", "").replace(")", "").replace(" ", "");
                final String[] propsplit = property.isEmpty() ? null : property.split(",");
                final PType[] arguments = new PType[propsplit == null ? 0 : propsplit.length];

                for (int argument = 0; argument < arguments.length; ++argument) {
                    final PType argtype = ptypes.get(propsplit[argument]);

                    if (argtype == null) {
                        throw new IllegalArgumentException();
                    }

                    arguments[argument] = argtype;
                }

                final PMethod pmethod = new PMethod(name, owner, call, rtn, arguments);

                if ("constructor".equals(keysplit[0])) {
                    owner.constructors.put(name, pmethod);
                } else if (key.startsWith("function")) {
                    owner.functions.put(name, pmethod);
                } else if (key.startsWith("method")) {
                    owner.methods.put(name, pmethod);
                } else {
                    throw new IllegalStateException();
                }
            } else if (key.startsWith("static") || key.startsWith("member")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String name = keysplit[2].trim();
                final PType owner = ptypes.get(keysplit[1].trim());

                if (owner == null) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String[] propsplit = property.split("\\s+");

                if (propsplit.length != 2) {
                    throw new IllegalArgumentException();
                }

                final String field = propsplit[1].trim();
                final PType type = ptypes.get(propsplit[0].trim());

                if ("".equals(field)) {
                    throw new IllegalArgumentException();
                }

                if (type == null) {
                    throw new IllegalArgumentException();
                }

                final PMember member = new PMember(name, owner, field, type);

                if (key.startsWith("static")) {
                    owner.statics.put(name, member);
                } else if (key.startsWith("member")) {
                    owner.members.put(name, member);
                } else {
                    throw new IllegalStateException();
                }
            } else if (key.startsWith("cast")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String[] propsplit = property.split("\\s+");

                if (propsplit.length != 2) {
                    throw new IllegalArgumentException();
                }

                final String from = propsplit[0].trim();
                final String to = propsplit[1].trim();

                if (!ptypes.containsKey(from)) {
                    throw new IllegalArgumentException();
                }

                if (!ptypes.containsKey(to)) {
                    throw new IllegalArgumentException();
                }

                final PCast cast = new PCast(from, to);

                if ("implicit".equals(keysplit[1])) {
                    implicits.add(cast);
                } else if ("explicit".equals(keysplit[1])) {
                    explicits.add(cast);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith("box") || key.startsWith("unbox")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String from = keysplit[1];
                final String to = keysplit[2];
                final PCast cast = new PCast(from, to);

                if (!implicits.contains(cast) && !explicits.contains(cast)) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String[] propsplit = property.split("\\s+");

                if (propsplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final PType owner = ptypes.get(propsplit[1]);

                if (owner == null) {
                    throw new IllegalArgumentException();
                }

                PMethod method;

                if ("function".equals(propsplit[0])) {
                    method = owner.methods.get(propsplit[2]);
                } else if ("method".equals(propsplit[0])) {
                    method = owner.methods.get(propsplit[2]);
                } else {
                    throw new IllegalArgumentException();
                }

                if ("box".equals(keysplit[0])) {
                    boxes.put(cast, method);
                } else if ("unbox".equals(keysplit[0])) {
                    unboxes.put(cast, method);
                } else {
                    throw new IllegalArgumentException();
                }
            } else if (key.startsWith("cross")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 4) {
                    throw new IllegalArgumentException();
                }

                final PType owner = ptypes.get(keysplit[2]);

                if (owner == null) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String[] propsplit = property.split("\\s+");

                if (propsplit.length != 2) {
                    throw new IllegalArgumentException();
                }

                final PType origin = ptypes.get(propsplit[0]);

                if (origin == null) {
                    throw new IllegalArgumentException();
                }

                if ("function".equals(keysplit[1])) {
                    PMethod function = origin.functions.get(propsplit[1]);

                    if (function == null) {
                        throw new IllegalArgumentException();
                    }

                    owner.functions.put(keysplit[3], function);
                } else if ("static".equals(keysplit[1])) {
                    PMember statik = origin.statics.get(propsplit[1]);

                    if (statik == null) {
                        throw new IllegalArgumentException();
                    }

                    owner.statics.put(keysplit[3], statik);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        for (final PType ptype : ptypes.values()) {
            for (final PMethod function : ptype.functions.values()) {
                final String name = function.method;
                final Class[] arguments = new Class[function.arguments.length];

                for (int argument = 0; argument < arguments.length; ++argument) {
                    arguments[argument] = function.arguments[argument].clazz;
                }

                java.lang.reflect.Method validate;

                try {
                    validate = function.owner.clazz.getMethod(name, arguments);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                if (!function.rtn.clazz.equals(validate.getReturnType())) {
                    throw new IllegalArgumentException();
                }

                if (!Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMember statik : ptype.statics.values()) {
                Field field;

                try {
                    field = statik.owner.clazz.getField(statik.field);
                } catch (NoSuchFieldException exception) {
                    throw new IllegalArgumentException();
                }

                if (!statik.type.clazz.equals(field.getType())) {
                    throw new IllegalArgumentException();
                }

                final int modifiers = field.getModifiers();

                if (!Modifier.isStatic(modifiers)) {
                    throw new IllegalArgumentException();
                }

                if (!Modifier.isFinal(modifiers)) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMethod constructor : ptype.constructors.values()) {
                final Class[] arguments = new Class[constructor.arguments.length];

                for (int argument = 0; argument < arguments.length; ++argument) {
                    arguments[argument] = constructor.arguments[argument].clazz;
                }

                java.lang.reflect.Constructor validate;

                try {
                    validate = constructor.owner.clazz.getConstructor(arguments);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                if (!constructor.rtn.clazz.equals(void.class)) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }

                if (!constructor.owner.clazz.equals(ptype.clazz)) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMethod method : ptype.methods.values()) {
                final String name = method.method;
                final Class[] arguments = new Class[method.arguments.length];

                for (int argument = 0; argument < arguments.length; ++argument) {
                    arguments[argument] = method.arguments[argument].clazz;
                }

                java.lang.reflect.Method validate;

                try {
                    validate = method.owner.clazz.getMethod(name, arguments);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                if (!method.rtn.clazz.equals(validate.getReturnType())) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }

                if (!method.owner.clazz.equals(ptype.clazz)) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMember member : ptype.members.values()) {
                Field field;

                try {
                    field = member.owner.clazz.getField(member.field);
                } catch (NoSuchFieldException exception) {
                    throw new IllegalArgumentException();
                }

                if (!member.type.clazz.equals(field.getType())) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalArgumentException();
                }

                if (!member.owner.clazz.equals(ptype.clazz)) {
                    throw new IllegalArgumentException();
                }
            }
        }

        for (PCast implicit : implicits) {
            PType from = ptypes.get(implicit.from);
            PType to = ptypes.get(implicit.to);

            if (boxes.containsKey(implicit) || unboxes.containsKey(implicit) ||
                    isNumeric(from.clazz) && isNumeric(to.clazz)) {
                continue;
            }

            try {
                from.clazz.asSubclass(to.clazz);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException();
            }
        }

        for (PCast explicit : explicits) {
            PType from = ptypes.get(explicit.from);
            PType to = ptypes.get(explicit.to);

            if (boxes.containsKey(explicit) || unboxes.containsKey(explicit) ||
                    isNumeric(from.clazz) && isNumeric(to.clazz)) {
                continue;
            }

            try {
                to.clazz.asSubclass(from.clazz);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException();
            }
        }
    }
}
