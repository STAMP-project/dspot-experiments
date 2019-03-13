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
package org.springframework.web.reactive;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link DispatcherHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class DispatcherHandlerTests {
    private static final MethodParameter RETURN_TYPE = ResolvableMethod.on(DispatcherHandler.class).named("handle").resolveReturnType();

    @Test
    public void handlerMappingOrder() {
        HandlerMapping hm1 = Mockito.mock(HandlerMapping.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        HandlerMapping hm2 = Mockito.mock(HandlerMapping.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        Mockito.when(getOrder()).thenReturn(1);
        Mockito.when(getOrder()).thenReturn(2);
        Mockito.when(hm1.getHandler(ArgumentMatchers.any())).thenReturn(Mono.just(((Supplier<String>) (() -> "1"))));
        Mockito.when(hm2.getHandler(ArgumentMatchers.any())).thenReturn(Mono.just(((Supplier<String>) (() -> "2"))));
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerBean("b2", HandlerMapping.class, () -> hm2);
        context.registerBean("b1", HandlerMapping.class, () -> hm1);
        context.registerBean(HandlerAdapter.class, DispatcherHandlerTests.SupplierHandlerAdapter::new);
        context.registerBean(HandlerResultHandler.class, DispatcherHandlerTests.StringHandlerResultHandler::new);
        context.refresh();
        DispatcherHandler dispatcherHandler = new DispatcherHandler(context);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        dispatcherHandler.handle(exchange).block(Duration.ofSeconds(0));
        Assert.assertEquals("1", exchange.getResponse().getBodyAsString().block(Duration.ofSeconds(5)));
    }

    private static class SupplierHandlerAdapter implements HandlerAdapter {
        @Override
        public boolean supports(Object handler) {
            return handler instanceof Supplier;
        }

        @Override
        public Mono<HandlerResult> handle(ServerWebExchange exchange, Object handler) {
            return Mono.just(new HandlerResult(handler, ((Supplier<?>) (handler)).get(), DispatcherHandlerTests.RETURN_TYPE));
        }
    }

    private static class StringHandlerResultHandler implements HandlerResultHandler {
        @Override
        public boolean supports(HandlerResult result) {
            Object value = result.getReturnValue();
            return (value != null) && (String.class.equals(value.getClass()));
        }

        @Override
        public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
            byte[] bytes = ((String) (result.getReturnValue())).getBytes(StandardCharsets.UTF_8);
            DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }
    }
}

