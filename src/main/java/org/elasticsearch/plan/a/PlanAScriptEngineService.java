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

import org.apache.lucene.index.LeafReaderContext;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.LeafSearchScript;
import org.elasticsearch.script.ScriptEngineService;
import org.elasticsearch.script.SearchScript;
import org.elasticsearch.search.lookup.SearchLookup;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

public class PlanAScriptEngineService extends AbstractComponent implements ScriptEngineService {

    public static final String NAME = "plan-a";

    @Inject
    public PlanAScriptEngineService(Settings settings) {
        super(settings);
    }

    @Override
    public String[] types() {
        return new String[] { NAME };
    }

    @Override
    public String[] extensions() {
        return new String[] { NAME };
    }

    @Override
    public boolean sandboxed() {
        return true;
    }

    @Override
    public Object compile(String script) {
        // classloader created here
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new SpecialPermission());
        }
        return AccessController.doPrivileged(new PrivilegedAction<Executable>() {
            @Override
            public Executable run() {
                // NOTE: validation is delayed to allow runtime vars, and we don't have access to per index stuff here
                return Compiler.compile("something", script, getClass().getClassLoader(), null);
            }
        });
    }

    @Override
    public ExecutableScript executable(CompiledScript compiledScript, Map<String,Object> vars) {
        return new ScriptImpl((Executable) compiledScript.compiled(), vars, null);
    }

    @Override
    public SearchScript search(CompiledScript compiledScript, SearchLookup lookup, Map<String,Object> vars) {
        return new SearchScript() {
            @Override
            public LeafSearchScript getLeafSearchScript(LeafReaderContext context) throws IOException {
                return new ScriptImpl((Executable) compiledScript.compiled(), vars, lookup.getLeafSearchLookup(context));
            }

            @Override
            public boolean needsScores() {
                return true; // TODO: maybe even do these different and more like expressions.
            }
        };
    }

    @Override
    public void scriptRemoved(CompiledScript script) {
        // nothing to do
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }
}
