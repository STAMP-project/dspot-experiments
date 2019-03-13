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
package org.springframework.web.servlet.mvc.method;


import HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;
import MediaType.APPLICATION_XML;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.util.UrlPathHelper;


/**
 * Test fixture with {@link RequestMappingInfoHandlerMapping}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class RequestMappingInfoHandlerMappingTests {
    private RequestMappingInfoHandlerMappingTests.TestRequestMappingInfoHandlerMapping handlerMapping;

    private HandlerMethod fooMethod;

    private HandlerMethod fooParamMethod;

    private HandlerMethod barMethod;

    private HandlerMethod emptyMethod;

    @Test
    public void getMappingPathPatterns() throws Exception {
        String[] patterns = new String[]{ "/foo/*", "/foo", "/bar/*", "/bar" };
        RequestMappingInfo info = RequestMappingInfo.paths(patterns).build();
        Set<String> actual = this.handlerMapping.getMappingPathPatterns(info);
        Assert.assertEquals(new HashSet<>(Arrays.asList(patterns)), actual);
    }

    @Test
    public void getHandlerDirectMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        HandlerMethod handlerMethod = getHandler(request);
        Assert.assertEquals(this.fooMethod.getMethod(), handlerMethod.getMethod());
    }

    @Test
    public void getHandlerGlobMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bar");
        HandlerMethod handlerMethod = getHandler(request);
        Assert.assertEquals(this.barMethod.getMethod(), handlerMethod.getMethod());
    }

    @Test
    public void getHandlerEmptyPathMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
        HandlerMethod handlerMethod = getHandler(request);
        Assert.assertEquals(this.emptyMethod.getMethod(), handlerMethod.getMethod());
        request = new MockHttpServletRequest("GET", "/");
        handlerMethod = getHandler(request);
        Assert.assertEquals(this.emptyMethod.getMethod(), handlerMethod.getMethod());
    }

    @Test
    public void getHandlerBestMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setParameter("p", "anything");
        HandlerMethod handlerMethod = getHandler(request);
        Assert.assertEquals(this.fooParamMethod.getMethod(), handlerMethod.getMethod());
    }

    @Test
    public void getHandlerRequestMethodNotAllowed() throws Exception {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/bar");
            this.handlerMapping.getHandler(request);
            Assert.fail("HttpRequestMethodNotSupportedException expected");
        } catch (HttpRequestMethodNotSupportedException ex) {
            Assert.assertArrayEquals("Invalid supported methods", new String[]{ "GET", "HEAD" }, ex.getSupportedMethods());
        }
    }

    // SPR-9603
    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void getHandlerRequestMethodMatchFalsePositive() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
        request.addHeader("Accept", "application/xml");
        this.handlerMapping.registerHandler(new RequestMappingInfoHandlerMappingTests.UserController());
        this.handlerMapping.getHandler(request);
    }

    // SPR-8462
    @Test
    public void getHandlerMediaTypeNotSupported() throws Exception {
        testHttpMediaTypeNotSupportedException("/person/1");
        testHttpMediaTypeNotSupportedException("/person/1/");
        testHttpMediaTypeNotSupportedException("/person/1.json");
    }

    @Test
    public void getHandlerHttpOptions() throws Exception {
        testHttpOptions("/foo", "GET,HEAD,OPTIONS");
        testHttpOptions("/person/1", "PUT,OPTIONS");
        testHttpOptions("/persons", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS");
        testHttpOptions("/something", "PUT,POST");
    }

    @Test
    public void getHandlerTestInvalidContentType() throws Exception {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/person/1");
            request.setContentType("bogus");
            this.handlerMapping.getHandler(request);
            Assert.fail("HttpMediaTypeNotSupportedException expected");
        } catch (HttpMediaTypeNotSupportedException ex) {
            Assert.assertEquals("Invalid mime type \"bogus\": does not contain \'/\'", ex.getMessage());
        }
    }

    // SPR-8462
    @Test
    public void getHandlerMediaTypeNotAccepted() throws Exception {
        testHttpMediaTypeNotAcceptableException("/persons");
        testHttpMediaTypeNotAcceptableException("/persons/");
        testHttpMediaTypeNotAcceptableException("/persons.json");
    }

    // SPR-12854
    @Test
    public void getHandlerUnsatisfiedServletRequestParameterException() throws Exception {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/params");
            this.handlerMapping.getHandler(request);
            Assert.fail("UnsatisfiedServletRequestParameterException expected");
        } catch (UnsatisfiedServletRequestParameterException ex) {
            List<String[]> groups = ex.getParamConditionGroups();
            Assert.assertEquals(2, groups.size());
            Assert.assertThat(Arrays.asList("foo=bar", "bar=baz"), containsInAnyOrder(groups.get(0)[0], groups.get(1)[0]));
        }
    }

    @Test
    public void getHandlerProducibleMediaTypesAttribute() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/content");
        request.addHeader("Accept", "application/xml");
        this.handlerMapping.getHandler(request);
        String name = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
        Assert.assertEquals(Collections.singleton(APPLICATION_XML), request.getAttribute(name));
        request = new MockHttpServletRequest("GET", "/content");
        request.addHeader("Accept", "application/json");
        this.handlerMapping.getHandler(request);
        Assert.assertNull("Negated expression shouldn't be listed as producible type", request.getAttribute(name));
    }

    @Test
    public void getHandlerMappedInterceptors() throws Exception {
        String path = "/foo";
        HandlerInterceptor interceptor = new HandlerInterceptorAdapter() {};
        MappedInterceptor mappedInterceptor = new MappedInterceptor(new String[]{ path }, interceptor);
        RequestMappingInfoHandlerMappingTests.TestRequestMappingInfoHandlerMapping mapping = new RequestMappingInfoHandlerMappingTests.TestRequestMappingInfoHandlerMapping();
        mapping.registerHandler(new RequestMappingInfoHandlerMappingTests.TestController());
        setInterceptors(new Object[]{ mappedInterceptor });
        mapping.setApplicationContext(new StaticWebApplicationContext());
        HandlerExecutionChain chain = mapping.getHandler(new MockHttpServletRequest("GET", path));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getInterceptors());
        Assert.assertSame(interceptor, chain.getInterceptors()[0]);
        chain = mapping.getHandler(new MockHttpServletRequest("GET", "/invalid"));
        Assert.assertNull(chain);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleMatchUriTemplateVariables() {
        RequestMappingInfo key = RequestMappingInfo.paths("/{path1}/{path2}").build();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/1/2");
        String lookupPath = new UrlPathHelper().getLookupPathForRequest(request);
        this.handlerMapping.handleMatch(key, lookupPath, request);
        String name = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVariables = ((Map<String, String>) (request.getAttribute(name)));
        Assert.assertNotNull(uriVariables);
        Assert.assertEquals("1", uriVariables.get("path1"));
        Assert.assertEquals("2", uriVariables.get("path2"));
    }

    // SPR-9098
    @SuppressWarnings("unchecked")
    @Test
    public void handleMatchUriTemplateVariablesDecode() {
        RequestMappingInfo key = RequestMappingInfo.paths("/{group}/{identifier}").build();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/group/a%2Fb");
        UrlPathHelper pathHelper = new UrlPathHelper();
        pathHelper.setUrlDecode(false);
        String lookupPath = pathHelper.getLookupPathForRequest(request);
        this.handlerMapping.setUrlPathHelper(pathHelper);
        this.handlerMapping.handleMatch(key, lookupPath, request);
        String name = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVariables = ((Map<String, String>) (request.getAttribute(name)));
        Assert.assertNotNull(uriVariables);
        Assert.assertEquals("group", uriVariables.get("group"));
        Assert.assertEquals("a/b", uriVariables.get("identifier"));
    }

    @Test
    public void handleMatchBestMatchingPatternAttribute() {
        RequestMappingInfo key = RequestMappingInfo.paths("/{path1}/2", "/**").build();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/1/2");
        this.handlerMapping.handleMatch(key, "/1/2", request);
        Assert.assertEquals("/{path1}/2", request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE));
    }

    @Test
    public void handleMatchBestMatchingPatternAttributeNoPatternsDefined() {
        RequestMappingInfo key = RequestMappingInfo.paths().build();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/1/2");
        this.handlerMapping.handleMatch(key, "/1/2", request);
        Assert.assertEquals("/1/2", request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE));
    }

    @Test
    public void handleMatchMatrixVariables() {
        MockHttpServletRequest request;
        MultiValueMap<String, String> matrixVariables;
        Map<String, String> uriVariables;
        // URI var parsed into path variable + matrix params..
        request = new MockHttpServletRequest();
        handleMatch(request, "/{cars}", "/cars;colors=red,blue,green;year=2012");
        matrixVariables = getMatrixVariables(request, "cars");
        uriVariables = getUriTemplateVariables(request);
        Assert.assertNotNull(matrixVariables);
        Assert.assertEquals(Arrays.asList("red", "blue", "green"), matrixVariables.get("colors"));
        Assert.assertEquals("2012", matrixVariables.getFirst("year"));
        Assert.assertEquals("cars", uriVariables.get("cars"));
        // URI var with regex for path variable, and URI var for matrix params..
        request = new MockHttpServletRequest();
        handleMatch(request, "/{cars:[^;]+}{params}", "/cars;colors=red,blue,green;year=2012");
        matrixVariables = getMatrixVariables(request, "params");
        uriVariables = getUriTemplateVariables(request);
        Assert.assertNotNull(matrixVariables);
        Assert.assertEquals(Arrays.asList("red", "blue", "green"), matrixVariables.get("colors"));
        Assert.assertEquals("2012", matrixVariables.getFirst("year"));
        Assert.assertEquals("cars", uriVariables.get("cars"));
        Assert.assertEquals(";colors=red,blue,green;year=2012", uriVariables.get("params"));
        // URI var with regex for path variable, and (empty) URI var for matrix params..
        request = new MockHttpServletRequest();
        handleMatch(request, "/{cars:[^;]+}{params}", "/cars");
        matrixVariables = getMatrixVariables(request, "params");
        uriVariables = getUriTemplateVariables(request);
        Assert.assertNull(matrixVariables);
        Assert.assertEquals("cars", uriVariables.get("cars"));
        Assert.assertEquals("", uriVariables.get("params"));
        // SPR-11897
        request = new MockHttpServletRequest();
        handleMatch(request, "/{foo}", "/a=42;b=c");
        matrixVariables = getMatrixVariables(request, "foo");
        uriVariables = getUriTemplateVariables(request);
        Assert.assertNotNull(matrixVariables);
        Assert.assertEquals(2, matrixVariables.size());
        Assert.assertEquals("42", matrixVariables.getFirst("a"));
        Assert.assertEquals("c", matrixVariables.getFirst("b"));
        Assert.assertEquals("a=42", uriVariables.get("foo"));
    }

    // SPR-10140, SPR-16867
    @Test
    public void handleMatchMatrixVariablesDecoding() {
        MockHttpServletRequest request;
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        urlPathHelper.setRemoveSemicolonContent(false);
        this.handlerMapping.setUrlPathHelper(urlPathHelper);
        request = new MockHttpServletRequest();
        handleMatch(request, "/{cars}", "/cars;mvar=a%2Fb");
        MultiValueMap<String, String> matrixVariables = getMatrixVariables(request, "cars");
        Map<String, String> uriVariables = getUriTemplateVariables(request);
        Assert.assertNotNull(matrixVariables);
        Assert.assertEquals(Collections.singletonList("a/b"), matrixVariables.get("mvar"));
        Assert.assertEquals("cars", uriVariables.get("cars"));
    }

    @SuppressWarnings("unused")
    @Controller
    private static class TestController {
        @RequestMapping(value = "/foo", method = RequestMethod.GET)
        public void foo() {
        }

        @RequestMapping(value = "/foo", method = RequestMethod.GET, params = "p")
        public void fooParam() {
        }

        @RequestMapping(value = "/ba*", method = { RequestMethod.GET, RequestMethod.HEAD })
        public void bar() {
        }

        @RequestMapping("")
        public void empty() {
        }

        @RequestMapping(value = "/person/{id}", method = RequestMethod.PUT, consumes = "application/xml")
        public void consumes(@RequestBody
        String text) {
        }

        @RequestMapping(value = "/persons", produces = "application/xml")
        public String produces() {
            return "";
        }

        @RequestMapping(value = "/params", params = "foo=bar")
        public String param() {
            return "";
        }

        @RequestMapping(value = "/params", params = "bar=baz")
        public String param2() {
            return "";
        }

        @RequestMapping(value = "/content", produces = "application/xml")
        public String xmlContent() {
            return "";
        }

        @RequestMapping(value = "/content", produces = "!application/xml")
        public String nonXmlContent() {
            return "";
        }

        @RequestMapping(value = "/something", method = RequestMethod.OPTIONS)
        public HttpHeaders fooOptions() {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Allow", "PUT,POST");
            return headers;
        }
    }

    @SuppressWarnings("unused")
    @Controller
    private static class UserController {
        @RequestMapping(value = "/users", method = RequestMethod.GET, produces = "application/json")
        public void getUser() {
        }

        @RequestMapping(value = "/users", method = RequestMethod.PUT)
        public void saveUser() {
        }
    }

    private static class TestRequestMappingInfoHandlerMapping extends RequestMappingInfoHandlerMapping {
        public void registerHandler(Object handler) {
            detectHandlerMethods(handler);
        }

        @Override
        protected boolean isHandler(Class<?> beanType) {
            return (AnnotationUtils.findAnnotation(beanType, RequestMapping.class)) != null;
        }

        @Override
        protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
            RequestMapping annot = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            if (annot != null) {
                return new RequestMappingInfo(new org.springframework.web.servlet.mvc.condition.PatternsRequestCondition(annot.value(), getUrlPathHelper(), getPathMatcher(), true, true), new org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition(annot.method()), new org.springframework.web.servlet.mvc.condition.ParamsRequestCondition(annot.params()), new org.springframework.web.servlet.mvc.condition.HeadersRequestCondition(annot.headers()), new org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition(annot.consumes(), annot.headers()), new org.springframework.web.servlet.mvc.condition.ProducesRequestCondition(annot.produces(), annot.headers()), null);
            } else {
                return null;
            }
        }
    }
}

