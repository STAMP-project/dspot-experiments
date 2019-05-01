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
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'db\'>d</li>") + "</div>")));
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
    public void testNextElementSiblings_add87_failAssert0_literalMutationString5446_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_add87 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add87_failAssert0_literalMutationString5446 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10897_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10897 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_add10894_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + " Hello\nthere \u00a0  ") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString38 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_add10894 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString30_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<=/ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString30 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add102_failAssert0_literalMutationString6589_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'{d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add102_failAssert0_literalMutationString6589 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add93_failAssert0_add12044_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add12044 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add90_failAssert0_literalMutationNumber6721_failAssert0() throws Exception {
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
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add90 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_literalMutationNumber6721 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add87_failAssert0_add11845_failAssert0() throws Exception {
        try {
            {
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
                doc.getElementById("div");
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add87 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add87_failAssert0_add11845 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString47_failAssert0_literalMutationNumber3564_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "y/div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString47 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString47_failAssert0_literalMutationNumber3564 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString33_failAssert0_literalMutationString3036_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "") + "") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString33 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString33_failAssert0_literalMutationString3036 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add97_failAssert0_literalMutationString5869_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_literalMutationString5869 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber66_failAssert0_add11355_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber66 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber66_failAssert0_add11355 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString44_failAssert0_add11195_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + " Hello\nthere \u00a0  ")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString44 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString44_failAssert0_add11195 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add96_failAssert0_add12369_failAssert0() throws Exception {
        try {
            {
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
                    elementSiblings4.get(0);
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add96 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add96_failAssert0_add12369 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString39_failAssert0_literalMutationNumber10253_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'db\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString39 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString39_failAssert0_literalMutationNumber10253 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add95_failAssert0_add12326_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                elementSiblings.get(1);
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add95_failAssert0_add12326 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber75_failAssert0_literalMutationNumber10313_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber75 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber75_failAssert0_literalMutationNumber10313 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber75_failAssert0_add13249_failAssert0() throws Exception {
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
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber75 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber75_failAssert0_add13249 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationNumber76_failAssert0_literalMutationString2081_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber76 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber76_failAssert0_literalMutationString2081 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString46_failAssert0_add12518_failAssert0() throws Exception {
        try {
            {
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
                div.nextElementSiblings();
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString46 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString46_failAssert0_add12518 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString37_failAssert0_add10497_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString37 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString37_failAssert0_add10497 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add89_failAssert0_add12292_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add89_failAssert0_add12292 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add90_failAssert0_add12200_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add90_failAssert0_add12200 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationNumber65_failAssert0_add10650_failAssert0() throws Exception {
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
                List<Element> elementSiblings3 = ul.nextElementSiblings();
                elementSiblings3.size();
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber65 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber65_failAssert0_add10650 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add93_failAssert0_add12046_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add93 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add93_failAssert0_add12046 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString46_failAssert0_literalMutationNumber7756_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings3.get(-1).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString46 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString46_failAssert0_literalMutationNumber7756 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber75_failAssert0_add13264_failAssert0() throws Exception {
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
                elementSiblings3.size();
                elementSiblings3.get(0).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber75 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber75_failAssert0_add13264 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString30_failAssert0_literalMutationString9087_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<=/ul>") + "<iv id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString30 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString30_failAssert0_literalMutationString9087 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString37_failAssert0_literalMutationString894_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<ul>") + "<div id='div'>") + "") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString37_failAssert0_literalMutationString894 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add94_failAssert0_add11814_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add94 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_add11814 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add108_failAssert0_add12257_failAssert0() throws Exception {
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
                doc.getElementById("ul");
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add108_failAssert0_add12257 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add101_failAssert0_add11938_failAssert0() throws Exception {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add101_failAssert0_add11938 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString39_failAssert0_add13232_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'db\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString39 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString39_failAssert0_add13232 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString39_failAssert0_add13235_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'db\'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString39 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString39_failAssert0_add13235 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add98_failAssert0_add11903_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add98_failAssert0_add11903 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2272_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2272 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber76_failAssert0_add10855_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber76 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber76_failAssert0_add10855 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_add11476_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings3.get(-1).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add11476 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add92_failAssert0_literalMutationNumber5652_failAssert0() throws Exception {
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
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add92 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_literalMutationNumber5652 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add98_failAssert0_literalMutationString5707_failAssert0() throws Exception {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add98_failAssert0_literalMutationString5707 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_add11479_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_add11479 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add96_failAssert0_literalMutationString7168_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<il id=\'ul\'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add96_failAssert0_literalMutationString7168 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add99_failAssert0_literalMutationString5952_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add99_failAssert0_literalMutationString5952 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add99_failAssert0_add12003_failAssert0() throws Exception {
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
                div.nextElementSiblings();
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_add99 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add99_failAssert0_add12003 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationNumber4253_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings3.get(-1).id();
                Element div = doc.getElementById("div");
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationNumber4253 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString36_failAssert0_add12525_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<diO id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString36 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString36_failAssert0_add12525 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add88_failAssert0_add12009_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add88_failAssert0_add12009 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString30_failAssert0_add12920_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<=/ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString30 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString30_failAssert0_add12920 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add91_failAssert0_add11782_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add11782 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add92_failAssert0_add11876_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add11876 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add94_failAssert0_literalMutationString5309_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</iv>")));
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
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_literalMutationString5309 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationNumber4286_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0_literalMutationNumber4286 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add92_failAssert0_add11892_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testNextElementSiblings_add92 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_add92_failAssert0_add11892 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_add91_failAssert0_add11776_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("a");
                List<Element> elementSiblings = element.nextElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
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
            org.junit.Assert.fail("testNextElementSiblings_add91_failAssert0_add11776 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationNumber74_failAssert0null13546_failAssert0() throws Exception {
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
                Element div = doc.getElementById(null);
                List<Element> elementSiblings4 = div.nextElementSiblings();
                {
                    Element elementSibling = elementSiblings4.get(0);
                }
                org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber74_failAssert0null13546 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString12_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a<,/li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
    public void testNextElementSiblings_literalMutationString24_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<vli id=\'c\'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString24 should have thrown IndexOutOfBoundsException");
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
    public void testNextElementSiblings_literalMutationNumber84_failAssert0() throws Exception {
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
                Element elementSibling = elementSiblings4.get(-1);
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationNumber84 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add102_failAssert0_add12172_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_add102_failAssert0_add12172 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add97_failAssert0_add11957_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add97_failAssert0_add11957 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString47_failAssert0_add11279_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString47_failAssert0_add11279 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2256_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString38_failAssert0_literalMutationString2256 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_literalMutationString45_failAssert0_add10930_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "Odwpau")));
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
                org.junit.Assert.fail("testNextElementSiblings_literalMutationString45 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testNextElementSiblings_literalMutationString45_failAssert0_add10930 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNextElementSiblings_add94_failAssert0_add11815_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testNextElementSiblings_add94_failAssert0_add11815 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15908_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15908 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15906_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15906 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15907_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15909_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15909_failAssert0_add23951_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909_failAssert0_add23951 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15881_failAssert0_literalMutationString22772_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("x&G:v]aXfi+q<2a)y" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</dv>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15881 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15881_failAssert0_literalMutationString22772 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15880_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + ":1u`3{")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15880 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15862_failAssert0_add25672_failAssert0() throws Exception {
        try {
            {
                Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15862 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15862_failAssert0_add25672 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15926_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15926 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15906_failAssert0_add24149_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15906 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15906_failAssert0_add24149 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15927_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15927 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15928_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15928 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15909_failAssert0_literalMutationString16932_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909_failAssert0_literalMutationString16932 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15906_failAssert0_add24142_failAssert0() throws Exception {
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
                    Element element3 = elementSiblings3.get(1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15906 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15906_failAssert0_add24142 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15860_failAssert0_add25293_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</xul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15860 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15860_failAssert0_add25293 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15857_failAssert0_add25710_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15857 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15857_failAssert0_add25710 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15881_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15881 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15927_failAssert0_add25108_failAssert0() throws Exception {
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
                ul.previousElementSiblings();
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add15927 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add15927_failAssert0_add25108 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15842_failAssert0null26201_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((">|d_|&!vkH10LS+C(" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
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
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15842 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15842_failAssert0null26201 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15842_failAssert0_literalMutationString22634_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((">|d_|&!vkH10LS+C(" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</u>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15842 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15842_failAssert0_literalMutationString22634 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15840_failAssert0_literalMutationString21829_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15840 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15840_failAssert0_literalMutationString21829 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15864_failAssert0_add25585_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0);
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15864 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15864_failAssert0_add25585 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15915_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15915 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15870_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15870 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15895_failAssert0_literalMutationString18671_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15895 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15895_failAssert0_literalMutationString18671 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15917_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15917 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15913_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15913 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15919_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15919 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15911_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15911 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15874_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>dM</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15874 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15888_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15888 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15886_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15886 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15844_failAssert0_add24353_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a</l>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15844 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15844_failAssert0_add24353 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15928_failAssert0_literalMutationString21041_failAssert0() throws Exception {
        try {
            {
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
                    elementSiblings3.get(0);
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add15928 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add15928_failAssert0_literalMutationString21041 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15872_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "pF>,f=ZT;}it3v!,Z") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15872 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15894_failAssert0_literalMutationNumber19521_failAssert0() throws Exception {
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
                Element element2 = doc.getElementById("v");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15894 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15894_failAssert0_literalMutationNumber19521 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15896_failAssert0_add23819_failAssert0() throws Exception {
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
                elementSiblings2.get(-1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15896 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15896_failAssert0_add23819 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15896_failAssert0_literalMutationString16399_failAssert0() throws Exception {
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
                elementSiblings2.get(-1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15896 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15896_failAssert0_literalMutationString16399 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15876_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15876 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15865_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15865 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15867_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div d=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15867 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15909_failAssert0_add23962_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909_failAssert0_add23962 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15841_failAssert0_literalMutationNumber16881_failAssert0() throws Exception {
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
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15841 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15841_failAssert0_literalMutationNumber16881 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15869_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "4ElCx ANuf@m$.") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15869 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15878_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15878 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15859_failAssert0_literalMutationString21887_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + " Hello\nthere \u00a0  ") + "<div id='div'>") + "") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15859 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15859_failAssert0_literalMutationString21887 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15924_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15924 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15862_failAssert0_literalMutationString23373_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15862 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15862_failAssert0_literalMutationString23373 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15899_failAssert0_literalMutationNumber18393_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15899 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15899_failAssert0_literalMutationNumber18393 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15895_failAssert0_add24413_failAssert0() throws Exception {
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
                elementSiblings2.get(1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15895 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15895_failAssert0_add24413 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15922_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15922 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15920_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15920 should have thrown IndexOutOfBoundsException");
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
    public void testPreviousElementSiblings_literalMutationString15858_failAssert0_add24043_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(0);
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15858 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15858_failAssert0_add24043 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15881_failAssert0_add25527_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15881 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15881_failAssert0_add25527 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15902_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15902 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15900_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15900 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15880_failAssert0_add24625_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + ":1u`3{")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15880 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15880_failAssert0_add24625 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15898_failAssert0_add23836_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15898 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15898_failAssert0_add23836 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15840_failAssert0_add25269_failAssert0() throws Exception {
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
                ul.previousElementSiblings();
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15840 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15840_failAssert0_add25269 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15840_failAssert0null26144_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15840 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15840_failAssert0null26144 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15841_failAssert0_add23935_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((" Hello\nthere \u00a0  " + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15841 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15841_failAssert0_add23935 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15908_failAssert0_add24228_failAssert0() throws Exception {
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
                    elementSiblings3.get(0);
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15908 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15908_failAssert0_add24228 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15909_failAssert0_literalMutationString16905_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'&>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15909_failAssert0_literalMutationString16905 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15844_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a</l>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15844 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15859_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15859 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15870_failAssert0_add24166_failAssert0() throws Exception {
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
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                doc.getElementById("ul");
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15870 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15870_failAssert0_add24166 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15858_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15858 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15861_failAssert0_add23802_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<-ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15861 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15861_failAssert0_add23802 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15857_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15857 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15843_failAssert0_add24072_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>,</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15843 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15843_failAssert0_add24072 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15840_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15840 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15928_failAssert0_add25069_failAssert0() throws Exception {
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
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    elementSiblings3.get(0);
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add15928 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add15928_failAssert0_add25069 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15862_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</l>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15862 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15841_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15841 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15926_failAssert0_literalMutationString20273_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<Kul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
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
                org.junit.Assert.fail("testPreviousElementSiblings_add15926 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add15926_failAssert0_literalMutationString20273 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15863_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "LHOLd") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15863 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15864_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15864 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15843_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>,</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15843 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15842_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((">|d_|&!vkH10LS+C(" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15842 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15896_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15896 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15896_failAssert0null25791_failAssert0() throws Exception {
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
                elementSiblings2.get(-1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById(null);
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15896 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15896_failAssert0null25791 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15898_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15898 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15897_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15897 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15844_failAssert0_literalMutationString18430_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id=\'a\'>a</l>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</sul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15844 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15844_failAssert0_literalMutationString18430 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15861_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "<-ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15861 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15860_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</xul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15860 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15895_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15895 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15907_failAssert0_literalMutationNumber16272_failAssert0() throws Exception {
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
                elementSiblings2.get(-1).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(-1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907_failAssert0_literalMutationNumber16272 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15863_failAssert0_add24108_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "LHOLd") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15863 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15863_failAssert0_add24108 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15897_failAssert0_add23896_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15897 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15897_failAssert0_add23896 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15916_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15916 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15912_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15912 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15873_failAssert0() throws Exception {
        try {
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
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15873 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15857_failAssert0_literalMutationString23477_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id=\'c\'c</li>") + "</ul>") + "c=g TZ*: >2PvQ") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15857 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15857_failAssert0_literalMutationString23477 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15859_failAssert0_add25287_failAssert0() throws Exception {
        try {
            {
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
                elementSiblings2.get(1);
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15859 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15859_failAssert0_add25287 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15875_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id=\'d\'>d<ili>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15875 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15918_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15918 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15910_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15910 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15887_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15887 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15871_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15871 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15907_failAssert0_literalMutationNumber16283_failAssert0() throws Exception {
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
                    Element element3 = elementSiblings3.get(-2);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907_failAssert0_literalMutationNumber16283 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15907_failAssert0_add23789_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907_failAssert0_add23789 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15908_failAssert0_literalMutationString17876_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15908 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15908_failAssert0_literalMutationString17876 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15915_failAssert0_add25008_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_add15915 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add15915_failAssert0_add25008 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15866_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<dSv id=\'div\'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15866 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15926_failAssert0_add24860_failAssert0() throws Exception {
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
                doc.getElementById("ul");
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_add15926 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_add15926_failAssert0_add24860 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15877_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</xdiv>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15877 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15908_failAssert0_add24215_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15908 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15908_failAssert0_add24215 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15868_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id=\'divl\'>") + "<li id='d'>d</li>") + "</div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15868 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15879_failAssert0() throws Exception {
        try {
            Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "<<div>")));
            Element element = doc.getElementById("b");
            List<Element> elementSiblings = element.previousElementSiblings();
            elementSiblings.size();
            elementSiblings.get(0).id();
            Element element1 = doc.getElementById("a");
            List<Element> elementSiblings1 = element1.previousElementSiblings();
            elementSiblings1.size();
            Element element2 = doc.getElementById("c");
            List<Element> elementSiblings2 = element2.previousElementSiblings();
            elementSiblings2.size();
            elementSiblings2.get(0).id();
            elementSiblings2.get(1).id();
            Element ul = doc.getElementById("ul");
            List<Element> elementSiblings3 = ul.previousElementSiblings();
            {
                Element element3 = elementSiblings3.get(0);
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15879 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15842_failAssert0_add25488_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + ((((((">|d_|&!vkH10LS+C(" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15842 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15842_failAssert0_add25488 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15914_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15914 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15925_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15925 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15923_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15923 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15880_failAssert0_literalMutationString19428_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<li id='c'>c</li>") + "</ul>") + "<div idb=\'div\'>") + "<li id='d'>d</li>") + ":1u`3{")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15880 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15880_failAssert0_literalMutationString19428 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15864_failAssert0_literalMutationString23008_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id='b'>b</li>") + "<l% id=\'c\'>c</li>") + "</ul>") + "") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15864 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15864_failAssert0_literalMutationString23008 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_add15921_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_add15921 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationString15860_failAssert0_literalMutationString21941_failAssert0() throws Exception {
        try {
            {
                Document doc = Jsoup.parse(("<ul id='ul'>" + (((((("<li id='a'>a</li>" + "<li id=\'b\'>b</oli>") + "<li id='c'>c</li>") + "</xul>") + "<div id='div'>") + "<li id='d'>d</li>") + "</div>")));
                Element element = doc.getElementById("b");
                List<Element> elementSiblings = element.previousElementSiblings();
                elementSiblings.size();
                elementSiblings.get(0).id();
                Element element1 = doc.getElementById("a");
                List<Element> elementSiblings1 = element1.previousElementSiblings();
                elementSiblings1.size();
                Element element2 = doc.getElementById("c");
                List<Element> elementSiblings2 = element2.previousElementSiblings();
                elementSiblings2.size();
                elementSiblings2.get(0).id();
                elementSiblings2.get(1).id();
                Element ul = doc.getElementById("ul");
                List<Element> elementSiblings3 = ul.previousElementSiblings();
                {
                    Element element3 = elementSiblings3.get(0);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15860 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationString15860_failAssert0_literalMutationString21941 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15907_failAssert0_add23787_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15907_failAssert0_add23787 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15901_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15901 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreviousElementSiblings_literalMutationNumber15906_failAssert0_literalMutationString17575_failAssert0() throws Exception {
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
                    Element element3 = elementSiblings3.get(1);
                }
                org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15906 should have thrown IndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPreviousElementSiblings_literalMutationNumber15906_failAssert0_literalMutationString17575 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 0", expected.getMessage());
        }
    }
}

