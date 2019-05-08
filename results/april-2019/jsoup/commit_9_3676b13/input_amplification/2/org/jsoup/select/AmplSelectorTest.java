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
    public void testById_literalMutationString22827_literalMutationNumber23077_failAssert0() throws Exception {
        try {
            Elements els = Jsoup.parse("<div><p id=foo>Hello</p><p id=foo>Foo two!</p></div>").select("#foo");
            int o_testById_literalMutationString22827__4 = els.size();
            String o_testById_literalMutationString22827__5 = els.get(-1).text();
            String o_testById_literalMutationString22827__7 = els.get(1).text();
            Elements none = Jsoup.parse("<div id=1o></div>").select("#foo");
            int o_testById_literalMutationString22827__12 = none.size();
            org.junit.Assert.fail("testById_literalMutationString22827_literalMutationNumber23077 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void adjacentSiblings_literalMutationNumber11414_failAssert0_add12341_failAssert0() throws Exception {
        try {
            {
                String h = "<ol><li>One<li>Two<li>Three</ol>";
                Document doc = Jsoup.parse(h);
                doc.select("li + li");
                Elements sibs = doc.select("li + li");
                sibs.size();
                sibs.get(-1).text();
                sibs.get(1).text();
                org.junit.Assert.fail("adjacentSiblings_literalMutationNumber11414 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("adjacentSiblings_literalMutationNumber11414_failAssert0_add12341 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber55264_failAssert0_literalMutationNumber57074_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:eq(0)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                Elements ps2 = doc.select("div:eq(0) p:eq(0)");
                ps2.size();
                ps2.get(1).text();
                ps2.get(0).tagName();
                org.junit.Assert.fail("testPseudoEquals_literalMutationNumber55264 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber55264_failAssert0_literalMutationNumber57074 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNestedHas_literalMutationString60457_add61699() throws Exception {
        Document doc = Jsoup.parse("<div><p><span>One</span></p></div> <div><p>Two</p>/div>");
        Elements divs = doc.select("div:has(p:has(span))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60457__5 = divs.size();
        String o_testNestedHas_literalMutationString60457__6 = divs.first().text();
        Assert.assertEquals("One", o_testNestedHas_literalMutationString60457__6);
        divs = doc.select("div:has(p:matches((?i)two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60457__11 = divs.size();
        int o_testNestedHas_literalMutationString60457__12 = divs.size();
        String o_testNestedHas_literalMutationString60457__13 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__13);
        String o_testNestedHas_literalMutationString60457__15 = divs.first().text();
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__15);
        divs = doc.select("div:has(p:contains(two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60457__20 = divs.size();
        int o_testNestedHas_literalMutationString60457__21 = divs.size();
        int o_testNestedHas_literalMutationString60457__22 = divs.size();
        String o_testNestedHas_literalMutationString60457__23 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__23);
        String o_testNestedHas_literalMutationString60457__25 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__25);
        Element o_testNestedHas_literalMutationString60457_add61699__49 = divs.first();
        Assert.assertFalse(((Collection) (((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).isBlock());
        Assert.assertTrue(((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).hasText());
        Assert.assertEquals("<div>\n <p>Two</p>/div&gt;\n</div>", ((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).toString());
        Assert.assertTrue(((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).hasParent());
        String o_testNestedHas_literalMutationString60457__27 = divs.first().text();
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__27);
        String o_testNestedHas_literalMutationString60457__29 = divs.first().text();
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__29);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("One", o_testNestedHas_literalMutationString60457__6);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__13);
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__15);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__23);
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__25);
        Assert.assertFalse(((Collection) (((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).isBlock());
        Assert.assertTrue(((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).hasText());
        Assert.assertEquals("<div>\n <p>Two</p>/div&gt;\n</div>", ((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).toString());
        Assert.assertTrue(((Element) (o_testNestedHas_literalMutationString60457_add61699__49)).hasParent());
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__27);
    }

    @Test(timeout = 10000)
    public void testNestedHas_literalMutationString60457() throws Exception {
        Document doc = Jsoup.parse("<div><p><span>One</span></p></div> <div><p>Two</p>/div>");
        Elements divs = doc.select("div:has(p:has(span))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60457__5 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__5)));
        String o_testNestedHas_literalMutationString60457__6 = divs.first().text();
        Assert.assertEquals("One", o_testNestedHas_literalMutationString60457__6);
        divs = doc.select("div:has(p:matches((?i)two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60457__11 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__11)));
        int o_testNestedHas_literalMutationString60457__12 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__12)));
        String o_testNestedHas_literalMutationString60457__13 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__13);
        String o_testNestedHas_literalMutationString60457__15 = divs.first().text();
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__15);
        divs = doc.select("div:has(p:contains(two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60457__20 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__20)));
        int o_testNestedHas_literalMutationString60457__21 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__21)));
        int o_testNestedHas_literalMutationString60457__22 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__22)));
        String o_testNestedHas_literalMutationString60457__23 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__23);
        String o_testNestedHas_literalMutationString60457__25 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__25);
        String o_testNestedHas_literalMutationString60457__27 = divs.first().text();
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__27);
        String o_testNestedHas_literalMutationString60457__29 = divs.first().text();
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__29);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__5)));
        Assert.assertEquals("One", o_testNestedHas_literalMutationString60457__6);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__11)));
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__12)));
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__13);
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__15);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__20)));
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__21)));
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60457__22)));
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__23);
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60457__25);
        Assert.assertEquals("Two/div>", o_testNestedHas_literalMutationString60457__27);
    }

    @Test(timeout = 10000)
    public void testNestedHas_literalMutationString60454() throws Exception {
        Document doc = Jsoup.parse("<div><p><span>One</span></p></div> <div><p>Two</p></>iv>");
        Elements divs = doc.select("div:has(p:has(span))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60454__5 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__5)));
        String o_testNestedHas_literalMutationString60454__6 = divs.first().text();
        Assert.assertEquals("One", o_testNestedHas_literalMutationString60454__6);
        divs = doc.select("div:has(p:matches((?i)two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60454__11 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__11)));
        int o_testNestedHas_literalMutationString60454__12 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__12)));
        String o_testNestedHas_literalMutationString60454__13 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60454__13);
        String o_testNestedHas_literalMutationString60454__15 = divs.first().text();
        Assert.assertEquals("Twoiv>", o_testNestedHas_literalMutationString60454__15);
        divs = doc.select("div:has(p:contains(two))");
        Assert.assertFalse(divs.isEmpty());
        int o_testNestedHas_literalMutationString60454__20 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__20)));
        int o_testNestedHas_literalMutationString60454__21 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__21)));
        int o_testNestedHas_literalMutationString60454__22 = divs.size();
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__22)));
        String o_testNestedHas_literalMutationString60454__23 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60454__23);
        String o_testNestedHas_literalMutationString60454__25 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60454__25);
        String o_testNestedHas_literalMutationString60454__27 = divs.first().text();
        Assert.assertEquals("Twoiv>", o_testNestedHas_literalMutationString60454__27);
        String o_testNestedHas_literalMutationString60454__29 = divs.first().text();
        Assert.assertEquals("Twoiv>", o_testNestedHas_literalMutationString60454__29);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__5)));
        Assert.assertEquals("One", o_testNestedHas_literalMutationString60454__6);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__11)));
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__12)));
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60454__13);
        Assert.assertEquals("Twoiv>", o_testNestedHas_literalMutationString60454__15);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__20)));
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__21)));
        Assert.assertEquals(1, ((int) (o_testNestedHas_literalMutationString60454__22)));
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60454__23);
        Assert.assertEquals("div", o_testNestedHas_literalMutationString60454__25);
        Assert.assertEquals("Twoiv>", o_testNestedHas_literalMutationString60454__27);
    }
}

