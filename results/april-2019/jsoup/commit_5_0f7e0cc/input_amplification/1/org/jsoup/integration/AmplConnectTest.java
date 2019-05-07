package org.jsoup.integration;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.integration.servlets.EchoServlet;
import org.jsoup.integration.servlets.SlowRider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class AmplConnectTest {
    private static String echoUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        TestServer.start();
        AmplConnectTest.echoUrl = EchoServlet.Url;
    }

    @org.junit.AfterClass
    public static void tearDown() throws Exception {
        TestServer.stop();
    }

    private static String ihVal(String key, Document doc) {
        return doc.select((("th:contains(" + key) + ") + td")).first().text();
    }

    @Ignore
    @Test
    public void canInterruptBodyStringRead() throws IOException, InterruptedException {
        final String[] body = new String[1];
        Thread runner = new Thread(new Runnable() {
            public void run() {
                try {
                    Connection.Response res = Jsoup.connect(SlowRider.Url).timeout((15 * 1000)).execute();
                    body[0] = res.body();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        runner.start();
        Thread.sleep((1000 * 3));
        runner.interrupt();
        Assert.assertTrue(runner.isInterrupted());
        runner.join();
        Assert.assertTrue(((body[0].length()) > 0));
        Assert.assertTrue(body[0].contains("<p>Are you still there?"));
    }

    @Ignore
    @Test
    public void canInterruptDocumentRead() throws IOException, InterruptedException {
        final String[] body = new String[1];
        Thread runner = new Thread(new Runnable() {
            public void run() {
                try {
                    Connection.Response res = Jsoup.connect(SlowRider.Url).timeout((15 * 1000)).execute();
                    body[0] = res.parse().text();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        runner.start();
        Thread.sleep((1000 * 3));
        runner.interrupt();
        Assert.assertTrue(runner.isInterrupted());
        runner.join();
        Assert.assertTrue(((body[0].length()) == 0));
    }

    @Ignore
    @Test
    public void totalTimeout() throws IOException {
        int timeout = 3 * 1000;
        long start = System.currentTimeMillis();
        boolean threw = false;
        try {
            Jsoup.connect(SlowRider.Url).timeout(timeout).get();
        } catch (SocketTimeoutException e) {
            long end = System.currentTimeMillis();
            long took = end - start;
            Assert.assertTrue(("Time taken was " + took), (took > timeout));
            Assert.assertTrue(("Time taken was " + took), (took < (timeout * 1.2)));
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Ignore
    @Test
    public void slowReadOk() throws IOException {
        Document doc = Jsoup.connect(SlowRider.Url).data(SlowRider.MaxTimeParam, "2000").get();
        Element h1 = doc.selectFirst("h1");
        Assert.assertEquals("outatime", h1.text());
    }

    @Ignore
    @Test
    public void infiniteReadSupported() throws IOException {
        Document doc = Jsoup.connect(SlowRider.Url).timeout(0).data(SlowRider.MaxTimeParam, "2000").get();
        Element h1 = doc.selectFirst("h1");
        Assert.assertEquals("outatime", h1.text());
    }

    @Test(timeout = 10000)
    public void multiCookieSet_add1220() throws IOException {
        Connection o_multiCookieSet_add1220__1 = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).isBlock());
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).hasText());
        Assert.assertEquals("<html>\n <head>\n  <title>CGI Environment Variables</title> \n  <style type=\"text/css\">\n      body, td, th {font: 10pt Verdana, Arial, sans-serif; text-align: left}\n      th {font-weight: bold}        \n    </style> \n </head> \n <body> \n  <table border=\"0\"> \n   <tbody>\n    <tr> \n     <th>CONTENT_LENGTH</th> \n     <td></td> \n    </tr> \n    <tr> \n     <th>CONTENT_TYPE</th> \n     <td></td> \n    </tr> \n    <tr> \n     <th>DAEMON_OPTS</th> \n     <td>-f</td> \n    </tr> \n    <tr> \n     <th>DOCUMENT_ROOT</th> \n     <td>/www/infohound</td> \n    </tr> \n    <tr> \n     <th>DOCUMENT_URI</th> \n     <td>/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>FCGI_ROLE</th> \n     <td>RESPONDER</td> \n    </tr> \n    <tr> \n     <th>GATEWAY_INTERFACE</th> \n     <td>CGI/1.1</td> \n    </tr> \n    <tr> \n     <th>HOME</th> \n     <td>/var/www</td> \n    </tr> \n    <tr> \n     <th>HTTP_ACCEPT</th> \n     <td>text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2</td> \n    </tr> \n    <tr> \n     <th>HTTP_ACCEPT_ENCODING</th> \n     <td>gzip</td> \n    </tr> \n    <tr> \n     <th>HTTP_CACHE_CONTROL</th> \n     <td>max-age=259200</td> \n    </tr> \n    <tr> \n     <th>HTTP_CONNECTION</th> \n     <td>keep-alive</td> \n    </tr> \n    <tr> \n     <th>HTTP_COOKIE</th> \n     <td>token=asdfg123; uid=jhy</td> \n    </tr> \n    <tr> \n     <th>HTTP_HOST</th> \n     <td>direct.infohound.net</td> \n    </tr> \n    <tr> \n     <th>HTTP_USER_AGENT</th> \n     <td>Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36</td> \n    </tr> \n    <tr> \n     <th>HTTP_VIA</th> \n     <td>1.1 gwol-north.grid5000.fr (squid/3.5.23)</td> \n    </tr> \n    <tr> \n     <th>HTTP_X_FORWARDED_FOR</th> \n     <td>172.16.39.4</td> \n    </tr> \n    <tr> \n     <th>LANG</th> \n     <td>en_US.UTF-8</td> \n    </tr> \n    <tr> \n     <th>LOGNAME</th> \n     <td>www-data</td> \n    </tr> \n    <tr> \n     <th>PATH</th> \n     <td>/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin</td> \n    </tr> \n    <tr> \n     <th>QUERY_STRING</th> \n     <td></td> \n    </tr> \n    <tr> \n     <th>REDIRECT_STATUS</th> \n     <td>200</td> \n    </tr> \n    <tr> \n     <th>REMOTE_ADDR</th> \n     <td>193.51.183.177</td> \n    </tr> \n    <tr> \n     <th>REMOTE_PORT</th> \n     <td>55986</td> \n    </tr> \n    <tr> \n     <th>REQUEST_METHOD</th> \n     <td>GET</td> \n    </tr> \n    <tr> \n     <th>REQUEST_SCHEME</th> \n     <td>http</td> \n    </tr> \n    <tr> \n     <th>REQUEST_URI</th> \n     <td>/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>SCRIPT_FILENAME</th> \n     <td>/www/infohound/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>SCRIPT_NAME</th> \n     <td>/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>SERVER_ADDR</th> \n     <td>45.79.72.37</td> \n    </tr> \n    <tr> \n     <th>SERVER_NAME</th> \n     <td>infohound.net</td> \n    </tr> \n    <tr> \n     <th>SERVER_PORT</th> \n     <td>80</td> \n    </tr> \n    <tr> \n     <th>SERVER_PROTOCOL</th> \n     <td>HTTP/1.1</td> \n    </tr> \n    <tr> \n     <th>SERVER_SOFTWARE</th> \n     <td>nginx/1.10.3</td> \n    </tr> \n    <tr> \n     <th>SHELL</th> \n     <td>/usr/sbin/nologin</td> \n    </tr> \n    <tr> \n     <th>USER</th> \n     <td>www-data</td> \n    </tr> \n    <tr>\n     <td colspan=\"2\">&nbsp;</td>\n    </tr> \n   </tbody>\n  </table>\n  <br>\n  <br> \n  <p><small>\u00a9 2004 <a href=\"http://jon.hedley.net/\">Jonathan Hedley</a></small></p> \n  <script>\n  (function(i,s,o,g,r,a,m){i[\'GoogleAnalyticsObject\']=r;i[r]=i[r]||function(){\n  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n  })(window,document,\'script\',\'//www.google-analytics.com/analytics.js\',\'ga\');\n\n  ga(\'create\', \'UA-89734-4\', \'auto\');\n  ga(\'require\', \'displayfeatures\');\n  ga(\'send\', \'pageview\');\n\n</script>   \n </body>\n</html>", ((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).toString());
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).hasParent());
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_add1220__9 = cookies.get("token");
        Assert.assertEquals("asdfg123", o_multiCookieSet_add1220__9);
        String o_multiCookieSet_add1220__10 = cookies.get("uid");
        Assert.assertEquals("jhy", o_multiCookieSet_add1220__10);
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_add1220__16 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_add1220__16);
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).isBlock());
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).hasText());
        Assert.assertEquals("<html>\n <head>\n  <title>CGI Environment Variables</title> \n  <style type=\"text/css\">\n      body, td, th {font: 10pt Verdana, Arial, sans-serif; text-align: left}\n      th {font-weight: bold}        \n    </style> \n </head> \n <body> \n  <table border=\"0\"> \n   <tbody>\n    <tr> \n     <th>CONTENT_LENGTH</th> \n     <td></td> \n    </tr> \n    <tr> \n     <th>CONTENT_TYPE</th> \n     <td></td> \n    </tr> \n    <tr> \n     <th>DAEMON_OPTS</th> \n     <td>-f</td> \n    </tr> \n    <tr> \n     <th>DOCUMENT_ROOT</th> \n     <td>/www/infohound</td> \n    </tr> \n    <tr> \n     <th>DOCUMENT_URI</th> \n     <td>/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>FCGI_ROLE</th> \n     <td>RESPONDER</td> \n    </tr> \n    <tr> \n     <th>GATEWAY_INTERFACE</th> \n     <td>CGI/1.1</td> \n    </tr> \n    <tr> \n     <th>HOME</th> \n     <td>/var/www</td> \n    </tr> \n    <tr> \n     <th>HTTP_ACCEPT</th> \n     <td>text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2</td> \n    </tr> \n    <tr> \n     <th>HTTP_ACCEPT_ENCODING</th> \n     <td>gzip</td> \n    </tr> \n    <tr> \n     <th>HTTP_CACHE_CONTROL</th> \n     <td>max-age=259200</td> \n    </tr> \n    <tr> \n     <th>HTTP_CONNECTION</th> \n     <td>keep-alive</td> \n    </tr> \n    <tr> \n     <th>HTTP_COOKIE</th> \n     <td>token=asdfg123; uid=jhy</td> \n    </tr> \n    <tr> \n     <th>HTTP_HOST</th> \n     <td>direct.infohound.net</td> \n    </tr> \n    <tr> \n     <th>HTTP_USER_AGENT</th> \n     <td>Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36</td> \n    </tr> \n    <tr> \n     <th>HTTP_VIA</th> \n     <td>1.1 gwol-north.grid5000.fr (squid/3.5.23)</td> \n    </tr> \n    <tr> \n     <th>HTTP_X_FORWARDED_FOR</th> \n     <td>172.16.39.4</td> \n    </tr> \n    <tr> \n     <th>LANG</th> \n     <td>en_US.UTF-8</td> \n    </tr> \n    <tr> \n     <th>LOGNAME</th> \n     <td>www-data</td> \n    </tr> \n    <tr> \n     <th>PATH</th> \n     <td>/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin</td> \n    </tr> \n    <tr> \n     <th>QUERY_STRING</th> \n     <td></td> \n    </tr> \n    <tr> \n     <th>REDIRECT_STATUS</th> \n     <td>200</td> \n    </tr> \n    <tr> \n     <th>REMOTE_ADDR</th> \n     <td>193.51.183.177</td> \n    </tr> \n    <tr> \n     <th>REMOTE_PORT</th> \n     <td>55986</td> \n    </tr> \n    <tr> \n     <th>REQUEST_METHOD</th> \n     <td>GET</td> \n    </tr> \n    <tr> \n     <th>REQUEST_SCHEME</th> \n     <td>http</td> \n    </tr> \n    <tr> \n     <th>REQUEST_URI</th> \n     <td>/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>SCRIPT_FILENAME</th> \n     <td>/www/infohound/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>SCRIPT_NAME</th> \n     <td>/tools/q.pl</td> \n    </tr> \n    <tr> \n     <th>SERVER_ADDR</th> \n     <td>45.79.72.37</td> \n    </tr> \n    <tr> \n     <th>SERVER_NAME</th> \n     <td>infohound.net</td> \n    </tr> \n    <tr> \n     <th>SERVER_PORT</th> \n     <td>80</td> \n    </tr> \n    <tr> \n     <th>SERVER_PROTOCOL</th> \n     <td>HTTP/1.1</td> \n    </tr> \n    <tr> \n     <th>SERVER_SOFTWARE</th> \n     <td>nginx/1.10.3</td> \n    </tr> \n    <tr> \n     <th>SHELL</th> \n     <td>/usr/sbin/nologin</td> \n    </tr> \n    <tr> \n     <th>USER</th> \n     <td>www-data</td> \n    </tr> \n    <tr>\n     <td colspan=\"2\">&nbsp;</td>\n    </tr> \n   </tbody>\n  </table>\n  <br>\n  <br> \n  <p><small>\u00a9 2004 <a href=\"http://jon.hedley.net/\">Jonathan Hedley</a></small></p> \n  <script>\n  (function(i,s,o,g,r,a,m){i[\'GoogleAnalyticsObject\']=r;i[r]=i[r]||function(){\n  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n  })(window,document,\'script\',\'//www.google-analytics.com/analytics.js\',\'ga\');\n\n  ga(\'create\', \'UA-89734-4\', \'auto\');\n  ga(\'require\', \'displayfeatures\');\n  ga(\'send\', \'pageview\');\n\n</script>   \n </body>\n</html>", ((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).toString());
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_add1220__1)).get())).hasParent());
        Assert.assertEquals("asdfg123", o_multiCookieSet_add1220__9);
        Assert.assertEquals("jhy", o_multiCookieSet_add1220__10);
    }
}

