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
    public void adjacentSiblings_literalMutationNumber27462_failAssert0_add28340_failAssert0() throws Exception {
        try {
            {
                String h = "<ol><li>One<li>Two<li>Three</ol>";
                Document doc = Jsoup.parse(h);
                Elements sibs = doc.select("li + li");
                sibs.size();
                sibs.get(-1).text();
                sibs.get(1).text();
                org.junit.Assert.fail("adjacentSiblings_literalMutationNumber27462 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("adjacentSiblings_literalMutationNumber27462_failAssert0_add28340 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_add157759_literalMutationNumber157990_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements o_testPseudoEquals_add157759__3 = doc.select("div p:eq(0)");
            Elements ps = doc.select("div p:eq(0)");
            int o_testPseudoEquals_add157759__6 = ps.size();
            String o_testPseudoEquals_add157759__7 = ps.get(-1).text();
            String o_testPseudoEquals_add157759__9 = ps.get(1).text();
            Elements ps2 = doc.select("div:eq(0) p:eq(0)");
            int o_testPseudoEquals_add157759__13 = ps2.size();
            String o_testPseudoEquals_add157759__14 = ps2.get(0).text();
            String o_testPseudoEquals_add157759__16 = ps2.get(0).tagName();
            org.junit.Assert.fail("testPseudoEquals_add157759_literalMutationNumber157990 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber157751_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:eq(0)");
            ps.size();
            ps.get(0).text();
            ps.get(1).text();
            Elements ps2 = doc.select("div:eq(0) p:eq(0)");
            ps2.size();
            ps2.get(-1).text();
            ps2.get(0).tagName();
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber157751 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber157737_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:eq(0)");
            ps.size();
            ps.get(-1).text();
            ps.get(1).text();
            Elements ps2 = doc.select("div:eq(0) p:eq(0)");
            ps2.size();
            ps2.get(0).text();
            ps2.get(0).tagName();
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber157737 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

