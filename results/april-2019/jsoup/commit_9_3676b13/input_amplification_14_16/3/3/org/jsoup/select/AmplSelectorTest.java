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
    public void mixCombinator_literalMutationNumber22809_literalMutationNumber23014_failAssert0() throws Exception {
        try {
            String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
            Document doc = Jsoup.parse(h);
            Elements sibs = doc.select("body > div.foo li + li");
            int o_mixCombinator_literalMutationNumber22809__6 = sibs.size();
            String o_mixCombinator_literalMutationNumber22809__7 = sibs.get(0).text();
            String o_mixCombinator_literalMutationNumber22809__9 = sibs.get(-1).text();
            org.junit.Assert.fail("mixCombinator_literalMutationNumber22809_literalMutationNumber23014 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoGreaterThan_add165686_literalMutationNumber166013_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
            Elements o_testPseudoGreaterThan_add165686__3 = doc.select("div p:gt(0)");
            Elements ps = doc.select("div p:gt(0)");
            int o_testPseudoGreaterThan_add165686__6 = ps.size();
            String o_testPseudoGreaterThan_add165686__7 = ps.get(-1).text();
            String o_testPseudoGreaterThan_add165686__9 = ps.get(1).text();
            org.junit.Assert.fail("testPseudoGreaterThan_add165686_literalMutationNumber166013 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber157296_failAssert0_add159794_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:eq(0)");
                ps.size();
                ps.get(0).text();
                ps.get(1);
                ps.get(1).text();
                Elements ps2 = doc.select("div:eq(0) p:eq(0)");
                ps2.size();
                ps2.get(-1).text();
                ps2.get(0).tagName();
                org.junit.Assert.fail("testPseudoEquals_literalMutationNumber157296 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber157296_failAssert0_add159794 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoBetween_add170180_add170669_literalMutationNumber171350_failAssert0() throws Exception {
        try {
            Document o_testPseudoBetween_add170180__1 = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:gt(0):lt(2)");
            int o_testPseudoBetween_add170180__6 = ps.size();
            String o_testPseudoBetween_add170180__7 = ps.get(-1).text();
            o_testPseudoBetween_add170180__1.isBlock();
            org.junit.Assert.fail("testPseudoBetween_add170180_add170669_literalMutationNumber171350 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoBetween_literalMutationString170167_literalMutationNumber170288_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div>p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:gt(0):lt(2)");
            int o_testPseudoBetween_literalMutationString170167__5 = ps.size();
            String o_testPseudoBetween_literalMutationString170167__6 = ps.get(-1).text();
            org.junit.Assert.fail("testPseudoBetween_literalMutationString170167_literalMutationNumber170288 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationString211037_literalMutationNumber211456_failAssert0null224826_failAssert0() throws Exception {
        try {
            {
                final String html = "<div class=\"value\">class without pace</div>\n" + "<div class=\"value \">class with space</div>";
                Document doc = Jsoup.parse(html);
                Elements found = doc.select("div[class=value ]");
                int o_selectClassWithSpace_literalMutationString211037__6 = found.size();
                String o_selectClassWithSpace_literalMutationString211037__7 = found.get(0).text();
                String o_selectClassWithSpace_literalMutationString211037__9 = found.get(1).text();
                found = doc.select("div[class=\"value \"]");
                int o_selectClassWithSpace_literalMutationString211037__13 = found.size();
                int o_selectClassWithSpace_literalMutationString211037__14 = found.size();
                String o_selectClassWithSpace_literalMutationString211037__15 = found.get(0).text();
                String o_selectClassWithSpace_literalMutationString211037__17 = found.get(-1).text();
                String o_selectClassWithSpace_literalMutationString211037__19 = found.get(1).text();
                String o_selectClassWithSpace_literalMutationString211037__21 = found.get(1).text();
                found = doc.select(null);
                int o_selectClassWithSpace_literalMutationString211037__25 = found.size();
                org.junit.Assert.fail("selectClassWithSpace_literalMutationString211037_literalMutationNumber211456 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("selectClassWithSpace_literalMutationString211037_literalMutationNumber211456_failAssert0null224826 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

