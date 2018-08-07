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
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode__16 = b.hashCode();
        Assert.assertEquals((-130075578), ((int) (o_equalsAndHashCode__16)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode__7)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString19_failAssert9_add835() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            b.hashCode();
            a = ParameterSpec.builder(int.class, "").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString19 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString25_failAssert13_add2205_mg6708() throws Exception {
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
            b = ParameterSpec.builder(int.class, "\n").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString25 should have thrown IllegalArgumentException");
            b.toString();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString26_failAssert14_add2234() throws Exception {
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
            b = ParameterSpec.builder(int.class, ":").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString26 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
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
    public void equalsAndHashCode_mg45() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg45__7 = b.hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_mg45__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg45__16 = b.hashCode();
        Assert.assertEquals((-130075578), ((int) (o_equalsAndHashCode_mg45__16)));
        a.toBuilder();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_mg45__7)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals((-130075578), ((int) (o_equalsAndHashCode_mg45__16)));
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
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg47__18 = b.hashCode();
        Assert.assertEquals((-130075578), ((int) (o_equalsAndHashCode_mg47__18)));
        boolean o_equalsAndHashCode_mg47__19 = b.equals(__DSPOT_o_1);
        Assert.assertFalse(o_equalsAndHashCode_mg47__19);
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_mg47__9)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals((-130075578), ((int) (o_equalsAndHashCode_mg47__18)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString8_failAssert3() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, ":").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString8 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString19_failAssert9() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString19 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString9() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCodelitString9__7 = b.hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCodelitString9__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        int o_equalsAndHashCodelitString9__16 = b.hashCode();
        Assert.assertEquals((-130075578), ((int) (o_equalsAndHashCodelitString9__16)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCodelitString9__7)));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString25_failAssert13_add707() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            int o_equalsAndHashCodelitString25_failAssert13_add707__9 = b.hashCode();
            Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCodelitString25_failAssert13_add707__9)));
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
            Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
            b = ParameterSpec.builder(int.class, "\n").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString25 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString19_failAssert9_add661() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            int o_equalsAndHashCodelitString19_failAssert9_add661__9 = b.hashCode();
            Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCodelitString19_failAssert9_add661__9)));
            a = ParameterSpec.builder(int.class, "").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString19 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString25_failAssert13_add711_mg6674() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            int o_equalsAndHashCodelitString25_failAssert13_add711__9 = b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
            Assert.assertEquals((-130075578), ((int) (((ParameterSpec) (a)).hashCode())));
            b = ParameterSpec.builder(int.class, "\n").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString25 should have thrown IllegalArgumentException");
            b.toBuilder();
        } catch (IllegalArgumentException expected) {
        }
    }
}

