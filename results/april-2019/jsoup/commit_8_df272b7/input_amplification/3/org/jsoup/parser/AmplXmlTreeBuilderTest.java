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
    public void testSupplyParserToDataStream_add43695() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add43695__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43695__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43695__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43695__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43695__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43695__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43695__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43695__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43695__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43695__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43695__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull43702_add43965() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStreamnull43702_add43965__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawUserInfo());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStreamnull43702__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testSupplyParserToDataStreamnull43702__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getRawUserInfo());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull43702_add43965__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add43700_add43947() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add43700_add43947__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawUserInfo());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add43700__10 = doc.html();
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add43700__10);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getRawUserInfo());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947__1)).getPort())));
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add43700__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add43700_add43947_add45410() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add43700_add43947__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        URI o_testSupplyParserToDataStream_add43700_add43947_add45410__5 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add43700__10 = doc.html();
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add43700__10);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43700_add43947_add45410__5)).getPort())));
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add43700__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add43695null44040_add45467() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add43695null44040_add45467__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getPort())));
        URI o_testSupplyParserToDataStream_add43695__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add43695null44040__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testSupplyParserToDataStream_add43695null44040__14);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getRawFragment());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add43695null44040_add45467__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString15123__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString15123__10);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15123_add15576__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString15123__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add15128() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add15128__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add15128__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add15128__12);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15128__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add15128__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http:U/example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString15124__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString15124__10);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString15124_remove15701_add17137__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add15129_add15548_add17289() throws IOException, URISyntaxException {
        XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml");
        URI o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add15129_add15548__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add15129_add15548__11);
        String o_testDetectCharsetEncodingDeclaration_add15129__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add15129__11);
        TextUtil.stripNewlines(doc.html());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getRawFragment());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add15129_add15548_add17289__2)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add15129_add15548__11);
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add15129__11);
    }
}

