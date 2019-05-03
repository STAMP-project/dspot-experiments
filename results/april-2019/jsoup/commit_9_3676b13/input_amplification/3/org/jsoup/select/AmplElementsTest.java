package org.jsoup.select;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void filter_add20055_literalMutationNumber20325_failAssert0() throws Exception {
        try {
            String h = "<p>Excl</p><div class=headline><p>Hello</p><p>There</p></div><div class=headline><h1>Headline</h1></div>";
            Document doc = Jsoup.parse(h);
            Elements els = doc.select(".headline").select("p");
            int o_filter_add20055__7 = els.size();
            String o_filter_add20055__8 = els.get(-1).text();
            String o_filter_add20055__10 = els.get(0).text();
            String o_filter_add20055__12 = els.get(1).text();
            org.junit.Assert.fail("filter_add20055_literalMutationNumber20325 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void filter_literalMutationString20028_failAssert0_add21216_failAssert0_literalMutationNumber26311_failAssert0() throws Exception {
        try {
            {
                {
                    String h = "";
                    Document doc = Jsoup.parse(h);
                    Elements els = doc.select(".headline").select("p");
                    els.size();
                    els.get(-1).text();
                    els.get(0).text();
                    els.get(1).text();
                    org.junit.Assert.fail("filter_literalMutationString20028 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("filter_literalMutationString20028_failAssert0_add21216 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("filter_literalMutationString20028_failAssert0_add21216_failAssert0_literalMutationNumber26311 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

