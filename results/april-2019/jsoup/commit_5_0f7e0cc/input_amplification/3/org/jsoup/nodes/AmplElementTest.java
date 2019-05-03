package org.jsoup.nodes;


import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void getElementsByTagName_literalMutationNumber799407_failAssert0_literalMutationNumber803761_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(reference);
                List<Element> divs = doc.getElementsByTag("div");
                divs.size();
                divs.get(0).id();
                divs.get(1).id();
                List<Element> ps = doc.getElementsByTag("p");
                ps.size();
                ((TextNode) (ps.get(-1).childNode(0))).getWholeText();
                ((TextNode) (ps.get(1).childNode(0))).getWholeText();
                List<Element> ps2 = doc.getElementsByTag("P");
                List<Element> imgs = doc.getElementsByTag("img");
                imgs.get(0).attr("src");
                List<Element> empty = doc.getElementsByTag("wtf");
                empty.size();
                org.junit.Assert.fail("getElementsByTagName_literalMutationNumber799407 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("getElementsByTagName_literalMutationNumber799407_failAssert0_literalMutationNumber803761 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetElementById_add380891_literalMutationNumber381148_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(reference);
            Element div = doc.getElementById("div1");
            String o_testGetElementById_add380891__5 = div.id();
            Element o_testGetElementById_add380891__6 = doc.getElementById("none");
            Document doc2 = Jsoup.parse("<div id=1><div id=2><p>Hello <span id=2>world!</span></p></div></div>");
            Element div2 = doc2.getElementById("2");
            String o_testGetElementById_add380891__11 = div2.tagName();
            Element span = div2.child(-1).getElementById("2");
            String o_testGetElementById_add380891__16 = span.tagName();
            String o_testGetElementById_add380891__17 = span.tagName();
            org.junit.Assert.fail("testGetElementById_add380891_literalMutationNumber381148 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetText_literalMutationNumber336732_literalMutationNumber336830_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(reference);
            String o_testGetText_literalMutationNumber336732__3 = doc.text();
            String o_testGetText_literalMutationNumber336732__4 = doc.getElementsByTag("p").get(-1).text();
            org.junit.Assert.fail("testGetText_literalMutationNumber336732_literalMutationNumber336830 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testElementSiblingIndex_literalMutationNumber191220_failAssert0_add192907_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p>...<p>Two</p>...<p>Three</p>");
                Elements ps = doc.select("p");
                ps.get(-1).elementSiblingIndex();
                boolean boolean_285 = 0 == (ps.get(-1).elementSiblingIndex());
                boolean boolean_286 = 1 == (ps.get(1).elementSiblingIndex());
                boolean boolean_287 = 2 == (ps.get(2).elementSiblingIndex());
                org.junit.Assert.fail("testElementSiblingIndex_literalMutationNumber191220 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testElementSiblingIndex_literalMutationNumber191220_failAssert0_add192907 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testElementSiblingIndexSameContent_literalMutationString746401_literalMutationNumber746920_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p>...<Lp>One</p>...<p>One</p>");
            Elements ps = doc.select("p");
            boolean boolean_1076 = 0 == (ps.get(-1).elementSiblingIndex());
            boolean boolean_1077 = 1 == (ps.get(1).elementSiblingIndex());
            boolean boolean_1078 = 2 == (ps.get(2).elementSiblingIndex());
            org.junit.Assert.fail("testElementSiblingIndexSameContent_literalMutationString746401_literalMutationNumber746920 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInnerHtml_literalMutationNumber396783_failAssert0_add397427_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div>\n <p>Hello</p> </div>");
                doc.getElementsByTag("div").get(-1).html();
                org.junit.Assert.fail("testInnerHtml_literalMutationNumber396783 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testInnerHtml_literalMutationNumber396783_failAssert0_add397427 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSetText_literalMutationNumber717073_failAssert0_add719183_failAssert0() throws Exception {
        try {
            {
                String h = "<div id=1>Hello <p>there <b>now</b></p></div>";
                Document doc = Jsoup.parse(h);
                doc.text();
                doc.select("p").get(-1).text();
                Element div = doc.getElementById("1").text("Gone");
                div.text();
                doc.select("p").size();
                doc.select("p").size();
                org.junit.Assert.fail("testSetText_literalMutationNumber717073 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testSetText_literalMutationNumber717073_failAssert0_add719183 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testShallowClone_add1259478_literalMutationNumber1259693_failAssert0() throws Exception {
        try {
            String base = "http://example.com/";
            Document doc = Jsoup.parse("<div id=1 class=one><p id=2 class=two>One", base);
            Element d = doc.selectFirst("div");
            Element p = doc.selectFirst("p");
            TextNode t = p.textNodes().get(-1);
            Element d2 = d.shallowClone();
            Element p2 = p.shallowClone();
            TextNode t2 = ((TextNode) (t.shallowClone()));
            int o_testShallowClone_add1259478__17 = d.childNodeSize();
            int o_testShallowClone_add1259478__18 = d2.childNodeSize();
            int o_testShallowClone_add1259478__19 = p.childNodeSize();
            int o_testShallowClone_add1259478__20 = p2.childNodeSize();
            String o_testShallowClone_add1259478__21 = p2.text();
            String o_testShallowClone_add1259478__22 = p2.text();
            String o_testShallowClone_add1259478__23 = p2.className();
            String o_testShallowClone_add1259478__24 = t2.text();
            Element o_testShallowClone_add1259478__25 = d2.append("<p id=3>Three");
            int o_testShallowClone_add1259478__26 = d2.childNodeSize();
            String o_testShallowClone_add1259478__27 = d2.text();
            String o_testShallowClone_add1259478__28 = d.text();
            String o_testShallowClone_add1259478__29 = d2.baseUri();
            org.junit.Assert.fail("testShallowClone_add1259478_literalMutationNumber1259693 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetTextNodes_literalMutationNumber229002_failAssert0_literalMutationNumber230019_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<p>One <span>Two</span> Three <br> Four</p>");
                List<TextNode> textNodes = doc.select("p").first().textNodes();
                textNodes.size();
                textNodes.get(-1).text();
                textNodes.get(0).text();
                textNodes.get(2).text();
                doc.select("br").first().textNodes().size();
                org.junit.Assert.fail("testGetTextNodes_literalMutationNumber229002 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGetTextNodes_literalMutationNumber229002_failAssert0_literalMutationNumber230019 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetDataNodes_literalMutationNumber971673_failAssert0_literalMutationNumber972749_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<script>One Two</script> <style>Three Four</style> <p>Fix Six</p>");
                Element script = doc.select("script").first();
                Element style = doc.select("style").first();
                Element p = doc.select("p").first();
                List<DataNode> scriptData = script.dataNodes();
                scriptData.size();
                scriptData.get(-1).getWholeData();
                List<DataNode> styleData = style.dataNodes();
                styleData.size();
                styleData.get(0).getWholeData();
                List<DataNode> pData = p.dataNodes();
                pData.size();
                org.junit.Assert.fail("testGetDataNodes_literalMutationNumber971673 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGetDataNodes_literalMutationNumber971673_failAssert0_literalMutationNumber972749 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void insertChildrenAtPosition_literalMutationNumber637020_failAssert0_add657423_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text1 <p>One</p> Text2 <p>Two</p></div><div id=2>Text3 <p>Three</p></div>");
                Element div1 = doc.select("div").get(-1);
                Elements p1s = div1.select("p");
                Element div2 = doc.select("div").get(1);
                div2.childNodeSize();
                div2.insertChildren((-1), p1s);
                div1.childNodeSize();
                div1.childNodeSize();
                div2.childNodeSize();
                p1s.get(1).siblingIndex();
                List<Node> els = new ArrayList<>();
                Element el1 = new Element(Tag.valueOf("span"), "").text("Span1");
                Element el2 = new Element(Tag.valueOf("span"), "").text("Span2");
                TextNode tn1 = new TextNode("Text4");
                els.add(el1);
                els.add(el2);
                els.add(tn1);
                el1.parent();
                div2.insertChildren((-2), els);
                el1.parent();
                div2.childNodeSize();
                el1.siblingIndex();
                el2.siblingIndex();
                tn1.siblingIndex();
                org.junit.Assert.fail("insertChildrenAtPosition_literalMutationNumber637020 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("insertChildrenAtPosition_literalMutationNumber637020_failAssert0_add657423 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void insertChildrenAtPosition_literalMutationNumber637020_failAssert0_add657430_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text1 <p>One</p> Text2 <p>Two</p></div><div id=2>Text3 <p>Three</p></div>");
                Element div1 = doc.select("div").get(-1);
                Elements p1s = div1.select("p");
                Element div2 = doc.select("div").get(1);
                div2.childNodeSize();
                div2.insertChildren((-1), p1s);
                div1.childNodeSize();
                div2.childNodeSize();
                p1s.get(1).siblingIndex();
                List<Node> els = new ArrayList<>();
                Element el1 = new Element(Tag.valueOf("span"), "").text("Span1");
                Tag.valueOf("span");
                Element el2 = new Element(Tag.valueOf("span"), "").text("Span2");
                TextNode tn1 = new TextNode("Text4");
                els.add(el1);
                els.add(el2);
                els.add(tn1);
                el1.parent();
                div2.insertChildren((-2), els);
                el1.parent();
                div2.childNodeSize();
                el1.siblingIndex();
                el2.siblingIndex();
                tn1.siblingIndex();
                org.junit.Assert.fail("insertChildrenAtPosition_literalMutationNumber637020 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("insertChildrenAtPosition_literalMutationNumber637020_failAssert0_add657430 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCssPath_add109667_literalMutationNumber110503_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=\"id1\">A</div><div>B</div><div class=\"c1 c2\">C</div>");
            Element divA = doc.select("div").get(-1);
            Element divB = doc.select("div").get(1);
            Element divC = doc.select("div").get(2);
            String o_testCssPath_add109667__12 = divA.cssSelector();
            String o_testCssPath_add109667__13 = divB.cssSelector();
            String o_testCssPath_add109667__14 = divC.cssSelector();
            boolean boolean_75 = divA == (doc.select(divA.cssSelector()).first());
            Elements o_testCssPath_add109667__19 = doc.select(divB.cssSelector());
            boolean boolean_76 = divB == (doc.select(divB.cssSelector()).first());
            boolean boolean_77 = divC == (doc.select(divC.cssSelector()).first());
            org.junit.Assert.fail("testCssPath_add109667_literalMutationNumber110503 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testChildrenElements_literalMutationString826909_failAssert0_literalMutationNumber831519_failAssert0() throws Exception {
        try {
            {
                String html = "<div><p><a>One</a></p><p><a>Two</a></p>Three</div><span>Four</span><foo></foo><img>";
                Document doc = Jsoup.parse(html);
                Element div = doc.select("div").first();
                Element p = doc.select("p").first();
                Element span = doc.select("span").first();
                Element foo = doc.select("fmoo").first();
                Element img = doc.select("img").first();
                Elements docChildren = div.children();
                docChildren.size();
                docChildren.get(-1).outerHtml();
                docChildren.get(1).outerHtml();
                div.childNodes().size();
                div.childNodes().get(2).outerHtml();
                p.children().size();
                p.children().text();
                span.children().size();
                span.childNodes().size();
                span.childNodes().get(0).outerHtml();
                foo.children().size();
                foo.childNodes().size();
                img.children().size();
                img.childNodes().size();
                org.junit.Assert.fail("testChildrenElements_literalMutationString826909 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testChildrenElements_literalMutationString826909_failAssert0_literalMutationNumber831519 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblingAfterClone_literalMutationNumber882423_failAssert0null894326_failAssert0() throws Exception {
        try {
            {
                String html = "<!DOCTYPE html><html lang=\"en\"><head></head><body><div>Initial element</div></body></html>";
                String expectedText = "New element";
                String cloneExpect = null;
                Document original = Jsoup.parse(html);
                Document clone = original.clone();
                Element originalElement = original.body().child(-1);
                originalElement.after((("<div>" + expectedText) + "</div>"));
                Element originalNextElementSibling = originalElement.nextElementSibling();
                Element originalNextSibling = ((Element) (originalElement.nextSibling()));
                originalNextElementSibling.text();
                originalNextSibling.text();
                Element cloneElement = clone.body().child(0);
                cloneElement.after((("<div>" + cloneExpect) + "</div>"));
                Element cloneNextElementSibling = cloneElement.nextElementSibling();
                Element cloneNextSibling = ((Element) (cloneElement.nextSibling()));
                cloneNextElementSibling.text();
                cloneNextSibling.text();
                org.junit.Assert.fail("testNextElementSiblingAfterClone_literalMutationNumber882423 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblingAfterClone_literalMutationNumber882423_failAssert0null894326 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

