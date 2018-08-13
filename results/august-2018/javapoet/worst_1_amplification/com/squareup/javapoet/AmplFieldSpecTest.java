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
    public void equalsAndHashCodelitString6_failAssert1() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: ", expected.getMessage());
        }
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
    public void equalsAndHashCodelitString5() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "dhs").build();
        Assert.assertEquals("int dhs;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1485641619, ((int) (((FieldSpec) (a)).hashCode())));
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
    public void equalsAndHashCodenull86_failAssert24null5584() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, null).build();
            b = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull86 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull87_failAssert25null2616() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, null).build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, null).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull87 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull87_failAssert25litString1379_failAssert32() throws Exception {
        try {
            try {
                FieldSpec a = FieldSpec.builder(int.class, "").build();
                FieldSpec b = FieldSpec.builder(int.class, "foo").build();
                b.hashCode();
                a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
                b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, null).build();
                b.hashCode();
                org.junit.Assert.fail("equalsAndHashCodenull87 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodenull87_failAssert25litString1379 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("not a valid name: ", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull86_failAssert24null5617() throws Exception {
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
            b = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull86 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull86_failAssert24null5629_add13358() throws Exception {
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
            FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull86 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
}

