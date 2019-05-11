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
    public void mixCombinator_literalMutationNumber4762_literalMutationNumber4961_failAssert0() throws Exception {
        try {
            String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
            Document doc = Jsoup.parse(h);
            Elements sibs = doc.select("body > div.foo li + li");
            int o_mixCombinator_literalMutationNumber4762__6 = sibs.size();
            String o_mixCombinator_literalMutationNumber4762__7 = sibs.get(-1).text();
            String o_mixCombinator_literalMutationNumber4762__9 = sibs.get(0).text();
            org.junit.Assert.fail("mixCombinator_literalMutationNumber4762_literalMutationNumber4961 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mixCombinator_literalMutationNumber4756_failAssert0_add5708_failAssert0() throws Exception {
        try {
            {
                String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
                Document doc = Jsoup.parse(h);
                Elements sibs = doc.select("body > div.foo li + li");
                sibs.size();
                sibs.get(-1).text();
                sibs.get(1).text();
                org.junit.Assert.fail("mixCombinator_literalMutationNumber4756 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("mixCombinator_literalMutationNumber4756_failAssert0_add5708 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mixCombinatorGroup_literalMutationNumber30636_literalMutationNumber30787_failAssert0() throws Exception {
        try {
            String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
            Document doc = Jsoup.parse(h);
            Elements els = doc.select(".foo > ol, ol > li + li");
            int o_mixCombinatorGroup_literalMutationNumber30636__6 = els.size();
            String o_mixCombinatorGroup_literalMutationNumber30636__7 = els.get(0).tagName();
            String o_mixCombinatorGroup_literalMutationNumber30636__9 = els.get(-1).text();
            String o_mixCombinatorGroup_literalMutationNumber30636__12 = els.get(2).text();
            org.junit.Assert.fail("mixCombinatorGroup_literalMutationNumber30636_literalMutationNumber30787 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

