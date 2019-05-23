package org.jsoup.select;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void filter_literalMutationString1763_literalMutationNumber1963_failAssert0() throws Exception {
        try {
            String h = "<p>Excl</p><div class=headline>.p>Hello</p><p>There</p></div><div class=headline><h1>Headline</h1></div>";
            Document doc = Jsoup.parse(h);
            Elements els = doc.select(".headline").select("p");
            int o_filter_literalMutationString1763__7 = els.size();
            String o_filter_literalMutationString1763__8 = els.get(-1).text();
            String o_filter_literalMutationString1763__10 = els.get(1).text();
            org.junit.Assert.fail("filter_literalMutationString1763_literalMutationNumber1963 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void filter_literalMutationNumber1774_failAssert0_add2962_failAssert0() throws Exception {
        try {
            {
                String h = "<p>Excl</p><div class=headline><p>Hello</p><p>There</p></div><div class=headline><h1>Headline</h1></div>";
                Document doc = Jsoup.parse(h);
                Elements els = doc.select(".headline").select("p");
                els.size();
                els.get(-1).text();
                els.get(1).text();
                els.get(1).text();
                org.junit.Assert.fail("filter_literalMutationNumber1774 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("filter_literalMutationNumber1774_failAssert0_add2962 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void filter_literalMutationString1759_failAssert0_literalMutationNumber2356_failAssert0() throws Exception {
        try {
            {
                String h = "";
                Document doc = Jsoup.parse(h);
                Elements els = doc.select(".headline").select("p");
                els.size();
                els.get(-1).text();
                els.get(1).text();
                org.junit.Assert.fail("filter_literalMutationString1759 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("filter_literalMutationString1759_failAssert0_literalMutationNumber2356 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void eachText_literalMutationNumber15590_failAssert0_add20506_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12<p></p></div>");
                List<String> divText = doc.select("div").eachText();
                divText.size();
                divText.get(-1);
                divText.get(1);
                List<String> pText = doc.select("p").eachText();
                Elements ps = doc.select("p");
                ps.size();
                pText.size();
                pText.get(0);
                pText.get(1);
                pText.get(4);
                pText.get(6);
                pText.get(11);
                org.junit.Assert.fail("eachText_literalMutationNumber15590 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("eachText_literalMutationNumber15590_failAssert0_add20506 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void eachText_literalMutationNumber15604_failAssert0_literalMutationNumber18423_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12<p></p></div>");
                List<String> divText = doc.select("div").eachText();
                divText.size();
                divText.get(0);
                divText.get(1);
                List<String> pText = doc.select("p").eachText();
                Elements ps = doc.select("p");
                ps.size();
                pText.size();
                pText.get(-1);
                pText.get(1);
                pText.get(4);
                pText.get(3);
                pText.get(11);
                org.junit.Assert.fail("eachText_literalMutationNumber15604 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("eachText_literalMutationNumber15604_failAssert0_literalMutationNumber18423 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

