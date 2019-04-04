package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testNormalisesText_add497356_literalMutationString497423() throws Exception {
        String h = "<p>Hello<p>There.</p>d \n <p>Here <b>is</b> \n s<b>om</b>e text.";
        Assert.assertEquals("<p>Hello<p>There.</p>d \n <p>Here <b>is</b> \n s<b>om</b>e text.", h);
        Document o_testNormalisesText_add497356__2 = Jsoup.parse(h);
        Assert.assertFalse(((Document) (o_testNormalisesText_add497356__2)).isBlock());
        Assert.assertTrue(((Document) (o_testNormalisesText_add497356__2)).hasText());
        Assert.assertFalse(((Document) (o_testNormalisesText_add497356__2)).getAllElements().isEmpty());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello</p>\n  <p>There.</p>d \n  <p>Here <b>is</b> s<b>om</b>e text.</p>\n </body>\n</html>", ((Document) (o_testNormalisesText_add497356__2)).toString());
        Assert.assertFalse(((Document) (o_testNormalisesText_add497356__2)).hasParent());
        Document doc = Jsoup.parse(h);
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello</p>\n  <p>There.</p>d \n  <p>Here <b>is</b> s<b>om</b>e text.</p>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String text = doc.text();
        Assert.assertEquals("Hello There.d Here is some text.", text);
        Assert.assertEquals("<p>Hello<p>There.</p>d \n <p>Here <b>is</b> \n s<b>om</b>e text.", h);
        Assert.assertFalse(((Document) (o_testNormalisesText_add497356__2)).isBlock());
        Assert.assertTrue(((Document) (o_testNormalisesText_add497356__2)).hasText());
        Assert.assertFalse(((Document) (o_testNormalisesText_add497356__2)).getAllElements().isEmpty());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello</p>\n  <p>There.</p>d \n  <p>Here <b>is</b> s<b>om</b>e text.</p>\n </body>\n</html>", ((Document) (o_testNormalisesText_add497356__2)).toString());
        Assert.assertFalse(((Document) (o_testNormalisesText_add497356__2)).hasParent());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello</p>\n  <p>There.</p>d \n  <p>Here <b>is</b> s<b>om</b>e text.</p>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
    }

    @Test(timeout = 10000)
    public void testKeepsPreText_literalMutationString99596_literalMutationString99677() throws Exception {
        String h = "<p>Hello \n \n there.</p>udiv><pre>  What\'s \n\n  that?</pre>";
        Assert.assertEquals("<p>Hello \n \n there.</p>udiv><pre>  What\'s \n\n  that?</pre>", h);
        Document doc = Jsoup.parse(h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>udiv&gt;\n  <pre>  What\'s \n\n  that?</pre>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String o_testKeepsPreText_literalMutationString99596__4 = doc.text();
        Assert.assertEquals("Hello there.udiv>   What\'s \n\n  that?", o_testKeepsPreText_literalMutationString99596__4);
        Assert.assertEquals("<p>Hello \n \n there.</p>udiv><pre>  What\'s \n\n  that?</pre>", h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>udiv&gt;\n  <pre>  What\'s \n\n  that?</pre>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
    }

    @Test(timeout = 10000)
    public void testKeepsPreText_literalMutationString99595_literalMutationString99656() throws Exception {
        String h = "<p>Hello \n \n there.</p>k<div><pre>  What\'sB\n\n  that?</pre>";
        Assert.assertEquals("<p>Hello \n \n there.</p>k<div><pre>  What\'sB\n\n  that?</pre>", h);
        Document doc = Jsoup.parse(h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>k\n  <div>\n   <pre>  What\'sB\n\n  that?</pre>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String o_testKeepsPreText_literalMutationString99595__4 = doc.text();
        Assert.assertEquals("Hello there.k   What\'sB\n\n  that?", o_testKeepsPreText_literalMutationString99595__4);
        Assert.assertEquals("<p>Hello \n \n there.</p>k<div><pre>  What\'sB\n\n  that?</pre>", h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).getAllElements().isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>k\n  <div>\n   <pre>  What\'sB\n\n  that?</pre>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add443538() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add443538__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__4)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add443538__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add443538__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__4)).hasParent());
        Element o_testAddBooleanAttribute_add443538__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__5)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add443538__5)).isBlock());
        Assert.assertEquals("<div true false=\"value\"></div>", ((Element) (o_testAddBooleanAttribute_add443538__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__5)).hasParent());
        Element o_testAddBooleanAttribute_add443538__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__6)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add443538__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add443538__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__6)).hasParent());
        boolean o_testAddBooleanAttribute_add443538__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add443538__7);
        String o_testAddBooleanAttribute_add443538__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add443538__8);
        List<Attribute> attributes = div.attributes().asList();
        attributes.size();
        Attribute o_testAddBooleanAttribute_add443538__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add443538__13)).toString());
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add443538__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add443538__13)).getKey());
        boolean boolean_748 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add443538__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add443538__16);
        String o_testAddBooleanAttribute_add443538__17 = div.outerHtml();
        Assert.assertEquals("<div true></div>", o_testAddBooleanAttribute_add443538__17);
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Element) (div)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__4)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__4)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add443538__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add443538__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__5)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__5)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add443538__5)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add443538__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__6)).hasText());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__6)).getAllElements().isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add443538__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add443538__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add443538__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add443538__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add443538__8);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add443538__13)).toString());
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add443538__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add443538__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add443538__16);
    }
}

