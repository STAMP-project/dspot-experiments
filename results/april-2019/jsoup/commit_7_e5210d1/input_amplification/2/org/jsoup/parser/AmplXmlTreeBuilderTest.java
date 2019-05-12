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
    public void testSupplyParserToDataStream_add12977() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add12977__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add12977__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add12977__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add12977__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add12977__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add12977__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add12977__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add12977__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add12977__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add12977__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add12977__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add12977__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add4374_add4762() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add4374_add4762__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testDetectCharsetEncodingDeclaration_add4374__7 = Parser.xmlParser();
        Assert.assertNull(((Parser) (o_testDetectCharsetEncodingDeclaration_add4374__7)).getErrors());
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add4374__7)).isTrackErrors());
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add4374__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add4374__11);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4374_add4762__1)).getPort())));
        Assert.assertNull(((Parser) (o_testDetectCharsetEncodingDeclaration_add4374__7)).getErrors());
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add4374__7)).isTrackErrors());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add4374__11);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "ht}p://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString4370__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString4370__10);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString4370_add4714__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString4370__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add4371() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add4371__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add4371__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add4371__12);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add4371__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull4380_add4706() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclarationnull4380_add4706__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclarationnull4380__10 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull4380__10);
        String o_testDetectCharsetEncodingDeclarationnull4380__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclarationnull4380__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull4380_add4706__1)).getPort())));
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull4380__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add4371_add4793() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add4371_add4793__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getPort())));
        URI o_testDetectCharsetEncodingDeclaration_add4371__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add4371__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add4371__12);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getRawUserInfo());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add4371_add4793__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add4371__12);
    }
}

