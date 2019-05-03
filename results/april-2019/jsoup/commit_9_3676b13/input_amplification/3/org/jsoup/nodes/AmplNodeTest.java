package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Assert;
import org.junit.Test;


public class AmplNodeTest {
    @Test(timeout = 10000)
    public void testRemove_add20_literalMutationNumber195_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
            Element p = doc.select("p").first();
            p.childNode(-1).remove();
            String o_testRemove_add20__8 = p.text();
            String o_testRemove_add20__9 = TextUtil.stripNewlines(p.html());
            String o_testRemove_add20__11 = TextUtil.stripNewlines(p.html());
            org.junit.Assert.fail("testRemove_add20_literalMutationNumber195 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unwrap_literalMutationNumber93426_failAssert0_add95710_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>");
                Element span = doc.select("span").first();
                Node twoText = span.childNode(-1);
                Node node = span.unwrap();
                TextUtil.stripNewlines(doc.body().html());
                boolean boolean_55 = node instanceof TextNode;
                ((TextNode) (node)).text();
                node.parent();
                node.parent();
                doc.select("div").first();
                org.junit.Assert.fail("unwrap_literalMutationNumber93426 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("unwrap_literalMutationNumber93426_failAssert0_add95710 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void childNodesCopy_literalMutationNumber58219_failAssert0_add63497_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
                Element div1 = doc.select("#1").first();
                Element div2 = doc.select("#2").first();
                List<Node> divChildren = div1.childNodesCopy();
                divChildren.size();
                div1.childNode(-1);
                TextNode tn1 = ((TextNode) (div1.childNode(-1)));
                TextNode tn2 = ((TextNode) (divChildren.get(0)));
                tn2.text("Text 1 updated");
                tn1.text();
                div2.insertChildren((-1), divChildren);
                String String_27 = "<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>";
                TextUtil.stripNewlines(doc.body().html());
                org.junit.Assert.fail("childNodesCopy_literalMutationNumber58219 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("childNodesCopy_literalMutationNumber58219_failAssert0_add63497 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void childNodesCopy_literalMutationNumber58219_failAssert0_add63497_failAssert0_add79864_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
                    Element div1 = doc.select("#1").first();
                    doc.select("#2");
                    Element div2 = doc.select("#2").first();
                    List<Node> divChildren = div1.childNodesCopy();
                    divChildren.size();
                    div1.childNode(-1);
                    TextNode tn1 = ((TextNode) (div1.childNode(-1)));
                    TextNode tn2 = ((TextNode) (divChildren.get(0)));
                    tn2.text("Text 1 updated");
                    tn1.text();
                    div2.insertChildren((-1), divChildren);
                    String String_27 = "<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>";
                    TextUtil.stripNewlines(doc.body().html());
                    org.junit.Assert.fail("childNodesCopy_literalMutationNumber58219 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("childNodesCopy_literalMutationNumber58219_failAssert0_add63497 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("childNodesCopy_literalMutationNumber58219_failAssert0_add63497_failAssert0_add79864 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void childNodesCopy_add58244_add62203_literalMutationNumber65323_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
            Element div1 = doc.select("#1").first();
            Element div2 = doc.select("#2").first();
            List<Node> divChildren = div1.childNodesCopy();
            int o_childNodesCopy_add58244__11 = divChildren.size();
            TextNode tn1 = ((TextNode) (div1.childNode(0)));
            Node o_childNodesCopy_add58244__14 = divChildren.get(0);
            TextNode tn2 = ((TextNode) (divChildren.get(-1)));
            TextNode o_childNodesCopy_add58244__17 = tn2.text("Text 1 updated");
            String o_childNodesCopy_add58244__18 = tn1.text();
            Element o_childNodesCopy_add58244__19 = div2.insertChildren((-1), divChildren);
            String String_40 = "<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>";
            String o_childNodesCopy_add58244__22 = TextUtil.stripNewlines(doc.body().html());
            tn1.hasParent();
            org.junit.Assert.fail("childNodesCopy_add58244_add62203_literalMutationNumber65323 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    private Attributes getAttributesCaseInsensitive(Element element, String attributeName) {
        Attributes matches = new Attributes();
        for (Attribute attribute : element.attributes()) {
            if (attribute.getKey().equalsIgnoreCase(attributeName)) {
                matches.put(attribute);
            }
        }
        return matches;
    }

    private Attributes singletonAttributes(String key, String value) {
        Attributes attributes = new Attributes();
        attributes.put(key, value);
        return attributes;
    }
}

