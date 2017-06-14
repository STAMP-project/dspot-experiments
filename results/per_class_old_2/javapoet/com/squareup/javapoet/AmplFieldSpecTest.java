

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

    @org.junit.Test
    public void equalsAndHashCode_literalMutation6_failAssert1() {
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "f}oo").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutation6 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    @org.junit.Test
    public void equalsAndHashCode_literalMutation13_failAssert8() {
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "@!x").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutation13 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    @org.junit.Test
    public void equalsAndHashCode_literalMutation20_failAssert15() {
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, " OO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutation20 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    @org.junit.Test
    public void equalsAndHashCode_literalMutation5_failAssert0() {
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutation5 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.squareup.javapoet.FieldSpecTest#equalsAndHashCode */
    @org.junit.Test
    public void equalsAndHashCode_literalMutation22_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.javapoet.FieldSpec a = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.squareup.javapoet.FieldSpec b = com.squareup.javapoet.FieldSpec.builder(int.class, "foo").build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            a = com.squareup.javapoet.FieldSpec.builder(int.class, "FOO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            b = com.squareup.javapoet.FieldSpec.builder(int.class, "F[OO", javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC).build();
            com.google.common.truth.Truth.assertThat(a.equals(b)).isTrue();
            com.google.common.truth.Truth.assertThat(a.hashCode()).isEqualTo(b.hashCode());
            org.junit.Assert.fail("equalsAndHashCode_literalMutation22 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

