/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.internal.annotation;


import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.PLAIN_TEXT_UTF_8;
import com.google.common.collect.ImmutableList;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpParameters;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpResponseWriter;
import com.linecorp.armeria.common.Request;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.server.HttpStatusException;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.TestConverters;
import com.linecorp.armeria.server.annotation.Consumes;
import com.linecorp.armeria.server.annotation.Default;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Header;
import com.linecorp.armeria.server.annotation.Order;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.Path;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.Produces;
import com.linecorp.armeria.server.annotation.ResponseConverter;
import com.linecorp.armeria.server.annotation.ResponseConverterFunction;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import com.linecorp.armeria.testing.server.ServerRule;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.ClassRule;
import org.junit.Test;

import static org.apache.http.HttpHeaders.IF_MATCH;


public class AnnotatedHttpServiceTest {
    @ClassRule
    public static final ServerRule rule = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            // Case 1, 2, and 3, with a converter map
            sb.annotatedService("/1", new AnnotatedHttpServiceTest.MyAnnotatedService1(), LoggingService.newDecorator(), new TestConverters.TypedNumberConverterFunction());
            // Case 4, 5, and 6
            sb.annotatedService("/2", new AnnotatedHttpServiceTest.MyAnnotatedService2(), LoggingService.newDecorator());
            // Bind more than one service under the same path prefix.
            sb.annotatedService("/3", new AnnotatedHttpServiceTest.MyAnnotatedService3(), LoggingService.newDecorator());
            sb.annotatedService("/3", new AnnotatedHttpServiceTest.MyAnnotatedService4(), LoggingService.newDecorator());
            sb.annotatedService("/3", new AnnotatedHttpServiceTest.MyAnnotatedService5(), LoggingService.newDecorator());
            // Bind using non-default path mappings
            sb.annotatedService("/6", new AnnotatedHttpServiceTest.MyAnnotatedService6(), LoggingService.newDecorator());
            sb.annotatedService("/7", new AnnotatedHttpServiceTest.MyAnnotatedService7(), LoggingService.newDecorator());
            sb.annotatedService("/8", new AnnotatedHttpServiceTest.MyAnnotatedService8(), LoggingService.newDecorator());
            sb.annotatedService("/9", new AnnotatedHttpServiceTest.MyAnnotatedService9(), LoggingService.newDecorator());
            sb.annotatedService("/10", new AnnotatedHttpServiceTest.MyAnnotatedService10(), LoggingService.newDecorator());
            sb.annotatedService("/11", new AnnotatedHttpServiceTest.MyAnnotatedService11(), LoggingService.newDecorator());
        }
    };

    @ResponseConverter(TestConverters.NaiveIntConverterFunction.class)
    @ResponseConverter(TestConverters.TypedStringConverterFunction.class)
    public static class MyAnnotatedService1 {
        // Case 1: returns Integer type and handled by builder-default Integer -> HttpResponse converter.
        @Get
        @Path("/int/:var")
        public int returnInt(@Param
        int var) {
            return var;
        }

        // Case 2: returns Long type and handled by class-default Number -> HttpResponse converter.
        @Post
        @Path("/long/{var}")
        public CompletionStage<Long> returnLong(@Param
        long var) {
            return CompletableFuture.supplyAsync(() -> var);
        }

        // Case 3: returns String type and handled by custom String -> HttpResponse converter.
        @Get
        @Path("/string/:var")
        @ResponseConverter(TestConverters.NaiveStringConverterFunction.class)
        public CompletionStage<String> returnString(@Param
        String var) {
            return CompletableFuture.supplyAsync(() -> var);
        }

        // Asynchronously returns Integer type and handled by builder-default Integer -> HttpResponse converter.
        @Get
        @Path("/int-async/:var")
        public CompletableFuture<Integer> returnIntAsync(@Param
        int var) {
            return CompletableFuture.completedFuture(var).thenApply(( n) -> n + 1);
        }

        @Get
        @Path("/path/ctx/async/:var")
        public static CompletableFuture<String> returnPathCtxAsync(@Param
        int var, ServiceRequestContext ctx, Request req) {
            AnnotatedHttpServiceTest.validateContextAndRequest(ctx, req);
            return CompletableFuture.completedFuture(ctx.path());
        }

        @Get
        @Path("/path/req/async/:var")
        public static CompletableFuture<String> returnPathReqAsync(@Param
        int var, HttpRequest req, ServiceRequestContext ctx) {
            AnnotatedHttpServiceTest.validateContextAndRequest(ctx, req);
            return CompletableFuture.completedFuture(req.path());
        }

        @Get
        @Path("/path/ctx/sync/:var")
        public static String returnPathCtxSync(@Param
        int var, RequestContext ctx, Request req) {
            AnnotatedHttpServiceTest.validateContextAndRequest(ctx, req);
            return ctx.path();
        }

        @Get
        @Path("/path/req/sync/:var")
        public static String returnPathReqSync(@Param
        int var, HttpRequest req, RequestContext ctx) {
            AnnotatedHttpServiceTest.validateContextAndRequest(ctx, req);
            return req.path();
        }

        // Throws an exception synchronously
        @Get
        @Path("/exception/:var")
        public int exception(@Param
        int var) {
            throw new AnticipatedException("bad var!");
        }

        // Throws an exception asynchronously
        @Get
        @Path("/exception-async/:var")
        public CompletableFuture<Integer> exceptionAsync(@Param
        int var) {
            final CompletableFuture<Integer> future = new CompletableFuture<>();
            future.completeExceptionally(new AnticipatedException("bad var!"));
            return future;
        }

        // Log warning.
        @Get("/warn/:var")
        public String warn() {
            return "warn";
        }

        @Get("/void/204")
        public void void204() {
        }

        @Get("/void/200")
        @ResponseConverter(AnnotatedHttpServiceTest.VoidTo200ResponseConverter.class)
        public void void200() {
        }
    }

    static class VoidTo200ResponseConverter implements ResponseConverterFunction {
        @Override
        public HttpResponse convertResponse(ServiceRequestContext ctx, HttpHeaders headers, @Nullable
        Object result, HttpHeaders trailingHeaders) throws Exception {
            if (result == null) {
                return HttpResponse.of(OK);
            }
            return ResponseConverterFunction.fallthrough();
        }
    }

    @ResponseConverter(TestConverters.TypedNumberConverterFunction.class)
    @ResponseConverter(TestConverters.TypedStringConverterFunction.class)
    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    public static class MyAnnotatedService2 {
        // Case 4: returns Integer type and handled by class-default Number -> HttpResponse converter.
        @Get
        @Path("/int/{var}")
        public CompletionStage<Integer> returnInt(@Param("var")
        int var) {
            return CompletableFuture.supplyAsync(() -> var);
        }

        // Case 5: returns Long type and handled by class-default Number -> HttpResponse converter.
        @Post
        @Path("/long/:var")
        public Long returnLong(@Param("var")
        long var) {
            return var;
        }

        // Case 6: returns String type and handled by custom String -> HttpResponse converter.
        @Get
        @Path("/string/{var}")
        @ResponseConverter(TestConverters.NaiveStringConverterFunction.class)
        public String returnString(@Param("var")
        String var) {
            return var;
        }

        @Get
        @Path("/boolean/{var}")
        public String returnBoolean(@Param("var")
        boolean var) {
            return Boolean.toString(var);
        }

        @Nullable
        @Get("/null1")
        public Object returnNull1() {
            return null;
        }

        @Get("/null2")
        public CompletionStage<Object> returnNull2() {
            return CompletableFuture.completedFuture(null);
        }
    }

    @ResponseConverter(TestConverters.TypedNumberConverterFunction.class)
    public static class MyAnnotatedService3 {
        @Get
        @Path("/int/{var}")
        public CompletionStage<Integer> returnInt(@Param("var")
        int var) {
            return CompletableFuture.supplyAsync(() -> var);
        }
    }

    @ResponseConverter(TestConverters.TypedStringConverterFunction.class)
    public static class MyAnnotatedService4 {
        @Get
        @Path("/string/{var}")
        public String returnString(@Param("var")
        String var) {
            return var;
        }

        @Get
        @Path("/no-path-param")
        public String noPathParam() {
            return "no-path-param";
        }
    }

    // Aggregation Test
    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    public static class MyAnnotatedService5 {
        @Post
        @Path("/a/string")
        public String postString(AggregatedHttpMessage message, RequestContext ctx) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return message.contentUtf8();
        }

        @Post
        @Path("/a/string-async1")
        public CompletionStage<String> postStringAsync1(AggregatedHttpMessage message, RequestContext ctx) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return CompletableFuture.supplyAsync(message::contentUtf8);
        }

        @Post
        @Path("/a/string-async2")
        public HttpResponse postStringAsync2(AggregatedHttpMessage message, RequestContext ctx) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            final HttpResponseWriter response = HttpResponse.streaming();
            response.write(HttpHeaders.of(OK));
            response.write(message.content());
            response.close();
            return response;
        }

        @Post
        @Path("/a/string-aggregate-response1")
        public AggregatedHttpMessage postStringAggregateResponse1(AggregatedHttpMessage message, RequestContext ctx) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return AggregatedHttpMessage.of(HttpHeaders.of(OK), message.content());
        }

        @Post
        @Path("/a/string-aggregate-response2")
        public AggregatedHttpMessage postStringAggregateResponse2(HttpRequest req, RequestContext ctx) {
            AnnotatedHttpServiceTest.validateContextAndRequest(ctx, req);
            final AggregatedHttpMessage message = req.aggregate().join();
            return AggregatedHttpMessage.of(HttpHeaders.of(OK), message.content());
        }
    }

    /**
     * An annotated service that's used for testing non-default path mappings.
     */
    @ResponseConverter(TestConverters.TypedStringConverterFunction.class)
    public static class MyAnnotatedService6 {
        @Get
        @Path("exact:/exact")
        public String exact(ServiceRequestContext ctx) {
            return "exact:" + (ctx.path());
        }

        @Get
        @Path("prefix:/prefix")
        public String prefix(ServiceRequestContext ctx) {
            return (("prefix:" + (ctx.path())) + ':') + (ctx.mappedPath());
        }

        // The pattern that starts with '/'
        @Get
        @Path("glob:/glob1/*")
        public String glob1(ServiceRequestContext ctx) {
            return "glob1:" + (ctx.path());
        }

        // The pattern that does not start with '/'
        @Get
        @Path("glob:glob2")
        public String glob2(ServiceRequestContext ctx) {
            // When this method is bound with a prefix 'foo', the path mapping of this method will be:
            // - /foo/**/glob2
            // Even if the resulting path mapping contains '**', ctx.pathParams().size() must be 0
            // because a user did not specify it.
            return (("glob2:" + (ctx.path())) + ':') + (ctx.pathParams().size());
        }

        @Get
        @Path("regex:^/regex/(?<path>.*)$")
        public String regex(ServiceRequestContext ctx, @Param("path")
        String path) {
            return (("regex:" + (ctx.path())) + ':') + path;
        }
    }

    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    public static class MyAnnotatedService7 {
        @Get("/param/get")
        public String paramGet(RequestContext ctx, @Param("username")
        String username, @Param("password")
        String password) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (username + '/') + password;
        }

        @Post("/param/post")
        public String paramPost(RequestContext ctx, @Param("username")
        String username, @Param("password")
        String password) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (username + '/') + password;
        }

        @Get
        @Path("/map/get")
        public String mapGet(RequestContext ctx, HttpParameters parameters) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return ((parameters.get("username")) + '/') + (parameters.get("password"));
        }

        @Post
        @Path("/map/post")
        public String mapPost(RequestContext ctx, HttpParameters parameters) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return ((parameters.get("username")) + '/') + (parameters.get("password"));
        }

        @Get("/param/enum")
        public String paramEnum(RequestContext ctx, @Param("username")
        String username, @Param("level")
        AnnotatedHttpServiceTest.UserLevel level) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (username + '/') + level;
        }

        @Get("/param/enum2")
        public String paramEnum2(RequestContext ctx, @Param("type")
        AnnotatedHttpServiceTest.UserType type, @Param("level")
        AnnotatedHttpServiceTest.UserLevel level) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (type + "/") + level;
        }

        @Get("/param/enum3")
        public String paramEnum3(RequestContext ctx, @Param("type")
        List<AnnotatedHttpServiceTest.UserType> types, @Param("level")
        Set<AnnotatedHttpServiceTest.UserLevel> levels) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return String.join("/", ImmutableList.builder().addAll(types).addAll(levels).build().stream().map(( e) -> ((Enum<?>) (e)).name()).collect(Collectors.toList()));
        }

        @Get
        @Path("/param/default1")
        public String paramDefault1(RequestContext ctx, @Param("username")
        @Default("hello")
        String username, @Param("password")
        @Default("world")
        Optional<String> password, @Param("extra")
        Optional<String> extra, @Param("number")
        Optional<Integer> number) {
            // "extra" might be null because there is no default value specified.
            AnnotatedHttpServiceTest.validateContext(ctx);
            return ((((username + '/') + (password.get())) + '/') + (extra.orElse("(null)"))) + (number.isPresent() ? "/" + (number.get()) : "");
        }

        @Get
        @Path("/param/default2")
        public String paramDefault2(RequestContext ctx, @Param("username")
        @Default("hello")
        String username, @Param("password")
        String password) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (username + '/') + password;
        }

        @Get
        @Path("/param/precedence/{username}")
        public String paramPrecedence(RequestContext ctx, @Param("username")
        String username, @Param("password")
        String password) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (username + '/') + password;
        }
    }

    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    public static class MyAnnotatedService8 {
        @Get("/same/path")
        public String sharedGet() {
            return "GET";
        }

        @Post("/same/path")
        public String sharedPost() {
            return "POST";
        }

        @Post("/same/path")
        @Consumes("application/json")
        public String sharedPostJson() {
            return "POST/JSON";
        }

        @Get("/same/path")
        @Produces("application/json")
        public String sharedGetJson() {
            return "GET/JSON";
        }

        @Order(-1)
        @Get("/same/path")
        @Produces("text/plain")
        public String sharedGetText() {
            return "GET/TEXT";
        }

        @Post("/same/path")
        @Consumes("application/json")
        @Produces("application/json")
        public String sharedPostJsonBoth() {
            return "POST/JSON/BOTH";
        }

        // To add one more produce type to the virtual host.
        @Get("/other")
        @Produces("application/x-www-form-urlencoded")
        public String other() {
            return "GET/FORM";
        }
    }

    @Produces("application/xml")
    @Produces("application/json")
    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    public static class MyAnnotatedService9 {
        @Get("/same/path")
        public String get() {
            return "GET";
        }

        @Post("/same/path")
        @Consumes("application/xml")
        @Consumes("application/json")
        public String post() {
            return "POST";
        }
    }

    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    public static class MyAnnotatedService10 {
        @Get("/syncThrow")
        public String sync() {
            throw new IllegalArgumentException("foo");
        }

        @Get("/asyncThrow")
        public CompletableFuture<String> async() {
            throw new IllegalArgumentException("bar");
        }

        @Get("/asyncThrowWrapped")
        public CompletableFuture<String> asyncThrowWrapped() {
            return CompletableFuture.supplyAsync(() -> {
                throw new IllegalArgumentException("hoge");
            });
        }

        @Get("/syncThrow401")
        public String sync401() {
            throw HttpStatusException.of(UNAUTHORIZED);
        }

        @Get("/asyncThrow401")
        public CompletableFuture<String> async401() {
            throw HttpStatusException.of(UNAUTHORIZED);
        }

        @Get("/asyncThrowWrapped401")
        public CompletableFuture<String> asyncThrowWrapped401() {
            return CompletableFuture.supplyAsync(() -> {
                throw HttpStatusException.of(UNAUTHORIZED);
            });
        }
    }

    @ResponseConverter(TestConverters.UnformattedStringConverterFunction.class)
    public static class MyAnnotatedService11 {
        @Get("/aHeader")
        public String aHeader(@Header
        String ifMatch) {
            if ("737060cd8c284d8af7ad3082f209582d".equalsIgnoreCase(ifMatch)) {
                return "matched";
            }
            return "unMatched";
        }

        @Post("/customHeader1")
        public String customHeader1(@Header
        List<String> aName) {
            return (String.join(":", aName)) + " is awesome";
        }

        @Post("/customHeader2")
        public String customHeader2(@Header
        Set<String> aName) {
            return (String.join(":", aName)) + " is awesome";
        }

        @Post("/customHeader3")
        public String customHeader3(@Header
        LinkedList<String> aName) {
            return (String.join(":", aName)) + " is awesome";
        }

        @Post("/customHeader4")
        public String customHeader3(@Header
        TreeSet<String> aName) {
            return (String.join(":", aName)) + " is awesome";
        }

        @Post("/customHeader5")
        public String customHeader5(@Header
        List<Integer> numbers, @Header
        Set<String> strings) {
            return ((String.join(":", numbers.stream().map(String::valueOf).collect(Collectors.toList()))) + '/') + (String.join(":", strings));
        }

        @Get("/headerDefault")
        public String headerDefault(RequestContext ctx, @Header
        @Default("hello")
        String username, @Header
        @Default("world")
        Optional<String> password, @Header
        Optional<String> extra, @Header
        Optional<Integer> number) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return ((((username + '/') + (password.get())) + '/') + (extra.orElse("(null)"))) + (number.isPresent() ? "/" + (number.get()) : "");
        }

        @Get("/headerWithParam")
        public String headerWithParam(RequestContext ctx, @Header("username")
        @Default("hello")
        String username, @Header("password")
        @Default("world")
        Optional<String> password, @Param("extra")
        Optional<String> extra, @Param("number")
        int number) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (((((username + '/') + (password.get())) + '/') + (extra.orElse("(null)"))) + '/') + number;
        }

        @Get
        @Path("/headerWithoutValue")
        public String headerWithoutValue(RequestContext ctx, @Header("username")
        @Default("hello")
        String username, @Header("password")
        String password) {
            AnnotatedHttpServiceTest.validateContext(ctx);
            return (username + '/') + password;
        }
    }

    @Test
    public void testAnnotatedHttpService() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/int/42"), "Integer: 42");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/int-async/42"), "Integer: 43");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.post("/1/long/42"), "Number[42]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/string/blah"), "String: blah");
            // ?
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/string/%F0%90%8D%88"), "String: \ud800\udf48", StandardCharsets.UTF_8);
            // Get a requested path as typed string from ServiceRequestContext or HttpRequest
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/path/ctx/async/1"), "String[/1/path/ctx/async/1]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/path/req/async/1"), "String[/1/path/req/async/1]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/path/ctx/sync/1"), "String[/1/path/ctx/sync/1]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/1/path/req/sync/1"), "String[/1/path/req/sync/1]");
            // Illegal parameter.
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/1/int/fourty-two"), 400);
            // Without parameter (non-existing url).
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.post("/1/long/"), 404);
            // Not-mapped HTTP method (Post).
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.post("/1/string/blah"), 405);
            // Exceptions in business logic
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/1/exception/42"), 500);
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/1/exception-async/1"), 500);
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/2/int/42"), "Number[42]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.post("/2/long/42"), "Number[42]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/2/string/blah"), "String: blah");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/2/boolean/true"), "String[true]");
            // Illegal parameter.
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/2/int/fourty-two"), 400);
            // Without parameter (non-existing url).
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.post("/2/long/"), 404);
            // Not-mapped HTTP method (Post).
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.post("/2/string/blah"), 405);
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/2/null1"), "(null)");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/2/null2"), "(null)");
            // Test the case where multiple annotated services are bound under the same path prefix.
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/3/int/42"), "Number[42]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/3/string/blah"), "String[blah]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/3/no-path-param"), "String[no-path-param]");
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/3/undefined"), 404);
        }
    }

    @Test
    public void testNonDefaultPathMappings() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            // Exact pattern
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/6/exact"), "String[exact:/6/exact]");
            // Prefix pattern
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/6/prefix/foo"), "String[prefix:/6/prefix/foo:/foo]");
            // Glob pattern
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/6/glob1/bar"), "String[glob1:/6/glob1/bar]");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/6/baz/glob2"), "String[glob2:/6/baz/glob2:0]");
            // Regex pattern
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/6/regex/foo/bar"), "String[regex:/6/regex/foo/bar:foo/bar]");
        }
    }

    @Test
    public void testAggregation() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            AnnotatedHttpServiceTest.testForm(hc, AnnotatedHttpServiceTest.form("/3/a/string"));
            AnnotatedHttpServiceTest.testForm(hc, AnnotatedHttpServiceTest.form("/3/a/string-async1"));
            AnnotatedHttpServiceTest.testForm(hc, AnnotatedHttpServiceTest.form("/3/a/string-async2"));
            AnnotatedHttpServiceTest.testForm(hc, AnnotatedHttpServiceTest.form("/3/a/string-aggregate-response1"));
            AnnotatedHttpServiceTest.testForm(hc, AnnotatedHttpServiceTest.form("/3/a/string-aggregate-response2"));
        }
    }

    @Test
    public void testParam() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/get?username=line1&password=armeria1"), "line1/armeria1");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.form("/7/param/post", StandardCharsets.UTF_8, "username", "line2", "password", "armeria2"), "line2/armeria2");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.form("/7/param/post", StandardCharsets.UTF_8, "username", "?????", "password", "?????"), "?????/?????", StandardCharsets.UTF_8);
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/map/get?username=line3&password=armeria3"), "line3/armeria3");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.form("/7/map/post", null, "username", "line4", "password", "armeria4"), "line4/armeria4");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/enum?username=line5&level=LV1"), "line5/LV1");
            // Case insensitive test for enum element
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/enum?username=line5&level=lv1"), "line5/LV1");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/enum?username=line6&level=Lv2"), "line6/LV2");
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/7/param/enum?username=line6&level=TEST3"), 400);
            // Case sensitive test enum
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/enum2?type=normal&level=LV1"), "normal/LV1");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/enum2?type=NORMAL&level=LV1"), "NORMAL/LV1");
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/7/param/enum2?type=MINOOX&level=LV1"), 400);
            // Case sensitive test enum
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/enum3?type=normal&level=LV1&level=LV1"), "normal/LV1");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/enum3?type=NORMAL&type=NORMAL&level=LV1"), "NORMAL/NORMAL/LV1");
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/7/param/enum3?type=BAD&level=LV100"), 400);
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/default1"), "hello/world/(null)");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/default1?extra=people&number=1"), "hello/world/people/1");
            // Precedence test. (path variable > query string parameter)
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.get("/7/param/precedence/line5?username=dot&password=armeria5"), "line5/armeria5");
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/7/param/default2"), 400);
        }
    }

    @Test
    public void testAdvancedAnnotatedHttpService() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final String uri = "/8/same/path";
            // No 'Accept' header means accepting everything. The order of -1 would be matched first.
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri), "GET/TEXT", "text/plain");
            // The same as the above.
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri, "*/*"), "GET/TEXT", "text/plain");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.post(uri, "application/json", "application/json"), "POST/JSON/BOTH", "application/json");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri, "application/json;q=0.9, text/plain"), "GET/TEXT", "text/plain");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri, "application/json;q=0.9, text/plain;q=0.7"), "GET/JSON", "application/json");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri, "application/json;charset=UTF-8;q=0.9, text/plain;q=0.7"), "GET/TEXT", "text/plain");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri, ("application/x-www-form-urlencoded" + (",application/json;charset=UTF-8;q=0.9" + ",text/plain;q=0.7"))), "GET/TEXT", "text/plain");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.post(uri, "application/json"), "POST/JSON/BOTH", "application/json");
            AnnotatedHttpServiceTest.testBody(hc, AnnotatedHttpServiceTest.post(uri), "POST");
            // No match on 'Accept' header list.
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.post(uri, null, "application/json"), 406);
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get(uri, "application/json;charset=UTF-8;q=0.9, text/html;q=0.7"), 406);
        }
    }

    @Test
    public void testServiceThrowIllegalArgumentException() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/10/syncThrow"), 400);
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/10/asyncThrow"), 400);
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/10/asyncThrowWrapped"), 400);
        }
    }

    @Test
    public void testServiceThrowHttpResponseException() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/10/syncThrow401"), 401);
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/10/asyncThrow401"), 401);
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/10/asyncThrowWrapped401"), 401);
        }
    }

    @Test
    public void testClassScopeMediaTypeAnnotations() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final String uri = "/9/same/path";
            // "application/xml" is matched because "Accept: */*" is specified and
            // the order of @ProduceTypes is {"application/xml", "application/json"}.
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri, "*/*"), "GET", "application/xml");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.post(uri, "application/xml", "*/*"), "POST", "application/xml");
            // "application/json" is matched because "Accept: application/json" is specified.
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get(uri, "application/json"), "GET", "application/json");
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.post(uri, "application/json", "application/json"), "POST", "application/json");
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get(uri, "text/plain"), 406);
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.post(uri, "text/plain", "*/*"), 415);
        }
    }

    @Test
    public void testRequestHeaderInjection() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            HttpRequestBase request = AnnotatedHttpServiceTest.get("/11/aHeader");
            request.setHeader(IF_MATCH, "737060cd8c284d8af7ad3082f209582d");
            AnnotatedHttpServiceTest.testBody(hc, request, "matched");
            request = AnnotatedHttpServiceTest.post("/11/customHeader1");
            request.setHeader("a-name", "minwoox");
            AnnotatedHttpServiceTest.testBody(hc, request, "minwoox is awesome");
            for (int i = 1; i < 4; i++) {
                request = AnnotatedHttpServiceTest.post(("/11/customHeader" + i));
                request.addHeader("a-name", "minwoox");
                request.addHeader("a-name", "giraffe");
                AnnotatedHttpServiceTest.testBody(hc, request, "minwoox:giraffe is awesome");
            }
            request = AnnotatedHttpServiceTest.post("/11/customHeader4");
            request.addHeader("a-name", "minwoox");
            request.addHeader("a-name", "giraffe");
            AnnotatedHttpServiceTest.testBody(hc, request, "giraffe:minwoox is awesome");
            request = AnnotatedHttpServiceTest.post("/11/customHeader5");
            request.addHeader("numbers", "1");
            request.addHeader("numbers", "2");
            request.addHeader("numbers", "1");
            request.addHeader("strings", "minwoox");
            request.addHeader("strings", "giraffe");
            request.addHeader("strings", "minwoox");
            AnnotatedHttpServiceTest.testBody(hc, request, "1:2:1/minwoox:giraffe");
            request = AnnotatedHttpServiceTest.get("/11/headerDefault");
            AnnotatedHttpServiceTest.testBody(hc, request, "hello/world/(null)");
            request = AnnotatedHttpServiceTest.get("/11/headerDefault");
            request.setHeader("extra", "people");
            request.setHeader("number", "1");
            AnnotatedHttpServiceTest.testBody(hc, request, "hello/world/people/1");
            request = AnnotatedHttpServiceTest.get("/11/headerWithParam?extra=people&number=2");
            request.setHeader("username", "trustin");
            request.setHeader("password", "hyangtack");
            AnnotatedHttpServiceTest.testBody(hc, request, "trustin/hyangtack/people/2");
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/11/headerWithoutValue"), 400);
        }
    }

    @Test
    public void testReturnVoid() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            AnnotatedHttpServiceTest.testStatusCode(hc, AnnotatedHttpServiceTest.get("/1/void/204"), 204);
            AnnotatedHttpServiceTest.testBodyAndContentType(hc, AnnotatedHttpServiceTest.get("/1/void/200"), "200 OK", PLAIN_TEXT_UTF_8.toString());
        }
    }

    private enum UserLevel {

        LV1,
        LV2;}

    private enum UserType {

        normal,
        NORMAL;}
}

