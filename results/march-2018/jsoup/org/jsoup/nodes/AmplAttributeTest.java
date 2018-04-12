package org.jsoup.nodes;


import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeTest {
    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_0 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_0);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", attr.toString());
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void html() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html__3);
        String o_html__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html__4);
        Assert.assertEquals("key=\"value &amp;\"", attr.toString());
    }

    @Test(timeout = 50000)
    public void htmllitString12_failAssert0() throws Exception {
        try {
            Attribute attr = new Attribute("", "value &");
            attr.html();
            attr.html();
            attr.toString();
            org.junit.Assert.fail("htmllitString12 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void html_sd24() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__4);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
    }

    @Test(timeout = 50000)
    public void html_sd25() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd25__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd25__5);
        String o_html_sd25__6 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd25__6);
        String o_html_sd25__7 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd25__7);
        boolean o_html_sd25__8 = attr.equals(new Attribute("c TM1`_8;0L`A=SO/woO", "!OKS@Rl&{ha!&Bcvg[?i"));
        Assert.assertFalse(o_html_sd25__8);
    }

    @Test(timeout = 50000)
    public void htmllitString14() throws Exception {
        Attribute attr = new Attribute("key", "key=\"value &amp;\"");
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(1434480345, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_htmllitString14__3 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString14__3);
        String o_htmllitString14__4 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_htmllitString14__4);
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", attr.toString());
    }

    @Test(timeout = 50000)
    public void html_add33() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_add33__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add33__3);
        String o_html_add33__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add33__4);
        String o_html_add33__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_add33__5);
        Assert.assertEquals("key=\"value &amp;\"", attr.toString());
    }

    @Test(timeout = 50000)
    public void htmllitString2() throws Exception {
        Attribute attr = new Attribute("A", "value &");
        Assert.assertEquals("A=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(231605526, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("A=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("A", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_htmllitString2__3 = attr.html();
        Assert.assertEquals("A=\"value &amp;\"", o_htmllitString2__3);
        String o_htmllitString2__4 = attr.html();
        Assert.assertEquals("A=\"value &amp;\"", o_htmllitString2__4);
        Assert.assertEquals("A=\"value &amp;\"", attr.toString());
    }

    @Test(timeout = 50000)
    public void html_sd30() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd30__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd30__4);
        String o_html_sd30__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd30__5);
        String o_html_sd30__6 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd30__6);
        attr.setKey("!rb0/|]6^FT)-ef&bk*2");
        Assert.assertEquals("!rb0/|]6^FT)-ef&bk*2=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(-721033140, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("!rb0/|]6^FT)-ef&bk*2=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("!rb0/|]6^FT)-ef&bk*2", ((org.jsoup.nodes.Attribute)attr).getKey());
    }

    @Test(timeout = 50000)
    public void htmllitString23() throws Exception {
        Attribute attr = new Attribute("key", "");
        Assert.assertEquals("key=\"\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(3288449, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_htmllitString23__3 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString23__3);
        String o_htmllitString23__4 = attr.html();
        Assert.assertEquals("key=\"\"", o_htmllitString23__4);
        Assert.assertEquals("key=\"\"", attr.toString());
    }

    @Test(timeout = 50000)
    public void html_sd31() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd31__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd31__4);
        String o_html_sd31__5 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd31__5);
        String o_html_sd31__6 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd31__6);
        Assert.assertEquals("value &", attr.setValue("01yCi*OdwpauR%h1,xav"));
        Assert.assertEquals("key=\"01yCi*OdwpauR%h1,xav\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(-1654182740, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"01yCi*OdwpauR%h1,xav\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("01yCi*OdwpauR%h1,xav", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    @Test(timeout = 50000)
    public void html_sd24_sd1004() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__4);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
        attr.setKey("UN)AU$4HgD@uUId=zIrf");
        Assert.assertEquals("UN)AU$4HgD@uUId=zIrf=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(-997487871, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("UN)AU$4HgD@uUId=zIrf=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("UN)AU$4HgD@uUId=zIrf", ((org.jsoup.nodes.Attribute)attr).getKey());
    }

    @Test(timeout = 50000)
    public void html_sd24_add1018() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__4);
        String o_html_sd24_add1018__9 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24_add1018__9);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
    }

    @Test(timeout = 50000)
    public void html_sd24_sd999() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__4);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
        boolean o_html_sd24_sd999__18 = attr.equals(new Attribute(";F#O7,e!<J!z&e#9R[n4", "Y#_=2(^ZtA&wJD0X/#9$"));
        Assert.assertFalse(o_html_sd24_sd999__18);
    }

    @Test(timeout = 50000)
    public void html_sd24_sd1005() throws Exception {
        Attribute attr = new Attribute("key", "value &");
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__4);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(234891960, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
        Assert.assertEquals("value &", attr.setValue("O$YaM)]&FgYicpi).(9M"));
        Assert.assertEquals("key=\"O$YaM)]&amp;FgYicpi).(9M\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(-960876977, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"O$YaM)]&amp;FgYicpi).(9M\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("O$YaM)]&FgYicpi).(9M", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    @Test(timeout = 50000)
    public void html_sd24_replacement971() throws Exception {
        Attribute attr = new Attribute("Nb,LXZU? pnD>XugatR#", "qBIyo=C)[DU@79_,7q>6");
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#=\"qBIyo=C)[DU@79_,7q>6\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(1679787476, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#=\"qBIyo=C)[DU@79_,7q>6\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("qBIyo=C)[DU@79_,7q>6", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#=\"qBIyo=C)[DU@79_,7q>6\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#=\"qBIyo=C)[DU@79_,7q>6\"", o_html_sd24__4);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#=\"qBIyo=C)[DU@79_,7q>6\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#=\"qBIyo=C)[DU@79_,7q>6\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("qBIyo=C)[DU@79_,7q>6", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("Nb,LXZU? pnD>XugatR#=\"qBIyo=C)[DU@79_,7q>6\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(1679787476, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
    }

    @Test(timeout = 50000)
    public void html_sd24litString978() throws Exception {
        Attribute attr = new Attribute("key=\"value &amp;\"", "value &");
        Assert.assertEquals("key=\"value &amp;\"=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(1648879327, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"value &amp;\"=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"=\"value &amp;\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("key=\"value &amp;\"=\"value &amp;\"", o_html_sd24__4);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("key=\"value &amp;\"=\"value &amp;\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("key=\"value &amp;\"=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("value &", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("key=\"value &amp;\"=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(1648879327, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
    }

    @Test(timeout = 50000)
    public void html_sd24litString989() throws Exception {
        Attribute attr = new Attribute("key", "key=\"value &amp;\"");
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(1434480345, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_html_sd24__3 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_sd24__3);
        String o_html_sd24__4 = attr.html();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_sd24__4);
        String o_html_sd24__5 = attr.toString();
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", o_html_sd24__5);
        Attribute o_html_sd24__6 = attr.clone();
        Assert.assertEquals("key", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getKey());
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).html());
        Assert.assertEquals("key=\"value &amp;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).getValue());
        Assert.assertEquals("key=\"key=&quot;value &amp;amp;&quot;\"", ((org.jsoup.nodes.Attribute)o_html_sd24__6).toString());
        Assert.assertEquals(1434480345, ((int) (((org.jsoup.nodes.Attribute)o_html_sd24__6).hashCode())));
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd4384() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_53 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_53);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4384__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4384__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4384__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4384__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4384__10 = attr.toString();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4384__10);
        Assert.assertEquals("A\ud844\udcc1B", attr.setValue("^O%bGI,_rE.EL5OIC+#&"));
        Assert.assertEquals("\ud844\udcc1", s);
        Assert.assertEquals("\ud844\udcc1=\"^O%bGI,_rE.EL5OIC+#&amp;\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(369251550, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"^O%bGI,_rE.EL5OIC+#&amp;\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("^O%bGI,_rE.EL5OIC+#&", ((org.jsoup.nodes.Attribute)attr).getValue());
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString4335() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("value &" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"value &amp;\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(2092632305, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"value &amp;\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("value &\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_4 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_4);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4335__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"value &amp;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4335__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4335__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"value &amp;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4335__8);
        Assert.assertEquals("\ud844\udcc1=\"value &amp;\ud844\udcc1B\"", attr.toString());
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString4336() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("key=\"value &amp;\"" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(607339824, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("key=\"value &amp;\"\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_5 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_5);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4336__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4336__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4336__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4336__8);
        Assert.assertEquals("\ud844\udcc1=\"key=&quot;value &amp;amp;&quot;\ud844\udcc1B\"", attr.toString());
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_47 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_47);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__10 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__10);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__11 = attr.toString();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__11);
        boolean o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__12 = attr.equals(new Attribute("fdFARw3_8zQEg(a!$A8w", "gtlyB*x1MM%ycxNr1ii0"));
        Assert.assertFalse(o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4378__12);
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString4341() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("vgEGk9>X^c" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"vgEGk9>X^c\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(1987187048, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"vgEGk9>X^c\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("vgEGk9>X^c\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_10 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_10);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4341__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"vgEGk9>X^c\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4341__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4341__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"vgEGk9>X^c\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4341__8);
        Assert.assertEquals("\ud844\udcc1=\"vgEGk9>X^c\ud844\udcc1B\"", attr.toString());
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_46 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_46);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__9 = attr.toString();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__9);
        Attribute o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__10 = attr.clone();
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__10).hashCode())));
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__10).getKey());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__10).html());
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__10).toString());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4377__10).getValue());
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValuelitString4360() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_29 = ((s + "=A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=A\ud844\udcc1B\"", String_29);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4360__7 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4360__7);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4360__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValuelitString4360__8);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", attr.toString());
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_add4387() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add4387__6 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add4387__6);
        String String_56 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_56);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add4387__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add4387__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_add4387__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_add4387__9);
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", attr.toString());
        Assert.assertEquals("\ud844\udcc1", s);
    }

    @Test(timeout = 50000)
    public void testWithSupplementaryCharacterInAttributeKeyAndValue_sd4383() throws Exception {
        String s = new String(Character.toChars(135361));
        Assert.assertEquals("\ud844\udcc1", s);
        Attribute attr = new Attribute(s, (("A" + s) + "B"));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(111849895, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("\ud844\udcc1", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("A\ud844\udcc1B", ((org.jsoup.nodes.Attribute)attr).getValue());
        String String_52 = ((s + "=\"A") + s) + "B\"";
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", String_52);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4383__8 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4383__8);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4383__9 = attr.html();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4383__9);
        String o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4383__10 = attr.toString();
        Assert.assertEquals("\ud844\udcc1=\"A\ud844\udcc1B\"", o_testWithSupplementaryCharacterInAttributeKeyAndValue_sd4383__10);
        attr.setKey("A9TI5}{WfU,YxM%[ch2r");
        Assert.assertEquals("A9TI5}{WfU,YxM%[ch2r=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).html());
        Assert.assertEquals(956131527, ((int) (((org.jsoup.nodes.Attribute)attr).hashCode())));
        Assert.assertEquals("A9TI5}{WfU,YxM%[ch2r=\"A\ud844\udcc1B\"", ((org.jsoup.nodes.Attribute)attr).toString());
        Assert.assertEquals("A9TI5}{WfU,YxM%[ch2r", ((org.jsoup.nodes.Attribute)attr).getKey());
        Assert.assertEquals("\ud844\udcc1", s);
    }
}

