package painless;

import java.io.IOException;
import java.io.InputStream;
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
        final Type type;
        final Type clazz;

        Member(final String name, final String field, final String type, final String clazz) {
            try {
                this.name = name;
                this.field = field;
                this.type = Type.getType(type);
                this.clazz = Type.getType(Class.forName(clazz));
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            }
        }
    }

    static class BasicType {
        final String name;
        final Type basic;
        final Type clazz;

        BasicType(final String name, final String basic, final String clazz) {
            try {
                this.name = name;

                String descriptor;

                switch (basic) {
                    case "void":
                        descriptor = "V";
                        break;
                    case "boolean":
                        descriptor = "Z";
                        break;
                    case "byte":
                        descriptor = "B";
                        break;
                    case "short":
                        descriptor = "S";
                        break;
                    case "char":
                        descriptor = "C";
                        break;
                    case "int":
                        descriptor = "I";
                        break;
                    case "long":
                        descriptor = "J";
                        break;
                    case "float":
                        descriptor = "F";
                        break;
                    case "double":
                        descriptor = "D";
                        break;
                    default:
                        throw new IllegalArgumentException();
                }

                this.basic = Type.getType(descriptor);
                this.clazz = Type.getType(Class.forName(clazz));
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            }
        }
    }

    static class ComplexType {
        final String name;
        final Type clazz;

        private final Map<String, Method> functions;
        private final Map<String, Member> statics;

        private final Map<String, Method> methods;
        private final Map<String, Member> members;

        ComplexType(final String name, final String clazz) {
            try {
                this.name = name;
                this.clazz = Type.getType(Class.forName(clazz));

                this.functions = new HashMap<>();
                this.statics = new HashMap<>();

                this.methods = new HashMap<>();
                this.members = new HashMap<>();
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            }
        }

        Method getFunction(String function) {
            return functions.get(function);
        }

        Member getStatic(String statik) {
            return statics.get(statik);
        }

        Method getMethod(String method) {
            return methods.get(method);
        }

        Member getMember(String member) {
            return members.get(member);
        }
    }

    static class Cast {
        final Type from;
        final Type to;

        Cast(final String from, final String to) {
            this.from = Type.getType(from);
            this.to = Type.getType(to);
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

    private static final String PROPERTIES_FILE = PainlessTypes.class.getSimpleName() + ".properties";

    private final Map<String, String> types;
    private final Map<String, BasicType> basics;
    private final Map<String, ComplexType> complexes;

    private final Set<Cast> numerics;
    private final Set<Cast> implicits;
    private final Set<Cast> explicits;
    private final Set<Cast> boxes;
    private final Set<Cast> unboxes;

    PainlessTypes() {
        types = new HashMap<>();
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

                final String basic = split[0].trim();
                final String clazz = split[1].trim();
                final BasicType type = new BasicType(name, basic, clazz);

                types.put(name, basic);
                basics.put(name, type);
            } else if (key.startsWith("complex")) {
                final String name = key.substring(key.indexOf('.') + 1).trim();

                if (types.containsKey(name)) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String clazz = property.trim();
                final ComplexType type = new ComplexType(name, clazz);

                types.put(name, clazz);
                complexes.put(name, type);
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
                complex.methods.put(name, method);
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
                            signature += argbasic.basic.getClassName();
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

                String type = split[1].trim();
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

                type = split[0].trim();
                type = types.get(type);

                if (type == null) {
                    throw new IllegalArgumentException();
                }

                final String field = split[1];

                if ("".equals(field)) {
                    throw new IllegalArgumentException();
                }

                final Member member = new Member(name, field, type, complex.clazz.getClassName());

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

                final String from = types.get(split[0].trim());

                if (from == null) {
                    throw new IllegalArgumentException();
                }

                final String to = types.get(split[1].trim());

                if (to == null) {
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
    }
}
