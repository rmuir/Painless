package org.elasticsearch.plan.a;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

import java.util.Map;

public class PlanABasicExpressionsTests extends ESTestCase {
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
        assertEquals(value, 2);

        value = testScript("testPrecedence", "bool t = true, f = false; return t && (f || t);", null);
        assertEquals(value, true);
    }

    public void testConstant() {
        Object value;

        value = testScript("testConstant", "return 5;", null);
        assertEquals(value, 5);

        value = testScript("testConstant", "return 7L;", null);
        assertEquals(value, 7L);

        value = testScript("testConstant", "return 7.0;", null);
        assertEquals(value, 7.0);

        value = testScript("testConstant", "return 32.0F;", null);
        assertEquals(value, 32.0F);

        value = testScript("testConstant", "return \"string\";", null);
        assertEquals(value, "string");

        value = testScript("testConstant", "return true;", null);
        assertEquals(value, true);

        value = testScript("testConstant", "return false;", null);
        assertEquals(value, false);

        value = testScript("testConstant", "return null;", null);
        assertNull(value);
    }

    public void testIncrement() {
        Object value;

        value = testScript("testIncrement", "int x = 0; return x++;", null);
        assertEquals(value, 0);

        value = testScript("testIncrement", "int x = 0; return x--;", null);
        assertEquals(value, 0);

        value = testScript("testIncrement", "int x = 0; return ++x;", null);
        assertEquals(value, 1);

        value = testScript("testIncrement", "int x = 0; return --x;", null);
        assertEquals(value, -1);
    }

    public void testUnary() {
        Object value;

        value = testScript("testUnary", "return !true;", null);
        assertEquals(value, false);

        value = testScript("testUnary", "bool x = false; return !x;", null);
        assertEquals(value, true);

        value = testScript("testUnary", "return ~1;", null);
        assertEquals(value, -2);

        value = testScript("testUnary", "byte x = 1; return ~x;", null);
        assertEquals(value, -2);

        value = testScript("testUnary", "return +1;", null);
        assertEquals(value, 1);

        value = testScript("testUnary", "double x = 1; return +x;", null);
        assertEquals(value, 1.0);

        value = testScript("testUnary", "return -1;", null);
        assertEquals(value, -1);

        value = testScript("testUnary", "short x = 2; return -x;", null);
        assertEquals(value, -2);
    }

    public void testCast() {
        Object value;

        value = testScript("testCast", "return (int)true;", null);
        assertEquals(value, 1);

        value = testScript("testCast", "double x = 100; return (byte)x;", null);
        assertEquals(value, (byte)100);

        value = testScript("testCast",
                "map x = hashmap.new();\n" +
                "object y = x;\n" +
                "((map)y).put(2, 3);\n" +
                "return x.get(2);\n",
                null);
        assertEquals(value, 3);
    }

    public void testCat() {
        Object value;

        value = testScript("testCast", "return \"aaa\" .. \"bbb\";", null);
        assertEquals(value, "aaabbb");

        value = testScript("testCast", "string aaa = \"aaa\", bbb = \"bbb\"; return aaa .. bbb;", null);
        assertEquals(value, "aaabbb");

        value = testScript("testCast",
                "string aaa = \"aaa\", bbb = \"bbb\";\n" +
                "for (int x = 0; x < 3; ++x)\n" +
                "    aaa ..= bbb;\n" +
                "return aaa;",
                null);
        assertEquals(value, "aaabbbbbbbbb");
    }


}
