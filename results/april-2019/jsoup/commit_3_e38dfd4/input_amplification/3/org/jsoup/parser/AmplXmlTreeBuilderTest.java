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
    public void testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "htVp://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4892_literalMutationString5053_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
            String o_testSupplyParserToDataStream_literalMutationString4892__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4892_literalMutationString5053 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4892_add5297() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString4892_add5297__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString4892__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4892__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4892_add5297__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "kzEGl!}t3s(O!>", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4897_add5219() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add4897_add5219__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getPort())));
        URI o_testSupplyParserToDataStream_add4897__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add4897__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add4897__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add4897_add5219__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0null5439_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0null5439 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_literalMutationString7865_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "htVp//foo.com", Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_literalMutationString7865 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_literalMutationString7864_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "htVp://foo.c-om", Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_literalMutationString7864 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4887_failAssert0_add5368_failAssert0_literalMutationString6934_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4887 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4887_failAssert0_add5368 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4887_failAssert0_add5368_failAssert0_literalMutationString6934 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0null5440_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0null5440 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_add9694_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "htVp://foo.com", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "htVp://foo.com", Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_add9694 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_add9693_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "htVp://foo.com", Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0_add9693 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_literalMutationString7883_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_literalMutationString7883 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4896_literalMutationString5065_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "|[as/pk;Ma{CQi", Parser.xmlParser());
            String o_testSupplyParserToDataStream_literalMutationString4896__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4896_literalMutationString5065 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343_failAssert0null10199_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("").toURI();
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343_failAssert0null10199 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4900_literalMutationString5009_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Parser o_testSupplyParserToDataStream_add4900__7 = Parser.xmlParser();
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStream_add4900__11 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_add4900_literalMutationString5009 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_literalMutationString7886_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "kzEGl!t3s(O!>", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_literalMutationString7886 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4895_add5291() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString4895_add5291__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "htVp://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4895__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4895_add5291__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4891null5412_failAssert0_literalMutationString8044_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4891__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4891null5412 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4891null5412_failAssert0_literalMutationString8044 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4896_literalMutationString5065_failAssert0_add9132_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Jsoup.parse(inStream, null, "|[as/pk;Ma{CQi", Parser.xmlParser());
                Document doc = Jsoup.parse(inStream, null, "|[as/pk;Ma{CQi", Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4896__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4896_literalMutationString5065 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4896_literalMutationString5065_failAssert0_add9132 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4900_literalMutationString5009_failAssert0_literalMutationString7946_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser o_testSupplyParserToDataStream_add4900__7 = Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                String o_testSupplyParserToDataStream_add4900__11 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_add4900_literalMutationString5009 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_add4900_literalMutationString5009_failAssert0_literalMutationString7946 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4893_add5315() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString4893_add5315__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "ht&tp://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString4893__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4893__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5315__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("").toURI();
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4900_literalMutationString5009_failAssert0null10488_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser o_testSupplyParserToDataStream_add4900__7 = Parser.xmlParser();
                Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
                String o_testSupplyParserToDataStream_add4900__11 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_add4900_literalMutationString5009 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_add4900_literalMutationString5009_failAssert0null10488 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4896_add5303() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString4896_add5303__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "|[as/pk;Ma{CQi", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString4896__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4896__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4896_add5303__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_add9708_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Jsoup.parse(inStream, null, "kzEGl!}t3s(O!>", Parser.xmlParser());
                    Document doc = Jsoup.parse(inStream, null, "kzEGl!}t3s(O!>", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_add9708 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4891_add5289_add8692() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString4891_add5289__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4891_add5289__10);
        String o_testSupplyParserToDataStream_literalMutationString4891__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4891__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4891_add5289_add8692__1)).getPort())));
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4891_add5289__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4893_add5319_add8487() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "ht&tp://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString4893_add5319__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4893_add5319__10);
        String o_testSupplyParserToDataStream_literalMutationString4893__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4893__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString4893_add5319_add8487__1)).getPort())));
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString4893_add5319__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_add9709_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(inStream, null, "kzEGl!}t3s(O!>", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0_add9709 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5346_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5346 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5344_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5344 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull4904_literalMutationString5101_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStreamnull4904__10 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStreamnull4904_literalMutationString5101 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4889_failAssert0_literalMutationString5164_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4889 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4889_failAssert0_literalMutationString5164 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343_failAssert0_add8961_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("").toURI();
                    XmlTreeBuilder.class.getResource("").toURI();
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343_failAssert0_add8961 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4902_literalMutationString4996_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStream_add4902__10 = doc.html();
            String o_testSupplyParserToDataStream_add4902__11 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_add4902_literalMutationString4996 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4895_literalMutationString5043_failAssert0_literalMutationString8155_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "htVp://foo.com", Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5043 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5043_failAssert0_literalMutationString8155 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull4905_failAssert0_literalMutationString5200_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStreamnull4905 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStreamnull4905_failAssert0_literalMutationString5200 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4888_failAssert0_literalMutationString5194_failAssert0_literalMutationString7972_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4888 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4888_failAssert0_literalMutationString5194 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4888_failAssert0_literalMutationString5194_failAssert0_literalMutationString7972 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5146_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http:/foo.com", Parser.xmlParser());
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5146 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4897() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add4897__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add4897__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add4897__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add4897__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add4897__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add4897__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add4897__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add4897__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add4897__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add4897__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add4897__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add4897__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add4897__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add4897__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4897__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add4897__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0null10466_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "htVp://foo.com", Parser.xmlParser());
                String o_testSupplyParserToDataStream_literalMutationString4895__10 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4895_literalMutationString5041_failAssert0null10466 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4900null5405_literalMutationString6710_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Parser o_testSupplyParserToDataStream_add4900__7 = Parser.xmlParser();
            Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
            String o_testSupplyParserToDataStream_add4900__11 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testSupplyParserToDataStream_add4900null5405_literalMutationString6710 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0null10473_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_literalMutationString5150_failAssert0null10473 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4900_literalMutationString5009_failAssert0_add9747_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser o_testSupplyParserToDataStream_add4900__7 = Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
                String o_testSupplyParserToDataStream_add4900__11 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testSupplyParserToDataStream_add4900_literalMutationString5009 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_add4900_literalMutationString5009_failAssert0_add9747 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add4898_add5213() throws IOException, URISyntaxException {
        XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml");
        URI o_testSupplyParserToDataStream_add4898_add5213__2 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add4898__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add4898__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add4898_add5213__2)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343_failAssert0_literalMutationString6878_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("").toURI();
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSupplyParserToDataStream_literalMutationString4885_failAssert0_add5343_failAssert0_literalMutationString6878 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046_failAssert0_add23688_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    doc.html();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046_failAssert0_add23688 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046_failAssert0_literalMutationString21531_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://exampl.com/", Parser.xmlParser());
                    doc.charset().name();
                    doc.html();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046_failAssert0_literalMutationString21531 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19529_literalMutationString19717_failAssert0_literalMutationString21959_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                String o_testDetectCharsetEncodingDeclaration_literalMutationString19529__10 = doc.charset().name();
                String o_testDetectCharsetEncodingDeclaration_literalMutationString19529__12 = TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19529_literalMutationString19717 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19529_literalMutationString19717_failAssert0_literalMutationString21959 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042_failAssert0_literalMutationString22465_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042_failAssert0_literalMutationString22465 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawFragment());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "X+W3C#K)t:MUeA!!K^<", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19532__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19532__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19532__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString19532__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19532_add19971__1)).getRawFragment());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19532__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19533_add19969_literalMutationString21149_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://exaple.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString19533__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19969__14 = TextUtil.stripNewlines(doc.html());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString19533__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19533_add19969_literalMutationString21149 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19527_failAssert0null20156_failAssert0_literalMutationString22771_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19527 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19527_failAssert0null20156 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19527_failAssert0null20156_failAssert0_literalMutationString22771 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add19535() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add19535__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add19535__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19535__12);
        String o_testDetectCharsetEncodingDeclaration_add19535__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add19535__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19535__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawFragment());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19529__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19529__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19529__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString19529__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19529_add19979__1)).getRawFragment());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19529__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19526_failAssert0_literalMutationString19789_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19526 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19526_failAssert0_literalMutationString19789 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040_failAssert0_literalMutationString21532_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040_failAssert0_literalMutationString21532 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "OIB2*[17{W&Mg)T24QB", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_literalMutationString21926_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://examle.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_literalMutationString21926 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add19538_add19889() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add19538_add19889__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawFragment());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testDetectCharsetEncodingDeclaration_add19538__7 = Parser.xmlParser();
        Assert.assertTrue(((Collection) (((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).getErrors())).isEmpty());
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).isTrackErrors());
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add19538__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19538__11);
        String o_testDetectCharsetEncodingDeclaration_add19538__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add19538__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889__1)).getRawFragment());
        Assert.assertTrue(((Collection) (((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).getErrors())).isEmpty());
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).isTrackErrors());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19538__11);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_literalMutationString21927_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://axample.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_literalMutationString21927 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805_failAssert0null25431_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805_failAssert0null25431 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20153_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20153 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add19538_literalMutationString19630_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Parser o_testDetectCharsetEncodingDeclaration_add19538__7 = Parser.xmlParser();
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_add19538__11 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_add19538__13 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_add19538_literalMutationString19630 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805_failAssert0_literalMutationString22609_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805_failAssert0_literalMutationString22609 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                doc.html();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add19538_add19889_add22961() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add19538_add19889__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        URI o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Parser o_testDetectCharsetEncodingDeclaration_add19538__7 = Parser.xmlParser();
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).isTrackErrors());
        Assert.assertTrue(((Collection) (((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).getErrors())).isEmpty());
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add19538__11 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19538__11);
        String o_testDetectCharsetEncodingDeclaration_add19538__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add19538__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19538_add19889_add22961__5)).getPort())));
        Assert.assertFalse(((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).isTrackErrors());
        Assert.assertTrue(((Collection) (((Parser) (o_testDetectCharsetEncodingDeclaration_add19538__7)).getErrors())).isEmpty());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19538__11);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805_failAssert0_add24695_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19805_failAssert0_add24695 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                XmlTreeBuilder.class.getResource("");
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                InputStream inStream = new FileInputStream(xmlFile);
                Parser.xmlParser();
                Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                doc.charset().name();
                TextUtil.stripNewlines(doc.html());
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152_failAssert0_literalMutationString22247_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152_failAssert0_literalMutationString22247 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0null25243_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0null25243 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152_failAssert0_add24326_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152_failAssert0_add24326 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_add23998_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_add23998 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040_failAssert0_add23704_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("");
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20040_failAssert0_add23704 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getPort())));
        URI o_testDetectCharsetEncodingDeclaration_add19535__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add19535__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19535__12);
        String o_testDetectCharsetEncodingDeclaration_add19535__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add19535__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19535_literalMutationString19684_add23236__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19535__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807_failAssert0_literalMutationString21936_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "OIB29*[17{W&Mg)T24QB", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807_failAssert0_literalMutationString21936 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_add23996_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19801_failAssert0_add23996 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull19545_failAssert0_literalMutationString19859_failAssert0_literalMutationString21937_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull19545 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull19545_failAssert0_literalMutationString19859 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull19545_failAssert0_literalMutationString19859_failAssert0_literalMutationString21937 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834_failAssert0_add24074_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834_failAssert0_add24074 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://exaple.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19533__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19533__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970__14 = doc.html();
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970__14);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19533__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString19533__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970_add22840__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19533__10);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString19533_add19970__14);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834_failAssert0_literalMutationString21992_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19524_failAssert0_literalMutationString19834_failAssert0_literalMutationString21992 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull19545_failAssert0_add20088_failAssert0_literalMutationString21544_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    doc.charset().name();
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull19545 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull19545_failAssert0_add20088 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclarationnull19545_failAssert0_add20088_failAssert0_literalMutationString21544 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19531_literalMutationString19723_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http:m/example.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString19531__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString19531__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19531_literalMutationString19723 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://examplfe.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19534__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19534__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString19534__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString19534__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007_add23615__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19534_add20007__10);
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString19534__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20153_failAssert0_add24318_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    doc.charset().name();
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20153 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20153_failAssert0_add24318 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add19541_add19904() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add19541_add19904__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawFragment());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add19541__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19541__10);
        String o_testDetectCharsetEncodingDeclaration_add19541__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add19541__12);
        String o_testDetectCharsetEncodingDeclaration_add19541__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add19541__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add19541_add19904__1)).getRawFragment());
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add19541__10);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add19541__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
            doc.charset().name();
            TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042_failAssert0null25392_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042_failAssert0null25392 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19534_literalMutationString19747_failAssert0() throws IOException, URISyntaxException {
        try {
            File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
            InputStream inStream = new FileInputStream(xmlFile);
            Document doc = Jsoup.parse(inStream, null, "http://examplfe.com/", Parser.xmlParser());
            String o_testDetectCharsetEncodingDeclaration_literalMutationString19534__10 = doc.charset().name();
            String o_testDetectCharsetEncodingDeclaration_literalMutationString19534__12 = TextUtil.stripNewlines(doc.html());
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19534_literalMutationString19747 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046_failAssert0null25136_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, null, Parser.xmlParser());
                    doc.charset().name();
                    doc.html();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20046_failAssert0null25136 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152_failAssert0null25336_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, null, Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0null20152_failAssert0null25336 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull19544_add20011() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclarationnull19544_add20011__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawFragment());
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclarationnull19544__10 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull19544__10);
        String o_testDetectCharsetEncodingDeclarationnull19544__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclarationnull19544__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawQuery());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getPort())));
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull19544_add20011__1)).getRawFragment());
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull19544__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807_failAssert0null25246_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(null, null, "OIB2*[17{W&Mg)T24QB", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807_failAssert0null25246 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807_failAssert0_add24003_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    XmlTreeBuilder.class.getResource("").toURI();
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Document doc = Jsoup.parse(inStream, null, "OIB2*[17{W&Mg)T24QB", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_literalMutationString19807_failAssert0_add24003 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042_failAssert0_add24582_failAssert0() throws IOException, URISyntaxException {
        try {
            {
                {
                    File xmlFile = new File(XmlTreeBuilder.class.getResource("").toURI());
                    InputStream inStream = new FileInputStream(xmlFile);
                    Parser.xmlParser();
                    Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
                    doc.charset().name();
                    TextUtil.stripNewlines(doc.html());
                    org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testDetectCharsetEncodingDeclaration_literalMutationString19523_failAssert0_add20042_failAssert0_add24582 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/parser (Is a directory)", expected.getMessage());
        }
    }
}

