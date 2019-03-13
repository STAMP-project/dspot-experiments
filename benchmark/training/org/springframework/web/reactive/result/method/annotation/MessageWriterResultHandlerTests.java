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
package org.springframework.web.reactive.result.method.annotation;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.reactivex.Flowable;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.HandlerMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import rx.Completable;
import rx.Observable;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


/**
 * Unit tests for {@link AbstractMessageWriterResultHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class MessageWriterResultHandlerTests {
    private final AbstractMessageWriterResultHandler resultHandler = initResultHandler();

    private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path"));

    // SPR-12894
    @Test
    public void useDefaultContentType() throws Exception {
        Resource body = new ClassPathResource("logo.png", getClass());
        MethodParameter type = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Resource.class);
        this.resultHandler.writeBody(body, type, this.exchange).block(Duration.ofSeconds(5));
        Assert.assertEquals("image/png", this.exchange.getResponse().getHeaders().getFirst("Content-Type"));
    }

    // SPR-13631
    @Test
    public void useDefaultCharset() throws Exception {
        this.exchange.getAttributes().put(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(APPLICATION_JSON));
        String body = "foo";
        MethodParameter type = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(String.class);
        this.resultHandler.writeBody(body, type, this.exchange).block(Duration.ofSeconds(5));
        Assert.assertEquals(APPLICATION_JSON_UTF8, this.exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    public void voidReturnType() throws Exception {
        testVoid(null, on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(void.class));
        testVoid(Mono.empty(), on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Mono.class, Void.class));
        testVoid(Flux.empty(), on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Flux.class, Void.class));
        testVoid(Completable.complete(), on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Completable.class));
        testVoid(Observable.empty(), on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Observable.class, Void.class));
        MethodParameter type = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Completable.class);
        testVoid(io.reactivex.Completable.complete(), type);
        type = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Observable.class, Void.class);
        testVoid(io.reactivex.Observable.empty(), type);
        type = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(Flowable.class, Void.class);
        testVoid(Flowable.empty(), type);
    }

    // SPR-13135
    @Test
    public void unsupportedReturnType() throws Exception {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        MethodParameter type = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(OutputStream.class);
        HttpMessageWriter<?> writer = new org.springframework.http.codec.EncoderHttpMessageWriter(new ByteBufferEncoder());
        Mono<Void> mono = initResultHandler(writer).writeBody(body, type, this.exchange);
        StepVerifier.create(mono).expectError(IllegalStateException.class).verify();
    }

    // SPR-12811
    @Test
    public void jacksonTypeOfListElement() throws Exception {
        MethodParameter returnType = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(List.class, MessageWriterResultHandlerTests.ParentClass.class);
        List<MessageWriterResultHandlerTests.ParentClass> body = Arrays.asList(new MessageWriterResultHandlerTests.Foo("foo"), new MessageWriterResultHandlerTests.Bar("bar"));
        this.resultHandler.writeBody(body, returnType, this.exchange).block(Duration.ofSeconds(5));
        Assert.assertEquals(APPLICATION_JSON_UTF8, this.exchange.getResponse().getHeaders().getContentType());
        assertResponseBody(("[{\"type\":\"foo\",\"parentProperty\":\"foo\"}," + "{\"type\":\"bar\",\"parentProperty\":\"bar\"}]"));
    }

    // SPR-13318
    @Test
    public void jacksonTypeWithSubType() throws Exception {
        MessageWriterResultHandlerTests.SimpleBean body = new MessageWriterResultHandlerTests.SimpleBean(123L, "foo");
        MethodParameter type = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(MessageWriterResultHandlerTests.Identifiable.class);
        this.resultHandler.writeBody(body, type, this.exchange).block(Duration.ofSeconds(5));
        Assert.assertEquals(APPLICATION_JSON_UTF8, this.exchange.getResponse().getHeaders().getContentType());
        assertResponseBody("{\"id\":123,\"name\":\"foo\"}");
    }

    // SPR-13318
    @Test
    public void jacksonTypeWithSubTypeOfListElement() throws Exception {
        MethodParameter returnType = on(MessageWriterResultHandlerTests.TestController.class).resolveReturnType(List.class, MessageWriterResultHandlerTests.Identifiable.class);
        List<MessageWriterResultHandlerTests.SimpleBean> body = Arrays.asList(new MessageWriterResultHandlerTests.SimpleBean(123L, "foo"), new MessageWriterResultHandlerTests.SimpleBean(456L, "bar"));
        this.resultHandler.writeBody(body, returnType, this.exchange).block(Duration.ofSeconds(5));
        Assert.assertEquals(APPLICATION_JSON_UTF8, this.exchange.getResponse().getHeaders().getContentType());
        assertResponseBody("[{\"id\":123,\"name\":\"foo\"},{\"id\":456,\"name\":\"bar\"}]");
    }

    @JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
    @SuppressWarnings("unused")
    private static class ParentClass {
        private String parentProperty;

        public ParentClass() {
        }

        ParentClass(String parentProperty) {
            this.parentProperty = parentProperty;
        }

        public String getParentProperty() {
            return parentProperty;
        }

        public void setParentProperty(String parentProperty) {
            this.parentProperty = parentProperty;
        }
    }

    @JsonTypeName("foo")
    private static class Foo extends MessageWriterResultHandlerTests.ParentClass {
        public Foo(String parentProperty) {
            super(parentProperty);
        }
    }

    @JsonTypeName("bar")
    private static class Bar extends MessageWriterResultHandlerTests.ParentClass {
        Bar(String parentProperty) {
            super(parentProperty);
        }
    }

    private interface Identifiable extends Serializable {
        @SuppressWarnings("unused")
        Long getId();
    }

    @SuppressWarnings({ "serial" })
    private static class SimpleBean implements MessageWriterResultHandlerTests.Identifiable {
        private Long id;

        private String name;

        SimpleBean(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public Long getId() {
            return id;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }
    }

    @SuppressWarnings("unused")
    private static class TestController {
        Resource resource() {
            return null;
        }

        String string() {
            return null;
        }

        void voidReturn() {
        }

        Mono<Void> monoVoid() {
            return null;
        }

        Completable completable() {
            return null;
        }

        Completable rxJava2Completable() {
            return null;
        }

        Flux<Void> fluxVoid() {
            return null;
        }

        Observable<Void> observableVoid() {
            return null;
        }

        io.reactivex.Observable<Void> rxJava2ObservableVoid() {
            return null;
        }

        Flowable<Void> flowableVoid() {
            return null;
        }

        OutputStream outputStream() {
            return null;
        }

        List<MessageWriterResultHandlerTests.ParentClass> listParentClass() {
            return null;
        }

        MessageWriterResultHandlerTests.Identifiable identifiable() {
            return null;
        }

        List<MessageWriterResultHandlerTests.Identifiable> listIdentifiable() {
            return null;
        }
    }
}

