/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.squareup.moshi;


public final class AmplClassJsonAdapterTest {
    private final com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().build();

    static class BasicPizza {
        int diameter;

        boolean extraCheese;
    }

    @org.junit.Test
    public void basicClassAdapter() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.BasicPizza value = new com.squareup.moshi.AmplClassJsonAdapterTest.BasicPizza();
        value.diameter = 13;
        value.extraCheese = true;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.BasicPizza.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"diameter\":13,\"extraCheese\":true}");
        com.squareup.moshi.AmplClassJsonAdapterTest.BasicPizza fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.BasicPizza.class, "{\"diameter\":13,\"extraCheese\":true}");
        org.assertj.core.api.Assertions.assertThat(fromJson.diameter).isEqualTo(13);
        org.assertj.core.api.Assertions.assertThat(fromJson.extraCheese).isTrue();
    }

    static class PrivateFieldsPizza {
        private java.lang.String secretIngredient;
    }

    @org.junit.Test
    public void privateFields() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.PrivateFieldsPizza value = new com.squareup.moshi.AmplClassJsonAdapterTest.PrivateFieldsPizza();
        value.secretIngredient = "vodka";
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.PrivateFieldsPizza.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"secretIngredient\":\"vodka\"}");
        com.squareup.moshi.AmplClassJsonAdapterTest.PrivateFieldsPizza fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.PrivateFieldsPizza.class, "{\"secretIngredient\":\"vodka\"}");
        org.assertj.core.api.Assertions.assertThat(fromJson.secretIngredient).isEqualTo("vodka");
    }

    static class BasePizza {
        int diameter;
    }

    static class DessertPizza extends com.squareup.moshi.AmplClassJsonAdapterTest.BasePizza {
        boolean chocolate;
    }

    @org.junit.Test
    public void typeHierarchy() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.DessertPizza value = new com.squareup.moshi.AmplClassJsonAdapterTest.DessertPizza();
        value.diameter = 13;
        value.chocolate = true;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.DessertPizza.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"chocolate\":true,\"diameter\":13}");
        com.squareup.moshi.AmplClassJsonAdapterTest.DessertPizza fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.DessertPizza.class, "{\"diameter\":13,\"chocolate\":true}");
        org.assertj.core.api.Assertions.assertThat(fromJson.diameter).isEqualTo(13);
        org.assertj.core.api.Assertions.assertThat(fromJson.chocolate).isTrue();
    }

    static class BaseAbcde {
        int d;

        int a;

        int c;
    }

    static class ExtendsBaseAbcde extends com.squareup.moshi.AmplClassJsonAdapterTest.BaseAbcde {
        int b;

        int e;
    }

    @org.junit.Test
    public void fieldsAreAlphabeticalAcrossFlattenedHierarchy() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsBaseAbcde value = new com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsBaseAbcde();
        value.a = 4;
        value.b = 5;
        value.c = 6;
        value.d = 7;
        value.e = 8;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsBaseAbcde.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"a\":4,\"b\":5,\"c\":6,\"d\":7,\"e\":8}");
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsBaseAbcde fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsBaseAbcde.class, "{\"a\":4,\"b\":5,\"c\":6,\"d\":7,\"e\":8}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(4);
        org.assertj.core.api.Assertions.assertThat(fromJson.b).isEqualTo(5);
        org.assertj.core.api.Assertions.assertThat(fromJson.c).isEqualTo(6);
        org.assertj.core.api.Assertions.assertThat(fromJson.d).isEqualTo(7);
        org.assertj.core.api.Assertions.assertThat(fromJson.e).isEqualTo(8);
    }

    static class StaticFields {
        static int a = 11;

        int b;
    }

    @org.junit.Test
    public void staticFieldsOmitted() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.StaticFields value = new com.squareup.moshi.AmplClassJsonAdapterTest.StaticFields();
        value.b = 12;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.StaticFields.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"b\":12}");
        com.squareup.moshi.AmplClassJsonAdapterTest.StaticFields fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.StaticFields.class, "{\"a\":13,\"b\":12}");
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.AmplClassJsonAdapterTest.StaticFields.a).isEqualTo(11);// Unchanged.
        
        org.assertj.core.api.Assertions.assertThat(fromJson.b).isEqualTo(12);
    }

    static class TransientFields {
        transient int a;

        int b;
    }

    @org.junit.Test
    public void transientFieldsOmitted() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.TransientFields value = new com.squareup.moshi.AmplClassJsonAdapterTest.TransientFields();
        value.a = 11;
        value.b = 12;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.TransientFields.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"b\":12}");
        com.squareup.moshi.AmplClassJsonAdapterTest.TransientFields fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.TransientFields.class, "{\"a\":13,\"b\":12}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(0);// Not assigned.
        
        org.assertj.core.api.Assertions.assertThat(fromJson.b).isEqualTo(12);
    }

    static class BaseA {
        int a;
    }

    static class ExtendsBaseA extends com.squareup.moshi.AmplClassJsonAdapterTest.BaseA {
        int a;
    }

    @org.junit.Test
    public void fieldNameCollision() throws java.lang.Exception {
        try {
            com.squareup.moshi.ClassJsonAdapter.FACTORY.create(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsBaseA.class, com.squareup.moshi.Util.NO_ANNOTATIONS, moshi);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("Conflicting fields:\n" + ("    int com.squareup.moshi.ClassJsonAdapterTest$ExtendsBaseA.a\n" + "    int com.squareup.moshi.ClassJsonAdapterTest$BaseA.a")));
        }
    }

    static class NameCollision {
        java.lang.String foo;

        @com.squareup.moshi.Json(name = "foo")
        java.lang.String bar;
    }

    @org.junit.Test
    public void jsonAnnotationNameCollision() throws java.lang.Exception {
        try {
            com.squareup.moshi.ClassJsonAdapter.FACTORY.create(com.squareup.moshi.AmplClassJsonAdapterTest.NameCollision.class, com.squareup.moshi.Util.NO_ANNOTATIONS, moshi);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("Conflicting fields:\n" + ("    java.lang.String com.squareup.moshi.ClassJsonAdapterTest$NameCollision.foo\n" + "    java.lang.String com.squareup.moshi.ClassJsonAdapterTest$NameCollision.bar")));
        }
    }

    static class TransientBaseA {
        transient int a;
    }

    static class ExtendsTransientBaseA extends com.squareup.moshi.AmplClassJsonAdapterTest.TransientBaseA {
        int a;
    }

    @org.junit.Test
    public void fieldNameCollisionWithTransientFieldIsOkay() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsTransientBaseA value = new com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsTransientBaseA();
        value.a = 11;
        ((com.squareup.moshi.AmplClassJsonAdapterTest.TransientBaseA) (value)).a = 12;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsTransientBaseA.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"a\":11}");
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsTransientBaseA fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsTransientBaseA.class, "{\"a\":11}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(11);
        org.assertj.core.api.Assertions.assertThat(((com.squareup.moshi.AmplClassJsonAdapterTest.TransientBaseA) (fromJson)).a).isEqualTo(0);// Not assigned.
        
    }

    static class NoArgConstructor {
        int a;

        int b;

        NoArgConstructor() {
            a = 5;
        }
    }

    @org.junit.Test
    public void noArgConstructor() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.NoArgConstructor fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NoArgConstructor.class, "{\"b\":8}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(5);
        org.assertj.core.api.Assertions.assertThat(fromJson.b).isEqualTo(8);
    }

    static class NoArgConstructorThrowsCheckedException {
        NoArgConstructorThrowsCheckedException() throws java.lang.Exception {
            throw new java.lang.Exception("foo");
        }
    }

    @org.junit.Test
    public void noArgConstructorThrowsCheckedException() throws java.lang.Exception {
        try {
            fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NoArgConstructorThrowsCheckedException.class, "{}");
            org.junit.Assert.fail();
        } catch (java.lang.RuntimeException expected) {
            org.assertj.core.api.Assertions.assertThat(expected.getCause()).hasMessage("foo");
        }
    }

    static class NoArgConstructorThrowsUncheckedException {
        NoArgConstructorThrowsUncheckedException() throws java.lang.Exception {
            throw new java.lang.UnsupportedOperationException("foo");
        }
    }

    @org.junit.Test
    public void noArgConstructorThrowsUncheckedException() throws java.lang.Exception {
        try {
            fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NoArgConstructorThrowsUncheckedException.class, "{}");
            org.junit.Assert.fail();
        } catch (java.lang.UnsupportedOperationException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("foo");
        }
    }

    static class NoArgConstructorWithDefaultField {
        int a = 5;

        int b;
    }

    @org.junit.Test
    public void noArgConstructorFieldDefaultsHonored() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.NoArgConstructorWithDefaultField fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NoArgConstructorWithDefaultField.class, "{\"b\":8}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(5);
        org.assertj.core.api.Assertions.assertThat(fromJson.b).isEqualTo(8);
    }

    static class MagicConstructor {
        int a;

        public MagicConstructor(java.lang.Void argument) {
            throw new java.lang.AssertionError();
        }
    }

    @org.junit.Test
    public void magicConstructor() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.MagicConstructor fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.MagicConstructor.class, "{\"a\":8}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(8);
    }

    static class MagicConstructorWithDefaultField {
        int a = 5;

        int b;

        public MagicConstructorWithDefaultField(java.lang.Void argument) {
            throw new java.lang.AssertionError();
        }
    }

    @org.junit.Test
    public void magicConstructorFieldDefaultsNotHonored() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.MagicConstructorWithDefaultField fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.MagicConstructorWithDefaultField.class, "{\"b\":3}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(0);// Surprising! No value is assigned.
        
        org.assertj.core.api.Assertions.assertThat(fromJson.b).isEqualTo(3);
    }

    static class NullRootObject {
        int a;
    }

    @org.junit.Test
    public void nullRootObject() throws java.lang.Exception {
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.PrivateFieldsPizza.class, null);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("null");
        com.squareup.moshi.AmplClassJsonAdapterTest.NullRootObject fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NullRootObject.class, "null");
        org.assertj.core.api.Assertions.assertThat(fromJson).isNull();
    }

    static class NullFieldValue {
        java.lang.String a = "not null";
    }

    @org.junit.Test
    public void nullFieldValues() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.NullFieldValue value = new com.squareup.moshi.AmplClassJsonAdapterTest.NullFieldValue();
        value.a = null;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.NullFieldValue.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"a\":null}");
        com.squareup.moshi.AmplClassJsonAdapterTest.NullFieldValue fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NullFieldValue.class, "{\"a\":null}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isNull();
    }

    class NonStatic {    }

    @org.junit.Test
    public void nonStaticNestedClassNotSupported() throws java.lang.Exception {
        try {
            com.squareup.moshi.ClassJsonAdapter.FACTORY.create(com.squareup.moshi.AmplClassJsonAdapterTest.NonStatic.class, com.squareup.moshi.Util.NO_ANNOTATIONS, moshi);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("Cannot serialize non-static nested class " + "com.squareup.moshi.ClassJsonAdapterTest$NonStatic"));
        }
    }

    @org.junit.Test
    public void anonymousClassNotSupported() throws java.lang.Exception {
        java.util.Comparator<java.lang.Object> c = new java.util.Comparator<java.lang.Object>() {
            @java.lang.Override
            public int compare(java.lang.Object a, java.lang.Object b) {
                return 0;
            }
        };
        try {
            com.squareup.moshi.ClassJsonAdapter.FACTORY.create(c.getClass(), com.squareup.moshi.Util.NO_ANNOTATIONS, moshi);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("Cannot serialize anonymous class " + (c.getClass().getName())));
        }
    }

    @org.junit.Test
    public void interfaceNotSupported() throws java.lang.Exception {
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.ClassJsonAdapter.FACTORY.create(java.lang.Runnable.class, com.squareup.moshi.Util.NO_ANNOTATIONS, moshi)).isNull();
    }

    abstract static class Abstract {    }

    @org.junit.Test
    public void abstractClassNotSupported() throws java.lang.Exception {
        try {
            com.squareup.moshi.ClassJsonAdapter.FACTORY.create(com.squareup.moshi.AmplClassJsonAdapterTest.Abstract.class, com.squareup.moshi.Util.NO_ANNOTATIONS, moshi);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("Cannot serialize abstract class " + "com.squareup.moshi.ClassJsonAdapterTest$Abstract"));
        }
    }

    static class ExtendsPlatformClassWithPrivateField extends java.util.SimpleTimeZone {
        int a;

        public ExtendsPlatformClassWithPrivateField() {
            super(0, "FOO");
        }
    }

    @org.junit.Test
    public void platformSuperclassPrivateFieldIsExcluded() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField value = new com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField();
        value.a = 4;
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"a\":4}");
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField.class, "{\"a\":4,\"ID\":\"BAR\"}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(4);
        org.assertj.core.api.Assertions.assertThat(fromJson.getID()).isEqualTo("FOO");
    }

    static class ExtendsPlatformClassWithProtectedField extends java.io.ByteArrayOutputStream {
        int a;

        public ExtendsPlatformClassWithProtectedField() {
            super(2);
        }
    }

    @org.junit.Test
    public void platformSuperclassProtectedFieldIsIncluded() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField value = new com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField();
        value.a = 4;
        value.write(5);
        value.write(6);
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo("{\"a\":4,\"buf\":[5,6],\"count\":2}");
        com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField.class, "{\"a\":4,\"buf\":[5,6],\"count\":2}");
        org.assertj.core.api.Assertions.assertThat(fromJson.a).isEqualTo(4);
        org.assertj.core.api.Assertions.assertThat(fromJson.toByteArray()).contains(((byte) (5)), ((byte) (6)));
    }

    static class NamedFields {
        @com.squareup.moshi.Json(name = "#")
        java.util.List<java.lang.String> phoneNumbers;

        @com.squareup.moshi.Json(name = "@")
        java.lang.String emailAddress;

        @com.squareup.moshi.Json(name = "zip code")
        java.lang.String zipCode;
    }

    @org.junit.Test
    public void jsonAnnotationHonored() throws java.lang.Exception {
        com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields value = new com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields();
        value.phoneNumbers = java.util.Arrays.asList("8005553333", "8005554444");
        value.emailAddress = "cash@square.com";
        value.zipCode = "94043";
        java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields.class, value);
        org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo(("{" + ((("\"#\":[\"8005553333\",\"8005554444\"]," + "\"@\":\"cash@square.com\",") + "\"zip code\":\"94043\"") + "}")));
        com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields.class, ("{" + ((("\"#\":[\"8005553333\",\"8005554444\"]," + "\"@\":\"cash@square.com\",") + "\"zip code\":\"94043\"") + "}")));
        org.assertj.core.api.Assertions.assertThat(fromJson.phoneNumbers).isEqualTo(java.util.Arrays.asList("8005553333", "8005554444"));
        org.assertj.core.api.Assertions.assertThat(fromJson.emailAddress).isEqualTo("cash@square.com");
        org.assertj.core.api.Assertions.assertThat(fromJson.zipCode).isEqualTo("94043");
    }

    private <T> java.lang.String toJson(java.lang.Class<T> type, T value) throws java.io.IOException {
        // Factory.create returns an adapter that matches its argument.
        @java.lang.SuppressWarnings(value = "unchecked")
        com.squareup.moshi.JsonAdapter<T> jsonAdapter = ((com.squareup.moshi.JsonAdapter<T>) (com.squareup.moshi.ClassJsonAdapter.FACTORY.create(type, com.squareup.moshi.Util.NO_ANNOTATIONS, moshi)));
        // Wrap in an array to avoid top-level object warnings without going completely lenient.
        okio.Buffer buffer = new okio.Buffer();
        com.squareup.moshi.JsonWriter jsonWriter = com.squareup.moshi.JsonWriter.of(buffer);
        jsonWriter.setSerializeNulls(true);
        jsonWriter.beginArray();
        jsonAdapter.toJson(jsonWriter, value);
        jsonWriter.endArray();
        org.assertj.core.api.Assertions.assertThat(buffer.readByte()).isEqualTo(((byte) ('[')));
        java.lang.String json = buffer.readUtf8(((buffer.size()) - 1));
        org.assertj.core.api.Assertions.assertThat(buffer.readByte()).isEqualTo(((byte) (']')));
        return json;
    }

    private <T> T fromJson(java.lang.Class<T> type, java.lang.String json) throws java.io.IOException {
        // Factory.create returns an adapter that matches its argument.
        @java.lang.SuppressWarnings(value = "unchecked")
        com.squareup.moshi.JsonAdapter<T> jsonAdapter = ((com.squareup.moshi.JsonAdapter<T>) (com.squareup.moshi.ClassJsonAdapter.FACTORY.create(type, com.squareup.moshi.Util.NO_ANNOTATIONS, moshi)));
        // Wrap in an array to avoid top-level object warnings without going completely lenient.
        com.squareup.moshi.JsonReader jsonReader = com.squareup.moshi.TestUtil.newReader((("[" + json) + "]"));
        jsonReader.beginArray();
        T result = jsonAdapter.fromJson(jsonReader);
        jsonReader.endArray();
        return result;
    }

    /* amplification of com.squareup.moshi.ClassJsonAdapterTest#jsonAnnotationHonored */
    @org.junit.Test
    public void jsonAnnotationHonored_literalMutation75530_failAssert33() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields value = new com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields();
            value.phoneNumbers = java.util.Arrays.asList("8005553333", "8005554444");
            value.emailAddress = "cash@square.com";
            value.zipCode = "94043";
            java.lang.String toJson = toJson(com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields.class, value);
            org.assertj.core.api.Assertions.assertThat(toJson).isEqualTo(("{" + ((("\"#\":[\"8005553333\",\"8005554444\"]," + "\"@\":\"cash@square.com\",") + "\"zip code\":\"94043\"") + "}")));
            com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields fromJson = fromJson(com.squareup.moshi.AmplClassJsonAdapterTest.NamedFields.class, ("{" + ((("\"#\":[\"8005553333\"^,\"8005554444\"]," + "\"@\":\"cash@square.com\",") + "\"zip code\":\"94043\"") + "}")));
            org.assertj.core.api.Assertions.assertThat(fromJson.phoneNumbers).isEqualTo(java.util.Arrays.asList("8005553333", "8005554444"));
            org.assertj.core.api.Assertions.assertThat(fromJson.emailAddress).isEqualTo("cash@square.com");
            org.assertj.core.api.Assertions.assertThat(fromJson.zipCode).isEqualTo("94043");
            org.junit.Assert.fail("jsonAnnotationHonored_literalMutation75530 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }
}

