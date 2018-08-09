package org.jsoup.nodes;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AttributesTest {
    @Test
    public void html() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        Assert.assertEquals(3, a.size());
        Assert.assertTrue(a.hasKey("Tot"));
        Assert.assertTrue(a.hasKey("Hello"));
        Assert.assertTrue(a.hasKey("data-name"));
        Assert.assertFalse(a.hasKey("tot"));
        Assert.assertTrue(a.hasKeyIgnoreCase("tot"));
        Assert.assertEquals("There", a.getIgnoreCase("hEllo"));
        Map<String, String> dataset = a.dataset();
        Assert.assertEquals(1, dataset.size());
        Assert.assertEquals("Jsoup", dataset.get("name"));
        Assert.assertEquals("", a.get("tot"));
        Assert.assertEquals("a&p", a.get("Tot"));
        Assert.assertEquals("a&p", a.getIgnoreCase("tot"));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", a.html());
        Assert.assertEquals(a.html(), a.toString());
    }

    @Test(timeout = 10000)
    public void htmllitString9() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString9__3 = a.put("Tot", "Tot");
        Assert.assertEquals(" Tot=\"Tot\"", ((Attributes) (o_htmllitString9__3)).toString());
        Assert.assertEquals(-1228736319, ((int) (((Attributes) (o_htmllitString9__3)).hashCode())));
        Attributes o_htmllitString9__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\"", ((Attributes) (o_htmllitString9__4)).toString());
        Assert.assertEquals(-1641757740, ((int) (((Attributes) (o_htmllitString9__4)).hashCode())));
        Attributes o_htmllitString9__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString9__5)).toString());
        Assert.assertEquals(1965660610, ((int) (((Attributes) (o_htmllitString9__5)).hashCode())));
        int o_htmllitString9__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString9__6)));
        boolean o_htmllitString9__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString9__7);
        boolean o_htmllitString9__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString9__8);
        boolean o_htmllitString9__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString9__9);
        boolean o_htmllitString9__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString9__10);
        boolean o_htmllitString9__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString9__11);
        String o_htmllitString9__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString9__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString9__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString9__15)));
        String o_htmllitString9__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString9__16);
        String o_htmllitString9__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString9__17);
        String o_htmllitString9__18 = a.get("Tot");
        Assert.assertEquals("Tot", o_htmllitString9__18);
        String o_htmllitString9__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("Tot", o_htmllitString9__19);
        String o_htmllitString9__20 = a.html();
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString9__20);
        String o_htmllitString9__21 = a.html();
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString9__21);
        a.toString();
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1965660610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString9__3)).toString());
        Assert.assertEquals(1965660610, ((int) (((Attributes) (o_htmllitString9__3)).hashCode())));
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString9__4)).toString());
        Assert.assertEquals(1965660610, ((int) (((Attributes) (o_htmllitString9__4)).hashCode())));
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString9__5)).toString());
        Assert.assertEquals(1965660610, ((int) (((Attributes) (o_htmllitString9__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString9__6)));
        Assert.assertTrue(o_htmllitString9__7);
        Assert.assertTrue(o_htmllitString9__8);
        Assert.assertTrue(o_htmllitString9__9);
        Assert.assertFalse(o_htmllitString9__10);
        Assert.assertTrue(o_htmllitString9__11);
        Assert.assertEquals("There", o_htmllitString9__12);
        Assert.assertEquals(1, ((int) (o_htmllitString9__15)));
        Assert.assertEquals("Jsoup", o_htmllitString9__16);
        Assert.assertEquals("", o_htmllitString9__17);
        Assert.assertEquals("Tot", o_htmllitString9__18);
        Assert.assertEquals("Tot", o_htmllitString9__19);
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString9__20);
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString9__21);
    }

    @Test(timeout = 10000)
    public void html_mg152() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_mg152__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_mg152__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_mg152__3)).hashCode())));
        Attributes o_html_mg152__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_mg152__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_mg152__4)).hashCode())));
        Attributes o_html_mg152__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg152__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg152__5)).hashCode())));
        int o_html_mg152__6 = a.size();
        Assert.assertEquals(3, ((int) (o_html_mg152__6)));
        boolean o_html_mg152__7 = a.hasKey("Tot");
        Assert.assertTrue(o_html_mg152__7);
        boolean o_html_mg152__8 = a.hasKey("Hello");
        Assert.assertTrue(o_html_mg152__8);
        boolean o_html_mg152__9 = a.hasKey("data-name");
        Assert.assertTrue(o_html_mg152__9);
        boolean o_html_mg152__10 = a.hasKey("tot");
        Assert.assertFalse(o_html_mg152__10);
        boolean o_html_mg152__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_mg152__11);
        String o_html_mg152__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_mg152__12);
        Map<String, String> dataset = a.dataset();
        int o_html_mg152__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_mg152__15)));
        String o_html_mg152__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_mg152__16);
        String o_html_mg152__17 = a.get("tot");
        Assert.assertEquals("", o_html_mg152__17);
        String o_html_mg152__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_mg152__18);
        String o_html_mg152__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_mg152__19);
        String o_html_mg152__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg152__20);
        String o_html_mg152__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg152__21);
        a.toString();
        List<Attribute> o_html_mg152__23 = a.asList();
        Assert.assertFalse(o_html_mg152__23.isEmpty());
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg152__3)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg152__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg152__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg152__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg152__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg152__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_mg152__6)));
        Assert.assertTrue(o_html_mg152__7);
        Assert.assertTrue(o_html_mg152__8);
        Assert.assertTrue(o_html_mg152__9);
        Assert.assertFalse(o_html_mg152__10);
        Assert.assertTrue(o_html_mg152__11);
        Assert.assertEquals("There", o_html_mg152__12);
        Assert.assertEquals(1, ((int) (o_html_mg152__15)));
        Assert.assertEquals("Jsoup", o_html_mg152__16);
        Assert.assertEquals("", o_html_mg152__17);
        Assert.assertEquals("a&p", o_html_mg152__18);
        Assert.assertEquals("a&p", o_html_mg152__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg152__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg152__21);
    }

    @Test(timeout = 10000)
    public void html_rv185() throws Exception {
        boolean __DSPOT_value_20 = true;
        String __DSPOT_key_19 = "zxk?Yw`yc.L`HJ*J8r}4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_html_rv185__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv185__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv185__8)).hashCode())));
        Attributes o_html_rv185__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv185__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv185__9)).hashCode())));
        int o_html_rv185__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv185__10)));
        boolean o_html_rv185__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv185__11);
        boolean o_html_rv185__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv185__12);
        boolean o_html_rv185__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv185__13);
        boolean o_html_rv185__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv185__14);
        boolean o_html_rv185__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv185__15);
        String o_html_rv185__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv185__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv185__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv185__19)));
        String o_html_rv185__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv185__20);
        String o_html_rv185__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv185__21);
        String o_html_rv185__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv185__22);
        String o_html_rv185__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv185__23);
        String o_html_rv185__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv185__24);
        String o_html_rv185__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv185__25);
        a.toString();
        Attributes o_html_rv185__27 = __DSPOT_invoc_3.put(__DSPOT_key_19, __DSPOT_value_20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" zxk?Yw`yc.L`HJ*J8r}4", ((Attributes) (o_html_rv185__27)).toString());
        Assert.assertEquals(601726070, ((int) (((Attributes) (o_html_rv185__27)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" zxk?Yw`yc.L`HJ*J8r}4", ((Attributes) (a)).toString());
        Assert.assertEquals(601726070, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" zxk?Yw`yc.L`HJ*J8r}4", ((Attributes) (o_html_rv185__8)).toString());
        Assert.assertEquals(601726070, ((int) (((Attributes) (o_html_rv185__8)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" zxk?Yw`yc.L`HJ*J8r}4", ((Attributes) (o_html_rv185__9)).toString());
        Assert.assertEquals(601726070, ((int) (((Attributes) (o_html_rv185__9)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv185__10)));
        Assert.assertTrue(o_html_rv185__11);
        Assert.assertTrue(o_html_rv185__12);
        Assert.assertTrue(o_html_rv185__13);
        Assert.assertFalse(o_html_rv185__14);
        Assert.assertTrue(o_html_rv185__15);
        Assert.assertEquals("There", o_html_rv185__16);
        Assert.assertEquals(1, ((int) (o_html_rv185__19)));
        Assert.assertEquals("Jsoup", o_html_rv185__20);
        Assert.assertEquals("", o_html_rv185__21);
        Assert.assertEquals("a&p", o_html_rv185__22);
        Assert.assertEquals("a&p", o_html_rv185__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv185__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv185__25);
    }

    @Test(timeout = 10000)
    public void htmllitString22_failAssert1() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase("tot");
            a.getIgnoreCase("hEllo");
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString22 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString90() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString90__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString90__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString90__3)).hashCode())));
        Attributes o_htmllitString90__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString90__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString90__4)).hashCode())));
        Attributes o_htmllitString90__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString90__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString90__5)).hashCode())));
        int o_htmllitString90__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString90__6)));
        boolean o_htmllitString90__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString90__7);
        boolean o_htmllitString90__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString90__8);
        boolean o_htmllitString90__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString90__9);
        boolean o_htmllitString90__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString90__10);
        boolean o_htmllitString90__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString90__11);
        String o_htmllitString90__12 = a.getIgnoreCase("6Ello");
        Assert.assertEquals("", o_htmllitString90__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString90__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString90__15)));
        String o_htmllitString90__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString90__16);
        String o_htmllitString90__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString90__17);
        String o_htmllitString90__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_htmllitString90__18);
        String o_htmllitString90__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_htmllitString90__19);
        String o_htmllitString90__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString90__20);
        String o_htmllitString90__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString90__21);
        a.toString();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString90__3)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString90__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString90__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString90__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString90__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString90__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString90__6)));
        Assert.assertTrue(o_htmllitString90__7);
        Assert.assertTrue(o_htmllitString90__8);
        Assert.assertTrue(o_htmllitString90__9);
        Assert.assertFalse(o_htmllitString90__10);
        Assert.assertTrue(o_htmllitString90__11);
        Assert.assertEquals("", o_htmllitString90__12);
        Assert.assertEquals(1, ((int) (o_htmllitString90__15)));
        Assert.assertEquals("Jsoup", o_htmllitString90__16);
        Assert.assertEquals("", o_htmllitString90__17);
        Assert.assertEquals("a&p", o_htmllitString90__18);
        Assert.assertEquals("a&p", o_htmllitString90__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString90__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString90__21);
    }

    @Test(timeout = 10000)
    public void html_mg153() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_mg153__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_mg153__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_mg153__3)).hashCode())));
        Attributes o_html_mg153__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_mg153__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_mg153__4)).hashCode())));
        Attributes o_html_mg153__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg153__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg153__5)).hashCode())));
        int o_html_mg153__6 = a.size();
        Assert.assertEquals(3, ((int) (o_html_mg153__6)));
        boolean o_html_mg153__7 = a.hasKey("Tot");
        Assert.assertTrue(o_html_mg153__7);
        boolean o_html_mg153__8 = a.hasKey("Hello");
        Assert.assertTrue(o_html_mg153__8);
        boolean o_html_mg153__9 = a.hasKey("data-name");
        Assert.assertTrue(o_html_mg153__9);
        boolean o_html_mg153__10 = a.hasKey("tot");
        Assert.assertFalse(o_html_mg153__10);
        boolean o_html_mg153__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_mg153__11);
        String o_html_mg153__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_mg153__12);
        Map<String, String> dataset = a.dataset();
        int o_html_mg153__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_mg153__15)));
        String o_html_mg153__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_mg153__16);
        String o_html_mg153__17 = a.get("tot");
        Assert.assertEquals("", o_html_mg153__17);
        String o_html_mg153__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_mg153__18);
        String o_html_mg153__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_mg153__19);
        String o_html_mg153__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg153__20);
        String o_html_mg153__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg153__21);
        a.toString();
        Attributes o_html_mg153__23 = a.clone();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg153__23)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg153__23)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-758045610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg153__3)).toString());
        Assert.assertEquals(-758045610, ((int) (((Attributes) (o_html_mg153__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg153__4)).toString());
        Assert.assertEquals(-758045610, ((int) (((Attributes) (o_html_mg153__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg153__5)).toString());
        Assert.assertEquals(-758045610, ((int) (((Attributes) (o_html_mg153__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_mg153__6)));
        Assert.assertTrue(o_html_mg153__7);
        Assert.assertTrue(o_html_mg153__8);
        Assert.assertTrue(o_html_mg153__9);
        Assert.assertFalse(o_html_mg153__10);
        Assert.assertTrue(o_html_mg153__11);
        Assert.assertEquals("There", o_html_mg153__12);
        Assert.assertEquals(1, ((int) (o_html_mg153__15)));
        Assert.assertEquals("Jsoup", o_html_mg153__16);
        Assert.assertEquals("", o_html_mg153__17);
        Assert.assertEquals("a&p", o_html_mg153__18);
        Assert.assertEquals("a&p", o_html_mg153__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg153__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg153__21);
    }

    @Test(timeout = 10000)
    public void html_mg164() throws Exception {
        boolean __DSPOT_value_7 = false;
        String __DSPOT_key_6 = "gchsEp#5_w)+KtmI6N*:";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_mg164__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_mg164__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_mg164__5)).hashCode())));
        Attributes o_html_mg164__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_mg164__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_mg164__6)).hashCode())));
        Attributes o_html_mg164__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg164__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg164__7)).hashCode())));
        int o_html_mg164__8 = a.size();
        Assert.assertEquals(3, ((int) (o_html_mg164__8)));
        boolean o_html_mg164__9 = a.hasKey("Tot");
        Assert.assertTrue(o_html_mg164__9);
        boolean o_html_mg164__10 = a.hasKey("Hello");
        Assert.assertTrue(o_html_mg164__10);
        boolean o_html_mg164__11 = a.hasKey("data-name");
        Assert.assertTrue(o_html_mg164__11);
        boolean o_html_mg164__12 = a.hasKey("tot");
        Assert.assertFalse(o_html_mg164__12);
        boolean o_html_mg164__13 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_mg164__13);
        String o_html_mg164__14 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_mg164__14);
        Map<String, String> dataset = a.dataset();
        int o_html_mg164__17 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_mg164__17)));
        String o_html_mg164__18 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_mg164__18);
        String o_html_mg164__19 = a.get("tot");
        Assert.assertEquals("", o_html_mg164__19);
        String o_html_mg164__20 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_mg164__20);
        String o_html_mg164__21 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_mg164__21);
        String o_html_mg164__22 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg164__22);
        String o_html_mg164__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg164__23);
        a.toString();
        Attributes o_html_mg164__25 = a.put(__DSPOT_key_6, __DSPOT_value_7);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg164__25)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg164__25)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg164__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg164__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg164__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg164__6)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_mg164__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_mg164__7)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_mg164__8)));
        Assert.assertTrue(o_html_mg164__9);
        Assert.assertTrue(o_html_mg164__10);
        Assert.assertTrue(o_html_mg164__11);
        Assert.assertFalse(o_html_mg164__12);
        Assert.assertTrue(o_html_mg164__13);
        Assert.assertEquals("There", o_html_mg164__14);
        Assert.assertEquals(1, ((int) (o_html_mg164__17)));
        Assert.assertEquals("Jsoup", o_html_mg164__18);
        Assert.assertEquals("", o_html_mg164__19);
        Assert.assertEquals("a&p", o_html_mg164__20);
        Assert.assertEquals("a&p", o_html_mg164__21);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg164__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_mg164__23);
    }

    @Test(timeout = 10000)
    public void htmllitString106() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString106__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString106__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString106__3)).hashCode())));
        Attributes o_htmllitString106__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString106__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString106__4)).hashCode())));
        Attributes o_htmllitString106__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString106__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString106__5)).hashCode())));
        int o_htmllitString106__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString106__6)));
        boolean o_htmllitString106__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString106__7);
        boolean o_htmllitString106__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString106__8);
        boolean o_htmllitString106__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString106__9);
        boolean o_htmllitString106__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString106__10);
        boolean o_htmllitString106__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString106__11);
        String o_htmllitString106__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString106__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString106__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString106__15)));
        String o_htmllitString106__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString106__16);
        String o_htmllitString106__17 = a.get("wot");
        Assert.assertEquals("", o_htmllitString106__17);
        String o_htmllitString106__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_htmllitString106__18);
        String o_htmllitString106__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_htmllitString106__19);
        String o_htmllitString106__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString106__20);
        String o_htmllitString106__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString106__21);
        a.toString();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString106__3)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString106__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString106__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString106__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString106__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString106__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString106__6)));
        Assert.assertTrue(o_htmllitString106__7);
        Assert.assertTrue(o_htmllitString106__8);
        Assert.assertTrue(o_htmllitString106__9);
        Assert.assertFalse(o_htmllitString106__10);
        Assert.assertTrue(o_htmllitString106__11);
        Assert.assertEquals("There", o_htmllitString106__12);
        Assert.assertEquals(1, ((int) (o_htmllitString106__15)));
        Assert.assertEquals("Jsoup", o_htmllitString106__16);
        Assert.assertEquals("", o_htmllitString106__17);
        Assert.assertEquals("a&p", o_htmllitString106__18);
        Assert.assertEquals("a&p", o_htmllitString106__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString106__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString106__21);
    }

    @Test(timeout = 10000)
    public void html_rv210() throws Exception {
        String __DSPOT_key_38 = "QDhB+ _ 2&pb?56TtKz.";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv210__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv210__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv210__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_html_rv210__8 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv210__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv210__8)).hashCode())));
        int o_html_rv210__9 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv210__9)));
        boolean o_html_rv210__10 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv210__10);
        boolean o_html_rv210__11 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv210__11);
        boolean o_html_rv210__12 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv210__12);
        boolean o_html_rv210__13 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv210__13);
        boolean o_html_rv210__14 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv210__14);
        String o_html_rv210__15 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv210__15);
        Map<String, String> dataset = a.dataset();
        int o_html_rv210__18 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv210__18)));
        String o_html_rv210__19 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv210__19);
        String o_html_rv210__20 = a.get("tot");
        Assert.assertEquals("", o_html_rv210__20);
        String o_html_rv210__21 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv210__21);
        String o_html_rv210__22 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv210__22);
        String o_html_rv210__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv210__23);
        String o_html_rv210__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv210__24);
        a.toString();
        __DSPOT_invoc_4.removeIgnoreCase(__DSPOT_key_38);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv210__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv210__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv210__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv210__8)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv210__9)));
        Assert.assertTrue(o_html_rv210__10);
        Assert.assertTrue(o_html_rv210__11);
        Assert.assertTrue(o_html_rv210__12);
        Assert.assertFalse(o_html_rv210__13);
        Assert.assertTrue(o_html_rv210__14);
        Assert.assertEquals("There", o_html_rv210__15);
        Assert.assertEquals(1, ((int) (o_html_rv210__18)));
        Assert.assertEquals("Jsoup", o_html_rv210__19);
        Assert.assertEquals("", o_html_rv210__20);
        Assert.assertEquals("a&p", o_html_rv210__21);
        Assert.assertEquals("a&p", o_html_rv210__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv210__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv210__24);
    }

    @Test(timeout = 10000)
    public void html_rv222() throws Exception {
        String __DSPOT_key_44 = "Iw9b>c1NAnIwcz&T&5+;";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv222__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv222__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv222__4)).hashCode())));
        Attributes o_html_rv222__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv222__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv222__5)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv222__9 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv222__9)));
        boolean o_html_rv222__10 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv222__10);
        boolean o_html_rv222__11 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv222__11);
        boolean o_html_rv222__12 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv222__12);
        boolean o_html_rv222__13 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv222__13);
        boolean o_html_rv222__14 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv222__14);
        String o_html_rv222__15 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv222__15);
        Map<String, String> dataset = a.dataset();
        int o_html_rv222__18 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv222__18)));
        String o_html_rv222__19 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv222__19);
        String o_html_rv222__20 = a.get("tot");
        Assert.assertEquals("", o_html_rv222__20);
        String o_html_rv222__21 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv222__21);
        String o_html_rv222__22 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv222__22);
        String o_html_rv222__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv222__23);
        String o_html_rv222__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv222__24);
        a.toString();
        boolean o_html_rv222__26 = __DSPOT_invoc_5.hasKeyIgnoreCase(__DSPOT_key_44);
        Assert.assertFalse(o_html_rv222__26);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv222__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv222__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv222__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv222__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv222__9)));
        Assert.assertTrue(o_html_rv222__10);
        Assert.assertTrue(o_html_rv222__11);
        Assert.assertTrue(o_html_rv222__12);
        Assert.assertFalse(o_html_rv222__13);
        Assert.assertTrue(o_html_rv222__14);
        Assert.assertEquals("There", o_html_rv222__15);
        Assert.assertEquals(1, ((int) (o_html_rv222__18)));
        Assert.assertEquals("Jsoup", o_html_rv222__19);
        Assert.assertEquals("", o_html_rv222__20);
        Assert.assertEquals("a&p", o_html_rv222__21);
        Assert.assertEquals("a&p", o_html_rv222__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv222__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv222__24);
    }

    @Test(timeout = 10000)
    public void html_rv186() throws Exception {
        String __DSPOT_value_22 = "u)p]QM-k,I]-r8//GGUV";
        String __DSPOT_key_21 = "(!YL#ZQsb>_1JVt2Y][1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_html_rv186__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv186__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv186__8)).hashCode())));
        Attributes o_html_rv186__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv186__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv186__9)).hashCode())));
        int o_html_rv186__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv186__10)));
        boolean o_html_rv186__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv186__11);
        boolean o_html_rv186__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv186__12);
        boolean o_html_rv186__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv186__13);
        boolean o_html_rv186__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv186__14);
        boolean o_html_rv186__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv186__15);
        String o_html_rv186__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv186__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv186__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv186__19)));
        String o_html_rv186__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv186__20);
        String o_html_rv186__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv186__21);
        String o_html_rv186__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv186__22);
        String o_html_rv186__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv186__23);
        String o_html_rv186__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv186__24);
        String o_html_rv186__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv186__25);
        a.toString();
        Attributes o_html_rv186__27 = __DSPOT_invoc_3.put(__DSPOT_key_21, __DSPOT_value_22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" (!YL#ZQsb>_1JVt2Y][1=\"u)p]QM-k,I]-r8//GGUV\"", ((Attributes) (o_html_rv186__27)).toString());
        Assert.assertEquals(1445089545, ((int) (((Attributes) (o_html_rv186__27)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" (!YL#ZQsb>_1JVt2Y][1=\"u)p]QM-k,I]-r8//GGUV\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1445089545, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" (!YL#ZQsb>_1JVt2Y][1=\"u)p]QM-k,I]-r8//GGUV\"", ((Attributes) (o_html_rv186__8)).toString());
        Assert.assertEquals(1445089545, ((int) (((Attributes) (o_html_rv186__8)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" (!YL#ZQsb>_1JVt2Y][1=\"u)p]QM-k,I]-r8//GGUV\"", ((Attributes) (o_html_rv186__9)).toString());
        Assert.assertEquals(1445089545, ((int) (((Attributes) (o_html_rv186__9)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv186__10)));
        Assert.assertTrue(o_html_rv186__11);
        Assert.assertTrue(o_html_rv186__12);
        Assert.assertTrue(o_html_rv186__13);
        Assert.assertFalse(o_html_rv186__14);
        Assert.assertTrue(o_html_rv186__15);
        Assert.assertEquals("There", o_html_rv186__16);
        Assert.assertEquals(1, ((int) (o_html_rv186__19)));
        Assert.assertEquals("Jsoup", o_html_rv186__20);
        Assert.assertEquals("", o_html_rv186__21);
        Assert.assertEquals("a&p", o_html_rv186__22);
        Assert.assertEquals("a&p", o_html_rv186__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv186__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv186__25);
    }

    @Test(timeout = 10000)
    public void html_rv197() throws Exception {
        Attributes __DSPOT_o_27 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv197__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv197__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv197__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_html_rv197__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv197__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv197__9)).hashCode())));
        int o_html_rv197__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv197__10)));
        boolean o_html_rv197__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv197__11);
        boolean o_html_rv197__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv197__12);
        boolean o_html_rv197__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv197__13);
        boolean o_html_rv197__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv197__14);
        boolean o_html_rv197__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv197__15);
        String o_html_rv197__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv197__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv197__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv197__19)));
        String o_html_rv197__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv197__20);
        String o_html_rv197__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv197__21);
        String o_html_rv197__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv197__22);
        String o_html_rv197__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv197__23);
        String o_html_rv197__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv197__24);
        String o_html_rv197__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv197__25);
        a.toString();
        boolean o_html_rv197__27 = __DSPOT_invoc_4.equals(__DSPOT_o_27);
        Assert.assertFalse(o_html_rv197__27);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv197__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv197__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv197__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv197__9)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv197__10)));
        Assert.assertTrue(o_html_rv197__11);
        Assert.assertTrue(o_html_rv197__12);
        Assert.assertTrue(o_html_rv197__13);
        Assert.assertFalse(o_html_rv197__14);
        Assert.assertTrue(o_html_rv197__15);
        Assert.assertEquals("There", o_html_rv197__16);
        Assert.assertEquals(1, ((int) (o_html_rv197__19)));
        Assert.assertEquals("Jsoup", o_html_rv197__20);
        Assert.assertEquals("", o_html_rv197__21);
        Assert.assertEquals("a&p", o_html_rv197__22);
        Assert.assertEquals("a&p", o_html_rv197__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv197__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv197__25);
    }

    @Test(timeout = 10000)
    public void html_rv176() throws Exception {
        Object __DSPOT_o_14 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_html_rv176__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv176__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv176__8)).hashCode())));
        Attributes o_html_rv176__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv176__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv176__9)).hashCode())));
        int o_html_rv176__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv176__10)));
        boolean o_html_rv176__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv176__11);
        boolean o_html_rv176__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv176__12);
        boolean o_html_rv176__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv176__13);
        boolean o_html_rv176__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv176__14);
        boolean o_html_rv176__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv176__15);
        String o_html_rv176__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv176__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv176__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv176__19)));
        String o_html_rv176__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv176__20);
        String o_html_rv176__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv176__21);
        String o_html_rv176__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv176__22);
        String o_html_rv176__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv176__23);
        String o_html_rv176__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv176__24);
        String o_html_rv176__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv176__25);
        a.toString();
        boolean o_html_rv176__27 = __DSPOT_invoc_3.equals(__DSPOT_o_14);
        Assert.assertFalse(o_html_rv176__27);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv176__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv176__8)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv176__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv176__9)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv176__10)));
        Assert.assertTrue(o_html_rv176__11);
        Assert.assertTrue(o_html_rv176__12);
        Assert.assertTrue(o_html_rv176__13);
        Assert.assertFalse(o_html_rv176__14);
        Assert.assertTrue(o_html_rv176__15);
        Assert.assertEquals("There", o_html_rv176__16);
        Assert.assertEquals(1, ((int) (o_html_rv176__19)));
        Assert.assertEquals("Jsoup", o_html_rv176__20);
        Assert.assertEquals("", o_html_rv176__21);
        Assert.assertEquals("a&p", o_html_rv176__22);
        Assert.assertEquals("a&p", o_html_rv176__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv176__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv176__25);
    }

    @Test(timeout = 10000)
    public void htmllitString45() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString45__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString45__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString45__3)).hashCode())));
        Attributes o_htmllitString45__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString45__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString45__4)).hashCode())));
        Attributes o_htmllitString45__5 = a.put("data-name", ">UgIv");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", ((Attributes) (o_htmllitString45__5)).toString());
        Assert.assertEquals(1898767508, ((int) (((Attributes) (o_htmllitString45__5)).hashCode())));
        int o_htmllitString45__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString45__6)));
        boolean o_htmllitString45__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString45__7);
        boolean o_htmllitString45__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString45__8);
        boolean o_htmllitString45__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString45__9);
        boolean o_htmllitString45__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString45__10);
        boolean o_htmllitString45__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString45__11);
        String o_htmllitString45__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString45__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString45__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString45__15)));
        String o_htmllitString45__16 = dataset.get("name");
        Assert.assertEquals(">UgIv", o_htmllitString45__16);
        String o_htmllitString45__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString45__17);
        String o_htmllitString45__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_htmllitString45__18);
        String o_htmllitString45__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_htmllitString45__19);
        String o_htmllitString45__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", o_htmllitString45__20);
        String o_htmllitString45__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", o_htmllitString45__21);
        a.toString();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1898767508, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", ((Attributes) (o_htmllitString45__3)).toString());
        Assert.assertEquals(1898767508, ((int) (((Attributes) (o_htmllitString45__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", ((Attributes) (o_htmllitString45__4)).toString());
        Assert.assertEquals(1898767508, ((int) (((Attributes) (o_htmllitString45__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", ((Attributes) (o_htmllitString45__5)).toString());
        Assert.assertEquals(1898767508, ((int) (((Attributes) (o_htmllitString45__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString45__6)));
        Assert.assertTrue(o_htmllitString45__7);
        Assert.assertTrue(o_htmllitString45__8);
        Assert.assertTrue(o_htmllitString45__9);
        Assert.assertFalse(o_htmllitString45__10);
        Assert.assertTrue(o_htmllitString45__11);
        Assert.assertEquals("There", o_htmllitString45__12);
        Assert.assertEquals(1, ((int) (o_htmllitString45__15)));
        Assert.assertEquals(">UgIv", o_htmllitString45__16);
        Assert.assertEquals("", o_htmllitString45__17);
        Assert.assertEquals("a&p", o_htmllitString45__18);
        Assert.assertEquals("a&p", o_htmllitString45__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", o_htmllitString45__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\">UgIv\"", o_htmllitString45__21);
    }

    @Test(timeout = 10000)
    public void html_rv226() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv226__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv226__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv226__3)).hashCode())));
        Attributes o_html_rv226__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv226__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv226__4)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv226__8 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv226__8)));
        boolean o_html_rv226__9 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv226__9);
        boolean o_html_rv226__10 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv226__10);
        boolean o_html_rv226__11 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv226__11);
        boolean o_html_rv226__12 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv226__12);
        boolean o_html_rv226__13 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv226__13);
        String o_html_rv226__14 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv226__14);
        Map<String, String> dataset = a.dataset();
        int o_html_rv226__17 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv226__17)));
        String o_html_rv226__18 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv226__18);
        String o_html_rv226__19 = a.get("tot");
        Assert.assertEquals("", o_html_rv226__19);
        String o_html_rv226__20 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv226__20);
        String o_html_rv226__21 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv226__21);
        String o_html_rv226__22 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv226__22);
        String o_html_rv226__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv226__23);
        a.toString();
        __DSPOT_invoc_5.normalize();
        Assert.assertEquals(" tot=\"a&amp;p\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(541005968, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"a&amp;p\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv226__3)).toString());
        Assert.assertEquals(541005968, ((int) (((Attributes) (o_html_rv226__3)).hashCode())));
        Assert.assertEquals(" tot=\"a&amp;p\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv226__4)).toString());
        Assert.assertEquals(541005968, ((int) (((Attributes) (o_html_rv226__4)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv226__8)));
        Assert.assertTrue(o_html_rv226__9);
        Assert.assertTrue(o_html_rv226__10);
        Assert.assertTrue(o_html_rv226__11);
        Assert.assertFalse(o_html_rv226__12);
        Assert.assertTrue(o_html_rv226__13);
        Assert.assertEquals("There", o_html_rv226__14);
        Assert.assertEquals(1, ((int) (o_html_rv226__17)));
        Assert.assertEquals("Jsoup", o_html_rv226__18);
        Assert.assertEquals("", o_html_rv226__19);
        Assert.assertEquals("a&p", o_html_rv226__20);
        Assert.assertEquals("a&p", o_html_rv226__21);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv226__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv226__23);
    }

    @Test(timeout = 10000)
    public void html_rv229() throws Exception {
        Attribute __DSPOT_attribute_49 = new Attribute("tS}%g/mS6TE0=.J($16q", "vQ}E3:oK*M=;$a4UUWY=");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv229__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv229__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv229__5)).hashCode())));
        Attributes o_html_rv229__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv229__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv229__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv229__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv229__10)));
        boolean o_html_rv229__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv229__11);
        boolean o_html_rv229__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv229__12);
        boolean o_html_rv229__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv229__13);
        boolean o_html_rv229__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv229__14);
        boolean o_html_rv229__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv229__15);
        String o_html_rv229__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv229__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv229__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv229__19)));
        String o_html_rv229__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv229__20);
        String o_html_rv229__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv229__21);
        String o_html_rv229__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv229__22);
        String o_html_rv229__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv229__23);
        String o_html_rv229__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv229__24);
        String o_html_rv229__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv229__25);
        a.toString();
        Attributes o_html_rv229__27 = __DSPOT_invoc_5.put(__DSPOT_attribute_49);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" tS}%g/mS6TE0=.J($16q=\"vQ}E3:oK*M=;$a4UUWY=\"", ((Attributes) (o_html_rv229__27)).toString());
        Assert.assertEquals(-111646617, ((int) (((Attributes) (o_html_rv229__27)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" tS}%g/mS6TE0=.J($16q=\"vQ}E3:oK*M=;$a4UUWY=\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-111646617, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" tS}%g/mS6TE0=.J($16q=\"vQ}E3:oK*M=;$a4UUWY=\"", ((Attributes) (o_html_rv229__5)).toString());
        Assert.assertEquals(-111646617, ((int) (((Attributes) (o_html_rv229__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" tS}%g/mS6TE0=.J($16q=\"vQ}E3:oK*M=;$a4UUWY=\"", ((Attributes) (o_html_rv229__6)).toString());
        Assert.assertEquals(-111646617, ((int) (((Attributes) (o_html_rv229__6)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv229__10)));
        Assert.assertTrue(o_html_rv229__11);
        Assert.assertTrue(o_html_rv229__12);
        Assert.assertTrue(o_html_rv229__13);
        Assert.assertFalse(o_html_rv229__14);
        Assert.assertTrue(o_html_rv229__15);
        Assert.assertEquals("There", o_html_rv229__16);
        Assert.assertEquals(1, ((int) (o_html_rv229__19)));
        Assert.assertEquals("Jsoup", o_html_rv229__20);
        Assert.assertEquals("", o_html_rv229__21);
        Assert.assertEquals("a&p", o_html_rv229__22);
        Assert.assertEquals("a&p", o_html_rv229__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv229__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv229__25);
    }

    @Test(timeout = 10000)
    public void htmllitString15() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString15__3 = a.put("Tot", "\n");
        Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_htmllitString15__3)).toString());
        Assert.assertEquals(555713616, ((int) (((Attributes) (o_htmllitString15__3)).hashCode())));
        Attributes o_htmllitString15__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\"", ((Attributes) (o_htmllitString15__4)).toString());
        Assert.assertEquals(142692195, ((int) (((Attributes) (o_htmllitString15__4)).hashCode())));
        Attributes o_htmllitString15__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString15__5)).toString());
        Assert.assertEquals(-544856751, ((int) (((Attributes) (o_htmllitString15__5)).hashCode())));
        int o_htmllitString15__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString15__6)));
        boolean o_htmllitString15__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString15__7);
        boolean o_htmllitString15__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString15__8);
        boolean o_htmllitString15__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString15__9);
        boolean o_htmllitString15__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString15__10);
        boolean o_htmllitString15__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString15__11);
        String o_htmllitString15__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString15__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString15__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString15__15)));
        String o_htmllitString15__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString15__16);
        String o_htmllitString15__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString15__17);
        String o_htmllitString15__18 = a.get("Tot");
        Assert.assertEquals("\n", o_htmllitString15__18);
        String o_htmllitString15__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("\n", o_htmllitString15__19);
        String o_htmllitString15__20 = a.html();
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString15__20);
        String o_htmllitString15__21 = a.html();
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString15__21);
        a.toString();
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-544856751, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString15__3)).toString());
        Assert.assertEquals(-544856751, ((int) (((Attributes) (o_htmllitString15__3)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString15__4)).toString());
        Assert.assertEquals(-544856751, ((int) (((Attributes) (o_htmllitString15__4)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString15__5)).toString());
        Assert.assertEquals(-544856751, ((int) (((Attributes) (o_htmllitString15__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString15__6)));
        Assert.assertTrue(o_htmllitString15__7);
        Assert.assertTrue(o_htmllitString15__8);
        Assert.assertTrue(o_htmllitString15__9);
        Assert.assertFalse(o_htmllitString15__10);
        Assert.assertTrue(o_htmllitString15__11);
        Assert.assertEquals("There", o_htmllitString15__12);
        Assert.assertEquals(1, ((int) (o_htmllitString15__15)));
        Assert.assertEquals("Jsoup", o_htmllitString15__16);
        Assert.assertEquals("", o_htmllitString15__17);
        Assert.assertEquals("\n", o_htmllitString15__18);
        Assert.assertEquals("\n", o_htmllitString15__19);
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString15__20);
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString15__21);
    }

    @Test(timeout = 10000)
    public void htmllitString36() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString36__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString36__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString36__3)).hashCode())));
        Attributes o_htmllitString36__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString36__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString36__4)).hashCode())));
        Attributes o_htmllitString36__5 = a.put("dat-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" dat-name=\"Jsoup\"", ((Attributes) (o_htmllitString36__5)).toString());
        Assert.assertEquals(-277173245, ((int) (((Attributes) (o_htmllitString36__5)).hashCode())));
        int o_htmllitString36__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString36__6)));
        boolean o_htmllitString36__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString36__7);
        boolean o_htmllitString36__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString36__8);
        boolean o_htmllitString36__9 = a.hasKey("data-name");
        Assert.assertFalse(o_htmllitString36__9);
        boolean o_htmllitString36__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString36__10);
        boolean o_htmllitString36__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString36__11);
        String o_htmllitString36__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString36__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString36__15 = dataset.size();
        Assert.assertEquals(0, ((int) (o_htmllitString36__15)));
        dataset.get("name");
        a.get("tot");
        a.get("Tot");
        a.getIgnoreCase("tot");
        a.html();
        a.html();
        a.toString();
    }

    @Test(timeout = 10000)
    public void html_rv228() throws Exception {
        String __DSPOT_value_48 = "d&W_#*uaYP&<w$N`nJW,";
        String __DSPOT_key_47 = "z,lI:lZE.`#n5*TWD1iX";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv228__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv228__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv228__5)).hashCode())));
        Attributes o_html_rv228__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv228__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv228__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv228__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv228__10)));
        boolean o_html_rv228__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv228__11);
        boolean o_html_rv228__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv228__12);
        boolean o_html_rv228__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv228__13);
        boolean o_html_rv228__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv228__14);
        boolean o_html_rv228__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv228__15);
        String o_html_rv228__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv228__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv228__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv228__19)));
        String o_html_rv228__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv228__20);
        String o_html_rv228__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv228__21);
        String o_html_rv228__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv228__22);
        String o_html_rv228__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv228__23);
        String o_html_rv228__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv228__24);
        String o_html_rv228__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv228__25);
        a.toString();
        Attributes o_html_rv228__27 = __DSPOT_invoc_5.put(__DSPOT_key_47, __DSPOT_value_48);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" z,lI:lZE.`#n5*TWD1iX=\"d&amp;W_#*uaYP&amp;<w$N`nJW,\"", ((Attributes) (o_html_rv228__27)).toString());
        Assert.assertEquals(1027810254, ((int) (((Attributes) (o_html_rv228__27)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" z,lI:lZE.`#n5*TWD1iX=\"d&amp;W_#*uaYP&amp;<w$N`nJW,\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1027810254, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" z,lI:lZE.`#n5*TWD1iX=\"d&amp;W_#*uaYP&amp;<w$N`nJW,\"", ((Attributes) (o_html_rv228__5)).toString());
        Assert.assertEquals(1027810254, ((int) (((Attributes) (o_html_rv228__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" z,lI:lZE.`#n5*TWD1iX=\"d&amp;W_#*uaYP&amp;<w$N`nJW,\"", ((Attributes) (o_html_rv228__6)).toString());
        Assert.assertEquals(1027810254, ((int) (((Attributes) (o_html_rv228__6)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv228__10)));
        Assert.assertTrue(o_html_rv228__11);
        Assert.assertTrue(o_html_rv228__12);
        Assert.assertTrue(o_html_rv228__13);
        Assert.assertFalse(o_html_rv228__14);
        Assert.assertTrue(o_html_rv228__15);
        Assert.assertEquals("There", o_html_rv228__16);
        Assert.assertEquals(1, ((int) (o_html_rv228__19)));
        Assert.assertEquals("Jsoup", o_html_rv228__20);
        Assert.assertEquals("", o_html_rv228__21);
        Assert.assertEquals("a&p", o_html_rv228__22);
        Assert.assertEquals("a&p", o_html_rv228__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv228__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv228__25);
    }

    @Test(timeout = 10000)
    public void html_rv209() throws Exception {
        String __DSPOT_key_37 = "5pNO|oPq,r5>K`HNw]f4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv209__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv209__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv209__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_html_rv209__8 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv209__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv209__8)).hashCode())));
        int o_html_rv209__9 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv209__9)));
        boolean o_html_rv209__10 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv209__10);
        boolean o_html_rv209__11 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv209__11);
        boolean o_html_rv209__12 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv209__12);
        boolean o_html_rv209__13 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv209__13);
        boolean o_html_rv209__14 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv209__14);
        String o_html_rv209__15 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv209__15);
        Map<String, String> dataset = a.dataset();
        int o_html_rv209__18 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv209__18)));
        String o_html_rv209__19 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv209__19);
        String o_html_rv209__20 = a.get("tot");
        Assert.assertEquals("", o_html_rv209__20);
        String o_html_rv209__21 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv209__21);
        String o_html_rv209__22 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv209__22);
        String o_html_rv209__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv209__23);
        String o_html_rv209__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv209__24);
        a.toString();
        __DSPOT_invoc_4.remove(__DSPOT_key_37);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv209__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv209__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv209__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv209__8)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv209__9)));
        Assert.assertTrue(o_html_rv209__10);
        Assert.assertTrue(o_html_rv209__11);
        Assert.assertTrue(o_html_rv209__12);
        Assert.assertFalse(o_html_rv209__13);
        Assert.assertTrue(o_html_rv209__14);
        Assert.assertEquals("There", o_html_rv209__15);
        Assert.assertEquals(1, ((int) (o_html_rv209__18)));
        Assert.assertEquals("Jsoup", o_html_rv209__19);
        Assert.assertEquals("", o_html_rv209__20);
        Assert.assertEquals("a&p", o_html_rv209__21);
        Assert.assertEquals("a&p", o_html_rv209__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv209__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv209__24);
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert2() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase("tot");
            a.getIgnoreCase("hEllo");
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString17() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString17__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString17__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString17__3)).hashCode())));
        Attributes o_htmllitString17__4 = a.put("Tot", "There");
        Assert.assertEquals(" Tot=\"There\"", ((Attributes) (o_htmllitString17__4)).toString());
        Assert.assertEquals(1733532630, ((int) (((Attributes) (o_htmllitString17__4)).hashCode())));
        Attributes o_htmllitString17__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString17__5)).toString());
        Assert.assertEquals(1894322954, ((int) (((Attributes) (o_htmllitString17__5)).hashCode())));
        int o_htmllitString17__6 = a.size();
        Assert.assertEquals(2, ((int) (o_htmllitString17__6)));
        boolean o_htmllitString17__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString17__7);
        boolean o_htmllitString17__8 = a.hasKey("Hello");
        Assert.assertFalse(o_htmllitString17__8);
        boolean o_htmllitString17__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString17__9);
        boolean o_htmllitString17__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString17__10);
        boolean o_htmllitString17__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString17__11);
        String o_htmllitString17__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("", o_htmllitString17__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString17__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString17__15)));
        String o_htmllitString17__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString17__16);
        String o_htmllitString17__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString17__17);
        String o_htmllitString17__18 = a.get("Tot");
        Assert.assertEquals("There", o_htmllitString17__18);
        String o_htmllitString17__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("There", o_htmllitString17__19);
        String o_htmllitString17__20 = a.html();
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", o_htmllitString17__20);
        String o_htmllitString17__21 = a.html();
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", o_htmllitString17__21);
        a.toString();
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1894322954, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString17__3)).toString());
        Assert.assertEquals(1894322954, ((int) (((Attributes) (o_htmllitString17__3)).hashCode())));
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString17__4)).toString());
        Assert.assertEquals(1894322954, ((int) (((Attributes) (o_htmllitString17__4)).hashCode())));
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString17__5)).toString());
        Assert.assertEquals(1894322954, ((int) (((Attributes) (o_htmllitString17__5)).hashCode())));
        Assert.assertEquals(2, ((int) (o_htmllitString17__6)));
        Assert.assertTrue(o_htmllitString17__7);
        Assert.assertFalse(o_htmllitString17__8);
        Assert.assertTrue(o_htmllitString17__9);
        Assert.assertFalse(o_htmllitString17__10);
        Assert.assertTrue(o_htmllitString17__11);
        Assert.assertEquals("", o_htmllitString17__12);
        Assert.assertEquals(1, ((int) (o_htmllitString17__15)));
        Assert.assertEquals("Jsoup", o_htmllitString17__16);
        Assert.assertEquals("", o_htmllitString17__17);
        Assert.assertEquals("There", o_htmllitString17__18);
        Assert.assertEquals("There", o_htmllitString17__19);
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", o_htmllitString17__20);
        Assert.assertEquals(" Tot=\"There\" data-name=\"Jsoup\"", o_htmllitString17__21);
    }

    @Test(timeout = 10000)
    public void htmllitString38_failAssert0litString24168() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString38_failAssert0litString24168__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString38_failAssert0litString24168__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString38_failAssert0litString24168__5)).hashCode())));
            Attributes o_htmllitString38_failAssert0litString24168__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString38_failAssert0litString24168__6)).toString());
            Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString38_failAssert0litString24168__6)).hashCode())));
            Attributes o_htmllitString38_failAssert0litString24168__7 = a.put("", "\n");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" =\"\n\"", ((Attributes) (o_htmllitString38_failAssert0litString24168__7)).toString());
            Assert.assertEquals(-1337113703, ((int) (((Attributes) (o_htmllitString38_failAssert0litString24168__7)).hashCode())));
            int o_htmllitString38_failAssert0litString24168__8 = a.size();
            Assert.assertEquals(3, ((int) (o_htmllitString38_failAssert0litString24168__8)));
            boolean o_htmllitString38_failAssert0litString24168__9 = a.hasKey("Tot");
            Assert.assertTrue(o_htmllitString38_failAssert0litString24168__9);
            boolean o_htmllitString38_failAssert0litString24168__10 = a.hasKey("Hello");
            Assert.assertTrue(o_htmllitString38_failAssert0litString24168__10);
            boolean o_htmllitString38_failAssert0litString24168__11 = a.hasKey("data-name");
            Assert.assertFalse(o_htmllitString38_failAssert0litString24168__11);
            boolean o_htmllitString38_failAssert0litString24168__12 = a.hasKey("tot");
            Assert.assertFalse(o_htmllitString38_failAssert0litString24168__12);
            boolean o_htmllitString38_failAssert0litString24168__13 = a.hasKeyIgnoreCase("tot");
            Assert.assertTrue(o_htmllitString38_failAssert0litString24168__13);
            String o_htmllitString38_failAssert0litString24168__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("There", o_htmllitString38_failAssert0litString24168__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString38 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString22_failAssert1litString18443() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString22_failAssert1litString18443__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString22_failAssert1litString18443__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString22_failAssert1litString18443__5)).hashCode())));
            Attributes o_htmllitString22_failAssert1litString18443__6 = a.put("", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" =\"There\"", ((Attributes) (o_htmllitString22_failAssert1litString18443__6)).toString());
            Assert.assertEquals(-608994156, ((int) (((Attributes) (o_htmllitString22_failAssert1litString18443__6)).hashCode())));
            Attributes o_htmllitString22_failAssert1litString18443__7 = a.put("data-name", "AZpV<");
            Assert.assertEquals(" Tot=\"a&amp;p\" =\"There\" data-name=\"AZpV<\"", ((Attributes) (o_htmllitString22_failAssert1litString18443__7)).toString());
            Assert.assertEquals(-1577295098, ((int) (((Attributes) (o_htmllitString22_failAssert1litString18443__7)).hashCode())));
            int o_htmllitString22_failAssert1litString18443__8 = a.size();
            Assert.assertEquals(3, ((int) (o_htmllitString22_failAssert1litString18443__8)));
            boolean o_htmllitString22_failAssert1litString18443__9 = a.hasKey("Tot");
            Assert.assertTrue(o_htmllitString22_failAssert1litString18443__9);
            boolean o_htmllitString22_failAssert1litString18443__10 = a.hasKey("Hello");
            Assert.assertFalse(o_htmllitString22_failAssert1litString18443__10);
            boolean o_htmllitString22_failAssert1litString18443__11 = a.hasKey("data-name");
            Assert.assertTrue(o_htmllitString22_failAssert1litString18443__11);
            boolean o_htmllitString22_failAssert1litString18443__12 = a.hasKey("tot");
            Assert.assertFalse(o_htmllitString22_failAssert1litString18443__12);
            boolean o_htmllitString22_failAssert1litString18443__13 = a.hasKeyIgnoreCase("tot");
            Assert.assertTrue(o_htmllitString22_failAssert1litString18443__13);
            String o_htmllitString22_failAssert1litString18443__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("", o_htmllitString22_failAssert1litString18443__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString22 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString38_failAssert0litString24159() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString38_failAssert0litString24159__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString38_failAssert0litString24159__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString38_failAssert0litString24159__5)).hashCode())));
            Attributes o_htmllitString38_failAssert0litString24159__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString38_failAssert0litString24159__6)).toString());
            Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString38_failAssert0litString24159__6)).hashCode())));
            Attributes o_htmllitString38_failAssert0litString24159__7 = a.put("", "");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" =\"\"", ((Attributes) (o_htmllitString38_failAssert0litString24159__7)).toString());
            Assert.assertEquals(-1337114013, ((int) (((Attributes) (o_htmllitString38_failAssert0litString24159__7)).hashCode())));
            int o_htmllitString38_failAssert0litString24159__8 = a.size();
            Assert.assertEquals(3, ((int) (o_htmllitString38_failAssert0litString24159__8)));
            boolean o_htmllitString38_failAssert0litString24159__9 = a.hasKey("Tot");
            Assert.assertTrue(o_htmllitString38_failAssert0litString24159__9);
            boolean o_htmllitString38_failAssert0litString24159__10 = a.hasKey("Hello");
            Assert.assertTrue(o_htmllitString38_failAssert0litString24159__10);
            boolean o_htmllitString38_failAssert0litString24159__11 = a.hasKey("data-name");
            Assert.assertFalse(o_htmllitString38_failAssert0litString24159__11);
            boolean o_htmllitString38_failAssert0litString24159__12 = a.hasKey("tot");
            Assert.assertFalse(o_htmllitString38_failAssert0litString24159__12);
            boolean o_htmllitString38_failAssert0litString24159__13 = a.hasKeyIgnoreCase("tot");
            Assert.assertTrue(o_htmllitString38_failAssert0litString24159__13);
            String o_htmllitString38_failAssert0litString24159__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("There", o_htmllitString38_failAssert0litString24159__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString38 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert2litString21201() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString6_failAssert2litString21201__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_htmllitString6_failAssert2litString21201__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_htmllitString6_failAssert2litString21201__5)).hashCode())));
            Attributes o_htmllitString6_failAssert2litString21201__6 = a.put("\n", "There");
            Assert.assertEquals(" =\"a&amp;p\" \n=\"There\"", ((Attributes) (o_htmllitString6_failAssert2litString21201__6)).toString());
            Assert.assertEquals(-1134558319, ((int) (((Attributes) (o_htmllitString6_failAssert2litString21201__6)).hashCode())));
            Attributes o_htmllitString6_failAssert2litString21201__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"a&amp;p\" \n=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString6_failAssert2litString21201__7)).toString());
            Assert.assertEquals(-1822107265, ((int) (((Attributes) (o_htmllitString6_failAssert2litString21201__7)).hashCode())));
            int o_htmllitString6_failAssert2litString21201__8 = a.size();
            Assert.assertEquals(3, ((int) (o_htmllitString6_failAssert2litString21201__8)));
            boolean o_htmllitString6_failAssert2litString21201__9 = a.hasKey("Tot");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21201__9);
            boolean o_htmllitString6_failAssert2litString21201__10 = a.hasKey("Hello");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21201__10);
            boolean o_htmllitString6_failAssert2litString21201__11 = a.hasKey("data-name");
            Assert.assertTrue(o_htmllitString6_failAssert2litString21201__11);
            boolean o_htmllitString6_failAssert2litString21201__12 = a.hasKey("tot");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21201__12);
            boolean o_htmllitString6_failAssert2litString21201__13 = a.hasKeyIgnoreCase("tot");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21201__13);
            String o_htmllitString6_failAssert2litString21201__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("", o_htmllitString6_failAssert2litString21201__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert2litString21196() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString6_failAssert2litString21196__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_htmllitString6_failAssert2litString21196__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_htmllitString6_failAssert2litString21196__5)).hashCode())));
            Attributes o_htmllitString6_failAssert2litString21196__6 = a.put("", "There");
            Assert.assertEquals(" =\"There\"", ((Attributes) (o_htmllitString6_failAssert2litString21196__6)).toString());
            Assert.assertEquals(1207670557, ((int) (((Attributes) (o_htmllitString6_failAssert2litString21196__6)).hashCode())));
            Attributes o_htmllitString6_failAssert2litString21196__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString6_failAssert2litString21196__7)).toString());
            Assert.assertEquals(1368460881, ((int) (((Attributes) (o_htmllitString6_failAssert2litString21196__7)).hashCode())));
            int o_htmllitString6_failAssert2litString21196__8 = a.size();
            Assert.assertEquals(2, ((int) (o_htmllitString6_failAssert2litString21196__8)));
            boolean o_htmllitString6_failAssert2litString21196__9 = a.hasKey("Tot");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21196__9);
            boolean o_htmllitString6_failAssert2litString21196__10 = a.hasKey("Hello");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21196__10);
            boolean o_htmllitString6_failAssert2litString21196__11 = a.hasKey("data-name");
            Assert.assertTrue(o_htmllitString6_failAssert2litString21196__11);
            boolean o_htmllitString6_failAssert2litString21196__12 = a.hasKey("tot");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21196__12);
            boolean o_htmllitString6_failAssert2litString21196__13 = a.hasKeyIgnoreCase("tot");
            Assert.assertFalse(o_htmllitString6_failAssert2litString21196__13);
            String o_htmllitString6_failAssert2litString21196__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("", o_htmllitString6_failAssert2litString21196__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert2_add22531litString56733() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22531__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_htmllitString6_failAssert2_add22531__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22531__5)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22531__6 = a.put("", "There");
            Assert.assertEquals(" =\"There\"", ((Attributes) (o_htmllitString6_failAssert2_add22531__6)).toString());
            Assert.assertEquals(1207670557, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22531__6)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22531__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString6_failAssert2_add22531__7)).toString());
            Assert.assertEquals(1368460881, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22531__7)).hashCode())));
            int o_htmllitString6_failAssert2_add22531__8 = a.size();
            boolean o_htmllitString6_failAssert2_add22531__9 = a.hasKey("Tot");
            boolean o_htmllitString6_failAssert2_add22531__10 = a.hasKey("Hello");
            boolean o_htmllitString6_failAssert2_add22531__11 = a.hasKey("data-name");
            boolean o_htmllitString6_failAssert2_add22531__12 = a.hasKey("tot");
            boolean o_htmllitString6_failAssert2_add22531__13 = a.hasKeyIgnoreCase("tot");
            String o_htmllitString6_failAssert2_add22531__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("", o_htmllitString6_failAssert2_add22531__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert2_add22462litString57735() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__5)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__6 = a.put("Hello", "");
            Assert.assertEquals(" =\"a&amp;p\" Hello=\"\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__6)).toString());
            Assert.assertEquals(2116891813, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__6)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__7)).toString());
            Assert.assertEquals(1429342867, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__7)).hashCode())));
            int o_htmllitString6_failAssert2_add22462__8 = a.size();
            boolean o_htmllitString6_failAssert2_add22462__9 = a.hasKey("Tot");
            boolean o_htmllitString6_failAssert2_add22462__10 = a.hasKey("Hello");
            boolean o_htmllitString6_failAssert2_add22462__11 = a.hasKey("data-name");
            boolean o_htmllitString6_failAssert2_add22462__12 = a.hasKey("tot");
            boolean o_htmllitString6_failAssert2_add22462__13 = a.hasKeyIgnoreCase("tot");
            String o_htmllitString6_failAssert2_add22462__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("", o_htmllitString6_failAssert2_add22462__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert2_add22462litString57626() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__5)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__6 = a.put(":", "There");
            Assert.assertEquals(" =\"a&amp;p\" :=\"There\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__6)).toString());
            Assert.assertEquals(-1133128351, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__6)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"a&amp;p\" :=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__7)).toString());
            Assert.assertEquals(-1820677297, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__7)).hashCode())));
            int o_htmllitString6_failAssert2_add22462__8 = a.size();
            boolean o_htmllitString6_failAssert2_add22462__9 = a.hasKey("Tot");
            boolean o_htmllitString6_failAssert2_add22462__10 = a.hasKey("Hello");
            boolean o_htmllitString6_failAssert2_add22462__11 = a.hasKey("data-name");
            boolean o_htmllitString6_failAssert2_add22462__12 = a.hasKey("tot");
            boolean o_htmllitString6_failAssert2_add22462__13 = a.hasKeyIgnoreCase("tot");
            String o_htmllitString6_failAssert2_add22462__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("", o_htmllitString6_failAssert2_add22462__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmllitString6_failAssert2_add22462litString57758() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__5)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__6 = a.put("Hello", "\n");
            Assert.assertEquals(" =\"a&amp;p\" Hello=\"\n\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__6)).toString());
            Assert.assertEquals(2116901423, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__6)).hashCode())));
            Attributes o_htmllitString6_failAssert2_add22462__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString6_failAssert2_add22462__7)).toString());
            Assert.assertEquals(1429352477, ((int) (((Attributes) (o_htmllitString6_failAssert2_add22462__7)).hashCode())));
            int o_htmllitString6_failAssert2_add22462__8 = a.size();
            boolean o_htmllitString6_failAssert2_add22462__9 = a.hasKey("Tot");
            boolean o_htmllitString6_failAssert2_add22462__10 = a.hasKey("Hello");
            boolean o_htmllitString6_failAssert2_add22462__11 = a.hasKey("data-name");
            boolean o_htmllitString6_failAssert2_add22462__12 = a.hasKey("tot");
            boolean o_htmllitString6_failAssert2_add22462__13 = a.hasKeyIgnoreCase("tot");
            String o_htmllitString6_failAssert2_add22462__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("\n", o_htmllitString6_failAssert2_add22462__14);
            Map<String, String> dataset = a.dataset();
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmllitString6 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testIteratorRemovable() {
        Attributes a = new Attributes();
        a.put("Tot", "a&p");
        a.put("Hello", "There");
        a.put("data-name", "Jsoup");
        Assert.assertTrue(a.hasKey("Tot"));
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot", attr.getKey());
        iterator.remove();
        Assert.assertEquals(2, a.size());
        attr = iterator.next();
        Assert.assertEquals("Hello", attr.getKey());
        Assert.assertEquals("There", attr.getValue());
        Assert.assertEquals(2, a.size());
        Assert.assertEquals("There", a.get("Hello"));
        Assert.assertFalse(a.hasKey("Tot"));
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309285() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString309285__3 = a.put("Tot", "tot");
        Assert.assertEquals(" Tot=\"tot\"", ((Attributes) (o_testIteratorRemovablelitString309285__3)).toString());
        Assert.assertEquals(-312603487, ((int) (((Attributes) (o_testIteratorRemovablelitString309285__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString309285__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"tot\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309285__4)).toString());
        Assert.assertEquals(-725624908, ((int) (((Attributes) (o_testIteratorRemovablelitString309285__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString309285__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"tot\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309285__5)).toString());
        Assert.assertEquals(-1413173854, ((int) (((Attributes) (o_testIteratorRemovablelitString309285__5)).hashCode())));
        boolean o_testIteratorRemovablelitString309285__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablelitString309285__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"tot\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2727744, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("tot", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString309285__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309285__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString309285__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309285__18)));
        String o_testIteratorRemovablelitString309285__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablelitString309285__19);
        boolean o_testIteratorRemovablelitString309285__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString309285__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309285__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309285__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309285__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309285__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309285__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309285__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablelitString309285__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309285__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309285__18)));
        Assert.assertEquals("There", o_testIteratorRemovablelitString309285__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309376() throws Exception {
        String __DSPOT_key_94243 = "xIbCxQ[jv(SM1;jfa5U:";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309376__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309376__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309376__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg309376__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309376__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309376__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg309376__6 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309376__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309376__6)).hashCode())));
        boolean o_testIteratorRemovable_mg309376__7 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309376__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309376__14 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309376__14)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309376__19 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309376__19)));
        String o_testIteratorRemovable_mg309376__20 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309376__20);
        boolean o_testIteratorRemovable_mg309376__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309376__21);
        boolean o_testIteratorRemovable_mg309376__22 = a.hasKeyIgnoreCase(__DSPOT_key_94243);
        Assert.assertFalse(o_testIteratorRemovable_mg309376__22);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309376__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309376__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309376__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309376__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309376__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309376__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309376__7);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309376__14)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309376__19)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309376__20);
        Assert.assertFalse(o_testIteratorRemovable_mg309376__21);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309309() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString309309__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString309309__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablelitString309309__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString309309__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309309__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovablelitString309309__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString309309__5 = a.put("Tot", "Jsoup");
        Assert.assertEquals(" Tot=\"Jsoup\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309309__5)).toString());
        Assert.assertEquals(-1760694580, ((int) (((Attributes) (o_testIteratorRemovablelitString309309__5)).hashCode())));
        boolean o_testIteratorRemovablelitString309309__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablelitString309309__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"Jsoup\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(74489640, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Jsoup", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString309309__13 = a.size();
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString309309__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString309309__18 = a.size();
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString309309__18)));
        String o_testIteratorRemovablelitString309309__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablelitString309309__19);
        boolean o_testIteratorRemovablelitString309309__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString309309__20);
        Assert.assertEquals(" Hello=\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(110761679, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309309__3)).toString());
        Assert.assertEquals(110761679, ((int) (((Attributes) (o_testIteratorRemovablelitString309309__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309309__4)).toString());
        Assert.assertEquals(110761679, ((int) (((Attributes) (o_testIteratorRemovablelitString309309__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309309__5)).toString());
        Assert.assertEquals(110761679, ((int) (((Attributes) (o_testIteratorRemovablelitString309309__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablelitString309309__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString309309__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString309309__18)));
        Assert.assertEquals("There", o_testIteratorRemovablelitString309309__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309388() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309388__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309388__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309388__3)).hashCode())));
        Attributes o_testIteratorRemovable_mg309388__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309388__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309388__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg309388__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309388__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309388__5)).hashCode())));
        boolean o_testIteratorRemovable_mg309388__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309388__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309388__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309388__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309388__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309388__18)));
        String o_testIteratorRemovable_mg309388__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309388__19);
        boolean o_testIteratorRemovable_mg309388__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309388__20);
        Attribute o_testIteratorRemovable_mg309388__21 = attr.clone();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (o_testIteratorRemovable_mg309388__21)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (o_testIteratorRemovable_mg309388__21)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (o_testIteratorRemovable_mg309388__21)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (o_testIteratorRemovable_mg309388__21)).getKey());
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309388__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309388__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309388__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309388__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309388__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309388__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309388__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309388__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309388__18)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309388__19);
        Assert.assertFalse(o_testIteratorRemovable_mg309388__20);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309389() throws Exception {
        Attribute __DSPOT_o_94251 = new Attribute(")og!d<ZHLiS;CnJLOmw!", "M<(|),A.!p jNRy];&`^");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309389__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309389__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309389__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg309389__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309389__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309389__6)).hashCode())));
        Attributes o_testIteratorRemovable_mg309389__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309389__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309389__7)).hashCode())));
        boolean o_testIteratorRemovable_mg309389__8 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309389__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309389__15 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309389__15)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309389__20 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309389__20)));
        String o_testIteratorRemovable_mg309389__21 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309389__21);
        boolean o_testIteratorRemovable_mg309389__22 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309389__22);
        boolean o_testIteratorRemovable_mg309389__23 = attr.equals(__DSPOT_o_94251);
        Assert.assertFalse(o_testIteratorRemovable_mg309389__23);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309389__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309389__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309389__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309389__6)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309389__7)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309389__7)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309389__8);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309389__15)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309389__20)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309389__21);
        Assert.assertFalse(o_testIteratorRemovable_mg309389__22);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309383() throws Exception {
        Attribute __DSPOT_attribute_94248 = new Attribute("+d2g6}v!Fh[lv:A^J!:%", "#,(dQP b>)$(zfqMyV%1", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309383__6 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309383__6)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309383__6)).hashCode())));
        Attributes o_testIteratorRemovable_mg309383__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309383__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309383__7)).hashCode())));
        Attributes o_testIteratorRemovable_mg309383__8 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309383__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309383__8)).hashCode())));
        boolean o_testIteratorRemovable_mg309383__9 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309383__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309383__16 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309383__16)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309383__21 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309383__21)));
        String o_testIteratorRemovable_mg309383__22 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309383__22);
        boolean o_testIteratorRemovable_mg309383__23 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309383__23);
        Attributes o_testIteratorRemovable_mg309383__24 = a.put(__DSPOT_attribute_94248);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" +d2g6}v!Fh[lv:A^J!:%=\"#,(dQP b>)$(zfqMyV%1\"", ((Attributes) (o_testIteratorRemovable_mg309383__24)).toString());
        Assert.assertEquals(-510929772, ((int) (((Attributes) (o_testIteratorRemovable_mg309383__24)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" +d2g6}v!Fh[lv:A^J!:%=\"#,(dQP b>)$(zfqMyV%1\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-510929772, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" +d2g6}v!Fh[lv:A^J!:%=\"#,(dQP b>)$(zfqMyV%1\"", ((Attributes) (o_testIteratorRemovable_mg309383__6)).toString());
        Assert.assertEquals(-510929772, ((int) (((Attributes) (o_testIteratorRemovable_mg309383__6)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" +d2g6}v!Fh[lv:A^J!:%=\"#,(dQP b>)$(zfqMyV%1\"", ((Attributes) (o_testIteratorRemovable_mg309383__7)).toString());
        Assert.assertEquals(-510929772, ((int) (((Attributes) (o_testIteratorRemovable_mg309383__7)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" +d2g6}v!Fh[lv:A^J!:%=\"#,(dQP b>)$(zfqMyV%1\"", ((Attributes) (o_testIteratorRemovable_mg309383__8)).toString());
        Assert.assertEquals(-510929772, ((int) (((Attributes) (o_testIteratorRemovable_mg309383__8)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309383__9);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309383__16)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309383__21)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309383__22);
        Assert.assertFalse(o_testIteratorRemovable_mg309383__23);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309384() throws Exception {
        String __DSPOT_key_94249 = "#?q7Mu>[bvuhQ8{3<a/T";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309384__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309384__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309384__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg309384__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309384__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309384__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg309384__6 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309384__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309384__6)).hashCode())));
        boolean o_testIteratorRemovable_mg309384__7 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309384__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309384__14 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309384__14)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309384__19 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309384__19)));
        String o_testIteratorRemovable_mg309384__20 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309384__20);
        boolean o_testIteratorRemovable_mg309384__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309384__21);
        a.remove(__DSPOT_key_94249);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309384__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309384__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309384__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309384__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309384__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309384__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309384__7);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309384__14)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309384__19)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309384__20);
        Assert.assertFalse(o_testIteratorRemovable_mg309384__21);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309395() throws Exception {
        String __DSPOT_val_94253 = "@&UtJ2PBTV]?:L2$qOUu";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309395__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309395__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309395__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg309395__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309395__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309395__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg309395__6 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309395__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309395__6)).hashCode())));
        boolean o_testIteratorRemovable_mg309395__7 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309395__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309395__14 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309395__14)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309395__19 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309395__19)));
        String o_testIteratorRemovable_mg309395__20 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309395__20);
        boolean o_testIteratorRemovable_mg309395__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309395__21);
        String o_testIteratorRemovable_mg309395__22 = attr.setValue(__DSPOT_val_94253);
        Assert.assertEquals("There", o_testIteratorRemovable_mg309395__22);
        Assert.assertEquals(" Hello=\"@&amp;UtJ2PBTV]?:L2$qOUu\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1166953890, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"@&amp;UtJ2PBTV]?:L2$qOUu\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309395__4)).toString());
        Assert.assertEquals(1166953890, ((int) (((Attributes) (o_testIteratorRemovable_mg309395__4)).hashCode())));
        Assert.assertEquals(" Hello=\"@&amp;UtJ2PBTV]?:L2$qOUu\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309395__5)).toString());
        Assert.assertEquals(1166953890, ((int) (((Attributes) (o_testIteratorRemovable_mg309395__5)).hashCode())));
        Assert.assertEquals(" Hello=\"@&amp;UtJ2PBTV]?:L2$qOUu\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309395__6)).toString());
        Assert.assertEquals(1166953890, ((int) (((Attributes) (o_testIteratorRemovable_mg309395__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309395__7);
        Assert.assertEquals("Hello=\"@&amp;UtJ2PBTV]?:L2$qOUu\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-244911981, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("@&UtJ2PBTV]?:L2$qOUu", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309395__14)));
        Assert.assertEquals("Hello=\"@&amp;UtJ2PBTV]?:L2$qOUu\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-244911981, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("@&UtJ2PBTV]?:L2$qOUu", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309395__19)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309395__20);
        Assert.assertFalse(o_testIteratorRemovable_mg309395__21);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309374() throws Exception {
        String __DSPOT_key_94241 = "rG{c}662Kcu*5<fE@0di";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309374__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309374__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309374__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg309374__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309374__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309374__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg309374__6 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309374__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309374__6)).hashCode())));
        boolean o_testIteratorRemovable_mg309374__7 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309374__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309374__14 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309374__14)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309374__19 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309374__19)));
        String o_testIteratorRemovable_mg309374__20 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309374__20);
        boolean o_testIteratorRemovable_mg309374__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309374__21);
        String o_testIteratorRemovable_mg309374__22 = a.getIgnoreCase(__DSPOT_key_94241);
        Assert.assertEquals("", o_testIteratorRemovable_mg309374__22);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309374__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309374__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309374__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309374__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309374__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309374__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309374__7);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309374__14)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309374__19)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309374__20);
        Assert.assertFalse(o_testIteratorRemovable_mg309374__21);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_remove309367() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_remove309367__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_remove309367__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_remove309367__3)).hashCode())));
        Attributes o_testIteratorRemovable_remove309367__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_remove309367__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_remove309367__4)).hashCode())));
        Attributes o_testIteratorRemovable_remove309367__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_remove309367__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_remove309367__5)).hashCode())));
        boolean o_testIteratorRemovable_remove309367__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_remove309367__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        int o_testIteratorRemovable_remove309367__12 = a.size();
        Assert.assertEquals(3, ((int) (o_testIteratorRemovable_remove309367__12)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_remove309367__17 = a.size();
        Assert.assertEquals(3, ((int) (o_testIteratorRemovable_remove309367__17)));
        String o_testIteratorRemovable_remove309367__18 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_remove309367__18);
        boolean o_testIteratorRemovable_remove309367__19 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_remove309367__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_remove309367__3)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_remove309367__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_remove309367__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_remove309367__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_remove309367__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_remove309367__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_remove309367__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(3, ((int) (o_testIteratorRemovable_remove309367__12)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(3, ((int) (o_testIteratorRemovable_remove309367__17)));
        Assert.assertEquals("There", o_testIteratorRemovable_remove309367__18);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg309370() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg309370__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg309370__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg309370__3)).hashCode())));
        Attributes o_testIteratorRemovable_mg309370__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg309370__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg309370__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg309370__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309370__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg309370__5)).hashCode())));
        boolean o_testIteratorRemovable_mg309370__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg309370__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg309370__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309370__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg309370__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309370__18)));
        String o_testIteratorRemovable_mg309370__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg309370__19);
        boolean o_testIteratorRemovable_mg309370__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg309370__20);
        Attributes o_testIteratorRemovable_mg309370__21 = a.clone();
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309370__21)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg309370__21)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309370__3)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (o_testIteratorRemovable_mg309370__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309370__4)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (o_testIteratorRemovable_mg309370__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg309370__5)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (o_testIteratorRemovable_mg309370__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg309370__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309370__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg309370__18)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg309370__19);
        Assert.assertFalse(o_testIteratorRemovable_mg309370__20);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309283() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString309283__3 = a.put("\n", "a&p");
        Assert.assertEquals(" \n=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString309283__3)).toString());
        Assert.assertEquals(-1440720416, ((int) (((Attributes) (o_testIteratorRemovablelitString309283__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString309283__4 = a.put("Hello", "There");
        Assert.assertEquals(" \n=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309283__4)).toString());
        Assert.assertEquals(-1853741837, ((int) (((Attributes) (o_testIteratorRemovablelitString309283__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString309283__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" \n=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309283__5)).toString());
        Assert.assertEquals(1753676513, ((int) (((Attributes) (o_testIteratorRemovablelitString309283__5)).hashCode())));
        boolean o_testIteratorRemovablelitString309283__6 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString309283__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(94507, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString309283__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309283__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString309283__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309283__18)));
        String o_testIteratorRemovablelitString309283__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablelitString309283__19);
        boolean o_testIteratorRemovablelitString309283__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString309283__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309283__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309283__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309283__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309283__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309283__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309283__5)).hashCode())));
        Assert.assertFalse(o_testIteratorRemovablelitString309283__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309283__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309283__18)));
        Assert.assertEquals("There", o_testIteratorRemovablelitString309283__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv309415() throws Exception {
        String __DSPOT_key_94266 = "^JY;1Z{pP@!7E%KoD:[B";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorRemovable_rv309415__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv309415__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv309415__7)).hashCode())));
        Attributes o_testIteratorRemovable_rv309415__8 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309415__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv309415__8)).hashCode())));
        boolean o_testIteratorRemovable_rv309415__9 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv309415__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv309415__16 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309415__16)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv309415__21 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309415__21)));
        String o_testIteratorRemovable_rv309415__22 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv309415__22);
        boolean o_testIteratorRemovable_rv309415__23 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv309415__23);
        __DSPOT_invoc_3.removeIgnoreCase(__DSPOT_key_94266);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309415__7)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309415__7)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309415__8)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309415__8)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv309415__9);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309415__16)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309415__21)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv309415__22);
        Assert.assertFalse(o_testIteratorRemovable_rv309415__23);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309291() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString309291__3 = a.put("Tot", "\n");
        Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorRemovablelitString309291__3)).toString());
        Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorRemovablelitString309291__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString309291__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309291__4)).toString());
        Assert.assertEquals(142692195, ((int) (((Attributes) (o_testIteratorRemovablelitString309291__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString309291__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309291__5)).toString());
        Assert.assertEquals(-544856751, ((int) (((Attributes) (o_testIteratorRemovablelitString309291__5)).hashCode())));
        boolean o_testIteratorRemovablelitString309291__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablelitString309291__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2612721, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString309291__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309291__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString309291__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309291__18)));
        String o_testIteratorRemovablelitString309291__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablelitString309291__19);
        boolean o_testIteratorRemovablelitString309291__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString309291__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309291__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309291__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309291__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309291__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309291__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString309291__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablelitString309291__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309291__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString309291__18)));
        Assert.assertEquals("There", o_testIteratorRemovablelitString309291__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv309402() throws Exception {
        Attributes __DSPOT_o_94255 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorRemovable_rv309402__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv309402__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv309402__8)).hashCode())));
        Attributes o_testIteratorRemovable_rv309402__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309402__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv309402__9)).hashCode())));
        boolean o_testIteratorRemovable_rv309402__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv309402__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv309402__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309402__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv309402__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309402__22)));
        String o_testIteratorRemovable_rv309402__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv309402__23);
        boolean o_testIteratorRemovable_rv309402__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv309402__24);
        boolean o_testIteratorRemovable_rv309402__25 = __DSPOT_invoc_3.equals(__DSPOT_o_94255);
        Assert.assertFalse(o_testIteratorRemovable_rv309402__25);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309402__8)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309402__8)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309402__9)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309402__9)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv309402__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309402__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309402__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv309402__23);
        Assert.assertFalse(o_testIteratorRemovable_rv309402__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv309444() throws Exception {
        Object __DSPOT_o_94281 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv309444__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv309444__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv309444__5)).hashCode())));
        Attributes o_testIteratorRemovable_rv309444__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv309444__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv309444__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        boolean o_testIteratorRemovable_rv309444__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv309444__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv309444__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309444__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv309444__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309444__22)));
        String o_testIteratorRemovable_rv309444__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv309444__23);
        boolean o_testIteratorRemovable_rv309444__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv309444__24);
        boolean o_testIteratorRemovable_rv309444__25 = __DSPOT_invoc_5.equals(__DSPOT_o_94281);
        Assert.assertFalse(o_testIteratorRemovable_rv309444__25);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309444__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309444__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309444__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309444__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv309444__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309444__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309444__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv309444__23);
        Assert.assertFalse(o_testIteratorRemovable_rv309444__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv309411() throws Exception {
        boolean __DSPOT_value_94261 = true;
        String __DSPOT_key_94260 = "mZIbp6gkS*9XQe#_HK[L";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorRemovable_rv309411__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv309411__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv309411__8)).hashCode())));
        Attributes o_testIteratorRemovable_rv309411__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309411__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv309411__9)).hashCode())));
        boolean o_testIteratorRemovable_rv309411__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv309411__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv309411__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309411__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv309411__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309411__22)));
        String o_testIteratorRemovable_rv309411__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv309411__23);
        boolean o_testIteratorRemovable_rv309411__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv309411__24);
        Attributes o_testIteratorRemovable_rv309411__25 = __DSPOT_invoc_3.put(__DSPOT_key_94260, __DSPOT_value_94261);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" mZIbp6gkS*9XQe#_HK[L", ((Attributes) (o_testIteratorRemovable_rv309411__25)).toString());
        Assert.assertEquals(1099171854, ((int) (((Attributes) (o_testIteratorRemovable_rv309411__25)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" mZIbp6gkS*9XQe#_HK[L", ((Attributes) (a)).toString());
        Assert.assertEquals(1099171854, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" mZIbp6gkS*9XQe#_HK[L", ((Attributes) (o_testIteratorRemovable_rv309411__8)).toString());
        Assert.assertEquals(1099171854, ((int) (((Attributes) (o_testIteratorRemovable_rv309411__8)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" mZIbp6gkS*9XQe#_HK[L", ((Attributes) (o_testIteratorRemovable_rv309411__9)).toString());
        Assert.assertEquals(1099171854, ((int) (((Attributes) (o_testIteratorRemovable_rv309411__9)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv309411__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309411__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309411__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv309411__23);
        Assert.assertFalse(o_testIteratorRemovable_rv309411__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309282_failAssert81() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            a.hasKey("Tot");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablelitString309282 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv309399() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorRemovable_rv309399__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv309399__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv309399__6)).hashCode())));
        Attributes o_testIteratorRemovable_rv309399__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309399__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv309399__7)).hashCode())));
        boolean o_testIteratorRemovable_rv309399__8 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv309399__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv309399__15 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309399__15)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv309399__20 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309399__20)));
        String o_testIteratorRemovable_rv309399__21 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv309399__21);
        boolean o_testIteratorRemovable_rv309399__22 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv309399__22);
        List<Attribute> o_testIteratorRemovable_rv309399__23 = __DSPOT_invoc_3.asList();
        Assert.assertFalse(o_testIteratorRemovable_rv309399__23.isEmpty());
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309399__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309399__6)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309399__7)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309399__7)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv309399__8);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309399__15)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309399__20)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv309399__21);
        Assert.assertFalse(o_testIteratorRemovable_rv309399__22);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv309410() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorRemovable_rv309410__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv309410__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv309410__6)).hashCode())));
        Attributes o_testIteratorRemovable_rv309410__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309410__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv309410__7)).hashCode())));
        boolean o_testIteratorRemovable_rv309410__8 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv309410__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv309410__15 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309410__15)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv309410__20 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309410__20)));
        String o_testIteratorRemovable_rv309410__21 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv309410__21);
        boolean o_testIteratorRemovable_rv309410__22 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv309410__22);
        __DSPOT_invoc_3.normalize();
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309410__6)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_testIteratorRemovable_rv309410__6)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309410__7)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_testIteratorRemovable_rv309410__7)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv309410__8);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309410__15)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309410__20)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv309410__21);
        Assert.assertFalse(o_testIteratorRemovable_rv309410__22);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv309432() throws Exception {
        boolean __DSPOT_value_94274 = false;
        String __DSPOT_key_94273 = "0ThO]Sz[{+$}8sn#no64";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv309432__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv309432__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv309432__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_testIteratorRemovable_rv309432__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309432__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv309432__9)).hashCode())));
        boolean o_testIteratorRemovable_rv309432__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv309432__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv309432__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309432__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv309432__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309432__22)));
        String o_testIteratorRemovable_rv309432__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv309432__23);
        boolean o_testIteratorRemovable_rv309432__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv309432__24);
        Attributes o_testIteratorRemovable_rv309432__25 = __DSPOT_invoc_4.put(__DSPOT_key_94273, __DSPOT_value_94274);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309432__25)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309432__25)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309432__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309432__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv309432__9)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv309432__9)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv309432__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309432__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv309432__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv309432__23);
        Assert.assertFalse(o_testIteratorRemovable_rv309432__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324619() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString324619__5 = a.put("Tot", "\n");
            Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324619__5)).toString());
            Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324619__5)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString324619__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"\n\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324619__6)).toString());
            Assert.assertEquals(142692195, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324619__6)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString324619__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324619__7)).toString());
            Assert.assertEquals(-544856751, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324619__7)).hashCode())));
            boolean o_testIteratorRemovable_add309356_failAssert83litString324619__8 = a.hasKey("Tot");
            Assert.assertTrue(o_testIteratorRemovable_add309356_failAssert83litString324619__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"\n\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2612721, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.getKey();
            iterator.remove();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324543_failAssert84() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                a.put("", "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                a.hasKey("Tot");
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = iterator.next();
                attr.getKey();
                iterator.remove();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
            } catch (ArrayIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovable_add309356_failAssert83litString324543 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("String must not be empty", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309282_failAssert81litString326976() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326976__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326976__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326976__5)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326976__6 = a.put("Helo", "There");
            Assert.assertEquals(" =\"a&amp;p\" Helo=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326976__6)).toString());
            Assert.assertEquals(1335490683, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326976__6)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326976__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"a&amp;p\" Helo=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326976__7)).toString());
            Assert.assertEquals(647941737, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326976__7)).hashCode())));
            boolean o_testIteratorRemovablelitString309282_failAssert81litString326976__8 = a.hasKey("Tot");
            Assert.assertFalse(o_testIteratorRemovablelitString309282_failAssert81litString326976__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablelitString309282 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309282_failAssert81litString326987() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326987__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326987__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326987__5)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326987__6 = a.put("", "There");
            Assert.assertEquals(" =\"There\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326987__6)).toString());
            Assert.assertEquals(1207670557, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326987__6)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326987__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326987__7)).toString());
            Assert.assertEquals(1368460881, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326987__7)).hashCode())));
            boolean o_testIteratorRemovablelitString309282_failAssert81litString326987__8 = a.hasKey("Tot");
            Assert.assertFalse(o_testIteratorRemovablelitString309282_failAssert81litString326987__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablelitString309282 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324554() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString324554__5 = a.put("\n", "a&p");
            Assert.assertEquals(" \n=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324554__5)).toString());
            Assert.assertEquals(-1440720416, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324554__5)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString324554__6 = a.put("Hello", "There");
            Assert.assertEquals(" \n=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324554__6)).toString());
            Assert.assertEquals(-1853741837, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324554__6)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString324554__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" \n=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324554__7)).toString());
            Assert.assertEquals(1753676513, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324554__7)).hashCode())));
            boolean o_testIteratorRemovable_add309356_failAssert83litString324554__8 = a.hasKey("Tot");
            Assert.assertFalse(o_testIteratorRemovable_add309356_failAssert83litString324554__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("=\"a&amp;p\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(94507, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
            Assert.assertEquals("", ((Attribute) (attr)).getKey());
            attr.getKey();
            iterator.remove();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309282_failAssert81litString326931() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326931__5 = a.put("", "");
            Assert.assertEquals(" =\"\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326931__5)).toString());
            Assert.assertEquals(29553633, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326931__5)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326931__6 = a.put("Hello", "There");
            Assert.assertEquals(" =\"\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326931__6)).toString());
            Assert.assertEquals(-383467788, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326931__6)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326931__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326931__7)).toString());
            Assert.assertEquals(-1071016734, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326931__7)).hashCode())));
            boolean o_testIteratorRemovablelitString309282_failAssert81litString326931__8 = a.hasKey("Tot");
            Assert.assertFalse(o_testIteratorRemovablelitString309282_failAssert81litString326931__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablelitString309282 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString325006() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString325006__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString325006__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString325006__5)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString325006__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString325006__6)).toString());
            Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString325006__6)).hashCode())));
            Attributes o_testIteratorRemovable_add309356_failAssert83litString325006__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString325006__7)).toString());
            Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString325006__7)).hashCode())));
            boolean o_testIteratorRemovable_add309356_failAssert83litString325006__8 = a.hasKey("Tot");
            Assert.assertTrue(o_testIteratorRemovable_add309356_failAssert83litString325006__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.getKey();
            iterator.remove();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309282_failAssert81litString326938() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326938__5 = a.put("", "\n");
            Assert.assertEquals(" =\"\n\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326938__5)).toString());
            Assert.assertEquals(29851543, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326938__5)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326938__6 = a.put("Hello", "There");
            Assert.assertEquals(" =\"\n\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326938__6)).toString());
            Assert.assertEquals(-383169878, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326938__6)).hashCode())));
            Attributes o_testIteratorRemovablelitString309282_failAssert81litString326938__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" =\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326938__7)).toString());
            Assert.assertEquals(-1070718824, ((int) (((Attributes) (o_testIteratorRemovablelitString309282_failAssert81litString326938__7)).hashCode())));
            boolean o_testIteratorRemovablelitString309282_failAssert81litString326938__8 = a.hasKey("Tot");
            Assert.assertFalse(o_testIteratorRemovablelitString309282_failAssert81litString326938__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablelitString309282 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString309298_failAssert82_add323040_mg375423() throws Exception {
        try {
            Attribute __DSPOT_attribute_114973 = new Attribute("})Jey_]0U^mfz([yl$?Y", "2w4&#^ux 4_dYg5r>LG=");
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablelitString309298_failAssert82_add323040__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString309298_failAssert82_add323040__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablelitString309298_failAssert82_add323040__5)).hashCode())));
            Attributes o_testIteratorRemovablelitString309298_failAssert82_add323040__6 = a.put("", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" =\"There\"", ((Attributes) (o_testIteratorRemovablelitString309298_failAssert82_add323040__6)).toString());
            Assert.assertEquals(-608994156, ((int) (((Attributes) (o_testIteratorRemovablelitString309298_failAssert82_add323040__6)).hashCode())));
            Attributes o_testIteratorRemovablelitString309298_failAssert82_add323040__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" =\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString309298_failAssert82_add323040__7)).toString());
            Assert.assertEquals(-1296543102, ((int) (((Attributes) (o_testIteratorRemovablelitString309298_failAssert82_add323040__7)).hashCode())));
            boolean o_testIteratorRemovablelitString309298_failAssert82_add323040__8 = a.hasKey("Tot");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.getKey();
            iterator.remove();
            int o_testIteratorRemovablelitString309298_failAssert82_add323040__15 = a.size();
            int o_testIteratorRemovablelitString309298_failAssert82_add323040__16 = a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablelitString309298 should have thrown IllegalArgumentException");
            o_testIteratorRemovablelitString309298_failAssert82_add323040__5.put(__DSPOT_attribute_114973);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__7 = a.put("", "");
                Assert.assertEquals(" =\"\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__7)).toString());
                Assert.assertEquals(29553633, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__7)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__8 = a.put("Hello", "There");
                Assert.assertEquals(" =\"\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__8)).toString());
                Assert.assertEquals(-383467788, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__8)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__9 = a.put("data-name", "Jsoup");
                Assert.assertEquals(" =\"\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__9)).toString());
                Assert.assertEquals(-1071016734, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__9)).hashCode())));
                boolean o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__10 = a.hasKey("Tot");
                Assert.assertFalse(o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356932__10);
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = iterator.next();
                attr.getKey();
                iterator.remove();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
            } catch (ArrayIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovable_add309356_failAssert83litString324543 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__7 = a.put("", "\n");
                Assert.assertEquals(" =\"\n\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__7)).toString());
                Assert.assertEquals(29851543, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__7)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__8 = a.put("Hello", "There");
                Assert.assertEquals(" =\"\n\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__8)).toString());
                Assert.assertEquals(-383169878, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__8)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__9 = a.put("data-name", "Jsoup");
                Assert.assertEquals(" =\"\n\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__9)).toString());
                Assert.assertEquals(-1070718824, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__9)).hashCode())));
                boolean o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__10 = a.hasKey("Tot");
                Assert.assertFalse(o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356942__10);
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = iterator.next();
                attr.getKey();
                iterator.remove();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
            } catch (ArrayIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovable_add309356_failAssert83litString324543 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__7 = a.put("", "a&p");
                Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__7)).toString());
                Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__7)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__8 = a.put("Hello", "There");
                Assert.assertEquals(" =\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__8)).toString());
                Assert.assertEquals(-1862977047, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__8)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__9 = a.put("data-name", "]0:>x");
                Assert.assertEquals(" =\"a&amp;p\" Hello=\"There\" data-name=\"]0:>x\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__9)).toString());
                Assert.assertEquals(-2070079561, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__9)).hashCode())));
                boolean o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__10 = a.hasKey("Tot");
                Assert.assertFalse(o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357258__10);
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = iterator.next();
                attr.getKey();
                iterator.remove();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
            } catch (ArrayIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovable_add309356_failAssert83litString324543 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__7 = a.put("", "a&p");
                Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__7)).toString());
                Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__7)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__8 = a.put("", "There");
                Assert.assertEquals(" =\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__8)).toString());
                Assert.assertEquals(1207670557, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__8)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__9 = a.put("data-name", "Jsoup");
                Assert.assertEquals(" =\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__9)).toString());
                Assert.assertEquals(1368460881, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__9)).hashCode())));
                boolean o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__10 = a.hasKey("Tot");
                Assert.assertFalse(o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString357016__10);
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = iterator.next();
                attr.getKey();
                iterator.remove();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
            } catch (ArrayIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovable_add309356_failAssert83litString324543 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__7 = a.put("", ":");
                Assert.assertEquals(" =\":\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__7)).toString());
                Assert.assertEquals(31281511, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__7)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__8 = a.put("Hello", "There");
                Assert.assertEquals(" =\":\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__8)).toString());
                Assert.assertEquals(-381739910, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__8)).hashCode())));
                Attributes o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__9 = a.put("data-name", "Jsoup");
                Assert.assertEquals(" =\":\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__9)).toString());
                Assert.assertEquals(-1069288856, ((int) (((Attributes) (o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__9)).hashCode())));
                boolean o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__10 = a.hasKey("Tot");
                Assert.assertFalse(o_testIteratorRemovable_add309356_failAssert83litString324543_failAssert84litString356951__10);
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = iterator.next();
                attr.getKey();
                iterator.remove();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovable_add309356 should have thrown ArrayIndexOutOfBoundsException");
            } catch (ArrayIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovable_add309356_failAssert83litString324543 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable__3)).hashCode())));
        Attributes o_testIteratorUpdateable__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable__4)).hashCode())));
        boolean o_testIteratorUpdateable__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable__14);
        String o_testIteratorUpdateable__15 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable__15);
        String o_testIteratorUpdateable__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable__16);
        boolean o_testIteratorUpdateable__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable__17);
        boolean o_testIteratorUpdateable__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable__18);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable__3)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable__3)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable__4)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable__14);
        Assert.assertEquals("a&p", o_testIteratorUpdateable__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateable__16);
        Assert.assertFalse(o_testIteratorUpdateable__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376938_failAssert86() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("", "a&p");
            a.put("Hello", "There");
            a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablelitString376938 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377081() throws Exception {
        Attributes __DSPOT_o_115531 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377081__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377081__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377081__8)).hashCode())));
        boolean o_testIteratorUpdateable_rv377081__9 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377081__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377081__18 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377081__18);
        String o_testIteratorUpdateable_rv377081__19 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377081__19);
        String o_testIteratorUpdateable_rv377081__20 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377081__20);
        boolean o_testIteratorUpdateable_rv377081__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377081__21);
        boolean o_testIteratorUpdateable_rv377081__22 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377081__22);
        boolean o_testIteratorUpdateable_rv377081__23 = __DSPOT_invoc_3.equals(__DSPOT_o_115531);
        Assert.assertFalse(o_testIteratorUpdateable_rv377081__23);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377081__8)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377081__8)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377081__9);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377081__18);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377081__19);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377081__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv377081__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv377081__22);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377092() throws Exception {
        Attribute __DSPOT_attribute_115540 = new Attribute("5_6#V}@;ZGOS:TU%0$iB", "@#!CC->n*yT;?5-wuCi>");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377092__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377092__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377092__8)).hashCode())));
        boolean o_testIteratorUpdateable_rv377092__9 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377092__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377092__18 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377092__18);
        String o_testIteratorUpdateable_rv377092__19 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377092__19);
        String o_testIteratorUpdateable_rv377092__20 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377092__20);
        boolean o_testIteratorUpdateable_rv377092__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377092__21);
        boolean o_testIteratorUpdateable_rv377092__22 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377092__22);
        Attributes o_testIteratorUpdateable_rv377092__23 = __DSPOT_invoc_3.put(__DSPOT_attribute_115540);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" 5_6#V}@;ZGOS:TU%0$iB=\"@#!CC->n*yT;?5-wuCi>\"", ((Attributes) (o_testIteratorUpdateable_rv377092__23)).toString());
        Assert.assertEquals(1934266696, ((int) (((Attributes) (o_testIteratorUpdateable_rv377092__23)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" 5_6#V}@;ZGOS:TU%0$iB=\"@#!CC->n*yT;?5-wuCi>\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1934266696, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" 5_6#V}@;ZGOS:TU%0$iB=\"@#!CC->n*yT;?5-wuCi>\"", ((Attributes) (o_testIteratorUpdateable_rv377092__8)).toString());
        Assert.assertEquals(1934266696, ((int) (((Attributes) (o_testIteratorUpdateable_rv377092__8)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377092__9);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377092__18);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377092__19);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377092__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv377092__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv377092__22);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_mg377067() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_mg377067__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_mg377067__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_mg377067__3)).hashCode())));
        Attributes o_testIteratorUpdateable_mg377067__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_mg377067__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_mg377067__4)).hashCode())));
        boolean o_testIteratorUpdateable_mg377067__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_mg377067__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_mg377067__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_mg377067__14);
        String o_testIteratorUpdateable_mg377067__15 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg377067__15);
        String o_testIteratorUpdateable_mg377067__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg377067__16);
        boolean o_testIteratorUpdateable_mg377067__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_mg377067__17);
        boolean o_testIteratorUpdateable_mg377067__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_mg377067__18);
        Attribute o_testIteratorUpdateable_mg377067__19 = attr.clone();
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (o_testIteratorUpdateable_mg377067__19)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (o_testIteratorUpdateable_mg377067__19)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (o_testIteratorUpdateable_mg377067__19)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (o_testIteratorUpdateable_mg377067__19)).getKey());
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg377067__3)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg377067__3)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg377067__4)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg377067__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_mg377067__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_mg377067__14);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg377067__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg377067__16);
        Assert.assertFalse(o_testIteratorUpdateable_mg377067__17);
        Assert.assertFalse(o_testIteratorUpdateable_mg377067__18);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_mg377068() throws Exception {
        Attribute __DSPOT_o_115527 = new Attribute("yK8JAOF Oe}f%Ol*}-pE", "_o`nifa<yf+QOOZ_,n8*");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_mg377068__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_mg377068__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_mg377068__5)).hashCode())));
        Attributes o_testIteratorUpdateable_mg377068__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_mg377068__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_mg377068__6)).hashCode())));
        boolean o_testIteratorUpdateable_mg377068__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_mg377068__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_mg377068__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_mg377068__16);
        String o_testIteratorUpdateable_mg377068__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg377068__17);
        String o_testIteratorUpdateable_mg377068__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg377068__18);
        boolean o_testIteratorUpdateable_mg377068__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_mg377068__19);
        boolean o_testIteratorUpdateable_mg377068__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_mg377068__20);
        boolean o_testIteratorUpdateable_mg377068__21 = attr.equals(__DSPOT_o_115527);
        Assert.assertFalse(o_testIteratorUpdateable_mg377068__21);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg377068__5)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg377068__5)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg377068__6)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg377068__6)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_mg377068__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_mg377068__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg377068__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg377068__18);
        Assert.assertFalse(o_testIteratorUpdateable_mg377068__19);
        Assert.assertFalse(o_testIteratorUpdateable_mg377068__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376933_failAssert85() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Hello", "a&p");
            a.put("Hello", "There");
            a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablelitString376933 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376978_failAssert88() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey("");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablelitString376978 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377078() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377078__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377078__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377078__6)).hashCode())));
        boolean o_testIteratorUpdateable_rv377078__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377078__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377078__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377078__16);
        String o_testIteratorUpdateable_rv377078__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377078__17);
        String o_testIteratorUpdateable_rv377078__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377078__18);
        boolean o_testIteratorUpdateable_rv377078__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377078__19);
        boolean o_testIteratorUpdateable_rv377078__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377078__20);
        List<Attribute> o_testIteratorUpdateable_rv377078__21 = __DSPOT_invoc_3.asList();
        Assert.assertFalse(o_testIteratorUpdateable_rv377078__21.isEmpty());
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377078__6)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377078__6)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377078__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377078__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377078__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377078__18);
        Assert.assertFalse(o_testIteratorUpdateable_rv377078__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv377078__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377089() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377089__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377089__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377089__6)).hashCode())));
        boolean o_testIteratorUpdateable_rv377089__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377089__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377089__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377089__16);
        String o_testIteratorUpdateable_rv377089__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377089__17);
        String o_testIteratorUpdateable_rv377089__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377089__18);
        boolean o_testIteratorUpdateable_rv377089__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377089__19);
        boolean o_testIteratorUpdateable_rv377089__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377089__20);
        __DSPOT_invoc_3.normalize();
        Assert.assertEquals(" foo=\"a&amp;p\" bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(843527454, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" foo=\"a&amp;p\" bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377089__6)).toString());
        Assert.assertEquals(843527454, ((int) (((Attributes) (o_testIteratorUpdateable_rv377089__6)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377089__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377089__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377089__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377089__18);
        Assert.assertFalse(o_testIteratorUpdateable_rv377089__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv377089__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376961() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376961__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateablelitString376961__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateablelitString376961__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376961__4 = a.put("Hello", "TUJi<");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"TUJi<\"", ((Attributes) (o_testIteratorUpdateablelitString376961__4)).toString());
        Assert.assertEquals(-1906311430, ((int) (((Attributes) (o_testIteratorUpdateablelitString376961__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString376961__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString376961__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"TUJi<\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056885718, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("TUJi<", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateablelitString376961__14 = attr.setValue("Qux");
        Assert.assertEquals("TUJi<", o_testIteratorUpdateablelitString376961__14);
        String o_testIteratorUpdateablelitString376961__15 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateablelitString376961__15);
        String o_testIteratorUpdateablelitString376961__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString376961__16);
        boolean o_testIteratorUpdateablelitString376961__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString376961__17);
        boolean o_testIteratorUpdateablelitString376961__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString376961__18);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString376961__3)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateablelitString376961__3)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString376961__4)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateablelitString376961__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString376961__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("TUJi<", o_testIteratorUpdateablelitString376961__14);
        Assert.assertEquals("a&p", o_testIteratorUpdateablelitString376961__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString376961__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString376961__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376979_failAssert89() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey("\n");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablelitString376979 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377093() throws Exception {
        String __DSPOT_key_115541 = "Gx,Go@!#x{^NB8JVbW04";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377093__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377093__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377093__7)).hashCode())));
        boolean o_testIteratorUpdateable_rv377093__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377093__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377093__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377093__17);
        String o_testIteratorUpdateable_rv377093__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377093__18);
        String o_testIteratorUpdateable_rv377093__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377093__19);
        boolean o_testIteratorUpdateable_rv377093__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377093__20);
        boolean o_testIteratorUpdateable_rv377093__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377093__21);
        __DSPOT_invoc_3.remove(__DSPOT_key_115541);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377093__7)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377093__7)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377093__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377093__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377093__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377093__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv377093__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv377093__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376946() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376946__3 = a.put("Tot", "");
        Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_testIteratorUpdateablelitString376946__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorUpdateablelitString376946__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376946__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString376946__4)).toString());
        Assert.assertEquals(142394285, ((int) (((Attributes) (o_testIteratorUpdateablelitString376946__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString376946__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString376946__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2612711, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateablelitString376946__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateablelitString376946__14);
        String o_testIteratorUpdateablelitString376946__15 = a.get("Foo");
        Assert.assertEquals("", o_testIteratorUpdateablelitString376946__15);
        String o_testIteratorUpdateablelitString376946__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString376946__16);
        boolean o_testIteratorUpdateablelitString376946__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString376946__17);
        boolean o_testIteratorUpdateablelitString376946__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString376946__18);
        Assert.assertEquals(" Foo=\"\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString376946__3)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (o_testIteratorUpdateablelitString376946__3)).hashCode())));
        Assert.assertEquals(" Foo=\"\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString376946__4)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (o_testIteratorUpdateablelitString376946__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString376946__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateablelitString376946__14);
        Assert.assertEquals("", o_testIteratorUpdateablelitString376946__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString376946__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString376946__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377083() throws Exception {
        String __DSPOT_key_115533 = "c4L3HaZ.-4R&ue9W=s{0";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377083__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377083__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377083__7)).hashCode())));
        boolean o_testIteratorUpdateable_rv377083__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377083__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377083__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377083__17);
        String o_testIteratorUpdateable_rv377083__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377083__18);
        String o_testIteratorUpdateable_rv377083__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377083__19);
        boolean o_testIteratorUpdateable_rv377083__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377083__20);
        boolean o_testIteratorUpdateable_rv377083__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377083__21);
        String o_testIteratorUpdateable_rv377083__22 = __DSPOT_invoc_3.getIgnoreCase(__DSPOT_key_115533);
        Assert.assertEquals("", o_testIteratorUpdateable_rv377083__22);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377083__7)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377083__7)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377083__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377083__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377083__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377083__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv377083__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv377083__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377094() throws Exception {
        String __DSPOT_key_115542 = ":N@fJz8|vcEL6UE{a[iO";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377094__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377094__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377094__7)).hashCode())));
        boolean o_testIteratorUpdateable_rv377094__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377094__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377094__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377094__17);
        String o_testIteratorUpdateable_rv377094__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377094__18);
        String o_testIteratorUpdateable_rv377094__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377094__19);
        boolean o_testIteratorUpdateable_rv377094__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377094__20);
        boolean o_testIteratorUpdateable_rv377094__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377094__21);
        __DSPOT_invoc_3.removeIgnoreCase(__DSPOT_key_115542);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377094__7)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377094__7)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377094__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377094__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377094__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377094__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv377094__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv377094__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376955() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376955__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateablelitString376955__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateablelitString376955__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376955__4 = a.put("\n", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" \n=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString376955__4)).toString());
        Assert.assertEquals(-608696246, ((int) (((Attributes) (o_testIteratorUpdateablelitString376955__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString376955__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString376955__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(80774724, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateablelitString376955__14 = attr.setValue("Qux");
        Assert.assertEquals("", o_testIteratorUpdateablelitString376955__14);
        String o_testIteratorUpdateablelitString376955__15 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateablelitString376955__15);
        String o_testIteratorUpdateablelitString376955__16 = a.get("Bar");
        Assert.assertEquals("", o_testIteratorUpdateablelitString376955__16);
        boolean o_testIteratorUpdateablelitString376955__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString376955__17);
        boolean o_testIteratorUpdateablelitString376955__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString376955__18);
        Assert.assertEquals(" Foo=\"a&amp;p\" \n=\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-153463497, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" \n=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString376955__3)).toString());
        Assert.assertEquals(-153463497, ((int) (((Attributes) (o_testIteratorUpdateablelitString376955__3)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" \n=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString376955__4)).toString());
        Assert.assertEquals(-153463497, ((int) (((Attributes) (o_testIteratorUpdateablelitString376955__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString376955__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("", o_testIteratorUpdateablelitString376955__14);
        Assert.assertEquals("a&p", o_testIteratorUpdateablelitString376955__15);
        Assert.assertEquals("", o_testIteratorUpdateablelitString376955__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString376955__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377106() throws Exception {
        String __DSPOT_key_115548 = "Up4 {_V5L=Q[/4X.Vx<M";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv377106__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv377106__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv377106__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv377106__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377106__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377106__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377106__17);
        String o_testIteratorUpdateable_rv377106__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377106__18);
        String o_testIteratorUpdateable_rv377106__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377106__19);
        boolean o_testIteratorUpdateable_rv377106__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377106__20);
        boolean o_testIteratorUpdateable_rv377106__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377106__21);
        boolean o_testIteratorUpdateable_rv377106__22 = __DSPOT_invoc_4.hasKeyIgnoreCase(__DSPOT_key_115548);
        Assert.assertFalse(o_testIteratorUpdateable_rv377106__22);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377106__4)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377106__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377106__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377106__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377106__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377106__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv377106__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv377106__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString376947() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376947__3 = a.put("Tot", "\n");
        Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorUpdateablelitString376947__3)).toString());
        Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorUpdateablelitString376947__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString376947__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString376947__4)).toString());
        Assert.assertEquals(142692195, ((int) (((Attributes) (o_testIteratorUpdateablelitString376947__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString376947__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString376947__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2612721, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateablelitString376947__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateablelitString376947__14);
        String o_testIteratorUpdateablelitString376947__15 = a.get("Foo");
        Assert.assertEquals("\n", o_testIteratorUpdateablelitString376947__15);
        String o_testIteratorUpdateablelitString376947__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString376947__16);
        boolean o_testIteratorUpdateablelitString376947__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString376947__17);
        boolean o_testIteratorUpdateablelitString376947__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString376947__18);
        Assert.assertEquals(" Foo=\"\n\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1223112225, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"\n\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString376947__3)).toString());
        Assert.assertEquals(-1223112225, ((int) (((Attributes) (o_testIteratorUpdateablelitString376947__3)).hashCode())));
        Assert.assertEquals(" Foo=\"\n\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString376947__4)).toString());
        Assert.assertEquals(-1223112225, ((int) (((Attributes) (o_testIteratorUpdateablelitString376947__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString376947__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateablelitString376947__14);
        Assert.assertEquals("\n", o_testIteratorUpdateablelitString376947__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString376947__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString376947__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377079() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv377079__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv377079__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv377079__6)).hashCode())));
        boolean o_testIteratorUpdateable_rv377079__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377079__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377079__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377079__16);
        String o_testIteratorUpdateable_rv377079__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377079__17);
        String o_testIteratorUpdateable_rv377079__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377079__18);
        boolean o_testIteratorUpdateable_rv377079__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377079__19);
        boolean o_testIteratorUpdateable_rv377079__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377079__20);
        Attributes o_testIteratorUpdateable_rv377079__21 = __DSPOT_invoc_3.clone();
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377079__21)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377079__21)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(73166878, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377079__6)).toString());
        Assert.assertEquals(73166878, ((int) (((Attributes) (o_testIteratorUpdateable_rv377079__6)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377079__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377079__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377079__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377079__18);
        Assert.assertFalse(o_testIteratorUpdateable_rv377079__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv377079__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377046() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_remove377046__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377046__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_remove377046__3)).hashCode())));
        Attributes o_testIteratorUpdateable_remove377046__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_remove377046__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_remove377046__4)).hashCode())));
        boolean o_testIteratorUpdateable_remove377046__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_remove377046__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_remove377046__14 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_remove377046__14);
        String o_testIteratorUpdateable_remove377046__15 = a.get("Bar");
        Assert.assertEquals("There", o_testIteratorUpdateable_remove377046__15);
        boolean o_testIteratorUpdateable_remove377046__16 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_remove377046__16);
        boolean o_testIteratorUpdateable_remove377046__17 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_remove377046__17);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1828740270, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"There\"", ((Attributes) (o_testIteratorUpdateable_remove377046__3)).toString());
        Assert.assertEquals(1828740270, ((int) (((Attributes) (o_testIteratorUpdateable_remove377046__3)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"There\"", ((Attributes) (o_testIteratorUpdateable_remove377046__4)).toString());
        Assert.assertEquals(1828740270, ((int) (((Attributes) (o_testIteratorUpdateable_remove377046__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_remove377046__5);
        Assert.assertEquals("Bar=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(82837681, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(82837681, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("a&p", o_testIteratorUpdateable_remove377046__14);
        Assert.assertEquals("There", o_testIteratorUpdateable_remove377046__15);
        Assert.assertFalse(o_testIteratorUpdateable_remove377046__16);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv377102() throws Exception {
        Object __DSPOT_o_115544 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv377102__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv377102__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv377102__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv377102__9 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv377102__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_rv377102__18 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377102__18);
        String o_testIteratorUpdateable_rv377102__19 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377102__19);
        String o_testIteratorUpdateable_rv377102__20 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377102__20);
        boolean o_testIteratorUpdateable_rv377102__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv377102__21);
        boolean o_testIteratorUpdateable_rv377102__22 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv377102__22);
        boolean o_testIteratorUpdateable_rv377102__23 = __DSPOT_invoc_4.equals(__DSPOT_o_115544);
        Assert.assertFalse(o_testIteratorUpdateable_rv377102__23);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv377102__5)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv377102__5)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv377102__9);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv377102__18);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv377102__19);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv377102__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv377102__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv377102__22);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_mg377060() throws Exception {
        boolean __DSPOT_value_115521 = true;
        String __DSPOT_key_115520 = "N{PbA%Uj2Dz_CF^e&%Ze";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_mg377060__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_mg377060__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_mg377060__5)).hashCode())));
        Attributes o_testIteratorUpdateable_mg377060__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_mg377060__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_mg377060__6)).hashCode())));
        boolean o_testIteratorUpdateable_mg377060__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_mg377060__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateable_mg377060__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_mg377060__16);
        String o_testIteratorUpdateable_mg377060__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg377060__17);
        String o_testIteratorUpdateable_mg377060__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg377060__18);
        boolean o_testIteratorUpdateable_mg377060__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_mg377060__19);
        boolean o_testIteratorUpdateable_mg377060__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_mg377060__20);
        Attributes o_testIteratorUpdateable_mg377060__21 = a.put(__DSPOT_key_115520, __DSPOT_value_115521);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" N{PbA%Uj2Dz_CF^e&%Ze", ((Attributes) (o_testIteratorUpdateable_mg377060__21)).toString());
        Assert.assertEquals(1458774978, ((int) (((Attributes) (o_testIteratorUpdateable_mg377060__21)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" N{PbA%Uj2Dz_CF^e&%Ze", ((Attributes) (a)).toString());
        Assert.assertEquals(1458774978, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" N{PbA%Uj2Dz_CF^e&%Ze", ((Attributes) (o_testIteratorUpdateable_mg377060__5)).toString());
        Assert.assertEquals(1458774978, ((int) (((Attributes) (o_testIteratorUpdateable_mg377060__5)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" N{PbA%Uj2Dz_CF^e&%Ze", ((Attributes) (o_testIteratorUpdateable_mg377060__6)).toString());
        Assert.assertEquals(1458774978, ((int) (((Attributes) (o_testIteratorUpdateable_mg377060__6)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_mg377060__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_mg377060__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg377060__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg377060__18);
        Assert.assertFalse(o_testIteratorUpdateable_mg377060__19);
        Assert.assertFalse(o_testIteratorUpdateable_mg377060__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93litString385415() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93litString385415__5 = a.put("\n", "a&p");
            Assert.assertEquals(" \n=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385415__5)).toString());
            Assert.assertEquals(-1440720416, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385415__5)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93litString385415__6 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateable_remove377043_failAssert93litString385415__6);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("=\"a&amp;p\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(94507, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
            Assert.assertEquals("", ((Attribute) (attr)).getKey());
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93litString385547() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93litString385547__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385547__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385547__5)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93litString385547__6 = a.hasKey("");
            Assert.assertFalse(o_testIteratorUpdateable_remove377043_failAssert93litString385547__6);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93litString385403() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93litString385403__5 = a.put("", "a&p");
            Assert.assertEquals(" =\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385403__5)).toString());
            Assert.assertEquals(-1449955626, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385403__5)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93litString385403__6 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateable_remove377043_failAssert93litString385403__6);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93litString385478() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93litString385478__5 = a.put("Tot", "");
            Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385478__5)).toString());
            Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385478__5)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93litString385478__6 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateable_remove377043_failAssert93litString385478__6);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2612711, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93litString385488() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93litString385488__5 = a.put("Tot", "\n");
            Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385488__5)).toString());
            Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385488__5)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93litString385488__6 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateable_remove377043_failAssert93litString385488__6);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"\n\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2612721, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93_add386280() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93_add386280__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__5)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93_add386280__6 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__6)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__6)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93_add386280__7 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateable_remove377043_failAssert93_add386280__7);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93litString385403litString423925() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93litString385403__5 = a.put("", "");
            Assert.assertEquals(" =\"\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385403__5)).toString());
            Assert.assertEquals(29553633, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385403__5)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93litString385403__6 = a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93litString385403litString423936() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93litString385403__5 = a.put("", "\n");
            Assert.assertEquals(" =\"\n\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385403__5)).toString());
            Assert.assertEquals(29851543, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93litString385403__5)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93litString385403__6 = a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_remove377043_failAssert93_add386280_mg434513() throws Exception {
        try {
            Attributes __DSPOT_incoming_129107 = new Attributes();
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93_add386280__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__5)).hashCode())));
            Attributes o_testIteratorUpdateable_remove377043_failAssert93_add386280__6 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__6)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_remove377043_failAssert93_add386280__6)).hashCode())));
            boolean o_testIteratorUpdateable_remove377043_failAssert93_add386280__7 = a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
            Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
            Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
            Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateable_remove377043 should have thrown IllegalArgumentException");
            a.addAll(__DSPOT_incoming_129107);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext__3)).hashCode())));
        Attributes o_testIteratorHasNext__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext__4)).hashCode())));
        Attributes o_testIteratorHasNext__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext__4)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260524() throws Exception {
        String __DSPOT_value_74816 = "D=)rv?-5})zo7[H&[Kq?";
        String __DSPOT_key_74815 = "R)$%=.0UZ!.h%xp HY0V";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "1");
        Attributes o_testIteratorHasNext_rv260524__8 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv260524__8)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv260524__8)).hashCode())));
        Attributes o_testIteratorHasNext_rv260524__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260524__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260524__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv260524__17 = __DSPOT_invoc_3.put(__DSPOT_key_74815, __DSPOT_value_74816);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" R)$%=.0UZ!.h%xp HY0V=\"D=)rv?-5})zo7[H&amp;[Kq?\"", ((Attributes) (o_testIteratorHasNext_rv260524__17)).toString());
        Assert.assertEquals(-1282775758, ((int) (((Attributes) (o_testIteratorHasNext_rv260524__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" R)$%=.0UZ!.h%xp HY0V=\"D=)rv?-5})zo7[H&amp;[Kq?\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1282775758, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" R)$%=.0UZ!.h%xp HY0V=\"D=)rv?-5})zo7[H&amp;[Kq?\"", ((Attributes) (o_testIteratorHasNext_rv260524__8)).toString());
        Assert.assertEquals(-1282775758, ((int) (((Attributes) (o_testIteratorHasNext_rv260524__8)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" R)$%=.0UZ!.h%xp HY0V=\"D=)rv?-5})zo7[H&amp;[Kq?\"", ((Attributes) (o_testIteratorHasNext_rv260524__9)).toString());
        Assert.assertEquals(-1282775758, ((int) (((Attributes) (o_testIteratorHasNext_rv260524__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260535() throws Exception {
        Attributes __DSPOT_o_74821 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260535__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260535__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260535__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv260535__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260535__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260535__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        boolean o_testIteratorHasNext_rv260535__17 = __DSPOT_invoc_4.equals(__DSPOT_o_74821);
        Assert.assertFalse(o_testIteratorHasNext_rv260535__17);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260535__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260535__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260535__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260535__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260546() throws Exception {
        Attribute __DSPOT_attribute_74830 = new Attribute("2WZ %=-{Q7J.THge@%UP", "yf,GUFi+uZ!2K)CkA5+P", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260546__6 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260546__6)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260546__6)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv260546__10 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260546__10)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260546__10)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv260546__18 = __DSPOT_invoc_4.put(__DSPOT_attribute_74830);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" 2WZ %=-{Q7J.THge@%UP=\"yf,GUFi+uZ!2K)CkA5+P\"", ((Attributes) (o_testIteratorHasNext_rv260546__18)).toString());
        Assert.assertEquals(1683061322, ((int) (((Attributes) (o_testIteratorHasNext_rv260546__18)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" 2WZ %=-{Q7J.THge@%UP=\"yf,GUFi+uZ!2K)CkA5+P\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1683061322, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" 2WZ %=-{Q7J.THge@%UP=\"yf,GUFi+uZ!2K)CkA5+P\"", ((Attributes) (o_testIteratorHasNext_rv260546__6)).toString());
        Assert.assertEquals(1683061322, ((int) (((Attributes) (o_testIteratorHasNext_rv260546__6)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" 2WZ %=-{Q7J.THge@%UP=\"yf,GUFi+uZ!2K)CkA5+P\"", ((Attributes) (o_testIteratorHasNext_rv260546__10)).toString());
        Assert.assertEquals(1683061322, ((int) (((Attributes) (o_testIteratorHasNext_rv260546__10)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260533() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260533__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260533__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260533__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv260533__7 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260533__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260533__7)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv260533__15 = __DSPOT_invoc_4.clone();
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260533__15)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260533__15)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(870255776, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260533__3)).toString());
        Assert.assertEquals(870255776, ((int) (((Attributes) (o_testIteratorHasNext_rv260533__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260533__7)).toString());
        Assert.assertEquals(870255776, ((int) (((Attributes) (o_testIteratorHasNext_rv260533__7)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260438() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNextlitString260438__3 = a.put("Tot", "\n");
        Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorHasNextlitString260438__3)).toString());
        Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorHasNextlitString260438__3)).hashCode())));
        Attributes o_testIteratorHasNextlitString260438__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260438__4)).toString());
        Assert.assertEquals(-172358191, ((int) (((Attributes) (o_testIteratorHasNextlitString260438__4)).hashCode())));
        Attributes o_testIteratorHasNextlitString260438__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260438__5)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString260438__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260438__3)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString260438__3)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260438__4)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString260438__4)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260438__5)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString260438__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260427() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNextlitString260427__3 = a.put("data-name", "1");
        Assert.assertEquals(" data-name=\"1\"", ((Attributes) (o_testIteratorHasNextlitString260427__3)).toString());
        Assert.assertEquals(-1671362082, ((int) (((Attributes) (o_testIteratorHasNextlitString260427__3)).hashCode())));
        Attributes o_testIteratorHasNextlitString260427__4 = a.put("Hello", "2");
        Assert.assertEquals(" data-name=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260427__4)).toString());
        Assert.assertEquals(1895533407, ((int) (((Attributes) (o_testIteratorHasNextlitString260427__4)).hashCode())));
        Attributes o_testIteratorHasNextlitString260427__5 = a.put("data-name", "3");
        Assert.assertEquals(" data-name=\"3\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260427__5)).toString());
        Assert.assertEquals(1895592989, ((int) (((Attributes) (o_testIteratorHasNextlitString260427__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" data-name=\"3\" Hello=\"2\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1895592989, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" data-name=\"3\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260427__3)).toString());
        Assert.assertEquals(1895592989, ((int) (((Attributes) (o_testIteratorHasNextlitString260427__3)).hashCode())));
        Assert.assertEquals(" data-name=\"3\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260427__4)).toString());
        Assert.assertEquals(1895592989, ((int) (((Attributes) (o_testIteratorHasNextlitString260427__4)).hashCode())));
        Assert.assertEquals(" data-name=\"3\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260427__5)).toString());
        Assert.assertEquals(1895592989, ((int) (((Attributes) (o_testIteratorHasNextlitString260427__5)).hashCode())));
        Assert.assertEquals(2, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260544() throws Exception {
        boolean __DSPOT_value_74827 = false;
        String __DSPOT_key_74826 = "a:Q!6+1.J({P=GH&qO:)";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260544__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260544__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260544__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv260544__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260544__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260544__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv260544__17 = __DSPOT_invoc_4.put(__DSPOT_key_74826, __DSPOT_value_74827);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260544__17)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260544__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260544__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260544__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260544__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260544__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_mg260507() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_mg260507__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_mg260507__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_mg260507__3)).hashCode())));
        Attributes o_testIteratorHasNext_mg260507__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_mg260507__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_mg260507__4)).hashCode())));
        Attributes o_testIteratorHasNext_mg260507__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260507__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260507__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            String __DSPOT_val_74806 = "3&qZQqi<Hh:;>fXNJG}_";
            seen++;
            String.valueOf(seen);
            attribute.getValue();
            attribute.setValue(__DSPOT_val_74806);
        }
        Assert.assertEquals(" Tot=\"3&amp;qZQqi<Hh:;>fXNJG}_\" Hello=\"3&amp;qZQqi<Hh:;>fXNJG}_\" data-name=\"3&amp;qZQqi<Hh:;>fXNJG}_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(158659499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"3&amp;qZQqi<Hh:;>fXNJG}_\" Hello=\"3&amp;qZQqi<Hh:;>fXNJG}_\" data-name=\"3&amp;qZQqi<Hh:;>fXNJG}_\"", ((Attributes) (o_testIteratorHasNext_mg260507__3)).toString());
        Assert.assertEquals(158659499, ((int) (((Attributes) (o_testIteratorHasNext_mg260507__3)).hashCode())));
        Assert.assertEquals(" Tot=\"3&amp;qZQqi<Hh:;>fXNJG}_\" Hello=\"3&amp;qZQqi<Hh:;>fXNJG}_\" data-name=\"3&amp;qZQqi<Hh:;>fXNJG}_\"", ((Attributes) (o_testIteratorHasNext_mg260507__4)).toString());
        Assert.assertEquals(158659499, ((int) (((Attributes) (o_testIteratorHasNext_mg260507__4)).hashCode())));
        Assert.assertEquals(" Tot=\"3&amp;qZQqi<Hh:;>fXNJG}_\" Hello=\"3&amp;qZQqi<Hh:;>fXNJG}_\" data-name=\"3&amp;qZQqi<Hh:;>fXNJG}_\"", ((Attributes) (o_testIteratorHasNext_mg260507__5)).toString());
        Assert.assertEquals(158659499, ((int) (((Attributes) (o_testIteratorHasNext_mg260507__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260556() throws Exception {
        Object __DSPOT_o_74834 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260556__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260556__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260556__5)).hashCode())));
        Attributes o_testIteratorHasNext_rv260556__6 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv260556__6)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv260556__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "3");
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        boolean o_testIteratorHasNext_rv260556__17 = __DSPOT_invoc_5.equals(__DSPOT_o_74834);
        Assert.assertFalse(o_testIteratorHasNext_rv260556__17);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260556__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260556__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260556__6)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260556__6)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260545() throws Exception {
        String __DSPOT_value_74829 = "GOtJ)Q}kj(-&EFc0<Ty5";
        String __DSPOT_key_74828 = "X^)Z<:;/G;r&dxu[R3&>";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260545__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260545__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260545__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv260545__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260545__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260545__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv260545__17 = __DSPOT_invoc_4.put(__DSPOT_key_74828, __DSPOT_value_74829);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" X^)Z<:;/G;r&dxu[R3&>=\"GOtJ)Q}kj(-&amp;EFc0<Ty5\"", ((Attributes) (o_testIteratorHasNext_rv260545__17)).toString());
        Assert.assertEquals(-161693648, ((int) (((Attributes) (o_testIteratorHasNext_rv260545__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" X^)Z<:;/G;r&dxu[R3&>=\"GOtJ)Q}kj(-&amp;EFc0<Ty5\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-161693648, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" X^)Z<:;/G;r&dxu[R3&>=\"GOtJ)Q}kj(-&amp;EFc0<Ty5\"", ((Attributes) (o_testIteratorHasNext_rv260545__5)).toString());
        Assert.assertEquals(-161693648, ((int) (((Attributes) (o_testIteratorHasNext_rv260545__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" X^)Z<:;/G;r&dxu[R3&>=\"GOtJ)Q}kj(-&amp;EFc0<Ty5\"", ((Attributes) (o_testIteratorHasNext_rv260545__9)).toString());
        Assert.assertEquals(-161693648, ((int) (((Attributes) (o_testIteratorHasNext_rv260545__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_mg260505() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_mg260505__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_mg260505__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_mg260505__3)).hashCode())));
        Attributes o_testIteratorHasNext_mg260505__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_mg260505__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_mg260505__4)).hashCode())));
        Attributes o_testIteratorHasNext_mg260505__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260505__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260505__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
            attribute.html();
        }
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260505__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260505__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260505__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260505__4)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260505__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260505__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260516() throws Exception {
        String __DSPOT_key_74810 = ";v|%a/%|+,rjh4g?n8fQ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "1");
        Attributes o_testIteratorHasNext_rv260516__7 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv260516__7)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv260516__7)).hashCode())));
        Attributes o_testIteratorHasNext_rv260516__8 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260516__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260516__8)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        String o_testIteratorHasNext_rv260516__16 = __DSPOT_invoc_3.getIgnoreCase(__DSPOT_key_74810);
        Assert.assertEquals("", o_testIteratorHasNext_rv260516__16);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260516__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260516__7)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260516__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260516__8)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_mg260497() throws Exception {
        String __DSPOT_key_74803 = "w[e!9y,5Ds94/>Y!Qh%e";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_mg260497__4 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_mg260497__4)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_mg260497__4)).hashCode())));
        Attributes o_testIteratorHasNext_mg260497__5 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_mg260497__5)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_mg260497__5)).hashCode())));
        Attributes o_testIteratorHasNext_mg260497__6 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260497__6)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260497__6)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        a.removeIgnoreCase(__DSPOT_key_74803);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260497__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260497__4)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260497__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260497__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg260497__6)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg260497__6)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260450() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNextlitString260450__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextlitString260450__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextlitString260450__3)).hashCode())));
        Attributes o_testIteratorHasNextlitString260450__4 = a.put("Hello", "");
        Assert.assertEquals(" Tot=\"1\" Hello=\"\"", ((Attributes) (o_testIteratorHasNextlitString260450__4)).toString());
        Assert.assertEquals(-171244392, ((int) (((Attributes) (o_testIteratorHasNextlitString260450__4)).hashCode())));
        Attributes o_testIteratorHasNextlitString260450__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260450__5)).toString());
        Assert.assertEquals(1207990740, ((int) (((Attributes) (o_testIteratorHasNextlitString260450__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" Tot=\"1\" Hello=\"\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1207990740, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260450__3)).toString());
        Assert.assertEquals(1207990740, ((int) (((Attributes) (o_testIteratorHasNextlitString260450__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260450__4)).toString());
        Assert.assertEquals(1207990740, ((int) (((Attributes) (o_testIteratorHasNextlitString260450__4)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260450__5)).toString());
        Assert.assertEquals(1207990740, ((int) (((Attributes) (o_testIteratorHasNextlitString260450__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260560() throws Exception {
        String __DSPOT_key_74838 = ";t{C1]1no4/bz4yk]5#s";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260560__4 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260560__4)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260560__4)).hashCode())));
        Attributes o_testIteratorHasNext_rv260560__5 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv260560__5)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv260560__5)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "3");
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        boolean o_testIteratorHasNext_rv260560__16 = __DSPOT_invoc_5.hasKeyIgnoreCase(__DSPOT_key_74838);
        Assert.assertFalse(o_testIteratorHasNext_rv260560__16);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260560__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260560__4)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260560__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260560__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260432_failAssert78() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("", "1");
            a.put("Hello", "2");
            a.put("data-name", "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString260432 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260553() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260553__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260553__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260553__3)).hashCode())));
        Attributes o_testIteratorHasNext_rv260553__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv260553__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv260553__4)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "3");
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        List<Attribute> o_testIteratorHasNext_rv260553__15 = __DSPOT_invoc_5.asList();
        Assert.assertFalse(o_testIteratorHasNext_rv260553__15.isEmpty());
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260553__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260553__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260553__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260553__4)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260543() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260543__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260543__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260543__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv260543__7 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260543__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260543__7)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        __DSPOT_invoc_4.normalize();
        Assert.assertEquals(" tot=\"1\" hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-521258618, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"1\" hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260543__3)).toString());
        Assert.assertEquals(-521258618, ((int) (((Attributes) (o_testIteratorHasNext_rv260543__3)).hashCode())));
        Assert.assertEquals(" tot=\"1\" hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260543__7)).toString());
        Assert.assertEquals(-521258618, ((int) (((Attributes) (o_testIteratorHasNext_rv260543__7)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260540() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260540__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260540__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260540__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv260540__7 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260540__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260540__7)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        __DSPOT_invoc_4.hashCode();
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260540__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260540__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260540__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260540__7)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv260562() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv260562__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv260562__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv260562__3)).hashCode())));
        Attributes o_testIteratorHasNext_rv260562__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv260562__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv260562__4)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "3");
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        String o_testIteratorHasNext_rv260562__15 = __DSPOT_invoc_5.html();
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", o_testIteratorHasNext_rv260562__15);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260562__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260562__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv260562__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv260562__4)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260432_failAssert78litString271719() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271719__5 = a.put("", "1");
            Assert.assertEquals(" =\"1\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271719__5)).toString());
            Assert.assertEquals(31013392, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271719__5)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271719__6 = a.put("Hello", "2");
            Assert.assertEquals(" =\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271719__6)).toString());
            Assert.assertEquals(-697058415, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271719__6)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271719__7 = a.put("Hello", "3");
            Assert.assertEquals(" =\"1\" Hello=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271719__7)).toString());
            Assert.assertEquals(-697057454, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271719__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString260432 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260445_failAssert79_mg271593() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextlitString260445_failAssert79_mg271593__5 = a.put("Tot", "1");
            Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_mg271593__5)).toString());
            Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_mg271593__5)).hashCode())));
            Attributes o_testIteratorHasNextlitString260445_failAssert79_mg271593__6 = a.put("", "2");
            Assert.assertEquals(" Tot=\"1\" =\"2\"", ((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_mg271593__6)).toString());
            Assert.assertEquals(556924476, ((int) (((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_mg271593__6)).hashCode())));
            Attributes o_testIteratorHasNextlitString260445_failAssert79_mg271593__7 = a.put("data-name", "3");
            Assert.assertEquals(" Tot=\"1\" =\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_mg271593__7)).toString());
            Assert.assertEquals(1936159608, ((int) (((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_mg271593__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
                attribute.html();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString260445 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260432_failAssert78litString271787() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271787__5 = a.put("", "1");
            Assert.assertEquals(" =\"1\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271787__5)).toString());
            Assert.assertEquals(31013392, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271787__5)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271787__6 = a.put("Hello", "2");
            Assert.assertEquals(" =\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271787__6)).toString());
            Assert.assertEquals(-697058415, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271787__6)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271787__7 = a.put("data-name", "\n");
            Assert.assertEquals(" =\"1\" Hello=\"2\" data-name=\"\n\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271787__7)).toString());
            Assert.assertEquals(682175446, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271787__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString260432 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260432_failAssert78litString271775() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271775__5 = a.put("", "1");
            Assert.assertEquals(" =\"1\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271775__5)).toString());
            Assert.assertEquals(31013392, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271775__5)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271775__6 = a.put("Hello", "2");
            Assert.assertEquals(" =\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271775__6)).toString());
            Assert.assertEquals(-697058415, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271775__6)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271775__7 = a.put("data-name", "");
            Assert.assertEquals(" =\"1\" Hello=\"2\" data-name=\"\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271775__7)).toString());
            Assert.assertEquals(682175136, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271775__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString260432 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260432_failAssert78litString271722() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271722__5 = a.put("", "1");
            Assert.assertEquals(" =\"1\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271722__5)).toString());
            Assert.assertEquals(31013392, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271722__5)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271722__6 = a.put("Hello", "2");
            Assert.assertEquals(" =\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271722__6)).toString());
            Assert.assertEquals(-697058415, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271722__6)).hashCode())));
            Attributes o_testIteratorHasNextlitString260432_failAssert78litString271722__7 = a.put("data#name", "3");
            Assert.assertEquals(" =\"1\" Hello=\"2\" data#name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271722__7)).toString());
            Assert.assertEquals(397074499, ((int) (((Attributes) (o_testIteratorHasNextlitString260432_failAssert78litString271722__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString260432 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString260445_failAssert79_add271144_mg298545() throws Exception {
        try {
            boolean __DSPOT_value_89312 = false;
            String __DSPOT_key_89311 = "v_[0aq#M[vDDv&az<+9c";
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextlitString260445_failAssert79_add271144__5 = a.put("Tot", "1");
            Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__5)).toString());
            Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__5)).hashCode())));
            Attributes o_testIteratorHasNextlitString260445_failAssert79_add271144__6 = a.put("Tot", "1");
            Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__6)).toString());
            Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__6)).hashCode())));
            Attributes o_testIteratorHasNextlitString260445_failAssert79_add271144__7 = a.put("", "2");
            Assert.assertEquals(" Tot=\"1\" =\"2\"", ((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__7)).toString());
            Assert.assertEquals(556924476, ((int) (((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__7)).hashCode())));
            Attributes o_testIteratorHasNextlitString260445_failAssert79_add271144__8 = a.put("data-name", "3");
            Assert.assertEquals(" Tot=\"1\" =\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__8)).toString());
            Assert.assertEquals(1936159608, ((int) (((Attributes) (o_testIteratorHasNextlitString260445_failAssert79_add271144__8)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString260445 should have thrown IllegalArgumentException");
            o_testIteratorHasNextlitString260445_failAssert79_add271144__6.put(__DSPOT_key_89311, __DSPOT_value_89312);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testIterator() {
        Attributes a = new Attributes();
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        Assert.assertTrue(iterator.hasNext());
        int i = 0;
        for (Attribute attribute : a) {
            Assert.assertEquals(datas[i][0], attribute.getKey());
            Assert.assertEquals(datas[i][1], attribute.getValue());
            i++;
        }
        Assert.assertEquals(datas.length, i);
    }

    @Test(timeout = 10000)
    public void testIterator_mg189603() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189603__10 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189603__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_216 = datas[i][0];
            attribute.getKey();
            String String_217 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.html();
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189603__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv189620() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.normalize();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_rv189620__13 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_rv189620__13);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_248 = datas[i][0];
            attribute.getKey();
            String String_249 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1649101113, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_rv189620__13);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189605() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189605__10 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189605__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String __DSPOT_val_55737 = "7r839sC4l#;1vGloe=f&";
            String String_220 = datas[i][0];
            attribute.getKey();
            String String_221 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.setValue(__DSPOT_val_55737);
        }
        Assert.assertEquals(" Tot=\"7r839sC4l#;1vGloe=f&amp;\" Hello=\"7r839sC4l#;1vGloe=f&amp;\" data-name=\"7r839sC4l#;1vGloe=f&amp;\"", ((Attributes) (a)).toString());
        Assert.assertEquals(41251166, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189605__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv189621() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            boolean __DSPOT_value_55745 = false;
            String __DSPOT_key_55744 = "?f?W-7Fqp4<R!V}`!Ap?";
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.put(__DSPOT_key_55744, __DSPOT_value_55745);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_rv189621__15 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_rv189621__15);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_250 = datas[i][0];
            attribute.getKey();
            String String_251 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_rv189621__15);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratorlitNum189561() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorlitNum189561__10 = iterator.hasNext();
        Assert.assertTrue(o_testIteratorlitNum189561__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_132 = datas[i][1];
            attribute.getKey();
            String String_133 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIteratorlitNum189561__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratorlitString189509() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorlitString189509__10 = iterator.hasNext();
        Assert.assertTrue(o_testIteratorlitString189509__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_28 = datas[i][0];
            attribute.getKey();
            String String_29 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-904305267, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIteratorlitString189509__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratorlitString189505() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "r>ul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorlitString189505__10 = iterator.hasNext();
        Assert.assertTrue(o_testIteratorlitString189505__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_20 = datas[i][0];
            attribute.getKey();
            String String_21 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"r>ul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-921823990, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIteratorlitString189505__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189591() throws Exception {
        boolean __DSPOT_value_55729 = true;
        String __DSPOT_key_55728 = "wc_aqL*wEcQ|rQxu)i$6";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189591__12 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189591__12);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_192 = datas[i][0];
            attribute.getKey();
            String String_193 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189591__22 = a.put(__DSPOT_key_55728, __DSPOT_value_55729);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" wc_aqL*wEcQ|rQxu)i$6", ((Attributes) (o_testIterator_mg189591__22)).toString());
        Assert.assertEquals(-1123403813, ((int) (((Attributes) (o_testIterator_mg189591__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" wc_aqL*wEcQ|rQxu)i$6", ((Attributes) (a)).toString());
        Assert.assertEquals(-1123403813, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189591__12);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv189609() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.asList();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_rv189609__13 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_rv189609__13);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_226 = datas[i][0];
            attribute.getKey();
            String String_227 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_rv189609__13);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189593__13);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189593__13);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592() throws Exception {
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189592__12);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189592__12);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189595() throws Exception {
        String __DSPOT_key_55734 = "d{.r,cE${kmf7Gyzy$!$";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189595__11 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189595__11);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_200 = datas[i][0];
            attribute.getKey();
            String String_201 = datas[i][1];
            attribute.getValue();
            i++;
        }
        a.removeIgnoreCase(__DSPOT_key_55734);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189595__11);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189594() throws Exception {
        String __DSPOT_key_55733 = "3d<o=#G$N TExQ@3>z ;";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189594__11 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189594__11);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_198 = datas[i][0];
            attribute.getKey();
            String String_199 = datas[i][1];
            attribute.getValue();
            i++;
        }
        a.remove(__DSPOT_key_55733);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189594__11);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189579() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189579__10 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189579__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_168 = datas[i][0];
            attribute.getKey();
            String String_169 = datas[i][1];
            attribute.getValue();
            i++;
        }
        List<Attribute> o_testIterator_mg189579__20 = a.asList();
        Assert.assertFalse(o_testIterator_mg189579__20.isEmpty());
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189579__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratorlitString189517_failAssert5() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_44 = datas[i][0];
                attribute.getKey();
                String String_45 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIteratorlitString189517 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_rv189610() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.clone();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_rv189610__13 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_rv189610__13);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_228 = datas[i][0];
            attribute.getKey();
            String String_229 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-274504897, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_rv189610__13);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv189612() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Object __DSPOT_o_55739 = new Object();
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.equals(__DSPOT_o_55739);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_rv189612__15 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_rv189612__15);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_232 = datas[i][0];
            attribute.getKey();
            String String_233 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_rv189612__15);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratorlitString189510() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "\n" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorlitString189510__10 = iterator.hasNext();
        Assert.assertTrue(o_testIteratorlitString189510__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_30 = datas[i][0];
            attribute.getKey();
            String String_31 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"\n\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-904007357, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIteratorlitString189510__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv189614() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            String __DSPOT_key_55741 = "A6,zhX/TKe].7A;VP;u`";
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.getIgnoreCase(__DSPOT_key_55741);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_rv189614__14 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_rv189614__14);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_236 = datas[i][0];
            attribute.getKey();
            String String_237 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_rv189614__14);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv189622_failAssert28() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                String __DSPOT_value_55747 = "<P*EBRM1;fexU9OKxaD/";
                String __DSPOT_key_55746 = "r+|&jQb%jW|{tud# Kt%";
                Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_7.put(__DSPOT_key_55746, __DSPOT_value_55747);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_252 = datas[i][0];
                attribute.getKey();
                String String_253 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIterator_rv189622 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_rv189623_failAssert29() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                Attribute __DSPOT_attribute_55748 = new Attribute("1SCl(3W,@m>xNjDq7+qL", "NymE[P_-fnQXVLu1zts(", new Attributes());
                Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_7.put(__DSPOT_attribute_55748);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_254 = datas[i][0];
                attribute.getKey();
                String String_255 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIterator_rv189623 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg189580() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189580__10 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189580__10);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_170 = datas[i][0];
            attribute.getKey();
            String String_171 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189580__20 = a.clone();
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (o_testIterator_mg189580__20)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (o_testIterator_mg189580__20)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-274504897, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189580__10);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_add189572() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_add189572__11 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_add189572__11);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_154 = datas[i][0];
            attribute.getKey();
            String String_155 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_add189572__11);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189582() throws Exception {
        Object __DSPOT_o_55723 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189582__12 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189582__12);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_174 = datas[i][0];
            attribute.getKey();
            String String_175 = datas[i][1];
            attribute.getValue();
            i++;
        }
        boolean o_testIterator_mg189582__22 = a.equals(__DSPOT_o_55723);
        Assert.assertFalse(o_testIterator_mg189582__22);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189582__12);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189586() throws Exception {
        String __DSPOT_key_55727 = "6[m<5`s1VIENEgef%=@o";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189586__11 = iterator.hasNext();
        Assert.assertTrue(o_testIterator_mg189586__11);
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_182 = datas[i][0];
            attribute.getKey();
            String String_183 = datas[i][1];
            attribute.getValue();
            i++;
        }
        boolean o_testIterator_mg189586__21 = a.hasKeyIgnoreCase(__DSPOT_key_55727);
        Assert.assertFalse(o_testIterator_mg189586__21);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertTrue(o_testIterator_mg189586__11);
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_rv205927() throws Exception {
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.normalize();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(931534881, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(931534881, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189591_rv206557_failAssert35() throws Exception {
        try {
            boolean __DSPOT_value_55729 = true;
            String __DSPOT_key_55728 = "wc_aqL*wEcQ|rQxu)i$6";
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                Attribute __DSPOT_attribute_59577 = new Attribute("^pj/q=BEB]A@3i(v8b3B", "?0cmIeq1=px8.qqFddYr");
                Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_15.put(__DSPOT_attribute_59577);
            }
            Iterator<Attribute> iterator = a.iterator();
            boolean o_testIterator_mg189591__12 = iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_192 = datas[i][0];
                attribute.getKey();
                String String_193 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg189591__22 = a.put(__DSPOT_key_55728, __DSPOT_value_55729);
            org.junit.Assert.fail("testIterator_mg189591_rv206557 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206353() throws Exception {
        Attribute __DSPOT_o_59451 = new Attribute("KX[_x>b4pDi]xta*3uW+", "oEe.<[*i;;+{t*6/2nZc");
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        boolean o_testIterator_mg189593_mg206353__30 = __DSPOT_attribute_55732.equals(__DSPOT_o_59451);
        Assert.assertFalse(o_testIterator_mg189593_mg206353__30);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206430() throws Exception {
        String __DSPOT_val_59498 = "DDVPdw#_CAT[tpiFBk(&";
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        String o_testIterator_mg189593_mg206430__29 = __DSPOT_attribute_55732.setValue(__DSPOT_val_59498);
        Assert.assertEquals("qOA],k? [.TC,B)&u&D3", o_testIterator_mg189593_mg206430__29);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"DDVPdw#_CAT[tpiFBk(&amp;\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1759479233, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"DDVPdw#_CAT[tpiFBk(&amp;\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(1759479233, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206410() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        String o_testIterator_mg189593_mg206410__28 = __DSPOT_attribute_55732.html();
        Assert.assertEquals(")?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", o_testIterator_mg189593_mg206410__28);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg205096() throws Exception {
        boolean __DSPOT_value_58999 = true;
        String __DSPOT_key_58998 = "4$C#K1%j!fItLC@;&LuC";
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Attributes o_testIterator_mg189592_mg205096__29 = o_testIterator_mg189592__22.put(__DSPOT_key_58998, __DSPOT_value_58999);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\" 4$C#K1%j!fItLC@;&LuC", ((Attributes) (o_testIterator_mg189592_mg205096__29)).toString());
        Assert.assertEquals(1333518515, ((int) (((Attributes) (o_testIterator_mg189592_mg205096__29)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\" 4$C#K1%j!fItLC@;&LuC", ((Attributes) (a)).toString());
        Assert.assertEquals(1333518515, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\" 4$C#K1%j!fItLC@;&LuC", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(1333518515, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_rv205582() throws Exception {
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.asList();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg204434() throws Exception {
        Attributes __DSPOT_o_58845 = new Attributes();
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        boolean o_testIterator_mg189592_mg204434__29 = o_testIterator_mg189592__22.equals(__DSPOT_o_58845);
        Assert.assertFalse(o_testIterator_mg189592_mg204434__29);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593litString204365() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "xD;ne<#@Xw#$U0Kwt!x$", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"xD;ne<#@Xw#$U0Kwt!x$\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-415750970, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"xD;ne<#@Xw#$U0Kwt!x$\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-415750970, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg203405() throws Exception {
        String __DSPOT_key_58604 = "lyG 4>HX{#[y&gqc/#*{";
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        a.removeIgnoreCase(__DSPOT_key_58604);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206627() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.html();
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206422() throws Exception {
        String __DSPOT_key_59493 = "Toz|6fc.C6RC,qE&-,Py";
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        __DSPOT_attribute_55732.setKey(__DSPOT_key_59493);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" Toz|6fc.C6RC,qE&-,Py=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1905692109, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" Toz|6fc.C6RC,qE&-,Py=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(1905692109, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206688() throws Exception {
        String __DSPOT_key_59651 = "O)]=EHZ7[2.5=C&}u:z!";
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        String o_testIterator_mg189593_mg206688__29 = o_testIterator_mg189593__23.getIgnoreCase(__DSPOT_key_59651);
        Assert.assertEquals("", o_testIterator_mg189593_mg206688__29);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189591_rv206544_failAssert37() throws Exception {
        try {
            boolean __DSPOT_value_55729 = true;
            String __DSPOT_key_55728 = "wc_aqL*wEcQ|rQxu)i$6";
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                boolean __DSPOT_value_59567 = true;
                String __DSPOT_key_59566 = "y_}6b`LKs}i!C!;$K=!n";
                Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_15.put(__DSPOT_key_59566, __DSPOT_value_59567);
            }
            Iterator<Attribute> iterator = a.iterator();
            boolean o_testIterator_mg189591__12 = iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_192 = datas[i][0];
                attribute.getKey();
                String String_193 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg189591__22 = a.put(__DSPOT_key_55728, __DSPOT_value_55729);
            org.junit.Assert.fail("testIterator_mg189591_rv206544 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg189591_rv206478() throws Exception {
        boolean __DSPOT_value_55729 = true;
        String __DSPOT_key_55728 = "wc_aqL*wEcQ|rQxu)i$6";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Object __DSPOT_o_59528 = new Object();
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.equals(__DSPOT_o_59528);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189591__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_192 = datas[i][0];
            attribute.getKey();
            String String_193 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189591__22 = a.put(__DSPOT_key_55728, __DSPOT_value_55729);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" wc_aqL*wEcQ|rQxu)i$6", ((Attributes) (o_testIterator_mg189591__22)).toString());
        Assert.assertEquals(-1123403813, ((int) (((Attributes) (o_testIterator_mg189591__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" wc_aqL*wEcQ|rQxu)i$6", ((Attributes) (a)).toString());
        Assert.assertEquals(-1123403813, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206561() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        a.normalize();
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" )?[za+u/s.>g+r[:&&8j=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2054883230, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" )?[za+u/s.>g+r[:&&8j=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-2054883230, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206341() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attribute o_testIterator_mg189593_mg206341__28 = __DSPOT_attribute_55732.clone();
        Assert.assertEquals(")?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attribute) (o_testIterator_mg189593_mg206341__28)).toString());
        Assert.assertEquals(-66780646, ((int) (((Attribute) (o_testIterator_mg189593_mg206341__28)).hashCode())));
        Assert.assertEquals("qOA],k? [.TC,B)&u&D3", ((Attribute) (o_testIterator_mg189593_mg206341__28)).getValue());
        Assert.assertEquals(")?[Za+u/S.>G+r[:&&8J", ((Attribute) (o_testIterator_mg189593_mg206341__28)).getKey());
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_rv205618() throws Exception {
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.clone();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206482() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206482__28 = a.clone();
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593_mg206482__28)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593_mg206482__28)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_add206202() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg205285() throws Exception {
        String __DSPOT_key_59054 = "NSBmQb}#{Z=^5?!qjCZN";
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        o_testIterator_mg189592__22.remove(__DSPOT_key_59054);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg203531() throws Exception {
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            Object __DSPOT_o_58649 = new Object();
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.equals(__DSPOT_o_58649);
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206618() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206536() throws Exception {
        String __DSPOT_key_59562 = "m{w:LFDn&61V:B+1DgQ+";
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        boolean o_testIterator_mg189593_mg206536__29 = a.hasKeyIgnoreCase(__DSPOT_key_59562);
        Assert.assertFalse(o_testIterator_mg189593_mg206536__29);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206658() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        List<Attribute> o_testIterator_mg189593_mg206658__28 = o_testIterator_mg189593__23.asList();
        Assert.assertFalse(o_testIterator_mg189593_mg206658__28.isEmpty());
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206713() throws Exception {
        String __DSPOT_value_59667 = "E%nDy[w#8Rjis+>IQSxH";
        String __DSPOT_key_59666 = "T@&!2Cd8]?ZF0w<]t%#[";
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206713__30 = o_testIterator_mg189593__23.put(__DSPOT_key_59666, __DSPOT_value_59667);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" T@&!2Cd8]?ZF0w<]t%#[=\"E%nDy[w#8Rjis+>IQSxH\"", ((Attributes) (o_testIterator_mg189593_mg206713__30)).toString());
        Assert.assertEquals(1296545980, ((int) (((Attributes) (o_testIterator_mg189593_mg206713__30)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" T@&!2Cd8]?ZF0w<]t%#[=\"E%nDy[w#8Rjis+>IQSxH\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1296545980, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" T@&!2Cd8]?ZF0w<]t%#[=\"E%nDy[w#8Rjis+>IQSxH\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(1296545980, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_rv205950() throws Exception {
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            boolean __DSPOT_value_59274 = false;
            String __DSPOT_key_59272 = "9A.aG*o[m5?gm]v{1|4I";
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.put(__DSPOT_key_59272, __DSPOT_value_59274);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206637() throws Exception {
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String __DSPOT_val_59623 = "TV)jZ;*EC*4#oKHk@`O2";
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.setValue(__DSPOT_val_59623);
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"TV)jZ;*EC*4#oKHk@`O2\" Hello=\"TV)jZ;*EC*4#oKHk@`O2\" data-name=\"TV)jZ;*EC*4#oKHk@`O2\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(1360900156, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"TV)jZ;*EC*4#oKHk@`O2\" Hello=\"TV)jZ;*EC*4#oKHk@`O2\" data-name=\"TV)jZ;*EC*4#oKHk@`O2\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1360900156, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206575_mg233864() throws Exception {
        boolean __DSPOT_value_65929 = false;
        String __DSPOT_key_65928 = "wrH]i)B]#j}1.<@<Mzd.";
        Attribute __DSPOT_attribute_59589 = new Attribute("` 6cOo&|e^74L#k_WJRz", "_s Ab>9i2l`DQxw?We+:");
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206575__30 = a.put(__DSPOT_attribute_59589);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (o_testIterator_mg189593_mg206575__30)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (o_testIterator_mg189593_mg206575__30)).hashCode())));
        Attributes o_testIterator_mg189593_mg206575_mg233864__35 = o_testIterator_mg189593__23.put(__DSPOT_key_65928, __DSPOT_value_65929);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (o_testIterator_mg189593_mg206575_mg233864__35)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (o_testIterator_mg189593_mg206575_mg233864__35)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (a)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (o_testIterator_mg189593_mg206575__30)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (o_testIterator_mg189593_mg206575__30)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg205133_rv215325_failAssert65() throws Exception {
        try {
            String __DSPOT_value_59021 = "H:&_B,5[vQ]8b|Nj&wj^";
            String __DSPOT_key_59020 = "p!3eSa%z6^Tp=$$<-|Wa";
            String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
            String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                boolean __DSPOT_value_61248 = true;
                String __DSPOT_key_61247 = "uB]_XN|W]88QyOGXa@T.";
                Attributes __DSPOT_invoc_17 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_17.put(__DSPOT_key_61247, __DSPOT_value_61248);
            }
            Iterator<Attribute> iterator = a.iterator();
            boolean o_testIterator_mg189592__12 = iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_194 = datas[i][0];
                attribute.getKey();
                String String_195 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
            Attributes o_testIterator_mg189592_mg205133__29 = o_testIterator_mg189592__22.put(__DSPOT_key_59020, __DSPOT_value_59021);
            org.junit.Assert.fail("testIterator_mg189592_mg205133_rv215325 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg205133_rv214877() throws Exception {
        String __DSPOT_value_59021 = "H:&_B,5[vQ]8b|Nj&wj^";
        String __DSPOT_key_59020 = "p!3eSa%z6^Tp=$$<-|Wa";
        String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
        String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Object __DSPOT_o_61222 = new Object();
            Attributes __DSPOT_invoc_17 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_17.equals(__DSPOT_o_61222);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189592__12 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(-1634135007, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
        Attributes o_testIterator_mg189592_mg205133__29 = o_testIterator_mg189592__22.put(__DSPOT_key_59020, __DSPOT_value_59021);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\" p!3eSa%z6^Tp=$$<-|Wa=\"H:&amp;_B,5[vQ]8b|Nj&amp;wj^\"", ((Attributes) (o_testIterator_mg189592_mg205133__29)).toString());
        Assert.assertEquals(280568280, ((int) (((Attributes) (o_testIterator_mg189592_mg205133__29)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\" p!3eSa%z6^Tp=$$<-|Wa=\"H:&amp;_B,5[vQ]8b|Nj&amp;wj^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(280568280, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" !t&U^Hb}i#h+[Ew+0uZk=\"1Ol =qHuL=R$t{8#,}k#\" p!3eSa%z6^Tp=$$<-|Wa=\"H:&amp;_B,5[vQ]8b|Nj&amp;wj^\"", ((Attributes) (o_testIterator_mg189592__22)).toString());
        Assert.assertEquals(280568280, ((int) (((Attributes) (o_testIterator_mg189592__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206575_rv236463() throws Exception {
        Attribute __DSPOT_attribute_59589 = new Attribute("` 6cOo&|e^74L#k_WJRz", "_s Ab>9i2l`DQxw?We+:");
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            String __DSPOT_key_66644 = "oqFs5U9G[*hF&q+B`n&N";
            Attributes __DSPOT_invoc_18 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_18.removeIgnoreCase(__DSPOT_key_66644);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206575__30 = a.put(__DSPOT_attribute_59589);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (o_testIterator_mg189593_mg206575__30)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (o_testIterator_mg189593_mg206575__30)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (a)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206575_mg233907() throws Exception {
        String __DSPOT_value_65946 = "bE85)i{c%NH_Iw_Q#9N4";
        String __DSPOT_key_65945 = "X_]+xE0pAa#lb[f08HS9";
        Attribute __DSPOT_attribute_59589 = new Attribute("` 6cOo&|e^74L#k_WJRz", "_s Ab>9i2l`DQxw?We+:");
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206575__30 = a.put(__DSPOT_attribute_59589);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\"", ((Attributes) (o_testIterator_mg189593_mg206575__30)).toString());
        Assert.assertEquals(634476023, ((int) (((Attributes) (o_testIterator_mg189593_mg206575__30)).hashCode())));
        Attributes o_testIterator_mg189593_mg206575_mg233907__35 = o_testIterator_mg189593__23.put(__DSPOT_key_65945, __DSPOT_value_65946);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\" X_]+xE0pAa#lb[f08HS9=\"bE85)i{c%NH_Iw_Q#9N4\"", ((Attributes) (o_testIterator_mg189593_mg206575_mg233907__35)).toString());
        Assert.assertEquals(-1513575464, ((int) (((Attributes) (o_testIterator_mg189593_mg206575_mg233907__35)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\" X_]+xE0pAa#lb[f08HS9=\"bE85)i{c%NH_Iw_Q#9N4\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1513575464, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\" X_]+xE0pAa#lb[f08HS9=\"bE85)i{c%NH_Iw_Q#9N4\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1513575464, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ` 6cOo&|e^74L#k_WJRz=\"_s Ab>9i2l`DQxw?We+:\" X_]+xE0pAa#lb[f08HS9=\"bE85)i{c%NH_Iw_Q#9N4\"", ((Attributes) (o_testIterator_mg189593_mg206575__30)).toString());
        Assert.assertEquals(-1513575464, ((int) (((Attributes) (o_testIterator_mg189593_mg206575__30)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_rv235216() throws Exception {
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_19 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_19.normalize();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1715880798, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(889060698, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(889060698, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(889060698, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206575_rv236223_failAssert62() throws Exception {
        try {
            Attribute __DSPOT_attribute_59589 = new Attribute("` 6cOo&|e^74L#k_WJRz", "_s Ab>9i2l`DQxw?We+:");
            Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                String __DSPOT_value_66596 = "Wo#b:[zlt6I<!SDYFhix";
                String __DSPOT_key_66595 = "EQr&mB_azo=F{k[,# ,Y";
                Attributes __DSPOT_invoc_18 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_18.put(__DSPOT_key_66595, __DSPOT_value_66596);
            }
            Iterator<Attribute> iterator = a.iterator();
            boolean o_testIterator_mg189593__13 = iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_196 = datas[i][0];
                attribute.getKey();
                String String_197 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
            Attributes o_testIterator_mg189593_mg206575__30 = a.put(__DSPOT_attribute_59589);
            org.junit.Assert.fail("testIterator_mg189593_mg206575_rv236223 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg232697() throws Exception {
        String __DSPOT_key_65615 = "xO8c$].#8&L,s@&s{foU";
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        String o_testIterator_mg189593_mg206715_mg232697__35 = o_testIterator_mg189593__23.getIgnoreCase(__DSPOT_key_65615);
        Assert.assertEquals("", o_testIterator_mg189593_mg206715_mg232697__35);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg230145() throws Exception {
        String __DSPOT_key_64952 = "NOCgqcDPvUui<$VAF?&&";
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        __DSPOT_attribute_59669.setKey(__DSPOT_key_64952);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" NOCgqcDPvUui<$VAF?&&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2017969262, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" NOCgqcDPvUui<$VAF?&&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-2017969262, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" NOCgqcDPvUui<$VAF?&&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-2017969262, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg232541() throws Exception {
        Attributes __DSPOT_o_65582 = new Attributes();
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        boolean o_testIterator_mg189593_mg206715_mg232541__36 = o_testIterator_mg189593__23.equals(__DSPOT_o_65582);
        Assert.assertFalse(o_testIterator_mg189593_mg206715_mg232541__36);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189592_mg205133_rv215423_failAssert56() throws Exception {
        try {
            String __DSPOT_value_59021 = "H:&_B,5[vQ]8b|Nj&wj^";
            String __DSPOT_key_59020 = "p!3eSa%z6^Tp=$$<-|Wa";
            String __DSPOT_value_55731 = "1Ol =qHuL=R$t{8#,}k#";
            String __DSPOT_key_55730 = "!t&U^Hb}i#h+[Ew+0uZk";
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                Attribute __DSPOT_attribute_61253 = new Attribute("W9H]H(@rWy(sbKhr%D.a", "iZl:G*H^ABoVmcXO!CqP", new Attributes());
                Attributes __DSPOT_invoc_17 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_17.put(__DSPOT_attribute_61253);
            }
            Iterator<Attribute> iterator = a.iterator();
            boolean o_testIterator_mg189592__12 = iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_194 = datas[i][0];
                attribute.getKey();
                String String_195 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg189592__22 = a.put(__DSPOT_key_55730, __DSPOT_value_55731);
            Attributes o_testIterator_mg189592_mg205133__29 = o_testIterator_mg189592__22.put(__DSPOT_key_59020, __DSPOT_value_59021);
            org.junit.Assert.fail("testIterator_mg189592_mg205133_rv215423 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206341_mg218443() throws Exception {
        Attribute __DSPOT_attribute_61957 = new Attribute("4,[D3<({R!ReS;[ZzwtE", "vuW<Z(9XN3Luw8=@O!=z", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attribute o_testIterator_mg189593_mg206341__28 = __DSPOT_attribute_55732.clone();
        Assert.assertEquals(")?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attribute) (o_testIterator_mg189593_mg206341__28)).toString());
        Assert.assertEquals(-66780646, ((int) (((Attribute) (o_testIterator_mg189593_mg206341__28)).hashCode())));
        Assert.assertEquals("qOA],k? [.TC,B)&u&D3", ((Attribute) (o_testIterator_mg189593_mg206341__28)).getValue());
        Assert.assertEquals(")?[Za+u/S.>G+r[:&&8J", ((Attribute) (o_testIterator_mg189593_mg206341__28)).getKey());
        Attributes o_testIterator_mg189593_mg206341_mg218443__34 = a.put(__DSPOT_attribute_61957);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" 4,[D3<({R!ReS;[ZzwtE=\"vuW<Z(9XN3Luw8=@O!=z\"", ((Attributes) (o_testIterator_mg189593_mg206341_mg218443__34)).toString());
        Assert.assertEquals(-411469243, ((int) (((Attributes) (o_testIterator_mg189593_mg206341_mg218443__34)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" 4,[D3<({R!ReS;[ZzwtE=\"vuW<Z(9XN3Luw8=@O!=z\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-411469243, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" 4,[D3<({R!ReS;[ZzwtE=\"vuW<Z(9XN3Luw8=@O!=z\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-411469243, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(")?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attribute) (o_testIterator_mg189593_mg206341__28)).toString());
        Assert.assertEquals(-66780646, ((int) (((Attribute) (o_testIterator_mg189593_mg206341__28)).hashCode())));
        Assert.assertEquals("qOA],k? [.TC,B)&u&D3", ((Attribute) (o_testIterator_mg189593_mg206341__28)).getValue());
        Assert.assertEquals(")?[Za+u/S.>G+r[:&&8J", ((Attribute) (o_testIterator_mg189593_mg206341__28)).getKey());
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_rv234771() throws Exception {
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_19 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_19.asList();
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206565_mg222232() throws Exception {
        Attribute __DSPOT_attribute_62899 = new Attribute("[-yNK.1m[r?f=}W[j83t", "[HaKUz&F9PS8YF7VmNux", new Attributes());
        boolean __DSPOT_value_59581 = true;
        String __DSPOT_key_59580 = "c9fHu:[x}+RwG$M|&0s>";
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206565__30 = a.put(__DSPOT_key_59580, __DSPOT_value_59581);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" c9fHu:[x}+RwG$M|&0s>", ((Attributes) (o_testIterator_mg189593_mg206565__30)).toString());
        Assert.assertEquals(1139961937, ((int) (((Attributes) (o_testIterator_mg189593_mg206565__30)).hashCode())));
        Attributes o_testIterator_mg189593_mg206565_mg222232__36 = o_testIterator_mg189593_mg206565__30.put(__DSPOT_attribute_62899);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" c9fHu:[x}+RwG$M|&0s> [-yNK.1m[r?f=}W[j83t=\"[HaKUz&amp;F9PS8YF7VmNux\"", ((Attributes) (o_testIterator_mg189593_mg206565_mg222232__36)).toString());
        Assert.assertEquals(-620001437, ((int) (((Attributes) (o_testIterator_mg189593_mg206565_mg222232__36)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" c9fHu:[x}+RwG$M|&0s> [-yNK.1m[r?f=}W[j83t=\"[HaKUz&amp;F9PS8YF7VmNux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-620001437, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" c9fHu:[x}+RwG$M|&0s> [-yNK.1m[r?f=}W[j83t=\"[HaKUz&amp;F9PS8YF7VmNux\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-620001437, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" c9fHu:[x}+RwG$M|&0s> [-yNK.1m[r?f=}W[j83t=\"[HaKUz&amp;F9PS8YF7VmNux\"", ((Attributes) (o_testIterator_mg189593_mg206565__30)).toString());
        Assert.assertEquals(-620001437, ((int) (((Attributes) (o_testIterator_mg189593_mg206565__30)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg230381() throws Exception {
        String __DSPOT_val_65059 = "4O;R!iaEDSATHBF!t7hT";
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        String o_testIterator_mg189593_mg206715_mg230381__35 = __DSPOT_attribute_55732.setValue(__DSPOT_val_65059);
        Assert.assertEquals("qOA],k? [.TC,B)&u&D3", o_testIterator_mg189593_mg206715_mg230381__35);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"4O;R!iaEDSATHBF!t7hT\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-575797489, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"4O;R!iaEDSATHBF!t7hT\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-575797489, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"4O;R!iaEDSATHBF!t7hT\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-575797489, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg230238() throws Exception {
        Object __DSPOT_o_65002 = new Object();
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        boolean o_testIterator_mg189593_mg206715_mg230238__36 = __DSPOT_attribute_55732.equals(__DSPOT_o_65002);
        Assert.assertFalse(o_testIterator_mg189593_mg206715_mg230238__36);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206571_mg217750() throws Exception {
        Attribute __DSPOT_o_61661 = new Attribute("q2)?JBz_(4LPQEdO^Aj/", "`@iPsK&^NpcOz1O_p1hF", new Attributes());
        String __DSPOT_value_59586 = "Ktk7V,&Wa]ADe6-{aS.K";
        String __DSPOT_key_59584 = ",2m##8^wz6smD[c[,Ovq";
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206571__30 = a.put(__DSPOT_key_59584, __DSPOT_value_59586);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ,2m##8^wz6smD[c[,Ovq=\"Ktk7V,&amp;Wa]ADe6-{aS.K\"", ((Attributes) (o_testIterator_mg189593_mg206571__30)).toString());
        Assert.assertEquals(-1743763193, ((int) (((Attributes) (o_testIterator_mg189593_mg206571__30)).hashCode())));
        boolean o_testIterator_mg189593_mg206571_mg217750__36 = __DSPOT_attribute_55732.equals(__DSPOT_o_61661);
        Assert.assertFalse(o_testIterator_mg189593_mg206571_mg217750__36);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ,2m##8^wz6smD[c[,Ovq=\"Ktk7V,&amp;Wa]ADe6-{aS.K\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1743763193, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ,2m##8^wz6smD[c[,Ovq=\"Ktk7V,&amp;Wa]ADe6-{aS.K\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1743763193, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" ,2m##8^wz6smD[c[,Ovq=\"Ktk7V,&amp;Wa]ADe6-{aS.K\"", ((Attributes) (o_testIterator_mg189593_mg206571__30)).toString());
        Assert.assertEquals(-1743763193, ((int) (((Attributes) (o_testIterator_mg189593_mg206571__30)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg232801() throws Exception {
        String __DSPOT_key_65640 = "b1sT0b7OBo}%c[DAkM[v";
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        boolean o_testIterator_mg189593_mg206715_mg232801__35 = o_testIterator_mg189593__23.hasKeyIgnoreCase(__DSPOT_key_65640);
        Assert.assertFalse(o_testIterator_mg189593_mg206715_mg232801__35);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg230984() throws Exception {
        Attribute __DSPOT_attribute_65245 = new Attribute("T+ktFb!PnYAZ8/mu7k1%", "sJ4]TQU$[tFn0:Z,ra_x", new Attributes());
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(13416610, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-1573717670, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715_mg230984__37 = a.put(__DSPOT_attribute_65245);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\" T+ktFb!PnYAZ8/mu7k1%=\"sJ4]TQU$[tFn0:Z,ra_x\"", ((Attributes) (o_testIterator_mg189593_mg206715_mg230984__37)).toString());
        Assert.assertEquals(1336644022, ((int) (((Attributes) (o_testIterator_mg189593_mg206715_mg230984__37)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\" T+ktFb!PnYAZ8/mu7k1%=\"sJ4]TQU$[tFn0:Z,ra_x\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1336644022, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\" T+ktFb!PnYAZ8/mu7k1%=\"sJ4]TQU$[tFn0:Z,ra_x\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(1336644022, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\" T+ktFb!PnYAZ8/mu7k1%=\"sJ4]TQU$[tFn0:Z,ra_x\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(1336644022, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg189593_mg206715_mg231995() throws Exception {
        Attribute __DSPOT_attribute_59669 = new Attribute("+%&G)ZP|c@7z9yh0s2{&", "}k}TCbbL{mE*X5Q<Dy7H", new Attributes());
        Attribute __DSPOT_attribute_55732 = new Attribute(")?[Za+u/S.>G+r[:&&8J", "qOA],k? [.TC,B)&u&D3", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIterator_mg189593__13 = iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String __DSPOT_val_65474 = "w<$oVzhBfdztJBK%ZkfZ";
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.setValue(__DSPOT_val_65474);
        }
        Attributes o_testIterator_mg189593__23 = a.put(__DSPOT_attribute_55732);
        Assert.assertEquals(" Tot=\"w<$oVzhBfdztJBK%ZkfZ\" Hello=\"w<$oVzhBfdztJBK%ZkfZ\" data-name=\"w<$oVzhBfdztJBK%ZkfZ\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(1039385334, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
        Attributes o_testIterator_mg189593_mg206715__31 = o_testIterator_mg189593__23.put(__DSPOT_attribute_59669);
        Assert.assertEquals(" Tot=\"w<$oVzhBfdztJBK%ZkfZ\" Hello=\"w<$oVzhBfdztJBK%ZkfZ\" data-name=\"w<$oVzhBfdztJBK%ZkfZ\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593_mg206715__31)).toString());
        Assert.assertEquals(-2056996434, ((int) (((Attributes) (o_testIterator_mg189593_mg206715__31)).hashCode())));
        Assert.assertEquals(" Tot=\"w<$oVzhBfdztJBK%ZkfZ\" Hello=\"w<$oVzhBfdztJBK%ZkfZ\" data-name=\"w<$oVzhBfdztJBK%ZkfZ\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2056996434, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"w<$oVzhBfdztJBK%ZkfZ\" Hello=\"w<$oVzhBfdztJBK%ZkfZ\" data-name=\"w<$oVzhBfdztJBK%ZkfZ\" )?[Za+u/S.>G+r[:&&8J=\"qOA],k? [.TC,B)&amp;u&amp;D3\" +%&G)ZP|c@7z9yh0s2{&=\"}k}TCbbL{mE*X5Q<Dy7H\"", ((Attributes) (o_testIterator_mg189593__23)).toString());
        Assert.assertEquals(-2056996434, ((int) (((Attributes) (o_testIterator_mg189593__23)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty__5 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty__5);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245150() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245150__5 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245150__5);
        String o_testIteratorEmpty_mg245150__6 = a.html();
        Assert.assertEquals("", o_testIteratorEmpty_mg245150__6);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245150__5);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245144() throws Exception {
        Attributes __DSPOT_o_69140 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245144__7 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245144__7);
        boolean o_testIteratorEmpty_mg245144__8 = a.equals(__DSPOT_o_69140);
        Assert.assertTrue(o_testIteratorEmpty_mg245144__8);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245144__7);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155() throws Exception {
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245155__8);
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245155__8);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245154() throws Exception {
        String __DSPOT_value_69148 = "V[S`R>8(!8d)8VhOF7jX";
        String __DSPOT_key_69147 = "%u0FanN0LG4y_{,lq.hI";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245154__7 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245154__7);
        Attributes o_testIteratorEmpty_mg245154__8 = a.put(__DSPOT_key_69147, __DSPOT_value_69148);
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245154__7);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153() throws Exception {
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245153__7);
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245153__7);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245142() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245142__5 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245142__5);
        Attributes o_testIteratorEmpty_mg245142__6 = a.clone();
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg245142__6)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg245142__6)).hashCode())));
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245142__5);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245141() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245141__5 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245141__5);
        List<Attribute> o_testIteratorEmpty_mg245141__6 = a.asList();
        Assert.assertTrue(o_testIteratorEmpty_mg245141__6.isEmpty());
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245141__5);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245152() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245152__5 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245152__5);
        a.normalize();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245152__5);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245148() throws Exception {
        String __DSPOT_key_69144 = "S!}D%(F*:{o@mFf^-Ku%";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245148__6 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245148__6);
        boolean o_testIteratorEmpty_mg245148__7 = a.hasKeyIgnoreCase(__DSPOT_key_69144);
        Assert.assertFalse(o_testIteratorEmpty_mg245148__7);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245148__6);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245146() throws Exception {
        String __DSPOT_key_69142 = "hxVu77tA{P,_{3Z&a:#)";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245146__6 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245146__6);
        String o_testIteratorEmpty_mg245146__7 = a.getIgnoreCase(__DSPOT_key_69142);
        Assert.assertEquals("", o_testIteratorEmpty_mg245146__7);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245146__6);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245157() throws Exception {
        String __DSPOT_key_69151 = "!ljjx5uO3s!i<s;s:Z>l";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245157__6 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245157__6);
        a.removeIgnoreCase(__DSPOT_key_69151);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245157__6);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245156() throws Exception {
        String __DSPOT_key_69150 = "YLoE<1cr2c2GRM:#]4RJ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245156__6 = iterator.hasNext();
        Assert.assertFalse(o_testIteratorEmpty_mg245156__6);
        a.remove(__DSPOT_key_69150);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testIteratorEmpty_mg245156__6);
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155_mg246015() throws Exception {
        String __DSPOT_key_69493 = "3HEwV<Gy89uF#06`Vpv!";
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        a.remove(__DSPOT_key_69493);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155_mg245749() throws Exception {
        Object __DSPOT_o_69319 = new Object();
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        boolean o_testIteratorEmpty_mg245155_mg245749__16 = __DSPOT_attribute_69149.equals(__DSPOT_o_69319);
        Assert.assertFalse(o_testIteratorEmpty_mg245155_mg245749__16);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg245543() throws Exception {
        String __DSPOT_key_69216 = "xvW]Bw3jAEO#VmDZ(uOT";
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        String o_testIteratorEmpty_mg245153_mg245543__14 = a.getIgnoreCase(__DSPOT_key_69216);
        Assert.assertEquals("", o_testIteratorEmpty_mg245153_mg245543__14);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg245564() throws Exception {
        String __DSPOT_key_69229 = ". 7!,+UQDxq@M[neA?#7";
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        boolean o_testIteratorEmpty_mg245153_mg245564__14 = a.hasKeyIgnoreCase(__DSPOT_key_69229);
        Assert.assertFalse(o_testIteratorEmpty_mg245153_mg245564__14);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg245486() throws Exception {
        Attributes __DSPOT_o_69193 = new Attributes();
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        boolean o_testIteratorEmpty_mg245153_mg245486__15 = a.equals(__DSPOT_o_69193);
        Assert.assertFalse(o_testIteratorEmpty_mg245153_mg245486__15);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg245800() throws Exception {
        String __DSPOT_key_69350 = "k,]L{-O;d+.@`u,FG($C";
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        a.remove(__DSPOT_key_69350);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg245623() throws Exception {
        boolean __DSPOT_value_69282 = false;
        String __DSPOT_key_69281 = "Ua6?KFee7sM%!2sN}jEk";
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg245153_mg245623__15 = a.put(__DSPOT_key_69281, __DSPOT_value_69282);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153_mg245623__15)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153_mg245623__15)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155litString245265() throws Exception {
        Attribute __DSPOT_attribute_69149 = new Attribute("\n", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" =\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-1229988063, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        Assert.assertEquals(" =\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1229988063, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245154_mg245534() throws Exception {
        String __DSPOT_value_69148 = "V[S`R>8(!8d)8VhOF7jX";
        String __DSPOT_key_69147 = "%u0FanN0LG4y_{,lq.hI";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245154__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245154__8 = a.put(__DSPOT_key_69147, __DSPOT_value_69148);
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg245154_mg245534__13 = a.clone();
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154_mg245534__13)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154_mg245534__13)).hashCode())));
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (a)).toString());
        Assert.assertEquals(694308640, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(694308640, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_add245138_mg245437() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_add245138__5 = iterator.hasNext();
        a.normalize();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245154_mg245778() throws Exception {
        String __DSPOT_value_69148 = "V[S`R>8(!8d)8VhOF7jX";
        String __DSPOT_key_69147 = "%u0FanN0LG4y_{,lq.hI";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245154__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245154__8 = a.put(__DSPOT_key_69147, __DSPOT_value_69148);
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
        a.toString();
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245154_mg245973() throws Exception {
        String __DSPOT_value_69148 = "V[S`R>8(!8d)8VhOF7jX";
        String __DSPOT_key_69147 = "%u0FanN0LG4y_{,lq.hI";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245154__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245154__8 = a.put(__DSPOT_key_69147, __DSPOT_value_69148);
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
        o_testIteratorEmpty_mg245154__8.normalize();
        Assert.assertEquals(" %u0fann0lg4y_{,lq.hi=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (a)).toString());
        Assert.assertEquals(2053632034, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" %u0fann0lg4y_{,lq.hi=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(2053632034, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_add245139_mg245581() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_add245139__5 = iterator.hasNext();
        Attributes o_testIteratorEmpty_add245139_mg245581__8 = a.clone();
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_add245139_mg245581__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_add245139_mg245581__8)).hashCode())));
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155_mg245813() throws Exception {
        String __DSPOT_val_69360 = "*6d-ey:5Sc]9guV[P;el";
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        String o_testIteratorEmpty_mg245155_mg245813__15 = __DSPOT_attribute_69149.setValue(__DSPOT_val_69360);
        Assert.assertEquals("8Sd[j%^vc|G8|FD#^ME^", o_testIteratorEmpty_mg245155_mg245813__15);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"*6d-ey:5Sc]9guV[P;el\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1894377869, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"*6d-ey:5Sc]9guV[P;el\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(1894377869, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155_mg245799() throws Exception {
        String __DSPOT_key_69349 = "o#wJ<o%RDqk NgaqpXW:";
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        __DSPOT_attribute_69149.setKey(__DSPOT_key_69349);
        Assert.assertEquals(" o#wJ<o%RDqk NgaqpXW:=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1258024990, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" o#wJ<o%RDqk NgaqpXW:=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(1258024990, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245154litString245275() throws Exception {
        String __DSPOT_value_69148 = "Ct3GkE<1XI[z$cm.%TX8";
        String __DSPOT_key_69147 = "%u0FanN0LG4y_{,lq.hI";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245154__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245154__8 = a.put(__DSPOT_key_69147, __DSPOT_value_69148);
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"Ct3GkE<1XI[z$cm.%TX8\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-68849827, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"Ct3GkE<1XI[z$cm.%TX8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-68849827, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155litString245323() throws Exception {
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(662036258, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(662036258, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155_mg245739() throws Exception {
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        Attribute o_testIteratorEmpty_mg245155_mg245739__14 = __DSPOT_attribute_69149.clone();
        Assert.assertEquals("&[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attribute) (o_testIteratorEmpty_mg245155_mg245739__14)).toString());
        Assert.assertEquals(-1176592097, ((int) (((Attribute) (o_testIteratorEmpty_mg245155_mg245739__14)).hashCode())));
        Assert.assertEquals("8Sd[j%^vc|G8|FD#^ME^", ((Attribute) (o_testIteratorEmpty_mg245155_mg245739__14)).getValue());
        Assert.assertEquals("&[/g*]jE)ym/q;xObu((", ((Attribute) (o_testIteratorEmpty_mg245155_mg245739__14)).getKey());
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"8Sd[j%^vc|G8|FD#^ME^\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(-597505438, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155litString245328() throws Exception {
        Attribute __DSPOT_attribute_69149 = new Attribute("&[/g*]jE)ym/q;xObu((", "\n", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"\n\"", ((Attributes) (o_testIteratorEmpty_mg245155__9)).toString());
        Assert.assertEquals(662334168, ((int) (((Attributes) (o_testIteratorEmpty_mg245155__9)).hashCode())));
        Assert.assertEquals(" &[/g*]jE)ym/q;xObu((=\"\n\"", ((Attributes) (a)).toString());
        Assert.assertEquals(662334168, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245159_remove245812() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245159__5 = iterator.hasNext();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245155litString245263_failAssert77() throws Exception {
        try {
            Attribute __DSPOT_attribute_69149 = new Attribute("", "8Sd[j%^vc|G8|FD#^ME^", new Attributes());
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            boolean o_testIteratorEmpty_mg245155__8 = iterator.hasNext();
            Attributes o_testIteratorEmpty_mg245155__9 = a.put(__DSPOT_attribute_69149);
            org.junit.Assert.fail("testIteratorEmpty_mg245155litString245263 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg245930() throws Exception {
        Object __DSPOT_o_69440 = new Object();
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        boolean o_testIteratorEmpty_mg245153_mg245930__15 = o_testIteratorEmpty_mg245153__8.equals(__DSPOT_o_69440);
        Assert.assertFalse(o_testIteratorEmpty_mg245153_mg245930__15);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg246006() throws Exception {
        boolean __DSPOT_value_69489 = true;
        String __DSPOT_key_69488 = "fI&QBi.7Gy;?Bx2f4v[=";
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg245153_mg246006__15 = o_testIteratorEmpty_mg245153__8.put(__DSPOT_key_69488, __DSPOT_value_69489);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW fI&QBi.7Gy;?Bx2f4v[=", ((Attributes) (o_testIteratorEmpty_mg245153_mg246006__15)).toString());
        Assert.assertEquals(-1401839709, ((int) (((Attributes) (o_testIteratorEmpty_mg245153_mg246006__15)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW fI&QBi.7Gy;?Bx2f4v[=", ((Attributes) (a)).toString());
        Assert.assertEquals(-1401839709, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW fI&QBi.7Gy;?Bx2f4v[=", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1401839709, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245154_mg245546() throws Exception {
        String __DSPOT_value_69148 = "V[S`R>8(!8d)8VhOF7jX";
        String __DSPOT_key_69147 = "%u0FanN0LG4y_{,lq.hI";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245154__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245154__8 = a.put(__DSPOT_key_69147, __DSPOT_value_69148);
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
        Map<String, String> o_testIteratorEmpty_mg245154_mg245546__13 = a.dataset();
        Assert.assertTrue(o_testIteratorEmpty_mg245154_mg245546__13.isEmpty());
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245153_mg245813() throws Exception {
        String __DSPOT_key_69361 = "8z/Xvb>r5Y.gp17PaKPc";
        boolean __DSPOT_value_69146 = true;
        String __DSPOT_key_69145 = "Qh7El3?2fwJ)iy^lV(SW";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245153__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245153__8 = a.put(__DSPOT_key_69145, __DSPOT_value_69146);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
        a.removeIgnoreCase(__DSPOT_key_69361);
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (a)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Qh7El3?2fwJ)iy^lV(SW", ((Attributes) (o_testIteratorEmpty_mg245153__8)).toString());
        Assert.assertEquals(-1498155964, ((int) (((Attributes) (o_testIteratorEmpty_mg245153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_add245139_mg245574() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_add245139__5 = iterator.hasNext();
        List<Attribute> o_testIteratorEmpty_add245139_mg245574__8 = a.asList();
        Assert.assertTrue(o_testIteratorEmpty_add245139_mg245574__8.isEmpty());
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg245154_mg245862() throws Exception {
        String __DSPOT_value_69148 = "V[S`R>8(!8d)8VhOF7jX";
        String __DSPOT_key_69147 = "%u0FanN0LG4y_{,lq.hI";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_mg245154__7 = iterator.hasNext();
        Attributes o_testIteratorEmpty_mg245154__8 = a.put(__DSPOT_key_69147, __DSPOT_value_69148);
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
        List<Attribute> o_testIteratorEmpty_mg245154_mg245862__13 = o_testIteratorEmpty_mg245154__8.asList();
        Assert.assertFalse(o_testIteratorEmpty_mg245154_mg245862__13.isEmpty());
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" %u0FanN0LG4y_{,lq.hI=\"V[S`R>8(!8d)8VhOF7jX\"", ((Attributes) (o_testIteratorEmpty_mg245154__8)).toString());
        Assert.assertEquals(-442431486, ((int) (((Attributes) (o_testIteratorEmpty_mg245154__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_add245138_add245327_mg256705() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_add245138__5 = iterator.hasNext();
        Attributes o_testIteratorEmpty_add245138_add245327_mg256705__8 = a.clone();
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_add245138_add245327_mg256705__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_add245138_add245327_mg256705__8)).hashCode())));
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_add245139_add245427_mg257362() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_add245139__5 = iterator.hasNext();
        List<Attribute> o_testIteratorEmpty_add245139_add245427_mg257362__8 = a.asList();
        Assert.assertTrue(o_testIteratorEmpty_add245139_add245427_mg257362__8.isEmpty());
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_add245138_add245324_add248411() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        boolean o_testIteratorEmpty_add245138__5 = iterator.hasNext();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive__3)).hashCode())));
        Attributes o_removeCaseSensitive__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive__4)).hashCode())));
        Attributes o_removeCaseSensitive__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive__5)).hashCode())));
        Attributes o_removeCaseSensitive__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive__6)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive__6)).hashCode())));
        Attributes o_removeCaseSensitive__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive__7)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive__7)).hashCode())));
        int o_removeCaseSensitive__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive__11)));
        boolean o_removeCaseSensitive__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive__12);
        boolean o_removeCaseSensitive__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive__13);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive__3)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive__11)));
        Assert.assertTrue(o_removeCaseSensitive__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76392() throws Exception {
        String __DSPOT_key_21097 = "$m*l#pRTXZD2pO-1%j&N";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv76392__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv76392__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("tot", "one");
        Attributes o_removeCaseSensitive_rv76392__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76392__8)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv76392__9 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76392__9)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76392__10 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76392__10)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__10)).hashCode())));
        int o_removeCaseSensitive_rv76392__11 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76392__11)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76392__14 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76392__14)));
        boolean o_removeCaseSensitive_rv76392__15 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76392__15);
        boolean o_removeCaseSensitive_rv76392__16 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76392__16);
        __DSPOT_invoc_4.remove(__DSPOT_key_21097);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76392__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76392__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76392__9)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76392__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76392__10)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76392__11)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76392__14)));
        Assert.assertTrue(o_removeCaseSensitive_rv76392__15);
        Assert.assertFalse(o_removeCaseSensitive_rv76392__16);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76370() throws Exception {
        Attribute __DSPOT_attribute_21083 = new Attribute("/qa&3%[(&sh`8V(S1-W0", "&F26@e&)8|yn8bFLCn0q");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_removeCaseSensitive_rv76370__8 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv76370__8)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv76370__9 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76370__9)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76370__10 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76370__10)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__10)).hashCode())));
        Attributes o_removeCaseSensitive_rv76370__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76370__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__11)).hashCode())));
        int o_removeCaseSensitive_rv76370__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76370__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76370__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76370__15)));
        boolean o_removeCaseSensitive_rv76370__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76370__16);
        boolean o_removeCaseSensitive_rv76370__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76370__17);
        Attributes o_removeCaseSensitive_rv76370__18 = __DSPOT_invoc_3.put(__DSPOT_attribute_21083);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" /qa&3%[(&sh`8V(S1-W0=\"&amp;F26@e&amp;)8|yn8bFLCn0q\"", ((Attributes) (o_removeCaseSensitive_rv76370__18)).toString());
        Assert.assertEquals(-238526450, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__18)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" /qa&3%[(&sh`8V(S1-W0=\"&amp;F26@e&amp;)8|yn8bFLCn0q\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-238526450, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" /qa&3%[(&sh`8V(S1-W0=\"&amp;F26@e&amp;)8|yn8bFLCn0q\"", ((Attributes) (o_removeCaseSensitive_rv76370__8)).toString());
        Assert.assertEquals(-238526450, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" /qa&3%[(&sh`8V(S1-W0=\"&amp;F26@e&amp;)8|yn8bFLCn0q\"", ((Attributes) (o_removeCaseSensitive_rv76370__9)).toString());
        Assert.assertEquals(-238526450, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" /qa&3%[(&sh`8V(S1-W0=\"&amp;F26@e&amp;)8|yn8bFLCn0q\"", ((Attributes) (o_removeCaseSensitive_rv76370__10)).toString());
        Assert.assertEquals(-238526450, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__10)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" /qa&3%[(&sh`8V(S1-W0=\"&amp;F26@e&amp;)8|yn8bFLCn0q\"", ((Attributes) (o_removeCaseSensitive_rv76370__11)).toString());
        Assert.assertEquals(-238526450, ((int) (((Attributes) (o_removeCaseSensitive_rv76370__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76370__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76370__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv76370__16);
        Assert.assertFalse(o_removeCaseSensitive_rv76370__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivelitString76217() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitivelitString76217__3 = a.put("Tot", "");
        Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_removeCaseSensitivelitString76217__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__3)).hashCode())));
        Attributes o_removeCaseSensitivelitString76217__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivelitString76217__4)).toString());
        Assert.assertEquals(-206717624, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__4)).hashCode())));
        Attributes o_removeCaseSensitivelitString76217__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString76217__5)).toString());
        Assert.assertEquals(472696695, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__5)).hashCode())));
        Attributes o_removeCaseSensitivelitString76217__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString76217__6)).toString());
        Assert.assertEquals(-667462934, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__6)).hashCode())));
        Attributes o_removeCaseSensitivelitString76217__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76217__7)).toString());
        Assert.assertEquals(-895488360, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__7)).hashCode())));
        int o_removeCaseSensitivelitString76217__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString76217__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitivelitString76217__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString76217__11)));
        boolean o_removeCaseSensitivelitString76217__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitivelitString76217__12);
        boolean o_removeCaseSensitivelitString76217__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitivelitString76217__13);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76217__3)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76217__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76217__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76217__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76217__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString76217__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString76217__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString76217__11)));
        Assert.assertTrue(o_removeCaseSensitivelitString76217__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76380() throws Exception {
        Object __DSPOT_o_21087 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv76380__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv76380__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("tot", "one");
        Attributes o_removeCaseSensitive_rv76380__9 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76380__9)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76380__10 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76380__10)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__10)).hashCode())));
        Attributes o_removeCaseSensitive_rv76380__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76380__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__11)).hashCode())));
        int o_removeCaseSensitive_rv76380__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76380__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76380__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76380__15)));
        boolean o_removeCaseSensitive_rv76380__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76380__16);
        boolean o_removeCaseSensitive_rv76380__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76380__17);
        boolean o_removeCaseSensitive_rv76380__18 = __DSPOT_invoc_4.equals(__DSPOT_o_21087);
        Assert.assertFalse(o_removeCaseSensitive_rv76380__18);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76380__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76380__9)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76380__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__10)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76380__11)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76380__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76380__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76380__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv76380__16);
        Assert.assertFalse(o_removeCaseSensitive_rv76380__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76361() throws Exception {
        String __DSPOT_key_21076 = "|G!@OAigUUy>3|-.)1y.";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_removeCaseSensitive_rv76361__7 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv76361__7)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__7)).hashCode())));
        Attributes o_removeCaseSensitive_rv76361__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76361__8)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv76361__9 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76361__9)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76361__10 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76361__10)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__10)).hashCode())));
        int o_removeCaseSensitive_rv76361__11 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76361__11)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76361__14 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76361__14)));
        boolean o_removeCaseSensitive_rv76361__15 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76361__15);
        boolean o_removeCaseSensitive_rv76361__16 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76361__16);
        String o_removeCaseSensitive_rv76361__17 = __DSPOT_invoc_3.getIgnoreCase(__DSPOT_key_21076);
        Assert.assertEquals("", o_removeCaseSensitive_rv76361__17);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76361__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76361__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76361__9)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76361__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76361__10)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76361__11)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76361__14)));
        Assert.assertTrue(o_removeCaseSensitive_rv76361__15);
        Assert.assertFalse(o_removeCaseSensitive_rv76361__16);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76393() throws Exception {
        String __DSPOT_key_21098 = "Quir4?m)Y%q5`RW4 syB";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv76393__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv76393__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("tot", "one");
        Attributes o_removeCaseSensitive_rv76393__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76393__8)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv76393__9 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76393__9)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76393__10 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76393__10)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__10)).hashCode())));
        int o_removeCaseSensitive_rv76393__11 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76393__11)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76393__14 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76393__14)));
        boolean o_removeCaseSensitive_rv76393__15 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76393__15);
        boolean o_removeCaseSensitive_rv76393__16 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76393__16);
        __DSPOT_invoc_4.removeIgnoreCase(__DSPOT_key_21098);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76393__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76393__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76393__9)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76393__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76393__10)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76393__11)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76393__14)));
        Assert.assertTrue(o_removeCaseSensitive_rv76393__15);
        Assert.assertFalse(o_removeCaseSensitive_rv76393__16);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76336() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76336__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76336__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__3)).hashCode())));
        Attributes o_removeCaseSensitive_mg76336__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76336__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__4)).hashCode())));
        Attributes o_removeCaseSensitive_mg76336__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76336__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76336__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76336__6)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76336__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76336__7)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__7)).hashCode())));
        int o_removeCaseSensitive_mg76336__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_mg76336__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76336__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_mg76336__11)));
        boolean o_removeCaseSensitive_mg76336__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_mg76336__12);
        boolean o_removeCaseSensitive_mg76336__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_mg76336__13);
        Attributes o_removeCaseSensitive_mg76336__14 = a.clone();
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76336__14)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__14)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1491502383, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76336__3)).toString());
        Assert.assertEquals(-1491502383, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76336__4)).toString());
        Assert.assertEquals(-1491502383, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76336__5)).toString());
        Assert.assertEquals(-1491502383, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76336__6)).toString());
        Assert.assertEquals(-1491502383, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76336__7)).toString());
        Assert.assertEquals(-1491502383, ((int) (((Attributes) (o_removeCaseSensitive_mg76336__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_mg76336__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_mg76336__11)));
        Assert.assertTrue(o_removeCaseSensitive_mg76336__12);
        Assert.assertFalse(o_removeCaseSensitive_mg76336__13);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76335() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76335__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76335__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__3)).hashCode())));
        Attributes o_removeCaseSensitive_mg76335__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76335__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__4)).hashCode())));
        Attributes o_removeCaseSensitive_mg76335__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76335__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76335__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76335__6)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76335__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76335__7)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__7)).hashCode())));
        int o_removeCaseSensitive_mg76335__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_mg76335__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76335__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_mg76335__11)));
        boolean o_removeCaseSensitive_mg76335__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_mg76335__12);
        boolean o_removeCaseSensitive_mg76335__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_mg76335__13);
        List<Attribute> o_removeCaseSensitive_mg76335__14 = a.asList();
        Assert.assertFalse(o_removeCaseSensitive_mg76335__14.isEmpty());
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76335__3)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76335__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76335__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76335__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76335__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76335__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_mg76335__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_mg76335__11)));
        Assert.assertTrue(o_removeCaseSensitive_mg76335__12);
        Assert.assertFalse(o_removeCaseSensitive_mg76335__13);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76346() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76346__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76346__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__3)).hashCode())));
        Attributes o_removeCaseSensitive_mg76346__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76346__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__4)).hashCode())));
        Attributes o_removeCaseSensitive_mg76346__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76346__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76346__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76346__6)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76346__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76346__7)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__7)).hashCode())));
        int o_removeCaseSensitive_mg76346__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_mg76346__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76346__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_mg76346__11)));
        boolean o_removeCaseSensitive_mg76346__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_mg76346__12);
        boolean o_removeCaseSensitive_mg76346__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_mg76346__13);
        a.normalize();
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76346__3)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76346__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76346__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76346__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76346__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_mg76346__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_mg76346__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_mg76346__11)));
        Assert.assertTrue(o_removeCaseSensitive_mg76346__12);
        Assert.assertFalse(o_removeCaseSensitive_mg76346__13);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76389() throws Exception {
        boolean __DSPOT_value_21093 = true;
        String __DSPOT_key_21092 = "sdv:=9|F?wy-Q2Q@{x^.";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv76389__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv76389__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("tot", "one");
        Attributes o_removeCaseSensitive_rv76389__9 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76389__9)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76389__10 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76389__10)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__10)).hashCode())));
        Attributes o_removeCaseSensitive_rv76389__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76389__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__11)).hashCode())));
        int o_removeCaseSensitive_rv76389__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76389__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76389__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76389__15)));
        boolean o_removeCaseSensitive_rv76389__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76389__16);
        boolean o_removeCaseSensitive_rv76389__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76389__17);
        Attributes o_removeCaseSensitive_rv76389__18 = __DSPOT_invoc_4.put(__DSPOT_key_21092, __DSPOT_value_21093);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" sdv:=9|F?wy-Q2Q@{x^.", ((Attributes) (o_removeCaseSensitive_rv76389__18)).toString());
        Assert.assertEquals(1606848722, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__18)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" sdv:=9|F?wy-Q2Q@{x^.", ((Attributes) (a)).toString());
        Assert.assertEquals(1606848722, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" sdv:=9|F?wy-Q2Q@{x^.", ((Attributes) (o_removeCaseSensitive_rv76389__5)).toString());
        Assert.assertEquals(1606848722, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" sdv:=9|F?wy-Q2Q@{x^.", ((Attributes) (o_removeCaseSensitive_rv76389__9)).toString());
        Assert.assertEquals(1606848722, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" sdv:=9|F?wy-Q2Q@{x^.", ((Attributes) (o_removeCaseSensitive_rv76389__10)).toString());
        Assert.assertEquals(1606848722, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__10)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" sdv:=9|F?wy-Q2Q@{x^.", ((Attributes) (o_removeCaseSensitive_rv76389__11)).toString());
        Assert.assertEquals(1606848722, ((int) (((Attributes) (o_removeCaseSensitive_rv76389__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76389__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76389__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv76389__16);
        Assert.assertFalse(o_removeCaseSensitive_rv76389__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivelitString76220() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitivelitString76220__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivelitString76220__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__3)).hashCode())));
        Attributes o_removeCaseSensitivelitString76220__4 = a.put("Hello", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"one\"", ((Attributes) (o_removeCaseSensitivelitString76220__4)).toString());
        Assert.assertEquals(-1546328508, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__4)).hashCode())));
        Attributes o_removeCaseSensitivelitString76220__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString76220__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__5)).hashCode())));
        Attributes o_removeCaseSensitivelitString76220__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString76220__6)).toString());
        Assert.assertEquals(1972613361, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__6)).hashCode())));
        Attributes o_removeCaseSensitivelitString76220__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76220__7)).toString());
        Assert.assertEquals(-2067437339, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__7)).hashCode())));
        int o_removeCaseSensitivelitString76220__8 = a.size();
        Assert.assertEquals(4, ((int) (o_removeCaseSensitivelitString76220__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitivelitString76220__11 = a.size();
        Assert.assertEquals(2, ((int) (o_removeCaseSensitivelitString76220__11)));
        boolean o_removeCaseSensitivelitString76220__12 = a.hasKey("tot");
        Assert.assertFalse(o_removeCaseSensitivelitString76220__12);
        boolean o_removeCaseSensitivelitString76220__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitivelitString76220__13);
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76220__3)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__3)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76220__4)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__4)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76220__5)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__5)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76220__6)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__6)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76220__7)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_removeCaseSensitivelitString76220__7)).hashCode())));
        Assert.assertEquals(4, ((int) (o_removeCaseSensitivelitString76220__8)));
        Assert.assertEquals(2, ((int) (o_removeCaseSensitivelitString76220__11)));
        Assert.assertFalse(o_removeCaseSensitivelitString76220__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76433() throws Exception {
        Attribute __DSPOT_attribute_21122 = new Attribute("zW]0nQVMcJesMx.?-Sfh", "E-jNZg> B7R8U}]Y;/)#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv76433__6 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv76433__6)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv76433__7 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv76433__7)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__7)).hashCode())));
        Attributes o_removeCaseSensitive_rv76433__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76433__8)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__8)).hashCode())));
        Attributes __DSPOT_invoc_6 = a.put("hello", "There");
        Attributes o_removeCaseSensitive_rv76433__12 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76433__12)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__12)).hashCode())));
        int o_removeCaseSensitive_rv76433__13 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76433__13)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76433__16 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76433__16)));
        boolean o_removeCaseSensitive_rv76433__17 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76433__17);
        boolean o_removeCaseSensitive_rv76433__18 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76433__18);
        Attributes o_removeCaseSensitive_rv76433__19 = __DSPOT_invoc_6.put(__DSPOT_attribute_21122);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" zW]0nQVMcJesMx.?-Sfh=\"E-jNZg> B7R8U}]Y;/)#\"", ((Attributes) (o_removeCaseSensitive_rv76433__19)).toString());
        Assert.assertEquals(-631662373, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__19)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" zW]0nQVMcJesMx.?-Sfh=\"E-jNZg> B7R8U}]Y;/)#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-631662373, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" zW]0nQVMcJesMx.?-Sfh=\"E-jNZg> B7R8U}]Y;/)#\"", ((Attributes) (o_removeCaseSensitive_rv76433__6)).toString());
        Assert.assertEquals(-631662373, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" zW]0nQVMcJesMx.?-Sfh=\"E-jNZg> B7R8U}]Y;/)#\"", ((Attributes) (o_removeCaseSensitive_rv76433__7)).toString());
        Assert.assertEquals(-631662373, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" zW]0nQVMcJesMx.?-Sfh=\"E-jNZg> B7R8U}]Y;/)#\"", ((Attributes) (o_removeCaseSensitive_rv76433__8)).toString());
        Assert.assertEquals(-631662373, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" zW]0nQVMcJesMx.?-Sfh=\"E-jNZg> B7R8U}]Y;/)#\"", ((Attributes) (o_removeCaseSensitive_rv76433__12)).toString());
        Assert.assertEquals(-631662373, ((int) (((Attributes) (o_removeCaseSensitive_rv76433__12)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76433__13)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76433__16)));
        Assert.assertTrue(o_removeCaseSensitive_rv76433__17);
        Assert.assertFalse(o_removeCaseSensitive_rv76433__18);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76369() throws Exception {
        String __DSPOT_value_21082 = "D;T(G@kQW[I<KPCa!1[a";
        String __DSPOT_key_21081 = "5:yS_SV$Jsi/mS6NxZ#8";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_removeCaseSensitive_rv76369__8 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv76369__8)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv76369__9 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76369__9)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76369__10 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76369__10)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__10)).hashCode())));
        Attributes o_removeCaseSensitive_rv76369__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76369__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__11)).hashCode())));
        int o_removeCaseSensitive_rv76369__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76369__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76369__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76369__15)));
        boolean o_removeCaseSensitive_rv76369__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76369__16);
        boolean o_removeCaseSensitive_rv76369__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76369__17);
        Attributes o_removeCaseSensitive_rv76369__18 = __DSPOT_invoc_3.put(__DSPOT_key_21081, __DSPOT_value_21082);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 5:yS_SV$Jsi/mS6NxZ#8=\"D;T(G@kQW[I<KPCa!1[a\"", ((Attributes) (o_removeCaseSensitive_rv76369__18)).toString());
        Assert.assertEquals(-817780501, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__18)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 5:yS_SV$Jsi/mS6NxZ#8=\"D;T(G@kQW[I<KPCa!1[a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-817780501, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 5:yS_SV$Jsi/mS6NxZ#8=\"D;T(G@kQW[I<KPCa!1[a\"", ((Attributes) (o_removeCaseSensitive_rv76369__8)).toString());
        Assert.assertEquals(-817780501, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 5:yS_SV$Jsi/mS6NxZ#8=\"D;T(G@kQW[I<KPCa!1[a\"", ((Attributes) (o_removeCaseSensitive_rv76369__9)).toString());
        Assert.assertEquals(-817780501, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 5:yS_SV$Jsi/mS6NxZ#8=\"D;T(G@kQW[I<KPCa!1[a\"", ((Attributes) (o_removeCaseSensitive_rv76369__10)).toString());
        Assert.assertEquals(-817780501, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__10)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 5:yS_SV$Jsi/mS6NxZ#8=\"D;T(G@kQW[I<KPCa!1[a\"", ((Attributes) (o_removeCaseSensitive_rv76369__11)).toString());
        Assert.assertEquals(-817780501, ((int) (((Attributes) (o_removeCaseSensitive_rv76369__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76369__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76369__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv76369__16);
        Assert.assertFalse(o_removeCaseSensitive_rv76369__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76401() throws Exception {
        Attributes __DSPOT_o_21100 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv76401__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv76401__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv76401__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv76401__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("Hello", "There");
        Attributes o_removeCaseSensitive_rv76401__10 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76401__10)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__10)).hashCode())));
        Attributes o_removeCaseSensitive_rv76401__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76401__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__11)).hashCode())));
        int o_removeCaseSensitive_rv76401__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76401__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76401__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76401__15)));
        boolean o_removeCaseSensitive_rv76401__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76401__16);
        boolean o_removeCaseSensitive_rv76401__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76401__17);
        boolean o_removeCaseSensitive_rv76401__18 = __DSPOT_invoc_5.equals(__DSPOT_o_21100);
        Assert.assertFalse(o_removeCaseSensitive_rv76401__18);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76401__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76401__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76401__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__10)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76401__11)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76401__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76401__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76401__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv76401__16);
        Assert.assertFalse(o_removeCaseSensitive_rv76401__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivelitString76234() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitivelitString76234__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivelitString76234__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__3)).hashCode())));
        Attributes o_removeCaseSensitivelitString76234__4 = a.put("tot", "\n");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"\n\"", ((Attributes) (o_removeCaseSensitivelitString76234__4)).toString());
        Assert.assertEquals(-1792102175, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__4)).hashCode())));
        Attributes o_removeCaseSensitivelitString76234__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"\n\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString76234__5)).toString());
        Assert.assertEquals(-1112687856, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__5)).hashCode())));
        Attributes o_removeCaseSensitivelitString76234__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"\n\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString76234__6)).toString());
        Assert.assertEquals(2042119811, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__6)).hashCode())));
        Attributes o_removeCaseSensitivelitString76234__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"\n\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76234__7)).toString());
        Assert.assertEquals(349924785, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__7)).hashCode())));
        int o_removeCaseSensitivelitString76234__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString76234__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitivelitString76234__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString76234__11)));
        boolean o_removeCaseSensitivelitString76234__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitivelitString76234__12);
        boolean o_removeCaseSensitivelitString76234__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitivelitString76234__13);
        Assert.assertEquals(" tot=\"\n\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1367772591, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"\n\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76234__3)).toString());
        Assert.assertEquals(-1367772591, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__3)).hashCode())));
        Assert.assertEquals(" tot=\"\n\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76234__4)).toString());
        Assert.assertEquals(-1367772591, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__4)).hashCode())));
        Assert.assertEquals(" tot=\"\n\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76234__5)).toString());
        Assert.assertEquals(-1367772591, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__5)).hashCode())));
        Assert.assertEquals(" tot=\"\n\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76234__6)).toString());
        Assert.assertEquals(-1367772591, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__6)).hashCode())));
        Assert.assertEquals(" tot=\"\n\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString76234__7)).toString());
        Assert.assertEquals(-1367772591, ((int) (((Attributes) (o_removeCaseSensitivelitString76234__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString76234__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString76234__11)));
        Assert.assertTrue(o_removeCaseSensitivelitString76234__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76368() throws Exception {
        boolean __DSPOT_value_21080 = false;
        String __DSPOT_key_21079 = "6)*Qj>/R^^OT87KDdx+o";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_removeCaseSensitive_rv76368__8 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv76368__8)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv76368__9 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76368__9)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76368__10 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76368__10)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__10)).hashCode())));
        Attributes o_removeCaseSensitive_rv76368__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76368__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__11)).hashCode())));
        int o_removeCaseSensitive_rv76368__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76368__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76368__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76368__15)));
        boolean o_removeCaseSensitive_rv76368__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76368__16);
        boolean o_removeCaseSensitive_rv76368__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76368__17);
        Attributes o_removeCaseSensitive_rv76368__18 = __DSPOT_invoc_3.put(__DSPOT_key_21079, __DSPOT_value_21080);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76368__18)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__18)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76368__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76368__9)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76368__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__10)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76368__11)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76368__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76368__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76368__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv76368__16);
        Assert.assertFalse(o_removeCaseSensitive_rv76368__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv76384() throws Exception {
        String __DSPOT_key_21091 = "(%1b**r-o?TC4{/EDM#Z";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv76384__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv76384__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("tot", "one");
        Attributes o_removeCaseSensitive_rv76384__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76384__8)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv76384__9 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv76384__9)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv76384__10 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76384__10)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__10)).hashCode())));
        int o_removeCaseSensitive_rv76384__11 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76384__11)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv76384__14 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76384__14)));
        boolean o_removeCaseSensitive_rv76384__15 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv76384__15);
        boolean o_removeCaseSensitive_rv76384__16 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv76384__16);
        boolean o_removeCaseSensitive_rv76384__17 = __DSPOT_invoc_4.hasKeyIgnoreCase(__DSPOT_key_21091);
        Assert.assertFalse(o_removeCaseSensitive_rv76384__17);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76384__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76384__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76384__9)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv76384__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv76384__10)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv76384__11)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv76384__14)));
        Assert.assertTrue(o_removeCaseSensitive_rv76384__15);
        Assert.assertFalse(o_removeCaseSensitive_rv76384__16);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76349_mg106391() throws Exception {
        String __DSPOT_key_30338 = "7!n$}qBAM60sVFFO|QEw";
        Attribute __DSPOT_attribute_21070 = new Attribute("[;|*&Llg+?(tn$!qj(@V", "E7)bJ$}&t%Cmv8XD?{(/");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        int o_removeCaseSensitive_mg76349__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76349__13 = a.size();
        boolean o_removeCaseSensitive_mg76349__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76349__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76349__16 = a.put(__DSPOT_attribute_21070);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
        String o_removeCaseSensitive_mg76349_mg106391__38 = o_removeCaseSensitive_mg76349__5.getIgnoreCase(__DSPOT_key_30338);
        Assert.assertEquals("", o_removeCaseSensitive_mg76349_mg106391__38);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg98241() throws Exception {
        String __DSPOT_key_28003 = "XOKoQJp?&u^xe9`Hw^k_";
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        o_removeCaseSensitive_mg76348__5.removeIgnoreCase(__DSPOT_key_28003);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_add76320_mg122133() throws Exception {
        Attribute __DSPOT_attribute_35335 = new Attribute("[-Bp6t/+XiZ6a]_c(*XP", "]Q%&?v<p@=?Lx`)|5vO_", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_add76320__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_add76320__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_add76320__3)).hashCode())));
        Attributes o_removeCaseSensitive_add76320__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_add76320__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_add76320__4)).hashCode())));
        Attributes o_removeCaseSensitive_add76320__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_add76320__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_add76320__5)).hashCode())));
        Attributes o_removeCaseSensitive_add76320__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_add76320__6)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_add76320__6)).hashCode())));
        Attributes o_removeCaseSensitive_add76320__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_add76320__7)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_add76320__7)).hashCode())));
        Attributes o_removeCaseSensitive_add76320__8 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_add76320__8)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_add76320__8)).hashCode())));
        int o_removeCaseSensitive_add76320__9 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_add76320__12 = a.size();
        boolean o_removeCaseSensitive_add76320__13 = a.hasKey("tot");
        boolean o_removeCaseSensitive_add76320__14 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_add76320_mg122133__38 = o_removeCaseSensitive_add76320__7.put(__DSPOT_attribute_35335);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (o_removeCaseSensitive_add76320_mg122133__38)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (o_removeCaseSensitive_add76320_mg122133__38)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (o_removeCaseSensitive_add76320__3)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (o_removeCaseSensitive_add76320__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (o_removeCaseSensitive_add76320__4)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (o_removeCaseSensitive_add76320__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (o_removeCaseSensitive_add76320__5)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (o_removeCaseSensitive_add76320__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (o_removeCaseSensitive_add76320__6)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (o_removeCaseSensitive_add76320__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (o_removeCaseSensitive_add76320__7)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (o_removeCaseSensitive_add76320__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [-Bp6t/+XiZ6a]_c(*XP=\"]Q%&amp;?v<p@=?Lx`)|5vO_\"", ((Attributes) (o_removeCaseSensitive_add76320__8)).toString());
        Assert.assertEquals(-1226707509, ((int) (((Attributes) (o_removeCaseSensitive_add76320__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString100945() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(784138288, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-356021341, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg97298() throws Exception {
        Attributes __DSPOT_o_27637 = new Attributes();
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        boolean o_removeCaseSensitive_mg76348_mg97298__39 = a.equals(__DSPOT_o_27637);
        Assert.assertFalse(o_removeCaseSensitive_mg76348_mg97298__39);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76349_mg105480() throws Exception {
        Object __DSPOT_o_30057 = new Object();
        Attribute __DSPOT_attribute_21070 = new Attribute("[;|*&Llg+?(tn$!qj(@V", "E7)bJ$}&t%Cmv8XD?{(/");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        int o_removeCaseSensitive_mg76349__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76349__13 = a.size();
        boolean o_removeCaseSensitive_mg76349__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76349__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76349__16 = a.put(__DSPOT_attribute_21070);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
        boolean o_removeCaseSensitive_mg76349_mg105480__39 = __DSPOT_attribute_21070.equals(__DSPOT_o_30057);
        Assert.assertFalse(o_removeCaseSensitive_mg76349_mg105480__39);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg98082() throws Exception {
        boolean __DSPOT_value_27957 = true;
        String __DSPOT_key_27956 = "&eR/kozJ>GF5M,eUEWgL";
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348_mg98082__39 = o_removeCaseSensitive_mg76348__5.put(__DSPOT_key_27956, __DSPOT_value_27957);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (o_removeCaseSensitive_mg76348_mg98082__39)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (o_removeCaseSensitive_mg76348_mg98082__39)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (a)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &eR/kozJ>GF5M,eUEWgL", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1300052013, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg98184() throws Exception {
        Attribute __DSPOT_attribute_27984 = new Attribute("FKnI.4<7cm|5A:``[:wn", "^LEKge(%2<?KnJD> 4iI");
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348_mg98184__39 = o_removeCaseSensitive_mg76348__5.put(__DSPOT_attribute_27984);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (o_removeCaseSensitive_mg76348_mg98184__39)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (o_removeCaseSensitive_mg76348_mg98184__39)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" FKnI.4<7cm|5A:``[:wn=\"^LEKge(%2<?KnJD> 4iI\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(-1784292061, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76349_mg105867() throws Exception {
        Object __DSPOT_o_30165 = new Object();
        Attribute __DSPOT_attribute_21070 = new Attribute("[;|*&Llg+?(tn$!qj(@V", "E7)bJ$}&t%Cmv8XD?{(/");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        int o_removeCaseSensitive_mg76349__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76349__13 = a.size();
        boolean o_removeCaseSensitive_mg76349__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76349__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76349__16 = a.put(__DSPOT_attribute_21070);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
        boolean o_removeCaseSensitive_mg76349_mg105867__39 = a.equals(__DSPOT_o_30165);
        Assert.assertFalse(o_removeCaseSensitive_mg76349_mg105867__39);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg103281() throws Exception {
        Attribute __DSPOT_attribute_29219 = new Attribute("6O4G%C=e1VWNgsk#5KD3", "3Ew0:FH( ,Qwd#29W0I&");
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348_mg103281__39 = o_removeCaseSensitive_mg76348__16.put(__DSPOT_attribute_29219);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (o_removeCaseSensitive_mg76348_mg103281__39)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (o_removeCaseSensitive_mg76348_mg103281__39)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (a)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" 6O4G%C=e1VWNgsk#5KD3=\"3Ew0:FH( ,Qwd#29W0I&amp;\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(2022825009, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg101790() throws Exception {
        boolean __DSPOT_value_28910 = false;
        String __DSPOT_key_28909 = "RP&hS=wd`G36)9z!aJFN";
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348_mg101790__39 = o_removeCaseSensitive_mg76348__9.put(__DSPOT_key_28909, __DSPOT_value_28910);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348_mg101790__39)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348_mg101790__39)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76349_mg105627() throws Exception {
        String __DSPOT_key_30096 = "m-_#{k9(0EGM556-yPhM";
        Attribute __DSPOT_attribute_21070 = new Attribute("[;|*&Llg+?(tn$!qj(@V", "E7)bJ$}&t%Cmv8XD?{(/");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        int o_removeCaseSensitive_mg76349__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76349__13 = a.size();
        boolean o_removeCaseSensitive_mg76349__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76349__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76349__16 = a.put(__DSPOT_attribute_21070);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
        __DSPOT_attribute_21070.setKey(__DSPOT_key_30096);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" m-_#{k9(0EGM556-yPhM=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1823258271, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" m-_#{k9(0EGM556-yPhM=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(1823258271, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" m-_#{k9(0EGM556-yPhM=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(1823258271, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" m-_#{k9(0EGM556-yPhM=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(1823258271, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" m-_#{k9(0EGM556-yPhM=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(1823258271, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" m-_#{k9(0EGM556-yPhM=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(1823258271, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" m-_#{k9(0EGM556-yPhM=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(1823258271, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg97940() throws Exception {
        String __DSPOT_key_27886 = "!#{/&[_H5tonp=%dhUGB";
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        boolean o_removeCaseSensitive_mg76348_mg97940__38 = o_removeCaseSensitive_mg76348__5.hasKeyIgnoreCase(__DSPOT_key_27886);
        Assert.assertFalse(o_removeCaseSensitive_mg76348_mg97940__38);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg103201() throws Exception {
        String __DSPOT_value_29195 = "Chn_R>W2(2%v##CZy&q3";
        String __DSPOT_key_29194 = "&d:<*zbgJ@%>bW1i*:c(";
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348_mg103201__39 = o_removeCaseSensitive_mg76348__16.put(__DSPOT_key_29194, __DSPOT_value_29195);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (o_removeCaseSensitive_mg76348_mg103201__39)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (o_removeCaseSensitive_mg76348_mg103201__39)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\" &d:<*zbgJ@%>bW1i*:c(=\"Chn_R>W2(2%v##CZy&amp;q3\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(-1259168313, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76349_mg105660() throws Exception {
        String __DSPOT_val_30113 = "[NW(-/>EsMAI<,SNVATJ";
        Attribute __DSPOT_attribute_21070 = new Attribute("[;|*&Llg+?(tn$!qj(@V", "E7)bJ$}&t%Cmv8XD?{(/");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76349__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        int o_removeCaseSensitive_mg76349__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76349__13 = a.size();
        boolean o_removeCaseSensitive_mg76349__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76349__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76349__16 = a.put(__DSPOT_attribute_21070);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"E7)bJ$}&amp;t%Cmv8XD?{(/\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(-337298777, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
        String o_removeCaseSensitive_mg76349_mg105660__38 = __DSPOT_attribute_21070.setValue(__DSPOT_val_30113);
        Assert.assertEquals("E7)bJ$}&t%Cmv8XD?{(/", o_removeCaseSensitive_mg76349_mg105660__38);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"[NW(-/>EsMAI<,SNVATJ\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1509633800, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"[NW(-/>EsMAI<,SNVATJ\"", ((Attributes) (o_removeCaseSensitive_mg76349__5)).toString());
        Assert.assertEquals(1509633800, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"[NW(-/>EsMAI<,SNVATJ\"", ((Attributes) (o_removeCaseSensitive_mg76349__6)).toString());
        Assert.assertEquals(1509633800, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"[NW(-/>EsMAI<,SNVATJ\"", ((Attributes) (o_removeCaseSensitive_mg76349__7)).toString());
        Assert.assertEquals(1509633800, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"[NW(-/>EsMAI<,SNVATJ\"", ((Attributes) (o_removeCaseSensitive_mg76349__8)).toString());
        Assert.assertEquals(1509633800, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"[NW(-/>EsMAI<,SNVATJ\"", ((Attributes) (o_removeCaseSensitive_mg76349__9)).toString());
        Assert.assertEquals(1509633800, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" [;|*&Llg+?(tn$!qj(@V=\"[NW(-/>EsMAI<,SNVATJ\"", ((Attributes) (o_removeCaseSensitive_mg76349__16)).toString());
        Assert.assertEquals(1509633800, ((int) (((Attributes) (o_removeCaseSensitive_mg76349__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg97920() throws Exception {
        String __DSPOT_key_27883 = "V$bs%F(EAL}jyezNprxQ";
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        boolean o_removeCaseSensitive_mg76348_mg97920__38 = o_removeCaseSensitive_mg76348__5.hasKey(__DSPOT_key_27883);
        Assert.assertFalse(o_removeCaseSensitive_mg76348_mg97920__38);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_mg76348_mg98217() throws Exception {
        String __DSPOT_key_27997 = "B+ mLfs%YglhXnkq;[:D";
        String __DSPOT_value_21069 = "b/I Ij}&4O!4G_B* a7y";
        String __DSPOT_key_21068 = "]OY)K-a9}{V%kCy$.5_c";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Attributes o_removeCaseSensitive_mg76348__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        int o_removeCaseSensitive_mg76348__10 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_mg76348__13 = a.size();
        boolean o_removeCaseSensitive_mg76348__14 = a.hasKey("tot");
        boolean o_removeCaseSensitive_mg76348__15 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_mg76348__16 = a.put(__DSPOT_key_21068, __DSPOT_value_21069);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
        o_removeCaseSensitive_mg76348__5.remove(__DSPOT_key_27997);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__5)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__6)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__7)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__8)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__9)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ]OY)K-a9}{V%kCy$.5_c=\"b/I Ij}&amp;4O!4G_B* a7y\"", ((Attributes) (o_removeCaseSensitive_mg76348__16)).toString());
        Assert.assertEquals(1515963550, ((int) (((Attributes) (o_removeCaseSensitive_mg76348__16)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331_add102938() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(1695528839, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString101114litString133108() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-926036879, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove(")ot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" =\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(817790522, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(817790522, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(817790522, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(817790522, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(817790522, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString101114litString133055() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("", "\n");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" =\"\n\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-1006811593, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        Assert.assertEquals(" tot=\"one\" =\"\n\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2121762337, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"\n\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-2121762337, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"\n\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-2121762337, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"\n\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-2121762337, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"\n\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-2121762337, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString101282_add158278() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("hello", "");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(2067220379, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString101282_mg158733() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("hello", "");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(2067220379, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        Attributes o_removeCaseSensitive_remove76331litString101282_mg158733__29 = a.clone();
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331litString101282_mg158733__29)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331litString101282_mg158733__29)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1106940285, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-1106940285, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1106940285, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1106940285, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-1106940285, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString101114_mg134987_failAssert3() throws Exception {
        try {
            Attributes a = new Attributes();
            Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
            Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
            Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
            Attributes o_removeCaseSensitive_remove76331__6 = a.put("", "There");
            int o_removeCaseSensitive_remove76331__7 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            int o_removeCaseSensitive_remove76331__10 = a.size();
            boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
            boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
            a.asList();
            org.junit.Assert.fail("removeCaseSensitive_remove76331litString101114_mg134987 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString101282_mg158714() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("hello", "");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(2067220379, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        List<Attribute> o_removeCaseSensitive_remove76331litString101282_mg158714__29 = a.asList();
        Assert.assertFalse(o_removeCaseSensitive_remove76331litString101282_mg158714__29.isEmpty());
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(1380430403, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_remove76331litString101114_mg135164() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Attributes o_removeCaseSensitive_remove76331__6 = a.put("", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-926036879, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
        int o_removeCaseSensitive_remove76331__7 = a.size();
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_remove76331__10 = a.size();
        boolean o_removeCaseSensitive_remove76331__11 = a.hasKey("tot");
        boolean o_removeCaseSensitive_remove76331__12 = a.hasKey("Tot");
        a.normalize();
        Assert.assertEquals(" tot=\"one\" =\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1806673511, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__3)).toString());
        Assert.assertEquals(-1806673511, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__4)).toString());
        Assert.assertEquals(-1806673511, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__5)).toString());
        Assert.assertEquals(-1806673511, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" =\"There\"", ((Attributes) (o_removeCaseSensitive_remove76331__6)).toString());
        Assert.assertEquals(-1806673511, ((int) (((Attributes) (o_removeCaseSensitive_remove76331__6)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency__8);
        boolean o_testSetKeyConsistency__9 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency__9);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency__3)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_remove435456() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_remove435456__7 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_remove435456__7);
        boolean o_testSetKeyConsistency_remove435456__8 = a.hasKey("b");
        Assert.assertFalse(o_testSetKeyConsistency_remove435456__8);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_remove435456__7);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435462() throws Exception {
        Attributes __DSPOT_o_129283 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435462__5 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435462__5)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435462__5)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435462__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg435462__10);
        boolean o_testSetKeyConsistency_mg435462__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg435462__11);
        boolean o_testSetKeyConsistency_mg435462__12 = a.equals(__DSPOT_o_129283);
        Assert.assertFalse(o_testSetKeyConsistency_mg435462__12);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435462__5)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_mg435462__5)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg435462__10);
        Assert.assertTrue(o_testSetKeyConsistency_mg435462__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473() throws Exception {
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg435473__11);
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg435473__12);
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (a)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg435473__11);
        Assert.assertTrue(o_testSetKeyConsistency_mg435473__12);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435434() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistencylitString435434__3 = a.put("a", "");
        Assert.assertEquals(" a=\"\"", ((Attributes) (o_testSetKeyConsistencylitString435434__3)).toString());
        Assert.assertEquals(119135170, ((int) (((Attributes) (o_testSetKeyConsistencylitString435434__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistencylitString435434__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistencylitString435434__8);
        boolean o_testSetKeyConsistencylitString435434__9 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistencylitString435434__9);
        Assert.assertEquals(" b=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(120058691, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"\"", ((Attributes) (o_testSetKeyConsistencylitString435434__3)).toString());
        Assert.assertEquals(120058691, ((int) (((Attributes) (o_testSetKeyConsistencylitString435434__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistencylitString435434__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435466() throws Exception {
        String __DSPOT_key_129287 = "f+[!_;6T*Pj%XU|cdGs@";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435466__4 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435466__4)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435466__4)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435466__9 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg435466__9);
        boolean o_testSetKeyConsistency_mg435466__10 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg435466__10);
        boolean o_testSetKeyConsistency_mg435466__11 = a.hasKeyIgnoreCase(__DSPOT_key_129287);
        Assert.assertFalse(o_testSetKeyConsistency_mg435466__11);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435466__4)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_mg435466__4)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg435466__9);
        Assert.assertTrue(o_testSetKeyConsistency_mg435466__10);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435485() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435485__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435485__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435485__3)).hashCode())));
        for (Attribute at : a) {
            String __DSPOT_val_129297 = "`-kv<y>b<,F5V>.r=$<t";
            at.setKey("b");
            at.setValue(__DSPOT_val_129297);
        }
        boolean o_testSetKeyConsistency_mg435485__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg435485__10);
        boolean o_testSetKeyConsistency_mg435485__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg435485__11);
        Assert.assertEquals(" b=\"`-kv<y>b<,F5V>.r=$<t\"", ((Attributes) (a)).toString());
        Assert.assertEquals(701163584, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"`-kv<y>b<,F5V>.r=$<t\"", ((Attributes) (o_testSetKeyConsistency_mg435485__3)).toString());
        Assert.assertEquals(701163584, ((int) (((Attributes) (o_testSetKeyConsistency_mg435485__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg435485__10);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435430() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistencylitString435430__3 = a.put("\n", "a");
        Assert.assertEquals(" \n=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString435430__3)).toString());
        Assert.assertEquals(41678570, ((int) (((Attributes) (o_testSetKeyConsistencylitString435430__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistencylitString435430__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistencylitString435430__8);
        boolean o_testSetKeyConsistencylitString435430__9 = a.hasKey("b");
        Assert.assertFalse(o_testSetKeyConsistencylitString435430__9);
        Assert.assertEquals(" \n=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(41678570, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" \n=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString435430__3)).toString());
        Assert.assertEquals(41678570, ((int) (((Attributes) (o_testSetKeyConsistencylitString435430__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistencylitString435430__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435486() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435486__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435486__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435486__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
            at.toString();
        }
        boolean o_testSetKeyConsistency_mg435486__9 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg435486__9);
        boolean o_testSetKeyConsistency_mg435486__10 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg435486__10);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435486__3)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_mg435486__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg435486__9);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435504() throws Exception {
        String __DSPOT_key_129309 = "WD#usrY[Er4q^S:KB(.`";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435504__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435504__11);
        boolean o_testSetKeyConsistency_rv435504__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435504__12);
        __DSPOT_invoc_3.remove(__DSPOT_key_129309);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435504__11);
        Assert.assertTrue(o_testSetKeyConsistency_rv435504__12);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435505() throws Exception {
        String __DSPOT_key_129310 = ">,ra/IN&NQA?^W2BtObY";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435505__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435505__11);
        boolean o_testSetKeyConsistency_rv435505__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435505__12);
        __DSPOT_invoc_3.removeIgnoreCase(__DSPOT_key_129310);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435505__11);
        Assert.assertTrue(o_testSetKeyConsistency_rv435505__12);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435502() throws Exception {
        String __DSPOT_value_129307 = "A(NZcXknKhf| *#YQtR(";
        String __DSPOT_key_129306 = "l!Ndz4UobUb[fN#Qo?:j";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435502__12 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435502__12);
        boolean o_testSetKeyConsistency_rv435502__13 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435502__13);
        Attributes o_testSetKeyConsistency_rv435502__14 = __DSPOT_invoc_3.put(__DSPOT_key_129306, __DSPOT_value_129307);
        Assert.assertEquals(" b=\"a\" l!Ndz4UobUb[fN#Qo?:j=\"A(NZcXknKhf| *#YQtR(\"", ((Attributes) (o_testSetKeyConsistency_rv435502__14)).toString());
        Assert.assertEquals(560667641, ((int) (((Attributes) (o_testSetKeyConsistency_rv435502__14)).hashCode())));
        Assert.assertEquals(" b=\"a\" l!Ndz4UobUb[fN#Qo?:j=\"A(NZcXknKhf| *#YQtR(\"", ((Attributes) (a)).toString());
        Assert.assertEquals(560667641, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435502__12);
        Assert.assertTrue(o_testSetKeyConsistency_rv435502__13);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435500() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435500__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435500__10);
        boolean o_testSetKeyConsistency_rv435500__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435500__11);
        __DSPOT_invoc_3.normalize();
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435500__10);
        Assert.assertTrue(o_testSetKeyConsistency_rv435500__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435501() throws Exception {
        boolean __DSPOT_value_129305 = true;
        String __DSPOT_key_129304 = "6v8Nu&[L37CM!+WBU&()";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435501__12 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435501__12);
        boolean o_testSetKeyConsistency_rv435501__13 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435501__13);
        Attributes o_testSetKeyConsistency_rv435501__14 = __DSPOT_invoc_3.put(__DSPOT_key_129304, __DSPOT_value_129305);
        Assert.assertEquals(" b=\"a\" 6v8Nu&[L37CM!+WBU&()", ((Attributes) (o_testSetKeyConsistency_rv435501__14)).toString());
        Assert.assertEquals(925882998, ((int) (((Attributes) (o_testSetKeyConsistency_rv435501__14)).hashCode())));
        Assert.assertEquals(" b=\"a\" 6v8Nu&[L37CM!+WBU&()", ((Attributes) (a)).toString());
        Assert.assertEquals(925882998, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435501__12);
        Assert.assertTrue(o_testSetKeyConsistency_rv435501__13);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435440_failAssert96() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("a", "a");
            for (Attribute at : a) {
                at.setKey("\n");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435440 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435459() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435459__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435459__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435459__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435459__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg435459__8);
        boolean o_testSetKeyConsistency_mg435459__9 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg435459__9);
        List<Attribute> o_testSetKeyConsistency_mg435459__10 = a.asList();
        Assert.assertFalse(o_testSetKeyConsistency_mg435459__10.isEmpty());
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435459__3)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_mg435459__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg435459__8);
        Assert.assertTrue(o_testSetKeyConsistency_mg435459__9);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435494() throws Exception {
        String __DSPOT_key_129301 = "T CX9(XZ)!r![p2$_rS,";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435494__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435494__11);
        boolean o_testSetKeyConsistency_rv435494__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435494__12);
        String o_testSetKeyConsistency_rv435494__13 = __DSPOT_invoc_3.getIgnoreCase(__DSPOT_key_129301);
        Assert.assertEquals("", o_testSetKeyConsistency_rv435494__13);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435494__11);
        Assert.assertTrue(o_testSetKeyConsistency_rv435494__12);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435479() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435479__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435479__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435479__3)).hashCode())));
        for (Attribute at : a) {
            Object __DSPOT_o_129295 = new Object();
            at.setKey("b");
            at.equals(__DSPOT_o_129295);
        }
        boolean o_testSetKeyConsistency_mg435479__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg435479__11);
        boolean o_testSetKeyConsistency_mg435479__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg435479__12);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435479__3)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_mg435479__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg435479__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435492() throws Exception {
        Object __DSPOT_o_129299 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435492__12 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435492__12);
        boolean o_testSetKeyConsistency_rv435492__13 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435492__13);
        boolean o_testSetKeyConsistency_rv435492__14 = __DSPOT_invoc_3.equals(__DSPOT_o_129299);
        Assert.assertFalse(o_testSetKeyConsistency_rv435492__14);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435492__12);
        Assert.assertTrue(o_testSetKeyConsistency_rv435492__13);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435435() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistencylitString435435__3 = a.put("a", "\n");
        Assert.assertEquals(" a=\"\n\"", ((Attributes) (o_testSetKeyConsistencylitString435435__3)).toString());
        Assert.assertEquals(119433080, ((int) (((Attributes) (o_testSetKeyConsistencylitString435435__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistencylitString435435__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistencylitString435435__8);
        boolean o_testSetKeyConsistencylitString435435__9 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistencylitString435435__9);
        Assert.assertEquals(" b=\"\n\"", ((Attributes) (a)).toString());
        Assert.assertEquals(120356601, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"\n\"", ((Attributes) (o_testSetKeyConsistencylitString435435__3)).toString());
        Assert.assertEquals(120356601, ((int) (((Attributes) (o_testSetKeyConsistencylitString435435__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistencylitString435435__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_add435452() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_add435452__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_add435452__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_add435452__3)).hashCode())));
        Attributes o_testSetKeyConsistency_add435452__4 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_add435452__4)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_add435452__4)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_add435452__9 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_add435452__9);
        boolean o_testSetKeyConsistency_add435452__10 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_add435452__10);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_add435452__3)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_add435452__3)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_add435452__4)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_add435452__4)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_add435452__9);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435439_failAssert95() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("a", "a");
            for (Attribute at : a) {
                at.setKey("");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435439 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv435490() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv435490__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv435490__10);
        boolean o_testSetKeyConsistency_rv435490__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv435490__11);
        Attributes o_testSetKeyConsistency_rv435490__12 = __DSPOT_invoc_3.clone();
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_rv435490__12)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_rv435490__12)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(5088, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv435490__10);
        Assert.assertTrue(o_testSetKeyConsistency_rv435490__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435429_failAssert94() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("", "a");
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435429 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg440970() throws Exception {
        String __DSPOT_value_130920 = "JZ]%pg6v$O%2Qe_ZV67a";
        String __DSPOT_key_130918 = "i)EXu3Wj{DPN=JKLrJme";
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg440970__24 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_key_130918, __DSPOT_value_130920);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435429_failAssert94_rv437429() throws Exception {
        try {
            boolean __DSPOT_value_129588 = true;
            String __DSPOT_key_129587 = "}X+%2-`g5f3]DCiL7Q,d";
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes __DSPOT_invoc_5 = a.put("", "a");
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435429 should have thrown IllegalArgumentException");
            __DSPOT_invoc_5.put(__DSPOT_key_129587, __DSPOT_value_129588);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435440_failAssert96_rv437154() throws Exception {
        try {
            String __DSPOT_key_129457 = "fzIWPsETdy}# ew&.{F8";
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes __DSPOT_invoc_5 = a.put("a", "a");
            for (Attribute at : a) {
                at.setKey("\n");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435440 should have thrown IllegalArgumentException");
            __DSPOT_invoc_5.getIgnoreCase(__DSPOT_key_129457);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435429_failAssert94_mg437110() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencylitString435429_failAssert94_mg437110__5 = a.put("", "a");
            Assert.assertEquals(" =\"a\"", ((Attributes) (o_testSetKeyConsistencylitString435429_failAssert94_mg437110__5)).toString());
            Assert.assertEquals(32443360, ((int) (((Attributes) (o_testSetKeyConsistencylitString435429_failAssert94_mg437110__5)).hashCode())));
            for (Attribute at : a) {
                String __DSPOT_val_129439 = "]kY4BsLqmT,1&aO^%d7q";
                at.setKey("b");
                at.setValue(__DSPOT_val_129439);
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435429 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435429_failAssert94litString436016() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencylitString435429_failAssert94litString436016__5 = a.put("", "");
            Assert.assertEquals(" =\"\"", ((Attributes) (o_testSetKeyConsistencylitString435429_failAssert94litString436016__5)).toString());
            Assert.assertEquals(29553633, ((int) (((Attributes) (o_testSetKeyConsistencylitString435429_failAssert94litString436016__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435429 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435439_failAssert95_add437884() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencylitString435439_failAssert95_add437884__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString435439_failAssert95_add437884__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencylitString435439_failAssert95_add437884__5)).hashCode())));
            Attributes o_testSetKeyConsistencylitString435439_failAssert95_add437884__6 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString435439_failAssert95_add437884__6)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencylitString435439_failAssert95_add437884__6)).hashCode())));
            for (Attribute at : a) {
                at.setKey("");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435439 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString435429_failAssert94litString436028() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencylitString435429_failAssert94litString436028__5 = a.put("", "\n");
            Assert.assertEquals(" =\"\n\"", ((Attributes) (o_testSetKeyConsistencylitString435429_failAssert94litString436028__5)).toString());
            Assert.assertEquals(29851543, ((int) (((Attributes) (o_testSetKeyConsistencylitString435429_failAssert94litString436028__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString435429 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg450614() throws Exception {
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            String __DSPOT_val_134721 = "Vv:*}nlm1G5>7]JW=ka ";
            at.setKey("b");
            at.setValue(__DSPOT_val_134721);
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"Vv:*}nlm1G5>7]JW=ka \" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-1994539568, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"Vv:*}nlm1G5>7]JW=ka \" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(337706728, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        Assert.assertEquals(" b=\"Vv:*}nlm1G5>7]JW=ka \" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(337706728, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"Vv:*}nlm1G5>7]JW=ka \" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(337706728, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"Vv:*}nlm1G5>7]JW=ka \" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(337706728, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg448312() throws Exception {
        String __DSPOT_val_133777 = "[-#ly},REGG*bq$ClfPL";
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        String o_testSetKeyConsistency_mg435473_mg441010_mg448312__29 = __DSPOT_attribute_130937.setValue(__DSPOT_val_133777);
        Assert.assertEquals("i]e!W z`2kfKpov?S1&h", o_testSetKeyConsistency_mg435473_mg441010_mg448312__29);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"[-#ly},REGG*bq$ClfPL\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1196946719, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"[-#ly},REGG*bq$ClfPL\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(1196946719, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"[-#ly},REGG*bq$ClfPL\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(1196946719, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"[-#ly},REGG*bq$ClfPL\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(1196946719, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg448522() throws Exception {
        String __DSPOT_key_133839 = "/b9XE&a$lhSaQy&,$r9X";
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        __DSPOT_attribute_129292.setKey(__DSPOT_key_133839);
        Assert.assertEquals(" b=\"a\" /b9XE&a$lhSaQy&,$r9X=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(153547706, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" /b9XE&a$lhSaQy&,$r9X=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(153547706, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" /b9XE&a$lhSaQy&,$r9X=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(153547706, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" /b9XE&a$lhSaQy&,$r9X=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(153547706, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg449253() throws Exception {
        String __DSPOT_key_134189 = "G4npGsfjH(MXo2Sw!K|b";
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        a.remove(__DSPOT_key_134189);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg449597() throws Exception {
        Object __DSPOT_o_134376 = new Object();
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        boolean o_testSetKeyConsistency_mg435473_mg441010_mg449597__30 = o_testSetKeyConsistency_mg435473__6.equals(__DSPOT_o_134376);
        Assert.assertFalse(o_testSetKeyConsistency_mg435473_mg441010_mg449597__30);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg448387() throws Exception {
        Object __DSPOT_o_133797 = new Object();
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        boolean o_testSetKeyConsistency_mg435473_mg441010_mg448387__30 = __DSPOT_attribute_129292.equals(__DSPOT_o_133797);
        Assert.assertFalse(o_testSetKeyConsistency_mg435473_mg441010_mg448387__30);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg451149() throws Exception {
        String __DSPOT_key_134934 = "ZE/%qJ)n@Oofl?:-pm4Z";
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        boolean o_testSetKeyConsistency_mg435473_mg441010_mg451149__29 = o_testSetKeyConsistency_mg435473__13.hasKeyIgnoreCase(__DSPOT_key_134934);
        Assert.assertFalse(o_testSetKeyConsistency_mg435473_mg441010_mg451149__29);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg451603() throws Exception {
        String __DSPOT_key_135175 = "x|A:znc+=F=|jB2w.GfW";
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        o_testSetKeyConsistency_mg435473__13.removeIgnoreCase(__DSPOT_key_135175);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg439165_mg457537() throws Exception {
        Attribute __DSPOT_o_137564 = new Attribute("TP<s$]IhzH<e={=]7-I3", "]aY./mtN6oqAZT1K(tgh", new Attributes());
        Attribute __DSPOT_attribute_130301 = new Attribute("23f7 }{g0ocy<d0Lz*rj", "[?T[6Ynb. (#6]>>ve(p", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg439165__25 = a.put(__DSPOT_attribute_130301);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).hashCode())));
        boolean o_testSetKeyConsistency_mg435473_mg439165_mg457537__31 = __DSPOT_attribute_130301.equals(__DSPOT_o_137564);
        Assert.assertFalse(o_testSetKeyConsistency_mg435473_mg439165_mg457537__31);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg441010_mg451056() throws Exception {
        String __DSPOT_key_134895 = "[UZt-G}`6F%xRs0Knj!m";
        Attribute __DSPOT_attribute_130937 = new Attribute("g/.!f#*7.vCa2s3S|P+g", "i]e!W z`2kfKpov?S1&h", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg441010__25 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_attribute_130937);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
        String o_testSetKeyConsistency_mg435473_mg441010_mg451056__29 = o_testSetKeyConsistency_mg435473__13.getIgnoreCase(__DSPOT_key_134895);
        Assert.assertEquals("", o_testSetKeyConsistency_mg435473_mg441010_mg451056__29);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (a)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" g/.!f#*7.vCa2s3S|P+g=\"i]e!W z`2kfKpov?S1&amp;h\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).toString());
        Assert.assertEquals(59059272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg441010__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg440970_mg455142() throws Exception {
        boolean __DSPOT_value_136610 = true;
        String __DSPOT_key_136609 = "$-1>Gt[AuWNlDWF#-^n`";
        String __DSPOT_value_130920 = "JZ]%pg6v$O%2Qe_ZV67a";
        String __DSPOT_key_130918 = "i)EXu3Wj{DPN=JKLrJme";
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg440970__24 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_key_130918, __DSPOT_value_130920);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg440970_mg455142__29 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_key_136609, __DSPOT_value_136610);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" $-1>Gt[AuWNlDWF#-^n`", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970_mg455142__29)).toString());
        Assert.assertEquals(-1830186913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970_mg455142__29)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" $-1>Gt[AuWNlDWF#-^n`", ((Attributes) (a)).toString());
        Assert.assertEquals(-1830186913, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" $-1>Gt[AuWNlDWF#-^n`", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(-1830186913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" $-1>Gt[AuWNlDWF#-^n`", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-1830186913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" $-1>Gt[AuWNlDWF#-^n`", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).toString());
        Assert.assertEquals(-1830186913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg440970_mg454742() throws Exception {
        Attributes __DSPOT_o_136475 = new Attributes();
        String __DSPOT_value_130920 = "JZ]%pg6v$O%2Qe_ZV67a";
        String __DSPOT_key_130918 = "i)EXu3Wj{DPN=JKLrJme";
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg440970__24 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_key_130918, __DSPOT_value_130920);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).hashCode())));
        boolean o_testSetKeyConsistency_mg435473_mg440970_mg454742__29 = o_testSetKeyConsistency_mg435473__13.equals(__DSPOT_o_136475);
        Assert.assertFalse(o_testSetKeyConsistency_mg435473_mg440970_mg454742__29);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg439821_mg457762() throws Exception {
        Attribute __DSPOT_attribute_137668 = new Attribute("&[X!<jfz`;!&Ss:OmpQk", "#1l]Hop$gpCMO8qANn=+");
        String __DSPOT_value_130506 = "+-q}tn(7xAGH3)3QX?bn";
        String __DSPOT_key_130505 = "V=mw^$H6%obVAe>#*Noe";
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg439821__24 = o_testSetKeyConsistency_mg435473__6.put(__DSPOT_key_130505, __DSPOT_value_130506);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" V=mw^$H6%obVAe>#*Noe=\"+-q}tn(7xAGH3)3QX?bn\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439821__24)).toString());
        Assert.assertEquals(-1245055889, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439821__24)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg439821_mg457762__29 = a.put(__DSPOT_attribute_137668);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" V=mw^$H6%obVAe>#*Noe=\"+-q}tn(7xAGH3)3QX?bn\" &[X!<jfz`;!&Ss:OmpQk=\"#1l]Hop$gpCMO8qANn=+\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439821_mg457762__29)).toString());
        Assert.assertEquals(-720664142, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439821_mg457762__29)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" V=mw^$H6%obVAe>#*Noe=\"+-q}tn(7xAGH3)3QX?bn\" &[X!<jfz`;!&Ss:OmpQk=\"#1l]Hop$gpCMO8qANn=+\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-720664142, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" V=mw^$H6%obVAe>#*Noe=\"+-q}tn(7xAGH3)3QX?bn\" &[X!<jfz`;!&Ss:OmpQk=\"#1l]Hop$gpCMO8qANn=+\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(-720664142, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" V=mw^$H6%obVAe>#*Noe=\"+-q}tn(7xAGH3)3QX?bn\" &[X!<jfz`;!&Ss:OmpQk=\"#1l]Hop$gpCMO8qANn=+\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-720664142, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" V=mw^$H6%obVAe>#*Noe=\"+-q}tn(7xAGH3)3QX?bn\" &[X!<jfz`;!&Ss:OmpQk=\"#1l]Hop$gpCMO8qANn=+\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439821__24)).toString());
        Assert.assertEquals(-720664142, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439821__24)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg439165_mg458455() throws Exception {
        Attribute __DSPOT_attribute_138062 = new Attribute("fa_2xd0oT(3UPg5#s,n2", "oT_5-UY%<G,pr4JtAVZ2", new Attributes());
        Attribute __DSPOT_attribute_130301 = new Attribute("23f7 }{g0ocy<d0Lz*rj", "[?T[6Ynb. (#6]>>ve(p", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg439165__25 = a.put(__DSPOT_attribute_130301);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg439165_mg458455__31 = o_testSetKeyConsistency_mg435473__6.put(__DSPOT_attribute_138062);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\" fa_2xd0oT(3UPg5#s,n2=\"oT_5-UY%<G,pr4JtAVZ2\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165_mg458455__31)).toString());
        Assert.assertEquals(-359477913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165_mg458455__31)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\" fa_2xd0oT(3UPg5#s,n2=\"oT_5-UY%<G,pr4JtAVZ2\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-359477913, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\" fa_2xd0oT(3UPg5#s,n2=\"oT_5-UY%<G,pr4JtAVZ2\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(-359477913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\" fa_2xd0oT(3UPg5#s,n2=\"oT_5-UY%<G,pr4JtAVZ2\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-359477913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\" fa_2xd0oT(3UPg5#s,n2=\"oT_5-UY%<G,pr4JtAVZ2\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).toString());
        Assert.assertEquals(-359477913, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg439165_mg458222() throws Exception {
        boolean __DSPOT_value_137935 = false;
        String __DSPOT_key_137934 = "ul)<_*zS-&;U8r/EUBn,";
        Attribute __DSPOT_attribute_130301 = new Attribute("23f7 }{g0ocy<d0Lz*rj", "[?T[6Ynb. (#6]>>ve(p", new Attributes());
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg439165__25 = a.put(__DSPOT_attribute_130301);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg439165_mg458222__30 = a.put(__DSPOT_key_137934, __DSPOT_value_137935);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165_mg458222__30)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165_mg458222__30)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" 23f7 }{g0ocy<d0Lz*rj=\"[?T[6Ynb. (#6]>>ve(p\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).toString());
        Assert.assertEquals(-1663856384, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg439165__25)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg435473_mg440970_mg453964() throws Exception {
        Attribute __DSPOT_attribute_136161 = new Attribute("-]Lh-=l(,T{vic0U[6k ", ":TEg R=Ql]QOb=bPone:");
        String __DSPOT_value_130920 = "JZ]%pg6v$O%2Qe_ZV67a";
        String __DSPOT_key_130918 = "i)EXu3Wj{DPN=JKLrJme";
        Attribute __DSPOT_attribute_129292 = new Attribute("Tr5K,*hEIfakP{m-T!gF", "P54{/R{(>q1srmKK=(`#", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg435473__11 = a.hasKey("a");
        boolean o_testSetKeyConsistency_mg435473__12 = a.hasKey("b");
        Attributes o_testSetKeyConsistency_mg435473__13 = a.put(__DSPOT_attribute_129292);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(2021780272, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg440970__24 = o_testSetKeyConsistency_mg435473__13.put(__DSPOT_key_130918, __DSPOT_value_130920);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).toString());
        Assert.assertEquals(-863281734, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).hashCode())));
        Attributes o_testSetKeyConsistency_mg435473_mg440970_mg453964__29 = o_testSetKeyConsistency_mg435473__6.put(__DSPOT_attribute_136161);
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" -]Lh-=l(,T{vic0U[6k=\":TEg R=Ql]QOb=bPone:\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970_mg453964__29)).toString());
        Assert.assertEquals(1544424563, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970_mg453964__29)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" -]Lh-=l(,T{vic0U[6k=\":TEg R=Ql]QOb=bPone:\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1544424563, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" -]Lh-=l(,T{vic0U[6k=\":TEg R=Ql]QOb=bPone:\"", ((Attributes) (o_testSetKeyConsistency_mg435473__6)).toString());
        Assert.assertEquals(1544424563, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__6)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" -]Lh-=l(,T{vic0U[6k=\":TEg R=Ql]QOb=bPone:\"", ((Attributes) (o_testSetKeyConsistency_mg435473__13)).toString());
        Assert.assertEquals(1544424563, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" Tr5K,*hEIfakP{m-T!gF=\"P54{/R{(>q1srmKK=(`#\" i)EXu3Wj{DPN=JKLrJme=\"JZ]%pg6v$O%2Qe_ZV67a\" -]Lh-=l(,T{vic0U[6k=\":TEg R=Ql]QOb=bPone:\"", ((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).toString());
        Assert.assertEquals(1544424563, ((int) (((Attributes) (o_testSetKeyConsistency_mg435473_mg440970__24)).hashCode())));
    }
}

