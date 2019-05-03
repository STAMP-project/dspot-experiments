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
    public void testByTag_literalMutationNumber20418_add22973_literalMutationNumber26127_failAssert0() throws Exception {
        try {
            Elements els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV");
            int o_testByTag_literalMutationNumber20418__5 = els.size();
            String o_testByTag_literalMutationNumber20418__6 = els.get(-1).id();
            String o_testByTag_literalMutationNumber20418__8 = els.get(0).id();
            Element o_testByTag_literalMutationNumber20418_add22973__17 = els.get(2);
            String o_testByTag_literalMutationNumber20418__11 = els.get(2).id();
            Elements none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span");
            int o_testByTag_literalMutationNumber20418__16 = none.size();
            org.junit.Assert.fail("testByTag_literalMutationNumber20418_add22973_literalMutationNumber26127 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testByTag_literalMutationNumber20421_failAssert0_literalMutationNumber22210_failAssert0() throws Exception {
        try {
            {
                Elements els = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><DIV id=3>").select("DIV");
                els.size();
                els.get(-1).id();
                els.get(1).id();
                els.get(4).id();
                Elements none = Jsoup.parse("<div id=1><div id=2><p>Hello</p></div></div><div id=3>").select("span");
                none.size();
                org.junit.Assert.fail("testByTag_literalMutationNumber20421 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testByTag_literalMutationNumber20421_failAssert0_literalMutationNumber22210 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testById_literalMutationString186488_failAssert0_literalMutationString187825_failAssert0_literalMutationNumber193759_failAssert0() throws Exception {
        try {
            {
                {
                    Elements els = Jsoup.parse("<p>Hello <em>there</em> <em>now</em></p>").select("#foo");
                    els.size();
                    els.get(-1).text();
                    els.get(1).text();
                    Elements none = Jsoup.parse("<div id=1></div>").select("<p>Hello <em>there</em> <em>now</em></p>");
                    none.size();
                    org.junit.Assert.fail("testById_literalMutationString186488 should have thrown Selector$SelectorParseException");
                }
                org.junit.Assert.fail("testById_literalMutationString186488_failAssert0_literalMutationString187825 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testById_literalMutationString186488_failAssert0_literalMutationString187825_failAssert0_literalMutationNumber193759 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoGreaterThan_add365307_literalMutationNumber365504_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
            Elements o_testPseudoGreaterThan_add365307__3 = doc.select("div p:gt(0)");
            Elements ps = doc.select("div p:gt(0)");
            int o_testPseudoGreaterThan_add365307__6 = ps.size();
            String o_testPseudoGreaterThan_add365307__7 = ps.get(-1).text();
            String o_testPseudoGreaterThan_add365307__9 = ps.get(1).text();
            org.junit.Assert.fail("testPseudoGreaterThan_add365307_literalMutationNumber365504 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoGreaterThan_literalMutationNumber365299_failAssert0_add366268_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:gt(0)");
                ps.size();
                ps.get(-1).text();
                ps.get(1);
                ps.get(1).text();
                org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber365299 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoGreaterThan_literalMutationNumber365299_failAssert0_add366268 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber351293_failAssert0_add353840_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:eq(0)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                Elements ps2 = doc.select("div:eq(0) p:eq(0)");
                ps2.size();
                ps2.get(0);
                ps2.get(0).text();
                ps2.get(0).tagName();
                org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0_add353840 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_add351323_literalMutationNumber351851_failAssert0_literalMutationString359744_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("UFj4ClG#N5}pADd.,wFH f({43aIJ[9?ISj(]`avQ%&5OT*[MuNkXxK-zGNI");
                Elements ps = doc.select("div p:eq(0)");
                int o_testPseudoEquals_add351323__5 = ps.size();
                String o_testPseudoEquals_add351323__6 = ps.get(-1).text();
                String o_testPseudoEquals_add351323__8 = ps.get(1).text();
                Elements ps2 = doc.select("div:eq(0) p:eq(0)");
                int o_testPseudoEquals_add351323__12 = ps2.size();
                String o_testPseudoEquals_add351323__13 = ps2.get(0).text();
                String o_testPseudoEquals_add351323__15 = ps2.get(0).text();
                String o_testPseudoEquals_add351323__17 = ps2.get(0).tagName();
                org.junit.Assert.fail("testPseudoEquals_add351323_literalMutationNumber351851 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_add351323_literalMutationNumber351851_failAssert0_literalMutationString359744 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationString351280_literalMutationNumber352077_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>One</p><p>Two=</p><p>Three</>p></div><div><p>Four</p>");
            Elements ps = doc.select("div p:eq(0)");
            int o_testPseudoEquals_literalMutationString351280__5 = ps.size();
            String o_testPseudoEquals_literalMutationString351280__6 = ps.get(0).text();
            String o_testPseudoEquals_literalMutationString351280__8 = ps.get(1).text();
            Elements ps2 = doc.select("div:eq(0) p:eq(0)");
            int o_testPseudoEquals_literalMutationString351280__12 = ps2.size();
            String o_testPseudoEquals_literalMutationString351280__13 = ps2.get(-1).text();
            String o_testPseudoEquals_literalMutationString351280__15 = ps2.get(0).tagName();
            org.junit.Assert.fail("testPseudoEquals_literalMutationString351280_literalMutationNumber352077 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber351293_failAssert0null354064_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                Elements ps = doc.select("div p:eq(0)");
                ps.size();
                ps.get(-1).text();
                ps.get(1).text();
                Elements ps2 = doc.select(null);
                ps2.size();
                ps2.get(0).text();
                ps2.get(0).tagName();
                org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0null354064 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber351293_failAssert0_add353840_failAssert0_literalMutationNumber356729_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                    Elements ps = doc.select("div p:eq(0)");
                    ps.size();
                    ps.get(-1).text();
                    ps.get(1).text();
                    Elements ps2 = doc.select("div:eq(0) p:eq(0)");
                    ps2.size();
                    ps2.get(0);
                    ps2.get(0).text();
                    ps2.get(0).tagName();
                    org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0_add353840 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0_add353840_failAssert0_literalMutationNumber356729 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber351293_failAssert0_add353840_failAssert0_add362285_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                    Elements ps = doc.select("div p:eq(0)");
                    ps.size();
                    ps.get(-1).text();
                    ps.get(-1).text();
                    ps.get(1).text();
                    Elements ps2 = doc.select("div:eq(0) p:eq(0)");
                    ps2.size();
                    ps2.get(0);
                    ps2.get(0).text();
                    ps2.get(0).tagName();
                    org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0_add353840 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0_add353840_failAssert0_add362285 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoEquals_literalMutationNumber351293_failAssert0_add353840_failAssert0_add362296_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div><p>One</p><p>Two</p><p>Three</>p></div><div><p>Four</p>");
                    Elements ps = doc.select("div p:eq(0)");
                    ps.size();
                    ps.get(-1).text();
                    ps.get(1).text();
                    Elements ps2 = doc.select("div:eq(0) p:eq(0)");
                    ps2.size();
                    ps2.get(0);
                    ps2.get(0).text();
                    ps2.get(0).tagName();
                    org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293 should have thrown ArrayIndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0_add353840 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoEquals_literalMutationNumber351293_failAssert0_add353840_failAssert0_add362296 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoCombined_literalMutationNumber204734_failAssert0_literalMutationString205092_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("id?B>(G}#8Za>tu]`EeIkRu>&ekmjlL|`qz??Eb}H4D{j$avLBRf#l8}4v<$tQ8#>LE0)]/M@9d:&");
                Elements ps = doc.select("div.foo p:gt(0)");
                ps.size();
                ps.get(-1).text();
                org.junit.Assert.fail("testPseudoCombined_literalMutationNumber204734 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoCombined_literalMutationNumber204734_failAssert0_literalMutationString205092 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoHas_add448137_literalMutationNumber449952_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>");
            Elements divs1 = doc.select("div:has(span)");
            int o_testPseudoHas_add448137__5 = divs1.size();
            Element o_testPseudoHas_add448137__6 = divs1.get(0);
            String o_testPseudoHas_add448137__7 = divs1.get(0).id();
            String o_testPseudoHas_add448137__9 = divs1.get(1).id();
            Elements divs2 = doc.select("div:has([class])");
            int o_testPseudoHas_add448137__13 = divs2.size();
            String o_testPseudoHas_add448137__14 = divs2.get(0).id();
            Elements divs3 = doc.select("div:has(span, p)");
            int o_testPseudoHas_add448137__18 = divs3.size();
            String o_testPseudoHas_add448137__19 = divs3.get(-1).id();
            String o_testPseudoHas_add448137__21 = divs3.get(1).id();
            String o_testPseudoHas_add448137__23 = divs3.get(2).id();
            Elements els1 = doc.body().select(":has(p)");
            int o_testPseudoHas_add448137__28 = els1.size();
            String o_testPseudoHas_add448137__29 = els1.first().tagName();
            String o_testPseudoHas_add448137__31 = els1.get(1).id();
            String o_testPseudoHas_add448137__33 = els1.get(2).id();
            org.junit.Assert.fail("testPseudoHas_add448137_literalMutationNumber449952 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPseudoHas_add448137_literalMutationNumber449952_failAssert0_literalMutationNumber467723_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=0><p><span>Hello</span></p></div> <div id=1><span class=foo>There</span></div> <div id=2><p>Not</p></div>");
                Elements divs1 = doc.select("div:has(span)");
                int o_testPseudoHas_add448137__5 = divs1.size();
                Element o_testPseudoHas_add448137__6 = divs1.get(0);
                String o_testPseudoHas_add448137__7 = divs1.get(0).id();
                String o_testPseudoHas_add448137__9 = divs1.get(1).id();
                Elements divs2 = doc.select("div:has([class])");
                int o_testPseudoHas_add448137__13 = divs2.size();
                String o_testPseudoHas_add448137__14 = divs2.get(0).id();
                Elements divs3 = doc.select("div:has(span, p)");
                int o_testPseudoHas_add448137__18 = divs3.size();
                String o_testPseudoHas_add448137__19 = divs3.get(-1).id();
                String o_testPseudoHas_add448137__21 = divs3.get(1).id();
                String o_testPseudoHas_add448137__23 = divs3.get(4).id();
                Elements els1 = doc.body().select(":has(p)");
                int o_testPseudoHas_add448137__28 = els1.size();
                String o_testPseudoHas_add448137__29 = els1.first().tagName();
                String o_testPseudoHas_add448137__31 = els1.get(1).id();
                String o_testPseudoHas_add448137__33 = els1.get(2).id();
                org.junit.Assert.fail("testPseudoHas_add448137_literalMutationNumber449952 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPseudoHas_add448137_literalMutationNumber449952_failAssert0_literalMutationNumber467723 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesCommasInSelector_literalMutationNumber249615_failAssert0_add251629_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<p name='1,2'>One</p><div>Two</div><ol><li>123</li><li>Text</li></ol>");
                Elements ps = doc.select("[name=1,2]");
                ps.size();
                Elements containers = doc.select("div, li:matches([0-9,]+)");
                containers.size();
                containers.size();
                containers.get(-1).tagName();
                containers.get(1).tagName();
                containers.get(1).text();
                org.junit.Assert.fail("handlesCommasInSelector_literalMutationNumber249615 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("handlesCommasInSelector_literalMutationNumber249615_failAssert0_add251629 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesCommasInSelector_literalMutationString249607_literalMutationNumber250627_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<p name='1,2'>One</p><div>Two</div><ol><li>123</li><li>Text</li></ol>");
            Elements ps = doc.select("[name=E1,2]");
            int o_handlesCommasInSelector_literalMutationString249607__5 = ps.size();
            Elements containers = doc.select("div, li:matches([0-9,]+)");
            int o_handlesCommasInSelector_literalMutationString249607__8 = containers.size();
            String o_handlesCommasInSelector_literalMutationString249607__9 = containers.get(-1).tagName();
            String o_handlesCommasInSelector_literalMutationString249607__11 = containers.get(1).tagName();
            String o_handlesCommasInSelector_literalMutationString249607__13 = containers.get(1).text();
            org.junit.Assert.fail("handlesCommasInSelector_literalMutationString249607_literalMutationNumber250627 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_add512251_add516034_literalMutationNumber520717_failAssert0() throws Exception {
        try {
            final String html = "<div class=\"value\">class without space</div>\n" + "<div class=\"value \">class with space</div>";
            Document doc = Jsoup.parse(html);
            Elements found = doc.select("div[class=value ]");
            int o_selectClassWithSpace_add512251__6 = found.size();
            String o_selectClassWithSpace_add512251_add516034__9 = found.get(0).text();
            String o_selectClassWithSpace_add512251__7 = found.get(0).text();
            String o_selectClassWithSpace_add512251__9 = found.get(1).text();
            found = doc.select("div[class=\"value \"]");
            int o_selectClassWithSpace_add512251__13 = found.size();
            int o_selectClassWithSpace_add512251__14 = found.size();
            String o_selectClassWithSpace_add512251__15 = found.get(-1).text();
            String o_selectClassWithSpace_add512251__17 = found.get(0).text();
            String o_selectClassWithSpace_add512251__19 = found.get(1).text();
            String o_selectClassWithSpace_add512251__21 = found.get(1).text();
            found = doc.select("div[class=\"value\\ \"]");
            int o_selectClassWithSpace_add512251__25 = found.size();
            int o_selectClassWithSpace_add512251__26 = found.size();
            org.junit.Assert.fail("selectClassWithSpace_add512251_add516034_literalMutationNumber520717 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationNumber512210_failAssert0null517730_failAssert0() throws Exception {
        try {
            {
                final String html = "<div class=\"value\">class without space</div>\n" + "<div class=\"value \">class with space</div>";
                Document doc = Jsoup.parse(html);
                Elements found = doc.select("div[class=value ]");
                found.size();
                found.get(-1).text();
                found.get(1).text();
                found = doc.select(null);
                found.size();
                found.size();
                found.get(0).text();
                found.get(1).text();
                found.get(1).text();
                found = doc.select("div[class=\"value\\ \"]");
                found.size();
                org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber512210 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber512210_failAssert0null517730 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void selectClassWithSpace_literalMutationNumber512224_failAssert0() throws Exception {
        try {
            final String html = "<div class=\"value\">class without space</div>\n" + "<div class=\"value \">class with space</div>";
            Document doc = Jsoup.parse(html);
            Elements found = doc.select("div[class=value ]");
            found.size();
            found.get(0).text();
            found.get(1).text();
            found = doc.select("div[class=\"value \"]");
            found.size();
            found.size();
            found.get(-1).text();
            found.get(1).text();
            found.get(1).text();
            found = doc.select("div[class=\"value\\ \"]");
            found.size();
            org.junit.Assert.fail("selectClassWithSpace_literalMutationNumber512224 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void splitOnBr_literalMutationNumber239615_failAssert0_literalMutationNumber240460_failAssert0() throws Exception {
        try {
            {
                String html = "<div><p>One<br>Two<br>Three</p></div>";
                Document doc = Jsoup.parse(html);
                Elements els = doc.select("p:matchText");
                els.size();
                els.get(-1).text();
                els.get(1).text();
                els.get(1).toString();
                org.junit.Assert.fail("splitOnBr_literalMutationNumber239615 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("splitOnBr_literalMutationNumber239615_failAssert0_literalMutationNumber240460 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

