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

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

import java.util.Map;

public class PlanABasicExpressionTests extends ESTestCase {
    private PlanAScriptEngineService se;

    @Before
    public void setup() {
        se = new PlanAScriptEngineService(Settings.Builder.EMPTY_SETTINGS);
    }

    public Object testScript(final String test, final String script, final Map<String, Object> vars) {
        final Object object = se.compile(script);
        final CompiledScript compiled = new CompiledScript(ScriptService.ScriptType.INLINE, test, "plan-a", object);
        final Object value = se.executable(compiled, vars).run();

        return value;
    }

    public void testPrecedence() {
        Object value;

        value = testScript("testPrecedence", "int x = 5; return (x+x)/x;", null);
        assertEquals(2, value);

        value = testScript("testPrecedence", "bool t = true, f = false; return t && (f || t);", null);
        assertEquals(true, value);
    }

    public void testConstant() {
        Object value;

        value = testScript("testConstant", "return 5;", null);
        assertEquals(5, value);

        value = testScript("testConstant", "return 7L;", null);
        assertEquals(7L, value);

        value = testScript("testConstant", "return 7.0;", null);
        assertEquals(7.0, value);

        value = testScript("testConstant", "return 32.0F;", null);
        assertEquals(32.0F, value);

        value = testScript("testConstant", "return \"string\";", null);
        assertEquals("string", value);

        value = testScript("testConstant", "return true;", null);
        assertEquals(true, value);

        value = testScript("testConstant", "return false;", null);
        assertEquals(false, value);

        value = testScript("testConstant", "return null;", null);
        assertNull(value);
    }

    public void testIncrement() {
        Object value;

        value = testScript("testIncrement", "int x = 0; return x++;", null);
        assertEquals(0, value);

        value = testScript("testIncrement", "int x = 0; return x--;", null);
        assertEquals(0, value);

        value = testScript("testIncrement", "int x = 0; return ++x;", null);
        assertEquals(1, value);

        value = testScript("testIncrement", "int x = 0; return --x;", null);
        assertEquals(-1, value);
    }

    public void testUnary() {
        Object value;

        value = testScript("testUnary", "return !true;", null);
        assertEquals(false, value);

        value = testScript("testUnary", "bool x = false; return !x;", null);
        assertEquals(true, value);

        value = testScript("testUnary", "return ~1;", null);
        assertEquals(-2, value);

        value = testScript("testUnary", "byte x = 1; return ~x;", null);
        assertEquals(-2, value);

        value = testScript("testUnary", "return +1;", null);
        assertEquals(1, value);

        value = testScript("testUnary", "double x = 1; return +x;", null);
        assertEquals(1.0, value);

        value = testScript("testUnary", "return -1;", null);
        assertEquals(-1, value);

        value = testScript("testUnary", "short x = 2; return -x;", null);
        assertEquals(-2, value);
    }

    public void testCast() {
        Object value;

        value = testScript("testCast", "return (int)true;", null);
        assertEquals(1, value);

        value = testScript("testCast", "double x = 100; return (byte)x;", null);
        assertEquals((byte)100, value);

        value = testScript("testCast",
                "map x = hashmap.new();\n" +
                "object y = x;\n" +
                "((map)y).put(2, 3);\n" +
                "return x.get(2);\n",
                null);
        assertEquals(3, value);
    }

    public void testCat() {
        Object value;

        value = testScript("testCast", "return \"aaa\" .. \"bbb\";", null);
        assertEquals("aaabbb", value);

        value = testScript("testCast", "string aaa = \"aaa\", bbb = \"bbb\"; return aaa .. bbb;", null);
        assertEquals("aaabbb", value);

        value = testScript("testCast",
                "string aaa = \"aaa\", bbb = \"bbb\"; int x;\n" +
                "for (; x < 3; ++x) \n" +
                "    aaa ..= bbb;\n" +
                "return aaa;",
                null);
        assertEquals("aaabbbbbbbbb", value);
    }

    public void testBinary() {
        Object value;

        value = testScript("testBinary", "return 2 * 3;", null);
        assertEquals(6, value);

        value = testScript("testBinary", "int x = 4; char y = 2; return x*y;", null);
        assertEquals(8, value);

        value = testScript("testBinary", "return 2.25F / 1.5F;", null);
        assertEquals(1.5F, value);

        value = testScript("testBinary", "double x = 1; float y = 2; return x / y;", null);
        assertEquals(0.5, value);

        value = testScript("testBinary", "return 2.25F % 1.5F;", null);
        assertEquals(0.75F, value);

        value = testScript("testBinary", "int x = 3; int y = 2; return x % y;", null);
        assertEquals(1, value);

        value = testScript("testBinary", "return 1 + 2;", null);
        assertEquals(3, value);

        value = testScript("testBinary", "double x = 1; byte y = 2; return x + y;", null);
        assertEquals(3.0, value);

        value = testScript("testBinary", "return 2 - 1;", null);
        assertEquals(1, value);

        value = testScript("testBinary", "int x = 1; char y = 2; return x - y;", null);
        assertEquals(-1, value);

        value = testScript("testBinary", "return 1 << 2;", null);
        assertEquals(4, value);

        value = testScript("testBinary", "int x = 1; char y = 2; return x << y;", null);
        assertEquals(4, value);

        value = testScript("testBinary", "return 4 >> 2;", null);
        assertEquals(1, value);

        value = testScript("testBinary", "int x = -1; char y = 29; return x >> y;", null);
        assertEquals(-1, value);

        value = testScript("testBinary", "return -1 >>> 29;", null);
        assertEquals(7, value);

        value = testScript("testBinary", "int x = -1; char y = 30; return x >>> y;", null);
        assertEquals(3, value);

        value = testScript("testBinary", "return 5L & 3;", null);
        assertEquals(1L, value);

        value = testScript("testBinary", "int x = 5; long y = 3; return x & y;", null);
        assertEquals(1L, value);

        value = testScript("testBinary", "return 5 | 3;", null);
        assertEquals(7, value);

        value = testScript("testBinary", "short x = 5; byte y = 3; return x | y;", null);
        assertEquals(7, value);

        value = testScript("testBinary", "return 9 ^ 3;", null);
        assertEquals(10, value);

        value = testScript("testBinary", "short x = 9; char y = 3; return x ^ y;", null);
        assertEquals(10, value);
    }

    public void testComp() {
        Object value;

        value = testScript("testComp", "return 2 < 3;", null);
        assertEquals(true, value);

        value = testScript("testComp", "int x = 4; char y = 2; return x < y;", null);
        assertEquals(true, value);

        value = testScript("testComp", "return 3 <= 3;", null);
        assertEquals(true, value);

        value = testScript("testComp", "int x = 3; char y = 3; return x <= y;", null);
        assertEquals(true, value);

        value = testScript("testComp", "return 2 > 3;", null);
        assertEquals(true, value);

        value = testScript("testComp", "int x = 4; long y = 2; return x > y;", null);
        assertEquals(false, value);

        value = testScript("testComp", "return 3 >= 4;", null);
        assertEquals(false, value);

        value = testScript("testComp", "double x = 3; float y = 3; return x >= y;", null);
        assertEquals(true, value);

        value = testScript("testComp", "return 3 == 4;", null);
        assertEquals(false, value);

        value = testScript("testComp", "double x = 3; float y = 3; return x == y;", null);
        assertEquals(true, value);

        value = testScript("testComp", "return 3 != 4;", null);
        assertEquals(true, value);

        value = testScript("testComp", "double x = 3; float y = 3; return x != y;", null);
        assertEquals(false, value);
    }

    public void testBool() {
        Object value;

        value = testScript("testBool", "return true && true;", null);
        assertEquals(true, value);

        value = testScript("testBool", "bool a = true, b = false; return a && b;", null);
        assertEquals(false, value);

        value = testScript("testBool", "return true || true;", null);
        assertEquals(true, value);

        value = testScript("testBool", "bool a = true, b = false; return a || b;", null);
        assertEquals(true, value);
    }

    public void testConditional() {
        Object value;

        value = testScript("testConditional", "int x = 5; return x > 3 ? 1 : 0;", null);
        assertEquals(1, value);

        value = testScript("testConditional", "string a = null; return a != null ? 1 : 0;", null);
        assertEquals(0, value);
    }
}
