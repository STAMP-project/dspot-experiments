package org.jsoup.select;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void hasClassCaseInsensitive_literalMutationString197140_literalMutationNumber199048_failAssert0null219189_failAssert0() throws Exception {
        try {
            {
                Elements els = Jsoup.parse("<p Class=One>One <p class=Two>Two <p CLASS=THREE>THREE").select("p");
                Element one = els.get(-1);
                Element two = els.get(1);
                Element thr = els.get(2);
                boolean o_hasClassCaseInsensitive_literalMutationString197140__10 = one.hasClass("One");
                boolean o_hasClassCaseInsensitive_literalMutationString197140__11 = one.hasClass("ONE");
                boolean o_hasClassCaseInsensitive_literalMutationString197140__12 = two.hasClass(null);
                boolean o_hasClassCaseInsensitive_literalMutationString197140__13 = two.hasClass("Two");
                boolean o_hasClassCaseInsensitive_literalMutationString197140__14 = thr.hasClass("ThreE");
                boolean o_hasClassCaseInsensitive_literalMutationString197140__15 = thr.hasClass("three");
                org.junit.Assert.fail("hasClassCaseInsensitive_literalMutationString197140_literalMutationNumber199048 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("hasClassCaseInsensitive_literalMutationString197140_literalMutationNumber199048_failAssert0null219189 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void eachAttr_literalMutationNumber105162_failAssert0_add110916_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse("<div><a href='/foo'>1</a><a href='http://example.com/bar'>2</a><a href=''>3</a><a>4</a>", "http://example.com");
                Document doc = Jsoup.parse("<div><a href='/foo'>1</a><a href='http://example.com/bar'>2</a><a href=''>3</a><a>4</a>", "http://example.com");
                List<String> hrefAttrs = doc.select("a").eachAttr("href");
                hrefAttrs.size();
                hrefAttrs.get(-1);
                hrefAttrs.get(1);
                hrefAttrs.get(2);
                doc.select("a").size();
                List<String> absAttrs = doc.select("a").eachAttr("abs:href");
                absAttrs.size();
                absAttrs.size();
                absAttrs.size();
                absAttrs.get(0);
                absAttrs.get(1);
                absAttrs.get(2);
                org.junit.Assert.fail("eachAttr_literalMutationNumber105162 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("eachAttr_literalMutationNumber105162_failAssert0_add110916 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

