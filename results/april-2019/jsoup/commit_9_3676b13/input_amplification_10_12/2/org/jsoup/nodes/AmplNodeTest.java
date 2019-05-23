package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplNodeTest {
    @Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_add23853_literalMutationNumber24360_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            Element p2 = doc.select("p").get(1);
            String o_nodeIsNotASiblingOfItself_add23853__6 = p2.text();
            List<Node> nodes = p2.siblingNodes();
            int o_nodeIsNotASiblingOfItself_add23853__9 = nodes.size();
            nodes.get(-1).outerHtml();
            nodes.get(1).outerHtml();
            nodes.get(1).outerHtml();
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_add23853_literalMutationNumber24360 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_literalMutationNumber23838_failAssert0_literalMutationNumber24591_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
                Element p2 = doc.select("p").get(2);
                p2.text();
                List<Node> nodes = p2.siblingNodes();
                nodes.size();
                nodes.get(-1).outerHtml();
                nodes.get(1).outerHtml();
                org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber23838 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_literalMutationNumber23838_failAssert0_literalMutationNumber24591 should have thrown ArrayIndexOutOfBoundsException");
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

