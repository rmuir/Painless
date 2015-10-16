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

import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

/**
 *
 */
public class PlanAScriptEngineTests extends ESTestCase {

    private PlanAScriptEngineService se;

    @Before
    public void setup() {
        se = new PlanAScriptEngineService(Settings.Builder.EMPTY_SETTINGS);
    }

    public void testSimpleEquation() {
        Map<String, Object> vars = new HashMap<String, Object>();
        Object o = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testSimpleEquation", "plan-a", se.compile("return 1 + 2;")), vars).run();
        assertEquals(3, ((Number) o).intValue());
    }

    public void testMapAccess() {
        Map<String, Object> vars = new HashMap<String, Object>();

        Map<String, Object> obj2 = MapBuilder.<String, Object>newMapBuilder().put("prop2", "value2").map();
        Map<String, Object> obj1 = MapBuilder.<String, Object>newMapBuilder().put("prop1", "value1").put("obj2", obj2).put("l", Arrays.asList("2", "1")).map();
        vars.put("obj1", obj1);
        Object o = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testMapAccess", "plan-a", se.compile("return input[\"obj1\"];")), vars).run();
        assertThat(o, instanceOf(Map.class));
        obj1 = (Map<String, Object>) o;
        assertEquals("value1", obj1.get("prop1"));
        assertEquals("value2", ((Map<String, Object>) obj1.get("obj2")).get("prop2"));

        o = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testMapAccess", "plan-a", se.compile("return input[\"obj1\"][\"l\"][0];")), vars).run();
        assertEquals("2", o);
    }

    @AwaitsFix(bugUrl = "jack look at these")
    public void testAccessListInScript() {
        Map<String, Object> vars = new HashMap<String, Object>();
        Map<String, Object> obj2 = MapBuilder.<String, Object>newMapBuilder().put("prop2", "value2").map();
        Map<String, Object> obj1 = MapBuilder.<String, Object>newMapBuilder().put("prop1", "value1").put("obj2", obj2).map();
        vars.put("l", Arrays.asList("1", "2", "3", obj1));

        Object o = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testAccessInScript", "plan-a",
                se.compile("return ((list)input[\"l\"]).size();")), vars).run();
        assertThat(((Number) o).intValue(), equalTo(4));

        o = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testAccessInScript", "plan-a",
                se.compile("return input[\"l\"][0];")), vars).run();
        assertThat(((String) o), equalTo("1"));

        o = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testAccessInScript", "plan-a",
                se.compile("return input[\"l\"][3];")), vars).run();
        obj1 = (Map<String, Object>) o;
        assertThat((String) obj1.get("prop1"), equalTo("value1"));
        assertThat((String) ((Map<String, Object>) obj1.get("obj2")).get("prop2"), equalTo("value2"));

        o = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testAccessInScript", "plan-a",
                se.compile("return input[\"l\"][3][\"prop1\"];")), vars).run();
        assertThat(((String) o), equalTo("value1"));
    }

    public void testChangingVarsCrossExecution1() {
        Map<String, Object> vars = new HashMap<String, Object>();
        Map<String, Object> ctx = new HashMap<String, Object>();
        vars.put("ctx", ctx);
        Object compiledScript = se.compile("return input[\"ctx\"][\"value\"];");

        ExecutableScript script = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testChangingVarsCrossExecution1", "plan-a",
                compiledScript), vars);
        ctx.put("value", 1);
        Object o = script.run();
        assertEquals(1, ((Number) o).intValue());

        ctx.put("value", 2);
        o = script.run();
        assertEquals(2, ((Number) o).intValue());
    }

    public void testChangingVarsCrossExecution2() {
        Map<String, Object> vars = new HashMap<String, Object>();
        Object compiledScript = se.compile("return input[\"value\"];");

        ExecutableScript script = se.executable(new CompiledScript(ScriptService.ScriptType.INLINE, "testChangingVarsCrossExecution2", "plan-a",
                compiledScript), vars);
        script.setNextVar("value", 1);
        Object o = script.run();
        assertEquals(1, ((Number) o).intValue());

        script.setNextVar("value", 2);
        o = script.run();
        assertEquals(2, ((Number) o).intValue());
    }
}
