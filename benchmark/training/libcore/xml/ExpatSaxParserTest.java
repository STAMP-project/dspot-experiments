/**
 * Copyright (C) 2007 The Android Open Source Project
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


import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.harmony.xml.ExpatReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.DefaultHandler;


public class ExpatSaxParserTest extends TestCase {
    private static final String SNIPPET = "<dagny dad=\"bob\">hello</dagny>";

    public void testGlobalReferenceTableOverflow() throws Exception {
        // We used to use a JNI global reference per interned string.
        // Framework apps have a limit of 2000 JNI global references per VM.
        StringBuilder xml = new StringBuilder();
        xml.append("<root>");
        for (int i = 0; i < 4000; ++i) {
            xml.append((("<tag" + i) + ">"));
            xml.append((("</tag" + i) + ">"));
        }
        xml.append("</root>");
        ExpatSaxParserTest.parse(xml.toString(), new DefaultHandler());
    }

    public void testExceptions() {
        // From startElement().
        ContentHandler contentHandler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                throw new SAXException();
            }
        };
        try {
            ExpatSaxParserTest.parse(ExpatSaxParserTest.SNIPPET, contentHandler);
            TestCase.fail();
        } catch (SAXException checked) {
            /* expected */
        }
        // From endElement().
        contentHandler = new DefaultHandler() {
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                throw new SAXException();
            }
        };
        try {
            ExpatSaxParserTest.parse(ExpatSaxParserTest.SNIPPET, contentHandler);
            TestCase.fail();
        } catch (SAXException checked) {
            /* expected */
        }
        // From characters().
        contentHandler = new DefaultHandler() {
            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                throw new SAXException();
            }
        };
        try {
            ExpatSaxParserTest.parse(ExpatSaxParserTest.SNIPPET, contentHandler);
            TestCase.fail();
        } catch (SAXException checked) {
            /* expected */
        }
    }

    public void testSax() {
        try {
            // Parse String.
            ExpatSaxParserTest.TestHandler handler = new ExpatSaxParserTest.TestHandler();
            ExpatSaxParserTest.parse(ExpatSaxParserTest.SNIPPET, handler);
            ExpatSaxParserTest.validate(handler);
            // Parse Reader.
            handler = new ExpatSaxParserTest.TestHandler();
            ExpatSaxParserTest.parse(new StringReader(ExpatSaxParserTest.SNIPPET), handler);
            ExpatSaxParserTest.validate(handler);
            // Parse InputStream.
            handler = new ExpatSaxParserTest.TestHandler();
            ExpatSaxParserTest.parse(new ByteArrayInputStream(ExpatSaxParserTest.SNIPPET.getBytes()), ExpatSaxParserTest.Encoding.UTF_8, handler);
            ExpatSaxParserTest.validate(handler);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class TestHandler extends DefaultHandler {
        String startElementName;

        String endElementName;

        StringBuilder text = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            TestCase.assertNull(this.startElementName);
            this.startElementName = localName;
            // Validate attributes.
            TestCase.assertEquals(1, attributes.getLength());
            TestCase.assertEquals("", attributes.getURI(0));
            TestCase.assertEquals("dad", attributes.getLocalName(0));
            TestCase.assertEquals("bob", attributes.getValue(0));
            TestCase.assertEquals(0, attributes.getIndex("", "dad"));
            TestCase.assertEquals("bob", attributes.getValue("", "dad"));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            TestCase.assertNull(this.endElementName);
            this.endElementName = localName;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.text.append(ch, start, length);
        }
    }

    static final String XML = "<one xmlns=\'ns:default\' xmlns:n1=\'ns:1\' a=\'b\'>\n" + ("  <n1:two c=\'d\' n1:e=\'f\' xmlns:n2=\'ns:2\'>text</n1:two>\n" + "</one>");

    public void testNamespaces() {
        try {
            ExpatSaxParserTest.NamespaceHandler handler = new ExpatSaxParserTest.NamespaceHandler();
            ExpatSaxParserTest.parse(ExpatSaxParserTest.XML, handler);
            handler.validate();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    static class NamespaceHandler implements ContentHandler {
        Locator locator;

        boolean documentStarted;

        boolean documentEnded;

        Map<String, String> prefixMappings = new HashMap<String, String>();

        boolean oneStarted;

        boolean twoStarted;

        boolean oneEnded;

        boolean twoEnded;

        public void validate() {
            TestCase.assertTrue(documentEnded);
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        public void startDocument() throws SAXException {
            documentStarted = true;
            TestCase.assertNotNull(locator);
            TestCase.assertEquals(0, prefixMappings.size());
            TestCase.assertFalse(documentEnded);
        }

        public void endDocument() throws SAXException {
            TestCase.assertTrue(documentStarted);
            TestCase.assertTrue(oneEnded);
            TestCase.assertTrue(twoEnded);
            TestCase.assertEquals(0, prefixMappings.size());
            documentEnded = true;
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            prefixMappings.put(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            TestCase.assertNotNull(prefixMappings.remove(prefix));
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if (localName == "one") {
                TestCase.assertEquals(2, prefixMappings.size());
                TestCase.assertEquals(1, locator.getLineNumber());
                TestCase.assertFalse(oneStarted);
                TestCase.assertFalse(twoStarted);
                TestCase.assertFalse(oneEnded);
                TestCase.assertFalse(twoEnded);
                oneStarted = true;
                TestCase.assertSame("ns:default", uri);
                TestCase.assertEquals("one", qName);
                // Check atts.
                TestCase.assertEquals(1, atts.getLength());
                TestCase.assertSame("", atts.getURI(0));
                TestCase.assertSame("a", atts.getLocalName(0));
                TestCase.assertEquals("b", atts.getValue(0));
                TestCase.assertEquals(0, atts.getIndex("", "a"));
                TestCase.assertEquals("b", atts.getValue("", "a"));
                return;
            }
            if (localName == "two") {
                TestCase.assertEquals(3, prefixMappings.size());
                TestCase.assertTrue(oneStarted);
                TestCase.assertFalse(twoStarted);
                TestCase.assertFalse(oneEnded);
                TestCase.assertFalse(twoEnded);
                twoStarted = true;
                TestCase.assertSame("ns:1", uri);
                Assert.assertEquals("n1:two", qName);
                // Check atts.
                TestCase.assertEquals(2, atts.getLength());
                TestCase.assertSame("", atts.getURI(0));
                TestCase.assertSame("c", atts.getLocalName(0));
                TestCase.assertEquals("d", atts.getValue(0));
                TestCase.assertEquals(0, atts.getIndex("", "c"));
                TestCase.assertEquals("d", atts.getValue("", "c"));
                TestCase.assertSame("ns:1", atts.getURI(1));
                TestCase.assertSame("e", atts.getLocalName(1));
                TestCase.assertEquals("f", atts.getValue(1));
                TestCase.assertEquals(1, atts.getIndex("ns:1", "e"));
                TestCase.assertEquals("f", atts.getValue("ns:1", "e"));
                // We shouldn't find these.
                TestCase.assertEquals((-1), atts.getIndex("ns:default", "e"));
                TestCase.assertEquals(null, atts.getValue("ns:default", "e"));
                return;
            }
            TestCase.fail();
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName == "one") {
                TestCase.assertEquals(3, locator.getLineNumber());
                TestCase.assertTrue(oneStarted);
                TestCase.assertTrue(twoStarted);
                TestCase.assertTrue(twoEnded);
                TestCase.assertFalse(oneEnded);
                oneEnded = true;
                TestCase.assertSame("ns:default", uri);
                TestCase.assertEquals("one", qName);
                return;
            }
            if (localName == "two") {
                TestCase.assertTrue(oneStarted);
                TestCase.assertTrue(twoStarted);
                TestCase.assertFalse(twoEnded);
                TestCase.assertFalse(oneEnded);
                twoEnded = true;
                TestCase.assertSame("ns:1", uri);
                TestCase.assertEquals("n1:two", qName);
                return;
            }
            TestCase.fail();
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String s = new String(ch, start, length).trim();
            if (!(s.equals(""))) {
                TestCase.assertTrue(oneStarted);
                TestCase.assertTrue(twoStarted);
                TestCase.assertFalse(oneEnded);
                TestCase.assertFalse(twoEnded);
                TestCase.assertEquals("text", s);
            }
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            TestCase.fail();
        }

        public void processingInstruction(String target, String data) throws SAXException {
            TestCase.fail();
        }

        public void skippedEntity(String name) throws SAXException {
            TestCase.fail();
        }
    }

    public void testDtdDoctype() throws Exception {
        ExpatSaxParserTest.TestDtdHandler handler = runDtdTest("<?xml version=\"1.0\"?><!DOCTYPE foo PUBLIC \'bar\' \'tee\'><a></a>");
        TestCase.assertEquals("foo", handler.name);
        TestCase.assertEquals("bar", handler.publicId);
        TestCase.assertEquals("tee", handler.systemId);
        TestCase.assertTrue(handler.ended);
    }

    public void testDtdUnparsedEntity_system() throws Exception {
        ExpatSaxParserTest.TestDtdHandler handler = runDtdTest("<?xml version=\"1.0\"?><!DOCTYPE foo PUBLIC \'bar\' \'tee\' [ <!ENTITY ent SYSTEM \'blah\' NDATA poop> ]><a></a>");
        TestCase.assertEquals("ent", handler.ueName);
        TestCase.assertEquals(null, handler.uePublicId);
        TestCase.assertEquals("blah", handler.ueSystemId);
        TestCase.assertEquals("poop", handler.ueNotationName);
    }

    public void testDtdUnparsedEntity_public() throws Exception {
        ExpatSaxParserTest.TestDtdHandler handler = runDtdTest("<?xml version=\"1.0\"?><!DOCTYPE foo PUBLIC \'bar\' \'tee\' [ <!ENTITY ent PUBLIC \'a\' \'b\' NDATA poop> ]><a></a>");
        TestCase.assertEquals("ent", handler.ueName);
        TestCase.assertEquals("a", handler.uePublicId);
        TestCase.assertEquals("b", handler.ueSystemId);
        TestCase.assertEquals("poop", handler.ueNotationName);
    }

    public void testDtdNotation_system() throws Exception {
        ExpatSaxParserTest.TestDtdHandler handler = runDtdTest("<?xml version=\"1.0\"?><!DOCTYPE foo PUBLIC \'bar\' \'tee\' [ <!NOTATION sn SYSTEM \'nf2\'> ]><a></a>");
        TestCase.assertEquals("sn", handler.ndName);
        TestCase.assertEquals(null, handler.ndPublicId);
        TestCase.assertEquals("nf2", handler.ndSystemId);
    }

    public void testDtdNotation_public() throws Exception {
        ExpatSaxParserTest.TestDtdHandler handler = runDtdTest("<?xml version=\"1.0\"?><!DOCTYPE foo PUBLIC \'bar\' \'tee\' [ <!NOTATION pn PUBLIC \'nf1\'> ]><a></a>");
        TestCase.assertEquals("pn", handler.ndName);
        TestCase.assertEquals("nf1", handler.ndPublicId);
        TestCase.assertEquals(null, handler.ndSystemId);
    }

    static class TestDtdHandler extends DefaultHandler2 {
        String name;

        String publicId;

        String systemId;

        String ndName;

        String ndPublicId;

        String ndSystemId;

        String ueName;

        String uePublicId;

        String ueSystemId;

        String ueNotationName;

        boolean ended;

        Locator locator;

        @Override
        public void startDTD(String name, String publicId, String systemId) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
        }

        @Override
        public void endDTD() {
            ended = true;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void notationDecl(String name, String publicId, String systemId) {
            this.ndName = name;
            this.ndPublicId = publicId;
            this.ndSystemId = systemId;
        }

        @Override
        public void unparsedEntityDecl(String entityName, String publicId, String systemId, String notationName) {
            this.ueName = entityName;
            this.uePublicId = publicId;
            this.ueSystemId = systemId;
            this.ueNotationName = notationName;
        }
    }

    public void testCdata() throws Exception {
        Reader in = new StringReader("<a><![CDATA[<b></b>]]> <![CDATA[<c></c>]]></a>");
        ExpatReader reader = new ExpatReader();
        ExpatSaxParserTest.TestCdataHandler handler = new ExpatSaxParserTest.TestCdataHandler();
        reader.setContentHandler(handler);
        reader.setLexicalHandler(handler);
        reader.parse(new InputSource(in));
        TestCase.assertEquals(2, handler.startCdata);
        TestCase.assertEquals(2, handler.endCdata);
        TestCase.assertEquals("<b></b> <c></c>", handler.buffer.toString());
    }

    static class TestCdataHandler extends DefaultHandler2 {
        int startCdata;

        int endCdata;

        StringBuffer buffer = new StringBuffer();

        @Override
        public void characters(char[] ch, int start, int length) {
            buffer.append(ch, start, length);
        }

        @Override
        public void startCDATA() throws SAXException {
            (startCdata)++;
        }

        @Override
        public void endCDATA() throws SAXException {
            (endCdata)++;
        }
    }

    public void testProcessingInstructions() throws IOException, SAXException {
        Reader in = new StringReader("<?bob lee?><a></a>");
        ExpatReader reader = new ExpatReader();
        ExpatSaxParserTest.TestProcessingInstrutionHandler handler = new ExpatSaxParserTest.TestProcessingInstrutionHandler();
        reader.setContentHandler(handler);
        reader.parse(new InputSource(in));
        TestCase.assertEquals("bob", handler.target);
        TestCase.assertEquals("lee", handler.data);
    }

    static class TestProcessingInstrutionHandler extends DefaultHandler2 {
        String target;

        String data;

        @Override
        public void processingInstruction(String target, String data) {
            this.target = target;
            this.data = data;
        }
    }

    public void testExternalEntity() throws IOException, SAXException {
        class Handler extends DefaultHandler {
            List<String> elementNames = new ArrayList<String>();

            StringBuilder text = new StringBuilder();

            public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
                if ((publicId.equals("publicA")) && (systemId.equals("systemA"))) {
                    return new InputSource(new StringReader("<a/>"));
                } else
                    if ((publicId.equals("publicB")) && (systemId.equals("systemB"))) {
                        /* Explicitly set the encoding here or else the parser will
                        try to use the parent parser's encoding which is utf-16.
                         */
                        InputSource inputSource = new InputSource(new ByteArrayInputStream("bob".getBytes("utf-8")));
                        inputSource.setEncoding("utf-8");
                        return inputSource;
                    }

                throw new AssertionError();
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                elementNames.add(localName);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                elementNames.add(("/" + localName));
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                text.append(ch, start, length);
            }
        }
        Reader in = new StringReader(("<?xml version=\"1.0\"?>\n" + ((((("<!DOCTYPE foo [\n" + "  <!ENTITY a PUBLIC \'publicA\' \'systemA\'>\n") + "  <!ENTITY b PUBLIC \'publicB\' \'systemB\'>\n") + "]>\n") + "<foo>\n") + "  &a;<b>&b;</b></foo>")));
        ExpatReader reader = new ExpatReader();
        Handler handler = new Handler();
        reader.setContentHandler(handler);
        reader.setEntityResolver(handler);
        reader.parse(new InputSource(in));
        TestCase.assertEquals(Arrays.asList("foo", "a", "/a", "b", "/b", "/foo"), handler.elementNames);
        TestCase.assertEquals("bob", handler.text.toString().trim());
    }

    public void testExternalEntityDownload() throws IOException, SAXException {
        final MockWebServer server = new MockWebServer();
        try {
            // RoboVM note: Modified to call server.shutdown() after test finishes.
            server.enqueue(new MockResponse().setBody("<bar></bar>"));
            server.play();
            class Handler extends DefaultHandler {
                final List<String> elementNames = new ArrayList<String>();

                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws IOException {
                    // The parser should have resolved the systemId.
                    TestCase.assertEquals(server.getUrl("/systemBar").toString(), systemId);
                    return new InputSource(systemId);
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    elementNames.add(localName);
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    elementNames.add(("/" + localName));
                }
            }
            // 'systemBar', the external entity, is relative to 'systemFoo':
            Reader in = new StringReader(("<?xml version=\"1.0\"?>\n" + ((("<!DOCTYPE foo [\n" + "  <!ENTITY bar SYSTEM \'systemBar\'>\n") + "]>\n") + "<foo>&bar;</foo>")));
            ExpatReader reader = new ExpatReader();
            Handler handler = new Handler();
            reader.setContentHandler(handler);
            reader.setEntityResolver(handler);
            InputSource source = new InputSource(in);
            source.setSystemId(server.getUrl("/systemFoo").toString());
            reader.parse(source);
            TestCase.assertEquals(Arrays.asList("foo", "bar", "/bar", "/foo"), handler.elementNames);
        } finally {
            server.shutdown();
        }
    }

    /**
     * Supported character encodings.
     */
    private enum Encoding {

        US_ASCII("US-ASCII"),
        UTF_8("UTF-8"),
        UTF_16("UTF-16"),
        ISO_8859_1("ISO-8859-1");
        final String expatName;

        Encoding(String expatName) {
            this.expatName = expatName;
        }
    }
}

