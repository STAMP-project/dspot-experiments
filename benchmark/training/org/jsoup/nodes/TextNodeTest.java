package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test TextNodes
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class TextNodeTest {
    @Test
    public void testBlank() {
        TextNode one = new TextNode("");
        TextNode two = new TextNode("     ");
        TextNode three = new TextNode("  \n\n   ");
        TextNode four = new TextNode("Hello");
        TextNode five = new TextNode("  \nHello ");
        Assert.assertTrue(one.isBlank());
        Assert.assertTrue(two.isBlank());
        Assert.assertTrue(three.isBlank());
        Assert.assertFalse(four.isBlank());
        Assert.assertFalse(five.isBlank());
    }

    @Test
    public void testTextBean() {
        Document doc = Jsoup.parse("<p>One <span>two &amp;</span> three &amp;</p>");
        Element p = doc.select("p").first();
        Element span = doc.select("span").first();
        Assert.assertEquals("two &", span.text());
        TextNode spanText = ((TextNode) (span.childNode(0)));
        Assert.assertEquals("two &", spanText.text());
        TextNode tn = ((TextNode) (p.childNode(2)));
        Assert.assertEquals(" three &", tn.text());
        tn.text(" POW!");
        Assert.assertEquals("One <span>two &amp;</span> POW!", TextUtil.stripNewlines(p.html()));
        tn.attr(tn.nodeName(), "kablam &");
        Assert.assertEquals("kablam &", tn.text());
        Assert.assertEquals("One <span>two &amp;</span>kablam &amp;", TextUtil.stripNewlines(p.html()));
    }

    @Test
    public void testSplitText() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = ((TextNode) (div.childNode(0)));
        TextNode tail = tn.splitText(6);
        Assert.assertEquals("Hello ", tn.getWholeText());
        Assert.assertEquals("there", tail.getWholeText());
        tail.text("there!");
        Assert.assertEquals("Hello there!", div.text());
        Assert.assertTrue(((tn.parent()) == (tail.parent())));
    }

    @Test
    public void testSplitAnEmbolden() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = ((TextNode) (div.childNode(0)));
        TextNode tail = tn.splitText(6);
        tail.wrap("<b></b>");
        Assert.assertEquals("Hello <b>there</b>", TextUtil.stripNewlines(div.html()));// not great that we get \n<b>there there... must correct

    }

    @Test
    public void testWithSupplementaryCharacter() {
        Document doc = Jsoup.parse(new String(Character.toChars(135361)));
        TextNode t = doc.body().textNodes().get(0);
        Assert.assertEquals(new String(Character.toChars(135361)), t.outerHtml().trim());
    }

    @Test
    public void testLeadNodesHaveNoChildren() {
        Document doc = Jsoup.parse("<div>Hello there</div>");
        Element div = doc.select("div").first();
        TextNode tn = ((TextNode) (div.childNode(0)));
        List<Node> nodes = tn.childNodes();
        Assert.assertEquals(0, nodes.size());
    }
}

