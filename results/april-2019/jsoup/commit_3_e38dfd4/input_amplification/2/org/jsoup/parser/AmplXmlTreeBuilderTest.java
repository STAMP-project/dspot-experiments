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
    public void testSupplyParserToDataStream_literalMutationString839_add1253() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString839_add1253__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawQuery());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "!ttp://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString839__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString839__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString839_add1253__1)).getRawQuery());
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString831_failAssert0_literalMutationString1132_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString831 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString831_failAssert0_literalMutationString1132 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_add1294_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("").toURI();
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_add1294 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add846_add1156() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add846_add1156__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawQuery());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add846__10 = doc.html();
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add846__10);
        String o_testSupplyParserToDataStream_add846__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add846__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add846_add1156__1)).getRawQuery());
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add846__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_add1296_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_add1296 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0null1386_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0null1386 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_add1295_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_add1295 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0null1387_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0null1387 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1106_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http//foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1106 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString837_add1241() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString837_add1241__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawQuery());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "6T<a&-IlgDP6p]", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString837__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString837__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString837_add1241__1)).getRawQuery());
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_add1299_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                doc.html();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_add1299 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1104_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1104 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull849_failAssert0_literalMutationString1145_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStreamnull849 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStreamnull849_failAssert0_literalMutationString1145 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add841() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add841__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add841__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add841__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add841__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add841__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add841__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add841__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add841__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add841__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add841__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add841__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add841__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add841__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add841__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add841__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull848_literalMutationString1045_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStreamnull848__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStreamnull848_literalMutationString1045 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1103_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1103 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString839_literalMutationString1024_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "!ttp://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStream_literalMutationString839__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString839_literalMutationString1024 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1102_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo8com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString829_failAssert0_literalMutationString1102 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add841_add1170() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add841_add1170__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawQuery());
        URI o_testSupplyParserToDataStream_add841__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add841__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add841__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getPort())));
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add841_add1170__1)).getRawQuery());
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString838_literalMutationString1012_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.cm", Parser.xmlParser());
            String o_testSupplyParserToDataStream_literalMutationString838__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString838_literalMutationString1012 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3423_literalMutationString3595_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString3423__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString3423__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3423_literalMutationString3595 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3424__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3424__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3424__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString3424__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3424_add3872__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3424__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "httY://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3426__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3426__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3426__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString3426__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3426_add3896__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3426__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0null4055_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0null4055 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0null4054_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0null4054 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3432_add3803() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add3432_add3803__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add3432__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3432__10);
        String o_testDetectCharsetEncodingDeclaration_add3432__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3432__12);
        String o_testDetectCharsetEncodingDeclaration_add3432__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3432__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3432_add3803__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3432__10);
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3432__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3428() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add3428__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add3428__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3428__12);
        String o_testDetectCharsetEncodingDeclaration_add3428__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3428__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3428__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3428__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3429_literalMutationString3535_failAssert0() throws IOException, URISyntaxException {
        try {
            XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml");
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_add3429__11 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_add3429__13 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add3429_literalMutationString3535 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull3438_failAssert0_literalMutationString3751_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull3438 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull3438_failAssert0_literalMutationString3751 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_add3965_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_add3965 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_add3961_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_add3961 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_literalMutationString3736_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.c[m/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_literalMutationString3736 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3435_literalMutationString3559_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_add3435__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_add3435__12 = doc.html();
            String o_testDetectCharsetEncodingDeclaration_add3435__13 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add3435_literalMutationString3559 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3427_literalMutationString3616_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "h=ttp://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString3427__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString3427__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3427_literalMutationString3616 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            doc.charset().name();
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "^5*F{490vCT[y!DOWH?", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3425__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3425__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3425__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString3425__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3425_add3888__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3425__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_add3959_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("").toURI();
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3416_failAssert0_add3959 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull3437_literalMutationString3653_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclarationnull3437__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclarationnull3437__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull3437_literalMutationString3653 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "h=ttp://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3427__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3427__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3427__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString3427__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3427_add3880__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3427__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3421_failAssert0_literalMutationString3706_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3421 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString3421_failAssert0_literalMutationString3706 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3430_failAssert0_literalMutationString3664_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add3430 should have thrown IOException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add3430_failAssert0_literalMutationString3664 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3429_add3814() throws IOException, URISyntaxException {
        XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml");
        URI o_testDetectCharsetEncodingDeclaration_add3429_add3814__2 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add3429__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3429__11);
        String o_testDetectCharsetEncodingDeclaration_add3429__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3429__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3429_add3814__2)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3429__11);
    }
}

