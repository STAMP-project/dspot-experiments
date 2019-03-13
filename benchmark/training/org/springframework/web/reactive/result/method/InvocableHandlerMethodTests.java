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


import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link InvocableHandlerMethod}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class InvocableHandlerMethodTests {
    private final MockServerWebExchange exchange = MockServerWebExchange.from(get("http://localhost:8080/path"));

    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

    @Test
    public void resolveArg() {
        this.resolvers.add(stubResolver("value1"));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( o) -> o.singleArg(null)).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        assertHandlerResultValue(mono, "success:value1");
    }

    @Test
    public void resolveNoArgValue() {
        this.resolvers.add(stubResolver(Mono.empty()));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( o) -> o.singleArg(null)).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        assertHandlerResultValue(mono, "success:null");
    }

    @Test
    public void resolveNoArgs() {
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(InvocableHandlerMethodTests.TestController::noArgs).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        assertHandlerResultValue(mono, "success");
    }

    @Test
    public void cannotResolveArg() {
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( o) -> o.singleArg(null)).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        try {
            mono.block();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertThat(ex.getMessage(), is((("Could not resolve parameter [0] in " + (method.toGenericString())) + ": No suitable resolver")));
        }
    }

    @Test
    public void resolveProvidedArg() {
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( o) -> o.singleArg(null)).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method, "value1");
        assertHandlerResultValue(mono, "success:value1");
    }

    @Test
    public void resolveProvidedArgFirst() {
        this.resolvers.add(stubResolver("value1"));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( o) -> o.singleArg(null)).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method, "value2");
        assertHandlerResultValue(mono, "success:value2");
    }

    @Test
    public void exceptionInResolvingArg() {
        this.resolvers.add(stubResolver(Mono.error(new UnsupportedMediaTypeStatusException("boo"))));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( o) -> o.singleArg(null)).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        try {
            mono.block();
            Assert.fail("Expected UnsupportedMediaTypeStatusException");
        } catch (UnsupportedMediaTypeStatusException ex) {
            Assert.assertThat(ex.getMessage(), is("415 UNSUPPORTED_MEDIA_TYPE \"boo\""));
        }
    }

    @Test
    public void illegalArgumentException() {
        this.resolvers.add(stubResolver(1));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( o) -> o.singleArg(null)).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        try {
            mono.block();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertNotNull("Exception not wrapped", ex.getCause());
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
            Assert.assertTrue(ex.getMessage().contains("Controller ["));
            Assert.assertTrue(ex.getMessage().contains("Method ["));
            Assert.assertTrue(ex.getMessage().contains("with argument values:"));
            Assert.assertTrue(ex.getMessage().contains("[0] [type=java.lang.Integer] [value=1]"));
        }
    }

    @Test
    public void invocationTargetException() {
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(InvocableHandlerMethodTests.TestController::exceptionMethod).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        try {
            mono.block();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertThat(ex.getMessage(), is("boo"));
        }
    }

    @Test
    public void responseStatusAnnotation() {
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(InvocableHandlerMethodTests.TestController::created).method();
        Mono<HandlerResult> mono = invoke(new InvocableHandlerMethodTests.TestController(), method);
        assertHandlerResultValue(mono, "created");
        Assert.assertThat(this.exchange.getResponse().getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void voidMethodWithResponseArg() {
        ServerHttpResponse response = this.exchange.getResponse();
        this.resolvers.add(stubResolver(response));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( c) -> c.response(response)).method();
        HandlerResult result = invokeForResult(new InvocableHandlerMethodTests.TestController(), method);
        Assert.assertNull("Expected no result (i.e. fully handled)", result);
        Assert.assertEquals("bar", this.exchange.getResponse().getHeaders().getFirst("foo"));
    }

    @Test
    public void voidMonoMethodWithResponseArg() {
        ServerHttpResponse response = this.exchange.getResponse();
        this.resolvers.add(stubResolver(response));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( c) -> c.responseMonoVoid(response)).method();
        HandlerResult result = invokeForResult(new InvocableHandlerMethodTests.TestController(), method);
        Assert.assertNull("Expected no result (i.e. fully handled)", result);
        Assert.assertEquals("body", this.exchange.getResponse().getBodyAsString().block(Duration.ZERO));
    }

    @Test
    public void voidMethodWithExchangeArg() {
        this.resolvers.add(stubResolver(this.exchange));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( c) -> c.exchange(exchange)).method();
        HandlerResult result = invokeForResult(new InvocableHandlerMethodTests.TestController(), method);
        Assert.assertNull("Expected no result (i.e. fully handled)", result);
        Assert.assertEquals("bar", this.exchange.getResponse().getHeaders().getFirst("foo"));
    }

    @Test
    public void voidMonoMethodWithExchangeArg() {
        this.resolvers.add(stubResolver(this.exchange));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( c) -> c.exchangeMonoVoid(exchange)).method();
        HandlerResult result = invokeForResult(new InvocableHandlerMethodTests.TestController(), method);
        Assert.assertNull("Expected no result (i.e. fully handled)", result);
        Assert.assertEquals("body", this.exchange.getResponse().getBodyAsString().block(Duration.ZERO));
    }

    @Test
    public void checkNotModified() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").ifModifiedSince(((10 * 1000) * 1000)).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        this.resolvers.add(stubResolver(exchange));
        Method method = org.springframework.web.method.ResolvableMethod.on(InvocableHandlerMethodTests.TestController.class).mockCall(( c) -> c.notModified(exchange)).method();
        HandlerResult result = invokeForResult(new InvocableHandlerMethodTests.TestController(), method);
        Assert.assertNull("Expected no result (i.e. fully handled)", result);
    }

    @SuppressWarnings({ "unused", "UnusedReturnValue", "SameParameterValue" })
    static class TestController {
        String singleArg(String q) {
            return "success:" + q;
        }

        String noArgs() {
            return "success";
        }

        void exceptionMethod() {
            throw new IllegalStateException("boo");
        }

        @ResponseStatus(HttpStatus.CREATED)
        String created() {
            return "created";
        }

        void response(ServerHttpResponse response) {
            response.getHeaders().add("foo", "bar");
        }

        Mono<Void> responseMonoVoid(ServerHttpResponse response) {
            return Mono.delay(Duration.ofMillis(100)).thenEmpty(Mono.defer(() -> response.writeWith(getBody("body"))));
        }

        void exchange(ServerWebExchange exchange) {
            exchange.getResponse().getHeaders().add("foo", "bar");
        }

        Mono<Void> exchangeMonoVoid(ServerWebExchange exchange) {
            return Mono.delay(Duration.ofMillis(100)).thenEmpty(Mono.defer(() -> exchange.getResponse().writeWith(getBody("body"))));
        }

        @Nullable
        String notModified(ServerWebExchange exchange) {
            if (exchange.checkNotModified(Instant.ofEpochMilli((1000 * 1000)))) {
                return null;
            }
            return "body";
        }

        private Flux<DataBuffer> getBody(String body) {
            return Flux.just(new DefaultDataBufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)));
        }
    }
}

