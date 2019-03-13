package spark;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;


/**
 * Created by Tom on 08/02/2017.
 */
public class ServicePortIntegrationTest {
    private static Service service;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePortIntegrationTest.class);

    @Test
    public void testGetPort_withRandomPort() throws Exception {
        int actualPort = ServicePortIntegrationTest.service.port();
        ServicePortIntegrationTest.LOGGER.info("got port ");
        SparkTestUtil testUtil = new SparkTestUtil(actualPort);
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }
}

