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
    public void testNextElementSiblings_literalMutationString44_failAssert0_add10584_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString44 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add10584 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0_literalMutationString17995_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<i id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=\'dPv\'>") + "<li id='d'>d</li>") + "</div>")));
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
                    Element ul = doc.getElementById("1");
                    List<Element> elementSiblings3 = ul.nextElementSiblings();
                    elementSiblings3.size();
                    elementSiblings3.get(0).id();
                    Element div = doc.getElementById("div");
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString71 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0_literalMutationString17995 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10288_failAssert0() throws Exception {
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
                Element div = doc.getElementById("div");
                div.nextElementSiblings();
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10288 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10281_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10281 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationNumber73_failAssert0_literalMutationNumber5067_failAssert0_literalMutationNumber17176_failAssert0() throws Exception {
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
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber73 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber73_failAssert0_literalMutationNumber5067 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber73_failAssert0_literalMutationNumber5067_failAssert0_literalMutationNumber17176 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0_literalMutationString17984_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<i id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'>c<</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    Element ul = doc.getElementById("1");
                    List<Element> elementSiblings3 = ul.nextElementSiblings();
                    elementSiblings3.size();
                    elementSiblings3.get(0).id();
                    Element div = doc.getElementById("div");
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString71 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0_literalMutationString17984 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString6_failAssert0null12017_failAssert0_literalMutationNumber15671_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id=\'u\'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    Element div = doc.getElementById(null);
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString6 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString6_failAssert0null12017 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString6_failAssert0null12017_failAssert0_literalMutationNumber15671 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
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
    public void testNextElementSiblings_add90_failAssert0_add9388_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_add9388 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_add10865_failAssert0() throws Exception {
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
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add10865 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add92_failAssert0_add9091_failAssert0() throws Exception {
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
                elementSiblings3.get(0);
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add92 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add9091 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString44_failAssert0_literalMutationNumber5907_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_literalMutationNumber5907 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10281_failAssert0_add20492_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10281 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10281_failAssert0_add20492 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString44_failAssert0_add10584_failAssert0_add21005_failAssert0() throws Exception {
        try {
            {
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
                    Element element2 = doc.getElementById("c");
                    List<Element> elementSiblings2 = element2.nextElementSiblings();
                    elementSiblings2.size();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.nextElementSiblings();
                    elementSiblings3.size();
                    elementSiblings3.size();
                    elementSiblings3.size();
                    elementSiblings3.get(0).id();
                    Element div = doc.getElementById("div");
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString44 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add10584 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add10584_failAssert0_add21005 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add93_failAssert0_add9248_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add93 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add9248 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<i id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                Element ul = doc.getElementById("1");
                List<Element> elementSiblings3 = ul.nextElementSiblings();
                elementSiblings3.size();
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString71 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0_add22171_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<i id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    Element ul = doc.getElementById("1");
                    ul.nextElementSiblings();
                    List<Element> elementSiblings3 = ul.nextElementSiblings();
                    elementSiblings3.size();
                    elementSiblings3.get(0).id();
                    Element div = doc.getElementById("div");
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString71 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0_add22171 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add8965_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add8965 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add94_failAssert0_add8995_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
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
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_add8995 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add91_failAssert0_add8981_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add91 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add8981 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add93_failAssert0_add9243_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add93 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add9243 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationNumber4885_failAssert0() throws Exception {
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
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationNumber4885 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString31_failAssert0null11807_failAssert0_literalMutationString15715_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<ul>") + "<div id=\'dSiv\'>") + "<li id='d'>d</li>") + "</div>")));
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
                    Element div = doc.getElementById(null);
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString31 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString31_failAssert0null11807 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString31_failAssert0null11807_failAssert0_literalMutationString15715 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10288_failAssert0_add22424_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
                    div.nextElementSiblings();
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10288 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10288_failAssert0_add22424 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add92_failAssert0_add9090_failAssert0() throws Exception {
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
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add92 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add9090 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add97_failAssert0_add9161_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_add9161 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString44_failAssert0_add10584_failAssert0_add20999_failAssert0() throws Exception {
        try {
            {
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
                    elementSiblings1.get(0);
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
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString44 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add10584 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add10584_failAssert0_add20999 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add88_failAssert0_add9222_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add88 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add88_failAssert0_add9222 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0null23318_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<i id=\'a\'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    Element element = doc.getElementById("a");
                    List<Element> elementSiblings = element.nextElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
                    elementSiblings.get(1).id();
                    Element element1 = doc.getElementById("b");
                    List<Element> elementSiblings1 = element1.nextElementSiblings();
                    elementSiblings1.size();
                    elementSiblings1.get(0).id();
                    Element element2 = doc.getElementById(null);
                    List<Element> elementSiblings2 = element2.nextElementSiblings();
                    elementSiblings2.size();
                    Element ul = doc.getElementById("1");
                    List<Element> elementSiblings3 = ul.nextElementSiblings();
                    elementSiblings3.size();
                    elementSiblings3.get(0).id();
                    Element div = doc.getElementById("div");
                    List<Element> elementSiblings4 = div.nextElementSiblings();
                    {
                        Element elementSibling = elementSiblings4.get(0);
                    }
                    org.junit.Assert.fail("testNextElementSiblings_literalMutationString71 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString71_failAssert0_literalMutationString7918_failAssert0null23318 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28209_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28208_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28203_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=U\'d\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28255_failAssert0_add36451_failAssert0() throws Exception {
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
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28255 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36451 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28202_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28202 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_literalMutationNumber43480_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
                    Element element = doc.getElementById("b");
                    List<Element> elementSiblings = element.previousElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(1).id();
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_literalMutationNumber43480 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28174_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\',a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28172_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a<C/li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28175_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("p5e`osqZ.+Ejjy#PM" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28171_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28205_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'P\'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28205 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_add47487_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_add47487 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28207_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28206_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28206 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0() throws Exception {
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
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<liid=\'d\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28256 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677_failAssert0() throws Exception {
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
                    Element element3 = elementSiblings3.get(1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28257 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0_add46305_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0_add46305 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28251_failAssert0_add36183_failAssert0() throws Exception {
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
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    elementSiblings3.get(0);
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28251 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28251_failAssert0_add36183 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28203_failAssert0_add37794_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=U\'d\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203_failAssert0_add37794 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29107_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29107 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28175_failAssert0_literalMutationNumber33949_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("p5e`osqZ.+Ejjy#PM" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175_failAssert0_literalMutationNumber33949 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28252_failAssert0_literalMutationString29789_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=\'3iv\'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28252 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28252_failAssert0_literalMutationString29789 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a<C/li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28228_failAssert0_add37167_failAssert0() throws Exception {
        try {
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
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28228 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28228_failAssert0_add37167 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28210_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<H/div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28210 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28211_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "18-4H7")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28211 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28171_failAssert0null38474_failAssert0_literalMutationString41764_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((" Hello\nthere \u00a0  " + "<li id='b'>b</li>") + "") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0null38474 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0null38474_failAssert0_literalMutationString41764 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0_add37749_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_add37749 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28191_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "t[h_,") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28191 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28193_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<Cul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28193 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28253_failAssert0_literalMutationString29157_failAssert0_literalMutationNumber42484_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "_/7&D$:aS{blneTa#") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    elementSiblings2.get(0);
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28253 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28253_failAssert0_literalMutationString29157 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28253_failAssert0_literalMutationString29157_failAssert0_literalMutationNumber42484 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0_literalMutationString39326_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li d=\'d\'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0_literalMutationString39326 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28255_failAssert0_literalMutationString29876_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28255 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_literalMutationString29876 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28256_failAssert0_add36475_failAssert0() throws Exception {
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
                doc.getElementById("ul");
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28256 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28256_failAssert0_add36475 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28206_failAssert0_add36825_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28206 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28206_failAssert0_add36825 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28205_failAssert0_add37383_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'P\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28205 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28205_failAssert0_add37383 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28195_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28195 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28239_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28239 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28209_failAssert0_add36707_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_add36707 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28237_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28237 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28250_failAssert0_literalMutationString29300_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>na</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28250 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28250_failAssert0_literalMutationString29300 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690_failAssert0_literalMutationString39720_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a<C/li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d<li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690_failAssert0_literalMutationString39720 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28202_failAssert0_add37611_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28202 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28202_failAssert0_add37611 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28231_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28231 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956_failAssert0_add47411_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<liid=\'d\'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_add28256 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956_failAssert0_add47411 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28175_failAssert0null38460_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("p5e`osqZ.+Ejjy#PM" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById(null);
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175_failAssert0null38460 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28208_failAssert0_literalMutationString34819_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + " Hello\nthere \u00a0  ") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208_failAssert0_literalMutationString34819 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28202_failAssert0_literalMutationString34197_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'#a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28202 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28202_failAssert0_literalMutationString34197 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28255_failAssert0_add36451_failAssert0_add46337_failAssert0() throws Exception {
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
                    org.junit.Assert.fail("testPreviousElementSiblings_add28255 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36451 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36451_failAssert0_add46337 should have thrown IndexOutOfBoundsException");
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
    public void testPreviousElementSiblings_literalMutationString28180_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>b<li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28180 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28250_failAssert0_add36296_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28250 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28250_failAssert0_add36296 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28171_failAssert0_literalMutationNumber34182_failAssert0() throws Exception {
        try {
            {
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
                    Element element3 = elementSiblings3.get(1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0_literalMutationNumber34182 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28255_failAssert0_add36451_failAssert0_add46331_failAssert0() throws Exception {
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
                    elementSiblings1.size();
                    Element element2 = doc.getElementById("c");
                    List<Element> elementSiblings2 = element2.previousElementSiblings();
                    elementSiblings2.size();
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28255 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36451 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36451_failAssert0_add46331 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690_failAssert0_add46428_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a<C/li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    doc.getElementById("ul");
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28172_failAssert0_add36690_failAssert0_add46428 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28203_failAssert0_add37794_failAssert0_add46361_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=U\'d\'>d</li>") + "</div>")));
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
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203_failAssert0_add37794 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203_failAssert0_add37794_failAssert0_add46361 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_literalMutationString40266_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d/li>") + " Hello\nthere \u00a0  ")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_literalMutationString40266 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28255_failAssert0_add36443_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28255 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36443 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28208_failAssert0_add37774_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208_failAssert0_add37774 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28250_failAssert0_add36307_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28250 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28250_failAssert0_add36307 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28253_failAssert0_add36258_failAssert0() throws Exception {
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
                elementSiblings2.get(0);
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28253 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28253_failAssert0_add36258 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28165_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<l id=\'ul\'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28165 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28188_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28188 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28200_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28200 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28252_failAssert0_add36438_failAssert0() throws Exception {
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
                    elementSiblings3.get(0);
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28252 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28252_failAssert0_add36438 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28208_failAssert0_literalMutationString34799_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<u id=\'ul\'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208_failAssert0_literalMutationString34799 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842_failAssert0_literalMutationString46115_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + " Hello\nthere \u00a0  ") + "</ul>") + "<div id='div'>") + "") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842_failAssert0_literalMutationString46115 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28253_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28253 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28250_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28250 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28257_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28257 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28255_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28255 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28256_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28256 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\',a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28251_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28251 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28252_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28252 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_literalMutationString40235_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_literalMutationString40235 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28218_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28218 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28217_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28217 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28228_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28228 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29107_failAssert0_add47274_failAssert0() throws Exception {
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
                    elementSiblings2.get(1).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29107 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29107_failAssert0_add47274 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28225_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28225 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28226_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28226 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28216_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28216 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28227_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28227 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28171_failAssert0null38474_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((" Hello\nthere \u00a0  " + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0null38474 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677_failAssert0_add47330_failAssert0() throws Exception {
        try {
            {
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
                    Element element2 = doc.getElementById("c");
                    List<Element> elementSiblings2 = element2.previousElementSiblings();
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28257 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677_failAssert0_add47330 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956_failAssert0_literalMutationString43155_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "") + "<liid=\'d\'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_add28256 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28256_failAssert0_literalMutationString29956_failAssert0_literalMutationString43155 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28244_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28244 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28242_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28242 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0null48560_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
                    Element element = doc.getElementById("b");
                    List<Element> elementSiblings = element.previousElementSiblings();
                    elementSiblings.size();
                    elementSiblings.get(0).id();
                    Element element1 = doc.getElementById("a");
                    List<Element> elementSiblings1 = element1.previousElementSiblings();
                    elementSiblings1.size();
                    Element element2 = doc.getElementById(null);
                    List<Element> elementSiblings2 = element2.previousElementSiblings();
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0null48560 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28243_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28243 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28246_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28246 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28230_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28230 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28245_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28245 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28251_failAssert0_add36183_failAssert0_literalMutationNumber40217_failAssert0() throws Exception {
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
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(0).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        elementSiblings3.get(0);
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28251 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28251_failAssert0_add36183 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28251_failAssert0_add36183_failAssert0_literalMutationNumber40217 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28240_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28240 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28241_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28241 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28255_failAssert0_add36451_failAssert0_literalMutationString39392_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28255 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36451 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28255_failAssert0_add36451_failAssert0_literalMutationString39392 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28253_failAssert0_literalMutationString29171_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0);
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28253 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28253_failAssert0_literalMutationString29171 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938_failAssert0_add46446_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\',a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    doc.getElementById("ul");
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938_failAssert0_add46446 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28198_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28198 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28197_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + ")G`)`8cT!+[qO&") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28197 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28171_failAssert0null38474_failAssert0null48454_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((" Hello\nthere \u00a0  " + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    Element ul = doc.getElementById(null);
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0null38474 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0null38474_failAssert0null48454 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28257_failAssert0_add36383_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28257 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_add36383 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28199_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28199 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28217_failAssert0_add38036_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28217 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28217_failAssert0_add38036 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_add46572_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_add46572 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28247_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28247 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827_failAssert0_literalMutationString45564_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827_failAssert0_literalMutationString45564 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28248_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28248 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28171_failAssert0_add37595_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0);
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0_add37595 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827_failAssert0_add48094_failAssert0() throws Exception {
        try {
            {
                {
                    Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827_failAssert0_add48094 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_add46575_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
                    elementSiblings2.get(1);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28207_failAssert0_add37312_failAssert0_add46575 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28249_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28249 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28174_failAssert0_literalMutationString31608_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("M$&5={yo#.BsLdel{" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_literalMutationString31608 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28251_failAssert0_add36183_failAssert0_add46544_failAssert0() throws Exception {
        try {
            {
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
                    Element element2 = doc.getElementById("c");
                    List<Element> elementSiblings2 = element2.previousElementSiblings();
                    elementSiblings2.size();
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        elementSiblings3.get(0);
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28251 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28251_failAssert0_add36183 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28251_failAssert0_add36183_failAssert0_add46544 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938_failAssert0_literalMutationString39830_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\',a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    doc.getElementById(" Hello\nthere \u00a0  ");
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938_failAssert0_literalMutationString39830 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28190_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28190 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28171_failAssert0null38474_failAssert0_add47017_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((" Hello\nthere \u00a0  " + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                    doc.getElementById("b");
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0null38474 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28171_failAssert0null38474_failAssert0_add47017 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29658_failAssert0() throws Exception {
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
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28257 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29658 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28203_failAssert0_literalMutationString34878_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("Z|_DkdcE#uBq_(Jl&" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=U\'d\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28203_failAssert0_literalMutationString34878 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28194_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<divp id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28194 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0null48293_failAssert0() throws Exception {
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
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0null48293 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0_add36243_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_add36243 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28196_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div i`=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28196 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28192_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</8ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28192 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0null38455_failAssert0() throws Exception {
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
                Element element2 = doc.getElementById(null);
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0null38455 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120_failAssert0_literalMutationString45658_failAssert0() throws Exception {
        try {
            {
                {
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
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120_failAssert0_literalMutationString45658 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28208_failAssert0_add37763_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28208_failAssert0_add37763 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28257_failAssert0_add36390_failAssert0() throws Exception {
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
                ul.previousElementSiblings();
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28257 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_add36390 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28209_failAssert0_add36714_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_add36714 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_add37504_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37504 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28236_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28236 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827_failAssert0_add48111_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dZv>")));
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
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28209_failAssert0_literalMutationNumber30827_failAssert0_add48111 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28238_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28238 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_add47501_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_add47501 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28232_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28232 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28175_failAssert0_literalMutationNumber33949_failAssert0_add47523_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("p5e`osqZ.+Ejjy#PM" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175_failAssert0_literalMutationNumber33949 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175_failAssert0_literalMutationNumber33949_failAssert0_add47523 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28250_failAssert0_literalMutationString29305_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("m,e)H^Oibj`99)[#9" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_add28250 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28250_failAssert0_literalMutationString29305 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0_add36239_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_add36239 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28206_failAssert0_literalMutationNumber31272_failAssert0() throws Exception {
        try {
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
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28206 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28206_failAssert0_literalMutationNumber31272 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842_failAssert0_add48258_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33842_failAssert0_add48258 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0_add46318_failAssert0() throws Exception {
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
                    elementSiblings2.size();
                    elementSiblings2.get(0).id();
                    elementSiblings2.get(1);
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_add37513_failAssert0_add46318 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber28218_failAssert0_add37053_failAssert0() throws Exception {
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
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28218 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber28218_failAssert0_add37053 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28185_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<l id=\'c\'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28185 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677_failAssert0_literalMutationString42858_failAssert0() throws Exception {
        try {
            {
                {
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
                    ul.previousElementSiblings();
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(1);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28257 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28257_failAssert0_literalMutationNumber29677_failAssert0_literalMutationString42858 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33848_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</di[v>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33848 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28175_failAssert0_add37529_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("p5e`osqZ.+Ejjy#PM" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28175_failAssert0_add37529 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33846_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li d=\'d\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28170_failAssert0_literalMutationString33846 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28189_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28189 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120_failAssert0_add48124_failAssert0() throws Exception {
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
                    elementSiblings2.get(1).id();
                    Element ul = doc.getElementById("ul");
                    List<Element> elementSiblings3 = ul.previousElementSiblings();
                    {
                        Element element3 = elementSiblings3.get(0);
                    }
                    org.junit.Assert.fail("testPreviousElementSiblings_add28254 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28254_failAssert0_literalMutationNumber29120_failAssert0_add48124 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28187_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\']>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28187 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_literalMutationString43438_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "FL2=<)( -Juxh3GWb") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28204_failAssert0_literalMutationString34724_failAssert0_literalMutationString43438 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28253_failAssert0_add36255_failAssert0() throws Exception {
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
                elementSiblings2.get(0);
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add28253 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add28253_failAssert0_add36255 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938_failAssert0_add46438_failAssert0() throws Exception {
        try {
            {
                {
                    Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\',a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                    org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174 should have thrown IndexOutOfBoundsException");
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28174_failAssert0_add36938_failAssert0_add46438 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString28201_failAssert0() throws Exception {
        try {
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
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString28201 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add28258_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add28258 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }
}

