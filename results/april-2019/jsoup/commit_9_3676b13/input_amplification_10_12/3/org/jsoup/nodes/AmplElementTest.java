package org.jsoup.nodes;


import java.util.Collection;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add314861() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add314861__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add314861__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add314861__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__4)).hasParent());
        Element o_testAddBooleanAttribute_add314861__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add314861__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add314861__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__5)).hasParent());
        Element o_testAddBooleanAttribute_add314861__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add314861__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add314861__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__6)).hasParent());
        boolean o_testAddBooleanAttribute_add314861__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add314861__7);
        String o_testAddBooleanAttribute_add314861__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add314861__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add314861__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add314861__12)));
        Attribute o_testAddBooleanAttribute_add314861__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).getKey());
        boolean boolean_719 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add314861__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add314861__16);
        div.outerHtml();
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add314861__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add314861__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add314861__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add314861__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add314861__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add314861__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add314861__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add314861__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add314861__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add314861__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add314861__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add314861__16);
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_literalMutationString314850_add320661() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString314850__4 = div.attr("true", true);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString314850__5 = div.attr("false", "value");
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).hasParent());
        Element o_testAddBooleanAttribute_literalMutationString314850__6 = div.attr("false", false);
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).hasParent());
        boolean o_testAddBooleanAttribute_literalMutationString314850__7 = div.hasAttr("true");
        String o_testAddBooleanAttribute_literalMutationString314850__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString314850__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_literalMutationString314850__12 = attributes.size();
        Attribute o_testAddBooleanAttribute_literalMutationString314850_add320661__25 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).getKey());
        boolean boolean_714 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_literalMutationString314850__15 = div.hasAttr("fValse");
        div.outerHtml();
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__4)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__5)).hasParent());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).getAllElements())).isEmpty());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_literalMutationString314850__6)).hasParent());
        Assert.assertEquals("", o_testAddBooleanAttribute_literalMutationString314850__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_literalMutationString314850_add320661__25)).getKey());
    }

    @Test(timeout = 10000)
    public void testPrependText_literalMutationString3069_remove4296() throws Exception {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p>></div>");
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Element div = doc.getElementById("1");
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Element o_testPrependText_literalMutationString3069__5 = div.prependText("there & now > ");
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString3069__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasText());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString3069__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasParent());
        String o_testPrependText_literalMutationString3069__6 = div.text();
        Assert.assertEquals("there & now > Hello>", o_testPrependText_literalMutationString3069__6);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   there &amp; now &gt; \n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString3069__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasText());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString3069__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasParent());
    }

    @Test(timeout = 10000)
    public void testPrependText_literalMutationString3069() throws Exception {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p>></div>");
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Element div = doc.getElementById("1");
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Element o_testPrependText_literalMutationString3069__5 = div.prependText("there & now > ");
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString3069__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString3069__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasParent());
        String o_testPrependText_literalMutationString3069__6 = div.text();
        Assert.assertEquals("there & now > Hello>", o_testPrependText_literalMutationString3069__6);
        TextUtil.stripNewlines(div.html());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   there &amp; now &gt; \n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString3069__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString3069__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasParent());
        Assert.assertEquals("there & now > Hello>", o_testPrependText_literalMutationString3069__6);
    }

    @Test(timeout = 10000)
    public void testPrependText_literalMutationString3069_add3821() throws Exception {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p>></div>");
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Element div = doc.getElementById("1");
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Element o_testPrependText_literalMutationString3069__5 = div.prependText("there & now > ");
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString3069__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasText());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString3069__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasParent());
        ((Element) (o_testPrependText_literalMutationString3069__5)).getAllElements().isEmpty();
        String o_testPrependText_literalMutationString3069__6 = div.text();
        Assert.assertEquals("there & now > Hello>", o_testPrependText_literalMutationString3069__6);
        TextUtil.stripNewlines(div.html());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   there &amp; now &gt; \n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString3069__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasText());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString3069__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString3069__5)).hasParent());
        Assert.assertEquals("there & now > Hello>", o_testPrependText_literalMutationString3069__6);
    }

    @Test(timeout = 10000)
    public void elementIsNotASiblingOfItself_literalMutationNumber9008_literalMutationNumber9254_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            Element p2 = doc.select("p").get(1);
            String o_elementIsNotASiblingOfItself_literalMutationNumber9008__6 = p2.text();
            Elements els = p2.siblingElements();
            int o_elementIsNotASiblingOfItself_literalMutationNumber9008__9 = els.size();
            els.get(-1).outerHtml();
            els.get(1).outerHtml();
            org.junit.Assert.fail("elementIsNotASiblingOfItself_literalMutationNumber9008_literalMutationNumber9254 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

