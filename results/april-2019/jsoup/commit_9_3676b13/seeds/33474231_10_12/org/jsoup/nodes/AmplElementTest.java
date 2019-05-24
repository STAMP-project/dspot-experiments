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
    public void testKeepsPreText_literalMutationString671() throws Exception {
        String h = "<p>Hello \n \n there.</p>h<div><pre>  What\'s \n\n  that?</pre>";
        Document doc = Jsoup.parse(h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>h\n  <div>\n   <pre>  What\'s \n\n  that?</pre>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String o_testKeepsPreText_literalMutationString671__4 = doc.text();
        Assert.assertEquals("Hello there.h   What\'s \n\n  that?", o_testKeepsPreText_literalMutationString671__4);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>h\n  <div>\n   <pre>  What\'s \n\n  that?</pre>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add4328() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add4328__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4328__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4328__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__4)).hasParent());
        Element o_testAddBooleanAttribute_add4328__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4328__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4328__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__5)).hasParent());
        Element o_testAddBooleanAttribute_add4328__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4328__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4328__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__6)).hasParent());
        boolean o_testAddBooleanAttribute_add4328__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add4328__7);
        String o_testAddBooleanAttribute_add4328__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add4328__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add4328__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add4328__12)));
        Attribute o_testAddBooleanAttribute_add4328__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).getKey());
        boolean boolean_719 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add4328__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add4328__16);
        div.outerHtml();
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4328__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4328__4)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4328__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4328__5)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add4328__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add4328__6)).isBlock());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add4328__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add4328__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add4328__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add4328__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add4328__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add4328__16);
    }
}

