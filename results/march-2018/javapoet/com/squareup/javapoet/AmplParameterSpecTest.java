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
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString19_failAssert8() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCodelitString19 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add49litString515_failAssert8() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "M-k,I]-r8/").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            boolean o_equalsAndHashCode_add49__15 = ParameterSpec.builder(int.class, "foo").build().equals(ParameterSpec.builder(int.class, "foo").build());
            org.junit.Assert.fail("equalsAndHashCode_add49litString515 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
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
    public void equalsAndHashCode_sd30litString288_failAssert18() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "f)oo").build();
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            int o_equalsAndHashCode_sd30__15 = ParameterSpec.builder(int.class, "foo").build().hashCode();
            org.junit.Assert.fail("equalsAndHashCode_sd30litString288 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd26_sd217() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        int o_equalsAndHashCode_sd26__15 = a.hashCode();
        int o_equalsAndHashCode_sd26_sd217__19 = a.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd26_sd217__19)));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32litString1652_failAssert9() throws Exception {
        try {
            ParameterSpec a;
            ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
            a = ParameterSpec.builder(int.class, "8").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__15 = ParameterSpec.builder(int.class, "foo").build().toString();
            org.junit.Assert.fail("equalsAndHashCode_sd32litString1652 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
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
    public void equalsAndHashCode_sd32_add1667() throws Exception {
        ParameterSpec a;
        ParameterSpec.builder(int.class, "i");
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        Assert.assertEquals("static int i", ParameterSpec.builder(int.class, "foo").build().toString());
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_sd32_sd1656() throws Exception {
        ParameterSpec a;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__15 = ParameterSpec.builder(int.class, "foo").build().toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
        int o_equalsAndHashCode_sd32_sd1656__17 = a.hashCode();
        Assert.assertEquals(-130075578, ((int) (o_equalsAndHashCode_sd32_sd1656__17)));
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
    public void equalsAndHashCode_sd32_sd1695() throws Exception {
        ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
        ParameterSpec b;
        a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
        String o_equalsAndHashCode_sd32__15 = b.toString();
        Assert.assertEquals("static int i", o_equalsAndHashCode_sd32__15);
        b.toBuilder();
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
    public void equalsAndHashCode_sd32litString1675_failAssert11() throws Exception {
        try {
            ParameterSpec a = ParameterSpec.builder(int.class, "f:oo").build();
            ParameterSpec b;
            a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
            String o_equalsAndHashCode_sd32__15 = b.toString();
            org.junit.Assert.fail("equalsAndHashCode_sd32litString1675 should have thrown IllegalArgumentException");
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
    public void nullAnnotationsAddition_add4236litString4302() throws Exception {
        try {
            ParameterSpec.builder(int.class, "MtN|3}(#*Q").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: MtN|3}(#*Q", e.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nullAnnotationsAddition_add4236litString4298_add4391() throws Exception {
        try {
            ParameterSpec.builder(int.class, "annotationSpecs == null");
            ParameterSpec.builder(int.class, "annotationSpecs == null").addAnnotations(null);
        } catch (Exception e) {
            String o_nullAnnotationsAddition_add4236__6 = e.getMessage();
            Assert.assertEquals("not a valid name: annotationSpecs == null", e.getMessage());
        }
    }

    @Test(timeout = 10000)
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

