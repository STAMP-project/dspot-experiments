package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplFieldSpecTest {
    @Test(timeout = 10000)
    public void equalsAndHashCode() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode__7 = b.hashCode();
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCode__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode__14 = b.hashCode();
        Assert.assertEquals((-1882877815), ((int) (o_equalsAndHashCode__14)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCode__7)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString16_failAssert8litString373() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, ":").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString16 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString19_failAssert10() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "F(OO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString19 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: F(OO", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString26() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "gOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int gOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1852401622, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int gOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1852401622, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int gOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1852401622, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString30_failAssert16_add824() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
            b = FieldSpec.builder(int.class, "", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString30 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString31_failAssert17_add780_mg6937() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
            b = FieldSpec.builder(int.class, "\n", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString31 should have thrown IllegalArgumentException");
            b.toString();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString3_failAssert0() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "f,oo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString3 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: f,oo", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString25() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCodelitString25__7 = b.hashCode();
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCodelitString25__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "foo", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1852341079), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCodelitString25__14 = b.hashCode();
        Assert.assertEquals((-1852341079), ((int) (o_equalsAndHashCodelitString25__14)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1852341079), ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCodelitString25__7)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1852341079), ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString8_failAssert3() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, ":").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString8 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString18_failAssert9() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, ",OO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString18 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: ,OO", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg51() throws Exception {
        Object __DSPOT_o_1 = new Object();
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg51__9 = b.hashCode();
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCode_mg51__9)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg51__16 = b.hashCode();
        Assert.assertEquals((-1882877815), ((int) (o_equalsAndHashCode_mg51__16)));
        boolean o_equalsAndHashCode_mg51__17 = b.equals(__DSPOT_o_1);
        Assert.assertFalse(o_equalsAndHashCode_mg51__17);
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCode_mg51__9)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals((-1882877815), ((int) (o_equalsAndHashCode_mg51__16)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg53() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg53__7 = b.hashCode();
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCode_mg53__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg53__14 = b.hashCode();
        Assert.assertEquals((-1882877815), ((int) (o_equalsAndHashCode_mg53__14)));
        b.toBuilder();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCode_mg53__7)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals((-1882877815), ((int) (o_equalsAndHashCode_mg53__14)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString22_failAssert12_add2321() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (b)).hashCode())));
            int o_equalsAndHashCodelitString22_failAssert12_add2321__9 = b.hashCode();
            Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCodelitString22_failAssert12_add2321__9)));
            a = FieldSpec.builder(int.class, "", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString22 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString30_failAssert16_add1908() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (b)).hashCode())));
            int o_equalsAndHashCodelitString30_failAssert16_add1908__9 = b.hashCode();
            Assert.assertEquals((-1483589884), ((int) (o_equalsAndHashCodelitString30_failAssert16_add1908__9)));
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
            b = FieldSpec.builder(int.class, "", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString30 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString31_failAssert17_add1452_mg7143() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals((-1483589884), ((int) (((FieldSpec) (b)).hashCode())));
            int o_equalsAndHashCodelitString31_failAssert17_add1452__9 = b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals((-1882877815), ((int) (((FieldSpec) (a)).hashCode())));
            b = FieldSpec.builder(int.class, "\n", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            Assert.fail("equalsAndHashCodelitString31 should have thrown IllegalArgumentException");
            b.hashCode();
        } catch (IllegalArgumentException expected) {
        }
    }
}

