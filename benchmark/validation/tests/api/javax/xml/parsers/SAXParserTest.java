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
package tests.api.javax.xml.parsers;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.framework.TestCase;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import tests.api.org.xml.sax.support.BrokenInputStream;
import tests.api.org.xml.sax.support.MethodLogger;
import tests.api.org.xml.sax.support.MockHandler;


@SuppressWarnings("deprecation")
public class SAXParserTest extends TestCase {
    private class MockSAXParser extends SAXParser {
        public MockSAXParser() {
            super();
        }

        /* @see javax.xml.parsers.SAXParser#getParser() */
        @Override
        public Parser getParser() throws SAXException {
            // it is a fake
            return null;
        }

        /* @see javax.xml.parsers.SAXParser#getProperty(java.lang.String) */
        @Override
        public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            // it is a fake
            return null;
        }

        /* @see javax.xml.parsers.SAXParser#getXMLReader() */
        @Override
        public XMLReader getXMLReader() throws SAXException {
            // it is a fake
            return null;
        }

        /* @see javax.xml.parsers.SAXParser#isNamespaceAware() */
        @Override
        public boolean isNamespaceAware() {
            // it is a fake
            return false;
        }

        /* @see javax.xml.parsers.SAXParser#isValidating() */
        @Override
        public boolean isValidating() {
            // it is a fake
            return false;
        }

        /* @see javax.xml.parsers.SAXParser#setProperty(java.lang.String,
        java.lang.Object)
         */
        @Override
        public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
            // it is a fake
        }
    }

    private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";

    SAXParserFactory spf;

    SAXParser parser;

    static HashMap<String, String> ns;

    static Vector<String> el;

    static HashMap<String, String> attr;

    SAXParserTestSupport sp = new SAXParserTestSupport();

    File[] list_wf;

    File[] list_nwf;

    File[] list_out_dh;

    File[] list_out_hb;

    boolean validating = false;

    // public static void main(String[] args) throws Exception {
    // SAXParserTest st = new SAXParserTest();
    // st.setUp();
    // st.generateDataFromReferenceImpl();
    // 
    // }
    // 
    // private void generateDataFromReferenceImpl() {
    // try {
    // for(int i = 0; i < list_wf.length; i++) {
    // MyDefaultHandler dh = new MyDefaultHandler();
    // InputStream is = new FileInputStream(list_wf[i]);
    // parser.parse(is, dh, ParsingSupport.XML_SYSTEM_ID);
    // HashMap refHm = dh.createData();
    // 
    // StringBuilder sb = new StringBuilder();
    // for (int j = 0; j < ParsingSupport.KEYS.length; j++) {
    // String key = ParsingSupport.KEYS[j];
    // sb.append(refHm.get(key)).append(
    // ParsingSupport.SEPARATOR_DATA);
    // }
    // FileWriter fw = new FileWriter("/tmp/build_dh"+i+".out");
    // fw.append(sb.toString());
    // fw.close();
    // }
    // 
    // for(int i = 0; i < list_nwf.length; i++) {
    // MyHandler hb = new MyHandler();
    // InputStream is = new FileInputStream(list_wf[i]);
    // parser.parse(is, hb, ParsingSupport.XML_SYSTEM_ID);
    // HashMap refHm = hb.createData();
    // 
    // StringBuilder sb = new StringBuilder();
    // for (int j = 0; j < ParsingSupport.KEYS.length; j++) {
    // String key = ParsingSupport.KEYS[j];
    // sb.append(refHm.get(key)).append(
    // ParsingSupport.SEPARATOR_DATA);
    // }
    // FileWriter fw = new FileWriter("/tmp/build_hb"+i+".out");
    // fw.append(sb.toString());
    // fw.close();
    // }
    // 
    // 
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    public void testSAXParser() {
        try {
            new SAXParserTest.MockSAXParser();
        } catch (Exception e) {
            TestCase.fail(("unexpected exception " + (e.toString())));
        }
    }

    /**
     * javax.xml.parser.SAXParser#getSchema().
     * TODO getSchema() IS NOT SUPPORTED
     */
    /* public void test_getSchema() {
    assertNull(parser.getSchema());
    SchemaFactory sf =
    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
    Schema schema = sf.newSchema();
    spf.setSchema(schema);
    assertNotNull(spf.newSAXParser().getSchema());
    } catch (ParserConfigurationException pce) {
    fail("Unexpected ParserConfigurationException " + pce.toString());
    } catch (SAXException sax) {
    fail("Unexpected SAXException " + sax.toString());
    }
    }
     */
    public void testIsNamespaceAware() {
        try {
            spf.setNamespaceAware(true);
            TestCase.assertTrue(spf.newSAXParser().isNamespaceAware());
            spf.setNamespaceAware(false);
            TestCase.assertFalse(spf.newSAXParser().isNamespaceAware());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testIsValidating() {
        try {
            spf.setValidating(false);
            TestCase.assertFalse(spf.newSAXParser().isValidating());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testIsXIncludeAware() {
        try {
            spf.setXIncludeAware(false);
            TestCase.assertFalse(spf.newSAXParser().isXIncludeAware());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    /**
     *
     *
     * @unknown javax.xml.parsers.SAXParser#parse(java.io.File,
    org.xml.sax.helpers.DefaultHandler)
     */
    public void test_parseLjava_io_FileLorg_xml_sax_helpers_DefaultHandler() throws Exception {
        for (int i = 0; i < (list_wf.length); i++) {
            HashMap<String, String> hm = new SAXParserTestSupport().readFile(list_out_dh[i].getPath());
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            parser.parse(list_wf[i], dh);
            TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
        }
        for (int i = 0; i < (list_nwf.length); i++) {
            try {
                SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
                parser.parse(list_nwf[i], dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException se) {
                // expected
            }
        }
        try {
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            parser.parse(((File) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            parser.parse(list_wf[0], ((DefaultHandler) (null)));
        } catch (IllegalArgumentException iae) {
            TestCase.fail("java.lang.IllegalArgumentException is thrown");
        }
    }

    public void testParseFileHandlerBase() {
        for (int i = 0; i < (list_wf.length); i++) {
            try {
                HashMap<String, String> hm = sp.readFile(list_out_hb[i].getPath());
                SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
                parser.parse(list_wf[i], dh);
                TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
            } catch (IOException ioe) {
                TestCase.fail(("Unexpected IOException " + (ioe.toString())));
            } catch (SAXException sax) {
                TestCase.fail(("Unexpected SAXException " + (sax.toString())));
            }
        }
        for (int i = 0; i < (list_nwf.length); i++) {
            try {
                SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
                parser.parse(list_nwf[i], dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException se) {
                // expected
            } catch (FileNotFoundException fne) {
                TestCase.fail(("Unexpected FileNotFoundException " + (fne.toString())));
            } catch (IOException ioe) {
                TestCase.fail(("Unexpected IOException " + (ioe.toString())));
            }
        }
        try {
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            parser.parse(((File) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        } catch (IOException ioe) {
            TestCase.fail(("Unexpected IOException " + (ioe.toString())));
        } catch (SAXException sax) {
            TestCase.fail(("Unexpected SAXException " + (sax.toString())));
        }
        try {
            parser.parse(list_wf[0], ((HandlerBase) (null)));
        } catch (IllegalArgumentException iae) {
            TestCase.fail("java.lang.IllegalArgumentException is thrown");
        } catch (FileNotFoundException fne) {
            TestCase.fail(("Unexpected FileNotFoundException " + (fne.toString())));
        } catch (IOException ioe) {
            TestCase.fail(("Unexpected IOException " + (ioe.toString())));
        } catch (SAXException sax) {
            TestCase.fail(("Unexpected SAXException " + (sax.toString())));
        }
    }

    /**
     *
     *
     * @unknown javax.xml.parsers.SAXParser#parse(org.xml.sax.InputSource,
    org.xml.sax.helpers.DefaultHandler)
     */
    public void test_parseLorg_xml_sax_InputSourceLorg_xml_sax_helpers_DefaultHandler() throws Exception {
        for (int i = 0; i < (list_wf.length); i++) {
            HashMap<String, String> hm = new SAXParserTestSupport().readFile(list_out_dh[i].getPath());
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            InputSource is = new InputSource(new FileInputStream(list_wf[i]));
            parser.parse(is, dh);
            TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
        }
        for (File file : list_nwf) {
            try {
                SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
                InputSource is = new InputSource(new FileInputStream(file));
                parser.parse(is, dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException expected) {
            }
        }
        try {
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            parser.parse(((InputSource) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException expected) {
        }
        InputSource is = new InputSource(new FileInputStream(list_wf[0]));
        parser.parse(is, ((DefaultHandler) (null)));
        InputStream in = null;
        try {
            in = new BrokenInputStream(new FileInputStream(list_wf[0]), 10);
            is = new InputSource(in);
            parser.parse(is, ((DefaultHandler) (null)));
            TestCase.fail("IOException expected");
        } catch (IOException expected) {
        } finally {
            in.close();
        }
    }

    public void testParseInputSourceHandlerBase() throws Exception {
        for (int i = 0; i < (list_wf.length); i++) {
            HashMap<String, String> hm = sp.readFile(list_out_hb[i].getPath());
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            InputSource is = new InputSource(new FileInputStream(list_wf[i]));
            parser.parse(is, dh);
            TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
        }
        for (File file : list_nwf) {
            try {
                SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
                InputSource is = new InputSource(new FileInputStream(file));
                parser.parse(is, dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException expected) {
            }
        }
        try {
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            parser.parse(((InputSource) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException expected) {
        }
        InputSource is = new InputSource(new FileInputStream(list_wf[0]));
        parser.parse(is, ((HandlerBase) (null)));
        // Reader case
        is = new InputSource(new InputStreamReader(new FileInputStream(list_wf[0])));
        parser.parse(is, ((HandlerBase) (null)));
        // SystemID case
        is = new InputSource(list_wf[0].toURI().toString());
        parser.parse(is, ((HandlerBase) (null)));
        // Inject IOException
        InputStream in = null;
        try {
            in = new BrokenInputStream(new FileInputStream(list_wf[0]), 10);
            parser.parse(in, ((HandlerBase) (null)), SAXParserTestSupport.XML_SYSTEM_ID);
            TestCase.fail("IOException expected");
        } catch (IOException expected) {
        } finally {
            in.close();
        }
    }

    /**
     *
     *
     * @unknown javax.xml.parsers.SAXParser#parse(java.io.InputStream,
    org.xml.sax.helpers.DefaultHandler)
     */
    public void test_parseLjava_io_InputStreamLorg_xml_sax_helpers_DefaultHandler() throws Exception {
        for (int i = 0; i < (list_wf.length); i++) {
            HashMap<String, String> hm = new SAXParserTestSupport().readFile(list_out_dh[i].getPath());
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            InputStream is = new FileInputStream(list_wf[i]);
            parser.parse(is, dh);
            TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
        }
        for (int i = 0; i < (list_nwf.length); i++) {
            try {
                SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
                InputStream is = new FileInputStream(list_nwf[i]);
                parser.parse(is, dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException se) {
                // expected
            }
        }
        try {
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            parser.parse(((InputStream) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            InputStream is = new FileInputStream(list_wf[0]);
            parser.parse(is, ((DefaultHandler) (null)));
        } catch (IllegalArgumentException iae) {
            TestCase.fail("java.lang.IllegalArgumentException is thrown");
        }
    }

    public void testParseInputStreamHandlerBase() throws Exception {
        for (int i = 0; i < (list_wf.length); i++) {
            HashMap<String, String> hm = sp.readFile(list_out_hb[i].getPath());
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            InputStream is = new FileInputStream(list_wf[i]);
            parser.parse(is, dh);
            TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
        }
        for (File file : list_nwf) {
            try {
                SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
                InputStream is = new FileInputStream(file);
                parser.parse(is, dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException expected) {
            }
        }
        try {
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            parser.parse(((InputStream) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException expected) {
        }
        InputStream is = new FileInputStream(list_wf[0]);
        parser.parse(is, ((HandlerBase) (null)));
        // Inject IOException
        try {
            is = new BrokenInputStream(new FileInputStream(list_wf[0]), 10);
            parser.parse(is, ((HandlerBase) (null)));
            TestCase.fail("IOException expected");
        } catch (IOException e) {
            // Expected
        } finally {
            is.close();
        }
    }

    public void testParseInputStreamHandlerBaseString() throws Exception {
        for (int i = 0; i < (list_wf.length); i++) {
            HashMap<String, String> hm = sp.readFile(list_out_hb[i].getPath());
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            InputStream is = new FileInputStream(list_wf[i]);
            parser.parse(is, dh, SAXParserTestSupport.XML_SYSTEM_ID);
            TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
        }
        for (File file : list_nwf) {
            try {
                SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
                InputStream is = new FileInputStream(file);
                parser.parse(is, dh, SAXParserTestSupport.XML_SYSTEM_ID);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException expected) {
            }
        }
        try {
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            parser.parse(null, dh, SAXParserTestSupport.XML_SYSTEM_ID);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException expected) {
        }
        InputStream is = new FileInputStream(list_wf[0]);
        parser.parse(is, ((HandlerBase) (null)), SAXParserTestSupport.XML_SYSTEM_ID);
        // Inject IOException
        try {
            is = new BrokenInputStream(new FileInputStream(list_wf[0]), 10);
            parser.parse(is, ((HandlerBase) (null)), SAXParserTestSupport.XML_SYSTEM_ID);
            TestCase.fail("IOException expected");
        } catch (IOException expected) {
        } finally {
            is.close();
        }
    }

    /**
     *
     *
     * @unknown javax.xml.parsers.SAXParser#parse(java.lang.String,
    org.xml.sax.helpers.DefaultHandler)
     */
    public void test_parseLjava_lang_StringLorg_xml_sax_helpers_DefaultHandler() throws Exception {
        for (int i = 0; i < (list_wf.length); i++) {
            HashMap<String, String> hm = new SAXParserTestSupport().readFile(list_out_dh[i].getPath());
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            parser.parse(list_wf[i].toURI().toString(), dh);
            TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
        }
        for (int i = 0; i < (list_nwf.length); i++) {
            try {
                SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
                parser.parse(list_nwf[i].toURI().toString(), dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException se) {
                // expected
            }
        }
        try {
            SAXParserTestSupport.MyDefaultHandler dh = new SAXParserTestSupport.MyDefaultHandler();
            parser.parse(((String) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            parser.parse(list_wf[0].toURI().toString(), ((DefaultHandler) (null)));
        } catch (IllegalArgumentException iae) {
            TestCase.fail("java.lang.IllegalArgumentException is thrown");
        }
    }

    public void testParseStringHandlerBase() {
        for (int i = 0; i < (list_wf.length); i++) {
            try {
                HashMap<String, String> hm = sp.readFile(list_out_hb[i].getPath());
                SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
                parser.parse(list_wf[i].toURI().toString(), dh);
                TestCase.assertTrue(SAXParserTestSupport.equalsMaps(hm, dh.createData()));
            } catch (IOException ioe) {
                TestCase.fail(("Unexpected IOException " + (ioe.toString())));
            } catch (SAXException sax) {
                TestCase.fail(("Unexpected SAXException " + (sax.toString())));
            }
        }
        for (int i = 0; i < (list_nwf.length); i++) {
            try {
                SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
                parser.parse(list_nwf[i].toURI().toString(), dh);
                TestCase.fail("SAXException is not thrown");
            } catch (SAXException se) {
                // expected
            } catch (FileNotFoundException fne) {
                TestCase.fail(("Unexpected FileNotFoundException " + (fne.toString())));
            } catch (IOException ioe) {
                TestCase.fail(("Unexpected IOException " + (ioe.toString())));
            }
        }
        try {
            SAXParserTestSupport.MyHandler dh = new SAXParserTestSupport.MyHandler();
            parser.parse(((String) (null)), dh);
            TestCase.fail("java.lang.IllegalArgumentException is not thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        } catch (IOException ioe) {
            TestCase.fail(("Unexpected IOException " + (ioe.toString())));
        } catch (SAXException sax) {
            TestCase.fail(("Unexpected SAXException " + (sax.toString())));
        }
        try {
            parser.parse(list_wf[0].toURI().toString(), ((HandlerBase) (null)));
        } catch (IllegalArgumentException iae) {
            TestCase.fail("java.lang.IllegalArgumentException is thrown");
        } catch (FileNotFoundException fne) {
            TestCase.fail(("Unexpected FileNotFoundException " + (fne.toString())));
        } catch (IOException ioe) {
            TestCase.fail(("Unexpected IOException " + (ioe.toString())));
        } catch (SAXException sax) {
            TestCase.fail(("Unexpected SAXException " + (sax.toString())));
        }
    }

    public void testReset() {
        try {
            spf = SAXParserFactory.newInstance();
            parser = spf.newSAXParser();
            parser.setProperty(SAXParserTest.LEXICAL_HANDLER_PROPERTY, new MockHandler(new MethodLogger()));
            parser.reset();
            TestCase.assertEquals(null, parser.getProperty(SAXParserTest.LEXICAL_HANDLER_PROPERTY));
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testGetParser() {
        spf = SAXParserFactory.newInstance();
        try {
            Parser parser = spf.newSAXParser().getParser();
            TestCase.assertNotNull(parser);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testGetReader() {
        spf = SAXParserFactory.newInstance();
        try {
            XMLReader reader = spf.newSAXParser().getXMLReader();
            TestCase.assertNotNull(reader);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void testSetGetProperty() {
        // Ordinary case
        String validName = "http://xml.org/sax/properties/lexical-handler";
        LexicalHandler validValue = new MockHandler(new MethodLogger());
        try {
            SAXParser parser = spf.newSAXParser();
            parser.setProperty(validName, validValue);
            TestCase.assertEquals(validValue, parser.getProperty(validName));
            parser.setProperty(validName, null);
            TestCase.assertEquals(null, parser.getProperty(validName));
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        // Unsupported property
        try {
            SAXParser parser = spf.newSAXParser();
            parser.setProperty("foo", "bar");
            TestCase.fail("SAXNotRecognizedException expected");
        } catch (SAXNotRecognizedException e) {
            // Expected
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        try {
            SAXParser parser = spf.newSAXParser();
            parser.getProperty("foo");
            TestCase.fail("SAXNotRecognizedException expected");
        } catch (SAXNotRecognizedException e) {
            // Expected
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        // No name case
        try {
            SAXParser parser = spf.newSAXParser();
            parser.setProperty(null, "bar");
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        try {
            SAXParser parser = spf.newSAXParser();
            parser.getProperty(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }
}

