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
        assertEquals("cat" + true, exec("String s = \"cat\"; return s .. true;"));
        // byte
        assertEquals("cat" + (byte)3, exec("String s = \"cat\"; return s .. (byte)3;"));
        // short
        assertEquals("cat" + (short)3, exec("String s = \"cat\"; return s .. (short)3;"));
        // char
        assertEquals("cat" + 't', exec("String s = \"cat\"; return s .. 't';"));
        assertEquals("cat" + (char)40, exec("String s = \"cat\"; return s .. (char)40;"));
        // int
        assertEquals("cat" + 2, exec("String s = \"cat\"; return s .. 2;"));
        // long
        assertEquals("cat" + 2L, exec("String s = \"cat\"; return s .. 2L;"));
        // float
        assertEquals("cat" + 2F, exec("String s = \"cat\"; return s .. 2F;"));
        // double
        assertEquals("cat" + 2.0, exec("String s = \"cat\"; return s .. 2.0;"));
        // String
        assertEquals("cat" + "cat", exec("String s = \"cat\"; return s .. s;"));
    }

    public void testStringAPI() {
        assertEquals("", exec("return String.new();"));
        assertEquals("test", exec("return String.fromString(\"test\");"));
        assertEquals('x', exec("return String.fromString(\"x\").charAt(0);"));
        assertEquals(120, exec("return String.fromString(\"x\").codePointAt(0);"));
        assertEquals(0, exec("return String.fromString(\"x\").compareTo(\"x\");"));
        assertEquals("xx", exec("return String.fromString(\"x\").concat(\"x\");"));
        assertEquals(true, exec("return String.fromString(\"xy\").endsWith(\"y\");"));
        assertEquals(2, exec("String t = \"abcde\"; return t.indexOf(\"cd\", 1);"));
        assertEquals(false, exec("String t = \"abcde\"; return t.isEmpty();"));
        assertEquals(5, exec("String t = \"abcde\"; return t.length();"));
        assertEquals("cdcde", exec("String t = \"abcde\"; return t.replace(\"ab\", \"cd\");"));
        assertEquals(false, exec("return String.fromString(\"xy\").startsWith(\"y\");"));
        assertEquals("e", exec("String t = \"abcde\"; return t.substring(4, 5);"));
        assertEquals(97, ((char[])exec("return String.fromString(\"a\").toCharArray();"))[0]);
        assertEquals("a", exec("return String.fromString(\" a \").trim();"));
    }
}
