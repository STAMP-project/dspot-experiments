package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplFieldSpecTest {
    @Test(timeout = 50000)
    public void equalsAndHashCode_add45() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        int o_equalsAndHashCode_add45__7 = FieldSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_add45__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
    }

    @Test(timeout = 50000)
    public void equalsAndHashCodelitString20_failAssert8() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "QV5:Wz2[|+", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString20 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add54() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        int o_equalsAndHashCode_add54__13 = FieldSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_add54__13)));
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add51() throws Exception {
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "foo").build().equals(FieldSpec.builder(int.class, "foo").build());
        Assert.assertTrue(o_equalsAndHashCode_add51__13);
    }

    @Test(timeout = 50000)
    public void equalsAndHashCodelitString3_failAssert1() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "*oo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString3 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd34litString302_failAssert15() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "#OO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            int o_equalsAndHashCode_sd34__13 = FieldSpec.builder(int.class, "foo").build().hashCode();
            org.junit.Assert.fail("equalsAndHashCode_sd34litString302 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32() throws Exception {
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", FieldSpec.builder(int.class, "foo").build().toString());
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add51_add555() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51_add555__13 = a.equals(b);
        Assert.assertTrue(o_equalsAndHashCode_add51_add555__13);
        boolean o_equalsAndHashCode_add51__13 = a.equals(b);
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add51_sd544() throws Exception {
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "foo").build().equals(b);
        int o_equalsAndHashCode_add51_sd544__16 = b.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_add51_sd544__16)));
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add51litString523_failAssert6() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "Va&1`i[aMe").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "Va&1`i[aMe").build().equals(FieldSpec.builder(int.class, "foo").build());
            org.junit.Assert.fail("equalsAndHashCode_add51litString523 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32_add282_sd2340_failAssert0() throws Exception {
        try {
            Object[] __DSPOT_args_90 = new Object[]{ new Object(), new Object() };
            String __DSPOT_format_89 = ";s4m>$PTOy/fW<J(^Wif";
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            FieldSpec.Builder __DSPOT_invoc_7 = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC);
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__13 = FieldSpec.builder(int.class, "foo").build().toString();
            FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).addJavadoc(";s4m>$PTOy/fW<J(^Wif", new Object[]{ new Object(), new Object() });
            org.junit.Assert.fail("equalsAndHashCode_sd32_add282_sd2340 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32litString1742() throws Exception {
        FieldSpec a;
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "foo", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int foo;\n", a.toString());
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32litString1737_failAssert10() throws Exception {
        try {
            FieldSpec a;
            FieldSpec b = FieldSpec.builder(int.class, ")oo").build();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__13 = a.toString();
            org.junit.Assert.fail("equalsAndHashCode_sd32litString1737 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd36_add372_sd2222() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd36__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
        FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).addJavadoc("|URllB^!%Y]_E]i.}JC]", new Object[0]);
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd36_sd1830() throws Exception {
        FieldSpec a;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd36__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
        a.toBuilder();
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add51_sd546() throws Exception {
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "foo").build().equals(b);
        Assert.assertEquals("public static int FOO;\n", b.toString());
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32_add284_sd2281() throws Exception {
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd32__13);
        FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).addJavadoc("?2l[mns*tG=FjE&}Qd4 ", new Object[]{ new Object(), new Object(), new Object(), new Object() });
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd36_add374_sd2166_failAssert1() throws Exception {
        try {
            Object[] __DSPOT_args_72 = new Object[]{ new Object() };
            String __DSPOT_format_71 = "ob]U)GU|GC>>@9%$tKd!";
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            FieldSpec.Builder __DSPOT_invoc_10 = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC);
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            String o_equalsAndHashCode_sd36__13 = FieldSpec.builder(int.class, "foo").build().toString();
            FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).initializer("ob]U)GU|GC>>@9%$tKd!", new Object[]{ new Object() });
            org.junit.Assert.fail("equalsAndHashCode_sd36_add374_sd2166 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd36() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("U{R IF%j!9gwX1[[en#R", "U{R IF%j!9gwX1[[en#R");
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd36_sd1833() throws Exception {
        FieldSpec a;
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd36__13 = b.toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
        int o_equalsAndHashCode_sd36_sd1833__15 = b.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_sd36_sd1833__15)));
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32_add284_sd2284() throws Exception {
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd32__13);
        FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).initializer("p.X)v_mKH*(,<,)|Wy@j", new Object[]{ new Object(), new Object(), new Object() });
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4414() throws Exception {
        try {
            FieldSpec.builder(int.class, "foo").addAnnotations(null);
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("annotationSpecs == null", expected.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4414litString4482() throws Exception {
        try {
            FieldSpec.builder(int.class, "annotationSpecs == null").addAnnotations(null);
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("not a valid name: annotationSpecs == null", expected.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4414_sd4489() throws Exception {
        try {
            Object[] __DSPOT_args_166 = new Object[]{ new Object(), new Object(), new Object(), new Object() };
            String __DSPOT_format_165 = "3TG?Pgmuhyp([DypXWAg";
            FieldSpec.Builder __DSPOT_invoc_3 = FieldSpec.builder(int.class, "foo").addAnnotations(null);
            FieldSpec.builder(int.class, "foo").addAnnotations(null).addJavadoc("3TG?Pgmuhyp([DypXWAg", new Object[]{ new Object(), new Object(), new Object(), new Object() });
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("annotationSpecs == null", expected.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4414_sd4492_sd4759() throws Exception {
        try {
            Object[] __DSPOT_args_169 = new Object[]{ new Object(), new Object(), new Object() };
            String __DSPOT_format_168 = "%<0v|2J j[AsO&r7Mez&";
            FieldSpec.Builder __DSPOT_invoc_3 = FieldSpec.builder(int.class, "foo").addAnnotations(null);
            FieldSpec.Builder __DSPOT_invoc_12 = FieldSpec.builder(int.class, "foo").addAnnotations(null).initializer("%<0v|2J j[AsO&r7Mez&", new Object[]{ new Object(), new Object(), new Object() });
            FieldSpec.builder(int.class, "foo").addAnnotations(null).initializer("%<0v|2J j[AsO&r7Mez&", new Object[]{ new Object(), new Object(), new Object() }).build();
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("annotationSpecs == null", expected.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4414_sd4492litString4750() throws Exception {
        try {
            Object[] __DSPOT_args_169 = new Object[]{ new Object(), new Object(), new Object() };
            String __DSPOT_format_168 = "%<0v|2J j[AsO&r7Mez&";
            FieldSpec.Builder __DSPOT_invoc_3 = FieldSpec.builder(int.class, "tRK]cAR})i").addAnnotations(null);
            FieldSpec.builder(int.class, "tRK]cAR})i").addAnnotations(null).initializer("%<0v|2J j[AsO&r7Mez&", new Object[]{ new Object(), new Object(), new Object() });
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("not a valid name: tRK]cAR})i", expected.getMessage());
        }
    }
}

