package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplParameterSpecTest {
    @Test(timeout = 10000)
    public void equalsAndHashCode() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        b.hashCode();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        b.hashCode();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg45() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        a.toBuilder();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg47() throws Exception {
        Object __DSPOT_o_1 = new Object();
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        boolean o_equalsAndHashCode_mg47__19 = b.equals(__DSPOT_o_1);
        Assert.assertFalse(o_equalsAndHashCode_mg47__19);
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString6_failAssert1() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString4() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "fo").build();
        Assert.assertEquals("int fo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-1183829350, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull82_failAssert19null2060() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
            b = ParameterSpec.builder(int.class, null).addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull82 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull56_failAssert16litString323_failAssert23() throws Exception {
        try {
            try {
                ParameterSpec a = ParameterSpec.builder(int.class, "L[{").build();
                ParameterSpec b = null;
                b.hashCode();
                a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
                b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
                b.hashCode();
                org.junit.Assert.fail("equalsAndHashCodenull56 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodenull56_failAssert16litString323 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("not a valid name: L[{", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull56_failAssert16_mg1101() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = null;
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull56 should have thrown NullPointerException");
            b.hashCode();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull82_failAssert19null2060null11422() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
            b = ParameterSpec.builder(int.class, null).addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull82 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull82_failAssert19null2060null11370() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, null).build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, null).addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull82 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
}

