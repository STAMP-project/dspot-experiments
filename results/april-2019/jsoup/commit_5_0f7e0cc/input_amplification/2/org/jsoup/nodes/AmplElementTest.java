package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void getElementsByTagName_literalMutationNumber191248_failAssert0_add198537_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(reference);
                List<Element> divs = doc.getElementsByTag("div");
                divs.size();
                divs.get(-1).id();
                divs.get(1).id();
                List<Element> ps = doc.getElementsByTag("p");
                ps.size();
                ps.get(0);
                ((TextNode) (ps.get(0).childNode(0))).getWholeText();
                ((TextNode) (ps.get(1).childNode(0))).getWholeText();
                List<Element> ps2 = doc.getElementsByTag("P");
                List<Element> imgs = doc.getElementsByTag("img");
                imgs.get(0).attr("src");
                List<Element> empty = doc.getElementsByTag("wtf");
                empty.size();
                org.junit.Assert.fail("getElementsByTagName_literalMutationNumber191248 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("getElementsByTagName_literalMutationNumber191248_failAssert0_add198537 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCssPath_literalMutationNumber24811_failAssert0_add29425_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse("<div id=\"id1\">A</div><div>B</div><div class=\"c1 c2\">C</div>");
                Document doc = Jsoup.parse("<div id=\"id1\">A</div><div>B</div><div class=\"c1 c2\">C</div>");
                Element divA = doc.select("div").get(-1);
                Element divB = doc.select("div").get(1);
                Element divC = doc.select("div").get(2);
                divA.cssSelector();
                divB.cssSelector();
                divC.cssSelector();
                boolean boolean_43 = divA == (doc.select(divA.cssSelector()).first());
                boolean boolean_44 = divB == (doc.select(divB.cssSelector()).first());
                boolean boolean_45 = divC == (doc.select(divC.cssSelector()).first());
                org.junit.Assert.fail("testCssPath_literalMutationNumber24811 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testCssPath_literalMutationNumber24811_failAssert0_add29425 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

