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


import Route.HTTP_METHOD_WEBSOCKET;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.Map;
import ninja.params.Param;
import ninja.utils.MethodReference;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.utils.NinjaProperties;
import ninja.validation.ValidationImpl;
import ninja.websockets.WebSockets;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * => Most tests are done via class RoutesTest in project
 * ninja-servlet-integration-test.
 */
@RunWith(MockitoJUnitRunner.class)
public class RouterImplTest {
    Router router;

    @Mock
    NinjaProperties ninjaProperties;

    NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver;

    @Mock
    Injector injector;

    @Mock
    Provider<RouterImplTest.TestController> testControllerProvider;

    @Mock
    WebSockets webSockets;

    ArgumentCaptor<Route> webSocketsCompileRouteCaptor;

    @Test
    public void webSocketCompileDelegatedToImpl() {
        Mockito.verify(webSockets).compileRoute(webSocketsCompileRouteCaptor.capture());
        Route route = webSocketsCompileRouteCaptor.getValue();
        Assert.assertThat(route.getHttpMethod(), CoreMatchers.is(HTTP_METHOD_WEBSOCKET));
        Assert.assertThat(route.getUri(), CoreMatchers.is("/websocket"));
    }

    @Test
    public void getPathParametersEncodedWithNoPathParams() {
        Route route = router.getRouteFor("GET", "/testroute");
        Map<String, String> pathParameters = route.getPathParametersEncoded("/testroute");
        Assert.assertThat(pathParameters, Matchers.aMapWithSize(0));
    }

    @Test
    public void getReverseRouteWithNoContextPathWorks() {
        String contextPath = "";
        Mockito.when(ninjaProperties.getContextPath()).thenReturn(contextPath);
        String route = router.getReverseRoute(RouterImplTest.TestController.class, "index");
        Assert.assertThat(route, CoreMatchers.is("/testroute"));
    }

    @Test
    public void getReverseRouteContextPathWorks() {
        String contextPath = "/myappcontext";
        Mockito.when(ninjaProperties.getContextPath()).thenReturn(contextPath);
        String route = router.getReverseRoute(RouterImplTest.TestController.class, "index");
        Assert.assertThat(route, CoreMatchers.is("/myappcontext/testroute"));
    }

    @Test
    public void getReverseRouteWithRegexWorks() {
        String contextPath = "";
        Mockito.when(ninjaProperties.getContextPath()).thenReturn(contextPath);
        String route = router.getReverseRoute(RouterImplTest.TestController.class, "user", "email", "me@me.com", "id", 10000);
        Assert.assertThat(route, CoreMatchers.is("/user/me@me.com/10000"));
    }

    @Test
    public void getReverseRouteWithRegexAndQueryParametersWorks() {
        String contextPath = "";
        Mockito.when(ninjaProperties.getContextPath()).thenReturn(contextPath);
        String route = router.getReverseRoute(RouterImplTest.TestController.class, "user", "email", "me@me.com", "id", 10000, "q", "froglegs");
        Assert.assertThat(route, CoreMatchers.equalTo("/user/me@me.com/10000?q=froglegs"));
    }

    @Test
    public void getReverseRouteWithMultipleRegexWorks() {
        String contextPath = "";
        Mockito.when(ninjaProperties.getContextPath()).thenReturn(contextPath);
        String route = router.getReverseRoute(RouterImplTest.TestController.class, "entry", "userId", 1, "entryId", 100);
        Assert.assertThat(route, CoreMatchers.equalTo("/u1/entries/100"));
    }

    @Test
    public void getReverseRouteWithMethodReference() {
        String contextPath = "";
        Mockito.when(ninjaProperties.getContextPath()).thenReturn(contextPath);
        String route = router.getReverseRoute(RouterImplTest.TestController.class, "ref");
        String route2 = router.getReverseRoute(new MethodReference(RouterImplTest.TestController.class, "ref"));
        Assert.assertThat(route, CoreMatchers.is("/ref"));
        Assert.assertThat(route2, CoreMatchers.is("/ref"));
    }

    @Test
    public void routeForAnyInstanceMethodReference() {
        Route route = router.getRouteFor("GET", "/any_instance_method_ref");
        Result result = route.getFilterChain().next(null);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(201));
    }

    @Test
    public void routeForAnyInstanceMethodReferenceThrowsException() {
        Route route = router.getRouteFor("GET", "/any_instance_method_ref_exception");
        try {
            Result result = route.getFilterChain().next(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e.getCause().getMessage(), CoreMatchers.is("test"));
        }
    }

    @Test
    public void routeForAnyInstanceMethodReference2() {
        Route route = router.getRouteFor("GET", "/any_instance_method_ref2");
        Result result = route.getFilterChain().next(null);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(201));
    }

    @Test
    public void routeForSpecificInstanceMethodReference() {
        Route route = router.getRouteFor("GET", "/specific_instance_method_ref");
        Result result = route.getFilterChain().next(null);
        // message set on specific instance
        Assert.assertThat(result.getRenderable(), CoreMatchers.is("Hi!"));
    }

    @Test
    public void routeForSpecificInstanceMethodReferenceWithAnnotations() {
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getParameter("status")).thenReturn("207");
        Mockito.when(context.getValidation()).thenReturn(new ValidationImpl());
        Route route = router.getRouteFor("GET", "/specific_instance_method_ref_annotations");
        Result result = route.getFilterChain().next(context);
        // message set on specific instance
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(207));
        Assert.assertThat(result.getRenderable(), CoreMatchers.is("Hi!"));
    }

    @Test
    public void routeForAnonymoumsMethodReference() {
        Route route = router.getRouteFor("GET", "/anonymous_method_ref");
        Result result = route.getFilterChain().next(null);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(202));
    }

    @Test
    public void routeForAnonymoumsMethodReferenceWithCaptured() {
        Context context = Mockito.mock(Context.class);
        Route route = router.getRouteFor("GET", "/anonymous_method_ref_captured");
        Result result = route.getFilterChain().next(context);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(208));
    }

    @Test
    public void routeForAnonymoumsMethodReferenceWithContext() {
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getParameterAsInteger("status")).thenReturn(206);
        Route route = router.getRouteFor("GET", "/anonymous_method_ref_context");
        Result result = route.getFilterChain().next(context);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(206));
    }

    @Test
    public void routeForAnonymoumsClassInstance() {
        Route route = router.getRouteFor("GET", "/anonymous_class");
        Result result = route.getFilterChain().next(null);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(203));
    }

    @Test
    public void routeForAnonymoumsClassInstanceWithAnnotations() {
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getParameter("status")).thenReturn("205");
        Mockito.when(context.getValidation()).thenReturn(new ValidationImpl());
        Route route = router.getRouteFor("GET", "/anonymous_class_annotations");
        Result result = route.getFilterChain().next(context);
        Assert.assertThat(result.getStatusCode(), CoreMatchers.is(205));
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

