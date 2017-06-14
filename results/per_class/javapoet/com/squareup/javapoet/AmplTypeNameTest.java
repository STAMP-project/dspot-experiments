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

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeClassName */
    @org.junit.Test
    public void equalsAndHashCodeClassName_literalMutation39_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ClassName.get(java.lang.Object.class), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.get(java.lang.Object.class), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ClassName.bestGuess("GdhscbCS@!x*zH_,"), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            org.junit.Assert.fail("equalsAndHashCodeClassName_literalMutation39 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_cf277_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_42 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_40 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_40.get(vc_42);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.ClassName.get(java.util.List.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf277 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_cf281_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_48 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_46 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_44 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_44.get(vc_46, vc_48);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.ClassName.get(java.util.List.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf281 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_cf265_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_32 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_30 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_30.get(vc_32);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.ClassName.get(java.util.List.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf265 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2978_failAssert35() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_120 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_118 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_118, vc_120);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2978 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2999() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        int o_innerClassInGenericType_cf2999__9 = // StatementAdderMethod cloned existing statement
genericTypeName.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_innerClassInGenericType_cf2999__9, 409558280);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2950_failAssert30() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_110 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_108 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_106 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_106.get(vc_108, vc_110);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2950 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2891() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        boolean o_innerClassInGenericType_cf2891__9 = // StatementAdderMethod cloned existing statement
genericTypeName.isAnnotated();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_innerClassInGenericType_cf2891__9);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2939() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        com.squareup.javapoet.TypeName o_innerClassInGenericType_cf2939__9 = // StatementAdderMethod cloned existing statement
genericTypeName.box();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerClassInGenericType_cf2939__9).isBoxedPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_innerClassInGenericType_cf2939__9.equals(genericTypeName));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerClassInGenericType_cf2939__9).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerClassInGenericType_cf2939__9).isAnnotated());
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2968_failAssert33() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_114 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_114);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2968 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3008_failAssert42() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_132 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.concatAnnotations(vc_132);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf3008 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2884() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_74 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_74);
        // AssertGenerator replace invocation
        boolean o_innerClassInGenericType_cf2884__11 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_74);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_innerClassInGenericType_cf2884__11);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2991_failAssert37() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderMethod cloned existing statement
            genericTypeName.unbox();
            // MethodAssertGenerator build local variable
            Object o_11_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2991 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2904_failAssert21() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_84 = (com.squareup.javapoet.CodeWriter)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_84);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2904 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2912_cf4116_failAssert57() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_88 = (com.squareup.javapoet.CodeWriter)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_88;
            // AssertGenerator replace invocation
            com.squareup.javapoet.CodeWriter o_innerClassInGenericType_cf2912__11 = // StatementAdderMethod cloned existing statement
genericTypeName.emitAnnotations(vc_88);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerClassInGenericType_cf2912__11;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_510 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_510.isBoxedPrimitive();
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2912_cf4116 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3006_failAssert41_literalMutation5433() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isPrimitive());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_132 = (java.util.List)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_132);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_130 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_130);
            // StatementAdderMethod cloned existing statement
            vc_130.concatAnnotations(vc_132);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.InOner"));
            org.junit.Assert.fail("innerClassInGenericType_cf3006 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2895_cf3854_failAssert14() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            boolean o_innerClassInGenericType_cf2895__9 = // StatementAdderMethod cloned existing statement
genericTypeName.isBoxedPrimitive();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerClassInGenericType_cf2895__9;
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_408 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_406 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_406, vc_408);
            // MethodAssertGenerator build local variable
            Object o_19_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2895_cf3854 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3003_cf4677_failAssert33() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            java.lang.String o_innerClassInGenericType_cf3003__9 = // StatementAdderMethod cloned existing statement
genericTypeName.toString();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerClassInGenericType_cf3003__9;
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_744 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.annotated(vc_744);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf3003_cf4677 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2912_cf4174_failAssert60() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_88 = (com.squareup.javapoet.CodeWriter)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_88;
            // AssertGenerator replace invocation
            com.squareup.javapoet.CodeWriter o_innerClassInGenericType_cf2912__11 = // StatementAdderMethod cloned existing statement
genericTypeName.emitAnnotations(vc_88);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerClassInGenericType_cf2912__11;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_536 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_536);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2912_cf4174 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2899_cf4017_failAssert51() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            boolean o_innerClassInGenericType_cf2899__9 = // StatementAdderMethod cloned existing statement
genericTypeName.isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerClassInGenericType_cf2899__9;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_474 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_474);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2899_cf4017 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3006_failAssert41_literalMutation5433_cf5617_failAssert28() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_15_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
                // MethodAssertGenerator build local variable
                Object o_9_0 = ((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isAnnotated();
                // MethodAssertGenerator build local variable
                Object o_11_0 = ((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isBoxedPrimitive();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isPrimitive();
                java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
                com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
                com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
                // StatementAdderOnAssert create null value
                java.util.List<com.squareup.javapoet.AnnotationSpec> vc_132 = (java.util.List)null;
                // MethodAssertGenerator build local variable
                Object o_25_0 = vc_132;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_130 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_830 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_828 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_826 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_826.get(vc_828, vc_830);
                // MethodAssertGenerator build local variable
                Object o_37_0 = vc_130;
                // StatementAdderMethod cloned existing statement
                vc_130.concatAnnotations(vc_132);
                // MethodAssertGenerator build local variable
                Object o_15_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
                // Make sure the generic argument is present
                com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.InOner"));
                org.junit.Assert.fail("innerClassInGenericType_cf3006 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("innerClassInGenericType_cf3006_failAssert41_literalMutation5433_cf5617 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6846_failAssert30() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_902 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_900 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_898 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_898.get(vc_900, vc_902);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6846 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6887_failAssert37() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderMethod cloned existing statement
            genericTypeName.unbox();
            // MethodAssertGenerator build local variable
            Object o_11_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6887 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6780() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_866 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_866);
        // AssertGenerator replace invocation
        boolean o_innerGenericInGenericType_cf6780__11 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_866);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_innerGenericInGenericType_cf6780__11);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6895() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        int o_innerGenericInGenericType_cf6895__9 = // StatementAdderMethod cloned existing statement
genericTypeName.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_innerGenericInGenericType_cf6895__9, -302376430);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6870_failAssert34() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_912 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_910 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_908 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_908.get(vc_910, vc_912);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6870 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6904_failAssert42() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_924 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.concatAnnotations(vc_924);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6904 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6864_failAssert33() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_906 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_906);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6864 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6835() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        com.squareup.javapoet.TypeName o_innerGenericInGenericType_cf6835__9 = // StatementAdderMethod cloned existing statement
genericTypeName.box();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf6835__9).isAnnotated());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_innerGenericInGenericType_cf6835__9.equals(genericTypeName));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf6835__9).isBoxedPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf6835__9).isPrimitive());
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6800_failAssert21() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_876 = (com.squareup.javapoet.CodeWriter)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_876);
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6800 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6781_cf7304_failAssert14() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_867 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_innerGenericInGenericType_cf6781__11 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_867);
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_innerGenericInGenericType_cf6781__11;
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_1046 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_1044 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_1042 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_1042.get(vc_1044, vc_1046);
            // MethodAssertGenerator build local variable
            Object o_23_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6781_cf7304 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6780_cf7010_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.lang.Object vc_866 = (java.lang.Object)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_866;
            // AssertGenerator replace invocation
            boolean o_innerGenericInGenericType_cf6780__11 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_866);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerGenericInGenericType_cf6780__11;
            // StatementAdderOnAssert create null value
            java.util.List<com.squareup.javapoet.AnnotationSpec> vc_960 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.annotated(vc_960);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6780_cf7010 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6808_cf8033_failAssert9() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_880 = (com.squareup.javapoet.CodeWriter)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_880;
            // AssertGenerator replace invocation
            com.squareup.javapoet.CodeWriter o_innerGenericInGenericType_cf6808__11 = // StatementAdderMethod cloned existing statement
genericTypeName.emitAnnotations(vc_880);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerGenericInGenericType_cf6808__11;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_1310 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_1310.emitAnnotations(vc_880);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6808_cf8033 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6895_cf8440_failAssert47() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // MethodAssertGenerator build local variable
            Object o_11_1 = -302376430;
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            int o_innerGenericInGenericType_cf6895__9 = // StatementAdderMethod cloned existing statement
genericTypeName.hashCode();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerGenericInGenericType_cf6895__9;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_1482 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_1482);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6895_cf8440 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6850_failAssert31_literalMutation9150() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isPrimitive());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_902 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_902);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_900 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_900);
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_900, vc_902);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "c%>ct-g/><(;FkQj(!%3b`58ZE3#sHdd*?(,MAonC[6(hn"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6850 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6850_failAssert31_literalMutation9150_cf9549_failAssert37() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_15_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
                // MethodAssertGenerator build local variable
                Object o_9_0 = ((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isAnnotated();
                // MethodAssertGenerator build local variable
                Object o_11_0 = ((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isBoxedPrimitive();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((com.squareup.javapoet.ParameterizedTypeName)o_15_1).isPrimitive();
                java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
                com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
                com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_902 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_25_0 = vc_902;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_900 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_1626 = (javax.lang.model.type.TypeMirror)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_1624 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_1624.get(vc_1626);
                // MethodAssertGenerator build local variable
                Object o_35_0 = vc_900;
                // StatementAdderMethod cloned existing statement
                genericTypeName.get(vc_900, vc_902);
                // MethodAssertGenerator build local variable
                Object o_15_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
                // Make sure the generic argument is present
                com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "c%>ct-g/><(;FkQj(!%3b`58ZE3#sHdd*?(,MAonC[6(hn"));
                org.junit.Assert.fail("innerGenericInGenericType_cf6850 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("innerGenericInGenericType_cf6850_failAssert31_literalMutation9150_cf9549 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6787_literalMutation7468_failAssert1_literalMutation9665() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_13_1).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_13_1).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_13_1).isAnnotated());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLtg");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            boolean o_innerGenericInGenericType_cf6787__9 = // StatementAdderMethod cloned existing statement
genericTypeName.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerGenericInGenericType_cf6787__9;
            // MethodAssertGenerator build local variable
            Object o_13_0 = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6787_literalMutation7468 should have thrown NoSuchMethodException");
        } catch (java.lang.NoSuchMethodException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isBoxedPrimitive */
    @org.junit.Test
    public void isBoxedPrimitive_literalMutation11194_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Int+ger").isBoxedPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "String").isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isBoxedPrimitive()).isFalse();
            org.junit.Assert.fail("isBoxedPrimitive_literalMutation11194 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isPrimitive */
    @org.junit.Test
    public void isPrimitive_literalMutation18408_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "|{M4fTk").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "String").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
            org.junit.Assert.fail("isPrimitive_literalMutation18408 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

