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
    public void testSupplyParserToDataStream_literalMutationString2384_add2787() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString2384_add2787__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http:/$/foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString2384__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString2384__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2384_add2787__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull2395_failAssert0_literalMutationString2628_failAssert0_literalMutationString3990_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStreamnull2395 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStreamnull2395_failAssert0_literalMutationString2628 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStreamnull2395_failAssert0_literalMutationString2628_failAssert0_literalMutationString3990 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add2390_add2724_add4380() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add2390_add2724_add4380__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testSupplyParserToDataStream_add2390__7 = Parser.xmlParser();
        Assert.assertTrue(((Collection) (((Parser) (o_testSupplyParserToDataStream_add2390__7)).getErrors())).isEmpty());
        Assert.assertFalse(((Parser) (o_testSupplyParserToDataStream_add2390__7)).isTrackErrors());
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add2390_add2724__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2390_add2724__13);
        String o_testSupplyParserToDataStream_add2390__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2390__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2390_add2724_add4380__1)).getPort())));
        Assert.assertTrue(((Collection) (((Parser) (o_testSupplyParserToDataStream_add2390__7)).getErrors())).isEmpty());
        Assert.assertFalse(((Parser) (o_testSupplyParserToDataStream_add2390__7)).isTrackErrors());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2390_add2724__13);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2385_add2805() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString2385_add2805__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http:(/foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString2385__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString2385__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2385_add2805__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2376_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2376_failAssert0null2936_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0null2936 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664_failAssert0_literalMutationString4278_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664_failAssert0_literalMutationString4278 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2375_failAssert0_literalMutationString2678_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2375 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2375_failAssert0_literalMutationString2678 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull2394_add2780_literalMutationString3486_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStreamnull2394_add2780__10 = doc.html();
            String o_testSupplyParserToDataStreamnull2394__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStreamnull2394_add2780_literalMutationString3486 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://fooQcom", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add2387() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add2387__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2387__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2387__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2387__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2387__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2387__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2387__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add2387__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2387__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2387__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2387__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2387__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2387__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2387__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2387__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2387__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add2388_literalMutationString2456_failAssert0() throws IOException, URISyntaxException {
        try {
            XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml");
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStream_add2388__11 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_add2388_literalMutationString2456 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2376_failAssert0_add2856_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0_add2856 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2382_add2799() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString2382_add2799__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString2382__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString2382__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2382_add2799__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664_failAssert0_add5167_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://fooQcom", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0_literalMutationString2664_failAssert0_add5167 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2376_failAssert0_add2858_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2376_failAssert0_add2858 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
            String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0null13704_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0null13704 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_add13204_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_add13204 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10518null11119_failAssert0_literalMutationString12089_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10518__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10518__12 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10518null11119 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10518null11119_failAssert0_literalMutationString12089 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10511_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            doc.charset().name();
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10511 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0null13703_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0null13703 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10511_failAssert0_literalMutationString10818_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "htp://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10511 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10511_failAssert0_literalMutationString10818 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_literalMutationString12174_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "l!<8+&YnD?tr9%#y6* ", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_literalMutationString12174 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_add13208_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
                doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_add13208 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10521() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add10521__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add10521__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10521__12);
        String o_testDetectCharsetEncodingDeclaration_add10521__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add10521__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10521__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10521__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10511_failAssert0_add11057_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset();
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10511 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10511_failAssert0_add11057 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString10516__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString10516__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString10516__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString10516__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString10516_add10997__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString10516__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787_failAssert0null13773_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787_failAssert0null13773 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_add13203_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_add13203 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787_failAssert0_add13456_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787_failAssert0_add13456 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787_failAssert0_literalMutationString12430_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http:/example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10512_failAssert0_literalMutationString10787_failAssert0_literalMutationString12430 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_literalMutationString12167_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10528__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10528__12 = doc.html();
                String o_testDetectCharsetEncodingDeclaration_add10528__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10528_literalMutationString10604_failAssert0_literalMutationString12167 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }
}

