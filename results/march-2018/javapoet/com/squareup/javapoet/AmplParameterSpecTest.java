package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplParameterSpecTest {
    @Test(timeout = 10000)
    public void equalsAndHashCode_add49() throws Exception {
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(ParameterSpec.builder(int.class, "foo").build());
        Assert.assertTrue(o_equalsAndHashCode_add49__15);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString18_failAssert7() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "L[{$QV5:Wz").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString18 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add52() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        int o_equalsAndHashCode_add52__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_add52__15)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add41() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        int o_equalsAndHashCode_add41__7 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_add41__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_add41__7)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString3_failAssert1() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "*oo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString3 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add49_sd522() throws Exception {
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(b);
        int o_equalsAndHashCode_add49_sd522__18 = b.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_add49_sd522__18)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd30litString290_failAssert27() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "7x>[Bob5_8").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            int o_equalsAndHashCode_sd30__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
            org.junit.Assert.fail("equalsAndHashCode_sd30litString290 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add51litString554_failAssert19() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            int o_equalsAndHashCode_add51__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
            org.junit.Assert.fail("equalsAndHashCode_add51litString554 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd26_sd221() throws Exception {
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        int o_equalsAndHashCode_sd26__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        int o_equalsAndHashCode_sd26_sd221__19 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd26_sd221__19)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add41_sd474() throws Exception {
        int o_equalsAndHashCode_add41__7 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add41_sd474__20 = ParameterSpec.builder(int.class, "foo").build().equals(new Object());
        Assert.assertFalse(o_equalsAndHashCode_add41_sd474__20);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add49_add535() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49_add535__15 = a.equals(b);
        Assert.assertTrue(o_equalsAndHashCode_add49_add535__15);
        boolean o_equalsAndHashCode_add49__15 = a.equals(b);
        Assert.assertTrue(o_equalsAndHashCode_add49_add535__15);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32litString1644_failAssert0() throws Exception {
        try {
            ParameterSpec a;
            ParameterSpec b = ParameterSpec.builder(int.class, "").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__15 = ParameterSpec.builder(int.class, "").build().toString();
            org.junit.Assert.fail("equalsAndHashCode_sd32litString1644 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_sd1657() throws Exception {
        ParameterSpec a;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__15 = ParameterSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
        a.toBuilder();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_add1664() throws Exception {
        ParameterSpec a;
        ParameterSpec.builder(int.class, "foo");
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ParameterSpec.builder(int.class, "foo").build().toString());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add49_sd524() throws Exception {
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(b);
        Assert.assertEquals("static int i", b.toString());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32() throws Exception {
        ParameterSpec a;
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("u&sdcOgKS{qxxjff`y&R", "u&sdcOgKS{qxxjff`y&R");
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd28_sd1761() throws Exception {
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd28__15 = ParameterSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd28__15);
        boolean o_equalsAndHashCode_sd28_sd1761__19 = b.equals(new Object());
        Assert.assertFalse(o_equalsAndHashCode_sd28_sd1761__19);
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd28__15);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_sd1690() throws Exception {
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__15 = b.toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
        int o_equalsAndHashCode_sd32_sd1690__17 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd32_sd1690__17)));
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236() throws Exception {
        try {
            ParameterSpec.builder(int.class, "foo").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236_sd4306() throws Exception {
        try {
            ParameterSpec.Builder __DSPOT_invoc_3 = ParameterSpec.builder(int.class, "foo").addAnnotations(null);
            ParameterSpec.builder(int.class, "foo").addAnnotations(null).build();
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236litString4302() throws Exception {
        try {
            ParameterSpec.builder(int.class, "MtN|3}(#*Q").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: MtN|3}(#*Q", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236_sd4306_sd4483() throws Exception {
        try {
            ParameterSpec.Builder __DSPOT_invoc_3 = ParameterSpec.builder(int.class, "foo").addAnnotations(null);
            __DSPOT_invoc_3.build();
            __DSPOT_invoc_3.build();
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236_sd4306litString4480() throws Exception {
        try {
            ParameterSpec.Builder __DSPOT_invoc_3 = ParameterSpec.builder(int.class, "USC<F7cF@g").addAnnotations(null);
            ParameterSpec.builder(int.class, "USC<F7cF@g").addAnnotations(null).build();
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: USC<F7cF@g", e.getMessage());
        }
    }
}

