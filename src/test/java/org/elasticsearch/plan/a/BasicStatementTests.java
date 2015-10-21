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

public class BasicStatementTests extends ScriptTestCase {

    public void testIfStatement() {
        assertEquals(1, exec("int x = 5; if (x == 5) return 1; return 0;"));
        assertEquals(0, exec("int x = 4; if (x == 5) return 1; else return 0;"));
        assertEquals(2, exec("int x = 4; if (x == 5) return 1; else if (x == 4) return 2; else return 0;"));
        assertEquals(1, exec("int x = 4; if (x == 5) return 1; else if (x == 4) return 1; else return 0;"));

        assertEquals(3, exec(
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
                "return x;\n"));
    }

    public void testWhileStatement() throws Exception {

        //assertEquals("aaaaaa", exec("string c = \"a\"; int x; while (x < 5) { c ..= \"a\"; ++x; } return c;"));

        Object value = exec(
                " byte[][] b = byte.makearray(5, 5); \n" +
                " byte x = 0, y;                     \n" +
                "                                    \n" +
                " while (x < 5) {                    \n" +
                "     y = 0;                         \n" +
                "                                    \n" +
                "     while (y < 5) {                \n" +
                "         b[x][y] = (byte)(x*y);     \n" +
                "         ++y;                       \n" +
                "     }                              \n" +
                "                                    \n" +
                "     ++x;                           \n" +
                " }                                  \n" +
                "                                    \n" +
                " return b;                          \n");

        byte[][] b = (byte[][])value;

        for (byte x = 0; x < 5; ++x) {
            for (byte y = 0; y < 5; ++y) {
                assertEquals(x*y, b[x][y]);
            }
        }
    }

    public void testDoWhileStatement() throws Exception {
        assertEquals("aaaaaa", exec("string c = \"a\"; int x; do { c ..= \"a\"; ++x; } while (x < 5); return c;"));

        Object value = exec(
                " int[][] b = int.makearray(5, 5); \n" +
                " int x = 0, y;                    \n" +
                "                                  \n" +
                " do {                             \n" +
                "     y = 0;                       \n" +
                "                                  \n" +
                "     do {                         \n" +
                "         b[x][y] = x*y;           \n" +
                "         ++y;                     \n" +
                "     } while (y < 5);             \n" +
                "                                  \n" +
                "     ++x;                         \n" +
                " } while (x < 5);                 \n" +
                "                                  \n" +
                " return b;                        \n");

        int[][] b = (int[][])value;

        for (byte x = 0; x < 5; ++x) {
            for (byte y = 0; y < 5; ++y) {
                assertEquals(x*y, b[x][y]);
            }
        }
    }

    public void testForStatement() throws Exception {
        assertEquals("aaaaaa", exec("string c = \"a\"; for (int x = 0; x < 5; ++x) c ..= \"a\"; return c;"));

        Object value = exec(
                " int[][] b = int.makearray(5, 5);  \n" +
                " for (int x = 0; x < 5; ++x) {     \n" +
                "     for (int y = 0; y < 5; ++y) { \n" +
                "         b[x][y] = x*y;            \n" +
                "     }                             \n" +
                " }                                 \n" +
                "                                   \n" +
                " return b;                         \n");

        int[][] b = (int[][])value;

        for (byte x = 0; x < 5; ++x) {
            for (byte y = 0; y < 5; ++y) {
                assertEquals(x*y, b[x][y]);
            }
        }
    }
}
