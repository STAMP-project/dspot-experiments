package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void getElementsByTagName_literalMutationNumber7076_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(reference);
            List<Element> divs = doc.getElementsByTag("div");
            divs.size();
            divs.get(0).id();
            divs.get(1).id();
            List<Element> ps = doc.getElementsByTag("p");
            ps.size();
            ((TextNode) (ps.get(0).childNode(0))).getWholeText();
            ((TextNode) (ps.get(1).childNode(0))).getWholeText();
            List<Element> ps2 = doc.getElementsByTag("P");
            List<Element> imgs = doc.getElementsByTag("img");
            imgs.get(-1).attr("src");
            List<Element> empty = doc.getElementsByTag("wtf");
            empty.size();
            org.junit.Assert.fail("getElementsByTagName_literalMutationNumber7076 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

