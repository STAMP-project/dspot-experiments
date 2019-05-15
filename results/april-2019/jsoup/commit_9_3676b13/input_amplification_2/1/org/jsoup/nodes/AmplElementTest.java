package org.jsoup.nodes;


import java.util.Collection;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add4326() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add4326__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4326__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4326__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__4)).hasParent());
        Element o_testAddBooleanAttribute_add4326__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4326__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4326__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__5)).hasParent());
        Element o_testAddBooleanAttribute_add4326__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4326__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4326__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__6)).hasParent());
        boolean o_testAddBooleanAttribute_add4326__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add4326__7);
        String o_testAddBooleanAttribute_add4326__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add4326__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add4326__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add4326__12)));
        Attribute o_testAddBooleanAttribute_add4326__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).getKey());
        boolean boolean_694 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add4326__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add4326__16);
        div.outerHtml();
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4326__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4326__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4326__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4326__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4326__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4326__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4326__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add4326__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add4326__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add4326__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add4326__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add4326__16);
    }

    @Test(timeout = 10000)
    public void testPrependText_literalMutationString42() throws Exception {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p>w</div>");
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   <p>Hello</p>w\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Element div = doc.getElementById("1");
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>w\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Element o_testPrependText_literalMutationString42__5 = div.prependText("there & now > ");
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString42__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString42__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString42__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>w\n</div>", ((Element) (o_testPrependText_literalMutationString42__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString42__5)).hasParent());
        String o_testPrependText_literalMutationString42__6 = div.text();
        Assert.assertEquals("there & now > Hellow", o_testPrependText_literalMutationString42__6);
        TextUtil.stripNewlines(div.html());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   there &amp; now &gt; \n   <p>Hello</p>w\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>w\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString42__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString42__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString42__5)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n there &amp; now &gt; \n <p>Hello</p>w\n</div>", ((Element) (o_testPrependText_literalMutationString42__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString42__5)).hasParent());
        Assert.assertEquals("there & now > Hellow", o_testPrependText_literalMutationString42__6);
    }
}

