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
    public void testByTag_literalMutationNumber4286_failAssert0_add6956_failAssert0() throws Exception {
        try {
            {
                Elements els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV");
                els.size();
                els.get(-1).id();
                els.get(1);
                els.get(1).id();
                els.get(2).id();
                Elements none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span");
                none.size();
                org.junit.Assert.fail("testByTag_literalMutationNumber4286 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testByTag_literalMutationNumber4286_failAssert0_add6956 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testByTag_literalMutationNumber4286_failAssert0_literalMutationString5703_failAssert0() throws Exception {
        try {
            {
                Elements els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV");
                els.size();
                els.get(-1).id();
                els.get(1).id();
                els.get(2).id();
                Elements none = Jsoup.parse("").select("span");
                none.size();
                org.junit.Assert.fail("testByTag_literalMutationNumber4286 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testByTag_literalMutationNumber4286_failAssert0_literalMutationString5703 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testById_literalMutationNumber42368_failAssert0_literalMutationString43652_failAssert0() throws Exception {
        try {
            {
                Elements els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo");
                els.size();
                els.get(-1).text();
                els.get(1).text();
                Elements none = Jsoup.parse("<div id=1></div>").select("");
                none.size();
                org.junit.Assert.fail("testById_literalMutationNumber42368 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testById_literalMutationNumber42368_failAssert0_literalMutationString43652 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoGreaterThan_literalMutationNumber81001_failAssert0_add81940_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:gt(0)");
                ps.size();
                ps.get(-1);
                ps.get(-1).text();
                ps.get(1).text();
                org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber81001 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber81001_failAssert0_add81940 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoGreaterThan_literalMutationNumber81006_literalMutationNumber81412_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:gt(0)");
            int o_testPseudoGreaterThan_literalMutationNumber81006__5 = ps.size();
            String o_testPseudoGreaterThan_literalMutationNumber81006__6 = ps.get(-1).text();
            String o_testPseudoGreaterThan_literalMutationNumber81006__8 = ps.get(0).text();
            org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber81006_literalMutationNumber81412 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber77743_literalMutationNumber78633_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:eq(0)");
            int o_testPseudoEquals_literalMutationNumber77743__5 = ps.size();
            String o_testPseudoEquals_literalMutationNumber77743__6 = ps.get(1).text();
            String o_testPseudoEquals_literalMutationNumber77743__9 = ps.get(1).text();
            Elements ps2 = doc.select("div:eq(0) p:eq(0)");
            int o_testPseudoEquals_literalMutationNumber77743__13 = ps2.size();
            String o_testPseudoEquals_literalMutationNumber77743__14 = ps2.get(0).text();
            String o_testPseudoEquals_literalMutationNumber77743__16 = ps2.get(-1).tagName();
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber77743_literalMutationNumber78633 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoCombined_literalMutationNumber46149_failAssert0_literalMutationString46463_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<p>Hello <em>there</em> <em>now</em></p>");
                Elements ps = doc.select("div.foo p:gt(0)");
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoCombined_literalMutationNumber46149 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoCombined_literalMutationNumber46149_failAssert0_literalMutationString46463 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationString119775_literalMutationNumber121722_failAssert0() throws Exception {
        try {
            final String html = "<div class=\"value\">cla(s without space</div>\n" + "<div class=\"value \">class with space</div>";
            Document doc = Jsoup.parse(html);
            Elements found = doc.select("div[class=value ]");
            int o_selectClassWithSpace_literalMutationString119775__6 = found.size();
            String o_selectClassWithSpace_literalMutationString119775__7 = found.get(0).text();
            String o_selectClassWithSpace_literalMutationString119775__9 = found.get(1).text();
            found = doc.select("div[class=\"value \"]");
            int o_selectClassWithSpace_literalMutationString119775__13 = found.size();
            int o_selectClassWithSpace_literalMutationString119775__14 = found.size();
            String o_selectClassWithSpace_literalMutationString119775__15 = found.get(-1).text();
            String o_selectClassWithSpace_literalMutationString119775__17 = found.get(0).text();
            String o_selectClassWithSpace_literalMutationString119775__19 = found.get(1).text();
            String o_selectClassWithSpace_literalMutationString119775__21 = found.get(1).text();
            found = doc.select("div[class=\"value\\ \"]");
            int o_selectClassWithSpace_literalMutationString119775__25 = found.size();
            org.junit.Assert.fail("selectClassWithSpace_literalMutationString119775_literalMutationNumber121722 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

