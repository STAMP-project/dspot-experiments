/**
 * The MIT License
 *
 * Copyright (c) 2012, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.util.xstream;


import hudson.util.XStream2;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class XStreamDOMTest {
    private XStream2 xs;

    public static class Foo {
        XStreamDOM bar;

        XStreamDOM zot;
    }

    @Test
    public void testMarshal() throws IOException {
        XStreamDOMTest.Foo foo = createSomeFoo();
        String xml = xs.toXML(foo);
        System.out.println(xml);
        Assert.assertEquals(getTestData1().trim(), xml.trim());
    }

    @Test
    public void testUnmarshal() throws Exception {
        XStreamDOMTest.Foo foo;
        try (InputStream is = XStreamDOMTest.class.getResourceAsStream("XStreamDOMTest.data1.xml")) {
            foo = ((XStreamDOMTest.Foo) (xs.fromXML(is)));
        }
        Assert.assertEquals("test1", foo.bar.getTagName());
        Assert.assertEquals("value", foo.bar.getAttribute("key"));
        Assert.assertEquals("text!", foo.bar.getValue());
    }

    @Test
    public void testWriteToDOM() throws Exception {
        // roundtrip via DOM
        XStreamDOM dom = XStreamDOM.from(xs, createSomeFoo());
        XStreamDOMTest.Foo foo = dom.unmarshal(xs);
        String xml = xs.toXML(foo);
        System.out.println(xml);
        Assert.assertEquals(getTestData1().trim(), xml.trim());
    }

    @Test
    public void testNoChild() {
        String[] in = new String[0];
        XStreamDOM dom = XStreamDOM.from(xs, in);
        System.out.println(xs.toXML(dom));
        String[] out = dom.unmarshal(xs);
        Assert.assertEquals(in.length, out.length);
    }

    @Test
    public void testNameEscape() {
        Object o = new XStreamDOMTest.Name_That_Gets_Escaped();
        XStreamDOM dom = XStreamDOM.from(xs, o);
        System.out.println(xs.toXML(dom));
        Object out = dom.unmarshal(xs);
        Assert.assertEquals(o.getClass(), out.getClass());
    }

    public static class Name_That_Gets_Escaped {}

    public static class DomInMap {
        Map<String, XStreamDOM> values = new HashMap<String, XStreamDOM>();
    }

    @Test
    public void testDomInMap() {
        XStreamDOMTest.DomInMap v = new XStreamDOMTest.DomInMap();
        v.values.put("foo", createSomeFoo().bar);
        String xml = xs.toXML(v);
        Object v2 = xs.fromXML(xml);
        Assert.assertTrue((v2 instanceof XStreamDOMTest.DomInMap));
        assertXStreamDOMEquals(v.values.get("foo"), ((XStreamDOMTest.DomInMap) (v2)).values.get("foo"));
    }

    @Test
    public void readFromInputStream() throws Exception {
        for (String name : new String[]{ "XStreamDOMTest.data1.xml", "XStreamDOMTest.data2.xml" }) {
            String input = getTestData(name);
            XStreamDOM dom = XStreamDOM.from(new StringReader(input));
            StringWriter sw = new StringWriter();
            dom.writeTo(sw);
            Assert.assertEquals(input.trim(), sw.toString().trim());
        }
    }

    /**
     * Regardless of how we read XML into XStreamDOM, XStreamDOM should retain the raw XML infoset,
     * which means escaped names.
     */
    @Test
    public void escapeHandling() throws Exception {
        String input = getTestData("XStreamDOMTest.data3.xml");
        XStreamDOM dom = XStreamDOM.from(new StringReader(input));
        List<XStreamDOM> children = dom.getChildren().get(0).getChildren().get(0).getChildren();
        assertNamesAreEscaped(children);
        XStreamDOMTest.Foo foo = ((XStreamDOMTest.Foo) (xs.fromXML(new StringReader(input))));
        assertNamesAreEscaped(foo.bar.getChildren());
        StringWriter sw = new StringWriter();
        dom.writeTo(sw);
        Assert.assertTrue(sw.toString().contains("bar_-bar"));
        Assert.assertTrue(sw.toString().contains("zot__bar"));
        String s = xs.toXML(foo);
        Assert.assertTrue(s.contains("bar_-bar"));
        Assert.assertTrue(s.contains("zot__bar"));
    }
}

