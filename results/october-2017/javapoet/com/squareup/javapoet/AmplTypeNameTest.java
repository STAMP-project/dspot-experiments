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

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeArrayTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeArrayTypeName_add5_sd36_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ArrayTypeName.of(java.lang.Object.class), com.squareup.javapoet.ArrayTypeName.of(java.lang.Object.class));
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_4 = // MethodCallAdder
            com.squareup.javapoet.TypeName.get(java.lang.Object[].class);
            assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.get(java.lang.Object[].class), com.squareup.javapoet.ArrayTypeName.of(java.lang.Object.class));
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("equalsAndHashCodeArrayTypeName_add5_sd36 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeClassName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeClassName_literalMutationString158_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ClassName.get(java.lang.Object.class), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeName.get(java.lang.Object.class), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ClassName.bestGuess(""), com.squareup.javapoet.ClassName.get(java.lang.Object.class));
            org.junit.Assert.fail("equalsAndHashCodeClassName_literalMutationString158 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add1321_sd1363_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_80 = "ZqW<Z|&oob]U)GU|GC>>";
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.ClassName __DSPOT_invoc_7 = // MethodCallAdder
            com.squareup.javapoet.ClassName.get(java.util.List.class);
            com.squareup.javapoet.ClassName.get(java.util.List.class);
            com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_7.peerClass(__DSPOT_name_80);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_add1321_sd1363 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add1316_sd1351_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_1 = // MethodCallAdder
            com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class);
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            com.squareup.javapoet.ClassName.get(java.util.List.class);
            com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_1.unbox();
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_add1316_sd1351 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add1321_sd1361_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_79 = "WRC/$oDWlo<Yu0]keSCJ";
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.ClassName __DSPOT_invoc_7 = // MethodCallAdder
            com.squareup.javapoet.ClassName.get(java.util.List.class);
            com.squareup.javapoet.ClassName.get(java.util.List.class);
            com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_7.nestedClass(__DSPOT_name_79);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_add1321_sd1361 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeParameterizedTypeName */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeParameterizedTypeName_add1322_sd1371_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_name_82 = "9%$tKd!o7n&%5]#&n[!M";
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class), com.squareup.javapoet.ParameterizedTypeName.get(java.lang.Object.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class), com.squareup.javapoet.ParameterizedTypeName.get(java.util.Set.class, java.util.UUID.class));
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.ParameterizedTypeName __DSPOT_invoc_7 = // MethodCallAdder
            com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            com.squareup.javapoet.ClassName.get(java.util.List.class);
            com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_7.nestedClass(__DSPOT_name_82);
            org.junit.Assert.fail("equalsAndHashCodeParameterizedTypeName_add1322_sd1371 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeTypeVariableName */
    /* amplification of equalsAndHashCodeTypeVariableName_sd2189 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_sd2189_sd2713_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class), com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class));
            com.squareup.javapoet.TypeVariableName typeVar1 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
            com.squareup.javapoet.TypeVariableName typeVar2 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
            assertEqualsHashCodeAndToString(typeVar1, typeVar2);
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            typeVar2.withoutAnnotations();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.unbox();
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_sd2189_sd2713 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeTypeVariableName */
    /* amplification of equalsAndHashCodeTypeVariableName_sd2181 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_sd2181_literalMutationNumber2500_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.TypeName[] __DSPOT_bounds_170 = new com.squareup.javapoet.TypeName[// TestDataMutator on numbers
            1];
            assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class), com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class));
            com.squareup.javapoet.TypeVariableName typeVar1 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
            com.squareup.javapoet.TypeVariableName typeVar2 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
            assertEqualsHashCodeAndToString(typeVar1, typeVar2);
            // StatementAdd: add invocation of a method
            typeVar1.withBounds(__DSPOT_bounds_170);
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_sd2181_literalMutationNumber2500 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeTypeVariableName */
    /* amplification of equalsAndHashCodeTypeVariableName_sd2181 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeTypeVariableName_sd2181_literalMutationNumber2501_failAssert0_literalMutationNumber3088_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.squareup.javapoet.TypeName[] __DSPOT_bounds_170 = new com.squareup.javapoet.TypeName[// TestDataMutator on numbers
                10];
                assertEqualsHashCodeAndToString(com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class), com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class));
                com.squareup.javapoet.TypeVariableName typeVar1 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
                com.squareup.javapoet.TypeVariableName typeVar2 = com.squareup.javapoet.TypeVariableName.get("T", java.util.Comparator.class, java.io.Serializable.class);
                assertEqualsHashCodeAndToString(typeVar1, typeVar2);
                // StatementAdd: add invocation of a method
                typeVar1.withBounds(__DSPOT_bounds_170);
                org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_sd2181_literalMutationNumber2501 should have thrown NegativeArraySizeException");
            } catch (java.lang.NegativeArraySizeException eee) {
            }
            org.junit.Assert.fail("equalsAndHashCodeTypeVariableName_sd2181_literalMutationNumber2501_failAssert0_literalMutationNumber3088 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#equalsAndHashCodeWildcardTypeName */
    /* amplification of equalsAndHashCodeWildcardTypeName_add3406_sd3437 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCodeWildcardTypeName_add3406_sd3437_sd3487_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            assertEqualsHashCodeAndToString(com.squareup.javapoet.WildcardTypeName.subtypeOf(java.lang.Object.class), com.squareup.javapoet.WildcardTypeName.subtypeOf(java.lang.Object.class));
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.WildcardTypeName __DSPOT_invoc_4 = // MethodCallAdder
            com.squareup.javapoet.WildcardTypeName.subtypeOf(java.io.Serializable.class);
            assertEqualsHashCodeAndToString(com.squareup.javapoet.WildcardTypeName.subtypeOf(java.io.Serializable.class), com.squareup.javapoet.WildcardTypeName.subtypeOf(java.io.Serializable.class));
            assertEqualsHashCodeAndToString(com.squareup.javapoet.WildcardTypeName.supertypeOf(java.lang.String.class), com.squareup.javapoet.WildcardTypeName.supertypeOf(java.lang.String.class));
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_14 = // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.withoutAnnotations();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_14.unbox();
            org.junit.Assert.fail("equalsAndHashCodeWildcardTypeName_add3406_sd3437_sd3487 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#genericType */
    @org.junit.Test(timeout = 10000)
    public void genericType_sd3682_failAssert15() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method recursiveEnum = getClass().getDeclaredMethod("generic", java.lang.Enum[].class);
            com.squareup.javapoet.TypeName.get(recursiveEnum.getReturnType());
            com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(recursiveEnum.getParameterTypes()[0]);
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_11 = com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).contains("Enum");
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_11.unbox();
            org.junit.Assert.fail("genericType_sd3682 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#genericType */
    @org.junit.Test(timeout = 10000)
    public void genericType_sd3670_failAssert14() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method recursiveEnum = getClass().getDeclaredMethod("generic", java.lang.Enum[].class);
            com.squareup.javapoet.TypeName.get(recursiveEnum.getReturnType());
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_6 = com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(recursiveEnum.getParameterTypes()[0]);
            com.squareup.javapoet.TypeName.get(recursiveEnum.getGenericParameterTypes()[0]);
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).contains("Enum");
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_6.unbox();
            org.junit.Assert.fail("genericType_sd3670 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerClassInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerClassInGenericType_sd7526_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericStringInner");
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_4 = com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericIntInner").getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.String>.Inner"));
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerClassInGenericType_sd7526 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerGenericInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerGenericInGenericType_sd10361_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method genericStringInner = getClass().getDeclaredMethod("testGenericInnerLong");
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_4 = com.squareup.javapoet.TypeName.get(genericStringInner.getReturnType());
            com.squareup.javapoet.TypeName genericTypeName = com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            com.squareup.javapoet.TypeName.get(genericStringInner.getGenericReturnType());
            com.squareup.javapoet.TypeName.get(getClass().getDeclaredMethod("testGenericInnerInt").getGenericReturnType());
            // Make sure the generic argument is present
            com.google.common.truth.Truth.assertThat(genericTypeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + "<java.lang.Short>.InnerGeneric<java.lang.Long>"));
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerGenericInGenericType_sd10361 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#innerStaticInGenericType */
    @org.junit.Test(timeout = 10000)
    public void innerStaticInGenericType_sd12915_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.reflect.Method staticInGeneric = getClass().getDeclaredMethod("testNestedNonGeneric");
            // StatementAdd: generate variable from return value
            com.squareup.javapoet.TypeName __DSPOT_invoc_4 = com.squareup.javapoet.TypeName.get(staticInGeneric.getReturnType());
            com.squareup.javapoet.TypeName typeName = com.squareup.javapoet.TypeName.get(staticInGeneric.getGenericReturnType());
            // Make sure there are no generic arguments
            com.google.common.truth.Truth.assertThat(typeName.toString()).isEqualTo(((com.squareup.javapoet.AmplTypeNameTest.TestGeneric.class.getCanonicalName()) + ".NestedNonGeneric"));
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.unbox();
            org.junit.Assert.fail("innerStaticInGenericType_sd12915 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isBoxedPrimitive */
    @org.junit.Test(timeout = 10000)
    public void isBoxedPrimitive_literalMutationString14718_failAssert4() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Integer").isBoxedPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "\n.build()$<$<").isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isBoxedPrimitive()).isFalse();
            org.junit.Assert.fail("isBoxedPrimitive_literalMutationString14718 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isBoxedPrimitive */
    @org.junit.Test(timeout = 10000)
    public void isBoxedPrimitive_literalMutationString14706_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "\n.build()$<$<").isBoxedPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "String").isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isBoxedPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isBoxedPrimitive()).isFalse();
            org.junit.Assert.fail("isBoxedPrimitive_literalMutationString14706 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isPrimitive */
    @org.junit.Test(timeout = 10000)
    public void isPrimitive_literalMutationString23774_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "W_KP9>R").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "String").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
            org.junit.Assert.fail("isPrimitive_literalMutationString23774 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.TypeNameTest#isPrimitive */
    @org.junit.Test(timeout = 10000)
    public void isPrimitive_literalMutationString23778_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.INT.isPrimitive()).isTrue();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "In>teger").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "String").isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.TypeName.VOID.isPrimitive()).isFalse();
            com.google.common.truth.Truth.assertThat(com.squareup.javapoet.ClassName.get("java.lang", "Void").isPrimitive()).isFalse();
            org.junit.Assert.fail("isPrimitive_literalMutationString23778 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

