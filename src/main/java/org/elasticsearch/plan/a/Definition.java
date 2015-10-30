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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Definition {
    enum Sort {
        VOID(       void.class      , 0 , true  , false , false , false ),
        BOOL(       boolean.class   , 1 , true  , false , true  , false ),
        BYTE(       byte.class      , 1 , true  , true  , true  , false ),
        SHORT(      short.class     , 1 , true  , true  , true  , false ),
        CHAR(       char.class      , 1 , true  , true  , true  , false ),
        INT(        int.class       , 1 , true  , true  , true  , false ),
        LONG(       long.class      , 2 , true  , true  , true  , false ),
        FLOAT(      float.class     , 1 , true  , true  , true  , false ),
        DOUBLE(     double.class    , 2 , true  , true  , true  , false ),

        VOID_OBJ(   Void.class      , 1 , true  , false , false , false ),
        BOOL_OBJ(   Boolean.class   , 1 , false , false , false , true  ),
        BYTE_OBJ(   Byte.class      , 1 , false , true  , false , true  ),
        SHORT_OBJ(  Short.class     , 1 , false , true  , false , true  ),
        CHAR_OBJ(   Character.class , 1 , false , true  , false , true  ),
        INT_OBJ(    Integer.class   , 1 , false , true  , false , true  ),
        LONG_OBJ(   Long.class      , 1 , false , true  , false , true  ),
        FLOAT_OBJ(  Float.class     , 1 , false , true  , false , true  ),
        DOUBLE_OBJ( Double.class    , 1 , false , true  , false , true  ),

        NUMBER(     Number.class    , 1 , false , true  , false , true  ),
        STRING(     String.class    , 1 , false , false , true  , true  ),

        OBJECT(     null            , 1 , false , false , false , true  ),
        GENERIC(    null            , 1 , false , false , false , true  ),
        ARRAY(      null            , 1 , false , false , false , true  );

        final Class<?> clazz;
        final int size;
        final boolean primitive;
        final boolean numeric;
        final boolean constant;
        final boolean object;

        Sort(final Class<?> clazz, final int size, final boolean primitive,
             final boolean numeric, final boolean constant, final boolean object) {
            this.clazz = clazz;
            this.size = size;
            this.primitive = primitive;
            this.numeric = numeric;
            this.constant = constant;
            this.object = object;
        }
    }

    static class Type {
        final String name;
        final Struct struct;
        final Class<?> clazz;
        final org.objectweb.asm.Type type;
        final Sort sort;

        private Type(final String name, final Struct struct, final Class<?> clazz,
                     final org.objectweb.asm.Type type, final Sort sort) {
            this.name = name;
            this.struct = struct;
            this.clazz = clazz;
            this.type = type;
            this.sort = sort;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final Type type = (Type)object;

            return this.type.equals(type.type) && struct.equals(type.struct);
        }

        @Override
        public int hashCode() {
            int result = struct.hashCode();
            result = 31 * result + type.hashCode();

            return result;
        }
    }

    static class Constructor {
        final String name;
        final Struct owner;
        final List<Type> arguments;
        final org.objectweb.asm.commons.Method method;
        final java.lang.reflect.Constructor<?> reflect;

        private Constructor(final String name, final Struct owner, final List<Type> arguments,
                            final org.objectweb.asm.commons.Method method, final java.lang.reflect.Constructor<?> reflect) {
            this.name = name;
            this.owner = owner;
            this.arguments = Collections.unmodifiableList(arguments);
            this.method = method;
            this.reflect = reflect;
        }
    }

    static class Method {
        final String name;
        final Struct owner;
        final Type rtn;
        final List<Type> arguments;
        final org.objectweb.asm.commons.Method method;
        final java.lang.reflect.Method reflect;

        private Method(final String name, final Struct owner, final Type rtn, final List<Type> arguments,
               final org.objectweb.asm.commons.Method method, final java.lang.reflect.Method reflect) {
            this.name = name;
            this.owner = owner;
            this.rtn = rtn;
            this.arguments = Collections.unmodifiableList(arguments);
            this.method = method;
            this.reflect = reflect;
        }
    }

    static class Field {
        final String name;
        final Struct owner;
        final Type type;
        final java.lang.reflect.Field reflect;

        private Field(final String name, final Struct owner, final Type type, final java.lang.reflect.Field reflect) {
            this.name = name;
            this.owner = owner;
            this.type = type;
            this.reflect = reflect;
        }
    }

    static class Struct {
        final String name;
        final Class<?> clazz;
        final org.objectweb.asm.Type type;
        final boolean generic;

        final Map<String, Constructor> constructors;
        final Map<String, Method> functions;
        final Map<String, Method> methods;

        final Map<String, Field> statics;
        final Map<String, Field> members;

        private Struct(final String name, final Class<?> clazz, final org.objectweb.asm.Type type, final boolean generic) {
            this.name = name;
            this.clazz = clazz;
            this.type = type;
            this.generic = generic;

            constructors = new HashMap<>();
            functions = new HashMap<>();
            methods = new HashMap<>();

            statics = new HashMap<>();
            members = new HashMap<>();
        }

        private Struct(final Struct struct) {
            name = struct.name;
            clazz = struct.clazz;
            type = struct.type;
            generic = struct.generic;

            constructors = Collections.unmodifiableMap(struct.constructors);
            functions = Collections.unmodifiableMap(struct.functions);
            methods = Collections.unmodifiableMap(struct.methods);

            statics = Collections.unmodifiableMap(struct.statics);
            members = Collections.unmodifiableMap(struct.members);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            Struct struct = (Struct)object;

            return name.equals(struct.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    static class Cast {
        final Type from;
        final Type to;

        Cast(final Type from, final Type to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final Cast cast = (Cast)object;

            return from.equals(cast.from) && to.equals(cast.to);
        }

        @Override
        public int hashCode() {
            int result = from.hashCode();
            result = 31 * result + to.hashCode();

            return result;
        }
    }

    static class Transform extends Cast {
        final Cast cast;
        final Method method;
        final Type upcast;
        final Type downcast;

        private Transform(final Cast cast, Method method, final Type upcast, final Type downcast) {
            super(cast.from, cast.to);

            this.cast = cast;
            this.method = method;
            this.upcast = upcast;
            this.downcast = downcast;
        }
    }

    final Map<String, Struct> structs;
    final Map<Cast, Transform> transforms;

    final Type voidType;
    final Type booleanType;
    final Type byteType;
    final Type shortType;
    final Type charType;
    final Type intType;
    final Type longType;
    final Type floatType;
    final Type doubleType;

    final Type voidobjType;
    final Type booleanobjType;
    final Type byteobjType;
    final Type shortobjType;
    final Type charobjType;
    final Type intobjType;
    final Type longobjType;
    final Type floatobjType;
    final Type doubleobjType;

    final Type objectType;
    final Type numberType;
    final Type charseqType;
    final Type stringType;
    final Type utilityType;

    final Type listType;
    final Type arraylistType;
    final Type mapType;
    final Type hashmapType;
    final Type smapType;

    final Type execType;

    public Definition() {
        structs = new HashMap<>();
        transforms = new HashMap<>();

        addDefaultStructs();

        voidType = getType("void");
        booleanType = getType("boolean");
        byteType = getType("byte");
        shortType = getType("short");
        charType = getType("char");
        intType = getType("int");
        longType = getType("long");
        floatType = getType("float");
        doubleType = getType("double");

        voidobjType = getType("Void");
        booleanobjType = getType("Boolean");
        byteobjType = getType("Byte");
        shortobjType = getType("Short");
        charobjType = getType("Character");
        intobjType = getType("Integer");
        longobjType = getType("Long");
        floatobjType = getType("Float");
        doubleobjType = getType("Double");

        objectType = getType("Object");
        numberType = getType("Number");
        charseqType = getType("CharSequence");
        stringType = getType("String");
        utilityType = getType("Utility");

        listType = getType("List");
        arraylistType = getType("ArrayList");
        mapType = getType("Map");
        hashmapType = getType("HashMap");
        smapType = getType("Map<String,Object>");

        execType = getType("Executable");

        addDefaultElements();
        copyDefaultStructs();
        addDefaultTransforms();
    }

    Definition(final Definition definition) {
        final Map<String, Struct> ummodifiable = new HashMap<>();

        for (final Struct struct : definition.structs.values()) {
            ummodifiable.put(struct.name, new Struct(struct));
        }

        structs = Collections.unmodifiableMap(ummodifiable);
        transforms = Collections.unmodifiableMap(definition.transforms);

        voidType = definition.voidType;
        booleanType = definition.booleanType;
        byteType = definition.byteType;
        shortType = definition.shortType;
        charType = definition.charType;
        intType = definition.intType;
        longType = definition.longType;
        floatType = definition.floatType;
        doubleType = definition.doubleType;

        voidobjType = definition.voidobjType;
        booleanobjType = definition.booleanobjType;
        byteobjType = definition.byteobjType;
        shortobjType = definition.shortobjType;
        charobjType = definition.charobjType;
        intobjType = definition.intobjType;
        longobjType = definition.longobjType;
        floatobjType = definition.floatobjType;
        doubleobjType = definition.doubleobjType;

        objectType = definition.objectType;
        numberType = definition.numberType;
        charseqType = definition.charseqType;
        stringType = definition.stringType;
        utilityType = definition.utilityType;

        listType = definition.listType;
        arraylistType = definition.arraylistType;
        mapType = definition.mapType;
        hashmapType = definition.hashmapType;
        smapType = definition.smapType;

        execType = definition.execType;
    }

    private void addDefaultStructs() {
        addStruct("void"    , void.class    , false);
        addStruct("boolean" , boolean.class , false);
        addStruct("byte"    , byte.class    , false);
        addStruct("short"   , short.class   , false);
        addStruct("char"    , char.class    , false);
        addStruct("int"     , int.class     , false);
        addStruct("long"    , long.class    , false);
        addStruct("float"   , float.class   , false);
        addStruct("double"  , double.class  , false);

        addStruct("Void"      , Void.class      , false);
        addStruct("Boolean"   , Boolean.class   , false);
        addStruct("Byte"      , Byte.class      , false);
        addStruct("Short"     , Short.class     , false);
        addStruct("Character" , Character.class , false);
        addStruct("Integer"   , Integer.class   , false);
        addStruct("Long"      , Long.class      , false);
        addStruct("Float"     , Float.class     , false);
        addStruct("Double"    , Double.class    , false);

        addStruct("Object"       , Object.class       , false);
        addStruct("Number"       , Number.class       , false);
        addStruct("CharSequence" , CharSequence.class , false);
        addStruct("String"       , String.class       , false);
        addStruct("Utility"      , Utility.class      , false);

        addStruct("List"                   , List.class      , false);
        addStruct("ArrayList"              , ArrayList.class , false);
        addStruct("Map"                    , Map.class       , false);
        addStruct("HashMap"                , HashMap.class   , false);
        addStruct("Map<String,Object>"     , Map.class       , true);
        addStruct("HashMap<String,Object>" , HashMap.class   , true);

        addStruct("Executable" , Executable.class , false);
    }

    private void addDefaultElements() {
        addMethod("Object", "toString", null, false, stringType, new Type[] {}, null, null);
        addMethod("Object", "equals", null, false, booleanType, new Type[] {objectType}, null, null);
        addMethod("Object", "hashCode", null, false, intType, new Type[] {}, null, null);

        addConstructor("Boolean", "new", new Type[] {booleanType}, null);
        addMethod("Boolean", "valueOf", null, true, booleanobjType, new Type[] {booleanType}, null, null);
        addMethod("Boolean", "booleanValue", null, false, booleanType, new Type[] {}, null, null);

        addConstructor("Byte", "new", new Type[]{byteType}, null);
        addMethod("Byte", "valueOf", null, true, byteobjType, new Type[] {byteType}, null, null);
        addMethod("Byte", "byteValue", null, false, byteType, new Type[] {}, null, null);
        addField("Byte", "MIN_VALUE", null, true, byteType);
        addField("Byte", "MAX_VALUE", null, true, byteType);

        addConstructor("Short", "new", new Type[]{shortType}, null);
        addMethod("Short", "valueOf", null, true, shortobjType, new Type[] {shortType}, null, null);
        addMethod("Short", "shortValue", null, false, shortType, new Type[] {}, null, null);
        addField("Short", "MIN_VALUE", null, true, shortType);
        addField("Short", "MAX_VALUE", null, true, shortType);

        addConstructor("Character", "new", new Type[]{charType}, null);
        addMethod("Character", "valueOf", null, true, charobjType, new Type[] {charType}, null, null);
        addMethod("Character", "charValue", null, false, charType, new Type[] {}, null, null);
        addField("Character", "MIN_VALUE", null, true, charType);
        addField("Character", "MAX_VALUE", null, true, charType);

        addConstructor("Integer", "new", new Type[]{intType}, null);
        addMethod("Integer", "valueOf", null, true, intobjType, new Type[] {intType}, null, null);
        addMethod("Integer", "intValue", null, false, intType, new Type[] {}, null, null);
        addField("Integer", "MIN_VALUE", null, true, intType);
        addField("Integer", "MAX_VALUE", null, true, intType);

        addConstructor("Long", "new", new Type[]{longType}, null);
        addMethod("Long", "valueOf", null, true, longobjType, new Type[] {longType}, null, null);
        addMethod("Long", "longValue", null, false, longType, new Type[] {}, null, null);
        addField("Long", "MIN_VALUE", null, true, longType);
        addField("Long", "MAX_VALUE", null, true, longType);

        addConstructor("Float", "new", new Type[]{floatType}, null);
        addMethod("Float", "valueOf", null, true, floatobjType, new Type[] {floatType}, null, null);
        addMethod("Float", "floatValue", null, false, floatType, new Type[] {}, null, null);
        addField("Float", "MIN_VALUE", null, true, floatType);
        addField("Float", "MAX_VALUE", null, true, floatType);

        addConstructor("Double", "new", new Type[]{doubleType}, null);
        addMethod("Double", "valueOf", null, true, doubleobjType, new Type[] {doubleType}, null, null);
        addMethod("Double", "doubleValue", null, false, doubleType, new Type[] {}, null, null);
        addField("Double", "MIN_VALUE", null, true, doubleType);
        addField("Double", "MAX_VALUE", null, true, doubleType);

        addMethod("Number", "byteValue", null, false, byteType, new Type[] {}, null, null);
        addMethod("Number", "shortValue", null, false, shortType, new Type[] {}, null, null);
        addMethod("Number", "intValue", null, false, intType, new Type[] {}, null, null);
        addMethod("Number", "longValue", null, false, longType, new Type[] {}, null, null);
        addMethod("Number", "floatValue", null, false, floatType, new Type[] {}, null, null);
        addMethod("Number", "doubleValue", null, false, doubleType, new Type[] {}, null, null);

        addMethod("CharSequence", "charAt", null, false, charType, new Type[] {intType}, null, null);
        addMethod("CharSequence", "length", null, false, intType, new Type[] {}, null, null);

        addConstructor("String", "new", new Type[] {}, null);
        addMethod("String", "codePointAt", null, false, intType, new Type[] {intType}, null, null);
        addMethod("String", "compareTo", null, false, intType, new Type[] {stringType}, null, null);
        addMethod("String", "concat", null, false, stringType, new Type[] {stringType}, null, null);
        addMethod("String", "endsWith", null, false, booleanType, new Type[] {stringType}, null, null);
        addMethod("String", "indexOf", null, false, intType, new Type[] {stringType, intType}, null, null);
        addMethod("String", "isEmpty", null, false, booleanType, new Type[] {}, null, null);
        addMethod("String", "replace", null, false, stringType, new Type[] {charseqType, charseqType}, null, null);
        addMethod("String", "startsWith", null, false, booleanType, new Type[] {stringType}, null, null);
        addMethod("String", "substring", null, false, stringType, new Type[] {intType, intType}, null, null);
        addMethod("String", "toCharArray", null, false, getType(charType.struct, 1), new Type[] {}, null, null);
        addMethod("String", "trim", null, false, stringType, new Type[] {}, null, null);

        addMethod("Utility", "NumberToboolean", null, true, booleanType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberTochar", null, true, charType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToBoolean", null, true, booleanobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToByte", null, true, byteobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToShort", null, true, shortobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToCharacter", null, true, charobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToInteger", null, true, intobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToLong", null, true, longobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToFloat", null, true, floatobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "NumberToDouble", null, true, doubleobjType, new Type[] {numberType}, null, null);
        addMethod("Utility", "booleanTobyte", null, true, byteType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "booleanToshort", null, true, shortType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "booleanTochar", null, true, charType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "booleanToint", null, true, intType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "booleanTolong", null, true, longType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "booleanTofloat", null, true, floatType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "booleanTodouble", null, true, doubleType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "booleanToInteger", null, true, intobjType, new Type[] {booleanType}, null, null);
        addMethod("Utility", "BooleanTobyte", null, true, byteType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToshort", null, true, shortType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanTochar", null, true, charType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToint", null, true, intType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanTolong", null, true, longType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanTofloat", null, true, floatType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanTodouble", null, true, doubleType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToByte", null, true, byteobjType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToShort", null, true, shortobjType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToCharacter", null, true, charobjType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToInteger", null, true, intobjType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToLong", null, true, longobjType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToFloat", null, true, floatobjType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "BooleanToDouble", null, true, doubleobjType, new Type[] {booleanobjType}, null, null);
        addMethod("Utility", "byteToboolean", null, true, booleanType, new Type[] {byteType}, null, null);
        addMethod("Utility", "ByteToboolean", null, true, booleanType, new Type[] {byteobjType}, null, null);
        addMethod("Utility", "ByteTochar", null, true, charType, new Type[] {byteobjType}, null, null);
        addMethod("Utility", "shortToboolean", null, true, booleanType, new Type[] {shortType}, null, null);
        addMethod("Utility", "ShortToboolean", null, true, booleanType, new Type[] {shortobjType}, null, null);
        addMethod("Utility", "ShortTochar", null, true, charType, new Type[] {shortobjType}, null, null);
        addMethod("Utility", "charToboolean", null, true, booleanType, new Type[] {charType}, null, null);
        addMethod("Utility", "charToInteger", null, true, intobjType, new Type[] {charType}, null, null);
        addMethod("Utility", "CharacterToboolean", null, true, booleanType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterTobyte", null, true, byteType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToshort", null, true, shortType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToint", null, true, intType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterTolong", null, true, longType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterTofloat", null, true, floatType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterTodouble", null, true, doubleType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToBoolean", null, true, booleanobjType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToByte", null, true, byteobjType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToShort", null, true, shortobjType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToInteger", null, true, intobjType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToLong", null, true, longobjType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToFloat", null, true, floatobjType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "CharacterToDouble", null, true, doubleobjType, new Type[] {charobjType}, null, null);
        addMethod("Utility", "intToboolean", null, true, booleanType, new Type[] {intType}, null, null);
        addMethod("Utility", "IntegerToboolean", null, true, booleanType, new Type[] {intobjType}, null, null);
        addMethod("Utility", "IntegerTochar", null, true, charType, new Type[] {intobjType}, null, null);
        addMethod("Utility", "longToboolean", null, true, booleanType, new Type[] {longType}, null, null);
        addMethod("Utility", "LongToboolean", null, true, booleanType, new Type[] {longobjType}, null, null);
        addMethod("Utility", "LongTochar", null, true, charType, new Type[] {longobjType}, null, null);
        addMethod("Utility", "floatToboolean", null, true, booleanType, new Type[] {floatType}, null, null);
        addMethod("Utility", "FloatToboolean", null, true, booleanType, new Type[] {floatobjType}, null, null);
        addMethod("Utility", "FloatTochar", null, true, charType, new Type[] {floatobjType}, null, null);
        addMethod("Utility", "doubleToboolean", null, true, booleanType, new Type[] {doubleType}, null, null);
        addMethod("Utility", "DoubleToboolean", null, true, booleanType, new Type[] {doubleobjType}, null, null);
        addMethod("Utility", "DoubleTochar", null, true, charType, new Type[] {doubleobjType}, null, null);

        addMethod("List", "addLast", "add", false, booleanType, new Type[] {objectType}, null, null);
        addMethod("List", "add", null, false, voidType, new Type[] {intType, objectType}, null, null);
        addMethod("List", "get", null, false, objectType, new Type[] {intType}, null, null);
        addMethod("List", "remove", null, false, objectType, new Type[] {intType}, null, null);
        addMethod("List", "size", null, false, intType, new Type[] {}, null, null);
        addMethod("List", "isEmpty", null, false, booleanType, new Type[] {}, null, null);

        addConstructor("ArrayList", "new", new Type[] {}, null);

        addMethod("Map", "put", null, false, objectType, new Type[] {objectType, objectType}, null, null);
        addMethod("Map", "get", null, false, objectType, new Type[] {objectType}, null, null);
        addMethod("Map", "remove", null, false, objectType, new Type[] {objectType}, null, null);
        addMethod("Map", "size", null, false, intType, new Type[] {}, null, null);
        addMethod("Map", "isEmpty", null, false, booleanType, new Type[] {}, null, null);

        addConstructor("HashMap", "new", new Type[] {}, null);

        addMethod("Map<String,Object>", "put", null, false, objectType, new Type[] {objectType, objectType}, null, new Type[] {stringType, objectType});
        addMethod("Map<String,Object>", "get", null, false, objectType, new Type[] {objectType}, null, new Type[] {stringType});
        addMethod("Map<String,Object>", "remove", null, false, objectType, new Type[] {objectType}, null, new Type[] {stringType});
        addMethod("Map<String,Object>", "size", null, false, intType, new Type[] {}, null, null);
        addMethod("Map<String,Object>", "isEmpty", null, false, booleanType, new Type[] {}, null, null);

        addConstructor("HashMap<String,Object>", "new", new Type[] {}, null);
    }

    private void copyDefaultStructs() {
        copyStruct("Void", "Object");
        copyStruct("Boolean", "Object");
        copyStruct("Byte", "Number", "Object");
        copyStruct("Short", "Number", "Object");
        copyStruct("Character", "Object");
        copyStruct("Integer", "Number", "Object");
        copyStruct("Long", "Number", "Object");
        copyStruct("Float", "Number", "Object");
        copyStruct("Double", "Number", "Object");

        copyStruct("Number", "Object");
        copyStruct("CharSequence", "Object");
        copyStruct("String", "CharSequence", "Object");

        copyStruct("List", "Object");
        copyStruct("ArrayList", "List", "Object");
        copyStruct("Map", "Object");
        copyStruct("HashMap", "Map", "Object");
        copyStruct("Map<String,Object>", "Object");
        copyStruct("HashMap<String,Object>", "Map<String,Object>", "Object");

        copyStruct("Executable", "Object");
    }

    private void addDefaultTransforms() {
        addTransform(booleanType, byteType, "Utility", "booleanTobyte", true);
        addTransform(booleanType, shortType, "Utility", "booleanToshort", true);
        addTransform(booleanType, charType, "Utility", "booleanTochar", true);
        addTransform(booleanType, intType, "Utility", "booleanToint", true);
        addTransform(booleanType, longType, "Utility", "booleanTolong", true);
        addTransform(booleanType, floatType, "Utility", "booleanTofloat", true);
        addTransform(booleanType, doubleType, "Utility", "booleanTodouble", true);
        addTransform(booleanType, objectType, "Boolean", "valueOf", true);
        addTransform(booleanType, numberType, "Utility", "booleanToInteger", true);
        addTransform(booleanType, booleanobjType, "Boolean", "valueOf", true);

        addTransform(byteType, booleanType, "Utility", "byteToboolean", true);
        addTransform(byteType, objectType, "Byte", "valueOf", true);
        addTransform(byteType, numberType, "Byte", "valueOf", true);
        addTransform(byteType, byteobjType, "Byte", "valueOf", true);

        addTransform(shortType, booleanType, "Utility", "shortToboolean", true);
        addTransform(shortType, objectType, "Short", "valueOf", true);
        addTransform(shortType, numberType, "Short", "valueOf", true);
        addTransform(shortType, shortobjType, "Short", "valueOf", true);

        addTransform(charType, booleanType, "Utility", "charToboolean", true);
        addTransform(charType, objectType, "Character", "valueOf", true);
        addTransform(charType, numberType, "Utility", "charToInteger", true);
        addTransform(charType, charobjType, "Character", "valueOf", true);

        addTransform(intType, booleanType, "Utility", "intToboolean", true);
        addTransform(intType, objectType, "Integer", "valueOf", true);
        addTransform(intType, numberType, "Integer", "valueOf", true);
        addTransform(intType, intobjType, "Integer", "valueOf", true);

        addTransform(longType, booleanType, "Utility", "longToboolean", true);
        addTransform(longType, objectType, "Long", "valueOf", true);
        addTransform(longType, numberType, "Long", "valueOf", true);
        addTransform(longType, longobjType, "Long", "valueOf", true);

        addTransform(floatType, booleanType, "Utility", "floatToboolean", true);
        addTransform(floatType, objectType, "Float", "valueOf", true);
        addTransform(floatType, numberType, "Float", "valueOf", true);
        addTransform(floatType, floatobjType, "Float", "valueOf", true);

        addTransform(doubleType, booleanType, "Utility", "doubleToboolean", true);
        addTransform(doubleType, objectType, "Double", "valueOf", true);
        addTransform(doubleType, numberType, "Double", "valueOf", true);
        addTransform(doubleType, doubleobjType, "Double", "valueOf", true);

        addTransform(objectType, booleanType, "Boolean", "booleanValue", false);
        addTransform(objectType, byteType, "Number", "byteValue", false);
        addTransform(objectType, shortType, "Number", "shortValue", false);
        addTransform(objectType, charType, "Character", "charValue", false);
        addTransform(objectType, intType, "Number", "intValue", false);
        addTransform(objectType, longType, "Number", "longValue", false);
        addTransform(objectType, floatType, "Number", "floatValue", false);
        addTransform(objectType, doubleType, "Number", "doubleValue", false);
        
        addTransform(numberType, booleanType, "Utility", "NumberToboolean", true);
        addTransform(numberType, byteType, "Number", "byteValue", false);
        addTransform(numberType, shortType, "Number", "shortValue", false);
        addTransform(numberType, charType, "Utility", "NumberTochar", true);
        addTransform(numberType, intType, "Number", "intValue", false);
        addTransform(numberType, longType, "Number", "longValue", false);
        addTransform(numberType, floatType, "Number", "floatValue", false);
        addTransform(numberType, doubleType, "Number", "doubleValue", false);
        addTransform(numberType, booleanobjType, "Utility", "NumberToBoolean", true);
        addTransform(numberType, byteobjType, "Utility", "NumberToByte", true);
        addTransform(numberType, shortobjType, "Utility", "NumberToShort", true);
        addTransform(numberType, charobjType, "Utility", "NumberToCharacter", true);
        addTransform(numberType, intobjType, "Utility", "NumberToInteger", true);
        addTransform(numberType, longobjType, "Utility", "NumberToLong", true);
        addTransform(numberType, floatobjType, "Utility", "NumberToFloat", true);
        addTransform(numberType, doubleobjType, "Utility", "NumberToDouble", true);

        addTransform(booleanobjType, booleanType, "Boolean", "booleanValue", false);
        addTransform(booleanobjType, byteType, "Utility", "BooleanTobyte", true);
        addTransform(booleanobjType, shortType, "Utility", "BooleanToshort", true);
        addTransform(booleanobjType, charType, "Utility", "BooleanTochar", true);
        addTransform(booleanobjType, intType, "Utility", "BooleanToint", true);
        addTransform(booleanobjType, longType, "Utility", "BooleanTolong", true);
        addTransform(booleanobjType, floatType, "Utility", "BooleanTofloat", true);
        addTransform(booleanobjType, doubleType, "Utility", "BooleanTodouble", true);
        addTransform(booleanobjType, numberType, "Utility", "BooleanToLong", true);
        addTransform(booleanobjType, byteobjType, "Utility", "BooleanToByte", true);
        addTransform(booleanobjType, shortobjType, "Utility", "BooleanToShort", true);
        addTransform(booleanobjType, charobjType, "Utility", "BooleanToCharacter", true);
        addTransform(booleanobjType, intobjType, "Utility", "BooleanToInteger", true);
        addTransform(booleanobjType, longobjType, "Utility", "BooleanToLong", true);
        addTransform(booleanobjType, floatobjType, "Utility", "BooleanToFloat", true);
        addTransform(booleanobjType, doubleobjType, "Utility", "BooleanToDouble", true);

        addTransform(byteobjType, booleanType, "Utility", "ByteToboolean", true);
        addTransform(byteobjType, byteType, "Byte", "byteValue", false);
        addTransform(byteobjType, shortType, "Byte", "shortValue", false);
        addTransform(byteobjType, charType, "Utility", "ByteTochar", true);
        addTransform(byteobjType, intType, "Byte", "intValue", false);
        addTransform(byteobjType, longType, "Byte", "longValue", false);
        addTransform(byteobjType, floatType, "Byte", "floatValue", false);
        addTransform(byteobjType, doubleType, "Byte", "doubleValue", false);
        addTransform(byteobjType, booleanobjType, "Utility", "NumberToBoolean", true);
        addTransform(byteobjType, shortobjType, "Utility", "NumberToShort", true);
        addTransform(byteobjType, charobjType, "Utility", "NumberToCharacter", true);
        addTransform(byteobjType, intobjType, "Utility", "NumberToInteger", true);
        addTransform(byteobjType, longobjType, "Utility", "NumberToLong", true);
        addTransform(byteobjType, floatobjType, "Utility", "NumberToFloat", true);
        addTransform(byteobjType, doubleobjType, "Utility", "NumberToDouble", true);

        addTransform(shortobjType, booleanType, "Utility", "ShortToboolean", true);
        addTransform(shortobjType, byteType, "Short", "byteValue", false);
        addTransform(shortobjType, shortType, "Short", "shortValue", false);
        addTransform(shortobjType, charType, "Utility", "ShortTochar", true);
        addTransform(shortobjType, intType, "Short", "intValue", false);
        addTransform(shortobjType, longType, "Short", "longValue", false);
        addTransform(shortobjType, floatType, "Short", "floatValue", false);
        addTransform(shortobjType, doubleType, "Short", "doubleValue", false);
        addTransform(shortobjType, booleanobjType, "Utility", "NumberToBoolean", true);
        addTransform(shortobjType, byteobjType, "Utility", "NumberToByte", true);
        addTransform(shortobjType, charobjType, "Utility", "NumberToCharacter", true);
        addTransform(shortobjType, intobjType, "Utility", "NumberToInteger", true);
        addTransform(shortobjType, longobjType, "Utility", "NumberToLong", true);
        addTransform(shortobjType, floatobjType, "Utility", "NumberToFloat", true);
        addTransform(shortobjType, doubleobjType, "Utility", "NumberToDouble", true);

        addTransform(charobjType, booleanType, "Utility", "CharacterToboolean", true);
        addTransform(charobjType, byteType, "Utility", "CharacterTobyte", true);
        addTransform(charobjType, shortType, "Utility", "CharacterToshort", true);
        addTransform(charobjType, charType, "Character", "charValue", false);
        addTransform(charobjType, intType, "Utility", "CharacterToint", true);
        addTransform(charobjType, longType, "Utility", "CharacterTolong", true);
        addTransform(charobjType, floatType, "Utility", "CharacterTofloat", true);
        addTransform(charobjType, doubleType, "Utility", "CharacterTodouble", true);
        addTransform(charobjType, booleanobjType, "Utility", "CharacterToBoolean", true);
        addTransform(charobjType, byteobjType, "Utility", "CharacterToByte", true);
        addTransform(charobjType, shortobjType, "Utility", "CharacterToShort", true);
        addTransform(charobjType, intobjType, "Utility", "CharacterToInteger", true);
        addTransform(charobjType, longobjType, "Utility", "CharacterToLong", true);
        addTransform(charobjType, floatobjType, "Utility", "CharacterToFloat", true);
        addTransform(charobjType, doubleobjType, "Utility", "CharacterToDouble", true);

        addTransform(intobjType, booleanType, "Utility", "IntegerToboolean", true);
        addTransform(intobjType, byteType, "Integer", "byteValue", false);
        addTransform(intobjType, shortType, "Integer", "shortValue", false);
        addTransform(intobjType, charType, "Utility", "IntegerTochar", true);
        addTransform(intobjType, intType, "Integer", "intValue", false);
        addTransform(intobjType, longType, "Integer", "longValue", false);
        addTransform(intobjType, floatType, "Integer", "floatValue", false);
        addTransform(intobjType, doubleType, "Integer", "doubleValue", false);
        addTransform(intobjType, booleanobjType, "Utility", "NumberToBoolean", true);
        addTransform(intobjType, byteobjType, "Utility", "NumberToByte", true);
        addTransform(intobjType, shortobjType, "Utility", "NumberToShort", true);
        addTransform(intobjType, charobjType, "Utility", "NumberToCharacter", true);
        addTransform(intobjType, longobjType, "Utility", "NumberToLong", true);
        addTransform(intobjType, floatobjType, "Utility", "NumberToFloat", true);
        addTransform(intobjType, doubleobjType, "Utility", "NumberToDouble", true);

        addTransform(longobjType, booleanType, "Utility", "LongToboolean", true);
        addTransform(longobjType, byteType, "Long", "byteValue", false);
        addTransform(longobjType, shortType, "Long", "shortValue", false);
        addTransform(longobjType, charType, "Utility", "LongTochar", true);
        addTransform(longobjType, intType, "Long", "intValue", false);
        addTransform(longobjType, longType, "Long", "longValue", false);
        addTransform(longobjType, floatType, "Long", "floatValue", false);
        addTransform(longobjType, doubleType, "Long", "doubleValue", false);
        addTransform(longobjType, booleanobjType, "Utility", "NumberToBoolean", true);
        addTransform(longobjType, byteobjType, "Utility", "NumberToByte", true);
        addTransform(longobjType, shortobjType, "Utility", "NumberToShort", true);
        addTransform(longobjType, charobjType, "Utility", "NumberToCharacter", true);
        addTransform(longobjType, intobjType, "Utility", "NumberToInteger", true);
        addTransform(longobjType, floatobjType, "Utility", "NumberToFloat", true);
        addTransform(longobjType, doubleobjType, "Utility", "NumberToDouble", true);

        addTransform(floatobjType, booleanType, "Utility", "FloatToboolean", true);
        addTransform(floatobjType, byteType, "Float", "byteValue", false);
        addTransform(floatobjType, shortType, "Float", "shortValue", false);
        addTransform(floatobjType, charType, "Utility", "FloatTochar", true);
        addTransform(floatobjType, intType, "Float", "intValue", false);
        addTransform(floatobjType, longType, "Float", "longValue", false);
        addTransform(floatobjType, floatType, "Float", "floatValue", false);
        addTransform(floatobjType, doubleType, "Float", "doubleValue", false);
        addTransform(floatobjType, booleanobjType, "Utility", "NumberToBoolean", true);
        addTransform(floatobjType, byteobjType, "Utility", "NumberToByte", true);
        addTransform(floatobjType, shortobjType, "Utility", "NumberToShort", true);
        addTransform(floatobjType, charobjType, "Utility", "NumberToCharacter", true);
        addTransform(floatobjType, intobjType, "Utility", "NumberToInteger", true);
        addTransform(floatobjType, longobjType, "Utility", "NumberToLong", true);
        addTransform(floatobjType, doubleobjType, "Utility", "NumberToDouble", true);

        addTransform(doubleobjType, booleanType, "Utility", "DoubleToboolean", true);
        addTransform(doubleobjType, byteType, "Double", "byteValue", false);
        addTransform(doubleobjType, shortType, "Double", "shortValue", false);
        addTransform(doubleobjType, charType, "Utility", "DoubleTochar", true);
        addTransform(doubleobjType, intType, "Double", "intValue", false);
        addTransform(doubleobjType, longType, "Double", "longValue", false);
        addTransform(doubleobjType, floatType, "Double", "floatValue", false);
        addTransform(doubleobjType, doubleType, "Double", "doubleValue", false);
        addTransform(doubleobjType, booleanobjType, "Utility", "NumberToBoolean", true);
        addTransform(doubleobjType, byteobjType, "Utility", "NumberToByte", true);
        addTransform(doubleobjType, shortobjType, "Utility", "NumberToShort", true);
        addTransform(doubleobjType, charobjType, "Utility", "NumberToCharacter", true);
        addTransform(doubleobjType, intobjType, "Utility", "NumberToInteger", true);
        addTransform(doubleobjType, longobjType, "Utility", "NumberToLong", true);
        addTransform(doubleobjType, floatobjType, "Utility", "NumberToFloat", true);
    }

    private void addStruct(final String name, final Class<?> clazz, final boolean generic) {
        if (!name.matches("^[_a-zA-Z][<>,_a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException("Invalid struct name [" + name + "].");
        }

        if (structs.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate struct name [" + name + "].");
        }

        final Struct struct = new Struct(name, clazz, org.objectweb.asm.Type.getType(clazz), generic);

        structs.put(name, struct);
    }

    private void addConstructor(final String struct, final String name, final Type[] args, final Type[] genargs) {
        final Struct owner = structs.get(struct);

        if (owner == null) {
            throw new IllegalArgumentException(
                    "Owner struct [" + struct + "] not defined for constructor [" + name + "].");
        }

        if (!name.matches("^[_a-zA-Z][_a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException(
                    "Invalid constructor name [" + name + "] with the struct [" + owner.name + "].");
        }

        if (owner.constructors.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Duplicate constructor name [" + name + "] found within the struct [" + owner.name + "].");
        }

        if (owner.statics.containsKey(name)) {
            throw new IllegalArgumentException("Constructors and functions may not have the same name" +
                    " [" + name + "] within the same struct [" + owner.name + "].");
        }

        if (owner.methods.containsKey(name)) {
            throw new IllegalArgumentException("Constructors and methods may not have the same name" +
                    " [" + name + "] within the same struct [" + owner.name + "].");
        }

        final Class[] classes = new Class[args.length];

        for (int count = 0; count < classes.length; ++count) {
            if (genargs != null) {
                try {
                    genargs[count].clazz.asSubclass(args[count].clazz);
                } catch (ClassCastException exception) {
                    throw new ClassCastException("Generic argument [" + genargs[count].name + "]" +
                            " is not a sub class of [" + args[count].name + "] in the constructor" +
                            " [" + name + " ] from the struct [" + owner.name + "].");
                }
            }

            classes[count] = args[count].clazz;
        }

        final java.lang.reflect.Constructor<?> reflect;

        try {
            reflect = owner.clazz.getConstructor(classes);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException("Constructor [" + name + "] not found for class" +
                    " [" + owner.clazz.getName() + "] with arguments " + Arrays.toString(classes) + ".");
        }

        final org.objectweb.asm.commons.Method asm = org.objectweb.asm.commons.Method.getMethod(reflect);
        final Constructor constructor =
                new Constructor(name, owner, Arrays.asList(genargs != null ? genargs : args), asm, reflect);

        owner.constructors.put(name, constructor);
    }

    private void addMethod(final String struct, final String name, final String alias, final boolean statik,
                          final Type rtn, final Type[] args, final Type genrtn, final Type[] genargs) {
        final Struct owner = structs.get(struct);

        if (owner == null) {
            throw new IllegalArgumentException("Owner struct [" + struct + "] not defined" +
                    " for " + (statik ? "function" : "method") + " [" + name + "].");
        }

        if (!name.matches("^[_a-zA-Z][_a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException("Invalid " + (statik ? "function" : "method") +
                    " name [" + name + "] with the struct [" + owner.name + "].");
        }

        if (owner.constructors.containsKey(name)) {
            throw new IllegalArgumentException("Constructors and " + (statik ? "functions" : "methods") +
                    " may not have the same name [" + name + "] within the same struct" +
                    " [" + owner.name + "].");
        }

        if (owner.statics.containsKey(name)) {
            if (statik) {
                throw new IllegalArgumentException(
                        "Duplicate function name [" + name + "] found within the struct [" + owner.name + "].");
            } else {
                throw new IllegalArgumentException("Functions and methods may not have the same name" +
                        " [" + name + "] within the same struct [" + owner.name + "].");
            }
        }

        if (owner.methods.containsKey(name)) {
            if (statik) {
                throw new IllegalArgumentException("Functions and methods may not have the same name" +
                        " [" + name + "] within the same struct [" + owner.name + "].");
            } else {
                throw new IllegalArgumentException("Duplicate method name [" + name + "]" +
                        " found within the struct [" + owner.name + "].");
            }
        }

        if (genrtn != null) {
            try {
                genrtn.clazz.asSubclass(rtn.clazz);
            } catch (ClassCastException exception) {
                throw new ClassCastException("Generic return [" + genrtn.clazz.getCanonicalName() + "]" +
                        " is not a sub class of [" + rtn.clazz.getCanonicalName() + "] in the method" +
                        " [" + name + " ] from the struct [" + owner.name + "].");
            }
        }

        if (genargs != null && genargs.length != args.length) {
            throw new IllegalArgumentException("Generic arguments arity [" +  genargs.length + "] is not the same as " +
                    (statik ? "function" : "method") + " [" + name + "] arguments arity" +
                    " [" + args.length + "] within the struct [" + owner.name + "].");
        }

        final Class[] classes = new Class[args.length];

        for (int count = 0; count < classes.length; ++count) {
            if (genargs != null) {
                try {
                    genargs[count].clazz.asSubclass(args[count].clazz);
                } catch (ClassCastException exception) {
                    throw new ClassCastException("Generic argument [" + genargs[count].name + "] is not a sub class" +
                            " of [" + args[count].name + "] in the " + (statik ? "function" : "method") +
                            " [" + name + " ] from the struct [" + owner.name + "].");
                }
            }

            classes[count] = args[count].clazz;
        }

        final java.lang.reflect.Method reflect;

        try {
            reflect = owner.clazz.getMethod(alias == null ? name : alias, classes);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException((statik ? "Function" : "Method") +
                    " [" + (alias == null ? name : alias) + "] not found for class [" + owner.clazz.getName() + "]" +
                    " with arguments " + Arrays.toString(classes) + ".");
        }

        if (!reflect.getReturnType().equals(rtn.clazz)) {
            throw new IllegalArgumentException("Specified return type class [" + rtn.clazz + "]" +
                    " does not match the found return type class [" + reflect.getReturnType() + "] for the " +
                    (statik ? "function" : "method") + " [" + name + "]" +
                    " within the struct [" + owner.name + "].");
        }

        final org.objectweb.asm.commons.Method asm = org.objectweb.asm.commons.Method.getMethod(reflect);
        final Method method = new Method(name, owner, genrtn != null ? genrtn : rtn,
                Arrays.asList(genargs != null ? genargs : args), asm, reflect);
        final int modifiers = reflect.getModifiers();

        if (statik) {
            if (!java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException("Function [" + name + "]" +
                        " within the struct [" + owner.name + "] is not linked to a static Java method.");
            }

            owner.functions.put(name, method);
        } else {
            if (java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException("Method [" + name + "]" +
                        " within the struct [" + owner.name + "] is not linked to a non-static Java method.");
            }

            owner.methods.put(name, method);
        }
    }

    private void addField(final String struct, final String name, final String alias,
                         final boolean statik, final Type type) {
        final Struct owner = structs.get(struct);

        if (owner == null) {
            throw new IllegalArgumentException("Owner struct [" + struct + "] not defined for " +
                    (statik ? "static" : "member") + " [" + name + "].");
        }

        if (!name.matches("^[_a-zA-Z][_a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException("Invalid " + (statik ? "static" : "member") +
                    " name [" + name + "] with the struct [" + owner.name + "].");
        }

        if (owner.statics.containsKey(name)) {
            if (statik) {
                throw new IllegalArgumentException("Duplicate static name [" + name + "]" +
                        " found within the struct [" + owner.name + "].");
            } else {
                throw new IllegalArgumentException("Statics and members may not have the same name " +
                        "[" + name + "] within the same struct [" + owner.name + "].");
            }
        }

        if (owner.members.containsKey(name)) {
            if (statik) {
                throw new IllegalArgumentException("Statics and members may not have the same name " +
                        "[" + name + "] within the same struct [" + owner.name + "].");
            } else {
                throw new IllegalArgumentException("Duplicate member name [" + name + "]" +
                        " found within the struct [" + owner.name + "].");
            }
        }

        java.lang.reflect.Field reflect;

        try {
            reflect = owner.clazz.getField(alias == null ? name : alias);
        } catch (NoSuchFieldException exception) {
            throw new IllegalArgumentException("Field [" + name + "]" +
                    " not found for class [" + owner.clazz.getName() + "].");
        }

        final Field field = new Field(name, owner, type, reflect);
        final int modifiers = reflect.getModifiers();

        if (statik) {
            if (!java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException();
            }

            if (!java.lang.reflect.Modifier.isFinal(modifiers)) {
                throw new IllegalArgumentException("Static [" + name + "]" +
                        " within the struct [" + owner.name + "] is not linked to static Java field.");
            }

            owner.statics.put(alias == null ? name : alias, field);
        } else {
            if (java.lang.reflect.Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException("Member [" + name + "]" +
                        " within the struct [" + owner.name + "] is not linked to non-static Java field.");
            }

            owner.members.put(alias == null ? name : alias, field);
        }
    }

    private void copyStruct(final String struct, final String... children) {
        final Struct owner = structs.get(struct);

        if (owner == null) {
            throw new IllegalArgumentException("Owner struct [" + struct + "] not defined for copy.");
        }

        for (int count = 0; count < children.length; ++count) {
            final Struct child = structs.get(children[count]);

            if (struct == null) {
                throw new IllegalArgumentException("Child struct [" + children[count] + "]" +
                        " not defined for copy to owner struct [" + owner.name + "].");
            }

            try {
                owner.clazz.asSubclass(child.clazz);
            } catch (ClassCastException exception) {
                throw new ClassCastException("Child struct [" + child.name + "]" +
                        " is not a super type of owner struct [" + owner.name + "] in copy.");
            }

            final boolean object = child.clazz.equals(Object.class) &&
                    java.lang.reflect.Modifier.isInterface(owner.clazz.getModifiers());

            for (final Method method : child.methods.values()) {
                if (owner.methods.get(method.name) == null) {
                    java.lang.reflect.Method reflect;

                    try {
                        final Class<?> clazz = object ? Object.class : owner.clazz;
                        reflect = clazz.getMethod(method.method.getName(), method.reflect.getParameterTypes());
                    } catch (NoSuchMethodException exception) {
                        throw new IllegalArgumentException("Method [" + method.method.getName() + "] not found for" +
                                " class [" + owner.clazz.getName() + "] with arguments " +
                                Arrays.toString(method.reflect.getParameterTypes()) + ".");
                    }

                    owner.methods.put(method.name,
                            new Method(method.name, owner, method.rtn, method.arguments, method.method, reflect));
                }
            }

            for (final Field field : child.members.values()) {
                if (owner.members.get(field.name) == null) {
                    java.lang.reflect.Field reflect;

                    try {
                        reflect = owner.clazz.getField(field.reflect.getName());
                    } catch (NoSuchFieldException exception) {
                        throw new IllegalArgumentException("Field [" + field.reflect.getName() + "]" +
                                " not found for class [" + owner.clazz.getName() + "].");
                    }

                    owner.members.put(field.name, new Field(field.name, owner, field.type, reflect));
                }
            }
        }
    }

    private void addTransform(final Type from, final Type to, final String struct, final String name, final boolean statik) {
        final Struct owner = structs.get(struct);

        if (owner == null) {
            throw new IllegalArgumentException("Owner struct [" + struct + "] not defined for" +
                    " transform with cast type from [" + from.name + "] and cast type to [" + to.name + "].");
        }

        if (from.equals(to)) {
            throw new IllegalArgumentException("Transform with owner struct [" + owner.name + "] cannot" +
                    " have cast type from [" + from.name + "] be the same as cast type to [" + to.name + "].");
        }

        final Cast cast = new Cast(from, to);

        if (transforms.containsKey(cast)) {
            throw new IllegalArgumentException("Transform with owner struct [" + owner.name + "]" +
                    " and cast type from [" + from.name + "] to cast type to [" + to.name + "] already defined.");
        }

        Method method;
        Type upcast = null;
        Type downcast = null;

        if (statik) {
            method = owner.functions.get(name);

            if (method == null) {
                throw new IllegalArgumentException("Transform with owner struct [" + owner.name + "]" +
                        " and cast type from [" + from.name + "] to cast type to [" + to.name +
                        "] using a function [" + name + "] that is not defined.");
            }

            if (method.arguments.size() != 1) {
                throw new IllegalArgumentException("Transform with owner struct [" + owner.name + "]" +
                        " and cast type from [" + from.name + "] to cast type to [" + to.name +
                        "] using function [" + name + "] does not have a single type argument.");
            }

            Type argument = method.arguments.get(0);

            try {
                from.clazz.asSubclass(argument.clazz);
            } catch (ClassCastException cce0) {
                try {
                    argument.clazz.asSubclass(from.clazz);
                    upcast = argument;
                } catch (ClassCastException cce1) {
                    throw new ClassCastException("Transform with owner struct [" + owner.name + "]" +
                            " and cast type from [" + from.name + "] to cast type to [" + to.name + "] using" +
                            " function [" + name + "] cannot cast from type to the function input argument type.");
                }
            }

            final Type rtn = method.rtn;

            try {
                rtn.clazz.asSubclass(to.clazz);
            } catch (ClassCastException cce0) {
                try {
                    to.clazz.asSubclass(rtn.clazz);
                    downcast = to;
                } catch (ClassCastException cce1) {
                    throw new ClassCastException("Transform with owner struct [" + owner.name + "]" +
                            " and cast type from [" + from.name + "] to cast type to [" + to.name + "] using" +
                            " function [" + name + "] cannot cast to type to the function return argument type.");
                }
            }
        } else {
            method = owner.methods.get(name);

            if (method == null) {
                throw new IllegalArgumentException("Transform with owner struct [" + owner.name + "]" +
                        " and cast type from [" + from.name + "] to cast type to [" + to.name +
                        "] using a method [" + name + "] that is not defined.");
            }

            if (!method.arguments.isEmpty()) {
                throw new IllegalArgumentException("Transform with owner struct [" + owner.name + "]" +
                        " and cast type from [" + from.name + "] to cast type to [" + to.name +
                        "] using method [" + name + "] does not have a single type argument.");
            }

            try {
                from.clazz.asSubclass(owner.clazz);
            } catch (ClassCastException cce0) {
                try {
                    owner.clazz.asSubclass(from.clazz);
                    upcast = getType(owner.name);
                } catch (ClassCastException cce1) {
                    throw new ClassCastException("Transform with owner struct [" + owner.name + "]" +
                            " and cast type from [" + from.name + "] to cast type to [" + to.name + "] using" +
                            " method [" + name + "] cannot cast from type to the method input argument type.");
                }
            }

            final Type rtn = method.rtn;

            try {
                rtn.clazz.asSubclass(to.clazz);
            } catch (ClassCastException cce0) {
                try {
                    to.clazz.asSubclass(rtn.clazz);
                    downcast = to;
                } catch (ClassCastException cce1) {
                    throw new ClassCastException("Transform with owner struct [" + owner.name + "]" +
                            " and cast type from [" + from.name + "] to cast type to [" + to.name + "]" +
                            " using method [" + name + "] cannot cast to type to the method return argument type.");
                }
            }
        }

        final Transform transform = new Transform(cast, method, upcast, downcast);
        transforms.put(cast, transform);
    }

    Type getType(final String name) {
        final int dimensions = getDimensions(name);
        final String structstr = dimensions == 0 ? name : name.substring(0, name.indexOf('['));
        final Struct struct = structs.get(structstr);

        if (struct == null) {
            throw new IllegalArgumentException("The struct with name [" + name + "] has not been defined.");
        }

        return getType(struct, dimensions);
    }

    Type getType(final Struct struct, final int dimensions) {
        String name = struct.name;
        org.objectweb.asm.Type type = struct.type;
        Class<?> clazz = struct.clazz;
        Sort sort;

        if (dimensions > 0) {
            final StringBuilder builder = new StringBuilder(name);
            final char[] brackets = new char[dimensions];

            for (int count = 0; count < dimensions; ++count) {
                builder.append("[]");
                brackets[count] = '[';
            }

            final String descriptor = new String(brackets) + struct.type.getDescriptor();

            name = builder.toString();
            type = org.objectweb.asm.Type.getType(descriptor);

            try {
                clazz = Class.forName(type.getInternalName().replace('/', '.'));
            } catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException("The class [" + type.getInternalName() + "]" +
                        " could not be found to create type [" + name + "].");
            }

            sort = Sort.ARRAY;
        } else if (struct.generic) {
            sort = Sort.GENERIC;
        } else {
            sort = Sort.OBJECT;

            for (Sort value : Sort.values()) {
                if (value.clazz == null) {
                    continue;
                }

                if (value.clazz.equals(struct.clazz)) {
                    sort = value;

                    break;
                }
            }
        }

        return new Type(name, struct, clazz, type, sort);
    }

    private int getDimensions(final String name) {
        int dimensions = 0;
        int index = name.indexOf('[');

        if (index != -1) {
            final int length = name.length();

            while (index < length) {
                if (name.charAt(index) == '[' && ++index < length && name.charAt(index++) == ']') {
                    ++dimensions;
                } else {
                    throw new IllegalArgumentException("Invalid array braces in canonical name [" + name + "].");
                }
            }
        }

        return dimensions;
    }
}
