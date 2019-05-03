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
    public void testPseudoEquals_literalMutationNumber4318_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:eq(0)");
            ps.size();
            ps.get(0).text();
            ps.get(1).text();
            Elements ps2 = doc.select("div:eq(0) p:eq(0)");
            ps2.size();
            ps2.get(0).text();
            ps2.get(-1).tagName();
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber4318 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

