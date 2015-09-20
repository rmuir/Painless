package painless;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

class PainlessTypes {
    static class PType {
        private final PClass pclass;
        private final Class jclass;
        private final int pdimensions;

        private PType(final PClass pclass, final Class jclass, final int pdimensions) {
            this.pclass = pclass;
            this.jclass = jclass;
            this.pdimensions = pdimensions;
        }

        PClass getPClass() {
            return pclass;
        }

        Class getJClass() {
            return jclass;
        }

        int getPDimensions() {
            return pdimensions;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final PType ptype = (PType)object;

            if (pdimensions != ptype.pdimensions) {
                return false;
            }

            return pclass.pname.equals(ptype.pclass.pname);
        }

        @Override
        public int hashCode() {
            int result = pclass.pname.hashCode();
            result = 31 * result + pdimensions;

            return result;
        }
    }

    static class PConstructor {
        private final String pname;
        private final PClass powner;
        private final List<PType> parguments;
        private final Constructor jconstructor;

        private PConstructor(final String pname, final PClass powner,
                             final List<PType> parguments, final Constructor jconstructor) {
            this.pname = pname;
            this.powner = powner;
            this.parguments = Collections.unmodifiableList(parguments);
            this.jconstructor = jconstructor;
        }

        String getPName() {
            return pname;
        }

        PClass getPOwner() {
            return powner;
        }

        List<PType> getPArguments() {
            return parguments;
        }

        Constructor getJConstructor() {
            return jconstructor;
        }
    }

    static class PMethod {
        private final String pname;
        private final PClass powner;
        private final PType preturn;
        private final List<PType> parguments;
        private final Method jmethod;

        private PMethod(final String pname, final PClass powner,
                        final PType preturn, final List<PType> parguments, final Method jmethod) {
            this.pname = pname;
            this.powner = powner;
            this.preturn = preturn;
            this.parguments = Collections.unmodifiableList(parguments);
            this.jmethod = jmethod;
        }

        String getPName() {
            return pname;
        }

        PClass getPOwner() {
            return powner;
        }

        PType getPReturn() {
            return preturn;
        }

        List<PType> getPArguments() {
            return parguments;
        }

        Method getJmethod() {
            return jmethod;
        }
    }

    static class PField {
        private final String pname;
        private final PClass powner;
        private final PType ptype;
        private final Field jfield;

        private PField(final String pname, final PClass powner, final PType ptype, final Field jfield) {
            this.pname = pname;
            this.powner = powner;
            this.ptype = ptype;
            this.jfield = jfield;
        }

        String getPName() {
            return pname;
        }

        PClass getPOwner() {
            return powner;
        }

        PType getPType() {
            return ptype;
        }

        Field getJField() {
            return jfield;
        }
    }

    static class PClass {
        private final String pname;
        private final Class jclass;

        private final Map<String, PConstructor> pconstructors;
        private final Map<String, PMethod> pfunctions;
        private final Map<String, PMethod> pmethods;

        private final Map<String, PField> pstatics;
        private final Map<String, PField> pmembers;

        private PClass(final String pname, final Class jclass) {
            this.pname = pname;
            this.jclass = jclass;

            this.pconstructors = new HashMap<>();
            this.pfunctions = new HashMap<>();
            this.pmethods = new HashMap<>();

            this.pstatics = new HashMap<>();
            this.pmembers = new HashMap<>();
        }

        String getPName() {
            return pname;
        }

        Class getJClass() {
            return jclass;
        }

        PConstructor getPConstructor(final String pname) {
            return pconstructors.get(pname);
        }

        PMethod getPFunction(final String pname) {
            return pfunctions.get(pname);
        }

        PMethod getPMethod(final String pname) {
            return pmethods.get(pname);
        }

        PField getPStatic(final String pname) {
            return pstatics.get(pname);
        }

        PField getPMember(final String pname) {
            return pmembers.get(pname);
        }
    }

    static class PCast {
        private final PType pfrom;
        private final PType pto;

        PCast(final PType afrom, final PType ato) {
            this.pfrom = afrom;
            this.pto = ato;
        }

        PType getPFrom() {
            return pfrom;
        }

        PType getPTo() {
            return pto;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final PCast pCast = (PCast)object;

            if (!pfrom.equals(pCast.pfrom)) {
                return false;
            }

            return pto.equals(pCast.pto);
        }

        @Override
        public int hashCode() {
            int result = pfrom.hashCode();
            result = 31 * result + pto.hashCode();

            return result;
        }
    }

    static class PTypes {
        private final Map<String, PClass> pclasses;

        private final Set<PCast> pdisalloweds;
        private final Map<PCast, PMethod> pexplicits;
        private final Map<PCast, PMethod> pimplicits;

        private PTypes() {
            pclasses = new HashMap<>();

            pdisalloweds = new HashSet<>();
            pexplicits = new HashMap<>();
            pimplicits = new HashMap<>();
        }

        PClass getPClass(final String pname) {
            return pclasses.get(pname);
        }

        boolean isPDisallowed(final PCast pcast) {
            return pdisalloweds.contains(pcast);
        }

        PMethod getPExplicit(final PCast pcast) {
            return pexplicits.get(pcast);
        }

        PMethod getPImplicit(final PCast pcast) {
            return pimplicits.get(pcast);
        }
    }

    private static final String PROPERTIES_FILE = PainlessTypes.class.getSimpleName() + ".properties";

    static PTypes loadFromProperties() {
        final Properties properties = new Properties();

        try (final InputStream stream = PainlessTypes.class.getResourceAsStream(PROPERTIES_FILE)) {
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException(); // TODO: message
        }

        return loadFromProperties(properties);
    }

    static PTypes loadFromProperties(final Properties properties) {
        final PTypes ptypes = new PTypes();

        for (String key : properties.stringPropertyNames()) {
            final String property = properties.getProperty(key);

            if (key.startsWith("class")) {
                loadPClassFromProperty(ptypes, property);
            } else {
                boolean valid = key.startsWith("constructor") || key.startsWith("function")   ||
                                key.startsWith("method")      || key.startsWith("cross")      ||
                                key.startsWith("static")      || key.startsWith("transform")  ||
                                key.startsWith("transform");

                if (!valid) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        for (String key : properties.stringPropertyNames()) {
            final String property = properties.getProperty(key);

            if (key.startsWith("constructor")) {
                loadPConstructorFromProperty(ptypes, property);
            } else if (key.startsWith("function")) {
                loadPMethodFromProperty(ptypes, property, true);
            } else if (key.startsWith("method")) {
                loadPMethodFromProperty(ptypes, property, false);
            } else if (key.startsWith("static")) {
                loadPFieldFromProperty(ptypes, property, true);
            } else if (key.startsWith("member")) {
                loadPFieldFromProperty(ptypes, property, false);
            }
        }

        for (String key : properties.stringPropertyNames()) {
            final String property = properties.getProperty(key);

            if (key.startsWith("cross.function")) {

            } else if (key.startsWith("cross.static")) {

            } else if (key.startsWith("transform.explicit")) {

            } else if (key.startsWith("transform.implicit")) {

            } else if (key.startsWith("transfrom.disallow")) {

            }
        }

        return ptypes;
    }

    private static void loadPClassFromProperty(final PTypes ptypes, final String property) {
        final String[] split = property.split("\\s+");

        if (split.length != 2) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pnamestr = split[0];
        final String jclassstr = split[1];

        loadPClass(ptypes, pnamestr, jclassstr);
    }

    private static void loadPConstructorFromProperty(final PTypes ptypes, final String property) {
        String parsed = property;
        int index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pownerstr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pnamestr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();

        final String[][] pargumentsstrs = parsePArgumentsStr(parsed);

        loadPConstructor(ptypes, pownerstr, pnamestr, pargumentsstrs);
    }

    private static void loadPMethodFromProperty(final PTypes ptypes, final String property, boolean statik) {
        String parsed = property;
        int index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pownerstr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pnamestr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf(' ');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String preturnstr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();
        index = parsed.indexOf('(');

        if (index == -1) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String jnamestr = parsed.substring(0, index);
        parsed = parsed.substring(index).trim();

        final String[][] pargumentsstrs = parsePArgumentsStr(parsed);

        loadPMethod(ptypes, pownerstr, pnamestr, preturnstr, jnamestr, pargumentsstrs, statik);
    }

    private static void loadPFieldFromProperty(final PTypes ptypes, final String property, final boolean statik) {
        final String[] split = property.split("\\s+");

        if (split.length != 4) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pownerstr = split[0];
        final String pnamestr = split[1];
        final String ptypestr = split[2];
        final String jnamestr = split[3];

        loadPField(ptypes, pownerstr, pnamestr, ptypestr, jnamestr, statik);
    }

    private static void loadPClass(final PTypes ptypes, final String pnamestr, final String jclassstr) {
        if (!pnamestr.matches("^[$a-zA-Z][a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (ptypes.pclasses.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Class jclass = getJClassFromCanonicalJName(jclassstr);
        final PClass pclass = new PClass(pnamestr, jclass);

        ptypes.pclasses.put(pnamestr, pclass);
    }

    private static void loadPConstructor(final PTypes ptypes, final String pownerstr,
                                         final String pnamestr, final String[][] pargumentsstrs) {
        final PClass powner = ptypes.getPClass(pownerstr);

        if (powner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!pnamestr.matches("^[a-zA-Z][a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pconstructors.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pstatics.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pmethods.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final int length = pargumentsstrs.length;
        final PType[] parguments = new PType[length];
        final Class[] jarguments = new Class[length];

        for (int pargumentindex = 0; pargumentindex < length; ++ pargumentindex) {
            final String[] pargumentstrs = pargumentsstrs[pargumentindex];

            if (pargumentstrs.length == 1) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]);
                jarguments[pargumentindex] = parguments[pargumentindex].jclass;
            } else if (pargumentstrs.length == 2) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[1]);
                jarguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]).jclass;
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        final Constructor jconstructor = getJConstructorFromJClass(powner.jclass, jarguments);
        final PConstructor pconstructor = new PConstructor(pnamestr, powner, Arrays.asList(parguments), jconstructor);

        powner.pconstructors.put(pnamestr, pconstructor);
    }

    private static void loadPMethod(final PTypes ptypes, final String pownerstr, final String pnamestr,
                                    final String preturnstr, final String jnamestr,
                                    final String[][] pargumentsstrs, boolean statik) {
        final PClass powner = ptypes.getPClass(pownerstr);

        if (powner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!pnamestr.matches("^[a-zA-Z][a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pconstructors.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pstatics.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pmethods.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String[] preturnstrs = parsePArgumentStr(preturnstr);
        PType preturn;
        Class jreturn;

        if (preturnstrs.length == 1) {
            preturn = getPTypeFromCanonicalPName(ptypes, preturnstrs[0]);
            jreturn = preturn.jclass;
        } else if (preturnstrs.length == 2) {
            preturn = getPTypeFromCanonicalPName(ptypes, preturnstrs[1]);
            jreturn = getPTypeFromCanonicalPName(ptypes, preturnstrs[0]).jclass;
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }

        final int length = pargumentsstrs.length;
        final PType[] parguments = new PType[length];
        final Class[] jarguments = new Class[length];

        for (int pargumentindex = 0; pargumentindex < length; ++ pargumentindex) {
            final String[] pargumentstrs = pargumentsstrs[pargumentindex];

            if (pargumentstrs.length == 1) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]);
                jarguments[pargumentindex] = parguments[pargumentindex].jclass;
            } else if (pargumentstrs.length == 2) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[1]);
                jarguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]).jclass;
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        final Method jmethod = getJMethodFromJClass(powner.jclass, jnamestr, jarguments);

        if (!jreturn.equals(jmethod.getReturnType())) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final PMethod pmethod = new PMethod(pnamestr, powner, preturn, Arrays.asList(parguments), jmethod);

        if (statik) {
            powner.pfunctions.put(pnamestr, pmethod);
        } else {
            powner.pmethods.put(pnamestr, pmethod);
        }
    }

    private static void loadPField(final PTypes ptypes, final String pownerstr, final String pnamestr,
                                   final String ptypestr, final String jnamestr, final boolean statik) {
        final PClass powner = ptypes.getPClass(pownerstr);

        if (powner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!pnamestr.matches("^[a-zA-Z][a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pstatics.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (powner.pmembers.containsKey(pnamestr)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final PType ptype = getPTypeFromCanonicalPName(ptypes, ptypestr);
        final Field jfield = getJFieldFromJClass(powner.jclass, jnamestr);
        final PField pfield = new PField(pnamestr, powner, ptype, jfield);

        if (statik) {
            powner.pstatics.put(pnamestr, pfield);
        } else {
            powner.pmembers.put(pnamestr, pfield);
        }
    }

    private static String[][] parsePArgumentsStr(final String pargumentstr) {
        if (!pargumentstr.startsWith("(") || !pargumentstr.endsWith(")")) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String tidy = pargumentstr.substring(1, pargumentstr.length() - 1).replace(" ", "");

        if ("".equals(tidy)) {
            return new String[0][];
        }

        final String[] pargumentsstr = tidy.split(",");
        final String[][] pargumentsstrs = new String[pargumentsstr.length][];

        for (int pargumentindex = 0; pargumentindex < pargumentsstr.length; ++pargumentindex) {
            pargumentsstrs[pargumentindex] = parsePArgumentStr(pargumentsstr[pargumentindex]);
        }

        return pargumentsstrs;
    }

    private static String[] parsePArgumentStr(final String pargumentstr) {
        final String[] pargumentstrs = pargumentstr.split("\\^");

        if (pargumentstrs.length == 1) {
            return new String[] {pargumentstrs[0]};
        } else if (pargumentstrs.length == 2) {
            return new String[] {pargumentstrs[0], pargumentstrs[1]};
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static PType getPTypeFromCanonicalPName(final PTypes ptypes, final String pnamestr) {
        final int dimensions = getArrayDimensionsFromCanonicalName(pnamestr);

        if (dimensions == 0) {
            final PClass pclass = ptypes.getPClass(pnamestr);

            if (pclass == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            return new PType(pclass, pclass.jclass, 0);
        } else {
            final int index = pnamestr.indexOf('[');
            final int length = pnamestr.length();
            final String pclassstr = pnamestr.substring(0, index);
            final PClass pclass = ptypes.getPClass(pclassstr);

            if (pclass == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            final String brackets = pnamestr.substring(index, length);
            final String jnamestr = pclass.jclass.getCanonicalName() + brackets;
            final Class jclass = getJClassFromCanonicalJName(jnamestr);

            return new PType(pclass, jclass, dimensions);
        }
    }

    private static Class getJClassFromCanonicalJName(final String jnamestr) {
        try {
            final int dimensions = getArrayDimensionsFromCanonicalName(jnamestr);

            if (dimensions == 0) {
                switch (jnamestr) {
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
                        return Class.forName(jnamestr);
                }
            } else {
                String jclassstr = jnamestr.substring(jnamestr.indexOf('['));

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
            String brackets = name.substring(index);
            final int length = name.length();

            while (index < length) {
                if (brackets.charAt(index) == '[' && ++index < length && brackets.charAt(index++) == ']') {
                    ++dimensions;
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        return dimensions;
    }

    private static Constructor getJConstructorFromJClass(final Class jclass, final Class[] jarguments) {
        try {
            return jclass.getConstructor(jarguments);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    static Method getJMethodFromJClass(final Class jclass, final String jname, final Class[] jarguments) {
        try {
            return jclass.getMethod(jname, jarguments);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    static Field getJFieldFromJClass(final Class jclass, final String jname) {
        try {
            return jclass.getField(jname);
        } catch (NoSuchFieldException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private PainlessTypes() {}
}
