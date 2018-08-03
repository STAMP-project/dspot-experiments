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
        int o_equalsAndHashCode__7 = b.hashCode();
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode__14 = b.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode__14)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode__7)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString23_failAssert13() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "\n", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString23 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString9() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCodelitString9__7 = b.hashCode();
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCodelitString9__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCodelitString9__14 = b.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCodelitString9__14)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCodelitString9__7)));
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
        int o_equalsAndHashCode_mg51__9 = b.hashCode();
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_mg51__9)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg51__16 = b.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_mg51__16)));
        boolean o_equalsAndHashCode_mg51__17 = b.equals(__DSPOT_o_1);
        Assert.assertFalse(o_equalsAndHashCode_mg51__17);
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_mg51__9)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_mg51__16)));
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
        int o_equalsAndHashCode_mg53__7 = b.hashCode();
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_mg53__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        int o_equalsAndHashCode_mg53__14 = b.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_mg53__14)));
        b.toBuilder();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_mg53__7)));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_mg53__14)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull86_failAssert24null1571() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = null;
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull86 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull86_failAssert24litString586_failAssert34() throws Exception {
        try {
            try {
                FieldSpec a = FieldSpec.builder(int.class, "").build();
                FieldSpec b = FieldSpec.builder(int.class, "foo").build();
                b.hashCode();
                a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
                b = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
                b.hashCode();
                org.junit.Assert.fail("equalsAndHashCodenull86 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("equalsAndHashCodenull86_failAssert24litString586 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("not a valid name: ", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull86_failAssert24_add1212() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
            int o_equalsAndHashCodenull86_failAssert24_add1212__9 = b.hashCode();
            Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCodenull86_failAssert24_add1212__9)));
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
    public void equalsAndHashCodenull86_failAssert24null1587() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
            int o_equalsAndHashCodenull86_failAssert24null1587__9 = b.hashCode();
            Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCodenull86_failAssert24null1587__9)));
            a = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull86 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodenull86_failAssert24null1605_mg14548() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
            int o_equalsAndHashCodenull86_failAssert24null1605__9 = b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
            b = FieldSpec.builder(int.class, "FOO", null, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodenull86 should have thrown NullPointerException");
            a.toBuilder();
        } catch (NullPointerException expected) {
        }
    }
}

