package spark;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.embeddedserver.jetty.websocket.WebSocketTestClient;
import spark.embeddedserver.jetty.websocket.WebSocketTestHandler;
import spark.examples.exception.JWGmeligMeylingException;
import spark.util.SparkTestUtil;


public class GenericIntegrationTest {
    private static final String NOT_FOUND_BRO = "Not found bro";

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericIntegrationTest.class);

    static SparkTestUtil testUtil;

    static File tmpExternalFile;

    @Test
    public void filters_should_be_accept_type_aware() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/protected/resource", null, "application/json");
        Assert.assertTrue(((response.status) == 401));
        Assert.assertEquals("{\"message\": \"Go Away!\"}", response.body);
    }

    @Test
    public void routes_should_be_accept_type_aware() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/hi", null, "application/json");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("{\"message\": \"Hello World\"}", response.body);
    }

    @Test
    public void template_view_should_be_rendered_with_given_model_view_object() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/templateView", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello from my view", response.body);
    }

    @Test
    public void testGetHi() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @Test
    public void testGetBinaryHi() {
        try {
            SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/binaryhi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetByteBufferHi() {
        try {
            SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/bytebufferhi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetInputStreamHi() {
        try {
            SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/inputstreamhi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHiHead() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("HEAD", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("", response.body);
    }

    @Test
    public void testGetHiAfterFilter() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/hi", null);
        Assert.assertTrue(response.headers.get("after").contains("foobar"));
    }

    @Test
    public void testXForwardedFor() throws Exception {
        final String xForwardedFor = "XXX.XXX.XXX.XXX";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-For", xForwardedFor);
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/ip", null, false, "text/html", headers);
        Assert.assertEquals(xForwardedFor, response.body);
        response = GenericIntegrationTest.testUtil.doMethod("GET", "/ip", null, false, "text/html", null);
        Assert.assertNotEquals(xForwardedFor, response.body);
    }

    @Test
    public void testGetRoot() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello Root!", response.body);
    }

    @Test
    public void testParamAndWild() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/paramandwild/thedude/stuff/andits", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("paramandwild: thedudeandits", response.body);
    }

    @Test
    public void testEchoParam1() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/param/shizzy", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: shizzy", response.body);
    }

    @Test
    public void testEchoParam2() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/param/gunit", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: gunit", response.body);
    }

    @Test
    public void testEchoParam3() throws Exception {
        String polyglot = "?? ? ?";
        String encoded = URIUtil.encodePath(polyglot);
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", ("/param/" + encoded), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(("echo: " + polyglot), response.body);
    }

    @Test
    public void testPathParamsWithPlusSign() throws Exception {
        String pathParamWithPlusSign = "not+broken+path+param";
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", ("/param/" + pathParamWithPlusSign), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(("echo: " + pathParamWithPlusSign), response.body);
    }

    @Test
    public void testParamWithEncodedSlash() throws Exception {
        String polyglot = "te/st";
        String encoded = URLEncoder.encode(polyglot, "UTF-8");
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", ("/param/" + encoded), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(("echo: " + polyglot), response.body);
    }

    @Test
    public void testSplatWithEncodedSlash() throws Exception {
        String param = "fo/shizzle";
        String encodedParam = URLEncoder.encode(param, "UTF-8");
        String splat = "mah/FRIEND";
        String encodedSplat = URLEncoder.encode(splat, "UTF-8");
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", ((("/paramandwild/" + encodedParam) + "/stuff/") + encodedSplat), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals((("paramandwild: " + param) + splat), response.body);
    }

    @Test
    public void testEchoParamWithUpperCaseInValue() throws Exception {
        final String camelCased = "ThisIsAValueAndSparkShouldRetainItsUpperCasedCharacters";
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", ("/param/" + camelCased), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(("echo: " + camelCased), response.body);
    }

    @Test
    public void testTwoRoutesWithDifferentCaseButSameName() throws Exception {
        String lowerCasedRoutePart = "param";
        String upperCasedRoutePart = "PARAM";
        GenericIntegrationTest.registerEchoRoute(lowerCasedRoutePart);
        GenericIntegrationTest.registerEchoRoute(upperCasedRoutePart);
        GenericIntegrationTest.assertEchoRoute(lowerCasedRoutePart);
        GenericIntegrationTest.assertEchoRoute(upperCasedRoutePart);
    }

    @Test
    public void testEchoParamWithMaj() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/paramwithmaj/plop", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: plop", response.body);
    }

    @Test
    public void testUnauthorized() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/secretcontent/whateva", null);
        Assert.assertTrue(((response.status) == 401));
    }

    @Test
    public void testNotFound() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/no/resource", null);
        Assert.assertTrue(((response.status) == 404));
    }

    @Test
    public void testPost() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("POST", "/poster", "Fo shizzy");
        GenericIntegrationTest.LOGGER.info(response.body);
        Assert.assertEquals(201, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }

    @Test
    public void testPostViaGetWithMethodOverrideHeader() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("X-HTTP-Method-Override", "POST");
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/post_via_get", "Fo shizzy", false, "*/*", map);
        System.out.println(response.body);
        Assert.assertEquals(201, response.status);
        Assert.assertTrue(response.body.contains("Method Override Worked"));
    }

    @Test
    public void testPatch() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("PATCH", "/patcher", "Fo shizzy");
        GenericIntegrationTest.LOGGER.info(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }

    @Test
    public void testSessionReset() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/session_reset", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("22222", response.body);
    }

    @Test
    public void testStaticFile() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/css/style.css", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of css file", response.body);
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/externalFile.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
    }

    @Test
    public void testExceptionMapper() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/throwexception", null);
        Assert.assertEquals("Exception handled", response.body);
    }

    @Test
    public void testInheritanceExceptionMapper() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/throwsubclassofbaseexception", null);
        Assert.assertEquals("Exception handled", response.body);
    }

    @Test
    public void testNotFoundExceptionMapper() throws Exception {
        // thrownotfound
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/thrownotfound", null);
        Assert.assertEquals(GenericIntegrationTest.NOT_FOUND_BRO, response.body);
        Assert.assertEquals(404, response.status);
    }

    @Test
    public void testTypedExceptionMapper() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/throwmeyling", null);
        Assert.assertEquals(new JWGmeligMeylingException().trustButVerify(), response.body);
    }

    @Test
    public void testWebSocketConversation() throws Exception {
        String uri = "ws://localhost:4567/ws";
        WebSocketClient client = new WebSocketClient();
        WebSocketTestClient ws = new WebSocketTestClient();
        try {
            client.start();
            client.connect(ws, URI.create(uri), new ClientUpgradeRequest());
            ws.awaitClose(30, TimeUnit.SECONDS);
        } finally {
            client.stop();
        }
        List<String> events = WebSocketTestHandler.events;
        Assert.assertEquals(3, events.size(), 3);
        Assert.assertEquals("onConnect", events.get(0));
        Assert.assertEquals("onMessage: Hi Spark!", events.get(1));
        Assert.assertEquals("onClose: 1000 Bye!", events.get(2));
    }

    @Test
    public void path_should_prefix_routes() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/firstPath/test", null, "application/json");
        Assert.assertTrue(((response.status) == 200));
        Assert.assertEquals("Single path-prefix works", response.body);
        Assert.assertEquals("true", response.headers.get("before-filter-ran"));
    }

    @Test
    public void paths_should_be_nestable() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/firstPath/secondPath/test", null, "application/json");
        Assert.assertTrue(((response.status) == 200));
        Assert.assertEquals("Nested path-prefix works", response.body);
        Assert.assertEquals("true", response.headers.get("before-filter-ran"));
    }

    @Test
    public void paths_should_be_very_nestable() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/firstPath/secondPath/thirdPath/test", null, "application/json");
        Assert.assertTrue(((response.status) == 200));
        Assert.assertEquals("Very nested path-prefix works", response.body);
        Assert.assertEquals("true", response.headers.get("before-filter-ran"));
    }

    @Test
    public void testRuntimeExceptionForDone() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/exception", null);
        Assert.assertEquals("done executed for exception", response.body);
        Assert.assertEquals(500, response.status);
    }

    @Test
    public void testRuntimeExceptionForAllRoutesFinally() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("GET", "/hi", null);
        Assert.assertEquals("foobar", response.headers.get("after"));
        Assert.assertEquals("nice done response after all", response.headers.get("post-process-all"));
        Assert.assertEquals(200, response.status);
    }

    @Test
    public void testPostProcessBodyForFinally() throws Exception {
        SparkTestUtil.UrlResponse response = GenericIntegrationTest.testUtil.doMethod("POST", "/nice", "");
        Assert.assertEquals("nice response", response.body);
        Assert.assertEquals("nice done response", response.headers.get("post-process"));
        Assert.assertEquals(200, response.status);
    }
}

