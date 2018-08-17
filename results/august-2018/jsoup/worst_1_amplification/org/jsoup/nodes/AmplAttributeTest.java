package org.jsoup.nodes;


import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeTest {
    @Test(timeout = 10000)
    public void htmllitString15() throws Exception {
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_htmllitString15__3 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        String o_htmllitString15__4 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
        attr.toString();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
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
    public void html_mg23() throws Exception {
        String __DSPOT_key_1 = "z2[|+mr6#-VtX(r!Fs2l";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg23__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__4);
        String o_html_mg23__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__5);
        attr.toString();
        attr.setKey(__DSPOT_key_1);
        Assert.assertEquals("z2[|+mr6#-VtX(r!Fs2l=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2004551670, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("z2[|+mr6#-VtX(r!Fs2l", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__5);
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert1() throws Exception {
        try {
            Attribute attr = new Attribute("", "value &");
            attr.html();
            attr.html();
            attr.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString7() throws Exception {
        Attribute attr = new Attribute("\n", "value &");
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String o_htmllitString7__3 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__3);
        String o_htmllitString7__4 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__4);
        attr.toString();
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__3);
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__4);
    }

    @Test(timeout = 10000)
    public void html_mg20() throws Exception {
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
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg21() throws Exception {
        Object __DSPOT_o_0 = new Object();
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
        Assert.assertFalse(o_html_mg21__8);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void htmllitString11() throws Exception {
        Attribute attr = new Attribute("key", "value@ &");
        Assert.assertEquals("key=\"value@ &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1406906698, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value@ &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_htmllitString11__3 = attr.html();
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__3);
        String o_htmllitString11__4 = attr.html();
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__4);
        attr.toString();
        Assert.assertEquals("key=\"value@ &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1406906698, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value@ &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__3);
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__4);
    }

    @Test(timeout = 10000)
    public void html_mg24_failAssert0() throws Exception {
        try {
            String __DSPOT_val_2 = ">UgIvC=TU&zgYc TM1`_";
            Attribute attr = new Attribute("key", "value &");
            attr.html();
            attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_2);
            org.junit.Assert.fail("html_mg24 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg23_mg612() throws Exception {
        String __DSPOT_key_16 = "0C-?9AC*$S oY.>c^U!$";
        String __DSPOT_key_1 = "z2[|+mr6#-VtX(r!Fs2l";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg23__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__4);
        String o_html_mg23__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__5);
        attr.toString();
        attr.setKey(__DSPOT_key_1);
        attr.setKey(__DSPOT_key_16);
        Assert.assertEquals("0C-?9AC*$S oY.>c^U!$=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1525214191, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("0C-?9AC*$S oY.>c^U!$", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__5);
    }

    @Test(timeout = 10000)
    public void htmllitString8_mg639() throws Exception {
        Attribute attr = new Attribute(":", "value &");
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        String o_htmllitString8__3 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__3);
        String o_htmllitString8__4 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__4);
        attr.toString();
        Attribute o_htmllitString8_mg639__10 = attr.clone();
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (o_htmllitString8_mg639__10)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (o_htmllitString8_mg639__10)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_htmllitString8_mg639__10)).getValue());
        Assert.assertEquals(":", ((Attribute) (o_htmllitString8_mg639__10)).getKey());
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__3);
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__4);
    }

    @Test(timeout = 10000)
    public void htmllitString4litString247() throws Exception {
        Attribute attr = new Attribute("ky", "key=\"value &amp;\"");
        Assert.assertEquals("ky=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1431298474, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("ky", ((Attribute) (attr)).getKey());
        String o_htmllitString4__3 = attr.html();
        Assert.assertEquals("ky=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString4__3);
        String o_htmllitString4__4 = attr.html();
        Assert.assertEquals("ky=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString4__4);
        attr.toString();
        Assert.assertEquals("ky=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1431298474, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("ky", ((Attribute) (attr)).getKey());
        Assert.assertEquals("ky=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString4__3);
        Assert.assertEquals("ky=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString4__4);
    }

    @Test(timeout = 10000)
    public void html_mg21null716() throws Exception {
        Object __DSPOT_o_0 = new Object();
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
        boolean o_html_mg21__8 = attr.equals(null);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg23null714_failAssert6() throws Exception {
        try {
            String __DSPOT_key_1 = null;
            Attribute attr = new Attribute("key", "value &");
            String o_html_mg23__4 = attr.html();
            String o_html_mg23__5 = attr.html();
            attr.toString();
            attr.setKey(__DSPOT_key_1);
            org.junit.Assert.fail("html_mg23null714 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString8_mg640() throws Exception {
        Attribute __DSPOT_o_33 = new Attribute("!2!1tKs!9)M4gfZk]Jpa", "[bR-2-=M,.G+$]g)e+[i");
        Attribute attr = new Attribute(":", "value &");
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        String o_htmllitString8__3 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__3);
        String o_htmllitString8__4 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__4);
        attr.toString();
        boolean o_htmllitString8_mg640__12 = attr.equals(__DSPOT_o_33);
        Assert.assertFalse(o_htmllitString8_mg640__12);
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__3);
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__4);
    }

    @Test(timeout = 10000)
    public void htmllitString11_mg690() throws Exception {
        Object __DSPOT_o_63 = new Object();
        Attribute attr = new Attribute("key", "value@ &");
        Assert.assertEquals("key=\"value@ &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1406906698, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value@ &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_htmllitString11__3 = attr.html();
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__3);
        String o_htmllitString11__4 = attr.html();
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__4);
        attr.toString();
        boolean o_htmllitString11_mg690__12 = attr.equals(__DSPOT_o_63);
        Assert.assertFalse(o_htmllitString11_mg690__12);
        Assert.assertEquals("key=\"value@ &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1406906698, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value@ &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__3);
        Assert.assertEquals("key=\"value@ &amp;\"", o_htmllitString11__4);
    }

    @Test(timeout = 10000)
    public void html_mg23_mg613_failAssert3() throws Exception {
        try {
            String __DSPOT_val_17 = "Cz2lvLY3Pe#L360:}[gY";
            String __DSPOT_key_1 = "z2[|+mr6#-VtX(r!Fs2l";
            Attribute attr = new Attribute("key", "value &");
            String o_html_mg23__4 = attr.html();
            String o_html_mg23__5 = attr.html();
            attr.toString();
            attr.setKey(__DSPOT_key_1);
            attr.setValue(__DSPOT_val_17);
            org.junit.Assert.fail("html_mg23_mg613 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString15_add460() throws Exception {
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        ((Attribute) (attr)).getKey();
        String o_htmllitString15__3 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        String o_htmllitString15__4 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
        attr.toString();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
    }

    @Test(timeout = 10000)
    public void htmllitString14_add400() throws Exception {
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
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\"", o_htmllitString14__3);
        Assert.assertEquals("key=\"\"", o_htmllitString14__4);
    }

    @Test(timeout = 10000)
    public void htmllitString5_add550() throws Exception {
        Attribute attr = new Attribute("dhs", "value &");
        Assert.assertEquals("dhs=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234686120, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("dhs", ((Attribute) (attr)).getKey());
        String o_htmllitString5__3 = attr.html();
        Assert.assertEquals("dhs=\"value &amp;\"", o_htmllitString5__3);
        String o_htmllitString5_add550__6 = attr.html();
        Assert.assertEquals("dhs=\"value &amp;\"", o_htmllitString5_add550__6);
        String o_htmllitString5__4 = attr.html();
        Assert.assertEquals("dhs=\"value &amp;\"", o_htmllitString5__4);
        attr.toString();
        Assert.assertEquals("dhs=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234686120, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("dhs", ((Attribute) (attr)).getKey());
        Assert.assertEquals("dhs=\"value &amp;\"", o_htmllitString5__3);
        Assert.assertEquals("dhs=\"value &amp;\"", o_htmllitString5_add550__6);
        Assert.assertEquals("dhs=\"value &amp;\"", o_htmllitString5__4);
    }

    @Test(timeout = 10000)
    public void html_mg21_remove573() throws Exception {
        Object __DSPOT_o_0 = new Object();
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg24_failAssert0litString291() throws Exception {
        try {
            String __DSPOT_val_2 = ">UgIvC=TU&zgYc TM1`_";
            Attribute attr = new Attribute("key", "m&)<4oK");
            Assert.assertEquals("key=\"m&amp;)<4oK\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(-915445083, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("m&)<4oK", ((Attribute) (attr)).getValue());
            Assert.assertEquals("key", ((Attribute) (attr)).getKey());
            String o_html_mg24_failAssert0litString291__6 = attr.html();
            Assert.assertEquals("key=\"m&amp;)<4oK\"", o_html_mg24_failAssert0litString291__6);
            String o_html_mg24_failAssert0litString291__7 = attr.html();
            Assert.assertEquals("key=\"m&amp;)<4oK\"", o_html_mg24_failAssert0litString291__7);
            attr.toString();
            attr.setValue(__DSPOT_val_2);
            org.junit.Assert.fail("html_mg24 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString7_add455() throws Exception {
        Attribute attr = new Attribute("\n", "value &");
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String o_htmllitString7__3 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__3);
        String o_htmllitString7__4 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__4);
        attr.toString();
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__3);
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__4);
    }

    @Test(timeout = 10000)
    public void html_add19_mg602litString2505_failAssert18() throws Exception {
        try {
            String __DSPOT_key_10 = ",$16qvQ}E3:oK*M=;$a4";
            Attribute attr = new Attribute("", "value &");
            String o_html_add19__3 = attr.html();
            attr.toString();
            attr.toString();
            String o_html_add19__5 = attr.html();
            attr.setKey(__DSPOT_key_10);
            org.junit.Assert.fail("html_add19_mg602litString2505 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg619_add3978() throws Exception {
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
        ((Attribute) (o_html_mg20__6)).toString();
        Attribute o_html_mg20_mg619__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg619__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg619__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg619__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg619__13)).getKey());
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
    public void html_mg23_mg612_add3793() throws Exception {
        String __DSPOT_key_16 = "0C-?9AC*$S oY.>c^U!$";
        String __DSPOT_key_1 = "z2[|+mr6#-VtX(r!Fs2l";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg23__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__4);
        String o_html_mg23__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__5);
        attr.toString();
        attr.setKey(__DSPOT_key_1);
        attr.setKey(__DSPOT_key_16);
        attr.setKey(__DSPOT_key_16);
        Assert.assertEquals("0C-?9AC*$S oY.>c^U!$=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1525214191, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("0C-?9AC*$S oY.>c^U!$", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg23__5);
    }

    @Test(timeout = 10000)
    public void htmllitString14_add400_add3701() throws Exception {
        Attribute attr = new Attribute("key", "");
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        ((Attribute) (attr)).getValue();
        String o_htmllitString14__3 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString14__3);
        String o_htmllitString14__4 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString14__4);
        attr.toString();
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("key=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\"", o_htmllitString14__3);
        Assert.assertEquals("key=\"\"", o_htmllitString14__4);
    }

    @Test(timeout = 10000)
    public void htmllitString3_mg670_mg5903() throws Exception {
        Attribute __DSPOT_o_600 = new Attribute("ICHSS.8xd##-1gQt{UpI", "AbwcHl4Z#%w(Qz(!&?km", new Attributes());
        Attribute __DSPOT_o_51 = new Attribute("&4Bc{vs!b(f2kvd|>op3", "F[UUbDcpS ]x;K>tB$@7");
        Attribute attr = new Attribute("k,ey", "value &");
        Assert.assertEquals("k,ey=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(331831874, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("k,ey", ((Attribute) (attr)).getKey());
        String o_htmllitString3__3 = attr.html();
        Assert.assertEquals("k,ey=\"value &amp;\"", o_htmllitString3__3);
        String o_htmllitString3__4 = attr.html();
        Assert.assertEquals("k,ey=\"value &amp;\"", o_htmllitString3__4);
        attr.toString();
        boolean o_htmllitString3_mg670__12 = attr.equals(__DSPOT_o_51);
        boolean o_htmllitString3_mg670_mg5903__18 = __DSPOT_o_51.equals(__DSPOT_o_600);
        Assert.assertFalse(o_htmllitString3_mg670_mg5903__18);
        Assert.assertEquals("k,ey=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(331831874, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("k,ey", ((Attribute) (attr)).getKey());
        Assert.assertEquals("k,ey=\"value &amp;\"", o_htmllitString3__3);
        Assert.assertEquals("k,ey=\"value &amp;\"", o_htmllitString3__4);
    }

    @Test(timeout = 10000)
    public void html_mg21_add389null6150() throws Exception {
        Object __DSPOT_o_0 = new Object();
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
        boolean o_html_mg21__8 = attr.equals(null);
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void htmllitString15_mg662_remove4953() throws Exception {
        String __DSPOT_key_46 = "A*O^;uS9b&r5!GQi?`Oi";
        Attribute attr = new Attribute("key", "\n");
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_htmllitString15__3 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        String o_htmllitString15__4 = attr.html();
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
        attr.toString();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__3);
        Assert.assertEquals("key=\"\n\"", o_htmllitString15__4);
    }

    @Test(timeout = 10000)
    public void htmllitString3_mg670litString2484() throws Exception {
        Attribute __DSPOT_o_51 = new Attribute("&4Bc{vs!b(f2kvd|>op3", "F[UUbDcpS ]x;K>tB$@7");
        Attribute attr = new Attribute("k,ey", "key=\"value &amp;\"");
        Assert.assertEquals("k,ey=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1531420259, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("k,ey", ((Attribute) (attr)).getKey());
        String o_htmllitString3__3 = attr.html();
        Assert.assertEquals("k,ey=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString3__3);
        String o_htmllitString3__4 = attr.html();
        Assert.assertEquals("k,ey=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString3__4);
        attr.toString();
        boolean o_htmllitString3_mg670__12 = attr.equals(__DSPOT_o_51);
        Assert.assertEquals("k,ey=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1531420259, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("k,ey", ((Attribute) (attr)).getKey());
        Assert.assertEquals("k,ey=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString3__3);
        Assert.assertEquals("k,ey=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString3__4);
    }

    @Test(timeout = 10000)
    public void htmllitString7_add455_remove4982() throws Exception {
        Attribute attr = new Attribute("\n", "value &");
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String o_htmllitString7__3 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__3);
        String o_htmllitString7__4 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__4);
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__3);
        Assert.assertEquals("=\"value &amp;\"", o_htmllitString7__4);
    }

    @Test(timeout = 10000)
    public void html_add19_mg602null6190_failAssert27() throws Exception {
        try {
            String __DSPOT_key_10 = ",$16qvQ}E3:oK*M=;$a4";
            Attribute attr = new Attribute("key", "value &");
            String o_html_add19__3 = attr.html();
            attr.toString();
            attr.toString();
            String o_html_add19__5 = attr.html();
            attr.setKey(null);
            org.junit.Assert.fail("html_add19_mg602null6190 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString8_remove576_mg5338() throws Exception {
        Object __DSPOT_o_261 = new Object();
        Attribute attr = new Attribute(":", "value &");
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        String o_htmllitString8__3 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__3);
        String o_htmllitString8__4 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__4);
        boolean o_htmllitString8_remove576_mg5338__11 = attr.equals(__DSPOT_o_261);
        Assert.assertFalse(o_htmllitString8_remove576_mg5338__11);
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__3);
        Assert.assertEquals(":=\"value &amp;\"", o_htmllitString8__4);
    }

    @Test(timeout = 10000)
    public void html_mg21_add389litString1912() throws Exception {
        Object __DSPOT_o_0 = new Object();
        Attribute attr = new Attribute("key", "zesi<&g");
        Assert.assertEquals("key=\"zesi<&amp;g\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-389476003, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("zesi<&g", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"zesi<&amp;g\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"zesi<&amp;g\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("key=\"zesi<&amp;g\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-389476003, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("zesi<&g", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"zesi<&amp;g\"", o_html_mg21__5);
        Assert.assertEquals("key=\"zesi<&amp;g\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg20_mg619_mg5670() throws Exception {
        String __DSPOT_key_460 = "Z9GL&J.[|N0fR#]G,));";
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
        Attribute o_html_mg20_mg619__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg619__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg619__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg619__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg619__13)).getKey());
        o_html_mg20__6.setKey(__DSPOT_key_460);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("Z9GL&J.[|N0fR#]G,));=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(-1785865936, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("Z9GL&J.[|N0fR#]G,));", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg619__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg619__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg619__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg619__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_add19_mg600_remove4945() throws Exception {
        Object __DSPOT_o_9 = new Object();
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_add19__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add19__3);
        attr.toString();
        String o_html_add19__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add19__5);
        boolean o_html_add19_mg600__13 = attr.equals(__DSPOT_o_9);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_add19__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_add19__5);
    }

    @Test(timeout = 10000)
    public void htmllitString11_mg689_mg5626_failAssert21() throws Exception {
        try {
            String __DSPOT_val_434 = "bFmK@PK!*-phK>AlI+&1";
            Attribute attr = new Attribute("key", "value@ &");
            String o_htmllitString11__3 = attr.html();
            String o_htmllitString11__4 = attr.html();
            attr.toString();
            Attribute o_htmllitString11_mg689__10 = attr.clone();
            o_htmllitString11_mg689__10.setValue(__DSPOT_val_434);
            org.junit.Assert.fail("htmllitString11_mg689_mg5626 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
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
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "=\"A"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6809() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String String_14 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6809__7 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6809__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6809__8 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6809__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_14);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6809__7);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6809__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("B" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"B\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111879686, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("B\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_18 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_18);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"B\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"B\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"B\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111879686, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("B\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_18);
        Assert.assertEquals("\ud844\udcc1=\"B\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__7);
        Assert.assertEquals("\ud844\udcc1=\"B\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819_failAssert33() throws Exception {
        try {
            String __DSPOT_val_734 = "(RXjLAhEj!h48JiBiD4d";
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_6 = ((s + "=\"A") + s) + "B\"";
            attr.html();
            attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_734);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818() throws Exception {
        String __DSPOT_key_733 = "F(`PV[) YbH|gpl(5:px";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_9 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
        attr.toString();
        attr.setKey(__DSPOT_key_733);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-542281721, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816() throws Exception {
        Object __DSPOT_o_732 = new Object();
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_7 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__12 = attr.equals(__DSPOT_o_732);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__12);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_5 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_5);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_5);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6815__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "\n"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849839, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849839, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816null7911() throws Exception {
        Object __DSPOT_o_732 = new Object();
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_7 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__12 = attr.equals(null);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799_mg7857() throws Exception {
        Object __DSPOT_o_777 = new Object();
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, ((":" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\":\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111641358, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals(":\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_19 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_19);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\":\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\":\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799__8);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799_mg7857__16 = attr.equals(__DSPOT_o_777);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799_mg7857__16);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\":\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111641358, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals(":\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_19);
        Assert.assertEquals("\ud844\udcc1=\":\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799__7);
        Assert.assertEquals("\ud844\udcc1=\":\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6799__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803litNum7460() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "\n"));
        Assert.assertEquals("=\"A\u0000\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62475, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__7 = attr.html();
        Assert.assertEquals("=\"A\u0000\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__8 = attr.html();
        Assert.assertEquals("=\"A\u0000\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62475, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_21);
        Assert.assertEquals("=\"A\u0000\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__7);
        Assert.assertEquals("=\"A\u0000\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6803__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795_mg7855_failAssert56() throws Exception {
        try {
            String __DSPOT_val_776 = "QyicB$Wr!!8TVL=2e>nI";
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("B" + s) + "B"));
            String String_18 = ((s + "=\"A") + s) + "B\"";
            String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__7 = attr.html();
            String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795__8 = attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_776);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValuelitString6795_mg7855 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816litString6996() throws Exception {
        Object __DSPOT_o_732 = new Object();
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_7 = ((s + "=A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=A\ud844\udcc1B\"", String_7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__12 = attr.equals(__DSPOT_o_732);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=A\ud844\udcc1B\"", String_7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6816__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6801litNum7484() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "q"));
        Assert.assertEquals("=\"A\u0000q\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62578, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000q", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String String_25 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_25);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6801__7 = attr.html();
        Assert.assertEquals("=\"A\u0000q\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6801__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6801__8 = attr.html();
        Assert.assertEquals("=\"A\u0000q\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6801__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000q\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62578, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000q", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_25);
        Assert.assertEquals("=\"A\u0000q\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6801__7);
        Assert.assertEquals("=\"A\u0000q\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6801__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_mg7821() throws Exception {
        String __DSPOT_key_733 = "F(`PV[) YbH|gpl(5:px";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_9 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
        attr.toString();
        attr.setKey(__DSPOT_key_733);
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_mg7821__16 = attr.clone();
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_mg7821__16)).toString());
        Assert.assertEquals(-542281721, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_mg7821__16)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_mg7821__16)).getValue());
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_mg7821__16)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-542281721, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805_mg7827() throws Exception {
        Attribute __DSPOT_o_759 = new Attribute("cE-;**T^]U-s|uW9iL^k", "HxY.TF__uT{/`ku{46b(", new Attributes());
        String s = new String(Character.toChars(135362));
        Assert.assertEquals("\ud844\udcc2", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc2", ((Attribute) (attr)).getKey());
        String String_12 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", String_12);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805__7 = attr.html();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805__8 = attr.html();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805__8);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805_mg7827__17 = attr.equals(__DSPOT_o_759);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805_mg7827__17);
        Assert.assertEquals("\ud844\udcc2", s);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc2", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", String_12);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805__7);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum6805__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_add6814_add7519() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        ((Attribute) (attr)).toString();
        String String_3 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_3);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add6814__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add6814__7);
        attr.toString();
        attr.toString();
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add6814__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add6814__9);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_3);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add6814__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add6814__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_add7585() throws Exception {
        String __DSPOT_key_733 = "F(`PV[) YbH|gpl(5:px";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_9 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
        attr.toString();
        attr.setKey(__DSPOT_key_733);
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-542281721, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "=\"A"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818null7912_failAssert67() throws Exception {
        try {
            String __DSPOT_key_733 = null;
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_9 = ((s + "=\"A") + s) + "B\"";
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8 = attr.html();
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9 = attr.html();
            attr.toString();
            attr.setKey(__DSPOT_key_733);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818null7912 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7872null15652() throws Exception {
        Object __DSPOT_o_786 = new Object();
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "=\"A"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7872__16 = attr.equals(null);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881_mg15328() throws Exception {
        String __DSPOT_key_1252 = "w_0E#FhFgcYPrwGB;D8|";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_24 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_24);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).getKey());
        o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14.setKey(__DSPOT_key_1252);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_24);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8);
        Assert.assertEquals("w_0E#FhFgcYPrwGB;D8|=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).toString());
        Assert.assertEquals(-763676131, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).getValue());
        Assert.assertEquals("w_0E#FhFgcYPrwGB;D8|", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7881__14)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819_failAssert33null7914_add13724() throws Exception {
        try {
            String __DSPOT_val_734 = null;
            String s = new String(Character.toChars(135361));
            Assert.assertEquals("\ud844\udcc1", s);
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
            Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
            String String_6 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819_failAssert33null7914__10 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819_failAssert33null7914__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819_failAssert33null7914__11 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819_failAssert33null7914__11);
            attr.toString();
            attr.setValue(__DSPOT_val_734);
            attr.setValue(__DSPOT_val_734);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg6819 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7884_mg14676() throws Exception {
        Object __DSPOT_o_861 = new Object();
        String __DSPOT_key_793 = "/h:+#@1&/>m8C)e61J3T";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_24 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_24);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8);
        attr.toString();
        attr.setKey(__DSPOT_key_793);
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7884_mg14676__18 = attr.equals(__DSPOT_o_861);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_mg7884_mg14676__18);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("/h:+#@1&/>m8C)e61J3T=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1529140308, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("/h:+#@1&/>m8C)e61J3T", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_24);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802litString7139_mg15411() throws Exception {
        Attribute __DSPOT_o_1302 = new Attribute("XpoW4cn{P.[!lg#DNuMR", "aLCul42g=c#|E:]WjXj(", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + ""));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(56791969, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_16 = ((s + "=\"A") + s) + "";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_16);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802__8);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802litString7139_mg15411__17 = attr.equals(__DSPOT_o_1302);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802litString7139_mg15411__17);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(56791969, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1", String_16);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6802__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_add7585null15631_failAssert88() throws Exception {
        try {
            String __DSPOT_key_733 = null;
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_9 = ((s + "=\"A") + s) + "B\"";
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8 = attr.html();
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9 = attr.html();
            attr.toString();
            attr.setKey(__DSPOT_key_733);
            ((Attribute) (attr)).getValue();
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_add7585null15631 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_add7722_remove14506() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        ((Attribute) (attr)).getValue();
        String String_24 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_24);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_24);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871_add12573() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "=\"A"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871_add12573__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871_add12573__10);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871__14)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800_mg7871_add12573__10);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6800__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798_add7722litNum11920() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("=\"\n\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(9676, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        ((Attribute) (attr)).getValue();
        String String_24 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_24);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7 = attr.html();
        Assert.assertEquals("=\"\n\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8 = attr.html();
        Assert.assertEquals("=\"\n\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"\n\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(9676, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_24);
        Assert.assertEquals("=\"\n\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__7);
        Assert.assertEquals("=\"\n\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString6798__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818_add7585litString9888() throws Exception {
        String __DSPOT_key_733 = "F(`PV[) YbH|gpl(5:px";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + " "));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1 \"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849861, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1 ", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_9 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1 \"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1 \"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
        attr.toString();
        attr.setKey(__DSPOT_key_733);
        ((Attribute) (attr)).getValue();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px=\"A\ud844\udcc1 \"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-542281755, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1 ", ((Attribute) (attr)).getValue());
        Assert.assertEquals("F(`PV[) YbH|gpl(5:px", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1 \"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1 \"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg6818__9);
    }
}

