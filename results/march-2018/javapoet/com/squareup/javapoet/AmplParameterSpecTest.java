package com.squareup.javapoet;


import javax.lang.model.element.Modifier;
import org.junit.Assert;
import org.junit.Test;


public class AmplParameterSpecTest {
    @Test(timeout = 50000)
    public void equalsAndHashCode_add49() throws Exception {
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(ParameterSpec.builder(int.class, "foo").build());
        Assert.assertTrue(o_equalsAndHashCode_add49__15);
    }

    @Test(timeout = 50000)
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

    @Test(timeout = 50000)
    public void equalsAndHashCode_add52() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        int o_equalsAndHashCode_add52__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_add52__15)));
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add41() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        int o_equalsAndHashCode_add41__7 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        Assert.assertEquals(1955995925, ((int) (o_equalsAndHashCode_add41__7)));
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
    }

    @Test(timeout = 50000)
    public void equalsAndHashCodelitString6_failAssert3() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "GdhscbCS@!").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add49_sd522() throws Exception {
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(b);
        int o_equalsAndHashCode_add49_sd522__18 = b.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_add49_sd522__18)));
    }

    @Test(timeout = 50000)
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

    @Test(timeout = 50000)
    public void equalsAndHashCode_add41_sd474() throws Exception {
        int o_equalsAndHashCode_add41__7 = ParameterSpec.builder(int.class, "foo").build().hashCode();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add41_sd474__20 = ParameterSpec.builder(int.class, "foo").build().equals(new Object());
        Assert.assertFalse(o_equalsAndHashCode_add41_sd474__20);
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add49_add535() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49_add535__15 = a.equals(b);
        Assert.assertTrue(o_equalsAndHashCode_add49_add535__15);
        boolean o_equalsAndHashCode_add49__15 = a.equals(b);
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd26_sd217() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        int o_equalsAndHashCode_sd26__15 = a.hashCode();
        int o_equalsAndHashCode_sd26_sd217__19 = a.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd26_sd217__19)));
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd26litString194_failAssert16() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "=oo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            int o_equalsAndHashCode_sd26__15 = ParameterSpec.builder(int.class, "=oo").build().hashCode();
            org.junit.Assert.fail("equalsAndHashCode_sd26litString194 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_add49_sd524() throws Exception {
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(b);
        Assert.assertEquals("static int i", b.toString());
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32() throws Exception {
        ParameterSpec a;
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("u&sdcOgKS{qxxjff`y&R", "u&sdcOgKS{qxxjff`y&R");
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32litString1648_failAssert23() throws Exception {
        try {
            ParameterSpec a;
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "imrOiR]O2;").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__15 = ParameterSpec.builder(int.class, "foo").build().toString();
            org.junit.Assert.fail("equalsAndHashCode_sd32litString1648 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32litString1639_failAssert25() throws Exception {
        try {
            ParameterSpec a;
            ParameterSpec b = ParameterSpec.builder(int.class, "annotationSpecs == null").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__15 = ParameterSpec.builder(int.class, "annotationSpecs == null").build().toString();
            org.junit.Assert.fail("equalsAndHashCode_sd32litString1639 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
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

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32_sd1691() throws Exception {
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__15 = b.toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
        ParameterSpec.builder(int.class, "foo").build().toBuilder();
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd32_add1671() throws Exception {
        ParameterSpec a;
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32_add1671__13 = b.toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32_add1671__13);
        Assert.assertEquals("static int i", b.toString());
    }

    @Test(timeout = 50000)
    public void equalsAndHashCode_sd28_sd1761() throws Exception {
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd28__15 = ParameterSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd28__15);
        boolean o_equalsAndHashCode_sd28_sd1761__19 = b.equals(new Object());
        Assert.assertFalse(o_equalsAndHashCode_sd28_sd1761__19);
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4236() throws Exception {
        try {
            ParameterSpec.builder(int.class, "foo").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4236litString4298() throws Exception {
        try {
            ParameterSpec.builder(int.class, "annotationSpecs == null").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4236litString4301() throws Exception {
        try {
            ParameterSpec.builder(int.class, "fo").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4236_sd4306litString4481() throws Exception {
        try {
            ParameterSpec.Builder __DSPOT_invoc_3 = ParameterSpec.builder(int.class, "").addAnnotations(null);
            ParameterSpec.builder(int.class, "").addAnnotations(null).build();
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: ", e.getMessage());
        }
    }

    @Test(timeout = 50000)
    public void nullAnnotationsAddition_add4236_sd4306_add4489() throws Exception {
        try {
            ParameterSpec.builder(int.class, "foo").addAnnotations(null);
            ParameterSpec.Builder __DSPOT_invoc_3 = ParameterSpec.builder(int.class, "foo").addAnnotations(null);
            ParameterSpec.builder(int.class, "foo").addAnnotations(null).build();
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("annotationSpecs == null", e.getMessage());
        }
    }
}

