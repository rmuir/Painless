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
import org.objectweb.asm.commons.Method;

class PainlessTypes {
    static class Member {
        final String name;
        final String field;
        final Type clazz;
        final Type owner;

        Member(final String name, final String field, final String clazz, final String owner) {
            this.name = name;
            this.field = field;

            try {
                switch (clazz) {
                    case "void":
                        this.clazz = Type.VOID_TYPE;
                        break;
                    case "boolean":
                        this.clazz = Type.BOOLEAN_TYPE;
                        break;
                    case "byte":
                        this.clazz = Type.BYTE_TYPE;
                        break;
                    case "short":
                        this.clazz = Type.SHORT_TYPE;
                        break;
                    case "char":
                        this.clazz = Type.CHAR_TYPE;
                        break;
                    case "int":
                        this.clazz = Type.INT_TYPE;
                        break;
                    case "long":
                        this.clazz = Type.LONG_TYPE;
                        break;
                    case "float":
                        this.clazz = Type.FLOAT_TYPE;
                        break;
                    case "double":
                        this.clazz = Type.DOUBLE_TYPE;
                        break;
                    default:
                        this.clazz = Type.getType(Class.forName(owner));
                }
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            }

            try {
                this.owner = Type.getType(Class.forName(owner));
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            }
        }
    }

    static class BasicType {
        final String name;
        final Type clazz;
        final Type object;

        BasicType(final String name, final String clazz, final String object) {
            this.name = name;

            try {
                switch (clazz) {
                    case "void":
                        this.clazz = Type.VOID_TYPE;
                        Type.getObjectType()
                        Void.class.asSubclass(Class.forName(object));
                        break;
                    case "boolean":
                        this.clazz = Type.BOOLEAN_TYPE;
                        Boolean.class.asSubclass(Class.forName(object));
                        break;
                    case "byte":
                        this.clazz = Type.BYTE_TYPE;
                        Byte.class.asSubclass(Class.forName(object));
                        break;
                    case "short":
                        this.clazz = Type.SHORT_TYPE;
                        Short.class.asSubclass(Class.forName(object));
                        break;
                    case "char":
                        this.clazz = Type.CHAR_TYPE;
                        Character.class.asSubclass(Class.forName(object));
                        break;
                    case "int":
                        this.clazz = Type.INT_TYPE;
                        Integer.class.asSubclass(Class.forName(object));
                        break;
                    case "long":
                        this.clazz = Type.LONG_TYPE;
                        Long.class.asSubclass(Class.forName(object));
                        break;
                    case "float":
                        this.clazz = Type.FLOAT_TYPE;
                        Float.class.asSubclass(Class.forName(object));
                        break;
                    case "double":
                        this.clazz = Type.DOUBLE_TYPE;
                        Double.class.asSubclass(Class.forName(object));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException();
            }

            try {
                this.object = Type.getType(Class.forName(object));
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException(exception);
            }
        }
    }

    static class ComplexType {
        final String name;
        final Type clazz;

        private final Map<String, Method> functions;
        private final Map<String, Member> statics;

        private final Map<String, Method> constructors;
        private final Map<String, Method> methods;
        private final Map<String, Member> members;

        ComplexType(final String name, final String clazz) {
            this.name = name;

            try {
                this.clazz = Type.getType(Class.forName(clazz));
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            }

            this.functions = new HashMap<>();
            this.statics = new HashMap<>();

            this.constructors = new HashMap<>();
            this.methods = new HashMap<>();
            this.members = new HashMap<>();
        }

        Method getFunction(String function) {
            return functions.get(function);
        }

        Member getStatic(String statik) {
            return statics.get(statik);
        }

        Method getConstructor(String constructor) {
            return constructors.get(constructor);
        }

        Method getMethod(String method) {
            return methods.get(method);
        }

        Member getMember(String member) {
            return members.get(member);
        }
    }

    static class Cast {
        final String from;
        final String to;

        Cast(final String from, final String to) {
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

            final Cast cast = (Cast)object;

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

    static Class getClass(final Type type) throws ClassNotFoundException {
        return getClass(type.getClassName());
    }

    static Class getClass(final String clazz) throws ClassNotFoundException {
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

    static boolean isNumeric(final Type type) {

    }

    private static final String PROPERTIES_FILE = PainlessTypes.class.getSimpleName() + ".properties";

    private final Map<String, Type> types;
    private final Map<String, Class> classes;
    private final Map<String, BasicType> basics;
    private final Map<String, ComplexType> complexes;

    private final Set<Cast> numerics;
    private final Set<Cast> implicits;
    private final Set<Cast> explicits;
    private final Set<Cast> boxes;
    private final Set<Cast> unboxes;

    PainlessTypes() {
        types = new HashMap<>();
        classes = new HashMap<>();
        basics = new HashMap<>();
        complexes = new HashMap<>();

        numerics = new HashSet<>();
        implicits = new HashSet<>();
        explicits = new HashSet<>();
        boxes = new HashSet<>();
        unboxes = new HashSet<>();

        final Properties properties = new Properties();

        try (InputStream stream = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }

        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("basic")) {
                final String name = key.substring(key.indexOf('.') + 1).trim();

                if (types.containsKey(name)) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String[] split = property.split("\\s+");

                if (split.length != 2) {
                    throw new IllegalArgumentException();
                }

                final String clazz = split[0].trim();
                final String object = split[1].trim();
                final BasicType basic = new BasicType(name, clazz, object);

                types.put(name, clazz);
                basics.put(name, basic);
            } else if (key.startsWith("complex")) {
                final String name = key.substring(key.indexOf('.') + 1).trim();

                if (types.containsKey(name)) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String clazz = property.trim();
                final ComplexType complex = new ComplexType(name, clazz);

                types.put(name, clazz);
                complexes.put(name, complex);
            } else {
                boolean valid = key.startsWith("constructor") || key.startsWith("function") ||
                        key.startsWith("static") || key.startsWith("method") ||
                        key.startsWith("member") || key.startsWith("cast");

                if (!valid) {
                    throw new IllegalArgumentException();
                }
            }
        }

        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("constructor")) {
                String[] split = key.split("\\.");

                if (split.length != 3) {
                    throw new IllegalArgumentException();
                }

                String type = split[1].trim();
                final ComplexType complex = complexes.get(type);

                if (complex == null) {
                    throw new IllegalArgumentException();
                }

                final String name = split[2].trim();

                String arguments = properties.getProperty(key);
                arguments = arguments.trim();

                if (arguments.charAt(0) != '(' || arguments.charAt(arguments.length() - 1) != ')') {
                    throw new IllegalArgumentException();
                }

                arguments = arguments.replace("(", "").replace(")", "").replace(" ", "");
                split = arguments.isEmpty() ? null : arguments.split(",");
                String signature = "void <init>(";

                if (split != null) {
                    for (int argument = 0; argument < split.length; ++argument) {
                        final String argtype = types.get(split[argument]);

                        if (argtype == null) {
                            throw new IllegalArgumentException();
                        }

                        signature += argtype;

                        if (argument < split.length - 1) {
                            signature += ",";
                        }
                    }
                }

                signature += ")";

                final Method method = Method.getMethod(signature);
                complex.constructors.put(name, method);
            } else if (key.startsWith("function") || key.startsWith("method")) {
                String[] split = key.split("\\.");

                if (split.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String type = split[1].trim();
                final ComplexType complex = complexes.get(type);

                if (complex == null) {
                    throw new IllegalArgumentException();
                }

                final String name = split[2].trim();
                String signature = properties.getProperty(key);

                int index = signature.indexOf(" ");
                final String rtn = signature.substring(0, index);
                signature = signature.substring(index + 1);
                final String rtntype = types.get(rtn);

                if (rtntype == null) {
                    throw new IllegalArgumentException();
                }

                index = signature.indexOf("(");
                final String call = signature.substring(0, index);

                if ("".equals(call)) {
                    throw new IllegalArgumentException();
                }

                String arguments = signature.substring(index);

                if (arguments.charAt(0) != '(' || arguments.charAt(arguments.length() - 1) != ')') {
                    throw new IllegalArgumentException();
                }

                arguments = arguments.replace("(", "").replace(")", "").replace(" ", "");
                split = arguments.isEmpty() ? null : arguments.split(",");

                signature = rtntype + " " + call + "(";

                if (split != null) {
                    for (int argument = 0; argument < split.length; ++argument) {
                        final String argtype = split[argument].trim();
                        final BasicType argbasic = basics.get(argtype);
                        final ComplexType argcomplex = complexes.get(argtype);

                        if (argbasic != null) {
                            signature += argbasic.clazz.getClassName();
                        } else if (argcomplex != null) {
                            signature += argcomplex.clazz.getClassName();
                        } else {
                            throw new IllegalArgumentException();
                        }

                        if (argument < split.length - 1) {
                            signature += ",";
                        }
                    }
                }

                signature += ")";

                final Method method = Method.getMethod(signature);

                if (key.startsWith("function")) {
                    complex.functions.put(name, method);
                } else if (key.startsWith("method")) {
                    complex.methods.put(name, method);
                } else {
                    throw new IllegalStateException();
                }
            } else if (key.startsWith("static") || key.startsWith("member")) {
                String[] split = key.split("\\.");

                if (split.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String type = split[1].trim();
                final ComplexType complex = complexes.get(type);

                if (complex == null) {
                    throw new IllegalArgumentException();
                }

                final String name = split[2].trim();

                final String property = properties.getProperty(key);
                split = property.split("\\s+");

                if (split.length != 2) {
                    throw new IllegalArgumentException();
                }

                final String clazz = types.get(split[0].trim());

                if (clazz == null) {
                    throw new IllegalArgumentException();
                }

                final String field = split[1];

                if ("".equals(field)) {
                    throw new IllegalArgumentException();
                }

                final String owner = complex.clazz.getClassName();

                final Member member = new Member(name, field, clazz, owner);

                if (key.startsWith("static")) {
                    complex.statics.put(name, member);
                } else if (key.startsWith("member")) {
                    complex.members.put(name, member);
                } else {
                    throw new IllegalStateException();
                }
            } else if (key.startsWith("cast")) {
                String[] split = key.split("\\.");

                if (split.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String type = split[1];

                final String property = properties.getProperty(key);
                split = property.split("\\s+");

                if (split.length != 2 && split.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String from = split[0].trim();

                if (!types.containsKey(from)) {
                    throw new IllegalArgumentException();
                }

                final String to = split[1].trim();

                if (!types.containsKey(to)) {
                    throw new IllegalArgumentException();
                }

                final Cast cast = new Cast(from, to);

                if ("numeric".equals(type)) {
                    numerics.add(cast);
                } else if ("implicit".equals(type)) {
                    implicits.add(cast);
                } else if ("explicit".equals(type)) {
                    explicits.add(cast);
                } else {
                    throw new IllegalArgumentException();
                }

                if (split.length == 3) {
                    if ("box".equals(split[2])) {
                        boxes.add(cast);
                    } else if ("unbox".equals(split[2])) {
                        unboxes.add(cast);
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }

        for (final ComplexType complex : complexes.values()) {
            Class clazz;

            try {
                clazz = getClass(complex.clazz);
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException();
            }

            for (final Method function : complex.functions.values()) {
                final String name = function.getName();
                final Type[] types = function.getArgumentTypes();
                final Class[] arguments = new Class[types.length];

                for (int type = 0; type < types.length; ++type) {
                    try {
                        arguments[type] = getClass(types[type]);
                    } catch (ClassNotFoundException exception) {
                        throw new IllegalArgumentException();
                    }
                }

                java.lang.reflect.Method validate;

                try {
                    validate = clazz.getMethod(name, arguments);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                try {
                    if (!getClass(function.getReturnType()).equals(validate.getReturnType())) {
                        throw new IllegalArgumentException();
                    }
                } catch (ClassNotFoundException exception ) {
                    throw new IllegalArgumentException();
                }

                if (!Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }
            }

            for (final Member statik : complex.statics.values()) {
                Field field;

                try {
                    field = clazz.getField(statik.field);
                } catch (NoSuchFieldException exception) {
                    throw new IllegalArgumentException();
                }

                Class type;

                try {
                    type = getClass(statik.clazz);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                }

                if (!type.equals(field.getType())) {
                    throw new IllegalArgumentException();
                }

                if (!Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalArgumentException();
                }
            }

            for (final Method constructor : complex.constructors.values()) {
                final Type[] types = constructor.getArgumentTypes();
                final Class[] arguments = new Class[types.length];

                for (int type = 0; type < types.length; ++type) {
                    try {
                        arguments[type] = getClass(types[type]);
                    } catch (ClassNotFoundException exception) {
                        throw new IllegalArgumentException();
                    }
                }

                try {
                    clazz.getConstructor(arguments);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }
            }

            for (final Method method : complex.methods.values()) {
                final String name = method.getName();
                final Type[] types = method.getArgumentTypes();
                final Class[] arguments = new Class[types.length];

                for (int type = 0; type < types.length; ++type) {
                    try {
                        arguments[type] = getClass(types[type]);
                    } catch (ClassNotFoundException exception) {
                        throw new IllegalArgumentException();
                    }
                }

                java.lang.reflect.Method validate;

                try {
                    validate = clazz.getMethod(name, arguments);
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                try {
                    if (!getClass(method.getReturnType()).equals(validate.getReturnType())) {
                        throw new IllegalArgumentException();
                    }
                } catch (ClassNotFoundException exception ) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }
            }

            for (final Member member : complex.members.values()) {
                Field field;

                try {
                    field = clazz.getField(member.field);
                } catch (NoSuchFieldException exception) {
                    throw new IllegalArgumentException();
                }

                Class type;

                try {
                    type = getClass(member.clazz);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                }

                if (!type.equals(field.getType())) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalArgumentException();
                }
            }
        }

        for (Cast numeric : numerics) {
            try {
                getClass(numeric.from);
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException();
            }

            try {
                getClass(numeric.to);
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException();
            }
        }
    }
}
