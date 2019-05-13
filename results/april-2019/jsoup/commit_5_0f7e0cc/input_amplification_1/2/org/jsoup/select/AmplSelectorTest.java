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
    public void descendant_literalMutationNumber12843_failAssert0() throws Exception {
        try {
            String h = "<div class=head><p class=first>Hello</p><p>There</p></div><p>None</p>";
            Document doc = Jsoup.parse(h);
            Element root = doc.getElementsByClass("HEAD").first();
            Elements els = root.select(".head p");
            els.size();
            els.get(0).text();
            els.get(1).text();
            Elements p = root.select("p.first");
            p.size();
            p.get(-1).text();
            Elements empty = root.select("p .first");
            empty.size();
            Elements aboveRoot = root.select("body div.head");
            aboveRoot.size();
            org.junit.Assert.fail("descendant_literalMutationNumber12843 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

