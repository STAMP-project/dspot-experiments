package org.jsoup.nodes;


import java.util.Collection;
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
    public void testNextElementSiblings_add97_failAssert0_add9171_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add97 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_add9171 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString4405_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</diiv>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString4405 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10121_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10121 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
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
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationString6862_failAssert0() throws Exception {
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
                elementSiblings1.get(0).id();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.nextElementSiblings();
                elementSiblings2.size();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.nextElementSiblings();
                elementSiblings3.size();
                elementSiblings3.get(-1).id();
                Element div = doc.getElementById("");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationString6862 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
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
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_add10856_failAssert0() throws Exception {
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
                elementSiblings1.get(0).id();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.nextElementSiblings();
                elementSiblings2.size();
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add10856 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
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
    public void testNextElementSiblings_literalMutationString44_failAssert0_add10579_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString44 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add10579 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add90_failAssert0_add9401_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add90 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_add9401 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add92_failAssert0_add9073_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
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
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add9073 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add92_failAssert0_add9094_failAssert0() throws Exception {
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
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    elementSiblings4.get(0);
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add92 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add9094 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add94_failAssert0_add9002_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add94 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_add9002 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add91_failAssert0_add8986_failAssert0() throws Exception {
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
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add91 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add8986 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add93_failAssert0_literalMutationString1377_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_literalMutationString1377 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add93_failAssert0_add9236_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                elementSiblings.get(1).id();
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
                org.junit.Assert.fail("testNextElementSiblings_add93 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add9236 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add93_failAssert0_add9240_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add9240 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add94_failAssert0_literalMutationString522_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</u>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_literalMutationString522 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add91_failAssert0_literalMutationString435_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_literalMutationString435 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString4387_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString4387 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10136_failAssert0() throws Exception {
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
                elementSiblings3.get(0);
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10136 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add97_failAssert0_literalMutationNumber1158_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_literalMutationNumber1158 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add8976_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add91 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add8976 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add88_failAssert0_add9204_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                doc.getElementById("a");
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
                org.junit.Assert.fail("testNextElementSiblings_add88 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add88_failAssert0_add9204 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13567_failAssert0_add21712_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13567 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13567_failAssert0_add21712 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13519_failAssert0_add23441_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + " Hello\nthere \u00a0  ") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13519 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13519_failAssert0_add23441 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13559() throws Exception {
        Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li id=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Element element = doc.getElementById("b");
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        List<Element> elementSiblings = element.previousElementSiblings();
        Assert.assertFalse(elementSiblings.isEmpty());
        int o_testPreviousElementSiblings_literalMutationString13559__7 = elementSiblings.size();
        Assert.assertEquals(1, ((int) (o_testPreviousElementSiblings_literalMutationString13559__7)));
        String o_testPreviousElementSiblings_literalMutationString13559__8 = elementSiblings.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Element element1 = doc.getElementById("a");
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        List<Element> elementSiblings1 = element1.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__14 = elementSiblings1.size();
        Assert.assertEquals(0, ((int) (o_testPreviousElementSiblings_literalMutationString13559__14)));
        Element element2 = doc.getElementById("c");
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        List<Element> elementSiblings2 = element2.previousElementSiblings();
        Assert.assertFalse(elementSiblings2.isEmpty());
        int o_testPreviousElementSiblings_literalMutationString13559__19 = elementSiblings2.size();
        Assert.assertEquals(2, ((int) (o_testPreviousElementSiblings_literalMutationString13559__19)));
        String o_testPreviousElementSiblings_literalMutationString13559__20 = elementSiblings2.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        String o_testPreviousElementSiblings_literalMutationString13559__22 = elementSiblings2.get(1).id();
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Element ul = doc.getElementById("c");
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
        List<Element> elementSiblings3 = ul.previousElementSiblings();
        {
            Element element3 = elementSiblings3.get(0);
            Assert.assertTrue(((Element) (element3)).isBlock());
            Assert.assertTrue(((Element) (element3)).hasText());
            Assert.assertFalse(((Collection) (((Element) (element3)).getAllElements())).isEmpty());
            Assert.assertTrue(((Element) (element3)).hasParent());
            Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element3)).toString());
        }
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li id=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        Assert.assertFalse(elementSiblings.isEmpty());
        Assert.assertEquals(1, ((int) (o_testPreviousElementSiblings_literalMutationString13559__7)));
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        Assert.assertEquals(0, ((int) (o_testPreviousElementSiblings_literalMutationString13559__14)));
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        Assert.assertFalse(elementSiblings2.isEmpty());
        Assert.assertEquals(2, ((int) (o_testPreviousElementSiblings_literalMutationString13559__19)));
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13569_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13569 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13568_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13568 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13566_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13566 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13567_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13567 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13519_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13519 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13563_failAssert0_add22102_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13563 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13563_failAssert0_add22102 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13564_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13564 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13565_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13565 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13571_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13571 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13572_failAssert0_add21833_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13572 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13572_failAssert0_add21833 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13570_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13570 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13573_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13573 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13564_failAssert0_add21496_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13564 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13564_failAssert0_add21496 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13559_add21443() throws Exception {
        Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li id=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Element element = doc.getElementById("b");
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        List<Element> elementSiblings = element.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__7 = elementSiblings.size();
        String o_testPreviousElementSiblings_literalMutationString13559__8 = elementSiblings.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Element element1 = doc.getElementById("a");
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        List<Element> elementSiblings1 = element1.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__14 = elementSiblings1.size();
        Element element2 = doc.getElementById("c");
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        List<Element> elementSiblings2 = element2.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__19 = elementSiblings2.size();
        String o_testPreviousElementSiblings_literalMutationString13559_add21443__28 = elementSiblings2.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559_add21443__28);
        String o_testPreviousElementSiblings_literalMutationString13559__20 = elementSiblings2.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        String o_testPreviousElementSiblings_literalMutationString13559__22 = elementSiblings2.get(1).id();
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Element ul = doc.getElementById("c");
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
        List<Element> elementSiblings3 = ul.previousElementSiblings();
        {
            Element element3 = elementSiblings3.get(0);
            Assert.assertTrue(((Element) (element3)).hasText());
            Assert.assertFalse(((Collection) (((Element) (element3)).getAllElements())).isEmpty());
            Assert.assertTrue(((Element) (element3)).isBlock());
            Assert.assertTrue(((Element) (element3)).hasParent());
            Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element3)).toString());
        }
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li id=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559_add21443__28);
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13572_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13572 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13569_failAssert0_add21649_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0);
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13569 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13569_failAssert0_add21649 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13563_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13563 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13542_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13542 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13570_failAssert0_add21702_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                doc.getElementById("ul");
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add13570 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13570_failAssert0_add21702 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13578_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13578 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13559_literalMutationString13816() throws Exception {
        Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n   <div id=\"div\">\n    <li id=\"d\">d</li>\n   </div>\n  </ul>\n </body>\n</html>", ((Document) (doc)).toString());
        Element element = doc.getElementById("b");
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        List<Element> elementSiblings = element.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__7 = elementSiblings.size();
        String o_testPreviousElementSiblings_literalMutationString13559__8 = elementSiblings.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Element element1 = doc.getElementById("a");
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        List<Element> elementSiblings1 = element1.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__14 = elementSiblings1.size();
        Element element2 = doc.getElementById("c");
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        List<Element> elementSiblings2 = element2.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__19 = elementSiblings2.size();
        String o_testPreviousElementSiblings_literalMutationString13559__20 = elementSiblings2.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        String o_testPreviousElementSiblings_literalMutationString13559__22 = elementSiblings2.get(1).id();
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Element ul = doc.getElementById("c");
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
        List<Element> elementSiblings3 = ul.previousElementSiblings();
        {
            Element element3 = elementSiblings3.get(0);
            Assert.assertTrue(((Element) (element3)).hasText());
            Assert.assertFalse(((Collection) (((Element) (element3)).getAllElements())).isEmpty());
            Assert.assertTrue(((Element) (element3)).isBlock());
            Assert.assertTrue(((Element) (element3)).hasParent());
            Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element3)).toString());
        }
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n   <div id=\"div\">\n    <li id=\"d\">d</li>\n   </div>\n  </ul>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13579_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13579 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13560_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13560 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13577_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13577 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13565_failAssert0_add21559_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                doc.getElementById("b");
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13565 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13565_failAssert0_add21559 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13541_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13541 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13569_failAssert0_add21650_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0);
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13569 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13569_failAssert0_add21650 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13574_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13574 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13509_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'y>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13509 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13562_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13562 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13529_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li vid=\'d\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13529 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13575_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13575 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13561_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13561 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13576_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13576 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13540_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13540 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13526_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d</l>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13526 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13505_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>b</hli>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13505 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13566_failAssert0_add21624_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                element.previousElementSiblings();
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13566 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13566_failAssert0_add21624 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13504_failAssert0() throws Exception {
        try {
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
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13504 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13527_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13527 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13525_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13525 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13528_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "J#RAS9=bg#3>Y$r[t") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13528 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13524_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=zd\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13524 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13566_failAssert0_add21642_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13566 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13566_failAssert0_add21642 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13571_failAssert0_add21757_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                element1.previousElementSiblings();
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13571 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13571_failAssert0_add21757 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13559_literalMutationString13828() throws Exception {
        Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li Md=\'d\'>d</li>") + "</div>")));
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li md=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Element element = doc.getElementById("b");
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        List<Element> elementSiblings = element.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__7 = elementSiblings.size();
        String o_testPreviousElementSiblings_literalMutationString13559__8 = elementSiblings.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Element element1 = doc.getElementById("a");
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        List<Element> elementSiblings1 = element1.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__14 = elementSiblings1.size();
        Element element2 = doc.getElementById("c");
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        List<Element> elementSiblings2 = element2.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__19 = elementSiblings2.size();
        String o_testPreviousElementSiblings_literalMutationString13559__20 = elementSiblings2.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        String o_testPreviousElementSiblings_literalMutationString13559__22 = elementSiblings2.get(1).id();
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Element ul = doc.getElementById("c");
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
        List<Element> elementSiblings3 = ul.previousElementSiblings();
        {
            Element element3 = elementSiblings3.get(0);
            Assert.assertTrue(((Element) (element3)).hasText());
            Assert.assertFalse(((Collection) (((Element) (element3)).getAllElements())).isEmpty());
            Assert.assertTrue(((Element) (element3)).isBlock());
            Assert.assertTrue(((Element) (element3)).hasParent());
            Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element3)).toString());
        }
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li md=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13520_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=\'dxiv\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13520 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13522_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<divRid=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13522 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13578_failAssert0_add21529_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add13578 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13578_failAssert0_add21529 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13521_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<divid=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13521 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13523_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "IaGS3Va4 zw,3l") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13523 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13555_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13555 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13567_failAssert0_literalMutationString14744_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<rdiv>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13567 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13567_failAssert0_literalMutationString14744 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13556_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13556 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13568_failAssert0_add21585_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13568 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13568_failAssert0_add21585 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13566_failAssert0_literalMutationString14419_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + " Hello\nthere \u00a0  ") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13566 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13566_failAssert0_literalMutationString14419 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13559_add21475() throws Exception {
        Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li id=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Element element = doc.getElementById("b");
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        List<Element> elementSiblings = element.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__7 = elementSiblings.size();
        String o_testPreviousElementSiblings_literalMutationString13559__8 = elementSiblings.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Element element1 = doc.getElementById("a");
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        List<Element> elementSiblings1 = element1.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__14 = elementSiblings1.size();
        Element element2 = doc.getElementById("c");
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        List<Element> elementSiblings2 = element2.previousElementSiblings();
        int o_testPreviousElementSiblings_literalMutationString13559__19 = elementSiblings2.size();
        String o_testPreviousElementSiblings_literalMutationString13559__20 = elementSiblings2.get(0).id();
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        String o_testPreviousElementSiblings_literalMutationString13559__22 = elementSiblings2.get(1).id();
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Element ul = doc.getElementById("c");
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
        List<Element> elementSiblings3 = ul.previousElementSiblings();
        {
            Element element3 = elementSiblings3.get(0);
            Assert.assertTrue(((Element) (element3)).hasText());
            Assert.assertFalse(((Collection) (((Element) (element3)).getAllElements())).isEmpty());
            Assert.assertTrue(((Element) (element3)).isBlock());
            Assert.assertTrue(((Element) (element3)).hasParent());
            Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element3)).toString());
        }
        element1.isBlock();
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <ul id=\"ul\">\n   <li id=\"a\">a</li>\n   <li id=\"b\">b</li>\n   <li id=\"c\">c</li>\n  </ul>\n  <div id=\"div\">\n   <li id=\"d\">d</li>\n  </div>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertTrue(((Element) (element)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element)).isBlock());
        Assert.assertTrue(((Element) (element)).hasParent());
        Assert.assertEquals("<li id=\"b\">b</li>", ((Element) (element)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__8);
        Assert.assertTrue(((Element) (element1)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element1)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element1)).isBlock());
        Assert.assertTrue(((Element) (element1)).hasParent());
        Assert.assertEquals("<li id=\"a\">a</li>", ((Element) (element1)).toString());
        Assert.assertTrue(((Element) (element2)).hasText());
        Assert.assertFalse(((Collection) (((Element) (element2)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (element2)).isBlock());
        Assert.assertTrue(((Element) (element2)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (element2)).toString());
        Assert.assertEquals("a", o_testPreviousElementSiblings_literalMutationString13559__20);
        Assert.assertEquals("b", o_testPreviousElementSiblings_literalMutationString13559__22);
        Assert.assertTrue(((Element) (ul)).hasText());
        Assert.assertFalse(((Collection) (((Element) (ul)).getAllElements())).isEmpty());
        Assert.assertTrue(((Element) (ul)).isBlock());
        Assert.assertTrue(((Element) (ul)).hasParent());
        Assert.assertEquals("<li id=\"c\">c</li>", ((Element) (ul)).toString());
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13554_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13554 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13566_failAssert0_literalMutationString14413_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<B/ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13566 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13566_failAssert0_literalMutationString14413 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13552_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13552 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13551_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13551 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13550_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13550 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13581_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13581 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13582_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13582 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13580_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add13580 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13563_failAssert0_literalMutationString16187_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div i=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13563 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13563_failAssert0_literalMutationString16187 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13573_failAssert0_add21546_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13573 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13573_failAssert0_add21546 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13542_failAssert0_add21970_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13542 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13542_failAssert0_add21970 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13564_failAssert0_literalMutationString13865_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse(("" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13564 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13564_failAssert0_literalMutationString13865 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13568_failAssert0_add21593_failAssert0() throws Exception {
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
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add13568 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13568_failAssert0_add21593 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13542_failAssert0_literalMutationString15631_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13542 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13542_failAssert0_literalMutationString15631 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13496_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a</Ri>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13496 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13499_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a/li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13499 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13495_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13495 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber13549_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber13549 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13570_failAssert0_literalMutationString14665_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13570 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13570_failAssert0_literalMutationString14665 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13494_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13494 should have thrown IndexOutOfBoundsException");
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

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13568_failAssert0_literalMutationNumber14279_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(-1).id();
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13568 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13568_failAssert0_literalMutationNumber14279 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add13565_failAssert0_literalMutationString14156_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a</l[i>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add13565 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add13565_failAssert0_literalMutationString14156 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13497_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("`ZA+!keUy,1eKJHQw" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13497 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13516_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13516 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13515_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "hz,Id") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13515 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13535_failAssert0() throws Exception {
        try {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13535 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13513_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13513 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13518_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13518 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13517_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</il>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13517 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13514_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13514 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13530_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13530 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13531_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13531 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13533_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dDiv>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13533 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13512_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13512 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13511_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'>c /li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13511 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13534_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13534 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString13532_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "Hm58iR")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString13532 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }
}

