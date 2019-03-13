/**
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.moshi;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public final class TypesTest {
    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    @interface TestQualifier {}

    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    @interface AnotherTestQualifier {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {}

    @TypesTest.TestQualifier
    private Object hasTestQualifier;

    @Test
    public void nextAnnotationsRequiresJsonAnnotation() throws Exception {
        try {
            Types.nextAnnotations(Collections.<Annotation>emptySet(), TypesTest.TestAnnotation.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("interface com.squareup.moshi.TypesTest$TestAnnotation is not a JsonQualifier.");
        }
    }

    @Test
    public void nextAnnotationsDoesNotContainReturnsNull() throws Exception {
        Set<? extends Annotation> annotations = Collections.singleton(Types.createJsonQualifierImplementation(TypesTest.AnotherTestQualifier.class));
        assertThat(Types.nextAnnotations(annotations, TypesTest.TestQualifier.class)).isNull();
        assertThat(Types.nextAnnotations(Collections.<Annotation>emptySet(), TypesTest.TestQualifier.class)).isNull();
    }

    @Test
    public void nextAnnotationsReturnsDelegateAnnotations() throws Exception {
        Set<Annotation> annotations = new LinkedHashSet<>(2);
        annotations.add(Types.createJsonQualifierImplementation(TypesTest.TestQualifier.class));
        annotations.add(Types.createJsonQualifierImplementation(TypesTest.AnotherTestQualifier.class));
        Set<TypesTest.AnotherTestQualifier> expected = Collections.singleton(Types.createJsonQualifierImplementation(TypesTest.AnotherTestQualifier.class));
        assertThat(Types.nextAnnotations(Collections.unmodifiableSet(annotations), TypesTest.TestQualifier.class)).isEqualTo(expected);
    }

    @Test
    public void newParameterizedType() throws Exception {
        // List<A>. List is a top-level class.
        Type type = Types.newParameterizedType(List.class, TypesTest.A.class);
        assertThat(TypesTest.getFirstTypeArgument(type)).isEqualTo(TypesTest.A.class);
        // A<B>. A is a static inner class.
        type = Types.newParameterizedTypeWithOwner(TypesTest.class, TypesTest.A.class, TypesTest.B.class);
        assertThat(TypesTest.getFirstTypeArgument(type)).isEqualTo(TypesTest.B.class);
    }

    @Test
    public void parameterizedTypeWithRequiredOwnerMissing() throws Exception {
        try {
            Types.newParameterizedType(TypesTest.A.class, TypesTest.B.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage((("unexpected owner type for " + (TypesTest.A.class)) + ": null"));
        }
    }

    @Test
    public void parameterizedTypeWithUnnecessaryOwnerProvided() throws Exception {
        try {
            Types.newParameterizedTypeWithOwner(TypesTest.A.class, List.class, TypesTest.B.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(((("unexpected owner type for " + (List.class)) + ": ") + (TypesTest.A.class)));
        }
    }

    @Test
    public void parameterizedTypeWithIncorrectOwnerProvided() throws Exception {
        try {
            Types.newParameterizedTypeWithOwner(TypesTest.A.class, TypesTest.D.class, TypesTest.B.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(((("unexpected owner type for " + (TypesTest.D.class)) + ": ") + (TypesTest.A.class)));
        }
    }

    @Test
    public void arrayOf() {
        assertThat(Types.getRawType(Types.arrayOf(int.class))).isEqualTo(int[].class);
        assertThat(Types.getRawType(Types.arrayOf(List.class))).isEqualTo(List[].class);
        assertThat(Types.getRawType(Types.arrayOf(String[].class))).isEqualTo(String[][].class);
    }

    List<? extends CharSequence> listSubtype;

    List<? super String> listSupertype;

    @Test
    public void subtypeOf() throws Exception {
        Type listOfWildcardType = TypesTest.class.getDeclaredField("listSubtype").getGenericType();
        Type expected = Types.collectionElementType(listOfWildcardType, List.class);
        assertThat(Types.subtypeOf(CharSequence.class)).isEqualTo(expected);
    }

    @Test
    public void supertypeOf() throws Exception {
        Type listOfWildcardType = TypesTest.class.getDeclaredField("listSupertype").getGenericType();
        Type expected = Types.collectionElementType(listOfWildcardType, List.class);
        assertThat(Types.supertypeOf(String.class)).isEqualTo(expected);
    }

    @Test
    public void getFirstTypeArgument() throws Exception {
        assertThat(TypesTest.getFirstTypeArgument(TypesTest.A.class)).isNull();
        Type type = Types.newParameterizedTypeWithOwner(TypesTest.class, TypesTest.A.class, TypesTest.B.class, TypesTest.C.class);
        assertThat(TypesTest.getFirstTypeArgument(type)).isEqualTo(TypesTest.B.class);
    }

    @Test
    public void newParameterizedTypeObjectMethods() throws Exception {
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        ParameterizedType newMapType = Types.newParameterizedType(Map.class, String.class, Integer.class);
        assertThat(newMapType).isEqualTo(mapOfStringIntegerType);
        assertThat(newMapType.hashCode()).isEqualTo(mapOfStringIntegerType.hashCode());
        assertThat(newMapType.toString()).isEqualTo(mapOfStringIntegerType.toString());
        Type arrayListOfMapOfStringIntegerType = TypesTest.class.getDeclaredField("arrayListOfMapOfStringInteger").getGenericType();
        ParameterizedType newListType = Types.newParameterizedType(ArrayList.class, newMapType);
        assertThat(newListType).isEqualTo(arrayListOfMapOfStringIntegerType);
        assertThat(newListType.hashCode()).isEqualTo(arrayListOfMapOfStringIntegerType.hashCode());
        assertThat(newListType.toString()).isEqualTo(arrayListOfMapOfStringIntegerType.toString());
    }

    private static final class A {}

    private static final class B {}

    private static final class C {}

    private static final class D<T> {}

    Map<String, Integer> mapOfStringInteger;

    Map<String, Integer>[] arrayOfMapOfStringInteger;

    ArrayList<Map<String, Integer>> arrayListOfMapOfStringInteger;

    interface StringIntegerMap extends Map<String, Integer> {}

    @Test
    public void arrayComponentType() throws Exception {
        assertThat(Types.arrayComponentType(String[][].class)).isEqualTo(String[].class);
        assertThat(Types.arrayComponentType(String[].class)).isEqualTo(String.class);
        Type arrayOfMapOfStringIntegerType = TypesTest.class.getDeclaredField("arrayOfMapOfStringInteger").getGenericType();
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        assertThat(Types.arrayComponentType(arrayOfMapOfStringIntegerType)).isEqualTo(mapOfStringIntegerType);
    }

    @Test
    public void collectionElementType() throws Exception {
        Type arrayListOfMapOfStringIntegerType = TypesTest.class.getDeclaredField("arrayListOfMapOfStringInteger").getGenericType();
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        assertThat(Types.collectionElementType(arrayListOfMapOfStringIntegerType, List.class)).isEqualTo(mapOfStringIntegerType);
    }

    @Test
    public void mapKeyAndValueTypes() throws Exception {
        Type mapOfStringIntegerType = TypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        assertThat(Types.mapKeyAndValueTypes(mapOfStringIntegerType, Map.class)).containsExactly(String.class, Integer.class);
    }

    @Test
    public void propertiesTypes() throws Exception {
        assertThat(Types.mapKeyAndValueTypes(Properties.class, Properties.class)).containsExactly(String.class, String.class);
    }

    @Test
    public void fixedVariablesTypes() throws Exception {
        assertThat(Types.mapKeyAndValueTypes(TypesTest.StringIntegerMap.class, TypesTest.StringIntegerMap.class)).containsExactly(String.class, Integer.class);
    }

    // Explicitly checking for proxy implementation.
    @SuppressWarnings("GetClassOnAnnotation")
    @Test
    public void createJsonQualifierImplementation() throws Exception {
        TypesTest.TestQualifier actual = Types.createJsonQualifierImplementation(TypesTest.TestQualifier.class);
        TypesTest.TestQualifier expected = ((TypesTest.TestQualifier) (TypesTest.class.getDeclaredField("hasTestQualifier").getAnnotations()[0]));
        assertThat(actual.annotationType()).isEqualTo(TypesTest.TestQualifier.class);
        assertThat(actual).isEqualTo(expected);
        assertThat(actual).isNotEqualTo(null);
        assertThat(actual.hashCode()).isEqualTo(expected.hashCode());
        assertThat(actual.getClass()).isNotEqualTo(TypesTest.TestQualifier.class);
    }

    @Test
    public void arrayEqualsGenericTypeArray() {
        assertThat(Types.equals(int[].class, Types.arrayOf(int.class))).isTrue();
        assertThat(Types.equals(Types.arrayOf(int.class), int[].class)).isTrue();
        assertThat(Types.equals(String[].class, Types.arrayOf(String.class))).isTrue();
        assertThat(Types.equals(Types.arrayOf(String.class), String[].class)).isTrue();
    }

    @Test
    public void parameterizedAndWildcardTypesCannotHavePrimitiveArguments() throws Exception {
        try {
            Types.newParameterizedType(List.class, int.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Unexpected primitive int. Use the boxed type.");
        }
        try {
            Types.subtypeOf(byte.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Unexpected primitive byte. Use the boxed type.");
        }
        try {
            Types.subtypeOf(boolean.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Unexpected primitive boolean. Use the boxed type.");
        }
    }

    @Test
    public void getFieldJsonQualifierAnnotations_privateFieldTest() {
        Set<? extends Annotation> annotations = Types.getFieldJsonQualifierAnnotations(TypesTest.ClassWithAnnotatedFields.class, "privateField");
        assertThat(annotations).hasSize(1);
        assertThat(annotations.iterator().next()).isInstanceOf(TypesTest.FieldAnnotation.class);
    }

    @Test
    public void getFieldJsonQualifierAnnotations_publicFieldTest() {
        Set<? extends Annotation> annotations = Types.getFieldJsonQualifierAnnotations(TypesTest.ClassWithAnnotatedFields.class, "publicField");
        assertThat(annotations).hasSize(1);
        assertThat(annotations.iterator().next()).isInstanceOf(TypesTest.FieldAnnotation.class);
    }

    @Test
    public void getFieldJsonQualifierAnnotations_unannotatedTest() {
        Set<? extends Annotation> annotations = Types.getFieldJsonQualifierAnnotations(TypesTest.ClassWithAnnotatedFields.class, "unannotatedField");
        assertThat(annotations).hasSize(0);
    }

    @JsonQualifier
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FieldAnnotation {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NoQualifierAnnotation {}

    static class ClassWithAnnotatedFields {
        @TypesTest.FieldAnnotation
        @TypesTest.NoQualifierAnnotation
        private final int privateField = 0;

        @TypesTest.FieldAnnotation
        @TypesTest.NoQualifierAnnotation
        public final int publicField = 0;

        private final int unannotatedField = 0;
    }
}

