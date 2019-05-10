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
    public void testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "Z&@n/w_7F5V-t}U%YT7", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString1873__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString1873__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString1873__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString1873__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1873_add2219__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString1873__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add1883_add2259() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add1883_add2259__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add1883__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add1883__10);
        String o_testDetectCharsetEncodingDeclaration_add1883__12 = doc.html();
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add1883__12);
        String o_testDetectCharsetEncodingDeclaration_add1883__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add1883__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1883_add2259__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add1883__10);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add1883__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString1870__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString1870__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString1870__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString1870__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1870_add2243__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString1870__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add1876() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add1876__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add1876__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add1876__12);
        String o_testDetectCharsetEncodingDeclaration_add1876__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add1876__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add1876__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add1876__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "Fttp://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString1874__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString1874__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString1874__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString1874__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString1874_add2211__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString1874__10);
    }
}

