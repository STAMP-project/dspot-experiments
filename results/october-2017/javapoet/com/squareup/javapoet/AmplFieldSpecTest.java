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


public class AmplFieldSpecTest {
    @org.junit.Test
    public void equalsAndHashCode() {
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @org.junit.Test
    public void nullAnnotationsAddition() {
        try {
            com.squareup.javapoet.FieldSpec.builder(int.class, "foo").addAnnotations(null);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            com.google.common.truth.Truth.assertThat(expected.getMessage()).isEqualTo("annotationSpecs == null");
        }
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd48() {
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd48__27 = // StatementAdd: add invocation of a method
        a.hashCode();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_sd48__27)));
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_literalMutationString3_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "f}oo").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutationString3 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd47() {
        java.lang.Object __DSPOT_o_0 = new java.lang.Object();
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd47__29 = // StatementAdd: add invocation of a method
        a.equals(__DSPOT_o_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_equalsAndHashCode_sd47__29);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_add41_add454() {
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_add41__23 = // MethodCallAdder
        a.hashCode();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_add41_add454__27 = // MethodCallAdder
        a.hashCode();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_add41_add454__27)));
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd54 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd54_add824() {
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        // MethodCallAdder
        com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd54__27 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd54__27);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd54 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd54_sd849() {
        java.lang.Object __DSPOT_o_25 = new java.lang.Object();
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd54__27 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd54_sd849__33 = // StatementAdd: add invocation of a method
        b.equals(__DSPOT_o_25);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_equalsAndHashCode_sd54_sd849__33);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd54__27);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_add41_literalMutationString418_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "V!3").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            // AssertGenerator create local variable with return value of invocation
            int o_equalsAndHashCode_add41__23 = // MethodCallAdder
            a.hashCode();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_add41_literalMutationString418 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd54 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd54_sd845 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd54_sd845_add2293() {
        java.lang.Object __DSPOT_o_24 = new java.lang.Object();
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd54__27 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd54__27);
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd54_sd845_add2293__33 = // StatementAdd: add invocation of a method
        // MethodCallAdder
        a.equals(__DSPOT_o_24);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_equalsAndHashCode_sd54_sd845_add2293__33);
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd54_sd845__33 = // StatementAdd: add invocation of a method
        a.equals(__DSPOT_o_24);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_equalsAndHashCode_sd54_sd845_add2293__33);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd54__27);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd50 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd50_add668 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd50_add668_sd2421() {
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        // MethodCallAdder
        com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd50__27 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd50_add668_sd2421__34 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd50_add668_sd2421__34);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd50__27);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd48 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd48_sd632 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd48_sd632_sd3194() {
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd48__27 = // StatementAdd: add invocation of a method
        a.hashCode();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd48_sd632__31 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd48_sd632__31);
        // StatementAdd: add invocation of a method
        b.toBuilder();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd48_sd632__31);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd54 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd54_add837 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd54_add837_add3451() {
        com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd54_add837_add3451__10 = // MethodCallAdder
        a.hashCode();
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
        com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
        // MethodCallAdder
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_equalsAndHashCode_sd54__27 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd54__27);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_sd54_add837_add3451__10)));
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd50 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd50_add668 */
    @org.junit.Test(timeout = 10000)
    public void equalsAndHashCode_sd50_add668_literalMutationString2365_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "f]oo").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            // MethodCallAdder
            com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_equalsAndHashCode_sd50__27 = // StatementAdd: add invocation of a method
            a.toString();
            org.junit.Assert.fail("equalsAndHashCode_sd50_add668_literalMutationString2365 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

