package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Assert;
import org.junit.Test;


public class AmplNodeTest {
    @Test(timeout = 10000)
    public void testRemove_literalMutationNumber7371_failAssert0_literalMutationNumber7747_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
                Element p = doc.select("p").first();
                p.childNode(-2).remove();
                p.text();
                TextUtil.stripNewlines(p.html());
                org.junit.Assert.fail("testRemove_literalMutationNumber7371 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testRemove_literalMutationNumber7371_failAssert0_literalMutationNumber7747 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testRemove_literalMutationNumber7371_failAssert0_add8336_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
                Element p = doc.select("p").first();
                p.childNode(-1).remove();
                p.text();
                p.html();
                TextUtil.stripNewlines(p.html());
                org.junit.Assert.fail("testRemove_literalMutationNumber7371 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testRemove_literalMutationNumber7371_failAssert0_add8336 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testRemove_literalMutationNumber7371_failAssert0_add8329_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse("<p>One <span>two</span> three</p>");
                Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
                Element p = doc.select("p").first();
                p.childNode(-1).remove();
                p.text();
                TextUtil.stripNewlines(p.html());
                org.junit.Assert.fail("testRemove_literalMutationNumber7371 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testRemove_literalMutationNumber7371_failAssert0_add8329 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_add74142_literalMutationNumber74254_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            Element p2 = doc.select("p").get(1);
            String o_nodeIsNotASiblingOfItself_add74142__6 = p2.text();
            List<Node> nodes = p2.siblingNodes();
            int o_nodeIsNotASiblingOfItself_add74142__9 = nodes.size();
            Node o_nodeIsNotASiblingOfItself_add74142__10 = nodes.get(0);
            String o_nodeIsNotASiblingOfItself_add74142__11 = nodes.get(-1).outerHtml();
            String o_nodeIsNotASiblingOfItself_add74142__13 = nodes.get(1).outerHtml();
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_add74142_literalMutationNumber74254 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void childNodesCopy_add10753_literalMutationNumber11225_failAssert0() throws Exception {
        try {
            Document o_childNodesCopy_add10753__1 = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
            Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
            Element div1 = doc.select("#1").first();
            Element div2 = doc.select("#2").first();
            List<Node> divChildren = div1.childNodesCopy();
            int o_childNodesCopy_add10753__12 = divChildren.size();
            TextNode tn1 = ((TextNode) (div1.childNode(-1)));
            TextNode tn2 = ((TextNode) (divChildren.get(0)));
            TextNode o_childNodesCopy_add10753__17 = tn2.text("Text 1 updated");
            String o_childNodesCopy_add10753__18 = tn1.text();
            Element o_childNodesCopy_add10753__19 = div2.insertChildren((-1), divChildren);
            String String_19 = "<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>";
            String o_childNodesCopy_add10753__22 = TextUtil.stripNewlines(doc.body().html());
            org.junit.Assert.fail("childNodesCopy_add10753_literalMutationNumber11225 should have thrown ArrayIndexOutOfBoundsException");
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

