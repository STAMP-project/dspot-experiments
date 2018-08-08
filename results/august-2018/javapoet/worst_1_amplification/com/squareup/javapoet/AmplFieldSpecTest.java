package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplFieldSpecTest {
    @Test(timeout = 10000)
    public void equalsAndHashCode() throws Exception {
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
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        b.hashCode();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString9() throws Exception {
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
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg51() throws Exception {
        Object __DSPOT_o_1 = new Object();
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
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        boolean o_equalsAndHashCode_mg51__17 = b.equals(__DSPOT_o_1);
        Assert.assertFalse(o_equalsAndHashCode_mg51__17);
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg53() throws Exception {
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
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        b.toBuilder();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
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
    public void equalsAndHashCodelitString30_failAssert16_add2385() throws Exception {
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
    public void equalsAndHashCodelitString15_failAssert7_add889() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "\n").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString15 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString31_failAssert17_add871_mg7058() throws Exception {
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
            b.toBuilder();
        } catch (IllegalArgumentException expected) {
        }
    }
}

