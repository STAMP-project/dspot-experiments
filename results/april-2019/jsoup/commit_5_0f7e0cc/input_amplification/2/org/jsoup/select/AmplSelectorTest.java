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
    public void testByTag_literalMutationNumber3841_failAssert0_literalMutationNumber5594_failAssert0() throws Exception {
        try {
            {
                Elements els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV");
                els.size();
                els.get(-1).id();
                els.get(2).id();
                els.get(2).id();
                Elements none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span");
                none.size();
                org.junit.Assert.fail("testByTag_literalMutationNumber3841 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testByTag_literalMutationNumber3841_failAssert0_literalMutationNumber5594 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void descendant_literalMutationNumber14208_failAssert0() throws Exception {
        try {
            String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
            Document doc = Jsoup.parse(h);
            Element root = doc.getElementsByClass("HEAD").first();
            Elements els = root.select(".head p");
            els.size();
            els.get(0).text();
            els.get(1).text();
            Elements p = root.select("p.first");
            p.size();
            p.get(-1).text();
            Elements empty = root.select("p .first");
            empty.size();
            Elements aboveRoot = root.select("body div.head");
            aboveRoot.size();
            org.junit.Assert.fail("descendant_literalMutationNumber14208 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void parentChildStar_literalMutationNumber20910_failAssert0_literalMutationString21613_failAssert0() throws Exception {
        try {
            {
                String h = "uBW7[1iB#lS&JH-P@`y=8uZTQFi=w,lDZI%t#.*e7`.l<I&h}S$&@r6Ona:uwZv r%M:8[nn(q";
                Document doc = Jsoup.parse(h);
                Elements divChilds = doc.select("div > *");
                divChilds.size();
                divChilds.get(-1).tagName();
                divChilds.get(1).tagName();
                divChilds.get(2).tagName();
                org.junit.Assert.fail("parentChildStar_literalMutationNumber20910 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parentChildStar_literalMutationNumber20910_failAssert0_literalMutationString21613 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoGreaterThan_literalMutationNumber73302_literalMutationNumber73524_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:gt(0)");
            int o_testPseudoGreaterThan_literalMutationNumber73302__5 = ps.size();
            String o_testPseudoGreaterThan_literalMutationNumber73302__6 = ps.get(-1).text();
            String o_testPseudoGreaterThan_literalMutationNumber73302__9 = ps.get(1).text();
            org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber73302_literalMutationNumber73524 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

