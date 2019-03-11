package org.jsoup.nodes;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Element (DOM stuff mostly).
 *
 * @author Jonathan Hedley
 */
public class ElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test
    public void getElementsByTagName() {
        Document doc = Jsoup.parse(reference);
        List<Element> divs = doc.getElementsByTag("div");
        Assert.assertEquals(2, divs.size());
        Assert.assertEquals("div1", divs.get(0).id());
        Assert.assertEquals("div2", divs.get(1).id());
        List<Element> ps = doc.getElementsByTag("p");
        Assert.assertEquals(2, ps.size());
        Assert.assertEquals("Hello", getWholeText());
        Assert.assertEquals("Another ", getWholeText());
        List<Element> ps2 = doc.getElementsByTag("P");
        Assert.assertEquals(ps, ps2);
        List<Element> imgs = doc.getElementsByTag("img");
        Assert.assertEquals("foo.png", imgs.get(0).attr("src"));
        List<Element> empty = doc.getElementsByTag("wtf");
        Assert.assertEquals(0, empty.size());
    }

    @Test
    public void getNamespacedElementsByTag() {
        Document doc = Jsoup.parse("<div><abc:def id=1>Hello</abc:def></div>");
        Elements els = doc.getElementsByTag("abc:def");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("1", els.first().id());
        Assert.assertEquals("abc:def", els.first().tagName());
    }

    @Test
    public void testGetElementById() {
        Document doc = Jsoup.parse(reference);
        Element div = doc.getElementById("div1");
        Assert.assertEquals("div1", div.id());
        Assert.assertNull(doc.getElementById("none"));
        Document doc2 = Jsoup.parse("<div id=1><div id=2><p>Hello <span id=2>world!</span></p></div></div>");
        Element div2 = doc2.getElementById("2");
        Assert.assertEquals("div", div2.tagName());// not the span

        Element span = div2.child(0).getElementById("2");// called from <p> context should be span

        Assert.assertEquals("span", span.tagName());
    }

    @Test
    public void testGetText() {
        Document doc = Jsoup.parse(reference);
        Assert.assertEquals("Hello Another element", doc.text());
        Assert.assertEquals("Another element", doc.getElementsByTag("p").get(1).text());
    }

    @Test
    public void testGetChildText() {
        Document doc = Jsoup.parse("<p>Hello <b>there</b> now");
        Element p = doc.select("p").first();
        Assert.assertEquals("Hello there now", p.text());
        Assert.assertEquals("Hello now", p.ownText());
    }

    @Test
    public void testNormalisesText() {
        String h = "<p>Hello<p>There.</p> \n <p>Here <b>is</b> \n s<b>om</b>e text.";
        Document doc = Jsoup.parse(h);
        String text = doc.text();
        Assert.assertEquals("Hello There. Here is some text.", text);
    }

    @Test
    public void testKeepsPreText() {
        String h = "<p>Hello \n \n there.</p> <div><pre>  What\'s \n\n  that?</pre>";
        Document doc = Jsoup.parse(h);
        Assert.assertEquals("Hello there.   What\'s \n\n  that?", doc.text());
    }

    @Test
    public void testKeepsPreTextInCode() {
        String h = "<pre><code>code\n\ncode</code></pre>";
        Document doc = Jsoup.parse(h);
        Assert.assertEquals("code\n\ncode", doc.text());
        Assert.assertEquals("<pre><code>code\n\ncode</code></pre>", doc.body().html());
    }

    @Test
    public void testKeepsPreTextAtDepth() {
        String h = "<pre><code><span><b>code\n\ncode</b></span></code></pre>";
        Document doc = Jsoup.parse(h);
        Assert.assertEquals("code\n\ncode", doc.text());
        Assert.assertEquals("<pre><code><span><b>code\n\ncode</b></span></code></pre>", doc.body().html());
    }

    @Test
    public void testBrHasSpace() {
        Document doc = Jsoup.parse("<p>Hello<br>there</p>");
        Assert.assertEquals("Hello there", doc.text());
        Assert.assertEquals("Hello there", doc.select("p").first().ownText());
        doc = Jsoup.parse("<p>Hello <br> there</p>");
        Assert.assertEquals("Hello there", doc.text());
    }

    @Test
    public void testWholeText() {
        Document doc = Jsoup.parse("<p> Hello\nthere &nbsp;  </p>");
        Assert.assertEquals(" Hello\nthere \u00a0  ", doc.wholeText());
        doc = Jsoup.parse("<p>Hello  \n  there</p>");
        Assert.assertEquals("Hello  \n  there", doc.wholeText());
        doc = Jsoup.parse("<p>Hello  <div>\n  there</div></p>");
        Assert.assertEquals("Hello  \n  there", doc.wholeText());
    }

    @Test
    public void testGetSiblings() {
        Document doc = Jsoup.parse("<div><p>Hello<p id=1>there<p>this<p>is<p>an<p id=last>element</div>");
        Element p = doc.getElementById("1");
        Assert.assertEquals("there", p.text());
        Assert.assertEquals("Hello", p.previousElementSibling().text());
        Assert.assertEquals("this", p.nextElementSibling().text());
        Assert.assertEquals("Hello", p.firstElementSibling().text());
        Assert.assertEquals("element", p.lastElementSibling().text());
    }

    @Test
    public void testGetSiblingsWithDuplicateContent() {
        Document doc = Jsoup.parse("<div><p>Hello<p id=1>there<p>this<p>this<p>is<p>an<p id=last>element</div>");
        Element p = doc.getElementById("1");
        Assert.assertEquals("there", p.text());
        Assert.assertEquals("Hello", p.previousElementSibling().text());
        Assert.assertEquals("this", p.nextElementSibling().text());
        Assert.assertEquals("this", p.nextElementSibling().nextElementSibling().text());
        Assert.assertEquals("is", p.nextElementSibling().nextElementSibling().nextElementSibling().text());
        Assert.assertEquals("Hello", p.firstElementSibling().text());
        Assert.assertEquals("element", p.lastElementSibling().text());
    }

    @Test
    public void testGetParents() {
        Document doc = Jsoup.parse("<div><p>Hello <span>there</span></div>");
        Element span = doc.select("span").first();
        Elements parents = span.parents();
        Assert.assertEquals(4, parents.size());
        Assert.assertEquals("p", parents.get(0).tagName());
        Assert.assertEquals("div", parents.get(1).tagName());
        Assert.assertEquals("body", parents.get(2).tagName());
        Assert.assertEquals("html", parents.get(3).tagName());
    }

    @Test
    public void testElementSiblingIndex() {
        Document doc = Jsoup.parse("<div><p>One</p>...<p>Two</p>...<p>Three</p>");
        Elements ps = doc.select("p");
        Assert.assertTrue((0 == (ps.get(0).elementSiblingIndex())));
        Assert.assertTrue((1 == (ps.get(1).elementSiblingIndex())));
        Assert.assertTrue((2 == (ps.get(2).elementSiblingIndex())));
    }

    @Test
    public void testElementSiblingIndexSameContent() {
        Document doc = Jsoup.parse("<div><p>One</p>...<p>One</p>...<p>One</p>");
        Elements ps = doc.select("p");
        Assert.assertTrue((0 == (ps.get(0).elementSiblingIndex())));
        Assert.assertTrue((1 == (ps.get(1).elementSiblingIndex())));
        Assert.assertTrue((2 == (ps.get(2).elementSiblingIndex())));
    }

    @Test
    public void testGetElementsWithClass() {
        Document doc = Jsoup.parse("<div class='mellow yellow'><span class=mellow>Hello <b class='yellow'>Yellow!</b></span><p>Empty</p></div>");
        List<Element> els = doc.getElementsByClass("mellow");
        Assert.assertEquals(2, els.size());
        Assert.assertEquals("div", els.get(0).tagName());
        Assert.assertEquals("span", els.get(1).tagName());
        List<Element> els2 = doc.getElementsByClass("yellow");
        Assert.assertEquals(2, els2.size());
        Assert.assertEquals("div", els2.get(0).tagName());
        Assert.assertEquals("b", els2.get(1).tagName());
        List<Element> none = doc.getElementsByClass("solo");
        Assert.assertEquals(0, none.size());
    }

    @Test
    public void testGetElementsWithAttribute() {
        Document doc = Jsoup.parse("<div style='bold'><p title=qux><p><b style></b></p></div>");
        List<Element> els = doc.getElementsByAttribute("style");
        Assert.assertEquals(2, els.size());
        Assert.assertEquals("div", els.get(0).tagName());
        Assert.assertEquals("b", els.get(1).tagName());
        List<Element> none = doc.getElementsByAttribute("class");
        Assert.assertEquals(0, none.size());
    }

    @Test
    public void testGetElementsWithAttributeDash() {
        Document doc = Jsoup.parse("<meta http-equiv=content-type value=utf8 id=1> <meta name=foo content=bar id=2> <div http-equiv=content-type value=utf8 id=3>");
        Elements meta = doc.select("meta[http-equiv=content-type], meta[charset]");
        Assert.assertEquals(1, meta.size());
        Assert.assertEquals("1", meta.first().id());
    }

    @Test
    public void testGetElementsWithAttributeValue() {
        Document doc = Jsoup.parse("<div style='bold'><p><p><b style></b></p></div>");
        List<Element> els = doc.getElementsByAttributeValue("style", "bold");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("div", els.get(0).tagName());
        List<Element> none = doc.getElementsByAttributeValue("style", "none");
        Assert.assertEquals(0, none.size());
    }

    @Test
    public void testClassDomMethods() {
        Document doc = Jsoup.parse("<div><span class=' mellow yellow '>Hello <b>Yellow</b></span></div>");
        List<Element> els = doc.getElementsByAttribute("class");
        Element span = els.get(0);
        Assert.assertEquals("mellow yellow", span.className());
        Assert.assertTrue(span.hasClass("mellow"));
        Assert.assertTrue(span.hasClass("yellow"));
        Set<String> classes = span.classNames();
        Assert.assertEquals(2, classes.size());
        Assert.assertTrue(classes.contains("mellow"));
        Assert.assertTrue(classes.contains("yellow"));
        Assert.assertEquals("", doc.className());
        classes = doc.classNames();
        Assert.assertEquals(0, classes.size());
        Assert.assertFalse(doc.hasClass("mellow"));
    }

    @Test
    public void testHasClassDomMethods() {
        Tag tag = Tag.valueOf("a");
        Attributes attribs = new Attributes();
        Element el = new Element(tag, "", attribs);
        attribs.put("class", "toto");
        boolean hasClass = el.hasClass("toto");
        Assert.assertTrue(hasClass);
        attribs.put("class", " toto");
        hasClass = el.hasClass("toto");
        Assert.assertTrue(hasClass);
        attribs.put("class", "toto ");
        hasClass = el.hasClass("toto");
        Assert.assertTrue(hasClass);
        attribs.put("class", "\ttoto ");
        hasClass = el.hasClass("toto");
        Assert.assertTrue(hasClass);
        attribs.put("class", "  toto ");
        hasClass = el.hasClass("toto");
        Assert.assertTrue(hasClass);
        attribs.put("class", "ab");
        hasClass = el.hasClass("toto");
        Assert.assertFalse(hasClass);
        attribs.put("class", "     ");
        hasClass = el.hasClass("toto");
        Assert.assertFalse(hasClass);
        attribs.put("class", "tototo");
        hasClass = el.hasClass("toto");
        Assert.assertFalse(hasClass);
        attribs.put("class", "raulpismuth  ");
        hasClass = el.hasClass("raulpismuth");
        Assert.assertTrue(hasClass);
        attribs.put("class", " abcd  raulpismuth efgh ");
        hasClass = el.hasClass("raulpismuth");
        Assert.assertTrue(hasClass);
        attribs.put("class", " abcd efgh raulpismuth");
        hasClass = el.hasClass("raulpismuth");
        Assert.assertTrue(hasClass);
        attribs.put("class", " abcd efgh raulpismuth ");
        hasClass = el.hasClass("raulpismuth");
        Assert.assertTrue(hasClass);
    }

    @Test
    public void testClassUpdates() {
        Document doc = Jsoup.parse("<div class='mellow yellow'></div>");
        Element div = doc.select("div").first();
        div.addClass("green");
        Assert.assertEquals("mellow yellow green", div.className());
        div.removeClass("red");// noop

        div.removeClass("yellow");
        Assert.assertEquals("mellow green", div.className());
        div.toggleClass("green").toggleClass("red");
        Assert.assertEquals("mellow red", div.className());
    }

    @Test
    public void testOuterHtml() {
        Document doc = Jsoup.parse("<div title='Tags &amp;c.'><img src=foo.png><p><!-- comment -->Hello<p>there");
        Assert.assertEquals("<html><head></head><body><div title=\"Tags &amp;c.\"><img src=\"foo.png\"><p><!-- comment -->Hello</p><p>there</p></div></body></html>", TextUtil.stripNewlines(doc.outerHtml()));
    }

    @Test
    public void testInnerHtml() {
        Document doc = Jsoup.parse("<div>\n <p>Hello</p> </div>");
        Assert.assertEquals("<p>Hello</p>", doc.getElementsByTag("div").get(0).html());
    }

    @Test
    public void testFormatHtml() {
        Document doc = Jsoup.parse("<title>Format test</title><div><p>Hello <span>jsoup <span>users</span></span></p><p>Good.</p></div>");
        Assert.assertEquals("<html>\n <head>\n  <title>Format test</title>\n </head>\n <body>\n  <div>\n   <p>Hello <span>jsoup <span>users</span></span></p>\n   <p>Good.</p>\n  </div>\n </body>\n</html>", doc.html());
    }

    @Test
    public void testFormatOutline() {
        Document doc = Jsoup.parse("<title>Format test</title><div><p>Hello <span>jsoup <span>users</span></span></p><p>Good.</p></div>");
        doc.outputSettings().outline(true);
        Assert.assertEquals("<html>\n <head>\n  <title>Format test</title>\n </head>\n <body>\n  <div>\n   <p>\n    Hello \n    <span>\n     jsoup \n     <span>users</span>\n    </span>\n   </p>\n   <p>Good.</p>\n  </div>\n </body>\n</html>", doc.html());
    }

    @Test
    public void testSetIndent() {
        Document doc = Jsoup.parse("<div><p>Hello\nthere</p></div>");
        doc.outputSettings().indentAmount(0);
        Assert.assertEquals("<html>\n<head></head>\n<body>\n<div>\n<p>Hello there</p>\n</div>\n</body>\n</html>", doc.html());
    }

    @Test
    public void testNotPretty() {
        Document doc = Jsoup.parse("<div>   \n<p>Hello\n there\n</p></div>");
        doc.outputSettings().prettyPrint(false);
        Assert.assertEquals("<html><head></head><body><div>   \n<p>Hello\n there\n</p></div></body></html>", doc.html());
        Element div = doc.select("div").first();
        Assert.assertEquals("   \n<p>Hello\n there\n</p>", div.html());
    }

    @Test
    public void testEmptyElementFormatHtml() {
        // don't put newlines into empty blocks
        Document doc = Jsoup.parse("<section><div></div></section>");
        Assert.assertEquals("<section>\n <div></div>\n</section>", doc.select("section").first().outerHtml());
    }

    @Test
    public void testNoIndentOnScriptAndStyle() {
        // don't newline+indent closing </script> and </style> tags
        Document doc = Jsoup.parse("<script>one\ntwo</script>\n<style>three\nfour</style>");
        Assert.assertEquals("<script>one\ntwo</script> \n<style>three\nfour</style>", doc.head().html());
    }

    @Test
    public void testContainerOutput() {
        Document doc = Jsoup.parse("<title>Hello there</title> <div><p>Hello</p><p>there</p></div> <div>Another</div>");
        Assert.assertEquals("<title>Hello there</title>", doc.select("title").first().outerHtml());
        Assert.assertEquals("<div>\n <p>Hello</p>\n <p>there</p>\n</div>", doc.select("div").first().outerHtml());
        Assert.assertEquals("<div>\n <p>Hello</p>\n <p>there</p>\n</div> \n<div>\n Another\n</div>", doc.select("body").first().html());
    }

    @Test
    public void testSetText() {
        String h = "<div id=1>Hello <p>there <b>now</b></p></div>";
        Document doc = Jsoup.parse(h);
        Assert.assertEquals("Hello there now", doc.text());// need to sort out node whitespace

        Assert.assertEquals("there now", doc.select("p").get(0).text());
        Element div = text("Gone");
        Assert.assertEquals("Gone", div.text());
        Assert.assertEquals(0, doc.select("p").size());
    }

    @Test
    public void testAddNewElement() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.appendElement("p").text("there");
        div.appendElement("P").attr("CLASS", "second").text("now");
        // manually specifying tag and attributes should maintain case based on parser settings
        Assert.assertEquals("<html><head></head><body><div id=\"1\"><p>Hello</p><p>there</p><p class=\"second\">now</p></div></body></html>", TextUtil.stripNewlines(doc.html()));
        // check sibling index (with short circuit on reindexChildren):
        Elements ps = doc.select("p");
        for (int i = 0; i < (ps.size()); i++) {
            Assert.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testAddBooleanAttribute() {
        Element div = new Element(Tag.valueOf("div"), "");
        div.attr("true", true);
        div.attr("false", "value");
        div.attr("false", false);
        Assert.assertTrue(div.hasAttr("true"));
        Assert.assertEquals("", div.attr("true"));
        List<Attribute> attributes = div.attributes().asList();
        Assert.assertEquals("There should be one attribute", 1, attributes.size());
        Assert.assertTrue("Attribute should be boolean", ((attributes.get(0)) instanceof BooleanAttribute));
        Assert.assertFalse(div.hasAttr("false"));
        Assert.assertEquals("<div true></div>", div.outerHtml());
    }

    @Test
    public void testAppendRowToTable() {
        Document doc = Jsoup.parse("<table><tr><td>1</td></tr></table>");
        Element table = doc.select("tbody").first();
        table.append("<tr><td>2</td></tr>");
        Assert.assertEquals("<table><tbody><tr><td>1</td></tr><tr><td>2</td></tr></tbody></table>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testPrependRowToTable() {
        Document doc = Jsoup.parse("<table><tr><td>1</td></tr></table>");
        Element table = doc.select("tbody").first();
        table.prepend("<tr><td>2</td></tr>");
        Assert.assertEquals("<table><tbody><tr><td>2</td></tr><tr><td>1</td></tr></tbody></table>", TextUtil.stripNewlines(doc.body().html()));
        // check sibling index (reindexChildren):
        Elements ps = doc.select("tr");
        for (int i = 0; i < (ps.size()); i++) {
            Assert.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testPrependElement() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prependElement("p").text("Before");
        Assert.assertEquals("Before", div.child(0).text());
        Assert.assertEquals("Hello", div.child(1).text());
    }

    @Test
    public void testAddNewText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.appendText(" there & now >");
        Assert.assertEquals("<p>Hello</p> there &amp; now &gt;", TextUtil.stripNewlines(div.html()));
    }

    @Test
    public void testPrependText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prependText("there & now > ");
        Assert.assertEquals("there & now > Hello", div.text());
        Assert.assertEquals("there &amp; now &gt; <p>Hello</p>", TextUtil.stripNewlines(div.html()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnAddNullText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.appendText(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnPrependNullText() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prependText(null);
    }

    @Test
    public void testAddNewHtml() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.append("<p>there</p><p>now</p>");
        Assert.assertEquals("<p>Hello</p><p>there</p><p>now</p>", TextUtil.stripNewlines(div.html()));
        // check sibling index (no reindexChildren):
        Elements ps = doc.select("p");
        for (int i = 0; i < (ps.size()); i++) {
            Assert.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testPrependNewHtml() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.prepend("<p>there</p><p>now</p>");
        Assert.assertEquals("<p>there</p><p>now</p><p>Hello</p>", TextUtil.stripNewlines(div.html()));
        // check sibling index (reindexChildren):
        Elements ps = doc.select("p");
        for (int i = 0; i < (ps.size()); i++) {
            Assert.assertEquals(i, ps.get(i).siblingIndex);
        }
    }

    @Test
    public void testSetHtml() {
        Document doc = Jsoup.parse("<div id=1><p>Hello</p></div>");
        Element div = doc.getElementById("1");
        div.html("<p>there</p><p>now</p>");
        Assert.assertEquals("<p>there</p><p>now</p>", TextUtil.stripNewlines(div.html()));
    }

    @Test
    public void testSetHtmlTitle() {
        Document doc = Jsoup.parse("<html><head id=2><title id=1></title></head></html>");
        Element title = doc.getElementById("1");
        title.html("good");
        Assert.assertEquals("good", title.html());
        title.html("<i>bad</i>");
        Assert.assertEquals("&lt;i&gt;bad&lt;/i&gt;", title.html());
        Element head = doc.getElementById("2");
        head.html("<title><i>bad</i></title>");
        Assert.assertEquals("<title>&lt;i&gt;bad&lt;/i&gt;</title>", head.html());
    }

    @Test
    public void testWrap() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>");
        Element p = doc.select("p").first();
        p.wrap("<div class='head'></div>");
        Assert.assertEquals("<div><div class=\"head\"><p>Hello</p></div><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));
        Element ret = p.wrap("<div><div class=foo></div><p>What?</p></div>");
        Assert.assertEquals("<div><div class=\"head\"><div><div class=\"foo\"><p>Hello</p></div><p>What?</p></div></div><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));
        Assert.assertEquals(ret, p);
    }

    @Test
    public void before() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>");
        Element p1 = doc.select("p").first();
        p1.before("<div>one</div><div>two</div>");
        Assert.assertEquals("<div><div>one</div><div>two</div><p>Hello</p><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));
        doc.select("p").last().before("<p>Three</p><!-- four -->");
        Assert.assertEquals("<div><div>one</div><div>two</div><p>Hello</p><p>Three</p><!-- four --><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void after() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p>There</p></div>");
        Element p1 = doc.select("p").first();
        p1.after("<div>one</div><div>two</div>");
        Assert.assertEquals("<div><p>Hello</p><div>one</div><div>two</div><p>There</p></div>", TextUtil.stripNewlines(doc.body().html()));
        doc.select("p").last().after("<p>Three</p><!-- four -->");
        Assert.assertEquals("<div><p>Hello</p><div>one</div><div>two</div><p>There</p><p>Three</p><!-- four --></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testWrapWithRemainder() {
        Document doc = Jsoup.parse("<div><p>Hello</p></div>");
        Element p = doc.select("p").first();
        p.wrap("<div class='head'></div><p>There!</p>");
        Assert.assertEquals("<div><div class=\"head\"><p>Hello</p><p>There!</p></div></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testHasText() {
        Document doc = Jsoup.parse("<div><p>Hello</p><p></p></div>");
        Element div = doc.select("div").first();
        Elements ps = doc.select("p");
        Assert.assertTrue(div.hasText());
        Assert.assertTrue(ps.first().hasText());
        Assert.assertFalse(ps.last().hasText());
    }

    @Test
    public void dataset() {
        Document doc = Jsoup.parse("<div id=1 data-name=jsoup class=new data-package=jar>Hello</div><p id=2>Hello</p>");
        Element div = doc.select("div").first();
        Map<String, String> dataset = div.dataset();
        Attributes attributes = div.attributes();
        // size, get, set, add, remove
        Assert.assertEquals(2, dataset.size());
        Assert.assertEquals("jsoup", dataset.get("name"));
        Assert.assertEquals("jar", dataset.get("package"));
        dataset.put("name", "jsoup updated");
        dataset.put("language", "java");
        dataset.remove("package");
        Assert.assertEquals(2, dataset.size());
        Assert.assertEquals(4, attributes.size());
        Assert.assertEquals("jsoup updated", attributes.get("data-name"));
        Assert.assertEquals("jsoup updated", dataset.get("name"));
        Assert.assertEquals("java", attributes.get("data-language"));
        Assert.assertEquals("java", dataset.get("language"));
        attributes.put("data-food", "bacon");
        Assert.assertEquals(3, dataset.size());
        Assert.assertEquals("bacon", dataset.get("food"));
        attributes.put("data-", "empty");
        Assert.assertEquals(null, dataset.get(""));// data- is not a data attribute

        Element p = doc.select("p").first();
        Assert.assertEquals(0, p.dataset().size());
    }

    @Test
    public void parentlessToString() {
        Document doc = Jsoup.parse("<img src='foo'>");
        Element img = doc.select("img").first();
        Assert.assertEquals("<img src=\"foo\">", img.toString());
        img.remove();// lost its parent

        Assert.assertEquals("<img src=\"foo\">", img.toString());
    }

    @Test
    public void testClone() {
        Document doc = Jsoup.parse("<div><p>One<p><span>Two</div>");
        Element p = doc.select("p").get(1);
        Element clone = p.clone();
        Assert.assertNull(clone.parent());// should be orphaned

        Assert.assertEquals(0, clone.siblingIndex);
        Assert.assertEquals(1, p.siblingIndex);
        Assert.assertNotNull(p.parent());
        clone.append("<span>Three");
        Assert.assertEquals("<p><span>Two</span><span>Three</span></p>", TextUtil.stripNewlines(clone.outerHtml()));
        Assert.assertEquals("<div><p>One</p><p><span>Two</span></p></div>", TextUtil.stripNewlines(doc.body().html()));// not modified

        doc.body().appendChild(clone);// adopt

        Assert.assertNotNull(clone.parent());
        Assert.assertEquals("<div><p>One</p><p><span>Two</span></p></div><p><span>Two</span><span>Three</span></p>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testClonesClassnames() {
        Document doc = Jsoup.parse("<div class='one two'></div>");
        Element div = doc.select("div").first();
        Set<String> classes = div.classNames();
        Assert.assertEquals(2, classes.size());
        Assert.assertTrue(classes.contains("one"));
        Assert.assertTrue(classes.contains("two"));
        Element copy = div.clone();
        Set<String> copyClasses = copy.classNames();
        Assert.assertEquals(2, copyClasses.size());
        Assert.assertTrue(copyClasses.contains("one"));
        Assert.assertTrue(copyClasses.contains("two"));
        copyClasses.add("three");
        copyClasses.remove("one");
        Assert.assertTrue(classes.contains("one"));
        Assert.assertFalse(classes.contains("three"));
        Assert.assertFalse(copyClasses.contains("one"));
        Assert.assertTrue(copyClasses.contains("three"));
        Assert.assertEquals("", div.html());
        Assert.assertEquals("", copy.html());
    }

    @Test
    public void testShallowClone() {
        String base = "http://example.com/";
        Document doc = Jsoup.parse("<div id=1 class=one><p id=2 class=two>One", base);
        Element d = doc.selectFirst("div");
        Element p = doc.selectFirst("p");
        TextNode t = p.textNodes().get(0);
        Element d2 = d.shallowClone();
        Element p2 = p.shallowClone();
        TextNode t2 = ((TextNode) (t.shallowClone()));
        Assert.assertEquals(1, d.childNodeSize());
        Assert.assertEquals(0, d2.childNodeSize());
        Assert.assertEquals(1, p.childNodeSize());
        Assert.assertEquals(0, p2.childNodeSize());
        Assert.assertEquals("", p2.text());
        Assert.assertEquals("two", p2.className());
        Assert.assertEquals("One", t2.text());
        d2.append("<p id=3>Three");
        Assert.assertEquals(1, d2.childNodeSize());
        Assert.assertEquals("Three", d2.text());
        Assert.assertEquals("One", d.text());
        Assert.assertEquals(base, d2.baseUri());
    }

    @Test
    public void testTagNameSet() {
        Document doc = Jsoup.parse("<div><i>Hello</i>");
        doc.select("i").first().tagName("em");
        Assert.assertEquals(0, doc.select("i").size());
        Assert.assertEquals(1, doc.select("em").size());
        Assert.assertEquals("<em>Hello</em>", doc.select("div").first().html());
    }

    @Test
    public void testHtmlContainsOuter() {
        Document doc = Jsoup.parse("<title>Check</title> <div>Hello there</div>");
        doc.outputSettings().indentAmount(0);
        Assert.assertTrue(doc.html().contains(doc.select("title").outerHtml()));
        Assert.assertTrue(doc.html().contains(doc.select("div").outerHtml()));
    }

    @Test
    public void testGetTextNodes() {
        Document doc = Jsoup.parse("<p>One <span>Two</span> Three <br> Four</p>");
        List<TextNode> textNodes = doc.select("p").first().textNodes();
        Assert.assertEquals(3, textNodes.size());
        Assert.assertEquals("One ", textNodes.get(0).text());
        Assert.assertEquals(" Three ", textNodes.get(1).text());
        Assert.assertEquals(" Four", textNodes.get(2).text());
        Assert.assertEquals(0, doc.select("br").first().textNodes().size());
    }

    @Test
    public void testManipulateTextNodes() {
        Document doc = Jsoup.parse("<p>One <span>Two</span> Three <br> Four</p>");
        Element p = doc.select("p").first();
        List<TextNode> textNodes = p.textNodes();
        textNodes.get(1).text(" three-more ");
        textNodes.get(2).splitText(3).text("-ur");
        Assert.assertEquals("One Two three-more Fo-ur", p.text());
        Assert.assertEquals("One three-more Fo-ur", p.ownText());
        Assert.assertEquals(4, p.textNodes().size());// grew because of split

    }

    @Test
    public void testGetDataNodes() {
        Document doc = Jsoup.parse("<script>One Two</script> <style>Three Four</style> <p>Fix Six</p>");
        Element script = doc.select("script").first();
        Element style = doc.select("style").first();
        Element p = doc.select("p").first();
        List<DataNode> scriptData = script.dataNodes();
        Assert.assertEquals(1, scriptData.size());
        Assert.assertEquals("One Two", scriptData.get(0).getWholeData());
        List<DataNode> styleData = style.dataNodes();
        Assert.assertEquals(1, styleData.size());
        Assert.assertEquals("Three Four", styleData.get(0).getWholeData());
        List<DataNode> pData = p.dataNodes();
        Assert.assertEquals(0, pData.size());
    }

    @Test
    public void elementIsNotASiblingOfItself() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
        Element p2 = doc.select("p").get(1);
        Assert.assertEquals("Two", p2.text());
        Elements els = p2.siblingElements();
        Assert.assertEquals(2, els.size());
        Assert.assertEquals("<p>One</p>", els.get(0).outerHtml());
        Assert.assertEquals("<p>Three</p>", els.get(1).outerHtml());
    }

    @Test
    public void testChildThrowsIndexOutOfBoundsOnMissing() {
        Document doc = Jsoup.parse("<div><p>One</p><p>Two</p></div>");
        Element div = doc.select("div").first();
        Assert.assertEquals(2, div.children().size());
        Assert.assertEquals("One", div.child(0).text());
        try {
            div.child(3);
            Assert.fail("Should throw index out of bounds");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void moveByAppend() {
        // test for https://github.com/jhy/jsoup/issues/239
        // can empty an element and append its children to another element
        Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
        Element div1 = doc.select("div").get(0);
        Element div2 = doc.select("div").get(1);
        Assert.assertEquals(4, div1.childNodeSize());
        List<Node> children = div1.childNodes();
        Assert.assertEquals(4, children.size());
        div2.insertChildren(0, children);
        Assert.assertEquals(0, children.size());// children is backed by div1.childNodes, moved, so should be 0 now

        Assert.assertEquals(0, div1.childNodeSize());
        Assert.assertEquals(4, div2.childNodeSize());
        Assert.assertEquals("<div id=\"1\"></div>\n<div id=\"2\">\n Text \n <p>One</p> Text \n <p>Two</p>\n</div>", doc.body().html());
    }

    @Test
    public void insertChildrenArgumentValidation() {
        Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
        Element div1 = doc.select("div").get(0);
        Element div2 = doc.select("div").get(1);
        List<Node> children = div1.childNodes();
        try {
            div2.insertChildren(6, children);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            div2.insertChildren((-5), children);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            div2.insertChildren(0, ((Collection<? extends Node>) (null)));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void insertChildrenAtPosition() {
        Document doc = Jsoup.parse("<div id=1>Text1 <p>One</p> Text2 <p>Two</p></div><div id=2>Text3 <p>Three</p></div>");
        Element div1 = doc.select("div").get(0);
        Elements p1s = div1.select("p");
        Element div2 = doc.select("div").get(1);
        Assert.assertEquals(2, div2.childNodeSize());
        div2.insertChildren((-1), p1s);
        Assert.assertEquals(2, div1.childNodeSize());// moved two out

        Assert.assertEquals(4, div2.childNodeSize());
        Assert.assertEquals(3, p1s.get(1).siblingIndex());// should be last

        List<Node> els = new ArrayList<>();
        Element el1 = text("Span1");
        Element el2 = text("Span2");
        TextNode tn1 = new TextNode("Text4");
        els.add(el1);
        els.add(el2);
        els.add(tn1);
        Assert.assertNull(el1.parent());
        div2.insertChildren((-2), els);
        Assert.assertEquals(div2, el1.parent());
        Assert.assertEquals(7, div2.childNodeSize());
        Assert.assertEquals(3, el1.siblingIndex());
        Assert.assertEquals(4, el2.siblingIndex());
        Assert.assertEquals(5, tn1.siblingIndex());
    }

    @Test
    public void insertChildrenAsCopy() {
        Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
        Element div1 = doc.select("div").get(0);
        Element div2 = doc.select("div").get(1);
        Elements ps = doc.select("p").clone();
        ps.first().text("One cloned");
        div2.insertChildren((-1), ps);
        Assert.assertEquals(4, div1.childNodeSize());// not moved -- cloned

        Assert.assertEquals(2, div2.childNodeSize());
        Assert.assertEquals("<div id=\"1\">Text <p>One</p> Text <p>Two</p></div><div id=\"2\"><p>One cloned</p><p>Two</p></div>", TextUtil.stripNewlines(doc.body().html()));
    }

    @Test
    public void testCssPath() {
        Document doc = Jsoup.parse("<div id=\"id1\">A</div><div>B</div><div class=\"c1 c2\">C</div>");
        Element divA = doc.select("div").get(0);
        Element divB = doc.select("div").get(1);
        Element divC = doc.select("div").get(2);
        Assert.assertEquals(divA.cssSelector(), "#id1");
        Assert.assertEquals(divB.cssSelector(), "html > body > div:nth-child(2)");
        Assert.assertEquals(divC.cssSelector(), "html > body > div.c1.c2");
        Assert.assertTrue((divA == (doc.select(divA.cssSelector()).first())));
        Assert.assertTrue((divB == (doc.select(divB.cssSelector()).first())));
        Assert.assertTrue((divC == (doc.select(divC.cssSelector()).first())));
    }

    @Test
    public void testClassNames() {
        Document doc = Jsoup.parse("<div class=\"c1 c2\">C</div>");
        Element div = doc.select("div").get(0);
        Assert.assertEquals("c1 c2", div.className());
        final Set<String> set1 = div.classNames();
        final Object[] arr1 = set1.toArray();
        Assert.assertTrue(((arr1.length) == 2));
        Assert.assertEquals("c1", arr1[0]);
        Assert.assertEquals("c2", arr1[1]);
        // Changes to the set should not be reflected in the Elements getters
        set1.add("c3");
        Assert.assertTrue((2 == (div.classNames().size())));
        Assert.assertEquals("c1 c2", div.className());
        // Update the class names to a fresh set
        final Set<String> newSet = new LinkedHashSet<>(3);
        newSet.addAll(set1);
        newSet.add("c3");
        div.classNames(newSet);
        Assert.assertEquals("c1 c2 c3", div.className());
        final Set<String> set2 = div.classNames();
        final Object[] arr2 = set2.toArray();
        Assert.assertTrue(((arr2.length) == 3));
        Assert.assertEquals("c1", arr2[0]);
        Assert.assertEquals("c2", arr2[1]);
        Assert.assertEquals("c3", arr2[2]);
    }

    @Test
    public void testHashAndEqualsAndValue() {
        // .equals and hashcode are identity. value is content.
        String doc1 = "<div id=1><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>" + "<div id=2><p class=one>One</p><p class=one>One</p><p class=one>Two</p><p class=two>One</p></div>";
        Document doc = Jsoup.parse(doc1);
        Elements els = doc.select("p");
        /* for (Element el : els) {
        System.out.println(el.hashCode() + " - " + el.outerHtml());
        }

        0 1534787905 - <p class="one">One</p>
        1 1534787905 - <p class="one">One</p>
        2 1539683239 - <p class="one">Two</p>
        3 1535455211 - <p class="two">One</p>
        4 1534787905 - <p class="one">One</p>
        5 1534787905 - <p class="one">One</p>
        6 1539683239 - <p class="one">Two</p>
        7 1535455211 - <p class="two">One</p>
         */
        Assert.assertEquals(8, els.size());
        Element e0 = els.get(0);
        Element e1 = els.get(1);
        Element e2 = els.get(2);
        Element e3 = els.get(3);
        Element e4 = els.get(4);
        Element e5 = els.get(5);
        Element e6 = els.get(6);
        Element e7 = els.get(7);
        Assert.assertEquals(e0, e0);
        Assert.assertTrue(e0.hasSameValue(e1));
        Assert.assertTrue(e0.hasSameValue(e4));
        Assert.assertTrue(e0.hasSameValue(e5));
        Assert.assertFalse(e0.equals(e2));
        Assert.assertFalse(e0.hasSameValue(e2));
        Assert.assertFalse(e0.hasSameValue(e3));
        Assert.assertFalse(e0.hasSameValue(e6));
        Assert.assertFalse(e0.hasSameValue(e7));
        Assert.assertEquals(e0.hashCode(), e0.hashCode());
        Assert.assertFalse(((e0.hashCode()) == (e2.hashCode())));
        Assert.assertFalse(((e0.hashCode()) == (e3.hashCode())));
        Assert.assertFalse(((e0.hashCode()) == (e6.hashCode())));
        Assert.assertFalse(((e0.hashCode()) == (e7.hashCode())));
    }

    @Test
    public void testRelativeUrls() {
        String html = "<body><a href='./one.html'>One</a> <a href='two.html'>two</a> <a href='../three.html'>Three</a> <a href='//example2.com/four/'>Four</a> <a href='https://example2.com/five/'>Five</a>";
        Document doc = Jsoup.parse(html, "http://example.com/bar/");
        Elements els = doc.select("a");
        Assert.assertEquals("http://example.com/bar/one.html", els.get(0).absUrl("href"));
        Assert.assertEquals("http://example.com/bar/two.html", els.get(1).absUrl("href"));
        Assert.assertEquals("http://example.com/three.html", els.get(2).absUrl("href"));
        Assert.assertEquals("http://example2.com/four/", els.get(3).absUrl("href"));
        Assert.assertEquals("https://example2.com/five/", els.get(4).absUrl("href"));
    }

    @Test
    public void appendMustCorrectlyMoveChildrenInsideOneParentElement() {
        Document doc = new Document("");
        Element body = doc.appendElement("body");
        body.appendElement("div1");
        body.appendElement("div2");
        final Element div3 = body.appendElement("div3");
        div3.text("Check");
        final Element div4 = body.appendElement("div4");
        ArrayList<Element> toMove = new ArrayList<>();
        toMove.add(div3);
        toMove.add(div4);
        body.insertChildren(0, toMove);
        String result = doc.toString().replaceAll("\\s+", "");
        Assert.assertEquals("<body><div3>Check</div3><div4></div4><div1></div1><div2></div2></body>", result);
    }

    @Test
    public void testHashcodeIsStableWithContentChanges() {
        Element root = new Element(Tag.valueOf("root"), "");
        HashSet<Element> set = new HashSet<>();
        // Add root node:
        set.add(root);
        root.appendChild(new Element(Tag.valueOf("a"), ""));
        Assert.assertTrue(set.contains(root));
    }

    @Test
    public void testNamespacedElements() {
        // Namespaces with ns:tag in HTML must be translated to ns|tag in CSS.
        String html = "<html><body><fb:comments /></body></html>";
        Document doc = Jsoup.parse(html, "http://example.com/bar/");
        Elements els = doc.select("fb|comments");
        Assert.assertEquals(1, els.size());
        Assert.assertEquals("html > body > fb|comments", els.get(0).cssSelector());
    }

    @Test
    public void testChainedRemoveAttributes() {
        String html = "<a one two three four>Text</a>";
        Document doc = Jsoup.parse(html);
        Element a = doc.select("a").first();
        a.removeAttr("zero").removeAttr("one").removeAttr("two").removeAttr("three").removeAttr("four").removeAttr("five");
        Assert.assertEquals("<a>Text</a>", a.outerHtml());
    }

    @Test
    public void testLoopedRemoveAttributes() {
        String html = "<a one two three four>Text</a><p foo>Two</p>";
        Document doc = Jsoup.parse(html);
        for (Element el : doc.getAllElements()) {
            el.clearAttributes();
        }
        Assert.assertEquals("<a>Text</a>\n<p>Two</p>", doc.body().html());
    }

    @Test
    public void testIs() {
        String html = "<div><p>One <a class=big>Two</a> Three</p><p>Another</p>";
        Document doc = Jsoup.parse(html);
        Element p = doc.select("p").first();
        Assert.assertTrue(p.is("p"));
        Assert.assertFalse(p.is("div"));
        Assert.assertTrue(p.is("p:has(a)"));
        Assert.assertTrue(p.is("p:first-child"));
        Assert.assertFalse(p.is("p:last-child"));
        Assert.assertTrue(p.is("*"));
        Assert.assertTrue(p.is("div p"));
        Element q = doc.select("p").last();
        Assert.assertTrue(q.is("p"));
        Assert.assertTrue(q.is("p ~ p"));
        Assert.assertTrue(q.is("p + p"));
        Assert.assertTrue(q.is("p:last-child"));
        Assert.assertFalse(q.is("p a"));
        Assert.assertFalse(q.is("a"));
    }

    @Test
    public void elementByTagName() {
        Element a = new Element("P");
        Assert.assertTrue(a.tagName().equals("P"));
    }

    @Test
    public void testChildrenElements() {
        String html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>";
        Document doc = Jsoup.parse(html);
        Element div = doc.select("div").first();
        Element p = doc.select("p").first();
        Element span = doc.select("span").first();
        Element foo = doc.select("foo").first();
        Element img = doc.select("img").first();
        Elements docChildren = div.children();
        Assert.assertEquals(2, docChildren.size());
        Assert.assertEquals("<p><a>One</a></p>", docChildren.get(0).outerHtml());
        Assert.assertEquals("<p><a>Two</a></p>", docChildren.get(1).outerHtml());
        Assert.assertEquals(3, div.childNodes().size());
        Assert.assertEquals("Three", div.childNodes().get(2).outerHtml());
        Assert.assertEquals(1, p.children().size());
        Assert.assertEquals("One", p.children().text());
        Assert.assertEquals(0, span.children().size());
        Assert.assertEquals(1, span.childNodes().size());
        Assert.assertEquals("Four", span.childNodes().get(0).outerHtml());
        Assert.assertEquals(0, foo.children().size());
        Assert.assertEquals(0, foo.childNodes().size());
        Assert.assertEquals(0, img.children().size());
        Assert.assertEquals(0, img.childNodes().size());
    }

    @Test
    public void testShadowElementsAreUpdated() {
        String html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>";
        Document doc = Jsoup.parse(html);
        Element div = doc.select("div").first();
        Elements els = div.children();
        List<Node> nodes = div.childNodes();
        Assert.assertEquals(2, els.size());// the two Ps

        Assert.assertEquals(3, nodes.size());// the "Three" textnode

        Element p3 = new Element("p").text("P3");
        Element p4 = new Element("p").text("P4");
        div.insertChildren(1, p3);
        div.insertChildren(3, p4);
        Elements els2 = div.children();
        // first els should not have changed
        Assert.assertEquals(2, els.size());
        Assert.assertEquals(4, els2.size());
        Assert.assertEquals(("<p><a>One</a></p>\n" + (("<p>P3</p>\n" + "<p><a>Two</a></p>\n") + "<p>P4</p>Three")), div.html());
        Assert.assertEquals("P3", els2.get(1).text());
        Assert.assertEquals("P4", els2.get(3).text());
        p3.after("<span>Another</span");
        Elements els3 = div.children();
        Assert.assertEquals(5, els3.size());
        Assert.assertEquals("span", els3.get(2).tagName());
        Assert.assertEquals("Another", els3.get(2).text());
        Assert.assertEquals(("<p><a>One</a></p>\n" + ((("<p>P3</p>\n" + "<span>Another</span>\n") + "<p><a>Two</a></p>\n") + "<p>P4</p>Three")), div.html());
    }

    @Test
    public void classNamesAndAttributeNameIsCaseInsensitive() {
        String html = "<p Class='SomeText AnotherText'>One</p>";
        Document doc = Jsoup.parse(html);
        Element p = doc.select("p").first();
        Assert.assertEquals("SomeText AnotherText", p.className());
        Assert.assertTrue(p.classNames().contains("SomeText"));
        Assert.assertTrue(p.classNames().contains("AnotherText"));
        Assert.assertTrue(p.hasClass("SomeText"));
        Assert.assertTrue(p.hasClass("sometext"));
        Assert.assertTrue(p.hasClass("AnotherText"));
        Assert.assertTrue(p.hasClass("anothertext"));
        Element p1 = doc.select(".SomeText").first();
        Element p2 = doc.select(".sometext").first();
        Element p3 = doc.select("[class=SomeText AnotherText]").first();
        Element p4 = doc.select("[Class=SomeText AnotherText]").first();
        Element p5 = doc.select("[class=sometext anothertext]").first();
        Element p6 = doc.select("[class=SomeText AnotherText]").first();
        Element p7 = doc.select("[class^=sometext]").first();
        Element p8 = doc.select("[class$=nothertext]").first();
        Element p9 = doc.select("[class^=sometext]").first();
        Element p10 = doc.select("[class$=AnotherText]").first();
        Assert.assertEquals("One", p1.text());
        Assert.assertEquals(p1, p2);
        Assert.assertEquals(p1, p3);
        Assert.assertEquals(p1, p4);
        Assert.assertEquals(p1, p5);
        Assert.assertEquals(p1, p6);
        Assert.assertEquals(p1, p7);
        Assert.assertEquals(p1, p8);
        Assert.assertEquals(p1, p9);
        Assert.assertEquals(p1, p10);
    }

    @Test
    public void testAppendTo() {
        String parentHtml = "<div class='a'></div>";
        String childHtml = "<div class='b'></div><p>Two</p>";
        Document parentDoc = Jsoup.parse(parentHtml);
        Element parent = parentDoc.body();
        Document childDoc = Jsoup.parse(childHtml);
        Element div = childDoc.select("div").first();
        Element p = childDoc.select("p").first();
        Element appendTo1 = div.appendTo(parent);
        Assert.assertEquals(div, appendTo1);
        Element appendTo2 = p.appendTo(div);
        Assert.assertEquals(p, appendTo2);
        Assert.assertEquals("<div class=\"a\"></div>\n<div class=\"b\">\n <p>Two</p>\n</div>", parentDoc.body().html());
        Assert.assertEquals("", childDoc.body().html());// got moved out

    }

    @Test
    public void testNormalizesNbspInText() {
        String escaped = "You can't always get what you&nbsp;want.";
        String withNbsp = "You can't always get what you?want.";// there is an nbsp char in there

        Document doc = Jsoup.parse(("<p>" + escaped));
        Element p = doc.select("p").first();
        Assert.assertEquals("You can't always get what you want.", p.text());// text is normalized

        Assert.assertEquals((("<p>" + escaped) + "</p>"), p.outerHtml());// html / whole text keeps &nbsp;

        Assert.assertEquals(withNbsp, getWholeText());
        Assert.assertEquals(160, withNbsp.charAt(29));
        Element matched = doc.select("p:contains(get what you want)").first();
        Assert.assertEquals("p", matched.nodeName());
        Assert.assertTrue(matched.is(":containsOwn(get what you want)"));
    }

    @Test
    public void testNormalizesInvisiblesInText() {
        // return Character.getType(c) == 16 && (c == 8203 || c == 8204 || c == 8205 || c == 173);
        String escaped = "This&shy;is&#x200b;one&#x200c;long&#x200d;word";
        String decoded = "This\u00adis\u200bone\u200clong\u200dword";// browser would not display those soft hyphens / other chars, so we don't want them in the text

        Document doc = Jsoup.parse(("<p>" + escaped));
        Element p = doc.select("p").first();
        doc.outputSettings().charset("ascii");// so that the outer html is easier to see with escaped invisibles

        Assert.assertEquals("Thisisonelongword", p.text());// text is normalized

        Assert.assertEquals((("<p>" + escaped) + "</p>"), p.outerHtml());// html / whole text keeps &shy etc;

        Assert.assertEquals(decoded, getWholeText());
        Element matched = doc.select("p:contains(Thisisonelongword)").first();// really just oneloneword, no invisibles

        Assert.assertEquals("p", matched.nodeName());
        Assert.assertTrue(matched.is(":containsOwn(Thisisonelongword)"));
    }

    @Test
    public void testRemoveBeforeIndex() {
        Document doc = Jsoup.parse("<html><body><div><p>before1</p><p>before2</p><p>XXX</p><p>after1</p><p>after2</p></div></body></html>", "");
        Element body = doc.select("body").first();
        Elements elems = body.select("p:matchesOwn(XXX)");
        Element xElem = elems.first();
        Elements beforeX = xElem.parent().getElementsByIndexLessThan(xElem.elementSiblingIndex());
        for (Element p : beforeX) {
            p.remove();
        }
        Assert.assertEquals("<body><div><p>XXX</p><p>after1</p><p>after2</p></div></body>", TextUtil.stripNewlines(body.outerHtml()));
    }

    @Test
    public void testRemoveAfterIndex() {
        Document doc2 = Jsoup.parse("<html><body><div><p>before1</p><p>before2</p><p>XXX</p><p>after1</p><p>after2</p></div></body></html>", "");
        Element body = doc2.select("body").first();
        Elements elems = body.select("p:matchesOwn(XXX)");
        Element xElem = elems.first();
        Elements afterX = xElem.parent().getElementsByIndexGreaterThan(xElem.elementSiblingIndex());
        for (Element p : afterX) {
            p.remove();
        }
        Assert.assertEquals("<body><div><p>before1</p><p>before2</p><p>XXX</p></div></body>", TextUtil.stripNewlines(body.outerHtml()));
    }

    @Test
    public void whiteSpaceClassElement() {
        Tag tag = Tag.valueOf("a");
        Attributes attribs = new Attributes();
        Element el = new Element(tag, "", attribs);
        attribs.put("class", "abc ");
        boolean hasClass = el.hasClass("ab");
        Assert.assertFalse(hasClass);
    }

    @Test
    public void testNextElementSiblingAfterClone() {
        // via https://github.com/jhy/jsoup/issues/951
        String html = "<!DOCTYPE html><html lang=\"en\"><head></head><body><div>Initial element</div></body></html>";
        String expectedText = "New element";
        String cloneExpect = "New element in clone";
        Document original = Jsoup.parse(html);
        Document clone = original.clone();
        Element originalElement = original.body().child(0);
        originalElement.after((("<div>" + expectedText) + "</div>"));
        Element originalNextElementSibling = originalElement.nextElementSibling();
        Element originalNextSibling = ((Element) (originalElement.nextSibling()));
        Assert.assertEquals(expectedText, originalNextElementSibling.text());
        Assert.assertEquals(expectedText, originalNextSibling.text());
        Element cloneElement = clone.body().child(0);
        cloneElement.after((("<div>" + cloneExpect) + "</div>"));
        Element cloneNextElementSibling = cloneElement.nextElementSibling();
        Element cloneNextSibling = ((Element) (cloneElement.nextSibling()));
        Assert.assertEquals(cloneExpect, cloneNextElementSibling.text());
        Assert.assertEquals(cloneExpect, cloneNextSibling.text());
    }

    @Test
    public void testRemovingEmptyClassAttributeWhenLastClassRemoved() {
        // https://github.com/jhy/jsoup/issues/947
        Document doc = Jsoup.parse("<img class=\"one two\" />");
        Element img = doc.select("img").first();
        img.removeClass("one");
        img.removeClass("two");
        Assert.assertFalse(doc.body().html().contains("class=\"\""));
    }

    @Test
    public void booleanAttributeOutput() {
        Document doc = Jsoup.parse("<img src=foo noshade='' nohref async=async autofocus=false>");
        Element img = doc.selectFirst("img");
        Assert.assertEquals("<img src=\"foo\" noshade nohref async autofocus=\"false\">", img.outerHtml());
    }

    @Test
    public void textHasSpaceAfterBlockTags() {
        Document doc = Jsoup.parse("<div>One</div>Two");
        Assert.assertEquals("One Two", doc.text());
    }

    @Test
    public void textHasSpaceBetweenDivAndCenterTags() {
        Document doc = Jsoup.parse("<div>One</div><div>Two</div><center>Three</center><center>Four</center>");
        Assert.assertEquals("One Two Three Four", doc.text());
    }

    @Test
    public void testNextElementSiblings() {
        Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul> Not An Element but a node") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
        Element element = doc.getElementById("a");
        Elements elementSiblings = element.nextElementSiblings();
        Assert.assertNotNull(elementSiblings);
        Assert.assertEquals(2, elementSiblings.size());
        Assert.assertEquals("b", elementSiblings.get(0).id());
        Assert.assertEquals("c", elementSiblings.get(1).id());
        Element element1 = doc.getElementById("b");
        List<Element> elementSiblings1 = element1.nextElementSiblings();
        Assert.assertNotNull(elementSiblings1);
        Assert.assertEquals(1, elementSiblings1.size());
        Assert.assertEquals("c", elementSiblings1.get(0).id());
        Element element2 = doc.getElementById("c");
        List<Element> elementSiblings2 = element2.nextElementSiblings();
        Assert.assertEquals(0, elementSiblings2.size());
        Element ul = doc.getElementById("ul");
        List<Element> elementSiblings3 = ul.nextElementSiblings();
        Assert.assertNotNull(elementSiblings3);
        Assert.assertEquals(1, elementSiblings3.size());
        Assert.assertEquals("div", elementSiblings3.get(0).id());
        Element div = doc.getElementById("div");
        List<Element> elementSiblings4 = div.nextElementSiblings();
        Assert.assertEquals(0, elementSiblings4.size());
    }

    @Test
    public void testPreviousElementSiblings() {
        Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
        Element element = doc.getElementById("b");
        Elements elementSiblings = element.previousElementSiblings();
        Assert.assertNotNull(elementSiblings);
        Assert.assertEquals(1, elementSiblings.size());
        Assert.assertEquals("a", elementSiblings.get(0).id());
        Element element1 = doc.getElementById("a");
        List<Element> elementSiblings1 = element1.previousElementSiblings();
        Assert.assertEquals(0, elementSiblings1.size());
        Element element2 = doc.getElementById("c");
        List<Element> elementSiblings2 = element2.previousElementSiblings();
        Assert.assertNotNull(elementSiblings2);
        Assert.assertEquals(2, elementSiblings2.size());
        Assert.assertEquals("b", elementSiblings2.get(0).id());
        Assert.assertEquals("a", elementSiblings2.get(1).id());
        Element ul = doc.getElementById("ul");
        List<Element> elementSiblings3 = ul.previousElementSiblings();
        Assert.assertEquals(0, elementSiblings3.size());
    }
}

