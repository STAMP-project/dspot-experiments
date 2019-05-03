package org.jsoup.select;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void eq_literalMutationNumber25993_literalMutationNumber26223_failAssert0() throws Exception {
        try {
            String h = "<p>Hello<p>there<p>world";
            Document doc = Jsoup.parse(h);
            String o_eq_literalMutationNumber25993__4 = doc.select("p").eq(-1).text();
            String o_eq_literalMutationNumber25993__8 = doc.select("p").get(1).text();
            org.junit.Assert.fail("eq_literalMutationNumber25993_literalMutationNumber26223 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void eq_literalMutationNumber26000_literalMutationNumber26290_failAssert0() throws Exception {
        try {
            String h = "<p>Hello<p>there<p>world";
            Document doc = Jsoup.parse(h);
            String o_eq_literalMutationNumber26000__4 = doc.select("p").eq(1).text();
            String o_eq_literalMutationNumber26000__7 = doc.select("p").get(-1).text();
            org.junit.Assert.fail("eq_literalMutationNumber26000_literalMutationNumber26290 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

