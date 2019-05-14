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
    public void parentChildStar_literalMutationNumber19572_failAssert0_literalMutationString20379_failAssert0() throws Exception {
        try {
            {
                String h = "K&Y&,JIY|YF7kuYx,:St6q}<8(]Kb8p]W?cSk&UBI#Iqb9Cd!Ee_%=[}<Z15qO}PS[xE@CMeOb";
                Document doc = Jsoup.parse(h);
                Elements divChilds = doc.select("div > *");
                divChilds.size();
                divChilds.get(-1).tagName();
                divChilds.get(1).tagName();
                divChilds.get(2).tagName();
                org.junit.Assert.fail("parentChildStar_literalMutationNumber19572 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parentChildStar_literalMutationNumber19572_failAssert0_literalMutationString20379 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber68822_literalMutationNumber69586_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:eq(0)");
            int o_testPseudoEquals_literalMutationNumber68822__5 = ps.size();
            String o_testPseudoEquals_literalMutationNumber68822__6 = ps.get(0).text();
            String o_testPseudoEquals_literalMutationNumber68822__8 = ps.get(1).text();
            Elements ps2 = doc.select("div:eq(0) p:eq(0)");
            int o_testPseudoEquals_literalMutationNumber68822__12 = ps2.size();
            String o_testPseudoEquals_literalMutationNumber68822__13 = ps2.get(-1).text();
            String o_testPseudoEquals_literalMutationNumber68822__15 = ps2.get(0).tagName();
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber68822_literalMutationNumber69586 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoCombined_literalMutationNumber40668_failAssert0_add41221_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>");
                doc.select("div.foo p:gt(0)");
                Elements ps = doc.select("div.foo p:gt(0)");
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoCombined_literalMutationNumber40668 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoCombined_literalMutationNumber40668_failAssert0_add41221 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationNumber107980_failAssert0_literalMutationNumber110402_failAssert0() throws Exception {
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
                found.get(0).text();
                found.get(-1).text();
                found.get(2).text();
                found = doc.select("div[class=\"value\\ \"]");
                found.size();
                org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber107980 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber107980_failAssert0_literalMutationNumber110402 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

