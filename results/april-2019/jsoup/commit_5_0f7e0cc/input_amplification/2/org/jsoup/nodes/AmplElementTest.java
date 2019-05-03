package org.jsoup.nodes;


import java.util.Collection;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testElementSiblingIndexSameContent_literalMutationNumber211797_failAssert0_add213455_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p>...<p>One</p>...<p>One</p>");
                Elements ps = doc.select("p");
                ps.get(-1);
                boolean boolean_1103 = 0 == (ps.get(-1).elementSiblingIndex());
                boolean boolean_1104 = 1 == (ps.get(1).elementSiblingIndex());
                boolean boolean_1105 = 2 == (ps.get(2).elementSiblingIndex());
                org.junit.Assert.fail("testElementSiblingIndexSameContent_literalMutationNumber211797 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testElementSiblingIndexSameContent_literalMutationNumber211797_failAssert0_add213455 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testElementSiblingIndexSameContent_add211809_literalMutationNumber212066_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p>...<p>One</p>...<p>One</p>");
            Elements o_testElementSiblingIndexSameContent_add211809__3 = doc.select("p");
            Elements ps = doc.select("p");
            boolean boolean_1040 = 0 == (ps.get(-1).elementSiblingIndex());
            boolean boolean_1041 = 1 == (ps.get(1).elementSiblingIndex());
            boolean boolean_1042 = 2 == (ps.get(2).elementSiblingIndex());
            org.junit.Assert.fail("testElementSiblingIndexSameContent_add211809_literalMutationNumber212066 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetElementsWithClass_add86142_literalMutationNumber86453_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div class='mellow yellow'><span class=mellow>Hello <b class='yellow'>Yellow!</b></span><p>Empty</p></div>");
            List<Element> els = doc.getElementsByClass("mellow");
            int o_testGetElementsWithClass_add86142__5 = els.size();
            String o_testGetElementsWithClass_add86142__6 = els.get(-1).tagName();
            String o_testGetElementsWithClass_add86142__8 = els.get(1).tagName();
            List<Element> els2 = doc.getElementsByClass("yellow");
            int o_testGetElementsWithClass_add86142__12 = els2.size();
            int o_testGetElementsWithClass_add86142__13 = els2.size();
            String o_testGetElementsWithClass_add86142__14 = els2.get(0).tagName();
            String o_testGetElementsWithClass_add86142__16 = els2.get(1).tagName();
            List<Element> none = doc.getElementsByClass("solo");
            int o_testGetElementsWithClass_add86142__20 = none.size();
            org.junit.Assert.fail("testGetElementsWithClass_add86142_literalMutationNumber86453 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetText_literalMutationNumber206064_failAssert0_literalMutationString207009_failAssert0() throws Exception {
        try {
            {
                String h = "<div id=1>Hello <p>there <b>now</b></p></div>";
                Document doc = Jsoup.parse(h);
                doc.text();
                doc.select("p").get(-1).text();
                Element div = doc.getElementById("1").text("Gone");
                div.text();
                doc.select(" Hello\nthere \u00a0  ").size();
                org.junit.Assert.fail("testSetText_literalMutationNumber206064 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testSetText_literalMutationNumber206064_failAssert0_literalMutationString207009 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void insertChildrenArgumentValidation_literalMutationNumber57879_failAssert0_literalMutationNumber58904_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
                Element div1 = doc.select("div").get(-1);
                Element div2 = doc.select("div").get(0);
                List<Node> children = div1.childNodes();
                {
                    div2.insertChildren(6, children);
                }
                {
                    div2.insertChildren((-5), children);
                }
                {
                    div2.insertChildren(0, ((Collection<? extends Node>) (null)));
                }
                org.junit.Assert.fail("insertChildrenArgumentValidation_literalMutationNumber57879 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("insertChildrenArgumentValidation_literalMutationNumber57879_failAssert0_literalMutationNumber58904 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCssPath_add26837_literalMutationNumber27494_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=\"id1\">A</div><div>B</div><div class=\"c1 c2\">C</div>");
            Element o_testCssPath_add26837__3 = doc.select("div").get(0);
            Element divA = doc.select("div").get(-1);
            Element divB = doc.select("div").get(1);
            Element divC = doc.select("div").get(2);
            String o_testCssPath_add26837__14 = divA.cssSelector();
            String o_testCssPath_add26837__15 = divB.cssSelector();
            String o_testCssPath_add26837__16 = divC.cssSelector();
            boolean boolean_60 = divA == (doc.select(divA.cssSelector()).first());
            boolean boolean_61 = divB == (doc.select(divB.cssSelector()).first());
            boolean boolean_62 = divC == (doc.select(divC.cssSelector()).first());
            org.junit.Assert.fail("testCssPath_add26837_literalMutationNumber27494 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testChildrenElements_literalMutationNumber232112_failAssert0_literalMutationNumber237000_failAssert0() throws Exception {
        try {
            {
                String html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>";
                Document doc = Jsoup.parse(html);
                Element div = doc.select("div").first();
                Element p = doc.select("p").first();
                Element span = doc.select("span").first();
                Element foo = doc.select("foo").first();
                Element img = doc.select("img").first();
                Elements docChildren = div.children();
                docChildren.size();
                docChildren.get(0).outerHtml();
                docChildren.get(1).outerHtml();
                div.childNodes().size();
                div.childNodes().get(2).outerHtml();
                p.children().size();
                p.children().text();
                span.children().size();
                span.childNodes().size();
                span.childNodes().get(-2).outerHtml();
                foo.children().size();
                foo.childNodes().size();
                img.children().size();
                img.childNodes().size();
                org.junit.Assert.fail("testChildrenElements_literalMutationNumber232112 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testChildrenElements_literalMutationNumber232112_failAssert0_literalMutationNumber237000 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

