package org.jsoup.select;


import java.util.Collection;
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
    public void testByAttributeRegexCombined_literalMutationString53457() throws Exception {
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td><,table></div>");
        Elements els = doc.select("div table[class~=x|y]");
        Assert.assertFalse(els.isEmpty());
        int o_testByAttributeRegexCombined_literalMutationString53457__5 = els.size();
        Assert.assertEquals(1, ((int) (o_testByAttributeRegexCombined_literalMutationString53457__5)));
        String o_testByAttributeRegexCombined_literalMutationString53457__6 = els.text();
        Assert.assertEquals("Hello<,table>", o_testByAttributeRegexCombined_literalMutationString53457__6);
        Assert.assertFalse(els.isEmpty());
        Assert.assertEquals(1, ((int) (o_testByAttributeRegexCombined_literalMutationString53457__5)));
    }

    @Test(timeout = 10000)
    public void testByAttributeRegexCombined_literalMutationString53457_add53732() throws Exception {
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td><,table></div>");
        Elements els = doc.select("div table[class~=x|y]");
        Assert.assertFalse(els.isEmpty());
        int o_testByAttributeRegexCombined_literalMutationString53457_add53732__5 = els.size();
        Assert.assertEquals(1, ((int) (o_testByAttributeRegexCombined_literalMutationString53457_add53732__5)));
        int o_testByAttributeRegexCombined_literalMutationString53457__5 = els.size();
        String o_testByAttributeRegexCombined_literalMutationString53457__6 = els.text();
        Assert.assertEquals("Hello<,table>", o_testByAttributeRegexCombined_literalMutationString53457__6);
        Assert.assertFalse(els.isEmpty());
        Assert.assertEquals(1, ((int) (o_testByAttributeRegexCombined_literalMutationString53457_add53732__5)));
    }

    @Test(timeout = 10000)
    public void testByAttributeRegexCombined_literalMutationString53457_add53731() throws Exception {
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td><,table></div>");
        Elements els = doc.select("div table[class~=x|y]");
        Assert.assertFalse(els.isEmpty());
        els.isEmpty();
        int o_testByAttributeRegexCombined_literalMutationString53457__5 = els.size();
        String o_testByAttributeRegexCombined_literalMutationString53457__6 = els.text();
        Assert.assertEquals("Hello<,table>", o_testByAttributeRegexCombined_literalMutationString53457__6);
        Assert.assertFalse(els.isEmpty());
    }

    @Test(timeout = 10000)
    public void testByAttributeRegexCombined_literalMutationString53457_add53730() throws Exception {
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td><,table></div>");
        Elements o_testByAttributeRegexCombined_literalMutationString53457_add53730__3 = doc.select("div table[class~=x|y]");
        Assert.assertFalse(o_testByAttributeRegexCombined_literalMutationString53457_add53730__3.isEmpty());
        Elements els = doc.select("div table[class~=x|y]");
        Assert.assertFalse(els.isEmpty());
        int o_testByAttributeRegexCombined_literalMutationString53457__5 = els.size();
        String o_testByAttributeRegexCombined_literalMutationString53457__6 = els.text();
        Assert.assertEquals("Hello<,table>", o_testByAttributeRegexCombined_literalMutationString53457__6);
        Assert.assertFalse(o_testByAttributeRegexCombined_literalMutationString53457_add53730__3.isEmpty());
        Assert.assertFalse(els.isEmpty());
    }

    @Test(timeout = 10000)
    public void testByAttributeRegexCombined_literalMutationString53457_add53729() throws Exception {
        Document o_testByAttributeRegexCombined_literalMutationString53457_add53729__1 = Jsoup.parse("<div><table class=x><td>Hello</td><,table></div>");
        Assert.assertTrue(((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).hasText());
        Assert.assertFalse(((Collection) (((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div>\n   <table class=\"x\">\n    <tbody>\n     <tr>\n      <td>Hello</td>&lt;,table&gt;\n     </tr>\n    </tbody>\n   </table>\n  </div>\n </body>\n</html>", ((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).toString());
        Assert.assertFalse(((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).hasParent());
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td><,table></div>");
        Elements els = doc.select("div table[class~=x|y]");
        Assert.assertFalse(els.isEmpty());
        int o_testByAttributeRegexCombined_literalMutationString53457__5 = els.size();
        String o_testByAttributeRegexCombined_literalMutationString53457__6 = els.text();
        Assert.assertEquals("Hello<,table>", o_testByAttributeRegexCombined_literalMutationString53457__6);
        Assert.assertTrue(((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).hasText());
        Assert.assertFalse(((Collection) (((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <div>\n   <table class=\"x\">\n    <tbody>\n     <tr>\n      <td>Hello</td>&lt;,table&gt;\n     </tr>\n    </tbody>\n   </table>\n  </div>\n </body>\n</html>", ((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).toString());
        Assert.assertFalse(((Document) (o_testByAttributeRegexCombined_literalMutationString53457_add53729__1)).hasParent());
        Assert.assertFalse(els.isEmpty());
    }

    @Test(timeout = 10000)
    public void descendant_literalMutationString4819_failAssert0_literalMutationNumber7511_failAssert0() throws Exception {
        try {
            {
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
                Elements empty = root.select("<p>Hello <em>there</em> <em>now</em></p>");
                empty.size();
                Elements aboveRoot = root.select("body div.head");
                aboveRoot.size();
                org.junit.Assert.fail("descendant_literalMutationString4819 should have thrown Selector$SelectorParseException");
            }
            org.junit.Assert.fail("descendant_literalMutationString4819_failAssert0_literalMutationNumber7511 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mixCombinator_literalMutationNumber11045_failAssert0_add11940_failAssert0() throws Exception {
        try {
            {
                String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
                Document doc = Jsoup.parse(h);
                Elements sibs = doc.select("body > div.foo li + li");
                sibs.size();
                sibs.get(-1).text();
                sibs.get(1).text();
                org.junit.Assert.fail("mixCombinator_literalMutationNumber11045 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("mixCombinator_literalMutationNumber11045_failAssert0_add11940 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mixCombinatorGroup_literalMutationNumber87946_literalMutationNumber88120_failAssert0() throws Exception {
        try {
            String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
            Document doc = Jsoup.parse(h);
            Elements els = doc.select(".foo > ol, ol > li + li");
            int o_mixCombinatorGroup_literalMutationNumber87946__6 = els.size();
            String o_mixCombinatorGroup_literalMutationNumber87946__7 = els.get(-1).tagName();
            String o_mixCombinatorGroup_literalMutationNumber87946__10 = els.get(1).text();
            String o_mixCombinatorGroup_literalMutationNumber87946__12 = els.get(2).text();
            org.junit.Assert.fail("mixCombinatorGroup_literalMutationNumber87946_literalMutationNumber88120 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoLessThan_literalMutationNumber78171_failAssert0_add79508_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                doc.select("div p:lt(2)");
                Elements ps = doc.select("div p:lt(2)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                ps.get(2).text();
                org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber78171 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber78171_failAssert0_add79508 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoLessThan_literalMutationNumber78171_failAssert0_literalMutationString78995_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>TwOo</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:lt(2)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                ps.get(2).text();
                org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber78171 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber78171_failAssert0_literalMutationString78995 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber61401_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber61401 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoBetween_add66208_literalMutationNumber66382_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:gt(0):lt(2)");
            int o_testPseudoBetween_add66208__5 = ps.size();
            int o_testPseudoBetween_add66208__6 = ps.size();
            String o_testPseudoBetween_add66208__7 = ps.get(-1).text();
            org.junit.Assert.fail("testPseudoBetween_add66208_literalMutationNumber66382 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoCombined_literalMutationNumber28611_failAssert0_add29188_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>");
                Elements ps = doc.select("div.foo p:gt(0)");
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoCombined_literalMutationNumber28611 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoCombined_literalMutationNumber28611_failAssert0_add29188 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNestedHas_add67348_literalMutationString67900() throws Exception {
        Document doc = Jsoup.parse("<div><p><span>One</span></p>t/div> <div><p>Two</p></div>");
        Elements divs = doc.select("div:has(p:has(span))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_add67348__5 = divs.size();
        String o_testNestedHas_add67348__6 = divs.first().text();
        Assert.assertEquals("Onet/div> Two", o_testNestedHas_add67348__6);
        divs = doc.select("div:has(p:matches((?i)two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_add67348__11 = divs.size();
        int o_testNestedHas_add67348__12 = divs.size();
        String o_testNestedHas_add67348__13 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_add67348__13);
        String o_testNestedHas_add67348__15 = divs.first().text();
        Assert.assertEquals("Onet/div> Two", o_testNestedHas_add67348__15);
        divs = doc.select("div:has(p:contains(two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_add67348__20 = divs.size();
        int o_testNestedHas_add67348__21 = divs.size();
        int o_testNestedHas_add67348__22 = divs.size();
        int o_testNestedHas_add67348__23 = divs.size();
        String o_testNestedHas_add67348__24 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_add67348__24);
        String o_testNestedHas_add67348__26 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_add67348__26);
        String o_testNestedHas_add67348__28 = divs.first().text();
        Assert.assertEquals("Onet/div> Two", o_testNestedHas_add67348__28);
        String o_testNestedHas_add67348__30 = divs.first().text();
        Assert.assertEquals("Onet/div> Two", o_testNestedHas_add67348__30);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("Onet/div> Two", o_testNestedHas_add67348__6);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("div", o_testNestedHas_add67348__13);
        Assert.assertEquals("Onet/div> Two", o_testNestedHas_add67348__15);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("div", o_testNestedHas_add67348__24);
        Assert.assertEquals("div", o_testNestedHas_add67348__26);
        Assert.assertEquals("Onet/div> Two", o_testNestedHas_add67348__28);
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationNumber81990_failAssert0_add86581_failAssert0() throws Exception {
        try {
            {
                final String html = "<div class=\"value\">class without space</div>\n" + "<div class=\"value \">class with space</div>";
                Document doc = Jsoup.parse(html);
                Elements found = doc.select("div[class=value ]");
                found.size();
                found.get(0).text();
                found.get(1).text();
                doc.select("div[class=\"value \"]");
                found = doc.select("div[class=\"value \"]");
                found.size();
                found.size();
                found.get(-1).text();
                found.get(1).text();
                found.get(1).text();
                found = doc.select("div[class=\"value\\ \"]");
                found.size();
                org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber81990 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber81990_failAssert0_add86581 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationNumber81976_failAssert0null87433_failAssert0() throws Exception {
        try {
            {
                final String html = "<div class=\"value\">class without space</div>\n" + "<div class=\"value \">class with space</div>";
                Document doc = Jsoup.parse(html);
                Elements found = doc.select("div[class=value ]");
                found.size();
                found.get(-1).text();
                found.get(1).text();
                found = doc.select(null);
                found.size();
                found.size();
                found.get(0).text();
                found.get(1).text();
                found.get(1).text();
                found = doc.select("div[class=\"value\\ \"]");
                found.size();
                org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber81976 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber81976_failAssert0null87433 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void splitOnBr_literalMutationNumber36792_failAssert0_add38123_failAssert0() throws Exception {
        try {
            {
                String html = "<div><p>One<br>Two<br>Three</p></div>";
                Document doc = Jsoup.parse(html);
                Elements els = doc.select("p:matchText");
                els.size();
                els.get(-1).text();
                els.get(1);
                els.get(1).text();
                els.get(2).toString();
                org.junit.Assert.fail("splitOnBr_literalMutationNumber36792 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("splitOnBr_literalMutationNumber36792_failAssert0_add38123 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

