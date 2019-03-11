package spark;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;


public class GenericSecureIntegrationTest {
    static SparkTestUtil testUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericSecureIntegrationTest.class);

    @Test
    public void testGetHi() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @Test
    public void testXForwardedFor() throws Exception {
        final String xForwardedFor = "XXX.XXX.XXX.XXX";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-For", xForwardedFor);
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethod("GET", "/ip", null, true, "text/html", headers);
        Assert.assertEquals(xForwardedFor, response.body);
        response = GenericSecureIntegrationTest.testUtil.doMethod("GET", "/ip", null, true, "text/html", null);
        Assert.assertNotEquals(xForwardedFor, response.body);
    }

    @Test
    public void testHiHead() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("HEAD", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("", response.body);
    }

    @Test
    public void testGetHiAfterFilter() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/hi", null);
        Assert.assertTrue(response.headers.get("after").contains("foobar"));
    }

    @Test
    public void testGetRoot() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello Root!", response.body);
    }

    @Test
    public void testEchoParam1() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/shizzy", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: shizzy", response.body);
    }

    @Test
    public void testEchoParam2() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/gunit", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: gunit", response.body);
    }

    @Test
    public void testEchoParamWithMaj() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/paramwithmaj/plop", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: plop", response.body);
    }

    @Test
    public void testUnauthorized() throws Exception {
        SparkTestUtil.UrlResponse urlResponse = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/protected/resource", null);
        Assert.assertTrue(((urlResponse.status) == 401));
    }

    @Test
    public void testNotFound() throws Exception {
        SparkTestUtil.UrlResponse urlResponse = GenericSecureIntegrationTest.testUtil.doMethodSecure("GET", "/no/resource", null);
        Assert.assertTrue(((urlResponse.status) == 404));
    }

    @Test
    public void testPost() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("POST", "/poster", "Fo shizzy");
        GenericSecureIntegrationTest.LOGGER.info(response.body);
        Assert.assertEquals(201, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }

    @Test
    public void testPatch() throws Exception {
        SparkTestUtil.UrlResponse response = GenericSecureIntegrationTest.testUtil.doMethodSecure("PATCH", "/patcher", "Fo shizzy");
        GenericSecureIntegrationTest.LOGGER.info(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }
}

