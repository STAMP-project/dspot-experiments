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
    public void testSetText_literalMutationString78342() throws Exception {
        String h = "<div id=1>Hello <p>there <b>now</b></p>M/div>";
        Assert.assertEquals("<div id=1>Hello <p>there <b>now</b></p>M/div>", h);
        Document doc = Jsoup.parse(h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   Hello \n   <p>there <b>now</b></p>M/div&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        String o_testSetText_literalMutationString78342__4 = doc.text();
        Assert.assertEquals("Hello there nowM/div>", o_testSetText_literalMutationString78342__4);
        String o_testSetText_literalMutationString78342__5 = doc.select("p").get(0).text();
        Assert.assertEquals("there now", o_testSetText_literalMutationString78342__5);
        Element div = doc.getElementById("1").text("Gone");
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n Gone\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        String o_testSetText_literalMutationString78342__11 = div.text();
        Assert.assertEquals("Gone", o_testSetText_literalMutationString78342__11);
        int o_testSetText_literalMutationString78342__12 = doc.select("p").size();
        Assert.assertEquals(0, ((int) (o_testSetText_literalMutationString78342__12)));
        Assert.assertEquals("<div id=1>Hello <p>there <b>now</b></p>M/div>", h);
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   Gone\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("Hello there nowM/div>", o_testSetText_literalMutationString78342__4);
        Assert.assertEquals("there now", o_testSetText_literalMutationString78342__5);
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div id=\"1\">\n Gone\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertEquals("Gone", o_testSetText_literalMutationString78342__11);
    }

    @Test(timeout = 10000)
    public void testAddBooleanAttribute_add148756() throws Exception {
        Element div = new Element(Tag.valueOf("div"), "");
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Element o_testAddBooleanAttribute_add148756__4 = div.attr("true", true);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add148756__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add148756__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add148756__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__4)).hasParent());
        Element o_testAddBooleanAttribute_add148756__5 = div.attr("false", "value");
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add148756__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add148756__5)).isBlock());
        Assert.assertEquals("<div true false=\"value\"></div>", ((Element) (o_testAddBooleanAttribute_add148756__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__5)).hasParent());
        Element o_testAddBooleanAttribute_add148756__6 = div.attr("false", false);
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add148756__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add148756__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add148756__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__6)).hasParent());
        boolean o_testAddBooleanAttribute_add148756__7 = div.hasAttr("true");
        Assert.assertTrue(o_testAddBooleanAttribute_add148756__7);
        String o_testAddBooleanAttribute_add148756__8 = div.attr("true");
        Assert.assertEquals("", o_testAddBooleanAttribute_add148756__8);
        List<Attribute> attributes = div.attributes().asList();
        int o_testAddBooleanAttribute_add148756__12 = attributes.size();
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add148756__12)));
        Attribute o_testAddBooleanAttribute_add148756__13 = attributes.get(0);
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).getKey());
        boolean boolean_723 = (attributes.get(0)) instanceof BooleanAttribute;
        boolean o_testAddBooleanAttribute_add148756__16 = div.hasAttr("false");
        Assert.assertFalse(o_testAddBooleanAttribute_add148756__16);
        String o_testAddBooleanAttribute_add148756__17 = div.outerHtml();
        Assert.assertEquals("<div true></div>", o_testAddBooleanAttribute_add148756__17);
        Assert.assertFalse(((Element) (div)).hasText());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (div)).toString());
        Assert.assertFalse(((Element) (div)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__4)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add148756__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add148756__4)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add148756__4)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__4)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__5)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add148756__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add148756__5)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add148756__5)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__5)).hasParent());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__6)).hasText());
        Assert.assertFalse(((Collection) (((Element) (o_testAddBooleanAttribute_add148756__6)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testAddBooleanAttribute_add148756__6)).isBlock());
        Assert.assertEquals("<div true></div>", ((Element) (o_testAddBooleanAttribute_add148756__6)).toString());
        Assert.assertFalse(((Element) (o_testAddBooleanAttribute_add148756__6)).hasParent());
        Assert.assertTrue(o_testAddBooleanAttribute_add148756__7);
        Assert.assertEquals("", o_testAddBooleanAttribute_add148756__8);
        Assert.assertEquals(1, ((int) (o_testAddBooleanAttribute_add148756__12)));
        Assert.assertEquals("true=\"\"", ((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).toString());
        Assert.assertEquals(110640178, ((int) (((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).getValue());
        Assert.assertEquals("true", ((BooleanAttribute) (o_testAddBooleanAttribute_add148756__13)).getKey());
        Assert.assertFalse(o_testAddBooleanAttribute_add148756__16);
    }

    @Test(timeout = 10000)
    public void testPrependText_literalMutationString5515_literalMutationString5703() throws Exception {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p>></div>");
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Element div = doc.getElementById("1");
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Element o_testPrependText_literalMutationString5515__5 = div.prependText("");
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString5515__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString5515__5)).isBlock());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString5515__5)).hasText());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString5515__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString5515__5)).hasParent());
        String o_testPrependText_literalMutationString5515__6 = div.text();
        Assert.assertEquals("Hello>", o_testPrependText_literalMutationString5515__6);
        String o_testPrependText_literalMutationString5515__7 = TextUtil.stripNewlines(div.html());
        Assert.assertEquals("<p>Hello</p>&gt;", o_testPrependText_literalMutationString5515__7);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div id=\"1\">\n   <p>Hello</p>&gt;\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (div)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (div)).isBlock());
        Assert.assertTrue(((Element) (div)).hasText());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>&gt;\n</div>", ((Element) (div)).toString());
        Assert.assertTrue(((Element) (div)).hasParent());
        Assert.assertFalse(((Collection) (((Element) (o_testPrependText_literalMutationString5515__5)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString5515__5)).isBlock());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString5515__5)).hasText());
        Assert.assertEquals("<div id=\"1\">\n <p>Hello</p>&gt;\n</div>", ((Element) (o_testPrependText_literalMutationString5515__5)).toString());
        Assert.assertTrue(((Element) (o_testPrependText_literalMutationString5515__5)).hasParent());
        Assert.assertEquals("Hello>", o_testPrependText_literalMutationString5515__6);
    }

    @Test(timeout = 10000)
    public void insertChildrenAsCopy_literalMutationNumber56486_failAssert0_literalMutationString58700_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
                Element div1 = doc.select("div").get(-1);
                Element div2 = doc.select(".iv").get(1);
                Elements ps = doc.select("p").clone();
                ps.first().text("One cloned");
                div2.insertChildren((-1), ps);
                div1.childNodeSize();
                div2.childNodeSize();
                TextUtil.stripNewlines(doc.body().html());
                org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber56486 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber56486_failAssert0_literalMutationString58700 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

