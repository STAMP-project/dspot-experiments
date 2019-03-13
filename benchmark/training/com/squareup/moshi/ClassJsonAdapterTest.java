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


import ClassJsonAdapter.FACTORY;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SimpleTimeZone;
import org.junit.Assert;
import org.junit.Test;


public final class ClassJsonAdapterTest {
    private final Moshi moshi = new Moshi.Builder().build();

    static class BasicPizza {
        int diameter;

        boolean extraCheese;
    }

    @Test
    public void basicClassAdapter() throws Exception {
        ClassJsonAdapterTest.BasicPizza value = new ClassJsonAdapterTest.BasicPizza();
        value.diameter = 13;
        value.extraCheese = true;
        String toJson = toJson(ClassJsonAdapterTest.BasicPizza.class, value);
        assertThat(toJson).isEqualTo("{\"diameter\":13,\"extraCheese\":true}");
        ClassJsonAdapterTest.BasicPizza fromJson = fromJson(ClassJsonAdapterTest.BasicPizza.class, "{\"diameter\":13,\"extraCheese\":true}");
        assertThat(fromJson.diameter).isEqualTo(13);
        assertThat(fromJson.extraCheese).isTrue();
    }

    static class PrivateFieldsPizza {
        private String secretIngredient;
    }

    @Test
    public void privateFields() throws Exception {
        ClassJsonAdapterTest.PrivateFieldsPizza value = new ClassJsonAdapterTest.PrivateFieldsPizza();
        value.secretIngredient = "vodka";
        String toJson = toJson(ClassJsonAdapterTest.PrivateFieldsPizza.class, value);
        assertThat(toJson).isEqualTo("{\"secretIngredient\":\"vodka\"}");
        ClassJsonAdapterTest.PrivateFieldsPizza fromJson = fromJson(ClassJsonAdapterTest.PrivateFieldsPizza.class, "{\"secretIngredient\":\"vodka\"}");
        assertThat(fromJson.secretIngredient).isEqualTo("vodka");
    }

    static class BasePizza {
        int diameter;
    }

    static class DessertPizza extends ClassJsonAdapterTest.BasePizza {
        boolean chocolate;
    }

    @Test
    public void typeHierarchy() throws Exception {
        ClassJsonAdapterTest.DessertPizza value = new ClassJsonAdapterTest.DessertPizza();
        value.diameter = 13;
        value.chocolate = true;
        String toJson = toJson(ClassJsonAdapterTest.DessertPizza.class, value);
        assertThat(toJson).isEqualTo("{\"chocolate\":true,\"diameter\":13}");
        ClassJsonAdapterTest.DessertPizza fromJson = fromJson(ClassJsonAdapterTest.DessertPizza.class, "{\"diameter\":13,\"chocolate\":true}");
        assertThat(fromJson.diameter).isEqualTo(13);
        assertThat(fromJson.chocolate).isTrue();
    }

    static class BaseAbcde {
        int d;

        int a;

        int c;
    }

    static class ExtendsBaseAbcde extends ClassJsonAdapterTest.BaseAbcde {
        int b;

        int e;
    }

    @Test
    public void fieldsAreAlphabeticalAcrossFlattenedHierarchy() throws Exception {
        ClassJsonAdapterTest.ExtendsBaseAbcde value = new ClassJsonAdapterTest.ExtendsBaseAbcde();
        value.a = 4;
        value.b = 5;
        value.c = 6;
        value.d = 7;
        value.e = 8;
        String toJson = toJson(ClassJsonAdapterTest.ExtendsBaseAbcde.class, value);
        assertThat(toJson).isEqualTo("{\"a\":4,\"b\":5,\"c\":6,\"d\":7,\"e\":8}");
        ClassJsonAdapterTest.ExtendsBaseAbcde fromJson = fromJson(ClassJsonAdapterTest.ExtendsBaseAbcde.class, "{\"a\":4,\"b\":5,\"c\":6,\"d\":7,\"e\":8}");
        assertThat(fromJson.a).isEqualTo(4);
        assertThat(fromJson.b).isEqualTo(5);
        assertThat(fromJson.c).isEqualTo(6);
        assertThat(fromJson.d).isEqualTo(7);
        assertThat(fromJson.e).isEqualTo(8);
    }

    static class StaticFields {
        static int a = 11;

        int b;
    }

    @Test
    public void staticFieldsOmitted() throws Exception {
        ClassJsonAdapterTest.StaticFields value = new ClassJsonAdapterTest.StaticFields();
        value.b = 12;
        String toJson = toJson(ClassJsonAdapterTest.StaticFields.class, value);
        assertThat(toJson).isEqualTo("{\"b\":12}");
        ClassJsonAdapterTest.StaticFields fromJson = fromJson(ClassJsonAdapterTest.StaticFields.class, "{\"a\":13,\"b\":12}");
        assertThat(ClassJsonAdapterTest.StaticFields.a).isEqualTo(11);// Unchanged.

        assertThat(fromJson.b).isEqualTo(12);
    }

    static class TransientFields {
        transient int a;

        int b;
    }

    @Test
    public void transientFieldsOmitted() throws Exception {
        ClassJsonAdapterTest.TransientFields value = new ClassJsonAdapterTest.TransientFields();
        value.a = 11;
        value.b = 12;
        String toJson = toJson(ClassJsonAdapterTest.TransientFields.class, value);
        assertThat(toJson).isEqualTo("{\"b\":12}");
        ClassJsonAdapterTest.TransientFields fromJson = fromJson(ClassJsonAdapterTest.TransientFields.class, "{\"a\":13,\"b\":12}");
        assertThat(fromJson.a).isEqualTo(0);// Not assigned.

        assertThat(fromJson.b).isEqualTo(12);
    }

    static class BaseA {
        int a;
    }

    static class ExtendsBaseA extends ClassJsonAdapterTest.BaseA {
        int a;
    }

    @Test
    public void fieldNameCollision() throws Exception {
        try {
            FACTORY.create(ClassJsonAdapterTest.ExtendsBaseA.class, NO_ANNOTATIONS, moshi);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("Conflicting fields:\n" + ("    int com.squareup.moshi.ClassJsonAdapterTest$ExtendsBaseA.a\n" + "    int com.squareup.moshi.ClassJsonAdapterTest$BaseA.a")));
        }
    }

    static class NameCollision {
        String foo;

        @Json(name = "foo")
        String bar;
    }

    @Test
    public void jsonAnnotationNameCollision() throws Exception {
        try {
            FACTORY.create(ClassJsonAdapterTest.NameCollision.class, NO_ANNOTATIONS, moshi);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("Conflicting fields:\n" + ("    java.lang.String com.squareup.moshi.ClassJsonAdapterTest$NameCollision.foo\n" + "    java.lang.String com.squareup.moshi.ClassJsonAdapterTest$NameCollision.bar")));
        }
    }

    static class TransientBaseA {
        transient int a;
    }

    static class ExtendsTransientBaseA extends ClassJsonAdapterTest.TransientBaseA {
        int a;
    }

    @Test
    public void fieldNameCollisionWithTransientFieldIsOkay() throws Exception {
        ClassJsonAdapterTest.ExtendsTransientBaseA value = new ClassJsonAdapterTest.ExtendsTransientBaseA();
        value.a = 11;
        ((ClassJsonAdapterTest.TransientBaseA) (value)).a = 12;
        String toJson = toJson(ClassJsonAdapterTest.ExtendsTransientBaseA.class, value);
        assertThat(toJson).isEqualTo("{\"a\":11}");
        ClassJsonAdapterTest.ExtendsTransientBaseA fromJson = fromJson(ClassJsonAdapterTest.ExtendsTransientBaseA.class, "{\"a\":11}");
        assertThat(fromJson.a).isEqualTo(11);
        assertThat(((ClassJsonAdapterTest.TransientBaseA) (fromJson)).a).isEqualTo(0);// Not assigned.

    }

    static class NoArgConstructor {
        int a;

        int b;

        NoArgConstructor() {
            a = 5;
        }
    }

    @Test
    public void noArgConstructor() throws Exception {
        ClassJsonAdapterTest.NoArgConstructor fromJson = fromJson(ClassJsonAdapterTest.NoArgConstructor.class, "{\"b\":8}");
        assertThat(fromJson.a).isEqualTo(5);
        assertThat(fromJson.b).isEqualTo(8);
    }

    static class NoArgConstructorThrowsCheckedException {
        NoArgConstructorThrowsCheckedException() throws Exception {
            throw new Exception("foo");
        }
    }

    @Test
    public void noArgConstructorThrowsCheckedException() throws Exception {
        try {
            fromJson(ClassJsonAdapterTest.NoArgConstructorThrowsCheckedException.class, "{}");
            Assert.fail();
        } catch (RuntimeException expected) {
            assertThat(expected.getCause()).hasMessage("foo");
        }
    }

    static class NoArgConstructorThrowsUncheckedException {
        NoArgConstructorThrowsUncheckedException() throws Exception {
            throw new UnsupportedOperationException("foo");
        }
    }

    @Test
    public void noArgConstructorThrowsUncheckedException() throws Exception {
        try {
            fromJson(ClassJsonAdapterTest.NoArgConstructorThrowsUncheckedException.class, "{}");
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
            assertThat(expected).hasMessage("foo");
        }
    }

    static class NoArgConstructorWithDefaultField {
        int a = 5;

        int b;
    }

    @Test
    public void noArgConstructorFieldDefaultsHonored() throws Exception {
        ClassJsonAdapterTest.NoArgConstructorWithDefaultField fromJson = fromJson(ClassJsonAdapterTest.NoArgConstructorWithDefaultField.class, "{\"b\":8}");
        assertThat(fromJson.a).isEqualTo(5);
        assertThat(fromJson.b).isEqualTo(8);
    }

    static class MagicConstructor {
        int a;

        public MagicConstructor(Void argument) {
            throw new AssertionError();
        }
    }

    @Test
    public void magicConstructor() throws Exception {
        ClassJsonAdapterTest.MagicConstructor fromJson = fromJson(ClassJsonAdapterTest.MagicConstructor.class, "{\"a\":8}");
        assertThat(fromJson.a).isEqualTo(8);
    }

    static class MagicConstructorWithDefaultField {
        int a = 5;

        int b;

        public MagicConstructorWithDefaultField(Void argument) {
            throw new AssertionError();
        }
    }

    @Test
    public void magicConstructorFieldDefaultsNotHonored() throws Exception {
        ClassJsonAdapterTest.MagicConstructorWithDefaultField fromJson = fromJson(ClassJsonAdapterTest.MagicConstructorWithDefaultField.class, "{\"b\":3}");
        assertThat(fromJson.a).isEqualTo(0);// Surprising! No value is assigned.

        assertThat(fromJson.b).isEqualTo(3);
    }

    static class NullRootObject {
        int a;
    }

    @Test
    public void nullRootObject() throws Exception {
        String toJson = toJson(ClassJsonAdapterTest.PrivateFieldsPizza.class, null);
        assertThat(toJson).isEqualTo("null");
        ClassJsonAdapterTest.NullRootObject fromJson = fromJson(ClassJsonAdapterTest.NullRootObject.class, "null");
        assertThat(fromJson).isNull();
    }

    static class NullFieldValue {
        String a = "not null";
    }

    @Test
    public void nullFieldValues() throws Exception {
        ClassJsonAdapterTest.NullFieldValue value = new ClassJsonAdapterTest.NullFieldValue();
        value.a = null;
        String toJson = toJson(ClassJsonAdapterTest.NullFieldValue.class, value);
        assertThat(toJson).isEqualTo("{\"a\":null}");
        ClassJsonAdapterTest.NullFieldValue fromJson = fromJson(ClassJsonAdapterTest.NullFieldValue.class, "{\"a\":null}");
        assertThat(fromJson.a).isNull();
    }

    class NonStatic {}

    @Test
    public void nonStaticNestedClassNotSupported() throws Exception {
        try {
            FACTORY.create(ClassJsonAdapterTest.NonStatic.class, NO_ANNOTATIONS, moshi);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("Cannot serialize non-static nested class " + "com.squareup.moshi.ClassJsonAdapterTest$NonStatic"));
        }
    }

    @Test
    public void anonymousClassNotSupported() throws Exception {
        Comparator<Object> c = new Comparator<Object>() {
            @Override
            public int compare(Object a, Object b) {
                return 0;
            }
        };
        try {
            FACTORY.create(c.getClass(), NO_ANNOTATIONS, moshi);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("Cannot serialize anonymous class " + (c.getClass().getName())));
        }
    }

    @Test
    public void localClassNotSupported() throws Exception {
        class Local {}
        try {
            FACTORY.create(Local.class, NO_ANNOTATIONS, moshi);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("Cannot serialize local class " + "com.squareup.moshi.ClassJsonAdapterTest$1Local"));
        }
    }

    interface Interface {}

    @Test
    public void interfaceNotSupported() throws Exception {
        assertThat(FACTORY.create(ClassJsonAdapterTest.Interface.class, NO_ANNOTATIONS, moshi)).isNull();
    }

    abstract static class Abstract {}

    @Test
    public void abstractClassNotSupported() throws Exception {
        try {
            FACTORY.create(ClassJsonAdapterTest.Abstract.class, NO_ANNOTATIONS, moshi);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("Cannot serialize abstract class " + "com.squareup.moshi.ClassJsonAdapterTest$Abstract"));
        }
    }

    static class ExtendsPlatformClassWithPrivateField extends SimpleTimeZone {
        int a;

        public ExtendsPlatformClassWithPrivateField() {
            super(0, "FOO");
        }
    }

    @Test
    public void platformSuperclassPrivateFieldIsExcluded() throws Exception {
        ClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField value = new ClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField();
        value.a = 4;
        String toJson = toJson(ClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField.class, value);
        assertThat(toJson).isEqualTo("{\"a\":4}");
        ClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField fromJson = fromJson(ClassJsonAdapterTest.ExtendsPlatformClassWithPrivateField.class, "{\"a\":4,\"ID\":\"BAR\"}");
        assertThat(fromJson.a).isEqualTo(4);
        assertThat(fromJson.getID()).isEqualTo("FOO");
    }

    static class ExtendsPlatformClassWithProtectedField extends ByteArrayOutputStream {
        int a;

        public ExtendsPlatformClassWithProtectedField() {
            super(2);
        }
    }

    @Test
    public void platformSuperclassProtectedFieldIsIncluded() throws Exception {
        ClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField value = new ClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField();
        value.a = 4;
        value.write(5);
        value.write(6);
        String toJson = toJson(ClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField.class, value);
        assertThat(toJson).isEqualTo("{\"a\":4,\"buf\":[5,6],\"count\":2}");
        ClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField fromJson = fromJson(ClassJsonAdapterTest.ExtendsPlatformClassWithProtectedField.class, "{\"a\":4,\"buf\":[5,6],\"count\":2}");
        assertThat(fromJson.a).isEqualTo(4);
        assertThat(fromJson.toByteArray()).contains(((byte) (5)), ((byte) (6)));
    }

    static class NamedFields {
        @Json(name = "#")
        List<String> phoneNumbers;

        @Json(name = "@")
        String emailAddress;

        @Json(name = "zip code")
        String zipCode;
    }

    @Test
    public void jsonAnnotationHonored() throws Exception {
        ClassJsonAdapterTest.NamedFields value = new ClassJsonAdapterTest.NamedFields();
        value.phoneNumbers = Arrays.asList("8005553333", "8005554444");
        value.emailAddress = "cash@square.com";
        value.zipCode = "94043";
        String toJson = toJson(ClassJsonAdapterTest.NamedFields.class, value);
        assertThat(toJson).isEqualTo(("{" + ((("\"#\":[\"8005553333\",\"8005554444\"]," + "\"@\":\"cash@square.com\",") + "\"zip code\":\"94043\"") + "}")));
        ClassJsonAdapterTest.NamedFields fromJson = fromJson(ClassJsonAdapterTest.NamedFields.class, ("{" + ((("\"#\":[\"8005553333\",\"8005554444\"]," + "\"@\":\"cash@square.com\",") + "\"zip code\":\"94043\"") + "}")));
        assertThat(fromJson.phoneNumbers).isEqualTo(Arrays.asList("8005553333", "8005554444"));
        assertThat(fromJson.emailAddress).isEqualTo("cash@square.com");
        assertThat(fromJson.zipCode).isEqualTo("94043");
    }

    static final class Box<T> {
        final T data;

        Box(T data) {
            this.data = data;
        }
    }

    @Test
    public void parameterizedType() throws Exception {
        @SuppressWarnings("unchecked")
        JsonAdapter<ClassJsonAdapterTest.Box<Integer>> adapter = ((JsonAdapter<ClassJsonAdapterTest.Box<Integer>>) (FACTORY.create(Types.newParameterizedTypeWithOwner(ClassJsonAdapterTest.class, ClassJsonAdapterTest.Box.class, Integer.class), NO_ANNOTATIONS, moshi)));
        assertThat(adapter.fromJson("{\"data\":5}").data).isEqualTo(5);
        assertThat(adapter.toJson(new ClassJsonAdapterTest.Box(5))).isEqualTo("{\"data\":5}");
    }
}

