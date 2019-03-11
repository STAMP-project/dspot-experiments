package org.jsoup.helper;


import Connection.KeyVal;
import Connection.Method.GET;
import Connection.Method.POST;
import Connection.Request;
import Connection.Response;
import HttpConnection.DEFAULT_UA;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.MultiLocaleRule;
import org.jsoup.integration.ParseTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HttpConnectionTest {
    /* most actual network http connection tests are in integration */
    @Rule
    public MultiLocaleRule rule = new MultiLocaleRule();

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionOnParseWithoutExecute() throws IOException {
        Connection con = HttpConnection.connect("http://example.com");
        con.response().parse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionOnBodyWithoutExecute() throws IOException {
        Connection con = HttpConnection.connect("http://example.com");
        con.response().body();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionOnBodyAsBytesWithoutExecute() throws IOException {
        Connection con = HttpConnection.connect("http://example.com");
        con.response().bodyAsBytes();
    }

    @Test
    @MultiLocaleRule.MultiLocaleTest
    public void caseInsensitiveHeaders() {
        Connection.Response res = new HttpConnection.Response();
        res.header("Accept-Encoding", "gzip");
        res.header("content-type", "text/html");
        res.header("refErrer", "http://example.com");
        Assert.assertTrue(res.hasHeader("Accept-Encoding"));
        Assert.assertTrue(res.hasHeader("accept-encoding"));
        Assert.assertTrue(res.hasHeader("accept-Encoding"));
        Assert.assertTrue(res.hasHeader("ACCEPT-ENCODING"));
        Assert.assertEquals("gzip", res.header("accept-Encoding"));
        Assert.assertEquals("gzip", res.header("ACCEPT-ENCODING"));
        Assert.assertEquals("text/html", res.header("Content-Type"));
        Assert.assertEquals("http://example.com", res.header("Referrer"));
        res.removeHeader("Content-Type");
        Assert.assertFalse(res.hasHeader("content-type"));
        res.removeHeader("ACCEPT-ENCODING");
        Assert.assertFalse(res.hasHeader("Accept-Encoding"));
        res.header("ACCEPT-ENCODING", "deflate");
        Assert.assertEquals("deflate", res.header("Accept-Encoding"));
        Assert.assertEquals("deflate", res.header("accept-Encoding"));
    }

    @Test
    public void headers() {
        Connection con = HttpConnection.connect("http://example.com");
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "text/html");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "http://example.com");
        con.headers(headers);
        Assert.assertEquals("text/html", con.request().header("content-type"));
        Assert.assertEquals("keep-alive", con.request().header("Connection"));
        Assert.assertEquals("http://example.com", con.request().header("Host"));
    }

    @Test
    public void sameHeadersCombineWithComma() {
        Map<String, List<String>> headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("no-cache");
        values.add("no-store");
        headers.put("Cache-Control", values);
        HttpConnection.Response res = new HttpConnection.Response();
        res.processResponseHeaders(headers);
        Assert.assertEquals("no-cache, no-store", res.header("Cache-Control"));
    }

    @Test
    public void multipleHeaders() {
        Connection.Request req = new HttpConnection.Request();
        req.addHeader("Accept", "Something");
        req.addHeader("Accept", "Everything");
        req.addHeader("Foo", "Bar");
        Assert.assertTrue(req.hasHeader("Accept"));
        Assert.assertTrue(req.hasHeader("ACCEpt"));
        Assert.assertEquals("Something, Everything", req.header("accept"));
        Assert.assertTrue(req.hasHeader("fOO"));
        Assert.assertEquals("Bar", req.header("foo"));
        List<String> accept = req.headers("accept");
        Assert.assertEquals(2, accept.size());
        Assert.assertEquals("Something", accept.get(0));
        Assert.assertEquals("Everything", accept.get(1));
        Map<String, List<String>> headers = req.multiHeaders();
        Assert.assertEquals(accept, headers.get("Accept"));
        Assert.assertEquals("Bar", headers.get("Foo").get(0));
        Assert.assertTrue(req.hasHeader("Accept"));
        Assert.assertTrue(req.hasHeaderWithValue("accept", "Something"));
        Assert.assertTrue(req.hasHeaderWithValue("accept", "Everything"));
        Assert.assertFalse(req.hasHeaderWithValue("accept", "Something for nothing"));
        req.removeHeader("accept");
        headers = req.multiHeaders();
        Assert.assertEquals("Bar", headers.get("Foo").get(0));
        Assert.assertFalse(req.hasHeader("Accept"));
        Assert.assertTrue(((headers.get("Accept")) == null));
    }

    @Test
    public void ignoresEmptySetCookies() {
        // prep http response header map
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Set-Cookie", Collections.<String>emptyList());
        HttpConnection.Response res = new HttpConnection.Response();
        res.processResponseHeaders(headers);
        Assert.assertEquals(0, res.cookies().size());
    }

    @Test
    public void ignoresEmptyCookieNameAndVals() {
        // prep http response header map
        Map<String, List<String>> headers = new HashMap<>();
        List<String> cookieStrings = new ArrayList<>();
        cookieStrings.add(null);
        cookieStrings.add("");
        cookieStrings.add("one");
        cookieStrings.add("two=");
        cookieStrings.add("three=;");
        cookieStrings.add("four=data; Domain=.example.com; Path=/");
        headers.put("Set-Cookie", cookieStrings);
        HttpConnection.Response res = new HttpConnection.Response();
        res.processResponseHeaders(headers);
        Assert.assertEquals(4, res.cookies().size());
        Assert.assertEquals("", res.cookie("one"));
        Assert.assertEquals("", res.cookie("two"));
        Assert.assertEquals("", res.cookie("three"));
        Assert.assertEquals("data", res.cookie("four"));
    }

    @Test
    public void connectWithUrl() throws MalformedURLException {
        Connection con = HttpConnection.connect(new URL("http://example.com"));
        Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnMalformedUrl() {
        Connection con = HttpConnection.connect("bzzt");
    }

    @Test
    public void userAgent() {
        Connection con = HttpConnection.connect("http://example.com/");
        Assert.assertEquals(DEFAULT_UA, con.request().header("User-Agent"));
        con.userAgent("Mozilla");
        Assert.assertEquals("Mozilla", con.request().header("User-Agent"));
    }

    @Test
    public void timeout() {
        Connection con = HttpConnection.connect("http://example.com/");
        Assert.assertEquals((30 * 1000), con.request().timeout());
        con.timeout(1000);
        Assert.assertEquals(1000, con.request().timeout());
    }

    @Test
    public void referrer() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.referrer("http://foo.com");
        Assert.assertEquals("http://foo.com", con.request().header("Referer"));
    }

    @Test
    public void method() {
        Connection con = HttpConnection.connect("http://example.com/");
        Assert.assertEquals(GET, con.request().method());
        con.method(POST);
        Assert.assertEquals(POST, con.request().method());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnOddData() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.data("Name", "val", "what");
    }

    @Test
    public void data() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.data("Name", "Val", "Foo", "bar");
        Collection<Connection.KeyVal> values = con.request().data();
        Object[] data = values.toArray();
        Connection.KeyVal one = ((Connection.KeyVal) (data[0]));
        Connection.KeyVal two = ((Connection.KeyVal) (data[1]));
        Assert.assertEquals("Name", one.key());
        Assert.assertEquals("Val", one.value());
        Assert.assertEquals("Foo", two.key());
        Assert.assertEquals("bar", two.value());
    }

    @Test
    public void cookie() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.cookie("Name", "Val");
        Assert.assertEquals("Val", con.request().cookie("Name"));
    }

    @Test
    public void inputStream() {
        Connection.KeyVal kv = HttpConnection.KeyVal.create("file", "thumb.jpg", ParseTest.inputStreamFrom("Check"));
        Assert.assertEquals("file", kv.key());
        Assert.assertEquals("thumb.jpg", kv.value());
        Assert.assertTrue(kv.hasInputStream());
        kv = HttpConnection.KeyVal.create("one", "two");
        Assert.assertEquals("one", kv.key());
        Assert.assertEquals("two", kv.value());
        Assert.assertFalse(kv.hasInputStream());
    }

    @Test
    public void requestBody() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.requestBody("foo");
        Assert.assertEquals("foo", con.request().requestBody());
    }

    @Test
    public void encodeUrl() throws MalformedURLException {
        URL url1 = new URL("http://test.com/?q=white space");
        URL url2 = HttpConnection.encodeUrl(url1);
        Assert.assertEquals("http://test.com/?q=white%20space", url2.toExternalForm());
    }

    @Test
    public void noUrlThrowsValidationError() throws IOException {
        HttpConnection con = new HttpConnection();
        boolean threw = false;
        try {
            con.execute();
        } catch (IllegalArgumentException e) {
            threw = true;
            Assert.assertEquals("URL must be specified to connect", e.getMessage());
        }
        Assert.assertTrue(threw);
    }
}

