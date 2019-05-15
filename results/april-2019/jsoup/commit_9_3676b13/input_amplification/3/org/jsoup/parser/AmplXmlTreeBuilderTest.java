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
    public void testSupplyParserToDataStream_add17620() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add17620__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add17620__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add17620__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add17620__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add17620__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add17620__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add17620__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add17620__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add17620__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add17620__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17620__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add17620__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add17625_add17883() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add17625_add17883__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add17625__10 = doc.html();
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add17625__10);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add17625_add17883__1)).getPort())));
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add17625__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull17627_add17890() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStreamnull17627_add17890__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStreamnull17627__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testSupplyParserToDataStreamnull17627__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull17627_add17890__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add31447() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add31447__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add31447__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add31447__12);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add31447__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add31447__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://eample.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString31445__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString31445__10);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString31445_add31798__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString31445__10);
    }
}

