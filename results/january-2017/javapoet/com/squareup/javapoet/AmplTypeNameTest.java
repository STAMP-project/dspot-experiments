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

    protected static com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.String>.Inner testGenericStringInner() {
        return null;
    }

    protected static com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Integer>.Inner testGenericIntInner() {
        return null;
    }

    protected static com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Long> testGenericInnerLong() {
        return null;
    }

    protected static com.squareup.javapoet.TypeNameTest.TestGeneric<java.lang.Short>.InnerGeneric<java.lang.Integer> testGenericInnerInt() {
        return null;
    }

    protected static com.squareup.javapoet.TypeNameTest.TestGeneric.NestedNonGeneric testNestedNonGeneric() {
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
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    @org.junit.Test
    public void innerGenericInGenericType() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    @org.junit.Test
    public void innerStaticInGenericType() throws java.lang.Exception {
        java.lang.reflect.Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        com.squareup.javapoet.TypeName.get(staticInGeneric.getReturnType());
        com.squareup.javapoet.TypeName typeName = com.squareup.javapoet.TypeName.get(staticInGeneric.getGenericReturnType());
        // Make sure there are no generic arguments
        com.google.common.truth.Truth.assertThat(typeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric"));
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
    @org.junit.Test(timeout = 1000)
    public void equalsAndHashCodeParameterizedTypeName_cf38_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_4_1 = com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class);
            // MethodAssertGenerator build local variable
            Object o_4_0 = com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_32 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_30 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_30.get(vc_32);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.ClassName.get(java.util.List.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class));
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf38 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45260_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_15878 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_15876 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_15874 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_15874.get(vc_15876, vc_15878);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf45260 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45194() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_15842 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_15842);
        // AssertGenerator replace invocation
        boolean o_innerClassInGenericType_cf45194__11 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_15842);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_innerClassInGenericType_cf45194__11);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45214_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_15852 = (com.squareup.javapoet.CodeWriter)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_15852);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf45214 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45214_failAssert11_cf48457() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_15852 = (com.squareup.javapoet.CodeWriter)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15852);
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_15852);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_17066 = (java.lang.Object)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.equals(vc_17066);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf45214 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45252_failAssert18_cf49995() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_15872 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15872);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_15870 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15870);
            // StatementAdderMethod cloned existing statement
            vc_15870.get(vc_15872);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_17714 = (java.lang.Object)null;
            // StatementAdderMethod cloned existing statement
            vc_15870.equals(vc_17714);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf45252 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45327_cf53766() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create random local variable
        java.lang.reflect.Type[] vc_15905 = new java.lang.reflect.Type []{};
        // AssertGenerator replace invocation
        java.util.List<com.squareup.javapoet.TypeName> o_innerClassInGenericType_cf45327__11 = // StatementAdderMethod cloned existing statement
genericTypeName.list(vc_15905);
        // AssertGenerator add assertion
        java.util.ArrayList collection_339047480 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_339047480, o_innerClassInGenericType_cf45327__11);;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_19226 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_19226);
        // AssertGenerator replace invocation
        boolean o_innerClassInGenericType_cf45327_cf53766__17 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_19226);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_innerClassInGenericType_cf45327_cf53766__17);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45214_failAssert11_cf48580_cf61312() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_15852 = (com.squareup.javapoet.CodeWriter)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15852);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15852);
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_15852);
            // StatementAdderMethod cloned existing statement
            genericTypeName.hashCode();
            // StatementAdderMethod cloned existing statement
            genericTypeName.isBoxedPrimitive();
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf45214 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45327_cf53766_cf62450_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create random local variable
            java.lang.reflect.Type[] vc_15905 = new java.lang.reflect.Type []{};
            // AssertGenerator replace invocation
            java.util.List<com.squareup.javapoet.TypeName> o_innerClassInGenericType_cf45327__11 = // StatementAdderMethod cloned existing statement
genericTypeName.list(vc_15905);
            // AssertGenerator add assertion
            java.util.ArrayList collection_339047480 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_339047480, o_innerClassInGenericType_cf45327__11);;
            // StatementAdderOnAssert create null value
            java.lang.Object vc_19226 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_19226);
            // AssertGenerator replace invocation
            boolean o_innerClassInGenericType_cf45327_cf53766__17 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_19226);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_innerClassInGenericType_cf45327_cf53766__17);
            // StatementAdderMethod cloned existing statement
            vc_19226.toString();
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf45327_cf53766_cf62450 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerClassInGenericType_cf45264_failAssert21_cf50605_cf59840() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_15878 = (java.util.Map)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15878);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15878);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_15876 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15876);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_15876);
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_15876, vc_15878);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_17956 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_17956.box();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_21459 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_21456 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_21456.equals(vc_21459);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf45264 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerGenericInGenericType_cf65616_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_23582 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_23580 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_23578 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_23578.get(vc_23580, vc_23582);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf65616 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerGenericInGenericType_cf65570_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_23556 = (com.squareup.javapoet.CodeWriter)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_23556);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf65570 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerGenericInGenericType_cf65550() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_23546 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_23546);
        // AssertGenerator replace invocation
        boolean o_innerGenericInGenericType_cf65550__11 = // StatementAdderMethod cloned existing statement
genericTypeName.equals(vc_23546);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_innerGenericInGenericType_cf65550__11);
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerGenericInGenericType_cf65578_cf69219() throws java.lang.Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
        com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create null value
        com.squareup.javapoet.CodeWriter vc_23560 = (com.squareup.javapoet.CodeWriter)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_23560);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_23560);
        // AssertGenerator replace invocation
        com.squareup.javapoet.CodeWriter o_innerGenericInGenericType_cf65578__11 = // StatementAdderMethod cloned existing statement
genericTypeName.emitAnnotations(vc_23560);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_innerGenericInGenericType_cf65578__11);
        // AssertGenerator replace invocation
        com.squareup.javapoet.TypeName o_innerGenericInGenericType_cf65578_cf69219__17 = // StatementAdderMethod cloned existing statement
genericTypeName.box();
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).isPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).isAnnotated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).isBoxedPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).isPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).isBoxedPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).box().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).box().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).isAnnotated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).box().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_innerGenericInGenericType_cf65578_cf69219__17.equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).isPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).isPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).isBoxedPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).isAnnotated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).withoutAnnotations().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).isBoxedPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).isBoxedPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).isPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).box().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).isAnnotated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).isPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).isAnnotated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).withoutAnnotations().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).isAnnotated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).isPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).isBoxedPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).isBoxedPrimitive());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).withoutAnnotations().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).withoutAnnotations().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).isAnnotated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations().equals(genericTypeName));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations().equals(genericTypeName));
        org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerGenericInGenericType_cf65610_failAssert19_literalMutation70522() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_23576 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_23576);
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_23576);
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("<javalang.Short>.InnerGeneric<java.lang.Long>").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf65610 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerGenericInGenericType_cf65610_failAssert19_literalMutation70522_cf78285() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_23576 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_23576);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_23576);
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_23576);
            // StatementAdderMethod cloned existing statement
            genericTypeName.isBoxedPrimitive();
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("<javalang.Short>.InnerGeneric<java.lang.Long>").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf65610 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 1000)
    public void innerGenericInGenericType_cf65578_cf69219_literalMutation81925_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.CodeWriter vc_23560 = (com.squareup.javapoet.CodeWriter)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_23560);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_23560);
            // AssertGenerator replace invocation
            com.squareup.javapoet.CodeWriter o_innerGenericInGenericType_cf65578__11 = // StatementAdderMethod cloned existing statement
genericTypeName.emitAnnotations(vc_23560);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_innerGenericInGenericType_cf65578__11);
            // AssertGenerator replace invocation
            com.squareup.javapoet.TypeName o_innerGenericInGenericType_cf65578_cf69219__17 = // StatementAdderMethod cloned existing statement
genericTypeName.box();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).isPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).isAnnotated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).isBoxedPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).isPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).isBoxedPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).box().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).box().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).isAnnotated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).box().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(o_innerGenericInGenericType_cf65578_cf69219__17.equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).isPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).isPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).isBoxedPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).isAnnotated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).withoutAnnotations().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).isBoxedPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).isBoxedPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).box().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).isPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).box().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).isAnnotated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).isPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).isAnnotated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations().equals(o_innerGenericInGenericType_cf65578_cf69219__17));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).withoutAnnotations().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).isAnnotated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).isPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).isBoxedPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations()).isBoxedPrimitive());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).box()).withoutAnnotations().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).withoutAnnotations()).withoutAnnotations().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations()).box()).isAnnotated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).withoutAnnotations().equals(genericTypeName));
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.squareup.javapoet.ParameterizedTypeName)((com.squareup.javapoet.ParameterizedTypeName)o_innerGenericInGenericType_cf65578_cf69219__17).box()).withoutAnnotations().equals(genericTypeName));
            org.junit.Assert.assertNotEquals(com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType()), com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("<java.lang.Short>.InnerGeneVic<java.lang.Long>").getGenericReturnType()));
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.TypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf65578_cf69219_literalMutation81925 should have thrown NoSuchMethodException");
        } catch (java.lang.NoSuchMethodException eee) {
        }
    }
}

