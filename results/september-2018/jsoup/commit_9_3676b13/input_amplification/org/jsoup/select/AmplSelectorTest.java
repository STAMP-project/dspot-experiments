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
    public void testNestedHas_literalMutationString336789() throws Exception {
        Document doc = Jsoup.parse("<div><p><span>One</span></p>5</div> <div><p>Two</p></div>");
        Elements divs = doc.select("div:has(p:has(span))");
        Assert.assertFalse(divs.isEmpty());
        divs.size();
        divs.size();
        divs.size();
        divs.size();
        divs.size();
        divs.size();
        String o_testNestedHas_literalMutationString336789__6 = divs.first().text();
        Assert.assertEquals("One5", o_testNestedHas_literalMutationString336789__6);
        divs = doc.select("div:has(p:matches((?i)two))");
        Assert.assertFalse(divs.isEmpty());
        String o_testNestedHas_literalMutationString336789__13 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString336789__13);
        String o_testNestedHas_literalMutationString336789__15 = divs.first().text();
        Assert.assertEquals("Two", o_testNestedHas_literalMutationString336789__15);
        divs = doc.select("div:has(p:contains(two))");
        Assert.assertFalse(divs.isEmpty());
        String o_testNestedHas_literalMutationString336789__23 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString336789__23);
        String o_testNestedHas_literalMutationString336789__25 = divs.first().tagName();
        Assert.assertEquals("div", o_testNestedHas_literalMutationString336789__25);
        String o_testNestedHas_literalMutationString336789__27 = divs.first().text();
        Assert.assertEquals("Two", o_testNestedHas_literalMutationString336789__27);
        String o_testNestedHas_literalMutationString336789__29 = divs.first().text();
        Assert.assertEquals("Two", o_testNestedHas_literalMutationString336789__29);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("One5", o_testNestedHas_literalMutationString336789__6);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("div", o_testNestedHas_literalMutationString336789__13);
        Assert.assertEquals("Two", o_testNestedHas_literalMutationString336789__15);
        Assert.assertFalse(divs.isEmpty());
        Assert.assertEquals("div", o_testNestedHas_literalMutationString336789__23);
        Assert.assertEquals("div", o_testNestedHas_literalMutationString336789__25);
        Assert.assertEquals("Two", o_testNestedHas_literalMutationString336789__27);
    }
}

