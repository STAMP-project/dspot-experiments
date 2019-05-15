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
    public void testNormalisesText_add157343_literalMutationString157380() throws Exception {
        String h = "<p>Hello<p>There.</p>[\n <p>Here <b>is</b> \n s<b>om</b>e text.";
        Document o_testNormalisesText_add157343__2 = Jsoup.parse(h);
        Assert.assertTrue(((Document) (o_testNormalisesText_add157343__2)).hasText());
        Assert.assertFalse(((Collection) (((Document) (o_testNormalisesText_add157343__2)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testNormalisesText_add157343__2)).isBlock());
        Assert.assertFalse(((Document) (o_testNormalisesText_add157343__2)).hasParent());
        Document doc = Jsoup.parse(h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String text = doc.text();
        Assert.assertEquals("Hello There.[ Here is some text.", text);
        Assert.assertTrue(((Document) (o_testNormalisesText_add157343__2)).hasText());
        Assert.assertFalse(((Collection) (((Document) (o_testNormalisesText_add157343__2)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testNormalisesText_add157343__2)).isBlock());
        Assert.assertFalse(((Document) (o_testNormalisesText_add157343__2)).hasParent());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add139594() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add139594__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add139594__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add139594__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__4)).hasParent());
        Element o_testAddBooleanAttribute_add139594__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add139594__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add139594__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__5)).hasParent());
        Element o_testAddBooleanAttribute_add139594__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add139594__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add139594__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__6)).hasParent());
        boolean o_testAddBooleanAttribute_add139594__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add139594__7);
        String o_testAddBooleanAttribute_add139594__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add139594__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add139594__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add139594__12)));
        Attribute o_testAddBooleanAttribute_add139594__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).getKey());
        boolean boolean_694 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add139594__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add139594__16);
        div.outerHtml();
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add139594__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add139594__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add139594__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add139594__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add139594__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add139594__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add139594__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add139594__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add139594__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add139594__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add139594__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add139594__16);
    }

    @Test(timeout = 10000)
    public void testNormalizesInvisiblesInText_literalMutationNumber113703_failAssert0_literalMutationString115711_failAssert0() throws Exception {
        try {
            {
                String escaped = "This&shy;is&#x200b;one&#x200c;long&#x200d;word";
                String decoded = "This\u00adis\u200bone\u200clong\u200dword";
                Document doc = Jsoup.parse(("<p>" + escaped));
                Element p = doc.select("p").first();
                doc.outputSettings().charset("ascii");
                p.text();
                String String_278 = ("jp>" + escaped) + "</p>";
                p.outerHtml();
                p.textNodes().get(-1).getWholeText();
                Element matched = doc.select("p:contains(Thisisonelongword)").first();
                matched.nodeName();
                matched.is(":containsOwn(Thisisonelongword)");
                org.junit.Assert.fail("testNormalizesInvisiblesInText_literalMutationNumber113703 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNormalizesInvisiblesInText_literalMutationNumber113703_failAssert0_literalMutationString115711 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

