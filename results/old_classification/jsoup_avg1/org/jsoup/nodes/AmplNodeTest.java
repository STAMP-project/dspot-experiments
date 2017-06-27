

package org.jsoup.nodes;


/**
 * Tests Nodes
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class AmplNodeTest {
    @org.junit.Test
    public void handlesBaseUri() {
        org.jsoup.parser.Tag tag = org.jsoup.parser.Tag.valueOf("a");
        org.jsoup.nodes.Attributes attribs = new org.jsoup.nodes.Attributes();
        attribs.put("relHref", "/foo");
        attribs.put("absHref", "http://bar/qux");
        org.jsoup.nodes.Element noBase = new org.jsoup.nodes.Element(tag, "", attribs);
        org.junit.Assert.assertEquals("", noBase.absUrl("relHref"));// with no base, should NOT fallback to href attrib, whatever it is
        
        org.junit.Assert.assertEquals("http://bar/qux", noBase.absUrl("absHref"));// no base but valid attrib, return attrib
        
        org.jsoup.nodes.Element withBase = new org.jsoup.nodes.Element(tag, "http://foo/", attribs);
        org.junit.Assert.assertEquals("http://foo/foo", withBase.absUrl("relHref"));// construct abs from base + rel
        
        org.junit.Assert.assertEquals("http://bar/qux", withBase.absUrl("absHref"));// href is abs, so returns that
        
        org.junit.Assert.assertEquals("", withBase.absUrl("noval"));
        org.jsoup.nodes.Element dodgyBase = new org.jsoup.nodes.Element(tag, "wtf://no-such-protocol/", attribs);
        org.junit.Assert.assertEquals("http://bar/qux", dodgyBase.absUrl("absHref"));// base fails, but href good, so get that
        
        org.junit.Assert.assertEquals("", dodgyBase.absUrl("relHref"));// base fails, only rel href, so return nothing
        
    }

    @org.junit.Test
    public void setBaseUriIsRecursive() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p></p></div>");
        java.lang.String baseUri = "https://jsoup.org";
        doc.setBaseUri(baseUri);
        org.junit.Assert.assertEquals(baseUri, doc.baseUri());
        org.junit.Assert.assertEquals(baseUri, doc.select("div").first().baseUri());
        org.junit.Assert.assertEquals(baseUri, doc.select("p").first().baseUri());
    }

    @org.junit.Test
    public void handlesAbsPrefix() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href=/foo>Hello</a>", "https://jsoup.org/");
        org.jsoup.nodes.Element a = doc.select("a").first();
        org.junit.Assert.assertEquals("/foo", a.attr("href"));
        org.junit.Assert.assertEquals("https://jsoup.org/foo", a.attr("abs:href"));
        org.junit.Assert.assertTrue(a.hasAttr("abs:href"));
    }

    @org.junit.Test
    public void handlesAbsOnImage() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p><img src=\"/rez/osi_logo.png\" /></p>", "https://jsoup.org/");
        org.jsoup.nodes.Element img = doc.select("img").first();
        org.junit.Assert.assertEquals("https://jsoup.org/rez/osi_logo.png", img.attr("abs:src"));
        org.junit.Assert.assertEquals(img.absUrl("src"), img.attr("abs:src"));
    }

    @org.junit.Test
    public void handlesAbsPrefixOnHasAttr() {
        // 1: no abs url; 2: has abs url
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org/'>Two</a>");
        org.jsoup.nodes.Element one = doc.select("#1").first();
        org.jsoup.nodes.Element two = doc.select("#2").first();
        org.junit.Assert.assertFalse(one.hasAttr("abs:href"));
        org.junit.Assert.assertTrue(one.hasAttr("href"));
        org.junit.Assert.assertEquals("", one.absUrl("href"));
        org.junit.Assert.assertTrue(two.hasAttr("abs:href"));
        org.junit.Assert.assertTrue(two.hasAttr("href"));
        org.junit.Assert.assertEquals("https://jsoup.org/", two.absUrl("href"));
    }

    @org.junit.Test
    public void literalAbsPrefix() {
        // if there is a literal attribute "abs:xxx", don't try and make absolute.
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a abs:href='odd'>One</a>");
        org.jsoup.nodes.Element el = doc.select("a").first();
        org.junit.Assert.assertTrue(el.hasAttr("abs:href"));
        org.junit.Assert.assertEquals("odd", el.attr("abs:href"));
    }

    @org.junit.Test
    public void handleAbsOnFileUris() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file:/etc/");
        org.jsoup.nodes.Element one = doc.select("a").first();
        org.junit.Assert.assertEquals("file:/etc/password", one.absUrl("href"));
        org.jsoup.nodes.Element two = doc.select("a").get(1);
        org.junit.Assert.assertEquals("file:/var/log/messages", two.absUrl("href"));
    }

    @org.junit.Test
    public void handleAbsOnLocalhostFileUris() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file://localhost/etc/");
        org.jsoup.nodes.Element one = doc.select("a").first();
        org.junit.Assert.assertEquals("file://localhost/etc/password", one.absUrl("href"));
    }

    @org.junit.Test
    public void handlesAbsOnProtocolessAbsoluteUris() {
        org.jsoup.nodes.Document doc1 = org.jsoup.Jsoup.parse("<a href='//example.net/foo'>One</a>", "http://example.com/");
        org.jsoup.nodes.Document doc2 = org.jsoup.Jsoup.parse("<a href='//example.net/foo'>One</a>", "https://example.com/");
        org.jsoup.nodes.Element one = doc1.select("a").first();
        org.jsoup.nodes.Element two = doc2.select("a").first();
        org.junit.Assert.assertEquals("http://example.net/foo", one.absUrl("href"));
        org.junit.Assert.assertEquals("https://example.net/foo", two.absUrl("href"));
        org.jsoup.nodes.Document doc3 = org.jsoup.Jsoup.parse("<img src=//www.google.com/images/errors/logo_sm.gif alt=Google>", "https://google.com");
        org.junit.Assert.assertEquals("https://www.google.com/images/errors/logo_sm.gif", doc3.select("img").attr("abs:src"));
    }

    /* Test for an issue with Java's abs URL handler. */
    @org.junit.Test
    public void absHandlesRelativeQuery() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='?foo'>One</a> <a href='bar.html?foo'>Two</a>", "https://jsoup.org/path/file?bar");
        org.jsoup.nodes.Element a1 = doc.select("a").first();
        org.junit.Assert.assertEquals("https://jsoup.org/path/file?foo", a1.absUrl("href"));
        org.jsoup.nodes.Element a2 = doc.select("a").get(1);
        org.junit.Assert.assertEquals("https://jsoup.org/path/bar.html?foo", a2.absUrl("href"));
    }

    @org.junit.Test
    public void absHandlesDotFromIndex() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='./one/two.html'>One</a>", "http://example.com");
        org.jsoup.nodes.Element a1 = doc.select("a").first();
        org.junit.Assert.assertEquals("http://example.com/one/two.html", a1.absUrl("href"));
    }

    @org.junit.Test
    public void testRemove() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
        org.jsoup.nodes.Element p = doc.select("p").first();
        p.childNode(0).remove();
        org.junit.Assert.assertEquals("two three", p.text());
        org.junit.Assert.assertEquals("<span>two</span> three", org.jsoup.TextUtil.stripNewlines(p.html()));
    }

    @org.junit.Test
    public void testReplace() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
        org.jsoup.nodes.Element p = doc.select("p").first();
        org.jsoup.nodes.Element insert = doc.createElement("em").text("foo");
        p.childNode(1).replaceWith(insert);
        org.junit.Assert.assertEquals("One <em>foo</em> three", p.html());
    }

    @org.junit.Test
    public void ownerDocument() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
        org.jsoup.nodes.Element p = doc.select("p").first();
        org.junit.Assert.assertTrue(((p.ownerDocument()) == doc));
        org.junit.Assert.assertTrue(((doc.ownerDocument()) == doc));
        org.junit.Assert.assertNull(doc.parent());
    }

    @org.junit.Test
    public void root() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
        org.jsoup.nodes.Element p = doc.select("p").first();
        org.jsoup.nodes.Node root = p.root();
        org.junit.Assert.assertTrue((doc == root));
        org.junit.Assert.assertNull(root.parent());
        org.junit.Assert.assertTrue(((doc.root()) == doc));
        org.junit.Assert.assertTrue(((doc.root()) == (doc.ownerDocument())));
        org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
        org.junit.Assert.assertTrue(((standAlone.parent()) == null));
        org.junit.Assert.assertTrue(((standAlone.root()) == standAlone));
        org.junit.Assert.assertTrue(((standAlone.ownerDocument()) == null));
    }

    @org.junit.Test
    public void before() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
        org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
        newNode.appendText("four");
        doc.select("b").first().before(newNode);
        org.junit.Assert.assertEquals("<p>One <em>four</em><b>two</b> three</p>", doc.body().html());
        doc.select("b").first().before("<i>five</i>");
        org.junit.Assert.assertEquals("<p>One <em>four</em><i>five</i><b>two</b> three</p>", doc.body().html());
    }

    @org.junit.Test
    public void after() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
        org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
        newNode.appendText("four");
        doc.select("b").first().after(newNode);
        org.junit.Assert.assertEquals("<p>One <b>two</b><em>four</em> three</p>", doc.body().html());
        doc.select("b").first().after("<i>five</i>");
        org.junit.Assert.assertEquals("<p>One <b>two</b><i>five</i><em>four</em> three</p>", doc.body().html());
    }

    @org.junit.Test
    public void unwrap() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div>One <span>Two <b>Three</b></span> Four</div>");
        org.jsoup.nodes.Element span = doc.select("span").first();
        org.jsoup.nodes.Node twoText = span.childNode(0);
        org.jsoup.nodes.Node node = span.unwrap();
        org.junit.Assert.assertEquals("<div>One Two <b>Three</b> Four</div>", org.jsoup.TextUtil.stripNewlines(doc.body().html()));
        org.junit.Assert.assertTrue((node instanceof org.jsoup.nodes.TextNode));
        org.junit.Assert.assertEquals("Two ", ((org.jsoup.nodes.TextNode) (node)).text());
        org.junit.Assert.assertEquals(node, twoText);
        org.junit.Assert.assertEquals(node.parent(), doc.select("div").first());
    }

    @org.junit.Test
    public void unwrapNoChildren() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div>One <span></span> Two</div>");
        org.jsoup.nodes.Element span = doc.select("span").first();
        org.jsoup.nodes.Node node = span.unwrap();
        org.junit.Assert.assertEquals("<div>One  Two</div>", org.jsoup.TextUtil.stripNewlines(doc.body().html()));
        org.junit.Assert.assertTrue((node == null));
    }

    @org.junit.Test
    public void traverse() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello</p></div><div>There</div>");
        final java.lang.StringBuilder accum = new java.lang.StringBuilder();
        doc.select("div").first().traverse(new org.jsoup.select.NodeVisitor() {
            public void head(org.jsoup.nodes.Node node, int depth) {
                accum.append((("<" + (node.nodeName())) + ">"));
            }

            public void tail(org.jsoup.nodes.Node node, int depth) {
                accum.append((("</" + (node.nodeName())) + ">"));
            }
        });
        org.junit.Assert.assertEquals("<div><p><#text></#text></p></div>", accum.toString());
    }

    @org.junit.Test
    public void orphanNodeReturnsNullForSiblingElements() {
        org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
        org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
        org.junit.Assert.assertEquals(0, node.siblingIndex());
        org.junit.Assert.assertEquals(0, node.siblingNodes().size());
        org.junit.Assert.assertNull(node.previousSibling());
        org.junit.Assert.assertNull(node.nextSibling());
        org.junit.Assert.assertEquals(0, el.siblingElements().size());
        org.junit.Assert.assertNull(el.previousElementSibling());
        org.junit.Assert.assertNull(el.nextElementSibling());
    }

    @org.junit.Test
    public void nodeIsNotASiblingOfItself() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
        org.jsoup.nodes.Element p2 = doc.select("p").get(1);
        org.junit.Assert.assertEquals("Two", p2.text());
        java.util.List<org.jsoup.nodes.Node> nodes = p2.siblingNodes();
        org.junit.Assert.assertEquals(2, nodes.size());
        org.junit.Assert.assertEquals("<p>One</p>", nodes.get(0).outerHtml());
        org.junit.Assert.assertEquals("<p>Three</p>", nodes.get(1).outerHtml());
    }

    @org.junit.Test
    public void childNodesCopy() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div id=1>Text 1 <p>One</p> Text 2 <p>Two<p>Three</div><div id=2>");
        org.jsoup.nodes.Element div1 = doc.select("#1").first();
        org.jsoup.nodes.Element div2 = doc.select("#2").first();
        java.util.List<org.jsoup.nodes.Node> divChildren = div1.childNodesCopy();
        org.junit.Assert.assertEquals(5, divChildren.size());
        org.jsoup.nodes.TextNode tn1 = ((org.jsoup.nodes.TextNode) (div1.childNode(0)));
        org.jsoup.nodes.TextNode tn2 = ((org.jsoup.nodes.TextNode) (divChildren.get(0)));
        tn2.text("Text 1 updated");
        org.junit.Assert.assertEquals("Text 1 ", tn1.text());
        div2.insertChildren((-1), divChildren);
        org.junit.Assert.assertEquals(("<div id=\"1\">Text 1 <p>One</p> Text 2 <p>Two</p><p>Three</p></div><div id=\"2\">Text 1 updated" + "<p>One</p> Text 2 <p>Two</p><p>Three</p></div>"), org.jsoup.TextUtil.stripNewlines(doc.body().html()));
    }

    @org.junit.Test
    public void supportsClone() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div class=foo>Text</div>");
        org.jsoup.nodes.Element el = doc.select("div").first();
        org.junit.Assert.assertTrue(el.hasClass("foo"));
        org.jsoup.nodes.Element elClone = doc.clone().select("div").first();
        org.junit.Assert.assertTrue(elClone.hasClass("foo"));
        org.junit.Assert.assertTrue(elClone.text().equals("Text"));
        el.removeClass("foo");
        el.text("None");
        org.junit.Assert.assertFalse(el.hasClass("foo"));
        org.junit.Assert.assertTrue(elClone.hasClass("foo"));
        org.junit.Assert.assertTrue(el.text().equals("None"));
        org.junit.Assert.assertTrue(elClone.text().equals("Text"));
    }

    /* amplification of org.jsoup.nodes.NodeTest#absHandlesDotFromIndex */
    @org.junit.Test(timeout = 10000)
    public void absHandlesDotFromIndex_cf105_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='./one/two.html'>One</a>", "http://example.com");
            org.jsoup.nodes.Element a1 = doc.select("a").first();
            // StatementAdderOnAssert create null value
            java.lang.String vc_58 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_56 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_56.before(vc_58);
            // MethodAssertGenerator build local variable
            Object o_12_0 = a1.absUrl("href");
            org.junit.Assert.fail("absHandlesDotFromIndex_cf105 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#absHandlesDotFromIndex */
    /* amplification of org.jsoup.nodes.NodeTest#absHandlesDotFromIndex_cf106_failAssert43_add309 */
    @org.junit.Test(timeout = 10000)
    public void absHandlesDotFromIndex_cf106_failAssert43_add309_literalMutation599() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='./one/two.html'>One</a>", "http://example.com");
            org.jsoup.nodes.Element a1 = doc.select("a").first();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_5 = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_5, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_56 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_56.before(String_vc_5);
            // StatementAdderMethod cloned existing statement
            vc_56.before(String_vc_5);
            // MethodAssertGenerator build local variable
            Object o_12_0 = a1.absUrl("href");
            org.junit.Assert.fail("absHandlesDotFromIndex_cf106 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* Test for an issue with Java's abs URL handler. */
    /* amplification of org.jsoup.nodes.NodeTest#absHandlesRelativeQuery */
    @org.junit.Test(timeout = 10000)
    public void absHandlesRelativeQuery_cf726_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='?foo'>One</a> <a href='bar.html?foo'>Two</a>", "https://jsoup.org/path/file?bar");
            org.jsoup.nodes.Element a1 = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = a1.absUrl("href");
            org.jsoup.nodes.Element a2 = doc.select("a").get(1);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_144 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_142 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_142.equals(vc_144);
            // MethodAssertGenerator build local variable
            Object o_17_0 = a2.absUrl("href");
            org.junit.Assert.fail("absHandlesRelativeQuery_cf726 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* Test for an issue with Java's abs URL handler. */
    /* amplification of org.jsoup.nodes.NodeTest#absHandlesRelativeQuery */
    /* amplification of org.jsoup.nodes.NodeTest#absHandlesRelativeQuery_cf733_failAssert9_add1002 */
    @org.junit.Test(timeout = 10000)
    public void absHandlesRelativeQuery_cf733_failAssert9_add1002_literalMutation1188() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='?foo'>One</a> <a href='bar.html?foo'>Two</a>", "https://jsoup.org/path/file?bar");
            org.jsoup.nodes.Element a1 = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = a1.absUrl("href");
            org.jsoup.nodes.Element a2 = doc.select("a").get(1);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_9 = "httpds://jsoup.org/path/bar.html?foo";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_9, "httpds://jsoup.org/path/bar.html?foo");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_146 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_146.hasAttr(String_vc_9);
            // StatementAdderMethod cloned existing statement
            vc_146.hasAttr(String_vc_9);
            // MethodAssertGenerator build local variable
            Object o_17_0 = a2.absUrl("href");
            org.junit.Assert.fail("absHandlesRelativeQuery_cf733 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#after */
    @org.junit.Test(timeout = 10000)
    public void after_cf1500_failAssert40() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
            org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
            newNode.appendText("four");
            doc.select("b").first().after(newNode);
            // MethodAssertGenerator build local variable
            Object o_10_0 = doc.body().html();
            doc.select("b").first().after("<i>five</i>");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_282 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_280 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_280.equals(vc_282);
            // MethodAssertGenerator build local variable
            Object o_22_0 = doc.body().html();
            org.junit.Assert.fail("after_cf1500 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#after */
    @org.junit.Test(timeout = 10000)
    public void after_cf1538_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
            org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
            newNode.appendText("four");
            doc.select("b").first().after(newNode);
            // MethodAssertGenerator build local variable
            Object o_10_0 = doc.body().html();
            doc.select("b").first().after("<i>five</i>");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_303 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_300 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_300.attr(vc_303);
            // MethodAssertGenerator build local variable
            Object o_22_0 = doc.body().html();
            org.junit.Assert.fail("after_cf1538 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#after */
    @org.junit.Test(timeout = 10000)
    public void after_cf1665_failAssert13_literalMutation1802() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
            org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
            newNode.appendText("four");
            doc.select("b").first().after(newNode);
            // MethodAssertGenerator build local variable
            Object o_10_0 = doc.body().html();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, "<p>One <b>two</b><em>four</em> three</p>");
            doc.select("b").first().after("<i>five</i>");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node[] vc_376 = (org.jsoup.nodes.Node[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_376);
            // StatementAdderOnAssert create random local variable
            int vc_375 = 2147483647;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_375, 2147483647);
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_373 = (org.jsoup.nodes.Node)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_373);
            // StatementAdderMethod cloned existing statement
            vc_373.addChildren(vc_375, vc_376);
            // MethodAssertGenerator build local variable
            Object o_24_0 = doc.body().html();
            org.junit.Assert.fail("after_cf1665 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#before */
    @org.junit.Test(timeout = 10000)
    public void before_cf20964_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
            org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
            newNode.appendText("four");
            doc.select("b").first().before(newNode);
            // MethodAssertGenerator build local variable
            Object o_10_0 = doc.body().html();
            doc.select("b").first().before("<i>five</i>");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_7132 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_7129 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_7129.wrap(vc_7132);
            // MethodAssertGenerator build local variable
            Object o_22_0 = doc.body().html();
            org.junit.Assert.fail("before_cf20964 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#before */
    @org.junit.Test(timeout = 10000)
    public void before_cf20809_failAssert53() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
            org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
            newNode.appendText("four");
            doc.select("b").first().before(newNode);
            // MethodAssertGenerator build local variable
            Object o_10_0 = doc.body().html();
            doc.select("b").first().before("<i>five</i>");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_7044 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_7042 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_7042.equals(vc_7044);
            // MethodAssertGenerator build local variable
            Object o_22_0 = doc.body().html();
            org.junit.Assert.fail("before_cf20809 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#before */
    @org.junit.Test(timeout = 10000)
    public void before_cf20883_failAssert3_add21063() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <b>two</b> three</p>");
            org.jsoup.nodes.Element newNode = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "");
            newNode.appendText("four");
            doc.select("b").first().before(newNode);
            // MethodAssertGenerator build local variable
            Object o_10_0 = doc.body().html();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, "<p>One <em>four</em><b>two</b> three</p>");
            doc.select("b").first().before("<i>five</i>");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_7087 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_7087, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_7084 = (org.jsoup.nodes.Node)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7084);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_7084.after(vc_7087);
            // StatementAdderMethod cloned existing statement
            vc_7084.after(vc_7087);
            // MethodAssertGenerator build local variable
            Object o_22_0 = doc.body().html();
            org.junit.Assert.fail("before_cf20883 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handleAbsOnFileUris */
    @org.junit.Test(timeout = 10000)
    public void handleAbsOnFileUris_cf206068_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file:/etc/");
            org.jsoup.nodes.Element one = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = one.absUrl("href");
            org.jsoup.nodes.Element two = doc.select("a").get(1);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_69558 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_69556 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_69556.equals(vc_69558);
            // MethodAssertGenerator build local variable
            Object o_17_0 = two.absUrl("href");
            org.junit.Assert.fail("handleAbsOnFileUris_cf206068 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handleAbsOnFileUris */
    /* amplification of org.jsoup.nodes.NodeTest#handleAbsOnFileUris_cf206075_failAssert0_add206320 */
    @org.junit.Test(timeout = 10000)
    public void handleAbsOnFileUris_cf206075_failAssert0_add206320_literalMutation206415() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file:/etc/");
            org.jsoup.nodes.Element one = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = one.absUrl("href");
            org.jsoup.nodes.Element two = doc.select("a").get(1);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_6540 = "Z";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_6540, "Z");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_69560 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_69560.hasAttr(String_vc_6540);
            // StatementAdderMethod cloned existing statement
            vc_69560.hasAttr(String_vc_6540);
            // MethodAssertGenerator build local variable
            Object o_17_0 = two.absUrl("href");
            org.junit.Assert.fail("handleAbsOnFileUris_cf206075 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handleAbsOnLocalhostFileUris */
    @org.junit.Test(timeout = 10000)
    public void handleAbsOnLocalhostFileUris_cf206766_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file://localhost/etc/");
            org.jsoup.nodes.Element one = doc.select("a").first();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_69696 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_69694 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_69694.equals(vc_69696);
            // MethodAssertGenerator build local variable
            Object o_12_0 = one.absUrl("href");
            org.junit.Assert.fail("handleAbsOnLocalhostFileUris_cf206766 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handleAbsOnLocalhostFileUris */
    /* amplification of org.jsoup.nodes.NodeTest#handleAbsOnLocalhostFileUris_cf206773_failAssert23_add207034 */
    @org.junit.Test(timeout = 10000)
    public void handleAbsOnLocalhostFileUris_cf206773_failAssert23_add207034_literalMutation207262() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href='password'>One/a><a href='/var/log/messages'>Two</a>", "file://localhost/etc/");
            org.jsoup.nodes.Element one = doc.select("a").first();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_6553 = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_6553, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_69698 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_69698.hasAttr(String_vc_6553);
            // StatementAdderMethod cloned existing statement
            vc_69698.hasAttr(String_vc_6553);
            // MethodAssertGenerator build local variable
            Object o_12_0 = one.absUrl("href");
            org.junit.Assert.fail("handleAbsOnLocalhostFileUris_cf206773 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsOnProtocolessAbsoluteUris */
    @org.junit.Test(timeout = 10000)
    public void handlesAbsOnProtocolessAbsoluteUris_cf363523_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc1 = org.jsoup.Jsoup.parse("<a href='//example.net/foo'>One</a>", "http://example.com/");
            org.jsoup.nodes.Document doc2 = org.jsoup.Jsoup.parse("<a href='//example.net/foo'>One</a>", "https://example.com/");
            org.jsoup.nodes.Element one = doc1.select("a").first();
            org.jsoup.nodes.Element two = doc2.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.absUrl("href");
            // MethodAssertGenerator build local variable
            Object o_13_0 = two.absUrl("href");
            org.jsoup.nodes.Document doc3 = org.jsoup.Jsoup.parse("<img src=//www.google.com/images/errors/logo_sm.gif alt=Google>", "https://google.com");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_125172 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_125170 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_125170.equals(vc_125172);
            // MethodAssertGenerator build local variable
            Object o_23_0 = doc3.select("img").attr("abs:src");
            org.junit.Assert.fail("handlesAbsOnProtocolessAbsoluteUris_cf363523 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsOnProtocolessAbsoluteUris */
    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsOnProtocolessAbsoluteUris_cf363530_failAssert23_add363791 */
    @org.junit.Test(timeout = 10000)
    public void handlesAbsOnProtocolessAbsoluteUris_cf363530_failAssert23_add363791_literalMutation364012() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc1 = org.jsoup.Jsoup.parse("<a href='//example.net/foo'>One</a>", "http://example.com/");
            org.jsoup.nodes.Document doc2 = org.jsoup.Jsoup.parse("<a href='//example.net/foo'>One</a>", "https://example.com/");
            org.jsoup.nodes.Element one = doc1.select("a").first();
            org.jsoup.nodes.Element two = doc2.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.absUrl("href");
            // MethodAssertGenerator build local variable
            Object o_13_0 = two.absUrl("href");
            org.jsoup.nodes.Document doc3 = org.jsoup.Jsoup.parse("<img src=//www.google.com/images/errors/logo_sm.gif alt=Google>", "https://google.com");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_11759 = "f";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_11759, "f");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_125174 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_125174.hasAttr(String_vc_11759);
            // StatementAdderMethod cloned existing statement
            vc_125174.hasAttr(String_vc_11759);
            // MethodAssertGenerator build local variable
            Object o_23_0 = doc3.select("img").attr("abs:src");
            org.junit.Assert.fail("handlesAbsOnProtocolessAbsoluteUris_cf363530 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsPrefix */
    @org.junit.Test(timeout = 10000)
    public void handlesAbsPrefix_cf364187_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href=/foo>Hello</a>", "https://jsoup.org/");
            org.jsoup.nodes.Element a = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = a.attr("href");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.attr("abs:href");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_125310 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_125308 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_125308.equals(vc_125310);
            // MethodAssertGenerator build local variable
            Object o_16_0 = a.hasAttr("abs:href");
            org.junit.Assert.fail("handlesAbsPrefix_cf364187 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsPrefix */
    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsPrefix_cf364194_failAssert32_add364457 */
    @org.junit.Test(timeout = 10000)
    public void handlesAbsPrefix_cf364194_failAssert32_add364457_literalMutation364657() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a href=/foo>Hello</a>", "https://jsoup.org/");
            org.jsoup.nodes.Element a = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = a.attr("href");
            // MethodAssertGenerator build local variable
            Object o_8_0 = a.attr("abs:href");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_11768 = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_11768, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_125312 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_125312.hasAttr(String_vc_11768);
            // StatementAdderMethod cloned existing statement
            vc_125312.hasAttr(String_vc_11768);
            // MethodAssertGenerator build local variable
            Object o_16_0 = a.hasAttr("abs:href");
            org.junit.Assert.fail("handlesAbsPrefix_cf364194 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsPrefixOnHasAttr */
    @org.junit.Test(timeout = 10000)
    public void handlesAbsPrefixOnHasAttr_cf364918_failAssert41() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // 1: no abs url; 2: has abs url
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org/'>Two</a>");
            org.jsoup.nodes.Element one = doc.select("#1").first();
            org.jsoup.nodes.Element two = doc.select("#2").first();
            // MethodAssertGenerator build local variable
            Object o_10_0 = one.hasAttr("abs:href");
            // MethodAssertGenerator build local variable
            Object o_12_0 = one.hasAttr("href");
            // MethodAssertGenerator build local variable
            Object o_14_0 = one.absUrl("href");
            // MethodAssertGenerator build local variable
            Object o_16_0 = two.hasAttr("abs:href");
            // MethodAssertGenerator build local variable
            Object o_18_0 = two.hasAttr("href");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_125448 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_125446 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_125446.equals(vc_125448);
            // MethodAssertGenerator build local variable
            Object o_26_0 = two.absUrl("href");
            org.junit.Assert.fail("handlesAbsPrefixOnHasAttr_cf364918 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsPrefixOnHasAttr */
    /* amplification of org.jsoup.nodes.NodeTest#handlesAbsPrefixOnHasAttr_cf364946_failAssert30_add365191 */
    @org.junit.Test(timeout = 10000)
    public void handlesAbsPrefixOnHasAttr_cf364946_failAssert30_add365191_literalMutation365389() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // 1: no abs url; 2: has abs url
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a id=1 href='/foo'>One</a> <a id=2 href='https://jsoup.org/'>Two</a>");
            org.jsoup.nodes.Element one = doc.select("#1").first();
            org.jsoup.nodes.Element two = doc.select("#2").first();
            // MethodAssertGenerator build local variable
            Object o_10_0 = one.hasAttr("abs:href");
            // MethodAssertGenerator build local variable
            Object o_12_0 = one.hasAttr("href");
            // MethodAssertGenerator build local variable
            Object o_14_0 = one.absUrl("href");
            // MethodAssertGenerator build local variable
            Object o_16_0 = two.hasAttr("abs:href");
            // MethodAssertGenerator build local variable
            Object o_18_0 = two.hasAttr("href");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_11778 = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_11778, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_125462 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_125462.absUrl(String_vc_11778);
            // StatementAdderMethod cloned existing statement
            vc_125462.absUrl(String_vc_11778);
            // MethodAssertGenerator build local variable
            Object o_26_0 = two.absUrl("href");
            org.junit.Assert.fail("handlesAbsPrefixOnHasAttr_cf364946 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#literalAbsPrefix */
    @org.junit.Test(timeout = 10000)
    public void literalAbsPrefix_cf386408_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // if there is a literal attribute "abs:xxx", don't try and make absolute.
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a abs:href='odd'>One</a>");
            org.jsoup.nodes.Element el = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_7_0 = el.hasAttr("abs:href");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_132348 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_132346 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_132346.equals(vc_132348);
            // MethodAssertGenerator build local variable
            Object o_15_0 = el.attr("abs:href");
            org.junit.Assert.fail("literalAbsPrefix_cf386408 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#literalAbsPrefix */
    /* amplification of org.jsoup.nodes.NodeTest#literalAbsPrefix_cf386436_failAssert20_add386668 */
    @org.junit.Test(timeout = 10000)
    public void literalAbsPrefix_cf386436_failAssert20_add386668_literalMutation386845() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // if there is a literal attribute "abs:xxx", don't try and make absolute.
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<a abs:href='odd'>One</a>");
            org.jsoup.nodes.Element el = doc.select("a").first();
            // MethodAssertGenerator build local variable
            Object o_7_0 = el.hasAttr("abs:href");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12420 = "<a aDbs:href=\'odd\'>One</a>";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_12420, "<a aDbs:href=\'odd\'>One</a>");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_132362 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_132362.absUrl(String_vc_12420);
            // StatementAdderMethod cloned existing statement
            vc_132362.absUrl(String_vc_12420);
            // MethodAssertGenerator build local variable
            Object o_15_0 = el.attr("abs:href");
            org.junit.Assert.fail("literalAbsPrefix_cf386436 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#nodeIsNotASiblingOfItself */
    @org.junit.Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_cf387171_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            org.jsoup.nodes.Element p2 = doc.select("p").get(1);
            // MethodAssertGenerator build local variable
            Object o_6_0 = p2.text();
            java.util.List<org.jsoup.nodes.Node> nodes = p2.siblingNodes();
            // MethodAssertGenerator build local variable
            Object o_10_0 = nodes.size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = nodes.get(0).outerHtml();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12433 = "<div><p>One<p>Two<p>Three</div>";
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_132536 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_132536.before(String_vc_12433);
            // MethodAssertGenerator build local variable
            Object o_21_0 = nodes.get(1).outerHtml();
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_cf387171 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#nodeIsNotASiblingOfItself */
    @org.junit.Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_cf387077_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            org.jsoup.nodes.Element p2 = doc.select("p").get(1);
            // MethodAssertGenerator build local variable
            Object o_6_0 = p2.text();
            java.util.List<org.jsoup.nodes.Node> nodes = p2.siblingNodes();
            // MethodAssertGenerator build local variable
            Object o_10_0 = nodes.size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = nodes.get(0).outerHtml();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_132486 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_132484 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_132484.equals(vc_132486);
            // MethodAssertGenerator build local variable
            Object o_21_0 = nodes.get(1).outerHtml();
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_cf387077 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#nodeIsNotASiblingOfItself */
    /* amplification of org.jsoup.nodes.NodeTest#nodeIsNotASiblingOfItself_cf387105_failAssert29_add387374 */
    @org.junit.Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_cf387105_failAssert29_add387374_literalMutation387594() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            org.jsoup.nodes.Element p2 = doc.select("p").get(1);
            // MethodAssertGenerator build local variable
            Object o_6_0 = p2.text();
            java.util.List<org.jsoup.nodes.Node> nodes = p2.siblingNodes();
            // MethodAssertGenerator build local variable
            Object o_10_0 = nodes.size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = nodes.get(0).outerHtml();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12429 = "T1wo";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_12429, "T1wo");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_132500 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_132500.absUrl(String_vc_12429);
            // StatementAdderMethod cloned existing statement
            vc_132500.absUrl(String_vc_12429);
            // MethodAssertGenerator build local variable
            Object o_21_0 = nodes.get(1).outerHtml();
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_cf387105 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#nodeIsNotASiblingOfItself */
    /* amplification of org.jsoup.nodes.NodeTest#nodeIsNotASiblingOfItself_cf387213_failAssert44_literalMutation387400 */
    @org.junit.Test(timeout = 10000)
    public void nodeIsNotASiblingOfItself_cf387213_failAssert44_literalMutation387400_literalMutation387696() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
            org.jsoup.nodes.Element p2 = doc.select("p").get(1);
            // MethodAssertGenerator build local variable
            Object o_6_0 = p2.text();
            java.util.List<org.jsoup.nodes.Node> nodes = p2.siblingNodes();
            // MethodAssertGenerator build local variable
            Object o_10_0 = nodes.size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = nodes.get(0).outerHtml();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12435 = " RC";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_12435, " RC");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_132559 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_132559.removeAttr(String_vc_12435);
            // MethodAssertGenerator build local variable
            Object o_21_0 = nodes.get(1).outerHtml();
            org.junit.Assert.fail("nodeIsNotASiblingOfItself_cf387213 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf388026_failAssert50() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create null value
            java.lang.Appendable vc_132735 = (java.lang.Appendable)null;
            // StatementAdderMethod cloned existing statement
            node.outerHtml(vc_132735);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf388026 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387877_failAssert41() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderMethod cloned existing statement
            node.before(node);
            // MethodAssertGenerator build local variable
            Object o_23_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387877 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387938_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create null value
            org.jsoup.select.NodeVisitor vc_132705 = (org.jsoup.select.NodeVisitor)null;
            // StatementAdderMethod cloned existing statement
            node.traverse(vc_132705);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387938 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387836_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12444 = "p";
            // StatementAdderMethod cloned existing statement
            node.after(String_vc_12444);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387836 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387924_failAssert39() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12448 = "";
            // StatementAdderMethod cloned existing statement
            node.removeAttr(String_vc_12448);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387924 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387777_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_132641 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            node.absUrl(vc_132641);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387777 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387945_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderMethod cloned existing statement
            node.unwrap();
            // MethodAssertGenerator build local variable
            Object o_23_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387945 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf388033_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderMethod cloned existing statement
            node.remove();
            // MethodAssertGenerator build local variable
            Object o_23_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf388033 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387952_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12449 = "";
            // StatementAdderMethod cloned existing statement
            node.wrap(String_vc_12449);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387952 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387747_failAssert34() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create null value
            java.lang.String vc_132628 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            node.hasAttr(vc_132628);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387747 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387759() {
        org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
        org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
        org.junit.Assert.assertEquals(0, node.siblingIndex());
        org.junit.Assert.assertEquals(0, node.siblingNodes().size());
        org.junit.Assert.assertNull(node.previousSibling());
        org.junit.Assert.assertNull(node.nextSibling());
        org.junit.Assert.assertEquals(0, el.siblingElements().size());
        org.junit.Assert.assertNull(el.previousElementSibling());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_132633 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_orphanNodeReturnsNullForSiblingElements_cf387759__23 = // StatementAdderMethod cloned existing statement
node.hasSameValue(vc_132633);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_orphanNodeReturnsNullForSiblingElements_cf387759__23);
        org.junit.Assert.assertNull(el.nextElementSibling());
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387953_failAssert22_add390634() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, 0);
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, 0);
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_12_0);
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_14_0);
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_16_0, 0);
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_19_0);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_132712 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_132712, "");
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            node.wrap(vc_132712);
            // StatementAdderMethod cloned existing statement
            node.wrap(vc_132712);
            // MethodAssertGenerator build local variable
            Object o_25_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387953 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387788_literalMutation390116_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12443 = "";
            // MethodAssertGenerator build local variable
            Object o_23_0 = String_vc_12443;
            // AssertGenerator replace invocation
            java.lang.String o_orphanNodeReturnsNullForSiblingElements_cf387788__23 = // StatementAdderMethod cloned existing statement
node.attr(String_vc_12443);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_orphanNodeReturnsNullForSiblingElements_cf387788__23;
            // MethodAssertGenerator build local variable
            Object o_29_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387788_literalMutation390116 should have thrown Exception");
        } catch (java.lang.Exception eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387749_cf388313_failAssert40() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_132629 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_23_0 = vc_132629;
            // AssertGenerator replace invocation
            boolean o_orphanNodeReturnsNullForSiblingElements_cf387749__23 = // StatementAdderMethod cloned existing statement
node.hasAttr(vc_132629);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_orphanNodeReturnsNullForSiblingElements_cf387749__23;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12461 = "";
            // StatementAdderMethod cloned existing statement
            node.removeAttr(String_vc_12461);
            // MethodAssertGenerator build local variable
            Object o_33_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387749_cf388313 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387759_cf388735_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_132633 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_orphanNodeReturnsNullForSiblingElements_cf387759__23 = // StatementAdderMethod cloned existing statement
node.hasSameValue(vc_132633);
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_orphanNodeReturnsNullForSiblingElements_cf387759__23;
            // StatementAdderOnAssert create null value
            org.jsoup.select.NodeVisitor vc_132981 = (org.jsoup.select.NodeVisitor)null;
            // StatementAdderMethod cloned existing statement
            node.traverse(vc_132981);
            // MethodAssertGenerator build local variable
            Object o_31_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387759_cf388735 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#orphanNodeReturnsNullForSiblingElements */
    @org.junit.Test(timeout = 10000)
    public void orphanNodeReturnsNullForSiblingElements_cf387765_cf389863_failAssert42() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 0;
            org.jsoup.nodes.Node node = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_7_0 = node.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_9_0 = node.siblingNodes().size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = node.previousSibling();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node.nextSibling();
            // MethodAssertGenerator build local variable
            Object o_16_0 = el.siblingElements().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.previousElementSibling();
            // AssertGenerator replace invocation
            int o_orphanNodeReturnsNullForSiblingElements_cf387765__21 = // StatementAdderMethod cloned existing statement
node.childNodeSize();
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_orphanNodeReturnsNullForSiblingElements_cf387765__21;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12509 = "";
            // StatementAdderMethod cloned existing statement
            node.after(String_vc_12509);
            // MethodAssertGenerator build local variable
            Object o_29_0 = el.nextElementSibling();
            org.junit.Assert.fail("orphanNodeReturnsNullForSiblingElements_cf387765_cf389863 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument */
    @org.junit.Test(timeout = 10000)
    public void ownerDocument_cf393645_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = (p.ownerDocument()) == doc;
            // MethodAssertGenerator build local variable
            Object o_8_0 = (doc.ownerDocument()) == doc;
            // StatementAdderOnAssert create null value
            org.jsoup.select.NodeVisitor vc_134499 = (org.jsoup.select.NodeVisitor)null;
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.traverse(vc_134499);
            // MethodAssertGenerator build local variable
            Object o_17_0 = doc.parent();
            org.junit.Assert.fail("ownerDocument_cf393645 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument */
    @org.junit.Test(timeout = 10000)
    public void ownerDocument_cf393636_failAssert47() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = (p.ownerDocument()) == doc;
            // MethodAssertGenerator build local variable
            Object o_8_0 = (doc.ownerDocument()) == doc;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_134494 = new java.lang.String();
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.removeAttr(vc_134494);
            // MethodAssertGenerator build local variable
            Object o_17_0 = doc.parent();
            org.junit.Assert.fail("ownerDocument_cf393636 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument */
    @org.junit.Test(timeout = 10000)
    public void ownerDocument_cf393575_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = (p.ownerDocument()) == doc;
            // MethodAssertGenerator build local variable
            Object o_8_0 = (doc.ownerDocument()) == doc;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12613 = "p";
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.after(String_vc_12613);
            // MethodAssertGenerator build local variable
            Object o_17_0 = doc.parent();
            org.junit.Assert.fail("ownerDocument_cf393575 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument */
    @org.junit.Test(timeout = 10000)
    public void ownerDocument_cf393715_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = (p.ownerDocument()) == doc;
            // MethodAssertGenerator build local variable
            Object o_8_0 = (doc.ownerDocument()) == doc;
            // StatementAdderOnAssert create null value
            java.lang.String vc_134543 = (java.lang.String)null;
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.setBaseUri(vc_134543);
            // MethodAssertGenerator build local variable
            Object o_17_0 = doc.parent();
            org.junit.Assert.fail("ownerDocument_cf393715 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument */
    @org.junit.Test(timeout = 10000)
    public void ownerDocument_cf393499_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = (p.ownerDocument()) == doc;
            // MethodAssertGenerator build local variable
            Object o_8_0 = (doc.ownerDocument()) == doc;
            // StatementAdderOnAssert create null value
            java.lang.Object vc_134418 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_134416 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_134416.equals(vc_134418);
            // MethodAssertGenerator build local variable
            Object o_16_0 = doc.parent();
            org.junit.Assert.fail("ownerDocument_cf393499 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument */
    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument_cf393506_failAssert13_add393746 */
    @org.junit.Test(timeout = 10000)
    public void ownerDocument_cf393506_failAssert13_add393746_literalMutation393882() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = (p.ownerDocument()) == doc;
            // MethodAssertGenerator build local variable
            Object o_8_0 = (doc.ownerDocument()) == doc;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12610 = "<>Hello";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_12610, "<>Hello");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_134420 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_134420.hasAttr(String_vc_12610);
            // StatementAdderMethod cloned existing statement
            vc_134420.hasAttr(String_vc_12610);
            // MethodAssertGenerator build local variable
            Object o_16_0 = doc.parent();
            org.junit.Assert.fail("ownerDocument_cf393506 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument */
    /* amplification of org.jsoup.nodes.NodeTest#ownerDocument_cf393575_failAssert0_add393727 */
    @org.junit.Test(timeout = 10000)
    public void ownerDocument_cf393575_failAssert0_add393727_literalMutation393823() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = (p.ownerDocument()) == doc;
            // MethodAssertGenerator build local variable
            Object o_8_0 = (doc.ownerDocument()) == doc;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12613 = "l";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_12613, "l");
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            root.after(String_vc_12613);
            // StatementAdderMethod cloned existing statement
            root.after(String_vc_12613);
            // MethodAssertGenerator build local variable
            Object o_17_0 = doc.parent();
            org.junit.Assert.fail("ownerDocument_cf393575 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394419_failAssert46() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderMethod cloned existing statement
            root.remove();
            // MethodAssertGenerator build local variable
            Object o_25_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394419 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394150_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create null value
            java.lang.Object vc_134556 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_134554 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_134554.equals(vc_134556);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394150 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394335_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_134632 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            root.removeAttr(vc_134632);
            // MethodAssertGenerator build local variable
            Object o_27_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394335 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394219() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
        org.jsoup.nodes.Element p = doc.select("p").first();
        org.jsoup.nodes.Node root = p.root();
        org.junit.Assert.assertTrue((doc == root));
        org.junit.Assert.assertNull(root.parent());
        org.junit.Assert.assertTrue(((doc.root()) == doc));
        org.junit.Assert.assertTrue(((doc.root()) == (doc.ownerDocument())));
        org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
        org.junit.Assert.assertTrue(((standAlone.parent()) == null));
        org.junit.Assert.assertTrue(((standAlone.root()) == standAlone));
        // AssertGenerator replace invocation
        java.lang.String o_root_cf394219__23 = // StatementAdderMethod cloned existing statement
root.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_root_cf394219__23, "<html>\n <head></head>\n <body>\n  <div>\n   <p>Hello</p>\n  </div>\n </body>\n</html>");
        org.junit.Assert.assertTrue(((standAlone.ownerDocument()) == null));
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394172() {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
        org.jsoup.nodes.Element p = doc.select("p").first();
        org.jsoup.nodes.Node root = p.root();
        org.junit.Assert.assertTrue((doc == root));
        org.junit.Assert.assertNull(root.parent());
        org.junit.Assert.assertTrue(((doc.root()) == doc));
        org.junit.Assert.assertTrue(((doc.root()) == (doc.ownerDocument())));
        org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
        org.junit.Assert.assertTrue(((standAlone.parent()) == null));
        org.junit.Assert.assertTrue(((standAlone.root()) == standAlone));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_134564 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_134564);
        // AssertGenerator replace invocation
        boolean o_root_cf394172__25 = // StatementAdderMethod cloned existing statement
root.hasSameValue(vc_134564);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_root_cf394172__25);
        org.junit.Assert.assertTrue(((standAlone.ownerDocument()) == null));
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394363_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_134644 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            root.wrap(vc_134644);
            // MethodAssertGenerator build local variable
            Object o_27_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394363 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394161_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create null value
            java.lang.String vc_134560 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            root.hasAttr(vc_134560);
            // MethodAssertGenerator build local variable
            Object o_27_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394161 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394203_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_134577 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            root.attr(vc_134577);
            // MethodAssertGenerator build local variable
            Object o_27_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394203 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394191_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_134573 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            root.absUrl(vc_134573);
            // MethodAssertGenerator build local variable
            Object o_27_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394191 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394251_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_134599 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            root.after(vc_134599);
            // MethodAssertGenerator build local variable
            Object o_27_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394251 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394441_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create null value
            java.lang.String vc_134681 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            root.setBaseUri(vc_134681);
            // MethodAssertGenerator build local variable
            Object o_27_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394441 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394153_cf397022_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_134557 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_root_cf394153__25 = // StatementAdderMethod cloned existing statement
root.equals(vc_134557);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_root_cf394153__25;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_135598 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            root.removeAttr(vc_135598);
            // MethodAssertGenerator build local variable
            Object o_33_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394153_cf397022 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394190_cf394938_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12620 = "p";
            // MethodAssertGenerator build local variable
            Object o_25_0 = String_vc_12620;
            // AssertGenerator replace invocation
            java.lang.String o_root_cf394190__25 = // StatementAdderMethod cloned existing statement
root.absUrl(String_vc_12620);
            // MethodAssertGenerator build local variable
            Object o_29_0 = o_root_cf394190__25;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12644 = "p";
            // StatementAdderMethod cloned existing statement
            root.after(String_vc_12644);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394190_cf394938 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394219_cf395859_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // AssertGenerator replace invocation
            java.lang.String o_root_cf394219__23 = // StatementAdderMethod cloned existing statement
root.toString();
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_root_cf394219__23;
            // StatementAdderOnAssert create null value
            org.jsoup.select.NodeVisitor vc_135189 = (org.jsoup.select.NodeVisitor)null;
            // StatementAdderMethod cloned existing statement
            root.traverse(vc_135189);
            // MethodAssertGenerator build local variable
            Object o_31_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394219_cf395859 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394190_cf394871_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_12620 = "p";
            // MethodAssertGenerator build local variable
            Object o_25_0 = String_vc_12620;
            // AssertGenerator replace invocation
            java.lang.String o_root_cf394190__25 = // StatementAdderMethod cloned existing statement
root.absUrl(String_vc_12620);
            // MethodAssertGenerator build local variable
            Object o_29_0 = o_root_cf394190__25;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_134849 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            root.absUrl(vc_134849);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394190_cf394871 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394323_cf394479_failAssert44() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // AssertGenerator replace invocation
            org.jsoup.nodes.Node o_root_cf394323__23 = // StatementAdderMethod cloned existing statement
root.parentNode();
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_root_cf394323__23;
            // StatementAdderOnAssert create null value
            java.lang.String vc_134698 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            root.hasAttr(vc_134698);
            // MethodAssertGenerator build local variable
            Object o_31_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394323_cf394479 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394153_cf397184_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_134557 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_root_cf394153__25 = // StatementAdderMethod cloned existing statement
root.equals(vc_134557);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_root_cf394153__25;
            // StatementAdderMethod cloned existing statement
            root.remove();
            // MethodAssertGenerator build local variable
            Object o_31_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394153_cf397184 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394153_cf396843_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_134557 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_root_cf394153__25 = // StatementAdderMethod cloned existing statement
root.equals(vc_134557);
            // MethodAssertGenerator build local variable
            Object o_27_0 = o_root_cf394153__25;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_135534 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_135534.siblingIndex();
            // MethodAssertGenerator build local variable
            Object o_33_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394153_cf396843 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394323_cf394787_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Node root = p.root();
            // MethodAssertGenerator build local variable
            Object o_8_0 = doc == root;
            // MethodAssertGenerator build local variable
            Object o_9_0 = root.parent();
            // MethodAssertGenerator build local variable
            Object o_11_0 = (doc.root()) == doc;
            // MethodAssertGenerator build local variable
            Object o_13_0 = (doc.root()) == (doc.ownerDocument());
            org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
            // MethodAssertGenerator build local variable
            Object o_19_0 = (standAlone.parent()) == null;
            // MethodAssertGenerator build local variable
            Object o_21_0 = (standAlone.root()) == standAlone;
            // AssertGenerator replace invocation
            org.jsoup.nodes.Node o_root_cf394323__23 = // StatementAdderMethod cloned existing statement
root.parentNode();
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_root_cf394323__23;
            // StatementAdderOnAssert create null value
            java.lang.String vc_134819 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            root.setBaseUri(vc_134819);
            // MethodAssertGenerator build local variable
            Object o_31_0 = (standAlone.ownerDocument()) == null;
            org.junit.Assert.fail("root_cf394323_cf394787 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394190_cf394920_failAssert22_literalMutation397871_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
                org.jsoup.nodes.Element p = doc.select("p").first();
                org.jsoup.nodes.Node root = p.root();
                // MethodAssertGenerator build local variable
                Object o_8_0 = doc == root;
                // MethodAssertGenerator build local variable
                Object o_9_0 = root.parent();
                // MethodAssertGenerator build local variable
                Object o_11_0 = (doc.root()) == doc;
                // MethodAssertGenerator build local variable
                Object o_13_0 = (doc.root()) == (doc.ownerDocument());
                org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
                // MethodAssertGenerator build local variable
                Object o_19_0 = (standAlone.parent()) == null;
                // MethodAssertGenerator build local variable
                Object o_21_0 = (standAlone.root()) == standAlone;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_12620 = "";
                // MethodAssertGenerator build local variable
                Object o_25_0 = String_vc_12620;
                // AssertGenerator replace invocation
                java.lang.String o_root_cf394190__25 = // StatementAdderMethod cloned existing statement
root.absUrl(String_vc_12620);
                // MethodAssertGenerator build local variable
                Object o_29_0 = o_root_cf394190__25;
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Node vc_134866 = (org.jsoup.nodes.Node)null;
                // StatementAdderMethod cloned existing statement
                vc_134866.attributes();
                // MethodAssertGenerator build local variable
                Object o_35_0 = (standAlone.ownerDocument()) == null;
                org.junit.Assert.fail("root_cf394190_cf394920 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("root_cf394190_cf394920_failAssert22_literalMutation397871 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#root */
    @org.junit.Test(timeout = 10000)
    public void root_cf394190_cf394825_failAssert15_literalMutation397847_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello");
                org.jsoup.nodes.Element p = doc.select("p").first();
                org.jsoup.nodes.Node root = p.root();
                // MethodAssertGenerator build local variable
                Object o_8_0 = doc == root;
                // MethodAssertGenerator build local variable
                Object o_9_0 = root.parent();
                // MethodAssertGenerator build local variable
                Object o_11_0 = (doc.root()) == doc;
                // MethodAssertGenerator build local variable
                Object o_13_0 = (doc.root()) == (doc.ownerDocument());
                org.jsoup.nodes.Element standAlone = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");
                // MethodAssertGenerator build local variable
                Object o_19_0 = (standAlone.parent()) == null;
                // MethodAssertGenerator build local variable
                Object o_21_0 = (standAlone.root()) == standAlone;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_12620 = "";
                // MethodAssertGenerator build local variable
                Object o_25_0 = String_vc_12620;
                // AssertGenerator replace invocation
                java.lang.String o_root_cf394190__25 = // StatementAdderMethod cloned existing statement
root.absUrl(String_vc_12620);
                // MethodAssertGenerator build local variable
                Object o_29_0 = o_root_cf394190__25;
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_134833 = new java.lang.Object();
                // StatementAdderOnAssert create null value
                org.jsoup.nodes.Node vc_134830 = (org.jsoup.nodes.Node)null;
                // StatementAdderMethod cloned existing statement
                vc_134830.equals(vc_134833);
                // MethodAssertGenerator build local variable
                Object o_37_0 = (standAlone.ownerDocument()) == null;
                org.junit.Assert.fail("root_cf394190_cf394825 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("root_cf394190_cf394825_failAssert15_literalMutation397847 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#setBaseUriIsRecursive */
    @org.junit.Test(timeout = 10000)
    public void setBaseUriIsRecursive_cf398000_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p></p></div>");
            java.lang.String baseUri = "https://jsoup.org";
            doc.setBaseUri(baseUri);
            // MethodAssertGenerator build local variable
            Object o_5_0 = doc.baseUri();
            // MethodAssertGenerator build local variable
            Object o_7_0 = doc.select("div").first().baseUri();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_135798 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_135796 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_135796.equals(vc_135798);
            // MethodAssertGenerator build local variable
            Object o_17_0 = doc.select("p").first().baseUri();
            org.junit.Assert.fail("setBaseUriIsRecursive_cf398000 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#supportsClone */
    @org.junit.Test(timeout = 10000)
    public void supportsClone_cf440426_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div class=foo>Text</div>");
            org.jsoup.nodes.Element el = doc.select("div").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = el.hasClass("foo");
            org.jsoup.nodes.Element elClone = doc.clone().select("div").first();
            // MethodAssertGenerator build local variable
            Object o_12_0 = elClone.hasClass("foo");
            // MethodAssertGenerator build local variable
            Object o_14_0 = elClone.text().equals("Text");
            el.removeClass("foo");
            el.text("None");
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.hasClass("foo");
            // MethodAssertGenerator build local variable
            Object o_21_0 = elClone.hasClass("foo");
            // MethodAssertGenerator build local variable
            Object o_23_0 = el.text().equals("None");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_163536 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_163534 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_163534.equals(vc_163536);
            // MethodAssertGenerator build local variable
            Object o_32_0 = elClone.text().equals("Text");
            org.junit.Assert.fail("supportsClone_cf440426 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#supportsClone */
    /* amplification of org.jsoup.nodes.NodeTest#supportsClone_cf440433_failAssert47_add440838 */
    @org.junit.Test(timeout = 10000)
    public void supportsClone_cf440433_failAssert47_add440838_literalMutation441843() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div class=foo>Text</div>");
            org.jsoup.nodes.Element el = doc.select("div").first();
            // MethodAssertGenerator build local variable
            Object o_6_0 = el.hasClass("foo");
            org.jsoup.nodes.Element elClone = doc.clone().select("div").first();
            // MethodAssertGenerator build local variable
            Object o_12_0 = elClone.hasClass("foo");
            // MethodAssertGenerator build local variable
            Object o_14_0 = elClone.text().equals("Text");
            // MethodCallAdder
            el.removeClass("foo");
            el.removeClass("foo");
            el.text("None");
            // MethodAssertGenerator build local variable
            Object o_19_0 = el.hasClass("foo");
            // MethodAssertGenerator build local variable
            Object o_21_0 = elClone.hasClass("foo");
            // MethodAssertGenerator build local variable
            Object o_23_0 = el.text().equals("None");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_15341 = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_15341, "");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_163538 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_163538.hasAttr(String_vc_15341);
            // MethodAssertGenerator build local variable
            Object o_32_0 = elClone.text().equals("Text");
            org.junit.Assert.fail("supportsClone_cf440433 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testRemove */
    @org.junit.Test(timeout = 10000)
    public void testRemove_cf441917_failAssert47() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            p.childNode(0).remove();
            // MethodAssertGenerator build local variable
            Object o_8_0 = p.text();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_163674 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_163672 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_163672.equals(vc_163674);
            // MethodAssertGenerator build local variable
            Object o_16_0 = org.jsoup.TextUtil.stripNewlines(p.html());
            org.junit.Assert.fail("testRemove_cf441917 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testRemove */
    @org.junit.Test(timeout = 10000)
    public void testRemove_cf442057_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            p.childNode(0).remove();
            // MethodAssertGenerator build local variable
            Object o_8_0 = p.text();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_163750 = new java.lang.String();
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.removeAttr(vc_163750);
            // MethodAssertGenerator build local variable
            Object o_17_0 = org.jsoup.TextUtil.stripNewlines(p.html());
            org.junit.Assert.fail("testRemove_cf442057 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testRemove */
    @org.junit.Test(timeout = 10000)
    public void testRemove_cf441958_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            p.childNode(0).remove();
            // MethodAssertGenerator build local variable
            Object o_8_0 = p.text();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_163695 = new java.lang.String();
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.attr(vc_163695);
            // MethodAssertGenerator build local variable
            Object o_17_0 = org.jsoup.TextUtil.stripNewlines(p.html());
            org.junit.Assert.fail("testRemove_cf441958 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testReplace */
    @org.junit.Test(timeout = 10000)
    public void testReplace_cf443061_failAssert36() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Element insert = doc.createElement("em").text("foo");
            p.childNode(1).replaceWith(insert);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_15368 = "em";
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.before(String_vc_15368);
            // MethodAssertGenerator build local variable
            Object o_18_0 = p.html();
            org.junit.Assert.fail("testReplace_cf443061 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testReplace */
    @org.junit.Test(timeout = 10000)
    public void testReplace_cf443118_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Element insert = doc.createElement("em").text("foo");
            p.childNode(1).replaceWith(insert);
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.unwrap();
            // MethodAssertGenerator build local variable
            Object o_16_0 = p.html();
            org.junit.Assert.fail("testReplace_cf443118 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testReplace */
    @org.junit.Test(timeout = 10000)
    public void testReplace_cf443125_failAssert46() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Element insert = doc.createElement("em").text("foo");
            p.childNode(1).replaceWith(insert);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_163900 = new java.lang.String();
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.wrap(vc_163900);
            // MethodAssertGenerator build local variable
            Object o_18_0 = p.html();
            org.junit.Assert.fail("testReplace_cf443125 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testReplace */
    @org.junit.Test(timeout = 10000)
    public void testReplace_cf442964_failAssert34() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Element insert = doc.createElement("em").text("foo");
            p.childNode(1).replaceWith(insert);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_163812 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_163810 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_163810.equals(vc_163812);
            // MethodAssertGenerator build local variable
            Object o_17_0 = p.html();
            org.junit.Assert.fail("testReplace_cf442964 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testReplace */
    @org.junit.Test(timeout = 10000)
    public void testReplace_cf442996_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
            org.jsoup.nodes.Element p = doc.select("p").first();
            org.jsoup.nodes.Element insert = doc.createElement("em").text("foo");
            p.childNode(1).replaceWith(insert);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_163829 = new java.lang.String();
            // StatementAddOnAssert local variable replacement
            org.jsoup.nodes.Node root = p.root();
            // StatementAdderMethod cloned existing statement
            root.absUrl(vc_163829);
            // MethodAssertGenerator build local variable
            Object o_18_0 = p.html();
            org.junit.Assert.fail("testReplace_cf442996 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#testReplace */
    @org.junit.Test(timeout = 10000)
    public void testReplace_cf443124_failAssert52_literalMutation443345_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<p>One <span>two</span> three</p>");
                org.jsoup.nodes.Element p = doc.select("p").first();
                org.jsoup.nodes.Element insert = doc.createElement("em").text("foo");
                p.childNode(1).replaceWith(insert);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_15371 = "";
                // StatementAddOnAssert local variable replacement
                org.jsoup.nodes.Node root = p.root();
                // StatementAdderMethod cloned existing statement
                root.wrap(String_vc_15371);
                // MethodAssertGenerator build local variable
                Object o_18_0 = p.html();
                org.junit.Assert.fail("testReplace_cf443124 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testReplace_cf443124_failAssert52_literalMutation443345 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#traverse */
    @org.junit.Test(timeout = 10000)
    public void traverse_cf443393_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello</p></div><div>There</div>");
            final java.lang.StringBuilder accum = new java.lang.StringBuilder();
            doc.select("div").first().traverse(new org.jsoup.select.NodeVisitor() {
                public void head(org.jsoup.nodes.Node node, int depth) {
                    accum.append((("<" + (node.nodeName())) + ">"));
                }

                public void tail(org.jsoup.nodes.Node node, int depth) {
                    accum.append((("</" + (node.nodeName())) + ">"));
                }
            });
            // StatementAdderOnAssert create null value
            java.lang.Object vc_163950 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_163948 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_163948.equals(vc_163950);
            // MethodAssertGenerator build local variable
            Object o_24_0 = accum.toString();
            org.junit.Assert.fail("traverse_cf443393 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#traverse */
    /* amplification of org.jsoup.nodes.NodeTest#traverse_cf443398 */
    @org.junit.Test(timeout = 10000)
    public void traverse_cf443398_failAssert19_literalMutation443595() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello</p></div><div>There</div>");
            final java.lang.StringBuilder accum = new java.lang.StringBuilder();
            doc.select("div").first().traverse(new org.jsoup.select.NodeVisitor() {
                public void head(org.jsoup.nodes.Node node, int depth) {
                    accum.append((("<" + (node.nodeName())) + ">"));
                }

                public void tail(org.jsoup.nodes.Node node, int depth) {
                    accum.append((("</" + (node.nodeName())) + ">"));
                }
            });
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_15376 = "<div><p>Hello</p></div>Sdiv>There</div>";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_15376, "<div><p>Hello</p></div>Sdiv>There</div>");
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_163952 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_163952.hasAttr(String_vc_15376);
            // MethodAssertGenerator build local variable
            Object o_24_0 = accum.toString();
            org.junit.Assert.fail("traverse_cf443398 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#unwrapNoChildren */
    @org.junit.Test(timeout = 10000)
    public void unwrapNoChildren_cf594146_failAssert76() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div>One <span></span> Two</div>");
            org.jsoup.nodes.Element span = doc.select("span").first();
            org.jsoup.nodes.Node node = span.unwrap();
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.TextUtil.stripNewlines(doc.body().html());
            // StatementAdderMethod cloned existing statement
            node.ensureChildNodes();
            // MethodAssertGenerator build local variable
            Object o_14_0 = node == null;
            org.junit.Assert.fail("unwrapNoChildren_cf594146 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#unwrapNoChildren */
    @org.junit.Test(timeout = 10000)
    public void unwrapNoChildren_cf593906_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div>One <span></span> Two</div>");
            org.jsoup.nodes.Element span = doc.select("span").first();
            org.jsoup.nodes.Node node = span.unwrap();
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.TextUtil.stripNewlines(doc.body().html());
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_221083 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            org.jsoup.nodes.Node vc_221080 = (org.jsoup.nodes.Node)null;
            // StatementAdderMethod cloned existing statement
            vc_221080.equals(vc_221083);
            // MethodAssertGenerator build local variable
            Object o_18_0 = node == null;
            org.junit.Assert.fail("unwrapNoChildren_cf593906 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#unwrapNoChildren */
    /* amplification of org.jsoup.nodes.NodeTest#unwrapNoChildren_cf593917_failAssert30_add594275 */
    @org.junit.Test(timeout = 10000)
    public void unwrapNoChildren_cf593917_failAssert30_add594275_literalMutation594644() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div>One <span></span> Two</div>");
            org.jsoup.nodes.Element span = doc.select("span").first();
            org.jsoup.nodes.Node node = span.unwrap();
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.TextUtil.stripNewlines(doc.body().html());
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_20754 = "<div>One <span></span> TUo</div>";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_20754, "<div>One <span></span> TUo</div>");
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            node.hasAttr(String_vc_20754);
            // StatementAdderMethod cloned existing statement
            node.hasAttr(String_vc_20754);
            // MethodAssertGenerator build local variable
            Object o_16_0 = node == null;
            org.junit.Assert.fail("unwrapNoChildren_cf593917 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.nodes.NodeTest#unwrapNoChildren */
    /* amplification of org.jsoup.nodes.NodeTest#unwrapNoChildren_cf594005_failAssert18_add594240 */
    @org.junit.Test(timeout = 10000)
    public void unwrapNoChildren_cf594005_failAssert18_add594240_literalMutation594471() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div>One <span></span> Two</div>");
            org.jsoup.nodes.Element span = doc.select("span").first();
            org.jsoup.nodes.Node node = span.unwrap();
            // MethodAssertGenerator build local variable
            Object o_8_0 = org.jsoup.TextUtil.stripNewlines(doc.body().html());
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_20757 = "<div>Ove  Two</div>";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_20757, "<div>Ove  Two</div>");
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            node.after(String_vc_20757);
            // StatementAdderMethod cloned existing statement
            node.after(String_vc_20757);
            // MethodAssertGenerator build local variable
            Object o_16_0 = node == null;
            org.junit.Assert.fail("unwrapNoChildren_cf594005 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

