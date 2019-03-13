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
package org.springframework.web.servlet.mvc.method.annotation;


import HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;


/**
 * Test fixture for {@link CrossOrigin @CrossOrigin} annotated methods.
 *
 * @author Sebastien Deleuze
 * @author Sam Brannen
 * @author Nicolas Labrot
 */
public class CrossOriginTests {
    private final CrossOriginTests.TestRequestMappingInfoHandlerMapping handlerMapping = new CrossOriginTests.TestRequestMappingInfoHandlerMapping();

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void noAnnotationWithoutOrigin() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/no");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        Assert.assertNull(getCorsConfiguration(chain, false));
    }

    // SPR-12931
    @Test
    public void noAnnotationWithOrigin() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setRequestURI("/no");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        Assert.assertNull(getCorsConfiguration(chain, false));
    }

    // SPR-12931
    @Test
    public void noAnnotationPostWithOrigin() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setMethod("POST");
        this.request.setRequestURI("/no");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        Assert.assertNull(getCorsConfiguration(chain, false));
    }

    @Test
    public void defaultAnnotation() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setRequestURI("/default");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "GET" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertNull(config.getAllowCredentials());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedHeaders().toArray());
        Assert.assertTrue(CollectionUtils.isEmpty(config.getExposedHeaders()));
        Assert.assertEquals(new Long(1800), config.getMaxAge());
    }

    @Test
    public void customized() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setRequestURI("/customized");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "DELETE" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "http://site1.com", "http://site2.com" }, config.getAllowedOrigins().toArray());
        Assert.assertArrayEquals(new String[]{ "header1", "header2" }, config.getAllowedHeaders().toArray());
        Assert.assertArrayEquals(new String[]{ "header3", "header4" }, config.getExposedHeaders().toArray());
        Assert.assertEquals(new Long(123), config.getMaxAge());
        Assert.assertFalse(config.getAllowCredentials());
    }

    @Test
    public void customOriginDefinedViaValueAttribute() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setRequestURI("/customOrigin");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertEquals(Arrays.asList("http://example.com"), config.getAllowedOrigins());
        Assert.assertNull(config.getAllowCredentials());
    }

    @Test
    public void customOriginDefinedViaPlaceholder() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setRequestURI("/someOrigin");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertEquals(Arrays.asList("http://example.com"), config.getAllowedOrigins());
        Assert.assertNull(config.getAllowCredentials());
    }

    @Test
    public void bogusAllowCredentialsValue() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(CoreMatchers.containsString("@CrossOrigin's allowCredentials"));
        exception.expectMessage(CoreMatchers.containsString("current value is [bogus]"));
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelControllerWithBogusAllowCredentialsValue());
    }

    @Test
    public void classLevel() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.ClassLevelController());
        this.request.setRequestURI("/foo");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "GET" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertFalse(config.getAllowCredentials());
        this.request.setRequestURI("/bar");
        chain = this.handlerMapping.getHandler(request);
        config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "GET" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertFalse(config.getAllowCredentials());
        this.request.setRequestURI("/baz");
        chain = this.handlerMapping.getHandler(request);
        config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "GET" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertTrue(config.getAllowCredentials());
    }

    // SPR-13468
    @Test
    public void classLevelComposedAnnotation() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.ClassLevelMappingWithComposedAnnotation());
        this.request.setRequestURI("/foo");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "GET" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "http://foo.com" }, config.getAllowedOrigins().toArray());
        Assert.assertTrue(config.getAllowCredentials());
    }

    // SPR-13468
    @Test
    public void methodLevelComposedAnnotation() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelMappingWithComposedAnnotation());
        this.request.setRequestURI("/foo");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, false);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "GET" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "http://foo.com" }, config.getAllowedOrigins().toArray());
        Assert.assertTrue(config.getAllowCredentials());
    }

    @Test
    public void preFlightRequest() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setMethod("OPTIONS");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.setRequestURI("/default");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, true);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "GET" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertNull(config.getAllowCredentials());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedHeaders().toArray());
        Assert.assertTrue(CollectionUtils.isEmpty(config.getExposedHeaders()));
        Assert.assertEquals(new Long(1800), config.getMaxAge());
    }

    @Test
    public void ambiguousHeaderPreFlightRequest() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setMethod("OPTIONS");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "header1");
        this.request.setRequestURI("/ambiguous-header");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, true);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedHeaders().toArray());
        Assert.assertTrue(config.getAllowCredentials());
        Assert.assertTrue(CollectionUtils.isEmpty(config.getExposedHeaders()));
        Assert.assertNull(config.getMaxAge());
    }

    @Test
    public void ambiguousProducesPreFlightRequest() throws Exception {
        this.handlerMapping.registerHandler(new CrossOriginTests.MethodLevelController());
        this.request.setMethod("OPTIONS");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.request.setRequestURI("/ambiguous-produces");
        HandlerExecutionChain chain = this.handlerMapping.getHandler(request);
        CorsConfiguration config = getCorsConfiguration(chain, true);
        Assert.assertNotNull(config);
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedMethods().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
        Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedHeaders().toArray());
        Assert.assertTrue(config.getAllowCredentials());
        Assert.assertTrue(CollectionUtils.isEmpty(config.getExposedHeaders()));
        Assert.assertNull(config.getMaxAge());
    }

    @Test
    public void preFlightRequestWithoutRequestMethodHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/default");
        request.addHeader(ORIGIN, "http://domain2.com");
        Assert.assertNull(this.handlerMapping.getHandler(request));
    }

    @Controller
    private static class MethodLevelController {
        @GetMapping("/no")
        public void noAnnotation() {
        }

        @PostMapping("/no")
        public void noAnnotationPost() {
        }

        @CrossOrigin
        @GetMapping(path = "/default")
        public void defaultAnnotation() {
        }

        @CrossOrigin
        @GetMapping(path = "/default", params = "q")
        public void defaultAnnotationWithParams() {
        }

        @CrossOrigin
        @GetMapping(path = "/ambiguous-header", headers = "header1=a")
        public void ambiguousHeader1a() {
        }

        @CrossOrigin
        @GetMapping(path = "/ambiguous-header", headers = "header1=b")
        public void ambiguousHeader1b() {
        }

        @CrossOrigin
        @GetMapping(path = "/ambiguous-produces", produces = "application/xml")
        public String ambiguousProducesXml() {
            return "<a></a>";
        }

        @CrossOrigin
        @GetMapping(path = "/ambiguous-produces", produces = "application/json")
        public String ambiguousProducesJson() {
            return "{}";
        }

        @CrossOrigin(origins = { "http://site1.com", "http://site2.com" }, allowedHeaders = { "header1", "header2" }, exposedHeaders = { "header3", "header4" }, methods = RequestMethod.DELETE, maxAge = 123, allowCredentials = "false")
        @RequestMapping(path = "/customized", method = { RequestMethod.GET, RequestMethod.POST })
        public void customized() {
        }

        @CrossOrigin("http://example.com")
        @RequestMapping("/customOrigin")
        public void customOriginDefinedViaValueAttribute() {
        }

        @CrossOrigin("${myOrigin}")
        @RequestMapping("/someOrigin")
        public void customOriginDefinedViaPlaceholder() {
        }
    }

    @Controller
    private static class MethodLevelControllerWithBogusAllowCredentialsValue {
        @CrossOrigin(allowCredentials = "bogus")
        @RequestMapping("/bogus")
        public void bogusAllowCredentialsValue() {
        }
    }

    @Controller
    @CrossOrigin(allowCredentials = "false")
    private static class ClassLevelController {
        @RequestMapping(path = "/foo", method = RequestMethod.GET)
        public void foo() {
        }

        @CrossOrigin
        @RequestMapping(path = "/bar", method = RequestMethod.GET)
        public void bar() {
        }

        @CrossOrigin(allowCredentials = "true")
        @RequestMapping(path = "/baz", method = RequestMethod.GET)
        public void baz() {
        }
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @CrossOrigin
    private @interface ComposedCrossOrigin {
        String[] origins() default {  };

        String allowCredentials() default "";
    }

    @Controller
    @CrossOriginTests.ComposedCrossOrigin(origins = "http://foo.com", allowCredentials = "true")
    private static class ClassLevelMappingWithComposedAnnotation {
        @RequestMapping(path = "/foo", method = RequestMethod.GET)
        public void foo() {
        }
    }

    @Controller
    private static class MethodLevelMappingWithComposedAnnotation {
        @RequestMapping(path = "/foo", method = RequestMethod.GET)
        @CrossOriginTests.ComposedCrossOrigin(origins = "http://foo.com", allowCredentials = "true")
        public void foo() {
        }
    }

    private static class TestRequestMappingInfoHandlerMapping extends RequestMappingHandlerMapping {
        public void registerHandler(Object handler) {
            detectHandlerMethods(handler);
        }

        @Override
        protected boolean isHandler(Class<?> beanType) {
            return (AnnotationUtils.findAnnotation(beanType, Controller.class)) != null;
        }

        @Override
        protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
            RequestMapping annotation = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (annotation != null) {
                return new RequestMappingInfo(new org.springframework.web.servlet.mvc.condition.PatternsRequestCondition(annotation.value(), getUrlPathHelper(), getPathMatcher(), true, true), new org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition(annotation.method()), new org.springframework.web.servlet.mvc.condition.ParamsRequestCondition(annotation.params()), new org.springframework.web.servlet.mvc.condition.HeadersRequestCondition(annotation.headers()), new org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition(annotation.consumes(), annotation.headers()), new org.springframework.web.servlet.mvc.condition.ProducesRequestCondition(annotation.produces(), annotation.headers()), null);
            } else {
                return null;
            }
        }
    }
}

