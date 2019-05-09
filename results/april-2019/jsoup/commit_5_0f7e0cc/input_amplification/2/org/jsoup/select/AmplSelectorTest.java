package org.jsoup.select;


import org.jsoup.Jsoup;
import org.jsoup.MultiLocaleRule;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class AmplSelectorTest {
    @Rule
    public MultiLocaleRule rule = new MultiLocaleRule();

    @Test(timeout = 10000)
    public void parentChildStar_literalMutationNumber20790_failAssert0_add22221_failAssert0() throws Exception {
        try {
            {
                String h = "<div id=1><p>Hello<p><b>there</b></p></div><div id=2><span>Hi</span></div>";
                Document doc = Jsoup.parse(h);
                Elements divChilds = doc.select("div > *");
                divChilds.size();
                divChilds.get(-1);
                divChilds.get(-1).tagName();
                divChilds.get(1).tagName();
                divChilds.get(2).tagName();
                org.junit.Assert.fail("parentChildStar_literalMutationNumber20790 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parentChildStar_literalMutationNumber20790_failAssert0_add22221 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoCombined_literalMutationNumber41951_failAssert0_add42567_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>");
                Elements ps = doc.select("div.foo p:gt(0)");
                ps.size();
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoCombined_literalMutationNumber41951 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoCombined_literalMutationNumber41951_failAssert0_add42567 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoHas_literalMutationNumber90833_failAssert0_add99472_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>");
                Elements divs1 = doc.select("div:has(span)");
                divs1.size();
                divs1.get(-1).id();
                divs1.get(1).id();
                doc.select("div:has([class])");
                Elements divs2 = doc.select("div:has([class])");
                divs2.size();
                divs2.get(0).id();
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
                org.junit.Assert.fail("testPseudoHas_literalMutationNumber90833 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoHas_literalMutationNumber90833_failAssert0_add99472 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

