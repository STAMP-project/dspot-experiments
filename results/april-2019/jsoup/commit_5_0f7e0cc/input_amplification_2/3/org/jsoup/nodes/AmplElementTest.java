package org.jsoup.nodes;


import java.util.Collection;
import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void getElementsByTagName_add470044_literalMutationNumber472252_failAssert0_add486499_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(reference);
                List<Element> divs = doc.getElementsByTag("div");
                int o_getElementsByTagName_add470044__5 = divs.size();
                Element o_getElementsByTagName_add470044__6 = divs.get(0);
                String o_getElementsByTagName_add470044__7 = divs.get(0).id();
                String o_getElementsByTagName_add470044__9 = divs.get(1).id();
                List<Element> ps = doc.getElementsByTag("p");
                ps.size();
                int o_getElementsByTagName_add470044__13 = ps.size();
                ((TextNode) (ps.get(0).childNode(0))).getWholeText();
                ((TextNode) (ps.get(1).childNode(-1))).getWholeText();
                List<Element> ps2 = doc.getElementsByTag("P");
                List<Element> imgs = doc.getElementsByTag("img");
                String o_getElementsByTagName_add470044__24 = imgs.get(0).attr("src");
                List<Element> empty = doc.getElementsByTag("wtf");
                int o_getElementsByTagName_add470044__28 = empty.size();
                org.junit.Assert.fail("getElementsByTagName_add470044_literalMutationNumber472252 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("getElementsByTagName_add470044_literalMutationNumber472252_failAssert0_add486499 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void insertChildrenArgumentValidation_literalMutationNumber138559_failAssert0_literalMutationNumber140223_failAssert0_literalMutationString142330_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse("<div id=1>Text <p>One</p> Text <p>Two</p></div><div id=2></div>");
                    Element div1 = doc.select("div").get(-1);
                    Element div2 = doc.select("Tiv").get(1);
                    List<Node> children = div1.childNodes();
                    {
                        div2.insertChildren(6, children);
                    }
                    {
                        div2.insertChildren((-5), children);
                    }
                    {
                        div2.insertChildren(0, ((Collection<? extends Node>) (null)));
                    }
                    org.junit.Assert.fail("insertChildrenArgumentValidation_literalMutationNumber138559 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("insertChildrenArgumentValidation_literalMutationNumber138559_failAssert0_literalMutationNumber140223 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("insertChildrenArgumentValidation_literalMutationNumber138559_failAssert0_literalMutationNumber140223_failAssert0_literalMutationString142330 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }
}

