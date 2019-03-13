package spark;


import org.junit.Assert;
import org.junit.Test;
import spark.util.SparkTestUtil;


/**
 * Basic test to ensure that multiple before and after filters can be mapped to a route.
 */
public class MultipleFiltersTest {
    private static SparkTestUtil http;

    @Test
    public void testMultipleFilters() {
        try {
            SparkTestUtil.UrlResponse response = MultipleFiltersTest.http.get("/user");
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Kevin", response.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Filter loadUser = ( request, response) -> {
        spark.User u = new spark.User();
        u.name("Kevin");
        request.attribute("user", u);
    };

    private static Filter initializeCounter = ( request, response) -> request.attribute("counter", 0);

    private static Filter incrementCounter = ( request, response) -> {
        int counter = request.attribute("counter");
        counter++;
        request.attribute("counter", counter);
    };

    private static class User {
        private String name;

        public String name() {
            return name;
        }

        public void name(String name) {
            this.name = name;
        }
    }
}

