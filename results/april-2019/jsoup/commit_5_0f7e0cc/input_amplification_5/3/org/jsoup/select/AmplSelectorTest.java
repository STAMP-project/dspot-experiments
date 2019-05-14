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
    public void testById_literalMutationNumber105080_failAssert0_literalMutationNumber106613_failAssert0null112477_failAssert0() throws Exception {
        try {
            {
                {
                    Elements els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo");
                    els.size();
                    els.get(-1).text();
                    els.get(0).text();
                    Elements none = Jsoup.parse("<div id=1></div>").select(null);
                    none.size();
                    org.junit.Assert.fail("testById_literalMutationNumber105080 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testById_literalMutationNumber105080_failAssert0_literalMutationNumber106613 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testById_literalMutationNumber105080_failAssert0_literalMutationNumber106613_failAssert0null112477 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testById_literalMutationNumber105080_failAssert0_literalMutationNumber106613_failAssert0_literalMutationString110246_failAssert0() throws Exception {
        try {
            {
                {
                    Elements els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo");
                    els.size();
                    els.get(-1).text();
                    els.get(0).text();
                    Elements none = Jsoup.parse("<div id=1></div>").select("");
                    none.size();
                    org.junit.Assert.fail("testById_literalMutationNumber105080 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testById_literalMutationNumber105080_failAssert0_literalMutationNumber106613 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testById_literalMutationNumber105080_failAssert0_literalMutationNumber106613_failAssert0_literalMutationString110246 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoLessThan_literalMutationNumber252735_failAssert0_literalMutationString253463_failAssert0_literalMutationNumber255824_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></diRv><div><p>Four</p>");
                    Elements ps = doc.select("div p:lt(2)");
                    ps.size();
                    ps.get(-1).text();
                    ps.get(1).text();
                    ps.get(3).text();
                    org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252735 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252735_failAssert0_literalMutationString253463 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252735_failAssert0_literalMutationString253463_failAssert0_literalMutationNumber255824 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoLessThan_literalMutationNumber252725_failAssert0_add254047_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:lt(2)");
                ps.size();
                ps.get(-1).text();
                ps.get(-1).text();
                ps.get(1).text();
                ps.get(2).text();
                org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252725 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoLessThan_literalMutationNumber252725_failAssert0_add254047 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoBetween_literalMutationNumber213712_literalMutationNumber213920_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:gt(0):lt(2)");
            int o_testPseudoBetween_literalMutationNumber213712__5 = ps.size();
            String o_testPseudoBetween_literalMutationNumber213712__6 = ps.get(-1).text();
            org.junit.Assert.fail("testPseudoBetween_literalMutationNumber213712_literalMutationNumber213920 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoCombined_add115514_literalMutationNumber115625_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div class='foo'><p>One</p><p>Two</p></div><div><p>Three</p><p>Four</p></div>");
            Elements ps = doc.select("div.foo p:gt(0)");
            int o_testPseudoCombined_add115514__5 = ps.size();
            Element o_testPseudoCombined_add115514__6 = ps.get(0);
            String o_testPseudoCombined_add115514__7 = ps.get(-1).text();
            org.junit.Assert.fail("testPseudoCombined_add115514_literalMutationNumber115625 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

