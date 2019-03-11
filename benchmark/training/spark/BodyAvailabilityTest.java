package spark;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;


public class BodyAvailabilityTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BodyAvailabilityTest.class);

    private static final String BODY_CONTENT = "the body content";

    private static SparkTestUtil testUtil;

    private static String beforeBody = null;

    private static String routeBody = null;

    private static String afterBody = null;

    @Test
    public void testPost() throws Exception {
        SparkTestUtil.UrlResponse response = BodyAvailabilityTest.testUtil.doMethod("POST", "/hello", BodyAvailabilityTest.BODY_CONTENT);
        BodyAvailabilityTest.LOGGER.info(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains(BodyAvailabilityTest.BODY_CONTENT));
        Assert.assertEquals(BodyAvailabilityTest.BODY_CONTENT, BodyAvailabilityTest.beforeBody);
        Assert.assertEquals(BodyAvailabilityTest.BODY_CONTENT, BodyAvailabilityTest.routeBody);
        Assert.assertEquals(BodyAvailabilityTest.BODY_CONTENT, BodyAvailabilityTest.afterBody);
    }
}

