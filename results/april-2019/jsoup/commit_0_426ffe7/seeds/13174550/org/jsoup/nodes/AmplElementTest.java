package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString39_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<-i id=\'d\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString39 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
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
            org.junit.Assert.fail("testNextElementSiblings_add91 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add92_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0);
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
            org.junit.Assert.fail("testNextElementSiblings_add92 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add93_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(1).id();
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
            org.junit.Assert.fail("testNextElementSiblings_add93 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber75_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber75 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0() throws Exception {
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
            elementSiblings3.get(-1).id();
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add94_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(1);
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
            org.junit.Assert.fail("testNextElementSiblings_add94 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString6_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<u^l id=\'ul\'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString6 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

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
    public void testNextElementSiblings_add90_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
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
            org.junit.Assert.fail("testNextElementSiblings_add90 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add97_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add97 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add88_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            doc.getElementById("a");
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
            org.junit.Assert.fail("testNextElementSiblings_add88 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString44_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + ">Sk-Y@")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add95_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(1).id();
            doc.getElementById("b");
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
            org.junit.Assert.fail("testNextElementSiblings_add95 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add99_failAssert0() throws Exception {
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
            elementSiblings1.get(0);
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
            org.junit.Assert.fail("testNextElementSiblings_add99 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add96_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(1).id();
            Element element1 = doc.getElementById("b");
            element1.nextElementSiblings();
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
            org.junit.Assert.fail("testNextElementSiblings_add96 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber65_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber65 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add89_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            element.nextElementSiblings();
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
            org.junit.Assert.fail("testNextElementSiblings_add89 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString46_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString46 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString37_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString37 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber66_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber66 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add98_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add98 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add87_failAssert0() throws Exception {
        try {
            Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_add87 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString36_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<di+ id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString36 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add102_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add102 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add103_failAssert0() throws Exception {
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
            doc.getElementById("ul");
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.nextElementSiblings();
            elementSiblings3.size();
            elementSiblings3.get(0).id();
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add103 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add101_failAssert0() throws Exception {
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
            element2.nextElementSiblings();
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
            org.junit.Assert.fail("testNextElementSiblings_add101 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add104_failAssert0() throws Exception {
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
            ul.nextElementSiblings();
            List<Element> elementSiblings3 = ul.nextElementSiblings();
            elementSiblings3.size();
            elementSiblings3.get(0).id();
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add104 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add100_failAssert0() throws Exception {
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
            doc.getElementById("c");
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
            org.junit.Assert.fail("testNextElementSiblings_add100 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString43_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString43 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString42_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<l id=\'d\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString42 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString41_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "O4]?n@k53@pfX&Ce7") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString41 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString40_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d`\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString40 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add109_failAssert0() throws Exception {
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
            div.nextElementSiblings();
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add109 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add108_failAssert0() throws Exception {
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
            doc.getElementById("div");
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add108 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add106_failAssert0() throws Exception {
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
            elementSiblings3.get(0).id();
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add106 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add107_failAssert0() throws Exception {
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
            elementSiblings3.get(0);
            elementSiblings3.get(0).id();
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add107 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add105_failAssert0() throws Exception {
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
            elementSiblings3.size();
            elementSiblings3.get(0).id();
            Element div = doc.getElementById("div");
            List<Element> elementSiblings4 = div.nextElementSiblings();
            {
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add105 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber53_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(-1).id();
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber53 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber52_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(1).id();
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber52 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString10_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>I</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString10 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add110_failAssert0() throws Exception {
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
                elementSiblings4.get(0);
                Element elementSibling = elementSiblings4.get(0);
            }
            org.junit.Assert.fail("testNextElementSiblings_add110 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber59_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(0).id();
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber59 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber57_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(0).id();
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber57 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString22_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'>c<li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString22 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString12_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li. id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString12 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber58_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("a");
            List<Element> elementSiblings = element.nextElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            elementSiblings.get(0).id();
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber58 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber54_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber54 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString25_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString25 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber55_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber55 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString29_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "wIoIo") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString29 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString28_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString28 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString48_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</Wiv>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString48 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber83_failAssert0() throws Exception {
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
                Element elementSibling = elementSiblings4.get(1);
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber83 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber64_failAssert0() throws Exception {
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
            elementSiblings1.get(-1).id();
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber64 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber85_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber85 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber472_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber472 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString429_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "][U)3[K.,sN$sc") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString429 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber471_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber471 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber470_failAssert0() throws Exception {
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
                Element element3 = elementSiblings3.get(-1);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber470 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add479_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            doc.getElementById("a");
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
            org.junit.Assert.fail("testPreviousElementSiblings_add479 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add478_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0);
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
            org.junit.Assert.fail("testPreviousElementSiblings_add478 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add477_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
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
            org.junit.Assert.fail("testPreviousElementSiblings_add477 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add475_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            element.previousElementSiblings();
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
            org.junit.Assert.fail("testPreviousElementSiblings_add475 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add476_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
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
            org.junit.Assert.fail("testPreviousElementSiblings_add476 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add473_failAssert0() throws Exception {
        try {
            Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_add473 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add474_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            doc.getElementById("b");
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
            org.junit.Assert.fail("testPreviousElementSiblings_add474 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString404_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString404 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString426_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</?ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString426 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString425_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</+l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString425 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString405_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((" Hello\nthere \u00a0  " + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString405 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString403_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((" Q%pP[qo;(w9x?`=S" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString403 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString424_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "r>$a`") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString424 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString423_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + " Hello\nthere \u00a0  ") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString423 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString428_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString428 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString427_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<di1v id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString427 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add490_failAssert0() throws Exception {
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
            ul.previousElementSiblings();
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add490 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add491_failAssert0() throws Exception {
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
                elementSiblings3.get(0);
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add491 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString440_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dOv>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString440 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString441_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString441 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString422_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString422 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString421_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</u>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString421 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString442_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</rdiv>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString442 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber459_failAssert0() throws Exception {
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
            elementSiblings2.get(-1).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber459 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString444_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dv>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString444 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString443_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "V]/ -+")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString443 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber458_failAssert0() throws Exception {
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
            elementSiblings2.get(1).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber458 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add488_failAssert0() throws Exception {
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
            elementSiblings2.get(1);
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add488 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add489_failAssert0() throws Exception {
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
            doc.getElementById("ul");
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add489 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add486_failAssert0() throws Exception {
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
            elementSiblings2.get(0);
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add486 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add487_failAssert0() throws Exception {
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
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add487 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add483_failAssert0() throws Exception {
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
            element2.previousElementSiblings();
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add483 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber450_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber450 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString407_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li& id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString407 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber451_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber451 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add484_failAssert0() throws Exception {
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
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add484 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add485_failAssert0() throws Exception {
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
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add485 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber460_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber460 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber469_failAssert0() throws Exception {
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
                Element element3 = elementSiblings3.get(1);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber469 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
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

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber464_failAssert0() throws Exception {
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
            elementSiblings2.get(0).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber464 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber461_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber461 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber465_failAssert0() throws Exception {
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
            elementSiblings2.get(0).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber465 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString419_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<Li id=\'c\'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString419 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString439_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString439 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString418_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<Xli id=\'c\'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString418 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber463_failAssert0() throws Exception {
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
            elementSiblings2.get(0).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber463 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString436_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d<bli>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString436 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString437_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "|>U:5!4ATXygT9Wi!") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString437 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString435_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>ud</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString435 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString434_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString434 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString438_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<l id=\'d\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString438 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add482_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            doc.getElementById("c");
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
            org.junit.Assert.fail("testPreviousElementSiblings_add482 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add480_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            element1.previousElementSiblings();
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
            org.junit.Assert.fail("testPreviousElementSiblings_add480 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add481_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
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
            org.junit.Assert.fail("testPreviousElementSiblings_add481 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString430_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + " Hello\nthere \u00a0  ") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString430 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString432_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString432 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString433_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString433 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString431_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=#div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString431 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString410_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>b</i>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString410 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber449_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(-1).id();
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber449 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }
}

