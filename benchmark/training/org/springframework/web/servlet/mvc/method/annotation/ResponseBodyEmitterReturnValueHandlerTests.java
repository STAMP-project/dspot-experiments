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


import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.test.MockAsyncContext;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.ModelAndViewContainer;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;


/**
 * Unit tests for ResponseBodyEmitterReturnValueHandler.
 *
 * @author Rossen Stoyanchev
 */
public class ResponseBodyEmitterReturnValueHandlerTests {
    private ResponseBodyEmitterReturnValueHandler handler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private NativeWebRequest webRequest;

    private final ModelAndViewContainer mavContainer = new ModelAndViewContainer();

    @Test
    public void supportsReturnTypes() throws Exception {
        Assert.assertTrue(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseBodyEmitter.class)));
        Assert.assertTrue(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(SseEmitter.class)));
        Assert.assertTrue(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseEntity.class, ResponseBodyEmitter.class)));
        Assert.assertTrue(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(Flux.class, String.class)));
        Assert.assertTrue(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(forClassWithGenerics(ResponseEntity.class, forClassWithGenerics(Flux.class, String.class)))));
    }

    @Test
    public void doesNotSupportReturnTypes() throws Exception {
        Assert.assertFalse(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseEntity.class, String.class)));
        Assert.assertFalse(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(forClassWithGenerics(ResponseEntity.class, forClassWithGenerics(AtomicReference.class, String.class)))));
        Assert.assertFalse(this.handler.supportsReturnType(on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseEntity.class)));
    }

    @Test
    public void responseBodyEmitter() throws Exception {
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseBodyEmitter.class);
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        this.handler.handleReturnValue(emitter, type, this.mavContainer, this.webRequest);
        Assert.assertTrue(this.request.isAsyncStarted());
        Assert.assertEquals("", this.response.getContentAsString());
        ResponseBodyEmitterReturnValueHandlerTests.SimpleBean bean = new ResponseBodyEmitterReturnValueHandlerTests.SimpleBean();
        bean.setId(1L);
        bean.setName("Joe");
        emitter.send(bean);
        emitter.send("\n");
        bean.setId(2L);
        bean.setName("John");
        emitter.send(bean);
        emitter.send("\n");
        bean.setId(3L);
        bean.setName("Jason");
        emitter.send(bean);
        Assert.assertEquals(("{\"id\":1,\"name\":\"Joe\"}\n" + ("{\"id\":2,\"name\":\"John\"}\n" + "{\"id\":3,\"name\":\"Jason\"}")), this.response.getContentAsString());
        MockAsyncContext asyncContext = ((MockAsyncContext) (this.request.getAsyncContext()));
        Assert.assertNull(asyncContext.getDispatchedPath());
        emitter.complete();
        Assert.assertNotNull(asyncContext.getDispatchedPath());
    }

    @Test
    public void responseBodyEmitterWithTimeoutValue() throws Exception {
        AsyncWebRequest asyncWebRequest = Mockito.mock(AsyncWebRequest.class);
        WebAsyncUtils.getAsyncManager(this.request).setAsyncWebRequest(asyncWebRequest);
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(19000L);
        emitter.onTimeout(Mockito.mock(Runnable.class));
        emitter.onCompletion(Mockito.mock(Runnable.class));
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseBodyEmitter.class);
        this.handler.handleReturnValue(emitter, type, this.mavContainer, this.webRequest);
        Mockito.verify(asyncWebRequest).setTimeout(19000L);
        Mockito.verify(asyncWebRequest).addTimeoutHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(asyncWebRequest, Mockito.times(2)).addCompletionHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(asyncWebRequest).startAsync();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void responseBodyEmitterWithErrorValue() throws Exception {
        AsyncWebRequest asyncWebRequest = Mockito.mock(AsyncWebRequest.class);
        WebAsyncUtils.getAsyncManager(this.request).setAsyncWebRequest(asyncWebRequest);
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(19000L);
        emitter.onError(Mockito.mock(Consumer.class));
        emitter.onCompletion(Mockito.mock(Runnable.class));
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseBodyEmitter.class);
        this.handler.handleReturnValue(emitter, type, this.mavContainer, this.webRequest);
        Mockito.verify(asyncWebRequest).addErrorHandler(ArgumentMatchers.any(Consumer.class));
        Mockito.verify(asyncWebRequest, Mockito.times(2)).addCompletionHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(asyncWebRequest).startAsync();
    }

    @Test
    public void sseEmitter() throws Exception {
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(SseEmitter.class);
        SseEmitter emitter = new SseEmitter();
        this.handler.handleReturnValue(emitter, type, this.mavContainer, this.webRequest);
        Assert.assertTrue(this.request.isAsyncStarted());
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("text/event-stream;charset=UTF-8", this.response.getContentType());
        ResponseBodyEmitterReturnValueHandlerTests.SimpleBean bean1 = new ResponseBodyEmitterReturnValueHandlerTests.SimpleBean();
        bean1.setId(1L);
        bean1.setName("Joe");
        ResponseBodyEmitterReturnValueHandlerTests.SimpleBean bean2 = new ResponseBodyEmitterReturnValueHandlerTests.SimpleBean();
        bean2.setId(2L);
        bean2.setName("John");
        emitter.send(SseEmitter.event().comment("a test").name("update").id("1").reconnectTime(5000L).data(bean1).data(bean2));
        Assert.assertEquals((":a test\n" + ((((("event:update\n" + "id:1\n") + "retry:5000\n") + "data:{\"id\":1,\"name\":\"Joe\"}\n") + "data:{\"id\":2,\"name\":\"John\"}\n") + "\n")), this.response.getContentAsString());
    }

    @Test
    public void responseBodyFlux() throws Exception {
        this.request.addHeader("Accept", "text/event-stream");
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(Flux.class, String.class);
        EmitterProcessor<String> processor = EmitterProcessor.create();
        this.handler.handleReturnValue(processor, type, this.mavContainer, this.webRequest);
        Assert.assertTrue(this.request.isAsyncStarted());
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("text/event-stream;charset=UTF-8", this.response.getContentType());
        processor.onNext("foo");
        processor.onNext("bar");
        processor.onNext("baz");
        processor.onComplete();
        Assert.assertEquals("data:foo\n\ndata:bar\n\ndata:baz\n\n", this.response.getContentAsString());
    }

    @Test
    public void responseEntitySse() throws Exception {
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseEntity.class, SseEmitter.class);
        ResponseEntity<SseEmitter> entity = ResponseEntity.ok().header("foo", "bar").body(new SseEmitter());
        this.handler.handleReturnValue(entity, type, this.mavContainer, this.webRequest);
        Assert.assertTrue(this.request.isAsyncStarted());
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("text/event-stream;charset=UTF-8", this.response.getContentType());
        Assert.assertEquals("bar", this.response.getHeader("foo"));
    }

    @Test
    public void responseEntitySseNoContent() throws Exception {
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseEntity.class, SseEmitter.class);
        ResponseEntity<?> entity = ResponseEntity.noContent().header("foo", "bar").build();
        this.handler.handleReturnValue(entity, type, this.mavContainer, this.webRequest);
        Assert.assertFalse(this.request.isAsyncStarted());
        Assert.assertEquals(204, this.response.getStatus());
        Assert.assertEquals(Collections.singletonList("bar"), this.response.getHeaders("foo"));
    }

    @Test
    public void responseEntityFlux() throws Exception {
        EmitterProcessor<String> processor = EmitterProcessor.create();
        ResponseEntity<Flux<String>> entity = ResponseEntity.ok().body(processor);
        ResolvableType bodyType = forClassWithGenerics(Flux.class, String.class);
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseEntity.class, bodyType);
        this.handler.handleReturnValue(entity, type, this.mavContainer, this.webRequest);
        Assert.assertTrue(this.request.isAsyncStarted());
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("text/plain", this.response.getContentType());
        processor.onNext("foo");
        processor.onNext("bar");
        processor.onNext("baz");
        processor.onComplete();
        Assert.assertEquals("foobarbaz", this.response.getContentAsString());
    }

    // SPR-17076
    @Test
    public void responseEntityFluxWithCustomHeader() throws Exception {
        EmitterProcessor<ResponseBodyEmitterReturnValueHandlerTests.SimpleBean> processor = EmitterProcessor.create();
        ResponseEntity<Flux<ResponseBodyEmitterReturnValueHandlerTests.SimpleBean>> entity = ResponseEntity.ok().header("x-foo", "bar").body(processor);
        ResolvableType bodyType = forClassWithGenerics(Flux.class, ResponseBodyEmitterReturnValueHandlerTests.SimpleBean.class);
        MethodParameter type = on(ResponseBodyEmitterReturnValueHandlerTests.TestController.class).resolveReturnType(ResponseEntity.class, bodyType);
        this.handler.handleReturnValue(entity, type, this.mavContainer, this.webRequest);
        Assert.assertTrue(this.request.isAsyncStarted());
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("bar", this.response.getHeader("x-foo"));
        Assert.assertFalse(this.response.isCommitted());
    }

    @SuppressWarnings("unused")
    private static class TestController {
        private ResponseBodyEmitter h1() {
            return null;
        }

        private ResponseEntity<ResponseBodyEmitter> h2() {
            return null;
        }

        private SseEmitter h3() {
            return null;
        }

        private ResponseEntity<SseEmitter> h4() {
            return null;
        }

        private ResponseEntity<String> h5() {
            return null;
        }

        private ResponseEntity<AtomicReference<String>> h6() {
            return null;
        }

        private ResponseEntity<?> h7() {
            return null;
        }

        private Flux<String> h8() {
            return null;
        }

        private ResponseEntity<Flux<String>> h9() {
            return null;
        }

        private ResponseEntity<Flux<ResponseBodyEmitterReturnValueHandlerTests.SimpleBean>> h10() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static class SimpleBean {
        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

