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
    public void equalsAndHashCodelitString8_failAssert10() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, ":").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString8 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull52() throws Exception {
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
    public void equalsAndHashCode_mg43() throws Exception {
        ParameterSpec __DSPOT_o_0 = null;
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
        boolean o_equalsAndHashCode_mg43__18 = a.equals(__DSPOT_o_0);
        Assert.assertFalse(o_equalsAndHashCode_mg43__18);
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
    public void equalsAndHashCode_mg44() throws Exception {
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
    public void equalsAndHashCodenull48_failAssert1_add2091() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, null).build();
            b.hashCode();
            ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull48 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString2_mg2527() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "koo").build();
        Assert.assertEquals("int koo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1956000730, ((int) (((ParameterSpec) (a)).hashCode())));
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
    public void equalsAndHashCodelitString2_remove2391() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "koo").build();
        Assert.assertEquals("int koo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1956000730, ((int) (((ParameterSpec) (a)).hashCode())));
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
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg44_rv2619() throws Exception {
        Modifier[] __DSPOT_modifiers_112 = new Modifier[]{  };
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
        ParameterSpec.Builder __DSPOT_invoc_41 = a.toBuilder();
        __DSPOT_invoc_41.addModifiers(__DSPOT_modifiers_112);
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
    public void equalsAndHashCode_add38litString527_failAssert40() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, ":").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC);
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCode_add38litString527 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString1_mg2540() throws Exception {
        ParameterSpec __DSPOT_o_73 = null;
        ParameterSpec a = ParameterSpec.builder(int.class, "i").build();
        Assert.assertEquals("int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(100359288, ((int) (((ParameterSpec) (a)).hashCode())));
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
        boolean o_equalsAndHashCodelitString1_mg2540__18 = b.equals(__DSPOT_o_73);
        Assert.assertFalse(o_equalsAndHashCodelitString1_mg2540__18);
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
    public void equalsAndHashCode_mg44_rv2620() throws Exception {
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
        ParameterSpec.Builder __DSPOT_invoc_41 = a.toBuilder();
        ParameterSpec o_equalsAndHashCode_mg44_rv2620__20 = __DSPOT_invoc_41.build();
        Assert.assertEquals("static int i", ((ParameterSpec) (o_equalsAndHashCode_mg44_rv2620__20)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (o_equalsAndHashCode_mg44_rv2620__20)).hashCode())));
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
    public void equalsAndHashCode_add38_rv2632_mg11495() throws Exception {
        ParameterSpec __DSPOT_o_332 = null;
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
        ParameterSpec.Builder __DSPOT_invoc_31 = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC);
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        ParameterSpec o_equalsAndHashCode_add38_rv2632__21 = __DSPOT_invoc_31.build();
        Assert.assertEquals("static int i", ((ParameterSpec) (o_equalsAndHashCode_add38_rv2632__21)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (o_equalsAndHashCode_add38_rv2632__21)).hashCode())));
        boolean o_equalsAndHashCode_add38_rv2632_mg11495__25 = a.equals(__DSPOT_o_332);
        Assert.assertFalse(o_equalsAndHashCode_add38_rv2632_mg11495__25);
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (o_equalsAndHashCode_add38_rv2632__21)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (o_equalsAndHashCode_add38_rv2632__21)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg46_rv2622litNum6626() throws Exception {
        Modifier[] __DSPOT_modifiers_113 = new Modifier[1];
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
        ParameterSpec.Builder __DSPOT_invoc_41 = b.toBuilder();
        __DSPOT_invoc_41.addModifiers(__DSPOT_modifiers_113);
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
    public void equalsAndHashCodelitString1_add2067_add7964() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "i").build();
        Assert.assertEquals("int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(100359288, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec.builder(int.class, "foo");
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
        ((ParameterSpec) (b)).toString();
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
    public void equalsAndHashCodelitString1_add2067_mg11317() throws Exception {
        Object __DSPOT_o_243 = new Object();
        ParameterSpec a = ParameterSpec.builder(int.class, "i").build();
        Assert.assertEquals("int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(100359288, ((int) (((ParameterSpec) (a)).hashCode())));
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
        ((ParameterSpec) (b)).toString();
        boolean o_equalsAndHashCodelitString1_add2067_mg11317__20 = b.equals(__DSPOT_o_243);
        Assert.assertFalse(o_equalsAndHashCodelitString1_add2067_mg11317__20);
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
    public void equalsAndHashCodelitString2litString885_mg11388() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "koo").build();
        Assert.assertEquals("int koo", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(1956000730, ((int) (((ParameterSpec) (a)).hashCode())));
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        b = ParameterSpec.builder(int.class, "foo").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-448578205, ((int) (((ParameterSpec) (b)).hashCode())));
        b.toBuilder();
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-448578205, ((int) (((ParameterSpec) (b)).hashCode())));
        Assert.assertEquals("static int i", ((ParameterSpec) (a)).toString());
        Assert.assertEquals(-130075578, ((int) (((ParameterSpec) (a)).hashCode())));
        Assert.assertEquals("static int foo", ((ParameterSpec) (b)).toString());
        Assert.assertEquals(-448578205, ((int) (((ParameterSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg45_remove2326litString3358_failAssert93() throws Exception {
        try {
            ParameterSpec __DSPOT_o_1 = null;
            ParameterSpec a = ParameterSpec.builder(int.class, ":").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            boolean o_equalsAndHashCode_mg45__18 = b.equals(__DSPOT_o_1);
            org.junit.Assert.fail("equalsAndHashCode_mg45_remove2326litString3358 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add39litString628_failAssert39litString6504() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (a)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (a)).hashCode())));
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo", ((ParameterSpec) (b)).toString());
            Assert.assertEquals(1955995925, ((int) (((ParameterSpec) (b)).hashCode())));
            b.hashCode();
            a = ParameterSpec.builder(int.class, " ").addModifiers(Modifier.STATIC).build();
            ParameterSpec.builder(int.class, "Y");
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCode_add39litString628 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }
}

