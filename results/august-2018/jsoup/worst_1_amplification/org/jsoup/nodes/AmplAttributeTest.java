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
    public void htmllitString1() throws Exception {
        Attribute attr = new Attribute("value &", "value &");
        Assert.assertEquals("value &=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1178622240, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("value &", ((Attribute) (attr)).getKey());
        String o_htmllitString1__3 = attr.html();
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__3);
        String o_htmllitString1__4 = attr.html();
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__4);
        attr.toString();
        Assert.assertEquals("value &=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-1178622240, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("value &", ((Attribute) (attr)).getKey());
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__3);
        Assert.assertEquals("value &=\"value &amp;\"", o_htmllitString1__4);
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
    public void html_mg20litString603() throws Exception {
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
    public void html_mg27_failAssert1litString377_failAssert4() throws Exception {
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
            org.junit.Assert.fail("html_mg27_failAssert1litString377 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("String must not be empty", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1821() throws Exception {
        String __DSPOT_key_106 = "D>!zc90 kasM39!TnKT>";
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
        o_html_mg20__6.setKey(__DSPOT_key_106);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("D>!zc90 kasM39!TnKT>=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(973592238, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("D>!zc90 kasM39!TnKT>", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1333() throws Exception {
        Object __DSPOT_o_75 = new Object();
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
        boolean o_html_mg20_mg1333__15 = attr.equals(__DSPOT_o_75);
        Assert.assertFalse(o_html_mg20_mg1333__15);
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
    public void html_mg20litString461() throws Exception {
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
        Assert.assertEquals("=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(231603511, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("=\"value &amp;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1511_failAssert6() throws Exception {
        try {
            String __DSPOT_val_92 = "RDg-zRApBXKD>MAy9vP&";
            Attribute attr = new Attribute("key", "value &");
            String o_html_mg20__3 = attr.html();
            String o_html_mg20__4 = attr.html();
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            attr.setValue(__DSPOT_val_92);
            org.junit.Assert.fail("html_mg20_mg1511 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20litString607() throws Exception {
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
    public void html_mg20litString519() throws Exception {
        Attribute attr = new Attribute("key", "key=\"value &amp;\"");
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        String o_html_mg20__3 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__3);
        String o_html_mg20__4 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__4);
        attr.toString();
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(1434480345, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__4);
    }

    @Test(timeout = 10000)
    public void html_mg20null2302() throws Exception {
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
    public void html_mg20_mg1579_mg15941() throws Exception {
        Attribute __DSPOT_o_779 = new Attribute("]+JMy.|R#Xc#*jq`)S|i", "YAu(TY#hoWZ5vY0z+Tb&");
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
        Attribute o_html_mg20_mg1579__13 = o_html_mg20__6.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg1579__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg1579__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg1579__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg1579__13)).getKey());
        boolean o_html_mg20_mg1579_mg15941__18 = o_html_mg20_mg1579__13.equals(__DSPOT_o_779);
        Assert.assertFalse(o_html_mg20_mg1579_mg15941__18);
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
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_mg1579__13)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_mg1579__13)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_mg1579__13)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_mg1579__13)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1863_failAssert5litString7366() throws Exception {
        try {
            String __DSPOT_val_107 = "gIevY]dkqSht.80@MCF3";
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
            o_html_mg20__6.setValue(__DSPOT_val_107);
            org.junit.Assert.fail("html_mg20_mg1863 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1863_failAssert5litString7376() throws Exception {
        try {
            String __DSPOT_val_107 = "gIevY]dkqSht.80@MCF3";
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
            o_html_mg20__6.setValue(__DSPOT_val_107);
            org.junit.Assert.fail("html_mg20_mg1863 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1511_failAssert6litString4941() throws Exception {
        try {
            String __DSPOT_val_92 = "RDg-zRApBXKD>MAy9vP&";
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
            attr.setValue(__DSPOT_val_92);
            org.junit.Assert.fail("html_mg20_mg1511 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1863_failAssert5litString7314() throws Exception {
        try {
            String __DSPOT_val_107 = "gIevY]dkqSht.80@MCF3";
            Attribute attr = new Attribute("key", "key=\"value &amp;\"");
            Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(1434480345, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).getValue());
            Assert.assertEquals("key", ((Attribute) (attr)).getKey());
            String o_html_mg20__3 = attr.html();
            Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__3);
            String o_html_mg20__4 = attr.html();
            Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_mg20__4);
            attr.toString();
            Attribute o_html_mg20__6 = attr.clone();
            Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((Attribute) (o_html_mg20__6)).toString());
            Assert.assertEquals(1434480345, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
            Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).getValue());
            Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
            o_html_mg20__6.setValue(__DSPOT_val_107);
            org.junit.Assert.fail("html_mg20_mg1863 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1863_failAssert5litString7275_failAssert11() throws Exception {
        try {
            try {
                String __DSPOT_val_107 = "gIevY]dkqSht.80@MCF3";
                Attribute attr = new Attribute("", "value &");
                String o_html_mg20__3 = attr.html();
                String o_html_mg20__4 = attr.html();
                attr.toString();
                Attribute o_html_mg20__6 = attr.clone();
                o_html_mg20__6.setValue(__DSPOT_val_107);
                org.junit.Assert.fail("html_mg20_mg1863 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("html_mg20_mg1863_failAssert5litString7275 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("String must not be empty", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_mg1863_failAssert5_mg7734() throws Exception {
        try {
            String __DSPOT_val_107 = "gIevY]dkqSht.80@MCF3";
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
            o_html_mg20__6.setValue(__DSPOT_val_107);
            org.junit.Assert.fail("html_mg20_mg1863 should have thrown NullPointerException");
            attr.clone();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void html_mg20_add1034_mg16524() throws Exception {
        String __DSPOT_key_811 = "kvHITA>|4}oV=/6`#K(?";
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
        Attribute o_html_mg20_add1034__10 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_add1034__10)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_add1034__10)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_add1034__10)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_add1034__10)).getKey());
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        o_html_mg20__6.setKey(__DSPOT_key_811);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_add1034__10)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_add1034__10)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_add1034__10)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_add1034__10)).getKey());
        Assert.assertEquals("kvHITA>|4}oV=/6`#K(?=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(-310779595, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("kvHITA>|4}oV=/6`#K(?", ((Attribute) (o_html_mg20__6)).getKey());
    }

    @Test(timeout = 10000)
    public void html_mg20_add1034_mg15853() throws Exception {
        Object __DSPOT_o_769 = new Object();
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
        Attribute o_html_mg20_add1034__10 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_add1034__10)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_add1034__10)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_add1034__10)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_add1034__10)).getKey());
        Attribute o_html_mg20__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20__6)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20__6)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20__6)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20__6)).getKey());
        boolean o_html_mg20_add1034_mg15853__18 = o_html_mg20_add1034__10.equals(__DSPOT_o_769);
        Assert.assertFalse(o_html_mg20_add1034_mg15853__18);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (attr)).getValue());
        Assert.assertEquals("key", ((Attribute) (attr)).getKey());
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__3);
        Assert.assertEquals("key=\"value &amp;\"", o_html_mg20__4);
        Assert.assertEquals("key=\"value &amp;\"", ((Attribute) (o_html_mg20_add1034__10)).toString());
        Assert.assertEquals(234891960, ((int) (((Attribute) (o_html_mg20_add1034__10)).hashCode())));
        Assert.assertEquals("value &", ((Attribute) (o_html_mg20_add1034__10)).getValue());
        Assert.assertEquals("key", ((Attribute) (o_html_mg20_add1034__10)).getKey());
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
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitNum18404() throws Exception {
        String s = new String(Character.toChars(0));
        Assert.assertEquals("\u0000", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        String String_15 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum18404__7 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum18404__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum18404__8 = attr.html();
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum18404__8);
        attr.toString();
        Assert.assertEquals("\u0000", s);
        Assert.assertEquals("=\"A\u0000B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(62531, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\u0000B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\u0000=\"A\u0000B\"", String_15);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum18404__7);
        Assert.assertEquals("=\"A\u0000B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitNum18404__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_21 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__8);
        attr.toString();
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__10 = attr.clone();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__10)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__10)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__10)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__10)).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_21);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18410__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString18395() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "key=\"value &amp;\""));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(481767101, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_6 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18395__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1key=&quot;value &amp;amp;&quot;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18395__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18395__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1key=&quot;value &amp;amp;&quot;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18395__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1key=&quot;value &amp;amp;&quot;\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(481767101, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1key=\"value &amp;\"", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_6);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1key=&quot;value &amp;amp;&quot;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18395__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1key=&quot;value &amp;amp;&quot;\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18395__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411() throws Exception {
        Attribute __DSPOT_o_840 = new Attribute("{v?wMQH=%>#xANJd<+`E", "Z2Rfcz}x?.SiB$<LwqY*", new Attributes());
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_22 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__10);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__11 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__11);
        attr.toString();
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__13 = attr.equals(__DSPOT_o_840);
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__13);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_22);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__10);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18411__11);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg18416() throws Exception {
        String __DSPOT_key_841 = "6G[aO8n|(<9I>y]$!7am";
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849895, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_27 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18416__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18416__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18416__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18416__9);
        attr.toString();
        attr.setKey(__DSPOT_key_841);
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("6G[aO8n|(<9I>y]$!7am=\"A\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(459444526, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("6G[aO8n|(<9I>y]$!7am", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_27);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18416__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_mg18416__9);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString18393() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("\n" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_4 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18393__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18393__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18393__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18393__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(110211390, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n\ud844\udcc1B", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18393__7);
        Assert.assertEquals("\ud844\udcc1=\"\n\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18393__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuenull18419_failAssert18() throws Exception {
        try {
            String s = null;
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_30 = ((s + "=\"A") + s) + "B\"";
            attr.html();
            attr.html();
            attr.toString();
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValuenull18419 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg18417_failAssert17() throws Exception {
        try {
            String __DSPOT_val_842 = "Cx[iE+0Cy+kO@)@{T3[#";
            String s = new String(Character.toChars(135361));
            Attribute attr = new Attribute(s, (("A" + s) + "B"));
            String String_28 = ((s + "=\"A") + s) + "B\"";
            attr.html();
            attr.html();
            attr.toString();
            attr.setValue(__DSPOT_val_842);
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg18417 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString18399() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + ":"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849887, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1:", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        String String_10 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_10);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18399__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18399__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18399__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18399__8);
        attr.toString();
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(111849887, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("A\ud844\udcc1:", ((Attribute) (attr)).getValue());
        Assert.assertEquals("\ud844\udcc1", ((Attribute) (attr)).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_10);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18399__7);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1:\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString18399__8);
    }

    @Test(timeout = 10000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_mg18417_failAssert17null19678_failAssert24() throws Exception {
        try {
            try {
                String __DSPOT_val_842 = "Cx[iE+0Cy+kO@)@{T3[#";
                String s = null;
                Attribute attr = new Attribute(s, (("A" + s) + "B"));
                String String_28 = ((s + "=\"A") + s) + "B\"";
                attr.html();
                attr.html();
                attr.toString();
                attr.setValue(__DSPOT_val_842);
                org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg18417 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testWithSupplementaryCharacterInAttributeKeyAndValue_mg18417_failAssert17null19678 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }
}

