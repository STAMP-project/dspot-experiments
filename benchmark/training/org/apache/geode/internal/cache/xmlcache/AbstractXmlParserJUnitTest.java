/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.xmlcache;


import java.util.Stack;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * Unit tests for {@link AbstractXmlParser}.
 *
 * @since GemFire 8.1
 */
public class AbstractXmlParserJUnitTest {
    /**
     * Test method for {@link AbstractXmlParser#setStack(java.util.Stack)}.
     */
    @Test
    public void testSetStack() {
        AbstractXmlParserJUnitTest.MockXmlParser m = new AbstractXmlParserJUnitTest.MockXmlParser();
        Stack<Object> s = new Stack<Object>();
        setStack(s);
        Assert.assertSame(s, m.stack);
    }

    /**
     * Test method for {@link AbstractXmlParser#setDocumentLocator(Locator)}.
     */
    @Test
    public void testSetDocumentLocator() {
        final AbstractXmlParserJUnitTest.MockXmlParser mockXmlParser = new AbstractXmlParserJUnitTest.MockXmlParser();
        final Locator mockLocator = new Locator() {
            @Override
            public String getSystemId() {
                return null;
            }

            @Override
            public String getPublicId() {
                return null;
            }

            @Override
            public int getLineNumber() {
                return 0;
            }

            @Override
            public int getColumnNumber() {
                return 0;
            }
        };
        setDocumentLocator(mockLocator);
        Assert.assertSame(mockLocator, mockXmlParser.documentLocator);
    }

    /**
     * Test method for {@link AbstractXmlParser#startDocument()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testStartDocument() throws SAXException {
        startDocument();
    }

    /**
     * Test method for {@link AbstractXmlParser#endDocument()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testEndDocument() throws SAXException {
        endDocument();
    }

    /**
     * Test method for {@link AbstractXmlParser#startPrefixMapping(String, String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testStartPrefixMapping() throws SAXException {
        startPrefixMapping(null, null);
    }

    /**
     * Test method for {@link AbstractXmlParser#endPrefixMapping(String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testEndPrefixMapping() throws SAXException {
        endPrefixMapping(null);
    }

    /**
     * Test method for {@link AbstractXmlParser#characters(char[], int, int)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCharacters() throws SAXException {
        characters(null, 0, 0);
    }

    /**
     * Test method for {@link AbstractXmlParser#ignorableWhitespace(char[], int, int)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIgnorableWhitespace() throws SAXException {
        ignorableWhitespace(null, 0, 0);
    }

    /**
     * Test method for {@link AbstractXmlParser#processingInstruction(String, String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testProcessingInstruction() throws SAXException {
        processingInstruction(null, null);
    }

    /**
     * Test method for {@link AbstractXmlParser#skippedEntity(String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSkippedEntity() throws SAXException {
        skippedEntity(null);
    }

    private static class MockXmlParser extends AbstractXmlParser {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            throw new IllegalStateException();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            throw new IllegalStateException();
        }

        @Override
        public String getNamespaceUri() {
            throw new IllegalStateException();
        }
    }
}

