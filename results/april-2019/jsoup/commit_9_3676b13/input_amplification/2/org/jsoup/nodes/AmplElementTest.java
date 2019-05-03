package org.jsoup.nodes;


import java.util.Collection;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testSetText_literalMutationString93741_failAssert0_literalMutationNumber94715_failAssert0() throws Exception {
        try {
            {
                String h = "<div id=1>Hello <p>there <b>now</b></p></div>";
                Document doc = Jsoup.parse(h);
                doc.text();
                doc.select("p").get(-1).text();
                Element div = doc.getElementById("[").text("Gone");
                div.text();
                doc.select("p").size();
                org.junit.Assert.fail("testSetText_literalMutationString93741 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSetText_literalMutationString93741_failAssert0_literalMutationNumber94715 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetText_literalMutationString93745_literalMutationNumber94127_failAssert0() throws Exception {
        try {
            String h = "<div id=1>Hello <p>there <b>now</b></p></div>";
            Document doc = Jsoup.parse(h);
            String o_testSetText_literalMutationString93745__4 = doc.text();
            String o_testSetText_literalMutationString93745__5 = doc.select("p").get(-1).text();
            Element div = doc.getElementById("1").text("GHne");
            String o_testSetText_literalMutationString93745__11 = div.text();
            int o_testSetText_literalMutationString93745__12 = doc.select("p").size();
            org.junit.Assert.fail("testSetText_literalMutationString93745_literalMutationNumber94127 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add167055() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add167055__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasParent());
        Element o_testAddBooleanAttribute_add167055__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__5)).isBlock());
        Assert.assertEquals("<div true false=\"value\"></div>", ((Element) (o_testAddBooleanAttribute_add167055__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasParent());
        Element o_testAddBooleanAttribute_add167055__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasParent());
        boolean o_testAddBooleanAttribute_add167055__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add167055__7);
        String o_testAddBooleanAttribute_add167055__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add167055__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add167055__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add167055__12)));
        Attribute o_testAddBooleanAttribute_add167055__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getKey());
        boolean boolean_746 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add167055__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add167055__16);
        String o_testAddBooleanAttribute_add167055__17 = div.outerHtml();
        Assert.assertEquals("<div true></div>", o_testAddBooleanAttribute_add167055__17);
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__5)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add167055__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add167055__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add167055__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add167055__16);
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add167055_add174918() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add167055__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasParent());
        o_testAddBooleanAttribute_add167055__4.hasParent();
        Element o_testAddBooleanAttribute_add167055__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__5)).isBlock());
        Assert.assertEquals("<div true false=\"value\"></div>", ((Element) (o_testAddBooleanAttribute_add167055__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasParent());
        Element o_testAddBooleanAttribute_add167055__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasParent());
        boolean o_testAddBooleanAttribute_add167055__7 = div.hasAttr("true");
        String o_testAddBooleanAttribute_add167055__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add167055__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add167055__12 = attributes.size();
        Attribute o_testAddBooleanAttribute_add167055__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getKey());
        boolean boolean_746 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add167055__16 = div.hasAttr("false");
        String o_testAddBooleanAttribute_add167055__17 = div.outerHtml();
        Assert.assertEquals("<div true></div>", o_testAddBooleanAttribute_add167055__17);
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__5)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add167055__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add167055__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasParent());
        Assert.assertEquals("", o_testAddBooleanAttribute_add167055__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getKey());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add167055_literalMutationString170264() throws Exception {
        Element div = new Element(Tag.valueOf("dv"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).isBlock());
        Assert.assertEquals("<dv></dv>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add167055__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).isBlock());
        Assert.assertEquals("<dv true></dv>", ((Element) (o_testAddBooleanAttribute_add167055__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasParent());
        Element o_testAddBooleanAttribute_add167055__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).isBlock());
        Assert.assertEquals("<dv true false=\"value\"></dv>", ((Element) (o_testAddBooleanAttribute_add167055__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasParent());
        Element o_testAddBooleanAttribute_add167055__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).isBlock());
        Assert.assertEquals("<dv true></dv>", ((Element) (o_testAddBooleanAttribute_add167055__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasParent());
        boolean o_testAddBooleanAttribute_add167055__7 = div.hasAttr("true");
        String o_testAddBooleanAttribute_add167055__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add167055__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add167055__12 = attributes.size();
        Attribute o_testAddBooleanAttribute_add167055__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getKey());
        boolean boolean_746 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add167055__16 = div.hasAttr("false");
        String o_testAddBooleanAttribute_add167055__17 = div.outerHtml();
        Assert.assertEquals("<dv true></dv>", o_testAddBooleanAttribute_add167055__17);
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).isBlock());
        Assert.assertEquals("<dv true></dv>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).isBlock());
        Assert.assertEquals("<dv true></dv>", ((Element) (o_testAddBooleanAttribute_add167055__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).isBlock());
        Assert.assertEquals("<dv true></dv>", ((Element) (o_testAddBooleanAttribute_add167055__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add167055__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).isBlock());
        Assert.assertEquals("<dv true></dv>", ((Element) (o_testAddBooleanAttribute_add167055__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add167055__6)).hasParent());
        Assert.assertEquals("", o_testAddBooleanAttribute_add167055__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add167055__13)).getKey());
    }

    @Test(timeout = 10000)
    public void testNextElementSiblingAfterClone_literalMutationString116181_failAssert0_literalMutationNumber119154_failAssert0() throws Exception {
        try {
            {
                String html = "<!DOCTYPE html><html lang=\"en\"><head></head><body><div>Initial element</div></body></html>";
                String expectedText = "New element";
                String cloneExpect = "New element in clone";
                Document original = Jsoup.parse(html);
                Document clone = original.clone();
                Element originalElement = original.body().child(-1);
                originalElement.after(((" Hello\nthere \u00a0  " + expectedText) + "</div>"));
                Element originalNextElementSibling = originalElement.nextElementSibling();
                Element originalNextSibling = ((Element) (originalElement.nextSibling()));
                originalNextElementSibling.text();
                originalNextSibling.text();
                Element cloneElement = clone.body().child(0);
                cloneElement.after((("<div>" + cloneExpect) + "</div>"));
                Element cloneNextElementSibling = cloneElement.nextElementSibling();
                Element cloneNextSibling = ((Element) (cloneElement.nextSibling()));
                cloneNextElementSibling.text();
                cloneNextSibling.text();
                org.junit.Assert.fail("testNextElementSiblingAfterClone_literalMutationString116181 should have thrown ClassCastException");
            }
            org.junit.Assert.fail("testNextElementSiblingAfterClone_literalMutationString116181_failAssert0_literalMutationNumber119154 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

