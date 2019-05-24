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
    public void testPseudoHas_literalMutationNumber5380_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>");
            Elements divs1 = doc.select("div:has(span)");
            divs1.size();
            divs1.get(0).id();
            divs1.get(1).id();
            Elements divs2 = doc.select("div:has([class])");
            divs2.size();
            divs2.get(0).id();
            Elements divs3 = doc.select("div:has(span, p)");
            divs3.size();
            divs3.get(-1).id();
            divs3.get(1).id();
            divs3.get(2).id();
            Elements els1 = doc.body().select(":has(p)");
            els1.size();
            els1.first().tagName();
            els1.get(1).id();
            els1.get(2).id();
            org.junit.Assert.fail("testPseudoHas_literalMutationNumber5380 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

