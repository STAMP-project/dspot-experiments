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
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedTwice() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    @org.junit.Test
    public void annotatedParameterizedType() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
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
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
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
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.util.Map.Entry.class).annotated(typeUseAnnotation);
        java.lang.String actual = type.toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    // https://github.com/square/javapoet/issues/431
    @org.junit.Ignore
    @org.junit.Test
    public void annotatedNestedParameterizedType() {
        java.lang.String expected = ("java.util.Map.@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.TypeUseAnnotation.class.getCanonicalName())) + " Entry<java.lang.Byte, java.lang.Byte>";
        com.squareup.javapoet.AnnotationSpec typeUseAnnotation = com.squareup.javapoet.AnnotationSpec.builder(com.squareup.javapoet.AmplAnnotatedTypeNameTest.TypeUseAnnotation.class).build();
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.Map.Entry.class, java.lang.Byte.class, java.lang.Byte.class).annotated(typeUseAnnotation);
        java.lang.String actual = type.toString();
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf46_failAssert10() {
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
            org.junit.Assert.fail("annotated_cf46 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf66_failAssert15() {
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
            javax.lang.model.type.TypeMirror vc_42 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            simpleString.get(vc_42);
            // MethodAssertGenerator build local variable
            Object o_15_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf66 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf70_failAssert16() {
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
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_48 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_46 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_44 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_44.get(vc_46, vc_48);
            // MethodAssertGenerator build local variable
            Object o_19_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf70 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf83_failAssert19() {
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
            // StatementAdderMethod cloned existing statement
            annotated.unbox();
            // MethodAssertGenerator build local variable
            Object o_13_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf83 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf11() {
        com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        org.junit.Assert.assertFalse(simpleString.isAnnotated());
        org.junit.Assert.assertEquals(simpleString, com.squareup.javapoet.TypeName.get(java.lang.String.class));
        com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
        org.junit.Assert.assertTrue(annotated.isAnnotated());
        // AssertGenerator replace invocation
        boolean o_annotated_cf11__11 = // StatementAdderMethod cloned existing statement
                annotated.isBoxedPrimitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotated_cf11__11);
        org.junit.Assert.assertEquals(annotated, annotated.annotated());
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
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_annotated_cf3__13 = // StatementAdderMethod cloned existing statement
                annotated.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotated_cf3__13);
        org.junit.Assert.assertEquals(annotated, annotated.annotated());
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf14_cf552_failAssert16() {
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
            boolean o_annotated_cf14__11 = // StatementAdderMethod cloned existing statement
                    annotated.isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_annotated_cf14__11;
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_264 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_262 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_260 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_260.get(vc_262, vc_264);
            // MethodAssertGenerator build local variable
            Object o_23_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf14_cf552 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf3_cf206_failAssert34() {
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
            // MethodAssertGenerator build local variable
            Object o_13_0 = vc_2;
            // AssertGenerator replace invocation
            boolean o_annotated_cf3__13 = // StatementAdderMethod cloned existing statement
                    annotated.equals(vc_2);
            // MethodAssertGenerator build local variable
            Object o_17_0 = o_annotated_cf3__13;
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
            org.junit.Assert.fail("annotated_cf3_cf206 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotated */
    @org.junit.Test(timeout = 10000)
    public void annotated_cf89_cf663_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = 1362420326;
            com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            // MethodAssertGenerator build local variable
            Object o_3_0 = simpleString.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_5_0 = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            com.squareup.javapoet.TypeName annotated = simpleString.annotated(NEVER_NULL);
            // MethodAssertGenerator build local variable
            Object o_9_0 = annotated.isAnnotated();
            // AssertGenerator replace invocation
            int o_annotated_cf89__11 = // StatementAdderMethod cloned existing statement
                    annotated.hashCode();
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_annotated_cf89__11;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_330 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_328 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_328.get(vc_330);
            // MethodAssertGenerator build local variable
            Object o_21_0 = annotated.annotated();
            org.junit.Assert.fail("annotated_cf89_cf663 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf927() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf927__10 = // StatementAdderMethod cloned existing statement
                type.isBoxedPrimitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf927__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf919() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_434 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_434);
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf919__12 = // StatementAdderMethod cloned existing statement
                type.equals(vc_434);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf919__12);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf982_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_474 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_474);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf982 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf999_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // StatementAdderMethod cloned existing statement
            type.unbox();
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf999 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf962_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_464 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_462 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_462.get(vc_464);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf962 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf920() {
        java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
        java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_435 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedArgumentOfParameterizedType_cf920__12 = // StatementAdderMethod cloned existing statement
                type.equals(vc_435);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedArgumentOfParameterizedType_cf920__12);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf920_cf1429_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_435 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_annotatedArgumentOfParameterizedType_cf920__12 = // StatementAdderMethod cloned existing statement
                    type.equals(vc_435);
            // MethodAssertGenerator build local variable
            Object o_14_0 = o_annotatedArgumentOfParameterizedType_cf920__12;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_618 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_618);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf920_cf1429 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf924_cf1605_failAssert31() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedArgumentOfParameterizedType_cf924__10 = // StatementAdderMethod cloned existing statement
                    type.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_annotatedArgumentOfParameterizedType_cf924__10;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_680 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_678 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_678.get(vc_680);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf924_cf1605 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf986_failAssert26_literalMutation2679() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("javautil.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(expected, "javautil.List<@com.squareup.javapoet.AmplAnnotatedTypeNameTest.NeverNull java.lang.String>");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
            java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_480 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_480);
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_478 = (javax.lang.model.type.TypeMirror)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_478);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_476 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_476);
            // StatementAdderMethod cloned existing statement
            vc_476.get(vc_478, vc_480);
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf986 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf968_failAssert22_literalMutation2638_cf4132_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + "HUZo@XT$QA3=&:J/N_";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
                java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_470 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_470;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_468 = (java.lang.reflect.Type)null;
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_468;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_466 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_1770 = (javax.lang.model.type.TypeMirror)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_1768 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_1768.get(vc_1770);
                // MethodAssertGenerator build local variable
                Object o_30_0 = vc_466;
                // StatementAdderMethod cloned existing statement
                vc_466.get(vc_468, vc_470);
                org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf968 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf968_failAssert22_literalMutation2638_cf4132 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf968_failAssert22_literalMutation2638_cf4166_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("java.util.List<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + "HUZo@XT$QA3=&:J/N_";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
                java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_470 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_470;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_468 = (java.lang.reflect.Type)null;
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_468;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_466 = (com.squareup.javapoet.TypeName)null;
                // StatementAddOnAssert local variable replacement
                com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
                // StatementAdderMethod cloned existing statement
                simpleString.unbox();
                // MethodAssertGenerator build local variable
                Object o_29_0 = vc_466;
                // StatementAdderMethod cloned existing statement
                vc_466.get(vc_468, vc_470);
                org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf968 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf968_failAssert22_literalMutation2638_cf4166 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedArgumentOfParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedArgumentOfParameterizedType_cf929_failAssert14_literalMutation2545_cf5426_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("java.util.Lit<@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String>";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                com.squareup.javapoet.ClassName list = com.squareup.javapoet.ClassName.get(java.util.List.class);
                java.lang.String actual = com.squareup.javapoet.ParameterizedTypeName.get(list, type).toString();
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_440 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_2336 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_2334 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_2334.get(vc_2336);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_440;
                // StatementAdderMethod cloned existing statement
                vc_440.isPrimitive();
                org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf929 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("annotatedArgumentOfParameterizedType_cf929_failAssert14_literalMutation2545_cf5426 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8782() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf8782__7 = // StatementAdderMethod cloned existing statement
                type.isBoxedPrimitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedParameterizedType_cf8782__7);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8817_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_3704 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_3702 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_3702.get(vc_3704);
            org.junit.Assert.fail("annotatedParameterizedType_cf8817 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8774() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3674 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3674);
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf8774__9 = // StatementAdderMethod cloned existing statement
                type.equals(vc_3674);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedParameterizedType_cf8774__9);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8775() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3675 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedParameterizedType_cf8775__9 = // StatementAdderMethod cloned existing statement
                type.equals(vc_3675);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedParameterizedType_cf8775__9);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8841_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_3720 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_3718 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_3716 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_3716.get(vc_3718, vc_3720);
            org.junit.Assert.fail("annotatedParameterizedType_cf8841 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8837_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_3714 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_3714);
            org.junit.Assert.fail("annotatedParameterizedType_cf8837 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8854_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderMethod cloned existing statement
            type.unbox();
            org.junit.Assert.fail("annotatedParameterizedType_cf8854 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8835_failAssert22_add10266() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_3714 = (javax.lang.model.type.TypeMirror)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3714);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_3712 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3712);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_3712.get(vc_3714);
            // StatementAdderMethod cloned existing statement
            vc_3712.get(vc_3714);
            org.junit.Assert.fail("annotatedParameterizedType_cf8835 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8860_cf9987_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_9_1 = 2123584667;
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // AssertGenerator replace invocation
            int o_annotatedParameterizedType_cf8860__7 = // StatementAdderMethod cloned existing statement
                    type.hashCode();
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_annotatedParameterizedType_cf8860__7;
            // StatementAdderMethod cloned existing statement
            type.unbox();
            org.junit.Assert.fail("annotatedParameterizedType_cf8860_cf9987 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8863_cf10076_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // AssertGenerator replace invocation
            java.lang.String o_annotatedParameterizedType_cf8863__7 = // StatementAdderMethod cloned existing statement
                    type.toString();
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_annotatedParameterizedType_cf8863__7;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_4280 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_4280);
            org.junit.Assert.fail("annotatedParameterizedType_cf8863_cf10076 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8845_failAssert25_literalMutation10295_cf10601_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<jaKva.lang.String>";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
                java.lang.String actual = type.annotated(NEVER_NULL).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_3720 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_13_0 = vc_3720;
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_3718 = (javax.lang.model.type.TypeMirror)null;
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_4430 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_4428 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_4426 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_4426.get(vc_4428, vc_4430);
                // MethodAssertGenerator build local variable
                Object o_25_0 = vc_3718;
                // StatementAdderMethod cloned existing statement
                type.get(vc_3718, vc_3720);
                org.junit.Assert.fail("annotatedParameterizedType_cf8845 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("annotatedParameterizedType_cf8845_failAssert25_literalMutation10295_cf10601 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8805_failAssert15_literalMutation10212_cf14321_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("o" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List<java.lang.String>";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
                java.lang.String actual = type.annotated(NEVER_NULL).toString();
                // StatementAdderOnAssert create null value
                java.util.List<com.squareup.javapoet.AnnotationSpec> vc_3696 = (java.util.List)null;
                // MethodAssertGenerator build local variable
                Object o_13_0 = vc_3696;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_3694 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                type.unbox();
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_3694;
                // StatementAdderMethod cloned existing statement
                vc_3694.annotated(vc_3696);
                org.junit.Assert.fail("annotatedParameterizedType_cf8805 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("annotatedParameterizedType_cf8805_failAssert15_literalMutation10212_cf14321 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedParameterizedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedParameterizedType_cf8823_failAssert20_literalMutation10254_cf15787_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.util.List0java.lang.String>";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.ParameterizedTypeName.get(java.util.List.class, java.lang.String.class);
                java.lang.String actual = type.annotated(NEVER_NULL).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_3710 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_13_0 = vc_3710;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_3708 = (java.lang.reflect.Type)null;
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_3708;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_3706 = (com.squareup.javapoet.TypeName)null;
                // StatementAddOnAssert local variable replacement
                com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
                // StatementAdderMethod cloned existing statement
                simpleString.unbox();
                // MethodAssertGenerator build local variable
                Object o_26_0 = vc_3706;
                // StatementAdderMethod cloned existing statement
                vc_3706.get(vc_3708, vc_3710);
                org.junit.Assert.fail("annotatedParameterizedType_cf8823 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("annotatedParameterizedType_cf8823_failAssert20_literalMutation10254_cf15787 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16418() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_7130 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7130);
        // AssertGenerator replace invocation
        boolean o_annotatedTwice_cf16418__12 = // StatementAdderMethod cloned existing statement
                type.equals(vc_7130);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedTwice_cf16418__12);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16419() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_7131 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedTwice_cf16419__12 = // StatementAdderMethod cloned existing statement
                type.equals(vc_7131);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedTwice_cf16419__12);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16463_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_7160 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_7160);
            org.junit.Assert.fail("annotatedTwice_cf16463 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16481_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_7170 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_7170);
            org.junit.Assert.fail("annotatedTwice_cf16481 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16498_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // StatementAdderMethod cloned existing statement
            type.unbox();
            org.junit.Assert.fail("annotatedTwice_cf16498 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16426() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedTwice_cf16426__10 = // StatementAdderMethod cloned existing statement
                type.isBoxedPrimitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedTwice_cf16426__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16507_cf17615_failAssert46() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // AssertGenerator replace invocation
            java.lang.String o_annotatedTwice_cf16507__10 = // StatementAdderMethod cloned existing statement
                    type.toString();
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_annotatedTwice_cf16507__10;
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_7680 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_7678 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_7676 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_7676.get(vc_7678, vc_7680);
            org.junit.Assert.fail("annotatedTwice_cf16507_cf17615 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16507_cf17593_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
            // AssertGenerator replace invocation
            java.lang.String o_annotatedTwice_cf16507__10 = // StatementAdderMethod cloned existing statement
                    type.toString();
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_annotatedTwice_cf16507__10;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_7664 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_7664);
            org.junit.Assert.fail("annotatedTwice_cf16507_cf17593 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedTwice */
    @org.junit.Test(timeout = 10000)
    public void annotatedTwice_cf16511_failAssert32_literalMutation18178_cf19554_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " @java.lang.Override java.langx.String";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
                java.lang.String actual = type.annotated(NEVER_NULL).annotated(com.squareup.javapoet.AnnotationSpec.builder(java.lang.Override.class).build()).toString();
                // StatementAdderOnAssert create null value
                java.util.List<com.squareup.javapoet.AnnotationSpec> vc_7188 = (java.util.List)null;
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_8534 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_8532 = (java.lang.reflect.Type)null;
                // StatementAdderMethod cloned existing statement
                type.get(vc_8532, vc_8534);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_7188;
                // StatementAdderMethod cloned existing statement
                type.concatAnnotations(vc_7188);
                org.junit.Assert.fail("annotatedTwice_cf16511 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("annotatedTwice_cf16511_failAssert32_literalMutation18178_cf19554 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21206_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_9264 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_9262 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_9260 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_9260.get(vc_9262, vc_9264);
            org.junit.Assert.fail("annotatedType_cf21206 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21184_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_9248 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_9248);
            org.junit.Assert.fail("annotatedType_cf21184 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21147() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedType_cf21147__7 = // StatementAdderMethod cloned existing statement
                type.isBoxedPrimitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedType_cf21147__7);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21139() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_9218 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9218);
        // AssertGenerator replace invocation
        boolean o_annotatedType_cf21139__9 = // StatementAdderMethod cloned existing statement
                type.equals(vc_9218);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedType_cf21139__9);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21202_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_9258 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_9258);
            org.junit.Assert.fail("annotatedType_cf21202 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21219_failAssert26() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderMethod cloned existing statement
            type.unbox();
            org.junit.Assert.fail("annotatedType_cf21219 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21140() {
        java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
        java.lang.String actual = type.annotated(NEVER_NULL).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_9219 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedType_cf21140__9 = // StatementAdderMethod cloned existing statement
                type.equals(vc_9219);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedType_cf21140__9);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21188_failAssert19_add22480() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_9254 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9254);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_9252 = (java.lang.reflect.Type)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9252);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_9250 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9250);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_9250.get(vc_9252, vc_9254);
            // StatementAdderMethod cloned existing statement
            vc_9250.get(vc_9252, vc_9254);
            org.junit.Assert.fail("annotatedType_cf21188 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21202_failAssert22_add22504() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
            java.lang.String actual = type.annotated(NEVER_NULL).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_9258 = (javax.lang.model.type.TypeMirror)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9258);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            type.get(vc_9258);
            // StatementAdderMethod cloned existing statement
            type.get(vc_9258);
            org.junit.Assert.fail("annotatedType_cf21202 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21192_failAssert20_add22488_cf25319_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("@" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
                java.lang.String actual = type.annotated(NEVER_NULL).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_9254 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_11_0 = vc_9254;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_9252 = (java.lang.reflect.Type)null;
                // StatementAddOnAssert local variable replacement
                com.squareup.javapoet.TypeName simpleString = com.squareup.javapoet.TypeName.get(java.lang.String.class);
                // StatementAdderMethod cloned existing statement
                simpleString.unbox();
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_9252;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                type.get(vc_9252, vc_9254);
                // StatementAdderMethod cloned existing statement
                type.get(vc_9252, vc_9254);
                org.junit.Assert.fail("annotatedType_cf21192 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("annotatedType_cf21192_failAssert20_add22488_cf25319 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedType */
    @org.junit.Test(timeout = 10000)
    public void annotatedType_cf21206_failAssert23_literalMutation22513_cf23139_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class);
                java.lang.String actual = type.annotated(NEVER_NULL).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_9264 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_13_0 = vc_9264;
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_9262 = (javax.lang.model.type.TypeMirror)null;
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_9262;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_9260 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_10112 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_10110 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_10110.get(vc_10112);
                // MethodAssertGenerator build local variable
                Object o_27_0 = vc_9260;
                // StatementAdderMethod cloned existing statement
                vc_9260.get(vc_9262, vc_9264);
                org.junit.Assert.fail("annotatedType_cf21206 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("annotatedType_cf21206_failAssert23_literalMutation22513_cf23139 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf27961() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_12387 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithExtends_cf27961__10 = // StatementAdderMethod cloned existing statement
                type.equals(vc_12387);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf27961__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf27960() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_12386 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_12386);
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithExtends_cf27960__10 = // StatementAdderMethod cloned existing statement
                type.equals(vc_12386);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf27960__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf28040_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderMethod cloned existing statement
            type.unbox();
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28040 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf28009_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_12422 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_12420 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_12418 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_12418.get(vc_12420, vc_12422);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28009 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf28023_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_12426 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_12426);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28023 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf27968() {
        java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithExtends_cf27968__8 = // StatementAdderMethod cloned existing statement
                type.isBoxedPrimitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedWildcardTypeNameWithExtends_cf27968__8);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf28046_cf29029_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_10_1 = 1362420326;
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // AssertGenerator replace invocation
            int o_annotatedWildcardTypeNameWithExtends_cf28046__8 = // StatementAdderMethod cloned existing statement
                    type.hashCode();
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_annotatedWildcardTypeNameWithExtends_cf28046__8;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_12848 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_12848);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28046_cf29029 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf27960_cf28212_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_12386 = (java.lang.Object)null;
            // MethodAssertGenerator build local variable
            Object o_10_0 = vc_12386;
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithExtends_cf27960__10 = // StatementAdderMethod cloned existing statement
                    type.equals(vc_12386);
            // MethodAssertGenerator build local variable
            Object o_14_0 = o_annotatedWildcardTypeNameWithExtends_cf27960__10;
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_12498 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_12498);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf27960_cf28212 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf28027_failAssert26_literalMutation29405() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? e{xtends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(expected, "? e{xtends @com.squareup.javapoet.AmplAnnotatedTypeNameTest.NeverNull java.lang.String");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_12432 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_12432);
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_12430 = (javax.lang.model.type.TypeMirror)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_12430);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_12428 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_12428);
            // StatementAdderMethod cloned existing statement
            vc_12428.get(vc_12430, vc_12432);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28027 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf28013_failAssert23_literalMutation29375_cf35869_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("? extends @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + "";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_12422 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_12422;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_12420 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_16032 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_16030 = (javax.lang.model.type.TypeMirror)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_16028 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_16028.get(vc_16030, vc_16032);
                // MethodAssertGenerator build local variable
                Object o_26_0 = vc_12420;
                // StatementAdderMethod cloned existing statement
                type.get(vc_12420, vc_12422);
                org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28013 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28013_failAssert23_literalMutation29375_cf35869 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithExtends */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithExtends_cf28045_failAssert31_literalMutation29460_cf33137_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("? extens @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                java.lang.String actual = com.squareup.javapoet.WildcardTypeName.subtypeOf(type).toString();
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_12438 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_14798 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_14796 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_14794 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_14794.get(vc_14796, vc_14798);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_12438;
                // StatementAdderMethod cloned existing statement
                vc_12438.hashCode();
                org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28045 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("annotatedWildcardTypeNameWithExtends_cf28045_failAssert31_literalMutation29460_cf33137 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36372() {
        java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_16131 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithSuper_cf36372__10 = // StatementAdderMethod cloned existing statement
                type.equals(vc_16131);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedWildcardTypeNameWithSuper_cf36372__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36379() {
        java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithSuper_cf36379__8 = // StatementAdderMethod cloned existing statement
                type.isBoxedPrimitive();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedWildcardTypeNameWithSuper_cf36379__8);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36451_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderMethod cloned existing statement
            type.unbox();
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36451 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36420_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_16166 = (java.util.Map)null;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_16164 = (java.lang.reflect.Type)null;
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_16162 = (com.squareup.javapoet.TypeName)null;
            // StatementAdderMethod cloned existing statement
            vc_16162.get(vc_16164, vc_16166);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36420 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36434_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_16170 = (javax.lang.model.type.TypeMirror)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_16170);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36434 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36371() {
        java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
        com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
        java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_16130 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16130);
        // AssertGenerator replace invocation
        boolean o_annotatedWildcardTypeNameWithSuper_cf36371__10 = // StatementAdderMethod cloned existing statement
                type.equals(vc_16130);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_annotatedWildcardTypeNameWithSuper_cf36371__10);
        org.junit.Assert.assertEquals(expected, actual);
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36376_cf37059_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // AssertGenerator replace invocation
            boolean o_annotatedWildcardTypeNameWithSuper_cf36376__8 = // StatementAdderMethod cloned existing statement
                    type.isAnnotated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_annotatedWildcardTypeNameWithSuper_cf36376__8;
            // StatementAdderOnAssert create null value
            java.lang.reflect.Type vc_16376 = (java.lang.reflect.Type)null;
            // StatementAdderMethod cloned existing statement
            type.get(vc_16376);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36376_cf37059 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36432_failAssert24_literalMutation37798() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(expected, "? super @com.squareup.javapoet.AmplAnnotatedTypeNameTest.NeverNull");
            com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
            java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
            // StatementAdderOnAssert create null value
            javax.lang.model.type.TypeMirror vc_16170 = (javax.lang.model.type.TypeMirror)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_16170);
            // StatementAdderOnAssert create null value
            com.squareup.javapoet.TypeName vc_16168 = (com.squareup.javapoet.TypeName)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_16168);
            // StatementAdderMethod cloned existing statement
            vc_16168.get(vc_16170);
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36432 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36424_failAssert23_literalMutation37781_cf43041_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
                // MethodAssertGenerator build local variable
                Object o_4_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.reflect.Type, com.squareup.javapoet.TypeVariableName> vc_16166 = (java.util.Map)null;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_16166;
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_16164 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                java.util.Map<javax.lang.model.element.TypeParameterElement, com.squareup.javapoet.TypeVariableName> vc_19200 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_19198 = (javax.lang.model.type.TypeMirror)null;
                // StatementAdderMethod cloned existing statement
                type.get(vc_19198, vc_19200);
                // MethodAssertGenerator build local variable
                Object o_24_0 = vc_16164;
                // StatementAdderMethod cloned existing statement
                type.get(vc_16164, vc_16166);
                org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36424 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36424_failAssert23_literalMutation37781_cf43041 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36434_failAssert25_add37802_cf43349_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.String";
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
                // StatementAdderOnAssert create null value
                javax.lang.model.type.TypeMirror vc_16170 = (javax.lang.model.type.TypeMirror)null;
                // StatementAdderMethod cloned existing statement
                type.unbox();
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_16170;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                type.get(vc_16170);
                // StatementAdderMethod cloned existing statement
                type.get(vc_16170);
                org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36434 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36434_failAssert25_add37802_cf43349 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#annotatedWildcardTypeNameWithSuper */
    @org.junit.Test(timeout = 10000)
    public void annotatedWildcardTypeNameWithSuper_cf36451_failAssert29_literalMutation37853_cf39954_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String expected = ("? super @" + (com.squareup.javapoet.AmplAnnotatedTypeNameTest.NN)) + " java.lang.Sring";
                // StatementAdderOnAssert create null value
                java.lang.reflect.Type vc_17672 = (java.lang.reflect.Type)null;
                // StatementAdderOnAssert create null value
                com.squareup.javapoet.TypeName vc_17670 = (com.squareup.javapoet.TypeName)null;
                // StatementAdderMethod cloned existing statement
                vc_17670.get(vc_17672);
                // MethodAssertGenerator build local variable
                Object o_10_0 = expected;
                com.squareup.javapoet.TypeName type = com.squareup.javapoet.TypeName.get(java.lang.String.class).annotated(NEVER_NULL);
                java.lang.String actual = com.squareup.javapoet.WildcardTypeName.supertypeOf(type).toString();
                // StatementAdderMethod cloned existing statement
                type.unbox();
                org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36451 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("annotatedWildcardTypeNameWithSuper_cf36451_failAssert29_literalMutation37853_cf39954 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#nullAnnotationArray */
    @org.junit.Test(timeout = 10000)
    public void nullAnnotationArray_add44749_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            com.squareup.javapoet.TypeName.BOOLEAN.annotated(((com.squareup.javapoet.AnnotationSpec[]) (null)));
            com.squareup.javapoet.TypeName.BOOLEAN.annotated(((com.squareup.javapoet.AnnotationSpec[]) (null)));
            org.junit.Assert.fail("nullAnnotationArray_add44749 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.AnnotatedTypeNameTest#nullAnnotationList */
    @org.junit.Test(timeout = 10000)
    public void nullAnnotationList_add44758_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            com.squareup.javapoet.TypeName.DOUBLE.annotated(((java.util.List<com.squareup.javapoet.AnnotationSpec>) (null)));
            com.squareup.javapoet.TypeName.DOUBLE.annotated(((java.util.List<com.squareup.javapoet.AnnotationSpec>) (null)));
            org.junit.Assert.fail("nullAnnotationList_add44758 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

