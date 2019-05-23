package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Assert;
import org.junit.Test;


public class AmplNodeTest {
    @Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_literalMutationNumber51785_failAssert0_add53424_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
                Element p2 = doc.select("p").get(1);
                p2.text();
                List<Node> nodes = p2.siblingNodes();
                nodes.size();
                nodes.get(-1).outerHtml();
                nodes.get(1);
                nodes.get(1).outerHtml();
                org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber51785 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber51785_failAssert0_add53424 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_literalMutationNumber51785_failAssert0_add53415_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
                Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
                Element p2 = doc.select("p").get(1);
                p2.text();
                List<Node> nodes = p2.siblingNodes();
                nodes.size();
                nodes.get(-1).outerHtml();
                nodes.get(1).outerHtml();
                org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber51785 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber51785_failAssert0_add53415 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void childNodesCopy_literalMutationNumber36760_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
            Element div1 = doc.select("#1").first();
            Element div2 = doc.select("#2").first();
            List<Node> divChildren = div1.childNodesCopy();
            divChildren.size();
            TextNode tn1 = ((TextNode) (div1.childNode(0)));
            TextNode tn2 = ((TextNode) (divChildren.get(-1)));
            tn2.text("Text 1 updated");
            tn1.text();
            div2.insertChildren((-1), divChildren);
            String String_16 = "<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>";
            TextUtil.stripNewlines(doc.body().html());
            org.junit.Assert.fail("childNodesCopy_literalMutationNumber36760 should have thrown ArrayIndexOutOfBoundsException");
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

