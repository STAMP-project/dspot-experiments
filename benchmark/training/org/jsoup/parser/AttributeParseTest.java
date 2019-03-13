package org.jsoup.parser;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.BooleanAttribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite for attribute parser.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class AttributeParseTest {
    @Test
    public void parsesRoughAttributeString() {
        String html = "<a id=\"123\" class=\"baz = \'bar\'\" style = \'border: 2px\'qux zim foo = 12 mux=18 />";
        // should be: <id=123>, <class=baz = 'bar'>, <qux=>, <zim=>, <foo=12>, <mux.=18>
        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assert.assertEquals(7, attr.size());
        Assert.assertEquals("123", attr.get("id"));
        Assert.assertEquals("baz = 'bar'", attr.get("class"));
        Assert.assertEquals("border: 2px", attr.get("style"));
        Assert.assertEquals("", attr.get("qux"));
        Assert.assertEquals("", attr.get("zim"));
        Assert.assertEquals("12", attr.get("foo"));
        Assert.assertEquals("18", attr.get("mux"));
    }

    @Test
    public void handlesNewLinesAndReturns() {
        String html = "<a\r\nfoo=\'bar\r\nqux\'\r\nbar\r\n=\r\ntwo>One</a>";
        Element el = Jsoup.parse(html).select("a").first();
        Assert.assertEquals(2, el.attributes().size());
        Assert.assertEquals("bar\r\nqux", el.attr("foo"));// currently preserves newlines in quoted attributes. todo confirm if should.

        Assert.assertEquals("two", el.attr("bar"));
    }

    @Test
    public void parsesEmptyString() {
        String html = "<a />";
        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assert.assertEquals(0, attr.size());
    }

    @Test
    public void canStartWithEq() {
        String html = "<a =empty />";
        Element el = Jsoup.parse(html).getElementsByTag("a").get(0);
        Attributes attr = el.attributes();
        Assert.assertEquals(1, attr.size());
        Assert.assertTrue(attr.hasKey("=empty"));
        Assert.assertEquals("", attr.get("=empty"));
    }

    @Test
    public void strictAttributeUnescapes() {
        String html = "<a id=1 href='?foo=bar&mid&lt=true'>One</a> <a id=2 href='?foo=bar&lt;qux&lg=1'>Two</a>";
        Elements els = Jsoup.parse(html).select("a");
        Assert.assertEquals("?foo=bar&mid&lt=true", els.first().attr("href"));
        Assert.assertEquals("?foo=bar<qux&lg=1", els.last().attr("href"));
    }

    @Test
    public void moreAttributeUnescapes() {
        String html = "<a href='&wr_id=123&mid-size=true&ok=&wr'>Check</a>";
        Elements els = Jsoup.parse(html).select("a");
        Assert.assertEquals("&wr_id=123&mid-size=true&ok=&wr", els.first().attr("href"));
    }

    @Test
    public void parsesBooleanAttributes() {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Element el = Jsoup.parse(html).select("a").first();
        Assert.assertEquals("123", el.attr("normal"));
        Assert.assertEquals("", el.attr("boolean"));
        Assert.assertEquals("", el.attr("empty"));
        List<Attribute> attributes = el.attributes().asList();
        Assert.assertEquals("There should be 3 attribute present", 3, attributes.size());
        // Assuming the list order always follows the parsed html
        Assert.assertFalse("'normal' attribute should not be boolean", ((attributes.get(0)) instanceof BooleanAttribute));
        Assert.assertTrue("'boolean' attribute should be boolean", ((attributes.get(1)) instanceof BooleanAttribute));
        Assert.assertFalse("'empty' attribute should not be boolean", ((attributes.get(2)) instanceof BooleanAttribute));
        Assert.assertEquals(html, el.outerHtml());
    }

    @Test
    public void dropsSlashFromAttributeName() {
        String html = "<img /onerror='doMyJob'/>";
        Document doc = Jsoup.parse(html);
        Assert.assertTrue("SelfClosingStartTag ignores last character", ((doc.select("img[onerror]").size()) != 0));
        Assert.assertEquals("<img onerror=\"doMyJob\">", doc.body().html());
        doc = Jsoup.parse(html, "", Parser.xmlParser());
        Assert.assertEquals("<img onerror=\"doMyJob\" />", doc.html());
    }
}

