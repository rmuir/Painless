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

package org.elasticsearch.plan.a;

/** Tests for increment/decrement operators across all data types */
public class IncrementTests extends ScriptTestCase {

    /** incrementing byte values */
    public void testIncrementByte() {
        assertEquals((byte)0, exec("byte x = (byte)0; return x++;"));
        assertEquals((byte)0, exec("byte x = (byte)0; return x--;"));
        assertEquals((byte)1, exec("byte x = (byte)0; return ++x;"));
        assertEquals((byte)-1, exec("byte x = (byte)0; return --x;"));
    }
    
    /** incrementing char values */
    public void testIncrementChar() {
        assertEquals((char)0, exec("char x = (char)0; return x++;"));
        assertEquals((char)0, exec("char x = (char)0; return x--;"));
        assertEquals((char)1, exec("char x = (char)0; return ++x;"));
        assertEquals((char)-1, exec("char x = (char)0; return --x;"));
    }
    
    /** incrementing short values */
    public void testIncrementShort() {
        assertEquals((short)0, exec("short x = (short)0; return x++;"));
        assertEquals((short)0, exec("short x = (short)0; return x--;"));
        assertEquals((short)1, exec("short x = (short)0; return ++x;"));
        assertEquals((short)-1, exec("short x = (short)0; return --x;"));
    }

    /** incrementing integer values */
    public void testIncrementInt() {
        assertEquals(0, exec("int x = 0; return x++;"));
        assertEquals(0, exec("int x = 0; return x--;"));
        assertEquals(1, exec("int x = 0; return ++x;"));
        assertEquals(-1, exec("int x = 0; return --x;"));
    }
    
    /** incrementing long values */
    public void testIncrementLong() {
        assertEquals(0L, exec("long x = 0; return x++;"));
        assertEquals(0L, exec("long x = 0; return x--;"));
        assertEquals(1L, exec("long x = 0; return ++x;"));
        assertEquals(-1L, exec("long x = 0; return --x;"));
    }
    
    /** incrementing float values */
    public void testIncrementFloat() {
        assertEquals(0F, exec("float x = 0F; return x++;"));
        assertEquals(0F, exec("float x = 0F; return x--;"));
        assertEquals(1F, exec("float x = 0F; return ++x;"));
        assertEquals(-1F, exec("float x = 0F; return --x;"));
    }
    
    /** incrementing double values */
    public void testIncrementDouble() {
        assertEquals(0D, exec("double x = 0.0; return x++;"));
        assertEquals(0D, exec("double x = 0.0; return x--;"));
        assertEquals(1D, exec("double x = 0.0; return ++x;"));
        assertEquals(-1D, exec("double x = 0.0; return --x;"));
    }
    
    public void testIncOverFlow() throws Exception {
        // byte
        assertEquals(Byte.MIN_VALUE, exec("byte x = 127; ++x; return x;"));
        assertEquals(Byte.MIN_VALUE, exec("byte x = 127; x++; return x;"));
        assertEquals(Byte.MAX_VALUE, exec("byte x = (byte) -128; --x; return x;"));
        assertEquals(Byte.MAX_VALUE, exec("byte x = (byte) -128; x--; return x;"));
        
        // short
        assertEquals(Short.MIN_VALUE, exec("short x = 32767; ++x; return x;"));
        assertEquals(Short.MIN_VALUE, exec("short x = 32767; x++; return x;"));
        assertEquals(Short.MAX_VALUE, exec("short x = (short) -32768; --x; return x;"));
        assertEquals(Short.MAX_VALUE, exec("short x = (short) -32768; x--; return x;"));
        
        // char
        assertEquals(Character.MIN_VALUE, exec("char x = 65535; ++x; return x;"));
        assertEquals(Character.MIN_VALUE, exec("char x = 65535; x++; return x;"));
        assertEquals(Character.MAX_VALUE, exec("char x = (char) 0; --x; return x;"));
        assertEquals(Character.MAX_VALUE, exec("char x = (char) 0; x--; return x;"));
        
        // int
        assertEquals(Integer.MIN_VALUE, exec("int x = 2147483647; ++x; return x;"));
        assertEquals(Integer.MIN_VALUE, exec("int x = 2147483647; x++; return x;"));
        assertEquals(Integer.MAX_VALUE, exec("int x = (int) -2147483648L; --x; return x;"));
        assertEquals(Integer.MAX_VALUE, exec("int x = (int) -2147483648L; x--; return x;"));
        
        // long
        try {
            exec("long x = 9223372036854775807L; ++x; return x;");
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
        
        try {
            exec("long x = 9223372036854775807L; x++; return x;");
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}

        try {
            exec("long x = -9223372036854775807L - 1L; --x; return x;");
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}

        try {
            exec("long x = -9223372036854775807L - 1L; x--; return x;");
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
    }
}
