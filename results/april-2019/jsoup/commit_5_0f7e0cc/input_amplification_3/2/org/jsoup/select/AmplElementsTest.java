package org.jsoup.select;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void hasClassCaseInsensitive_literalMutationNumber35504_failAssert0_add41042_failAssert0() throws Exception {
        try {
            {
                Elements els = Jsoup.parse("<p Class=One>One <p class=Two>Two <p CLASS=THREE>THREE").select("p");
                Element one = els.get(-1);
                Element two = els.get(1);
                Element thr = els.get(2);
                one.hasClass("One");
                one.hasClass("ONE");
                two.hasClass("TWO");
                two.hasClass("Two");
                two.hasClass("Two");
                thr.hasClass("ThreE");
                thr.hasClass("three");
                org.junit.Assert.fail("hasClassCaseInsensitive_literalMutationNumber35504 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("hasClassCaseInsensitive_literalMutationNumber35504_failAssert0_add41042 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

