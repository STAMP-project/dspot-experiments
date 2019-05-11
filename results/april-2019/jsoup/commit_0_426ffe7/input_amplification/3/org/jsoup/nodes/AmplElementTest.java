package org.jsoup.nodes;


import java.util.List;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;


public class AmplElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";

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
    public void testNextElementSiblings_literalMutationString38_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "") + " Hello\nthere \u00a0  ") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add90_failAssert0_add10177_failAssert0() throws Exception {
        try {
            {
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
                ul.nextElementSiblings();
                List<Element> elementSiblings3 = ul.nextElementSiblings();
                elementSiblings3.size();
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add90 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_add10177 should have thrown IndexOutOfBoundsException");
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
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
    public void testNextElementSiblings_literalMutationNumber76_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber76 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString45_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "Odwpau")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString45 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString46_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<i/div>")));
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
    public void testNextElementSiblings_literalMutationString47_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "y/div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString47 should have thrown IndexOutOfBoundsException");
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
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<diO id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationString6818_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div ie=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationString6818 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
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
    public void testNextElementSiblings_add97_failAssert0_add9937_failAssert0_add22248_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings1.get(0);
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
                    org.junit.Assert.fail("testNextElementSiblings_add97 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_add9937 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_add9937_failAssert0_add22248 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString42_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li d=\'d\'>d</li>") + "</div>")));
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
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "/|]6^FT)-ef&bk*20") + "</div>")));
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
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>!</li>") + "</div>")));
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
    public void testNextElementSiblings_add97_failAssert0_literalMutationString3703_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "}o1]Vl")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_literalMutationString3703 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add90_failAssert0_add10177_failAssert0_add22204_failAssert0() throws Exception {
        try {
            {
                {
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
                    ul.nextElementSiblings();
                    List<Element> elementSiblings3 = ul.nextElementSiblings();
                    elementSiblings3.size();
                    elementSiblings3.get(0).id();
                    Element div = doc.getElementById("div");
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        elementSiblings4.get(0);
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_add90 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_add10177 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_add10177_failAssert0_add22204 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_literalMutationString3018_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<pul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_literalMutationString3018 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add93_failAssert0_add10019_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add10019 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_literalMutationNumber3046_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_literalMutationNumber3046 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString44_failAssert0_add10566_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add10566 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add92_failAssert0_add9856_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add9856 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add91_failAssert0_add9752_failAssert0_literalMutationString19684_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'>cL</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Element element = doc.getElementById("a");
                    List<Element> elementSiblings = element.nextElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
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
                    org.junit.Assert.fail("testNextElementSiblings_add91 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9752 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9752_failAssert0_literalMutationString19684 should have thrown IndexOutOfBoundsException");
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
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "O/woO") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
    public void testNextElementSiblings_literalMutationString48_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dv>")));
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
    public void testNextElementSiblings_literalMutationString27_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString27 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
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
    public void testNextElementSiblings_literalMutationString44_failAssert0_literalMutationString5878_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id\'d\'>d</li>") + " Hello\nthere \u00a0  ")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_literalMutationString5878 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add93_failAssert0_add10020_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testNextElementSiblings_add93 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add10020 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_add9509_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add9509 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224_failAssert0_literalMutationString18790_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + " Hello\nthere \u00a0  ") + " Hello\nthere \u00a0  ") + "</div>")));
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
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224_failAssert0_literalMutationString18790 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add88_failAssert0_add9989_failAssert0_add22261_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    doc.getElementById("a");
                    doc.getElementById("a");
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
                    org.junit.Assert.fail("testNextElementSiblings_add88 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_add88_failAssert0_add9989 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add88_failAssert0_add9989_failAssert0_add22261 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add97_failAssert0_add9937_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testNextElementSiblings_add97 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_add9937 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add9752_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9752 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add9740_failAssert0_add21730_failAssert0() throws Exception {
        try {
            {
                {
                    Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                        elementSiblings4.get(0);
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_add91 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9740 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9740_failAssert0_add21730 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add88_failAssert0_add9989_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testNextElementSiblings_add88 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add88_failAssert0_add9989 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add9740_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9740 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add9752_failAssert0_add22789_failAssert0() throws Exception {
        try {
            {
                {
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
                    org.junit.Assert.fail("testNextElementSiblings_add91 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9752 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9752_failAssert0_add22789 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_add10846_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings3.get(-1).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add10846 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
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
    public void testNextElementSiblings_add88_failAssert0_literalMutationString3861_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + " Hello\nthere \u00a0  ") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add88_failAssert0_literalMutationString3861 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_add10846_failAssert0_add21927_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Element element = doc.getElementById("a");
                    List<Element> elementSiblings = element.nextElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0);
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
                    elementSiblings3.get(-1).id();
                    Element div = doc.getElementById("div");
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add10846 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add10846_failAssert0_add21927 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
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
    public void testNextElementSiblings_add93_failAssert0_literalMutationString3965_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_literalMutationString3965 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add94_failAssert0_literalMutationNumber3144_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                elementSiblings.get(1);
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
                org.junit.Assert.fail("testNextElementSiblings_add94 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_literalMutationNumber3144 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString18_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>b<zli>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString18 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224_failAssert0_add22479_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "") + " Hello\nthere \u00a0  ") + "</div>")));
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
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2224_failAssert0_add22479 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
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
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'Ha</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
    public void testNextElementSiblings_literalMutationString44_failAssert0_literalMutationString5886_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "html > body > fb|comments")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_literalMutationString5886 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_add10846_failAssert0null23312_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings3.get(-1).id();
                    Element div = doc.getElementById(null);
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add10846 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add10846_failAssert0null23312 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add94_failAssert0_add9785_failAssert0() throws Exception {
        try {
            {
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
                ul.nextElementSiblings();
                List<Element> elementSiblings3 = ul.nextElementSiblings();
                elementSiblings3.size();
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add94 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_add9785 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2240_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "<P/div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2240 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationNumber86_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber86 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString14_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'></li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString14 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString26_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + " Hello\nthere \u00a0  ") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString26 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add90_failAssert0_literalMutationNumber4537_failAssert0() throws Exception {
        try {
            {
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
                    Element elementSibling = elementSiblings4.get(-1);
                }
                org.junit.Assert.fail("testNextElementSiblings_add90 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_literalMutationNumber4537 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_add9493_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add9493 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString28_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "`/ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
    public void testNextElementSiblingsnull113_failAssert0_literalMutationNumber2750_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
                elementSiblings.get(-1).id();
                elementSiblings.get(1).id();
                Element element1 = doc.getElementById(null);
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
                org.junit.Assert.fail("testNextElementSiblingsnull113 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNextElementSiblingsnull113_failAssert0_literalMutationNumber2750 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add92_failAssert0_add9846_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                doc.getElementById("a");
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add9846 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add9740_failAssert0_literalMutationString16213_failAssert0() throws Exception {
        try {
            {
                {
                    Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'\'>d</li>") + "</div>")));
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
                }
                org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9740 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add9740_failAssert0_literalMutationString16213 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add93_failAssert0_add10019_failAssert0_literalMutationNumber20189_failAssert0() throws Exception {
        try {
            {
                {
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
                    org.junit.Assert.fail("testNextElementSiblings_add93 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add10019 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add10019_failAssert0_literalMutationNumber20189 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0null38346_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById(null);
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0null38346 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_literalMutationNumber42381_failAssert0() throws Exception {
        try {
            {
                {
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
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_literalMutationNumber42381 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884_failAssert0() throws Exception {
        try {
            {
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
                    Element element3 = elementSiblings3.get(-1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28188_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28188 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28187_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28187 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28202 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<dv id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28200_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28200 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28201_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28201 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_literalMutationString42335_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<i id=\'b\'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_literalMutationString42335 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28203_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28203 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28202_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28202 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244_failAssert0null48681_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Element element = doc.getElementById("b");
                    List<Element> elementSiblings = element.previousElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
                    Element element1 = doc.getElementById(null);
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244_failAssert0null48681 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28173_failAssert0_literalMutationString32554_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>b<./li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28173 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28173_failAssert0_literalMutationString32554 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28189_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28189 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28174_failAssert0_literalMutationNumber35710_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28174 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28174_failAssert0_literalMutationNumber35710 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28178_failAssert0_literalMutationNumber34726_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28178 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28178_failAssert0_literalMutationNumber34726 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28188_failAssert0_add36887_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                doc.getElementById("b");
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28188 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28188_failAssert0_add36887 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28158_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28158 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28157_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "J4H1Dp")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28157 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0null48716_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<ti id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Element element = doc.getElementById("b");
                    List<Element> elementSiblings = element.previousElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
                    Element element1 = doc.getElementById(null);
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0null48716 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28175_failAssert0_literalMutationString35536_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=Xd\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28175 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28175_failAssert0_literalMutationString35536 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28177_failAssert0_add37219_failAssert0_add47568_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(0).id();
                    Element ul = doc.getElementById("ul");
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28177 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28177_failAssert0_add37219 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28177_failAssert0_add37219_failAssert0_add47568 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28155_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28155 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28156_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</d[iv>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28156 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28154_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28154 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28179_failAssert0_add37997_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28179 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28179_failAssert0_add37997 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139_failAssert0_add46423_failAssert0() throws Exception {
        try {
            {
                {
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
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139_failAssert0_add46423 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28204_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28204 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28205_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28205 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525_failAssert0_add46681_failAssert0() throws Exception {
        try {
            {
                {
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
                    org.junit.Assert.fail("testPreviousElementSiblings_add28189 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525_failAssert0_add46681 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28187_failAssert0_literalMutationString30879_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</iv>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28187 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28187_failAssert0_literalMutationString30879 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28203_failAssert0_literalMutationString31573_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=\'diQv\'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28203 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28203_failAssert0_literalMutationString31573 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28201_failAssert0_literalMutationString30665_failAssert0_add46507_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d/li>") + "</div>")));
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
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28201 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28201_failAssert0_literalMutationString30665 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28201_failAssert0_literalMutationString30665_failAssert0_add46507 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28151_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>dC/li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28151 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28199_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28199 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28153_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</uiv>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28153 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244_failAssert0_add47797_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(0);
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244_failAssert0_add47797 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28163_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_add36068_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_add36068 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28165_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28165 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_literalMutationString44835_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<ti id=\'a\'>a</li>" + "<li id='b'>b</li>") + ",li id=\'c\'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_literalMutationString44835 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884_failAssert0_add48307_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884_failAssert0_add48307 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28193_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28193 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28191_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28191 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28189_failAssert0_add36846_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                element.previousElementSiblings();
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28189 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28189_failAssert0_add36846 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28172_failAssert0_add36497_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28172 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28172_failAssert0_add36497 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28197_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28197 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28174_failAssert0_add37965_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28174 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28174_failAssert0_add37965 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948_failAssert0_add46760_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<dv id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948_failAssert0_add46760 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28195_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28195 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28202_failAssert0_add36803_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28202 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28202_failAssert0_add36803 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_literalMutationString32797_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "zdiv id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_literalMutationString32797 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
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
    public void testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133_failAssert0_literalMutationString40603_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>Zd</li>") + "</div>")));
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
                    elementSiblings2.get(2).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133_failAssert0_literalMutationString40603 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28140_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<%ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28140 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28201_failAssert0_add36636_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28201 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28201_failAssert0_add36636 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28142_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28142 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28121_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<i id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28121 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525_failAssert0_literalMutationString40370_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "lr2Z]4O>!qS%z[") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_add28189 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525_failAssert0_literalMutationString40370 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28148_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id\'d\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28148 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28175_failAssert0_add37909_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28175 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28175_failAssert0_add37909 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139_failAssert0_add46419_failAssert0() throws Exception {
        try {
            {
                {
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
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139_failAssert0_add46419 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28146_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=\'dCv\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28146 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28144_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=\'di$v\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28144 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28187_failAssert0_add36687_failAssert0() throws Exception {
        try {
            {
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
                doc.getElementById("ul");
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28187 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28187_failAssert0_add36687 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28173_failAssert0_add37130_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(-1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28173 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28173_failAssert0_add37130 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28127_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>6b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28127 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884_failAssert0_literalMutationString46207_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "&1I%W") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884_failAssert0_literalMutationString46207 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<ti id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28183_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28183 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0() throws Exception {
        try {
            {
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
                    Element element3 = elementSiblings3.get(-1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28186_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_add47308_failAssert0() throws Exception {
        try {
            {
                {
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
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_add47308 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884_failAssert0null48803_failAssert0() throws Exception {
        try {
            {
                {
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
                    Element ul = doc.getElementById(null);
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28163_failAssert0_literalMutationNumber34884_failAssert0null48803 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_add47935_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<ti id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_add47935 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28177_failAssert0_add37219_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28177 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28177_failAssert0_add37219 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_add47937_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<ti id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_add47937 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_add47301_failAssert0() throws Exception {
        try {
            {
                {
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
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37206_failAssert0_add47301 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28201_failAssert0_literalMutationString30665_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d/li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28201 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28201_failAssert0_literalMutationString30665 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28189 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28189_failAssert0_literalMutationNumber31525 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0null38346_failAssert0_literalMutationString42176_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Element element = doc.getElementById("b");
                    List<Element> elementSiblings = element.previousElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
                    Element element1 = doc.getElementById(null);
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0null38346 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0null38346_failAssert0_literalMutationString42176 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(2).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28177_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28177 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28179_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28179 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948_failAssert0_literalMutationNumber40700_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<dv id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_literalMutationString32948_failAssert0_literalMutationNumber40700 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28172_failAssert0_literalMutationNumber30232_failAssert0() throws Exception {
        try {
            {
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
                    Element element3 = elementSiblings3.get(-1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28172 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28172_failAssert0_literalMutationNumber30232 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28178_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28178 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28173_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28173 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28172_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28172 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_add36079_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_add36079 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28174_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28174 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28175_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28175 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0null38346_failAssert0_literalMutationString42212_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
                    Element element = doc.getElementById("b");
                    List<Element> elementSiblings = element.previousElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
                    Element element1 = doc.getElementById(null);
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0null38346 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0null38346_failAssert0_literalMutationString42212 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28138_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28138 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28139_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</Rul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28139 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28137_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "-)F]1") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28137 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28136_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28136 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28118_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("nwqD{3EP*u.b7XZU7" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28118 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28604_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "Ctc@#R*M^B!]t(gn-") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28604 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28133_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<Pli id=\'c\'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28133 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28135_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28135 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28188_failAssert0_literalMutationString31648_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + " Hello\nthere \u00a0  ") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28188 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28188_failAssert0_literalMutationString31648 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28119_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28119 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28186_failAssert0_add37273_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_add37273 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28178_failAssert0_add37697_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28178 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28178_failAssert0_add37697 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28152_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "[GcRPTuoZsx^T|;W5") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28152 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28150_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28150 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28200_failAssert0_add36991_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0);
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28200 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28200_failAssert0_add36991 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28164_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28164 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372_failAssert0_literalMutationNumber40169_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(0);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28202 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372_failAssert0_literalMutationNumber40169 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28196_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28196 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28192_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28192 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28183_failAssert0_add36136_failAssert0() throws Exception {
        try {
            {
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
                    Element element3 = elementSiblings3.get(1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28183 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28183_failAssert0_add36136 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28198_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28198 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28190_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28190 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_literalMutationString32782_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>b</Ai>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_literalMutationString32782 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_literalMutationString44861_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<ti id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</d9iv>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28185_failAssert0_literalMutationString28573_failAssert0_literalMutationString44861 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244_failAssert0_literalMutationNumber44345_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(0);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0_add37244_failAssert0_literalMutationNumber44345 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372_failAssert0_add46604_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(0);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28202 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28202_failAssert0_literalMutationNumber31372_failAssert0_add46604 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28183_failAssert0_add36146_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28183 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28183_failAssert0_add36146 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28194_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28194 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28141_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28141 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28120_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a<g/li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28120 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28143_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<dv id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28143 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28117_failAssert0null38346_failAssert0_add47266_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Element element = doc.getElementById("b");
                    List<Element> elementSiblings = element.previousElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
                    Element element1 = doc.getElementById(null);
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0null38346 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28117_failAssert0null38346_failAssert0_add47266 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28149_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28149 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28126_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'!b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28126 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28147_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d</vli>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28147 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37198_failAssert0() throws Exception {
        try {
            {
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
                    Element element3 = elementSiblings3.get(-1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28184_failAssert0_add37198 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28145_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "/W&k[,J q9n{8%") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28145 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28122_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'Da</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28122 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139_failAssert0_literalMutationString39402_failAssert0() throws Exception {
        try {
            {
                {
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
                        Element element3 = elementSiblings3.get(-1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28186_failAssert0_literalMutationNumber33139_failAssert0_literalMutationString39402 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133_failAssert0_add46738_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(2).id();
                    doc.getElementById("ul");
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28176_failAssert0_literalMutationString34133_failAssert0_add46738 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28203_failAssert0_add36860_failAssert0() throws Exception {
        try {
            {
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
                doc.getElementById("ul");
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28203 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28203_failAssert0_add36860 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }
}

