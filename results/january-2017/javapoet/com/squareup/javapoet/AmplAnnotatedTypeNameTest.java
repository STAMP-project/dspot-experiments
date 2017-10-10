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


public class AmplAnnotatedTypeNameTest {
    private static final java.lang.String NN = com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull.class.getCanonicalName();

    private final com.squareup.javapoet.AnnotationSpec NEVER_NULL = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull.class).build();

    public @interface NeverNull {    }

    @org.junit.Test(expected = java.lang.NullPointerException.class)
    public void nullAnnotationArray() {
        com.squareup.javapoet.TypeName.BOOLEAN.annotated(((com.squareup.javapoet.AnnotationSpec[]) (null)));
    }

    @org.junit.Test(expected = java.lang.NullPointerException.class)
    public void nullAnnotationList() {
        com.squareup.javapoet.TypeName.DOUBLE.annotated(((java.util.List<com.squareup.javapoet.AnnotationSpec>) (null)));
    }

    @org.junit.Test
    public void annotated() {
        com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        org.junit.Assert.assertFalse(simpleString.isAnnotated());
        org.junit.Assert.assertEquals(simpleString, com.squareup.javapoet.TypeName.get(java.lang.String.class));
        com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
        org.junit.Assert.assertTrue(annotated.isAnnotated());
        org.junit.Assert.assertEquals(annotated, annotated.annotated());
    }

    @org.junit.Test
    public void annotatedType() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedTwice() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedParameterizedType() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedArgumentOfParameterizedType() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedWildcardTypeNameWithSuper() {
        java.lang.String expected = ("? super @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedWildcardTypeNameWithExtends() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedEquivalence() {
        annotatedEquivalence(com.squareup.javapoet.TypeName.VOID);
        annotatedEquivalence(com.squareup.javapoet.ArrayTypeName.get(java.lang.Object[].class));
        annotatedEquivalence(com.squareup.javapoet.ClassName.get(java.lang.Object.class));
        annotatedEquivalence(com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.Object.class));
        annotatedEquivalence(com.squareup.javapoet.TypeVariableName.get(java.lang.Object.class));
        annotatedEquivalence(com.squareup.javapoet.WildcardTypeName.get(java.lang.Object.class));
    }

    private void annotatedEquivalence(com.squareup.javapoet.TypeName type) {
        org.junit.Assert.assertFalse(type.isAnnotated());
        org.junit.Assert.assertEquals(type, type);
        org.junit.Assert.assertEquals(type.annotated(NEVER_NULL), type.annotated(NEVER_NULL));
        org.junit.Assert.assertNotEquals(type, type.annotated(NEVER_NULL));
        org.junit.Assert.assertEquals(type.hashCode(), type.hashCode());
        org.junit.Assert.assertEquals(type.annotated(NEVER_NULL).hashCode(), type.annotated(NEVER_NULL).hashCode());
        org.junit.Assert.assertNotEquals(type.hashCode(), type.annotated(NEVER_NULL).hashCode());
    }

    // https://github.com/square/javapoet/issues/431
    public @interface TypeUseAnnotation {    }

    // https://github.com/square/javapoet/issues/431
    @org.junit.Ignore
    @org.junit.Test
    public void annotatedNestedType() {
        java.lang.String expected = ("java.util.Map.@" + (com.squareup.javapoet.AnnotatedTypeNameTest.TypeUseAnnotation.class.getCanonicalName())) + " Entry";
        com.squareup.javapoet.AnnotationSpec typeUseAnnotation = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotatedTypeNameTest.TypeUseAnnotation.class).build();
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.util.Map.Entry.class).annotated(typeUseAnnotation);
        java.lang.String actual = type.toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    // https://github.com/square/javapoet/issues/431
    @org.junit.Ignore
    @org.junit.Test
    public void annotatedNestedParameterizedType() {
        java.lang.String expected = ("java.util.Map.@" + (com.squareup.javapoet.AnnotatedTypeNameTest.TypeUseAnnotation.class.getCanonicalName())) + " Entry<java.lang.Byte, java.lang.Byte>";
        com.squareup.javapoet.AnnotationSpec typeUseAnnotation = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AnnotatedTypeNameTest.TypeUseAnnotation.class).build();
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.Map.Entry.class, java.lang.Byte.class, java.lang.Byte.class).annotated(typeUseAnnotation);
        java.lang.String actual = type.toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 1000)
    public void annotated_cf61_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            // MethodAssertGenerator build local variable
            Object o_3_0 = simpleString.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
            // MethodAssertGenerator build local variable
            Object o_9_0 = annotated.isAnnotated();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_32 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_30 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_30.get(vc_32);
            // MethodAssertGenerator build local variable
            Object o_17_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf61 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 1000)
    public void annotated_cf3() {
        com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        org.junit.Assert.assertFalse(simpleString.isAnnotated());
        org.junit.Assert.assertEquals(simpleString, com.squareup.javapoet.TypeName.get(java.lang.String.class));
        com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
        org.junit.Assert.assertTrue(annotated.isAnnotated());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_annotated_cf3__13 = // StatementAdderMethod cloned existing statement
annotated.equals(vc_2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotated_cf3__13);
        org.junit.Assert.assertEquals(annotated, annotated.annotated());
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 1000)
    public void annotated_cf3_cf259_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            // MethodAssertGenerator build local variable
            Object o_3_0 = simpleString.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
            // MethodAssertGenerator build local variable
            Object o_9_0 = annotated.isAnnotated();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2);
            // AssertGenerator replace invocation
            boolean o_annotated_cf3__13 = // StatementAdderMethod cloned existing statement
annotated.equals(vc_2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotated_cf3__13);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_110 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_108 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_106 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_106.get(vc_108, vc_110);
            // MethodAssertGenerator build local variable
            Object o_27_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf3_cf259 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 1000)
    public void annotated_cf10_cf721_cf1869_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            // MethodAssertGenerator build local variable
            Object o_3_0 = simpleString.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
            // MethodAssertGenerator build local variable
            Object o_9_0 = annotated.isAnnotated();
            // AssertGenerator replace invocation
            boolean o_annotated_cf10__11 = // StatementAdderMethod cloned existing statement
annotated.isAnnotated();
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(o_annotated_cf10__11);
            // AssertGenerator replace invocation
            boolean o_annotated_cf10_cf721__15 = // StatementAdderMethod cloned existing statement
simpleString.isAnnotated();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotated_cf10_cf721__15);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_686 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_684 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            annotated.get(vc_684, vc_686);
            // MethodAssertGenerator build local variable
            Object o_25_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf10_cf721_cf1869 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 1000)
    public void annotated_cf3_cf368_cf2837_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            // MethodAssertGenerator build local variable
            Object o_3_0 = simpleString.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
            // MethodAssertGenerator build local variable
            Object o_9_0 = annotated.isAnnotated();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2);
            // AssertGenerator replace invocation
            boolean o_annotated_cf3__13 = // StatementAdderMethod cloned existing statement
annotated.equals(vc_2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotated_cf3__13);
            // AssertGenerator replace invocation
            int o_annotated_cf3_cf368__19 = // StatementAdderMethod cloned existing statement
simpleString.hashCode();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_annotated_cf3_cf368__19, 1195259493);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_1078 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type[] vc_1076 = (java.lang.reflect.Type[])null;
            // StatementAdderMethod cloned existing statement
            annotated.list(vc_1076, vc_1078);
            // MethodAssertGenerator build local variable
            Object o_31_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf3_cf368_cf2837 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 1000)
    public void annotated_cf122_cf1358_cf3526() {
        com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        org.junit.Assert.assertFalse(simpleString.isAnnotated());
        org.junit.Assert.assertEquals(simpleString, com.squareup.javapoet.TypeName.get(java.lang.String.class));
        com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
        org.junit.Assert.assertTrue(annotated.isAnnotated());
        // AssertGenerator replace invocation
        java.lang.String o_annotated_cf122__11 = // StatementAdderMethod cloned existing statement
annotated.toString();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_annotated_cf122__11, "@com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
        // StatementAdderOnAssert create null value
        java.lang.Object vc_506 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_506);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_506);
        // AssertGenerator replace invocation
        boolean o_annotated_cf122_cf1358__17 = // StatementAdderMethod cloned existing statement
simpleString.equals(vc_506);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotated_cf122_cf1358__17);
        // AssertGenerator replace invocation
        java.lang.String o_annotated_cf122_cf1358_cf3526__23 = // StatementAdderMethod cloned existing statement
annotated.toString();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_annotated_cf122_cf1358_cf3526__23, "@com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
        org.junit.Assert.assertEquals(annotated, annotated.annotated());
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedArgumentOfParameterizedType_cf3601() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1298 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1298);
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf3601__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3601__12);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedArgumentOfParameterizedType_cf3671_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_1334 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_1332 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_1332, vc_1334);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf3671 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedArgumentOfParameterizedType_cf3671_failAssert23_literalMutation5070() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" java.la:g.String>" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " java.la:g.String>com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String>");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_1334 = (java.util.Map)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1334);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_1332 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1332);
            // StatementAdderMethod cloned existing statement
            type.get(vc_1332, vc_1334);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf3671 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedArgumentOfParameterizedType_cf3601_cf3886() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1298 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1298);
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf3601__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3601__12);
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf3601_cf3886__18 = // StatementAdderMethod cloned existing statement
type.equals(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3601_cf3886__18);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedArgumentOfParameterizedType_cf3616_cf4878_failAssert24_literalMutation7662() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" java+lang.String>" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " java+lang.String>com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String>");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedArgumentOfParameterizedType_cf3616__10 = // StatementAdderMethod cloned existing statement
type.isPrimitive();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3616__10);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_1688 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1688);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_1686 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1686);
            // StatementAdderMethod cloned existing statement
            vc_1686.get(vc_1688);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf3616_cf4878 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedArgumentOfParameterizedType_cf3716_cf5173_cf5987_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // AssertGenerator replace invocation
            int o_annotatedArgumentOfParameterizedType_cf3716__10 = // StatementAdderMethod cloned existing statement
type.hashCode();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_annotatedArgumentOfParameterizedType_cf3716__10, 1362420326);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1730 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1730);
            // AssertGenerator replace invocation
            boolean o_annotatedArgumentOfParameterizedType_cf3716_cf5173__16 = // StatementAdderMethod cloned existing statement
type.equals(vc_1730);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3716_cf5173__16);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1946 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_1944 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_1944.equals(vc_1946);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf3716_cf5173_cf5987 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedArgumentOfParameterizedType_cf3601_cf3886_cf7119() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1298 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1298);
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf3601__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3601__12);
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf3601_cf3886__18 = // StatementAdderMethod cloned existing statement
type.equals(vc_1298);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3601_cf3886__18);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2234 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2234);
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf3601_cf3886_cf7119__26 = // StatementAdderMethod cloned existing statement
type.equals(vc_2234);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf3601_cf3886_cf7119__26);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedParameterizedType_cf7761() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2378 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2378);
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf7761__9 = // StatementAdderMethod cloned existing statement
type.equals(vc_2378);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedParameterizedType_cf7761__9);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedParameterizedType_cf7819_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_2408 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_2406 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_2406.get(vc_2408);
            org.junit.Assert.fail("annotatedParameterizedType_cf7819 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedParameterizedType_cf7768_cf8560() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf7768__7 = // StatementAdderMethod cloned existing statement
type.isAnnotated();
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedParameterizedType_cf7768__7);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2594 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2594);
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf7768_cf8560__13 = // StatementAdderMethod cloned existing statement
type.equals(vc_2594);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedParameterizedType_cf7768_cf8560__13);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedParameterizedType_cf7776_cf8960_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedParameterizedType_cf7776__7 = // StatementAdderMethod cloned existing statement
type.isPrimitive();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedParameterizedType_cf7776__7);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_2768 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_2766 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_2766.get(vc_2768);
            org.junit.Assert.fail("annotatedParameterizedType_cf7776_cf8960 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedParameterizedType_cf7768_cf8560_cf10140() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf7768__7 = // StatementAdderMethod cloned existing statement
type.isAnnotated();
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedParameterizedType_cf7768__7);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2594 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2594);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2594);
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf7768_cf8560__13 = // StatementAdderMethod cloned existing statement
type.equals(vc_2594);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedParameterizedType_cf7768_cf8560__13);
        // AssertGenerator replace invocation
        java.lang.String o_annotatedParameterizedType_cf7768_cf8560_cf10140__19 = // StatementAdderMethod cloned existing statement
type.toString();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_annotatedParameterizedType_cf7768_cf8560_cf10140__19, "java.util.List<java.lang.String>");
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedParameterizedType_cf7827_failAssert19_literalMutation9278_literalMutation11040() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, "com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.util.List<java.lang.String>");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, "com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.util.List<java.lang.String>");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_2414 = (java.util.Map)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2414);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2414);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_2412 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2412);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2412);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_2410 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2410);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2410);
            // StatementAdderMethod cloned existing statement
            vc_2410.get(vc_2412, vc_2414);
            org.junit.Assert.fail("annotatedParameterizedType_cf7827 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11584() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3530 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3530);
        // AssertGenerator replace invocation
        boolean o_annotatedTwice_cf11584__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_3530);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedTwice_cf11584__12);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11642_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_3560 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_3558 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_3558.get(vc_3560);
            org.junit.Assert.fail("annotatedTwice_cf11642 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11585_cf12190_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_3531 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_annotatedTwice_cf11585__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_3531);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedTwice_cf11585__12);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_3710 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_3708 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_3706 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_3706.get(vc_3708, vc_3710);
            org.junit.Assert.fail("annotatedTwice_cf11585_cf12190 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11585_cf12094() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3531 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedTwice_cf11585__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_3531);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedTwice_cf11585__12);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3674 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3674);
        // AssertGenerator replace invocation
        boolean o_annotatedTwice_cf11585_cf12094__18 = // StatementAdderMethod cloned existing statement
type.equals(vc_3674);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedTwice_cf11585_cf12094__18);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11584_cf11869_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_3530 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_3530);
            // AssertGenerator replace invocation
            boolean o_annotatedTwice_cf11584__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_3530);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedTwice_cf11584__12);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.AnnotationSpec[] vc_3620 = (com.squareup.javapoet.AnnotationSpec[])null;
            // StatementAdderMethod cloned existing statement
            type.annotated(vc_3620);
            org.junit.Assert.fail("annotatedTwice_cf11584_cf11869 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11584_cf11869_failAssert17_literalMutation15292() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" @java.lanGg.Override java.lang.String" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " @java.lanGg.Override java.lang.Stringcom.squareup.javapoet.AnnotatedTypeNameTest.NeverNull @java.lang.Override java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_3530 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_3530);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_3530);
            // AssertGenerator replace invocation
            boolean o_annotatedTwice_cf11584__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_3530);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedTwice_cf11584__12);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.AnnotationSpec[] vc_3620 = (com.squareup.javapoet.AnnotationSpec[])null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_3620);
            // StatementAdderMethod cloned existing statement
            type.annotated(vc_3620);
            org.junit.Assert.fail("annotatedTwice_cf11584_cf11869 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11599_cf12844_cf13484_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedTwice_cf11599__10 = // StatementAdderMethod cloned existing statement
type.isPrimitive();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedTwice_cf11599__10);
            // AssertGenerator replace invocation
            java.lang.String o_annotatedTwice_cf11599_cf12844__14 = // StatementAdderMethod cloned existing statement
type.toString();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_annotatedTwice_cf11599_cf12844__14, "java.lang.String");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_4142 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_4140 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_4140, vc_4142);
            org.junit.Assert.fail("annotatedTwice_cf11599_cf12844_cf13484 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 1000)
    public void annotatedTwice_cf11584_cf12040_cf15206() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3530 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3530);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3530);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3530);
        // AssertGenerator replace invocation
        boolean o_annotatedTwice_cf11584__12 = // StatementAdderMethod cloned existing statement
type.equals(vc_3530);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedTwice_cf11584__12);
        // StatementAdderOnAssert create random local variable
        java.lang.reflect.Type[] vc_3665 = new java.lang.reflect.Type []{};
        // AssertGenerator replace invocation
        java.util.List<com.squareup.javapoet.TypeName> o_annotatedTwice_cf11584_cf12040__20 = // StatementAdderMethod cloned existing statement
type.list(vc_3665);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2062954973 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_2062954973, o_annotatedTwice_cf11584_cf12040__20);;
        // StatementAdderOnAssert create null value
        com.squareup.javapoet.TypeName vc_4598 = (com.squareup.javapoet.TypeName)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4598);
        // AssertGenerator replace invocation
        java.util.List<com.squareup.javapoet.TypeName> o_annotatedTwice_cf11584_cf12040_cf15206__28 = // StatementAdderMethod cloned existing statement
vc_4598.list(vc_3665);
        // AssertGenerator add assertion
        java.util.ArrayList collection_13270394 = new java.util.ArrayList<Object>();
	junit.framework.Assert.assertEquals(collection_13270394, o_annotatedTwice_cf11584_cf12040_cf15206__28);;
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedType_cf15404() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_4610 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4610);
        // AssertGenerator replace invocation
        boolean o_annotatedType_cf15404__9 = // StatementAdderMethod cloned existing statement
type.equals(vc_4610);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedType_cf15404__9);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedType_cf15464_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_4640 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_4640);
            org.junit.Assert.fail("annotatedType_cf15464 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedType_cf15411_cf16203() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedType_cf15411__7 = // StatementAdderMethod cloned existing statement
type.isAnnotated();
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedType_cf15411__7);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_4826 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4826);
        // AssertGenerator replace invocation
        boolean o_annotatedType_cf15411_cf16203__13 = // StatementAdderMethod cloned existing statement
type.equals(vc_4826);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedType_cf15411_cf16203__13);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedType_cf15419_cf16611_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedType_cf15419__7 = // StatementAdderMethod cloned existing statement
type.isPrimitive();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedType_cf15419__7);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_5006 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_5004 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_5002 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_5002.get(vc_5004, vc_5006);
            org.junit.Assert.fail("annotatedType_cf15419_cf16611 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedType_cf15419_cf16611_failAssert9_literalMutation18336() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, "com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedType_cf15419__7 = // StatementAdderMethod cloned existing statement
type.isPrimitive();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedType_cf15419__7);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_5006 = (java.util.Map)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_5006);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_5004 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_5004);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_5002 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_5002);
            // StatementAdderMethod cloned existing statement
            vc_5002.get(vc_5004, vc_5006);
            org.junit.Assert.fail("annotatedType_cf15419_cf16611 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedType_cf15404_cf15736_failAssert18_literalMutation18487() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" jav!a.lang.String" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " jav!a.lang.Stringcom.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_4610 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4610);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4610);
            // AssertGenerator replace invocation
            boolean o_annotatedType_cf15404__9 = // StatementAdderMethod cloned existing statement
type.equals(vc_4610);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedType_cf15404__9);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_4718 = (java.util.Map)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4718);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_4716 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4716);
            // StatementAdderMethod cloned existing statement
            type.get(vc_4716, vc_4718);
            org.junit.Assert.fail("annotatedType_cf15404_cf15736 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 1000)
    public void annotatedType_cf15404_cf15827_failAssert4_literalMutation18278() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" jaa.lang.String" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " jaa.lang.Stringcom.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_4610 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4610);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4610);
            // AssertGenerator replace invocation
            boolean o_annotatedType_cf15404__9 = // StatementAdderMethod cloned existing statement
type.equals(vc_4610);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedType_cf15404__9);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_4730 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_4730);
            // StatementAdderMethod cloned existing statement
            vc_4730.unbox();
            org.junit.Assert.fail("annotatedType_cf15404_cf15827 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18600_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_5438 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_5436 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_5434 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_5434.get(vc_5436, vc_5438);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf18600 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18534() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5402 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_5402);
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithExtends_cf18534__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_5402);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18534__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18535_cf19104() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_5403 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithExtends_cf18535__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_5403);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18535__10);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5546 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_5546);
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithExtends_cf18535_cf19104__16 = // StatementAdderMethod cloned existing statement
type.equals(vc_5546);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18535_cf19104__16);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18534_cf19022_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_5402 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_5402);
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithExtends_cf18534__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_5402);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18534__10);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_5526 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_5526.hashCode();
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf18534_cf19022 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18592_failAssert20_literalMutation19970() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" java.lang.Strng" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " java.lang.Strngcom.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_5432 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_5432);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_5430 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_5430);
            // StatementAdderMethod cloned existing statement
            vc_5430.get(vc_5432);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf18592 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18535_cf19104_cf21605_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_5403 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithExtends_cf18535__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_5403);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18535__10);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_5546 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_5546);
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithExtends_cf18535_cf19104__16 = // StatementAdderMethod cloned existing statement
type.equals(vc_5546);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18535_cf19104__16);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_6272 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_6272.isPrimitive();
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf18535_cf19104_cf21605 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18653_cf20398_cf21116() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
        // AssertGenerator replace invocation
        java.lang.String o_annotatedWildcardTypeNameWithExtends_cf18653__8 = // StatementAdderMethod cloned existing statement
type.toString();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_annotatedWildcardTypeNameWithExtends_cf18653__8, "@com.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
        // AssertGenerator replace invocation
        int o_annotatedWildcardTypeNameWithExtends_cf18653_cf20398__12 = // StatementAdderMethod cloned existing statement
type.hashCode();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_annotatedWildcardTypeNameWithExtends_cf18653_cf20398__12, 1362420326);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_6122 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_6122);
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithExtends_cf18653_cf20398_cf21116__18 = // StatementAdderMethod cloned existing statement
type.equals(vc_6122);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18653_cf20398_cf21116__18);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithExtends_cf18549_cf19868_cf20790_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithExtends_cf18549__8 = // StatementAdderMethod cloned existing statement
type.isPrimitive();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf18549__8);
            // AssertGenerator replace invocation
            int o_annotatedWildcardTypeNameWithExtends_cf18549_cf19868__12 = // StatementAdderMethod cloned existing statement
type.hashCode();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_annotatedWildcardTypeNameWithExtends_cf18549_cf19868__12, 1362420326);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_6014 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_6012 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_6012, vc_6014);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf18549_cf19868_cf20790 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithSuper_cf22246_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_6440 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_6440);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf22246 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithSuper_cf22186() {
        java.lang.String expected = ("? super @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_6410 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_6410);
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithSuper_cf22186__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_6410);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithSuper_cf22186__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithSuper_cf22186_cf22566_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_6410 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6410);
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithSuper_cf22186__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_6410);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithSuper_cf22186__10);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_6518 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_6516 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_6514 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_6514.get(vc_6516, vc_6518);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf22186_cf22566 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithSuper_cf22301_cf23828_failAssert10_literalMutation24582() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" java.lang.Sring" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " java.lang.Sringcom.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // AssertGenerator replace invocation
            int o_annotatedWildcardTypeNameWithSuper_cf22301__8 = // StatementAdderMethod cloned existing statement
type.hashCode();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_annotatedWildcardTypeNameWithSuper_cf22301__8, 1362420326);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_6878 = (java.util.Map)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6878);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_6876 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6876);
            // StatementAdderMethod cloned existing statement
            type.get(vc_6876, vc_6878);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf22301_cf23828 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithSuper_cf22186_cf22557_failAssert7_add24364() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_6410 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6410);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6410);
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithSuper_cf22186__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_6410);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithSuper_cf22186__10);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_6512 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6512);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            type.get(vc_6512);
            // StatementAdderMethod cloned existing statement
            type.get(vc_6512);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf22186_cf22557 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 1000)
    public void annotatedWildcardTypeNameWithSuper_cf22186_cf22550_failAssert12_literalMutation24613() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = (" java.lang.tring" + (com.squareup.javapoet.AnnotatedTypeNameTest.NN)) + " java.lang.String";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expected, " java.lang.tringcom.squareup.javapoet.AnnotatedTypeNameTest.NeverNull java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_6410 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6410);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6410);
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithSuper_cf22186__10 = // StatementAdderMethod cloned existing statement
type.equals(vc_6410);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_annotatedWildcardTypeNameWithSuper_cf22186__10);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_6508 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_6508);
            // StatementAdderMethod cloned existing statement
            vc_6508.box();
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf22186_cf22550 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#nullAnnotationArray */
    @org.junit.Test(timeout = 1000)
    public void nullAnnotationArray_add24969_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            com.squareup.javapoet.TypeName.BOOLEAN.annotated(((com.squareup.javapoet.AnnotationSpec[]) (null)));
            com.squareup.javapoet.TypeName.BOOLEAN.annotated(((com.squareup.javapoet.AnnotationSpec[]) (null)));
            org.junit.Assert.fail("nullAnnotationArray_add24969 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#nullAnnotationList */
    @org.junit.Test(timeout = 1000)
    public void nullAnnotationList_add24972_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            com.squareup.javapoet.TypeName.DOUBLE.annotated(((java.util.List<com.squareup.javapoet.AnnotationSpec>) (null)));
            com.squareup.javapoet.TypeName.DOUBLE.annotated(((java.util.List<com.squareup.javapoet.AnnotationSpec>) (null)));
            org.junit.Assert.fail("nullAnnotationList_add24972 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

