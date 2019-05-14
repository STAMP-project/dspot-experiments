package org.jsoup.integration;


import java.io.IOException;
import java.net.SocketTimeoutException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.integration.servlets.EchoServlet;
import org.jsoup.integration.servlets.InterruptedServlet;
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
    public void bodyAfterParseThrowsValidationError_add162311_failAssert0_add162337_failAssert0_add162400_failAssert0() throws IOException {
        try {
            {
                {
                    Connection.Response res = Jsoup.connect(AmplConnectTest.echoUrl).execute();
                    res.parse();
                    res.parse();
                    res.parse();
                    Document doc = res.parse();
                    String body = res.body();
                    org.junit.Assert.fail("bodyAfterParseThrowsValidationError_add162311 should have thrown IllegalArgumentException");
                }
                org.junit.Assert.fail("bodyAfterParseThrowsValidationError_add162311_failAssert0_add162337 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("bodyAfterParseThrowsValidationError_add162311_failAssert0_add162337_failAssert0_add162400 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Input stream already read and parsed, cannot re-read.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_literalMutationBoolean138219_failAssert0_literalMutationNumber138293_failAssert0() throws IOException {
        try {
            {
                Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(1).execute();
                boolean threw = false;
                {
                    Document document = res.parse();
                    document.title();
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationBoolean138219 should have thrown IOException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationBoolean138219_failAssert0_literalMutationNumber138293 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_literalMutationNumber138213_failAssert0_literalMutationNumber138255_failAssert0() throws IOException {
        try {
            {
                Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(1).execute();
                boolean threw = false;
                {
                    Document document = res.parse();
                    document.title();
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138213 should have thrown SocketTimeoutException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138213_failAssert0_literalMutationNumber138255 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_literalMutationNumber138214_failAssert0_literalMutationNumber138265_failAssert0() throws IOException {
        try {
            {
                Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(1).execute();
                boolean threw = false;
                {
                    Document document = res.parse();
                    document.title();
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138214 should have thrown IOException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber138214_failAssert0_literalMutationNumber138265 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }
}

