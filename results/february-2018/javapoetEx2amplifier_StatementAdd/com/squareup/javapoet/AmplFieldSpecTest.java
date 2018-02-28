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


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplFieldSpecTest {
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd7() {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd7__13 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator add assertion
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_sd7__13)));
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5() {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__13 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd45() {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__13 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd5_sd45__17 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator add assertion
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_sd5_sd45__17)));
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd3 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd3_sd35() {
        Object __DSPOT_o_3 = new Object();
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd3__13 = // StatementAdd: add invocation of a method
        a.hashCode();
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd3_sd35__19 = // StatementAdd: add invocation of a method
        b.equals(__DSPOT_o_3);
        // AssertGenerator add assertion
        Assert.assertFalse(o_equalsAndHashCode_sd3_sd35__19);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd9 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9_sd60() {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__13 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd9__13);
        // StatementAdd: add invocation of a method
        a.toBuilder();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd9__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5_sd46 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd46_sd266() {
        Object[] __DSPOT_args_40 = new Object[0];
        String __DSPOT_format_39 = "SO/woO!OKS@Rl&{ha!&B";
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__13 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
        // StatementAdd: generate variable from return value
        FieldSpec.Builder __DSPOT_invoc_19 = // StatementAdd: add invocation of a method
        b.toBuilder();
        // StatementAdd: add invocation of a method
        __DSPOT_invoc_19.addJavadoc(__DSPOT_format_39, __DSPOT_args_40);
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd9 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd9_sd63 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9_sd63_sd348() {
        Object __DSPOT_o_65 = new Object();
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__13 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd9_sd63__17 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd9_sd63_sd348__23 = // StatementAdd: add invocation of a method
        a.equals(__DSPOT_o_65);
        // AssertGenerator add assertion
        Assert.assertFalse(o_equalsAndHashCode_sd9_sd63_sd348__23);
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd9__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5_sd42 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd42_sd225_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            Object[] __DSPOT_args_27 = new Object[0];
            String __DSPOT_format_26 = "[{$QV5:Wz2[|+mr6#-Vt";
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            // AssertGenerator create local variable with return value of invocation
            String o_equalsAndHashCode_sd5__13 = // StatementAdd: add invocation of a method
            a.toString();
            // StatementAdd: generate variable from return value
            FieldSpec.Builder __DSPOT_invoc_17 = // StatementAdd: add invocation of a method
            a.toBuilder();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_17.addJavadoc(__DSPOT_format_26, __DSPOT_args_27);
            org.junit.Assert.fail("equalsAndHashCode_sd5_sd42_sd225 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd9 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd9_sd60 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9_sd60_sd337() {
        Object[] __DSPOT_args_62 = new Object[]{ new Object(), new Object(), new Object(), new Object() };
        String __DSPOT_format_61 = " V!3a(!.#b{[Iz>YSe|%";
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__13 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd9__13);
        // StatementAdd: generate variable from return value
        FieldSpec.Builder __DSPOT_invoc_19 = // StatementAdd: add invocation of a method
        a.toBuilder();
        // StatementAdd: add invocation of a method
        __DSPOT_invoc_19.initializer(__DSPOT_format_61, __DSPOT_args_62);
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd9__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5_sd45 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd45_sd253() {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__13 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd5_sd45__17 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd5_sd45_sd253__21 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator add assertion
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_sd5_sd45_sd253__21)));
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd9 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd9_sd60 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9_sd60_sd334() {
        Object[] __DSPOT_args_59 = new Object[]{ new Object(), new Object(), new Object() };
        String __DSPOT_format_58 = ",xavU[1Rvnj|}8wu]&8(";
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__13 = // StatementAdd: add invocation of a method
        b.toString();
        // StatementAdd: generate variable from return value
        FieldSpec.Builder __DSPOT_invoc_19 = // StatementAdd: add invocation of a method
        a.toBuilder();
        // StatementAdd: add invocation of a method
        __DSPOT_invoc_19.addJavadoc(__DSPOT_format_58, __DSPOT_args_59);
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd9__13);
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode_sd5_sd45 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd45_sd254() {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__13 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd5_sd45__17 = // StatementAdd: add invocation of a method
        b.hashCode();
        // StatementAdd: add invocation of a method
        b.toBuilder();
        // AssertGenerator add assertion
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd5__13);
    }
}

