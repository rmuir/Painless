package painless;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

class PainlessTypes {
    enum PSort {
        VOID   ( 0 , false , "void"   , void.class               ),
        BOOL   ( 1 , false , "bool"   , boolean.class            ),
        BYTE   ( 1 , true  , "byte"   , byte.class               ),
        SHORT  ( 1 , true  , "short"  , short.class              ),
        CHAR   ( 1 , true  , "char"   , char.class               ),
        INT    ( 1 , true  , "int"    , int.class                ),
        LONG   ( 2 , true  , "long"   , long.class               ),
        FLOAT  ( 1 , true  , "float"  , float.class              ),
        DOUBLE ( 2 , true  , "double" , double.class             ),
        OBJECT ( 1 , false , "object" , Object.class             ),
        STRING ( 1 , false , "string" , String.class             ),
        EXEC   ( 1 , false , "exec"   , PainlessExecutable.class ),
        SMAP   ( 1 , false , "smap"   , Map.class                ),
        ARRAY  ( 1 , false , null     , null                     );

        private final int asize;
        private final boolean pnumeric;
        private final String pname;
        private final Class jclass;

        PSort(final int asize, final boolean pnumeric, final String pname, final Class jclass) {
            this.asize = asize;
            this.pnumeric = pnumeric;
            this.pname = pname;
            this.jclass = jclass;
        }

        int getASize() {
            return asize;
        }

        boolean isPNumeric() {
            return pnumeric;
        }

        String getPName() {
            return pname;
        }

        Class getJClass() {
            return jclass;
        }
    }

    static class PType {
        private final PClass pclass;
        private final Class jclass;
        private final int pdimensions;
        private final PSort psort;

        PType(final PClass pclass, final Class jclass, final int pdimensions, final PSort psort) {
            this.pclass = pclass;
            this.jclass = jclass;
            this.pdimensions = pdimensions;
            this.psort = psort;
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

        PSort getPSort() {
            return psort;
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

            return pclass.equals(ptype.pclass);
        }

        @Override
        public int hashCode() {
            int result = pclass.hashCode();
            result = 31 * result + pdimensions;

            return result;
        }
    }

    static class PConstructor {
        private final String pname;
        private final PClass powner;
        private final List<PType> parguments;
        private final List<PType> poriginals;
        private final Constructor jconstructor;

        private PConstructor(final String pname, final PClass powner,
                             final List<PType> parguments, final List<PType> poriginals,
                             final Constructor jconstructor) {
            this.pname = pname;
            this.powner = powner;
            this.parguments = Collections.unmodifiableList(parguments);
            this.poriginals = Collections.unmodifiableList(poriginals);
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

        List<PType> getPOriginals() {
            return poriginals;
        }

        Constructor getJConstructor() {
            return jconstructor;
        }
    }

    static class PMethod {
        private final String pname;
        private final PClass powner;
        private final PType preturn;
        private final PType poreturn;
        private final List<PType> parguments;
        private final List<PType> poriginals;
        private final Method jmethod;

        private PMethod(final String pname, final PClass powner, final PType preturn, final PType poreturn,
                        final List<PType> parguments, final List<PType> poriginals, final Method jmethod) {
            this.pname = pname;
            this.powner = powner;
            this.preturn = preturn;
            this.poreturn = poreturn;
            this.parguments = Collections.unmodifiableList(parguments);
            this.poriginals = Collections.unmodifiableList(poriginals);
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

        PType getPOReturn() {
            return poreturn;
        }

        List<PType> getPArguments() {
            return parguments;
        }

        List<PType> getPOriginals() {
            return poriginals;
        }

        Method getJMethod() {
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

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            PClass pclass = (PClass)object;

            return pname.equals(pclass.pname);
        }

        @Override
        public int hashCode() {
            return pname.hashCode();
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

            final PCast pcast = (PCast)object;

            if (!pfrom.equals(pcast.pfrom)) {
                return false;
            }

            return pto.equals(pcast.pto);
        }

        @Override
        public int hashCode() {
            int result = pfrom.hashCode();
            result = 31 * result + pto.hashCode();

            return result;
        }
    }

    static class PTransform {
        private final PCast pcast;
        private final PMethod pmethod;
        private final boolean pcastfrom;
        private final boolean pcastto;

        private PTransform(final PCast pcast, PMethod pmethod, final boolean pcastfrom, final boolean pcastto) {
            this.pcast = pcast;
            this.pmethod = pmethod;
            this.pcastfrom = pcastfrom;
            this.pcastto = pcastto;
        }

        PCast getPCast() {
            return pcast;
        }

        PMethod getPMethod() {
            return pmethod;
        }

        boolean getPCastFrom() {
            return pcastfrom;
        }

        boolean getPCastTo() {
            return pcastto;
        }
    }

    static class PTypes {
        private final Map<String, PClass> pclasses;

        private final Set<PCast> pdisalloweds;
        private final Map<PCast, PTransform> pexplicits;
        private final Map<PCast, PTransform> pimplicits;

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

        PTransform getPExplicit(final PCast pcast) {
            return pexplicits.get(pcast);
        }

        PTransform getPImplicit(final PCast pcast) {
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
                                key.startsWith("static")      || key.startsWith("member")     ||
                                key.startsWith("transform")   || key.startsWith("disallow");

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

            if (key.startsWith("cross")) {
                loadPCrossFromProperty(ptypes, property);
            } else if (key.startsWith("transform")) {
                loadPTransformFromProperty(ptypes, property);
            } else if (key.startsWith("disallow")) {
                loadPDisallowFromProperty(ptypes, property);
            }
        }

        createPSortTypes(ptypes);
        validatePMethods(ptypes);

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

    private static void loadPCrossFromProperty(final PTypes ptypes, final String property) {
        final String[] split = property.split("\\s+");

        if (split.length != 5) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String ptypestr = split[0];
        final String pcrossstr = split[1];
        final String pnamestr = split[2];
        final String pownerstr = split[3];
        final String pstaticstr = split[4];

        loadPCross(ptypes, ptypestr, pcrossstr, pnamestr, pownerstr, pstaticstr);
    }

    private static void loadPTransformFromProperty(final PTypes ptypes, final String property) {
        final String[] split = property.split("\\s+");

        if (split.length != 6) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String ptypestr = split[0];
        final String pfromstr = split[1];
        final String ptostr = split[2];
        final String pownerstr = split[3];
        final String pstaticstr = split[4];
        final String pmethodstr = split[5];

        loadPTransform(ptypes, ptypestr, pfromstr, ptostr, pownerstr, pstaticstr, pmethodstr);
    }

    private static void loadPDisallowFromProperty(final PTypes ptypes, final String property) {
        final String[] split = property.split("\\s+");

        if (split.length != 2) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pfromstr = split[0];
        final String ptostr = split[1];

        loadPDisallow(ptypes, pfromstr, ptostr);
    }

    private static void loadPClass(final PTypes ptypes, final String pnamestr, final String jclassstr) {
        if (!pnamestr.matches("^[$_a-zA-Z][_a-zA-Z0-9]+$")) {
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
        final PClass powner = ptypes.pclasses.get(pownerstr);

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
        final PType[] poriginals = new PType[length];
        final Class[] jarguments = new Class[length];

        for (int pargumentindex = 0; pargumentindex < length; ++ pargumentindex) {
            final String[] pargumentstrs = pargumentsstrs[pargumentindex];

            if (pargumentstrs.length == 1) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]);
                poriginals[pargumentindex] = parguments[pargumentindex];
                jarguments[pargumentindex] = parguments[pargumentindex].jclass;
            } else if (pargumentstrs.length == 2) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[1]);
                poriginals[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]);
                jarguments[pargumentindex] = poriginals[pargumentindex].jclass;
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        final Constructor jconstructor = getJConstructorFromJClass(powner.jclass, jarguments);
        final PConstructor pconstructor = new PConstructor(pnamestr, powner,
                Arrays.asList(parguments), Arrays.asList(poriginals), jconstructor);

        powner.pconstructors.put(pnamestr, pconstructor);
    }

    private static void loadPMethod(final PTypes ptypes, final String pownerstr, final String pnamestr,
                                    final String preturnstr, final String jnamestr,
                                    final String[][] pargumentsstrs, boolean statik) {
        final PClass powner = ptypes.pclasses.get(pownerstr);

        if (powner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!pnamestr.matches("^[_a-zA-Z][_a-zA-Z0-9]+$")) {
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
        PType poreturn;
        Class jreturn;

        if (preturnstrs.length == 1) {
            preturn = getPTypeFromCanonicalPName(ptypes, preturnstrs[0]);
            poreturn = preturn;
            jreturn = preturn.jclass;
        } else if (preturnstrs.length == 2) {
            preturn = getPTypeFromCanonicalPName(ptypes, preturnstrs[1]);
            poreturn = getPTypeFromCanonicalPName(ptypes, preturnstrs[0]);
            jreturn = poreturn.jclass;
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }

        final int length = pargumentsstrs.length;
        final PType[] parguments = new PType[length];
        final PType[] poriginals = new PType[length];
        final Class[] jarguments = new Class[length];

        for (int pargumentindex = 0; pargumentindex < length; ++ pargumentindex) {
            final String[] pargumentstrs = pargumentsstrs[pargumentindex];

            if (pargumentstrs.length == 1) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]);
                poriginals[pargumentindex] = parguments[pargumentindex];
                jarguments[pargumentindex] = parguments[pargumentindex].jclass;
            } else if (pargumentstrs.length == 2) {
                parguments[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[1]);
                poriginals[pargumentindex] = getPTypeFromCanonicalPName(ptypes, pargumentstrs[0]);
                jarguments[pargumentindex] = poriginals[pargumentindex].jclass;
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        final Method jmethod = getJMethodFromJClass(powner.jclass, jnamestr, jarguments);

        if (!jreturn.equals(jmethod.getReturnType())) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final PMethod pmethod = new PMethod(pnamestr, powner, preturn, poreturn,
                Arrays.asList(parguments), Arrays.asList(poriginals), jmethod);
        final int modifiers = jmethod.getModifiers();

        if (statik) {
            if (!Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            powner.pfunctions.put(pnamestr, pmethod);
        } else {
            if (Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            powner.pmethods.put(pnamestr, pmethod);
        }
    }

    private static void loadPField(final PTypes ptypes, final String pownerstr, final String pnamestr,
                                   final String ptypestr, final String jnamestr, final boolean statik) {
        final PClass powner = ptypes.pclasses.get(pownerstr);

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
        final int modifiers = jfield.getModifiers();

        if (statik) {
            if (!Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (!Modifier.isFinal(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            powner.pstatics.put(pnamestr, pfield);
        } else {
            if (Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            powner.pmembers.put(pnamestr, pfield);
        }
    }

    private static void loadPCross(final PTypes ptypes, final String ptypestr, final String pcrossstr,
                                   final String pnamestr, final String pownerstr, final String pstaticstr) {
        final PClass powner = ptypes.pclasses.get(pownerstr);

        if (powner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final PClass pcross = ptypes.pclasses.get(pcrossstr);

        if (pcross == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if ("function".equals(ptypestr)) {
            final PMethod pfunction = powner.pfunctions.get(pstaticstr);

            if (pfunction == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            pcross.pfunctions.put(pnamestr, pfunction);
        } else if ("static".equals(ptypestr)) {
            final PField pstatic = powner.pstatics.get(pstaticstr);

            if (pstatic == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            pcross.pstatics.put(pnamestr, pstatic);
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static void loadPTransform(final PTypes ptypes, final String ptypestr,
                                       final String pfromstr, final String ptostr, final String pownerstr,
                                       final String pstaticstr, final String pmethodstr) {
        final PClass powner = ptypes.pclasses.get(pownerstr);

        if (powner == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final PType pfrom = getPTypeFromCanonicalPName(ptypes, pfromstr);
        final PType pto = getPTypeFromCanonicalPName(ptypes, ptostr);

        final PCast pcast = new PCast(pfrom, pto);

        if (ptypes.pdisalloweds.contains(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        PMethod pmethod;
        boolean pcastfrom = false;
        boolean pcastto = false;

        if ("function".equals(pstaticstr)) {
            pmethod = powner.pfunctions.get(pmethodstr);

            if (pmethod == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (pmethod.parguments.size() != 1) {
                throw new IllegalArgumentException(); // TODO: message
            }

            PType pargument = pmethod.getPArguments().get(0);

            try {
                pfrom.jclass.asSubclass(pargument.jclass);
            } catch (ClassCastException cce0) {
                try {
                    pargument.jclass.asSubclass(pfrom.jclass);
                    pcastfrom = true;
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }

            final PType preturn = pmethod.preturn;

            try {
                pto.jclass.asSubclass(preturn.jclass);
            } catch (ClassCastException cce0) {
                try {
                    preturn.jclass.asSubclass(pto.jclass);
                    pcastto = true;
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        } else if ("method".equals(pstaticstr)) {
            pmethod = powner.pmethods.get(pmethodstr);

            if (pmethod == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (!pmethod.parguments.isEmpty()) {
                throw new IllegalArgumentException(); // TODO: message
            }

            try {
                pfrom.jclass.asSubclass(powner.jclass);
            } catch (ClassCastException cce0) {
                try {
                    powner.jclass.asSubclass(pfrom.jclass);
                    pcastfrom = true;
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }

            final PType preturn = pmethod.preturn;

            try {
                pto.jclass.asSubclass(preturn.jclass);
            } catch (ClassCastException cce0) {
                try {
                    preturn.jclass.asSubclass(pto.jclass);
                    pcastto = true;
                } catch (ClassCastException cce1) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }

        final PTransform ptransform = new PTransform(pcast, pmethod, pcastfrom, pcastto);

        if ("explicit".equals(ptypestr)) {
            if (ptypes.pexplicits.containsKey(pcast)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            ptypes.pexplicits.put(pcast, ptransform);
        } else if ("implicit".equals(ptypestr)) {
            if (ptypes.pimplicits.containsKey(pcast)) {
                throw new IllegalArgumentException(); // TODO: message
            }

            ptypes.pimplicits.put(pcast, ptransform);
        } else {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static void loadPDisallow(final PTypes ptypes, final String pfromstr, final String ptostr) {
        final PType pfrom = getPTypeFromCanonicalPName(ptypes, pfromstr);
        final PType pto = getPTypeFromCanonicalPName(ptypes, ptostr);

        final PCast pcast = new PCast(pfrom, pto);

        if (ptypes.pdisalloweds.contains(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (ptypes.pexplicits.containsKey(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (ptypes.pimplicits.containsKey(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        ptypes.pdisalloweds.add(pcast);
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

    static PType getPTypeFromCanonicalPName(final PTypes ptypes, final String pnamestr) {
        final int dimensions = getArrayDimensionsFromCanonicalName(pnamestr);

        if (dimensions == 0) {
            final PClass pclass = ptypes.pclasses.get(pnamestr);

            if (pclass == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            PSort psort = PSort.OBJECT;

            for (PSort pvalue : PSort.values()) {
                if (pvalue == PSort.ARRAY) {
                    continue;
                }

                if (pvalue.getPName().equals(pnamestr)) {
                    psort = pvalue;
                }
            }

            return new PType(pclass, pclass.jclass, 0, psort);
        } else {
            final int index = pnamestr.indexOf('[');
            final int length = pnamestr.length();
            final String pclassstr = pnamestr.substring(0, index);
            final PClass pclass = ptypes.pclasses.get(pclassstr);

            if (pclass == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            final String brackets = pnamestr.substring(index, length);
            final String jnamestr = pclass.jclass.getCanonicalName() + brackets;
            final Class jclass = getJClassFromCanonicalJName(jnamestr);

            return new PType(pclass, jclass, dimensions, PSort.ARRAY);
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
                String jclassstr = jnamestr.substring(0, jnamestr.indexOf('['));

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

    private static Constructor getJConstructorFromJClass(final Class jclass, final Class[] jarguments) {
        try {
            return jclass.getConstructor(jarguments);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static Method getJMethodFromJClass(final Class jclass, final String jname, final Class[] jarguments) {
        try {
            return jclass.getMethod(jname, jarguments);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static Field getJFieldFromJClass(final Class jclass, final String jname) {
        try {
            return jclass.getField(jname);
        } catch (NoSuchFieldException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private static void createPSortTypes(PTypes ptypes) {
        for (PSort psort : PSort.values()) {
            if (psort != PSort.ARRAY) {
                final PClass pclass = ptypes.pclasses.get(psort.pname);

                if (pclass == null) {
                    throw new IllegalArgumentException();  // TODO: message
                }

                try {
                    pclass.jclass.asSubclass(psort.jclass);
                } catch (ClassCastException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }
    }

    private static void validatePMethods(PTypes ptypes) {
        for (final PClass pclass : ptypes.pclasses.values()) {
            for (final PConstructor pconstructor : pclass.pconstructors.values()) {
                final int length = pconstructor.parguments.size();
                final List<PType> parguments = pconstructor.parguments;
                final List<PType> poriginals = pconstructor.poriginals;

                for (int argument = 0; argument < length; ++argument) {
                    validateArgument(ptypes, parguments.get(argument), poriginals.get(argument));
                }
            }

            for (final PMethod pfunction : pclass.pfunctions.values()) {
                final int length = pfunction.parguments.size();
                final List<PType> parguments = pfunction.parguments;
                final List<PType> poriginals = pfunction.poriginals;

                for (int argument = 0; argument < length; ++argument) {
                    validateArgument(ptypes, parguments.get(argument), poriginals.get(argument));
                }

                validateArgument(ptypes, pfunction.preturn, pfunction.poreturn);
            }

            for (final PMethod pmethod : pclass.pmethods.values()) {
                final int length = pmethod.parguments.size();
                final List<PType> parguments = pmethod.parguments;
                final List<PType> poriginals = pmethod.poriginals;

                for (int argument = 0; argument < length; ++argument) {
                    validateArgument(ptypes, parguments.get(argument), poriginals.get(argument));
                }

                validateArgument(ptypes, pmethod.preturn, pmethod.poreturn);
            }
        }
    }

    private static void validateArgument(PTypes ptypes, PType pargument, PType poriginal) {
        PCast pcast = new PCast(pargument, poriginal);

        if (ptypes.pdisalloweds.contains(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!ptypes.pimplicits.containsKey(pcast)) {
            try {
                pargument.jclass.asSubclass(poriginal.jclass);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }
    }

    private PainlessTypes() {}
}
