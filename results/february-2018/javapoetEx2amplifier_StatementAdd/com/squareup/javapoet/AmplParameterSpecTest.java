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


public class AmplParameterSpecTest {
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd7() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd7__15 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator add assertion
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd7__15)));
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__15 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd7 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd7_sd56() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd7__15 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd7_sd56__19 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd7_sd56__19);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd9 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9_sd61() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__15 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9_sd61__19 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9_sd61__19);
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd45() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__15 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd5_sd45__19 = // StatementAdd: add invocation of a method
        b.hashCode();
        // AssertGenerator add assertion
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd5_sd45__19)));
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd44() {
        Object __DSPOT_o_5 = new Object();
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__15 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd5_sd44__21 = // StatementAdd: add invocation of a method
        b.equals(__DSPOT_o_5);
        // AssertGenerator add assertion
        Assert.assertFalse(o_equalsAndHashCode_sd5_sd44__21);
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd9 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9_sd60() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__15 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9__15);
        // StatementAdd: add invocation of a method
        a.toBuilder();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5_sd46 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd46_sd256() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__15 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
        // StatementAdd: add invocation of a method
        b.toBuilder();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd5_sd46_sd256__21 = // StatementAdd: add invocation of a method
        a.hashCode();
        // AssertGenerator add assertion
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd5_sd46_sd256__21)));
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5_sd46 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd46_sd255() {
        Object __DSPOT_o_33 = new Object();
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__15 = // StatementAdd: add invocation of a method
        a.toString();
        // StatementAdd: add invocation of a method
        b.toBuilder();
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd5_sd46_sd255__23 = // StatementAdd: add invocation of a method
        a.equals(__DSPOT_o_33);
        // AssertGenerator add assertion
        Assert.assertFalse(o_equalsAndHashCode_sd5_sd46_sd255__23);
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5_sd44 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd44_sd243() {
        Object __DSPOT_o_5 = new Object();
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__15 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator create local variable with return value of invocation
        boolean o_equalsAndHashCode_sd5_sd44__21 = // StatementAdd: add invocation of a method
        b.equals(__DSPOT_o_5);
        // StatementAdd: add invocation of a method
        b.toBuilder();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd9 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd9_sd61 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd9_sd61_sd340() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9__15 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9_sd61__19 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9_sd61__19);
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd9_sd61_sd340__23 = // StatementAdd: add invocation of a method
        b.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9_sd61_sd340__23);
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9_sd61__19);
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd9__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5_sd45 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd45_sd252() {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__15 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator create local variable with return value of invocation
        int o_equalsAndHashCode_sd5_sd45__19 = // StatementAdd: add invocation of a method
        b.hashCode();
        // StatementAdd: add invocation of a method
        b.toBuilder();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
    }

    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5 */
    /* amplification of com.squareup.javapoet.ParameterSpecTest#equalsAndHashCode_sd5_sd42 */
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd5_sd42_sd225() {
        Modifier[] __DSPOT_modifiers_26 = new Modifier[]{  };
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        // AssertGenerator create local variable with return value of invocation
        String o_equalsAndHashCode_sd5__15 = // StatementAdd: add invocation of a method
        a.toString();
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
        // StatementAdd: generate variable from return value
        ParameterSpec.Builder __DSPOT_invoc_19 = // StatementAdd: add invocation of a method
        a.toBuilder();
        // StatementAdd: add invocation of a method
        __DSPOT_invoc_19.addModifiers(__DSPOT_modifiers_26);
        // AssertGenerator add assertion
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd5__15);
    }
}

