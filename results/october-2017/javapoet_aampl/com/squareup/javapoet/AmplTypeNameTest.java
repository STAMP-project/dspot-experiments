/**
 * Copyright (C) 2015 Square, Inc.
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
package com.squareup.javapoet;


public class AmplTypeNameTest {
    protected static class TestGeneric<T> {
        class Inner {}

        class InnerGeneric<T2> {}

        static class NestedNonGeneric {}
    }

    protected <E extends java.lang.Enum<E>> E generic(E[] values) {
        return values[0];
    }

    protected static com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.String>.Inner testGenericStringInner() {
        return null;
    }

    protected static com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Integer>.Inner testGenericIntInner() {
        return null;
    }

    protected static com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long> testGenericInnerLong() {
        return null;
    }

    protected static com.squareup.javapoet.AmplTypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer> testGenericInnerInt() {
        return null;
    }

    protected static com.squareup.javapoet.AmplTypeNameTest.TestGeneric.NestedNonGeneric testNestedNonGeneric() {
        return null;
    }

    @org.junit.Test
    public void genericType() throws java.lang.Exception {
        java.lang.reflect.Method recursiveEnum = getClass().getDeclaredMethod("generic", java.lang.Enum[].class);
        com.squareup.javapoet.TypeName.get(recursiveEnum.getReturnType());
        com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(recursiveEnum.getParameterTypes()[0]);
        com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).contains("Enum");
    }

    @org.junit.Test
    public void innerClassInGenericType() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    @org.junit.Test
    public void innerGenericInGenericType() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    @org.junit.Test
    public void innerStaticInGenericType() throws java.lang.Exception {
        java.lang.reflect.Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        com.squareup.javapoet.TypeName.get(staticInGeneric.getReturnType());
        com.squareup.javapoet.TypeName typeName = com.squareup.javapoet.TypeName.get(staticInGeneric.getGenericReturnType());
        // Make sure there are no generic arguments
        com.google.common.truth.Truth.assertThat(typeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric"));
    }

    @org.junit.Test
    public void equalsAndHashCodePrimitive() {
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.BOOLEAN, com.squareup.javapoet.TypeName.BOOLEAN);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.BYTE, com.squareup.javapoet.TypeName.BYTE);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.CHAR, com.squareup.javapoet.TypeName.CHAR);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.DOUBLE, com.squareup.javapoet.TypeName.DOUBLE);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.FLOAT, com.squareup.javapoet.TypeName.FLOAT);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.INT, com.squareup.javapoet.TypeName.INT);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.LONG, com.squareup.javapoet.TypeName.LONG);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.SHORT, com.squareup.javapoet.TypeName.SHORT);
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.VOID, com.squareup.javapoet.TypeName.VOID);
    }

    @org.junit.Test
    public void equalsAndHashCodeArrayTypeName() {
        assertEqualsHashCodeAndToString(com.squareup.javapoet.ArrayTypeName.of(java.lang.Object.class), com.squareup.javapoet.ArrayTypeName.of(java.lang.Object.class));
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.get(java.lang.Object[].class), com.squareup.javapoet.ArrayTypeName.of(java.lang.Object.class));
    }

    @org.junit.Test
    public void equalsAndHashCodeClassName() {
        assertEqualsHashCodeAndToString(com.squareup.javapoet.ClassName.get(java.lang.Object.class), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.get(java.lang.Object.class), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
        assertEqualsHashCodeAndToString(com.squareup.javapoet.ClassName.bestGuess("java.lang.Object"), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
    }

    @org.junit.Test
    public void equalsAndHashCodeParameterizedTypeName() {
        assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
        assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.ClassName.get(java.util.List.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class));
    }

    @org.junit.Test
    public void equalsAndHashCodeTypeVariableName() {
        assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class), com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class));
        com.squareup.javapoet.TypeVariableName typeVar1 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
        com.squareup.javapoet.TypeVariableName typeVar2 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
        assertEqualsHashCodeAndToString(typeVar1, typeVar2);
    }

    @org.junit.Test
    public void equalsAndHashCodeWildcardTypeName() {
        assertEqualsHashCodeAndToString(com.squareup.javapoet.WildcardTypeName.subtypeOf(java.lang.Object.class), com.squareup.javapoet.WildcardTypeName.subtypeOf(java.lang.Object.class));
        assertEqualsHashCodeAndToString(com.squareup.javapoet.WildcardTypeName.subtypeOf(java.io.Serializable.class), com.squareup.javapoet.WildcardTypeName.subtypeOf(java.io.Serializable.class));
        assertEqualsHashCodeAndToString(com.squareup.javapoet.WildcardTypeName.supertypeOf(java.lang.String.class), com.squareup.javapoet.WildcardTypeName.supertypeOf(java.lang.String.class));
    }

    @org.junit.Test
    public void isPrimitive() throws java.lang.Exception {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isPrimitive()).isTrue();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Integer").isPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "String").isPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
    }

    @org.junit.Test
    public void isBoxedPrimitive() throws java.lang.Exception {
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isBoxedPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Integer").isBoxedPrimitive()).isTrue();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "String").isBoxedPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isBoxedPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isBoxedPrimitive()).isFalse();
    }

    private void assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName a, com.squareup.javapoet.TypeName b) {
        org.junit.Assert.assertEquals(a.toString(), b.toString());
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}

