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
    public void testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0_add7566_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("").toURI();
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0_add7566 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add7042_add7450() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add7042_add7450__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testDetectCharsetEncodingDeclaration_add7042__7 = Parser.xmlParser();
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).isTrackErrors());
        Assert.assertNull(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).getErrors());
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add7042__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add7042__11);
        String o_testDetectCharsetEncodingDeclaration_add7042__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add7042__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_add7450__1)).getPort())));
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).isTrackErrors());
        Assert.assertNull(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).getErrors());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add7042__11);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "gE8kPc/p?qA9@!@Ha_*", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString7038__10 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_literalMutationString7038__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString7038__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclaration_literalMutationString7038__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7038null7612_add9336__1)).getPort())));
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_literalMutationString7038__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7028_failAssert0_add7522_failAssert0_literalMutationString8610_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("<span>Hello <div>there</div> <span>now</span></span>");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7028 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7028_failAssert0_add7522 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7028_failAssert0_add7522_failAssert0_literalMutationString8610 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323_failAssert0null10175_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323_failAssert0null10175 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://examplecom/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString7035__10 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_literalMutationString7035__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString7035__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclaration_literalMutationString7035__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7035null7618_add9370__1)).getPort())));
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_literalMutationString7035__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323_failAssert0_literalMutationString8364_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "httpD://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323_failAssert0_literalMutationString8364 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0_literalMutationString7339_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0_literalMutationString7339 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661_failAssert0_literalMutationString9025_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661_failAssert0_literalMutationString9025 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            doc.charset().name();
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323_failAssert0_add9454_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323_failAssert0_add9454 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testDetectCharsetEncodingDeclaration_add7042__7 = Parser.xmlParser();
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).isTrackErrors());
        Assert.assertNull(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).getErrors());
        Document doc = Jsoup.parse(inStream, null, "my@<0?n2|vsAQsN8GB&", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add7042__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add7042__11);
        String o_testDetectCharsetEncodingDeclaration_add7042__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add7042__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7042_literalMutationString7231_add9378__1)).getPort())));
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).isTrackErrors());
        Assert.assertNull(((Parser) (o_testDetectCharsetEncodingDeclaration_add7042__7)).getErrors());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add7042__11);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add7039() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add7039__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add7039__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add7039__12);
        String o_testDetectCharsetEncodingDeclaration_add7039__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add7039__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add7039__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add7039__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add7040_literalMutationString7190_failAssert0_literalMutationString8403_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add7040__11 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add7040__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add7040_literalMutationString7190 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add7040_literalMutationString7190_failAssert0_literalMutationString8403 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661_failAssert0_add10062_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661_failAssert0_add10062 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0_add7574_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0_add7574 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add7040_literalMutationString7185_failAssert0() throws IOException, URISyntaxException {
        try {
            XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml");
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_add7040__11 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_add7040__13 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add7040_literalMutationString7185 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7027_failAssert0null7661 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString7030_failAssert0_literalMutationString7323 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString7034__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString7034__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString7034__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString7034__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString7034_add7398__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString7034__10);
    }
}

