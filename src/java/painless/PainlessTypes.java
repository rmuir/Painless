package painless;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

class PainlessTypes {
    static class PMethod {
        final String pname;
        final PClass powner;
        final Method amethod;
        final boolean variadic;

        private PMethod(final String pname, final PClass powner, Method amethod, boolean variadic) {
            this.pname = pname;
            this.powner = powner;
            this.amethod = amethod;
            this.variadic = variadic;
        }
    }

    static class PMember {
        final String pname;
        final PClass powner;
        final String jfield;
        final Type atype;

        private PMember(final String pname, final PClass powner, final String jfield, final Type atype) {
            this.pname = pname;
            this.powner = powner;
            this.jfield = jfield;
            this.atype = atype;
        }
    }

    static class PClass {
        final String pname;
        final Type atype;

        private final Map<String, PMethod> pfunctions;
        private final Map<String, PMethod> pconstructors;
        private final Map<String, PMethod> pmethods;

        private final Map<String, PMember> pstatics;
        private final Map<String, PMember> pmembers;

        private PClass(final String pname, final Type atype) {
            this.pname = pname;
            this.atype = atype;

            this.pfunctions = new HashMap<>();
            this.pconstructors = new HashMap<>();
            this.pmethods = new HashMap<>();

            this.pstatics = new HashMap<>();
            this.pmembers = new HashMap<>();
        }

        PMethod getPFunction(String pfunction) {
            return pfunctions.get(pfunction);
        }

        PMethod getPConstructor(String pconstructor) {
            return pconstructors.get(pconstructor);
        }

        PMethod getPMethod(String pmethod) {
            return pmethods.get(pmethod);
        }

        PMember getPStatic(String pstatic) {
            return pstatics.get(pstatic);
        }

        PMember getPMember(String pmember) {
            return pmembers.get(pmember);
        }
    }

    static class PCast {
        final Type afrom;
        final Type ato;

        PCast(final Type afrom, final Type ato) {
            this.afrom = afrom;
            this.ato = ato;
        }
    }

    static Type getATypeFromJClass(final String jtype) {
        int index = jtype.indexOf('[');
        String type = jtype;
        String dimensions = "";

        if (index != -1) {
            type = jtype.substring(0, index);
            final int length = jtype.length();

            while (index < length) {
                if (jtype.charAt(index) == '[' && ++index < length && jtype.charAt(index++) == ']') {
                    dimensions += "[";
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        switch (type) {
            case "void":
                type = "V";
                break;
            case "boolean":
                type = "Z";
                break;
            case "byte":
                type = "B";
                break;
            case "short":
                type = "S";
                break;
            case "char":
                type = "C";
                break;
            case "int":
                type = "I";
                break;
            case "long":
                type = "J";
                break;
            case "float":
                type = "F";
                break;
            case "double":
                type = "D";
                break;
            default:
                type = "L" + type.replace(".", "/") + ";";
        }

        type = dimensions + type;

        return Type.getType(type);
    }

    static Class getJClass(Type atype) throws ClassNotFoundException {
        switch (atype.getSort()) {
            case Type.VOID:
                return void.class;
            case Type.BOOLEAN:
                return boolean.class;
            case Type.BYTE:
                return byte.class;
            case Type.CHAR:
                return char.class;
            case Type.SHORT:
                return short.class;
            case Type.INT:
                return int.class;
            case Type.LONG:
                return long.class;
            case Type.FLOAT:
                return float.class;
            case Type.DOUBLE:
                return double.class;
            default:
                return Class.forName(atype.getInternalName().replace('/', '.'));
        }
    }

    static final String PROPERTIES_FILE = PainlessTypes.class.getSimpleName() + ".properties";

    private final Map<String, PClass> pclasses;
    private final Map<Type, PClass> aclasses;
    private final Map<PCast, PMethod> ptransforms;

    PainlessTypes() {
        pclasses = new HashMap<>();
        aclasses = new HashMap<>();
        ptransforms = new HashMap<>();

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

                if (pclasses.containsKey(name)) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final Type type = getATypeFromJClass(property);
                final PClass pclass = new PClass(name, type);

                pclasses.put(name, pclass);
                aclasses.put(type, pclass);
            } else {
                boolean valid = key.startsWith("constructor") || key.startsWith("function")   ||
                                key.startsWith("static")      || key.startsWith("method")     ||
                                key.startsWith("member")      || key.startsWith("transform")  ||
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

                final String ptype = keysplit[1];
                final String pname = keysplit[2];
                final PClass powner = pclasses.get(ptype);

                if (powner == null) {
                    throw new IllegalArgumentException();
                }

                String property = properties.getProperty(key);
                int index = property.indexOf(" ");
                final Type artn = getATypeFromPClass(property.substring(0, index));
                property = property.substring(index + 1);
                index = property.indexOf("(");
                final String aname = property.substring(0, index);

                if ("".equals(aname)) {
                    throw new IllegalArgumentException();
                } else if ("constructor".equals(keysplit[0]) && !"<init>".equals(aname)) {
                    throw new IllegalArgumentException();
                }

                property = property.substring(index);

                if (property.charAt(0) != '(' || property.charAt(property.length() - 1) != ')') {
                    throw new IllegalArgumentException();
                }

                property = property.replace("(", "").replace(")", "").replace(" ", "");
                final String[] propsplit = property.isEmpty() ? null : property.split(",");
                final Type[] aarguments = new Type[propsplit == null ? 0 : propsplit.length];
                boolean variadic = false;

                for (int argument = 0; argument < aarguments.length; ++argument) {
                    String pargument = propsplit[argument];

                    if (argument + 1 == aarguments.length) {
                        if (pargument.endsWith("...")) {
                            variadic = true;
                            pargument = pargument.substring(0, pargument.length() - 3);
                        }
                    }

                    final Type aargument = getATypeFromPClass(pargument);
                    aarguments[argument] = aargument;
                }

                final Method amethod = new Method(aname, artn, aarguments);
                final PMethod pmethod = new PMethod(pname, powner, amethod, variadic);

                if ("constructor".equals(keysplit[0])) {
                    powner.pconstructors.put(pname, pmethod);
                } else if (key.startsWith("function")) {
                    powner.pfunctions.put(pname, pmethod);
                } else if (key.startsWith("method")) {
                    powner.pmethods.put(pname, pmethod);
                } else {
                    throw new IllegalStateException();
                }
            } else if (key.startsWith("static") || key.startsWith("member")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final String ptype = keysplit[1];
                final String pname = keysplit[2];
                final PClass powner = pclasses.get(ptype);

                if (powner == null) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String[] propsplit = property.split("\\s+");

                if (propsplit.length != 2) {
                    throw new IllegalArgumentException();
                }

                final String pmemtype = propsplit[0];
                final String jfield = propsplit[1];
                final Type atype = getATypeFromPClass(pmemtype);

                if ("".equals(jfield)) {
                    throw new IllegalArgumentException();
                }

                final PMember pmember = new PMember(pname, powner, jfield, atype);

                if ("static".equals(keysplit[0])) {
                    powner.pstatics.put(pname, pmember);
                } else if ("member".equals(keysplit[0])) {
                    powner.pmembers.put(pname, pmember);
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith("transform")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final Type afrom = getATypeFromPClass(keysplit[1]);
                final Type ato = getATypeFromPClass(keysplit[2]);

                final PCast pcast = new PCast(afrom, ato);

                final String property = properties.getProperty(key);
                final String[] propsplit = property.split("\\s+");

                if (propsplit.length != 3) {
                    throw new IllegalArgumentException();
                }

                final PClass powner = pclasses.get(propsplit[1]);

                if (powner == null) {
                    throw new IllegalArgumentException();
                }

                PMethod pmethod;

                if ("function".equals(propsplit[0])) {
                    pmethod = powner.pfunctions.get(propsplit[2]);
                } else if ("method".equals(propsplit[0])) {
                    pmethod = powner.pmethods.get(propsplit[2]);
                } else {
                    throw new IllegalArgumentException();
                }

                if (pmethod == null) {
                    throw new IllegalArgumentException();
                }

                ptransforms.put(pcast, pmethod);
            } else if (key.startsWith("cross")) {
                final String[] keysplit = key.split("\\.");

                if (keysplit.length != 4) {
                    throw new IllegalArgumentException();
                }

                final PClass powner = pclasses.get(keysplit[2]);

                if (powner == null) {
                    throw new IllegalArgumentException();
                }

                final String property = properties.getProperty(key);
                final String[] propsplit = property.split("\\s+");

                if (propsplit.length != 2) {
                    throw new IllegalArgumentException();
                }

                final PClass porigin = pclasses.get(propsplit[0]);

                if (porigin == null) {
                    throw new IllegalArgumentException();
                }

                if ("function".equals(keysplit[1])) {
                    PMethod pfunction = porigin.pfunctions.get(propsplit[1]);

                    if (pfunction == null) {
                        throw new IllegalArgumentException();
                    }

                    powner.pfunctions.put(keysplit[3], pfunction);
                } else if ("static".equals(keysplit[1])) {
                    PMember pstatik = porigin.pstatics.get(propsplit[1]);

                    if (pstatik == null) {
                        throw new IllegalArgumentException();
                    }

                    powner.pstatics.put(keysplit[3], pstatik);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        for (final PClass pclass : pclasses.values()) {
            try {
                getJClass(pclass.atype);
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException();
            }

            for (final PMethod pfunction : pclass.pfunctions.values()) {
                final String jname = pfunction.amethod.getName();
                final Type[] aarguments = pfunction.amethod.getArgumentTypes();
                final Class[] jarguments = new Class[aarguments.length];

                for (int argument = 0; argument < aarguments.length; ++argument) {
                    try {
                        jarguments[argument] = getJClass(aarguments[argument]);
                    } catch (ClassNotFoundException exception) {
                        throw new IllegalArgumentException();
                    }
                }

                java.lang.reflect.Method validate;

                try {
                    validate = getJClass(pclass.atype).getMethod(jname, jarguments);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                Class rtn;

                try {
                    rtn = getJClass(pfunction.amethod.getReturnType());
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                }

                if (!rtn.equals(validate.getReturnType())) {
                    throw new IllegalArgumentException();
                }

                if (!Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }

                if (pfunction.variadic != validate.isVarArgs()) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMethod pconstructor : pclass.pconstructors.values()) {
                final Type[] aarguments = pconstructor.amethod.getArgumentTypes();
                final Class[] jarguments = new Class[aarguments.length];

                for (int argument = 0; argument < aarguments.length; ++argument) {
                    try {
                        jarguments[argument] = getJClass(aarguments[argument]);
                    } catch (ClassNotFoundException exception) {
                        throw new IllegalArgumentException();
                    }
                }

                java.lang.reflect.Constructor validate;

                try {
                    validate = getJClass(pclass.atype).getConstructor(jarguments);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }

                if (pconstructor.variadic != validate.isVarArgs()) {
                    throw new IllegalArgumentException();
                }

                if (!pconstructor.powner.equals(pclass)) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMethod pmethod : pclass.pmethods.values()) {
                final String jname = pmethod.amethod.getName();
                final Type[] aarguments = pmethod.amethod.getArgumentTypes();
                final Class[] jarguments = new Class[aarguments.length];

                for (int argument = 0; argument < aarguments.length; ++argument) {
                    try {
                        jarguments[argument] = getJClass(aarguments[argument]);
                    } catch (ClassNotFoundException exception) {
                        throw new IllegalArgumentException();
                    }
                }

                java.lang.reflect.Method validate;

                try {
                    validate = getJClass(pclass.atype).getMethod(jname, jarguments);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                } catch (NoSuchMethodException exception) {
                    throw new IllegalArgumentException();
                }

                Class rtn;

                try {
                    rtn = getJClass(pmethod.amethod.getReturnType());
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                }

                if (!rtn.equals(validate.getReturnType())) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(validate.getModifiers())) {
                    throw new IllegalArgumentException();
                }

                if (pmethod.variadic != validate.isVarArgs()) {
                    throw new IllegalArgumentException();
                }

                if (!pmethod.powner.equals(pclass)) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMember pmember : pclass.pmembers.values()) {
                Field field;

                try {
                    field = getJClass(pmember.powner.atype).getField(pmember.jfield);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                } catch (NoSuchFieldException exception) {
                    throw new IllegalArgumentException();
                }

                try {
                    if (!getJClass(pmember.atype).equals(field.getType())) {
                        throw new IllegalArgumentException();
                    }
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                }

                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalArgumentException();
                }

                if (!pmember.powner.equals(pclass)) {
                    throw new IllegalArgumentException();
                }
            }

            for (final PMember pstatic : pclass.pstatics.values()) {
                Field field;

                try {
                    field = getJClass(pstatic.powner.atype).getField(pstatic.jfield);
                } catch (ClassNotFoundException exception) {
                    throw new IllegalArgumentException();
                } catch (NoSuchFieldException exception) {
                    throw new IllegalArgumentException();
                }

                try {
                    if (!getJClass(pstatic.atype).equals(field.getType())) {
                        throw new IllegalArgumentException();
                    }
                } catch (ClassNotFoundException exception) {
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
        }
    }

    final Type getATypeFromPClass(final String ptype) {
        int index = ptype.indexOf('[');
        String type = ptype;
        String dimensions = "";

        if (index != -1) {
            type = ptype.substring(0, index);
            final int length = ptype.length();

            while (index < length) {
                if (ptype.charAt(index) == '[' && ++index < length && ptype.charAt(index++) == ']') {
                    dimensions += "[";
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        PClass pclass = pclasses.get(type);

        if (pclass == null) {
            throw new IllegalArgumentException();
        }

        final String descriptor = dimensions + pclass.atype.getDescriptor();
        return Type.getType(descriptor);
    }

    final PClass getPClass(final Type atype) {
        return aclasses.get(atype);
    }

    final PMethod getTransform(final PCast pcast) {
        return ptransforms.get(pcast);
    }
}
