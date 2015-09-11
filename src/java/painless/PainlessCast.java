package painless;

import org.objectweb.asm.Type;

class PainlessCast {
    /*static final Type OBJECT_TYPE     = Type.getType("Ljava/lang/Object;");
    static final Type STRING_TYPE     = Type.getType("Ljava/lang/String;");

    static boolean isNumeric(Type type) {
        if (NUMBER_TYPE.equals(type) || COMPARATOR_TYPE.equals(type)) {
            return true;
        }

        int sort = type.getSort();
        return sort == Type.BYTE    ||
               sort == type.CHAR    ||
               sort == type.SHORT   ||
               sort == type.INT     ||
               sort == Type.LONG    ||
               sort == Type.FLOAT   ||
               sort == Type.DOUBLE;
    }

    static final boolean isLegalCast(Type from, Type to, boolean explicit) {
        return  from.equals(to) ||
                OBJECT_TYPE.equals(to) ||
                isNumeric(from) && isNumeric(to) ||
                OBJECT_TYPE.equals(from) && explicit;
    }

    final private Type from;
    final private Type to;

    PainlessCast(final Type from, final Type to) {
        this.from = from;
        this.to = to;
    }*/
}
