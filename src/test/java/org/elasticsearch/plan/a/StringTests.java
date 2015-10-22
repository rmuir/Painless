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
}
