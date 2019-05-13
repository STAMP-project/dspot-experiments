package org.jsoup.integration;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
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
    public static void setUp() {
        TestServer.start();
        AmplConnectTest.echoUrl = EchoServlet.Url;
    }

    @org.junit.AfterClass
    public static void tearDown() {
        TestServer.stop();
    }

    private static String ihVal(String key, Document doc) {
        final Element first = doc.select((("th:contains(" + key) + ") + td")).first();
        return first != null ? first.text() : null;
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
    public void multiCookieSet_literalMutationString90232null91791_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_literalMutationString90232__8 = cookies.get("token");
            String o_multiCookieSet_literalMutationString90232__9 = cookies.get("=#(");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(null).get();
            String o_multiCookieSet_literalMutationString90232__15 = AmplConnectTest.ihVal("Cookie", doc);
            org.junit.Assert.fail("multiCookieSet_literalMutationString90232null91791 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Cookie map must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_add90247_add91518() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_add90247_add91518__8 = cookies.get("token");
        Assert.assertEquals("asdfg123", o_multiCookieSet_add90247_add91518__8);
        String o_multiCookieSet_add90247__8 = cookies.get("token");
        Assert.assertEquals("asdfg123", o_multiCookieSet_add90247__8);
        String o_multiCookieSet_add90247__9 = cookies.get("uid");
        Assert.assertEquals("jhy", o_multiCookieSet_add90247__9);
        String o_multiCookieSet_add90247__10 = cookies.get("uid");
        Assert.assertEquals("jhy", o_multiCookieSet_add90247__10);
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_add90247__16 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_add90247__16);
        Assert.assertEquals("asdfg123", o_multiCookieSet_add90247_add91518__8);
        Assert.assertEquals("asdfg123", o_multiCookieSet_add90247__8);
        Assert.assertEquals("jhy", o_multiCookieSet_add90247__9);
        Assert.assertEquals("jhy", o_multiCookieSet_add90247__10);
    }
}

