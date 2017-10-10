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


public final class AmplTypesTest {
    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.squareup.moshi.JsonQualifier
    @interface TestQualifier {    }

    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.squareup.moshi.JsonQualifier
    @interface AnotherTestQualifier {    }

    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @interface TestAnnotation {    }

    @com.squareup.moshi.AmplTypesTest.TestQualifier
    private java.lang.Object hasTestQualifier;

    @org.junit.Test
    public void nextAnnotationsRequiresJsonAnnotation() throws java.lang.Exception {
        try {
            com.squareup.moshi.Types.nextAnnotations(java.util.Collections.<java.lang.annotation.Annotation>emptySet(), com.squareup.moshi.AmplTypesTest.TestAnnotation.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("interface com.squareup.moshi.TypesTest$TestAnnotation is not a JsonQualifier.");
        }
    }

    @org.junit.Test
    public void nextAnnotationsDoesNotContainReturnsNull() throws java.lang.Exception {
        java.util.Set<? extends java.lang.annotation.Annotation> annotations = java.util.Collections.singleton(com.squareup.moshi.Types.createJsonQualifierImplementation(com.squareup.moshi.AmplTypesTest.AnotherTestQualifier.class));
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.nextAnnotations(annotations, com.squareup.moshi.AmplTypesTest.TestQualifier.class)).isNull();
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.nextAnnotations(java.util.Collections.<java.lang.annotation.Annotation>emptySet(), com.squareup.moshi.AmplTypesTest.TestQualifier.class)).isNull();
    }

    @org.junit.Test
    public void nextAnnotationsReturnsDelegateAnnotations() throws java.lang.Exception {
        java.util.Set<java.lang.annotation.Annotation> annotations = new java.util.LinkedHashSet<>(2);
        annotations.add(com.squareup.moshi.Types.createJsonQualifierImplementation(com.squareup.moshi.AmplTypesTest.TestQualifier.class));
        annotations.add(com.squareup.moshi.Types.createJsonQualifierImplementation(com.squareup.moshi.AmplTypesTest.AnotherTestQualifier.class));
        java.util.Set<com.squareup.moshi.AmplTypesTest.AnotherTestQualifier> expected = java.util.Collections.singleton(com.squareup.moshi.Types.createJsonQualifierImplementation(com.squareup.moshi.AmplTypesTest.AnotherTestQualifier.class));
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.nextAnnotations(java.util.Collections.unmodifiableSet(annotations), com.squareup.moshi.AmplTypesTest.TestQualifier.class)).isEqualTo(expected);
    }

    @org.junit.Test
    public void newParameterizedType() throws java.lang.Exception {
        // List<A>. List is a top-level class.
        java.lang.reflect.Type type = com.squareup.moshi.Types.newParameterizedType(java.util.List.class, com.squareup.moshi.AmplTypesTest.A.class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.AmplTypesTest.getFirstTypeArgument(type)).isEqualTo(com.squareup.moshi.AmplTypesTest.A.class);
        // A<B>. A is a static inner class.
        type = com.squareup.moshi.Types.newParameterizedTypeWithOwner(com.squareup.moshi.AmplTypesTest.class, com.squareup.moshi.AmplTypesTest.A.class, com.squareup.moshi.AmplTypesTest.B.class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.AmplTypesTest.getFirstTypeArgument(type)).isEqualTo(com.squareup.moshi.AmplTypesTest.B.class);
    }

    @org.junit.Test
    public void parameterizedTypeWithRequiredOwnerMissing() throws java.lang.Exception {
        try {
            com.squareup.moshi.Types.newParameterizedType(com.squareup.moshi.AmplTypesTest.A.class, com.squareup.moshi.AmplTypesTest.B.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage((("unexpected owner type for " + (com.squareup.moshi.AmplTypesTest.A.class)) + ": null"));
        }
    }

    @org.junit.Test
    public void parameterizedTypeWithUnnecessaryOwnerProvided() throws java.lang.Exception {
        try {
            com.squareup.moshi.Types.newParameterizedTypeWithOwner(com.squareup.moshi.AmplTypesTest.A.class, java.util.List.class, com.squareup.moshi.AmplTypesTest.B.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(((("unexpected owner type for " + (java.util.List.class)) + ": ") + (com.squareup.moshi.AmplTypesTest.A.class)));
        }
    }

    @org.junit.Test
    public void arrayOf() {
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.getRawType(com.squareup.moshi.Types.arrayOf(int.class))).isEqualTo(int[].class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.getRawType(com.squareup.moshi.Types.arrayOf(java.util.List.class))).isEqualTo(java.util.List[].class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.getRawType(com.squareup.moshi.Types.arrayOf(java.lang.String[].class))).isEqualTo(java.lang.String[][].class);
    }

    java.util.List<? extends java.lang.CharSequence> listSubtype;

    java.util.List<? super java.lang.String> listSupertype;

    @org.junit.Test
    public void subtypeOf() throws java.lang.Exception {
        java.lang.reflect.Type listOfWildcardType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("listSubtype").getGenericType();
        java.lang.reflect.Type expected = com.squareup.moshi.Types.collectionElementType(listOfWildcardType, java.util.List.class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.subtypeOf(java.lang.CharSequence.class)).isEqualTo(expected);
    }

    @org.junit.Test
    public void supertypeOf() throws java.lang.Exception {
        java.lang.reflect.Type listOfWildcardType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("listSupertype").getGenericType();
        java.lang.reflect.Type expected = com.squareup.moshi.Types.collectionElementType(listOfWildcardType, java.util.List.class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.supertypeOf(java.lang.String.class)).isEqualTo(expected);
    }

    @org.junit.Test
    public void getFirstTypeArgument() throws java.lang.Exception {
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.AmplTypesTest.getFirstTypeArgument(com.squareup.moshi.AmplTypesTest.A.class)).isNull();
        java.lang.reflect.Type type = com.squareup.moshi.Types.newParameterizedTypeWithOwner(com.squareup.moshi.AmplTypesTest.class, com.squareup.moshi.AmplTypesTest.A.class, com.squareup.moshi.AmplTypesTest.B.class, com.squareup.moshi.AmplTypesTest.C.class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.AmplTypesTest.getFirstTypeArgument(type)).isEqualTo(com.squareup.moshi.AmplTypesTest.B.class);
    }

    @org.junit.Test
    public void newParameterizedTypeObjectMethods() throws java.lang.Exception {
        java.lang.reflect.Type mapOfStringIntegerType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        java.lang.reflect.ParameterizedType newMapType = com.squareup.moshi.Types.newParameterizedType(java.util.Map.class, java.lang.String.class, java.lang.Integer.class);
        org.assertj.core.api.Assertions.assertThat(newMapType).isEqualTo(mapOfStringIntegerType);
        org.assertj.core.api.Assertions.assertThat(newMapType.hashCode()).isEqualTo(mapOfStringIntegerType.hashCode());
        org.assertj.core.api.Assertions.assertThat(newMapType.toString()).isEqualTo(mapOfStringIntegerType.toString());
        java.lang.reflect.Type arrayListOfMapOfStringIntegerType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("arrayListOfMapOfStringInteger").getGenericType();
        java.lang.reflect.ParameterizedType newListType = com.squareup.moshi.Types.newParameterizedType(java.util.ArrayList.class, newMapType);
        org.assertj.core.api.Assertions.assertThat(newListType).isEqualTo(arrayListOfMapOfStringIntegerType);
        org.assertj.core.api.Assertions.assertThat(newListType.hashCode()).isEqualTo(arrayListOfMapOfStringIntegerType.hashCode());
        org.assertj.core.api.Assertions.assertThat(newListType.toString()).isEqualTo(arrayListOfMapOfStringIntegerType.toString());
    }

    private static final class A {    }

    private static final class B {    }

    private static final class C {    }

    /**
     * Given a parameterized type {@code A<B, C>}, returns B. If the specified type is not a generic
     * type, returns null.
     */
    public static java.lang.reflect.Type getFirstTypeArgument(java.lang.reflect.Type type) throws java.lang.Exception {
        if (!(type instanceof java.lang.reflect.ParameterizedType))
            return null;
        
        java.lang.reflect.ParameterizedType ptype = ((java.lang.reflect.ParameterizedType) (type));
        java.lang.reflect.Type[] actualTypeArguments = ptype.getActualTypeArguments();
        if ((actualTypeArguments.length) == 0)
            return null;
        
        return com.squareup.moshi.Types.canonicalize(actualTypeArguments[0]);
    }

    java.util.Map<java.lang.String, java.lang.Integer> mapOfStringInteger;

    java.util.Map<java.lang.String, java.lang.Integer>[] arrayOfMapOfStringInteger;

    java.util.ArrayList<java.util.Map<java.lang.String, java.lang.Integer>> arrayListOfMapOfStringInteger;

    interface StringIntegerMap extends java.util.Map<java.lang.String, java.lang.Integer> {    }

    @org.junit.Test
    public void arrayComponentType() throws java.lang.Exception {
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.arrayComponentType(java.lang.String[][].class)).isEqualTo(java.lang.String[].class);
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.arrayComponentType(java.lang.String[].class)).isEqualTo(java.lang.String.class);
        java.lang.reflect.Type arrayOfMapOfStringIntegerType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("arrayOfMapOfStringInteger").getGenericType();
        java.lang.reflect.Type mapOfStringIntegerType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.arrayComponentType(arrayOfMapOfStringIntegerType)).isEqualTo(mapOfStringIntegerType);
    }

    @org.junit.Test
    public void collectionElementType() throws java.lang.Exception {
        java.lang.reflect.Type arrayListOfMapOfStringIntegerType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("arrayListOfMapOfStringInteger").getGenericType();
        java.lang.reflect.Type mapOfStringIntegerType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.collectionElementType(arrayListOfMapOfStringIntegerType, java.util.List.class)).isEqualTo(mapOfStringIntegerType);
    }

    @org.junit.Test
    public void mapKeyAndValueTypes() throws java.lang.Exception {
        java.lang.reflect.Type mapOfStringIntegerType = com.squareup.moshi.AmplTypesTest.class.getDeclaredField("mapOfStringInteger").getGenericType();
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.mapKeyAndValueTypes(mapOfStringIntegerType, java.util.Map.class)).containsExactly(java.lang.String.class, java.lang.Integer.class);
    }

    @org.junit.Test
    public void propertiesTypes() throws java.lang.Exception {
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.mapKeyAndValueTypes(java.util.Properties.class, java.util.Properties.class)).containsExactly(java.lang.String.class, java.lang.String.class);
    }

    @org.junit.Test
    public void fixedVariablesTypes() throws java.lang.Exception {
        org.assertj.core.api.Assertions.assertThat(com.squareup.moshi.Types.mapKeyAndValueTypes(com.squareup.moshi.AmplTypesTest.StringIntegerMap.class, com.squareup.moshi.AmplTypesTest.StringIntegerMap.class)).containsExactly(java.lang.String.class, java.lang.Integer.class);
    }

    // Explicitly checking for proxy implementation.
    @java.lang.SuppressWarnings(value = "GetClassOnAnnotation")
    @org.junit.Test
    public void createJsonQualifierImplementation() throws java.lang.Exception {
        com.squareup.moshi.AmplTypesTest.TestQualifier actual = com.squareup.moshi.Types.createJsonQualifierImplementation(com.squareup.moshi.AmplTypesTest.TestQualifier.class);
        com.squareup.moshi.AmplTypesTest.TestQualifier expected = ((com.squareup.moshi.AmplTypesTest.TestQualifier) (com.squareup.moshi.AmplTypesTest.class.getDeclaredField("hasTestQualifier").getAnnotations()[0]));
        org.assertj.core.api.Assertions.assertThat(actual.annotationType()).isEqualTo(com.squareup.moshi.AmplTypesTest.TestQualifier.class);
        org.assertj.core.api.Assertions.assertThat(actual).isEqualTo(expected);
        org.assertj.core.api.Assertions.assertThat(actual).isNotEqualTo(null);
        org.assertj.core.api.Assertions.assertThat(actual.hashCode()).isEqualTo(expected.hashCode());
        org.assertj.core.api.Assertions.assertThat(actual.getClass()).isNotEqualTo(com.squareup.moshi.AmplTypesTest.TestQualifier.class);
    }
}

