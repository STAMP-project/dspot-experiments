package spark;


import org.junit.Assert;
import org.junit.Test;
import spark.util.SparkTestUtil;


public class ResponseWrapperDelegationTest {
    static SparkTestUtil testUtil;

    @Test
    public void filters_can_detect_response_status() throws Exception {
        SparkTestUtil.UrlResponse response = ResponseWrapperDelegationTest.testUtil.get("/204");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("ok", response.body);
    }

    @Test
    public void filters_can_detect_content_type() throws Exception {
        SparkTestUtil.UrlResponse response = ResponseWrapperDelegationTest.testUtil.get("/json");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("{\"status\": \"ok\"}", response.body);
        Assert.assertEquals("text/plain", response.headers.get("Content-Type"));
    }
}

