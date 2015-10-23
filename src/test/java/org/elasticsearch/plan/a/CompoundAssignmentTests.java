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

/**
 * Tests compound assignments (+=, etc) across all data types
 */
public class CompoundAssignmentTests extends ScriptTestCase {
    public void testAddition() {
        // byte
        assertEquals((byte) 15, exec("byte x = 5; x += 10; return x;"));
        assertEquals((byte) -5, exec("byte x = 5; x += -10; return x;"));
        // short
        assertEquals((short) 15, exec("short x = 5; x += 10; return x;"));
        assertEquals((short) -5, exec("short x = 5; x += -10; return x;"));
        // char
        assertEquals((char) 15, exec("char x = 5; x += 10; return x;"));
        assertEquals((char) 5, exec("char x = 10; x += -5; return x;"));
        // int
        assertEquals(15, exec("int x = 5; x += 10; return x;"));
        assertEquals(-5, exec("int x = 5; x += -10; return x;"));
        // long
        assertEquals(15L, exec("long x = 5; x += 10; return x;"));
        assertEquals(-5L, exec("long x = 5; x += -10; return x;"));
        // float
        assertEquals(15F, exec("float x = 5f; x += 10; return x;"));
        assertEquals(-5F, exec("float x = 5f; x += -10; return x;"));
        // double
        assertEquals(15D, exec("double x = 5.0; x += 10; return x;"));
        assertEquals(-5D, exec("double x = 5.0; x += -10; return x;"));
    }
    
    public void testSubtraction() {
        // byte
        assertEquals((byte) 15, exec("byte x = 5; x -= -10; return x;"));
        assertEquals((byte) -5, exec("byte x = 5; x -= 10; return x;"));
        // short
        assertEquals((short) 15, exec("short x = 5; x -= -10; return x;"));
        assertEquals((short) -5, exec("short x = 5; x -= 10; return x;"));
        // char
        assertEquals((char) 15, exec("char x = 5; x -= -10; return x;"));
        assertEquals((char) 5, exec("char x = 10; x -= 5; return x;"));
        // int
        assertEquals(15, exec("int x = 5; x -= -10; return x;"));
        assertEquals(-5, exec("int x = 5; x -= 10; return x;"));
        // long
        assertEquals(15L, exec("long x = 5; x -= -10; return x;"));
        assertEquals(-5L, exec("long x = 5; x -= 10; return x;"));
        // float
        assertEquals(15F, exec("float x = 5f; x -= -10; return x;"));
        assertEquals(-5F, exec("float x = 5f; x -= 10; return x;"));
        // double
        assertEquals(15D, exec("double x = 5.0; x -= -10; return x;"));
        assertEquals(-5D, exec("double x = 5.0; x -= 10; return x;"));
    }
    
    public void testMultiplication() {
        // byte
        assertEquals((byte) 15, exec("byte x = 5; x *= 3; return x;"));
        assertEquals((byte) -5, exec("byte x = 5; x *= -1; return x;"));
        // short
        assertEquals((short) 15, exec("short x = 5; x *= 3; return x;"));
        assertEquals((short) -5, exec("short x = 5; x *= -1; return x;"));
        // char
        assertEquals((char) 15, exec("char x = 5; x *= 3; return x;"));
        // int
        assertEquals(15, exec("int x = 5; x *= 3; return x;"));
        assertEquals(-5, exec("int x = 5; x *= -1; return x;"));
        // long
        assertEquals(15L, exec("long x = 5; x *= 3; return x;"));
        assertEquals(-5L, exec("long x = 5; x *= -1; return x;"));
        // float
        assertEquals(15F, exec("float x = 5f; x *= 3; return x;"));
        assertEquals(-5F, exec("float x = 5f; x *= -1; return x;"));
        // double
        assertEquals(15D, exec("double x = 5.0; x *= 3; return x;"));
        assertEquals(-5D, exec("double x = 5.0; x *= -1; return x;"));
    }
    
    public void testDivision() {
        // byte
        assertEquals((byte) 15, exec("byte x = 45; x /= 3; return x;"));
        assertEquals((byte) -5, exec("byte x = 5; x /= -1; return x;"));
        // short
        assertEquals((short) 15, exec("short x = 45; x /= 3; return x;"));
        assertEquals((short) -5, exec("short x = 5; x /= -1; return x;"));
        // char
        assertEquals((char) 15, exec("char x = 45; x /= 3; return x;"));
        // int
        assertEquals(15, exec("int x = 45; x /= 3; return x;"));
        assertEquals(-5, exec("int x = 5; x /= -1; return x;"));
        // long
        assertEquals(15L, exec("long x = 45; x /= 3; return x;"));
        assertEquals(-5L, exec("long x = 5; x /= -1; return x;"));
        // float
        assertEquals(15F, exec("float x = 45f; x /= 3; return x;"));
        assertEquals(-5F, exec("float x = 5f; x /= -1; return x;"));
        // double
        assertEquals(15D, exec("double x = 45.0; x /= 3; return x;"));
        assertEquals(-5D, exec("double x = 5.0; x /= -1; return x;"));
    }
    
    public void testRemainder() {
        // byte
        assertEquals((byte) 3, exec("byte x = 15; x %= 4; return x;"));
        assertEquals((byte) -3, exec("byte x = (byte) -15; x %= 4; return x;"));
        // short
        assertEquals((short) 3, exec("short x = 15; x %= 4; return x;"));
        assertEquals((short) -3, exec("short x = (short) -15; x %= 4; return x;"));
        // char
        assertEquals((char) 3, exec("char x = (char) 15; x %= 4; return x;"));
        // int
        assertEquals(3, exec("int x = 15; x %= 4; return x;"));
        assertEquals(-3, exec("int x = -15; x %= 4; return x;"));
        // long
        assertEquals(3L, exec("long x = 15L; x %= 4; return x;"));
        assertEquals(-3L, exec("long x = -15L; x %= 4; return x;"));
        // float
        assertEquals(3F, exec("float x = 15F; x %= 4; return x;"));
        assertEquals(-3F, exec("float x = -15F; x %= 4; return x;"));
        // double
        assertEquals(3D, exec("double x = 15.0; x %= 4; return x;"));
        assertEquals(-3D, exec("double x = -15.0; x %= 4; return x;"));
    }

    @AwaitsFix(bugUrl = "buggy")
    public void testLeftShift() {
        // byte
        assertEquals((byte) 60, exec("byte x = 15; x <<= 2; return x;"));
        assertEquals((byte) -60, exec("byte x = (byte) -15; x <<= 2; return x;"));
        // short
        assertEquals((short) 60, exec("short x = 15; x <<= 2; return x;"));
        assertEquals((short) -60, exec("short x = (short) -15; x <<= 2; return x;"));
        // char
        assertEquals((char) 60, exec("char x = (char) 15; x <<= 2; return x;"));
        // int
        assertEquals(60, exec("int x = 15; x <<= 2; return x;"));
        assertEquals(-60, exec("int x = -15; x <<= 2; return x;"));
        // long
        assertEquals(60L, exec("long x = 15L; x <<= 2; return x;"));
        assertEquals(-60L, exec("long x = -15L; x <<= 2; return x;"));
    }
}
