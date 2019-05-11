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
    public void adjacentSiblingsWithId_literalMutationString6836_literalMutationNumber6976_failAssert0() throws Exception {
        try {
            String h = "<ol><li id=1>One<li id=2>Two<li i>d=3>Three</ol>";
            Document doc = Jsoup.parse(h);
            Elements sibs = doc.select("li#1 + li#2");
            int o_adjacentSiblingsWithId_literalMutationString6836__6 = sibs.size();
            String o_adjacentSiblingsWithId_literalMutationString6836__7 = sibs.get(-1).text();
            org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationString6836_literalMutationNumber6976 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

