package org.jsoup.select;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void eachText_add16898_literalMutationNumber17933_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse("<div><p>1<p>2<p>3<p>4<p>5<p>6</div><div><p>7<p>8<p>9<p>10<p>11<p>12<p></p></div>");
            List<String> divText = doc.select("div").eachText();
            int o_eachText_add16898__6 = divText.size();
            String o_eachText_add16898__7 = divText.get(-1);
            String o_eachText_add16898__8 = divText.get(1);
            List<String> pText = doc.select("p").eachText();
            Elements ps = doc.select("p");
            int o_eachText_add16898__14 = ps.size();
            int o_eachText_add16898__15 = pText.size();
            int o_eachText_add16898__16 = pText.size();
            String o_eachText_add16898__17 = pText.get(0);
            String o_eachText_add16898__18 = pText.get(1);
            String o_eachText_add16898__19 = pText.get(4);
            String o_eachText_add16898__20 = pText.get(6);
            String o_eachText_add16898__21 = pText.get(11);
            org.junit.Assert.fail("eachText_add16898_literalMutationNumber17933 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

