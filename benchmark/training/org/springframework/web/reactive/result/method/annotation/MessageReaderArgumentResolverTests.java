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


import MediaType.APPLICATION_JSON;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.lang.Nullable;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import rx.Observable;
import rx.Single;


/**
 * Unit tests for {@link AbstractMessageReaderArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class MessageReaderArgumentResolverTests {
    private AbstractMessageReaderArgumentResolver resolver = resolver(new Jackson2JsonDecoder());

    private BindingContext bindingContext;

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @SuppressWarnings("unchecked")
    @Test
    public void missingContentType() throws Exception {
        MockServerHttpRequest request = post("/path").body("{\"bar\":\"BARBAR\",\"foo\":\"FOOFOO\"}");
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        ResolvableType type = forClassWithGenerics(Mono.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Mono<Object> result = this.resolver.readBody(param, true, this.bindingContext, exchange);
        Mono<MessageReaderArgumentResolverTests.TestBean> value = ((Mono<MessageReaderArgumentResolverTests.TestBean>) (result.block(Duration.ofSeconds(1))));
        StepVerifier.create(value).expectError(UnsupportedMediaTypeStatusException.class).verify();
    }

    // More extensive "empty body" tests in RequestBody- and HttpEntityArgumentResolverTests
    // SPR-9942
    @Test
    @SuppressWarnings("unchecked")
    public void emptyBody() throws Exception {
        MockServerHttpRequest request = post("/path").contentType(APPLICATION_JSON).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        ResolvableType type = forClassWithGenerics(Mono.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Mono<MessageReaderArgumentResolverTests.TestBean> result = ((Mono<MessageReaderArgumentResolverTests.TestBean>) (this.resolver.readBody(param, true, this.bindingContext, exchange).block()));
        StepVerifier.create(result).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void monoTestBean() throws Exception {
        String body = "{\"bar\":\"BARBAR\",\"foo\":\"FOOFOO\"}";
        ResolvableType type = forClassWithGenerics(Mono.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Mono<Object> mono = resolveValue(param, body);
        Assert.assertEquals(new MessageReaderArgumentResolverTests.TestBean("FOOFOO", "BARBAR"), mono.block());
    }

    @Test
    public void fluxTestBean() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\",\"foo\":\"f2\"}]";
        ResolvableType type = forClassWithGenerics(Flux.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Flux<MessageReaderArgumentResolverTests.TestBean> flux = resolveValue(param, body);
        Assert.assertEquals(Arrays.asList(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), new MessageReaderArgumentResolverTests.TestBean("f2", "b2")), flux.collectList().block());
    }

    @Test
    public void singleTestBean() throws Exception {
        String body = "{\"bar\":\"b1\",\"foo\":\"f1\"}";
        ResolvableType type = forClassWithGenerics(Single.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Single<MessageReaderArgumentResolverTests.TestBean> single = resolveValue(param, body);
        Assert.assertEquals(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), single.toBlocking().value());
    }

    @Test
    public void rxJava2SingleTestBean() throws Exception {
        String body = "{\"bar\":\"b1\",\"foo\":\"f1\"}";
        ResolvableType type = forClassWithGenerics(Single.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        io.reactivex.Single<MessageReaderArgumentResolverTests.TestBean> single = resolveValue(param, body);
        Assert.assertEquals(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), single.blockingGet());
    }

    @Test
    public void rxJava2MaybeTestBean() throws Exception {
        String body = "{\"bar\":\"b1\",\"foo\":\"f1\"}";
        ResolvableType type = forClassWithGenerics(Maybe.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Maybe<MessageReaderArgumentResolverTests.TestBean> maybe = resolveValue(param, body);
        Assert.assertEquals(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), maybe.blockingGet());
    }

    @Test
    public void observableTestBean() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\",\"foo\":\"f2\"}]";
        ResolvableType type = forClassWithGenerics(Observable.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Observable<?> observable = resolveValue(param, body);
        Assert.assertEquals(Arrays.asList(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), new MessageReaderArgumentResolverTests.TestBean("f2", "b2")), observable.toList().toBlocking().first());
    }

    @Test
    public void rxJava2ObservableTestBean() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\",\"foo\":\"f2\"}]";
        ResolvableType type = forClassWithGenerics(Observable.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        io.reactivex.Observable<?> observable = resolveValue(param, body);
        Assert.assertEquals(Arrays.asList(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), new MessageReaderArgumentResolverTests.TestBean("f2", "b2")), observable.toList().blockingGet());
    }

    @Test
    public void flowableTestBean() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\",\"foo\":\"f2\"}]";
        ResolvableType type = forClassWithGenerics(Flowable.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Flowable<?> flowable = resolveValue(param, body);
        Assert.assertEquals(Arrays.asList(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), new MessageReaderArgumentResolverTests.TestBean("f2", "b2")), flowable.toList().blockingGet());
    }

    @Test
    public void futureTestBean() throws Exception {
        String body = "{\"bar\":\"b1\",\"foo\":\"f1\"}";
        ResolvableType type = forClassWithGenerics(CompletableFuture.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        CompletableFuture<?> future = resolveValue(param, body);
        Assert.assertEquals(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), future.get());
    }

    @Test
    public void testBean() throws Exception {
        String body = "{\"bar\":\"b1\",\"foo\":\"f1\"}";
        MethodParameter param = this.testMethod.arg(MessageReaderArgumentResolverTests.TestBean.class);
        MessageReaderArgumentResolverTests.TestBean value = resolveValue(param, body);
        Assert.assertEquals(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), value);
    }

    @Test
    public void map() throws Exception {
        String body = "{\"bar\":\"b1\",\"foo\":\"f1\"}";
        Map<String, String> map = new HashMap<>();
        map.put("foo", "f1");
        map.put("bar", "b1");
        ResolvableType type = forClassWithGenerics(Map.class, String.class, String.class);
        MethodParameter param = this.testMethod.arg(type);
        Map<String, String> actual = resolveValue(param, body);
        Assert.assertEquals(map, actual);
    }

    @Test
    public void list() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\",\"foo\":\"f2\"}]";
        ResolvableType type = forClassWithGenerics(List.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        List<?> list = resolveValue(param, body);
        Assert.assertEquals(Arrays.asList(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), new MessageReaderArgumentResolverTests.TestBean("f2", "b2")), list);
    }

    @Test
    public void monoList() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\",\"foo\":\"f2\"}]";
        ResolvableType type = forClassWithGenerics(Mono.class, forClassWithGenerics(List.class, MessageReaderArgumentResolverTests.TestBean.class));
        MethodParameter param = this.testMethod.arg(type);
        Mono<?> mono = resolveValue(param, body);
        List<?> list = ((List<?>) (mono.block(Duration.ofSeconds(5))));
        Assert.assertEquals(Arrays.asList(new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), new MessageReaderArgumentResolverTests.TestBean("f2", "b2")), list);
    }

    @Test
    public void array() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\",\"foo\":\"f2\"}]";
        MethodParameter param = this.testMethod.arg(MessageReaderArgumentResolverTests.TestBean[].class);
        MessageReaderArgumentResolverTests.TestBean[] value = resolveValue(param, body);
        Assert.assertArrayEquals(new MessageReaderArgumentResolverTests.TestBean[]{ new MessageReaderArgumentResolverTests.TestBean("f1", "b1"), new MessageReaderArgumentResolverTests.TestBean("f2", "b2") }, value);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validateMonoTestBean() throws Exception {
        String body = "{\"bar\":\"b1\"}";
        ResolvableType type = forClassWithGenerics(Mono.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Mono<MessageReaderArgumentResolverTests.TestBean> mono = resolveValue(param, body);
        StepVerifier.create(mono).expectNextCount(0).expectError(ServerWebInputException.class).verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validateFluxTestBean() throws Exception {
        String body = "[{\"bar\":\"b1\",\"foo\":\"f1\"},{\"bar\":\"b2\"}]";
        ResolvableType type = forClassWithGenerics(Flux.class, MessageReaderArgumentResolverTests.TestBean.class);
        MethodParameter param = this.testMethod.arg(type);
        Flux<MessageReaderArgumentResolverTests.TestBean> flux = resolveValue(param, body);
        StepVerifier.create(flux).expectNext(new MessageReaderArgumentResolverTests.TestBean("f1", "b1")).expectError(ServerWebInputException.class).verify();
    }

    // SPR-9964
    @Test
    public void parameterizedMethodArgument() throws Exception {
        Method method = MessageReaderArgumentResolverTests.AbstractParameterizedController.class.getMethod("handleDto", MessageReaderArgumentResolverTests.Identifiable.class);
        HandlerMethod handlerMethod = new HandlerMethod(new MessageReaderArgumentResolverTests.ConcreteParameterizedController(), method);
        MethodParameter methodParam = handlerMethod.getMethodParameters()[0];
        MessageReaderArgumentResolverTests.SimpleBean simpleBean = resolveValue(methodParam, "{\"name\" : \"Jad\"}");
        Assert.assertEquals("Jad", simpleBean.getName());
    }

    @XmlRootElement
    @SuppressWarnings("unused")
    private static class TestBean {
        private String foo;

        private String bar;

        public TestBean() {
        }

        TestBean(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return this.bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (o instanceof MessageReaderArgumentResolverTests.TestBean) {
                MessageReaderArgumentResolverTests.TestBean other = ((MessageReaderArgumentResolverTests.TestBean) (o));
                return (this.foo.equals(other.foo)) && (this.bar.equals(other.bar));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (31 * (foo.hashCode())) + (bar.hashCode());
        }

        @Override
        public String toString() {
            return (((("TestBean[foo='" + (this.foo)) + "\'") + ", bar='") + (this.bar)) + "\']";
        }
    }

    private static class TestBeanValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return clazz.equals(MessageReaderArgumentResolverTests.TestBean.class);
        }

        @Override
        public void validate(@Nullable
        Object target, Errors errors) {
            MessageReaderArgumentResolverTests.TestBean testBean = ((MessageReaderArgumentResolverTests.TestBean) (target));
            if ((testBean.getFoo()) == null) {
                errors.rejectValue("foo", "nullValue");
            }
        }
    }

    private abstract static class AbstractParameterizedController<DTO extends MessageReaderArgumentResolverTests.Identifiable> {
        @SuppressWarnings("unused")
        public void handleDto(DTO dto) {
        }
    }

    private static class ConcreteParameterizedController extends MessageReaderArgumentResolverTests.AbstractParameterizedController<MessageReaderArgumentResolverTests.SimpleBean> {}

    private interface Identifiable extends Serializable {
        Long getId();

        void setId(Long id);
    }

    @SuppressWarnings({ "serial", "unused" })
    private static class SimpleBean implements MessageReaderArgumentResolverTests.Identifiable {
        private Long id;

        private String name;

        @Override
        public Long getId() {
            return id;
        }

        @Override
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

