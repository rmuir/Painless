package painless;

public class Utility {
    public static boolean numberToBoolean(final Number value) {
        return value.longValue() != 0;
    }

    public static char numberToChar(final Number value) {
        return (char)value.longValue();
    }

    public static byte booleanToByte(final boolean value) {
        return (byte)(value ? 1 : 0);
    }

    public static short booleanToShort(final boolean value) {
        return (short)(value ? 1 : 0);
    }

    public static char booleanToChar(final boolean value) {
        return (char)(value ? 1 : 0);
    }

    public static int booleanToInt(final boolean value) {
        return value ? 1 : 0;
    }

    public static long booleanToLong(final boolean value) {
        return value ? 1 : 0;
    }

    public static float booleanToFloat(final boolean value) {
        return value ? 1 : 0;
    }

    public static double booleanToDouble(final boolean value) {
        return value ? 1 : 0;
    }

    public static Integer booleanToInteger(final boolean value) {
        return value ? 1 : 0;
    }

    public static byte booleanToByte(final Boolean value) {
        return (byte)(value ? 1 : 0);
    }

    public static short booleanToShort(final Boolean value) {
        return (short)(value ? 1 : 0);
    }

    public static char booleanToCharacter(final Boolean value) {
        return (char)(value ? 1 : 0);
    }

    public static int booleanToInteger(final Boolean value) {
        return value ? 1 : 0;
    }

    public static long booleanToLong(final Boolean value) {
        return value ? 1 : 0;
    }

    public static float booleanToFloat(final Boolean value) {
        return value ? 1 : 0;
    }

    public static double booleanToDouble(final Boolean value) {
        return value ? 1 : 0;
    }

    public static boolean byteToBoolean(final byte value) {
        return value != 0;
    }

    public static boolean byteToBoolean(final Byte value) {
        return value != 0;
    }

    public static char byteToChar(final Byte value) {
        return (char)value.byteValue();
    }

    public static boolean shortToBoolean(final short value) {
        return value != 0;
    }

    public static boolean shortToBoolean(final Short value) {
        return value != 0;
    }

    public static char shortToChar(final Short value) {
        return (char)value.shortValue();
    }

    public static boolean charToBoolean(final char value) {
        return value != 0;
    }

    public static Integer charToInteger(final char value) {
        return (int)value;
    }

    public static boolean characterToBoolean(final Character value) {
        return value != 0;
    }

    public static byte characterToByte(final Character value) {
        return (byte)value.charValue();
    }

    public static short characterToShort(final Character value) {
        return (short)value.charValue();
    }

    public static int characterToInt(final Character value) {
        return (int)value;
    }

    public static long characterToLong(final Character value) {
        return (long)value;
    }

    public static float characterToFloat(final Character value) {
        return (float)value;
    }

    public static double characterToDouble(final Character value) {
        return (double)value;
    }

    public static boolean intToBoolean(final int value) {
        return value != 0;
    }

    public static boolean integerToBoolean(final Integer value) {
        return value != 0;
    }

    public static char integerToChar(final Integer value) {
        return (char)value.intValue();
    }

    public static boolean longToBoolean(final long value) {
        return value != 0;
    }

    public static boolean longToBoolean(final Long value) {
        return value != 0;
    }

    public static char longToChar(final Long value) {
        return (char)value.longValue();
    }

    public static boolean floatToBoolean(final float value) {
        return value != 0;
    }

    public static boolean floatToBoolean(final Float value) {
        return value != 0;
    }

    public static char floatToChar(final Float value) {
        return (char)value.floatValue();
    }

    public static boolean doubleToBoolean(final double value) {
        return value != 0;
    }

    public static boolean doubleToBoolean(final Double value) {
        return value != 0;
    }

    public static char doubleToChar(final Double value) {
        return (char)value.doubleValue();
    }

    public static String substring(final String string, final Integer begin, final Integer end) {
        final int length = string.length();
        final int localBegin = begin == null ? 0 : begin;

        if (localBegin < 0 || localBegin > length) {
            throw new IllegalArgumentException(); // TODO: message
        }

        int localEnd = end == null ? string.length() : end;

        if (localEnd < 0) {
            localEnd = length - localBegin;
        }

        if (localEnd < 0 || localEnd > length) {
            throw new IllegalArgumentException(); // TODO: message
        }

        return string.substring(localBegin, localEnd);
    }
}
