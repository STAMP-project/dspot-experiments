package com.blade.mvc.route;


import HttpMethod.BEFORE;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.POST;
import com.blade.mvc.handler.RouteHandler;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class RouteMatcherTest {
    private RouteMatcher routeMatcher;

    @Test
    public void testRouteMatcher() throws Exception {
        routeMatcher.addRoute("/", ( ctx) -> ctx.text("Ok"), GET);
        routeMatcher.addRoute("/*", ( ctx) -> ctx.text("Ok"), BEFORE);
        routeMatcher.register();
        Route route = routeMatcher.lookupRoute("GET", "/");
        Assert.assertEquals("GET\t/", route.toString());
        List<Route> routes = routeMatcher.getBefore("/");
        Assert.assertEquals(1, routes.size());
    }

    @Test
    public void testPathRegex() {
        Pattern PATH_VARIABLE_PATTERN = Pattern.compile("/([^:/]*):([^/]+)");
        // Matcher matcher = PATH_REGEX_PATTERN.matcher("/$:api[1-3]/");
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher("/user/api:api[1-2]/:name/:path");
        boolean find = false;
        while ((matcher != null) && (matcher.find())) {
            if (!find)
                find = true;

            System.out.println(matcher.group(1).length());
        } 
        String s = matcher.replaceAll("{sss}");
        System.out.println(s);
    }

    @Test
    public void testAddRoute() throws Exception {
        routeMatcher.addRoute(Route.builder().httpMethod(POST).targetType(RouteHandler.class).target(((RouteHandler) (( ctx) -> ctx.text("post request")))).path("/save").build());
        routeMatcher.register();
        Route saveRoute = routeMatcher.lookupRoute("POST", "/save");
        Assert.assertEquals("POST\t/save", saveRoute.toString());
    }

    @Test
    public void testAddMultiParameter() throws Exception {
        routeMatcher.route("/index", RouteMatcherDemoController.class, "index");
        routeMatcher.route("/remove", RouteMatcherDemoController.class, "remove", DELETE);
        routeMatcher.register();
        Route route = routeMatcher.lookupRoute("GET", "/index");
        Assert.assertEquals("ALL\t/index", route.toString());
        Route removeRoute = routeMatcher.lookupRoute("DELETE", "/remove");
        Assert.assertEquals("DELETE\t/remove", removeRoute.toString());
    }
}

