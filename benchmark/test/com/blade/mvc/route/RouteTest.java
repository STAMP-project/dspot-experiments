package com.blade.mvc.route;


import HttpMethod.DELETE;
import com.blade.BaseTestCase;
import com.blade.mvc.http.HttpMethod;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class RouteTest extends BaseTestCase {
    private Route route;

    @Test
    public void testBuildRoute() {
        route.toString();
        Route route2 = new Route(HttpMethod.GET, "/", null, null);
        Assert.assertNotEquals(route, route2);
    }

    @Test
    public void testSort() {
        Assert.assertEquals(Integer.MAX_VALUE, route.getSort());
        route.setSort(20);
        Assert.assertEquals(20, route.getSort());
    }

    @Test
    public void testPath() {
        Assert.assertEquals(null, route.getPath());
        route.setPath("/a");
        Assert.assertEquals("/a", route.getPath());
    }

    @Test
    public void testHttpMethod() {
        Assert.assertEquals(null, route.getHttpMethod());
        route = new Route(HttpMethod.DELETE, "/", null, null);
        Assert.assertEquals(DELETE, route.getHttpMethod());
    }

    @Test
    public void testPathParams() {
        Assert.assertEquals(0, route.getPathParams().size());
        Map<String, String> map = new HashMap<>();
        map.put("name", "jack");
        map.put("age", "22");
        route.setPathParams(map);
        Assert.assertEquals(2, route.getPathParams().size());
        Assert.assertEquals("jack", route.getPathParams().get("name"));
    }

    @Test
    public void testRobotsRequest() throws Exception {
        // start(app.get("/:id", (req, res) -> req.pathInt("id")));
        // String body = bodyToString("/robots.txt");
        // System.out.println(body);
    }
}

