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
 * Tests binary operators across different types
 */
// TODO: NaN/Inf/overflow/...
public class BinaryOperatorTests extends ScriptTestCase {
    
    // TODO: move to per-type tests and test for each type
    public void testBasics() {
        assertEquals(2.25F / 1.5F, exec("return 2.25F / 1.5F;"));
        assertEquals(2.25F % 1.5F, exec("return 2.25F % 1.5F;"));
        assertEquals(2 - 1, exec("return 2 - 1;"));
        assertEquals(1 << 2, exec("return 1 << 2;"));
        assertEquals(4 >> 2, exec("return 4 >> 2;"));
        assertEquals(-1 >>> 29, exec("return -1 >>> 29;"));
        assertEquals(5 & 3, exec("return 5 & 3;"));
        assertEquals(5 & 3L, exec("return 5 & 3L;"));
        assertEquals(5L & 3, exec("return 5L & 3;"));
        assertEquals(5 | 3, exec("return 5 | 3;"));
        assertEquals(5L | 3, exec("return 5L | 3;"));
        assertEquals(5 | 3L, exec("return 5 | 3L;"));
        assertEquals(9 ^ 3, exec("return 9 ^ 3;"));
        assertEquals(9L ^ 3, exec("return 9L ^ 3;"));
        assertEquals(9 ^ 3L, exec("return 9 ^ 3L;"));
    }
    
    public void testLongShifts() {
        // note: we always promote the results of shifts too (unlike java)
        assertEquals(1L << 2, exec("return 1L << 2;"));
        assertEquals(1L << 2L, exec("return 1 << 2L;"));
        assertEquals(4L >> 2L, exec("return 4 >> 2L;"));
        assertEquals(4L >> 2, exec("return 4L >> 2;"));
        assertEquals(-1L >>> 29, exec("return -1L >>> 29;"));
        assertEquals(-1L >>> 29L, exec("return -1 >>> 29L;"));
    }
    
    public void testMixedTypes() {
        assertEquals(8, exec("int x = 4; char y = 2; return x*y;"));
        assertEquals(0.5, exec("double x = 1; float y = 2; return x / y;"));
        assertEquals(1, exec("int x = 3; int y = 2; return x % y;"));
        assertEquals(3.0, exec("double x = 1; byte y = 2; return x + y;"));
        assertEquals(-1, exec("int x = 1; char y = 2; return x - y;"));
        assertEquals(4, exec("int x = 1; char y = 2; return x << y;"));
        assertEquals(-1, exec("int x = -1; char y = 29; return x >> y;"));
        assertEquals(3, exec("int x = -1; char y = 30; return x >>> y;"));
        assertEquals(1L, exec("int x = 5; long y = 3; return x & y;"));
        assertEquals(7, exec("short x = 5; byte y = 3; return x | y;"));
        assertEquals(10, exec("short x = 9; char y = 3; return x ^ y;"));
    }
    
    public void testAddInt() throws Exception {
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
    
    public void testAddLong() throws Exception {
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
    
    public void testMultiplyInt() throws Exception {
        assertEquals(1*1, exec("return 1*1;"));
        assertEquals(2*3, exec("return 2*3;"));
        assertEquals(5*10, exec("return 5*10;"));
        assertEquals(1*1*2, exec("return 1*1*2;"));
        assertEquals((1*1)*2, exec("return (1*1)*2;"));
        assertEquals(1*(1*2), exec("return 1*(1*2);"));
        assertEquals(10*0, exec("return 10*0;"));
        assertEquals(0*0, exec("return 0*0;"));
    }
    
    public void testMultiplyLong() throws Exception {
        assertEquals(1L*1L, exec("return 1L*1L;"));
        assertEquals(2L*3L, exec("return 2L*3L;"));
        assertEquals(5L*10L, exec("return 5L*10L;"));
        assertEquals(1L*1L*2L, exec("return 1L*1L*2L;"));
        assertEquals((1L*1L)*2L, exec("return (1L*1L)*2L;"));
        assertEquals(1L*(1L*2L), exec("return 1L*(1L*2L);"));
        assertEquals(10L*0L, exec("return 10L*0L;"));
        assertEquals(0L*0L, exec("return 0L*0L;"));
    }
    
    public void testMultiplyFloat() throws Exception {
        assertEquals(1F*1F, exec("return 1F*1F;"));
        assertEquals(2F*3F, exec("return 2F*3F;"));
        assertEquals(5F*10F, exec("return 5F*10F;"));
        assertEquals(1F*1F*2F, exec("return 1F*1F*2F;"));
        assertEquals((1F*1F)*2F, exec("return (1F*1F)*2F;"));
        assertEquals(1F*(1F*2F), exec("return 1F*(1F*2F);"));
        assertEquals(10F*0F, exec("return 10F*0F;"));
        assertEquals(0F*0F, exec("return 0F*0F;"));
    }
    
    public void testMultiplyDouble() throws Exception {
        assertEquals(1.0*1.0, exec("return 1.0*1.0;"));
        assertEquals(2.0*3.0, exec("return 2.0*3.0;"));
        assertEquals(5.0*10.0, exec("return 5.0*10.0;"));
        assertEquals(1.0*1.0*2.0, exec("return 1.0*1.0*2.0;"));
        assertEquals((1.0*1.0)*2.0, exec("return (1.0*1.0)*2.0;"));
        assertEquals(1.0*(1.0*2.0), exec("return 1.0*(1.0*2.0);"));
        assertEquals(10.0*0.0, exec("return 10.0*0.0;"));
        assertEquals(0.0*0.0, exec("return 0.0*0.0;"));
    }
    
    public void testMultiplyPromotion() throws Exception {
        assertEquals(50*0.1, exec("return 50*0.1;"));
    }
}
