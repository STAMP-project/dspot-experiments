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


import NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE;
import com.example.conf.Filters;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.Map;
import ninja.utils.MethodReference;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.utils.NinjaProperties;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RouteBuilderImplTest {
    @Mock
    Injector injector;

    @Mock
    NinjaProperties ninjaProperties;

    NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver;

    RouteBuilderImpl routeBuilder;

    @Test
    public void basicGETRoute() {
        routeBuilder.GET().route("/index");
        Assert.assertTrue(buildRoute(routeBuilder).matches("GET", "/index"));
    }

    @Test
    public void basicPOSTRoute() {
        routeBuilder.POST().route("/index");
        Assert.assertTrue(buildRoute(routeBuilder).matches("POST", "/index"));
    }

    @Test
    public void basicPUTRoute() {
        routeBuilder.PUT().route("/index");
        Assert.assertTrue(buildRoute(routeBuilder).matches("PUT", "/index"));
    }

    @Test
    public void basicRoutes() {
        routeBuilder.OPTIONS().route("/index");
        Assert.assertTrue(buildRoute(routeBuilder).matches("OPTIONS", "/index"));
    }

    @Test
    public void basisHEAD() {
        routeBuilder.HEAD().route("/index");
        Assert.assertTrue(buildRoute(routeBuilder).matches("HEAD", "/index"));
    }

    @Test
    public void basicAnyHttpMethod() {
        routeBuilder.METHOD("PROPFIND").route("/index");
        Assert.assertTrue(buildRoute(routeBuilder).matches("PROPFIND", "/index"));
    }

    @Test
    public void basisWS() {
        routeBuilder.WS().route("/index");
        Assert.assertTrue(buildRoute(routeBuilder).matches("WS", "/index"));
    }

    @Test
    public void basicRoutesWithRegex() {
        routeBuilder.GET().route("/.*");
        Route route = buildRoute(routeBuilder);
        // make sure the route catches everything
        Assert.assertTrue(route.matches("GET", "/index"));
        Assert.assertTrue(route.matches("GET", "/stylesheet.css"));
        Assert.assertTrue(route.matches("GET", "/public/stylesheet.css"));
        Assert.assertTrue(route.matches("GET", "/public/bootstrap.js"));
    }

    @Test
    public void basicPlaceholersAndParameters() {
        // /////////////////////////////////////////////////////////////////////
        // One parameter:
        // /////////////////////////////////////////////////////////////////////
        routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
        routeBuilder.GET().route("/{name}/dashboard");
        Route route = buildRoute(routeBuilder);
        Assert.assertFalse(route.matches("GET", "/dashboard"));
        Assert.assertTrue(route.matches("GET", "/John/dashboard"));
        Map<String, String> map = route.getPathParametersEncoded("/John/dashboard");
        Assert.assertEquals(1, map.entrySet().size());
        Assert.assertEquals("John", map.get("name"));
        // /////////////////////////////////////////////////////////////////////
        // More parameters
        // /////////////////////////////////////////////////////////////////////
        routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
        routeBuilder.GET().route("/{name}/{id}/dashboard");
        route = buildRoute(routeBuilder);
        Assert.assertFalse(route.matches("GET", "/dashboard"));
        Assert.assertTrue(route.matches("GET", "/John/20/dashboard"));
        map = route.getPathParametersEncoded("/John/20/dashboard");
        Assert.assertEquals(2, map.entrySet().size());
        Assert.assertEquals("John", map.get("name"));
        Assert.assertEquals("20", map.get("id"));
    }

    @Test
    public void basicPlaceholersParametersAndRegex() {
        // test that parameter parsing works in conjunction with
        // regex expressions...
        routeBuilder.GET().route("/John/{id}/.*");
        Route route = buildRoute(routeBuilder);
        Assert.assertTrue(route.matches("GET", "/John/20/dashboard"));
        Map<String, String> map = route.getPathParametersEncoded("/John/20/dashboard");
        Assert.assertEquals(1, map.entrySet().size());
        Assert.assertEquals("20", map.get("id"));
        Assert.assertTrue(route.matches("GET", "/John/20/admin"));
        map = route.getPathParametersEncoded("/John/20/admin");
        Assert.assertEquals(1, map.entrySet().size());
        Assert.assertEquals("20", map.get("id"));
        Assert.assertTrue(route.matches("GET", "/John/20/mock"));
        map = route.getPathParametersEncoded("/John/20/mock");
        Assert.assertEquals(1, map.entrySet().size());
        Assert.assertEquals("20", map.get("id"));
    }

    @Test
    public void basicPlaceholersParametersAndRegexInsideVariableParts() {
        // test that parameter parsing works in conjunction with
        // regex expressions...
        routeBuilder.GET().route("/assets/{file: .*}");
        Route route = buildRoute(routeBuilder);
        String pathUnderTest = "/assets/css/app.css";
        Assert.assertTrue(route.matches("GET", pathUnderTest));
        Map<String, String> map = route.getPathParametersEncoded(pathUnderTest);
        Assert.assertEquals(1, map.entrySet().size());
        Assert.assertEquals("css/app.css", map.get("file"));
        pathUnderTest = "/assets/javascripts/main.js";
        Assert.assertTrue(route.matches("GET", pathUnderTest));
        map = route.getPathParametersEncoded(pathUnderTest);
        Assert.assertEquals(1, map.entrySet().size());
        Assert.assertEquals("javascripts/main.js", map.get("file"));
        pathUnderTest = "/assets/robots.txt";
        Assert.assertTrue(route.matches("GET", pathUnderTest));
        map = route.getPathParametersEncoded(pathUnderTest);
        Assert.assertEquals(1, map.entrySet().size());
        Assert.assertEquals("robots.txt", map.get("file"));
        // multiple parameter parsing with regex expressions
        routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
        routeBuilder.GET().route("/{name: .+}/photos/{id: [0-9]+}");
        route = buildRoute(routeBuilder);
        pathUnderTest = "/John/photos/2201";
        Assert.assertTrue(route.matches("GET", pathUnderTest));
        Assert.assertFalse(route.matches("GET", "John/photos/first"));
        map = route.getPathParametersEncoded(pathUnderTest);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("John", map.get("name"));
        Assert.assertEquals("2201", map.get("id"));
    }

    @Test
    public void parametersDontCrossSlashes() {
        routeBuilder.GET().route("/blah/{id}/{id2}/{id3}/morestuff/at/the/end");
        Route route = buildRoute(routeBuilder);
        // this must match
        Assert.assertTrue(route.matches("GET", "/blah/id/id2/id3/morestuff/at/the/end"));
        // this should not match as the last "end" is missing
        Assert.assertFalse(route.matches("GET", "/blah/id/id2/id3/morestuff/at/the"));
    }

    @Test
    public void pointsInRegexDontCrashRegexInTheMiddleOfTheRoute() {
        routeBuilder.GET().route("/blah/{id}/myname");
        Route route = buildRoute(routeBuilder);
        // the "." in the route should not make any trouble:
        String routeFromServer = "/blah/my.id/myname";
        Assert.assertTrue(route.matches("GET", routeFromServer));
        Assert.assertEquals(1, route.getPathParametersEncoded(routeFromServer).entrySet().size());
        Assert.assertEquals("my.id", route.getPathParametersEncoded(routeFromServer).get("id"));
        // and another slightly different route
        routeFromServer = "/blah/my.id/myname/should_not_match";
        Assert.assertFalse(route.matches("GET", routeFromServer));
        Assert.assertEquals(0, route.getPathParametersEncoded(routeFromServer).entrySet().size());
    }

    @Test
    public void pointsInRegexDontCrashRegexAtEnd() {
        routeBuilder.GET().route("/blah/{id}");
        Route route = buildRoute(routeBuilder);
        // the "." in the route should not make any trouble:
        // even if it's the last part of the route
        String routeFromServer = "/blah/my.id";
        Assert.assertTrue(route.matches("GET", "/blah/my.id"));
        Assert.assertEquals(1, route.getPathParametersEncoded(routeFromServer).entrySet().size());
        Assert.assertEquals("my.id", route.getPathParametersEncoded(routeFromServer).get("id"));
    }

    @Test
    public void regexInRouteWorksWithEscapes() {
        // Test escaped constructs in regex
        // regex with escaped construct in a route
        routeBuilder.GET().route("/customers/\\d+");
        Route route = buildRoute(routeBuilder);
        Assert.assertTrue(route.matches("GET", "/customers/1234"));
        Assert.assertFalse(route.matches("GET", "/customers/12ab"));
        // regex with escaped construct in a route with variable parts
        routeBuilder.GET().route("/customers/{id: \\d+}");
        route = buildRoute(routeBuilder);
        Assert.assertTrue(route.matches("GET", "/customers/1234"));
        Assert.assertFalse(route.matches("GET", "/customers/12x"));
        Map<String, String> map = route.getPathParametersEncoded("/customers/1234");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("1234", map.get("id"));
    }

    @Test
    public void regexInRouteWorksWithoutSlashAtTheEnd() {
        routeBuilder.GET().route("/blah/{id}/.*");
        Route route = buildRoute(routeBuilder);
        // the "." in the real route should work without any problems:
        String routeFromServer = "/blah/my.id/and/some/more/stuff";
        Assert.assertTrue(route.matches("GET", routeFromServer));
        Assert.assertEquals(1, route.getPathParametersEncoded(routeFromServer).entrySet().size());
        Assert.assertEquals("my.id", route.getPathParametersEncoded(routeFromServer).get("id"));
        // another slightly different route.
        routeFromServer = "/blah/my.id/";
        Assert.assertTrue(route.matches("GET", "/blah/my.id/"));
        Assert.assertEquals(1, route.getPathParametersEncoded(routeFromServer).entrySet().size());
        Assert.assertEquals("my.id", route.getPathParametersEncoded(routeFromServer).get("id"));
        Assert.assertFalse(route.matches("GET", "/blah/my.id"));
    }

    @Test
    public void routeWithUrlEncodedSlashGetsChoppedCorrectly() {
        routeBuilder.GET().route("/blah/{id}/.*");
        Route route = buildRoute(routeBuilder);
        // Just a simple test to make sure everything works on a not encoded
        // uri:
        // decoded this would be /blah/my/id/and/some/more/stuff
        String routeFromServer = "/blah/my%2fid/and/some/more/stuff";
        Assert.assertTrue(route.matches("GET", routeFromServer));
        Assert.assertEquals(1, route.getPathParametersEncoded(routeFromServer).entrySet().size());
        Assert.assertEquals("my%2fid", route.getPathParametersEncoded(routeFromServer).get("id"));
    }

    @Test
    public void routeWithResult() {
        Context context = Mockito.mock(Context.class);
        String template = "/directly_result/stuff";
        routeBuilder.GET().route("/directly_result/route").with(Results.html().template(template));
        Route route = routeBuilder.buildRoute(injector);
        Assert.assertTrue(route.matches("GET", "/directly_result/route"));
        Result result = route.getFilterChain().next(context);
        Assert.assertEquals(result.getTemplate(), template);
    }

    @Test
    public void failedControllerRegistration() {
        routeBuilder.GET().route("/failure").with(RouteBuilderImplTest.MockController.class, "DoesNotExist");
        try {
            Route route = routeBuilder.buildRoute(injector);
            Assert.assertTrue((route == null));
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalStateException));
        }
    }

    @Test
    public void routeWithMethodReference() throws Exception {
        routeBuilder.GET().route("/method_reference").with(new MethodReference(RouteBuilderImplTest.MockController.class, "execute"));
        Route route = routeBuilder.buildRoute(injector);
        Assert.assertTrue(route.matches("GET", "/method_reference"));
        Assert.assertThat(route.getControllerClass(), CoreMatchers.is(RouteBuilderImplTest.MockController.class));
    }

    public static class MockController {
        public Result execute() {
            return null;
        }

        public Result execute2(Context context) {
            return null;
        }

        public static Result execute3(Context context) {
            return null;
        }
    }

    @Test
    public void routeToAnyInstanceMethodReference() throws Exception {
        routeBuilder.GET().route("/execute").with(RouteBuilderImplTest.MockController::execute);
        Route route = routeBuilder.buildRoute(injector);
        Assert.assertTrue(route.matches("GET", "/execute"));
        Assert.assertThat(route.getControllerClass(), CoreMatchers.is(RouteBuilderImplTest.MockController.class));
        Assert.assertThat(route.getControllerMethod().getName(), CoreMatchers.is("execute"));
    }

    @Test
    public void routeToSpecificInstanceMethodReference() throws Exception {
        RouteBuilderImplTest.MockController controller = new RouteBuilderImplTest.MockController();
        routeBuilder.GET().route("/execute").with(controller::execute);
        Route route = routeBuilder.buildRoute(injector);
        Assert.assertTrue(route.matches("GET", "/execute"));
        Assert.assertThat(route.getControllerClass().getCanonicalName(), CoreMatchers.startsWith(this.getClass().getCanonicalName()));
        Assert.assertThat(route.getControllerMethod().getName(), CoreMatchers.is("apply"));
    }

    @Test
    public void routeToStaticMethodReference() throws Exception {
        routeBuilder.GET().route("/execute").with(RouteBuilderImplTest.MockController::execute3);
        Route route = routeBuilder.buildRoute(injector);
        Assert.assertTrue(route.matches("GET", "/execute"));
        Assert.assertThat(route.getControllerClass(), CoreMatchers.is(RouteBuilderImplTest.MockController.class));
        Assert.assertThat(route.getControllerMethod().getName(), CoreMatchers.is("execute3"));
    }

    @Test
    @SuppressWarnings("Convert2Lambda")
    public void routeToAnonymousClassReference() throws Exception {
        routeBuilder.GET().route("/execute").with(new ControllerMethods.ControllerMethod0() {
            @Override
            public Result apply() {
                return Results.redirect("/");
            }
        });
        Route route = routeBuilder.buildRoute(injector);
        Assert.assertTrue(route.matches("GET", "/execute"));
        Assert.assertThat(route.getControllerClass().isAnonymousClass(), CoreMatchers.is(true));
        Assert.assertThat(route.getControllerMethod().getName(), CoreMatchers.is("apply"));
    }

    @Test
    public void routeToAnonymousMethodReference() throws Exception {
        routeBuilder.GET().route("/execute").with(() -> Results.redirect("/"));
        Route route = routeBuilder.buildRoute(injector);
        Assert.assertTrue(route.matches("GET", "/execute"));
        // should be a class within this test class as a real lambda
        Assert.assertThat(route.getControllerClass().getCanonicalName(), CoreMatchers.startsWith(this.getClass().getCanonicalName()));
        Assert.assertThat(route.getControllerMethod().getName(), CoreMatchers.is("apply"));
    }

    private class DummyFilter implements Filter {
        int executed = 0;

        @Override
        public Result filter(FilterChain filterChain, Context context) {
            (executed)++;
            return filterChain.next(context);
        }
    }

    private class DummyFilter2 extends RouteBuilderImplTest.DummyFilter {}

    @Test
    public void testGlobalFilters() throws Exception {
        // given
        // different setup that uses com.example packages and thus reads the Filters there
        Mockito.when(ninjaProperties.get(APPLICATION_MODULES_BASE_PACKAGE)).thenReturn("com.example");
        this.ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);
        this.routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
        RouteBuilderImplTest.DummyFilter dummyFilter = new RouteBuilderImplTest.DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        Filters filters = new Filters(RouteBuilderImplTest.DummyFilter.class);
        Mockito.when(injector.getInstance(Filters.class)).thenReturn(filters);
        Mockito.when(injector.getProvider(RouteBuilderImplTest.DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        routeBuilder.GET().route("/").with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);
        FilterChain filterChain = route.getFilterChain();
        // when
        Result result = filterChain.next(context);
        // then
        Mockito.verify(injector).getInstance(Filters.class);
        Assert.assertThat(dummyFilter.executed, Matchers.equalTo(1));
        Assert.assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }

    @Test
    public void testThatGlobalFiltersInRouteReplaceGlobalFiltersInConfFilters() throws Exception {
        // DummyFilter is defined in conf.Filters, but .globalFilters(DummyFilter2.class) should
        // override that.
        // given
        // different setup that uses com.example packages and thus reads the Filters there
        Mockito.when(ninjaProperties.get(APPLICATION_MODULES_BASE_PACKAGE)).thenReturn("com.example");
        this.ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);
        this.routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
        RouteBuilderImplTest.DummyFilter2 dummyFilter2 = new RouteBuilderImplTest.DummyFilter2();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        Mockito.when(injector.getProvider(RouteBuilderImplTest.DummyFilter2.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter2);
        routeBuilder.GET().route("/").globalFilters(RouteBuilderImplTest.DummyFilter2.class).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);
        FilterChain filterChain = route.getFilterChain();
        // when
        Result result = filterChain.next(context);
        // then
        Mockito.verify(injector, Mockito.never()).getProvider(RouteBuilderImplTest.DummyFilter.class);
        Mockito.verify(injector).getProvider(RouteBuilderImplTest.DummyFilter2.class);
        Assert.assertThat(dummyFilter2.executed, Matchers.equalTo(1));
        Assert.assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }

    @Test
    public void testWithFiltersClass() throws Exception {
        // given
        RouteBuilderImplTest.DummyFilter dummyFilter = new RouteBuilderImplTest.DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        Mockito.when(injector.getProvider(RouteBuilderImplTest.DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        routeBuilder.GET().route("/").filters(RouteBuilderImplTest.DummyFilter.class).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);
        FilterChain filterChain = route.getFilterChain();
        // when
        Result result = filterChain.next(context);
        // then
        Assert.assertThat(dummyFilter.executed, Matchers.equalTo(1));
        Assert.assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }

    @Test
    public void testWithFiltersList() throws Exception {
        // given
        RouteBuilderImplTest.DummyFilter dummyFilter = new RouteBuilderImplTest.DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        Mockito.when(injector.getProvider(RouteBuilderImplTest.DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        routeBuilder.GET().route("/").filters(Lists.newArrayList(RouteBuilderImplTest.DummyFilter.class)).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);
        FilterChain filterChain = route.getFilterChain();
        // when
        Result result = filterChain.next(context);
        // then
        Assert.assertThat(dummyFilter.executed, Matchers.equalTo(1));
        Assert.assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }

    @Test
    public void testWithGlobalFiltersClass() throws Exception {
        // given
        RouteBuilderImplTest.DummyFilter dummyFilter = new RouteBuilderImplTest.DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        Mockito.when(injector.getProvider(RouteBuilderImplTest.DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        routeBuilder.GET().route("/").globalFilters(RouteBuilderImplTest.DummyFilter.class).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);
        FilterChain filterChain = route.getFilterChain();
        // when
        Result result = filterChain.next(context);
        // then
        Assert.assertThat(dummyFilter.executed, Matchers.equalTo(1));
        Assert.assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }

    @Test
    public void testWithGlobalFiltersList() throws Exception {
        // given
        RouteBuilderImplTest.DummyFilter dummyFilter = new RouteBuilderImplTest.DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        Mockito.when(injector.getProvider(RouteBuilderImplTest.DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        routeBuilder.GET().route("/").globalFilters(Lists.newArrayList(RouteBuilderImplTest.DummyFilter.class)).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);
        FilterChain filterChain = route.getFilterChain();
        // when
        Result result = filterChain.next(context);
        // then
        Assert.assertThat(dummyFilter.executed, Matchers.equalTo(1));
        Assert.assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }
}

