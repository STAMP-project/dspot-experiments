package spark.customerrorpages;


import CustomErrorPages.INTERNAL_ERROR;
import org.junit.Assert;
import org.junit.Test;
import spark.util.SparkTestUtil;


public class CustomErrorPagesTest {
    private static final String CUSTOM_NOT_FOUND = "custom not found 404";

    private static final String CUSTOM_INTERNAL = "custom internal 500";

    private static final String HELLO_WORLD = "hello world!";

    public static final String APPLICATION_JSON = "application/json";

    private static final String QUERY_PARAM_KEY = "qparkey";

    static SparkTestUtil testUtil;

    @Test
    public void testGetHi() throws Exception {
        SparkTestUtil.UrlResponse response = CustomErrorPagesTest.testUtil.doMethod("GET", "/hello", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(CustomErrorPagesTest.HELLO_WORLD, response.body);
    }

    @Test
    public void testCustomNotFound() throws Exception {
        SparkTestUtil.UrlResponse response = CustomErrorPagesTest.testUtil.doMethod("GET", "/othernotmapped", null);
        Assert.assertEquals(404, response.status);
        Assert.assertEquals(CustomErrorPagesTest.CUSTOM_NOT_FOUND, response.body);
    }

    @Test
    public void testCustomInternal() throws Exception {
        SparkTestUtil.UrlResponse response = CustomErrorPagesTest.testUtil.doMethod("GET", "/raiseinternal", null);
        Assert.assertEquals(500, response.status);
        Assert.assertEquals(CustomErrorPagesTest.APPLICATION_JSON, response.headers.get("Content-Type"));
        Assert.assertEquals(CustomErrorPagesTest.CUSTOM_INTERNAL, response.body);
    }

    @Test
    public void testCustomInternalFailingRoute() throws Exception {
        SparkTestUtil.UrlResponse response = CustomErrorPagesTest.testUtil.doMethod("GET", (("/raiseinternal?" + (CustomErrorPagesTest.QUERY_PARAM_KEY)) + "=sumthin"), null);
        Assert.assertEquals(500, response.status);
        Assert.assertEquals(INTERNAL_ERROR, response.body);
    }
}

