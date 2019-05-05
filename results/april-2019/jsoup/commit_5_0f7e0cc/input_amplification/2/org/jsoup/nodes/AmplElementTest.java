package org.jsoup.nodes;


import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testGetParents_literalMutationNumber271235_failAssert0_literalMutationNumber272411_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div><p>Hello <span>there</span></div>");
                Element span = doc.select("span").first();
                Elements parents = span.parents();
                parents.size();
                parents.get(-1).tagName();
                parents.get(1).tagName();
                parents.get(2).tagName();
                parents.get(2).tagName();
                org.junit.Assert.fail("testGetParents_literalMutationNumber271235 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGetParents_literalMutationNumber271235_failAssert0_literalMutationNumber272411 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void insertChildrenAsCopy_literalMutationNumber146555_failAssert0null152540_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
                Element div1 = doc.select("div").get(-1);
                Element div2 = doc.select("div").get(1);
                Elements ps = doc.select(null).clone();
                ps.first().text("One cloned");
                div2.insertChildren((-1), ps);
                div1.childNodeSize();
                div2.childNodeSize();
                TextUtil.stripNewlines(doc.body().html());
                org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber146555 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("insertChildrenAsCopy_literalMutationNumber146555_failAssert0null152540 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

