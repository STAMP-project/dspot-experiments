package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.org.jsoup.Jsoup;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests Nodes
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class NodeTest {
    @Test
    public void handlesBaseUri() {
        Tag tag = Tag.valueOf("a");
        Attributes attribs = new Attributes();
        attribs.put("relHref", "/foo");
        attribs.put("absHref", "http://bar/qux");
        Element noBase = new Element(tag, "", attribs);
        Assert.assertEquals("", noBase.absUrl("relHref"));// with no base, should NOT fallback to href attrib, whatever it is

        Assert.assertEquals("http://bar/qux", noBase.absUrl("absHref"));// no base but valid attrib, return attrib

        Element withBase = new Element(tag, "http://foo/", attribs);
        Assert.assertEquals("http://foo/foo", withBase.absUrl("relHref"));// construct abs from base + rel

        Assert.assertEquals("http://bar/qux", withBase.absUrl("absHref"));// href is abs, so returns that

        Assert.assertEquals("", withBase.absUrl("noval"));
        Element dodgyBase = new Element(tag, "wtf://no-such-protocol/", attribs);
        Assert.assertEquals("http://bar/qux", dodgyBase.absUrl("absHref"));// base fails, but href good, so get that

        Assert.assertEquals("", dodgyBase.absUrl("relHref"));// base fails, only rel href, so return nothing

    }

    @Test
    public void setBaseUriIsRecursive() {
        Document doc = Jsoup.parse("<div><p></p></div>");
        String baseUri = "https://jsoup.org";
        doc.setBaseUri(baseUri);
        Assert.assertEquals(baseUri, doc.baseUri());
        Assert.assertEquals(baseUri, doc.select("div").first().baseUri());
        Assert.assertEquals(baseUri, doc.select("p").first().baseUri());
    }

    @Test
    public void handlesAbsPrefix() {
        Document doc = Jsoup.parse("<a href=/foo>Hello</a>", "https://jsoup.org/");
        Element a = doc.select("a").first();
        Assert.assertEquals("/foo", a.attr("href"));
        Assert.assertEquals("https://jsoup.org/foo", a.attr("abs:href"));
        Assert.assertTrue(a.hasAttr("abs:href"));
    }

    @Test
    public void handlesAbsOnImage() {
        Document doc = Jsoup.parse("<p><img src=\"/rez/osi_logo.png\" /></p>", "https://jsoup.org/");
        Element img = doc.select("img").first();
        Assert.assertEquals("https://jsoup.org/rez/osi_logo.png", img.attr("abs:src"));
        Assert.assertEquals(img.absUrl("src"), img.attr("abs:src"));
    }

    @Test
    public void handlesAbsPrefixOnHasAttr() {
        // 1: no abs url; 2: has abs url
        Document doc = Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org/'>Two</a>");
        Element one = doc.select("#1").first();
        Element two = doc.select("#2").first();
        Assert.assertFalse(one.hasAttr("abs:href"));
        Assert.assertTrue(one.hasAttr("href"));
        Assert.assertEquals("", one.absUrl("href"));
        Assert.assertTrue(two.hasAttr("abs:href"));
        Assert.assertTrue(two.hasAttr("href"));
        Assert.assertEquals("https://jsoup.org/", two.absUrl("href"));
    }

    @Test
    public void literalAbsPrefix() {
        // if there is a literal attribute "abs:xxx", don't try and make absolute.
        Document doc = Jsoup.parse("<a abs:href='odd'>One</a>");
        Element el = doc.select("a").first();
        Assert.assertTrue(el.hasAttr("abs:href"));
        Assert.assertEquals("odd", el.attr("abs:href"));
    }

    @Test
    public void handleAbsOnFileUris() {
        Document doc = Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file:/etc/");
        Element one = doc.select("a").first();
        Assert.assertEquals("file:/etc/password", one.absUrl("href"));
        Element two = doc.select("a").get(1);
        Assert.assertEquals("file:/var/log/messages", two.absUrl("href"));
    }

    @Test
    public void handleAbsOnLocalhostFileUris() {
        Document doc = Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file://localhost/etc/");
        Element one = doc.select("a").first();
        Assert.assertEquals("file://localhost/etc/password", one.absUrl("href"));
    }

    @Test
    public void handlesAbsOnProtocolessAbsoluteUris() {
        Document doc1 = Jsoup.parse("<a href='//example.net/foo'>One</a>", "http://example.com/");
        Document doc2 = Jsoup.parse("<a href='//example.net/foo'>One</a>", "https://example.com/");
        Element one = doc1.select("a").first();
        Element two = doc2.select("a").first();
        Assert.assertEquals("http://example.net/foo", one.absUrl("href"));
        Assert.assertEquals("https://example.net/foo", two.absUrl("href"));
        Document doc3 = Jsoup.parse("<img src=//www.google.com/images/errors/logo_sm.gif alt=Google>", "https://google.com");
        Assert.assertEquals("https://www.google.com/images/errors/logo_sm.gif", doc3.select("img").attr("abs:src"));
    }

    /* Test for an issue with Java's abs URL handler. */
    @Test
    public void absHandlesRelativeQuery() {
        Document doc = Jsoup.parse("<a href='?foo'>One</a> <a href='bar.html?foo'>Two</a>", "https://jsoup.org/path/file?bar");
        Element a1 = doc.select("a").first();
        Assert.assertEquals("https://jsoup.org/path/file?foo", a1.absUrl("href"));
        Element a2 = doc.select("a").get(1);
        Assert.assertEquals("https://jsoup.org/path/bar.html?foo", a2.absUrl("href"));
    }

    @Test
    public void absHandlesDotFromIndex() {
        Document doc = Jsoup.parse("<a href='./one/two.html'>One</a>", "http://example.com");
        Element a1 = doc.select("a").first();
        Assert.assertEquals("http://example.com/one/two.html", a1.absUrl("href"));
    }

    @Test
    public void testRemove() {
        Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
        Element p = doc.select("p").first();
        p.childNode(0).remove();
        Assert.assertEquals("two three", p.text());
        Assert.assertEquals("<span>two</span> three", TextUtil.stripNewlines(p.html()));
    }

    @Test
    public void testReplace() {
        Document doc = Jsoup.parse("<p>One <span>two</span> three</p>");
        Element p = doc.select("p").first();
        Element insert = doc.createElement("em").text("foo");
        p.childNode(1).replaceWith(insert);
        Assert.assertEquals("One <em>foo</em> three", p.html());
    }

    @Test
    public void ownerDocument() {
        Document doc = Jsoup.parse("<p>Hello");
        Element p = doc.select("p").first();
        Assert.assertTrue(((p.ownerDocument()) == doc));
        Assert.assertTrue(((doc.ownerDocument()) == doc));
        Assert.assertNull(doc.parent());
    }

    @Test
    public void root() {
        Document doc = Jsoup.parse("<div><p>Hello");
        Element p = doc.select("p").first();
        Node root = p.root();
        Assert.assertTrue((doc == root));
        Assert.assertNull(root.parent());
        Assert.assertTrue(((doc.root()) == doc));
        Assert.assertTrue(((doc.root()) == (doc.ownerDocument())));
        Element standAlone = new Element(Tag.valueOf("p"), "");
        Assert.assertTrue(((standAlone.parent()) == null));
        Assert.assertTrue(((standAlone.root()) == standAlone));
        Assert.assertTrue(((standAlone.ownerDocument()) == null));
    }

    @Test
    public void before() {
        Document doc = Jsoup.parse("<p>One <b>two</b> three</p>");
        Element newNode = new Element(Tag.valueOf("em"), "");
        newNode.appendText("four");
        doc.select("b").first().before(newNode);
        Assert.assertEquals("<p>One <em>four</em><b>two</b> three</p>", doc.body().html());
        doc.select("b").first().before("<i>five</i>");
        Assert.assertEquals("<p>One <em>four</em><i>five</i><b>two</b> three</p>", doc.body().html());
    }

    @Test
    public void after() {
        Document doc = Jsoup.parse("<p>One <b>two</b> three</p>");
        Element newNode = new Element(Tag.valueOf("em"), "");
        newNode.appendText("four");
        doc.select("b").first().after(newNode);
        Assert.assertEquals("<p>One <b>two</b><em>four</em> three</p>", doc.body().html());
        doc.select("b").first().after("<i>five</i>");
        Assert.assertEquals("<p>One <b>two</b><i>five</i><em>four</em> three</p>", doc.body().html());
    }

    @Test
    public void unwrap() {
        Document doc = Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>");
        Element span = doc.select("span").first();
        Node twoText = span.childNode(0);
        Node node = span.unwrap();
        Assert.assertEquals("<div>One Two <b>Three</b> Four</div>", TextUtil.stripNewlines(doc.body().html()));
        Assert.assertTrue((node instanceof TextNode));
        Assert.assertEquals("Two ", text());
        Assert.assertEquals(node, twoText);
        Assert.assertEquals(node.parent(), doc.select("div").first());
    }

    @Test
    public void unwrapNoChildren() {
        Document doc = Jsoup.parse("<div>One <span></span> Two</div>");
        Element span = doc.select("span").first();
        Node node = span.unwrap();
        Assert.assertEquals("<div>One  Two</div>", TextUtil.stripNewlines(doc.body().html()));
        Assert.assertTrue((node == null));
    }

    @Test
    public void traverse() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div><div>There</div>");
        final StringBuilder accum = new StringBuilder();
        doc.select("div").first().traverse(new org.jsoup.select.NodeVisitor() {
            public void head(Node node, int depth) {
                accum.append((("<" + (node.nodeName())) + ">"));
            }

            public void tail(Node node, int depth) {
                accum.append((("</" + (node.nodeName())) + ">"));
            }
        });
        Assert.assertEquals("<div><p><#text></#text></p></div>", accum.toString());
    }

    @Test
    public void orphanNodeReturnsNullForSiblingElements() {
        Node node = new Element(Tag.valueOf("p"), "");
        Element el = new Element(Tag.valueOf("p"), "");
        Assert.assertEquals(0, node.siblingIndex());
        Assert.assertEquals(0, node.siblingNodes().size());
        Assert.assertNull(node.previousSibling());
        Assert.assertNull(node.nextSibling());
        Assert.assertEquals(0, el.siblingElements().size());
        Assert.assertNull(el.previousElementSibling());
        Assert.assertNull(el.nextElementSibling());
    }

    @Test
    public void nodeIsNotASiblingOfItself() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
        Element p2 = doc.select("p").get(1);
        Assert.assertEquals("Two", p2.text());
        List<Node> nodes = p2.siblingNodes();
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals("<p>One</p>", nodes.get(0).outerHtml());
        Assert.assertEquals("<p>Three</p>", nodes.get(1).outerHtml());
    }

    @Test
    public void childNodesCopy() {
        Document doc = Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
        Element div1 = doc.select("#1").first();
        Element div2 = doc.select("#2").first();
        List<Node> divChildren = div1.childNodesCopy();
        Assert.assertEquals(5, divChildren.size());
        TextNode tn1 = ((TextNode) (div1.childNode(0)));
        TextNode tn2 = ((TextNode) (divChildren.get(0)));
        tn2.text("Text 1 updated");
        Assert.assertEquals("Text 1 ", tn1.text());
        div2.insertChildren((-1), divChildren);
        Assert.assertEquals(("<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>"), TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void supportsClone() {
        Document doc = org.jsoup.Jsoup.parse("<div class=foo>Text</div>");
        Element el = doc.select("div").first();
        Assert.assertTrue(el.hasClass("foo"));
        Element elClone = doc.clone().select("div").first();
        Assert.assertTrue(elClone.hasClass("foo"));
        Assert.assertTrue(elClone.text().equals("Text"));
        el.removeClass("foo");
        el.text("None");
        Assert.assertFalse(el.hasClass("foo"));
        Assert.assertTrue(elClone.hasClass("foo"));
        Assert.assertTrue(el.text().equals("None"));
        Assert.assertTrue(elClone.text().equals("Text"));
    }

    @Test
    public void changingAttributeValueShouldReplaceExistingAttributeCaseInsensitive() {
        Document document = Jsoup.parse("<INPUT id=\"foo\" NAME=\"foo\" VALUE=\"\">");
        Element inputElement = document.select("#foo").first();
        inputElement.attr("value", "bar");
        Assert.assertEquals(singletonAttributes("value", "bar"), getAttributesCaseInsensitive(inputElement, "value"));
    }
}

