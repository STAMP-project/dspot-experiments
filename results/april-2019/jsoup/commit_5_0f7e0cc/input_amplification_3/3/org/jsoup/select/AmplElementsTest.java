package org.jsoup.select;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementsTest {
    @Test(timeout = 10000)
    public void parents_literalMutationNumber164891_failAssert0_literalMutationString165551_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse("");
                Elements parents = doc.select("p").parents();
                parents.size();
                parents.get(-1).tagName();
                parents.get(1).tagName();
                parents.get(2).tagName();
                org.junit.Assert.fail("parents_literalMutationNumber164891 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("parents_literalMutationNumber164891_failAssert0_literalMutationString165551 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void parents_add164902_literalMutationNumber165092_failAssert0() throws Exception {
        try {
            Document o_parents_add164902__1 = Jsoup.parse("<div><p>Hello</p></div><p>There</p>");
            Document doc = Jsoup.parse("<div><p>Hello</p></div><p>There</p>");
            Elements parents = doc.select("p").parents();
            int o_parents_add164902__7 = parents.size();
            String o_parents_add164902__8 = parents.get(-1).tagName();
            String o_parents_add164902__10 = parents.get(1).tagName();
            String o_parents_add164902__12 = parents.get(2).tagName();
            org.junit.Assert.fail("parents_add164902_literalMutationNumber165092 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

