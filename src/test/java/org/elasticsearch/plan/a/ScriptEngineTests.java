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

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

public class ScriptEngineTests extends ESTestCase {

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

    public void testSimpleEquation() {
        final Object value = testScript("testSimpleEquation", "return 1 + 2;", null);
        assertEquals(3, ((Number)value).intValue());
    }

    public void testMapAccess() {
        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> obj2 = new HashMap<>();
        obj2.put("prop2", "value2");
        Map<String, Object> obj1 = new HashMap<>();
        obj1.put("prop1", "value1");
        obj1.put("obj2", obj2);
        obj1.put("l", Arrays.asList("2", "1"));
        vars.put("obj1", obj1);

        Object value = testScript("testMapAccess", "return input.get(\"obj1\");", vars);
        assertThat(value, instanceOf(Map.class));
        obj1 = (Map<String, Object>)value;
        assertEquals("value1", obj1.get("prop1"));
        assertEquals("value2", ((Map<String, Object>) obj1.get("obj2")).get("prop2"));

        value = testScript("testMapAccess", "return ((list)((smap)input.get(\"obj1\")).get(\"l\")).get(0);", vars);
        assertEquals("2", value);
    }

    public void testAccessListInScript() {
        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> obj2 = new HashMap<>();
        obj2.put("prop2", "value2");
        Map<String, Object> obj1 = new HashMap<>();
        obj1.put("prop1", "value1");
        obj1.put("obj2", obj2);
        vars.put("l", Arrays.asList("1", "2", "3", obj1));

        Object value = testScript("testAccessInScript", "return ((list)input.get(\"l\")).size();", vars);
        assertThat(((Number)value).intValue(), equalTo(4));

        value = testScript("testAccessInScript", "return ((list)input.get(\"l\")).get(0);", vars);
        assertThat(value, equalTo("1"));

        value = testScript("testAccessInScript", "return ((list)input.get(\"l\")).get(3);", vars);
        obj1 = (Map<String, Object>)value;
        assertThat(obj1.get("prop1"), equalTo("value1"));
        assertThat(((Map<String, Object>)obj1.get("obj2")).get("prop2"), equalTo("value2"));

        value = testScript("testAccessInScript", "return ((smap)((list)input.get(\"l\")).get(3)).get(\"prop1\");", vars);
        assertThat(value, equalTo("value1"));
    }

    public void testChangingVarsCrossExecution1() {
        Map<String, Object> vars = new HashMap<>();
        Map<String, Object> ctx = new HashMap<>();
        vars.put("ctx", ctx);

        Object compiledScript = se.compile("return ((smap)input.get(\"ctx\")).get(\"value\");");
        ExecutableScript script = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE,
                "testChangingVarsCrossExecution1", "plan-a", compiledScript), vars);

        ctx.put("value", 1);
        Object o = script.run();
        assertEquals(1, ((Number) o).intValue());

        ctx.put("value", 2);
        o = script.run();
        assertEquals(2, ((Number) o).intValue());
    }

    public void testChangingVarsCrossExecution2() {
        Map<String, Object> vars = new HashMap<>();
        Object compiledScript = se.compile("return input.get(\"value\");");

        ExecutableScript script = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE,
                "testChangingVarsCrossExecution2", "plan-a", compiledScript), vars);

        script.setNextVar("value", 1);
        Object value = script.run();
        assertEquals(1, ((Number)value).intValue());

        script.setNextVar("value", 2);
        value = script.run();
        assertEquals(2, ((Number)value).intValue());
    }
}
