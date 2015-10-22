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

public class DivisionTests extends ScriptTestCase {

    public void testDivideByZero() throws Exception {
        try {
            exec("int x = 1; int y = 0; return x / y;");
            fail("should have hit exception");
        } catch (ArithmeticException expected) {
            // divide by zero
        }
        
        try {
            exec("long x = 1L; long y = 0L; return x / y;");
            fail("should have hit exception");
        } catch (ArithmeticException expected) {
            // divide by zero
        }
    }
    
    public void testDivideByZeroConst() throws Exception {
        try {
            exec("return 1/0;");
            fail("should have hit exception");
        } catch (ArithmeticException expected) {
            // divide by zero
        }
        
        try {
            exec("return 1L/0L;");
            fail("should have hit exception");
        } catch (ArithmeticException expected) {
            // divide by zero
        }
    }
}
