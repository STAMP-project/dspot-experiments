package spark;


import org.junit.Assert;
import org.junit.Test;
import spark.util.SparkTestUtil;


public class FilterTest {
    static SparkTestUtil testUtil;

    @Test
    public void testJustFilter() throws Exception {
        SparkTestUtil.UrlResponse response = FilterTest.testUtil.doMethod("GET", "/justfilter", null);
        System.out.println(("response.status = " + (response.status)));
        Assert.assertEquals(404, response.status);
    }
}

