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
    public void testPseudoEquals_literalMutationString53937_failAssert0_literalMutationNumber55336_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:eq(0)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                Elements ps2 = doc.select("GC&(?Tu%+8EQ<9MEt");
                ps2.size();
                ps2.get(0).text();
                ps2.get(0).tagName();
                org.junit.Assert.fail("testPseudoEquals_literalMutationString53937 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationString53937_failAssert0_literalMutationNumber55336 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoBetween_literalMutationNumber58214_failAssert0_add58808_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:gt(0):lt(2)");
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoBetween_literalMutationNumber58214 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoBetween_literalMutationNumber58214_failAssert0_add58808 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoBetween_literalMutationString58206_failAssert0_literalMutationNumber58582_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("wc2R^?BxF]tL$:Dx`]au!56J)(_?<$LCWpKjkh)*61p<UjO+IaZLZVEz=X71");
                Elements ps = doc.select("div p:gt(0):lt(2)");
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoBetween_literalMutationString58206 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoBetween_literalMutationString58206_failAssert0_literalMutationNumber58582 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationNumber72397_failAssert0_literalMutationNumber75885_failAssert0() throws Exception {
        try {
            {
                final String html = "<div class=\"value\">class without space</div>\n" + "<div class=\"value \">class with space</div>";
                Document doc = Jsoup.parse(html);
                Elements found = doc.select("div[class=value ]");
                found.size();
                found.get(0).text();
                found.get(1).text();
                found = doc.select("div[class=\"value \"]");
                found.size();
                found.size();
                found.get(-1).text();
                found.get(0).text();
                found.get(1).text();
                found = doc.select("div[class=\"value\\ \"]");
                found.size();
                org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber72397 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber72397_failAssert0_literalMutationNumber75885 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void splitOnBr_add31863_literalMutationNumber32145_failAssert0() throws Exception {
        try {
            String html = "<div><p>One<br>Two<br>Three</p></div>";
            Document doc = Jsoup.parse(html);
            Elements o_splitOnBr_add31863__4 = doc.select("p:matchText");
            Elements els = doc.select("p:matchText");
            int o_splitOnBr_add31863__7 = els.size();
            String o_splitOnBr_add31863__8 = els.get(-1).text();
            String o_splitOnBr_add31863__10 = els.get(1).text();
            els.get(2).toString();
            org.junit.Assert.fail("splitOnBr_add31863_literalMutationNumber32145 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

