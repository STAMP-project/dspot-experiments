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
    public void testSupplyParserToDataStreamnull20_add328() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStreamnull20_add328__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawFragment());
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
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStreamnull20_add328__1)).getRawFragment());
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
    public void testSupplyParserToDataStream_add14_add392() throws IOException, URISyntaxException {
        XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml");
        URI o_testSupplyParserToDataStream_add14_add392__2 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add14_add392__2)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add14_add392__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add14__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add14__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add14_add392__2)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add14_add392__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add14_add392__2)).getPort())));
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_literalMutationString8_add346() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_literalMutationString8_add346__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "<span>Hello <div>there</div> <span>now</span></span>", Parser.xmlParser());
        String o_testSupplyParserToDataStream_literalMutationString8__10 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_literalMutationString8__10);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_literalMutationString8_add346__1)).getPort())));
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
    public void testSupplyParserToDataStream_add18null543_add1930() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add18null543_add1930__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(null, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add18__10 = doc.html();
        Assert.assertEquals("", o_testSupplyParserToDataStream_add18__10);
        String o_testSupplyParserToDataStream_add18__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("", o_testSupplyParserToDataStream_add18__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add18null543_add1930__1)).getPort())));
        Assert.assertEquals("", o_testSupplyParserToDataStream_add18__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add18_add436() throws IOException, URISyntaxException {
        URI o_testSupplyParserToDataStream_add18_add436__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add18_add436__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add436__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add18__10 = doc.html();
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add18__10);
        String o_testSupplyParserToDataStream_add18__11 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add18__11);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add18_add436__1)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add436__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add18_add436__1)).getPort())));
        Assert.assertEquals("<doc>\n <val>\n  One\n  <val>\n   Two\n  </val>Three\n </val>\n</doc>", o_testSupplyParserToDataStream_add18__10);
    }

    @Test(timeout = 10000)
    public void testSupplyParserToDataStream_add13_add399_add2026() throws IOException, URISyntaxException {
        XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml");
        URI o_testSupplyParserToDataStream_add13_add399_add2026__2 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getPort())));
        URI o_testSupplyParserToDataStream_add13__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI();
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-test.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://foo.com", Parser.xmlParser());
        String o_testSupplyParserToDataStream_add13__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<doc><val>One<val>Two</val>Three</val></doc>", o_testSupplyParserToDataStream_add13__12);
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-test.xml", ((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).toString());
        Assert.assertEquals(1549444439, ((int) (((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).hashCode())));
        Assert.assertTrue(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).isAbsolute());
        Assert.assertFalse(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getScheme());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getAuthority());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getFragment());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getQuery());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getHost());
        Assert.assertNull(((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testSupplyParserToDataStream_add13_add399_add2026__2)).getPort())));
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http:/6/example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3299__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3299__10);
        String o_testDetectCharsetEncodingDeclaration_literalMutationString3299__12 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_literalMutationString3299__12);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_literalMutationString3299_add3653__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_literalMutationString3299__10);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3302() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add3302__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add3302__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3302__12);
        String o_testDetectCharsetEncodingDeclaration_add3302__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3302__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getRawFragment());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3302__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3309_add3762() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add3309_add3762__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getPort())));
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add3309__10 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3309__10);
        String o_testDetectCharsetEncodingDeclaration_add3309__12 = doc.html();
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3309__12);
        String o_testDetectCharsetEncodingDeclaration_add3309__13 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3309__13);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3309_add3762__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3309__10);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n<data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3309__12);
    }

    @Test(timeout = 10000)
    public void testDetectCharsetEncodingDeclaration_add3302_add3705_add5404() throws IOException, URISyntaxException {
        URI o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getPort())));
        URI o_testDetectCharsetEncodingDeclaration_add3302__1 = XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI();
        o_testDetectCharsetEncodingDeclaration_add3302__1.getRawAuthority();
        File xmlFile = new File(XmlTreeBuilder.class.getResource("/htmltests/xml-charset.xml").toURI());
        InputStream inStream = new FileInputStream(xmlFile);
        Document doc = Jsoup.parse(inStream, null, "http://example.com/", Parser.xmlParser());
        String o_testDetectCharsetEncodingDeclaration_add3302__12 = doc.charset().name();
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3302__12);
        String o_testDetectCharsetEncodingDeclaration_add3302__14 = TextUtil.stripNewlines(doc.html());
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <data>\u00e4\u00f6\u00e5\u00e9\u00fc</data>", o_testDetectCharsetEncodingDeclaration_add3302__14);
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawUserInfo());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getRawAuthority());
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/htmltests/xml-charset.xml", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).toString());
        Assert.assertEquals(-1740517919, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).hashCode())));
        Assert.assertTrue(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).isAbsolute());
        Assert.assertFalse(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).isOpaque());
        Assert.assertEquals("file", ((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getScheme());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getAuthority());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getFragment());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getQuery());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getHost());
        Assert.assertNull(((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URI) (o_testDetectCharsetEncodingDeclaration_add3302_add3705_add5404__1)).getPort())));
        Assert.assertEquals("ISO-8859-1", o_testDetectCharsetEncodingDeclaration_add3302__12);
    }
}

