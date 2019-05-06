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
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0_literalMutationString2667_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_literalMutationString2667 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2401_add2823() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString2401_add2823__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://fio.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString2401__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString2401__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2401_add2823__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add2406_add2756_add4651() throws IOException, URISyntaxException {
        XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml");
        XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml");
        URI o_testSupplyParserToDataStream_add2406_add2756_add4651__3 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add2406__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2406__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2406_add2756_add4651__3)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2871_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2871 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866_failAssert0_add5181_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866_failAssert0_add5181 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0_literalMutationString2663_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_literalMutationString2663 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2402_add2811() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString2402_add2811__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.cm", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString2402__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString2402__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2402_add2811__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866_failAssert0_literalMutationString4264_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http:k//foo.com", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0_add2866_failAssert0_literalMutationString4264 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0null2952_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0null2952 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add2409_add2726() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add2409_add2726__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add2409__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2409__10);
        String o_testSupplyParserToDataStream_add2409__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2409__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2409_add2726__1)).getPort())));
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2409__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add2408null2908_failAssert0_literalMutationString3709_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser o_testSupplyParserToDataStream_add2408__7 = Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                String o_testSupplyParserToDataStream_add2408__11 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_add2408null2908 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_add2408null2908_failAssert0_literalMutationString3709 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2403_literalMutationString2565_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http:0//foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStream_literalMutationString2403__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2403_literalMutationString2565 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2399_add2829() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString2399_add2829__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString2399__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString2399__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString2399_add2829__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add2405() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add2405__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2405__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2405__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2405__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2405__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2405__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2405__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add2405__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add2405__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add2405__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add2405__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add2405__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add2405__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add2405__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add2405__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add2405__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString2393_failAssert0null2951_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString2393_failAssert0null2951 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524_failAssert0_add13175_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser o_testDetectCharsetEncodingDeclaration_add10444__7 = Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10444__11 = doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                String o_testDetectCharsetEncodingDeclaration_add10444__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524_failAssert0_add13175 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_add13083_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_add13083 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_add13085_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_add13085 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0null13627_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0null13627 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524_failAssert0null13655_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser o_testDetectCharsetEncodingDeclaration_add10444__7 = Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10444__11 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10444__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524_failAssert0null13655 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            doc.charset().name();
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10447_add10808() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add10447_add10808__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add10447__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10447__10);
        String o_testDetectCharsetEncodingDeclaration_add10447__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add10447__12);
        String o_testDetectCharsetEncodingDeclaration_add10447__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add10447__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10447_add10808__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10447__10);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add10447__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10433_failAssert0_literalMutationString10757_failAssert0_literalMutationString12054_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10433 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10433_failAssert0_literalMutationString10757 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10433_failAssert0_literalMutationString10757_failAssert0_literalMutationString12054 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Parser o_testDetectCharsetEncodingDeclaration_add10444__7 = Parser.xmlParser();
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_add10444__11 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_add10444__13 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10441() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add10441__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add10441__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10441__12);
        String o_testDetectCharsetEncodingDeclaration_add10441__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add10441__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10441__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_literalMutationString11996_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("9");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_literalMutationString11996 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617_failAssert0null13665_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__12 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617_failAssert0null13665 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10445null11029_add12443() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add10445__10 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_add10445__10);
        String o_testDetectCharsetEncodingDeclaration_add10445__12 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_add10445__12);
        String o_testDetectCharsetEncodingDeclaration_add10445__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclaration_add10445__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10445null11029_add12443__1)).getPort())));
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_add10445__10);
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_add10445__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add10441__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        URI o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add10441__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10441__12);
        String o_testDetectCharsetEncodingDeclaration_add10441__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add10441__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10441_literalMutationString10573_add12591__5)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add10441__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617_failAssert0_literalMutationString12145_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "<span>Hello <d,iv>there</div> <span>now</span></span>", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__12 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617_failAssert0_literalMutationString12145 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0null13626_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0null13626 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10444null11012_add12661() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testDetectCharsetEncodingDeclaration_add10444__7 = Parser.xmlParser();
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add10444__7)).isTrackErrors());
        Assert.assertTrue(((Collection) (((Parser) (o_testDetectCharsetEncodingDeclaration_add10444__7)).getErrors())).isEmpty());
        Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add10444__11 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_add10444__11);
        String o_testDetectCharsetEncodingDeclaration_add10444__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclaration_add10444__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add10444null11012_add12661__1)).getPort())));
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add10444__7)).isTrackErrors());
        Assert.assertTrue(((Collection) (((Parser) (o_testDetectCharsetEncodingDeclaration_add10444__7)).getErrors())).isEmpty());
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclaration_add10444__11);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_literalMutationString11997_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_literalMutationString11997 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_literalMutationString12005_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, ";_Ll?7wOy}NAd8(4>*p", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10429_failAssert0_add10973_failAssert0_literalMutationString12005 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10435_add10884_literalMutationString11452_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString10435__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString10435_add10884__14 = doc.html();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString10435__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10435_add10884_literalMutationString11452 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524_failAssert0_literalMutationString12104_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser o_testDetectCharsetEncodingDeclaration_add10444__7 = Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, "ht)p://example.com/", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_add10444__11 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_add10444__13 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add10444_literalMutationString10524_failAssert0_literalMutationString12104 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617_failAssert0_add13208_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_literalMutationString10436__12 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString10436_literalMutationString10617_failAssert0_add13208 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }
}

