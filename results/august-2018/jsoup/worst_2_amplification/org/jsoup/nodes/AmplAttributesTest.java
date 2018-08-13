package org.jsoup.nodes;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AmplAttributesTest {
    @Test(timeout = 10000)
    public void htmlnull267_failAssert10() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.hasKey("Tot");
            a.hasKey("Hello");
            a.hasKey("data-name");
            a.hasKey("tot");
            a.hasKeyIgnoreCase(null);
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
            org.junit.Assert.fail("htmlnull267 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmllitString85() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString85__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString85__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString85__3)).hashCode())));
        Attributes o_htmllitString85__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString85__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString85__4)).hashCode())));
        Attributes o_htmllitString85__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString85__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString85__5)).hashCode())));
        int o_htmllitString85__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString85__6)));
        boolean o_htmllitString85__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString85__7);
        boolean o_htmllitString85__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString85__8);
        boolean o_htmllitString85__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString85__9);
        boolean o_htmllitString85__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString85__10);
        boolean o_htmllitString85__11 = a.hasKeyIgnoreCase("b0/");
        Assert.assertFalse(o_htmllitString85__11);
        String o_htmllitString85__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString85__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString85__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString85__15)));
        String o_htmllitString85__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString85__16);
        String o_htmllitString85__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString85__17);
        String o_htmllitString85__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_htmllitString85__18);
        String o_htmllitString85__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_htmllitString85__19);
        String o_htmllitString85__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString85__20);
        String o_htmllitString85__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString85__21);
        a.toString();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString85__3)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString85__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString85__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString85__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString85__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmllitString85__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString85__6)));
        Assert.assertTrue(o_htmllitString85__7);
        Assert.assertTrue(o_htmllitString85__8);
        Assert.assertTrue(o_htmllitString85__9);
        Assert.assertFalse(o_htmllitString85__10);
        Assert.assertFalse(o_htmllitString85__11);
        Assert.assertEquals("There", o_htmllitString85__12);
        Assert.assertEquals(1, ((int) (o_htmllitString85__15)));
        Assert.assertEquals("Jsoup", o_htmllitString85__16);
        Assert.assertEquals("", o_htmllitString85__17);
        Assert.assertEquals("a&p", o_htmllitString85__18);
        Assert.assertEquals("a&p", o_htmllitString85__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString85__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_htmllitString85__21);
    }

    @Test(timeout = 10000)
    public void htmllitString30() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString30__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString30__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString30__3)).hashCode())));
        Attributes o_htmllitString30__4 = a.put("Hello", "");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\"", ((Attributes) (o_htmllitString30__4)).toString());
        Assert.assertEquals(-1652213410, ((int) (((Attributes) (o_htmllitString30__4)).hashCode())));
        Attributes o_htmllitString30__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString30__5)).toString());
        Assert.assertEquals(1955204940, ((int) (((Attributes) (o_htmllitString30__5)).hashCode())));
        int o_htmllitString30__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString30__6)));
        boolean o_htmllitString30__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString30__7);
        boolean o_htmllitString30__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString30__8);
        boolean o_htmllitString30__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString30__9);
        boolean o_htmllitString30__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString30__10);
        boolean o_htmllitString30__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString30__11);
        String o_htmllitString30__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("", o_htmllitString30__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString30__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString30__15)));
        String o_htmllitString30__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString30__16);
        String o_htmllitString30__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString30__17);
        String o_htmllitString30__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_htmllitString30__18);
        String o_htmllitString30__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_htmllitString30__19);
        String o_htmllitString30__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", o_htmllitString30__20);
        String o_htmllitString30__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", o_htmllitString30__21);
        a.toString();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1955204940, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString30__3)).toString());
        Assert.assertEquals(1955204940, ((int) (((Attributes) (o_htmllitString30__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString30__4)).toString());
        Assert.assertEquals(1955204940, ((int) (((Attributes) (o_htmllitString30__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString30__5)).toString());
        Assert.assertEquals(1955204940, ((int) (((Attributes) (o_htmllitString30__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString30__6)));
        Assert.assertTrue(o_htmllitString30__7);
        Assert.assertTrue(o_htmllitString30__8);
        Assert.assertTrue(o_htmllitString30__9);
        Assert.assertFalse(o_htmllitString30__10);
        Assert.assertTrue(o_htmllitString30__11);
        Assert.assertEquals("", o_htmllitString30__12);
        Assert.assertEquals(1, ((int) (o_htmllitString30__15)));
        Assert.assertEquals("Jsoup", o_htmllitString30__16);
        Assert.assertEquals("", o_htmllitString30__17);
        Assert.assertEquals("a&p", o_htmllitString30__18);
        Assert.assertEquals("a&p", o_htmllitString30__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", o_htmllitString30__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\" data-name=\"Jsoup\"", o_htmllitString30__21);
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
    public void html_rv231() throws Exception {
        String __DSPOT_key_51 = "]P!B!8p#]q;a7/ez@l%M";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv231__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv231__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv231__4)).hashCode())));
        Attributes o_html_rv231__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv231__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv231__5)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv231__9 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv231__9)));
        boolean o_html_rv231__10 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv231__10);
        boolean o_html_rv231__11 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv231__11);
        boolean o_html_rv231__12 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv231__12);
        boolean o_html_rv231__13 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv231__13);
        boolean o_html_rv231__14 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv231__14);
        String o_html_rv231__15 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv231__15);
        Map<String, String> dataset = a.dataset();
        int o_html_rv231__18 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv231__18)));
        String o_html_rv231__19 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv231__19);
        String o_html_rv231__20 = a.get("tot");
        Assert.assertEquals("", o_html_rv231__20);
        String o_html_rv231__21 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv231__21);
        String o_html_rv231__22 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv231__22);
        String o_html_rv231__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv231__23);
        String o_html_rv231__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv231__24);
        a.toString();
        __DSPOT_invoc_5.removeIgnoreCase(__DSPOT_key_51);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv231__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv231__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv231__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv231__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv231__9)));
        Assert.assertTrue(o_html_rv231__10);
        Assert.assertTrue(o_html_rv231__11);
        Assert.assertTrue(o_html_rv231__12);
        Assert.assertFalse(o_html_rv231__13);
        Assert.assertTrue(o_html_rv231__14);
        Assert.assertEquals("There", o_html_rv231__15);
        Assert.assertEquals(1, ((int) (o_html_rv231__18)));
        Assert.assertEquals("Jsoup", o_html_rv231__19);
        Assert.assertEquals("", o_html_rv231__20);
        Assert.assertEquals("a&p", o_html_rv231__21);
        Assert.assertEquals("a&p", o_html_rv231__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv231__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv231__24);
    }

    @Test(timeout = 10000)
    public void html_rv187() throws Exception {
        Attribute __DSPOT_attribute_23 = new Attribute("1wly$),bA%.UJum&)<4o", "K[>Va&1`i[aMe!@y;s?/");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_html_rv187__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv187__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv187__8)).hashCode())));
        Attributes o_html_rv187__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv187__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv187__9)).hashCode())));
        int o_html_rv187__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv187__10)));
        boolean o_html_rv187__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv187__11);
        boolean o_html_rv187__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv187__12);
        boolean o_html_rv187__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv187__13);
        boolean o_html_rv187__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv187__14);
        boolean o_html_rv187__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv187__15);
        String o_html_rv187__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv187__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv187__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv187__19)));
        String o_html_rv187__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv187__20);
        String o_html_rv187__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv187__21);
        String o_html_rv187__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv187__22);
        String o_html_rv187__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv187__23);
        String o_html_rv187__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv187__24);
        String o_html_rv187__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv187__25);
        a.toString();
        Attributes o_html_rv187__27 = __DSPOT_invoc_3.put(__DSPOT_attribute_23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" 1wly$),bA%.UJum&)<4o=\"K[>Va&amp;1`i[aMe!@y;s?/\"", ((Attributes) (o_html_rv187__27)).toString());
        Assert.assertEquals(-104880899, ((int) (((Attributes) (o_html_rv187__27)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" 1wly$),bA%.UJum&)<4o=\"K[>Va&amp;1`i[aMe!@y;s?/\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-104880899, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" 1wly$),bA%.UJum&)<4o=\"K[>Va&amp;1`i[aMe!@y;s?/\"", ((Attributes) (o_html_rv187__8)).toString());
        Assert.assertEquals(-104880899, ((int) (((Attributes) (o_html_rv187__8)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" 1wly$),bA%.UJum&)<4o=\"K[>Va&amp;1`i[aMe!@y;s?/\"", ((Attributes) (o_html_rv187__9)).toString());
        Assert.assertEquals(-104880899, ((int) (((Attributes) (o_html_rv187__9)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv187__10)));
        Assert.assertTrue(o_html_rv187__11);
        Assert.assertTrue(o_html_rv187__12);
        Assert.assertTrue(o_html_rv187__13);
        Assert.assertFalse(o_html_rv187__14);
        Assert.assertTrue(o_html_rv187__15);
        Assert.assertEquals("There", o_html_rv187__16);
        Assert.assertEquals(1, ((int) (o_html_rv187__19)));
        Assert.assertEquals("Jsoup", o_html_rv187__20);
        Assert.assertEquals("", o_html_rv187__21);
        Assert.assertEquals("a&p", o_html_rv187__22);
        Assert.assertEquals("a&p", o_html_rv187__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv187__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv187__25);
    }

    @Test(timeout = 10000)
    public void html_rv215() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv215__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv215__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv215__3)).hashCode())));
        Attributes o_html_rv215__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv215__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv215__4)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv215__8 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv215__8)));
        boolean o_html_rv215__9 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv215__9);
        boolean o_html_rv215__10 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv215__10);
        boolean o_html_rv215__11 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv215__11);
        boolean o_html_rv215__12 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv215__12);
        boolean o_html_rv215__13 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv215__13);
        String o_html_rv215__14 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv215__14);
        Map<String, String> dataset = a.dataset();
        int o_html_rv215__17 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv215__17)));
        String o_html_rv215__18 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv215__18);
        String o_html_rv215__19 = a.get("tot");
        Assert.assertEquals("", o_html_rv215__19);
        String o_html_rv215__20 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv215__20);
        String o_html_rv215__21 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv215__21);
        String o_html_rv215__22 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv215__22);
        String o_html_rv215__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv215__23);
        a.toString();
        List<Attribute> o_html_rv215__25 = __DSPOT_invoc_5.asList();
        Assert.assertFalse(o_html_rv215__25.isEmpty());
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv215__3)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv215__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv215__4)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv215__4)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv215__8)));
        Assert.assertTrue(o_html_rv215__9);
        Assert.assertTrue(o_html_rv215__10);
        Assert.assertTrue(o_html_rv215__11);
        Assert.assertFalse(o_html_rv215__12);
        Assert.assertTrue(o_html_rv215__13);
        Assert.assertEquals("There", o_html_rv215__14);
        Assert.assertEquals(1, ((int) (o_html_rv215__17)));
        Assert.assertEquals("Jsoup", o_html_rv215__18);
        Assert.assertEquals("", o_html_rv215__19);
        Assert.assertEquals("a&p", o_html_rv215__20);
        Assert.assertEquals("a&p", o_html_rv215__21);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv215__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv215__23);
    }

    @Test(timeout = 10000)
    public void htmlnull242_failAssert2() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.put(null, "Jsoup");
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
            org.junit.Assert.fail("htmlnull242 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmlnull237() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmlnull237__3 = a.put("Tot", null);
        Assert.assertEquals(" Tot", ((Attributes) (o_htmlnull237__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_htmlnull237__3)).hashCode())));
        Attributes o_htmlnull237__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot Hello=\"There\"", ((Attributes) (o_htmlnull237__4)).toString());
        Assert.assertEquals(142394285, ((int) (((Attributes) (o_htmlnull237__4)).hashCode())));
        Attributes o_htmlnull237__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmlnull237__5)).toString());
        Assert.assertEquals(-545154661, ((int) (((Attributes) (o_htmlnull237__5)).hashCode())));
        int o_htmlnull237__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmlnull237__6)));
        boolean o_htmlnull237__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmlnull237__7);
        boolean o_htmlnull237__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmlnull237__8);
        boolean o_htmlnull237__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmlnull237__9);
        boolean o_htmlnull237__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmlnull237__10);
        boolean o_htmlnull237__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmlnull237__11);
        String o_htmlnull237__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmlnull237__12);
        Map<String, String> dataset = a.dataset();
        int o_htmlnull237__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmlnull237__15)));
        String o_htmlnull237__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmlnull237__16);
        String o_htmlnull237__17 = a.get("tot");
        Assert.assertEquals("", o_htmlnull237__17);
        String o_htmlnull237__18 = a.get("Tot");
        Assert.assertEquals("", o_htmlnull237__18);
        String o_htmlnull237__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("", o_htmlnull237__19);
        String o_htmlnull237__20 = a.html();
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", o_htmlnull237__20);
        String o_htmlnull237__21 = a.html();
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", o_htmlnull237__21);
        a.toString();
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-545154661, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmlnull237__3)).toString());
        Assert.assertEquals(-545154661, ((int) (((Attributes) (o_htmlnull237__3)).hashCode())));
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmlnull237__4)).toString());
        Assert.assertEquals(-545154661, ((int) (((Attributes) (o_htmlnull237__4)).hashCode())));
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmlnull237__5)).toString());
        Assert.assertEquals(-545154661, ((int) (((Attributes) (o_htmlnull237__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmlnull237__6)));
        Assert.assertTrue(o_htmlnull237__7);
        Assert.assertTrue(o_htmlnull237__8);
        Assert.assertTrue(o_htmlnull237__9);
        Assert.assertFalse(o_htmlnull237__10);
        Assert.assertTrue(o_htmlnull237__11);
        Assert.assertEquals("There", o_htmlnull237__12);
        Assert.assertEquals(1, ((int) (o_htmlnull237__15)));
        Assert.assertEquals("Jsoup", o_htmlnull237__16);
        Assert.assertEquals("", o_htmlnull237__17);
        Assert.assertEquals("", o_htmlnull237__18);
        Assert.assertEquals("", o_htmlnull237__19);
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", o_htmlnull237__20);
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", o_htmlnull237__21);
    }

    @Test(timeout = 10000)
    public void htmllitString31() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString31__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString31__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString31__3)).hashCode())));
        Attributes o_htmllitString31__4 = a.put("Hello", "\n");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\"", ((Attributes) (o_htmllitString31__4)).toString());
        Assert.assertEquals(-1652203800, ((int) (((Attributes) (o_htmllitString31__4)).hashCode())));
        Attributes o_htmllitString31__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString31__5)).toString());
        Assert.assertEquals(1955214550, ((int) (((Attributes) (o_htmllitString31__5)).hashCode())));
        int o_htmllitString31__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString31__6)));
        boolean o_htmllitString31__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString31__7);
        boolean o_htmllitString31__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString31__8);
        boolean o_htmllitString31__9 = a.hasKey("data-name");
        Assert.assertTrue(o_htmllitString31__9);
        boolean o_htmllitString31__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString31__10);
        boolean o_htmllitString31__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString31__11);
        String o_htmllitString31__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("\n", o_htmllitString31__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString31__15 = dataset.size();
        Assert.assertEquals(1, ((int) (o_htmllitString31__15)));
        String o_htmllitString31__16 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_htmllitString31__16);
        String o_htmllitString31__17 = a.get("tot");
        Assert.assertEquals("", o_htmllitString31__17);
        String o_htmllitString31__18 = a.get("Tot");
        Assert.assertEquals("a&p", o_htmllitString31__18);
        String o_htmllitString31__19 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_htmllitString31__19);
        String o_htmllitString31__20 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", o_htmllitString31__20);
        String o_htmllitString31__21 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", o_htmllitString31__21);
        a.toString();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1955214550, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString31__3)).toString());
        Assert.assertEquals(1955214550, ((int) (((Attributes) (o_htmllitString31__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString31__4)).toString());
        Assert.assertEquals(1955214550, ((int) (((Attributes) (o_htmllitString31__4)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_htmllitString31__5)).toString());
        Assert.assertEquals(1955214550, ((int) (((Attributes) (o_htmllitString31__5)).hashCode())));
        Assert.assertEquals(3, ((int) (o_htmllitString31__6)));
        Assert.assertTrue(o_htmllitString31__7);
        Assert.assertTrue(o_htmllitString31__8);
        Assert.assertTrue(o_htmllitString31__9);
        Assert.assertFalse(o_htmllitString31__10);
        Assert.assertTrue(o_htmllitString31__11);
        Assert.assertEquals("\n", o_htmllitString31__12);
        Assert.assertEquals(1, ((int) (o_htmllitString31__15)));
        Assert.assertEquals("Jsoup", o_htmllitString31__16);
        Assert.assertEquals("", o_htmllitString31__17);
        Assert.assertEquals("a&p", o_htmllitString31__18);
        Assert.assertEquals("a&p", o_htmllitString31__19);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", o_htmllitString31__20);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", o_htmllitString31__21);
    }

    @Test(timeout = 10000)
    public void html_rv218() throws Exception {
        Object __DSPOT_o_40 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv218__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv218__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv218__5)).hashCode())));
        Attributes o_html_rv218__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv218__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv218__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv218__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv218__10)));
        boolean o_html_rv218__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv218__11);
        boolean o_html_rv218__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv218__12);
        boolean o_html_rv218__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv218__13);
        boolean o_html_rv218__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv218__14);
        boolean o_html_rv218__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv218__15);
        String o_html_rv218__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv218__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv218__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv218__19)));
        String o_html_rv218__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv218__20);
        String o_html_rv218__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv218__21);
        String o_html_rv218__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv218__22);
        String o_html_rv218__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv218__23);
        String o_html_rv218__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv218__24);
        String o_html_rv218__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv218__25);
        a.toString();
        boolean o_html_rv218__27 = __DSPOT_invoc_5.equals(__DSPOT_o_40);
        Assert.assertFalse(o_html_rv218__27);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv218__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv218__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv218__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv218__6)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv218__10)));
        Assert.assertTrue(o_html_rv218__11);
        Assert.assertTrue(o_html_rv218__12);
        Assert.assertTrue(o_html_rv218__13);
        Assert.assertFalse(o_html_rv218__14);
        Assert.assertTrue(o_html_rv218__15);
        Assert.assertEquals("There", o_html_rv218__16);
        Assert.assertEquals(1, ((int) (o_html_rv218__19)));
        Assert.assertEquals("Jsoup", o_html_rv218__20);
        Assert.assertEquals("", o_html_rv218__21);
        Assert.assertEquals("a&p", o_html_rv218__22);
        Assert.assertEquals("a&p", o_html_rv218__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv218__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv218__25);
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
    public void htmllitString37() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString37__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString37__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString37__3)).hashCode())));
        Attributes o_htmllitString37__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString37__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString37__4)).hashCode())));
        Attributes o_htmllitString37__5 = a.put("r6#-VtX(r", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" r6#-VtX(r=\"Jsoup\"", ((Attributes) (o_htmllitString37__5)).toString());
        Assert.assertEquals(580398872, ((int) (((Attributes) (o_htmllitString37__5)).hashCode())));
        int o_htmllitString37__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString37__6)));
        boolean o_htmllitString37__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString37__7);
        boolean o_htmllitString37__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString37__8);
        boolean o_htmllitString37__9 = a.hasKey("data-name");
        Assert.assertFalse(o_htmllitString37__9);
        boolean o_htmllitString37__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString37__10);
        boolean o_htmllitString37__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString37__11);
        String o_htmllitString37__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString37__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString37__15 = dataset.size();
        Assert.assertEquals(0, ((int) (o_htmllitString37__15)));
        dataset.get("name");
        a.get("tot");
        a.get("Tot");
        a.getIgnoreCase("tot");
        a.html();
        a.html();
        a.toString();
    }

    @Test(timeout = 10000)
    public void htmlnull236_failAssert14() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put(null, "a&p");
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
            org.junit.Assert.fail("htmlnull236 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void html_rv216() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv216__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv216__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv216__3)).hashCode())));
        Attributes o_html_rv216__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv216__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv216__4)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv216__8 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv216__8)));
        boolean o_html_rv216__9 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv216__9);
        boolean o_html_rv216__10 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv216__10);
        boolean o_html_rv216__11 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv216__11);
        boolean o_html_rv216__12 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv216__12);
        boolean o_html_rv216__13 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv216__13);
        String o_html_rv216__14 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv216__14);
        Map<String, String> dataset = a.dataset();
        int o_html_rv216__17 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv216__17)));
        String o_html_rv216__18 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv216__18);
        String o_html_rv216__19 = a.get("tot");
        Assert.assertEquals("", o_html_rv216__19);
        String o_html_rv216__20 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv216__20);
        String o_html_rv216__21 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv216__21);
        String o_html_rv216__22 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv216__22);
        String o_html_rv216__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv216__23);
        a.toString();
        Attributes o_html_rv216__25 = __DSPOT_invoc_5.clone();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv216__25)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv216__25)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-758045610, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv216__3)).toString());
        Assert.assertEquals(-758045610, ((int) (((Attributes) (o_html_rv216__3)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv216__4)).toString());
        Assert.assertEquals(-758045610, ((int) (((Attributes) (o_html_rv216__4)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv216__8)));
        Assert.assertTrue(o_html_rv216__9);
        Assert.assertTrue(o_html_rv216__10);
        Assert.assertTrue(o_html_rv216__11);
        Assert.assertFalse(o_html_rv216__12);
        Assert.assertTrue(o_html_rv216__13);
        Assert.assertEquals("There", o_html_rv216__14);
        Assert.assertEquals(1, ((int) (o_html_rv216__17)));
        Assert.assertEquals("Jsoup", o_html_rv216__18);
        Assert.assertEquals("", o_html_rv216__19);
        Assert.assertEquals("a&p", o_html_rv216__20);
        Assert.assertEquals("a&p", o_html_rv216__21);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv216__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv216__23);
    }

    @Test(timeout = 10000)
    public void html_rv205() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv205__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv205__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv205__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_html_rv205__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv205__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv205__7)).hashCode())));
        int o_html_rv205__8 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv205__8)));
        boolean o_html_rv205__9 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv205__9);
        boolean o_html_rv205__10 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv205__10);
        boolean o_html_rv205__11 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv205__11);
        boolean o_html_rv205__12 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv205__12);
        boolean o_html_rv205__13 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv205__13);
        String o_html_rv205__14 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv205__14);
        Map<String, String> dataset = a.dataset();
        int o_html_rv205__17 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv205__17)));
        String o_html_rv205__18 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv205__18);
        String o_html_rv205__19 = a.get("tot");
        Assert.assertEquals("", o_html_rv205__19);
        String o_html_rv205__20 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv205__20);
        String o_html_rv205__21 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv205__21);
        String o_html_rv205__22 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv205__22);
        String o_html_rv205__23 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv205__23);
        a.toString();
        __DSPOT_invoc_4.normalize();
        Assert.assertEquals(" tot=\"a&amp;p\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(541005968, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"a&amp;p\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv205__3)).toString());
        Assert.assertEquals(541005968, ((int) (((Attributes) (o_html_rv205__3)).hashCode())));
        Assert.assertEquals(" tot=\"a&amp;p\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv205__7)).toString());
        Assert.assertEquals(541005968, ((int) (((Attributes) (o_html_rv205__7)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv205__8)));
        Assert.assertTrue(o_html_rv205__9);
        Assert.assertTrue(o_html_rv205__10);
        Assert.assertTrue(o_html_rv205__11);
        Assert.assertFalse(o_html_rv205__12);
        Assert.assertTrue(o_html_rv205__13);
        Assert.assertEquals("There", o_html_rv205__14);
        Assert.assertEquals(1, ((int) (o_html_rv205__17)));
        Assert.assertEquals("Jsoup", o_html_rv205__18);
        Assert.assertEquals("", o_html_rv205__19);
        Assert.assertEquals("a&p", o_html_rv205__20);
        Assert.assertEquals("a&p", o_html_rv205__21);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv205__22);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv205__23);
    }

    @Test(timeout = 10000)
    public void html_rv227() throws Exception {
        boolean __DSPOT_value_46 = true;
        String __DSPOT_key_45 = "N4Sb)kE+#PmjF|_k>]VY";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv227__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv227__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv227__5)).hashCode())));
        Attributes o_html_rv227__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_html_rv227__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_html_rv227__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        int o_html_rv227__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv227__10)));
        boolean o_html_rv227__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv227__11);
        boolean o_html_rv227__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv227__12);
        boolean o_html_rv227__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv227__13);
        boolean o_html_rv227__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv227__14);
        boolean o_html_rv227__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv227__15);
        String o_html_rv227__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv227__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv227__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv227__19)));
        String o_html_rv227__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv227__20);
        String o_html_rv227__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv227__21);
        String o_html_rv227__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv227__22);
        String o_html_rv227__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv227__23);
        String o_html_rv227__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv227__24);
        String o_html_rv227__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv227__25);
        a.toString();
        Attributes o_html_rv227__27 = __DSPOT_invoc_5.put(__DSPOT_key_45, __DSPOT_value_46);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" N4Sb)kE+#PmjF|_k>]VY", ((Attributes) (o_html_rv227__27)).toString());
        Assert.assertEquals(2064508294, ((int) (((Attributes) (o_html_rv227__27)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" N4Sb)kE+#PmjF|_k>]VY", ((Attributes) (a)).toString());
        Assert.assertEquals(2064508294, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" N4Sb)kE+#PmjF|_k>]VY", ((Attributes) (o_html_rv227__5)).toString());
        Assert.assertEquals(2064508294, ((int) (((Attributes) (o_html_rv227__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\" N4Sb)kE+#PmjF|_k>]VY", ((Attributes) (o_html_rv227__6)).toString());
        Assert.assertEquals(2064508294, ((int) (((Attributes) (o_html_rv227__6)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv227__10)));
        Assert.assertTrue(o_html_rv227__11);
        Assert.assertTrue(o_html_rv227__12);
        Assert.assertTrue(o_html_rv227__13);
        Assert.assertFalse(o_html_rv227__14);
        Assert.assertTrue(o_html_rv227__15);
        Assert.assertEquals("There", o_html_rv227__16);
        Assert.assertEquals(1, ((int) (o_html_rv227__19)));
        Assert.assertEquals("Jsoup", o_html_rv227__20);
        Assert.assertEquals("", o_html_rv227__21);
        Assert.assertEquals("a&p", o_html_rv227__22);
        Assert.assertEquals("a&p", o_html_rv227__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv227__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv227__25);
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
    public void html_rv206() throws Exception {
        boolean __DSPOT_value_33 = false;
        String __DSPOT_key_32 = "4gdsL9rC)A6fdF&0xT!&";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_html_rv206__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_html_rv206__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_html_rv206__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_html_rv206__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv206__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv206__9)).hashCode())));
        int o_html_rv206__10 = a.size();
        Assert.assertEquals(3, ((int) (o_html_rv206__10)));
        boolean o_html_rv206__11 = a.hasKey("Tot");
        Assert.assertTrue(o_html_rv206__11);
        boolean o_html_rv206__12 = a.hasKey("Hello");
        Assert.assertTrue(o_html_rv206__12);
        boolean o_html_rv206__13 = a.hasKey("data-name");
        Assert.assertTrue(o_html_rv206__13);
        boolean o_html_rv206__14 = a.hasKey("tot");
        Assert.assertFalse(o_html_rv206__14);
        boolean o_html_rv206__15 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_html_rv206__15);
        String o_html_rv206__16 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_html_rv206__16);
        Map<String, String> dataset = a.dataset();
        int o_html_rv206__19 = dataset.size();
        Assert.assertEquals(1, ((int) (o_html_rv206__19)));
        String o_html_rv206__20 = dataset.get("name");
        Assert.assertEquals("Jsoup", o_html_rv206__20);
        String o_html_rv206__21 = a.get("tot");
        Assert.assertEquals("", o_html_rv206__21);
        String o_html_rv206__22 = a.get("Tot");
        Assert.assertEquals("a&p", o_html_rv206__22);
        String o_html_rv206__23 = a.getIgnoreCase("tot");
        Assert.assertEquals("a&p", o_html_rv206__23);
        String o_html_rv206__24 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv206__24);
        String o_html_rv206__25 = a.html();
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv206__25);
        a.toString();
        Attributes o_html_rv206__27 = __DSPOT_invoc_4.put(__DSPOT_key_32, __DSPOT_value_33);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv206__27)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv206__27)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv206__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv206__5)).hashCode())));
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_html_rv206__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_html_rv206__9)).hashCode())));
        Assert.assertEquals(3, ((int) (o_html_rv206__10)));
        Assert.assertTrue(o_html_rv206__11);
        Assert.assertTrue(o_html_rv206__12);
        Assert.assertTrue(o_html_rv206__13);
        Assert.assertFalse(o_html_rv206__14);
        Assert.assertTrue(o_html_rv206__15);
        Assert.assertEquals("There", o_html_rv206__16);
        Assert.assertEquals(1, ((int) (o_html_rv206__19)));
        Assert.assertEquals("Jsoup", o_html_rv206__20);
        Assert.assertEquals("", o_html_rv206__21);
        Assert.assertEquals("a&p", o_html_rv206__22);
        Assert.assertEquals("a&p", o_html_rv206__23);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv206__24);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", o_html_rv206__25);
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
    public void htmllitString39() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_htmllitString39__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmllitString39__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmllitString39__3)).hashCode())));
        Attributes o_htmllitString39__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmllitString39__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmllitString39__4)).hashCode())));
        Attributes o_htmllitString39__5 = a.put("\n", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" \n=\"Jsoup\"", ((Attributes) (o_htmllitString39__5)).toString());
        Assert.assertEquals(891080396, ((int) (((Attributes) (o_htmllitString39__5)).hashCode())));
        int o_htmllitString39__6 = a.size();
        Assert.assertEquals(3, ((int) (o_htmllitString39__6)));
        boolean o_htmllitString39__7 = a.hasKey("Tot");
        Assert.assertTrue(o_htmllitString39__7);
        boolean o_htmllitString39__8 = a.hasKey("Hello");
        Assert.assertTrue(o_htmllitString39__8);
        boolean o_htmllitString39__9 = a.hasKey("data-name");
        Assert.assertFalse(o_htmllitString39__9);
        boolean o_htmllitString39__10 = a.hasKey("tot");
        Assert.assertFalse(o_htmllitString39__10);
        boolean o_htmllitString39__11 = a.hasKeyIgnoreCase("tot");
        Assert.assertTrue(o_htmllitString39__11);
        String o_htmllitString39__12 = a.getIgnoreCase("hEllo");
        Assert.assertEquals("There", o_htmllitString39__12);
        Map<String, String> dataset = a.dataset();
        int o_htmllitString39__15 = dataset.size();
        Assert.assertEquals(0, ((int) (o_htmllitString39__15)));
        dataset.get("name");
        a.get("tot");
        a.get("Tot");
        a.getIgnoreCase("tot");
        a.html();
        a.html();
        a.toString();
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
    public void htmllitString22_failAssert8() throws Exception {
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
    public void htmlnull273_failAssert1null8756_failAssert18() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                a.put("Tot", "a&p");
                a.put("Hello", "There");
                a.put(null, "Jsoup");
                a.size();
                a.hasKey("Tot");
                a.hasKey("Hello");
                a.hasKey("data-name");
                a.hasKey("tot");
                a.hasKeyIgnoreCase("tot");
                a.getIgnoreCase("hEllo");
                Map<String, String> dataset = null;
                dataset.size();
                dataset.get("name");
                a.get("tot");
                a.get("Tot");
                a.getIgnoreCase("tot");
                a.html();
                a.html();
                a.toString();
                org.junit.Assert.fail("htmlnull273 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("htmlnull273_failAssert1null8756 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void htmlnull273_failAssert1null8948() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_htmlnull273_failAssert1null8948__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmlnull273_failAssert1null8948__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmlnull273_failAssert1null8948__5)).hashCode())));
            Attributes o_htmlnull273_failAssert1null8948__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmlnull273_failAssert1null8948__6)).toString());
            Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmlnull273_failAssert1null8948__6)).hashCode())));
            Attributes o_htmlnull273_failAssert1null8948__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_htmlnull273_failAssert1null8948__7)).toString());
            Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_htmlnull273_failAssert1null8948__7)).hashCode())));
            int o_htmlnull273_failAssert1null8948__8 = a.size();
            Assert.assertEquals(3, ((int) (o_htmlnull273_failAssert1null8948__8)));
            boolean o_htmlnull273_failAssert1null8948__9 = a.hasKey("Tot");
            Assert.assertTrue(o_htmlnull273_failAssert1null8948__9);
            boolean o_htmlnull273_failAssert1null8948__10 = a.hasKey("Hello");
            Assert.assertTrue(o_htmlnull273_failAssert1null8948__10);
            boolean o_htmlnull273_failAssert1null8948__11 = a.hasKey("data-name");
            Assert.assertTrue(o_htmlnull273_failAssert1null8948__11);
            boolean o_htmlnull273_failAssert1null8948__12 = a.hasKey("tot");
            Assert.assertFalse(o_htmlnull273_failAssert1null8948__12);
            boolean o_htmlnull273_failAssert1null8948__13 = a.hasKeyIgnoreCase("tot");
            Assert.assertTrue(o_htmlnull273_failAssert1null8948__13);
            String o_htmlnull273_failAssert1null8948__14 = a.getIgnoreCase("hEllo");
            Assert.assertEquals("There", o_htmlnull273_failAssert1null8948__14);
            Map<String, String> dataset = null;
            dataset.size();
            dataset.get("name");
            a.get("tot");
            a.get("Tot");
            a.getIgnoreCase("tot");
            a.html();
            a.html();
            a.toString();
            org.junit.Assert.fail("htmlnull273 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void htmlnull273_failAssert1null8756_failAssert18litString70231() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_htmlnull273_failAssert1null8756_failAssert18litString70231__7 = a.put("Tot", "a&p");
                Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18litString70231__7)).toString());
                Assert.assertEquals(-924093553, ((int) (((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18litString70231__7)).hashCode())));
                Attributes o_htmlnull273_failAssert1null8756_failAssert18litString70231__8 = a.put("Hello", "There");
                Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18litString70231__8)).toString());
                Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18litString70231__8)).hashCode())));
                a.put(null, "Jsoup");
                a.size();
                a.hasKey("Tot");
                a.hasKey("Hello");
                a.hasKey("data-name");
                a.hasKey("tot");
                a.hasKeyIgnoreCase("tot");
                a.getIgnoreCase("hEllo");
                Map<String, String> dataset = null;
                dataset.size();
                dataset.get("name");
                a.get("tt");
                a.get("Tot");
                a.getIgnoreCase("tot");
                a.html();
                a.html();
                a.toString();
                org.junit.Assert.fail("htmlnull273 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("htmlnull273_failAssert1null8756 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void htmlnull273_failAssert1null8820_failAssert17null70961() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                a.put(null, "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                a.size();
                a.hasKey("Tot");
                a.hasKey("Hello");
                a.hasKey(null);
                a.hasKey("tot");
                a.hasKeyIgnoreCase("tot");
                a.getIgnoreCase("hEllo");
                Map<String, String> dataset = null;
                dataset.size();
                dataset.get("name");
                a.get("tot");
                a.get("Tot");
                a.getIgnoreCase("tot");
                a.html();
                a.html();
                a.toString();
                org.junit.Assert.fail("htmlnull273 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("htmlnull273_failAssert1null8820 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void htmlnull273_failAssert1null8756_failAssert18null71110() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_htmlnull273_failAssert1null8756_failAssert18null71110__7 = a.put("Tot", null);
                Assert.assertEquals(" Tot", ((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18null71110__7)).toString());
                Assert.assertEquals(555415706, ((int) (((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18null71110__7)).hashCode())));
                Attributes o_htmlnull273_failAssert1null8756_failAssert18null71110__8 = a.put("Hello", "There");
                Assert.assertEquals(" Tot Hello=\"There\"", ((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18null71110__8)).toString());
                Assert.assertEquals(142394285, ((int) (((Attributes) (o_htmlnull273_failAssert1null8756_failAssert18null71110__8)).hashCode())));
                a.put(null, "Jsoup");
                a.size();
                a.hasKey("Tot");
                a.hasKey("Hello");
                a.hasKey("data-name");
                a.hasKey("tot");
                a.hasKeyIgnoreCase("tot");
                a.getIgnoreCase("hEllo");
                Map<String, String> dataset = null;
                dataset.size();
                dataset.get("name");
                a.get("tot");
                a.get("Tot");
                a.getIgnoreCase("tot");
                a.html();
                a.html();
                a.toString();
                org.junit.Assert.fail("htmlnull273 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("htmlnull273_failAssert1null8756 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300221() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablenull300221__3 = a.put("Tot", null);
        Assert.assertEquals(" Tot", ((Attributes) (o_testIteratorRemovablenull300221__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorRemovablenull300221__3)).hashCode())));
        Attributes o_testIteratorRemovablenull300221__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot Hello=\"There\"", ((Attributes) (o_testIteratorRemovablenull300221__4)).toString());
        Assert.assertEquals(142394285, ((int) (((Attributes) (o_testIteratorRemovablenull300221__4)).hashCode())));
        Attributes o_testIteratorRemovablenull300221__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300221__5)).toString());
        Assert.assertEquals(-545154661, ((int) (((Attributes) (o_testIteratorRemovablenull300221__5)).hashCode())));
        boolean o_testIteratorRemovablenull300221__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablenull300221__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot", ((Attribute) (attr)).toString());
        Assert.assertEquals(2612711, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertNull(((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablenull300221__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300221__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablenull300221__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300221__18)));
        String o_testIteratorRemovablenull300221__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablenull300221__19);
        boolean o_testIteratorRemovablenull300221__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablenull300221__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300221__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablenull300221__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300221__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablenull300221__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300221__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablenull300221__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablenull300221__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300221__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300221__18)));
        Assert.assertEquals("There", o_testIteratorRemovablenull300221__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString300041_failAssert174() throws Exception {
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
            org.junit.Assert.fail("testIteratorRemovablelitString300041 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg300153() throws Exception {
        String __DSPOT_val_62158 = "6h,$u<;57}4E,szj!.W]";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg300153__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg300153__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg300153__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg300153__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg300153__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg300153__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg300153__6 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300153__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg300153__6)).hashCode())));
        boolean o_testIteratorRemovable_mg300153__7 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg300153__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg300153__14 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300153__14)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg300153__19 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300153__19)));
        String o_testIteratorRemovable_mg300153__20 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg300153__20);
        boolean o_testIteratorRemovable_mg300153__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg300153__21);
        String o_testIteratorRemovable_mg300153__22 = attr.setValue(__DSPOT_val_62158);
        Assert.assertEquals("There", o_testIteratorRemovable_mg300153__22);
        Assert.assertEquals(" Hello=\"6h,$u<;57}4E,szj!.W]\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1902850075, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"6h,$u<;57}4E,szj!.W]\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300153__4)).toString());
        Assert.assertEquals(1902850075, ((int) (((Attributes) (o_testIteratorRemovable_mg300153__4)).hashCode())));
        Assert.assertEquals(" Hello=\"6h,$u<;57}4E,szj!.W]\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300153__5)).toString());
        Assert.assertEquals(1902850075, ((int) (((Attributes) (o_testIteratorRemovable_mg300153__5)).hashCode())));
        Assert.assertEquals(" Hello=\"6h,$u<;57}4E,szj!.W]\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300153__6)).toString());
        Assert.assertEquals(1902850075, ((int) (((Attributes) (o_testIteratorRemovable_mg300153__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg300153__7);
        Assert.assertEquals("Hello=\"6h,$u<;57}4E,szj!.W]\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(95209658, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("6h,$u<;57}4E,szj!.W]", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300153__14)));
        Assert.assertEquals("Hello=\"6h,$u<;57}4E,szj!.W]\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(95209658, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("6h,$u<;57}4E,szj!.W]", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300153__19)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg300153__20);
        Assert.assertFalse(o_testIteratorRemovable_mg300153__21);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300224() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablenull300224__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300224__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300224__3)).hashCode())));
        Attributes o_testIteratorRemovablenull300224__4 = a.put("Hello", null);
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello", ((Attributes) (o_testIteratorRemovablenull300224__4)).toString());
        Assert.assertEquals(-1652213410, ((int) (((Attributes) (o_testIteratorRemovablenull300224__4)).hashCode())));
        Attributes o_testIteratorRemovablenull300224__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300224__5)).toString());
        Assert.assertEquals(1955204940, ((int) (((Attributes) (o_testIteratorRemovablenull300224__5)).hashCode())));
        boolean o_testIteratorRemovablenull300224__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablenull300224__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablenull300224__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300224__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2137068146, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertNull(((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablenull300224__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300224__18)));
        String o_testIteratorRemovablenull300224__19 = a.get("Hello");
        Assert.assertEquals("", o_testIteratorRemovablenull300224__19);
        boolean o_testIteratorRemovablenull300224__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablenull300224__20);
        Assert.assertEquals(" Hello data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-906564921, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300224__3)).toString());
        Assert.assertEquals(-906564921, ((int) (((Attributes) (o_testIteratorRemovablenull300224__3)).hashCode())));
        Assert.assertEquals(" Hello data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300224__4)).toString());
        Assert.assertEquals(-906564921, ((int) (((Attributes) (o_testIteratorRemovablenull300224__4)).hashCode())));
        Assert.assertEquals(" Hello data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300224__5)).toString());
        Assert.assertEquals(-906564921, ((int) (((Attributes) (o_testIteratorRemovablenull300224__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablenull300224__6);
        Assert.assertEquals("Hello", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2137068146, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertNull(((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300224__13)));
        Assert.assertEquals("Hello", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2137068146, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertNull(((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300224__18)));
        Assert.assertEquals("", o_testIteratorRemovablenull300224__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300190() throws Exception {
        boolean __DSPOT_value_62179 = true;
        String __DSPOT_key_62178 = "c>h%en.!jGTq7P0,kXiy";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300190__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300190__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300190__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_testIteratorRemovable_rv300190__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300190__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv300190__9)).hashCode())));
        boolean o_testIteratorRemovable_rv300190__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300190__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300190__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300190__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300190__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300190__22)));
        String o_testIteratorRemovable_rv300190__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300190__23);
        boolean o_testIteratorRemovable_rv300190__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300190__24);
        Attributes o_testIteratorRemovable_rv300190__25 = __DSPOT_invoc_4.put(__DSPOT_key_62178, __DSPOT_value_62179);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" c>h%en.!jGTq7P0,kXiy", ((Attributes) (o_testIteratorRemovable_rv300190__25)).toString());
        Assert.assertEquals(1415168004, ((int) (((Attributes) (o_testIteratorRemovable_rv300190__25)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" c>h%en.!jGTq7P0,kXiy", ((Attributes) (a)).toString());
        Assert.assertEquals(1415168004, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" c>h%en.!jGTq7P0,kXiy", ((Attributes) (o_testIteratorRemovable_rv300190__5)).toString());
        Assert.assertEquals(1415168004, ((int) (((Attributes) (o_testIteratorRemovable_rv300190__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" c>h%en.!jGTq7P0,kXiy", ((Attributes) (o_testIteratorRemovable_rv300190__9)).toString());
        Assert.assertEquals(1415168004, ((int) (((Attributes) (o_testIteratorRemovable_rv300190__9)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300190__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300190__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300190__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300190__23);
        Assert.assertFalse(o_testIteratorRemovable_rv300190__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300228() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablenull300228__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300228__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300228__3)).hashCode())));
        Attributes o_testIteratorRemovablenull300228__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablenull300228__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovablenull300228__4)).hashCode())));
        Attributes o_testIteratorRemovablenull300228__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300228__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovablenull300228__5)).hashCode())));
        boolean o_testIteratorRemovablenull300228__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablenull300228__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablenull300228__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300228__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablenull300228__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300228__18)));
        String o_testIteratorRemovablenull300228__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablenull300228__19);
        boolean o_testIteratorRemovablenull300228__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablenull300228__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300228__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablenull300228__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300228__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablenull300228__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300228__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablenull300228__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablenull300228__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300228__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablenull300228__18)));
        Assert.assertEquals("There", o_testIteratorRemovablenull300228__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString300065() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString300065__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString300065__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablelitString300065__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString300065__4 = a.put("Hello", "\n");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\"", ((Attributes) (o_testIteratorRemovablelitString300065__4)).toString());
        Assert.assertEquals(-1652203800, ((int) (((Attributes) (o_testIteratorRemovablelitString300065__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString300065__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300065__5)).toString());
        Assert.assertEquals(1955214550, ((int) (((Attributes) (o_testIteratorRemovablelitString300065__5)).hashCode())));
        boolean o_testIteratorRemovablelitString300065__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablelitString300065__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString300065__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300065__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2137068136, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString300065__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300065__18)));
        String o_testIteratorRemovablelitString300065__19 = a.get("Hello");
        Assert.assertEquals("\n", o_testIteratorRemovablelitString300065__19);
        boolean o_testIteratorRemovablelitString300065__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString300065__20);
        Assert.assertEquals(" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-906267011, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300065__3)).toString());
        Assert.assertEquals(-906267011, ((int) (((Attributes) (o_testIteratorRemovablelitString300065__3)).hashCode())));
        Assert.assertEquals(" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300065__4)).toString());
        Assert.assertEquals(-906267011, ((int) (((Attributes) (o_testIteratorRemovablelitString300065__4)).hashCode())));
        Assert.assertEquals(" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300065__5)).toString());
        Assert.assertEquals(-906267011, ((int) (((Attributes) (o_testIteratorRemovablelitString300065__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablelitString300065__6);
        Assert.assertEquals("Hello=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2137068136, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300065__13)));
        Assert.assertEquals("Hello=\"\n\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2137068136, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("\n", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300065__18)));
        Assert.assertEquals("\n", o_testIteratorRemovablelitString300065__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString300042() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString300042__3 = a.put("\n", "a&p");
        Assert.assertEquals(" \n=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString300042__3)).toString());
        Assert.assertEquals(-1440720416, ((int) (((Attributes) (o_testIteratorRemovablelitString300042__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString300042__4 = a.put("Hello", "There");
        Assert.assertEquals(" \n=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString300042__4)).toString());
        Assert.assertEquals(-1853741837, ((int) (((Attributes) (o_testIteratorRemovablelitString300042__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString300042__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" \n=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300042__5)).toString());
        Assert.assertEquals(1753676513, ((int) (((Attributes) (o_testIteratorRemovablelitString300042__5)).hashCode())));
        boolean o_testIteratorRemovablelitString300042__6 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString300042__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(94507, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString300042__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300042__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString300042__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300042__18)));
        String o_testIteratorRemovablelitString300042__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablelitString300042__19);
        boolean o_testIteratorRemovablelitString300042__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString300042__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300042__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString300042__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300042__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString300042__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300042__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString300042__5)).hashCode())));
        Assert.assertFalse(o_testIteratorRemovablelitString300042__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300042__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300042__18)));
        Assert.assertEquals("There", o_testIteratorRemovablelitString300042__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300202() throws Exception {
        Attributes __DSPOT_o_62186 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300202__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300202__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300202__5)).hashCode())));
        Attributes o_testIteratorRemovable_rv300202__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv300202__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv300202__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        boolean o_testIteratorRemovable_rv300202__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300202__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300202__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300202__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300202__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300202__22)));
        String o_testIteratorRemovable_rv300202__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300202__23);
        boolean o_testIteratorRemovable_rv300202__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300202__24);
        boolean o_testIteratorRemovable_rv300202__25 = __DSPOT_invoc_5.equals(__DSPOT_o_62186);
        Assert.assertFalse(o_testIteratorRemovable_rv300202__25);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300202__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300202__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300202__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300202__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300202__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300202__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300202__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300202__23);
        Assert.assertFalse(o_testIteratorRemovable_rv300202__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300169() throws Exception {
        boolean __DSPOT_value_62166 = false;
        String __DSPOT_key_62165 = "R[u#O=2/?CCPG?f1@}D_";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorRemovable_rv300169__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv300169__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv300169__8)).hashCode())));
        Attributes o_testIteratorRemovable_rv300169__9 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300169__9)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv300169__9)).hashCode())));
        boolean o_testIteratorRemovable_rv300169__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300169__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300169__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300169__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300169__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300169__22)));
        String o_testIteratorRemovable_rv300169__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300169__23);
        boolean o_testIteratorRemovable_rv300169__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300169__24);
        Attributes o_testIteratorRemovable_rv300169__25 = __DSPOT_invoc_3.put(__DSPOT_key_62165, __DSPOT_value_62166);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300169__25)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300169__25)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300169__8)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300169__8)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300169__9)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300169__9)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300169__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300169__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300169__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300169__23);
        Assert.assertFalse(o_testIteratorRemovable_rv300169__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300189() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300189__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300189__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300189__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_testIteratorRemovable_rv300189__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300189__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv300189__7)).hashCode())));
        boolean o_testIteratorRemovable_rv300189__8 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300189__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300189__15 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300189__15)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300189__20 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300189__20)));
        String o_testIteratorRemovable_rv300189__21 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300189__21);
        boolean o_testIteratorRemovable_rv300189__22 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300189__22);
        __DSPOT_invoc_4.normalize();
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300189__3)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_testIteratorRemovable_rv300189__3)).hashCode())));
        Assert.assertEquals(" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300189__7)).toString());
        Assert.assertEquals(-1732415965, ((int) (((Attributes) (o_testIteratorRemovable_rv300189__7)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300189__8);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300189__15)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300189__20)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300189__21);
        Assert.assertFalse(o_testIteratorRemovable_rv300189__22);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString300049() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString300049__3 = a.put("Tot", "");
        Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_testIteratorRemovablelitString300049__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorRemovablelitString300049__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString300049__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString300049__4)).toString());
        Assert.assertEquals(142394285, ((int) (((Attributes) (o_testIteratorRemovablelitString300049__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString300049__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300049__5)).toString());
        Assert.assertEquals(-545154661, ((int) (((Attributes) (o_testIteratorRemovablelitString300049__5)).hashCode())));
        boolean o_testIteratorRemovablelitString300049__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablelitString300049__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2612711, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString300049__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300049__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString300049__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300049__18)));
        String o_testIteratorRemovablelitString300049__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablelitString300049__19);
        boolean o_testIteratorRemovablelitString300049__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString300049__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300049__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString300049__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300049__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString300049__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300049__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString300049__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablelitString300049__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300049__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300049__18)));
        Assert.assertEquals("There", o_testIteratorRemovablelitString300049__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg300147() throws Exception {
        Object __DSPOT_o_62156 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg300147__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg300147__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg300147__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg300147__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg300147__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg300147__6)).hashCode())));
        Attributes o_testIteratorRemovable_mg300147__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300147__7)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg300147__7)).hashCode())));
        boolean o_testIteratorRemovable_mg300147__8 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg300147__8);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg300147__15 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300147__15)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg300147__20 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300147__20)));
        String o_testIteratorRemovable_mg300147__21 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg300147__21);
        boolean o_testIteratorRemovable_mg300147__22 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg300147__22);
        boolean o_testIteratorRemovable_mg300147__23 = attr.equals(__DSPOT_o_62156);
        Assert.assertFalse(o_testIteratorRemovable_mg300147__23);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300147__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300147__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300147__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300147__6)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300147__7)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300147__7)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg300147__8);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300147__15)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300147__20)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg300147__21);
        Assert.assertFalse(o_testIteratorRemovable_mg300147__22);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300183() throws Exception {
        String __DSPOT_key_62175 = "DDtrxn>7ZC*WycT3wjY+";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300183__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300183__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300183__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_testIteratorRemovable_rv300183__8 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300183__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv300183__8)).hashCode())));
        boolean o_testIteratorRemovable_rv300183__9 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300183__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300183__16 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300183__16)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300183__21 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300183__21)));
        String o_testIteratorRemovable_rv300183__22 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300183__22);
        boolean o_testIteratorRemovable_rv300183__23 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300183__23);
        String o_testIteratorRemovable_rv300183__24 = __DSPOT_invoc_4.getIgnoreCase(__DSPOT_key_62175);
        Assert.assertEquals("", o_testIteratorRemovable_rv300183__24);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300183__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300183__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300183__8)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300183__8)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300183__9);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300183__16)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300183__21)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300183__22);
        Assert.assertFalse(o_testIteratorRemovable_rv300183__23);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg300127() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg300127__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg300127__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg300127__3)).hashCode())));
        Attributes o_testIteratorRemovable_mg300127__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg300127__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg300127__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg300127__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300127__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg300127__5)).hashCode())));
        boolean o_testIteratorRemovable_mg300127__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg300127__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg300127__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300127__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg300127__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300127__18)));
        String o_testIteratorRemovable_mg300127__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg300127__19);
        boolean o_testIteratorRemovable_mg300127__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg300127__20);
        List<Attribute> o_testIteratorRemovable_mg300127__21 = a.asList();
        Assert.assertFalse(o_testIteratorRemovable_mg300127__21.isEmpty());
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300127__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300127__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300127__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300127__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300127__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300127__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg300127__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300127__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300127__18)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg300127__19);
        Assert.assertFalse(o_testIteratorRemovable_mg300127__20);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg300128() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg300128__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg300128__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg300128__3)).hashCode())));
        Attributes o_testIteratorRemovable_mg300128__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg300128__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg300128__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg300128__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300128__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg300128__5)).hashCode())));
        boolean o_testIteratorRemovable_mg300128__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg300128__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg300128__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300128__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg300128__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300128__18)));
        String o_testIteratorRemovable_mg300128__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg300128__19);
        boolean o_testIteratorRemovable_mg300128__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg300128__20);
        Attributes o_testIteratorRemovable_mg300128__21 = a.clone();
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300128__21)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300128__21)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300128__3)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (o_testIteratorRemovable_mg300128__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300128__4)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (o_testIteratorRemovable_mg300128__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300128__5)).toString());
        Assert.assertEquals(934361667, ((int) (((Attributes) (o_testIteratorRemovable_mg300128__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg300128__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300128__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300128__18)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg300128__19);
        Assert.assertFalse(o_testIteratorRemovable_mg300128__20);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300223_failAssert168() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put(null, "There");
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
            org.junit.Assert.fail("testIteratorRemovablenull300223 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300220_failAssert171() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put(null, "a&p");
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
            org.junit.Assert.fail("testIteratorRemovablenull300220 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300185() throws Exception {
        String __DSPOT_key_62177 = "M+bR<cs!X(QNCmkWqOFK";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300185__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300185__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300185__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        Attributes o_testIteratorRemovable_rv300185__8 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300185__8)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_rv300185__8)).hashCode())));
        boolean o_testIteratorRemovable_rv300185__9 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300185__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300185__16 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300185__16)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300185__21 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300185__21)));
        String o_testIteratorRemovable_rv300185__22 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300185__22);
        boolean o_testIteratorRemovable_rv300185__23 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300185__23);
        boolean o_testIteratorRemovable_rv300185__24 = __DSPOT_invoc_4.hasKeyIgnoreCase(__DSPOT_key_62177);
        Assert.assertFalse(o_testIteratorRemovable_rv300185__24);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300185__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300185__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300185__8)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300185__8)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300185__9);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300185__16)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300185__21)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300185__22);
        Assert.assertFalse(o_testIteratorRemovable_rv300185__23);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg300142() throws Exception {
        String __DSPOT_key_62154 = "KV3CT)S7y(E#$cnt2E}[";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg300142__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg300142__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg300142__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg300142__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg300142__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg300142__5)).hashCode())));
        Attributes o_testIteratorRemovable_mg300142__6 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300142__6)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg300142__6)).hashCode())));
        boolean o_testIteratorRemovable_mg300142__7 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg300142__7);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg300142__14 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300142__14)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg300142__19 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300142__19)));
        String o_testIteratorRemovable_mg300142__20 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg300142__20);
        boolean o_testIteratorRemovable_mg300142__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg300142__21);
        a.remove(__DSPOT_key_62154);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300142__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300142__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300142__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300142__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300142__6)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300142__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg300142__7);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300142__14)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300142__19)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg300142__20);
        Assert.assertFalse(o_testIteratorRemovable_mg300142__21);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_mg300146() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_mg300146__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_mg300146__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_mg300146__3)).hashCode())));
        Attributes o_testIteratorRemovable_mg300146__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_mg300146__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_mg300146__4)).hashCode())));
        Attributes o_testIteratorRemovable_mg300146__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300146__5)).toString());
        Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovable_mg300146__5)).hashCode())));
        boolean o_testIteratorRemovable_mg300146__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_mg300146__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_mg300146__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300146__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_mg300146__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300146__18)));
        String o_testIteratorRemovable_mg300146__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_mg300146__19);
        boolean o_testIteratorRemovable_mg300146__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_mg300146__20);
        Attribute o_testIteratorRemovable_mg300146__21 = attr.clone();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (o_testIteratorRemovable_mg300146__21)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (o_testIteratorRemovable_mg300146__21)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (o_testIteratorRemovable_mg300146__21)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (o_testIteratorRemovable_mg300146__21)).getKey());
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300146__3)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300146__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300146__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300146__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_mg300146__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_mg300146__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_mg300146__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300146__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_mg300146__18)));
        Assert.assertEquals("There", o_testIteratorRemovable_mg300146__19);
        Assert.assertFalse(o_testIteratorRemovable_mg300146__20);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString300076() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString300076__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString300076__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablelitString300076__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString300076__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString300076__4)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovablelitString300076__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString300076__5 = a.put("data-name", "<soup");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"<soup\"", ((Attributes) (o_testIteratorRemovablelitString300076__5)).toString());
        Assert.assertEquals(1869495262, ((int) (((Attributes) (o_testIteratorRemovablelitString300076__5)).hashCode())));
        boolean o_testIteratorRemovablelitString300076__6 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovablelitString300076__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString300076__13 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300076__13)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString300076__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300076__18)));
        String o_testIteratorRemovablelitString300076__19 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovablelitString300076__19);
        boolean o_testIteratorRemovablelitString300076__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString300076__20);
        Assert.assertEquals(" Hello=\"There\" data-name=\"<soup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(731402357, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"<soup\"", ((Attributes) (o_testIteratorRemovablelitString300076__3)).toString());
        Assert.assertEquals(731402357, ((int) (((Attributes) (o_testIteratorRemovablelitString300076__3)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"<soup\"", ((Attributes) (o_testIteratorRemovablelitString300076__4)).toString());
        Assert.assertEquals(731402357, ((int) (((Attributes) (o_testIteratorRemovablelitString300076__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"<soup\"", ((Attributes) (o_testIteratorRemovablelitString300076__5)).toString());
        Assert.assertEquals(731402357, ((int) (((Attributes) (o_testIteratorRemovablelitString300076__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovablelitString300076__6);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300076__13)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovablelitString300076__18)));
        Assert.assertEquals("There", o_testIteratorRemovablelitString300076__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablelitString300036() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovablelitString300036__3 = a.put("Hello", "a&p");
        Assert.assertEquals(" Hello=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablelitString300036__3)).toString());
        Assert.assertEquals(1748102792, ((int) (((Attributes) (o_testIteratorRemovablelitString300036__3)).hashCode())));
        Attributes o_testIteratorRemovablelitString300036__4 = a.put("Hello", "There");
        Assert.assertEquals(" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablelitString300036__4)).toString());
        Assert.assertEquals(110761679, ((int) (((Attributes) (o_testIteratorRemovablelitString300036__4)).hashCode())));
        Attributes o_testIteratorRemovablelitString300036__5 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300036__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovablelitString300036__5)).hashCode())));
        boolean o_testIteratorRemovablelitString300036__6 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString300036__6);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovablelitString300036__13 = a.size();
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString300036__13)));
        attr = iterator.next();
        Assert.assertEquals("data-name=\"Jsoup\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(254915635, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Jsoup", ((Attribute) (attr)).getValue());
        Assert.assertEquals("data-name", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovablelitString300036__18 = a.size();
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString300036__18)));
        String o_testIteratorRemovablelitString300036__19 = a.get("Hello");
        Assert.assertEquals("", o_testIteratorRemovablelitString300036__19);
        boolean o_testIteratorRemovablelitString300036__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovablelitString300036__20);
        Assert.assertEquals(" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(719056590, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300036__3)).toString());
        Assert.assertEquals(719056590, ((int) (((Attributes) (o_testIteratorRemovablelitString300036__3)).hashCode())));
        Assert.assertEquals(" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300036__4)).toString());
        Assert.assertEquals(719056590, ((int) (((Attributes) (o_testIteratorRemovablelitString300036__4)).hashCode())));
        Assert.assertEquals(" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablelitString300036__5)).toString());
        Assert.assertEquals(719056590, ((int) (((Attributes) (o_testIteratorRemovablelitString300036__5)).hashCode())));
        Assert.assertFalse(o_testIteratorRemovablelitString300036__6);
        Assert.assertEquals("data-name=\"Jsoup\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(254915635, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Jsoup", ((Attribute) (attr)).getValue());
        Assert.assertEquals("data-name", ((Attribute) (attr)).getKey());
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString300036__13)));
        Assert.assertEquals("data-name=\"Jsoup\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(254915635, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Jsoup", ((Attribute) (attr)).getValue());
        Assert.assertEquals("data-name", ((Attribute) (attr)).getKey());
        Assert.assertEquals(1, ((int) (o_testIteratorRemovablelitString300036__18)));
        Assert.assertEquals("", o_testIteratorRemovablelitString300036__19);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300213() throws Exception {
        Attribute __DSPOT_attribute_62195 = new Attribute("LZNW;s!dZBU/Q!zi-3LA", "^>$h{y)(oG2F&%45i|Bc", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300213__6 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300213__6)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300213__6)).hashCode())));
        Attributes o_testIteratorRemovable_rv300213__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv300213__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv300213__7)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        boolean o_testIteratorRemovable_rv300213__11 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300213__11);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300213__18 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300213__18)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300213__23 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300213__23)));
        String o_testIteratorRemovable_rv300213__24 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300213__24);
        boolean o_testIteratorRemovable_rv300213__25 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300213__25);
        Attributes o_testIteratorRemovable_rv300213__26 = __DSPOT_invoc_5.put(__DSPOT_attribute_62195);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" LZNW;s!dZBU/Q!zi-3LA=\"^>$h{y)(oG2F&amp;%45i|Bc\"", ((Attributes) (o_testIteratorRemovable_rv300213__26)).toString());
        Assert.assertEquals(1950497361, ((int) (((Attributes) (o_testIteratorRemovable_rv300213__26)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" LZNW;s!dZBU/Q!zi-3LA=\"^>$h{y)(oG2F&amp;%45i|Bc\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1950497361, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" LZNW;s!dZBU/Q!zi-3LA=\"^>$h{y)(oG2F&amp;%45i|Bc\"", ((Attributes) (o_testIteratorRemovable_rv300213__6)).toString());
        Assert.assertEquals(1950497361, ((int) (((Attributes) (o_testIteratorRemovable_rv300213__6)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" LZNW;s!dZBU/Q!zi-3LA=\"^>$h{y)(oG2F&amp;%45i|Bc\"", ((Attributes) (o_testIteratorRemovable_rv300213__7)).toString());
        Assert.assertEquals(1950497361, ((int) (((Attributes) (o_testIteratorRemovable_rv300213__7)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300213__11);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300213__18)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300213__23)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300213__24);
        Assert.assertFalse(o_testIteratorRemovable_rv300213__25);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300212() throws Exception {
        String __DSPOT_value_62194 = "dVJU5>(O[xSVG_P0mi/p";
        String __DSPOT_key_62193 = "NvRPT*]HF2u Xkx7Dtxm";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300212__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300212__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300212__5)).hashCode())));
        Attributes o_testIteratorRemovable_rv300212__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv300212__6)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv300212__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        boolean o_testIteratorRemovable_rv300212__10 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300212__10);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300212__17 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300212__17)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300212__22 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300212__22)));
        String o_testIteratorRemovable_rv300212__23 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300212__23);
        boolean o_testIteratorRemovable_rv300212__24 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300212__24);
        Attributes o_testIteratorRemovable_rv300212__25 = __DSPOT_invoc_5.put(__DSPOT_key_62193, __DSPOT_value_62194);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" NvRPT*]HF2u Xkx7Dtxm=\"dVJU5>(O[xSVG_P0mi/p\"", ((Attributes) (o_testIteratorRemovable_rv300212__25)).toString());
        Assert.assertEquals(113326455, ((int) (((Attributes) (o_testIteratorRemovable_rv300212__25)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" NvRPT*]HF2u Xkx7Dtxm=\"dVJU5>(O[xSVG_P0mi/p\"", ((Attributes) (a)).toString());
        Assert.assertEquals(113326455, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" NvRPT*]HF2u Xkx7Dtxm=\"dVJU5>(O[xSVG_P0mi/p\"", ((Attributes) (o_testIteratorRemovable_rv300212__5)).toString());
        Assert.assertEquals(113326455, ((int) (((Attributes) (o_testIteratorRemovable_rv300212__5)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\" NvRPT*]HF2u Xkx7Dtxm=\"dVJU5>(O[xSVG_P0mi/p\"", ((Attributes) (o_testIteratorRemovable_rv300212__6)).toString());
        Assert.assertEquals(113326455, ((int) (((Attributes) (o_testIteratorRemovable_rv300212__6)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300212__10);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300212__17)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300212__22)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300212__23);
        Assert.assertFalse(o_testIteratorRemovable_rv300212__24);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovable_rv300215() throws Exception {
        String __DSPOT_key_62197 = "{RjQVcyq%f]G,mkCx{`O";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorRemovable_rv300215__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovable_rv300215__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovable_rv300215__4)).hashCode())));
        Attributes o_testIteratorRemovable_rv300215__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovable_rv300215__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovable_rv300215__5)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "Jsoup");
        boolean o_testIteratorRemovable_rv300215__9 = a.hasKey("Tot");
        Assert.assertTrue(o_testIteratorRemovable_rv300215__9);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2707218, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.getKey();
        attr.getKey();
        iterator.remove();
        int o_testIteratorRemovable_rv300215__16 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300215__16)));
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.getValue();
        int o_testIteratorRemovable_rv300215__21 = a.size();
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300215__21)));
        String o_testIteratorRemovable_rv300215__22 = a.get("Hello");
        Assert.assertEquals("There", o_testIteratorRemovable_rv300215__22);
        boolean o_testIteratorRemovable_rv300215__23 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorRemovable_rv300215__23);
        __DSPOT_invoc_5.removeIgnoreCase(__DSPOT_key_62197);
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300215__4)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300215__4)).hashCode())));
        Assert.assertEquals(" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovable_rv300215__5)).toString());
        Assert.assertEquals(271552003, ((int) (((Attributes) (o_testIteratorRemovable_rv300215__5)).hashCode())));
        Assert.assertTrue(o_testIteratorRemovable_rv300215__9);
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300215__16)));
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        Assert.assertEquals(2, ((int) (o_testIteratorRemovable_rv300215__21)));
        Assert.assertEquals("There", o_testIteratorRemovable_rv300215__22);
        Assert.assertFalse(o_testIteratorRemovable_rv300215__23);
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165null354336() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165null354336__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354336__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354336__5)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165null354336__6 = a.put("Hello", null);
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354336__6)).toString());
            Assert.assertEquals(-1652213410, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354336__6)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165null354336__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354336__7)).toString());
            Assert.assertEquals(1955204940, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354336__7)).hashCode())));
            boolean o_testIteratorRemovablenull300234_failAssert165null354336__8 = a.hasKey("Tot");
            Assert.assertTrue(o_testIteratorRemovablenull300234_failAssert165null354336__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165litString352499() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352499__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352499__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352499__5)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352499__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352499__6)).toString());
            Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352499__6)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352499__7 = a.put("data-name", "");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352499__7)).toString());
            Assert.assertEquals(42118577, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352499__7)).hashCode())));
            boolean o_testIteratorRemovablenull300234_failAssert165litString352499__8 = a.hasKey("Tot");
            Assert.assertTrue(o_testIteratorRemovablenull300234_failAssert165litString352499__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165litString352391() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352391__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352391__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352391__5)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352391__6 = a.put("Hello", "\n");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352391__6)).toString());
            Assert.assertEquals(-1652203800, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352391__6)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352391__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352391__7)).toString());
            Assert.assertEquals(1955214550, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352391__7)).hashCode())));
            boolean o_testIteratorRemovablenull300234_failAssert165litString352391__8 = a.hasKey("Tot");
            Assert.assertTrue(o_testIteratorRemovablenull300234_failAssert165litString352391__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300232_failAssert166null344804() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablenull300232_failAssert166null344804__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300232_failAssert166null344804__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300232_failAssert166null344804__5)).hashCode())));
            Attributes o_testIteratorRemovablenull300232_failAssert166null344804__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablenull300232_failAssert166null344804__6)).toString());
            Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovablenull300232_failAssert166null344804__6)).hashCode())));
            Attributes o_testIteratorRemovablenull300232_failAssert166null344804__7 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_testIteratorRemovablenull300232_failAssert166null344804__7)).toString());
            Assert.assertEquals(-2024663920, ((int) (((Attributes) (o_testIteratorRemovablenull300232_failAssert166null344804__7)).hashCode())));
            boolean o_testIteratorRemovablenull300232_failAssert166null344804__8 = a.hasKey("Tot");
            Assert.assertTrue(o_testIteratorRemovablenull300232_failAssert166null344804__8);
            Iterator<Attribute> iterator = null;
            Attribute attr = iterator.next();
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get(null);
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablenull300232 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165null354313_failAssert178() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                a.put(null, "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                a.hasKey("Tot");
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = null;
                attr.getKey();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovablenull300234_failAssert165null354313 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165litString352405() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352405__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352405__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352405__5)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352405__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352405__6)).toString());
            Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352405__6)).hashCode())));
            Attributes o_testIteratorRemovablenull300234_failAssert165litString352405__7 = a.put("Tot", "Jsoup");
            Assert.assertEquals(" Tot=\"Jsoup\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352405__7)).toString());
            Assert.assertEquals(-1760694580, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165litString352405__7)).hashCode())));
            boolean o_testIteratorRemovablenull300234_failAssert165litString352405__8 = a.hasKey("Tot");
            Assert.assertTrue(o_testIteratorRemovablenull300234_failAssert165litString352405__8);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.getKey();
            iterator.remove();
            a.size();
            attr = iterator.next();
            attr.getKey();
            attr.getValue();
            a.size();
            a.get("Hello");
            a.hasKey("Tot");
            org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165null354349_failAssert175litString368151() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_testIteratorRemovablenull300234_failAssert165null354349_failAssert175litString368151__7 = a.put("Tot", "a&p");
                Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354349_failAssert175litString368151__7)).toString());
                Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354349_failAssert175litString368151__7)).hashCode())));
                Attributes o_testIteratorRemovablenull300234_failAssert165null354349_failAssert175litString368151__8 = a.put("Hello", "There");
                Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354349_failAssert175litString368151__8)).toString());
                Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354349_failAssert175litString368151__8)).hashCode())));
                a.put(null, "Jsoup");
                a.hasKey("Tot");
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = null;
                attr.getKey();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("tot");
                org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovablenull300234_failAssert165null354349 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165null354313_failAssert178null372119() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                a.put(null, "a&p");
                a.put("Hello", "There");
                a.put("data-name", "Jsoup");
                a.hasKey("Tot");
                Iterator<Attribute> iterator = null;
                Attribute attr = null;
                attr.getKey();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovablenull300234_failAssert165null354313 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorRemovablenull300234_failAssert165null354328_failAssert177null375249() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                Assert.assertEquals("", ((Attributes) (a)).toString());
                Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
                Attributes o_testIteratorRemovablenull300234_failAssert165null354328_failAssert177null375249__7 = a.put("Tot", null);
                Assert.assertEquals(" Tot", ((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354328_failAssert177null375249__7)).toString());
                Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorRemovablenull300234_failAssert165null354328_failAssert177null375249__7)).hashCode())));
                a.put(null, "There");
                a.put("data-name", "Jsoup");
                a.hasKey("Tot");
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = null;
                attr.getKey();
                iterator.remove();
                a.size();
                attr = iterator.next();
                attr.getKey();
                attr.getValue();
                a.size();
                a.get("Hello");
                a.hasKey("Tot");
                org.junit.Assert.fail("testIteratorRemovablenull300234 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorRemovablenull300234_failAssert165null354328 should have thrown IllegalArgumentException");
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
    public void testIteratorUpdateablenull378819_failAssert192() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Hello", "There");
            a.hasKey("Foo");
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = iterator.next();
            attr.setKey(null);
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablenull378819 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString378640() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378640__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateablelitString378640__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateablelitString378640__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378640__4 = a.put("\n", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" \n=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString378640__4)).toString());
        Assert.assertEquals(-608696246, ((int) (((Attributes) (o_testIteratorUpdateablelitString378640__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString378640__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString378640__5);
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
        String o_testIteratorUpdateablelitString378640__14 = attr.setValue("Qux");
        Assert.assertEquals("", o_testIteratorUpdateablelitString378640__14);
        String o_testIteratorUpdateablelitString378640__15 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateablelitString378640__15);
        String o_testIteratorUpdateablelitString378640__16 = a.get("Bar");
        Assert.assertEquals("", o_testIteratorUpdateablelitString378640__16);
        boolean o_testIteratorUpdateablelitString378640__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString378640__17);
        boolean o_testIteratorUpdateablelitString378640__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString378640__18);
        Assert.assertEquals(" Foo=\"a&amp;p\" \n=\"There\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-153463497, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" \n=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString378640__3)).toString());
        Assert.assertEquals(-153463497, ((int) (((Attributes) (o_testIteratorUpdateablelitString378640__3)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" \n=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString378640__4)).toString());
        Assert.assertEquals(-153463497, ((int) (((Attributes) (o_testIteratorUpdateablelitString378640__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString378640__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("", o_testIteratorUpdateablelitString378640__14);
        Assert.assertEquals("a&p", o_testIteratorUpdateablelitString378640__15);
        Assert.assertEquals("", o_testIteratorUpdateablelitString378640__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString378640__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString378631() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378631__3 = a.put("Tot", "");
        Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_testIteratorUpdateablelitString378631__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorUpdateablelitString378631__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378631__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString378631__4)).toString());
        Assert.assertEquals(142394285, ((int) (((Attributes) (o_testIteratorUpdateablelitString378631__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString378631__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString378631__5);
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
        String o_testIteratorUpdateablelitString378631__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateablelitString378631__14);
        String o_testIteratorUpdateablelitString378631__15 = a.get("Foo");
        Assert.assertEquals("", o_testIteratorUpdateablelitString378631__15);
        String o_testIteratorUpdateablelitString378631__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString378631__16);
        boolean o_testIteratorUpdateablelitString378631__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString378631__17);
        boolean o_testIteratorUpdateablelitString378631__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString378631__18);
        Assert.assertEquals(" Foo=\"\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString378631__3)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (o_testIteratorUpdateablelitString378631__3)).hashCode())));
        Assert.assertEquals(" Foo=\"\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString378631__4)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (o_testIteratorUpdateablelitString378631__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString378631__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateablelitString378631__14);
        Assert.assertEquals("", o_testIteratorUpdateablelitString378631__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString378631__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString378631__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString378632() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378632__3 = a.put("Tot", "\n");
        Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorUpdateablelitString378632__3)).toString());
        Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorUpdateablelitString378632__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378632__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString378632__4)).toString());
        Assert.assertEquals(142692195, ((int) (((Attributes) (o_testIteratorUpdateablelitString378632__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString378632__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString378632__5);
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
        String o_testIteratorUpdateablelitString378632__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateablelitString378632__14);
        String o_testIteratorUpdateablelitString378632__15 = a.get("Foo");
        Assert.assertEquals("\n", o_testIteratorUpdateablelitString378632__15);
        String o_testIteratorUpdateablelitString378632__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString378632__16);
        boolean o_testIteratorUpdateablelitString378632__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString378632__17);
        boolean o_testIteratorUpdateablelitString378632__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString378632__18);
        Assert.assertEquals(" Foo=\"\n\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1223112225, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"\n\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString378632__3)).toString());
        Assert.assertEquals(-1223112225, ((int) (((Attributes) (o_testIteratorUpdateablelitString378632__3)).hashCode())));
        Assert.assertEquals(" Foo=\"\n\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString378632__4)).toString());
        Assert.assertEquals(-1223112225, ((int) (((Attributes) (o_testIteratorUpdateablelitString378632__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString378632__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateablelitString378632__14);
        Assert.assertEquals("\n", o_testIteratorUpdateablelitString378632__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString378632__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString378632__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378770() throws Exception {
        String __DSPOT_key_75612 = "fK&Nqq9n^r[/8d)VQIpE";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv378770__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv378770__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv378770__7)).hashCode())));
        boolean o_testIteratorUpdateable_rv378770__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378770__8);
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
        String o_testIteratorUpdateable_rv378770__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378770__17);
        String o_testIteratorUpdateable_rv378770__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378770__18);
        String o_testIteratorUpdateable_rv378770__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378770__19);
        boolean o_testIteratorUpdateable_rv378770__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378770__20);
        boolean o_testIteratorUpdateable_rv378770__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378770__21);
        boolean o_testIteratorUpdateable_rv378770__22 = __DSPOT_invoc_3.hasKeyIgnoreCase(__DSPOT_key_75612);
        Assert.assertFalse(o_testIteratorUpdateable_rv378770__22);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378770__7)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378770__7)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378770__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378770__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378770__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378770__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv378770__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv378770__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_mg378753() throws Exception {
        Attribute __DSPOT_o_75604 = new Attribute("n;(SuH*.l5^T$i*h%O:5", "|9]bILZ(g+[4Q%pRfkWE", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_mg378753__6 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_mg378753__6)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_mg378753__6)).hashCode())));
        Attributes o_testIteratorUpdateable_mg378753__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_mg378753__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_mg378753__7)).hashCode())));
        boolean o_testIteratorUpdateable_mg378753__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_mg378753__8);
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
        String o_testIteratorUpdateable_mg378753__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_mg378753__17);
        String o_testIteratorUpdateable_mg378753__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg378753__18);
        String o_testIteratorUpdateable_mg378753__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg378753__19);
        boolean o_testIteratorUpdateable_mg378753__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_mg378753__20);
        boolean o_testIteratorUpdateable_mg378753__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_mg378753__21);
        boolean o_testIteratorUpdateable_mg378753__22 = attr.equals(__DSPOT_o_75604);
        Assert.assertFalse(o_testIteratorUpdateable_mg378753__22);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg378753__6)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg378753__6)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg378753__7)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg378753__7)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_mg378753__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_mg378753__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg378753__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg378753__19);
        Assert.assertFalse(o_testIteratorUpdateable_mg378753__20);
        Assert.assertFalse(o_testIteratorUpdateable_mg378753__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378806() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablenull378806__3 = a.put("Tot", null);
        Assert.assertEquals(" Tot", ((Attributes) (o_testIteratorUpdateablenull378806__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorUpdateablenull378806__3)).hashCode())));
        Attributes o_testIteratorUpdateablenull378806__4 = a.put("Hello", "There");
        Assert.assertEquals(" Tot Hello=\"There\"", ((Attributes) (o_testIteratorUpdateablenull378806__4)).toString());
        Assert.assertEquals(142394285, ((int) (((Attributes) (o_testIteratorUpdateablenull378806__4)).hashCode())));
        boolean o_testIteratorUpdateablenull378806__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablenull378806__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("Tot", ((Attribute) (attr)).toString());
        Assert.assertEquals(2612711, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertNull(((Attribute) (attr)).getValue());
        Assert.assertEquals("Tot", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateablenull378806__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateablenull378806__14);
        String o_testIteratorUpdateablenull378806__15 = a.get("Foo");
        Assert.assertEquals("", o_testIteratorUpdateablenull378806__15);
        String o_testIteratorUpdateablenull378806__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateablenull378806__16);
        boolean o_testIteratorUpdateablenull378806__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablenull378806__17);
        boolean o_testIteratorUpdateablenull378806__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablenull378806__18);
        Assert.assertEquals(" Foo Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablenull378806__3)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (o_testIteratorUpdateablenull378806__3)).hashCode())));
        Assert.assertEquals(" Foo Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablenull378806__4)).toString());
        Assert.assertEquals(-1223410135, ((int) (((Attributes) (o_testIteratorUpdateablenull378806__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablenull378806__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateablenull378806__14);
        Assert.assertEquals("", o_testIteratorUpdateablenull378806__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateablenull378806__16);
        Assert.assertFalse(o_testIteratorUpdateablenull378806__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString378623_failAssert196() throws Exception {
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
            org.junit.Assert.fail("testIteratorUpdateablelitString378623 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_mg378738() throws Exception {
        String __DSPOT_key_75594 = ",hbw).j#%P+^UqNlIs;t";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_mg378738__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_mg378738__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_mg378738__4)).hashCode())));
        Attributes o_testIteratorUpdateable_mg378738__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_mg378738__5)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_mg378738__5)).hashCode())));
        boolean o_testIteratorUpdateable_mg378738__6 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_mg378738__6);
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
        String o_testIteratorUpdateable_mg378738__15 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_mg378738__15);
        String o_testIteratorUpdateable_mg378738__16 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg378738__16);
        String o_testIteratorUpdateable_mg378738__17 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg378738__17);
        boolean o_testIteratorUpdateable_mg378738__18 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_mg378738__18);
        boolean o_testIteratorUpdateable_mg378738__19 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_mg378738__19);
        String o_testIteratorUpdateable_mg378738__20 = a.getIgnoreCase(__DSPOT_key_75594);
        Assert.assertEquals("", o_testIteratorUpdateable_mg378738__20);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg378738__4)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg378738__4)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_mg378738__5)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_mg378738__5)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_mg378738__6);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_mg378738__15);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_mg378738__16);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_mg378738__17);
        Assert.assertFalse(o_testIteratorUpdateable_mg378738__18);
        Assert.assertFalse(o_testIteratorUpdateable_mg378738__19);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString378624() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378624__3 = a.put("\n", "a&p");
        Assert.assertEquals(" \n=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateablelitString378624__3)).toString());
        Assert.assertEquals(-1440720416, ((int) (((Attributes) (o_testIteratorUpdateablelitString378624__3)).hashCode())));
        Attributes o_testIteratorUpdateablelitString378624__4 = a.put("Hello", "There");
        Assert.assertEquals(" \n=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateablelitString378624__4)).toString());
        Assert.assertEquals(-1853741837, ((int) (((Attributes) (o_testIteratorUpdateablelitString378624__4)).hashCode())));
        boolean o_testIteratorUpdateablelitString378624__5 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateablelitString378624__5);
        Iterator<Attribute> iterator = a.iterator();
        Attribute attr = iterator.next();
        Assert.assertEquals("=\"a&amp;p\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(94507, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("a&p", ((Attribute) (attr)).getValue());
        Assert.assertEquals("", ((Attribute) (attr)).getKey());
        attr.setKey("Foo");
        attr = iterator.next();
        Assert.assertEquals("Hello=\"There\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(-2056293422, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("There", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Hello", ((Attribute) (attr)).getKey());
        attr.setKey("Bar");
        String o_testIteratorUpdateablelitString378624__14 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateablelitString378624__14);
        String o_testIteratorUpdateablelitString378624__15 = a.get("Foo");
        Assert.assertEquals("", o_testIteratorUpdateablelitString378624__15);
        String o_testIteratorUpdateablelitString378624__16 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString378624__16);
        boolean o_testIteratorUpdateablelitString378624__17 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateablelitString378624__17);
        boolean o_testIteratorUpdateablelitString378624__18 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateablelitString378624__18);
        Assert.assertEquals(" \n=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(620188290, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" \n=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString378624__3)).toString());
        Assert.assertEquals(620188290, ((int) (((Attributes) (o_testIteratorUpdateablelitString378624__3)).hashCode())));
        Assert.assertEquals(" \n=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateablelitString378624__4)).toString());
        Assert.assertEquals(620188290, ((int) (((Attributes) (o_testIteratorUpdateablelitString378624__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateablelitString378624__5);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateablelitString378624__14);
        Assert.assertEquals("", o_testIteratorUpdateablelitString378624__15);
        Assert.assertEquals("Qux", o_testIteratorUpdateablelitString378624__16);
        Assert.assertFalse(o_testIteratorUpdateablelitString378624__17);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378799() throws Exception {
        String __DSPOT_key_75631 = "$Do]$p(^-/AX#J[O2_^-";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv378799__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv378799__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv378799__4)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv378799__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378799__8);
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
        String o_testIteratorUpdateable_rv378799__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378799__17);
        String o_testIteratorUpdateable_rv378799__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378799__18);
        String o_testIteratorUpdateable_rv378799__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378799__19);
        boolean o_testIteratorUpdateable_rv378799__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378799__20);
        boolean o_testIteratorUpdateable_rv378799__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378799__21);
        __DSPOT_invoc_4.remove(__DSPOT_key_75631);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378799__4)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378799__4)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378799__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378799__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378799__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378799__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv378799__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv378799__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378766() throws Exception {
        Attributes __DSPOT_o_75608 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv378766__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv378766__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv378766__8)).hashCode())));
        boolean o_testIteratorUpdateable_rv378766__9 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378766__9);
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
        String o_testIteratorUpdateable_rv378766__18 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378766__18);
        String o_testIteratorUpdateable_rv378766__19 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378766__19);
        String o_testIteratorUpdateable_rv378766__20 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378766__20);
        boolean o_testIteratorUpdateable_rv378766__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378766__21);
        boolean o_testIteratorUpdateable_rv378766__22 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378766__22);
        boolean o_testIteratorUpdateable_rv378766__23 = __DSPOT_invoc_3.equals(__DSPOT_o_75608);
        Assert.assertFalse(o_testIteratorUpdateable_rv378766__23);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378766__8)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378766__8)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378766__9);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378766__18);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378766__19);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378766__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv378766__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv378766__22);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378779() throws Exception {
        String __DSPOT_key_75619 = ":[1r6&g ouLWFg%>aPK.";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv378779__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv378779__7)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv378779__7)).hashCode())));
        boolean o_testIteratorUpdateable_rv378779__8 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378779__8);
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
        String o_testIteratorUpdateable_rv378779__17 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378779__17);
        String o_testIteratorUpdateable_rv378779__18 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378779__18);
        String o_testIteratorUpdateable_rv378779__19 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378779__19);
        boolean o_testIteratorUpdateable_rv378779__20 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378779__20);
        boolean o_testIteratorUpdateable_rv378779__21 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378779__21);
        __DSPOT_invoc_3.removeIgnoreCase(__DSPOT_key_75619);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378779__7)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378779__7)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378779__8);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378779__17);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378779__18);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378779__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv378779__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv378779__21);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378785() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv378785__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv378785__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv378785__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv378785__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378785__7);
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
        String o_testIteratorUpdateable_rv378785__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378785__16);
        String o_testIteratorUpdateable_rv378785__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378785__17);
        String o_testIteratorUpdateable_rv378785__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378785__18);
        boolean o_testIteratorUpdateable_rv378785__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378785__19);
        boolean o_testIteratorUpdateable_rv378785__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378785__20);
        Attributes o_testIteratorUpdateable_rv378785__21 = __DSPOT_invoc_4.clone();
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378785__21)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378785__21)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(73166878, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378785__3)).toString());
        Assert.assertEquals(73166878, ((int) (((Attributes) (o_testIteratorUpdateable_rv378785__3)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378785__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378785__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378785__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378785__18);
        Assert.assertFalse(o_testIteratorUpdateable_rv378785__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv378785__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378808_failAssert187() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put(null, "There");
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
            org.junit.Assert.fail("testIteratorUpdateablenull378808 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378796() throws Exception {
        boolean __DSPOT_value_75627 = true;
        String __DSPOT_key_75626 = "MZH84}Z@d.O)jq/Lo#=[";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv378796__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv378796__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv378796__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv378796__9 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378796__9);
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
        String o_testIteratorUpdateable_rv378796__18 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378796__18);
        String o_testIteratorUpdateable_rv378796__19 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378796__19);
        String o_testIteratorUpdateable_rv378796__20 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378796__20);
        boolean o_testIteratorUpdateable_rv378796__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378796__21);
        boolean o_testIteratorUpdateable_rv378796__22 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378796__22);
        Attributes o_testIteratorUpdateable_rv378796__23 = __DSPOT_invoc_4.put(__DSPOT_key_75626, __DSPOT_value_75627);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" MZH84}Z@d.O)jq/Lo#=[", ((Attributes) (o_testIteratorUpdateable_rv378796__23)).toString());
        Assert.assertEquals(-406375099, ((int) (((Attributes) (o_testIteratorUpdateable_rv378796__23)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" MZH84}Z@d.O)jq/Lo#=[", ((Attributes) (a)).toString());
        Assert.assertEquals(-406375099, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" MZH84}Z@d.O)jq/Lo#=[", ((Attributes) (o_testIteratorUpdateable_rv378796__5)).toString());
        Assert.assertEquals(-406375099, ((int) (((Attributes) (o_testIteratorUpdateable_rv378796__5)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378796__9);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378796__18);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378796__19);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378796__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv378796__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv378796__22);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378784() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv378784__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv378784__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv378784__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv378784__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378784__7);
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
        String o_testIteratorUpdateable_rv378784__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378784__16);
        String o_testIteratorUpdateable_rv378784__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378784__17);
        String o_testIteratorUpdateable_rv378784__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378784__18);
        boolean o_testIteratorUpdateable_rv378784__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378784__19);
        boolean o_testIteratorUpdateable_rv378784__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378784__20);
        List<Attribute> o_testIteratorUpdateable_rv378784__21 = __DSPOT_invoc_4.asList();
        Assert.assertFalse(o_testIteratorUpdateable_rv378784__21.isEmpty());
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378784__3)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378784__3)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378784__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378784__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378784__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378784__18);
        Assert.assertFalse(o_testIteratorUpdateable_rv378784__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv378784__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378795() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv378795__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv378795__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv378795__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv378795__7 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378795__7);
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
        String o_testIteratorUpdateable_rv378795__16 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378795__16);
        String o_testIteratorUpdateable_rv378795__17 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378795__17);
        String o_testIteratorUpdateable_rv378795__18 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378795__18);
        boolean o_testIteratorUpdateable_rv378795__19 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378795__19);
        boolean o_testIteratorUpdateable_rv378795__20 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378795__20);
        __DSPOT_invoc_4.normalize();
        Assert.assertEquals(" foo=\"a&amp;p\" bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(843527454, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" foo=\"a&amp;p\" bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378795__3)).toString());
        Assert.assertEquals(843527454, ((int) (((Attributes) (o_testIteratorUpdateable_rv378795__3)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378795__7);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378795__16);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378795__17);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378795__18);
        Assert.assertFalse(o_testIteratorUpdateable_rv378795__19);
        Assert.assertFalse(o_testIteratorUpdateable_rv378795__20);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378798() throws Exception {
        Attribute __DSPOT_attribute_75630 = new Attribute("/ 8xrKn6i$/**Llg&?tT", "E-YqKH>|53v+KTEzb!+ ", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv378798__6 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv378798__6)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv378798__6)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv378798__10 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378798__10);
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
        String o_testIteratorUpdateable_rv378798__19 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378798__19);
        String o_testIteratorUpdateable_rv378798__20 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378798__20);
        String o_testIteratorUpdateable_rv378798__21 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378798__21);
        boolean o_testIteratorUpdateable_rv378798__22 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378798__22);
        boolean o_testIteratorUpdateable_rv378798__23 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378798__23);
        Attributes o_testIteratorUpdateable_rv378798__24 = __DSPOT_invoc_4.put(__DSPOT_attribute_75630);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" / 8xrKn6i$/**Llg&?tT=\"E-YqKH>|53v+KTEzb!+ \"", ((Attributes) (o_testIteratorUpdateable_rv378798__24)).toString());
        Assert.assertEquals(287351389, ((int) (((Attributes) (o_testIteratorUpdateable_rv378798__24)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" / 8xrKn6i$/**Llg&?tT=\"E-YqKH>|53v+KTEzb!+ \"", ((Attributes) (a)).toString());
        Assert.assertEquals(287351389, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" / 8xrKn6i$/**Llg&?tT=\"E-YqKH>|53v+KTEzb!+ \"", ((Attributes) (o_testIteratorUpdateable_rv378798__6)).toString());
        Assert.assertEquals(287351389, ((int) (((Attributes) (o_testIteratorUpdateable_rv378798__6)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378798__10);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378798__19);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378798__20);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378798__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv378798__22);
        Assert.assertFalse(o_testIteratorUpdateable_rv378798__23);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378797() throws Exception {
        String __DSPOT_value_75629 = "5!#OnvQOxj}xO?s1!lMQ";
        String __DSPOT_key_75628 = "!SoJ,ao3JXrP M%]{vR#";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorUpdateable_rv378797__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateable_rv378797__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateable_rv378797__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "There");
        boolean o_testIteratorUpdateable_rv378797__9 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378797__9);
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
        String o_testIteratorUpdateable_rv378797__18 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378797__18);
        String o_testIteratorUpdateable_rv378797__19 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378797__19);
        String o_testIteratorUpdateable_rv378797__20 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378797__20);
        boolean o_testIteratorUpdateable_rv378797__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378797__21);
        boolean o_testIteratorUpdateable_rv378797__22 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378797__22);
        Attributes o_testIteratorUpdateable_rv378797__23 = __DSPOT_invoc_4.put(__DSPOT_key_75628, __DSPOT_value_75629);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" !SoJ,ao3JXrP M%]{vR#=\"5!#OnvQOxj}xO?s1!lMQ\"", ((Attributes) (o_testIteratorUpdateable_rv378797__23)).toString());
        Assert.assertEquals(-833171238, ((int) (((Attributes) (o_testIteratorUpdateable_rv378797__23)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" !SoJ,ao3JXrP M%]{vR#=\"5!#OnvQOxj}xO?s1!lMQ\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-833171238, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\" !SoJ,ao3JXrP M%]{vR#=\"5!#OnvQOxj}xO?s1!lMQ\"", ((Attributes) (o_testIteratorUpdateable_rv378797__5)).toString());
        Assert.assertEquals(-833171238, ((int) (((Attributes) (o_testIteratorUpdateable_rv378797__5)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378797__9);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378797__18);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378797__19);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378797__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv378797__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv378797__22);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateable_rv378775() throws Exception {
        boolean __DSPOT_value_75614 = false;
        String __DSPOT_key_75613 = "5InW2,>(5f:!{8Trc&&1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_testIteratorUpdateable_rv378775__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"There\"", ((Attributes) (o_testIteratorUpdateable_rv378775__8)).toString());
        Assert.assertEquals(-1337114974, ((int) (((Attributes) (o_testIteratorUpdateable_rv378775__8)).hashCode())));
        boolean o_testIteratorUpdateable_rv378775__9 = a.hasKey("Foo");
        Assert.assertFalse(o_testIteratorUpdateable_rv378775__9);
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
        String o_testIteratorUpdateable_rv378775__18 = attr.setValue("Qux");
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378775__18);
        String o_testIteratorUpdateable_rv378775__19 = a.get("Foo");
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378775__19);
        String o_testIteratorUpdateable_rv378775__20 = a.get("Bar");
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378775__20);
        boolean o_testIteratorUpdateable_rv378775__21 = a.hasKey("Tot");
        Assert.assertFalse(o_testIteratorUpdateable_rv378775__21);
        boolean o_testIteratorUpdateable_rv378775__22 = a.hasKey("Hello");
        Assert.assertFalse(o_testIteratorUpdateable_rv378775__22);
        Attributes o_testIteratorUpdateable_rv378775__23 = __DSPOT_invoc_3.put(__DSPOT_key_75613, __DSPOT_value_75614);
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378775__23)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378775__23)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Foo=\"a&amp;p\" Bar=\"Qux\"", ((Attributes) (o_testIteratorUpdateable_rv378775__8)).toString());
        Assert.assertEquals(1592047902, ((int) (((Attributes) (o_testIteratorUpdateable_rv378775__8)).hashCode())));
        Assert.assertFalse(o_testIteratorUpdateable_rv378775__9);
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("Bar=\"Qux\"", ((Attribute) (attr)).toString());
        Assert.assertEquals(2144545, ((int) (((Attribute) (attr)).hashCode())));
        Assert.assertEquals("Qux", ((Attribute) (attr)).getValue());
        Assert.assertEquals("Bar", ((Attribute) (attr)).getKey());
        Assert.assertEquals("There", o_testIteratorUpdateable_rv378775__18);
        Assert.assertEquals("a&p", o_testIteratorUpdateable_rv378775__19);
        Assert.assertEquals("Qux", o_testIteratorUpdateable_rv378775__20);
        Assert.assertFalse(o_testIteratorUpdateable_rv378775__21);
        Assert.assertFalse(o_testIteratorUpdateable_rv378775__22);
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString378664_failAssert200() throws Exception {
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
            org.junit.Assert.fail("testIteratorUpdateablelitString378664 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablelitString378634_failAssert199() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "a&p");
            a.put("Tot", "There");
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
            org.junit.Assert.fail("testIteratorUpdateablelitString378634 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378805_failAssert189() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put(null, "a&p");
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
            org.junit.Assert.fail("testIteratorUpdateablenull378805 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378816_failAssert184litString380193() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184litString380193__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380193__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380193__5)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184litString380193__6 = a.put("Hello", "");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\"", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380193__6)).toString());
            Assert.assertEquals(-1652213410, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380193__6)).hashCode())));
            boolean o_testIteratorUpdateablenull378816_failAssert184litString380193__7 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateablenull378816_failAssert184litString380193__7);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablenull378816 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378816_failAssert184null383124() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184null383124__5 = a.put("Tot", null);
            Assert.assertEquals(" Tot", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184null383124__5)).toString());
            Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184null383124__5)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184null383124__6 = a.put("Hello", "There");
            Assert.assertEquals(" Tot Hello=\"There\"", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184null383124__6)).toString());
            Assert.assertEquals(142394285, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184null383124__6)).hashCode())));
            boolean o_testIteratorUpdateablenull378816_failAssert184null383124__7 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateablenull378816_failAssert184null383124__7);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablenull378816 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378816_failAssert184litString380071() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184litString380071__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380071__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380071__5)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184litString380071__6 = a.put(":", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" :=\"There\"", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380071__6)).toString());
            Assert.assertEquals(-607266278, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380071__6)).hashCode())));
            boolean o_testIteratorUpdateablenull378816_failAssert184litString380071__7 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateablenull378816_failAssert184litString380071__7);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablenull378816 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378816_failAssert184litString380204() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184litString380204__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380204__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380204__5)).hashCode())));
            Attributes o_testIteratorUpdateablenull378816_failAssert184litString380204__6 = a.put("Hello", "\n");
            Assert.assertEquals(" Tot=\"a&amp;p\" Hello=\"\n\"", ((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380204__6)).toString());
            Assert.assertEquals(-1652203800, ((int) (((Attributes) (o_testIteratorUpdateablenull378816_failAssert184litString380204__6)).hashCode())));
            boolean o_testIteratorUpdateablenull378816_failAssert184litString380204__7 = a.hasKey("Foo");
            Assert.assertFalse(o_testIteratorUpdateablenull378816_failAssert184litString380204__7);
            Iterator<Attribute> iterator = a.iterator();
            Attribute attr = null;
            attr.setKey("Foo");
            attr = iterator.next();
            attr.setKey("Bar");
            attr.setValue("Qux");
            a.get("Foo");
            a.get("Bar");
            a.hasKey("Tot");
            a.hasKey("Hello");
            org.junit.Assert.fail("testIteratorUpdateablenull378816 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorUpdateablenull378816_failAssert184null383114_failAssert205() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                a.put(null, "a&p");
                a.put("Hello", "There");
                a.hasKey("Foo");
                Iterator<Attribute> iterator = a.iterator();
                Attribute attr = null;
                attr.setKey("Foo");
                attr = iterator.next();
                attr.setKey("Bar");
                attr.setValue("Qux");
                a.get("Foo");
                a.get("Bar");
                a.hasKey("Tot");
                a.hasKey("Hello");
                org.junit.Assert.fail("testIteratorUpdateablenull378816 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorUpdateablenull378816_failAssert184null383114 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
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
    public void testIteratorHasNextlitString246267() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNextlitString246267__3 = a.put("Tot", "");
        Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_testIteratorHasNextlitString246267__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorHasNextlitString246267__3)).hashCode())));
        Attributes o_testIteratorHasNextlitString246267__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString246267__4)).toString());
        Assert.assertEquals(-172656101, ((int) (((Attributes) (o_testIteratorHasNextlitString246267__4)).hashCode())));
        Attributes o_testIteratorHasNextlitString246267__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246267__5)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextlitString246267__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" Tot=\"\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246267__3)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextlitString246267__3)).hashCode())));
        Assert.assertEquals(" Tot=\"\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246267__4)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextlitString246267__4)).hashCode())));
        Assert.assertEquals(" Tot=\"\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246267__5)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextlitString246267__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString246268() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNextlitString246268__3 = a.put("Tot", "\n");
        Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorHasNextlitString246268__3)).toString());
        Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorHasNextlitString246268__3)).hashCode())));
        Attributes o_testIteratorHasNextlitString246268__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString246268__4)).toString());
        Assert.assertEquals(-172358191, ((int) (((Attributes) (o_testIteratorHasNextlitString246268__4)).hashCode())));
        Attributes o_testIteratorHasNextlitString246268__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246268__5)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString246268__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246268__3)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString246268__3)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246268__4)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString246268__4)).hashCode())));
        Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246268__5)).toString());
        Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextlitString246268__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246401() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246401__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246401__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246401__3)).hashCode())));
        Attributes o_testIteratorHasNext_rv246401__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246401__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246401__4)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "3");
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        __DSPOT_invoc_5.toString();
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246401__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246401__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246401__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246401__4)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246346() throws Exception {
        String __DSPOT_key_47522 = "a__`!sT!R-:%fQ_L,- {";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "1");
        Attributes o_testIteratorHasNext_rv246346__7 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246346__7)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246346__7)).hashCode())));
        Attributes o_testIteratorHasNext_rv246346__8 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246346__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246346__8)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        String o_testIteratorHasNext_rv246346__16 = __DSPOT_invoc_3.getIgnoreCase(__DSPOT_key_47522);
        Assert.assertEquals("", o_testIteratorHasNext_rv246346__16);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246346__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246346__7)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246346__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246346__8)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246348() throws Exception {
        String __DSPOT_key_47524 = "6o$o%lS)O|lQJ]rGeKQ@";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "1");
        Attributes o_testIteratorHasNext_rv246348__7 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246348__7)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246348__7)).hashCode())));
        Attributes o_testIteratorHasNext_rv246348__8 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246348__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246348__8)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        boolean o_testIteratorHasNext_rv246348__16 = __DSPOT_invoc_3.hasKeyIgnoreCase(__DSPOT_key_47524);
        Assert.assertFalse(o_testIteratorHasNext_rv246348__16);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246348__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246348__7)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246348__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246348__8)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_mg246327() throws Exception {
        String __DSPOT_key_47515 = "e9(o:Jg`!,;IUy}#;m@I";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_mg246327__4 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_mg246327__4)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_mg246327__4)).hashCode())));
        Attributes o_testIteratorHasNext_mg246327__5 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_mg246327__5)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_mg246327__5)).hashCode())));
        Attributes o_testIteratorHasNext_mg246327__6 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246327__6)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246327__6)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        a.removeIgnoreCase(__DSPOT_key_47515);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246327__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246327__4)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246327__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246327__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246327__6)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246327__6)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246341() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "1");
        Attributes o_testIteratorHasNext_rv246341__6 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246341__6)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246341__6)).hashCode())));
        Attributes o_testIteratorHasNext_rv246341__7 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246341__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246341__7)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        List<Attribute> o_testIteratorHasNext_rv246341__15 = __DSPOT_invoc_3.asList();
        Assert.assertFalse(o_testIteratorHasNext_rv246341__15.isEmpty());
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246341__6)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246341__6)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246341__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246341__7)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246363() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246363__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246363__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246363__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv246363__7 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246363__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246363__7)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv246363__15 = __DSPOT_invoc_4.clone();
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246363__15)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246363__15)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(870255776, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246363__3)).toString());
        Assert.assertEquals(870255776, ((int) (((Attributes) (o_testIteratorHasNext_rv246363__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246363__7)).toString());
        Assert.assertEquals(870255776, ((int) (((Attributes) (o_testIteratorHasNext_rv246363__7)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246365() throws Exception {
        Object __DSPOT_o_47533 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246365__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246365__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246365__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv246365__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246365__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246365__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        boolean o_testIteratorHasNext_rv246365__17 = __DSPOT_invoc_4.equals(__DSPOT_o_47533);
        Assert.assertFalse(o_testIteratorHasNext_rv246365__17);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246365__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246365__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246365__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246365__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246344() throws Exception {
        Attributes __DSPOT_o_47520 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "1");
        Attributes o_testIteratorHasNext_rv246344__8 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246344__8)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246344__8)).hashCode())));
        Attributes o_testIteratorHasNext_rv246344__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246344__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246344__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        boolean o_testIteratorHasNext_rv246344__17 = __DSPOT_invoc_3.equals(__DSPOT_o_47520);
        Assert.assertFalse(o_testIteratorHasNext_rv246344__17);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246344__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246344__8)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246344__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246344__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString246275_failAssert144() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "1");
            a.put("", "2");
            a.put("data-name", "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextlitString246275 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246370() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246370__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246370__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246370__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv246370__7 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246370__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246370__7)).hashCode())));
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
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246370__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246370__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246370__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246370__7)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_mg246336() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_mg246336__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_mg246336__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_mg246336__3)).hashCode())));
        Attributes o_testIteratorHasNext_mg246336__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_mg246336__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_mg246336__4)).hashCode())));
        Attributes o_testIteratorHasNext_mg246336__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246336__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246336__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            String __DSPOT_key_47517 = "b_R9-v/>60SEpE$20]t ";
            seen++;
            String.valueOf(seen);
            attribute.getValue();
            attribute.setKey(__DSPOT_key_47517);
        }
        Assert.assertEquals(" b_R9-v/>60SEpE$20]t=\"1\" b_R9-v/>60SEpE$20]t=\"2\" b_R9-v/>60SEpE$20]t=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(26031245, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b_R9-v/>60SEpE$20]t=\"1\" b_R9-v/>60SEpE$20]t=\"2\" b_R9-v/>60SEpE$20]t=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246336__3)).toString());
        Assert.assertEquals(26031245, ((int) (((Attributes) (o_testIteratorHasNext_mg246336__3)).hashCode())));
        Assert.assertEquals(" b_R9-v/>60SEpE$20]t=\"1\" b_R9-v/>60SEpE$20]t=\"2\" b_R9-v/>60SEpE$20]t=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246336__4)).toString());
        Assert.assertEquals(26031245, ((int) (((Attributes) (o_testIteratorHasNext_mg246336__4)).hashCode())));
        Assert.assertEquals(" b_R9-v/>60SEpE$20]t=\"1\" b_R9-v/>60SEpE$20]t=\"2\" b_R9-v/>60SEpE$20]t=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246336__5)).toString());
        Assert.assertEquals(26031245, ((int) (((Attributes) (o_testIteratorHasNext_mg246336__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextlitString246257() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNextlitString246257__3 = a.put("Hello", "1");
        Assert.assertEquals(" Hello=\"1\"", ((Attributes) (o_testIteratorHasNextlitString246257__3)).toString());
        Assert.assertEquals(-1065895486, ((int) (((Attributes) (o_testIteratorHasNextlitString246257__3)).hashCode())));
        Attributes o_testIteratorHasNextlitString246257__4 = a.put("Hello", "2");
        Assert.assertEquals(" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextlitString246257__4)).toString());
        Assert.assertEquals(-1065865695, ((int) (((Attributes) (o_testIteratorHasNextlitString246257__4)).hashCode())));
        Attributes o_testIteratorHasNextlitString246257__5 = a.put("data-name", "3");
        Assert.assertEquals(" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246257__5)).toString());
        Assert.assertEquals(-1259278393, ((int) (((Attributes) (o_testIteratorHasNextlitString246257__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1259278393, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246257__3)).toString());
        Assert.assertEquals(-1259278393, ((int) (((Attributes) (o_testIteratorHasNextlitString246257__3)).hashCode())));
        Assert.assertEquals(" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246257__4)).toString());
        Assert.assertEquals(-1259278393, ((int) (((Attributes) (o_testIteratorHasNextlitString246257__4)).hashCode())));
        Assert.assertEquals(" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextlitString246257__5)).toString());
        Assert.assertEquals(-1259278393, ((int) (((Attributes) (o_testIteratorHasNextlitString246257__5)).hashCode())));
        Assert.assertEquals(2, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246404_failAssert147() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put(null, "1");
            a.put("Hello", "2");
            a.put("data-name", "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246404 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_mg246338() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_mg246338__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_mg246338__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_mg246338__3)).hashCode())));
        Attributes o_testIteratorHasNext_mg246338__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_mg246338__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_mg246338__4)).hashCode())));
        Attributes o_testIteratorHasNext_mg246338__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246338__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246338__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
            attribute.toString();
        }
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246338__3)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246338__3)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246338__4)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246338__4)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246338__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246338__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246405() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNextnull246405__3 = a.put("Tot", null);
        Assert.assertEquals(" Tot", ((Attributes) (o_testIteratorHasNextnull246405__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorHasNextnull246405__3)).hashCode())));
        Attributes o_testIteratorHasNextnull246405__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot Hello=\"2\"", ((Attributes) (o_testIteratorHasNextnull246405__4)).toString());
        Assert.assertEquals(-172656101, ((int) (((Attributes) (o_testIteratorHasNextnull246405__4)).hashCode())));
        Attributes o_testIteratorHasNextnull246405__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246405__5)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextnull246405__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Assert.assertEquals(" Tot Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246405__3)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextnull246405__3)).hashCode())));
        Assert.assertEquals(" Tot Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246405__4)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextnull246405__4)).hashCode())));
        Assert.assertEquals(" Tot Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246405__5)).toString());
        Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextnull246405__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_mg246337() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_mg246337__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_mg246337__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_mg246337__3)).hashCode())));
        Attributes o_testIteratorHasNext_mg246337__4 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_mg246337__4)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_mg246337__4)).hashCode())));
        Attributes o_testIteratorHasNext_mg246337__5 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_mg246337__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_mg246337__5)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            String __DSPOT_val_47518 = "Iahn/#BOGVA2&-4]<=;w";
            seen++;
            String.valueOf(seen);
            attribute.getValue();
            attribute.setValue(__DSPOT_val_47518);
        }
        Assert.assertEquals(" Tot=\"Iahn/#BOGVA2&amp;-4]<=;w\" Hello=\"Iahn/#BOGVA2&amp;-4]<=;w\" data-name=\"Iahn/#BOGVA2&amp;-4]<=;w\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-323786036, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"Iahn/#BOGVA2&amp;-4]<=;w\" Hello=\"Iahn/#BOGVA2&amp;-4]<=;w\" data-name=\"Iahn/#BOGVA2&amp;-4]<=;w\"", ((Attributes) (o_testIteratorHasNext_mg246337__3)).toString());
        Assert.assertEquals(-323786036, ((int) (((Attributes) (o_testIteratorHasNext_mg246337__3)).hashCode())));
        Assert.assertEquals(" Tot=\"Iahn/#BOGVA2&amp;-4]<=;w\" Hello=\"Iahn/#BOGVA2&amp;-4]<=;w\" data-name=\"Iahn/#BOGVA2&amp;-4]<=;w\"", ((Attributes) (o_testIteratorHasNext_mg246337__4)).toString());
        Assert.assertEquals(-323786036, ((int) (((Attributes) (o_testIteratorHasNext_mg246337__4)).hashCode())));
        Assert.assertEquals(" Tot=\"Iahn/#BOGVA2&amp;-4]<=;w\" Hello=\"Iahn/#BOGVA2&amp;-4]<=;w\" data-name=\"Iahn/#BOGVA2&amp;-4]<=;w\"", ((Attributes) (o_testIteratorHasNext_mg246337__5)).toString());
        Assert.assertEquals(-323786036, ((int) (((Attributes) (o_testIteratorHasNext_mg246337__5)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246407_failAssert148() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("Tot", "1");
            a.put(null, "2");
            a.put("data-name", "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246407 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246374() throws Exception {
        boolean __DSPOT_value_47539 = true;
        String __DSPOT_key_47538 = "%j(eFP%WS,>()]aV^2P%";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246374__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246374__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246374__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv246374__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246374__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246374__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv246374__17 = __DSPOT_invoc_4.put(__DSPOT_key_47538, __DSPOT_value_47539);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" %j(eFP%WS,>()]aV^2P%", ((Attributes) (o_testIteratorHasNext_rv246374__17)).toString());
        Assert.assertEquals(-11519820, ((int) (((Attributes) (o_testIteratorHasNext_rv246374__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" %j(eFP%WS,>()]aV^2P%", ((Attributes) (a)).toString());
        Assert.assertEquals(-11519820, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" %j(eFP%WS,>()]aV^2P%", ((Attributes) (o_testIteratorHasNext_rv246374__5)).toString());
        Assert.assertEquals(-11519820, ((int) (((Attributes) (o_testIteratorHasNext_rv246374__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" %j(eFP%WS,>()]aV^2P%", ((Attributes) (o_testIteratorHasNext_rv246374__9)).toString());
        Assert.assertEquals(-11519820, ((int) (((Attributes) (o_testIteratorHasNext_rv246374__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246396() throws Exception {
        String __DSPOT_value_47554 = "&UeQCWTC%e9<a^6SdSii";
        String __DSPOT_key_47553 = "W[i+&XIhpm7Bb2@W*(!H";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246396__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246396__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246396__5)).hashCode())));
        Attributes o_testIteratorHasNext_rv246396__6 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246396__6)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246396__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "3");
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv246396__17 = __DSPOT_invoc_5.put(__DSPOT_key_47553, __DSPOT_value_47554);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" W[i+&XIhpm7Bb2@W*(!H=\"&amp;UeQCWTC%e9<a^6SdSii\"", ((Attributes) (o_testIteratorHasNext_rv246396__17)).toString());
        Assert.assertEquals(-1555324410, ((int) (((Attributes) (o_testIteratorHasNext_rv246396__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" W[i+&XIhpm7Bb2@W*(!H=\"&amp;UeQCWTC%e9<a^6SdSii\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1555324410, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" W[i+&XIhpm7Bb2@W*(!H=\"&amp;UeQCWTC%e9<a^6SdSii\"", ((Attributes) (o_testIteratorHasNext_rv246396__5)).toString());
        Assert.assertEquals(-1555324410, ((int) (((Attributes) (o_testIteratorHasNext_rv246396__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" W[i+&XIhpm7Bb2@W*(!H=\"&amp;UeQCWTC%e9<a^6SdSii\"", ((Attributes) (o_testIteratorHasNext_rv246396__6)).toString());
        Assert.assertEquals(-1555324410, ((int) (((Attributes) (o_testIteratorHasNext_rv246396__6)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246373() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246373__3 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246373__3)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246373__3)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv246373__7 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246373__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246373__7)).hashCode())));
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
        Assert.assertEquals(" tot=\"1\" hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246373__3)).toString());
        Assert.assertEquals(-521258618, ((int) (((Attributes) (o_testIteratorHasNext_rv246373__3)).hashCode())));
        Assert.assertEquals(" tot=\"1\" hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246373__7)).toString());
        Assert.assertEquals(-521258618, ((int) (((Attributes) (o_testIteratorHasNext_rv246373__7)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246395() throws Exception {
        boolean __DSPOT_value_47552 = false;
        String __DSPOT_key_47551 = "[qiMCK]Vq!4]N#6I.S8%";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246395__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246395__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246395__5)).hashCode())));
        Attributes o_testIteratorHasNext_rv246395__6 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246395__6)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246395__6)).hashCode())));
        Attributes __DSPOT_invoc_5 = a.put("data-name", "3");
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv246395__17 = __DSPOT_invoc_5.put(__DSPOT_key_47551, __DSPOT_value_47552);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246395__17)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246395__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246395__5)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246395__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246395__6)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246395__6)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246376() throws Exception {
        Attribute __DSPOT_attribute_47542 = new Attribute(".Pn,`U8/yBb=E>d&-`=4", "07u:Zhj1|`?pL+{_u1jj");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246376__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246376__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246376__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv246376__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246376__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246376__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv246376__17 = __DSPOT_invoc_4.put(__DSPOT_attribute_47542);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" .Pn,`U8/yBb=E>d&-`=4=\"07u:Zhj1|`?pL+{_u1jj\"", ((Attributes) (o_testIteratorHasNext_rv246376__17)).toString());
        Assert.assertEquals(662670407, ((int) (((Attributes) (o_testIteratorHasNext_rv246376__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" .Pn,`U8/yBb=E>d&-`=4=\"07u:Zhj1|`?pL+{_u1jj\"", ((Attributes) (a)).toString());
        Assert.assertEquals(662670407, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" .Pn,`U8/yBb=E>d&-`=4=\"07u:Zhj1|`?pL+{_u1jj\"", ((Attributes) (o_testIteratorHasNext_rv246376__5)).toString());
        Assert.assertEquals(662670407, ((int) (((Attributes) (o_testIteratorHasNext_rv246376__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" .Pn,`U8/yBb=E>d&-`=4=\"07u:Zhj1|`?pL+{_u1jj\"", ((Attributes) (o_testIteratorHasNext_rv246376__9)).toString());
        Assert.assertEquals(662670407, ((int) (((Attributes) (o_testIteratorHasNext_rv246376__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246375() throws Exception {
        String __DSPOT_value_47541 = "7Ix4amp, :0K!x)C3>_2";
        String __DSPOT_key_47540 = "eoC,|jV#|#s(P`X[]Ou|";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testIteratorHasNext_rv246375__5 = a.put("Tot", "1");
        Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNext_rv246375__5)).toString());
        Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNext_rv246375__5)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("Hello", "2");
        Attributes o_testIteratorHasNext_rv246375__9 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246375__9)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246375__9)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        Attributes o_testIteratorHasNext_rv246375__17 = __DSPOT_invoc_4.put(__DSPOT_key_47540, __DSPOT_value_47541);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" eoC,|jV#|#s(P`X[]Ou|=\"7Ix4amp, :0K!x)C3>_2\"", ((Attributes) (o_testIteratorHasNext_rv246375__17)).toString());
        Assert.assertEquals(1986467435, ((int) (((Attributes) (o_testIteratorHasNext_rv246375__17)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" eoC,|jV#|#s(P`X[]Ou|=\"7Ix4amp, :0K!x)C3>_2\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1986467435, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" eoC,|jV#|#s(P`X[]Ou|=\"7Ix4amp, :0K!x)C3>_2\"", ((Attributes) (o_testIteratorHasNext_rv246375__5)).toString());
        Assert.assertEquals(1986467435, ((int) (((Attributes) (o_testIteratorHasNext_rv246375__5)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\" eoC,|jV#|#s(P`X[]Ou|=\"7Ix4amp, :0K!x)C3>_2\"", ((Attributes) (o_testIteratorHasNext_rv246375__9)).toString());
        Assert.assertEquals(1986467435, ((int) (((Attributes) (o_testIteratorHasNext_rv246375__9)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNext_rv246356() throws Exception {
        String __DSPOT_key_47530 = "]3LF9^L(HN5mo7.s6D&K";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "1");
        Attributes o_testIteratorHasNext_rv246356__7 = a.put("Hello", "2");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNext_rv246356__7)).toString());
        Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNext_rv246356__7)).hashCode())));
        Attributes o_testIteratorHasNext_rv246356__8 = a.put("data-name", "3");
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246356__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246356__8)).hashCode())));
        int seen = 0;
        Assert.assertEquals(0, ((int) (seen)));
        for (Attribute attribute : a) {
            seen++;
            String.valueOf(seen);
            attribute.getValue();
        }
        __DSPOT_invoc_3.remove(__DSPOT_key_47530);
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246356__7)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246356__7)).hashCode())));
        Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNext_rv246356__8)).toString());
        Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNext_rv246356__8)).hashCode())));
        Assert.assertEquals(3, ((int) (seen)));
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246410_failAssert149null255394() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextnull246410_failAssert149null255394__5 = a.put("Tot", "1");
            Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextnull246410_failAssert149null255394__5)).toString());
            Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextnull246410_failAssert149null255394__5)).hashCode())));
            Attributes o_testIteratorHasNextnull246410_failAssert149null255394__6 = a.put("Hello", "2");
            Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextnull246410_failAssert149null255394__6)).toString());
            Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNextnull246410_failAssert149null255394__6)).hashCode())));
            a.put(null, "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246410 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246416_failAssert150litString255612() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150litString255612__5 = a.put("Tot", "\n");
            Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255612__5)).toString());
            Assert.assertEquals(555713616, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255612__5)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150litString255612__6 = a.put("Hello", "2");
            Assert.assertEquals(" Tot=\"\n\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255612__6)).toString());
            Assert.assertEquals(-172358191, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255612__6)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150litString255612__7 = a.put("data-name", "3");
            Assert.assertEquals(" Tot=\"\n\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255612__7)).toString());
            Assert.assertEquals(1206876941, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255612__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(null);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246416 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246416_failAssert150null258122_failAssert156() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                a.put(null, "1");
                a.put("Hello", "2");
                a.put("data-name", "3");
                int seen = 0;
                for (Attribute attribute : a) {
                    seen++;
                    String.valueOf(null);
                    attribute.getValue();
                }
                org.junit.Assert.fail("testIteratorHasNextnull246416 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorHasNextnull246416_failAssert150null258122 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246416_failAssert150null258150() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150null258150__5 = a.put("Tot", "1");
            Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150null258150__5)).toString());
            Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150null258150__5)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150null258150__6 = a.put("Hello", null);
            Assert.assertEquals(" Tot=\"1\" Hello", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150null258150__6)).toString());
            Assert.assertEquals(-171244392, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150null258150__6)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150null258150__7 = a.put("data-name", "3");
            Assert.assertEquals(" Tot=\"1\" Hello data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150null258150__7)).toString());
            Assert.assertEquals(1207990740, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150null258150__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(null);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246416 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246416_failAssert150null258145_failAssert154() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                a.put("Tot", "1");
                a.put(null, "2");
                a.put("data-name", "3");
                int seen = 0;
                for (Attribute attribute : a) {
                    seen++;
                    String.valueOf(null);
                    attribute.getValue();
                }
                org.junit.Assert.fail("testIteratorHasNextnull246416 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorHasNextnull246416_failAssert150null258145 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246416_failAssert150litString255565_failAssert157() throws Exception {
        try {
            try {
                Attributes a = new Attributes();
                a.put("", "1");
                a.put("Hello", "2");
                a.put("data-name", "3");
                int seen = 0;
                for (Attribute attribute : a) {
                    seen++;
                    String.valueOf(null);
                    attribute.getValue();
                }
                org.junit.Assert.fail("testIteratorHasNextnull246416 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorHasNextnull246416_failAssert150litString255565 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("String must not be empty", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246404_failAssert147null249568() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            a.put(null, "1");
            a.put("Hello", "2");
            a.put("data-name", "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246404 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246416_failAssert150_add256319() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150_add256319__5 = a.put("Tot", "1");
            Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__5)).toString());
            Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__5)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150_add256319__6 = a.put("Tot", "1");
            Assert.assertEquals(" Tot=\"1\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__6)).toString());
            Assert.assertEquals(556875465, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__6)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150_add256319__7 = a.put("Hello", "2");
            Assert.assertEquals(" Tot=\"1\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__7)).toString());
            Assert.assertEquals(-171196342, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__7)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150_add256319__8 = a.put("data-name", "3");
            Assert.assertEquals(" Tot=\"1\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__8)).toString());
            Assert.assertEquals(1208038790, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150_add256319__8)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(null);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246416 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246416_failAssert150litString255605() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150litString255605__5 = a.put("Tot", "");
            Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255605__5)).toString());
            Assert.assertEquals(555415706, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255605__5)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150litString255605__6 = a.put("Hello", "2");
            Assert.assertEquals(" Tot=\"\" Hello=\"2\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255605__6)).toString());
            Assert.assertEquals(-172656101, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255605__6)).hashCode())));
            Attributes o_testIteratorHasNextnull246416_failAssert150litString255605__7 = a.put("data-name", "3");
            Assert.assertEquals(" Tot=\"\" Hello=\"2\" data-name=\"3\"", ((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255605__7)).toString());
            Assert.assertEquals(1206579031, ((int) (((Attributes) (o_testIteratorHasNextnull246416_failAssert150litString255605__7)).hashCode())));
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(null);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246416 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246410_failAssert149null255310litNum295800() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            a.put(null, "1");
            a.put("Hello", "2");
            a.put(null, "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246410 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorHasNextnull246404_failAssert147null249568litString283329() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            a.put(null, "1");
            a.put("", "2");
            a.put("data-name", "3");
            int seen = 0;
            for (Attribute attribute : a) {
                seen++;
                String.valueOf(seen);
                attribute.getValue();
            }
            org.junit.Assert.fail("testIteratorHasNextnull246404 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorlitString151869() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "*!p]Z<u" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_58 = datas[i][0];
            attribute.getKey();
            String String_59 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"*!p]Z<u\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2027503744, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratornull151976_failAssert66() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                a.put(null, atts[1]);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_270 = datas[i][0];
                attribute.getKey();
                String String_271 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIteratornull151976 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorlitString151846_failAssert38() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                a.put(atts[0], atts[1]);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_12 = datas[i][0];
                attribute.getKey();
                String String_13 = datas[i][1];
                attribute.getValue();
                i++;
            }
            org.junit.Assert.fail("testIteratorlitString151846 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorlitString151857() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Tot", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_34 = datas[i][0];
            attribute.getKey();
            String String_35 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-649411240, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(2, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv151959() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            String __DSPOT_key_28381 = "gcT1(^bk31VXr$n5^5p0";
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.getIgnoreCase(__DSPOT_key_28381);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
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
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratorlitNum151896() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[0]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_112 = datas[i][0];
            attribute.getKey();
            String String_113 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"Tot\" Hello=\"Hello\" data-name=\"data-name\"", ((Attributes) (a)).toString());
        Assert.assertEquals(2075780291, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv151955() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.clone();
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
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
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151940() throws Exception {
        String __DSPOT_key_28374 = "WZlpY2DQ/(LdK9s4]3y(";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_200 = datas[i][0];
            attribute.getKey();
            String String_201 = datas[i][1];
            attribute.getValue();
            i++;
        }
        a.removeIgnoreCase(__DSPOT_key_28374);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151950() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String __DSPOT_val_28377 = "21ZZwWzPR7H0wFz964L>";
            String String_220 = datas[i][0];
            attribute.getKey();
            String String_221 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.setValue(__DSPOT_val_28377);
        }
        Assert.assertEquals(" Tot=\"21ZZwWzPR7H0wFz964L>\" Hello=\"21ZZwWzPR7H0wFz964L>\" data-name=\"21ZZwWzPR7H0wFz964L>\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2104881032, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv151968_failAssert63() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                Attribute __DSPOT_attribute_28388 = new Attribute("@ncw.^{z4ahz<*.v>BJC", "s._v!v[7ELqS(cx2LNPY");
                Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_7.put(__DSPOT_attribute_28388);
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
            org.junit.Assert.fail("testIterator_rv151968 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_remove151922() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_164 = datas[i][0];
            attribute.getKey();
            String String_165 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(0, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv151967_failAssert62() throws Exception {
        try {
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                String __DSPOT_value_28387 = "]P_C-ky72gS,qEYP#L_(";
                String __DSPOT_key_28386 = "Dt&97^(QbS*XX* -ex:?";
                Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_7.put(__DSPOT_key_28386, __DSPOT_value_28387);
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
            org.junit.Assert.fail("testIterator_rv151967 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg151931() throws Exception {
        String __DSPOT_key_28367 = "^;Oy_On%4JsJiZqx&%^x";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_182 = datas[i][0];
            attribute.getKey();
            String String_183 = datas[i][1];
            attribute.getValue();
            i++;
        }
        boolean o_testIterator_mg151931__21 = a.hasKeyIgnoreCase(__DSPOT_key_28367);
        Assert.assertFalse(o_testIterator_mg151931__21);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151927() throws Exception {
        Attributes __DSPOT_o_28363 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_174 = datas[i][0];
            attribute.getKey();
            String String_175 = datas[i][1];
            attribute.getValue();
            i++;
        }
        boolean o_testIterator_mg151927__22 = a.equals(__DSPOT_o_28363);
        Assert.assertFalse(o_testIterator_mg151927__22);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_rv151954() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_7 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_7.asList();
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
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
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratorlitString151871() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "\n" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_62 = datas[i][0];
            attribute.getKey();
            String String_63 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot=\"raul\" Hello=\"\n\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(124258075, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151948() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
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
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151925() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_170 = datas[i][0];
            attribute.getKey();
            String String_171 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151925__20 = a.clone();
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (o_testIterator_mg151925__20)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (o_testIterator_mg151925__20)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-274504897, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151936() throws Exception {
        boolean __DSPOT_value_28369 = false;
        String __DSPOT_key_28368 = "fUO!&XtBu00F#^{[Tb&F";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_192 = datas[i][0];
            attribute.getKey();
            String String_193 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151936__22 = a.put(__DSPOT_key_28368, __DSPOT_value_28369);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (o_testIterator_mg151936__22)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (o_testIterator_mg151936__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151924() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_168 = datas[i][0];
            attribute.getKey();
            String String_169 = datas[i][1];
            attribute.getValue();
            i++;
        }
        List<Attribute> o_testIterator_mg151924__20 = a.asList();
        Assert.assertFalse(o_testIterator_mg151924__20.isEmpty());
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151935() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_190 = datas[i][0];
            attribute.getKey();
            String String_191 = datas[i][1];
            attribute.getValue();
            i++;
        }
        a.normalize();
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1649101113, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIteratornull151977() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], null);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        Assert.assertEquals(0, ((int) (i)));
        for (Attribute attribute : a) {
            String String_272 = datas[i][0];
            attribute.getKey();
            String String_273 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Assert.assertEquals(" Tot Hello data-name", ((Attributes) (a)).toString());
        Assert.assertEquals(1206529400, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(3, ((int) (i)));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_rv169326() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.asList();
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg172790() throws Exception {
        String __DSPOT_val_32075 = "Z[kTHX?NGr4#gd+):hH-";
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        String o_testIterator_mg151938_mg172790__26 = __DSPOT_attribute_28372.setValue(__DSPOT_val_32075);
        Assert.assertEquals("(e2-N0!BD-!9K8f@O`[)", o_testIterator_mg151938_mg172790__26);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"Z[kTHX?NGr4#gd+):hH-\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1438255542, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"Z[kTHX?NGr4#gd+):hH-\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(-1438255542, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174111() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String __DSPOT_val_32354 = "5kbor<=2bPE!ol(^x+7z";
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.setValue(__DSPOT_val_32354);
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"5kbor<=2bPE!ol(^x+7z\" Hello=\"5kbor<=2bPE!ol(^x+7z\" data-name=\"5kbor<=2bPE!ol(^x+7z\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(-1618236905, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"5kbor<=2bPE!ol(^x+7z\" Hello=\"5kbor<=2bPE!ol(^x+7z\" data-name=\"5kbor<=2bPE!ol(^x+7z\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1618236905, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg173486() throws Exception {
        Attribute __DSPOT_attribute_32193 = new Attribute("0A!id=-!7eJ:y;yy{y_t", "y<a+:#ngThO`lx}.fA|[", new Attributes());
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg173486__28 = a.put(__DSPOT_attribute_32193);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" 0A!id=-!7eJ:y;yy{y_t=\"y<a+:#ngThO`lx}.fA|[\"", ((Attributes) (o_testIterator_mg151938_mg173486__28)).toString());
        Assert.assertEquals(992272778, ((int) (((Attributes) (o_testIterator_mg151938_mg173486__28)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" 0A!id=-!7eJ:y;yy{y_t=\"y<a+:#ngThO`lx}.fA|[\"", ((Attributes) (a)).toString());
        Assert.assertEquals(992272778, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" 0A!id=-!7eJ:y;yy{y_t=\"y<a+:#ngThO`lx}.fA|[\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(992272778, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151936_mg169713() throws Exception {
        String __DSPOT_value_31576 = "I(!&GRJv[)[V,-8V>YK*";
        String __DSPOT_key_31575 = "JhW(KRZ(r7PQMFv#Jv]3";
        boolean __DSPOT_value_28369 = false;
        String __DSPOT_key_28368 = "fUO!&XtBu00F#^{[Tb&F";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_192 = datas[i][0];
            attribute.getKey();
            String String_193 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151936__22 = a.put(__DSPOT_key_28368, __DSPOT_value_28369);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\"", ((Attributes) (o_testIterator_mg151936__22)).toString());
        Assert.assertEquals(80196295, ((int) (((Attributes) (o_testIterator_mg151936__22)).hashCode())));
        Attributes o_testIterator_mg151936_mg169713__27 = o_testIterator_mg151936__22.put(__DSPOT_key_31575, __DSPOT_value_31576);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" JhW(KRZ(r7PQMFv#Jv]3=\"I(!&amp;GRJv[)[V,-8V>YK*\"", ((Attributes) (o_testIterator_mg151936_mg169713__27)).toString());
        Assert.assertEquals(1287059795, ((int) (((Attributes) (o_testIterator_mg151936_mg169713__27)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" JhW(KRZ(r7PQMFv#Jv]3=\"I(!&amp;GRJv[)[V,-8V>YK*\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1287059795, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" JhW(KRZ(r7PQMFv#Jv]3=\"I(!&amp;GRJv[)[V,-8V>YK*\"", ((Attributes) (o_testIterator_mg151936__22)).toString());
        Assert.assertEquals(1287059795, ((int) (((Attributes) (o_testIterator_mg151936__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_rv169387() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.clone();
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_add172176() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg167948() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg167948__25 = o_testIterator_mg151937__22.clone();
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937_mg167948__25)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937_mg167948__25)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938litString169557() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(266536757, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" Tot=\"a&amp;p\" Hello=\"There\" data-name=\"Jsoup\"=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(266536757, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg172756() throws Exception {
        String __DSPOT_key_32066 = ")Q{ak]q8H?XPhT%3k[{}";
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        __DSPOT_attribute_28372.setKey(__DSPOT_key_32066);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )Q{ak]q8H?XPhT%3k[{}=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1340043256, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" )Q{ak]q8H?XPhT%3k[{}=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(-1340043256, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg172536() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attribute o_testIterator_mg151938_mg172536__25 = __DSPOT_attribute_28372.clone();
        Assert.assertEquals(">L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attribute) (o_testIterator_mg151938_mg172536__25)).toString());
        Assert.assertEquals(1353456243, ((int) (((Attribute) (o_testIterator_mg151938_mg172536__25)).hashCode())));
        Assert.assertEquals("(e2-N0!BD-!9K8f@O`[)", ((Attribute) (o_testIterator_mg151938_mg172536__25)).getValue());
        Assert.assertEquals(">L4D}S{ ks/jt/j)&c6R", ((Attribute) (o_testIterator_mg151938_mg172536__25)).getKey());
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168793() throws Exception {
        String __DSPOT_key_31477 = "=W%=L:[|IIzT&FVklr8{";
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        o_testIterator_mg151937__22.removeIgnoreCase(__DSPOT_key_31477);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168490() throws Exception {
        boolean __DSPOT_value_31447 = false;
        String __DSPOT_key_31446 = "7u$t>6^ar3>IXJ2gVf5Q";
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168490__27 = o_testIterator_mg151937__22.put(__DSPOT_key_31446, __DSPOT_value_31447);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937_mg168490__27)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937_mg168490__27)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_rv170240_failAssert68() throws Exception {
        try {
            String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
            String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                String __DSPOT_value_31651 = "^I1QU<s#gz!ZbQ4Yv{f_";
                String __DSPOT_key_31650 = ".iZk^V}P#o/(ke}@!y;%";
                Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_15.put(__DSPOT_key_31650, __DSPOT_value_31651);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_194 = datas[i][0];
                attribute.getKey();
                String String_195 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
            org.junit.Assert.fail("testIterator_mg151937_rv170240 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg167911() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        List<Attribute> o_testIterator_mg151937_mg167911__25 = o_testIterator_mg151937__22.asList();
        Assert.assertFalse(o_testIterator_mg151937_mg167911__25.isEmpty());
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg173453() throws Exception {
        String __DSPOT_value_32182 = "4(?Q+7[vYf(Ki=?k[!w@";
        String __DSPOT_key_32181 = "Ib5siHmGHY}KcC1{W&UB";
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg173453__27 = a.put(__DSPOT_key_32181, __DSPOT_value_32182);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" Ib5siHmGHY}KcC1{W&UB=\"4(?Q+7[vYf(Ki=?k[!w@\"", ((Attributes) (o_testIterator_mg151938_mg173453__27)).toString());
        Assert.assertEquals(723376773, ((int) (((Attributes) (o_testIterator_mg151938_mg173453__27)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" Ib5siHmGHY}KcC1{W&UB=\"4(?Q+7[vYf(Ki=?k[!w@\"", ((Attributes) (a)).toString());
        Assert.assertEquals(723376773, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" Ib5siHmGHY}KcC1{W&UB=\"4(?Q+7[vYf(Ki=?k[!w@\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(723376773, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg167598() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
            attribute.html();
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_rv175210() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            String __DSPOT_key_32630 = "1k9!?u*lU9}P(h](cz2V";
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.getIgnoreCase(__DSPOT_key_32630);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168448() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        o_testIterator_mg151937__22.normalize();
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" +p]w.q-x1e-6=gzz(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-826171302, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"raul\" hello=\"pismuth\" data-name=\"Jsoup\" +p]w.q-x1e-6=gzz(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(-826171302, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg172567() throws Exception {
        Attribute __DSPOT_o_32020 = new Attribute("d-58dat+)A<(_qa,gs{b", "5Q!trxtv]6.X/X*%%EHB");
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        boolean o_testIterator_mg151938_mg172567__27 = __DSPOT_attribute_28372.equals(__DSPOT_o_32020);
        Assert.assertFalse(o_testIterator_mg151938_mg172567__27);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168288() throws Exception {
        String __DSPOT_key_31405 = "uxG !]ZO`&CI*?=F]`KN";
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        boolean o_testIterator_mg151937_mg168288__26 = o_testIterator_mg151937__22.hasKeyIgnoreCase(__DSPOT_key_31405);
        Assert.assertFalse(o_testIterator_mg151937_mg168288__26);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_rv170286_failAssert71() throws Exception {
        try {
            String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
            String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                Attribute __DSPOT_attribute_31666 = new Attribute("x%Ir31bj@52 *S$8H[#1", "Ou]bE.Vy*{RP]k?Mr/,#");
                Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_15.put(__DSPOT_attribute_31666);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_194 = datas[i][0];
                attribute.getKey();
                String String_195 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
            org.junit.Assert.fail("testIterator_mg151937_rv170286 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174409() throws Exception {
        Object __DSPOT_o_32425 = new Object();
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        boolean o_testIterator_mg151938_mg174409__27 = o_testIterator_mg151938__22.equals(__DSPOT_o_32425);
        Assert.assertFalse(o_testIterator_mg151938_mg174409__27);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg172726() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        String o_testIterator_mg151938_mg172726__25 = __DSPOT_attribute_28372.html();
        Assert.assertEquals(">L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", o_testIterator_mg151938_mg172726__25);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151936_rv171269_failAssert74() throws Exception {
        try {
            boolean __DSPOT_value_28369 = false;
            String __DSPOT_key_28368 = "fUO!&XtBu00F#^{[Tb&F";
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                boolean __DSPOT_value_31818 = true;
                String __DSPOT_key_31817 = "fK&X*Q<4ttJ 0F-*U+SF";
                Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_15.put(__DSPOT_key_31817, __DSPOT_value_31818);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_192 = datas[i][0];
                attribute.getKey();
                String String_193 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg151936__22 = a.put(__DSPOT_key_28368, __DSPOT_value_28369);
            org.junit.Assert.fail("testIterator_mg151936_rv171269 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_rv174998() throws Exception {
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_incoming_32576 = new Attributes();
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.addAll(__DSPOT_incoming_32576);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg167173() throws Exception {
        boolean __DSPOT_value_31053 = true;
        String __DSPOT_key_31052 = "Sb@JTScdLfX^YSu(X ;|";
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg167173__27 = a.put(__DSPOT_key_31052, __DSPOT_value_31053);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" Sb@JTScdLfX^YSu(X ;|", ((Attributes) (o_testIterator_mg151937_mg167173__27)).toString());
        Assert.assertEquals(1944935722, ((int) (((Attributes) (o_testIterator_mg151937_mg167173__27)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" Sb@JTScdLfX^YSu(X ;|", ((Attributes) (a)).toString());
        Assert.assertEquals(1944935722, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" Sb@JTScdLfX^YSu(X ;|", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1944935722, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_rv169848() throws Exception {
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_15.hashCode();
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg173051() throws Exception {
        Attributes __DSPOT_o_32116 = new Attributes();
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        boolean o_testIterator_mg151938_mg173051__27 = a.equals(__DSPOT_o_32116);
        Assert.assertFalse(o_testIterator_mg151938_mg173051__27);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_mg207059() throws Exception {
        String __DSPOT_key_38841 = "vnuSQVd-X{c5|KGqA Fw";
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        o_testIterator_mg151937_mg168607__28.removeIgnoreCase(__DSPOT_key_38841);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_mg207016() throws Exception {
        String __DSPOT_key_38823 = "MR7BwS&W4rnK#+.C%-Vv";
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        o_testIterator_mg151937_mg168607__28.remove(__DSPOT_key_38823);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg173486_mg197614() throws Exception {
        Attribute __DSPOT_o_36734 = new Attribute("#*pI(g5I#,k}{i/I[GO6", "^e:TFKWJCCt.ZnkH.8xz", new Attributes());
        Attribute __DSPOT_attribute_32193 = new Attribute("0A!id=-!7eJ:y;yy{y_t", "y<a+:#ngThO`lx}.fA|[", new Attributes());
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg173486__28 = a.put(__DSPOT_attribute_32193);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" 0A!id=-!7eJ:y;yy{y_t=\"y<a+:#ngThO`lx}.fA|[\"", ((Attributes) (o_testIterator_mg151938_mg173486__28)).toString());
        Assert.assertEquals(992272778, ((int) (((Attributes) (o_testIterator_mg151938_mg173486__28)).hashCode())));
        boolean o_testIterator_mg151938_mg173486_mg197614__34 = __DSPOT_attribute_32193.equals(__DSPOT_o_36734);
        Assert.assertFalse(o_testIterator_mg151938_mg173486_mg197614__34);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" 0A!id=-!7eJ:y;yy{y_t=\"y<a+:#ngThO`lx}.fA|[\"", ((Attributes) (a)).toString());
        Assert.assertEquals(992272778, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" 0A!id=-!7eJ:y;yy{y_t=\"y<a+:#ngThO`lx}.fA|[\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(992272778, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" 0A!id=-!7eJ:y;yy{y_t=\"y<a+:#ngThO`lx}.fA|[\"", ((Attributes) (o_testIterator_mg151938_mg173486__28)).toString());
        Assert.assertEquals(992272778, ((int) (((Attributes) (o_testIterator_mg151938_mg173486__28)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174792_mg199650() throws Exception {
        String __DSPOT_value_37312 = "`ivW:uA/`qa0f1z>GOm/";
        String __DSPOT_key_37311 = "jw0F5b@YnNL$M=6-(/IK";
        String __DSPOT_value_32526 = "W1Q:A1h8[ByG=of|}Vp4";
        String __DSPOT_key_32525 = "x*%!|J[JF:Ja4r|ix.oQ";
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg174792__27 = o_testIterator_mg151938__22.put(__DSPOT_key_32525, __DSPOT_value_32526);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\"", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(-1802094472, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
        Attributes o_testIterator_mg151938_mg174792_mg199650__32 = a.put(__DSPOT_key_37311, __DSPOT_value_37312);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" jw0F5b@YnNL$M=6-(/IK=\"`ivW:uA/`qa0f1z>GOm/\"", ((Attributes) (o_testIterator_mg151938_mg174792_mg199650__32)).toString());
        Assert.assertEquals(731589556, ((int) (((Attributes) (o_testIterator_mg151938_mg174792_mg199650__32)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" jw0F5b@YnNL$M=6-(/IK=\"`ivW:uA/`qa0f1z>GOm/\"", ((Attributes) (a)).toString());
        Assert.assertEquals(731589556, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" jw0F5b@YnNL$M=6-(/IK=\"`ivW:uA/`qa0f1z>GOm/\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(731589556, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" jw0F5b@YnNL$M=6-(/IK=\"`ivW:uA/`qa0f1z>GOm/\"", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(731589556, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174792_mg199695() throws Exception {
        Attribute __DSPOT_attribute_37319 = new Attribute("`;n4> >bw{>gB(#=MKL$", "+%ggT0cRb$6=:D+{tx<A", new Attributes());
        String __DSPOT_value_32526 = "W1Q:A1h8[ByG=of|}Vp4";
        String __DSPOT_key_32525 = "x*%!|J[JF:Ja4r|ix.oQ";
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg174792__27 = o_testIterator_mg151938__22.put(__DSPOT_key_32525, __DSPOT_value_32526);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\"", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(-1802094472, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
        Attributes o_testIterator_mg151938_mg174792_mg199695__33 = a.put(__DSPOT_attribute_37319);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" `;n4> >bw{>gB(#=MKL$=\"+%ggT0cRb$6=:D+{tx<A\"", ((Attributes) (o_testIterator_mg151938_mg174792_mg199695__33)).toString());
        Assert.assertEquals(-878563232, ((int) (((Attributes) (o_testIterator_mg151938_mg174792_mg199695__33)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" `;n4> >bw{>gB(#=MKL$=\"+%ggT0cRb$6=:D+{tx<A\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-878563232, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" `;n4> >bw{>gB(#=MKL$=\"+%ggT0cRb$6=:D+{tx<A\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(-878563232, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" `;n4> >bw{>gB(#=MKL$=\"+%ggT0cRb$6=:D+{tx<A\"", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(-878563232, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174817_rv208628() throws Exception {
        Attribute __DSPOT_attribute_32530 = new Attribute("`FR0eIVbFzMw&1F%T82q", "o)hax%#4lf]%q[*9yY31");
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            Object __DSPOT_o_39107 = new Object();
            Attributes __DSPOT_invoc_17 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_17.equals(__DSPOT_o_39107);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg174817__27 = o_testIterator_mg151938__22.put(__DSPOT_attribute_32530);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" `FR0eIVbFzMw&1F%T82q=\"o)hax%#4lf]%q[*9yY31\"", ((Attributes) (o_testIterator_mg151938_mg174817__27)).toString());
        Assert.assertEquals(-37354863, ((int) (((Attributes) (o_testIterator_mg151938_mg174817__27)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" `FR0eIVbFzMw&1F%T82q=\"o)hax%#4lf]%q[*9yY31\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-37354863, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" `FR0eIVbFzMw&1F%T82q=\"o)hax%#4lf]%q[*9yY31\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(-37354863, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_mg204840() throws Exception {
        String __DSPOT_key_38148 = "G5x!HEdYxZ=Z>m+8Oc/#";
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        boolean o_testIterator_mg151937_mg168607_mg204840__32 = o_testIterator_mg151937__22.hasKeyIgnoreCase(__DSPOT_key_38148);
        Assert.assertFalse(o_testIterator_mg151937_mg168607_mg204840__32);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174792_rv204608_failAssert125() throws Exception {
        try {
            String __DSPOT_value_32526 = "W1Q:A1h8[ByG=of|}Vp4";
            String __DSPOT_key_32525 = "x*%!|J[JF:Ja4r|ix.oQ";
            Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                boolean __DSPOT_value_38104 = true;
                String __DSPOT_key_38103 = "Gl##@T83Rb CiRl^N&R(";
                Attributes __DSPOT_invoc_17 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_17.put(__DSPOT_key_38103, __DSPOT_value_38104);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_196 = datas[i][0];
                attribute.getKey();
                String String_197 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
            Attributes o_testIterator_mg151938_mg174792__27 = o_testIterator_mg151938__22.put(__DSPOT_key_32525, __DSPOT_value_32526);
            org.junit.Assert.fail("testIterator_mg151938_mg174792_rv204608 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg172536_rv209094_failAssert128() throws Exception {
        try {
            Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                Attribute __DSPOT_attribute_39163 = new Attribute("kTh?P5iZSvO?IV 6.[+G", "({RHaz}Tk!sV3LzONLzP", new Attributes());
                Attributes __DSPOT_invoc_15 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_15.put(__DSPOT_attribute_39163);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_196 = datas[i][0];
                attribute.getKey();
                String String_197 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
            Attribute o_testIterator_mg151938_mg172536__25 = __DSPOT_attribute_28372.clone();
            org.junit.Assert.fail("testIterator_mg151938_mg172536_rv209094 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_mg204387() throws Exception {
        Object __DSPOT_o_38042 = new Object();
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        boolean o_testIterator_mg151937_mg168607_mg204387__33 = o_testIterator_mg151937__22.equals(__DSPOT_o_38042);
        Assert.assertFalse(o_testIterator_mg151937_mg168607_mg204387__33);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_mg202573() throws Exception {
        String __DSPOT_val_37658 = "xm@:l{j`Lo4TJEg[#e9`";
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        String o_testIterator_mg151937_mg168607_mg202573__32 = __DSPOT_attribute_31457.setValue(__DSPOT_val_37658);
        Assert.assertEquals("8M2`2/q!xn[Bl5$W,4E8", o_testIterator_mg151937_mg168607_mg202573__32);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"xm@:l{j`Lo4TJEg[#e9`\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1695541339, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"xm@:l{j`Lo4TJEg[#e9`\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1695541339, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"xm@:l{j`Lo4TJEg[#e9`\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1695541339, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174792_rv204750_failAssert104() throws Exception {
        try {
            String __DSPOT_value_32526 = "W1Q:A1h8[ByG=of|}Vp4";
            String __DSPOT_key_32525 = "x*%!|J[JF:Ja4r|ix.oQ";
            Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
            Attributes a = new Attributes();
            String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
            for (String[] atts : datas) {
                String __DSPOT_value_38120 = "@h7axdyQ#S/*p+N]7J!y";
                String __DSPOT_key_38119 = "HHr`pROmtMC9k:Xnog<&";
                Attributes __DSPOT_invoc_17 = a.put(atts[0], atts[1]);
                __DSPOT_invoc_17.put(__DSPOT_key_38119, __DSPOT_value_38120);
            }
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            int i = 0;
            for (Attribute attribute : a) {
                String String_196 = datas[i][0];
                attribute.getKey();
                String String_197 = datas[i][1];
                attribute.getValue();
                i++;
            }
            Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
            Attributes o_testIterator_mg151938_mg174792__27 = o_testIterator_mg151938__22.put(__DSPOT_key_32525, __DSPOT_value_32526);
            org.junit.Assert.fail("testIterator_mg151938_mg174792_rv204750 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174792_mg200724() throws Exception {
        Attributes __DSPOT_o_37486 = new Attributes();
        String __DSPOT_value_32526 = "W1Q:A1h8[ByG=of|}Vp4";
        String __DSPOT_key_32525 = "x*%!|J[JF:Ja4r|ix.oQ";
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg174792__27 = o_testIterator_mg151938__22.put(__DSPOT_key_32525, __DSPOT_value_32526);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\"", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(-1802094472, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
        boolean o_testIterator_mg151938_mg174792_mg200724__32 = o_testIterator_mg151938__22.equals(__DSPOT_o_37486);
        Assert.assertFalse(o_testIterator_mg151938_mg174792_mg200724__32);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1802094472, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(-1802094472, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\"", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(-1802094472, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg174792_mg201316() throws Exception {
        boolean __DSPOT_value_37534 = true;
        String __DSPOT_key_37533 = "E(,K$r:_shdn!RY+Kb-{";
        String __DSPOT_value_32526 = "W1Q:A1h8[ByG=of|}Vp4";
        String __DSPOT_key_32525 = "x*%!|J[JF:Ja4r|ix.oQ";
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attributes o_testIterator_mg151938_mg174792__27 = o_testIterator_mg151938__22.put(__DSPOT_key_32525, __DSPOT_value_32526);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\"", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(-1802094472, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
        Attributes o_testIterator_mg151938_mg174792_mg201316__32 = o_testIterator_mg151938__22.put(__DSPOT_key_37533, __DSPOT_value_37534);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" E(,K$r:_shdn!RY+Kb-{", ((Attributes) (o_testIterator_mg151938_mg174792_mg201316__32)).toString());
        Assert.assertEquals(1111801693, ((int) (((Attributes) (o_testIterator_mg151938_mg174792_mg201316__32)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" E(,K$r:_shdn!RY+Kb-{", ((Attributes) (a)).toString());
        Assert.assertEquals(1111801693, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" E(,K$r:_shdn!RY+Kb-{", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1111801693, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\" x*%!|J[JF:Ja4r|ix.oQ=\"W1Q:A1h8[ByG=of|}Vp4\" E(,K$r:_shdn!RY+Kb-{", ((Attributes) (o_testIterator_mg151938_mg174792__27)).toString());
        Assert.assertEquals(1111801693, ((int) (((Attributes) (o_testIterator_mg151938_mg174792__27)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_mg203225() throws Exception {
        String __DSPOT_value_37813 = "Y|_ec.#EemC{4gNwBZ)(";
        String __DSPOT_key_37811 = "ffwaC 3fJpZ<|)<)eY{}";
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607_mg203225__33 = a.put(__DSPOT_key_37811, __DSPOT_value_37813);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\" ffwaC 3fJpZ<|)<)eY{}=\"Y|_ec.#EemC{4gNwBZ)(\"", ((Attributes) (o_testIterator_mg151937_mg168607_mg203225__33)).toString());
        Assert.assertEquals(-583665090, ((int) (((Attributes) (o_testIterator_mg151937_mg168607_mg203225__33)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\" ffwaC 3fJpZ<|)<)eY{}=\"Y|_ec.#EemC{4gNwBZ)(\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-583665090, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\" ffwaC 3fJpZ<|)<)eY{}=\"Y|_ec.#EemC{4gNwBZ)(\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(-583665090, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\" ffwaC 3fJpZ<|)<)eY{}=\"Y|_ec.#EemC{4gNwBZ)(\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(-583665090, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151938_mg172536_mg207630() throws Exception {
        Attribute __DSPOT_o_38998 = new Attribute("up&,%FT)::[Vnf)|5sKi", "2}r Lx m@QqI_YPvVNMH", new Attributes());
        Attribute __DSPOT_attribute_28372 = new Attribute(">L4D}S{ ks/jt/j)&c6R", "(e2-N0!BD-!9K8f@O`[)");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            a.put(atts[0], atts[1]);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_196 = datas[i][0];
            attribute.getKey();
            String String_197 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151938__22 = a.put(__DSPOT_attribute_28372);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Attribute o_testIterator_mg151938_mg172536__25 = __DSPOT_attribute_28372.clone();
        Assert.assertEquals(">L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attribute) (o_testIterator_mg151938_mg172536__25)).toString());
        Assert.assertEquals(1353456243, ((int) (((Attribute) (o_testIterator_mg151938_mg172536__25)).hashCode())));
        Assert.assertEquals("(e2-N0!BD-!9K8f@O`[)", ((Attribute) (o_testIterator_mg151938_mg172536__25)).getValue());
        Assert.assertEquals(">L4D}S{ ks/jt/j)&c6R", ((Attribute) (o_testIterator_mg151938_mg172536__25)).getKey());
        boolean o_testIterator_mg151938_mg172536_mg207630__31 = o_testIterator_mg151938_mg172536__25.equals(__DSPOT_o_38998);
        Assert.assertFalse(o_testIterator_mg151938_mg172536_mg207630__31);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" >L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attributes) (o_testIterator_mg151938__22)).toString());
        Assert.assertEquals(1433653499, ((int) (((Attributes) (o_testIterator_mg151938__22)).hashCode())));
        Assert.assertEquals(">L4D}S{ ks/jt/j)&c6R=\"(e2-N0!BD-!9K8f@O`[)\"", ((Attribute) (o_testIterator_mg151938_mg172536__25)).toString());
        Assert.assertEquals(1353456243, ((int) (((Attribute) (o_testIterator_mg151938_mg172536__25)).hashCode())));
        Assert.assertEquals("(e2-N0!BD-!9K8f@O`[)", ((Attribute) (o_testIterator_mg151938_mg172536__25)).getValue());
        Assert.assertEquals(">L4D}S{ ks/jt/j)&c6R", ((Attribute) (o_testIterator_mg151938_mg172536__25)).getKey());
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_rv207598() throws Exception {
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            String __DSPOT_key_38993 = "*rzxEJH#QaMs|9KKdLN#";
            Attributes __DSPOT_invoc_18 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_18.getIgnoreCase(__DSPOT_key_38993);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIterator_mg151937_mg168607_rv207930() throws Exception {
        Attribute __DSPOT_attribute_31457 = new Attribute("p 4>o])yv#z)>k.5FmvV", "8M2`2/q!xn[Bl5$W,4E8", new Attributes());
        String __DSPOT_value_28371 = "_|(8N6fJ!vFMr0[6]:X_";
        String __DSPOT_key_28370 = "+P]W.q-X1E-6=GzZ(c#4";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        String[][] datas = new String[][]{ new String[]{ "Tot", "raul" }, new String[]{ "Hello", "pismuth" }, new String[]{ "data-name", "Jsoup" } };
        for (String[] atts : datas) {
            boolean __DSPOT_value_39036 = false;
            String __DSPOT_key_39035 = "mq[.6xP=rdonAMy}i|cf";
            Attributes __DSPOT_invoc_18 = a.put(atts[0], atts[1]);
            __DSPOT_invoc_18.put(__DSPOT_key_39035, __DSPOT_value_39036);
        }
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        int i = 0;
        for (Attribute attribute : a) {
            String String_194 = datas[i][0];
            attribute.getKey();
            String String_195 = datas[i][1];
            attribute.getValue();
            i++;
        }
        Attributes o_testIterator_mg151937__22 = a.put(__DSPOT_key_28370, __DSPOT_value_28371);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1252982042, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
        Attributes o_testIterator_mg151937_mg168607__28 = o_testIterator_mg151937__22.put(__DSPOT_attribute_31457);
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937_mg168607__28)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937_mg168607__28)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" Tot=\"raul\" Hello=\"pismuth\" data-name=\"Jsoup\" +P]W.q-X1E-6=GzZ(c#4=\"_|(8N6fJ!vFMr0[6]:X_\" p 4>o])yv#z)>k.5FmvV=\"8M2`2/q!xn[Bl5$W,4E8\"", ((Attributes) (o_testIterator_mg151937__22)).toString());
        Assert.assertEquals(1882072582, ((int) (((Attributes) (o_testIterator_mg151937__22)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223458() throws Exception {
        String __DSPOT_key_41962 = "dB*T#Lp*c`M!:|9Bc$Yi";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        boolean o_testIteratorEmpty_mg223458__7 = a.hasKeyIgnoreCase(__DSPOT_key_41962);
        Assert.assertFalse(o_testIteratorEmpty_mg223458__7);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465() throws Exception {
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223454() throws Exception {
        Attributes __DSPOT_o_41958 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        boolean o_testIteratorEmpty_mg223454__8 = a.equals(__DSPOT_o_41958);
        Assert.assertTrue(o_testIteratorEmpty_mg223454__8);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464() throws Exception {
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (a)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223456() throws Exception {
        String __DSPOT_key_41960 = "#9-!?R(=J;y2IwhH6Mmo";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        String o_testIteratorEmpty_mg223456__7 = a.getIgnoreCase(__DSPOT_key_41960);
        Assert.assertEquals("", o_testIteratorEmpty_mg223456__7);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223467() throws Exception {
        String __DSPOT_key_41969 = "R:9cmF9i^dfzai*ozyYf";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        a.removeIgnoreCase(__DSPOT_key_41969);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223461() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        a.iterator();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223452() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223452__6 = a.clone();
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223452__6)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223452__6)).hashCode())));
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223463() throws Exception {
        boolean __DSPOT_value_41964 = false;
        String __DSPOT_key_41963 = "lK}m],Yjf<4viJT;V7R1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223463__8 = a.put(__DSPOT_key_41963, __DSPOT_value_41964);
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223451() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        List<Attribute> o_testIteratorEmpty_mg223451__6 = a.asList();
        Assert.assertTrue(o_testIteratorEmpty_mg223451__6.isEmpty());
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223462() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        a.normalize();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464null225051_failAssert139() throws Exception {
        try {
            String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
            String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            Attributes o_testIteratorEmpty_mg223464__8 = a.put(null, __DSPOT_value_41966);
            org.junit.Assert.fail("testIteratorEmpty_mg223464null225051 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224169() throws Exception {
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg223464_mg224169__11 = a.clone();
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464_mg224169__11)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464_mg224169__11)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1703824248, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(1703824248, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223463_mg224946() throws Exception {
        String __DSPOT_key_42368 = "z?<D7KZ&+k,(6W3uxS56";
        boolean __DSPOT_value_41964 = false;
        String __DSPOT_key_41963 = "lK}m],Yjf<4viJT;V7R1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223463__8 = a.put(__DSPOT_key_41963, __DSPOT_value_41964);
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
        o_testIteratorEmpty_mg223463__8.removeIgnoreCase(__DSPOT_key_42368);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223463_mg224940() throws Exception {
        String __DSPOT_key_42365 = "FOEdo@n:YfjaM9`tyO9N";
        boolean __DSPOT_value_41964 = false;
        String __DSPOT_key_41963 = "lK}m],Yjf<4viJT;V7R1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223463__8 = a.put(__DSPOT_key_41963, __DSPOT_value_41964);
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
        o_testIteratorEmpty_mg223463__8.remove(__DSPOT_key_42365);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223463_mg224863() throws Exception {
        String __DSPOT_key_42339 = "sos6dZAq[l%ZI(%R-[G#";
        boolean __DSPOT_value_41964 = false;
        String __DSPOT_key_41963 = "lK}m],Yjf<4viJT;V7R1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223463__8 = a.put(__DSPOT_key_41963, __DSPOT_value_41964);
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
        boolean o_testIteratorEmpty_mg223463_mg224863__12 = o_testIteratorEmpty_mg223463__8.hasKeyIgnoreCase(__DSPOT_key_42339);
        Assert.assertFalse(o_testIteratorEmpty_mg223463_mg224863__12);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465_mg224292() throws Exception {
        Attribute __DSPOT_o_42190 = new Attribute("Z|l_JDTU9A3=3*GqLsic", "Qq(+}wrOAvG8i!}Nb1U*");
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        boolean o_testIteratorEmpty_mg223465_mg224292__13 = __DSPOT_attribute_41967.equals(__DSPOT_o_42190);
        Assert.assertFalse(o_testIteratorEmpty_mg223465_mg224292__13);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465_mg224470() throws Exception {
        String __DSPOT_key_42264 = "])A87BzR50@a<jhb#6*U";
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        __DSPOT_attribute_41967.setKey(__DSPOT_key_42264);
        Assert.assertEquals(" ])A87BzR50@a<jhb#6*U=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-281481967, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" ])A87BzR50@a<jhb#6*U=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(-281481967, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224546() throws Exception {
        boolean __DSPOT_value_42275 = true;
        String __DSPOT_key_42274 = "VoACV5heg(vXfN;{3<,z";
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg223464_mg224546__13 = a.put(__DSPOT_key_42274, __DSPOT_value_42275);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" VoACV5heg(vXfN;{3<,z", ((Attributes) (o_testIteratorEmpty_mg223464_mg224546__13)).toString());
        Assert.assertEquals(-1730098958, ((int) (((Attributes) (o_testIteratorEmpty_mg223464_mg224546__13)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" VoACV5heg(vXfN;{3<,z", ((Attributes) (a)).toString());
        Assert.assertEquals(-1730098958, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" VoACV5heg(vXfN;{3<,z", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(-1730098958, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465_mg224496() throws Exception {
        String __DSPOT_val_42266 = "t94(023(DCwUSir$&L=I";
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        String o_testIteratorEmpty_mg223465_mg224496__12 = __DSPOT_attribute_41967.setValue(__DSPOT_val_42266);
        Assert.assertEquals("]LP+KypasBb7Mqw+Q|W?", o_testIteratorEmpty_mg223465_mg224496__12);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"t94(023(DCwUSir$&amp;L=I\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-262799833, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"t94(023(DCwUSir$&amp;L=I\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(-262799833, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465litString223619_failAssert138() throws Exception {
        try {
            Attribute __DSPOT_attribute_41967 = new Attribute("", "]LP+KypasBb7Mqw+Q|W?");
            Attributes a = new Attributes();
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
            org.junit.Assert.fail("testIteratorEmpty_mg223465litString223619 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224944() throws Exception {
        String __DSPOT_value_42367 = "[WM5vQ`EiO!V(XUD`z>d";
        String __DSPOT_key_42366 = "Ou1921_unU:]_jdtJt#^";
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg223464_mg224944__13 = o_testIteratorEmpty_mg223464__8.put(__DSPOT_key_42366, __DSPOT_value_42367);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" Ou1921_unU:]_jdtJt#^=\"[WM5vQ`EiO!V(XUD`z>d\"", ((Attributes) (o_testIteratorEmpty_mg223464_mg224944__13)).toString());
        Assert.assertEquals(548713308, ((int) (((Attributes) (o_testIteratorEmpty_mg223464_mg224944__13)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" Ou1921_unU:]_jdtJt#^=\"[WM5vQ`EiO!V(XUD`z>d\"", ((Attributes) (a)).toString());
        Assert.assertEquals(548713308, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" Ou1921_unU:]_jdtJt#^=\"[WM5vQ`EiO!V(XUD`z>d\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(548713308, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465_mg224871() throws Exception {
        String __DSPOT_value_42343 = "r0[H_T7.(HG<z%MmcgFv";
        String __DSPOT_key_42342 = "6pyDCpQU1#L^UZs)b#O4";
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg223465_mg224871__13 = a.put(__DSPOT_key_42342, __DSPOT_value_42343);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\" 6pyDCpQU1#L^UZs)b#O4=\"r0[H_T7.(HG<z%MmcgFv\"", ((Attributes) (o_testIteratorEmpty_mg223465_mg224871__13)).toString());
        Assert.assertEquals(1800597127, ((int) (((Attributes) (o_testIteratorEmpty_mg223465_mg224871__13)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\" 6pyDCpQU1#L^UZs)b#O4=\"r0[H_T7.(HG<z%MmcgFv\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1800597127, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\" 6pyDCpQU1#L^UZs)b#O4=\"r0[H_T7.(HG<z%MmcgFv\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1800597127, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224151() throws Exception {
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        List<Attribute> o_testIteratorEmpty_mg223464_mg224151__11 = a.asList();
        Assert.assertFalse(o_testIteratorEmpty_mg223464_mg224151__11.isEmpty());
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (a)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465_add224153() throws Exception {
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465_add224153__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465_add224153__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465_add224153__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465_add224153__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465_add224153__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464null225052() throws Exception {
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, null);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(1539231599, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ", ((Attributes) (a)).toString());
        Assert.assertEquals(1539231599, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223463_mg224811() throws Exception {
        Attributes __DSPOT_o_42316 = new Attributes();
        boolean __DSPOT_value_41964 = false;
        String __DSPOT_key_41963 = "lK}m],Yjf<4viJT;V7R1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223463__8 = a.put(__DSPOT_key_41963, __DSPOT_value_41964);
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
        boolean o_testIteratorEmpty_mg223463_mg224811__13 = o_testIteratorEmpty_mg223463__8.equals(__DSPOT_o_42316);
        Assert.assertTrue(o_testIteratorEmpty_mg223463_mg224811__13);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223463_mg224851() throws Exception {
        String __DSPOT_key_42334 = "P$b_!{tE3_e$R%##Yi:?";
        boolean __DSPOT_value_41964 = false;
        String __DSPOT_key_41963 = "lK}m],Yjf<4viJT;V7R1";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223463__8 = a.put(__DSPOT_key_41963, __DSPOT_value_41964);
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
        String o_testIteratorEmpty_mg223463_mg224851__12 = o_testIteratorEmpty_mg223463__8.getIgnoreCase(__DSPOT_key_42334);
        Assert.assertEquals("", o_testIteratorEmpty_mg223463_mg224851__12);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals("", ((Attributes) (o_testIteratorEmpty_mg223463__8)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (o_testIteratorEmpty_mg223463__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224839() throws Exception {
        Attributes __DSPOT_o_42330 = new Attributes();
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        boolean o_testIteratorEmpty_mg223464_mg224839__13 = o_testIteratorEmpty_mg223464__8.equals(__DSPOT_o_42330);
        Assert.assertFalse(o_testIteratorEmpty_mg223464_mg224839__13);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (a)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmptynull223471_failAssert135null224030() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Iterator<Attribute> iterator = null;
            iterator.hasNext();
            org.junit.Assert.fail("testIteratorEmptynull223471 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465litString223646() throws Exception {
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1991071438, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1991071438, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465litString223624() throws Exception {
        Attribute __DSPOT_attribute_41967 = new Attribute("\n", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" =\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(-919841399, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        Assert.assertEquals(" =\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-919841399, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224579() throws Exception {
        String __DSPOT_value_42283 = "1tfWw2Yrc2?eqv;LWW,X";
        String __DSPOT_key_42282 = "T<aU6}YBU[j$sk]PxS%T";
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg223464_mg224579__13 = a.put(__DSPOT_key_42282, __DSPOT_value_42283);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" T<aU6}YBU[j$sk]PxS%T=\"1tfWw2Yrc2?eqv;LWW,X\"", ((Attributes) (o_testIteratorEmpty_mg223464_mg224579__13)).toString());
        Assert.assertEquals(441419657, ((int) (((Attributes) (o_testIteratorEmpty_mg223464_mg224579__13)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" T<aU6}YBU[j$sk]PxS%T=\"1tfWw2Yrc2?eqv;LWW,X\"", ((Attributes) (a)).toString());
        Assert.assertEquals(441419657, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" T<aU6}YBU[j$sk]PxS%T=\"1tfWw2Yrc2?eqv;LWW,X\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(441419657, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224832() throws Exception {
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Map<String, String> o_testIteratorEmpty_mg223464_mg224832__11 = o_testIteratorEmpty_mg223464__8.dataset();
        Assert.assertTrue(o_testIteratorEmpty_mg223464_mg224832__11.isEmpty());
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (a)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464litString223581() throws Exception {
        String __DSPOT_value_41966 = "";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(1539231599, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1539231599, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224515() throws Exception {
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        a.normalize();
        Assert.assertEquals(" *z2hiu[12uvduvznr.vz=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (a)).toString());
        Assert.assertEquals(2013099210, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2hiu[12uvduvznr.vz=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(2013099210, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464litString223587() throws Exception {
        String __DSPOT_value_41966 = "\n";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"\n\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(1539529509, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"\n\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1539529509, ((int) (((Attributes) (a)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465_mg224269() throws Exception {
        Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
        Attribute o_testIteratorEmpty_mg223465_mg224269__11 = __DSPOT_attribute_41967.clone();
        Assert.assertEquals("?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attribute) (o_testIteratorEmpty_mg223465_mg224269__11)).toString());
        Assert.assertEquals(-1254388853, ((int) (((Attribute) (o_testIteratorEmpty_mg223465_mg224269__11)).hashCode())));
        Assert.assertEquals("]LP+KypasBb7Mqw+Q|W?", ((Attribute) (o_testIteratorEmpty_mg223465_mg224269__11)).getValue());
        Assert.assertEquals("?BvD}eg2FZp/0HDhA_Q-", ((Attribute) (o_testIteratorEmpty_mg223465_mg224269__11)).getKey());
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" ?BvD}eg2FZp/0HDhA_Q-=\"]LP+KypasBb7Mqw+Q|W?\"", ((Attributes) (o_testIteratorEmpty_mg223465__8)).toString());
        Assert.assertEquals(1041676406, ((int) (((Attributes) (o_testIteratorEmpty_mg223465__8)).hashCode())));
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464null225051_failAssert139null236240() throws Exception {
        try {
            String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
            String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            Attributes o_testIteratorEmpty_mg223464__8 = a.put(null, __DSPOT_value_41966);
            org.junit.Assert.fail("testIteratorEmpty_mg223464null225051 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465litString223619_failAssert138null234468() throws Exception {
        try {
            Attribute __DSPOT_attribute_41967 = null;
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Iterator<Attribute> iterator = a.iterator();
            iterator.hasNext();
            Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
            org.junit.Assert.fail("testIteratorEmpty_mg223465litString223619 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465null225088_failAssert136litString228725_failAssert140() throws Exception {
        try {
            try {
                Attribute __DSPOT_attribute_41967 = new Attribute("", "]LP+KypasBb7Mqw+Q|W?");
                Attributes a = new Attributes();
                Iterator<Attribute> iterator = null;
                iterator.hasNext();
                Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
                org.junit.Assert.fail("testIteratorEmpty_mg223465null225088 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testIteratorEmpty_mg223465null225088_failAssert136litString228725 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("String must not be empty", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223465null225088_failAssert136_mg228922() throws Exception {
        try {
            Attribute __DSPOT_attribute_41967 = new Attribute("?BvD}eg2FZp/0HDhA_Q-", "]LP+KypasBb7Mqw+Q|W?");
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Iterator<Attribute> iterator = null;
            iterator.hasNext();
            Attributes o_testIteratorEmpty_mg223465__8 = a.put(__DSPOT_attribute_41967);
            org.junit.Assert.fail("testIteratorEmpty_mg223465null225088 should have thrown NullPointerException");
            __DSPOT_attribute_41967.clone();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIteratorEmpty_mg223464_mg224949_mg233196() throws Exception {
        Attribute __DSPOT_attribute_44297 = new Attribute("PNU64 q:S{73gect3|*6", "(h#f.wbt;d:ZMWb*y+Ic", new Attributes());
        Attribute __DSPOT_attribute_42370 = new Attribute("lCZA: #<Bd^(aBbKl[+.", "hefO0KVv-g;v@qV4-tf{", new Attributes());
        String __DSPOT_value_41966 = "TbfbF(E#XTqCl8|g#2*Q";
        String __DSPOT_key_41965 = "*z2HIu[12uvDUvznr.vZ";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Iterator<Attribute> iterator = a.iterator();
        iterator.hasNext();
        Attributes o_testIteratorEmpty_mg223464__8 = a.put(__DSPOT_key_41965, __DSPOT_value_41966);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(676039850, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Attributes o_testIteratorEmpty_mg223464_mg224949__14 = o_testIteratorEmpty_mg223464__8.put(__DSPOT_attribute_42370);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" lCZA: #<Bd^(aBbKl[+.=\"hefO0KVv-g;v@qV4-tf{\"", ((Attributes) (o_testIteratorEmpty_mg223464_mg224949__14)).toString());
        Assert.assertEquals(1231774823, ((int) (((Attributes) (o_testIteratorEmpty_mg223464_mg224949__14)).hashCode())));
        Attributes o_testIteratorEmpty_mg223464_mg224949_mg233196__20 = o_testIteratorEmpty_mg223464__8.put(__DSPOT_attribute_44297);
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" lCZA: #<Bd^(aBbKl[+.=\"hefO0KVv-g;v@qV4-tf{\" PNU64 q:S{73gect3|*6=\"(h#f.wbt;d:ZMWb*y+Ic\"", ((Attributes) (o_testIteratorEmpty_mg223464_mg224949_mg233196__20)).toString());
        Assert.assertEquals(2041613599, ((int) (((Attributes) (o_testIteratorEmpty_mg223464_mg224949_mg233196__20)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" lCZA: #<Bd^(aBbKl[+.=\"hefO0KVv-g;v@qV4-tf{\" PNU64 q:S{73gect3|*6=\"(h#f.wbt;d:ZMWb*y+Ic\"", ((Attributes) (a)).toString());
        Assert.assertEquals(2041613599, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" lCZA: #<Bd^(aBbKl[+.=\"hefO0KVv-g;v@qV4-tf{\" PNU64 q:S{73gect3|*6=\"(h#f.wbt;d:ZMWb*y+Ic\"", ((Attributes) (o_testIteratorEmpty_mg223464__8)).toString());
        Assert.assertEquals(2041613599, ((int) (((Attributes) (o_testIteratorEmpty_mg223464__8)).hashCode())));
        Assert.assertEquals(" *z2HIu[12uvDUvznr.vZ=\"TbfbF(E#XTqCl8|g#2*Q\" lCZA: #<Bd^(aBbKl[+.=\"hefO0KVv-g;v@qV4-tf{\" PNU64 q:S{73gect3|*6=\"(h#f.wbt;d:ZMWb*y+Ic\"", ((Attributes) (o_testIteratorEmpty_mg223464_mg224949__14)).toString());
        Assert.assertEquals(2041613599, ((int) (((Attributes) (o_testIteratorEmpty_mg223464_mg224949__14)).hashCode())));
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
    public void removeCaseSensitive_rv72949() throws Exception {
        Attributes __DSPOT_incoming_10241 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72949__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72949__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72949__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72949__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72949__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72949__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__7)).hashCode())));
        Attributes o_removeCaseSensitive_rv72949__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72949__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__8)).hashCode())));
        Attributes __DSPOT_invoc_7 = a.put("data-name", "Jsoup");
        int o_removeCaseSensitive_rv72949__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72949__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72949__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72949__15)));
        boolean o_removeCaseSensitive_rv72949__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72949__16);
        boolean o_removeCaseSensitive_rv72949__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72949__17);
        __DSPOT_invoc_7.addAll(__DSPOT_incoming_10241);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72949__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72949__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72949__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72949__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72949__8)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72949__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72949__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv72949__16);
        Assert.assertFalse(o_removeCaseSensitive_rv72949__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72957() throws Exception {
        String __DSPOT_key_10246 = "V&eQQwe]L.j&8U1]bj}M";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72957__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72957__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__4)).hashCode())));
        Attributes o_removeCaseSensitive_rv72957__5 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72957__5)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72957__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72957__6)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72957__7 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72957__7)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__7)).hashCode())));
        Attributes __DSPOT_invoc_7 = a.put("data-name", "Jsoup");
        int o_removeCaseSensitive_rv72957__11 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72957__11)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72957__14 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72957__14)));
        boolean o_removeCaseSensitive_rv72957__15 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72957__15);
        boolean o_removeCaseSensitive_rv72957__16 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72957__16);
        boolean o_removeCaseSensitive_rv72957__17 = __DSPOT_invoc_7.hasKeyIgnoreCase(__DSPOT_key_10246);
        Assert.assertFalse(o_removeCaseSensitive_rv72957__17);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72957__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72957__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72957__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72957__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72957__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72957__11)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72957__14)));
        Assert.assertTrue(o_removeCaseSensitive_rv72957__15);
        Assert.assertFalse(o_removeCaseSensitive_rv72957__16);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivelitString72746() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitivelitString72746__3 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivelitString72746__3)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__3)).hashCode())));
        Attributes o_removeCaseSensitivelitString72746__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivelitString72746__4)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__4)).hashCode())));
        Attributes o_removeCaseSensitivelitString72746__5 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72746__5)).toString());
        Assert.assertEquals(1623501452, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__5)).hashCode())));
        Attributes o_removeCaseSensitivelitString72746__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72746__6)).toString());
        Assert.assertEquals(1623501452, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__6)).hashCode())));
        Attributes o_removeCaseSensitivelitString72746__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72746__7)).toString());
        Assert.assertEquals(1878418048, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__7)).hashCode())));
        int o_removeCaseSensitivelitString72746__8 = a.size();
        Assert.assertEquals(4, ((int) (o_removeCaseSensitivelitString72746__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitivelitString72746__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString72746__11)));
        boolean o_removeCaseSensitivelitString72746__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitivelitString72746__12);
        boolean o_removeCaseSensitivelitString72746__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitivelitString72746__13);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1007979893, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72746__3)).toString());
        Assert.assertEquals(1007979893, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72746__4)).toString());
        Assert.assertEquals(1007979893, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72746__5)).toString());
        Assert.assertEquals(1007979893, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72746__6)).toString());
        Assert.assertEquals(1007979893, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72746__7)).toString());
        Assert.assertEquals(1007979893, ((int) (((Attributes) (o_removeCaseSensitivelitString72746__7)).hashCode())));
        Assert.assertEquals(4, ((int) (o_removeCaseSensitivelitString72746__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString72746__11)));
        Assert.assertTrue(o_removeCaseSensitivelitString72746__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivelitString72716() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitivelitString72716__3 = a.put("TOot", "a&p");
        Assert.assertEquals(" TOot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivelitString72716__3)).toString());
        Assert.assertEquals(-699464586, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__3)).hashCode())));
        Attributes o_removeCaseSensitivelitString72716__4 = a.put("tot", "one");
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivelitString72716__4)).toString());
        Assert.assertEquals(-1461597916, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__4)).hashCode())));
        Attributes o_removeCaseSensitivelitString72716__5 = a.put("Hello", "There");
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72716__5)).toString());
        Assert.assertEquals(-782183597, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__5)).hashCode())));
        Attributes o_removeCaseSensitivelitString72716__6 = a.put("hello", "There");
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72716__6)).toString());
        Assert.assertEquals(-1922343226, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__6)).hashCode())));
        Attributes o_removeCaseSensitivelitString72716__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72716__7)).toString());
        Assert.assertEquals(1827843188, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__7)).hashCode())));
        int o_removeCaseSensitivelitString72716__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString72716__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitivelitString72716__11 = a.size();
        Assert.assertEquals(4, ((int) (o_removeCaseSensitivelitString72716__11)));
        boolean o_removeCaseSensitivelitString72716__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitivelitString72716__12);
        boolean o_removeCaseSensitivelitString72716__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitivelitString72716__13);
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1153758745, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72716__3)).toString());
        Assert.assertEquals(-1153758745, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__3)).hashCode())));
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72716__4)).toString());
        Assert.assertEquals(-1153758745, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__4)).hashCode())));
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72716__5)).toString());
        Assert.assertEquals(-1153758745, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__5)).hashCode())));
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72716__6)).toString());
        Assert.assertEquals(-1153758745, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__6)).hashCode())));
        Assert.assertEquals(" TOot=\"a&amp;p\" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72716__7)).toString());
        Assert.assertEquals(-1153758745, ((int) (((Attributes) (o_removeCaseSensitivelitString72716__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString72716__8)));
        Assert.assertEquals(4, ((int) (o_removeCaseSensitivelitString72716__11)));
        Assert.assertTrue(o_removeCaseSensitivelitString72716__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivelitString72727() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitivelitString72727__3 = a.put("Tot", "");
        Assert.assertEquals(" Tot=\"\"", ((Attributes) (o_removeCaseSensitivelitString72727__3)).toString());
        Assert.assertEquals(555415706, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__3)).hashCode())));
        Attributes o_removeCaseSensitivelitString72727__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivelitString72727__4)).toString());
        Assert.assertEquals(-206717624, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__4)).hashCode())));
        Attributes o_removeCaseSensitivelitString72727__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72727__5)).toString());
        Assert.assertEquals(472696695, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__5)).hashCode())));
        Attributes o_removeCaseSensitivelitString72727__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72727__6)).toString());
        Assert.assertEquals(-667462934, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__6)).hashCode())));
        Attributes o_removeCaseSensitivelitString72727__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72727__7)).toString());
        Assert.assertEquals(-895488360, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__7)).hashCode())));
        int o_removeCaseSensitivelitString72727__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString72727__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitivelitString72727__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString72727__11)));
        boolean o_removeCaseSensitivelitString72727__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitivelitString72727__12);
        boolean o_removeCaseSensitivelitString72727__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitivelitString72727__13);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72727__3)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72727__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72727__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72727__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72727__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72727__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString72727__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString72727__11)));
        Assert.assertTrue(o_removeCaseSensitivelitString72727__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivelitString72728() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitivelitString72728__3 = a.put("Tot", "\n");
        Assert.assertEquals(" Tot=\"\n\"", ((Attributes) (o_removeCaseSensitivelitString72728__3)).toString());
        Assert.assertEquals(555713616, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__3)).hashCode())));
        Attributes o_removeCaseSensitivelitString72728__4 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"\n\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivelitString72728__4)).toString());
        Assert.assertEquals(-206419714, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__4)).hashCode())));
        Attributes o_removeCaseSensitivelitString72728__5 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"\n\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72728__5)).toString());
        Assert.assertEquals(472994605, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__5)).hashCode())));
        Attributes o_removeCaseSensitivelitString72728__6 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"\n\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivelitString72728__6)).toString());
        Assert.assertEquals(-667165024, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__6)).hashCode())));
        Attributes o_removeCaseSensitivelitString72728__7 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"\n\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72728__7)).toString());
        Assert.assertEquals(-647254194, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__7)).hashCode())));
        int o_removeCaseSensitivelitString72728__8 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString72728__8)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitivelitString72728__11 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString72728__11)));
        boolean o_removeCaseSensitivelitString72728__12 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitivelitString72728__12);
        boolean o_removeCaseSensitivelitString72728__13 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitivelitString72728__13);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72728__3)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__3)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72728__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72728__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72728__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivelitString72728__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitivelitString72728__7)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitivelitString72728__8)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitivelitString72728__11)));
        Assert.assertTrue(o_removeCaseSensitivelitString72728__12);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72882() throws Exception {
        String __DSPOT_key_10201 = "D,*Sz#66DW%fdeY/sI/y";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("Tot", "a&p");
        Attributes o_removeCaseSensitive_rv72882__7 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72882__7)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__7)).hashCode())));
        Attributes o_removeCaseSensitive_rv72882__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72882__8)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__8)).hashCode())));
        Attributes o_removeCaseSensitive_rv72882__9 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72882__9)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__9)).hashCode())));
        Attributes o_removeCaseSensitive_rv72882__10 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72882__10)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__10)).hashCode())));
        int o_removeCaseSensitive_rv72882__11 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72882__11)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72882__14 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72882__14)));
        boolean o_removeCaseSensitive_rv72882__15 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72882__15);
        boolean o_removeCaseSensitive_rv72882__16 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72882__16);
        __DSPOT_invoc_3.removeIgnoreCase(__DSPOT_key_10201);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72882__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72882__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72882__9)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__9)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72882__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72882__10)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72882__11)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72882__14)));
        Assert.assertTrue(o_removeCaseSensitive_rv72882__15);
        Assert.assertFalse(o_removeCaseSensitive_rv72882__16);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivenull72971_failAssert27() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put(null, "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.remove("Tot");
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitivenull72971 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72941() throws Exception {
        boolean __DSPOT_value_10235 = false;
        String __DSPOT_key_10234 = "hHf(.UO.-0reyG2)mQP}";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72941__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72941__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72941__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72941__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72941__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72941__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__7)).hashCode())));
        Attributes __DSPOT_invoc_6 = a.put("hello", "There");
        Attributes o_removeCaseSensitive_rv72941__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72941__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__11)).hashCode())));
        int o_removeCaseSensitive_rv72941__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72941__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72941__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72941__15)));
        boolean o_removeCaseSensitive_rv72941__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72941__16);
        boolean o_removeCaseSensitive_rv72941__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72941__17);
        Attributes o_removeCaseSensitive_rv72941__18 = __DSPOT_invoc_6.put(__DSPOT_key_10234, __DSPOT_value_10235);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72941__18)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__18)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72941__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72941__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72941__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72941__11)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72941__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72941__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72941__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv72941__16);
        Assert.assertFalse(o_removeCaseSensitive_rv72941__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72962() throws Exception {
        boolean __DSPOT_value_10248 = true;
        String __DSPOT_key_10247 = "*9&ae4S=+<l`C^[bX$Jf";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72962__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72962__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72962__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72962__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72962__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72962__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__7)).hashCode())));
        Attributes o_removeCaseSensitive_rv72962__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72962__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__8)).hashCode())));
        Attributes __DSPOT_invoc_7 = a.put("data-name", "Jsoup");
        int o_removeCaseSensitive_rv72962__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72962__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72962__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72962__15)));
        boolean o_removeCaseSensitive_rv72962__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72962__16);
        boolean o_removeCaseSensitive_rv72962__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72962__17);
        Attributes o_removeCaseSensitive_rv72962__18 = __DSPOT_invoc_7.put(__DSPOT_key_10247, __DSPOT_value_10248);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" *9&ae4S=+<l`C^[bX$Jf", ((Attributes) (o_removeCaseSensitive_rv72962__18)).toString());
        Assert.assertEquals(-763760508, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__18)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" *9&ae4S=+<l`C^[bX$Jf", ((Attributes) (a)).toString());
        Assert.assertEquals(-763760508, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" *9&ae4S=+<l`C^[bX$Jf", ((Attributes) (o_removeCaseSensitive_rv72962__5)).toString());
        Assert.assertEquals(-763760508, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" *9&ae4S=+<l`C^[bX$Jf", ((Attributes) (o_removeCaseSensitive_rv72962__6)).toString());
        Assert.assertEquals(-763760508, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" *9&ae4S=+<l`C^[bX$Jf", ((Attributes) (o_removeCaseSensitive_rv72962__7)).toString());
        Assert.assertEquals(-763760508, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" *9&ae4S=+<l`C^[bX$Jf", ((Attributes) (o_removeCaseSensitive_rv72962__8)).toString());
        Assert.assertEquals(-763760508, ((int) (((Attributes) (o_removeCaseSensitive_rv72962__8)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72962__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72962__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv72962__16);
        Assert.assertFalse(o_removeCaseSensitive_rv72962__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72934() throws Exception {
        String __DSPOT_key_10231 = "Ou^z:1X8&`T+_*D]5*fb";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72934__4 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72934__4)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__4)).hashCode())));
        Attributes o_removeCaseSensitive_rv72934__5 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72934__5)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72934__6 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72934__6)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__6)).hashCode())));
        Attributes __DSPOT_invoc_6 = a.put("hello", "There");
        Attributes o_removeCaseSensitive_rv72934__10 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72934__10)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__10)).hashCode())));
        int o_removeCaseSensitive_rv72934__11 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72934__11)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72934__14 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72934__14)));
        boolean o_removeCaseSensitive_rv72934__15 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72934__15);
        boolean o_removeCaseSensitive_rv72934__16 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72934__16);
        String o_removeCaseSensitive_rv72934__17 = __DSPOT_invoc_6.getIgnoreCase(__DSPOT_key_10231);
        Assert.assertEquals("", o_removeCaseSensitive_rv72934__17);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72934__4)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__4)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72934__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72934__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72934__10)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72934__10)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72934__11)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72934__14)));
        Assert.assertTrue(o_removeCaseSensitive_rv72934__15);
        Assert.assertFalse(o_removeCaseSensitive_rv72934__16);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72901() throws Exception {
        Attribute __DSPOT_attribute_10212 = new Attribute("jq8D4U9Fzyr*]Oc(Mi*&", "oUH{aRA/24$),9E@g:lj", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72901__6 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72901__6)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__6)).hashCode())));
        Attributes __DSPOT_invoc_4 = a.put("tot", "one");
        Attributes o_removeCaseSensitive_rv72901__10 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72901__10)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__10)).hashCode())));
        Attributes o_removeCaseSensitive_rv72901__11 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72901__11)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__11)).hashCode())));
        Attributes o_removeCaseSensitive_rv72901__12 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72901__12)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__12)).hashCode())));
        int o_removeCaseSensitive_rv72901__13 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72901__13)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72901__16 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72901__16)));
        boolean o_removeCaseSensitive_rv72901__17 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72901__17);
        boolean o_removeCaseSensitive_rv72901__18 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72901__18);
        Attributes o_removeCaseSensitive_rv72901__19 = __DSPOT_invoc_4.put(__DSPOT_attribute_10212);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" jq8D4U9Fzyr*]Oc(Mi*&=\"oUH{aRA/24$),9E@g:lj\"", ((Attributes) (o_removeCaseSensitive_rv72901__19)).toString());
        Assert.assertEquals(-1942937145, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__19)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" jq8D4U9Fzyr*]Oc(Mi*&=\"oUH{aRA/24$),9E@g:lj\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1942937145, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" jq8D4U9Fzyr*]Oc(Mi*&=\"oUH{aRA/24$),9E@g:lj\"", ((Attributes) (o_removeCaseSensitive_rv72901__6)).toString());
        Assert.assertEquals(-1942937145, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" jq8D4U9Fzyr*]Oc(Mi*&=\"oUH{aRA/24$),9E@g:lj\"", ((Attributes) (o_removeCaseSensitive_rv72901__10)).toString());
        Assert.assertEquals(-1942937145, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__10)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" jq8D4U9Fzyr*]Oc(Mi*&=\"oUH{aRA/24$),9E@g:lj\"", ((Attributes) (o_removeCaseSensitive_rv72901__11)).toString());
        Assert.assertEquals(-1942937145, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__11)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" jq8D4U9Fzyr*]Oc(Mi*&=\"oUH{aRA/24$),9E@g:lj\"", ((Attributes) (o_removeCaseSensitive_rv72901__12)).toString());
        Assert.assertEquals(-1942937145, ((int) (((Attributes) (o_removeCaseSensitive_rv72901__12)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72901__13)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72901__16)));
        Assert.assertTrue(o_removeCaseSensitive_rv72901__17);
        Assert.assertFalse(o_removeCaseSensitive_rv72901__18);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72932() throws Exception {
        Object __DSPOT_o_10229 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72932__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72932__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72932__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72932__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72932__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72932__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__7)).hashCode())));
        Attributes __DSPOT_invoc_6 = a.put("hello", "There");
        Attributes o_removeCaseSensitive_rv72932__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72932__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__11)).hashCode())));
        int o_removeCaseSensitive_rv72932__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72932__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72932__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72932__15)));
        boolean o_removeCaseSensitive_rv72932__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72932__16);
        boolean o_removeCaseSensitive_rv72932__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72932__17);
        boolean o_removeCaseSensitive_rv72932__18 = __DSPOT_invoc_6.equals(__DSPOT_o_10229);
        Assert.assertFalse(o_removeCaseSensitive_rv72932__18);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72932__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72932__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72932__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72932__11)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72932__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72932__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72932__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv72932__16);
        Assert.assertFalse(o_removeCaseSensitive_rv72932__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72943() throws Exception {
        Attribute __DSPOT_attribute_10238 = new Attribute("6d8FQ+9qw*43f}k4?3&q", "%[E6NVS*>#5mE(.X##)^", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72943__6 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72943__6)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72943__7 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72943__7)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__7)).hashCode())));
        Attributes o_removeCaseSensitive_rv72943__8 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72943__8)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__8)).hashCode())));
        Attributes __DSPOT_invoc_6 = a.put("hello", "There");
        Attributes o_removeCaseSensitive_rv72943__12 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72943__12)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__12)).hashCode())));
        int o_removeCaseSensitive_rv72943__13 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72943__13)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72943__16 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72943__16)));
        boolean o_removeCaseSensitive_rv72943__17 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72943__17);
        boolean o_removeCaseSensitive_rv72943__18 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72943__18);
        Attributes o_removeCaseSensitive_rv72943__19 = __DSPOT_invoc_6.put(__DSPOT_attribute_10238);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 6d8FQ+9qw*43f}k4?3&q=\"%[E6NVS*>#5mE(.X##)^\"", ((Attributes) (o_removeCaseSensitive_rv72943__19)).toString());
        Assert.assertEquals(-1235845412, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__19)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 6d8FQ+9qw*43f}k4?3&q=\"%[E6NVS*>#5mE(.X##)^\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1235845412, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 6d8FQ+9qw*43f}k4?3&q=\"%[E6NVS*>#5mE(.X##)^\"", ((Attributes) (o_removeCaseSensitive_rv72943__6)).toString());
        Assert.assertEquals(-1235845412, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 6d8FQ+9qw*43f}k4?3&q=\"%[E6NVS*>#5mE(.X##)^\"", ((Attributes) (o_removeCaseSensitive_rv72943__7)).toString());
        Assert.assertEquals(-1235845412, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 6d8FQ+9qw*43f}k4?3&q=\"%[E6NVS*>#5mE(.X##)^\"", ((Attributes) (o_removeCaseSensitive_rv72943__8)).toString());
        Assert.assertEquals(-1235845412, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__8)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" 6d8FQ+9qw*43f}k4?3&q=\"%[E6NVS*>#5mE(.X##)^\"", ((Attributes) (o_removeCaseSensitive_rv72943__12)).toString());
        Assert.assertEquals(-1235845412, ((int) (((Attributes) (o_removeCaseSensitive_rv72943__12)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72943__13)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72943__16)));
        Assert.assertTrue(o_removeCaseSensitive_rv72943__17);
        Assert.assertFalse(o_removeCaseSensitive_rv72943__18);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72953() throws Exception {
        Attributes __DSPOT_o_10242 = new Attributes();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72953__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72953__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72953__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72953__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72953__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72953__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__7)).hashCode())));
        Attributes o_removeCaseSensitive_rv72953__8 = a.put("hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72953__8)).toString());
        Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__8)).hashCode())));
        Attributes __DSPOT_invoc_7 = a.put("data-name", "Jsoup");
        int o_removeCaseSensitive_rv72953__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72953__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72953__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72953__15)));
        boolean o_removeCaseSensitive_rv72953__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72953__16);
        boolean o_removeCaseSensitive_rv72953__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72953__17);
        boolean o_removeCaseSensitive_rv72953__18 = __DSPOT_invoc_7.equals(__DSPOT_o_10242);
        Assert.assertFalse(o_removeCaseSensitive_rv72953__18);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72953__5)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72953__6)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72953__7)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72953__8)).toString());
        Assert.assertEquals(1019487349, ((int) (((Attributes) (o_removeCaseSensitive_rv72953__8)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72953__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72953__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv72953__16);
        Assert.assertFalse(o_removeCaseSensitive_rv72953__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitive_rv72942() throws Exception {
        String __DSPOT_value_10237 = "2<G8TSijT0B%+n|8$V[8";
        String __DSPOT_key_10236 = ";_8EElfkUhH:5Ad1h/Gx";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_removeCaseSensitive_rv72942__5 = a.put("Tot", "a&p");
        Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitive_rv72942__5)).toString());
        Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__5)).hashCode())));
        Attributes o_removeCaseSensitive_rv72942__6 = a.put("tot", "one");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitive_rv72942__6)).toString());
        Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__6)).hashCode())));
        Attributes o_removeCaseSensitive_rv72942__7 = a.put("Hello", "There");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitive_rv72942__7)).toString());
        Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__7)).hashCode())));
        Attributes __DSPOT_invoc_6 = a.put("hello", "There");
        Attributes o_removeCaseSensitive_rv72942__11 = a.put("data-name", "Jsoup");
        Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitive_rv72942__11)).toString());
        Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__11)).hashCode())));
        int o_removeCaseSensitive_rv72942__12 = a.size();
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72942__12)));
        a.remove("Tot");
        a.remove("Hello");
        int o_removeCaseSensitive_rv72942__15 = a.size();
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72942__15)));
        boolean o_removeCaseSensitive_rv72942__16 = a.hasKey("tot");
        Assert.assertTrue(o_removeCaseSensitive_rv72942__16);
        boolean o_removeCaseSensitive_rv72942__17 = a.hasKey("Tot");
        Assert.assertFalse(o_removeCaseSensitive_rv72942__17);
        Attributes o_removeCaseSensitive_rv72942__18 = __DSPOT_invoc_6.put(__DSPOT_key_10236, __DSPOT_value_10237);
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ;_8EElfkUhH:5Ad1h/Gx=\"2<G8TSijT0B%+n|8$V[8\"", ((Attributes) (o_removeCaseSensitive_rv72942__18)).toString());
        Assert.assertEquals(-857604021, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__18)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ;_8EElfkUhH:5Ad1h/Gx=\"2<G8TSijT0B%+n|8$V[8\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-857604021, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ;_8EElfkUhH:5Ad1h/Gx=\"2<G8TSijT0B%+n|8$V[8\"", ((Attributes) (o_removeCaseSensitive_rv72942__5)).toString());
        Assert.assertEquals(-857604021, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__5)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ;_8EElfkUhH:5Ad1h/Gx=\"2<G8TSijT0B%+n|8$V[8\"", ((Attributes) (o_removeCaseSensitive_rv72942__6)).toString());
        Assert.assertEquals(-857604021, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__6)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ;_8EElfkUhH:5Ad1h/Gx=\"2<G8TSijT0B%+n|8$V[8\"", ((Attributes) (o_removeCaseSensitive_rv72942__7)).toString());
        Assert.assertEquals(-857604021, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__7)).hashCode())));
        Assert.assertEquals(" tot=\"one\" hello=\"There\" data-name=\"Jsoup\" ;_8EElfkUhH:5Ad1h/Gx=\"2<G8TSijT0B%+n|8$V[8\"", ((Attributes) (o_removeCaseSensitive_rv72942__11)).toString());
        Assert.assertEquals(-857604021, ((int) (((Attributes) (o_removeCaseSensitive_rv72942__11)).hashCode())));
        Assert.assertEquals(5, ((int) (o_removeCaseSensitive_rv72942__12)));
        Assert.assertEquals(3, ((int) (o_removeCaseSensitive_rv72942__15)));
        Assert.assertTrue(o_removeCaseSensitive_rv72942__16);
        Assert.assertFalse(o_removeCaseSensitive_rv72942__17);
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivenull72983_failAssert20null77045() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_removeCaseSensitivenull72983_failAssert20null77045__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__5)).hashCode())));
            Attributes o_removeCaseSensitivenull72983_failAssert20null77045__6 = a.put("tot", "one");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__6)).toString());
            Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__6)).hashCode())));
            Attributes o_removeCaseSensitivenull72983_failAssert20null77045__7 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__7)).toString());
            Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__7)).hashCode())));
            Attributes o_removeCaseSensitivenull72983_failAssert20null77045__8 = a.put("hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__8)).toString());
            Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitivenull72983_failAssert20null77045__8)).hashCode())));
            a.put(null, "Jsoup");
            a.size();
            a.remove("Tot");
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitivenull72983 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivenull72971_failAssert27null89113() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            a.put(null, "a&p");
            a.put("tot", "one");
            a.put("Hello", "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.remove("Tot");
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitivenull72971 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivenull72977_failAssert23null84841() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_removeCaseSensitivenull72977_failAssert23null84841__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivenull72977_failAssert23null84841__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitivenull72977_failAssert23null84841__5)).hashCode())));
            Attributes o_removeCaseSensitivenull72977_failAssert23null84841__6 = a.put("tot", "one");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivenull72977_failAssert23null84841__6)).toString());
            Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitivenull72977_failAssert23null84841__6)).hashCode())));
            a.put(null, "There");
            a.put("hello", "There");
            a.put("data-name", "Jsoup");
            a.size();
            a.remove("Tot");
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitivenull72977 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivenull72990_failAssert28null92472() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_removeCaseSensitivenull72990_failAssert28null92472__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__5)).hashCode())));
            Attributes o_removeCaseSensitivenull72990_failAssert28null92472__6 = a.put("tot", "one");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__6)).toString());
            Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__6)).hashCode())));
            Attributes o_removeCaseSensitivenull72990_failAssert28null92472__7 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__7)).toString());
            Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__7)).hashCode())));
            Attributes o_removeCaseSensitivenull72990_failAssert28null92472__8 = a.put("hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__8)).toString());
            Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__8)).hashCode())));
            Attributes o_removeCaseSensitivenull72990_failAssert28null92472__9 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__9)).toString());
            Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitivenull72990_failAssert28null92472__9)).hashCode())));
            int o_removeCaseSensitivenull72990_failAssert28null92472__10 = a.size();
            Assert.assertEquals(5, ((int) (o_removeCaseSensitivenull72990_failAssert28null92472__10)));
            a.remove(null);
            a.remove("Hello");
            a.size();
            a.hasKey("tot");
            a.hasKey("Tot");
            org.junit.Assert.fail("removeCaseSensitivenull72990 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void removeCaseSensitivenull73004_failAssert26null95476_mg143484() throws Exception {
        try {
            Attributes __DSPOT_incoming_25369 = new Attributes();
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_removeCaseSensitivenull73004_failAssert26null95476__5 = a.put("Tot", "a&p");
            Assert.assertEquals(" Tot=\"a&amp;p\"", ((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__5)).toString());
            Assert.assertEquals(-924093553, ((int) (((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__5)).hashCode())));
            Attributes o_removeCaseSensitivenull73004_failAssert26null95476__6 = a.put("tot", "one");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\"", ((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__6)).toString());
            Assert.assertEquals(-1686226883, ((int) (((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__6)).hashCode())));
            Attributes o_removeCaseSensitivenull73004_failAssert26null95476__7 = a.put("Hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\"", ((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__7)).toString());
            Assert.assertEquals(-1006812564, ((int) (((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__7)).hashCode())));
            Attributes o_removeCaseSensitivenull73004_failAssert26null95476__8 = a.put("hello", "There");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\"", ((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__8)).toString());
            Assert.assertEquals(-2146972193, ((int) (((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__8)).hashCode())));
            Attributes o_removeCaseSensitivenull73004_failAssert26null95476__9 = a.put("data-name", "Jsoup");
            Assert.assertEquals(" Tot=\"a&amp;p\" tot=\"one\" Hello=\"There\" hello=\"There\" data-name=\"Jsoup\"", ((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__9)).toString());
            Assert.assertEquals(-819992819, ((int) (((Attributes) (o_removeCaseSensitivenull73004_failAssert26null95476__9)).hashCode())));
            int o_removeCaseSensitivenull73004_failAssert26null95476__10 = a.size();
            a.remove("Tot");
            a.remove("Hello");
            int o_removeCaseSensitivenull73004_failAssert26null95476__13 = a.size();
            boolean o_removeCaseSensitivenull73004_failAssert26null95476__14 = a.hasKey("tot");
            a.hasKey(null);
            org.junit.Assert.fail("removeCaseSensitivenull73004 should have thrown IllegalArgumentException");
            a.addAll(__DSPOT_incoming_25369);
        } catch (IllegalArgumentException expected) {
        }
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
    public void testSetKeyConsistency_rv446646() throws Exception {
        Object __DSPOT_o_85414 = new Object();
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv446646__12 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv446646__12);
        boolean o_testSetKeyConsistency_rv446646__13 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv446646__13);
        boolean o_testSetKeyConsistency_rv446646__14 = __DSPOT_invoc_3.equals(__DSPOT_o_85414);
        Assert.assertFalse(o_testSetKeyConsistency_rv446646__14);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv446646__12);
        Assert.assertTrue(o_testSetKeyConsistency_rv446646__13);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg446629() throws Exception {
        String __DSPOT_key_85409 = "[vJY%HvS^S02;qPfboYE";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg446629__4 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446629__4)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg446629__4)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg446629__9 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg446629__9);
        boolean o_testSetKeyConsistency_mg446629__10 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg446629__10);
        a.removeIgnoreCase(__DSPOT_key_85409);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446629__4)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_mg446629__4)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg446629__9);
        Assert.assertTrue(o_testSetKeyConsistency_mg446629__10);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446664_failAssert212() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put(null, "a");
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446664 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv446648() throws Exception {
        String __DSPOT_key_85416 = "I_zp&{/.{qW#>jKo-Y2<";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv446648__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv446648__11);
        boolean o_testSetKeyConsistency_rv446648__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv446648__12);
        String o_testSetKeyConsistency_rv446648__13 = __DSPOT_invoc_3.getIgnoreCase(__DSPOT_key_85416);
        Assert.assertEquals("", o_testSetKeyConsistency_rv446648__13);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv446648__11);
        Assert.assertTrue(o_testSetKeyConsistency_rv446648__12);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446583_failAssert208() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("", "a");
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString446583 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv446643() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv446643__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv446643__10);
        boolean o_testSetKeyConsistency_rv446643__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv446643__11);
        List<Attribute> o_testSetKeyConsistency_rv446643__12 = __DSPOT_invoc_3.asList();
        Assert.assertFalse(o_testSetKeyConsistency_rv446643__12.isEmpty());
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv446643__10);
        Assert.assertTrue(o_testSetKeyConsistency_rv446643__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446672_failAssert214() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("a", "a");
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey(null);
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446672 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv446654() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv446654__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv446654__10);
        boolean o_testSetKeyConsistency_rv446654__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv446654__11);
        __DSPOT_invoc_3.normalize();
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv446654__10);
        Assert.assertTrue(o_testSetKeyConsistency_rv446654__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv446644() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv446644__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv446644__10);
        boolean o_testSetKeyConsistency_rv446644__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv446644__11);
        Attributes o_testSetKeyConsistency_rv446644__12 = __DSPOT_invoc_3.clone();
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_rv446644__12)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_rv446644__12)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(5088, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv446644__10);
        Assert.assertTrue(o_testSetKeyConsistency_rv446644__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv446655() throws Exception {
        boolean __DSPOT_value_85420 = false;
        String __DSPOT_key_85419 = "/CDzpLZmx++[QG)C{;zS";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv446655__12 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv446655__12);
        boolean o_testSetKeyConsistency_rv446655__13 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv446655__13);
        Attributes o_testSetKeyConsistency_rv446655__14 = __DSPOT_invoc_3.put(__DSPOT_key_85419, __DSPOT_value_85420);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_rv446655__14)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_rv446655__14)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv446655__12);
        Assert.assertTrue(o_testSetKeyConsistency_rv446655__13);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_rv446650() throws Exception {
        String __DSPOT_key_85418 = ">zy@>$0BNQ4[R;kh<!|;";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes __DSPOT_invoc_3 = a.put("a", "a");
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_rv446650__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_rv446650__11);
        boolean o_testSetKeyConsistency_rv446650__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_rv446650__12);
        boolean o_testSetKeyConsistency_rv446650__13 = __DSPOT_invoc_3.hasKeyIgnoreCase(__DSPOT_key_85418);
        Assert.assertFalse(o_testSetKeyConsistency_rv446650__13);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_rv446650__11);
        Assert.assertTrue(o_testSetKeyConsistency_rv446650__12);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446589() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistencylitString446589__3 = a.put("a", "\n");
        Assert.assertEquals(" a=\"\n\"", ((Attributes) (o_testSetKeyConsistencylitString446589__3)).toString());
        Assert.assertEquals(119433080, ((int) (((Attributes) (o_testSetKeyConsistencylitString446589__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistencylitString446589__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistencylitString446589__8);
        boolean o_testSetKeyConsistencylitString446589__9 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistencylitString446589__9);
        Assert.assertEquals(" b=\"\n\"", ((Attributes) (a)).toString());
        Assert.assertEquals(120356601, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"\n\"", ((Attributes) (o_testSetKeyConsistencylitString446589__3)).toString());
        Assert.assertEquals(120356601, ((int) (((Attributes) (o_testSetKeyConsistencylitString446589__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistencylitString446589__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446594_failAssert210() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("a", "a");
            for (Attribute at : a) {
                at.setKey("\n");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString446594 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("String must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg446626() throws Exception {
        String __DSPOT_value_85406 = ">vMv:[SY1WsMFCOHOe<_";
        String __DSPOT_key_85405 = "t+,c;JU`0F=+fGpp-ayp";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg446626__5 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446626__5)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg446626__5)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg446626__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg446626__10);
        boolean o_testSetKeyConsistency_mg446626__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg446626__11);
        Attributes o_testSetKeyConsistency_mg446626__12 = a.put(__DSPOT_key_85405, __DSPOT_value_85406);
        Assert.assertEquals(" b=\"a\" t+,c;JU`0F=+fGpp-ayp=\">vMv:[SY1WsMFCOHOe<_\"", ((Attributes) (o_testSetKeyConsistency_mg446626__12)).toString());
        Assert.assertEquals(-1179156734, ((int) (((Attributes) (o_testSetKeyConsistency_mg446626__12)).hashCode())));
        Assert.assertEquals(" b=\"a\" t+,c;JU`0F=+fGpp-ayp=\">vMv:[SY1WsMFCOHOe<_\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-1179156734, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" t+,c;JU`0F=+fGpp-ayp=\">vMv:[SY1WsMFCOHOe<_\"", ((Attributes) (o_testSetKeyConsistency_mg446626__5)).toString());
        Assert.assertEquals(-1179156734, ((int) (((Attributes) (o_testSetKeyConsistency_mg446626__5)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg446626__10);
        Assert.assertTrue(o_testSetKeyConsistency_mg446626__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg446637() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg446637__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446637__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg446637__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
            at.html();
        }
        boolean o_testSetKeyConsistency_mg446637__9 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg446637__9);
        boolean o_testSetKeyConsistency_mg446637__10 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg446637__10);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446637__3)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_mg446637__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg446637__9);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446667_failAssert213() throws Exception {
        try {
            Attributes a = new Attributes();
            a.put("a", "a");
            for (Attribute at : a) {
                at.setKey(null);
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446667 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg446625() throws Exception {
        boolean __DSPOT_value_85404 = true;
        String __DSPOT_key_85403 = "B=Sm##<o<G2</]])<U%g";
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg446625__5 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446625__5)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg446625__5)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg446625__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg446625__10);
        boolean o_testSetKeyConsistency_mg446625__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg446625__11);
        Attributes o_testSetKeyConsistency_mg446625__12 = a.put(__DSPOT_key_85403, __DSPOT_value_85404);
        Assert.assertEquals(" b=\"a\" B=Sm##<o<G2</]])<U%g", ((Attributes) (o_testSetKeyConsistency_mg446625__12)).toString());
        Assert.assertEquals(1153612529, ((int) (((Attributes) (o_testSetKeyConsistency_mg446625__12)).hashCode())));
        Assert.assertEquals(" b=\"a\" B=Sm##<o<G2</]])<U%g", ((Attributes) (a)).toString());
        Assert.assertEquals(1153612529, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" B=Sm##<o<G2</]])<U%g", ((Attributes) (o_testSetKeyConsistency_mg446625__5)).toString());
        Assert.assertEquals(1153612529, ((int) (((Attributes) (o_testSetKeyConsistency_mg446625__5)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg446625__10);
        Assert.assertTrue(o_testSetKeyConsistency_mg446625__11);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg446639() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg446639__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446639__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg446639__3)).hashCode())));
        for (Attribute at : a) {
            String __DSPOT_val_85412 = "a%]w@LFE<8%c&YF@Q^RZ";
            at.setKey("b");
            at.setValue(__DSPOT_val_85412);
        }
        boolean o_testSetKeyConsistency_mg446639__10 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg446639__10);
        boolean o_testSetKeyConsistency_mg446639__11 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg446639__11);
        Assert.assertEquals(" b=\"a%]w@LFE<8%c&amp;YF@Q^RZ\"", ((Attributes) (a)).toString());
        Assert.assertEquals(1287321150, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a%]w@LFE<8%c&amp;YF@Q^RZ\"", ((Attributes) (o_testSetKeyConsistency_mg446639__3)).toString());
        Assert.assertEquals(1287321150, ((int) (((Attributes) (o_testSetKeyConsistency_mg446639__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg446639__10);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_mg446627() throws Exception {
        Attribute __DSPOT_attribute_85407 = new Attribute("34.cvUSk)L?M,)*,1#A;", "vL*.Jx@xW5$Qw&UBB=h]", new Attributes());
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_mg446627__6 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_mg446627__6)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_mg446627__6)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_mg446627__11 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_mg446627__11);
        boolean o_testSetKeyConsistency_mg446627__12 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_mg446627__12);
        Attributes o_testSetKeyConsistency_mg446627__13 = a.put(__DSPOT_attribute_85407);
        Assert.assertEquals(" b=\"a\" 34.cvUSk)L?M,)*,1#A;=\"vL*.Jx@xW5$Qw&amp;UBB=h]\"", ((Attributes) (o_testSetKeyConsistency_mg446627__13)).toString());
        Assert.assertEquals(-2040828207, ((int) (((Attributes) (o_testSetKeyConsistency_mg446627__13)).hashCode())));
        Assert.assertEquals(" b=\"a\" 34.cvUSk)L?M,)*,1#A;=\"vL*.Jx@xW5$Qw&amp;UBB=h]\"", ((Attributes) (a)).toString());
        Assert.assertEquals(-2040828207, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\" 34.cvUSk)L?M,)*,1#A;=\"vL*.Jx@xW5$Qw&amp;UBB=h]\"", ((Attributes) (o_testSetKeyConsistency_mg446627__6)).toString());
        Assert.assertEquals(-2040828207, ((int) (((Attributes) (o_testSetKeyConsistency_mg446627__6)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_mg446627__11);
        Assert.assertTrue(o_testSetKeyConsistency_mg446627__12);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446584() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistencylitString446584__3 = a.put("\n", "a");
        Assert.assertEquals(" \n=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString446584__3)).toString());
        Assert.assertEquals(41678570, ((int) (((Attributes) (o_testSetKeyConsistencylitString446584__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistencylitString446584__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistencylitString446584__8);
        boolean o_testSetKeyConsistencylitString446584__9 = a.hasKey("b");
        Assert.assertFalse(o_testSetKeyConsistencylitString446584__9);
        Assert.assertEquals(" \n=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(41678570, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" \n=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString446584__3)).toString());
        Assert.assertEquals(41678570, ((int) (((Attributes) (o_testSetKeyConsistencylitString446584__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistencylitString446584__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_remove446610() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_remove446610__7 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_remove446610__7);
        boolean o_testSetKeyConsistency_remove446610__8 = a.hasKey("b");
        Assert.assertFalse(o_testSetKeyConsistency_remove446610__8);
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_remove446610__7);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446588() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistencylitString446588__3 = a.put("a", "");
        Assert.assertEquals(" a=\"\"", ((Attributes) (o_testSetKeyConsistencylitString446588__3)).toString());
        Assert.assertEquals(119135170, ((int) (((Attributes) (o_testSetKeyConsistencylitString446588__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistencylitString446588__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistencylitString446588__8);
        boolean o_testSetKeyConsistencylitString446588__9 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistencylitString446588__9);
        Assert.assertEquals(" b=\"\"", ((Attributes) (a)).toString());
        Assert.assertEquals(120058691, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"\"", ((Attributes) (o_testSetKeyConsistencylitString446588__3)).toString());
        Assert.assertEquals(120058691, ((int) (((Attributes) (o_testSetKeyConsistencylitString446588__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistencylitString446588__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistency_add446606() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistency_add446606__3 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_add446606__3)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_add446606__3)).hashCode())));
        Attributes o_testSetKeyConsistency_add446606__4 = a.put("a", "a");
        Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistency_add446606__4)).toString());
        Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistency_add446606__4)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistency_add446606__9 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistency_add446606__9);
        boolean o_testSetKeyConsistency_add446606__10 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistency_add446606__10);
        Assert.assertEquals(" b=\"a\"", ((Attributes) (a)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_add446606__3)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_add446606__3)).hashCode())));
        Assert.assertEquals(" b=\"a\"", ((Attributes) (o_testSetKeyConsistency_add446606__4)).toString());
        Assert.assertEquals(122948418, ((int) (((Attributes) (o_testSetKeyConsistency_add446606__4)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistency_add446606__9);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446665() throws Exception {
        Attributes a = new Attributes();
        Assert.assertEquals("", ((Attributes) (a)).toString());
        Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
        Attributes o_testSetKeyConsistencynull446665__3 = a.put("a", null);
        Assert.assertEquals(" a", ((Attributes) (o_testSetKeyConsistencynull446665__3)).toString());
        Assert.assertEquals(119135170, ((int) (((Attributes) (o_testSetKeyConsistencynull446665__3)).hashCode())));
        for (Attribute at : a) {
            at.setKey("b");
        }
        boolean o_testSetKeyConsistencynull446665__8 = a.hasKey("a");
        Assert.assertFalse(o_testSetKeyConsistencynull446665__8);
        boolean o_testSetKeyConsistencynull446665__9 = a.hasKey("b");
        Assert.assertTrue(o_testSetKeyConsistencynull446665__9);
        Assert.assertEquals(" b", ((Attributes) (a)).toString());
        Assert.assertEquals(120058691, ((int) (((Attributes) (a)).hashCode())));
        Assert.assertEquals(" b", ((Attributes) (o_testSetKeyConsistencynull446665__3)).toString());
        Assert.assertEquals(120058691, ((int) (((Attributes) (o_testSetKeyConsistencynull446665__3)).hashCode())));
        Assert.assertFalse(o_testSetKeyConsistencynull446665__8);
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446667_failAssert213null448400() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446667_failAssert213null448400__5 = a.put("a", null);
            Assert.assertEquals(" a", ((Attributes) (o_testSetKeyConsistencynull446667_failAssert213null448400__5)).toString());
            Assert.assertEquals(119135170, ((int) (((Attributes) (o_testSetKeyConsistencynull446667_failAssert213null448400__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey(null);
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446667 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446583_failAssert208null454137() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencylitString446583_failAssert208null454137__5 = a.put("", "a");
            Assert.assertEquals(" =\"a\"", ((Attributes) (o_testSetKeyConsistencylitString446583_failAssert208null454137__5)).toString());
            Assert.assertEquals(32443360, ((int) (((Attributes) (o_testSetKeyConsistencylitString446583_failAssert208null454137__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString446583 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446672_failAssert214null452158() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            a.put(null, "a");
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey(null);
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446672 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446667_failAssert213litString447221() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446667_failAssert213litString447221__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencynull446667_failAssert213litString447221__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencynull446667_failAssert213litString447221__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey(null);
            }
            a.hasKey("u");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446667 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446672_failAssert214null452167() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446672_failAssert214null452167__5 = a.put("a", null);
            Assert.assertEquals(" a", ((Attributes) (o_testSetKeyConsistencynull446672_failAssert214null452167__5)).toString());
            Assert.assertEquals(119135170, ((int) (((Attributes) (o_testSetKeyConsistencynull446672_failAssert214null452167__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey(null);
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446672 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446593_failAssert209null452214() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencylitString446593_failAssert209null452214__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString446593_failAssert209null452214__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencylitString446593_failAssert209null452214__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString446593 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446672_failAssert214_mg451641() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446672_failAssert214_mg451641__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencynull446672_failAssert214_mg451641__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencynull446672_failAssert214_mg451641__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
                at.html();
            }
            a.hasKey(null);
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446672 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencylitString446594_failAssert210null452313() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencylitString446594_failAssert210null452313__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencylitString446594_failAssert210null452313__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencylitString446594_failAssert210null452313__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("\n");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencylitString446594 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446677_failAssert215_mg449284_rv467271() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446677_failAssert215_mg449284__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencynull446677_failAssert215_mg449284__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencynull446677_failAssert215_mg449284__5)).hashCode())));
            for (Attribute at : a) {
                Attribute __DSPOT_o_91074 = new Attribute("!@3@JHeB&hG^m> !n%qL", "ZaC/cCv<q BRDY:]c6XX", new Attributes());
                at.setKey("b");
                Attribute __DSPOT_invoc_24 = at.clone();
                __DSPOT_invoc_24.equals(__DSPOT_o_91074);
            }
            boolean o_testSetKeyConsistencynull446677_failAssert215_mg449284__11 = a.hasKey("a");
            a.hasKey(null);
            org.junit.Assert.fail("testSetKeyConsistencynull446677 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446677_failAssert215_mg448958_mg460624() throws Exception {
        try {
            Attributes __DSPOT_o_88680 = new Attributes();
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446677_failAssert215_mg448958__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencynull446677_failAssert215_mg448958__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencynull446677_failAssert215_mg448958__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
            }
            boolean o_testSetKeyConsistencynull446677_failAssert215_mg448958__10 = a.hasKey("a");
            a.hasKey(null);
            org.junit.Assert.fail("testSetKeyConsistencynull446677 should have thrown IllegalArgumentException");
            a.asList();
            o_testSetKeyConsistencynull446677_failAssert215_mg448958__5.equals(__DSPOT_o_88680);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446677_failAssert215_mg449404_mg464228() throws Exception {
        try {
            String __DSPOT_value_90021 = "*C|`6z(PPV{L+(BM?jpF";
            String __DSPOT_key_90020 = "u8jZfNet)n?Xm6=^Y_sV";
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446677_failAssert215_mg449404__5 = a.put("a", "a");
            Assert.assertEquals(" a=\"a\"", ((Attributes) (o_testSetKeyConsistencynull446677_failAssert215_mg449404__5)).toString());
            Assert.assertEquals(122024897, ((int) (((Attributes) (o_testSetKeyConsistencynull446677_failAssert215_mg449404__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
                at.html();
            }
            boolean o_testSetKeyConsistencynull446677_failAssert215_mg449404__11 = a.hasKey("a");
            a.hasKey(null);
            org.junit.Assert.fail("testSetKeyConsistencynull446677 should have thrown IllegalArgumentException");
            o_testSetKeyConsistencynull446677_failAssert215_mg449404__5.put(__DSPOT_key_90020, __DSPOT_value_90021);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446664_failAssert212_mg449605null467410() throws Exception {
        try {
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            a.put(null, "a");
            for (Attribute at : a) {
                at.setKey("b");
            }
            a.hasKey("a");
            a.hasKey("b");
            org.junit.Assert.fail("testSetKeyConsistencynull446664 should have thrown IllegalArgumentException");
            a.hashCode();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetKeyConsistencynull446677_failAssert215null450086_mg463157() throws Exception {
        try {
            Attributes __DSPOT_incoming_89472 = new Attributes();
            Attributes a = new Attributes();
            Assert.assertEquals("", ((Attributes) (a)).toString());
            Assert.assertEquals(32, ((int) (((Attributes) (a)).hashCode())));
            Attributes o_testSetKeyConsistencynull446677_failAssert215null450086__5 = a.put("a", null);
            Assert.assertEquals(" a", ((Attributes) (o_testSetKeyConsistencynull446677_failAssert215null450086__5)).toString());
            Assert.assertEquals(119135170, ((int) (((Attributes) (o_testSetKeyConsistencynull446677_failAssert215null450086__5)).hashCode())));
            for (Attribute at : a) {
                at.setKey("b");
            }
            boolean o_testSetKeyConsistencynull446677_failAssert215null450086__10 = a.hasKey("a");
            a.hasKey(null);
            org.junit.Assert.fail("testSetKeyConsistencynull446677 should have thrown IllegalArgumentException");
            o_testSetKeyConsistencynull446677_failAssert215null450086__5.addAll(__DSPOT_incoming_89472);
        } catch (IllegalArgumentException expected) {
        }
    }
}

