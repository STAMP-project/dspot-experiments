package org.jsoup.nodes;


import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeTest {
    @Test(timeout = 10000)
    public void html() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        String o_html__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html__4);
        attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html__4);
    }

    @Test(timeout = 10000)
    public void html_add17_mg782() throws Exception {
        Attribute __DSPOT_o_34 = new Attribute("]r3_{}VLc{;bMa&icyMO", "!`j!MNC@I#`g*s,=^$;H", new Attributes());
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_add17__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__3);
        String o_html_add17__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__4);
        attr.toString();
        boolean o_html_add17_mg782__13 = attr.equals(__DSPOT_o_34);
        Assert.assertFalse(o_html_add17_mg782__13);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__4);
    }

    @Test(timeout = 10000)
    public void html_mg20_mg940_add8039() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg940__13 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg940__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg940__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg940__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg940__13)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg940_mg9396() throws Exception {
        Object __DSPOT_o_917 = new Object();
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg940__13 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg940__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg940__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg940__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg940__13)).getKey());
        boolean o_html_mg20_mg940_mg9396__18 = o_html_mg20_mg940__13.equals(__DSPOT_o_917);
        Assert.assertFalse(o_html_mg20_mg940_mg9396__18);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg940__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg940__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg940__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg940__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg940_mg9702() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg940__13 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg940__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg940__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg940__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg940__13)).getKey());
        o_html_mg20_mg940__13.toString();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg940__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg940__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg940__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg940__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg940litString7220() throws Exception {
        Attribute attr = new Attribute("key", "");
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg940__13 = attr.clone();
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20_mg940__13)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20_mg940__13)).hashCode())));
        Assert.assertEquals("", ((Attribute) (o_html_mg20_mg940__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg940__13)).getKey());
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\"", o_html_mg20__3);
        Assert.assertEquals("key=\"\"", o_html_mg20__4);
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg940litString7237() throws Exception {
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg940__13 = attr.clone();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20_mg940__13)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20_mg940__13)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (o_html_mg20_mg940__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg940__13)).getKey());
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\n\"", o_html_mg20__3);
        Assert.assertEquals("key=\"\n\"", o_html_mg20__4);
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg949_mg9698() throws Exception {
        Attribute __DSPOT_o_969 = new Attribute("ao`Mq5?Gd&sx,0O[J-B!", "(K<.do>h<ZwB*d2!@u6e", new Attributes());
        Attribute __DSPOT_o_72 = new Attribute("e)X{?5;Z[ft0f^6j9]=|", "URllB^!%Y]_E]i.}JC]T");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        boolean o_html_mg20_mg949__15 = attr.equals(__DSPOT_o_72);
        boolean o_html_mg20_mg949_mg9698__21 = __DSPOT_o_72.equals(__DSPOT_o_969);
        Assert.assertFalse(o_html_mg20_mg949_mg9698__21);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg996_mg9079() throws Exception {
        String __DSPOT_key_881 = "Cmq2hX<m,>gkj?Y:DYZ}";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg996__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg996__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg996__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg996__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg996__13)).getKey());
        attr.setKey(__DSPOT_key_881);
        Assert.assertEquals("Cmq2hX<m,>gkj?Y:DYZ}=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1314991015, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Cmq2hX<m,>gkj?Y:DYZ}", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg996__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg996__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg996__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg996__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg996_mg9104_failAssert19() throws Exception {
        try {
            String __DSPOT_val_885 = "5X;&. glTk7XY]h:+#@1";
            Attribute attr = new Attribute("key", "value &");
            String o_html_mg20__3 = attr.html();
            String o_html_mg20__4 = attr.html();
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            Attribute o_html_mg20_mg996__13 = o_html_mg20__6.clone();
            attr.setValue(__DSPOT_val_885);
            org.junit.Assert.fail("html_mg20_mg996_mg9104 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg996litString7417_failAssert21() throws Exception {
        try {
            Attribute attr = new Attribute("", "value &");
            String o_html_mg20__3 = attr.html();
            String o_html_mg20__4 = attr.html();
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            Attribute o_html_mg20_mg996__13 = o_html_mg20__6.clone();
            org.junit.Assert.fail("html_mg20_mg996litString7417 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg996litString7519() throws Exception {
        Attribute attr = new Attribute("key", "=\"A");
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("=\"A", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg996__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (o_html_mg20_mg996__13)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (o_html_mg20_mg996__13)).hashCode())));
        Assert.assertEquals("=\"A", ((Attribute) (o_html_mg20_mg996__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg996__13)).getKey());
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__3);
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__4);
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("=\"A", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg998_mg9771() throws Exception {
        String __DSPOT_key_992 = "T@/U3DUPD_{F%Kv4g/?N";
        Attribute __DSPOT_o_87 = new Attribute("x2>iy;hPVv>/f/5!<Y=d", "gY,;s4m>$PTOy/fW<J(^", new Attributes());
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        boolean o_html_mg20_mg998__16 = o_html_mg20__6.equals(__DSPOT_o_87);
        __DSPOT_o_87.setKey(__DSPOT_key_992);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg998_mg9779() throws Exception {
        String __DSPOT_val_996 = "&Kw4=9O!A[LXY<Za0GoX";
        Attribute __DSPOT_o_87 = new Attribute("x2>iy;hPVv>/f/5!<Y=d", "gY,;s4m>$PTOy/fW<J(^", new Attributes());
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        boolean o_html_mg20_mg998__16 = o_html_mg20__6.equals(__DSPOT_o_87);
        String o_html_mg20_mg998_mg9779__20 = __DSPOT_o_87.setValue(__DSPOT_val_996);
        Assert.assertEquals("", o_html_mg20_mg998_mg9779__20);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20litString456() throws Exception {
        Attribute attr = new Attribute("key", "");
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\"", o_html_mg20__3);
        Assert.assertEquals("key=\"\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg21_add850() throws Exception {
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg21_mg986() throws Exception {
        Object __DSPOT_o_82 = new Object();
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        boolean o_html_mg21_mg986__17 = attr.equals(__DSPOT_o_82);
        Assert.assertFalse(o_html_mg21_mg986__17);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg21litString706() throws Exception {
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\n\"", o_html_mg21__5);
        Assert.assertEquals("key=\"\n\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg22() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg22__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__3);
        String o_html_mg22__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__4);
        attr.toString();
        attr.getKey();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__4);
    }

    @Test(timeout = 10000)
    public void html_mg22_mg906() throws Exception {
        Attribute __DSPOT_o_60 = new Attribute("F[UUbDcpS ]x;K>tB$@7", "*v3E3{w[&<Aihc:4mdp[", new Attributes());
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg22__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__3);
        String o_html_mg22__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__4);
        attr.toString();
        attr.getKey();
        boolean o_html_mg22_mg906__14 = attr.equals(__DSPOT_o_60);
        Assert.assertFalse(o_html_mg22_mg906__14);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg22__4);
    }

    @Test(timeout = 10000)
    public void html_mg25_mg973() throws Exception {
        String __DSPOT_key_77 = "G!0gGi?=}tR?SY{S>>6O";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg25__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__3);
        String o_html_mg25__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__4);
        attr.toString();
        String o_html_mg25__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__6);
        attr.setKey(__DSPOT_key_77);
        Assert.assertEquals("G!0gGi?=}tR?SY{S>>6O=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-829359061, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("G!0gGi?=}tR?SY{S>>6O", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__6);
    }

    @Test(timeout = 10000)
    public void html_mg25_mg979_failAssert10() throws Exception {
        try {
            String __DSPOT_val_81 = " Iz;of%p.X)v_mKH*(,<";
            Attribute attr = new Attribute("key", "value &");
            String o_html_mg25__3 = attr.html();
            String o_html_mg25__4 = attr.html();
            attr.toString();
            String o_html_mg25__6 = attr.html();
            attr.setValue(__DSPOT_val_81);
            org.junit.Assert.fail("html_mg25_mg979 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg27_failAssert1() throws Exception {
        try {
            String __DSPOT_val_2 = "`A=SO/woO!OKS@Rl&{ha";
            Attribute attr = new Attribute("key", "value &");
            attr.html();
            attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_2);
            org.junit.Assert.fail("html_mg27 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg27_failAssert1litString279_failAssert2() throws Exception {
        try {
            try {
                String __DSPOT_val_2 = "`A=SO/woO!OKS@Rl&{ha";
                Attribute attr = new Attribute("", "value &");
                attr.html();
                attr.html();
                attr.toString();
                attr.setValue(__DSPOT_val_2);
                org.junit.Assert.fail("html_mg27 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("html_mg27_failAssert1litString279 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("String must not be empty", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg27_failAssert1litString340() throws Exception {
        try {
            String __DSPOT_val_2 = "`A=SO/woO!OKS@Rl&{ha";
            Attribute attr = new Attribute("key", "b>_1JVt");
            Assert.assertEquals("key=\"b>_1JVt\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(-1351388517, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("b>_1JVt", ((Attribute) (attr)).getValue());
            Assert.assertEquals("key", ((Attribute) (attr)).getKey());
            String o_html_mg27_failAssert1litString340__6 = attr.html();
            Assert.assertEquals("key=\"b>_1JVt\"", o_html_mg27_failAssert1litString340__6);
            String o_html_mg27_failAssert1litString340__7 = attr.html();
            Assert.assertEquals("key=\"b>_1JVt\"", o_html_mg27_failAssert1litString340__7);
            attr.toString();
            attr.setValue(__DSPOT_val_2);
            org.junit.Assert.fail("html_mg27 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString14() throws Exception {
        Attribute attr = new Attribute("key", "");
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_htmllitString14__3 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString14__3);
        String o_htmllitString14__4 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString14__4);
        attr.toString();
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\"", o_htmllitString14__3);
        Assert.assertEquals("key=\"\"", o_htmllitString14__4);
    }

    @Test(timeout = 10000)
    public void htmllitString3_mg838_failAssert14litString5802() throws Exception {
        try {
            String __DSPOT_val_47 = "=EcyHGP}8hoa-J!Pzx|9";
            Attribute attr = new Attribute("k,ey", "N|4&<n]");
            Assert.assertEquals("k,ey=\"N|4&amp;<n]\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(-89697514, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("N|4&<n]", ((Attribute) (attr)).getValue());
            Assert.assertEquals("k,ey", ((Attribute) (attr)).getKey());
            String o_htmllitString3__3 = attr.html();
            Assert.assertEquals("k,ey=\"N|4&amp;<n]\"", o_htmllitString3__3);
            String o_htmllitString3__4 = attr.html();
            Assert.assertEquals("k,ey=\"N|4&amp;<n]\"", o_htmllitString3__4);
            attr.toString();
            attr.setValue(__DSPOT_val_47);
            org.junit.Assert.fail("htmllitString3_mg838 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_add10514() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_17 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10514__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10514__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10514__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10514__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10514__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10514__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_add11674() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919_add16516() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919_mg17047() throws Exception {
        Object __DSPOT_o_1481 = new Object();
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919_mg17047__22 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.equals(__DSPOT_o_1481);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919_mg17047__22);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919_mg17118() throws Exception {
        String __DSPOT_key_1493 = "U.XWFJ(JwZt!F|G.aL?j";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getKey());
        o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.setKey(__DSPOT_key_1493);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("U.XWFJ(JwZt!F|G.aL?j=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(1283059907, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("U.XWFJ(JwZt!F|G.aL?j", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919_mg17129_failAssert42() throws Exception {
        try {
            String __DSPOT_val_1499 = "-(eVcti}09U(2XHOvhU.";
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_21 = ((s + "=\"A") + s) + "B\"";
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
            attr.toString();
            Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
            Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17 = attr.clone();
            o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.setValue(__DSPOT_val_1499);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919_mg17129 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919litString15164() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11919__17)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11931() throws Exception {
        Attribute __DSPOT_o_1110 = new Attribute("6O:L?F)_3ZODl/5{zMpf", "lxk@?1]L)M4Ap>9zqp8h");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11931__19 = attr.equals(__DSPOT_o_1110);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11931__19);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11987() throws Exception {
        String __DSPOT_key_1126 = "7{%!$v4E1ev0u?c4^bkX";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        attr.setKey(__DSPOT_key_1126);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("7{%!$v4E1ev0u?c4^bkX=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-871096057, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("7{%!$v4E1ev0u?c4^bkX", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997_add14418() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997litString12608() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("key=\"value &amp;\"" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.clone();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11997__17)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15435() throws Exception {
        Attribute __DSPOT_o_1132 = new Attribute("+:}8r!G7!=z +J5v]jsz", "!m-]X@[M5(BD)b$>iB;]", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998__20 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.equals(__DSPOT_o_1132);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15435__23 = __DSPOT_o_1132.html();
        Assert.assertEquals("+:}8r!G7!=z +J5v]jsz=\"!m-]X@[M5(BD)b$>iB;]\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15435__23);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15462() throws Exception {
        String __DSPOT_key_1306 = "v1[(cqB-I;zJ=-mODC`B";
        Attribute __DSPOT_o_1132 = new Attribute("+:}8r!G7!=z +J5v]jsz", "!m-]X@[M5(BD)b$>iB;]", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998__20 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.equals(__DSPOT_o_1132);
        __DSPOT_o_1132.setKey(__DSPOT_key_1306);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15486() throws Exception {
        String __DSPOT_val_1308 = "0ohyAhauWIB.8e#4ZJQE";
        Attribute __DSPOT_o_1132 = new Attribute("+:}8r!G7!=z +J5v]jsz", "!m-]X@[M5(BD)b$>iB;]", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998__20 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.equals(__DSPOT_o_1132);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15486__24 = __DSPOT_o_1132.setValue(__DSPOT_val_1308);
        Assert.assertEquals("", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15486__24);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15544() throws Exception {
        Attribute __DSPOT_o_1330 = new Attribute("YD)X.LzM[^TW]S1=!mas", ":s3okxH*!TLD#dX({me,");
        Attribute __DSPOT_o_1132 = new Attribute("+:}8r!G7!=z +J5v]jsz", "!m-]X@[M5(BD)b$>iB;]", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998__20 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10.equals(__DSPOT_o_1132);
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15544__25 = attr.equals(__DSPOT_o_1330);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518_mg11998_mg15544__25);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518litString10635() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("!" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"!\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110896583, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("!\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"!\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"!\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"!\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).toString());
        Assert.assertEquals(110896583, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).hashCode())));
        Assert.assertEquals("!\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"!\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110896583, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("!\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"!\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__7);
        Assert.assertEquals("\ud844\udcc1=\"!\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10518__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519() throws Exception {
        Attribute __DSPOT_o_1041 = new Attribute("5RS:Q-9+4}uC6YH<1vCh", "s,I l4`eH<=080W8?=8B");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__12 = attr.equals(__DSPOT_o_1041);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__12);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10519__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10524() throws Exception {
        String __DSPOT_key_1042 = ".Siy14^;jR>L|LkAHDEM";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_27 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10524__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10524__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10524__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10524__9);
        attr.toString();
        attr.setKey(__DSPOT_key_1042);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals(".Siy14^;jR>L|LkAHDEM=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-124405841, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals(".Siy14^;jR>L|LkAHDEM", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10524__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10524__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30() throws Exception {
        try {
            String __DSPOT_val_1043 = " })hL,:!)w`i#o[p]&[T";
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_28 = ((s + "=\"A") + s) + "B\"";
            attr.html();
            attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_1043);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30_add11469() throws Exception {
        try {
            String __DSPOT_val_1043 = " })hL,:!)w`i#o[p]&[T";
            String s = new String(Character.toChars(135361));
            Assert.assertEquals("\ud844\udcc1", s);
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
            Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
            String String_28 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30_add11469__10 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30_add11469__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30_add11469__11 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30_add11469__11);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30_add11469__12 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30_add11469__12);
            attr.toString();
            attr.setValue(__DSPOT_val_1043);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString10904() throws Exception {
        try {
            String __DSPOT_val_1043 = " })hL,:!)w`i#o[p]&[T";
            String s = new String(Character.toChars(135361));
            Assert.assertEquals("\ud844\udcc1", s);
            Attribute attr = new Attribute(s, (("=\"A" + s) + "B"));
            Assert.assertEquals("\ud844\udcc1=\"=&quot;A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(1889627820, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("=\"A\ud844\udcc1B", ((Attribute) (attr)).getValue());
            Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
            String String_28 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString10904__10 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"=&quot;A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString10904__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString10904__11 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"=&quot;A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString10904__11);
            attr.toString();
            attr.setValue(__DSPOT_val_1043);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString11007() throws Exception {
        try {
            String __DSPOT_val_1043 = " })hL,:!)w`i#o[p]&[T";
            String s = new String(Character.toChars(135361));
            Assert.assertEquals("\ud844\udcc1", s);
            Attribute attr = new Attribute(s, (("A" + s) + "\n"));
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(111849839, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("A\ud844\udcc1\n", ((Attribute) (attr)).getValue());
            Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
            String String_28 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString11007__10 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString11007__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString11007__11 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525_failAssert30litString11007__11);
            attr.toString();
            attr.setValue(__DSPOT_val_1043);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10525 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString10499() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("r" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"r\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(113309654, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("r\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_2 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_2);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10499__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"r\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10499__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10499__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"r\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10499__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"r\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(113309654, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("r\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_2);
        Assert.assertEquals("\ud844\udcc1=\"r\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10499__7);
        Assert.assertEquals("\ud844\udcc1=\"r\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10499__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString10501() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_4 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10501__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10501__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10501__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10501__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10501__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10501__8);
    }

    @Test(timeout = 10000)
    public void html_mg28() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg28__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg28__3);
        String o_html_mg28__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg28__4);
        attr.toString();
        attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg28__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg28__4);
    }

    @Test(timeout = 10000)
    public void htmllitString15() throws Exception {
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        String o_htmllitString15__3 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        String o_htmllitString15__4 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
        attr.toString();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
    }

    @Test(timeout = 10000)
    public void html_mg27() throws Exception {
        String __DSPOT_value_2 = "`A=SO/woO!OKS@Rl&{ha";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg27__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        String o_html_mg27__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
        attr.toString();
        String o_html_mg27__7 = attr.setValue(__DSPOT_value_2);
        Assert.assertEquals("value &", o_html_mg27__7);
        Assert.assertEquals("key=\"`A=SO/woO!OKS@Rl&amp;{ha\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1958088499, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("`A=SO/woO!OKS@Rl&{ha", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
    }

    @Test(timeout = 10000)
    public void html_mg26() throws Exception {
        String __DSPOT_key_1 = "vC=TU&zgYc TM1`_8;0L";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg26__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__4);
        String o_html_mg26__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__5);
        attr.toString();
        attr.setKey(__DSPOT_key_1);
        Assert.assertEquals("vC=TU&zgYc TM1`_8;0L=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals((-1807868971), ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("vC=TU&zgYc TM1`_8;0L", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__5);
    }

    @Test(timeout = 10000)
    public void html_mg20() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert0() throws Exception {
        try {
            Attribute attr = new Attribute("", "value &");
            attr.html();
            attr.html();
            attr.toString();
            Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString1() throws Exception {
        Attribute attr = new Attribute("value &", "value &");
        Assert.assertEquals("value &=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals((-1178622240), ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("value &", getKey());
        String o_htmllitString1__3 = attr.html();
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__3);
        String o_htmllitString1__4 = attr.html();
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__4);
        attr.toString();
        Assert.assertEquals("value &=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals((-1178622240), ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("value &", getKey());
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__3);
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__4);
    }

    @Test(timeout = 10000)
    public void html_mg21() throws Exception {
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        Assert.assertFalse(o_html_mg21__8);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_add17() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_add17__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__3);
        String o_html_add17__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__4);
        attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__4);
    }

    @Test(timeout = 10000)
    public void html_mg25_mg956() throws Exception {
        String __DSPOT_key_70 = "{$F=,gxwDv>@=(rae5We";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg25__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__3);
        String o_html_mg25__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__4);
        attr.toString();
        String o_html_mg25__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__6);
        attr.setKey(__DSPOT_key_70);
        Assert.assertEquals("{$F=,gxwDv>@=(rae5We=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1323224113, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("{$F=,gxwDv>@=(rae5We", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__6);
    }

    @Test(timeout = 10000)
    public void html_mg21_mg985() throws Exception {
        String __DSPOT_value_80 = "@juza;+kVD6&G)ynZ< g";
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        String o_html_mg21_mg985__16 = __DSPOT_o_0.setValue(__DSPOT_value_80);
        Assert.assertEquals("+mr6#-VtX(r!Fs2l>UgI", o_html_mg21_mg985__16);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg20_add608() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg21_mg948() throws Exception {
        Attribute __DSPOT_o_69 = new Attribute("9EY]mb@&7yoEh?_F)3VJ", "g?!KP(j8}cK>tr@!m3PO");
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        boolean o_html_mg21_mg948__17 = __DSPOT_o_0.equals(__DSPOT_o_69);
        Assert.assertFalse(o_html_mg21_mg948__17);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg21_mg935() throws Exception {
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        Attribute o_html_mg21_mg935__15 = __DSPOT_o_0.clone();
        Assert.assertEquals("2 5[gpbL[{$QV5:Wz2[|=\"+mr6#-VtX(r!Fs2l>UgI\"", ((Attribute) (o_html_mg21_mg935__15)).toString());
        Assert.assertEquals(104321700, ((int) (((Attribute) (o_html_mg21_mg935__15)).hashCode())));
        Assert.assertEquals("+mr6#-VtX(r!Fs2l>UgI", getValue());
        Assert.assertEquals("2 5[gpbL[{$QV5:Wz2[|", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg20litString337() throws Exception {
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\n\"", o_html_mg20__3);
        Assert.assertEquals("key=\"\n\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg27litString292() throws Exception {
        String __DSPOT_value_2 = "`A=SO/wo<!OKS@Rl&{ha";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg27__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        String o_html_mg27__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
        attr.toString();
        String o_html_mg27__7 = attr.setValue(__DSPOT_value_2);
        Assert.assertEquals("value &", o_html_mg27__7);
        Assert.assertEquals("key=\"`A=SO/wo<!OKS@Rl&amp;{ha\"", ((Attribute) (attr)).toString());
        Assert.assertEquals((-494483162), ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("`A=SO/wo<!OKS@Rl&{ha", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
    }

    @Test(timeout = 10000)
    public void html_mg25_mg963() throws Exception {
        String __DSPOT_value_71 = ")X{?5;Z[ft0f^6j9]=|U";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg25__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__3);
        String o_html_mg25__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__4);
        attr.toString();
        String o_html_mg25__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__6);
        String o_html_mg25_mg963__14 = attr.setValue(__DSPOT_value_71);
        Assert.assertEquals("value &", o_html_mg25_mg963__14);
        Assert.assertEquals("key=\")X{?5;Z[ft0f^6j9]=|U\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(533100447, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals(")X{?5;Z[ft0f^6j9]=|U", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg25__6);
    }

    @Test(timeout = 10000)
    public void html_mg20_mg973() throws Exception {
        Object __DSPOT_o_76 = new Object();
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        boolean o_html_mg20_mg973__15 = attr.equals(__DSPOT_o_76);
        Assert.assertFalse(o_html_mg20_mg973__15);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20litString245() throws Exception {
        Attribute attr = new Attribute("key", "=\"A");
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("=\"A", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("=\"A", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3348189, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("=\"A", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__3);
        Assert.assertEquals("key=\"=&quot;A\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg21litString363_failAssert2() throws Exception {
        try {
            Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
            Attribute attr = new Attribute("", "value &");
            String o_html_mg21__5 = attr.html();
            String o_html_mg21__6 = attr.html();
            attr.toString();
            boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
            Assert.fail("html_mg21litString363 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20litString253() throws Exception {
        Attribute attr = new Attribute("key", "valuz &");
        Assert.assertEquals("key=\"valuz &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234912141, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("valuz &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"valuz &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"valuz &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"valuz &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234912141, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("valuz &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"valuz &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234912141, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("valuz &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"valuz &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"valuz &amp;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void htmllitString14_add409() throws Exception {
        Attribute attr = new Attribute("key", "");
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", getValue());
        Assert.assertEquals("key", getKey());
        String o_htmllitString14__3 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString14__3);
        String o_htmllitString14__4 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString14__4);
        attr.toString();
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\"", o_htmllitString14__3);
        Assert.assertEquals("key=\"\"", o_htmllitString14__4);
    }

    @Test(timeout = 10000)
    public void html_mg20_mg967_mg6909() throws Exception {
        String __DSPOT_value_622 = "iZM>s^$4&*&q=v6nsA(Q";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg967__13 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg967__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg967__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20_mg967_mg6909__17 = o_html_mg20__6.setValue(__DSPOT_value_622);
        Assert.assertEquals("value &", o_html_mg20_mg967_mg6909__17);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"iZM>s^$4&amp;*&amp;q=v6nsA(Q\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(1301500213, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("iZM>s^$4&*&q=v6nsA(Q", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg967__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg967__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg999_mg10018() throws Exception {
        Attribute __DSPOT_o_990 = new Attribute("7a3Yz+!Z]xo4yX1{u^^i", "}>>7.0)M5q!92IdN^Vyx");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg999__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        boolean o_html_mg20_mg999_mg10018__18 = attr.equals(__DSPOT_o_990);
        Assert.assertFalse(o_html_mg20_mg999_mg10018__18);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg967litString4707() throws Exception {
        Attribute attr = new Attribute("key", "");
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg967__13 = attr.clone();
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20_mg967__13)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20_mg967__13)).hashCode())));
        Assert.assertEquals("", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\"", o_html_mg20__3);
        Assert.assertEquals("key=\"\"", o_html_mg20__4);
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg967litString4719() throws Exception {
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg967__13 = attr.clone();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20_mg967__13)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20_mg967__13)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"\n\"", o_html_mg20__3);
        Assert.assertEquals("key=\"\n\"", o_html_mg20__4);
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("\n", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg999litString8854_failAssert5() throws Exception {
        try {
            Attribute attr = new Attribute("", "value &");
            String o_html_mg20__3 = attr.html();
            String o_html_mg20__4 = attr.html();
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            Attribute o_html_mg20_mg999__13 = o_html_mg20__6.clone();
            Assert.fail("html_mg20_mg999litString8854 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg999_mg10069() throws Exception {
        Object __DSPOT_o_1008 = new Object();
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg999__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        boolean o_html_mg20_mg999_mg10069__18 = o_html_mg20_mg999__13.equals(__DSPOT_o_1008);
        Assert.assertFalse(o_html_mg20_mg999_mg10069__18);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg967_add5802() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg967__13 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg967__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg967__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg967litString4638() throws Exception {
        Attribute attr = new Attribute("key", "key=\"value &amp;\"");
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg967__13 = attr.clone();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (o_html_mg20_mg967__13)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (o_html_mg20_mg967__13)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg999_mg10066() throws Exception {
        String __DSPOT_value_1007 = "j7i.jtkCD23&!%-r,Mry";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg999__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20_mg999_mg10066__17 = o_html_mg20__6.setValue(__DSPOT_value_1007);
        Assert.assertEquals("value &", o_html_mg20_mg999_mg10066__17);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"j7i.jtkCD23&amp;!%-r,Mry\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(1377009575, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("j7i.jtkCD23&!%-r,Mry", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg999litString8922() throws Exception {
        Attribute attr = new Attribute("key", "B");
        Assert.assertEquals("key=\"B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288515, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("B", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"B\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"B\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"B\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288515, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("B", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg999__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"B\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(3288515, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("B", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288515, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("B", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"B\"", o_html_mg20__3);
        Assert.assertEquals("key=\"B\"", o_html_mg20__4);
        Assert.assertEquals("key=\"B\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(3288515, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("B", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg999_mg10075() throws Exception {
        String __DSPOT_value_1010 = "Chs,I l4`eH<=080W8?=";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg999__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20_mg999_mg10075__17 = o_html_mg20_mg999__13.setValue(__DSPOT_value_1010);
        Assert.assertEquals("value &", o_html_mg20_mg999_mg10075__17);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"Chs,I l4`eH<=080W8?=\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals((-982710719), ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("Chs,I l4`eH<=080W8?=", getValue());
        Assert.assertEquals("key", getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg999_mg10074() throws Exception {
        String __DSPOT_key_1009 = "J25RS:Q-9+4}uC6YH<1v";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Attribute o_html_mg20_mg999__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        o_html_mg20_mg999__13.setKey(__DSPOT_key_1009);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("key", getKey());
        Assert.assertEquals("J25RS:Q-9+4}uC6YH<1v=\"value &amp;\"", ((Attribute) (o_html_mg20_mg999__13)).toString());
        Assert.assertEquals((-2093645078), ((int) (((Attribute) (o_html_mg20_mg999__13)).hashCode())));
        Assert.assertEquals("value &", getValue());
        Assert.assertEquals("J25RS:Q-9+4}uC6YH<1v", getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_0 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10701() throws Exception {
        String __DSPOT_key_1012 = "hv@lU>0U()|%u-FCV!50";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_27 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10701__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10701__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10701__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10701__9);
        attr.toString();
        attr.setKey(__DSPOT_key_1012);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("hv@lU>0U()|%u-FCV!50=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1627879063, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("hv@lU>0U()|%u-FCV!50", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10701__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10701__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702() throws Exception {
        String __DSPOT_value_1013 = "+`7*4!32@H:/*jAd>@I*";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_28 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__9);
        attr.toString();
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__11 = attr.setValue(__DSPOT_value_1013);
        Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__11);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"+`7*4!32@H:/*jAd>@I*\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1124027930, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("+`7*4!32@H:/*jAd>@I*", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("key=\"value &amp;\"" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_1 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_1);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_1);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__7);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696() throws Exception {
        Attribute __DSPOT_o_1011 = new Attribute("4^;jR>L|LkAHDEM })hL", ",:!)w`i#o[p]&[Tg[-+.");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__12 = attr.equals(__DSPOT_o_1011);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__12);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString10678() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_4 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10678__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10678__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10678__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10678__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10678__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10678__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10686() throws Exception {
        String s = new String(Character.toChars(135360));
        Assert.assertEquals("\ud844\udcc0", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849833, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc0B", getValue());
        Assert.assertEquals("\ud844\udcc0", getKey());
        String String_12 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_12);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10686__7 = attr.html();
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10686__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10686__8 = attr.html();
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10686__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc0", s);
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849833, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc0B", getValue());
        Assert.assertEquals("\ud844\udcc0", getKey());
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", String_12);
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10686__7);
        Assert.assertEquals("\ud844\udcc0=\"A\ud844\udcc0B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10686__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10689() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", getValue());
        Assert.assertEquals("", getKey());
        String String_15 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10689__7 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10689__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10689__8 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10689__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", getValue());
        Assert.assertEquals("", getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10689__7);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10689__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_add10691() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_17 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10691__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10691__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10691__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10691__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10691__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add10691__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702_mg12034() throws Exception {
        Attribute __DSPOT_o_1067 = new Attribute("Vq3+SM<{Q9h,p:[>|d_|", "&!vkH10LS+C(uj(}P`>p");
        String __DSPOT_value_1013 = "+`7*4!32@H:/*jAd>@I*";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_28 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__9);
        attr.toString();
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__11 = attr.setValue(__DSPOT_value_1013);
        Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__11);
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702_mg12034__20 = attr.equals(__DSPOT_o_1067);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702_mg12034__20);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"+`7*4!32@H:/*jAd>@I*\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1124027930, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("+`7*4!32@H:/*jAd>@I*", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__9);
        Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10702__11);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695_add11808() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696_mg12136() throws Exception {
        String __DSPOT_value_1092 = "r!amECjR7&*VARH<nk>l";
        Attribute __DSPOT_o_1011 = new Attribute("4^;jR>L|LkAHDEM })hL", ",:!)w`i#o[p]&[Tg[-+.");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__12 = attr.equals(__DSPOT_o_1011);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696_mg12136__20 = attr.setValue(__DSPOT_value_1092);
        Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696_mg12136__20);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"r!amECjR7&amp;*VARH<nk>l\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(820270735, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("r!amECjR7&*VARH<nk>l", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675_mg12093() throws Exception {
        String __DSPOT_key_1079 = "wC-N0Yq:<.K4p}KfwK{.";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("key=\"value &amp;\"" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_1 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_1);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__8);
        attr.toString();
        attr.setKey(__DSPOT_key_1079);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("wC-N0Yq:<.K4p}KfwK{.=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1511254820, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", getValue());
        Assert.assertEquals("wC-N0Yq:<.K4p}KfwK{.", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_1);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__7);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10675__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695litNum11272() throws Exception {
        String s = new String(Character.toChars(135362));
        Assert.assertEquals("\ud844\udcc2", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", getValue());
        Assert.assertEquals("\ud844\udcc2", getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7 = attr.html();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8 = attr.html();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", getValue());
        Assert.assertEquals("\ud844\udcc2", getKey());
        Assert.assertEquals("\ud844\udcc2", s);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", getValue());
        Assert.assertEquals("\ud844\udcc2", getKey());
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", String_21);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696_mg11886() throws Exception {
        Object __DSPOT_o_1043 = new Object();
        Attribute __DSPOT_o_1011 = new Attribute("4^;jR>L|LkAHDEM })hL", ",:!)w`i#o[p]&[Tg[-+.");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__12 = attr.equals(__DSPOT_o_1011);
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696_mg11886__21 = __DSPOT_o_1011.equals(__DSPOT_o_1043);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696_mg11886__21);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10696__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695litString10841() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__10)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", getValue());
        Assert.assertEquals("\ud844\udcc1", getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10695__8);
    }
}

