/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.reactive.result.method;


import HttpMethod.OPTIONS;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpMethod.TRACE;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link RequestMappingInfoHandlerMapping}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestMappingInfoHandlerMappingTests {
    private static final HandlerMethod handlerMethod = new HandlerMethod(new RequestMappingInfoHandlerMappingTests.TestController(), ClassUtils.getMethod(RequestMappingInfoHandlerMappingTests.TestController.class, "dummy"));

    private RequestMappingInfoHandlerMappingTests.TestRequestMappingInfoHandlerMapping handlerMapping;

    @Test
    public void getHandlerDirectMatch() {
        Method expected = on(RequestMappingInfoHandlerMappingTests.TestController.class).annot(getMapping("/foo").params()).resolveMethod();
        ServerWebExchange exchange = MockServerWebExchange.from(get("/foo"));
        HandlerMethod hm = ((HandlerMethod) (this.handlerMapping.getHandler(exchange).block()));
        Assert.assertEquals(expected, hm.getMethod());
    }

    @Test
    public void getHandlerGlobMatch() {
        Method expected = on(RequestMappingInfoHandlerMappingTests.TestController.class).annot(requestMapping("/ba*").method(GET, HEAD)).resolveMethod();
        ServerWebExchange exchange = MockServerWebExchange.from(get("/bar"));
        HandlerMethod hm = ((HandlerMethod) (this.handlerMapping.getHandler(exchange).block()));
        Assert.assertEquals(expected, hm.getMethod());
    }

    @Test
    public void getHandlerEmptyPathMatch() {
        Method expected = on(RequestMappingInfoHandlerMappingTests.TestController.class).annot(requestMapping("")).resolveMethod();
        ServerWebExchange exchange = MockServerWebExchange.from(get(""));
        HandlerMethod hm = ((HandlerMethod) (this.handlerMapping.getHandler(exchange).block()));
        Assert.assertEquals(expected, hm.getMethod());
        exchange = MockServerWebExchange.from(get("/"));
        hm = ((HandlerMethod) (this.handlerMapping.getHandler(exchange).block()));
        Assert.assertEquals(expected, hm.getMethod());
    }

    @Test
    public void getHandlerBestMatch() {
        Method expected = on(RequestMappingInfoHandlerMappingTests.TestController.class).annot(getMapping("/foo").params("p")).resolveMethod();
        ServerWebExchange exchange = MockServerWebExchange.from(get("/foo?p=anything"));
        HandlerMethod hm = ((HandlerMethod) (this.handlerMapping.getHandler(exchange).block()));
        Assert.assertEquals(expected, hm.getMethod());
    }

    @Test
    public void getHandlerRequestMethodNotAllowed() {
        ServerWebExchange exchange = MockServerWebExchange.from(post("/bar"));
        Mono<Object> mono = this.handlerMapping.getHandler(exchange);
        assertError(mono, MethodNotAllowedException.class, ( ex) -> assertEquals(EnumSet.of(HttpMethod.GET, HttpMethod.HEAD), ex.getSupportedMethods()));
    }

    // SPR-9603
    @Test
    public void getHandlerRequestMethodMatchFalsePositive() {
        ServerWebExchange exchange = MockServerWebExchange.from(get("/users").accept(APPLICATION_XML));
        this.handlerMapping.registerHandler(new RequestMappingInfoHandlerMappingTests.UserController());
        Mono<Object> mono = this.handlerMapping.getHandler(exchange);
        StepVerifier.create(mono).expectError(NotAcceptableStatusException.class).verify();
    }

    // SPR-8462
    @Test
    public void getHandlerMediaTypeNotSupported() {
        testHttpMediaTypeNotSupportedException("/person/1");
        testHttpMediaTypeNotSupportedException("/person/1/");
        testHttpMediaTypeNotSupportedException("/person/1.json");
    }

    @Test
    public void getHandlerTestInvalidContentType() {
        MockServerHttpRequest request = put("/person/1").header("content-type", "bogus").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> mono = this.handlerMapping.getHandler(exchange);
        assertError(mono, UnsupportedMediaTypeStatusException.class, ( ex) -> assertEquals(("415 UNSUPPORTED_MEDIA_TYPE " + "\"Invalid mime type \"bogus\": does not contain \'/\'\""), ex.getMessage()));
    }

    // SPR-8462
    @Test
    public void getHandlerTestMediaTypeNotAcceptable() {
        testMediaTypeNotAcceptable("/persons");
        testMediaTypeNotAcceptable("/persons/");
    }

    // SPR-12854
    @Test
    public void getHandlerTestRequestParamMismatch() {
        ServerWebExchange exchange = MockServerWebExchange.from(get("/params"));
        Mono<Object> mono = this.handlerMapping.getHandler(exchange);
        assertError(mono, ServerWebInputException.class, ( ex) -> {
            assertThat(ex.getReason(), containsString("[foo=bar]"));
            assertThat(ex.getReason(), containsString("[bar=baz]"));
        });
    }

    @Test
    public void getHandlerHttpOptions() {
        List<HttpMethod> allMethodExceptTrace = new java.util.ArrayList(Arrays.asList(HttpMethod.values()));
        allMethodExceptTrace.remove(TRACE);
        testHttpOptions("/foo", EnumSet.of(HttpMethod.GET, HttpMethod.HEAD, OPTIONS));
        testHttpOptions("/person/1", EnumSet.of(PUT, OPTIONS));
        testHttpOptions("/persons", EnumSet.copyOf(allMethodExceptTrace));
        testHttpOptions("/something", EnumSet.of(PUT, POST));
    }

    @Test
    public void getHandlerProducibleMediaTypesAttribute() {
        ServerWebExchange exchange = MockServerWebExchange.from(get("/content").accept(APPLICATION_XML));
        this.handlerMapping.getHandler(exchange).block();
        String name = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
        Assert.assertEquals(Collections.singleton(APPLICATION_XML), get(name));
        exchange = MockServerWebExchange.from(get("/content").accept(APPLICATION_JSON));
        this.handlerMapping.getHandler(exchange).block();
        Assert.assertNull("Negated expression shouldn't be listed as producible type", get(name));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleMatchUriTemplateVariables() {
        ServerWebExchange exchange = MockServerWebExchange.from(get("/1/2"));
        RequestMappingInfo key = paths("/{path1}/{path2}").build();
        this.handlerMapping.handleMatch(key, RequestMappingInfoHandlerMappingTests.handlerMethod, exchange);
        String name = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVariables = ((Map<String, String>) (get(name)));
        Assert.assertNotNull(uriVariables);
        Assert.assertEquals("1", uriVariables.get("path1"));
        Assert.assertEquals("2", uriVariables.get("path2"));
    }

    // SPR-9098
    @Test
    public void handleMatchUriTemplateVariablesDecode() {
        RequestMappingInfo key = paths("/{group}/{identifier}").build();
        URI url = URI.create("/group/a%2Fb");
        ServerWebExchange exchange = MockServerWebExchange.from(method(HttpMethod.GET, url));
        this.handlerMapping.handleMatch(key, RequestMappingInfoHandlerMappingTests.handlerMethod, exchange);
        String name = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        @SuppressWarnings("unchecked")
        Map<String, String> uriVariables = ((Map<String, String>) (get(name)));
        Assert.assertNotNull(uriVariables);
        Assert.assertEquals("group", uriVariables.get("group"));
        Assert.assertEquals("a/b", uriVariables.get("identifier"));
    }

    @Test
    public void handleMatchBestMatchingPatternAttribute() {
        RequestMappingInfo key = paths("/{path1}/2", "/**").build();
        ServerWebExchange exchange = MockServerWebExchange.from(get("/1/2"));
        this.handlerMapping.handleMatch(key, RequestMappingInfoHandlerMappingTests.handlerMethod, exchange);
        PathPattern bestMatch = ((PathPattern) (exchange.getAttributes().get(BEST_MATCHING_PATTERN_ATTRIBUTE)));
        Assert.assertEquals("/{path1}/2", bestMatch.getPatternString());
        HandlerMethod mapped = ((HandlerMethod) (exchange.getAttributes().get(BEST_MATCHING_HANDLER_ATTRIBUTE)));
        Assert.assertSame(RequestMappingInfoHandlerMappingTests.handlerMethod, mapped);
    }

    @Test
    public void handleMatchBestMatchingPatternAttributeNoPatternsDefined() {
        RequestMappingInfo key = paths().build();
        ServerWebExchange exchange = MockServerWebExchange.from(get("/1/2"));
        this.handlerMapping.handleMatch(key, RequestMappingInfoHandlerMappingTests.handlerMethod, exchange);
        PathPattern bestMatch = ((PathPattern) (exchange.getAttributes().get(BEST_MATCHING_PATTERN_ATTRIBUTE)));
        Assert.assertEquals("/1/2", bestMatch.getPatternString());
    }

    @Test
    public void handleMatchMatrixVariables() {
        MultiValueMap<String, String> matrixVariables;
        Map<String, String> uriVariables;
        ServerWebExchange exchange = MockServerWebExchange.from(get("/cars;colors=red,blue,green;year=2012"));
        handleMatch(exchange, "/{cars}");
        matrixVariables = getMatrixVariables(exchange, "cars");
        uriVariables = getUriTemplateVariables(exchange);
        Assert.assertNotNull(matrixVariables);
        Assert.assertEquals(Arrays.asList("red", "blue", "green"), matrixVariables.get("colors"));
        Assert.assertEquals("2012", matrixVariables.getFirst("year"));
        Assert.assertEquals("cars", uriVariables.get("cars"));
        // SPR-11897
        exchange = MockServerWebExchange.from(get("/a=42;b=c"));
        handleMatch(exchange, "/{foo}");
        matrixVariables = getMatrixVariables(exchange, "foo");
        uriVariables = getUriTemplateVariables(exchange);
        // Unlike Spring MVC, WebFlux currently does not support APIs like
        // "/foo/{ids}" and URL "/foo/id=1;id=2;id=3" where the whole path
        // segment is a sequence of name-value pairs.
        Assert.assertNotNull(matrixVariables);
        Assert.assertEquals(1, matrixVariables.size());
        Assert.assertEquals("c", matrixVariables.getFirst("b"));
        Assert.assertEquals("a=42", uriVariables.get("foo"));
    }

    @Test
    public void handleMatchMatrixVariablesDecoding() {
        MockServerHttpRequest request = method(HttpMethod.GET, URI.create("/cars;mvar=a%2Fb")).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        handleMatch(exchange, "/{cars}");
        MultiValueMap<String, String> matrixVariables = getMatrixVariables(exchange, "cars");
        Map<String, String> uriVariables = getUriTemplateVariables(exchange);
        Assert.assertNotNull(matrixVariables);
        Assert.assertEquals(Collections.singletonList("a/b"), matrixVariables.get("mvar"));
        Assert.assertEquals("cars", uriVariables.get("cars"));
    }

    @SuppressWarnings("unused")
    @Controller
    private static class TestController {
        @GetMapping("/foo")
        public void foo() {
        }

        @GetMapping(path = "/foo", params = "p")
        public void fooParam() {
        }

        @RequestMapping(path = "/ba*", method = { GET, HEAD })
        public void bar() {
        }

        @RequestMapping(path = "")
        public void empty() {
        }

        @PutMapping(path = "/person/{id}", consumes = "application/xml")
        public void consumes(@RequestBody
        String text) {
        }

        @RequestMapping(path = "/persons", produces = "application/xml")
        public String produces() {
            return "";
        }

        @RequestMapping(path = "/params", params = "foo=bar")
        public String param() {
            return "";
        }

        @RequestMapping(path = "/params", params = "bar=baz")
        public String param2() {
            return "";
        }

        @RequestMapping(path = "/content", produces = "application/xml")
        public String xmlContent() {
            return "";
        }

        @RequestMapping(path = "/content", produces = "!application/xml")
        public String nonXmlContent() {
            return "";
        }

        @RequestMapping(path = "/something", method = OPTIONS)
        public HttpHeaders fooOptions() {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Allow", "PUT,POST");
            return headers;
        }

        public void dummy() {
        }
    }

    @SuppressWarnings("unused")
    @Controller
    private static class UserController {
        @GetMapping(path = "/users", produces = "application/json")
        public void getUser() {
        }

        @PutMapping(path = "/users")
        public void saveUser() {
        }
    }

    private static class TestRequestMappingInfoHandlerMapping extends RequestMappingInfoHandlerMapping {
        void registerHandler(Object handler) {
            detectHandlerMethods(handler);
        }

        @Override
        protected boolean isHandler(Class<?> beanType) {
            return (AnnotationUtils.findAnnotation(beanType, RequestMapping.class)) != null;
        }

        @Override
        protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
            RequestMapping annot = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (annot != null) {
                BuilderConfiguration options = new BuilderConfiguration();
                options.setPatternParser(getPathPatternParser());
                return paths(annot.value()).methods(annot.method()).params(annot.params()).headers(annot.headers()).consumes(annot.consumes()).produces(annot.produces()).options(options).build();
            } else {
                return null;
            }
        }
    }
}

