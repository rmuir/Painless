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

public class StringTests extends ScriptTestCase {
    
    public void testAppend() {
        // boolean
        assertEquals("cat" + true, exec("string s = \"cat\"; return s .. true;"));
        // byte
        assertEquals("cat" + (byte)3, exec("string s = \"cat\"; return s .. (byte)3;"));
        // short
        assertEquals("cat" + (short)3, exec("string s = \"cat\"; return s .. (short)3;"));
        // char
        assertEquals("cat" + 't', exec("string s = \"cat\"; return s .. 't';"));
        assertEquals("cat" + (char)40, exec("string s = \"cat\"; return s .. (char)40;"));
        // int
        assertEquals("cat" + 2, exec("string s = \"cat\"; return s .. 2;"));
        // long
        assertEquals("cat" + 2L, exec("string s = \"cat\"; return s .. 2L;"));
        // float
        assertEquals("cat" + 2F, exec("string s = \"cat\"; return s .. 2F;"));
        // double
        assertEquals("cat" + 2.0, exec("string s = \"cat\"; return s .. 2.0;"));
        // string
        assertEquals("cat" + "cat", exec("string s = \"cat\"; return s .. s;"));
    }

    public void testStringAPI() {
        assertEquals("", exec("return string.new();"));
        assertEquals("test", exec("return string.snew(\"test\");"));
        assertEquals(new String(new char[] {'a', 'b'}), exec("char[] c = char.makearray(2); c[0] = 'a'; c[1] = 'b'; return string.canew(c);"));
        assertEquals('x', exec("return string.snew(\"x\").char(0);"));
        assertEquals(120, exec("return string.snew(\"x\").code(0);"));
        assertEquals(0, exec("return string.snew(\"x\").compare(\"x\");"));
        assertEquals("xx", exec("return string.snew(\"x\").concat(\"x\");"));
        assertEquals(true, exec("return string.snew(\"xy\").ends(\"y\");"));
        assertEquals(97, ((byte[])exec("return string.snew(\"a\").bytes();"))[0]);
        assertEquals(98, ((char[])exec("string t = \"abcde\"; char[] c = char.makearray(2); t.array(1, 2, c, 0); return c;"))[0]);
        assertEquals(2, exec("string t = \"abcde\"; return t.index('c', 0);"));
        assertEquals(2, exec("string t = \"abcde\"; return t.sindex(\"cd\", 1);"));
        assertEquals(false, exec("string t = \"abcde\"; return t.empty();"));
        assertEquals(5, exec("string t = \"abcde\"; return t.length();"));
        assertEquals(true, exec("string t = \"abcde\"; return t.matches(\"^[_a-zA-Z][_a-zA-Z0-9]*$\");"));
        assertEquals("bbcde", exec("string t = \"abcde\"; return t.creplace('a', 'b');"));
        assertEquals("cdcde", exec("string t = \"abcde\"; return t.replace(\"ab\", \"cd\");"));
        assertEquals(2, exec("string t = \"abcde\"; string[] s = t.split(\"c\"); return s.length;"));
        assertEquals(false, exec("return string.snew(\"xy\").starts(\"y\");"));
        assertEquals("e", exec("string t = \"abcde\"; return t.sub(4, 5);"));
        assertEquals(97, ((char[])exec("return string.snew(\"a\").chars();"))[0]);
        assertEquals("a", exec("return string.snew(\"A\").lower();"));
        assertEquals("A", exec("return string.snew(\"a\").upper();"));
        assertEquals("a", exec("return string.snew(\" a \").trim();"));
    }
}
