package org.jsoup.select;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void eachAttr_literalMutationNumber751_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><a href='/foo'>1</a><a href='http://example.com/bar'>2</a><a href=''>3</a><a>4</a>", "http://example.com");
            List<String> hrefAttrs = doc.select("a").eachAttr("href");
            hrefAttrs.size();
            hrefAttrs.get(0);
            hrefAttrs.get(1);
            hrefAttrs.get(2);
            doc.select("a").size();
            List<String> absAttrs = doc.select("a").eachAttr("abs:href");
            absAttrs.size();
            absAttrs.size();
            absAttrs.size();
            absAttrs.get(-1);
            absAttrs.get(1);
            absAttrs.get(2);
            org.junit.Assert.fail("eachAttr_literalMutationNumber751 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

