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
    public void adjacentSiblings_add24_literalMutationNumber438_failAssert0() throws Exception {
        try {
            String h = "<ol><li>One<li>Two<li>Three</ol>";
            Document doc = Jsoup.parse(h);
            Elements sibs = doc.select("li + li");
            int o_adjacentSiblings_add24__6 = sibs.size();
            String o_adjacentSiblings_add24__7 = sibs.get(-1).text();
            String o_adjacentSiblings_add24__9 = sibs.get(0).text();
            String o_adjacentSiblings_add24__11 = sibs.get(1).text();
            org.junit.Assert.fail("adjacentSiblings_add24_literalMutationNumber438 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void adjacentSiblings_add23_literalMutationString340_literalMutationNumber2166_failAssert0() throws Exception {
        try {
            String h = "<ol><li>One<li>Two<li>Three</l>";
            Document doc = Jsoup.parse(h);
            Elements sibs = doc.select("li + li");
            int o_adjacentSiblings_add23__6 = sibs.size();
            int o_adjacentSiblings_add23__7 = sibs.size();
            String o_adjacentSiblings_add23__8 = sibs.get(-1).text();
            String o_adjacentSiblings_add23__10 = sibs.get(1).text();
            org.junit.Assert.fail("adjacentSiblings_add23_literalMutationString340_literalMutationNumber2166 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void adjacentSiblingsWithId_literalMutationNumber43747_failAssert0_add44294_failAssert0_literalMutationString46902_failAssert0() throws Exception {
        try {
            {
                {
                    String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
                    Document doc = Jsoup.parse(h);
                    Elements sibs = doc.select("li1 + li#2");
                    sibs.size();
                    sibs.size();
                    sibs.get(-1).text();
                    org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationNumber43747 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationNumber43747_failAssert0_add44294 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationNumber43747_failAssert0_add44294_failAssert0_literalMutationString46902 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void adjacentSiblingsWithId_literalMutationNumber43747_failAssert0_add44294_failAssert0_add48806_failAssert0() throws Exception {
        try {
            {
                {
                    String h = "<ol><li id=1>One<li id=2>Two<li id=3>Three</ol>";
                    Document doc = Jsoup.parse(h);
                    Elements sibs = doc.select("li#1 + li#2");
                    sibs.size();
                    sibs.size();
                    sibs.get(-1).text();
                    org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationNumber43747 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationNumber43747_failAssert0_add44294 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationNumber43747_failAssert0_add44294_failAssert0_add48806 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void adjacentSiblingsWithId_literalMutationString43736_failAssert0_literalMutationNumber44077_failAssert0() throws Exception {
        try {
            {
                String h = "<ol><li iVd=1>One<li id=2>Two<li id=3>Three</ol>";
                Document doc = Jsoup.parse(h);
                Elements sibs = doc.select("li#1 + li#2");
                sibs.size();
                sibs.get(-1).text();
                org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationString43736 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("adjacentSiblingsWithId_literalMutationString43736_failAssert0_literalMutationNumber44077 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mixCombinator_add8264_literalMutationNumber8567_failAssert0() throws Exception {
        try {
            String h = "<div class=foo><ol><li>One<li>Two<li>Three</ol></div>";
            Document doc = Jsoup.parse(h);
            Elements sibs = doc.select("body > div.foo li + li");
            int o_mixCombinator_add8264__6 = sibs.size();
            String o_mixCombinator_add8264__7 = sibs.get(-1).text();
            String o_mixCombinator_add8264__9 = sibs.get(0).text();
            String o_mixCombinator_add8264__11 = sibs.get(1).text();
            org.junit.Assert.fail("mixCombinator_add8264_literalMutationNumber8567 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mixCombinatorGroup_literalMutationNumber55451_failAssert0_literalMutationString56104_failAssert0() throws Exception {
        try {
            {
                String h = "4n(. svt|6L<6[_h)[.>H=G<_p|4]*F3b4hYfOIG;^[Ny^ 2}dzPs";
                Document doc = Jsoup.parse(h);
                Elements els = doc.select(".foo > ol, ol > li + li");
                els.size();
                els.get(-1).tagName();
                els.get(1).text();
                els.get(2).text();
                org.junit.Assert.fail("mixCombinatorGroup_literalMutationNumber55451 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("mixCombinatorGroup_literalMutationNumber55451_failAssert0_literalMutationString56104 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

