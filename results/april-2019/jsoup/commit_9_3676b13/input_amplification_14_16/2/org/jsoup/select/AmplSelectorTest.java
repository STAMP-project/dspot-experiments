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
    public void descendant_literalMutationNumber2829_failAssert0null8499_failAssert0() throws Exception {
        try {
            {
                String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
                Document doc = Jsoup.parse(h);
                Element root = doc.getElementsByClass("HEAD").first();
                Elements els = root.select(".head p");
                els.size();
                els.get(-1).text();
                els.get(1).text();
                Elements p = root.select(null);
                p.size();
                p.get(0).text();
                Elements empty = root.select("p .first");
                empty.size();
                Elements aboveRoot = root.select("body div.head");
                aboveRoot.size();
                org.junit.Assert.fail("descendant_literalMutationNumber2829 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("descendant_literalMutationNumber2829_failAssert0null8499 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void descendant_literalMutationNumber2829_failAssert0_literalMutationString6094_failAssert0() throws Exception {
        try {
            {
                String h = "<div class=head><p class=first>Hello</p><p>There</p</div><p>None</p>";
                Document doc = Jsoup.parse(h);
                Element root = doc.getElementsByClass("HEAD").first();
                Elements els = root.select(".head p");
                els.size();
                els.get(-1).text();
                els.get(1).text();
                Elements p = root.select("p.first");
                p.size();
                p.get(0).text();
                Elements empty = root.select("p .first");
                empty.size();
                Elements aboveRoot = root.select("body div.head");
                aboveRoot.size();
                org.junit.Assert.fail("descendant_literalMutationNumber2829 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("descendant_literalMutationNumber2829_failAssert0_literalMutationString6094 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void descendant_add2859_literalMutationNumber3843_failAssert0() throws Exception {
        try {
            String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
            Document doc = Jsoup.parse(h);
            Element o_descendant_add2859__4 = doc.getElementsByClass("HEAD").first();
            Element root = doc.getElementsByClass("HEAD").first();
            Elements els = root.select(".head p");
            int o_descendant_add2859__11 = els.size();
            String o_descendant_add2859__12 = els.get(0).text();
            String o_descendant_add2859__14 = els.get(1).text();
            Elements p = root.select("p.first");
            int o_descendant_add2859__18 = p.size();
            String o_descendant_add2859__19 = p.get(-1).text();
            Elements empty = root.select("p .first");
            int o_descendant_add2859__24 = empty.size();
            Elements aboveRoot = root.select("body div.head");
            int o_descendant_add2859__27 = aboveRoot.size();
            org.junit.Assert.fail("descendant_add2859_literalMutationNumber3843 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void descendant_literalMutationNumber2829_failAssert0_literalMutationString6132_failAssert0() throws Exception {
        try {
            {
                String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
                Document doc = Jsoup.parse(h);
                Element root = doc.getElementsByClass("HEAD").first();
                Elements els = root.select(".head p");
                els.size();
                els.get(-1).text();
                els.get(1).text();
                Elements p = root.select("p.first");
                p.size();
                p.get(0).text();
                Elements empty = root.select("p .frirst");
                empty.size();
                Elements aboveRoot = root.select("body div.head");
                aboveRoot.size();
                org.junit.Assert.fail("descendant_literalMutationNumber2829 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("descendant_literalMutationNumber2829_failAssert0_literalMutationString6132 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationString53824_failAssert0_literalMutationNumber54937_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:eq(0)");
                ps.size();
                ps.get(0).text();
                ps.get(1).text();
                Elements ps2 = doc.select("div8:eq(0) p:eq(0)");
                ps2.size();
                ps2.get(-1).text();
                ps2.get(0).tagName();
                org.junit.Assert.fail("testPseudoEquals_literalMutationString53824 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationString53824_failAssert0_literalMutationNumber54937 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoBetween_literalMutationNumber58112_literalMutationNumber58237_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:gt(0):lt(2)");
            int o_testPseudoBetween_literalMutationNumber58112__5 = ps.size();
            String o_testPseudoBetween_literalMutationNumber58112__6 = ps.get(-1).text();
            org.junit.Assert.fail("testPseudoBetween_literalMutationNumber58112_literalMutationNumber58237 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationNumber72246_failAssert0_literalMutationString74668_failAssert0() throws Exception {
        try {
            {
                final String html = "<p>Hello <em>there</em> <em>now</em></p>" + "<div class=\"value \">class with space</div>";
                Document doc = Jsoup.parse(html);
                Elements found = doc.select("div[class=value ]");
                found.size();
                found.get(-1).text();
                found.get(1).text();
                found = doc.select("div[class=\"value \"]");
                found.size();
                found.size();
                found.get(0).text();
                found.get(1).text();
                found.get(1).text();
                found = doc.select("div[class=\"value\\ \"]");
                found.size();
                org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber72246 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber72246_failAssert0_literalMutationString74668 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

