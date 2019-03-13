/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;


import com.google.inject.Injector;
import com.google.inject.Provider;
import ninja.params.Param;
import ninja.utils.MethodReference;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.utils.NinjaProperties;
import ninja.websockets.WebSockets;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ReverseRouterTest {
    Router router;

    ReverseRouter reverseRouter;

    NinjaProperties ninjaProperties;

    WebSockets webSockets;

    NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver;

    Injector injector;

    Provider<ReverseRouterTest.TestController> testControllerProvider;

    @Test
    public void simple() {
        String route = reverseRouter.with(ReverseRouterTest.TestController::home).build();
        Assert.assertThat(route, CoreMatchers.is("/home"));
    }

    @Test
    public void simpleWithMethodReference() {
        MethodReference methodRef = new MethodReference(ReverseRouterTest.TestController.class, "home");
        String route = reverseRouter.with(methodRef).build();
        Assert.assertThat(route, CoreMatchers.is("/home"));
    }

    @Test
    public void simpleWithClassReference() {
        String route = reverseRouter.with(ReverseRouterTest.TestController.class, "home").build();
        Assert.assertThat(route, CoreMatchers.is("/home"));
    }

    @Test
    public void simpleWithContext() {
        String contextPath = "/context";
        Mockito.when(ninjaProperties.getContextPath()).thenReturn(contextPath);
        String route = reverseRouter.with(ReverseRouterTest.TestController::home).build();
        Assert.assertThat(route, CoreMatchers.is("/context/home"));
    }

    @Test
    public void simpleWithQuery() {
        String route = reverseRouter.with(ReverseRouterTest.TestController::home).queryParam("filter", true).queryParam("a", 1L).queryParam("foo", "bar").queryParam("email", "test@example.com").build();
        // insertion order retained
        Assert.assertThat(route, CoreMatchers.is("/home?filter=true&a=1&foo=bar&email=test%40example.com"));
    }

    @Test
    public void simpleWithRawQuery() {
        String route = reverseRouter.with(ReverseRouterTest.TestController::home).rawQueryParam("email", "test@example.com").build();
        // insertion order retained
        Assert.assertThat(route, CoreMatchers.is("/home?email=test@example.com"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void routeNotFound() {
        reverseRouter.with(ReverseRouterTest.TestController::notfound).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleNoPathParamsThrowsException() {
        reverseRouter.with(ReverseRouterTest.TestController::home).pathParam("id", 1000000L).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleInvalidPathParamThrowsException() {
        reverseRouter.with(ReverseRouterTest.TestController::user).pathParam("id2", 1000000L).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleMissingAllPathParamThrowsException() {
        // param for email missing
        reverseRouter.with(ReverseRouterTest.TestController::user).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleNotEnoughPathParamThrowsException() {
        // param for email missing
        reverseRouter.with(ReverseRouterTest.TestController::user).pathParam("id", 1000000L).build();
    }

    @Test
    public void path() {
        String route = reverseRouter.with(ReverseRouterTest.TestController::user).pathParam("email", "test@example.com").pathParam("id", 1000000L).build();
        Assert.assertThat(route, CoreMatchers.is("/user/test%40example.com/1000000"));
    }

    @Test
    public void rawPath() {
        String route = reverseRouter.with(ReverseRouterTest.TestController::user).rawPathParam("email", "test@example.com").pathParam("id", 1000000L).build();
        Assert.assertThat(route, CoreMatchers.is("/user/test@example.com/1000000"));
    }

    @Test
    public void verifySecondRouteMatched() {
        String route = reverseRouter.with(ReverseRouterTest.TestController::index).build();
        Assert.assertThat(route, CoreMatchers.is("/home/index"));
    }

    @Test
    public void absolute() {
        String route = reverseRouter.with(ReverseRouterTest.TestController::user).absolute("https", "www.greenback.com").pathParam("email", "test@example.com").pathParam("id", 1000000L).build();
        Assert.assertThat(route, CoreMatchers.is("https://www.greenback.com/user/test%40example.com/1000000"));
    }

    @Test
    public void absoluteWebSocket() {
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getScheme()).thenReturn("http");
        Mockito.when(context.getHostname()).thenReturn("www.example.com:8080");
        String route = reverseRouter.with(ReverseRouterTest.TestController::websocket).absolute(context).queryParam("a", 1).build();
        Assert.assertThat(route, CoreMatchers.is("ws://www.example.com:8080/websocket?a=1"));
    }

    @Test
    public void absoluteSecureWebSocket() {
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getScheme()).thenReturn("https");
        Mockito.when(context.getHostname()).thenReturn("www.example.com:8080");
        String route = reverseRouter.with(ReverseRouterTest.TestController::websocket).absolute(context).build();
        Assert.assertThat(route, CoreMatchers.is("wss://www.example.com:8080/websocket"));
    }

    /**
     * A dummy TestController for mocking.
     */
    public static class TestController {
        private final String message;

        public TestController() {
            this("not set");
        }

        public TestController(String message) {
            this.message = message;
        }

        public Result notfound() {
            return Results.ok();
        }

        public Result index() {
            return Results.ok();
        }

        public Result user() {
            return Results.ok();
        }

        public Result entry() {
            return Results.ok();
        }

        public Result ref() {
            return Results.ok();
        }

        public Result home() {
            return Results.status(201);
        }

        public Result message() {
            return Results.ok().render(message);
        }

        public Result status(@Param("status")
        Integer status) {
            return Results.status(status).render(message);
        }

        public Result exception() throws Exception {
            throw new Exception("test");
        }

        public Result websocket() {
            return Results.webSocketContinue();
        }
    }
}

