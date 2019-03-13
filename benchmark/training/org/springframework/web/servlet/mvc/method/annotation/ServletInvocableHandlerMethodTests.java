/**
 * Copyright 2002-2017 the original author or authors.
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


import HttpStatus.BAD_REQUEST;
import ReactiveTypeHandler.CollectedValuesList;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Flux;


/**
 * Test fixture with {@link ServletInvocableHandlerMethod}.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @author Juergen Hoeller
 */
public class ServletInvocableHandlerMethodTests {
    private final List<HttpMessageConverter<?>> converters = Collections.singletonList(new StringHttpMessageConverter());

    private final HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();

    private final HandlerMethodReturnValueHandlerComposite returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();

    private final ModelAndViewContainer mavContainer = new ModelAndViewContainer();

    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private final ServletWebRequest webRequest = new ServletWebRequest(this.request, this.response);

    @Test
    public void invokeAndHandle_VoidWithResponseStatus() throws Exception {
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.Handler(), "responseStatus");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertTrue("Null return value + @ResponseStatus should result in 'request handled'", this.mavContainer.isRequestHandled());
        Assert.assertEquals(BAD_REQUEST.value(), this.response.getStatus());
    }

    @Test
    public void invokeAndHandle_VoidWithComposedResponseStatus() throws Exception {
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.Handler(), "composedResponseStatus");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertTrue("Null return value + @ComposedResponseStatus should result in 'request handled'", this.mavContainer.isRequestHandled());
        Assert.assertEquals(BAD_REQUEST.value(), this.response.getStatus());
    }

    @Test
    public void invokeAndHandle_VoidWithTypeLevelResponseStatus() throws Exception {
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.ResponseStatusHandler(), "handle");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertTrue(this.mavContainer.isRequestHandled());
        Assert.assertEquals(BAD_REQUEST.value(), this.response.getStatus());
    }

    @Test
    public void invokeAndHandle_VoidWithHttpServletResponseArgument() throws Exception {
        this.argumentResolvers.addResolver(new ServletResponseMethodArgumentResolver());
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.Handler(), "httpServletResponse", HttpServletResponse.class);
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertTrue("Null return value + HttpServletResponse arg should result in 'request handled'", this.mavContainer.isRequestHandled());
    }

    @Test
    public void invokeAndHandle_VoidRequestNotModified() throws Exception {
        this.request.addHeader("If-Modified-Since", ((10 * 1000) * 1000));
        int lastModifiedTimestamp = 1000 * 1000;
        this.webRequest.checkNotModified(lastModifiedTimestamp);
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.Handler(), "notModified");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertTrue("Null return value + 'not modified' request should result in 'request handled'", this.mavContainer.isRequestHandled());
    }

    // SPR-9159
    @Test
    public void invokeAndHandle_NotVoidWithResponseStatusAndReason() throws Exception {
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.Handler(), "responseStatusWithReason");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertTrue("When a status reason w/ used, the request is handled", this.mavContainer.isRequestHandled());
        Assert.assertEquals(BAD_REQUEST.value(), this.response.getStatus());
        Assert.assertEquals("400 Bad Request", this.response.getErrorMessage());
    }

    @Test(expected = HttpMessageNotWritableException.class)
    public void invokeAndHandle_Exception() throws Exception {
        this.returnValueHandlers.addHandler(new ServletInvocableHandlerMethodTests.ExceptionRaisingReturnValueHandler());
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.Handler(), "handle");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.fail("Expected exception");
    }

    @Test
    public void invokeAndHandle_DynamicReturnValue() throws Exception {
        this.argumentResolvers.addResolver(new RequestParamMethodArgumentResolver(null, false));
        this.returnValueHandlers.addHandler(new ViewMethodReturnValueHandler());
        this.returnValueHandlers.addHandler(new ViewNameMethodReturnValueHandler());
        // Invoke without a request parameter (String return value)
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.Handler(), "dynamicReturnValue", String.class);
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertNotNull(this.mavContainer.getView());
        Assert.assertEquals(RedirectView.class, this.mavContainer.getView().getClass());
        // Invoke with a request parameter (RedirectView return value)
        this.request.setParameter("param", "value");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals("view", this.mavContainer.getViewName());
    }

    @Test
    public void wrapConcurrentResult_MethodLevelResponseBody() throws Exception {
        wrapConcurrentResult_ResponseBody(new ServletInvocableHandlerMethodTests.MethodLevelResponseBodyHandler(), "bar", String.class);
    }

    @Test
    public void wrapConcurrentResult_MethodLevelResponseBodyEmpty() throws Exception {
        wrapConcurrentResult_ResponseBody(new ServletInvocableHandlerMethodTests.MethodLevelResponseBodyHandler(), null, String.class);
    }

    @Test
    public void wrapConcurrentResult_TypeLevelResponseBody() throws Exception {
        wrapConcurrentResult_ResponseBody(new ServletInvocableHandlerMethodTests.TypeLevelResponseBodyHandler(), "bar", String.class);
    }

    @Test
    public void wrapConcurrentResult_TypeLevelResponseBodyEmpty() throws Exception {
        wrapConcurrentResult_ResponseBody(new ServletInvocableHandlerMethodTests.TypeLevelResponseBodyHandler(), null, String.class);
    }

    @Test
    public void wrapConcurrentResult_DeferredResultSubclass() throws Exception {
        wrapConcurrentResult_ResponseBody(new ServletInvocableHandlerMethodTests.DeferredResultSubclassHandler(), "bar", String.class);
    }

    @Test
    public void wrapConcurrentResult_DeferredResultSubclassEmpty() throws Exception {
        wrapConcurrentResult_ResponseBody(new ServletInvocableHandlerMethodTests.DeferredResultSubclassHandler(), null, ServletInvocableHandlerMethodTests.CustomDeferredResult.class);
    }

    @Test
    public void wrapConcurrentResult_ResponseEntity() throws Exception {
        this.returnValueHandlers.addHandler(new HttpEntityMethodProcessor(this.converters));
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.ResponseEntityHandler(), "handleDeferred");
        handlerMethod = handlerMethod.wrapConcurrentResult(new ResponseEntity("bar", HttpStatus.OK));
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals("bar", this.response.getContentAsString());
    }

    // SPR-12287
    @Test
    public void wrapConcurrentResult_ResponseEntityNullBody() throws Exception {
        this.returnValueHandlers.addHandler(new HttpEntityMethodProcessor(this.converters));
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.ResponseEntityHandler(), "handleDeferred");
        handlerMethod = handlerMethod.wrapConcurrentResult(new ResponseEntity(HttpStatus.OK));
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("", this.response.getContentAsString());
    }

    @Test
    public void wrapConcurrentResult_ResponseEntityNullReturnValue() throws Exception {
        this.returnValueHandlers.addHandler(new HttpEntityMethodProcessor(this.converters));
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.ResponseEntityHandler(), "handleDeferred");
        handlerMethod = handlerMethod.wrapConcurrentResult(null);
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("", this.response.getContentAsString());
    }

    @Test
    public void wrapConcurrentResult_ResponseBodyEmitter() throws Exception {
        this.returnValueHandlers.addHandler(new ResponseBodyEmitterReturnValueHandler(this.converters));
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.StreamingHandler(), "handleEmitter");
        handlerMethod = handlerMethod.wrapConcurrentResult(null);
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("", this.response.getContentAsString());
    }

    @Test
    public void wrapConcurrentResult_StreamingResponseBody() throws Exception {
        this.returnValueHandlers.addHandler(new StreamingResponseBodyReturnValueHandler());
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.StreamingHandler(), "handleStreamBody");
        handlerMethod = handlerMethod.wrapConcurrentResult(null);
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("", this.response.getContentAsString());
    }

    @Test
    public void wrapConcurrentResult_CollectedValuesList() throws Exception {
        List<HttpMessageConverter<?>> converters = Collections.singletonList(new MappingJackson2HttpMessageConverter());
        ResolvableType elementType = ResolvableType.forClass(List.class);
        ReactiveTypeHandler.CollectedValuesList result = new ReactiveTypeHandler.CollectedValuesList(elementType);
        result.add(Arrays.asList("foo1", "bar1"));
        result.add(Arrays.asList("foo2", "bar2"));
        ContentNegotiationManager manager = new ContentNegotiationManager();
        this.returnValueHandlers.addHandler(new RequestResponseBodyMethodProcessor(converters, manager));
        ServletInvocableHandlerMethod hm = getHandlerMethod(new ServletInvocableHandlerMethodTests.MethodLevelResponseBodyHandler(), "handleFluxOfLists");
        hm = hm.wrapConcurrentResult(result);
        hm.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("[[\"foo1\",\"bar1\"],[\"foo2\",\"bar2\"]]", this.response.getContentAsString());
    }

    // SPR-15478
    @Test
    public void wrapConcurrentResult_CollectedValuesListWithResponseEntity() throws Exception {
        List<HttpMessageConverter<?>> converters = Collections.singletonList(new MappingJackson2HttpMessageConverter());
        ResolvableType elementType = ResolvableType.forClass(ServletInvocableHandlerMethodTests.Bar.class);
        ReactiveTypeHandler.CollectedValuesList result = new ReactiveTypeHandler.CollectedValuesList(elementType);
        result.add(new ServletInvocableHandlerMethodTests.Bar("foo"));
        result.add(new ServletInvocableHandlerMethodTests.Bar("bar"));
        ContentNegotiationManager manager = new ContentNegotiationManager();
        this.returnValueHandlers.addHandler(new RequestResponseBodyMethodProcessor(converters, manager));
        ServletInvocableHandlerMethod hm = getHandlerMethod(new ServletInvocableHandlerMethodTests.ResponseEntityHandler(), "handleFlux");
        hm = hm.wrapConcurrentResult(result);
        hm.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("[{\"value\":\"foo\"},{\"value\":\"bar\"}]", this.response.getContentAsString());
    }

    // SPR-12287 (16/Oct/14 comments)
    @Test
    public void responseEntityRawTypeWithNullBody() throws Exception {
        this.returnValueHandlers.addHandler(new HttpEntityMethodProcessor(this.converters));
        ServletInvocableHandlerMethod handlerMethod = getHandlerMethod(new ServletInvocableHandlerMethodTests.ResponseEntityHandler(), "handleRawType");
        handlerMethod.invokeAndHandle(this.webRequest, this.mavContainer);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("", this.response.getContentAsString());
    }

    @SuppressWarnings("unused")
    @ResponseStatus
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComposedResponseStatus {
        @AliasFor(annotation = ResponseStatus.class, attribute = "code")
        HttpStatus responseStatus() default HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @SuppressWarnings("unused")
    private static class Handler {
        public String handle() {
            return "view";
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public void responseStatus() {
        }

        @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "400 Bad Request")
        public String responseStatusWithReason() {
            return "foo";
        }

        @ServletInvocableHandlerMethodTests.ComposedResponseStatus(responseStatus = HttpStatus.BAD_REQUEST)
        public void composedResponseStatus() {
        }

        public void httpServletResponse(HttpServletResponse response) {
        }

        public void notModified() {
        }

        public Object dynamicReturnValue(@RequestParam(required = false)
        String param) {
            return param != null ? "view" : new RedirectView("redirectView");
        }
    }

    @SuppressWarnings("unused")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private static class ResponseStatusHandler {
        public void handle() {
        }
    }

    private static class MethodLevelResponseBodyHandler {
        @ResponseBody
        public DeferredResult<String> handle() {
            return null;
        }

        // Unusual but legal return type
        // Properly test generic type handling of Flux values collected to a List
        @ResponseBody
        public Flux<List<String>> handleFluxOfLists() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    @ResponseBody
    private static class TypeLevelResponseBodyHandler {
        public DeferredResult<String> handle() {
            return null;
        }
    }

    private static class DeferredResultSubclassHandler {
        @ResponseBody
        public ServletInvocableHandlerMethodTests.CustomDeferredResult handle() {
            return null;
        }
    }

    private static class CustomDeferredResult extends DeferredResult<String> {}

    @SuppressWarnings("unused")
    private static class ResponseEntityHandler {
        public DeferredResult<ResponseEntity<String>> handleDeferred() {
            return null;
        }

        public ResponseEntity<Void> handleRawType() {
            return null;
        }

        public ResponseEntity<Flux<ServletInvocableHandlerMethodTests.Bar>> handleFlux() {
            return null;
        }
    }

    private static class ExceptionRaisingReturnValueHandler implements HandlerMethodReturnValueHandler {
        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return true;
        }

        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            throw new HttpMessageNotWritableException("oops, can't write");
        }
    }

    @SuppressWarnings("unused")
    private static class StreamingHandler {
        public ResponseBodyEmitter handleEmitter() {
            return null;
        }

        public StreamingResponseBody handleStreamBody() {
            return null;
        }
    }

    private static class Bar {
        private final String value;

        public Bar(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public String getValue() {
            return this.value;
        }
    }
}

