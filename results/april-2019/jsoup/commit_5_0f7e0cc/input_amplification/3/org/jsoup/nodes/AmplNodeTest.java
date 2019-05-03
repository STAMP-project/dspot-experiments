package org.jsoup.nodes;


import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Assert;
import org.junit.Test;


public class AmplNodeTest {
    @Test(timeout = 10000)
    public void testRemove_add29596_literalMutationNumber29689_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
            Element p = doc.select("p").first();
            Node o_testRemove_add29596__6 = p.childNode(0);
            p.childNode(-1).remove();
            String o_testRemove_add29596__9 = p.text();
            String o_testRemove_add29596__10 = TextUtil.stripNewlines(p.html());
            org.junit.Assert.fail("testRemove_add29596_literalMutationNumber29689 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testRemove_literalMutationNumber29589_failAssert0_add30580_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse("<p>One <span>two</span> three</p>");
                Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
                Element p = doc.select("p").first();
                p.childNode(-1).remove();
                p.text();
                TextUtil.stripNewlines(p.html());
                org.junit.Assert.fail("testRemove_literalMutationNumber29589 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testRemove_literalMutationNumber29589_failAssert0_add30580 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unwrap_literalMutationNumber70835_failAssert0_add73404_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>");
                doc.select("span");
                Element span = doc.select("span").first();
                Node twoText = span.childNode(-1);
                Node node = span.unwrap();
                TextUtil.stripNewlines(doc.body().html());
                boolean boolean_92 = node instanceof TextNode;
                ((TextNode) (node)).text();
                node.parent();
                doc.select("div").first();
                org.junit.Assert.fail("unwrap_literalMutationNumber70835 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("unwrap_literalMutationNumber70835_failAssert0_add73404 should have thrown ArrayIndexOutOfBoundsException");
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

