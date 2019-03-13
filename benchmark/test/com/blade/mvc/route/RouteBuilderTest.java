package com.blade.mvc.route;


import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Response;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class RouteBuilderTest {
    @Test
    public void testCreateRouteBuilder() throws Exception {
        RouteMatcher routeMatcher = new RouteMatcher();
        RouteBuilder routeBuilder = new RouteBuilder(routeMatcher);
        routeBuilder.addRouter(RouteBuilderTest.DemoController.class, new RouteBuilderTest.DemoController());
        routeBuilder.addWebHook(WebHook.class, "/*");
        routeMatcher.register();
        Route route = routeMatcher.lookupRoute("GET", "/");
        Assert.assertNotNull(route);
        Assert.assertEquals("GET\t/", route.toString());
    }

    @Path
    class DemoController {
        @GetRoute
        public void hello(Response response) {
            response.text("Ok.");
        }
    }
}

