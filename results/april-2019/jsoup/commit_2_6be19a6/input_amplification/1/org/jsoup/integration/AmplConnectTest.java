package org.jsoup.integration;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.integration.servlets.Deflateservlet;
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
    public void postFiles_literalMutationString1055_failAssert0() throws IOException {
        try {
            File thumb = ParseTest.getFile("");
            File html = ParseTest.getFile("/htmltests/google-ipod.html");
            Document res = Jsoup.connect(EchoServlet.Url).data("firstname", "Jay").data("firstPart", thumb.getName(), new FileInputStream(thumb), "image/jpeg").data("secondPart", html.getName(), new FileInputStream(html)).data("surname", "Soup").post();
            AmplConnectTest.ihVal("Parts", res);
            AmplConnectTest.ihVal("Part secondPart ContentType", res);
            AmplConnectTest.ihVal("Part secondPart Name", res);
            AmplConnectTest.ihVal("Part secondPart Filename", res);
            AmplConnectTest.ihVal("Part secondPart Size", res);
            AmplConnectTest.ihVal("Part firstPart ContentType", res);
            AmplConnectTest.ihVal("Part firstPart Name", res);
            AmplConnectTest.ihVal("Part firstPart Filename", res);
            AmplConnectTest.ihVal("Part firstPart Size", res);
            AmplConnectTest.ihVal("firstname", res);
            AmplConnectTest.ihVal("surname", res);

            org.junit.Assert.fail("postFiles_literalMutationString1055 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void postFiles_literalMutationString1060_failAssert0() throws IOException {
        try {
            File thumb = ParseTest.getFile("/htmltests/thumb.jpg");
            File html = ParseTest.getFile("");
            Document res = Jsoup.connect(EchoServlet.Url).data("firstname", "Jay").data("firstPart", thumb.getName(), new FileInputStream(thumb), "image/jpeg").data("secondPart", html.getName(), new FileInputStream(html)).data("surname", "Soup").post();
            AmplConnectTest.ihVal("Parts", res);
            AmplConnectTest.ihVal("Part secondPart ContentType", res);
            AmplConnectTest.ihVal("Part secondPart Name", res);
            AmplConnectTest.ihVal("Part secondPart Filename", res);
            AmplConnectTest.ihVal("Part secondPart Size", res);
            AmplConnectTest.ihVal("Part firstPart ContentType", res);
            AmplConnectTest.ihVal("Part firstPart Name", res);
            AmplConnectTest.ihVal("Part firstPart Filename", res);
            AmplConnectTest.ihVal("Part firstPart Size", res);
            AmplConnectTest.ihVal("firstname", res);
            AmplConnectTest.ihVal("surname", res);

            org.junit.Assert.fail("postFiles_literalMutationString1060 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsDeflate_add3281() throws IOException {
        Connection o_supportsDeflate_add3281__1 = Jsoup.connect(Deflateservlet.Url);
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).hasText());
        Assert.assertFalse(((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).isBlock());
        Assert.assertFalse(((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello, World!</p>\n  <p>That should be enough, right?</p>\n  <p>Hello, World!</p>\n  <p>That should be enough, right?</p>\n </body>\n</html>", ((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).toString());
        Connection.Response res = Jsoup.connect(Deflateservlet.Url).execute();
        String o_supportsDeflate_add3281__5 = res.header("Content-Encoding");
        Assert.assertEquals("deflate", o_supportsDeflate_add3281__5);
        Document doc = res.parse();
        String o_supportsDeflate_add3281__8 = doc.selectFirst("p").text();
        Assert.assertEquals("Hello, World!", o_supportsDeflate_add3281__8);
        Assert.assertFalse(((Collection) (((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).hasText());
        Assert.assertFalse(((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).isBlock());
        Assert.assertFalse(((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).hasParent());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Hello, World!</p>\n  <p>That should be enough, right?</p>\n  <p>Hello, World!</p>\n  <p>That should be enough, right?</p>\n </body>\n</html>", ((Document) (((HttpConnection) (o_supportsDeflate_add3281__1)).get())).toString());
        Assert.assertEquals("deflate", o_supportsDeflate_add3281__5);
    }
}

