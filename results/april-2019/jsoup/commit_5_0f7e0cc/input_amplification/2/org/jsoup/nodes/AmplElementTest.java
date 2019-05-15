package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void getElementsByTagName_literalMutationString183042_literalMutationNumber183559_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(reference);
            List<Element> divs = doc.getElementsByTag("div");
            int o_getElementsByTagName_literalMutationString183042__5 = divs.size();
            String o_getElementsByTagName_literalMutationString183042__6 = divs.get(0).id();
            String o_getElementsByTagName_literalMutationString183042__8 = divs.get(1).id();
            List<Element> ps = doc.getElementsByTag("p");
            int o_getElementsByTagName_literalMutationString183042__12 = ps.size();
            ((TextNode) (ps.get(0).childNode(0))).getWholeText();
            ((TextNode) (ps.get(1).childNode(-1))).getWholeText();
            List<Element> ps2 = doc.getElementsByTag("P");
            List<Element> imgs = doc.getElementsByTag("img");
            String o_getElementsByTagName_literalMutationString183042__23 = imgs.get(0).attr("src");
            List<Element> empty = doc.getElementsByTag("w|tf");
            int o_getElementsByTagName_literalMutationString183042__27 = empty.size();
            org.junit.Assert.fail("getElementsByTagName_literalMutationString183042_literalMutationNumber183559 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void moveByAppend_literalMutationNumber27700null29341_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
            Element div1 = doc.select("div").get(-1);
            Element div2 = doc.select(null).get(1);
            div1.childNodeSize();
            List<Node> children = div1.childNodes();
            children.size();
            div2.insertChildren(0, children);
            children.size();
            div1.childNodeSize();
            div2.childNodeSize();
            doc.body().html();
            org.junit.Assert.fail("moveByAppend_literalMutationNumber27700null29341 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

