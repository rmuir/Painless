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

import java.util.List;
import java.util.Map;

import static org.elasticsearch.plan.a.Definition.*;

public class Default {
    static class Standard {
        final Type voidType;
        final Type boolType;
        final Type byteType;
        final Type shortType;
        final Type charType;
        final Type intType;
        final Type longType;
        final Type floatType;
        final Type doubleType;
        final Type objectType;
        final Type stringType;
        final Type execType;
        final Type listType;
        final Type mapType;
        final Type smapType;

        Standard(final Definition definition) {
            validateExact(definition, "void", void.class);
            validateExact(definition, "boolean", boolean.class);
            validateExact(definition, "byte", byte.class);
            validateExact(definition, "short", short.class);
            validateExact(definition, "char", char.class);
            validateExact(definition, "int", int.class);
            validateExact(definition, "long", long.class);
            validateExact(definition, "float", float.class);
            validateExact(definition, "double", double.class);
            validateExact(definition, "Object", Object.class);
            validateExact(definition, "String", String.class);
            validateSubclass(definition, "Exectuable", Executable.class);
            validateSubclass(definition, "List", List.class);
            validateSubclass(definition, "Map", Map.class);
            validateSubclass(definition, "StringMap", Map.class);

            voidType = getTypeFromCanonicalName(definition, "void");
            boolType = getTypeFromCanonicalName(definition, "boolean");
            byteType = getTypeFromCanonicalName(definition, "byte");
            shortType = getTypeFromCanonicalName(definition, "short");
            charType = getTypeFromCanonicalName(definition, "char");
            intType = getTypeFromCanonicalName(definition, "int");
            longType = getTypeFromCanonicalName(definition, "long");
            floatType = getTypeFromCanonicalName(definition, "float");
            doubleType = getTypeFromCanonicalName(definition, "double");
            objectType = getTypeFromCanonicalName(definition, "object");
            stringType = getTypeFromCanonicalName(definition, "string");
            execType = getTypeFromCanonicalName(definition, "exec");
            listType = getTypeFromCanonicalName(definition, "list");
            mapType = getTypeFromCanonicalName(definition, "map");
            smapType = getTypeFromCanonicalName(definition, "smap");
        }

        private void validateExact(final Definition definition, final String name, final Class clazz) {
            final Definition.Struct struct = definition.structs.get(name);

            if (struct == null || !clazz.equals(struct.clazz)) {
                throw new IllegalArgumentException("Missing required definition of" +
                        " struct [" + name + "] with class type of [" + clazz.getCanonicalName() + "]");
            }
        }

        private void validateSubclass(final Definition definition, final String name, final Class clazz) {
            final Definition.Struct struct = definition.structs.get(name);

            if (struct == null) {
                throw new IllegalArgumentException("Missing required definition of" +
                        " struct [" + name + "] with super class type of [" + clazz.getCanonicalName() + "]");
            }

            try {
                struct.clazz.asSubclass(clazz);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException("Missing required definition of" +
                        " struct [" + name + "] with super class type of [" + clazz.getCanonicalName() + "]");
            }
        }
    }

    final static Definition DEFAULT_DEFINITION;
    final static Standard DEFAULT_STANDARD;
    final static Caster DEFAULT_CASTER;

    static {
        DEFAULT_DEFINITION = loadFromProperties();
        DEFAULT_STANDARD = new Standard(DEFAULT_DEFINITION);
        DEFAULT_CASTER = new Caster(DEFAULT_DEFINITION, DEFAULT_STANDARD);
    }

    private Default() {}
}
