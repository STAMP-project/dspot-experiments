package spark.servlet;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;


public class ServletTest {
    private static final String SOMEPATH = "/somepath";

    private static final int PORT = 9393;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletTest.class);

    private static SparkTestUtil testUtil;

    @Test
    public void testGetHi() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/hi"), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @Test
    public void testHiHead() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("HEAD", ((ServletTest.SOMEPATH) + "/hi"), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("", response.body);
    }

    @Test
    public void testGetHiAfterFilter() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/hi"), null);
        Assert.assertTrue(response.headers.get("after").contains("foobar"));
    }

    @Test
    public void testGetRoot() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/"), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello Root!", response.body);
    }

    @Test
    public void testEchoParam1() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/shizzy"), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: shizzy", response.body);
    }

    @Test
    public void testEchoParam2() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/gunit"), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: gunit", response.body);
    }

    @Test
    public void testUnauthorized() throws Exception {
        SparkTestUtil.UrlResponse urlResponse = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/protected/resource"), null);
        Assert.assertTrue(((urlResponse.status) == 401));
    }

    @Test
    public void testNotFound() throws Exception {
        SparkTestUtil.UrlResponse urlResponse = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/no/resource"), null);
        Assert.assertTrue(((urlResponse.status) == 404));
    }

    @Test
    public void testPost() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("POST", ((ServletTest.SOMEPATH) + "/poster"), "Fo shizzy");
        Assert.assertEquals(201, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }

    @Test
    public void testStaticResource() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/css/style.css"), null);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains("Content of css file"));
    }

    @Test
    public void testStaticWelcomeResource() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", ((ServletTest.SOMEPATH) + "/pages/"), null);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains("<html><body>Hello Static World!</body></html>"));
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        SparkTestUtil.UrlResponse response = ServletTest.testUtil.doMethod("GET", (((ServletTest.SOMEPATH) + "/") + (MyApp.EXTERNAL_FILE)), null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
    }
}

