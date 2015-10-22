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

/** Tests for addition operator across all types */
//TODO: NaN/Inf/overflow/...
public class AdditionTests extends ScriptTestCase {
    
    public void testAddInt() throws Exception {
        assertEquals(1+1, exec("int x = 1; int y = 1; return x+y;"));
        assertEquals(1+2, exec("int x = 1; int y = 2; return x+y;"));
        assertEquals(5+10, exec("int x = 5; int y = 10; return x+y;"));
        assertEquals(1+1+2, exec("int x = 1; int y = 1; int z = 2; return x+y+z;"));
        assertEquals((1+1)+2, exec("int x = 1; int y = 1; int z = 2; return (x+y)+z;"));
        assertEquals(1+(1+2), exec("int x = 1; int y = 1; int z = 2; return x+(y+z);"));
        assertEquals(0+1, exec("int x = 0; int y = 1; return x+y;"));
        assertEquals(1+0, exec("int x = 1; int y = 0; return x+y;"));
        assertEquals(0+0, exec("int x = 0; int y = 0; return x+y;"));
        assertEquals(0+0, exec("int x = 0; int y = 0; return x+y;"));
    }
    
    public void testAddIntConst() throws Exception {
        assertEquals(1+1, exec("return 1+1;"));
        assertEquals(1+2, exec("return 1+2;"));
        assertEquals(5+10, exec("return 5+10;"));
        assertEquals(1+1+2, exec("return 1+1+2;"));
        assertEquals((1+1)+2, exec("return (1+1)+2;"));
        assertEquals(1+(1+2), exec("return 1+(1+2);"));
        assertEquals(0+1, exec("return 0+1;"));
        assertEquals(1+0, exec("return 1+0;"));
        assertEquals(0+0, exec("return 0+0;"));
    }
    
    public void testAddByte() throws Exception {
        assertEquals((byte)1+(byte)1, exec("byte x = 1; byte y = 1; return x+y;"));
        assertEquals((byte)1+(byte)2, exec("byte x = 1; byte y = 2; return x+y;"));
        assertEquals((byte)5+(byte)10, exec("byte x = 5; byte y = 10; return x+y;"));
        assertEquals((byte)1+(byte)1+(byte)2, exec("byte x = 1; byte y = 1; byte z = 2; return x+y+z;"));
        assertEquals(((byte)1+(byte)1)+(byte)2, exec("byte x = 1; byte y = 1; byte z = 2; return (x+y)+z;"));
        assertEquals((byte)1+((byte)1+(byte)2), exec("byte x = 1; byte y = 1; byte z = 2; return x+(y+z);"));
        assertEquals((byte)0+(byte)1, exec("byte x = 0; byte y = 1; return x+y;"));
        assertEquals((byte)1+(byte)0, exec("byte x = 1; byte y = 0; return x+y;"));
        assertEquals((byte)0+(byte)0, exec("byte x = 0; byte y = 0; return x+y;"));
    }
    
    public void testAddChar() throws Exception {
        assertEquals((char)1+(char)1, exec("char x = 1; char y = 1; return x+y;"));
        assertEquals((char)1+(char)2, exec("char x = 1; char y = 2; return x+y;"));
        assertEquals((char)5+(char)10, exec("char x = 5; char y = 10; return x+y;"));
        assertEquals((char)1+(char)1+(char)2, exec("char x = 1; char y = 1; char z = 2; return x+y+z;"));
        assertEquals(((char)1+(char)1)+(char)2, exec("char x = 1; char y = 1; char z = 2; return (x+y)+z;"));
        assertEquals((char)1+((char)1+(char)2), exec("char x = 1; char y = 1; char z = 2; return x+(y+z);"));
        assertEquals((char)0+(char)1, exec("char x = 0; char y = 1; return x+y;"));
        assertEquals((char)1+(char)0, exec("char x = 1; char y = 0; return x+y;"));
        assertEquals((char)0+(char)0, exec("char x = 0; char y = 0; return x+y;"));
    }
    
    public void testAddShort() throws Exception {
        assertEquals((short)1+(short)1, exec("short x = 1; short y = 1; return x+y;"));
        assertEquals((short)1+(short)2, exec("short x = 1; short y = 2; return x+y;"));
        assertEquals((short)5+(short)10, exec("short x = 5; short y = 10; return x+y;"));
        assertEquals((short)1+(short)1+(short)2, exec("short x = 1; short y = 1; short z = 2; return x+y+z;"));
        assertEquals(((short)1+(short)1)+(short)2, exec("short x = 1; short y = 1; short z = 2; return (x+y)+z;"));
        assertEquals((short)1+((short)1+(short)2), exec("short x = 1; short y = 1; short z = 2; return x+(y+z);"));
        assertEquals((short)0+(short)1, exec("short x = 0; short y = 1; return x+y;"));
        assertEquals((short)1+(short)0, exec("short x = 1; short y = 0; return x+y;"));
        assertEquals((short)0+(short)0, exec("short x = 0; short y = 0; return x+y;"));
    }
    
    public void testAddLong() throws Exception {
        assertEquals(1L+1L, exec("long x = 1; long y = 1; return x+y;"));
        assertEquals(1L+2L, exec("long x = 1; long y = 2; return x+y;"));
        assertEquals(5L+10L, exec("long x = 5; long y = 10; return x+y;"));
        assertEquals(1L+1L+2L, exec("long x = 1; long y = 1; long z = 2; return x+y+z;"));
        assertEquals((1L+1L)+2L, exec("long x = 1; long y = 1; long z = 2; return (x+y)+z;"));
        assertEquals(1L+(1L+2L), exec("long x = 1; long y = 1; long z = 2; return x+(y+z);"));
        assertEquals(0L+1L, exec("long x = 0; long y = 1; return x+y;"));
        assertEquals(1L+0L, exec("long x = 1; long y = 0; return x+y;"));
        assertEquals(0L+0L, exec("long x = 0; long y = 0; return x+y;"));
    }
    
    public void testAddLongConst() throws Exception {
        assertEquals(1L+1L, exec("return 1L+1L;"));
        assertEquals(1L+2L, exec("return 1L+2L;"));
        assertEquals(5L+10L, exec("return 5L+10L;"));
        assertEquals(1L+1L+2L, exec("return 1L+1L+2L;"));
        assertEquals((1L+1L)+2L, exec("return (1L+1L)+2L;"));
        assertEquals(1L+(1L+2L), exec("return 1L+(1L+2L);"));
        assertEquals(0L+1L, exec("return 0L+1L;"));
        assertEquals(1L+0L, exec("return 1L+0L;"));
        assertEquals(0L+0L, exec("return 0L+0L;"));
    }

    public void testAddFloat() throws Exception {
        assertEquals(1F+1F, exec("float x = 1F; float y = 1F; return x+y;"));
        assertEquals(1F+2F, exec("float x = 1F; float y = 2F; return x+y;"));
        assertEquals(5F+10F, exec("float x = 5F; float y = 10F; return x+y;"));
        assertEquals(1F+1F+2F, exec("float x = 1F; float y = 1F; float z = 2F; return x+y+z;"));
        assertEquals((1F+1F)+2F, exec("float x = 1F; float y = 1F; float z = 2F; return (x+y)+z;"));
        assertEquals((1F+1F)+2F, exec("float x = 1F; float y = 1F; float z = 2F; return x+(y+z);"));
        assertEquals(0F+1F, exec("float x = 0F; float y = 1F; return x+y;"));
        assertEquals(1F+0F, exec("float x = 1F; float y = 0F; return x+y;"));
        assertEquals(0F+0F, exec("float x = 0F; float y = 0F; return x+y;"));
    }

    public void testAddFloatConst() throws Exception {
        assertEquals(1F+1F, exec("return 1F+1F;"));
        assertEquals(1F+2F, exec("return 1F+2F;"));
        assertEquals(5F+10F, exec("return 5F+10F;"));
        assertEquals(1F+1F+2F, exec("return 1F+1F+2F;"));
        assertEquals((1F+1F)+2F, exec("return (1F+1F)+2F;"));
        assertEquals(1F+(1F+2F), exec("return 1F+(1F+2F);"));
        assertEquals(0F+1F, exec("return 0F+1F;"));
        assertEquals(1F+0F, exec("return 1F+0F;"));
        assertEquals(0F+0F, exec("return 0F+0F;"));
    }

    public void testAddDouble() throws Exception {
        assertEquals(1.0+1.0, exec("double x = 1.0; double y = 1.0; return x+y;"));
        assertEquals(1.0+2.0, exec("double x = 1.0; double y = 2.0; return x+y;"));
        assertEquals(5.0+10.0, exec("double x = 5.0; double y = 10.0; return x+y;"));
        assertEquals(1.0+1.0+2.0, exec("double x = 1.0; double y = 1.0; double z = 2.0; return x+y+z;"));
        assertEquals((1.0+1.0)+2.0, exec("double x = 1.0; double y = 1.0; double z = 2.0; return (x+y)+z;"));
        assertEquals(1.0+(1.0+2.0), exec("double x = 1.0; double y = 1.0; double z = 2.0; return x+(y+z);"));
        assertEquals(0.0+1.0, exec("double x = 0.0; double y = 1.0; return x+y;"));
        assertEquals(1.0+0.0, exec("double x = 1.0; double y = 0.0; return x+y;"));
        assertEquals(0.0+0.0, exec("double x = 0.0; double y = 0.0; return x+y;"));
    }
    
    public void testAddDoubleConst() throws Exception {
        assertEquals(1.0+1.0, exec("return 1.0+1.0;"));
        assertEquals(1.0+2.0, exec("return 1.0+2.0;"));
        assertEquals(5.0+10.0, exec("return 5.0+10.0;"));
        assertEquals(1.0+1.0+2.0, exec("return 1.0+1.0+2.0;"));
        assertEquals((1.0+1.0)+2.0, exec("return (1.0+1.0)+2.0;"));
        assertEquals(1.0+(1.0+2.0), exec("return 1.0+(1.0+2.0);"));
        assertEquals(0.0+1.0, exec("return 0.0+1.0;"));
        assertEquals(1.0+0.0, exec("return 1.0+0.0;"));
        assertEquals(0.0+0.0, exec("return 0.0+0.0;"));
    }
    
    public void testAddPromotion() throws Exception {
        assertEquals(1+0.5+0.5, exec("return 1+0.5+0.5;"));
    }
    
    public void testOverflow() throws Exception {
        assertEquals(Integer.MAX_VALUE + Integer.MAX_VALUE, exec("int x = 2147483647; int y = 2147483647; return x + y;"));
        assertEquals(Long.MAX_VALUE + Long.MAX_VALUE, exec("long x = 9223372036854775807L; long y = 9223372036854775807L; return x + y;"));
    }
    
    public void testOverflowConst() throws Exception {
        assertEquals(Integer.MAX_VALUE + Integer.MAX_VALUE, exec("return 2147483647 + 2147483647;"));
        assertEquals(Long.MAX_VALUE + Long.MAX_VALUE, exec("return 9223372036854775807L + 9223372036854775807L;"));
    }
}