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


public class AmplParameterSpecTest {
    @org.junit.Test
    public void equalsAndHashCode() {
        com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @org.junit.Test
    public void nullAnnotationsAddition() {
        try {
            com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").addAnnotations(null);
            org.junit.Assert.fail();
        } catch (java.lang.Exception e) {
            com.google.common.truth.Truth.assertThat(e.getMessage()).isEqualTo("annotationSpecs == null");
        }
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd44() {
        com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd44__29 = // StatementAdd: add invocation of a method
        a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd44__29)));
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd43() {
        java.lang.Object __DSPOT_o_0 = new java.lang.Object();
        com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd43__31 = // StatementAdd: add invocation of a method
        a.equals(__DSPOT_o_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_equalsAndHashCode_sd43__31);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_literalMutationString1_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "").build();
            com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutationString1 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd47 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd47_sd689() {
        java.lang.Object __DSPOT_o_21 = new java.lang.Object();
        java.lang.Object __DSPOT_o_1 = new java.lang.Object();
        com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd47__31 = // StatementAdd: add invocation of a method
        b.equals(__DSPOT_o_1);
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd47_sd689__37 = // StatementAdd: add invocation of a method
        b.equals(__DSPOT_o_21);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_equalsAndHashCode_sd47_sd689__37);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd48 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd48_sd739() {
        com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd48__29 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd48_sd739__33 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("static int i", o_equalsAndHashCode_sd48_sd739__33);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_literalMutationString16_failAssert10_add1277() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            // AssertGenerator create local variable with return value of invocation
            int o_equalsAndHashCode_literalMutationString16_failAssert10_add1277__12 = // MethodCallAdder
            a.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_literalMutationString16_failAssert10_add1277__12)));
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.ParameterSpec.builder(int.class, "").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutationString16 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd43 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd43_literalMutationString489_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.Object __DSPOT_o_0 = new java.lang.Object();
            com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "").build();
            com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            // AssertGenerator create local variable with return value of invocation
            boolean o_equalsAndHashCode_sd43__31 = // StatementAdd: add invocation of a method
            a.equals(__DSPOT_o_0);
            org.junit.Assert.fail("equalsAndHashCode_sd43_literalMutationString489 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd50 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd50_sd793() {
        com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd50__29 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("static int i", o_equalsAndHashCode_sd50__29);
        // StatementAdd: add invocation of a method
        b.toBuilder();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("static int i", o_equalsAndHashCode_sd50__29);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd46 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd46_sd636 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd46_sd636_sd2977() {
        java.lang.Object __DSPOT_o_92 = new java.lang.Object();
        com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd46__29 = // StatementAdd: add invocation of a method
        a.toString();
        // StatementAdd: add invocation of a method
        a.toBuilder();
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd46_sd636_sd2977__37 = // StatementAdd: add invocation of a method
        b.equals(__DSPOT_o_92);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_equalsAndHashCode_sd46_sd636_sd2977__37);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("static int i", o_equalsAndHashCode_sd46__29);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd46 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd46_add610 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd46_add610_literalMutationString2362_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            com.squareup.javapoet.ParameterSpec.builder(int.class, "m|(");
            com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_equalsAndHashCode_sd46__29 = // StatementAdd: add invocation of a method
            a.toString();
            org.junit.Assert.fail("equalsAndHashCode_sd46_add610_literalMutationString2362 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd47 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd47_literalMutationString658 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd47_literalMutationString658_failAssert22_add4366() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.Object __DSPOT_o_1 = new java.lang.Object();
            com.squareup.javapoet.ParameterSpec a = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.squareup.javapoet.ParameterSpec b = com.squareup.javapoet.ParameterSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            // AssertGenerator create local variable with return value of invocation
            int o_equalsAndHashCode_sd47_literalMutationString658_failAssert22_add4366__14 = // MethodCallAdder
            b.hashCode();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_sd47_literalMutationString658_failAssert22_add4366__14)));
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.ParameterSpec.builder(int.class, "i").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.ParameterSpec.builder(int.class, "\n.build()$<$<").addModifiers(javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            // AssertGenerator create local variable with return value of invocation
            boolean o_equalsAndHashCode_sd47__31 = // StatementAdd: add invocation of a method
            b.equals(__DSPOT_o_1);
            org.junit.Assert.fail("equalsAndHashCode_sd47_literalMutationString658 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

