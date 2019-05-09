package org.jsoup.nodes;


import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testElementSiblingIndexSameContent_literalMutationNumber184340_failAssert0_literalMutationNumber185413_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p>...<p>One</p>...<p>One</p>");
                Elements ps = doc.select("p");
                boolean boolean_1101 = 0 == (ps.get(-1).elementSiblingIndex());
                boolean boolean_1102 = 2 == (ps.get(1).elementSiblingIndex());
                boolean boolean_1103 = 2 == (ps.get(2).elementSiblingIndex());
                org.junit.Assert.fail("testElementSiblingIndexSameContent_literalMutationNumber184340 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testElementSiblingIndexSameContent_literalMutationNumber184340_failAssert0_literalMutationNumber185413 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetText_literalMutationNumber179350_failAssert0_literalMutationString180266_failAssert0() throws Exception {
        try {
            {
                String h = "<div id=1>Hello <p>there <b>now</b></p></div>";
                Document doc = Jsoup.parse(h);
                doc.text();
                doc.select("p").get(-1).text();
                Element div = doc.getElementById("2").text("Gone");
                div.text();
                doc.select("p").size();
                org.junit.Assert.fail("testSetText_literalMutationNumber179350 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testSetText_literalMutationNumber179350_failAssert0_literalMutationString180266 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCssPath_literalMutationNumber24827_failAssert0_literalMutationString26809_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=\"id1\">A</div><div>B</div>%<div class=\"c1 c2\">C</div>");
                Element divA = doc.select("div").get(-1);
                Element divB = doc.select("div").get(1);
                Element divC = doc.select("div").get(2);
                divA.cssSelector();
                divB.cssSelector();
                divC.cssSelector();
                boolean boolean_151 = divA == (doc.select(divA.cssSelector()).first());
                boolean boolean_152 = divB == (doc.select(divB.cssSelector()).first());
                boolean boolean_153 = divC == (doc.select(divC.cssSelector()).first());
                org.junit.Assert.fail("testCssPath_literalMutationNumber24827 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testCssPath_literalMutationNumber24827_failAssert0_literalMutationString26809 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

