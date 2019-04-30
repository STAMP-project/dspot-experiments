package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testNextElementSiblings_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(1).id();
            Element element1 = doc.getElementById("b");
            List<Element> elementSiblings1 = element1.nextElementSiblings();
            elementSiblings1.size();
            elementSiblings1.get(0).id();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.nextElementSiblings();
            elementSiblings2.size();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.nextElementSiblings();
            elementSiblings3.size();
            elementSiblings3.get(0).id();
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }
}

