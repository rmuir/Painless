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
        final Type type;
        final Type clazz;

        Member(String name, String type, String clazz) {
            this.name = name;
            this.type = Type.getType(type);
            this.clazz = Type.getType(clazz);
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

        final Map<String, Method> methods;
        final Map<String, Member> members;

        ComplexType(final String name, final String clazz) {
            try {
                this.name = name;
                this.clazz = Type.getType(Class.forName(clazz));

                this.methods = new HashMap<>();
                this.members = new HashMap<>();
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException(exception);
            }
        }
    }

    static class Cast {
        final Type from;
        final Type to;

        final boolean box;
        final boolean unbox;

        Cast(final String from, final String to, final boolean box, final boolean unbox) {
            this.from = Type.getType(from);
            this.to = Type.getType(to);

            this.box = box;
            this.unbox = unbox;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            Cast cast = (Cast)object;

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

    PainlessTypes() {
        types = new HashMap<>();
        basics = new HashMap<>();
        complexes = new HashMap<>();

        numerics = new HashSet<>();
        implicits = new HashSet<>();
        explicits = new HashSet<>();

        final Properties properties = new Properties();

        try (InputStream stream = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }

        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("basic")) {
                String property = properties.getProperty(key);
                String[] split = property.split("\\s+");

                if (split.length != 2) {
                    throw new IllegalArgumentException();
                }

                String name = key.substring(key.indexOf('.') + 1).trim();

                if (types.containsKey(name)) {
                    throw new IllegalArgumentException();
                }

                String basic = split[0].trim();
                String clazz = split[1].trim();
                BasicType type = new BasicType(name, basic, clazz);

                types.put(name, basic);
                basics.put(name, type);
            } else if (key.startsWith("complex")) {
                String name = key.substring(key.indexOf('.') + 1).trim();

                if (types.containsKey(name)) {
                    throw new IllegalArgumentException();
                }

                String clazz = properties.getProperty(key).trim();
                ComplexType type = new ComplexType(name, clazz);

                types.put(name, clazz);
                complexes.put(name, type);
            } else {
                boolean valid = key.startsWith("constructor") || key.startsWith("method") ||
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
                ComplexType complex = complexes.get(type);

                if (complex == null) {
                    throw new IllegalArgumentException();
                }

                String name = split[2].trim();
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
                        String argtype = types.get(split[argument]);

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

                Method method = Method.getMethod(signature);
                complex.methods.put(name, method);
            } else if (key.startsWith("method")) {
                String[] split = key.split("\\.");

                if (split.length != 3) {
                    throw new IllegalArgumentException();
                }

                String type = split[1].trim();
                ComplexType complex = complexes.get(type);

                if (complex == null) {
                    throw new IllegalArgumentException();
                }

                String name = split[2].trim();
                String signature = properties.getProperty(key);

                int index = signature.indexOf(" ");
                String rtn = signature.substring(0, index);
                signature = signature.substring(index + 1);
                String rtntype = types.get(rtn);

                if (rtntype == null) {
                    throw new IllegalArgumentException();
                }

                index = signature.indexOf("(");
                String call = signature.substring(0, index);

                if ("".equals(call)) {
                    throw new IllegalArgumentException();
                }

                String arguments = signature.substring(index);

                if (arguments.charAt(0) != '(' || arguments.charAt(arguments.length() - 1) != ')') {
                    throw new IllegalArgumentException();
                }

                arguments = arguments.replace("(", "").replace(")", "").replace(" ", "");
                split = arguments.isEmpty() ? null : arguments.split(",");

                signature = rtntype + " " + name + "(";

                if (split != null) {
                    for (int argument = 0; argument < split.length; ++argument) {
                        String argtype = split[argument].trim();
                        BasicType argbasic = basics.get(argtype);
                        ComplexType argcomplex = complexes.get(argtype);

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

                Method method = Method.getMethod(signature);
                complex.methods.put(name, method);
            } else if (key.startsWith("member")) {
                String[] split = key.split("\\.");

                if (split.length != 3) {
                    throw new IllegalArgumentException();
                }

                String type = split[1].trim();
                ComplexType complex = complexes.get(type);

                if (complex == null) {
                    throw new IllegalArgumentException();
                }

                String name = split[2].trim();
                String values = properties.getProperty(key);

                split = values.split("\\s+");

                if (split.length != 2) {
                    throw new IllegalArgumentException();
                }

                type = split[0].trim();
                type = types.get(type);

                if (type == null) {
                    throw new IllegalArgumentException();
                }

                String field = split[1];

                if ("".equals(field)) {
                    throw new IllegalArgumentException();
                }

                Member member = new Member(field, type, complex.clazz.getClassName());
                complex.members.put(name, member);
            } else if (key.startsWith("cast")) {
                String[] split = key.split("\\.");

                if (split.length != 3) {
                    throw new IllegalArgumentException();
                }

                String cast = split[1];

                String values = properties.getProperty(key);
                split = values.split("\\s+");

                if (split.length != 2 && split.length != 3) {
                    throw new IllegalArgumentException();
                }

                String from = types.get(split[0].trim());

                if (from == null) {
                    throw new IllegalArgumentException();
                }

                String to = types.get(split[1].trim());

                if (to == null) {
                    throw new IllegalArgumentException();
                }

                boolean box = false;
                boolean unbox = false;

                if (split.length == 3) {
                    if ("box".equals(split[2])) {
                        box = true;
                    } else if ("unbox".equals(split[2])) {
                        unbox = true;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }

                if ("numeric".equals(cast)) {
                    numerics.add(new Cast(from, to, box, unbox));
                } else if ("implicit".equals(cast)) {
                    implicits.add(new Cast(from, to, box, unbox));
                } else if ("explicit".equals(cast)) {
                    explicits.add(new Cast(from, to, box, unbox));
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
    }
}
