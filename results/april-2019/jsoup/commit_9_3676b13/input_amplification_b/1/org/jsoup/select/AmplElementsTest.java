package org.jsoup.select;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void eachText_literalMutationNumber625_failAssert0() throws Exception {
        try {
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
            org.junit.Assert.fail("eachText_literalMutationNumber625 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

