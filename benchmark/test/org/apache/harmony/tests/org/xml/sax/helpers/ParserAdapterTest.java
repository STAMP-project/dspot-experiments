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
import junit.framework.TestCase;
import org.apache.harmony.tests.org.xml.sax.support.MethodLogger;
import org.apache.harmony.tests.org.xml.sax.support.MockHandler;
import org.apache.harmony.tests.org.xml.sax.support.MockParser;
import org.apache.harmony.tests.org.xml.sax.support.MockResolver;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.ParserAdapter;


@SuppressWarnings("deprecation")
public class ParserAdapterTest extends TestCase {
    // Note: In many cases we can only test that delegation works
    // properly. The rest is outside the scope of the specification.
    private static final String FEATURES = "http://xml.org/sax/features/";

    private static final String NAMESPACES = (ParserAdapterTest.FEATURES) + "namespaces";

    private static final String NAMESPACE_PREFIXES = (ParserAdapterTest.FEATURES) + "namespace-prefixes";

    private static final String XMLNS_URIs = (ParserAdapterTest.FEATURES) + "xmlns-uris";

    private MethodLogger logger = new MethodLogger();

    private MockHandler handler = new MockHandler(logger);

    private Parser parser = new MockParser(logger);

    private ParserAdapter adapter = new ParserAdapter(parser);

    public void testParserAdapter() {
        System.setProperty("org.xml.sax.parser", "org.apache.harmony.tests.org.xml.sax.support.DoNothingParser");
        try {
            new ParserAdapter();
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testParserAdapterParser() {
        // Ordinary case
        @SuppressWarnings("unused")
        ParserAdapter adapter = new ParserAdapter(parser);
        // Null case
        try {
            adapter = new ParserAdapter(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testGetSetFeature() {
        String[] features = new String[]{ ParserAdapterTest.NAMESPACES, ParserAdapterTest.NAMESPACE_PREFIXES, ParserAdapterTest.XMLNS_URIs };
        for (String s : features) {
            try {
                adapter.setFeature(s, true);
                TestCase.assertEquals(true, adapter.getFeature(s));
                adapter.setFeature(s, false);
                TestCase.assertEquals(false, adapter.getFeature(s));
            } catch (SAXException e) {
                throw new RuntimeException("Unexpected exception", e);
            }
        }
        try {
            adapter.setFeature("http://argle.bargle", true);
            TestCase.fail("SAXNotRecognizedException expected");
        } catch (SAXNotRecognizedException e) {
            // Expected
        } catch (SAXNotSupportedException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testGetSetProperty() {
        try {
            adapter.setProperty("http://argle.bargle", ":)");
            TestCase.fail("SAXNotRecognizedException expected");
        } catch (SAXNotRecognizedException e) {
            // Expected
        } catch (SAXNotSupportedException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        try {
            adapter.getProperty("http://argle.bargle");
            TestCase.fail("SAXNotRecognizedException expected");
        } catch (SAXNotRecognizedException e) {
            // Expected
        } catch (SAXNotSupportedException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testGetSetEntityResolver() {
        EntityResolver resolver = new MockResolver();
        adapter.setEntityResolver(resolver);
        TestCase.assertEquals(resolver, adapter.getEntityResolver());
        adapter.setEntityResolver(null);
        TestCase.assertEquals(null, adapter.getEntityResolver());
    }

    public void testGetSetDTDHandler() {
        adapter.setDTDHandler(null);
        TestCase.assertEquals(null, adapter.getDTDHandler());
        adapter.setDTDHandler(handler);
        TestCase.assertEquals(handler, adapter.getDTDHandler());
    }

    public void testGetSetContentHandler() {
        adapter.setContentHandler(null);
        TestCase.assertEquals(null, adapter.getContentHandler());
        adapter.setContentHandler(handler);
        TestCase.assertEquals(handler, adapter.getContentHandler());
    }

    public void testGetSetErrorHandler() {
        adapter.setErrorHandler(null);
        TestCase.assertEquals(null, adapter.getErrorHandler());
        adapter.setErrorHandler(handler);
        TestCase.assertEquals(handler, adapter.getErrorHandler());
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
        TestCase.assertEquals("parse", logger.getMethod());
        TestCase.assertEquals(InputSource.class, logger.getArgs()[0].getClass());
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
        Locator l = new LocatorImpl();
        adapter.setDocumentLocator(l);
        TestCase.assertEquals(logger.size(), 1);
        TestCase.assertEquals("setDocumentLocator", logger.getMethod());
        assertEquals(new Object[]{ l }, logger.getArgs());
        adapter.setDocumentLocator(null);
        TestCase.assertEquals(logger.size(), 2);
        TestCase.assertEquals("setDocumentLocator", logger.getMethod());
        assertEquals(new Object[]{ null }, logger.getArgs());
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

    public void testStartElement() {
        AttributeListImpl atts = new AttributeListImpl();
        atts.addAttribute("john:doe", "int", "42");
        try {
            adapter.startDocument();
            adapter.startElement("foo:bar", atts);
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals("startElement", logger.getMethod());
        TestCase.assertEquals("", logger.getArgs()[0]);
        TestCase.assertEquals("", logger.getArgs()[1]);
        TestCase.assertEquals("foo:bar", logger.getArgs()[2]);
        TestCase.assertEquals("john:doe", ((Attributes) (logger.getArgs()[3])).getQName(0));
    }

    public void testEndElement() {
        AttributeListImpl atts = new AttributeListImpl();
        atts.addAttribute("john:doe", "int", "42");
        try {
            adapter.startDocument();
            adapter.startElement("foo:bar", atts);
            adapter.endElement("foo:bar");
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        TestCase.assertEquals("endElement", logger.getMethod());
        assertEquals(new String[]{ "", "", "foo:bar" }, logger.getArgs());
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
}

