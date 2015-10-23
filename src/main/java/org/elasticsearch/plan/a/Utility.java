package org.elasticsearch.plan.a;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public class Utility {
    public static boolean NumberToboolean(final Number value) {
        return value.longValue() != 0;
    }

    public static char NumberTochar(final Number value) {
        return (char)value.longValue();
    }

    public static byte booleanTobyte(final boolean value) {
        return (byte)(value ? 1 : 0);
    }

    public static short booleanToshort(final boolean value) {
        return (short)(value ? 1 : 0);
    }

    public static char booleanTochar(final boolean value) {
        return (char)(value ? 1 : 0);
    }

    public static int booleanToint(final boolean value) {
        return value ? 1 : 0;
    }

    public static long booleanTolong(final boolean value) {
        return value ? 1 : 0;
    }

    public static float booleanTofloat(final boolean value) {
        return value ? 1 : 0;
    }

    public static double booleanTodouble(final boolean value) {
        return value ? 1 : 0;
    }

    public static Integer booleanToInteger(final boolean value) {
        return value ? 1 : 0;
    }

    public static byte BooleanTobyte(final Boolean value) {
        return (byte)(value ? 1 : 0);
    }

    public static short BooleanToshort(final Boolean value) {
        return (short)(value ? 1 : 0);
    }

    public static char BooleanTochar(final Boolean value) {
        return (char)(value ? 1 : 0);
    }

    public static int BooleanToint(final Boolean value) {
        return value ? 1 : 0;
    }

    public static long BooleanTolong(final Boolean value) {
        return value ? 1 : 0;
    }

    public static float BooleanTofloat(final Boolean value) {
        return value ? 1 : 0;
    }

    public static double BooleanTodouble(final Boolean value) {
        return value ? 1 : 0;
    }

    public static boolean byteToboolean(final byte value) {
        return value != 0;
    }

    public static boolean ByteToboolean(final Byte value) {
        return value != 0;
    }

    public static char ByteTochar(final Byte value) {
        return (char)value.byteValue();
    }

    public static boolean shortToboolean(final short value) {
        return value != 0;
    }

    public static boolean ShortToboolean(final Short value) {
        return value != 0;
    }

    public static char ShortTochar(final Short value) {
        return (char)value.shortValue();
    }

    public static boolean charToboolean(final char value) {
        return value != 0;
    }

    public static Integer charToInteger(final char value) {
        return (int)value;
    }

    public static boolean CharacterToboolean(final Character value) {
        return value != 0;
    }

    public static byte CharacterTobyte(final Character value) {
        return (byte)value.charValue();
    }

    public static short CharacterToshort(final Character value) {
        return (short)value.charValue();
    }

    public static int CharacterToint(final Character value) {
        return (int)value;
    }

    public static long CharacterTolong(final Character value) {
        return (long)value;
    }

    public static float CharacterTofloat(final Character value) {
        return (float)value;
    }

    public static double CharacterTodouble(final Character value) {
        return (double)value;
    }

    public static boolean intToboolean(final int value) {
        return value != 0;
    }

    public static boolean IntegerToboolean(final Integer value) {
        return value != 0;
    }

    public static char IntegerTochar(final Integer value) {
        return (char)value.intValue();
    }

    public static boolean longToboolean(final long value) {
        return value != 0;
    }

    public static boolean LongToboolean(final Long value) {
        return value != 0;
    }

    public static char LongTochar(final Long value) {
        return (char)value.longValue();
    }

    public static boolean floatToboolean(final float value) {
        return value != 0;
    }

    public static boolean FloatToboolean(final Float value) {
        return value != 0;
    }

    public static char FloatTochar(final Float value) {
        return (char)value.floatValue();
    }

    public static boolean doubleToboolean(final double value) {
        return value != 0;
    }

    public static boolean DoubleToboolean(final Double value) {
        return value != 0;
    }

    public static char DoubleTochar(final Double value) {
        return (char)value.doubleValue();
    }
    
    // although divide by zero is guaranteed, the special overflow case is not caught.
    // its not needed for remainder because it is not possible there.
    // see https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.17.2
    
    /**
     * Integer divide without overflow
     * @throws ArithmeticException on overflow or divide-by-zero
     */
    public static int divideWithoutOverflow(int x, int y) {
       if (x == Integer.MIN_VALUE && y == -1) {
           throw new ArithmeticException("integer overflow");
       }
       return x / y;
    }
    
    /**
     * Long divide without overflow
     * @throws ArithmeticException on overflow or divide-by-zero
     */
    public static long divideWithoutOverflow(long x, long y) {
        if (x == Long.MIN_VALUE && y == -1L) {
            throw new ArithmeticException("long overflow");
        }
        return x / y;
     }

    private Utility() {}
}
