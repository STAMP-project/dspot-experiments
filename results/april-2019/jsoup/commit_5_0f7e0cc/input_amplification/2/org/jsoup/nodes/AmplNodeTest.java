package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Assert;
import org.junit.Test;


public class AmplNodeTest {
    @Test(timeout = 10000)
    public void unwrap_literalMutationNumber15810_failAssert0_literalMutationString16396_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>");
                Element span = doc.select("span").first();
                Node twoText = span.childNode(-1);
                Node node = span.unwrap();
                TextUtil.stripNewlines(doc.body().html());
                boolean boolean_64 = node instanceof TextNode;
                ((TextNode) (node)).text();
                node.parent();
                doc.select("d-iv").first();
                org.junit.Assert.fail("unwrap_literalMutationNumber15810 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("unwrap_literalMutationNumber15810_failAssert0_literalMutationString16396 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void childNodesCopy_literalMutationNumber9821_failAssert0null15512_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
                Element div1 = doc.select("#1").first();
                Element div2 = doc.select("#2").first();
                List<Node> divChildren = div1.childNodesCopy();
                divChildren.size();
                TextNode tn1 = ((TextNode) (div1.childNode(0)));
                TextNode tn2 = ((TextNode) (divChildren.get(-1)));
                tn2.text(null);
                tn1.text();
                div2.insertChildren((-1), divChildren);
                String String_33 = "<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>";
                TextUtil.stripNewlines(doc.body().html());
                org.junit.Assert.fail("childNodesCopy_literalMutationNumber9821 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("childNodesCopy_literalMutationNumber9821_failAssert0null15512 should have thrown ArrayIndexOutOfBoundsException");
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

