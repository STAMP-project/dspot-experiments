package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplParameterSpecTest {
    @Test(timeout = 10000)
    public void equalsAndHashCode_sd30() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        int o_equalsAndHashCode_sd30__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd30__15)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add49() throws Exception {
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(ParameterSpec.builder(int.class, "foo").build());
        Assert.assertTrue(o_equalsAndHashCode_add49__15);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString2_failAssert0() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "annotationSpecs == null").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString2 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString16_failAssert6() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "annotationSpecs == null").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString16 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add41() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        int o_equalsAndHashCode_add41__7 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_add41__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
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
    public void equalsAndHashCode_add49litString510_failAssert15() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "t2Y][1u)p]").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(ParameterSpec.builder(int.class, "foo").build());
            org.junit.Assert.fail("equalsAndHashCode_add49litString510 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd30_sd303() throws Exception {
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        int o_equalsAndHashCode_sd30__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        int o_equalsAndHashCode_sd30_sd303__19 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd30_sd303__19)));
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
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd26litString198_failAssert7() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            int o_equalsAndHashCode_sd26__15 = ParameterSpec.builder(int.class, "").build().hashCode();
            org.junit.Assert.fail("equalsAndHashCode_sd26litString198 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd28() throws Exception {
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ParameterSpec.builder(int.class, "foo").build().toString());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_sd1657() throws Exception {
        ParameterSpec a;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__15 = ParameterSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
        a.toBuilder();
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
    public void equalsAndHashCode_sd28_sd1727() throws Exception {
        ParameterSpec a;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd28__15 = a.toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd28__15);
        boolean o_equalsAndHashCode_sd28_sd1727__19 = ParameterSpec.builder(int.class, "foo").build().equals(new Object());
        Assert.assertFalse(o_equalsAndHashCode_sd28_sd1727__19);
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_sd1660() throws Exception {
        ParameterSpec a;
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__15 = b.toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
        int o_equalsAndHashCode_sd32_sd1660__17 = b.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd32_sd1660__17)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32litString1677_failAssert13() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "DcJ+3%i%O4").build();
            ParameterSpec b;
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__15 = b.toString();
            org.junit.Assert.fail("equalsAndHashCode_sd32litString1677 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
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
    public void nullAnnotationsAddition_add4236litString4297() throws Exception {
        try {
            ParameterSpec.builder(int.class, "i").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236litString4303() throws Exception {
        try {
            ParameterSpec.builder(int.class, "").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: ", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236litString4297litString4373() throws Exception {
        try {
            ParameterSpec.builder(int.class, "").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: ", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236_sd4306_add4490() throws Exception {
        try {
            ParameterSpec.builder(int.class, "foo");
            ParameterSpec.Builder __DSPOT_invoc_3 = ParameterSpec.builder(int.class, "foo").addAnnotations(null);
            ParameterSpec.builder(int.class, "foo").addAnnotations(null).build();
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }
}

