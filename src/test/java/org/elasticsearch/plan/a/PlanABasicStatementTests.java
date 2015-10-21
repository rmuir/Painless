package org.elasticsearch.plan.a;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

public class PlanABasicStatementTests extends ESTestCase {
    private PlanAScriptEngineService se;

    @Before
    public void setup() {
        se = new PlanAScriptEngineService(Settings.Builder.EMPTY_SETTINGS);
    }

    public Object testScript(final String test, final String script) {
        final Object object = se.compile(script);
        final CompiledScript compiled = new CompiledScript(ScriptService.ScriptType.INLINE, test, "plan-a", object);
        final Object value = se.executable(compiled, null).run();

        return value;
    }

    public void testIfStatement() {
        Object value;

        value = testScript("testIfStatement", "int x = 5; if (x == 5) return 1; return 0;");
        assertEquals(1, value);

        value = testScript("testIfStatement", "int x = 4; if (x == 5) return 1; else return 0;");
        assertEquals(0, value);

        value = testScript("testIfStatement", "int x = 4; if (x == 5) return 1; else if (x == 4) return 2; else return 0;");
        assertEquals(2, value);

        value = testScript("testIfStatement", "int x = 4; if (x == 5) return 1; else if (x == 4) return 1; else return 0;");
        assertEquals(1, value);

        value = testScript("testIfStatement",
                "int x = 5;\n" +
                "if (x == 5) {\n" +
                "    int y = 2;\n" +
                "    \n" +
                "    if (y == 2) {\n" +
                "        x = 3;\n" +
                "    }\n" +
                "    \n" +
                "}\n" +
                "\n" +
                "return x;\n");
        assertEquals(3, value);
    }

    public void testWhileStatement() throws Exception {
        Object value;

        value = testScript("testWhile", "string c = \"a\"; int x; while (x < 5) { c ..= \"a\"; ++x; } return c;");
        assertEquals("aaaaaa", value);

        value = testScript("testWhile",
                "int[][] b = int.makearray(5, 5);\n" +
                "byte x = 0, y = 0;\n" +
                "\n" +
                "while (x < 5) {\n" +
                "    while (y < 5) {\n" +
                "        b[x][y] = (x*y);\n" +
                "        ++y;\n" +
                "    }\n" +
                "    \n" +
                "    ++x;\n" +
                "}\n" +
                "\n" +
                "return b;");

        int[][] b = (int[][])value;

        for (byte x = 0; x < 5; ++x) {
            for (byte y = 0; y < 5; ++y) {
                System.out.println(x + " " + y + " : " + b[x][y]);
                assertEquals(x*y, b[x][y]);
            }
        }
    }
}
