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
        int o_equalsAndHashCode__7 = b.hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode__16 = b.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode__16)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode__7)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString25_failAssert13() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "\n").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString25 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: \n", expected.getMessage());
        }
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
        int o_equalsAndHashCode_mg47__9 = b.hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_mg47__9)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg47__18 = b.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_mg47__18)));
        boolean o_equalsAndHashCode_mg47__19 = b.equals(__DSPOT_o_1);
        Assert.assertFalse(o_equalsAndHashCode_mg47__19);
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_mg47__9)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_mg47__18)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg49() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg49__7 = b.hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_mg49__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg49__16 = b.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_mg49__16)));
        b.toBuilder();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_mg49__7)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_mg49__16)));
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
        int o_equalsAndHashCodelitString4__7 = b.hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCodelitString4__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCodelitString4__16 = b.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCodelitString4__16)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCodelitString4__7)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull82_failAssert19null9247() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            int o_equalsAndHashCodenull82_failAssert19null9247__9 = b.hashCode();
            Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCodenull82_failAssert19null9247__9)));
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
    public void equalsAndHashCodenull56_failAssert16_add7105() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = null;
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull56 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull56_failAssert16litString6045_failAssert20() throws Exception {
        try {
            try {
                ParameterSpec a = ParameterSpec.builder(int.class, "").build();
                ParameterSpec b = null;
                b.hashCode();
                a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
                b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
                b.hashCode();
                org.junit.Assert.fail("equalsAndHashCodenull56 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodenull56_failAssert16litString6045 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("not a valid name: ", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull82_failAssert19null9244null12492() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = null;
            int o_equalsAndHashCodenull82_failAssert19null9244__9 = b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, null).addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull82 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull82_failAssert19null9244_add12094() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            int o_equalsAndHashCodenull82_failAssert19null9244__9 = b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
            b = ParameterSpec.builder(int.class, null).addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull82 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
}

