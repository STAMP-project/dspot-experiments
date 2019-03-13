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
package tests.api.javax.xml.parsers;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.framework.TestCase;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;


public class SAXParserFactoryTest extends TestCase {
    SAXParserFactory spf;

    InputStream is1;

    static HashMap<String, String> ns;

    static Vector<String> el;

    static HashMap<String, String> attr;

    /**
     * javax.xml.parsers.SAXParserFactory#getSchema().
     * TBD getSchema() IS NOT SUPPORTED
     */
    /* public void test_getSchema() {
    assertNull(spf.getSchema());
    SchemaFactory sf =
    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
    Schema schema = sf.newSchema();
    spf.setSchema(schema);
    assertNotNull(spf.getSchema());
    } catch (SAXException sax) {
    fail("Unexpected exception " + sax.toString());
    }
    }
     */
    public void test_setIsNamespaceAware() {
        spf.setNamespaceAware(true);
        TestCase.assertTrue(spf.isNamespaceAware());
        spf.setNamespaceAware(false);
        TestCase.assertFalse(spf.isNamespaceAware());
        spf.setNamespaceAware(true);
        TestCase.assertTrue(spf.isNamespaceAware());
    }

    public void test_setIsValidating() {
        spf.setValidating(true);
        TestCase.assertTrue(spf.isValidating());
        spf.setValidating(false);
        TestCase.assertFalse(spf.isValidating());
        spf.setValidating(true);
        TestCase.assertTrue(spf.isValidating());
    }

    public void test_setIsXIncludeAware() {
        spf.setXIncludeAware(true);
        TestCase.assertTrue(spf.isXIncludeAware());
        spf.setXIncludeAware(false);
        TestCase.assertFalse(spf.isXIncludeAware());
    }

    public void test_newSAXParser() {
        // Ordinary case
        try {
            SAXParser sp = spf.newSAXParser();
            TestCase.assertTrue((sp instanceof SAXParser));
            sp.parse(is1, new SAXParserFactoryTest.MyHandler());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        // Exception case
        spf.setValidating(true);
        try {
            SAXParser sp = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            // Expected, since Android doesn't have a validating parser.
        } catch (SAXException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    public void test_setFeatureLjava_lang_StringZ() {
        // We can't verify ParserConfigurationException and
        // SAXNotSupportedException since these are never
        // thrown by Android.
        String[] features = new String[]{ "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation" };
        for (int i = 0; i < (features.length); i++) {
            try {
                spf.setFeature(features[i], true);
                TestCase.assertTrue(spf.getFeature(features[i]));
                spf.setFeature(features[i], false);
                TestCase.assertFalse(spf.getFeature(features[i]));
            } catch (ParserConfigurationException pce) {
                TestCase.fail("ParserConfigurationException is thrown");
            } catch (SAXNotRecognizedException snre) {
                TestCase.fail("SAXNotRecognizedException is thrown");
            } catch (SAXNotSupportedException snse) {
                TestCase.fail("SAXNotSupportedException is thrown");
            }
        }
        try {
            spf.setFeature("", true);
            TestCase.fail("SAXNotRecognizedException is not thrown");
        } catch (ParserConfigurationException pce) {
            TestCase.fail("ParserConfigurationException is thrown");
        } catch (SAXNotRecognizedException snre) {
            // expected
        } catch (SAXNotSupportedException snse) {
            TestCase.fail("SAXNotSupportedException is thrown");
        } catch (NullPointerException npe) {
            TestCase.fail("NullPointerException is thrown");
        }
        try {
            spf.setFeature("http://xml.org/sax/features/unknown-feature", true);
        } catch (ParserConfigurationException pce) {
            TestCase.fail("ParserConfigurationException is thrown");
        } catch (SAXNotRecognizedException snre) {
            TestCase.fail("SAXNotRecognizedException is thrown");
        } catch (SAXNotSupportedException snse) {
            // Acceptable, although this doesn't happen an Android.
        } catch (NullPointerException npe) {
            TestCase.fail("NullPointerException is thrown");
        }
        try {
            spf.setFeature(null, true);
            TestCase.fail("NullPointerException is not thrown");
        } catch (ParserConfigurationException pce) {
            TestCase.fail("ParserConfigurationException is thrown");
        } catch (SAXNotRecognizedException snre) {
            TestCase.fail("SAXNotRecognizedException is thrown");
        } catch (SAXNotSupportedException snse) {
            TestCase.fail("SAXNotSupportedException is thrown");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    public void test_setNamespaceAwareZ() throws Exception {
        SAXParserFactoryTest.MyHandler mh = new SAXParserFactoryTest.MyHandler();
        spf.setNamespaceAware(true);
        InputStream is = getClass().getResourceAsStream("/simple_ns.xml");
        spf.newSAXParser().parse(is, mh);
        is.close();
        spf.setNamespaceAware(false);
        is = getClass().getResourceAsStream("/simple_ns.xml");
        spf.newSAXParser().parse(is, mh);
        is.close();
    }

    /* public void test_setSchemaLjavax_xml_validation_Schema() {
    SchemaFactory sf =
    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
    Schema schema = sf.newSchema();
    spf.setSchema(schema);
    assertNotNull(spf.getSchema());
    } catch (SAXException sax) {
    fail("Unexpected exception " + sax.toString());
    }
    }
     */
    // public void test_setValidatingZ() {
    // MyHandler mh = new MyHandler();
    // InputStream is2 = getClass().getResourceAsStream("/recipe.xml");
    // try {
    // spf.setValidating(true);
    // assertTrue(spf.isValidating());
    // spf.newSAXParser().parse(is2, mh);
    // } catch (org.xml.sax.SAXException se) {
    // fail("SAXException was thrown during parsing");
    // } catch (javax.xml.parsers.ParserConfigurationException pce) {
    // fail("ParserConfigurationException was thrown during parsing");
    // } catch (IOException ioe) {
    // fail("IOException was thrown during parsing");
    // } finally {
    // try {
    // is2.close();
    // } catch(Exception ioee) {}
    // }
    // InputStream is3 = getClass().getResourceAsStream("/recipe1.xml");
    // try {
    // assertTrue(spf.isValidating());
    // spf.newSAXParser().parse(is3, mh);
    // } catch (org.xml.sax.SAXException se) {
    // fail("SAXException was thrown during parsing");
    // } catch (javax.xml.parsers.ParserConfigurationException pce) {
    // fail("ParserConfigurationException was thrown during parsing");
    // } catch (IOException ioe) {
    // fail("IOEXception was thrown during parsing: " + ioe.getMessage());
    // } finally {
    // try {
    // is3.close();
    // } catch(Exception ioee) {}
    // }
    // is2 = getClass().getResourceAsStream("/recipe.xml");
    // try {
    // spf.setValidating(false);
    // assertFalse(spf.isValidating());
    // spf.newSAXParser().parse(is2, mh);
    // } catch (org.xml.sax.SAXException se) {
    // fail("SAXException was thrown during parsing");
    // } catch (javax.xml.parsers.ParserConfigurationException pce) {
    // fail("ParserConfigurationException was thrown during parsing");
    // } catch (IOException ioe) {
    // fail("IOException was thrown during parsing");
    // } finally {
    // try {
    // is2.close();
    // } catch(Exception ioee) {}
    // }
    // is3 = getClass().getResourceAsStream("/recipe1.xml");
    // try {
    // assertFalse(spf.isValidating());
    // spf.newSAXParser().parse(is3, mh);
    // } catch (org.xml.sax.SAXException se) {
    // fail("SAXException was thrown during parsing");
    // } catch (javax.xml.parsers.ParserConfigurationException pce) {
    // fail("ParserConfigurationException was thrown during parsing");
    // } catch (IOException ioe) {
    // fail("IOEXception was thrown during parsing: " + ioe.getMessage());
    // } finally {
    // try {
    // is3.close();
    // } catch(Exception ioee) {}
    // }
    // }
    // public void test_setXIncludeAwareZ() {
    // spf.setXIncludeAware(true);
    // MyHandler mh = new MyHandler();
    // InputStream is = getClass().getResourceAsStream("/simple_ns.xml");
    // try {
    // spf.newSAXParser().parse(is, mh);
    // } catch(javax.xml.parsers.ParserConfigurationException pce) {
    // fail("ParserConfigurationException was thrown during parsing");
    // } catch(org.xml.sax.SAXException se) {
    // fail("SAXException was thrown during parsing");
    // } catch(IOException ioe) {
    // fail("IOException was thrown during parsing");
    // } finally {
    // try {
    // is.close();
    // } catch(Exception ioee) {}
    // }
    // spf.setXIncludeAware(false);
    // is = getClass().getResourceAsStream("/simple_ns.xml");
    // try {
    // is = getClass().getResourceAsStream("/simple_ns.xml");
    // spf.newSAXParser().parse(is, mh);
    // } catch(javax.xml.parsers.ParserConfigurationException pce) {
    // fail("ParserConfigurationException was thrown during parsing");
    // } catch(org.xml.sax.SAXException se) {
    // fail("SAXException was thrown during parsing");
    // } catch(IOException ioe) {
    // fail("IOException was thrown during parsing");
    // } finally {
    // try {
    // is.close();
    // } catch(Exception ioee) {}
    // }
    // is = getClass().getResourceAsStream("/simple_ns.xml");
    // try {
    // spf.setXIncludeAware(true);
    // spf.newSAXParser().parse(is, mh);
    // } catch(javax.xml.parsers.ParserConfigurationException pce) {
    // fail("ParserConfigurationException was thrown during parsing");
    // } catch(org.xml.sax.SAXException se) {
    // fail("SAXException was thrown during parsing");
    // } catch(IOException ioe) {
    // fail("IOException was thrown during parsing");
    // } finally {
    // try {
    // is.close();
    // } catch(Exception ioee) {}
    // }
    // }
    static class MyHandler extends DefaultHandler {
        public void startElement(String uri, String localName, String qName, Attributes atts) {
            SAXParserFactoryTest.el.add(qName);
            if (!(uri.equals("")))
                SAXParserFactoryTest.ns.put(qName, uri);

            for (int i = 0; i < (atts.getLength()); i++) {
                SAXParserFactoryTest.attr.put(atts.getQName(i), atts.getValue(i));
            }
        }
    }

    class MySAXParserFactory extends SAXParserFactory {
        public MySAXParserFactory() {
            super();
        }

        public SAXParser newSAXParser() {
            return null;
        }

        public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        }

        public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
            return true;
        }
    }
}

