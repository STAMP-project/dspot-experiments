package org.jsoup.parser;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
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
    public void testSupplyParserToDataStream_literalMutationString1_failAssert0_add466_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("").toURI();
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1_failAssert0_add466 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString1_failAssert0_add468_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1_failAssert0_add468 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString1_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2_failAssert0_literalMutationString318_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2_failAssert0_literalMutationString318 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add16_add387() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add16_add387__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add16_add387__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add16_add387__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testSupplyParserToDataStream_add16__7 = Parser.xmlParser();
        Assert.assertFalse(((Parser) (o_testSupplyParserToDataStream_add16__7)).isTrackErrors());
        Assert.assertTrue(((Collection) (((Parser) (o_testSupplyParserToDataStream_add16__7)).getErrors())).isEmpty());
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add16__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add16__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add16_add387__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add16_add387__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add16_add387__1)).getPort())));
        Assert.assertFalse(((Parser) (o_testSupplyParserToDataStream_add16__7)).isTrackErrors());
        Assert.assertTrue(((Collection) (((Parser) (o_testSupplyParserToDataStream_add16__7)).getErrors())).isEmpty());
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString1_failAssert0null557_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1_failAssert0null557 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull21_failAssert0_literalMutationString229_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStreamnull21 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStreamnull21_failAssert0_literalMutationString229 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add13() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add13__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add13__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add13__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add13__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add13__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add13__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add13__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add13__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add13__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add13__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add13__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add13__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add13__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add13__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add13__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString1_failAssert0_literalMutationString262_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString1_failAssert0_literalMutationString262 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString10_add407() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString10_add407__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://fo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString10__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString10__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString10_add407__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString12_add419() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString12_add419__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "3f0Yt1z-!0c6w-", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString12__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString12__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString12_add419__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString812_literalMutationString1021_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://examplme.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString812__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString812__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString812_literalMutationString1021 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add816() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add816__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add816__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add816__12);
        String o_testDetectCharsetEncodingDeclaration_add816__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add816__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add816__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add816__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            doc.charset().name();
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0null1441_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0null1441 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0_add1350_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0_add1350 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0_add1355_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0_add1355 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0_literalMutationString1114_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "e$^hWa+NvYfoR-LcT5U", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString804_failAssert0_literalMutationString1114 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }
}

