package painless;

public class Utility {
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

    public static Integer booleanToInteger(boolean value) {
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

    public static boolean byteToBoolean(byte value) {
        return value != 0;
    }

    public static boolean byteToBoolean(Byte value) {
        return value != 0;
    }

    public static char byteToChar(Byte value) {
        return (char)value.byteValue();
    }

    public static boolean shortToBoolean(short value) {
        return value != 0;
    }

    public static boolean shortToBoolean(Short value) {
        return value != 0;
    }

    public static char shortToChar(Short value) {
        return (char)value.shortValue();
    }

    public static boolean charToBoolean(char value) {
        return value != 0;
    }

    public static Integer charToInteger(char value) {
        return (int)value;
    }

    public static boolean characterToBoolean(Character value) {
        return value != 0;
    }

    public static byte characterToByte(Character value) {
        return (byte)value.charValue();
    }

    public static short characterToShort(Character value) {
        return (short)value.charValue();
    }

    public static int characterToInt(Character value) {
        return (int)value;
    }

    public static long characterToLong(Character value) {
        return (long)value;
    }

    public static float characterToFloat(Character value) {
        return (float)value;
    }

    public static double characterToDouble(Character value) {
        return (double)value;
    }

    public static boolean intToBoolean(int value) {
        return value != 0;
    }

    public static boolean integerToBoolean(Integer value) {
        return value != 0;
    }

    public static char integerToChar(Integer value) {
        return (char)value.intValue();
    }

    public static boolean longToBoolean(long value) {
        return value != 0;
    }

    public static boolean longToBoolean(Long value) {
        return value != 0;
    }

    public static char longToChar(Long value) {
        return (char)value.longValue();
    }

    public static boolean floatToBoolean(float value) {
        return value != 0;
    }

    public static boolean floatToBoolean(Float value) {
        return value != 0;
    }

    public static char floatToChar(Float value) {
        return (char)value.floatValue();
    }

    public static boolean doubleToBoolean(double value) {
        return value != 0;
    }

    public static boolean doubleToBoolean(Double value) {
        return value != 0;
    }

    public static char doubleToChar(Double value) {
        return (char)value.doubleValue();
    }
}
