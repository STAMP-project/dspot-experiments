package org.jsoup.parser;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplXmlTreeBuilderTest {
    @Ignore
    @Test
    public void testSupplyParserToConnection() throws IOException {
        String xmlUrl = "http://direct.infohound.net/tools/jsoup-xml-test.xml";
        Document xmlDoc = Jsoup.connect(xmlUrl).parser(Parser.xmlParser()).get();
        Document htmlDoc = Jsoup.connect(xmlUrl).parser(Parser.htmlParser()).get();
        Document autoXmlDoc = Jsoup.connect(xmlUrl).get();
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", TextUtil.stripNewlines(xmlDoc.html()));
        Assert.assertFalse(htmlDoc.equals(xmlDoc));
        Assert.assertEquals(xmlDoc, autoXmlDoc);
        Assert.assertEquals(1, htmlDoc.select("head").size());
        Assert.assertEquals(0, xmlDoc.select("head").size());
        Assert.assertEquals(0, autoXmlDoc.select("head").size());
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add551_add768() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add551_add768__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawAuthority());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add551_add768__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add551__10 = doc.html();
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add551__10);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getRawAuthority());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add551_add768__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add551_add768__1)).getPort())));
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add551__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add546() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add546__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add546__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add546__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add546__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add546__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add546__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add546__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add546__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add546__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add546__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add546__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add546__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add2522_add2916() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add2522_add2916__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add2522__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add2522__10);
        String o_testDetectCharsetEncodingDeclaration_add2522__12 = doc.html();
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add2522__12);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2522_add2916__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add2522__10);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add2522__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add2515() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add2515__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add2515__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add2515__12);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add2515__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add2515__12);
    }
}

