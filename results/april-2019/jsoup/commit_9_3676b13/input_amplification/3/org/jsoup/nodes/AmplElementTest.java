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
    public void testGetText_literalMutationNumber479792_literalMutationNumber479837_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(reference);
            String o_testGetText_literalMutationNumber479792__3 = doc.text();
            String o_testGetText_literalMutationNumber479792__4 = doc.getElementsByTag("p").get(-1).text();
            org.junit.Assert.fail("testGetText_literalMutationNumber479792_literalMutationNumber479837 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testKeepsPreText_add130314_add130518_literalMutationString131724() throws Exception {
        String h = "<p>Hello \n \n there.</p>5 <div><pre>  What\'s \n\n  that?</pre>";
        Assert.assertEquals("<p>Hello \n \n there.</p>5 <div><pre>  What\'s \n\n  that?</pre>", h);
        Document doc = Jsoup.parse(h);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>5 \n  <div>\n   <pre>  What\'s \n\n  that?</pre>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String o_testKeepsPreText_add130314__4 = doc.text();
        Assert.assertEquals("Hello there.5   What\'s \n\n  that?", o_testKeepsPreText_add130314__4);
        String o_testKeepsPreText_add130314__5 = doc.text();
        Assert.assertEquals("Hello there.5   What\'s \n\n  that?", o_testKeepsPreText_add130314__5);
        ((Document) (doc)).getAllElements().isEmpty();
        Assert.assertEquals("<p>Hello \n \n there.</p>5 <div><pre>  What\'s \n\n  that?</pre>", h);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello there.</p>5 \n  <div>\n   <pre>  What\'s \n\n  that?</pre>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("Hello there.5   What\'s \n\n  that?", o_testKeepsPreText_add130314__4);
        Assert.assertEquals("Hello there.5   What\'s \n\n  that?", o_testKeepsPreText_add130314__5);
    }

    @Test(timeout = 10000)
    public void testSetText_add290554_literalMutationString291099() throws Exception {
        String h = "<div id=1>Hello <p>there <b>now</b></p>/div>";
        Assert.assertEquals("<div id=1>Hello <p>there <b>now</b></p>/div>", h);
        Document o_testSetText_add290554__2 = Jsoup.parse(h);
        Assert.assertFalse(((Collection) (((Document) (o_testSetText_add290554__2)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testSetText_add290554__2)).isBlock());
        Assert.assertTrue(((Document) (o_testSetText_add290554__2)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   Hello \n   <p>there <b>now</b></p>/div&gt;\n  </div>\n </body>\n</html>", ((Document) (o_testSetText_add290554__2)).toString());
        Assert.assertFalse(((Document) (o_testSetText_add290554__2)).hasParent());
        Document doc = Jsoup.parse(h);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   Hello \n   <p>there <b>now</b></p>/div&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String o_testSetText_add290554__5 = doc.text();
        Assert.assertEquals("Hello there now/div>", o_testSetText_add290554__5);
        String o_testSetText_add290554__6 = doc.select("p").get(0).text();
        Assert.assertEquals("there now", o_testSetText_add290554__6);
        Element div = doc.getElementById("1").text("Gone");
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertEquals("<div id=\"1\">\n Gone\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        String o_testSetText_add290554__12 = div.text();
        Assert.assertEquals("Gone", o_testSetText_add290554__12);
        int o_testSetText_add290554__13 = doc.select("p").size();
        Assert.assertEquals("<div id=1>Hello <p>there <b>now</b></p>/div>", h);
        Assert.assertFalse(((Collection) (((Document) (o_testSetText_add290554__2)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testSetText_add290554__2)).isBlock());
        Assert.assertTrue(((Document) (o_testSetText_add290554__2)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   Hello \n   <p>there <b>now</b></p>/div&gt;\n  </div>\n </body>\n</html>", ((Document) (o_testSetText_add290554__2)).toString());
        Assert.assertFalse(((Document) (o_testSetText_add290554__2)).hasParent());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   Gone\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("Hello there now/div>", o_testSetText_add290554__5);
        Assert.assertEquals("there now", o_testSetText_add290554__6);
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertEquals("<div id=\"1\">\n Gone\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertEquals("Gone", o_testSetText_add290554__12);
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add548272() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add548272__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add548272__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add548272__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add548272__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__4)).hasParent());
        Element o_testAddBooleanAttribute_add548272__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add548272__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add548272__5)).isBlock());
        Assert.assertEquals("<div true false=\"value\"></div>", ((Element) (o_testAddBooleanAttribute_add548272__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__5)).hasParent());
        Element o_testAddBooleanAttribute_add548272__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add548272__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add548272__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add548272__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__6)).hasParent());
        boolean o_testAddBooleanAttribute_add548272__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add548272__7);
        String o_testAddBooleanAttribute_add548272__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add548272__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add548272__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add548272__12)));
        Attribute o_testAddBooleanAttribute_add548272__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).getKey());
        boolean boolean_745 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add548272__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add548272__16);
        String o_testAddBooleanAttribute_add548272__17 = div.outerHtml();
        Assert.assertEquals("<div true></div>", o_testAddBooleanAttribute_add548272__17);
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add548272__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add548272__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add548272__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add548272__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add548272__5)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add548272__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add548272__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add548272__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add548272__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add548272__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add548272__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add548272__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add548272__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add548272__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add548272__16);
    }

    @Test(timeout = 10000)
    public void testPrependElement_literalMutationNumber230156_failAssert0_add232524_failAssert0_literalMutationString238140_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
                    Element div = doc.getElementById("1");
                    div.prependElement("p").text("Before");
                    div.prependElement("p").text("Bfore");
                    div.child(-1).text();
                    div.child(1).text();
                    org.junit.Assert.fail("testPrependElement_literalMutationNumber230156 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPrependElement_literalMutationNumber230156_failAssert0_add232524 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPrependElement_literalMutationNumber230156_failAssert0_add232524_failAssert0_literalMutationString238140 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPrependElement_literalMutationNumber230156_failAssert0_add232530_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
                Element div = doc.getElementById("1");
                div.prependElement("p").text("Before");
                div.child(-1).text();
                div.child(1).text();
                org.junit.Assert.fail("testPrependElement_literalMutationNumber230156 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPrependElement_literalMutationNumber230156_failAssert0_add232530 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPrependElement_literalMutationString230154_literalMutationNumber230424_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
            Element div = doc.getElementById("1");
            Element o_testPrependElement_literalMutationString230154__5 = div.prependElement("p").text("Bfore");
            String o_testPrependElement_literalMutationString230154__7 = div.child(-1).text();
            String o_testPrependElement_literalMutationString230154__9 = div.child(1).text();
            org.junit.Assert.fail("testPrependElement_literalMutationString230154_literalMutationNumber230424 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPrependText_literalMutationString26871_literalMutationString26996() throws Exception {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p>@</div>");
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   <p>Hello</p>@\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Element div = doc.getElementById("1");
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>@\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Element o_testPrependText_literalMutationString26871__5 = div.prependText(" Hello\nthere \u00a0  ");
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString26871__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString26871__5)).isBlock());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString26871__5)).hasText());
        Assert.assertEquals("<div id=\"1\">\n  Hello there &nbsp; \n <p>Hello</p>@\n</div>", ((Element) (o_testPrependText_literalMutationString26871__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString26871__5)).hasParent());
        String o_testPrependText_literalMutationString26871__6 = div.text();
        Assert.assertEquals("Hello there Hello@", o_testPrependText_literalMutationString26871__6);
        String o_testPrependText_literalMutationString26871__7 = TextUtil.stripNewlines(div.html());
        Assert.assertEquals("Hello there &nbsp; <p>Hello</p>@", o_testPrependText_literalMutationString26871__7);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n    Hello there &nbsp; \n   <p>Hello</p>@\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertEquals("<div id=\"1\">\n  Hello there &nbsp; \n <p>Hello</p>@\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString26871__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString26871__5)).isBlock());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString26871__5)).hasText());
        Assert.assertEquals("<div id=\"1\">\n  Hello there &nbsp; \n <p>Hello</p>@\n</div>", ((Element) (o_testPrependText_literalMutationString26871__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString26871__5)).hasParent());
        Assert.assertEquals("Hello there Hello@", o_testPrependText_literalMutationString26871__6);
    }

    @Test(timeout = 10000)
    public void elementIsNotASiblingOfItself_literalMutationNumber36152_literalMutationNumber36313_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            Element p2 = doc.select("p").get(1);
            String o_elementIsNotASiblingOfItself_literalMutationNumber36152__6 = p2.text();
            Elements els = p2.siblingElements();
            int o_elementIsNotASiblingOfItself_literalMutationNumber36152__9 = els.size();
            String o_elementIsNotASiblingOfItself_literalMutationNumber36152__10 = els.get(0).outerHtml();
            String o_elementIsNotASiblingOfItself_literalMutationNumber36152__12 = els.get(-1).outerHtml();
            org.junit.Assert.fail("elementIsNotASiblingOfItself_literalMutationNumber36152_literalMutationNumber36313 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testChildThrowsIndexOutOfBoundsOnMissing_literalMutationNumber259069_failAssert0_literalMutationNumber259314_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p></div>");
                Element div = doc.select("div").first();
                div.children().size();
                div.child(-1).text();
                {
                    div.child(0);
                }
                org.junit.Assert.fail("testChildThrowsIndexOutOfBoundsOnMissing_literalMutationNumber259069 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testChildThrowsIndexOutOfBoundsOnMissing_literalMutationNumber259069_failAssert0_literalMutationNumber259314 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void insertChildrenAsCopy_literalMutationNumber202324_failAssert0null208205_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
                Element div1 = doc.select("div").get(-1);
                Element div2 = doc.select("div").get(1);
                Elements ps = doc.select(null).clone();
                ps.first().text("One cloned");
                div2.insertChildren((-1), ps);
                div1.childNodeSize();
                div2.childNodeSize();
                TextUtil.stripNewlines(doc.body().html());
                org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber202324 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber202324_failAssert0null208205 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void insertChildrenAsCopy_literalMutationNumber202324_failAssert0null208205_failAssert0_add220854_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
                    Element div1 = doc.select("div").get(-1);
                    Element div2 = doc.select("div").get(1);
                    Elements ps = doc.select(null).clone();
                    ps.first().text("One cloned");
                    div2.insertChildren((-1), ps);
                    div1.childNodeSize();
                    div2.childNodeSize();
                    TextUtil.stripNewlines(doc.body().html());
                    TextUtil.stripNewlines(doc.body().html());
                    org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber202324 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber202324_failAssert0null208205 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber202324_failAssert0null208205_failAssert0_add220854 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testHashAndEqualsAndValue_literalMutationNumber485922_failAssert0_add501409_failAssert0() throws Exception {
        try {
            {
                String doc1 = "<div id=1><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>" + "<div id=2><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>";
                Jsoup.parse(doc1);
                Document doc = Jsoup.parse(doc1);
                Elements els = doc.select("p");
                els.size();
                Element e0 = els.get(-1);
                Element e1 = els.get(1);
                Element e2 = els.get(2);
                Element e3 = els.get(3);
                Element e4 = els.get(4);
                Element e5 = els.get(5);
                Element e6 = els.get(6);
                Element e7 = els.get(7);
                e0.hasSameValue(e1);
                e0.hasSameValue(e4);
                e0.hasSameValue(e5);
                e0.equals(e2);
                e0.hasSameValue(e2);
                e0.hasSameValue(e3);
                e0.hasSameValue(e6);
                e0.hasSameValue(e7);
                e0.hashCode();
                e0.hashCode();
                boolean boolean_378 = (e0.hashCode()) == (e2.hashCode());
                boolean boolean_379 = (e0.hashCode()) == (e3.hashCode());
                boolean boolean_380 = (e0.hashCode()) == (e6.hashCode());
                boolean boolean_381 = (e0.hashCode()) == (e7.hashCode());
                org.junit.Assert.fail("testHashAndEqualsAndValue_literalMutationNumber485922 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testHashAndEqualsAndValue_literalMutationNumber485922_failAssert0_add501409 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblingAfterClone_literalMutationString366505_literalMutationNumber367847_failAssert0_literalMutationNumber384632_failAssert0() throws Exception {
        try {
            {
                String html = "<!DOCTYPE html><html lang=\"en\"><head></head><body><div>Initial element</div></body></html>";
                String expectedText = "New element";
                String cloneExpect = "New element in clone";
                Document original = Jsoup.parse(html);
                Document clone = original.clone();
                Element originalElement = original.body().child(-1);
                Element o_testNextElementSiblingAfterClone_literalMutationString366505__12 = originalElement.after((("<div>" + expectedText) + "</div>"));
                Element originalNextElementSibling = originalElement.nextElementSibling();
                Element originalNextSibling = ((Element) (originalElement.nextSibling()));
                String o_testNextElementSiblingAfterClone_literalMutationString366505__17 = originalNextElementSibling.text();
                String o_testNextElementSiblingAfterClone_literalMutationString366505__18 = originalNextSibling.text();
                Element cloneElement = clone.body().child(1);
                Element o_testNextElementSiblingAfterClone_literalMutationString366505__22 = cloneElement.after((("<dv>" + cloneExpect) + "</div>"));
                Element cloneNextElementSibling = cloneElement.nextElementSibling();
                Element cloneNextSibling = ((Element) (cloneElement.nextSibling()));
                String o_testNextElementSiblingAfterClone_literalMutationString366505__27 = cloneNextElementSibling.text();
                String o_testNextElementSiblingAfterClone_literalMutationString366505__28 = cloneNextSibling.text();
                org.junit.Assert.fail("testNextElementSiblingAfterClone_literalMutationString366505_literalMutationNumber367847 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblingAfterClone_literalMutationString366505_literalMutationNumber367847_failAssert0_literalMutationNumber384632 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

