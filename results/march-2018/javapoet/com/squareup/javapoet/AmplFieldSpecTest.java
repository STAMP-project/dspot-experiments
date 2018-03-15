package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplFieldSpecTest {
    @Test(timeout = 10000)
    public void equalsAndHashCodelitString13_failAssert5() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, ",y(q2 5[gp").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString13 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add45() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        int o_equalsAndHashCode_add45__7 = FieldSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_add45__7)));
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals(-1483589884, ((int) (o_equalsAndHashCode_add45__7)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add53() throws Exception {
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        int o_equalsAndHashCode_add53__13 = FieldSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_add53__13)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString16_failAssert6() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "annotationSpecs == null", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString16 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add51() throws Exception {
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "foo").build().equals(FieldSpec.builder(int.class, "foo").build());
        Assert.assertTrue(o_equalsAndHashCode_add51__13);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32() throws Exception {
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", FieldSpec.builder(int.class, "foo").build().toString());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add51litString530_failAssert9() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "s?/OCDfviV", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "foo").build().equals(FieldSpec.builder(int.class, "foo").build());
            org.junit.Assert.fail("equalsAndHashCode_add51litString530 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add51_add555() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51_add555__13 = a.equals(b);
        Assert.assertTrue(o_equalsAndHashCode_add51_add555__13);
        boolean o_equalsAndHashCode_add51__13 = a.equals(b);
        Assert.assertTrue(o_equalsAndHashCode_add51_add555__13);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add51_sd544() throws Exception {
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "foo").build().equals(b);
        int o_equalsAndHashCode_add51_sd544__16 = b.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_add51_sd544__16)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add51litString516_failAssert4() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "A%.UJum&)<").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "A%.UJum&)<").build().equals(FieldSpec.builder(int.class, "foo").build());
            org.junit.Assert.fail("equalsAndHashCode_add51litString516 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
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

    @Test(timeout = 10000)
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

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd36litString1853() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FROO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", b.toString());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_sd1798() throws Exception {
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd32__13);
        b.toBuilder();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd32__13);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd36_add372_sd2222() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd36__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
        FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).addJavadoc("|URllB^!%Y]_E]i.}JC]", new Object[0]);
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add51_sd546() throws Exception {
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add51__13 = FieldSpec.builder(int.class, "foo").build().equals(b);
        Assert.assertEquals("public static int FOO;\n", b.toString());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd36litString1819_failAssert28() throws Exception {
        try {
            FieldSpec a;
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            a = FieldSpec.builder(int.class, "]g)e+[it&{", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            String o_equalsAndHashCode_sd36__13 = FieldSpec.builder(int.class, "foo").build().toString();
            org.junit.Assert.fail("equalsAndHashCode_sd36litString1819 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_add284_sd2281() throws Exception {
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd32__13);
        FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).addJavadoc("?2l[mns*tG=FjE&}Qd4 ", new Object[]{ new Object(), new Object(), new Object(), new Object() });
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd32__13);
    }

    @Test(timeout = 10000)
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

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd36() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        FieldSpec b;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("U{R IF%j!9gwX1[[en#R", "U{R IF%j!9gwX1[[en#R");
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd36_add372_sd2225() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd36__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
        FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).initializer("hX0]_^tFCqr,tX[gb 2P", new Object[]{ new Object(), new Object(), new Object() });
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd36_sd1829() throws Exception {
        FieldSpec a;
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        String o_equalsAndHashCode_sd36__13 = FieldSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
        int o_equalsAndHashCode_sd36_sd1829__15 = a.hashCode();
        Assert.assertEquals(-1882877815, ((int) (o_equalsAndHashCode_sd36_sd1829__15)));
        Assert.assertEquals("public static int FOO;\n", o_equalsAndHashCode_sd36__13);
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4414() throws Exception {
        try {
            FieldSpec.builder(int.class, "foo").addAnnotations(null);
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("annotationSpecs == null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4414litString4482() throws Exception {
        try {
            FieldSpec.builder(int.class, "annotationSpecs == null").addAnnotations(null);
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("not a valid name: annotationSpecs == null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4414litString4485() throws Exception {
        try {
            FieldSpec.builder(int.class, "fo").addAnnotations(null);
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("annotationSpecs == null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4414_sd4489litString4680() throws Exception {
        try {
            Object[] __DSPOT_args_166 = new Object[]{ new Object(), new Object(), new Object(), new Object() };
            String __DSPOT_format_165 = "3TG?Pgmuhyp([DypXWAg";
            FieldSpec.Builder __DSPOT_invoc_3 = FieldSpec.builder(int.class, "f)oo").addAnnotations(null);
            FieldSpec.builder(int.class, "f)oo").addAnnotations(null).addJavadoc("3TG?Pgmuhyp([DypXWAg", new Object[]{ new Object(), new Object(), new Object(), new Object() });
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("not a valid name: f)oo", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4414_sd4489litString4681() throws Exception {
        try {
            Object[] __DSPOT_args_166 = new Object[]{ new Object(), new Object(), new Object(), new Object() };
            String __DSPOT_format_165 = "3TG?Pgmuhyp([DypXWAg";
            FieldSpec.Builder __DSPOT_invoc_3 = FieldSpec.builder(int.class, "fo").addAnnotations(null);
            FieldSpec.builder(int.class, "fo").addAnnotations(null).addJavadoc("3TG?Pgmuhyp([DypXWAg", new Object[]{ new Object(), new Object(), new Object(), new Object() });
        } catch (IllegalArgumentException expected) {
            String o_nullAnnotationsAddition_add4414__6 = expected.getMessage();
            Assert.assertEquals("annotationSpecs == null", expected.getMessage());
        }
    }
}

