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
    public void html_mg21litString458_failAssert8() throws Exception {
        try {
            Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
            Attribute attr = new Attribute("", "value &");
            String o_html_mg21__5 = attr.html();
            String o_html_mg21__6 = attr.html();
            attr.toString();
            boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
            org.junit.Assert.fail("html_mg21litString458 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20litString414() throws Exception {
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
    public void html_mg20litString426() throws Exception {
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
    public void html_mg20_mg979() throws Exception {
        String __DSPOT_key_85 = "@H(x2>iy;hPVv>/f/5!<";
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
        attr.setKey(__DSPOT_key_85);
        Assert.assertEquals("@H(x2>iy;hPVv>/f/5!<=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(930911536, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("@H(x2>iy;hPVv>/f/5!<", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg21_mg981() throws Exception {
        Object __DSPOT_o_86 = new Object();
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
        boolean o_html_mg21_mg981__17 = attr.equals(__DSPOT_o_86);
        Assert.assertFalse(o_html_mg21_mg981__17);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg21litString474() throws Exception {
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("\n", "value &");
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg21_mg962() throws Exception {
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
        String o_html_mg21_mg962__15 = __DSPOT_o_0.html();
        Assert.assertEquals("2 5[gpbL[{$QV5:Wz2[|=\"+mr6#-VtX(r!Fs2l>UgI\"", o_html_mg21_mg962__15);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void html_mg21litString500() throws Exception {
        Attribute __DSPOT_o_0 = new Attribute("2 5[gpbL[{$QV5:Wz2[|", "+mr6#-VtX(r!Fs2l>UgI");
        Attribute attr = new Attribute("key", "key=\"value &amp;\"");
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg21__5 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg21__5);
        String o_html_mg21__6 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg21__6);
        attr.toString();
        boolean o_html_mg21__8 = attr.equals(__DSPOT_o_0);
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg21__5);
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg21__6);
    }

    @Test(timeout = 10000)
    public void htmllitString11_mg773_failAssert13() throws Exception {
        try {
            String __DSPOT_val_39 = "!`j!MNC@I#`g*s,=^$;H";
            Attribute attr = new Attribute("key", "value@ &");
            String o_htmllitString11__3 = attr.html();
            String o_htmllitString11__4 = attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_39);
            org.junit.Assert.fail("htmllitString11_mg773 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20litString352() throws Exception {
        Attribute attr = new Attribute("key", "vakue &");
        Assert.assertEquals("key=\"vakue &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(233968439, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("vakue &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"vakue &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"vakue &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"vakue &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(233968439, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("vakue &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"vakue &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(233968439, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("vakue &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"vakue &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"vakue &amp;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg24_mg770() throws Exception {
        Attribute __DSPOT_o_37 = new Attribute("w#7%vGBT#3Y8uSO_U./J", "9r^9e<Un((j*-vj<@X]Y");
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg24__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg24__3);
        String o_html_mg24__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg24__4);
        attr.toString();
        attr.hashCode();
        boolean o_html_mg24_mg770__13 = attr.equals(__DSPOT_o_37);
        Assert.assertFalse(o_html_mg24_mg770__13);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg24__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg24__4);
    }

    @Test(timeout = 10000)
    public void html_mg26_mg973_failAssert11() throws Exception {
        try {
            String __DSPOT_val_83 = ".&<MPZtI4De0drHp,* B";
            String __DSPOT_key_1 = "vC=TU&zgYc TM1`_8;0L";
            Attribute attr = new Attribute("key", "value &");
            String o_html_mg26__4 = attr.html();
            String o_html_mg26__5 = attr.html();
            attr.toString();
            attr.setKey(__DSPOT_key_1);
            attr.setValue(__DSPOT_val_83);
            org.junit.Assert.fail("html_mg26_mg973 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg986litString5114() throws Exception {
        Attribute attr = new Attribute("key", "m35:6>T");
        Assert.assertEquals("key=\"m35:6>T\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-532243038, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("m35:6>T", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"m35:6>T\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"m35:6>T\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"m35:6>T\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(-532243038, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("m35:6>T", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg986__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"m35:6>T\"", ((Attribute) (o_html_mg20_mg986__13)).toString());
        Assert.assertEquals(-532243038, ((int) (((Attribute) (o_html_mg20_mg986__13)).hashCode())));
        Assert.assertEquals("m35:6>T", ((Attribute) (o_html_mg20_mg986__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg986__13)).getKey());
        Assert.assertEquals("key=\"m35:6>T\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-532243038, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("m35:6>T", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"m35:6>T\"", o_html_mg20__3);
        Assert.assertEquals("key=\"m35:6>T\"", o_html_mg20__4);
        Assert.assertEquals("key=\"m35:6>T\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(-532243038, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("m35:6>T", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg989_mg9804() throws Exception {
        Object __DSPOT_o_997 = new Object();
        Attribute __DSPOT_o_88 = new Attribute("^QAT?>YN{q48ErJ,Q*wO", "|INCdXv>?dZt}}IntEF_");
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
        boolean o_html_mg20_mg989__15 = o_html_mg20__6.equals(__DSPOT_o_88);
        boolean o_html_mg20_mg989_mg9804__20 = __DSPOT_o_88.equals(__DSPOT_o_997);
        Assert.assertFalse(o_html_mg20_mg989_mg9804__20);
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
    public void html_mg20_mg942_remove4677() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg942__13 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg942__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg942__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg942__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg942__13)).getKey());
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
    public void html_mg20_mg942litString3762() throws Exception {
        Attribute attr = new Attribute("\n", "value &");
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("=\"value &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg942__13 = attr.clone();
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (o_html_mg20_mg942__13)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (o_html_mg20_mg942__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg942__13)).getValue());
        Assert.assertEquals("", ((Attribute) (o_html_mg20_mg942__13)).getKey());
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg986_mg7579_failAssert15() throws Exception {
        try {
            String __DSPOT_val_728 = "]oQ/a!Y{}tejH[6dcJi+";
            Attribute attr = new Attribute("key", "value &");
            String o_html_mg20__3 = attr.html();
            String o_html_mg20__4 = attr.html();
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            Attribute o_html_mg20_mg986__13 = o_html_mg20__6.clone();
            o_html_mg20__6.setValue(__DSPOT_val_728);
            org.junit.Assert.fail("html_mg20_mg986_mg7579 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg942litString3840() throws Exception {
        Attribute attr = new Attribute("key", "v<alue &");
        Assert.assertEquals("key=\"v<alue &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-207524664, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("v<alue &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"v<alue &amp;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"v<alue &amp;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"v<alue &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(-207524664, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("v<alue &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Attribute o_html_mg20_mg942__13 = attr.clone();
        Assert.assertEquals("key=\"v<alue &amp;\"", ((Attribute) (o_html_mg20_mg942__13)).toString());
        Assert.assertEquals(-207524664, ((int) (((Attribute) (o_html_mg20_mg942__13)).hashCode())));
        Assert.assertEquals("v<alue &", ((Attribute) (o_html_mg20_mg942__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg942__13)).getKey());
        Assert.assertEquals("key=\"v<alue &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-207524664, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("v<alue &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"v<alue &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"v<alue &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"v<alue &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(-207524664, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("v<alue &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg942litString3896() throws Exception {
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
        Attribute o_html_mg20_mg942__13 = attr.clone();
        Assert.assertEquals("key=\"\"", ((Attribute) (o_html_mg20_mg942__13)).toString());
        Assert.assertEquals(3288449, ((int) (((Attribute) (o_html_mg20_mg942__13)).hashCode())));
        Assert.assertEquals("", ((Attribute) (o_html_mg20_mg942__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg942__13)).getKey());
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
    public void html_mg20_mg986litString4895_failAssert20() throws Exception {
        try {
            Attribute attr = new Attribute("", "value &");
            String o_html_mg20__3 = attr.html();
            String o_html_mg20__4 = attr.html();
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            Attribute o_html_mg20_mg986__13 = o_html_mg20__6.clone();
            org.junit.Assert.fail("html_mg20_mg986litString4895 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg942litString3911() throws Exception {
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
        Attribute o_html_mg20_mg942__13 = attr.clone();
        Assert.assertEquals("key=\"\n\"", ((Attribute) (o_html_mg20_mg942__13)).toString());
        Assert.assertEquals(3288459, ((int) (((Attribute) (o_html_mg20_mg942__13)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (o_html_mg20_mg942__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg942__13)).getKey());
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
    public void html_mg20_mg986_mg7435() throws Exception {
        String __DSPOT_key_723 = "G^_<CFsw3r[e8NYbEots";
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
        Attribute o_html_mg20_mg986__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg986__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg986__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg986__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg986__13)).getKey());
        o_html_mg20__6.setKey(__DSPOT_key_723);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("G^_<CFsw3r[e8NYbEots=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(-2006422293, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("G^_<CFsw3r[e8NYbEots", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg986__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg986__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg986__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg986__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg989_mg9950() throws Exception {
        Attribute __DSPOT_o_1043 = new Attribute("WRVO/&FJ!l$&#wFDX!:p", "-+2y(1P47O[B;V#jrd8|");
        Attribute __DSPOT_o_88 = new Attribute("^QAT?>YN{q48ErJ,Q*wO", "|INCdXv>?dZt}}IntEF_");
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
        boolean o_html_mg20_mg989__15 = o_html_mg20__6.equals(__DSPOT_o_88);
        boolean o_html_mg20_mg989_mg9950__20 = o_html_mg20__6.equals(__DSPOT_o_1043);
        Assert.assertFalse(o_html_mg20_mg989_mg9950__20);
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
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28() throws Exception {
        try {
            String __DSPOT_val_1056 = "cY=$vZ|8}hv&4}.qZy2[";
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_28 = ((s + "=\"A") + s) + "B\"";
            attr.html();
            attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_1056);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10581() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String String_15 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10581__7 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10581__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10581__8 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10581__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10581__7);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum10581__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588() throws Exception {
        Attribute __DSPOT_o_1054 = new Attribute("[]@*K{,5)@@QU->@[9Yw", "yGl6[sO9wb=K|B^[?;_V");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__10);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__12 = attr.equals(__DSPOT_o_1054);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__12);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10588__10);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10593() throws Exception {
        String __DSPOT_key_1055 = "oY8Zn1}mQRX/Q1fmP&HT";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_27 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10593__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10593__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10593__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10593__9);
        attr.toString();
        attr.setKey(__DSPOT_key_1055);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("oY8Zn1}mQRX/Q1fmP&HT=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1888545948, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("oY8Zn1}mQRX/Q1fmP&HT", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10593__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10593__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10595() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_29 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_29);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10595__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10595__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10595__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10595__8);
        attr.toString();
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_29);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10595__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10595__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString10570() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_4 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10570__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10570__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10570__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10570__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10570__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10570__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString10572() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "=\"A"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_6 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10572__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10572__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10572__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10572__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10572__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString10572__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11019() throws Exception {
        try {
            String __DSPOT_val_1056 = "cY=$vZ|8}hv&4}.qZy2[";
            String s = new String(Character.toChars(135361));
            Assert.assertEquals("\ud844\udcc1", s);
            Attribute attr = new Attribute(s, (("A" + s) + "=\"A"));
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(-1105268159, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("A\ud844\udcc1=\"A", ((Attribute) (attr)).getValue());
            Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
            String String_28 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11019__10 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11019__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11019__11 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1=&quot;A\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11019__11);
            attr.toString();
            attr.setValue(__DSPOT_val_1056);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litNum11304() throws Exception {
        try {
            String __DSPOT_val_1056 = "cY=$vZ|8}hv&4}.qZy2[";
            String s = new String(Character.toChars(0));
            Assert.assertEquals("\u0000", s);
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
            Assert.assertEquals("", ((Attribute) (attr)).getKey());
            String String_28 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\u0000=\"A\u0000B\"", String_28);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litNum11304__10 = attr.html();
            Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litNum11304__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litNum11304__11 = attr.html();
            Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litNum11304__11);
            attr.toString();
            attr.setValue(__DSPOT_val_1056);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12071() throws Exception {
        String __DSPOT_key_1148 = "2O8[=[aDFG1Md)#1Bm+Q";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.setKey(__DSPOT_key_1148);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("2O8[=[aDFG1Md)#1Bm+Q=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(-1201416350, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("2O8[=[aDFG1Md)#1Bm+Q", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587litString11280() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=*\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=*\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=*\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28_add11456() throws Exception {
        try {
            String __DSPOT_val_1056 = "cY=$vZ|8}hv&4}.qZy2[";
            String s = new String(Character.toChars(135361));
            Assert.assertEquals("\ud844\udcc1", s);
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
            Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
            String String_28 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28_add11456__10 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28_add11456__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28_add11456__11 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28_add11456__11);
            attr.toString();
            attr.setValue(__DSPOT_val_1056);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11063() throws Exception {
        try {
            String __DSPOT_val_1056 = "cY=$vZ|8}hv&4}.qZy2[";
            String s = new String(Character.toChars(135361));
            Assert.assertEquals("\ud844\udcc1", s);
            Attribute attr = new Attribute(s, (("A" + s) + "\n"));
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(111849839, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("A\ud844\udcc1\n", ((Attribute) (attr)).getValue());
            Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
            String String_28 = ((s + "=\"A") + s) + "B\"";
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_28);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11063__10 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11063__10);
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11063__11 = attr.html();
            Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1\n\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594_failAssert28litString11063__11);
            attr.toString();
            attr.setValue(__DSPOT_val_1056);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10594 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12045() throws Exception {
        Attribute __DSPOT_o_1140 = new Attribute("u=L>KvS?I1uL8LQfY><m", "9|^gzH,eMfnA(A[UrF(%");
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12045__19 = attr.equals(__DSPOT_o_1140);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12045__19);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066_mg15281() throws Exception {
        String __DSPOT_val_1287 = "W#j7x/L]c{A3`H!@jU-u";
        Attribute __DSPOT_o_1147 = new Attribute("0]_n},H9{K (]AgQ|37L", "+#zz3m&;O|lHKp}-H8).", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066__20 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.equals(__DSPOT_o_1147);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066_mg15281__24 = __DSPOT_o_1147.setValue(__DSPOT_val_1287);
        Assert.assertEquals("", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066_mg15281__24);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065_mg14612() throws Exception {
        Object __DSPOT_o_1226 = new Object();
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065_mg14612__22 = attr.equals(__DSPOT_o_1226);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065_mg14612__22);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066_mg15381() throws Exception {
        Attribute __DSPOT_o_1297 = new Attribute("99/_yG8oTM^=Ru3:p|4G", "7ML,6nq(r|}kb3gQxp[R");
        Attribute __DSPOT_o_1147 = new Attribute("0]_n},H9{K (]AgQ|37L", "+#zz3m&;O|lHKp}-H8).", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066__20 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.equals(__DSPOT_o_1147);
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066_mg15381__25 = attr.equals(__DSPOT_o_1297);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066_mg15381__25);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042_mg16948() throws Exception {
        String __DSPOT_key_1473 = "pgpYQ:6a8Kpk=yGd-9V_";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).getKey());
        o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17.setKey(__DSPOT_key_1473);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Assert.assertEquals("pgpYQ:6a8Kpk=yGd-9V_=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).toString());
        Assert.assertEquals(-1491933932, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).getValue());
        Assert.assertEquals("pgpYQ:6a8Kpk=yGd-9V_", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12042__17)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065litString12702() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.clone();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065litString12669() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("key=\"value &amp;\"" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.clone();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(607339824, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066_mg15251() throws Exception {
        String __DSPOT_key_1284 = "Y0hBt9R$@E%06P$>2OoJ";
        Attribute __DSPOT_o_1147 = new Attribute("0]_n},H9{K (]AgQ|37L", "+#zz3m&;O|lHKp}-H8).", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12066__20 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.equals(__DSPOT_o_1147);
        __DSPOT_o_1147.setKey(__DSPOT_key_1284);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065litNum13194() throws Exception {
        String s = new String(Character.toChars(135362));
        Assert.assertEquals("\ud844\udcc2", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc2", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc2", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.clone();
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getValue());
        Assert.assertEquals("\ud844\udcc2", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getKey());
        Assert.assertEquals("\ud844\udcc2", s);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc2", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", String_21);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("\ud844\udcc2=\"A\ud844\udcc2B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(111849957, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc2B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("\ud844\udcc2", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12070_mg16981_failAssert51() throws Exception {
        try {
            String __DSPOT_val_1476 = " @{ed-aNUomo$O+{R>s%";
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_21 = ((s + "=\"A") + s) + "B\"";
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
            attr.toString();
            Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
            String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12070__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.html();
            o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.setValue(__DSPOT_val_1476);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12070_mg16981 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065litNum13280() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10 = attr.clone();
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17 = o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10.clone();
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getValue());
        Assert.assertEquals("", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587_mg12065__17)).getKey());
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_21);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__7);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__8);
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getValue());
        Assert.assertEquals("", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg10587__10)).getKey());
    }
}

