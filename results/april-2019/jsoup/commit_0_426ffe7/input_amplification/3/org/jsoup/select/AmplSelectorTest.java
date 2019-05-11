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
    public void adjacentSiblings_add22_literalMutationNumber126_failAssert0() throws Exception {
        try {
            String h = "<ol><li>One<li>Two<li>Three</ol>";
            Document doc = Jsoup.parse(h);
            Elements o_adjacentSiblings_add22__4 = doc.select("li + li");
            Elements sibs = doc.select("li + li");
            int o_adjacentSiblings_add22__7 = sibs.size();
            String o_adjacentSiblings_add22__8 = sibs.get(-1).text();
            String o_adjacentSiblings_add22__10 = sibs.get(1).text();
            org.junit.Assert.fail("adjacentSiblings_add22_literalMutationNumber126 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

