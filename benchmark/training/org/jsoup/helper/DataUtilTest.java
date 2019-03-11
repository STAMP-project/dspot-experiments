package org.jsoup.helper;


import DataUtil.boundaryLength;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.jsoup.Jsoup;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Assert;
import org.junit.Test;


public class DataUtilTest {
    @Test
    public void testCharset() {
        Assert.assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html;charset=utf-8 "));
        Assert.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
        Assert.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1"));
        Assert.assertEquals(null, DataUtil.getCharsetFromContentType("text/html"));
        Assert.assertEquals(null, DataUtil.getCharsetFromContentType(null));
        Assert.assertEquals(null, DataUtil.getCharsetFromContentType("text/html;charset=Unknown"));
    }

    @Test
    public void testQuotedCharset() {
        Assert.assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset=\"utf-8\""));
        Assert.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html;charset=\"UTF-8\""));
        Assert.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""));
        Assert.assertEquals(null, DataUtil.getCharsetFromContentType("text/html; charset=\"Unsupported\""));
        Assert.assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset='UTF-8'"));
    }

    @Test
    public void discardsSpuriousByteOrderMark() throws IOException {
        String html = "\ufeff<html><head><title>One</title></head><body>Two</body></html>";
        Document doc = DataUtil.parseInputStream(stream(html), "UTF-8", "http://foo.com/", Parser.htmlParser());
        Assert.assertEquals("One", doc.head().text());
    }

    @Test
    public void discardsSpuriousByteOrderMarkWhenNoCharsetSet() throws IOException {
        String html = "\ufeff<html><head><title>One</title></head><body>Two</body></html>";
        Document doc = DataUtil.parseInputStream(stream(html), null, "http://foo.com/", Parser.htmlParser());
        Assert.assertEquals("One", doc.head().text());
        Assert.assertEquals("UTF-8", doc.outputSettings().charset().displayName());
    }

    @Test
    public void shouldNotThrowExceptionOnEmptyCharset() {
        Assert.assertEquals(null, DataUtil.getCharsetFromContentType("text/html; charset="));
        Assert.assertEquals(null, DataUtil.getCharsetFromContentType("text/html; charset=;"));
    }

    @Test
    public void shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
        Assert.assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=ISO-8859-1, charset=1251"));
    }

    @Test
    public void shouldCorrectCharsetForDuplicateCharsetString() {
        Assert.assertEquals("iso-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=charset=iso-8859-1"));
    }

    @Test
    public void shouldReturnNullForIllegalCharsetNames() {
        Assert.assertEquals(null, DataUtil.getCharsetFromContentType("text/html; charset=$HJKDF?$/("));
    }

    @Test
    public void generatesMimeBoundaries() {
        String m1 = DataUtil.mimeBoundary();
        String m2 = DataUtil.mimeBoundary();
        Assert.assertEquals(boundaryLength, m1.length());
        Assert.assertEquals(boundaryLength, m2.length());
        Assert.assertNotSame(m1, m2);
    }

    @Test
    public void wrongMetaCharsetFallback() throws IOException {
        String html = "<html><head><meta charset=iso-8></head><body></body></html>";
        Document doc = DataUtil.parseInputStream(stream(html), null, "http://example.com", Parser.htmlParser());
        final String expected = "<html>\n" + ((((" <head>\n" + "  <meta charset=\"iso-8\">\n") + " </head>\n") + " <body></body>\n") + "</html>");
        Assert.assertEquals(expected, doc.toString());
    }

    @Test
    public void secondMetaElementWithContentTypeContainsCharsetParameter() throws Exception {
        String html = "<html><head>" + (("<meta http-equiv=\"Content-Type\" content=\"text/html\">" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">") + "</head><body>???</body></html>");
        Document doc = DataUtil.parseInputStream(stream(html, "euc-kr"), null, "http://example.com", Parser.htmlParser());
        Assert.assertEquals("???", doc.body().text());
    }

    @Test
    public void firstMetaElementWithCharsetShouldBeUsedForDecoding() throws Exception {
        String html = "<html><head>" + (("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=koi8-u\">") + "</head><body>?bergr??entr?ger</body></html>");
        Document doc = DataUtil.parseInputStream(stream(html, "iso-8859-1"), null, "http://example.com", Parser.htmlParser());
        Assert.assertEquals("?bergr??entr?ger", doc.body().text());
    }

    @Test
    public void supportsBOMinFiles() throws IOException {
        // test files from http://www.i18nl10n.com/korean/utftest/
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-16BE"));
        Assert.assertTrue(doc.text().contains("??????"));
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-16LE"));
        Assert.assertTrue(doc.text().contains("??????"));
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-32BE"));
        Assert.assertTrue(doc.text().contains("??????"));
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertTrue(doc.title().contains("UTF-32LE"));
        Assert.assertTrue(doc.text().contains("??????"));
    }

    @Test
    public void supportsUTF8BOM() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf8.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        Assert.assertEquals("OK", doc.head().select("title").text());
    }

    @Test
    public void supportsXmlCharsetDeclaration() throws IOException {
        String encoding = "iso-8859-1";
        InputStream soup = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" + ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" + "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">Hell\u00f6 W\u00f6rld!</html>")).getBytes(encoding));
        Document doc = Jsoup.parse(soup, null, "");
        Assert.assertEquals("Hell? W?rld!", doc.body().text());
    }
}

