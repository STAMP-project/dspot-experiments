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
    public void testSupplyParserToDataStream_literalMutationString9_add423() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString9_add423__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.cm", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString9__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString9__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString9_add423__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <diJv>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString8__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString8__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_literalMutationString215_add2135__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull20_add328() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStreamnull20_add328__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStreamnull20__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testSupplyParserToDataStreamnull20__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString11__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString11__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString191_add2141__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "W`_*s>).BmtV)2[", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString11__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString11__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString11_literalMutationString193_add2233__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStreamnull20_literalMutationString86_add2239() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://foocom", Parser.xmlParser());
        String o_testSupplyParserToDataStreamnull20__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testSupplyParserToDataStreamnull20__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStreamnull20_literalMutationString86_add2239__1)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString8_add429() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString8_add429__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString8__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString8__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add429__1)).getPort())));
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
    public void testSupplyParserToDataStream_add18_add372() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add18_add372__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add18_add372__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add372__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add18__10 = doc.html();
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add18__10);
        String o_testSupplyParserToDataStream_add18__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add18__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getRawUserInfo());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add18_add372__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add372__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add372__1)).getPort())));
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add18__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull3294_add3620() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclarationnull3294_add3620__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclarationnull3294__10 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull3294__10);
        String o_testDetectCharsetEncodingDeclarationnull3294__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclarationnull3294__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_add3620__1)).getPort())));
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull3294__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://exampl_e.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclarationnull3294__10 = doc.charset().name();
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull3294__10);
        String o_testDetectCharsetEncodingDeclarationnull3294__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testDetectCharsetEncodingDeclarationnull3294__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclarationnull3294_literalMutationString3364_add5430__1)).getPort())));
        Assert.assertEquals("UTF-8", o_testDetectCharsetEncodingDeclarationnull3294__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3285() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add3285__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add3285__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3285__12);
        String o_testDetectCharsetEncodingDeclaration_add3285__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3285__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3285__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3285__12);
    }
}

