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
    public void equalsAndHashCode_mg47() throws Exception {
        FieldSpec __DSPOT_o_0 = null;
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
        boolean o_equalsAndHashCode_mg47__16 = a.equals(__DSPOT_o_0);
        Assert.assertFalse(o_equalsAndHashCode_mg47__16);
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
    public void equalsAndHashCode_mg49() throws Exception {
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
        boolean o_equalsAndHashCode_mg49__17 = b.equals(__DSPOT_o_1);
        Assert.assertFalse(o_equalsAndHashCode_mg49__17);
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
    public void equalsAndHashCodelitString28() throws Exception {
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
        b = FieldSpec.builder(int.class, "FO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg50() throws Exception {
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
    public void equalsAndHashCodelitString3_failAssert1() throws Exception {
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
    public void equalsAndHashCode_add38_mg2620() throws Exception {
        FieldSpec __DSPOT_o_23 = null;
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        a.hashCode();
        b.hashCode();
        b.hashCode();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        boolean o_equalsAndHashCode_add38_mg2620__17 = b.equals(__DSPOT_o_23);
        Assert.assertFalse(o_equalsAndHashCode_add38_mg2620__17);
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
    public void equalsAndHashCode_mg48_rv2838_failAssert56() throws Exception {
        try {
            Object[] __DSPOT_args_145 = new Object[]{ new Object(), new Object() };
            String __DSPOT_format_144 = "mi$GMClhUaB;hvYoV%Zw";
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            FieldSpec.Builder __DSPOT_invoc_39 = a.toBuilder();
            __DSPOT_invoc_39.addJavadoc(__DSPOT_format_144, __DSPOT_args_145);
            org.junit.Assert.fail("equalsAndHashCode_mg48_rv2838 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("invalid format string: \'mi$GMClhUaB;hvYoV%Zw\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add39litString494_failAssert25() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "h4]").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCode_add39litString494 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: h4]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg48_mg2686() throws Exception {
        FieldSpec __DSPOT_o_56 = null;
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
        a.toBuilder();
        boolean o_equalsAndHashCode_mg48_mg2686__17 = b.equals(__DSPOT_o_56);
        Assert.assertFalse(o_equalsAndHashCode_mg48_mg2686__17);
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
    public void equalsAndHashCode_mg50_rv2835() throws Exception {
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
        FieldSpec.Builder __DSPOT_invoc_39 = b.toBuilder();
        FieldSpec o_equalsAndHashCode_mg50_rv2835__18 = __DSPOT_invoc_39.build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (o_equalsAndHashCode_mg50_rv2835__18)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (o_equalsAndHashCode_mg50_rv2835__18)).hashCode())));
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
    public void equalsAndHashCode_mg48_rv2839() throws Exception {
        Modifier[] __DSPOT_modifiers_146 = new Modifier[0];
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
        FieldSpec.Builder __DSPOT_invoc_39 = a.toBuilder();
        __DSPOT_invoc_39.addModifiers(__DSPOT_modifiers_146);
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
    public void equalsAndHashCode_add41_rv2826() throws Exception {
        Object[] __DSPOT_args_133 = new Object[]{ new Object(), new Object() };
        String __DSPOT_format_132 = "y!oNPJ-E.u#+<3OqUn7j";
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        FieldSpec.Builder __DSPOT_invoc_21 = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC);
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        __DSPOT_invoc_21.initializer(__DSPOT_format_132, __DSPOT_args_133);
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
    public void equalsAndHashCode_mg48_rv2841_failAssert23() throws Exception {
        try {
            Object[] __DSPOT_args_148 = new Object[]{ new Object() };
            String __DSPOT_format_147 = "O(4POj$iSH2aDj.pbZs?";
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            FieldSpec.Builder __DSPOT_invoc_39 = a.toBuilder();
            __DSPOT_invoc_39.initializer(__DSPOT_format_147, __DSPOT_args_148);
            org.junit.Assert.fail("equalsAndHashCode_mg48_rv2841 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("invalid format string: \'O(4POj$iSH2aDj.pbZs?\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add38litString538() throws Exception {
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        a.hashCode();
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
    public void equalsAndHashCode_add34_mg2632() throws Exception {
        Object __DSPOT_o_29 = new Object();
        FieldSpec.builder(int.class, "foo");
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
        boolean o_equalsAndHashCode_add34_mg2632__18 = a.equals(__DSPOT_o_29);
        Assert.assertFalse(o_equalsAndHashCode_add34_mg2632__18);
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
    public void equalsAndHashCodelitString11_failAssert8_add2343() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec b = FieldSpec.builder(int.class, "f@oo").build();
            b.hashCode();
            FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC);
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString11 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add34_rv2831_failAssert24() throws Exception {
        try {
            Object[] __DSPOT_args_138 = new Object[]{ new Object() };
            String __DSPOT_format_137 = "Cozg$:>o/lW&-[21$]n_";
            FieldSpec.Builder __DSPOT_invoc_1 = FieldSpec.builder(int.class, "foo");
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            __DSPOT_invoc_1.initializer(__DSPOT_format_137, __DSPOT_args_138);
            org.junit.Assert.fail("equalsAndHashCode_add34_rv2831 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("invalid format string: \'Cozg$:>o/lW&-[21$]n_\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add38litString538_remove11231() throws Exception {
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
    public void equalsAndHashCode_add38litString538_mg11807() throws Exception {
        Object __DSPOT_o_443 = new Object();
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        a.hashCode();
        b.hashCode();
        b.hashCode();
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        boolean o_equalsAndHashCode_add38litString538_mg11807__18 = a.equals(__DSPOT_o_443);
        Assert.assertFalse(o_equalsAndHashCode_add38litString538_mg11807__18);
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
    public void equalsAndHashCode_add37_remove2514litString6513_failAssert76() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            boolean o_equalsAndHashCode_add37__7 = a.equals(b);
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            org.junit.Assert.fail("equalsAndHashCode_add37_remove2514litString6513 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("not a valid name: ", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCodelitString11_failAssert8_add2340_rv12176() throws Exception {
        try {
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
            Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
            FieldSpec.Builder __DSPOT_invoc_12 = FieldSpec.builder(int.class, "f@oo");
            FieldSpec b = FieldSpec.builder(int.class, "f@oo").build();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            b.hashCode();
            org.junit.Assert.fail("equalsAndHashCodelitString11 should have thrown IllegalArgumentException");
            __DSPOT_invoc_12.build();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add43_rv2819_rv12324_failAssert74() throws Exception {
        try {
            Object[] __DSPOT_args_783 = new Object[0];
            String __DSPOT_format_782 = " ic-Xw[ &JB%50Ta3H$Y";
            Modifier[] __DSPOT_modifiers_126 = new Modifier[]{  };
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            FieldSpec.Builder __DSPOT_invoc_30 = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC);
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            FieldSpec.Builder __DSPOT_invoc_43 = __DSPOT_invoc_30.addModifiers(__DSPOT_modifiers_126);
            __DSPOT_invoc_43.addJavadoc(__DSPOT_format_782, __DSPOT_args_783);
            org.junit.Assert.fail("equalsAndHashCode_add43_rv2819_rv12324 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("index 1 for \'$Y\' not in range (received 0 arguments)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg48_rv2840litString4984() throws Exception {
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
        b = FieldSpec.builder(int.class, "FO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (b)).hashCode())));
        FieldSpec.Builder __DSPOT_invoc_39 = a.toBuilder();
        FieldSpec o_equalsAndHashCode_mg48_rv2840__18 = __DSPOT_invoc_39.build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (o_equalsAndHashCode_mg48_rv2840__18)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (o_equalsAndHashCode_mg48_rv2840__18)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg47litString861litString3927() throws Exception {
        FieldSpec __DSPOT_o_0 = null;
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        b.hashCode();
        a = FieldSpec.builder(int.class, "FO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "wOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int wOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1837625286, ((int) (((FieldSpec) (b)).hashCode())));
        b.hashCode();
        boolean o_equalsAndHashCode_mg47__16 = a.equals(__DSPOT_o_0);
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int wOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1837625286, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int wOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1837625286, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_add39_add1590_rv12192() throws Exception {
        Object[] __DSPOT_args_659 = new Object[]{ new Object(), new Object() };
        String __DSPOT_format_658 = "diK)9LWIx S:-5y5DAf<";
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        b.hashCode();
        FieldSpec.Builder __DSPOT_invoc_22 = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC);
        a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        __DSPOT_invoc_22.initializer(__DSPOT_format_658, __DSPOT_args_659);
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
    public void equalsAndHashCode_mg47litString861_mg11389() throws Exception {
        FieldSpec __DSPOT_o_198 = null;
        FieldSpec __DSPOT_o_0 = null;
        FieldSpec a = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (a)).hashCode())));
        FieldSpec b = FieldSpec.builder(int.class, "foo").build();
        Assert.assertEquals("int foo;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1483589884, ((int) (((FieldSpec) (b)).hashCode())));
        b.hashCode();
        b.hashCode();
        a = FieldSpec.builder(int.class, "FO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (a)).hashCode())));
        b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        boolean o_equalsAndHashCode_mg47__16 = a.equals(__DSPOT_o_0);
        boolean o_equalsAndHashCode_mg47litString861_mg11389__20 = b.equals(__DSPOT_o_198);
        Assert.assertFalse(o_equalsAndHashCode_mg47litString861_mg11389__20);
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
        Assert.assertEquals("public static int FO;\n", ((FieldSpec) (a)).toString());
        Assert.assertEquals(-753475324, ((int) (((FieldSpec) (a)).hashCode())));
        Assert.assertEquals("public static int FOO;\n", ((FieldSpec) (b)).toString());
        Assert.assertEquals(-1882877815, ((int) (((FieldSpec) (b)).hashCode())));
    }

    @Test(timeout = 10000)
    public void equalsAndHashCode_mg48_rv2839_rv12299() throws Exception {
        Object[] __DSPOT_args_758 = new Object[0];
        String __DSPOT_format_757 = "2|vsAQsN8GB&*3&t#d:c";
        Modifier[] __DSPOT_modifiers_146 = new Modifier[0];
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
        FieldSpec.Builder __DSPOT_invoc_39 = a.toBuilder();
        FieldSpec.Builder __DSPOT_invoc_43 = __DSPOT_invoc_39.addModifiers(__DSPOT_modifiers_146);
        __DSPOT_invoc_43.addJavadoc(__DSPOT_format_757, __DSPOT_args_758);
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
    public void equalsAndHashCode_add43_rv2818_rv12332_failAssert73() throws Exception {
        try {
            Object[] __DSPOT_args_791 = new Object[]{ new Object() };
            String __DSPOT_format_790 = "p$ph(mE>&G,99Dhj!W^$";
            Object[] __DSPOT_args_125 = new Object[0];
            String __DSPOT_format_124 = "IMmqw=Ma !VX)*-a  F#";
            FieldSpec a = FieldSpec.builder(int.class, "foo").build();
            FieldSpec b = FieldSpec.builder(int.class, "foo").build();
            b.hashCode();
            b.hashCode();
            a = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            FieldSpec.Builder __DSPOT_invoc_30 = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC);
            b = FieldSpec.builder(int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC).build();
            FieldSpec.Builder __DSPOT_invoc_44 = __DSPOT_invoc_30.addJavadoc(__DSPOT_format_124, __DSPOT_args_125);
            __DSPOT_invoc_44.initializer(__DSPOT_format_790, __DSPOT_args_791);
            org.junit.Assert.fail("equalsAndHashCode_add43_rv2818_rv12332 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("invalid format string: \'p$ph(mE>&G,99Dhj!W^$\'", expected.getMessage());
        }
    }
}

