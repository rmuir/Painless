package painless;

public class PainlessUtility {
    public static Integer booleanToInteger(boolean value) {
        return new Integer(value ? 1 : 0);
    }

    public static Integer charToInteger(char value) {
        return new Integer(value);
    }

    public static boolean numberToBoolean(Number value) {
        return value.longValue() != 0;
    }

    public static char numberToChar(Number value) {
        return (char)value.longValue();
    }

    public static byte booleanToByte(boolean value) {
        return (byte)(value ? 1 : 0);
    }

    public static short booleanToShort(boolean value) {
        return (short)(value ? 1 : 0);
    }

    public static char booleanToChar(boolean value) {
        return (char)(value ? 1 : 0);
    }

    public static int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

    public static long booleanToLong(boolean value) {
        return value ? 1 : 0;
    }

    public static float booleanToFloat(boolean value) {
        return value ? 1 : 0;
    }

    public static double booleanToDouble(boolean value) {
        return value ? 1 : 0;
    }

    public static byte booleanToByte(Boolean value) {
        return (byte)(value ? 1 : 0);
    }

    public static short booleanToShort(Boolean value) {
        return (short)(value ? 1 : 0);
    }

    public static char booleanToCharacter(Boolean value) {
        return (char)(value ? 1 : 0);
    }

    public static int booleanToInteger(Boolean value) {
        return value ? 1 : 0;
    }

    public static long booleanToLong(Boolean value) {
        return value ? 1 : 0;
    }

    public static float booleanToFloat(Boolean value) {
        return value ? 1 : 0;
    }

    public static double booleanToDouble(Boolean value) {
        return value ? 1 : 0;
    }

    public static boolean byteToBoolean(Byte value) {
        return value != 0;
    }

    public static char byteToChar(Byte value) {
        return (char)value.byteValue();
    }

    public static boolean shortToBoolean(Short value) {
        return value != 0;
    }

    public static char shortToChar(Short value) {
        return (char)value.shortValue();
    }
}
