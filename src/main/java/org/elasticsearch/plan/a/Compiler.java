package org.elasticsearch.plan.a;

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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.elasticsearch.common.SuppressForbidden;

import static org.elasticsearch.plan.a.Default.*;
import static org.elasticsearch.plan.a.Definition.*;

@SuppressForbidden(reason = "some of this is for debugging")
final class Compiler {
    private static class Loader extends ClassLoader {
        Loader(ClassLoader parent) {
            super(parent);
        }

        public Class<? extends Executable> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length).asSubclass(Executable.class);
        }
    }

    static Executable compile(final String name, final String source,
                              final ClassLoader parent, final Properties properties) {
        long start = System.currentTimeMillis();

        final Definition definition = properties == null ? DEFAULT_DEFINITION : loadFromProperties(properties);
        final Standard standard = properties == null ? DEFAULT_STANDARD : new Standard(definition);
        final Caster caster = properties == null ? DEFAULT_CASTER : new Caster(definition, standard);

        long end = System.currentTimeMillis() - start;
        System.out.println("definition: " + end);
        start = System.currentTimeMillis();

        final ParseTree root = createParseTree(source, definition);

        end = System.currentTimeMillis() - start;
        System.out.println("tree: " + end);

        final Adapter adapter = new Adapter(definition, standard, caster, source, root);
        adapter.incrementScope();
        adapter.addVariable("this", adapter.standard.execType);
        adapter.addVariable("input", adapter.standard.smapType);

        start = System.currentTimeMillis();

        Analyzer.analyze(adapter);
        adapter.decrementScope();

        end = System.currentTimeMillis() - start;
        System.out.println("analyze: " + end);
        start = System.currentTimeMillis();

        final byte[] bytes = Writer.write(adapter);

        end = System.currentTimeMillis() - start;
        System.out.println("write: " + end);
        start = System.currentTimeMillis();

        final Executable executable = createExecutable(name, source, parent, bytes);

        end = System.currentTimeMillis() - start;
        System.out.println("create: " + end);

        return executable;
    }

    private static ParseTree createParseTree(String source, Definition definition) {
        final ANTLRInputStream stream = new ANTLRInputStream(source);
        final PlanALexer lexer = new PlanALexer(stream);
        final PlanAParser parser = new PlanAParser(new CommonTokenStream(lexer));

        parser.setTypes(definition.structs.keySet());

        ParseTree root = parser.source();
        System.out.println(root.toStringTree(parser));
        return root;
    }

    private static Executable createExecutable(String name, String source, ClassLoader parent, byte[] bytes) {
        try {
            // for debugging:
            // try {
            //    FileOutputStream f = new FileOutputStream(new File("/Users/jdconrad/lang/generated/out.class"), false);
            //    f.write(bytes);
            //    f.close();
            // } catch (Exception e) {
            //    throw new RuntimeException(e);
            // }

            final Loader loader = new Loader(parent);
            final Class<? extends Executable> clazz = loader.define(Writer.CLASS_NAME, bytes);
            final java.lang.reflect.Constructor<? extends Executable> constructor =
                    clazz.getConstructor(String.class, String.class);

            return constructor.newInstance(name, source);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private Compiler() {}
}
