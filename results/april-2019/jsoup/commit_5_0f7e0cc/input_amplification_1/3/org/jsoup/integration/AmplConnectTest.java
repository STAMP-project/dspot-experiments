package org.jsoup.integration;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.integration.servlets.EchoServlet;
import org.jsoup.integration.servlets.InterruptedServlet;
import org.jsoup.integration.servlets.SlowRider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;
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
    public void multiCookieSet_literalMutationString82388null83996_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_literalMutationString82388__8 = cookies.get("token");
            String o_multiCookieSet_literalMutationString82388__9 = cookies.get("uMid");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
            String o_multiCookieSet_literalMutationString82388__15 = AmplConnectTest.ihVal(null, doc);
            org.junit.Assert.fail("multiCookieSet_literalMutationString82388null83996 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_add82404null83962_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_add82404__8 = cookies.get("token");
            String o_multiCookieSet_add82404__9 = cookies.get("uid");
            Connection o_multiCookieSet_add82404__10 = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies);
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
            String o_multiCookieSet_add82404__17 = AmplConnectTest.ihVal(null, doc);
            org.junit.Assert.fail("multiCookieSet_add82404null83962 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82389_add83731() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_literalMutationString82389__8 = cookies.get("token");
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82389__8);
        String o_multiCookieSet_literalMutationString82389__9 = cookies.get("<id");
        Assert.assertNull(o_multiCookieSet_literalMutationString82389__9);
        Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_literalMutationString82389__15 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_literalMutationString82389__15);
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82389__8);
        Assert.assertNull(o_multiCookieSet_literalMutationString82389__9);
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82383_add83700_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            con.execute();
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_literalMutationString82383__8 = cookies.get("q>qw ");
            String o_multiCookieSet_literalMutationString82383__9 = cookies.get("uid");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
            String o_multiCookieSet_literalMutationString82383__15 = AmplConnectTest.ihVal("Cookie", doc);
            org.junit.Assert.fail("multiCookieSet_literalMutationString82383_add83700 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82380null84000() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_literalMutationString82380__8 = cookies.get("");
        Assert.assertNull(o_multiCookieSet_literalMutationString82380__8);
        String o_multiCookieSet_literalMutationString82380__9 = cookies.get(null);
        Assert.assertNull(o_multiCookieSet_literalMutationString82380__9);
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_literalMutationString82380__15 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_literalMutationString82380__15);
        Assert.assertNull(o_multiCookieSet_literalMutationString82380__8);
        Assert.assertNull(o_multiCookieSet_literalMutationString82380__9);
    }

    @Test(timeout = 10000)
    public void multiCookieSet_add82406null83967_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_add82406__8 = cookies.get("token");
            String o_multiCookieSet_add82406__9 = cookies.get("uid");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(null).get();
            String o_multiCookieSet_add82406__15 = AmplConnectTest.ihVal("Cookie", doc);
            String o_multiCookieSet_add82406__16 = AmplConnectTest.ihVal("Cookie", doc);
            org.junit.Assert.fail("multiCookieSet_add82406null83967 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Cookie map must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82397_failAssert0_add83840_failAssert0() throws IOException {
        try {
            {
                Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
                Connection.Response res = con.execute();
                Map<String, String> cookies = res.cookies();
                cookies.get("token");
                cookies.get("uid");
                Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
                AmplConnectTest.ihVal("Cokie", doc);
                AmplConnectTest.ihVal("Cokie", doc);
                org.junit.Assert.fail("multiCookieSet_literalMutationString82397 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("multiCookieSet_literalMutationString82397_failAssert0_add83840 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82385_literalMutationString82943_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tooQls/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_literalMutationString82385__8 = cookies.get("tpken");
            String o_multiCookieSet_literalMutationString82385__9 = cookies.get("uid");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
            String o_multiCookieSet_literalMutationString82385__15 = AmplConnectTest.ihVal("Cookie", doc);
            org.junit.Assert.fail("multiCookieSet_literalMutationString82385_literalMutationString82943 should have thrown HttpStatusException");
        } catch (HttpStatusException expected) {
            Assert.assertEquals("HTTP error fetching URL", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82383null84037_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_literalMutationString82383__8 = cookies.get("q>qw ");
            String o_multiCookieSet_literalMutationString82383__9 = cookies.get("uid");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(null).get();
            String o_multiCookieSet_literalMutationString82383__15 = AmplConnectTest.ihVal("Cookie", doc);
            org.junit.Assert.fail("multiCookieSet_literalMutationString82383null84037 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Cookie map must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82388_add83641() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_literalMutationString82388__8 = cookies.get("token");
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82388__8);
        String o_multiCookieSet_literalMutationString82388__9 = cookies.get("uMid");
        Assert.assertNull(o_multiCookieSet_literalMutationString82388__9);
        Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_literalMutationString82388__15 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_literalMutationString82388__15);
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82388__8);
        Assert.assertNull(o_multiCookieSet_literalMutationString82388__9);
    }

    @Test(timeout = 10000)
    public void multiCookieSetnull82410_failAssert0null84160_failAssert0() throws IOException {
        try {
            {
                Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
                Connection.Response res = con.execute();
                Map<String, String> cookies = res.cookies();
                cookies.get("token");
                cookies.get("uid");
                Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(null).get();
                AmplConnectTest.ihVal("Cookie", null);
                org.junit.Assert.fail("multiCookieSetnull82410 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("multiCookieSetnull82410_failAssert0null84160 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Cookie map must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82386_add83708() throws IOException {
        Connection o_multiCookieSet_literalMutationString82386_add83708__1 = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).isBlock());
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).hasText());
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).hasParent());
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_literalMutationString82386__8 = cookies.get("token");
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82386__8);
        String o_multiCookieSet_literalMutationString82386__9 = cookies.get("");
        Assert.assertNull(o_multiCookieSet_literalMutationString82386__9);
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_literalMutationString82386__15 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_literalMutationString82386__15);
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).isBlock());
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).hasText());
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82386_add83708__1)).get())).hasParent());
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82386__8);
        Assert.assertNull(o_multiCookieSet_literalMutationString82386__9);
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82387_add83661() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_literalMutationString82387__8 = cookies.get("token");
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82387__8);
        String o_multiCookieSet_literalMutationString82387__9 = cookies.get("Jay");
        Assert.assertNull(o_multiCookieSet_literalMutationString82387__9);
        Connection o_multiCookieSet_literalMutationString82387_add83661__14 = Jsoup.connect(AmplConnectTest.echoUrl);
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).isBlock());
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).hasText());
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).hasParent());
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_literalMutationString82387__15 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_literalMutationString82387__15);
        Assert.assertEquals("asdfg123", o_multiCookieSet_literalMutationString82387__8);
        Assert.assertNull(o_multiCookieSet_literalMutationString82387__9);
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).isBlock());
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).hasText());
        Assert.assertFalse(((Document) (((HttpConnection) (o_multiCookieSet_literalMutationString82387_add83661__14)).get())).hasParent());
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82383_literalMutationString82982_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_literalMutationString82383__8 = cookies.get("q>qw ");
            String o_multiCookieSet_literalMutationString82383__9 = cookies.get("uid");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
            String o_multiCookieSet_literalMutationString82383__15 = AmplConnectTest.ihVal("", doc);
            org.junit.Assert.fail("multiCookieSet_literalMutationString82383_literalMutationString82982 should have thrown Selector$SelectorParseException");
        } catch (Selector.SelectorParseException expected) {
            Assert.assertEquals(":contains(text) query must not be empty", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82381null84047() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_literalMutationString82381__8 = cookies.get(null);
        Assert.assertNull(o_multiCookieSet_literalMutationString82381__8);
        String o_multiCookieSet_literalMutationString82381__9 = cookies.get("uid");
        Assert.assertEquals("jhy", o_multiCookieSet_literalMutationString82381__9);
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_literalMutationString82381__15 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_literalMutationString82381__15);
        Assert.assertNull(o_multiCookieSet_literalMutationString82381__8);
        Assert.assertEquals("jhy", o_multiCookieSet_literalMutationString82381__9);
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82380_literalMutationString82845_failAssert0() throws IOException {
        try {
            Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
            Connection.Response res = con.execute();
            Map<String, String> cookies = res.cookies();
            String o_multiCookieSet_literalMutationString82380__8 = cookies.get("");
            String o_multiCookieSet_literalMutationString82380__9 = cookies.get("uid");
            Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
            String o_multiCookieSet_literalMutationString82380__15 = AmplConnectTest.ihVal("Cooie", doc);
            org.junit.Assert.fail("multiCookieSet_literalMutationString82380_literalMutationString82845 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void multiCookieSet_literalMutationString82381null84048() throws IOException {
        Connection con = Jsoup.connect("http://direct.infohound.net/tools/302-cookie.pl");
        Connection.Response res = con.execute();
        Map<String, String> cookies = res.cookies();
        String o_multiCookieSet_literalMutationString82381__8 = cookies.get("toen");
        Assert.assertNull(o_multiCookieSet_literalMutationString82381__8);
        String o_multiCookieSet_literalMutationString82381__9 = cookies.get(null);
        Assert.assertNull(o_multiCookieSet_literalMutationString82381__9);
        Document doc = Jsoup.connect(AmplConnectTest.echoUrl).cookies(cookies).get();
        String o_multiCookieSet_literalMutationString82381__15 = AmplConnectTest.ihVal("Cookie", doc);
        Assert.assertEquals("token=asdfg123; uid=jhy", o_multiCookieSet_literalMutationString82381__15);
        Assert.assertNull(o_multiCookieSet_literalMutationString82381__8);
        Assert.assertNull(o_multiCookieSet_literalMutationString82381__9);
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_literalMutationNumber138868_failAssert0_add139052_failAssert0() throws IOException {
        try {
            {
                Jsoup.connect(InterruptedServlet.Url).timeout(1);
                Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(1).execute();
                boolean threw = false;
                {
                    Document document = res.parse();
                    document.title();
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138868 should have thrown SocketTimeoutException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138868_failAssert0_add139052 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_literalMutationNumber138868_failAssert0_literalMutationBoolean138972_failAssert0() throws IOException {
        try {
            {
                Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(1).execute();
                boolean threw = true;
                {
                    Document document = res.parse();
                    document.title();
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138868 should have thrown SocketTimeoutException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138868_failAssert0_literalMutationBoolean138972 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }
}

