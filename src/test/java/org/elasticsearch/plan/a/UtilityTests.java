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

import org.elasticsearch.test.ESTestCase;

/**
 * Tests utility methods (typically built-ins)
 */
public class UtilityTests extends ESTestCase {
    
    public void testDivideWithoutOverflowInt() {
        assertEquals(5 / 2, Utility.divideWithoutOverflow(5, 2));

        try {
            Utility.divideWithoutOverflow(Integer.MIN_VALUE, -1);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
        
        try {
            Utility.divideWithoutOverflow(5, 0);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
    }
    
    public void testDivideWithoutOverflowLong() {
        assertEquals(5L / 2L, Utility.divideWithoutOverflow(5L, 2L));
        
        try {
            Utility.divideWithoutOverflow(Long.MIN_VALUE, -1L);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
        
        try {
            Utility.divideWithoutOverflow(5L, 0L);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
    }
    
    public void testToByteExact() {
        for (int b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
            assertEquals((byte)b, Utility.toByteExact(b));
        }
        
        try {
            Utility.toByteExact(Byte.MIN_VALUE - 1);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
        
        try {
            Utility.toByteExact(Byte.MAX_VALUE + 1);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
    }
    
    public void testToShortExact() {
        for (int s = Short.MIN_VALUE; s < Short.MAX_VALUE; s++) {
            assertEquals((short)s, Utility.toShortExact(s));
        }
        
        try {
            Utility.toShortExact(Short.MIN_VALUE - 1);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
        
        try {
            Utility.toShortExact(Short.MAX_VALUE + 1);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
    }
    
    public void testToCharExact() {
        for (int c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            assertEquals((char)c, Utility.toCharExact(c));
        }
        
        try {
            Utility.toCharExact(Character.MIN_VALUE - 1);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
        
        try {
            Utility.toCharExact(Character.MAX_VALUE + 1);
            fail("did not get expected exception");
        } catch (ArithmeticException expected) {}
    }
}
