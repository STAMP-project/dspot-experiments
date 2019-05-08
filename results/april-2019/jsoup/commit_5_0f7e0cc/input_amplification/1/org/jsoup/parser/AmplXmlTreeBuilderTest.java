package org.jsoup.parser;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public void testDetectCharsetEncodingDeclaration_literalMutationString177_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            doc.charset().name();
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString177 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add189() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add189__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add189__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add189__12);
        String o_testDetectCharsetEncodingDeclaration_add189__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add189__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add189__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add189__12);
    }
}

