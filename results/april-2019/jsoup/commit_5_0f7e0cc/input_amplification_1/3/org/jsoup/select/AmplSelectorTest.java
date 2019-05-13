package org.jsoup.select;


import org.jsoup.Jsoup;
import org.jsoup.MultiLocaleRule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class AmplSelectorTest {
    @Rule
    public MultiLocaleRule rule = new MultiLocaleRule();

    @Test(timeout = 10000)
    public void parentChildStar_add56196_literalMutationNumber56414_failAssert0() throws Exception {
        try {
            String h = "<div id=1><p>Hello<p><b>there</b></p></div><div id=2><span>Hi</span></div>";
            Document doc = Jsoup.parse(h);
            Elements divChilds = doc.select("div > *");
            int o_parentChildStar_add56196__6 = divChilds.size();
            Element o_parentChildStar_add56196__7 = divChilds.get(-1);
            String o_parentChildStar_add56196__8 = divChilds.get(0).tagName();
            String o_parentChildStar_add56196__10 = divChilds.get(1).tagName();
            String o_parentChildStar_add56196__12 = divChilds.get(2).tagName();
            org.junit.Assert.fail("parentChildStar_add56196_literalMutationNumber56414 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoLessThan_literalMutationNumber252736_failAssert0_add254032_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:lt(2)");
                ps.size();
                ps.get(-1).text();
                ps.get(1);
                ps.get(1).text();
                ps.get(2).text();
                org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252736 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252736_failAssert0_add254032 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoLessThan_literalMutationNumber252736_failAssert0_add254033_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:lt(2)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                ps.get(2).text();
                ps.get(2).text();
                org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252736 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252736_failAssert0_add254033 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoGreaterThan_literalMutationNumber209247_failAssert0_add210123_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
                doc.select("div p:gt(0)");
                Elements ps = doc.select("div p:gt(0)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber209247 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber209247_failAssert0_add210123 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoCombined_literalMutationNumber115933_failAssert0_add116476_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>");
                doc.select("div.foo p:gt(0)");
                Elements ps = doc.select("div.foo p:gt(0)");
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoCombined_literalMutationNumber115933 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoCombined_literalMutationNumber115933_failAssert0_add116476 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoHas_literalMutationNumber258306_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>");
            Elements divs1 = doc.select("div:has(span)");
            divs1.size();
            divs1.get(0).id();
            divs1.get(1).id();
            Elements divs2 = doc.select("div:has([class])");
            divs2.size();
            divs2.get(-1).id();
            Elements divs3 = doc.select("div:has(span, p)");
            divs3.size();
            divs3.get(0).id();
            divs3.get(1).id();
            divs3.get(2).id();
            Elements els1 = doc.body().select(":has(p)");
            els1.size();
            els1.first().tagName();
            els1.get(1).id();
            els1.get(2).id();
            org.junit.Assert.fail("testPseudoHas_literalMutationNumber258306 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void splitOnBr_literalMutationNumber135654_failAssert0_literalMutationNumber136376_failAssert0_add140564_failAssert0() throws Exception {
        try {
            {
                {
                    String html = "<div><p>One<br>Two<br>Three</p></div>";
                    Document doc = Jsoup.parse(html);
                    Elements els = doc.select("p:matchText");
                    els.size();
                    els.get(-1).text();
                    els.get(1);
                    els.get(1).text();
                    els.get(3).toString();
                    org.junit.Assert.fail("splitOnBr_literalMutationNumber135654 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("splitOnBr_literalMutationNumber135654_failAssert0_literalMutationNumber136376 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("splitOnBr_literalMutationNumber135654_failAssert0_literalMutationNumber136376_failAssert0_add140564 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

