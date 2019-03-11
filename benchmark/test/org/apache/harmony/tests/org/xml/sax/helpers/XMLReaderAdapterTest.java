/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.org.xml.sax.helpers;


import java.io.IOException;
import java.util.Locale;
import junit.framework.TestCase;
import org.apache.harmony.tests.org.xml.sax.support.MethodLogger;
import org.apache.harmony.tests.org.xml.sax.support.MockHandler;
import org.apache.harmony.tests.org.xml.sax.support.MockReader;
import org.apache.harmony.tests.org.xml.sax.support.MockResolver;
import org.xml.sax.AttributeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderAdapter;


@SuppressWarnings("deprecation")
public class XMLReaderAdapterTest extends TestCase {
    // Note: In many cases we can only test that delegation works
    // properly. The rest is outside the scope of the specification.
    private MethodLogger logger = new MethodLogger();

    private MockHandler handler = new MockHandler(logger);

    private XMLReader reader = new MockReader(logger);

    private XMLReaderAdapter adapter = new XMLReaderAdapter(reader);

    public void testXMLReaderAdapter() {
        System.setProperty("org.xml.sax.driver", "org.apache.harmony.tests.org.xml.sax.support.DoNothingXMLReader");
        try {
            new XMLReaderAdapter();
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testXMLReaderAdapterXMLReader() {
        // Ordinary case
        @SuppressWarnings("unused")
        XMLReaderAdapter adapter = new XMLReaderAdapter(reader);
        // Null case
        try {
            adapter = new XMLReaderAdapter(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testSetLocale() {
        // SAX RI does not support this, hence always expect exception
        try {
            adapter.setLocale(Locale.getDefault());
            TestCase.fail("SAXException expected");
        } catch (SAXException e) {
            // Expected
        }
    }

    public void testSetEntityResolver() {
        EntityResolver resolver = new MockResolver();
        // Ordinary case
        adapter.setEntityResolver(resolver);
        TestCase.assertEquals(resolver, reader.getEntityResolver());
        // null case
        adapter.setEntityResolver(null);
        TestCase.assertEquals(null, reader.getEntityResolver());
    }

    public void testSetDTDHandler() {
        // Ordinary case
        TestCase.assertEquals(handler, reader.getDTDHandler());
        // null case
        adapter.setDTDHandler(null);
        TestCase.assertEquals(null, reader.getDTDHandler());
    }

    public void testSetDocumentHandler() {
        // There is no getter for the DocumentHandler, so we can only test
        // indirectly whether is has been set correctly.
        try {
            adapter.startDocument();
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals("startDocument", logger.getMethod());
        assertEquals(new Object[]{  }, logger.getArgs());
        // null case
        adapter.setDocumentHandler(null);
    }

    public void testSetErrorHandler() {
        // Ordinary case
        TestCase.assertEquals(handler, reader.getErrorHandler());
        // null case
        adapter.setErrorHandler(null);
        TestCase.assertEquals(null, reader.getErrorHandler());
    }

    public void testParseString() {
        try {
            adapter.parse("foo");
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        // The SAX RI creates an InputSource itself and then delegates to the
        // "other" parse method.
        TestCase.assertEquals("parse", logger.getMethod(0));
        TestCase.assertEquals(InputSource.class, logger.getArgs(0)[0].getClass());
    }

    public void testParseInputSource() {
        InputSource source = new InputSource("foo");
        try {
            adapter.parse(source);
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals("parse", logger.getMethod());
        assertEquals(new Object[]{ source }, logger.getArgs());
    }

    public void testSetDocumentLocator() {
        // Ordinary case
        LocatorImpl locator = new LocatorImpl();
        adapter.setDocumentLocator(locator);
        TestCase.assertEquals("setDocumentLocator", logger.getMethod());
        assertEquals(new Object[]{ locator }, logger.getArgs());
        // null case (for the DocumentHandler itself!)
        adapter.setDocumentHandler(null);
        adapter.setDocumentLocator(locator);
    }

    public void testStartDocument() {
        try {
            adapter.startDocument();
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("startDocument", logger.getMethod());
        assertEquals(new Object[]{  }, logger.getArgs());
    }

    public void testEndDocument() {
        try {
            adapter.endDocument();
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("endDocument", logger.getMethod());
        assertEquals(new Object[]{  }, logger.getArgs());
    }

    public void testStartPrefixMapping() {
        adapter.startPrefixMapping("foo", "http://some.uri");
        TestCase.assertEquals(logger.size(), 0);
    }

    public void testEndPrefixMapping() {
        adapter.endPrefixMapping("foo");
        TestCase.assertEquals(logger.size(), 0);
    }

    public void testStartElement() {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("http://some.other.uri", "gabba", "gabba:hey", "int", "42");
        try {
            adapter.startElement("http://some.uri", "bar", "foo:bar", atts);
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("startElement", logger.getMethod());
        TestCase.assertEquals("foo:bar", logger.getArgs()[0]);
        TestCase.assertEquals("gabba:hey", ((AttributeList) (logger.getArgs()[1])).getName(0));
    }

    public void testEndElement() {
        try {
            adapter.endElement("http://some.uri", "bar", "foo:bar");
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("endElement", logger.getMethod());
        assertEquals(new Object[]{ "foo:bar" }, logger.getArgs());
    }

    public void testCharacters() {
        char[] ch = "Android".toCharArray();
        try {
            adapter.characters(ch, 2, 5);
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("characters", logger.getMethod());
        assertEquals(new Object[]{ ch, 2, 5 }, logger.getArgs());
    }

    public void testIgnorableWhitespace() {
        char[] ch = "     ".toCharArray();
        try {
            adapter.ignorableWhitespace(ch, 0, 5);
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("ignorableWhitespace", logger.getMethod());
        assertEquals(new Object[]{ ch, 0, 5 }, logger.getArgs());
    }

    public void testProcessingInstruction() {
        try {
            adapter.processingInstruction("foo", "bar");
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("processingInstruction", logger.getMethod());
        assertEquals(new Object[]{ "foo", "bar" }, logger.getArgs());
    }

    public void testSkippedEntity() {
        try {
            adapter.skippedEntity("foo");
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals(logger.size(), 0);
    }
}

