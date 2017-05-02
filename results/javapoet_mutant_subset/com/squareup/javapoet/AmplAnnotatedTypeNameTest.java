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
    private static final java.lang.String NN = com.squareup.javapoet.AmplAnnotatedTypeNameTest.NeverNull.class.getCanonicalName();

    private final com.squareup.javapoet.AnnotationSpec NEVER_NULL = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AmplAnnotatedTypeNameTest.NeverNull.class).build();

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
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedTwice() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedParameterizedType() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List., java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedArgumentOfParameterizedType() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedWildcardTypeNameWithSuper() {
        java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedWildcardTypeNameWithExtends() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
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
        java.lang.String expected = ("java.util.Map.@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.TypeUseAnnotation.class.getCanonicalName())) + " Entry";
        com.squareup.javapoet.AnnotationSpec typeUseAnnotation = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AmplAnnotatedTypeNameTest.TypeUseAnnotation.class).build();
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.util.Map.Entry.).annotated(typeUseAnnotation);
        java.lang.String actual = type.toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    // https://github.com/square/javapoet/issues/431
    @org.junit.Ignore
    @org.junit.Test
    public void annotatedNestedParameterizedType() {
        java.lang.String expected = ("java.util.Map.@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.TypeUseAnnotation.class.getCanonicalName())) + " Entry<java.lang.Byte, java.lang.Byte>";
        com.squareup.javapoet.AnnotationSpec typeUseAnnotation = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AmplAnnotatedTypeNameTest.TypeUseAnnotation.class).build();
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.Map.Entry., java.lang.Byte.class, java.lang.Byte.class).annotated(typeUseAnnotation);
        java.lang.String actual = type.toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf52_failAssert12() {
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
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_38 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_36 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_34 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_34.get(vc_36, vc_38);
            // MethodAssertGenerator build local variable
            Object o_19_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf52 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf3() {
        com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        org.junit.Assert.assertFalse(simpleString.isAnnotated());
        org.junit.Assert.assertEquals(simpleString, com.squareup.javapoet.TypeName.get(java.lang.String.class));
        com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
        org.junit.Assert.assertTrue(annotated.isAnnotated());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator replace invocation
        boolean o_annotated_cf3__13 = // StatementAdderMethod cloned existing statement
annotated.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotated_cf3__13);
        org.junit.Assert.assertEquals(annotated, annotated.annotated());
    }
}

