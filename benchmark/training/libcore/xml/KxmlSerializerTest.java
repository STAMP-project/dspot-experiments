/**
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.xml;


import java.io.IOException;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.kxml2.io.KXmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;
import tests.support.Support_Xml;


public final class KxmlSerializerTest extends TestCase {
    private static final String NAMESPACE = null;

    public void testWhitespaceInAttributeValue() throws Exception {
        StringWriter stringWriter = new StringWriter();
        XmlSerializer serializer = new KXmlSerializer();
        serializer.setOutput(stringWriter);
        serializer.startDocument("UTF-8", null);
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "a");
        serializer.attribute(KxmlSerializerTest.NAMESPACE, "cr", "\r");
        serializer.attribute(KxmlSerializerTest.NAMESPACE, "lf", "\n");
        serializer.attribute(KxmlSerializerTest.NAMESPACE, "tab", "\t");
        serializer.attribute(KxmlSerializerTest.NAMESPACE, "space", " ");
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "a");
        serializer.endDocument();
        assertXmlEquals("<a cr=\"&#13;\" lf=\"&#10;\" tab=\"&#9;\" space=\" \" />", stringWriter.toString());
    }

    public void testWriteDocument() throws Exception {
        StringWriter stringWriter = new StringWriter();
        XmlSerializer serializer = new KXmlSerializer();
        serializer.setOutput(stringWriter);
        serializer.startDocument("UTF-8", null);
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "foo");
        serializer.attribute(KxmlSerializerTest.NAMESPACE, "quux", "abc");
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "bar");
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "bar");
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "baz");
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "baz");
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "foo");
        serializer.endDocument();
        assertXmlEquals("<foo quux=\"abc\"><bar /><baz /></foo>", stringWriter.toString());
    }

    // http://code.google.com/p/android/issues/detail?id=21250
    public void testWriteSpecialCharactersInText() throws Exception {
        StringWriter stringWriter = new StringWriter();
        XmlSerializer serializer = new KXmlSerializer();
        serializer.setOutput(stringWriter);
        serializer.startDocument("UTF-8", null);
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "foo");
        serializer.text("5\'8\", 5 < 6 & 7 > 3!");
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "foo");
        serializer.endDocument();
        assertXmlEquals("<foo>5\'8\", 5 &lt; 6 &amp; 7 &gt; 3!</foo>", stringWriter.toString());
    }

    public void testInvalidCharactersInText() throws IOException {
        XmlSerializer serializer = KxmlSerializerTest.newSerializer();
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "root");
        for (int ch = 0; ch <= 65535; ++ch) {
            final String s = Character.toString(((char) (ch)));
            if (KxmlSerializerTest.isValidXmlCodePoint(ch)) {
                serializer.text((("a" + s) + "b"));
            } else {
                try {
                    serializer.text((("a" + s) + "b"));
                    TestCase.fail(s);
                } catch (IllegalArgumentException expected) {
                }
            }
        }
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "root");
    }

    public void testInvalidCharactersInAttributeValues() throws IOException {
        XmlSerializer serializer = KxmlSerializerTest.newSerializer();
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "root");
        for (int ch = 0; ch <= 65535; ++ch) {
            final String s = Character.toString(((char) (ch)));
            if (KxmlSerializerTest.isValidXmlCodePoint(ch)) {
                serializer.attribute(KxmlSerializerTest.NAMESPACE, "a", (("a" + s) + "b"));
            } else {
                try {
                    serializer.attribute(KxmlSerializerTest.NAMESPACE, "a", (("a" + s) + "b"));
                    TestCase.fail(s);
                } catch (IllegalArgumentException expected) {
                }
            }
        }
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "root");
    }

    public void testInvalidCharactersInCdataSections() throws IOException {
        XmlSerializer serializer = KxmlSerializerTest.newSerializer();
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "root");
        for (int ch = 0; ch <= 65535; ++ch) {
            final String s = Character.toString(((char) (ch)));
            if (KxmlSerializerTest.isValidXmlCodePoint(ch)) {
                serializer.cdsect((("a" + s) + "b"));
            } else {
                try {
                    serializer.cdsect((("a" + s) + "b"));
                    TestCase.fail(s);
                } catch (IllegalArgumentException expected) {
                }
            }
        }
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "root");
    }

    public void testCdataWithTerminatorInside() throws Exception {
        StringWriter writer = new StringWriter();
        XmlSerializer serializer = new KXmlSerializer();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", null);
        serializer.startTag(KxmlSerializerTest.NAMESPACE, "p");
        serializer.cdsect("a]]>b");
        serializer.endTag(KxmlSerializerTest.NAMESPACE, "p");
        serializer.endDocument();
        // Adjacent CDATA sections aren't merged, so let's stick them together ourselves...
        Document doc = Support_Xml.domOf(writer.toString());
        NodeList children = doc.getFirstChild().getChildNodes();
        String text = "";
        for (int i = 0; i < (children.getLength()); ++i) {
            text += children.item(i).getNodeValue();
        }
        TestCase.assertEquals("a]]>b", text);
    }
}

