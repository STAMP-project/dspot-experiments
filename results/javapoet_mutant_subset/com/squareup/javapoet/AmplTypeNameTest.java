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
    protected <E extends java.lang.Enum<E>> E generic(E[] values) {
        return values[0];
    }

    protected static class TestGeneric<T> {
        class Inner {        }

        class InnerGeneric<T2> {        }

        static class NestedNonGeneric {        }
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

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_cf140_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_65385 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_65383 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_65383.get(vc_65385);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.ClassName.get(java.util.List.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf140 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#genericType */
    /* amplification of com.squareup.javapoet.TypeNameTest#genericType_literalMutation1692 */
    /* amplification of com.squareup.javapoet.TypeNameTest#genericType_literalMutation1692_failAssert3_add1735 */
    @org.junit.Test(timeout = 10000)
    public void genericType_literalMutation1692_failAssert3_add1735_literalMutation2287() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method recursiveEnum = getClass().getDeclaredMethod("generic", java.lang.Enum[].class);
            com.squareup.javapoet.TypeName.get(recursiveEnum.getReturnType());
            com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(recursiveEnum.getParameterTypes()[0]);
            // AssertGenerator replace invocation
            com.squareup.javapoet.TypeName o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13 = // MethodCallAdder
com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).withoutAnnotations()).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).withoutAnnotations()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).withoutAnnotations()).box().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).withoutAnnotations()).withoutAnnotations().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).withoutAnnotations()).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).box()).withoutAnnotations().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).box()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).box()).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).box()).box().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).withoutAnnotations()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).box()).withoutAnnotations().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).withoutAnnotations()).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).box().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).withoutAnnotations()).box().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).withoutAnnotations().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).withoutAnnotations()).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).box()).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).withoutAnnotations()).withoutAnnotations().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).box().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).box()).box().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).box()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations()).withoutAnnotations().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).box()).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box()).box()).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).withoutAnnotations().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((com.squareup.javapoet.ArrayTypeName)o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13).box().equals(o_genericType_literalMutation1692_failAssert3_add1735_literalMutation2287__13));
            com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericParameterTypes()[1]);
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).contains("Enum");
            org.junit.Assert.fail("genericType_literalMutation1692 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2812_failAssert21() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_65463 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_65461 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_65461, vc_65463);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2812 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf3306_failAssert19() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_65601 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_65599 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_65599.get(vc_65601);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf3306 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

