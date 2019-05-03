package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Assert;
import org.junit.Test;


public class AmplNodeTest {
    @Test(timeout = 10000)
    public void unwrap_literalMutationString27949_failAssert0_literalMutationNumber28701_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>");
                Element span = doc.select("span").first();
                Node twoText = span.childNode(-1);
                Node node = span.unwrap();
                TextUtil.stripNewlines(doc.body().html());
                boolean boolean_69 = node instanceof TextNode;
                ((TextNode) (node)).text();
                node.parent();
                doc.select("").first();
                org.junit.Assert.fail("unwrap_literalMutationString27949 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("unwrap_literalMutationString27949_failAssert0_literalMutationNumber28701 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_literalMutationNumber25607_failAssert0_add27310_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
                Element p2 = doc.select("p").get(1);
                p2.text();
                List<Node> nodes = p2.siblingNodes();
                nodes.size();
                nodes.get(-1).outerHtml();
                nodes.get(1).outerHtml();
                org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber25607 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber25607_failAssert0_add27310 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
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

