/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.xml;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;


public class XMLHandlerUnitTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    /**
     *
     *
     * @see <a href="https://en.wikipedia.org/wiki/Billion_laughs" />
     */
    private static final String MALICIOUS_XML = "<?xml version=\"1.0\"?>\n" + ((((((((((((("<!DOCTYPE lolz [\n" + " <!ENTITY lol \"lol\">\n") + " <!ELEMENT lolz (#PCDATA)>\n") + " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n") + " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n") + " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n") + " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n") + " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n") + " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n") + " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n") + " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n") + " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n") + "]>\n") + "<lolz>&lol9;</lolz>");

    private static final String cr = Const.CR;

    @Test
    public void openTagWithNotNull() {
        Assert.assertEquals("<qwerty>", XMLHandler.openTag("qwerty"));
    }

    @Test
    public void openTagWithNull() {
        Assert.assertEquals("<null>", XMLHandler.openTag(null));
    }

    @Test
    public void openTagWithExternalBuilder() {
        StringBuilder builder = new StringBuilder("qwe");
        XMLHandler.openTag(builder, "rty");
        Assert.assertEquals("qwe<rty>", builder.toString());
    }

    @Test
    public void closeTagWithNotNull() {
        Assert.assertEquals("</qwerty>", XMLHandler.closeTag("qwerty"));
    }

    @Test
    public void closeTagWithNull() {
        Assert.assertEquals("</null>", XMLHandler.closeTag(null));
    }

    @Test
    public void closeTagWithExternalBuilder() {
        StringBuilder builder = new StringBuilder("qwe");
        XMLHandler.closeTag(builder, "rty");
        Assert.assertEquals("qwe</rty>", builder.toString());
    }

    @Test
    public void buildCdataWithNotNull() {
        Assert.assertEquals("<![CDATA[qwerty]]>", XMLHandler.buildCDATA("qwerty"));
    }

    @Test
    public void buildCdataWithNull() {
        Assert.assertEquals("<![CDATA[]]>", XMLHandler.buildCDATA(null));
    }

    @Test
    public void buildCdataWithExternalBuilder() {
        StringBuilder builder = new StringBuilder("qwe");
        XMLHandler.buildCDATA(builder, "rty");
        Assert.assertEquals("qwe<![CDATA[rty]]>", builder.toString());
    }

    @Test
    public void timestamp2stringTest() {
        String actual = XMLHandler.timestamp2string(null);
        Assert.assertNull(actual);
    }

    @Test
    public void date2stringTest() {
        String actual = XMLHandler.date2string(null);
        Assert.assertNull(actual);
    }

    @Test
    public void addTagValueBigDecimal() {
        BigDecimal input = new BigDecimal("1234567890123456789.01");
        Assert.assertEquals(("<bigdec>1234567890123456789.01</bigdec>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("bigdec", input));
        Assert.assertEquals(("<bigdec>1234567890123456789.01</bigdec>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("bigdec", input, true));
        Assert.assertEquals("<bigdec>1234567890123456789.01</bigdec>", XMLHandler.addTagValue("bigdec", input, false));
    }

    @Test
    public void addTagValueBoolean() {
        Assert.assertEquals(("<abool>Y</abool>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("abool", true));
        Assert.assertEquals(("<abool>Y</abool>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("abool", true, true));
        Assert.assertEquals("<abool>Y</abool>", XMLHandler.addTagValue("abool", true, false));
        Assert.assertEquals(("<abool>N</abool>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("abool", false));
        Assert.assertEquals(("<abool>N</abool>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("abool", false, true));
        Assert.assertEquals("<abool>N</abool>", XMLHandler.addTagValue("abool", false, false));
    }

    @Test
    public void addTagValueDate() {
        String result = "2014/12/29 15:59:45.789";
        Calendar aDate = new GregorianCalendar();
        aDate.set(2014, (12 - 1), 29, 15, 59, 45);
        aDate.set(Calendar.MILLISECOND, 789);
        Assert.assertEquals(((("<adate>" + result) + "</adate>") + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("adate", aDate.getTime()));
        Assert.assertEquals(((("<adate>" + result) + "</adate>") + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("adate", aDate.getTime(), true));
        Assert.assertEquals((("<adate>" + result) + "</adate>"), XMLHandler.addTagValue("adate", aDate.getTime(), false));
    }

    @Test
    public void addTagValueLong() {
        long input = 123;
        Assert.assertEquals(("<along>123</along>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("along", input));
        Assert.assertEquals(("<along>123</along>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("along", input, true));
        Assert.assertEquals("<along>123</along>", XMLHandler.addTagValue("along", input, false));
        Assert.assertEquals((("<along>" + (String.valueOf(Long.MAX_VALUE))) + "</along>"), XMLHandler.addTagValue("along", Long.MAX_VALUE, false));
        Assert.assertEquals((("<along>" + (String.valueOf(Long.MIN_VALUE))) + "</along>"), XMLHandler.addTagValue("along", Long.MIN_VALUE, false));
    }

    @Test
    public void addTagValueInt() {
        int input = 456;
        Assert.assertEquals(("<anint>456</anint>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("anint", input));
        Assert.assertEquals(("<anint>456</anint>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("anint", input, true));
        Assert.assertEquals("<anint>456</anint>", XMLHandler.addTagValue("anint", input, false));
        Assert.assertEquals((("<anint>" + (String.valueOf(Integer.MAX_VALUE))) + "</anint>"), XMLHandler.addTagValue("anint", Integer.MAX_VALUE, false));
        Assert.assertEquals((("<anint>" + (String.valueOf(Integer.MIN_VALUE))) + "</anint>"), XMLHandler.addTagValue("anint", Integer.MIN_VALUE, false));
    }

    @Test
    public void addTagValueDouble() {
        double input = 123.45;
        Assert.assertEquals(("<adouble>123.45</adouble>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("adouble", input));
        Assert.assertEquals(("<adouble>123.45</adouble>" + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("adouble", input, true));
        Assert.assertEquals("<adouble>123.45</adouble>", XMLHandler.addTagValue("adouble", input, false));
        Assert.assertEquals((("<adouble>" + (String.valueOf(Double.MAX_VALUE))) + "</adouble>"), XMLHandler.addTagValue("adouble", Double.MAX_VALUE, false));
        Assert.assertEquals((("<adouble>" + (String.valueOf(Double.MIN_VALUE))) + "</adouble>"), XMLHandler.addTagValue("adouble", Double.MIN_VALUE, false));
        Assert.assertEquals((("<adouble>" + (String.valueOf(Double.MIN_NORMAL))) + "</adouble>"), XMLHandler.addTagValue("adouble", Double.MIN_NORMAL, false));
    }

    @Test
    public void addTagValueBinary() throws IOException {
        byte[] input = "Test Data".getBytes();
        String result = "H4sIAAAAAAAAAAtJLS5RcEksSQQAL4PL8QkAAAA=";
        Assert.assertEquals(((("<bytedata>" + result) + "</bytedata>") + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("bytedata", input));
        Assert.assertEquals(((("<bytedata>" + result) + "</bytedata>") + (XMLHandlerUnitTest.cr)), XMLHandler.addTagValue("bytedata", input, true));
        Assert.assertEquals((("<bytedata>" + result) + "</bytedata>"), XMLHandler.addTagValue("bytedata", input, false));
    }

    @Test
    public void addTagValueWithSurrogateCharacters() throws Exception {
        String expected = "<testTag attributeTest=\"test attribute value \ud842\udfb7\" >a\ud800\udc01\ud842\udfb7\ufec9\uff24test \ud802\udf44&lt;</testTag>";
        String tagValueWithSurrogates = "a\ud800\udc01\ud842\udfb7\ufec9\uff24test \ud802\udf44<";
        String attributeValueWithSurrogates = "test attribute value \ud842\udfb7";
        String result = XMLHandler.addTagValue("testTag", tagValueWithSurrogates, false, "attributeTest", attributeValueWithSurrogates);
        Assert.assertEquals(expected, result);
        DocumentBuilder builder = XMLHandler.createDocumentBuilder(false, false);
        builder.parse(new ByteArrayInputStream(result.getBytes()));
    }

    @Test
    public void testEscapingXmlBagCharacters() throws Exception {
        String testString = "[value_start (\"\'<&>) value_end]";
        String expectedStrAfterConversion = "<[value_start (&#34;&#39;&lt;&amp;&gt;) value_end] " + ((("[value_start (&#34;&#39;&lt;&amp;&gt;) value_end]=\"" + "[value_start (&#34;&#39;&lt;&amp;>) value_end]\" >") + "[value_start (&#34;&#39;&lt;&amp;&gt;) value_end]") + "</[value_start (&#34;&#39;&lt;&amp;&gt;) value_end]>");
        String result = XMLHandler.addTagValue(testString, testString, false, testString, testString);
        Assert.assertEquals(expectedStrAfterConversion, result);
    }

    @Test(expected = SAXParseException.class)
    public void createdDocumentBuilderThrowsExceptionWhenParsingXmlWithABigAmountOfExternalEntities() throws Exception {
        DocumentBuilder builder = XMLHandler.createDocumentBuilder(false, false);
        builder.parse(new ByteArrayInputStream(XMLHandlerUnitTest.MALICIOUS_XML.getBytes()));
    }

    @Test(expected = KettleXMLException.class)
    public void loadingXmlFromStreamThrowsExceptionWhenParsingXmlWithBigAmountOfExternalEntities() throws Exception {
        XMLHandler.loadXMLFile(new ByteArrayInputStream(XMLHandlerUnitTest.MALICIOUS_XML.getBytes()), "<def>", false, false);
    }

    @Test(expected = KettleXMLException.class)
    public void loadingXmlFromURLThrowsExceptionWhenParsingXmlWithBigAmountOfExternalEntities() throws Exception {
        File tmpFile = createTmpFile(XMLHandlerUnitTest.MALICIOUS_XML);
        XMLHandler.loadXMLFile(tmpFile.toURI().toURL());
    }

    @Test
    public void testGetSubNode() throws Exception {
        String testXML = "<?xml version=\"1.0\"?>\n" + ((((("<root>\n" + "<xpto>A</xpto>\n") + "<xpto>B</xpto>\n") + "<xpto>C</xpto>\n") + "<xpto>D</xpto>\n") + "</root>\n");
        DocumentBuilder builder = XMLHandler.createDocumentBuilder(false, false);
        Document parse = builder.parse(new ByteArrayInputStream(testXML.getBytes()));
        Node rootNode = parse.getFirstChild();
        Node lastSubNode = XMLHandler.getSubNode(rootNode, "xpto");
        Assert.assertNotNull(lastSubNode);
        Assert.assertEquals("A", lastSubNode.getTextContent());
    }

    @Test
    public void testGetLastSubNode() throws Exception {
        String testXML = "<?xml version=\"1.0\"?>\n" + ((((("<root>\n" + "<xpto>A</xpto>\n") + "<xpto>B</xpto>\n") + "<xpto>C</xpto>\n") + "<xpto>D</xpto>\n") + "</root>\n");
        DocumentBuilder builder = XMLHandler.createDocumentBuilder(false, false);
        Document parse = builder.parse(new ByteArrayInputStream(testXML.getBytes()));
        Node rootNode = parse.getFirstChild();
        Node lastSubNode = XMLHandler.getLastSubNode(rootNode, "xpto");
        Assert.assertNotNull(lastSubNode);
        Assert.assertEquals("D", lastSubNode.getTextContent());
    }

    @Test
    public void testGetSubNodeByNr_WithCache() throws Exception {
        String testXML = "<?xml version=\"1.0\"?>\n" + ((((("<root>\n" + "<xpto>0</xpto>\n") + "<xpto>1</xpto>\n") + "<xpto>2</xpto>\n") + "<xpto>3</xpto>\n") + "</root>\n");
        DocumentBuilder builder = XMLHandler.createDocumentBuilder(false, false);
        Document parse = builder.parse(new ByteArrayInputStream(testXML.getBytes()));
        Node rootNode = parse.getFirstChild();
        Node subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 0);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("0", subNode.getTextContent());
        subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 1);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("1", subNode.getTextContent());
        subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 2);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("2", subNode.getTextContent());
        subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 3);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("3", subNode.getTextContent());
    }

    @Test
    public void testGetSubNodeByNr_WithoutCache() throws Exception {
        String testXML = "<?xml version=\"1.0\"?>\n" + ((((("<root>\n" + "<xpto>0</xpto>\n") + "<xpto>1</xpto>\n") + "<xpto>2</xpto>\n") + "<xpto>3</xpto>\n") + "</root>\n");
        DocumentBuilder builder = XMLHandler.createDocumentBuilder(false, false);
        Document parse = builder.parse(new ByteArrayInputStream(testXML.getBytes()));
        Node rootNode = parse.getFirstChild();
        Node subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 0, false);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("0", subNode.getTextContent());
        subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 1, false);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("1", subNode.getTextContent());
        subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 2, false);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("2", subNode.getTextContent());
        subNode = XMLHandler.getSubNodeByNr(rootNode, "xpto", 3, false);
        Assert.assertNotNull(subNode);
        Assert.assertEquals("3", subNode.getTextContent());
    }
}

