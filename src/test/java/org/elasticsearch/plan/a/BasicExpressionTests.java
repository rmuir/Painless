package org.elasticsearch.plan.a;

import java.util.Collections;

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

public class BasicExpressionTests extends ScriptTestCase {

    /** simple tests returning a constant value */
    public void testReturnConstant() {
        assertEquals(5, exec("return 5;"));
        assertEquals(7L, exec("return 7L;"));
        assertEquals(7.0, exec("return 7.0;"));
        assertEquals(32.0F, exec("return 32.0F;"));
        assertEquals((byte)255, exec("return (byte)255;"));
        assertEquals((short)5, exec("return (short)5;"));
        assertEquals("string", exec("return \"string\";"));
        assertEquals(true, exec("return true;"));
        assertEquals(false, exec("return false;"));
        assertNull(exec("return null;"));
    }

    public void testReturnConstantChar() {
        assertEquals('x', exec("return 'x';"));
    }

    public void testConstantCharTruncation() {
        assertEquals('蚠', exec("return (char)100000;"));
    }

    /** declaring variables for primitive types */
    public void testDeclareVariable() {
        assertEquals(5, exec("int i = 5; return i;"));
        assertEquals(7L, exec("long l = 7; return l;"));
        assertEquals(7.0, exec("double d = 7; return d;"));
        assertEquals(32.0F, exec("float f = 32F; return f;"));
        assertEquals((byte)255, exec("byte b = (byte)255; return b;"));
        assertEquals((short)5, exec("short s = (short)5; return s;"));
        assertEquals("string", exec("string s = \"string\"; return s;"));
        assertEquals(true, exec("bool v = true; return v;"));
        assertEquals(false, exec("bool v = false; return v;"));
    }

    public void testCast() {
        assertEquals(1, exec("return (int)true;"));
        assertEquals((byte)100, exec("double x = 100; return (byte)x;"));

        assertEquals(3, exec(
                "map x = hashmap.new();\n" +
                "object y = x;\n" +
                "((map)y).put(2, 3);\n" +
                "return x.get(2);\n"));
    }

    public void testCat() {
        assertEquals("aaabbb", exec("return \"aaa\" .. \"bbb\";"));
        assertEquals("aaabbb", exec("string aaa = \"aaa\", bbb = \"bbb\"; return aaa .. bbb;"));

        assertEquals("aaabbbbbbbbb", exec(
                "string aaa = \"aaa\", bbb = \"bbb\"; int x;\n" +
                "for (; x < 3; ++x) \n" +
                "    aaa ..= bbb;\n" +
                "return aaa;"));
    }

    public void testComp() {
        assertEquals(true, exec("return 2 < 3;"));
        assertEquals(false, exec("int x = 4; char y = 2; return x < y;"));
        assertEquals(true, exec("return 3 <= 3;"));
        assertEquals(true, exec("int x = 3; char y = 3; return x <= y;"));
        assertEquals(false, exec("return 2 > 3;"));
        assertEquals(true, exec("int x = 4; long y = 2; return x > y;"));
        assertEquals(false, exec("return 3 >= 4;"));
        assertEquals(true, exec("double x = 3; float y = 3; return x >= y;"));
        assertEquals(false, exec("return 3 == 4;"));
        assertEquals(true, exec("double x = 3; float y = 3; return x == y;"));
        assertEquals(true, exec("return 3 != 4;"));
        assertEquals(false, exec("double x = 3; float y = 3; return x != y;"));
    }
    
    /** 
     * Test boxed objects in various places
     */
    public void testBoxing() {
        // return
        assertEquals(4, exec("return input.get(\"x\");", Collections.singletonMap("x", 4)));
        // assignment
        assertEquals(4, exec("int y = (intobj)input.get(\"x\"); return y;", Collections.singletonMap("x", 4)));
        // comparison
        assertEquals(true, exec("return 5 > (intobj)input.get(\"x\");", Collections.singletonMap("x", 4)));
    }

    public void testBool() {
        assertEquals(true, exec("return true && true;"));
        assertEquals(false, exec("bool a = true, b = false; return a && b;"));
        assertEquals(true, exec("return true || true;"));
        assertEquals(true, exec("bool a = true, b = false; return a || b;"));
    }

    public void testConditional() {
        assertEquals(1, exec("int x = 5; return x > 3 ? 1 : 0;"));
        assertEquals(0, exec("string a = null; return a != null ? 1 : 0;"));
    }

    public void testPrecedence() {
        assertEquals(2, exec("int x = 5; return (x+x)/x;"));
        assertEquals(true, exec("bool t = true, f = false; return t && (f || t);"));
    }
}
