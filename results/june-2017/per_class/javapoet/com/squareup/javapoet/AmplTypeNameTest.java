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
    protected <E extends Enum<E>> E generic(E[] values) {
        return values[0];
    }

    protected static class TestGeneric<T> {
        class Inner {        }

        class InnerGeneric<T2> {        }

        static class NestedNonGeneric {        }
    }

    protected static TestGeneric<String>.Inner testGenericStringInner() {
        return null;
    }

    protected static TestGeneric<Integer>.Inner testGenericIntInner() {
        return null;
    }

    protected static TestGeneric<Short>.InnerGeneric<Long> testGenericInnerLong() {
        return null;
    }

    protected static TestGeneric<Short>.InnerGeneric<Integer> testGenericInnerInt() {
        return null;
    }

    protected static TestGeneric.NestedNonGeneric testNestedNonGeneric() {
        return null;
    }

    @org.junit.Test
    public void genericType() throws Exception {
        java.lang.reflect.Method recursiveEnum = getClass().getDeclaredMethod("generic", Enum[].class);
        TypeName.get(recursiveEnum.getReturnType());
        TypeName.get(recursiveEnum.getGenericReturnType());
        TypeName genericTypeName = TypeName.get(recursiveEnum.getParameterTypes()[0]);
        TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).contains("Enum");
    }

    @org.junit.Test
    public void innerClassInGenericType() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    @org.junit.Test
    public void innerGenericInGenericType() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    @org.junit.Test
    public void innerStaticInGenericType() throws Exception {
        java.lang.reflect.Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
        TypeName.get(staticInGeneric.getReturnType());
        TypeName typeName = TypeName.get(staticInGeneric.getGenericReturnType());
        // Make sure there are no generic arguments
        com.google.common.truth.Truth.assertThat(typeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric"));
    }

    @org.junit.Test
    public void equalsAndHashCodePrimitive() {
        assertEqualsHashCodeAndToString(TypeName.BOOLEAN, TypeName.BOOLEAN);
        assertEqualsHashCodeAndToString(TypeName.BYTE, TypeName.BYTE);
        assertEqualsHashCodeAndToString(TypeName.CHAR, TypeName.CHAR);
        assertEqualsHashCodeAndToString(TypeName.DOUBLE, TypeName.DOUBLE);
        assertEqualsHashCodeAndToString(TypeName.FLOAT, TypeName.FLOAT);
        assertEqualsHashCodeAndToString(TypeName.INT, TypeName.INT);
        assertEqualsHashCodeAndToString(TypeName.LONG, TypeName.LONG);
        assertEqualsHashCodeAndToString(TypeName.SHORT, TypeName.SHORT);
        assertEqualsHashCodeAndToString(TypeName.VOID, TypeName.VOID);
    }

    @org.junit.Test
    public void equalsAndHashCodeArrayTypeName() {
        assertEqualsHashCodeAndToString(ArrayTypeName.of(Object.class), ArrayTypeName.of(Object.class));
        assertEqualsHashCodeAndToString(TypeName.get(Object[].class), ArrayTypeName.of(Object.class));
    }

    @org.junit.Test
    public void equalsAndHashCodeClassName() {
        assertEqualsHashCodeAndToString(ClassName.get(Object.class), ClassName.get(Object.class));
        assertEqualsHashCodeAndToString(TypeName.get(Object.class), ClassName.get(Object.class));
        assertEqualsHashCodeAndToString(ClassName.bestGuess("java.lang.Object"), ClassName.get(Object.class));
    }

    @org.junit.Test
    public void equalsAndHashCodeParameterizedTypeName() {
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Object.class), ParameterizedTypeName.get(Object.class));
        assertEqualsHashCodeAndToString(ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
        org.junit.Assert.assertNotEquals(ClassName.get(java.util.List.class), ParameterizedTypeName.get(java.util.List.class, String.class));
    }

    @org.junit.Test
    public void equalsAndHashCodeTypeVariableName() {
        assertEqualsHashCodeAndToString(TypeVariableName.get(Object.class), TypeVariableName.get(Object.class));
        TypeVariableName typeVar1 = TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
        TypeVariableName typeVar2 = TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
        assertEqualsHashCodeAndToString(typeVar1, typeVar2);
    }

    @org.junit.Test
    public void equalsAndHashCodeWildcardTypeName() {
        assertEqualsHashCodeAndToString(WildcardTypeName.subtypeOf(Object.class), WildcardTypeName.subtypeOf(Object.class));
        assertEqualsHashCodeAndToString(WildcardTypeName.subtypeOf(java.io.Serializable.class), WildcardTypeName.subtypeOf(java.io.Serializable.class));
        assertEqualsHashCodeAndToString(WildcardTypeName.supertypeOf(String.class), WildcardTypeName.supertypeOf(String.class));
    }

    @org.junit.Test
    public void isPrimitive() throws Exception {
        com.google.common.truth.Truth.assertThat(TypeName.INT.isPrimitive()).isTrue();
        com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "Integer").isPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "String").isPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(TypeName.VOID.isPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
    }

    @org.junit.Test
    public void isBoxedPrimitive() throws Exception {
        com.google.common.truth.Truth.assertThat(TypeName.INT.isBoxedPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "Integer").isBoxedPrimitive()).isTrue();
        com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "String").isBoxedPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(TypeName.VOID.isBoxedPrimitive()).isFalse();
        com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "Void").isBoxedPrimitive()).isFalse();
    }

    private void assertEqualsHashCodeAndToString(TypeName a, TypeName b) {
        org.junit.Assert.assertEquals(a.toString(), b.toString());
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeClassName */
    @org.junit.Test
    public void equalsAndHashCodeClassName_literalMutation39_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            assertEqualsHashCodeAndToString(ClassName.get(Object.class), ClassName.get(Object.class));
            assertEqualsHashCodeAndToString(TypeName.get(Object.class), ClassName.get(Object.class));
            assertEqualsHashCodeAndToString(ClassName.bestGuess("GdhscbCS@!x*zH_,"), ClassName.get(Object.class));
            org.junit.Assert.fail("equalsAndHashCodeClassName_literalMutation39 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_cf277_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = ParameterizedTypeName.get(java.util.List.class, String.class);
            assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Object.class), ParameterizedTypeName.get(Object.class));
            assertEqualsHashCodeAndToString(ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_42 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            TypeName vc_40 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_40.get(vc_42);
            // MethodAssertGenerator build local variable
            Object o_13_0 = ClassName.get(java.util.List.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf277 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_cf281_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = ParameterizedTypeName.get(java.util.List.class, String.class);
            assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Object.class), ParameterizedTypeName.get(Object.class));
            assertEqualsHashCodeAndToString(ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, TypeVariableName> vc_48 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_46 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            TypeName vc_44 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_44.get(vc_46, vc_48);
            // MethodAssertGenerator build local variable
            Object o_15_0 = ClassName.get(java.util.List.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf281 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_cf265_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = ParameterizedTypeName.get(java.util.List.class, String.class);
            assertEqualsHashCodeAndToString(ParameterizedTypeName.get(Object.class), ParameterizedTypeName.get(Object.class));
            assertEqualsHashCodeAndToString(ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_32 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            TypeName vc_30 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_30.get(vc_32);
            // MethodAssertGenerator build local variable
            Object o_13_0 = ClassName.get(java.util.List.class);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_cf265 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2978_failAssert35() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, TypeVariableName> vc_120 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_118 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_118, vc_120);
            // MethodAssertGenerator build local variable
            Object o_15_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2978 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2999() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        int o_innerClassInGenericType_cf2999__9 = // StatementAdderMethod cloned existing statement
                genericTypeName.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_innerClassInGenericType_cf2999__9, 932380112);
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2950_failAssert30() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, TypeVariableName> vc_110 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_108 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            TypeName vc_106 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_106.get(vc_108, vc_110);
            // MethodAssertGenerator build local variable
            Object o_17_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2950 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2891() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        boolean o_innerClassInGenericType_cf2891__9 = // StatementAdderMethod cloned existing statement
                genericTypeName.isAnnotated();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_innerClassInGenericType_cf2891__9);
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2939() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        TypeName o_innerClassInGenericType_cf2939__9 = // StatementAdderMethod cloned existing statement
                genericTypeName.box();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ParameterizedTypeName)o_innerClassInGenericType_cf2939__9).isBoxedPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_innerClassInGenericType_cf2939__9.equals(genericTypeName));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ParameterizedTypeName)o_innerClassInGenericType_cf2939__9).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ParameterizedTypeName)o_innerClassInGenericType_cf2939__9).isAnnotated());
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2968_failAssert33() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_114 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_114);
            // MethodAssertGenerator build local variable
            Object o_13_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2968 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3008_failAssert42() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.List<AnnotationSpec> vc_132 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.concatAnnotations(vc_132);
            // MethodAssertGenerator build local variable
            Object o_13_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf3008 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2884() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create null value
        Object vc_74 = (Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_74);
        // AssertGenerator replace invocation
        boolean o_innerClassInGenericType_cf2884__11 = // StatementAdderMethod cloned existing statement
                genericTypeName.equals(vc_74);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_innerClassInGenericType_cf2884__11);
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2991_failAssert37() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderMethod cloned existing statement
            genericTypeName.unbox();
            // MethodAssertGenerator build local variable
            Object o_11_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2991 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2904_failAssert21() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            CodeWriter vc_84 = (CodeWriter)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_84);
            // MethodAssertGenerator build local variable
            Object o_13_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2904 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2912_cf4116_failAssert57() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            CodeWriter vc_88 = (CodeWriter)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_88;
            // AssertGenerator replace invocation
            CodeWriter o_innerClassInGenericType_cf2912__11 = // StatementAdderMethod cloned existing statement
                    genericTypeName.emitAnnotations(vc_88);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerClassInGenericType_cf2912__11;
            // StatementAdderOnAssert create null value
            TypeName vc_510 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_510.isBoxedPrimitive();
            // MethodAssertGenerator build local variable
            Object o_21_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2912_cf4116 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3006_failAssert41_literalMutation5433() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_15_1).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_15_1).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_15_1).isPrimitive());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.List<AnnotationSpec> vc_132 = (java.util.List)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_132);
            // StatementAdderOnAssert create null value
            TypeName vc_130 = (TypeName)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_130);
            // StatementAdderMethod cloned existing statement
            vc_130.concatAnnotations(vc_132);
            // MethodAssertGenerator build local variable
            Object o_15_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.InOner"));
            org.junit.Assert.fail("innerClassInGenericType_cf3006 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2895_cf3854_failAssert14() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            boolean o_innerClassInGenericType_cf2895__9 = // StatementAdderMethod cloned existing statement
                    genericTypeName.isBoxedPrimitive();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerClassInGenericType_cf2895__9;
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, TypeVariableName> vc_408 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_406 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_406, vc_408);
            // MethodAssertGenerator build local variable
            Object o_19_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2895_cf3854 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3003_cf4677_failAssert33() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            String o_innerClassInGenericType_cf3003__9 = // StatementAdderMethod cloned existing statement
                    genericTypeName.toString();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerClassInGenericType_cf3003__9;
            // StatementAdderOnAssert create null value
            java.util.List<AnnotationSpec> vc_744 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.annotated(vc_744);
            // MethodAssertGenerator build local variable
            Object o_17_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf3003_cf4677 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2912_cf4174_failAssert60() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            CodeWriter vc_88 = (CodeWriter)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_88;
            // AssertGenerator replace invocation
            CodeWriter o_innerClassInGenericType_cf2912__11 = // StatementAdderMethod cloned existing statement
                    genericTypeName.emitAnnotations(vc_88);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerClassInGenericType_cf2912__11;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_536 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_536);
            // MethodAssertGenerator build local variable
            Object o_21_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2912_cf4174 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf2899_cf4017_failAssert51() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
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
            Object o_17_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            org.junit.Assert.fail("innerClassInGenericType_cf2899_cf4017 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_cf3006_failAssert41_literalMutation5433_cf5617_failAssert28() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_15_1 = TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
                // MethodAssertGenerator build local variable
                Object o_9_0 = ((ParameterizedTypeName)o_15_1).isAnnotated();
                // MethodAssertGenerator build local variable
                Object o_11_0 = ((ParameterizedTypeName)o_15_1).isBoxedPrimitive();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((ParameterizedTypeName)o_15_1).isPrimitive();
                java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
                TypeName.get(genericStringInner.getReturnType());
                TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
                // StatementAdderOnAssert create null value
                java.util.List<AnnotationSpec> vc_132 = (java.util.List)null;
                // MethodAssertGenerator build local variable
                Object o_25_0 = vc_132;
                // StatementAdderOnAssert create null value
                TypeName vc_130 = (TypeName)null;
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, TypeVariableName> vc_830 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_828 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                TypeName vc_826 = (TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_826.get(vc_828, vc_830);
                // MethodAssertGenerator build local variable
                Object o_37_0 = vc_130;
                // StatementAdderMethod cloned existing statement
                vc_130.concatAnnotations(vc_132);
                // MethodAssertGenerator build local variable
                Object o_15_0 = TypeName.get(genericStringInner.getGenericReturnType());
                // Make sure the generic argument is present
                com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.String>.InOner"));
                org.junit.Assert.fail("innerClassInGenericType_cf3006 should have thrown NullPointerException");
            } catch (NullPointerException eee) {
            }
            org.junit.Assert.fail("innerClassInGenericType_cf3006_failAssert41_literalMutation5433_cf5617 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6846_failAssert30() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, TypeVariableName> vc_902 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_900 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            TypeName vc_898 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_898.get(vc_900, vc_902);
            // MethodAssertGenerator build local variable
            Object o_17_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6846 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6887_failAssert37() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderMethod cloned existing statement
            genericTypeName.unbox();
            // MethodAssertGenerator build local variable
            Object o_11_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6887 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6780() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        // StatementAdderOnAssert create null value
        Object vc_866 = (Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_866);
        // AssertGenerator replace invocation
        boolean o_innerGenericInGenericType_cf6780__11 = // StatementAdderMethod cloned existing statement
                genericTypeName.equals(vc_866);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_innerGenericInGenericType_cf6780__11);
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6895() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        int o_innerGenericInGenericType_cf6895__9 = // StatementAdderMethod cloned existing statement
                genericTypeName.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_innerGenericInGenericType_cf6895__9, 1324053722);
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6870_failAssert34() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, TypeVariableName> vc_912 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_910 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            TypeName vc_908 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_908.get(vc_910, vc_912);
            // MethodAssertGenerator build local variable
            Object o_17_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6870 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6904_failAssert42() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.List<AnnotationSpec> vc_924 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.concatAnnotations(vc_924);
            // MethodAssertGenerator build local variable
            Object o_13_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6904 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6864_failAssert33() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_906 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_906);
            // MethodAssertGenerator build local variable
            Object o_13_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6864 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6835() throws Exception {
        java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
        TypeName.get(genericStringInner.getReturnType());
        TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
        // AssertGenerator replace invocation
        TypeName o_innerGenericInGenericType_cf6835__9 = // StatementAdderMethod cloned existing statement
                genericTypeName.box();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ParameterizedTypeName)o_innerGenericInGenericType_cf6835__9).isAnnotated());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_innerGenericInGenericType_cf6835__9.equals(genericTypeName));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ParameterizedTypeName)o_innerGenericInGenericType_cf6835__9).isBoxedPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ParameterizedTypeName)o_innerGenericInGenericType_cf6835__9).isPrimitive());
        org.junit.Assert.assertNotEquals(TypeName.get(genericStringInner.getGenericReturnType()), TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType()));
        // Make sure the generic argument is present
        com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6800_failAssert21() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            CodeWriter vc_876 = (CodeWriter)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.emit(vc_876);
            // MethodAssertGenerator build local variable
            Object o_13_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6800 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6781_cf7304_failAssert14() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create random local variable
            Object vc_867 = new Object();
            // AssertGenerator replace invocation
            boolean o_innerGenericInGenericType_cf6781__11 = // StatementAdderMethod cloned existing statement
                    genericTypeName.equals(vc_867);
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_innerGenericInGenericType_cf6781__11;
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, TypeVariableName> vc_1046 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_1044 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            TypeName vc_1042 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_1042.get(vc_1044, vc_1046);
            // MethodAssertGenerator build local variable
            Object o_23_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6781_cf7304 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6780_cf7010_failAssert20() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            Object vc_866 = (Object)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_866;
            // AssertGenerator replace invocation
            boolean o_innerGenericInGenericType_cf6780__11 = // StatementAdderMethod cloned existing statement
                    genericTypeName.equals(vc_866);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerGenericInGenericType_cf6780__11;
            // StatementAdderOnAssert create null value
            java.util.List<AnnotationSpec> vc_960 = (java.util.List)null;
            // StatementAdderMethod cloned existing statement
            genericTypeName.annotated(vc_960);
            // MethodAssertGenerator build local variable
            Object o_21_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6780_cf7010 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6808_cf8033_failAssert9() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            CodeWriter vc_880 = (CodeWriter)null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_880;
            // AssertGenerator replace invocation
            CodeWriter o_innerGenericInGenericType_cf6808__11 = // StatementAdderMethod cloned existing statement
                    genericTypeName.emitAnnotations(vc_880);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_innerGenericInGenericType_cf6808__11;
            // StatementAdderOnAssert create null value
            TypeName vc_1310 = (TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_1310.emitAnnotations(vc_880);
            // MethodAssertGenerator build local variable
            Object o_21_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6808_cf8033 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6895_cf8440_failAssert47() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // MethodAssertGenerator build local variable
            Object o_11_1 = -302376430;
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
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
            Object o_17_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6895_cf8440 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6850_failAssert31_literalMutation9150() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_15_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_15_1).isAnnotated());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_15_1).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_15_1).isPrimitive());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, TypeVariableName> vc_902 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_902);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_900 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_900);
            // StatementAdderMethod cloned existing statement
            genericTypeName.get(vc_900, vc_902);
            // MethodAssertGenerator build local variable
            Object o_15_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "c%>ct-g/><(;FkQj(!%3b`58ZE3#sHdd*?(,MAonC[6(hn"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6850 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6850_failAssert31_literalMutation9150_cf9549_failAssert37() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_15_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
                // MethodAssertGenerator build local variable
                Object o_9_0 = ((ParameterizedTypeName)o_15_1).isAnnotated();
                // MethodAssertGenerator build local variable
                Object o_11_0 = ((ParameterizedTypeName)o_15_1).isBoxedPrimitive();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((ParameterizedTypeName)o_15_1).isPrimitive();
                java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
                TypeName.get(genericStringInner.getReturnType());
                TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, TypeVariableName> vc_902 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_25_0 = vc_902;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_900 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_1626 = (javax.lang.model.type.TypeMirror)null;
                // StatementAdderOnAssert create null value
                TypeName vc_1624 = (TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_1624.get(vc_1626);
                // MethodAssertGenerator build local variable
                Object o_35_0 = vc_900;
                // StatementAdderMethod cloned existing statement
                genericTypeName.get(vc_900, vc_902);
                // MethodAssertGenerator build local variable
                Object o_15_0 = TypeName.get(genericStringInner.getGenericReturnType());
                // Make sure the generic argument is present
                com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "c%>ct-g/><(;FkQj(!%3b`58ZE3#sHdd*?(,MAonC[6(hn"));
                org.junit.Assert.fail("innerGenericInGenericType_cf6850 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("innerGenericInGenericType_cf6850_failAssert31_literalMutation9150_cf9549 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_cf6787_literalMutation7468_failAssert1_literalMutation9665() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_13_1).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_13_1).isBoxedPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((ParameterizedTypeName)o_13_1).isAnnotated());
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLtg");
            TypeName.get(genericStringInner.getReturnType());
            TypeName genericTypeName = TypeName.get(genericStringInner.getGenericReturnType());
            // AssertGenerator replace invocation
            boolean o_innerGenericInGenericType_cf6787__9 = // StatementAdderMethod cloned existing statement
                    genericTypeName.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_innerGenericInGenericType_cf6787__9;
            // MethodAssertGenerator build local variable
            Object o_13_0 = TypeName.get(genericStringInner.getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            org.junit.Assert.fail("innerGenericInGenericType_cf6787_literalMutation7468 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isBoxedPrimitive */
    @org.junit.Test
    public void isBoxedPrimitive_literalMutation11194_failAssert8() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(TypeName.INT.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "Int+ger").isBoxedPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "String").isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(TypeName.VOID.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "Void").isBoxedPrimitive()).isFalse();
            org.junit.Assert.fail("isBoxedPrimitive_literalMutation11194 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isPrimitive */
    @org.junit.Test
    public void isPrimitive_literalMutation18408_failAssert2() throws Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(TypeName.INT.isPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "|{M4fTk").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "String").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(TypeName.VOID.isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
            org.junit.Assert.fail("isPrimitive_literalMutation18408 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }
}

