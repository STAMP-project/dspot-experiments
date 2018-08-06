package org.jsoup.nodes;


import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeTest {
    @Test(timeout = 10000)
    public void html() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        String o_html__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html__4);
        attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html__4);
    }

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
    public void html_mg27() throws Exception {
        String __DSPOT_value_2 = "`A=SO/woO!OKS@Rl&{ha";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg27__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        String o_html_mg27__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
        attr.toString();
        String o_html_mg27__7 = attr.setValue(__DSPOT_value_2);
        Assert.assertEquals("value &", o_html_mg27__7);
        Assert.assertEquals("key=\"`A=SO/woO!OKS@Rl&amp;{ha\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1958088499, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("`A=SO/woO!OKS@Rl&{ha", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
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
    public void htmllitString13() throws Exception {
        Attribute attr = new Attribute("key", "x*zH_,y");
        Assert.assertEquals("key=\"x*zH_,y\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(446879405, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("x*zH_,y", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_htmllitString13__3 = attr.html();
        Assert.assertEquals("key=\"x*zH_,y\"", o_htmllitString13__3);
        String o_htmllitString13__4 = attr.html();
        Assert.assertEquals("key=\"x*zH_,y\"", o_htmllitString13__4);
        attr.toString();
        Assert.assertEquals("key=\"x*zH_,y\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(446879405, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("x*zH_,y", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"x*zH_,y\"", o_htmllitString13__3);
        Assert.assertEquals("key=\"x*zH_,y\"", o_htmllitString13__4);
    }

    @Test(timeout = 10000)
    public void html_mg26() throws Exception {
        String __DSPOT_key_1 = "vC=TU&zgYc TM1`_8;0L";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg26__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__4);
        String o_html_mg26__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__5);
        attr.toString();
        attr.setKey(__DSPOT_key_1);
        Assert.assertEquals("vC=TU&zgYc TM1`_8;0L=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1807868971, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("vC=TU&zgYc TM1`_8;0L", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg26__5);
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
    public void htmllitString6_failAssert0() throws Exception {
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
    public void html_mg21() throws Exception {
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
        Assert.assertFalse(o_html_mg21__8);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_add17() throws Exception {
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
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_add17__4);
    }

    @Test(timeout = 10000)
    public void html_mg20litString403() throws Exception {
        Attribute attr = new Attribute(":", "value &");
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals(":=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals(":", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals(":=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231605309, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals(":", ((Attribute) (attr)).getKey());
        Assert.assertEquals(":=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals(":=\"value &amp;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg21_mg2329() throws Exception {
        Attribute __DSPOT_o_102 = new Attribute("R!Y({UBa;x4GTC}l]qve", "lpPW`h79`wVYde!kAeH-");
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
        boolean o_html_mg21_mg2329__17 = attr.equals(__DSPOT_o_102);
        Assert.assertFalse(o_html_mg21_mg2329__17);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg20litString467() throws Exception {
        Attribute attr = new Attribute("key", "viVTx>D");
        Assert.assertEquals("key=\"viVTx>D\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(442643822, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("viVTx>D", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"viVTx>D\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"viVTx>D\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"viVTx>D\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(442643822, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("viVTx>D", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"viVTx>D\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(442643822, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("viVTx>D", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"viVTx>D\"", o_html_mg20__3);
        Assert.assertEquals("key=\"viVTx>D\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void htmlnull32null1031() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_htmlnull32__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_htmlnull32__3);
        attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_htmlnull32__3);
    }

    @Test(timeout = 10000)
    public void html_mg20litString478() throws Exception {
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
    public void html_mg20_add1289() throws Exception {
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
    public void html_mg20litString374_failAssert3() throws Exception {
        try {
            Attribute attr = new Attribute("", "value &");
            String o_html_mg20__3 = attr.html();
            String o_html_mg20__4 = attr.html();
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            org.junit.Assert.fail("html_mg20litString374 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20litString486() throws Exception {
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
        Assert.assertEquals("key=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"\n\"", o_html_mg20__3);
        Assert.assertEquals("key=\"\n\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg27_mg2690() throws Exception {
        Attribute __DSPOT_o_111 = new Attribute("d>#Um]cj_J,=?RrT=9@K", "Izr<#pvNdk}DF>z?CdS`");
        String __DSPOT_value_2 = "`A=SO/woO!OKS@Rl&{ha";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg27__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        String o_html_mg27__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
        attr.toString();
        String o_html_mg27__7 = attr.setValue(__DSPOT_value_2);
        Assert.assertEquals("value &", o_html_mg27__7);
        boolean o_html_mg27_mg2690__16 = attr.equals(__DSPOT_o_111);
        Assert.assertFalse(o_html_mg27_mg2690__16);
        Assert.assertEquals("key=\"`A=SO/woO!OKS@Rl&amp;{ha\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1958088499, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("`A=SO/woO!OKS@Rl&{ha", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
        Assert.assertEquals("value &", o_html_mg27__7);
    }

    @Test(timeout = 10000)
    public void html_mg27_mg2749() throws Exception {
        String __DSPOT_key_112 = "djm0dLB:fH=q(]@Dt@)l";
        String __DSPOT_value_2 = "`A=SO/woO!OKS@Rl&{ha";
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg27__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        String o_html_mg27__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
        attr.toString();
        String o_html_mg27__7 = attr.setValue(__DSPOT_value_2);
        Assert.assertEquals("value &", o_html_mg27__7);
        attr.setKey(__DSPOT_key_112);
        Assert.assertEquals("djm0dLB:fH=q(]@Dt@)l=\"`A=SO/woO!OKS@Rl&amp;{ha\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1554890901, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("`A=SO/woO!OKS@Rl&{ha", ((Attribute) (attr)).getValue());
        Assert.assertEquals("djm0dLB:fH=q(]@Dt@)l", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__4);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg27__5);
        Assert.assertEquals("value &", o_html_mg27__7);
    }

    @Test(timeout = 10000)
    public void html_mg20_mg2022_mg13179() throws Exception {
        Attribute __DSPOT_o_623 = new Attribute("XMm-qALi`v,g$D^sLTm}", "xyjwC0Fq#t[OEI_b%*ep");
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
        Attribute o_html_mg20_mg2022__13 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg2022__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg2022__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg2022__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg2022__13)).getKey());
        boolean o_html_mg20_mg2022_mg13179__18 = attr.equals(__DSPOT_o_623);
        Assert.assertFalse(o_html_mg20_mg2022_mg13179__18);
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
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg2022__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg2022__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg2022__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg2022__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg2322_mg10446() throws Exception {
        Attribute __DSPOT_o_497 = new Attribute("(m&/v)K66%8i@y[`--]/", "De]txC#Z;%zMoB,TM(Y{");
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
        Attribute o_html_mg20_mg2322__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg2322__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg2322__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg2322__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg2322__13)).getKey());
        boolean o_html_mg20_mg2322_mg10446__18 = attr.equals(__DSPOT_o_497);
        Assert.assertFalse(o_html_mg20_mg2322_mg10446__18);
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
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg2322__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg2322__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg2322__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg2322__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20null2822null7029() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = null;
        attr.toString();
        Attribute o_html_mg20__6 = null;
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
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772() throws Exception {
        Attribute __DSPOT_o_945 = new Attribute("Gce8elt0dD-NSMm!5|&6", "+K_`9kM)Ty(0UeX0/ Kf");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__12 = attr.equals(__DSPOT_o_945);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__12);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20772__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778() throws Exception {
        String __DSPOT_value_947 = "gF#)(h(WooM2 JUUR5%d";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_28 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__9);
        attr.toString();
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__11 = attr.setValue(__DSPOT_value_947);
        Assert.assertEquals("A\ud844\udcc1B", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__11);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"gF#)(h(WooM2 JUUR5%d\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2022373912, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("gF#)(h(WooM2 JUUR5%d", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20778__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_add20767() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_17 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add20767__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add20767__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add20767__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add20767__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_17);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add20767__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add20767__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuenull20780_failAssert13() throws Exception {
        try {
            String s = null;
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_30 = ((s + "=\"A") + s) + "B\"";
            attr.html();
            attr.html();
            attr.toString();
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValuenull20780 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg20777() throws Exception {
        String __DSPOT_key_946 = "Dsu_NX/#>Eoa#C T1whE";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_27 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20777__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20777__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20777__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20777__9);
        attr.toString();
        attr.setKey(__DSPOT_key_946);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("Dsu_NX/#>Eoa#C T1whE=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1932019453, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Dsu_NX/#>Eoa#C T1whE", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20777__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20777__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString20756() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "value &"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(182654236, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_6 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20756__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1value &amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20756__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20756__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1value &amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20756__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(182654236, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1value &amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20756__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1value &amp;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20756__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString20754() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_4 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20754__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20754__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20754__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20754__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20754__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString20754__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg20771__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitNum20765() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String String_15 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum20765__7 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum20765__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum20765__8 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum20765__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum20765__7);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum20765__8);
    }
}

