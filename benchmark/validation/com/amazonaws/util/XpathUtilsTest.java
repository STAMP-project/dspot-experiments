/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Unit tests for the XpathUtils class.
 *
 * @author fulghum@amazon.com
 */
public class XpathUtilsTest {
    /**
     * Test data for all tests to share
     */
    private static final String DOCUMENT = "<Foo>" + (((((((((((("    <Title>Boo</Title>" + "    <Count Foo='Bar'>1</Count>") + "    <Enabled>true</Enabled>") + "    <Usage>0.0000071759</Usage>") + "    <Since>2008-10-07T11:51:50.000Z</Since>") + "    <Item>A</Item>") + "    <Item>B</Item>") + "    <Item>C</Item>") + "    <Empty></Empty>") + "    <Blob>aGVsbG8gd29ybGQ=</Blob>") + "    <PositiveByte>123</PositiveByte>") + "    <NegativeByte>-99</NegativeByte>") + "</Foo>");

    /**
     * Test XML document with a namespace
     */
    private static final String DOCUMENT_WITH_NAMESPACE = "<?xml version=\"1.0\"?> \n" + ((("<AllocateAddressResponse xmlns=\"http://ec2.amazonaws.com/doc/2009-04-04/\"> \n" + "    <requestId>a074658d-7624-433e-b4e9-9271f6f5264b</requestId> \n") + "    <publicIp>174.129.242.223</publicIp> \n") + "</AllocateAddressResponse> \n");

    @Test
    public void testXmlDocumentWithNamespace() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT_WITH_NAMESPACE);
        XPath xpath = XpathUtils.xpath();
        Node root = XpathUtils.asNode("/", document, xpath);
        Assert.assertNotNull(root);
        Node node = XpathUtils.asNode("//AllocateAddressResponse", document, xpath);
        Assert.assertNotNull(node);
    }

    @Test
    public void testAsString() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Assert.assertEquals("Boo", XpathUtils.asString("Foo/Title", document, xpath));
        Assert.assertEquals("", XpathUtils.asString("Foo/Empty", document, xpath));
        Assert.assertEquals("Bar", XpathUtils.asString("Foo/Count/@Foo", document, xpath));
    }

    @Test
    public void testAsInteger() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Assert.assertEquals(((Integer) (1)), ((Integer) (XpathUtils.asInteger("Foo/Count", document, xpath))));
        Assert.assertEquals(null, XpathUtils.asInteger("Foo/Empty", document, xpath));
    }

    @Test
    public void testAsBoolean() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Assert.assertEquals(true, XpathUtils.asBoolean("Foo/Enabled", document, xpath));
        Assert.assertEquals(null, XpathUtils.asBoolean("Foo/Empty", document, xpath));
    }

    @Test
    public void testAsFloat() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Assert.assertEquals(((Float) (7.1759E-6F)), ((Float) (XpathUtils.asFloat("Foo/Usage", document, xpath))));
        Assert.assertEquals(null, XpathUtils.asFloat("Foo/Empty", document, xpath));
    }

    /**
     * Tests that we can correctly pull a Byte out of an XML document.
     */
    @Test
    public void testAsByte() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Assert.assertEquals(new Byte(((byte) (123))), XpathUtils.asByte("Foo/PositiveByte", document, xpath));
        Assert.assertEquals(new Byte(((byte) (-99))), XpathUtils.asByte("Foo/NegativeByte", document, xpath));
        Assert.assertEquals(null, XpathUtils.asByte("Foo/Empty", document));
    }

    /**
     * Tests that we can correctly parse out a Date from an XML document.
     */
    @Test
    public void testAsDate() throws Exception {
        /* The example date in our test XML document is:
          2008-10-07T11:51:50.000Z

        So we construct that same date and verify that it matches
        what we parsed out of the XML.
         */
        Calendar expectedDate = new GregorianCalendar();
        expectedDate.set(Calendar.YEAR, 2008);
        expectedDate.set(Calendar.MONTH, Calendar.OCTOBER);
        expectedDate.set(Calendar.DAY_OF_MONTH, 7);
        expectedDate.set(Calendar.AM_PM, Calendar.AM);
        expectedDate.set(Calendar.HOUR, 11);
        expectedDate.set(Calendar.MINUTE, 51);
        expectedDate.set(Calendar.SECOND, 50);
        expectedDate.set(Calendar.MILLISECOND, 0);
        expectedDate.setTimeZone(new SimpleTimeZone(0, "UTC"));
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Date date = XpathUtils.asDate("Foo/Since", document, xpath);
        Assert.assertNotNull(date);
        Assert.assertEquals(expectedDate.getTimeInMillis(), date.getTime());
        Assert.assertEquals(null, XpathUtils.asDate("Foo/Empty", document, xpath));
    }

    @Test
    public void testIsEmpty() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Node emptyNode = XpathUtils.asNode("Foo/Fake", document, xpath);
        Node realNode = XpathUtils.asNode("Foo/Count", document, xpath);
        Assert.assertTrue(XpathUtils.isEmpty(emptyNode));
        Assert.assertFalse(XpathUtils.isEmpty(realNode));
    }

    @Test
    public void testAsNode() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Node node = XpathUtils.asNode("Foo/Title", document, xpath);
        Assert.assertNotNull(node);
        Assert.assertEquals("Title", node.getNodeName());
    }

    /**
     * Tests that we return null when a specified expression doesn't
     * evaluate anything (instead of passing that null/empty value to
     * a parser and getting an error in the parser).
     */
    @Test
    public void testMissingNodes() throws Exception {
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        Assert.assertNull(XpathUtils.asDouble("non-existant-node/name", document, xpath));
        Assert.assertNull(XpathUtils.asLong("non-existant-node/name", document, xpath));
        Assert.assertNull(XpathUtils.asInteger("non-existant-node/name", document, xpath));
        Assert.assertNull(XpathUtils.asDate("non-existant-node/name", document, xpath));
        Assert.assertNull(XpathUtils.asFloat("non-existant-node/name", document, xpath));
        Assert.assertNull(XpathUtils.asString("non-existant-node/name", document, xpath));
    }

    /**
     * Tests that {@link XpathUtils#asByteBuffer(String, Node)} correctly base64
     * decodes the XML text data and transforms it into a ByteBuffer.
     */
    @Test
    public void testAsByteBuffer() throws Exception {
        String expectedData = "hello world";
        Document document = XpathUtils.documentFrom(XpathUtilsTest.DOCUMENT);
        XPath xpath = XpathUtils.xpath();
        ByteBuffer byteBuffer = XpathUtils.asByteBuffer("Foo/Blob", document, xpath);
        Assert.assertEquals(expectedData.length(), byteBuffer.limit());
        String data = new String(byteBuffer.array());
        Assert.assertEquals(expectedData, data);
        Assert.assertEquals(null, XpathUtils.asByteBuffer("Foo/Empty", document, xpath));
    }

    @Test
    public void testFromDocumentDoesNotWriteToStderrWhenXmlInvalid() throws IOException, ParserConfigurationException, SAXException {
        PrintStream err = System.err;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            PrintStream err2 = new PrintStream(bytes);
            System.setErr(err2);
            // invalid xml
            XpathUtils.documentFrom("a");
            Assert.fail();
        } catch (SAXParseException e) {
            // ensure nothing written to stderr
            Assert.assertEquals(0, bytes.toByteArray().length);
        } finally {
            System.setErr(err);
        }
    }
}

