package org.jsoup.integration;


import Connection.Method.PUT;
import Connection.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.helper.HttpConnection;
import org.jsoup.integration.servlets.Deflateservlet;
import org.jsoup.integration.servlets.EchoServlet;
import org.jsoup.integration.servlets.HelloServlet;
import org.jsoup.integration.servlets.InterruptedServlet;
import org.jsoup.integration.servlets.RedirectServlet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests Jsoup.connect against a local server.
 */
public class ConnectTest {
    private static String echoUrl;

    @Test
    public void canConnectToLocalServer() throws IOException {
        String url = HelloServlet.Url;
        Document doc = Jsoup.connect(url).get();
        Element p = doc.selectFirst("p");
        Assert.assertEquals("Hello, World!", p.text());
    }

    @Test
    public void fetchURl() throws IOException {
        Document doc = Jsoup.parse(new URL(ConnectTest.echoUrl), (10 * 1000));
        Assert.assertTrue(doc.title().contains("Environment Variables"));
    }

    @Test
    public void fetchURIWithWihtespace() throws IOException {
        Connection con = Jsoup.connect(((ConnectTest.echoUrl) + "#with whitespaces"));
        Document doc = con.get();
        Assert.assertTrue(doc.title().contains("Environment Variables"));
    }

    @Test
    public void exceptOnUnsupportedProtocol() {
        String url = "file://etc/passwd";
        boolean threw = false;
        try {
            Document doc = Jsoup.connect(url).get();
        } catch (MalformedURLException e) {
            threw = true;
            Assert.assertEquals("java.net.MalformedURLException: Only http & https protocols supported", e.toString());
        } catch (IOException e) {
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void doesPost() throws IOException {
        Document doc = Jsoup.connect(ConnectTest.echoUrl).data("uname", "Jsoup", "uname", "Jonathan", "?", "???").cookie("auth", "token").post();
        Assert.assertEquals("POST", ConnectTest.ihVal("Method", doc));
        Assert.assertEquals("gzip", ConnectTest.ihVal("Accept-Encoding", doc));
        Assert.assertEquals("auth=token", ConnectTest.ihVal("Cookie", doc));
        Assert.assertEquals("???", ConnectTest.ihVal("?", doc));
        Assert.assertEquals("Jsoup, Jonathan", ConnectTest.ihVal("uname", doc));
        Assert.assertEquals("application/x-www-form-urlencoded; charset=UTF-8", ConnectTest.ihVal("Content-Type", doc));
    }

    @Test
    public void doesPostMultipartWithoutInputstream() throws IOException {
        Document doc = Jsoup.connect(ConnectTest.echoUrl).header(HttpConnection.CONTENT_TYPE, HttpConnection.MULTIPART_FORM_DATA).userAgent(UrlConnectTest.browserUa).data("uname", "Jsoup", "uname", "Jonathan", "?", "???").post();
        Assert.assertTrue(ConnectTest.ihVal("Content-Type", doc).contains(HttpConnection.MULTIPART_FORM_DATA));
        Assert.assertTrue(ConnectTest.ihVal("Content-Type", doc).contains("boundary"));// should be automatically set

        Assert.assertEquals("Jsoup, Jonathan", ConnectTest.ihVal("uname", doc));
        Assert.assertEquals("???", ConnectTest.ihVal("?", doc));
    }

    @Test
    public void sendsRequestBodyJsonWithData() throws IOException {
        final String body = "{key:value}";
        Document doc = Jsoup.connect(ConnectTest.echoUrl).requestBody(body).header("Content-Type", "application/json").userAgent(UrlConnectTest.browserUa).data("foo", "true").post();
        Assert.assertEquals("POST", ConnectTest.ihVal("Method", doc));
        Assert.assertEquals("application/json", ConnectTest.ihVal("Content-Type", doc));
        Assert.assertEquals("foo=true", ConnectTest.ihVal("Query String", doc));
        Assert.assertEquals(body, ConnectTest.ihVal("Post Data", doc));
    }

    @Test
    public void sendsRequestBodyJsonWithoutData() throws IOException {
        final String body = "{key:value}";
        Document doc = Jsoup.connect(ConnectTest.echoUrl).requestBody(body).header("Content-Type", "application/json").userAgent(UrlConnectTest.browserUa).post();
        Assert.assertEquals("POST", ConnectTest.ihVal("Method", doc));
        Assert.assertEquals("application/json", ConnectTest.ihVal("Content-Type", doc));
        Assert.assertEquals(body, ConnectTest.ihVal("Post Data", doc));
    }

    @Test
    public void sendsRequestBody() throws IOException {
        final String body = "{key:value}";
        Document doc = Jsoup.connect(ConnectTest.echoUrl).requestBody(body).header("Content-Type", "text/plain").userAgent(UrlConnectTest.browserUa).post();
        Assert.assertEquals("POST", ConnectTest.ihVal("Method", doc));
        Assert.assertEquals("text/plain", ConnectTest.ihVal("Content-Type", doc));
        Assert.assertEquals(body, ConnectTest.ihVal("Post Data", doc));
    }

    @Test
    public void sendsRequestBodyWithUrlParams() throws IOException {
        final String body = "{key:value}";
        Document doc = // todo - if user sets content-type, we should append postcharset
        Jsoup.connect(ConnectTest.echoUrl).requestBody(body).data("uname", "Jsoup", "uname", "Jonathan", "?", "???").header("Content-Type", "text/plain").userAgent(UrlConnectTest.browserUa).post();
        Assert.assertEquals("POST", ConnectTest.ihVal("Method", doc));
        Assert.assertEquals("uname=Jsoup&uname=Jonathan&%E7%99%BE=%E5%BA%A6%E4%B8%80%E4%B8%8B", ConnectTest.ihVal("Query String", doc));
        Assert.assertEquals(body, ConnectTest.ihVal("Post Data", doc));
    }

    @Test
    public void doesGet() throws IOException {
        Connection con = Jsoup.connect(((ConnectTest.echoUrl) + "?what=the")).userAgent("Mozilla").referrer("http://example.com").data("what", "about & me?");
        Document doc = con.get();
        Assert.assertEquals("what=the&what=about+%26+me%3F", ConnectTest.ihVal("Query String", doc));
        Assert.assertEquals("the, about & me?", ConnectTest.ihVal("what", doc));
        Assert.assertEquals("Mozilla", ConnectTest.ihVal("User-Agent", doc));
        Assert.assertEquals("http://example.com", ConnectTest.ihVal("Referer", doc));
    }

    @Test
    public void doesPut() throws IOException {
        Connection.Response res = Jsoup.connect(ConnectTest.echoUrl).data("uname", "Jsoup", "uname", "Jonathan", "?", "???").cookie("auth", "token").method(PUT).execute();
        Document doc = res.parse();
        Assert.assertEquals("PUT", ConnectTest.ihVal("Method", doc));
        Assert.assertEquals("gzip", ConnectTest.ihVal("Accept-Encoding", doc));
        Assert.assertEquals("auth=token", ConnectTest.ihVal("Cookie", doc));
    }

    /**
     * Tests upload of content to a remote service.
     */
    @Test
    public void postFiles() throws IOException {
        File thumb = ParseTest.getFile("/htmltests/thumb.jpg");
        File html = ParseTest.getFile("/htmltests/google-ipod.html");
        Document res = // defaults to "application-octetstream";
        Jsoup.connect(EchoServlet.Url).data("firstname", "Jay").data("firstPart", thumb.getName(), new FileInputStream(thumb), "image/jpeg").data("secondPart", html.getName(), new FileInputStream(html)).data("surname", "Soup").post();
        Assert.assertEquals("4", ConnectTest.ihVal("Parts", res));
        Assert.assertEquals("application/octet-stream", ConnectTest.ihVal("Part secondPart ContentType", res));
        Assert.assertEquals("secondPart", ConnectTest.ihVal("Part secondPart Name", res));
        Assert.assertEquals("google-ipod.html", ConnectTest.ihVal("Part secondPart Filename", res));
        Assert.assertEquals("43963", ConnectTest.ihVal("Part secondPart Size", res));
        Assert.assertEquals("image/jpeg", ConnectTest.ihVal("Part firstPart ContentType", res));
        Assert.assertEquals("firstPart", ConnectTest.ihVal("Part firstPart Name", res));
        Assert.assertEquals("thumb.jpg", ConnectTest.ihVal("Part firstPart Filename", res));
        Assert.assertEquals("1052", ConnectTest.ihVal("Part firstPart Size", res));
        Assert.assertEquals("Jay", ConnectTest.ihVal("firstname", res));
        Assert.assertEquals("Soup", ConnectTest.ihVal("surname", res));
        /* <tr><th>Part secondPart ContentType</th><td>application/octet-stream</td></tr>
        <tr><th>Part secondPart Name</th><td>secondPart</td></tr>
        <tr><th>Part secondPart Filename</th><td>google-ipod.html</td></tr>
        <tr><th>Part secondPart Size</th><td>43972</td></tr>
        <tr><th>Part firstPart ContentType</th><td>image/jpeg</td></tr>
        <tr><th>Part firstPart Name</th><td>firstPart</td></tr>
        <tr><th>Part firstPart Filename</th><td>thumb.jpg</td></tr>
        <tr><th>Part firstPart Size</th><td>1052</td></tr>
         */
    }

    @Test
    public void multipleParsesOkAfterBufferUp() throws IOException {
        Connection.Response res = Jsoup.connect(ConnectTest.echoUrl).execute().bufferUp();
        Document doc = res.parse();
        Assert.assertTrue(doc.title().contains("Environment"));
        Document doc2 = res.parse();
        Assert.assertTrue(doc2.title().contains("Environment"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bodyAfterParseThrowsValidationError() throws IOException {
        Connection.Response res = Jsoup.connect(ConnectTest.echoUrl).execute();
        Document doc = res.parse();
        String body = res.body();
    }

    @Test
    public void bodyAndBytesAvailableBeforeParse() throws IOException {
        Connection.Response res = Jsoup.connect(ConnectTest.echoUrl).execute();
        String body = res.body();
        Assert.assertTrue(body.contains("Environment"));
        byte[] bytes = res.bodyAsBytes();
        Assert.assertTrue(((bytes.length) > 100));
        Document doc = res.parse();
        Assert.assertTrue(doc.title().contains("Environment"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseParseThrowsValidates() throws IOException {
        Connection.Response res = Jsoup.connect(ConnectTest.echoUrl).execute();
        Document doc = res.parse();
        Assert.assertTrue(doc.title().contains("Environment"));
        Document doc2 = res.parse();// should blow up because the response input stream has been drained

    }

    @Test
    public void multiCookieSet() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        // test cookies set by redirect:
        Map<String, String> cookies = res.cookies();
        Assert.assertEquals("asdfg123", cookies.get("token"));
        Assert.assertEquals("jhy", cookies.get("uid"));
        // send those cookies into the echo URL by map:
        Document doc = Jsoup.connect(ConnectTest.echoUrl).cookies(cookies).get();
        Assert.assertEquals("token=asdfg123; uid=jhy", ConnectTest.ihVal("Cookie", doc));
    }

    @Test
    public void supportsDeflate() throws IOException {
        Connection.Response res = Jsoup.connect(Deflateservlet.Url).execute();
        Assert.assertEquals("deflate", res.header("Content-Encoding"));
        Document doc = res.parse();
        Assert.assertEquals("Hello, World!", doc.selectFirst("p").text());
    }

    @Test
    public void handlesEmptyStreamDuringParseRead() throws IOException {
        // this handles situations where the remote server sets a content length greater than it actually writes
        Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(200).execute();
        boolean threw = false;
        try {
            Document document = res.parse();
            Assert.assertEquals("Something", document.title());
        } catch (IOException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void handlesEmtpyStreamDuringBufferdRead() throws IOException {
        Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(200).execute();
        boolean threw = false;
        try {
            res.bufferUp();
        } catch (UncheckedIOException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void handlesRedirect() throws IOException {
        Document doc = Jsoup.connect(RedirectServlet.Url).data(RedirectServlet.LocationParam, HelloServlet.Url).get();
        Element p = doc.selectFirst("p");
        Assert.assertEquals("Hello, World!", p.text());
        Assert.assertEquals(HelloServlet.Url, doc.location());
    }

    @Test
    public void handlesEmptyRedirect() throws IOException {
        boolean threw = false;
        try {
            Connection.Response res = Jsoup.connect(RedirectServlet.Url).execute();
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Too many redirects"));
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void doesNotPostFor302() throws IOException {
        final Document doc = Jsoup.connect(RedirectServlet.Url).data("Hello", "there").data(RedirectServlet.LocationParam, EchoServlet.Url).post();
        Assert.assertEquals(EchoServlet.Url, doc.location());
        Assert.assertEquals("GET", ConnectTest.ihVal("Method", doc));
        Assert.assertNull(ConnectTest.ihVal("Hello", doc));// data not sent

    }

    @Test
    public void doesPostFor307() throws IOException {
        final Document doc = Jsoup.connect(RedirectServlet.Url).data("Hello", "there").data(RedirectServlet.LocationParam, EchoServlet.Url).data(RedirectServlet.CodeParam, "307").post();
        Assert.assertEquals(EchoServlet.Url, doc.location());
        Assert.assertEquals("POST", ConnectTest.ihVal("Method", doc));
        Assert.assertEquals("there", ConnectTest.ihVal("Hello", doc));
    }
}

