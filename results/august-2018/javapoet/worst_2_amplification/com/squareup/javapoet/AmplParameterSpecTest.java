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
    public void equalsAndHashCodelitString18() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        a = ParameterSpec.builder(int.class, "_").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int _", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075588, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int _", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075588, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int _", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075588, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString3_failAssert0() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "f,oo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString3 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: f,oo", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString24_failAssert12_add710() throws Exception {
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
            b = ParameterSpec.builder(int.class, "").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString24 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString16_failAssert8litString242() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, ":").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, ":").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString16 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString25_failAssert13_add829_mg3939() throws Exception {
        try {
            Object __DSPOT_o_133 = new Object();
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
            b = ParameterSpec.builder(int.class, "\n").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString25 should have thrown IllegalArgumentException");
            a.equals(__DSPOT_o_133);
        } catch (IllegalArgumentException expected) {
        }
    }
}

