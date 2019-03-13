/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.web.server.adapter;


import java.time.Duration;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.web.filter.reactive.ForwardedHeaderFilter;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import static OrderedWebFilterBeanConfig.ATTRIBUTE;


/**
 * Unit tests for {@link WebHttpHandlerBuilder}.
 *
 * @author Rossen Stoyanchev
 */
public class WebHttpHandlerBuilderTests {
    // SPR-15074
    @Test
    public void orderedWebFilterBeans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(WebHttpHandlerBuilderTests.OrderedWebFilterBeanConfig.class);
        context.refresh();
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(context).build();
        Assert.assertTrue((httpHandler instanceof HttpWebHandlerAdapter));
        Assert.assertSame(context, getApplicationContext());
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        httpHandler.handle(request, response).block(Duration.ofMillis(5000));
        Assert.assertEquals("FilterB::FilterA", response.getBodyAsString().block(Duration.ofMillis(5000)));
    }

    @Test
    public void forwardedHeaderFilter() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(WebHttpHandlerBuilderTests.ForwardedHeaderFilterConfig.class);
        context.refresh();
        WebHttpHandlerBuilder builder = WebHttpHandlerBuilder.applicationContext(context);
        builder.filters(( filters) -> assertEquals(Collections.emptyList(), filters));
        Assert.assertTrue(builder.hasForwardedHeaderTransformer());
    }

    // SPR-15074
    @Test
    public void orderedWebExceptionHandlerBeans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(WebHttpHandlerBuilderTests.OrderedExceptionHandlerBeanConfig.class);
        context.refresh();
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(context).build();
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        httpHandler.handle(request, response).block(Duration.ofMillis(5000));
        Assert.assertEquals("ExceptionHandlerB", response.getBodyAsString().block(Duration.ofMillis(5000)));
    }

    @Test
    public void configWithoutFilters() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(WebHttpHandlerBuilderTests.NoFilterConfig.class);
        context.refresh();
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(context).build();
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        httpHandler.handle(request, response).block(Duration.ofMillis(5000));
        Assert.assertEquals("handled", response.getBodyAsString().block(Duration.ofMillis(5000)));
    }

    // SPR-16972
    @Test
    public void cloneWithApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(WebHttpHandlerBuilderTests.NoFilterConfig.class);
        context.refresh();
        WebHttpHandlerBuilder builder = WebHttpHandlerBuilder.applicationContext(context);
        Assert.assertSame(context, getApplicationContext());
        Assert.assertSame(context, getApplicationContext());
    }

    @Configuration
    @SuppressWarnings("unused")
    static class OrderedWebFilterBeanConfig {
        private static final String ATTRIBUTE = "attr";

        @Bean
        @Order(2)
        public WebFilter filterA() {
            return createFilter("FilterA");
        }

        @Bean
        @Order(1)
        public WebFilter filterB() {
            return createFilter("FilterB");
        }

        private WebFilter createFilter(String name) {
            return ( exchange, chain) -> {
                String value = exchange.getAttribute(OrderedWebFilterBeanConfig.ATTRIBUTE);
                value = (value != null) ? (value + "::") + name : name;
                exchange.getAttributes().put(OrderedWebFilterBeanConfig.ATTRIBUTE, value);
                return chain.filter(exchange);
            };
        }

        @Bean
        public WebHandler webHandler() {
            return ( exchange) -> {
                String value = exchange.getAttributeOrDefault(OrderedWebFilterBeanConfig.ATTRIBUTE, "none");
                return writeToResponse(exchange, value);
            };
        }
    }

    @Configuration
    @SuppressWarnings("unused")
    static class OrderedExceptionHandlerBeanConfig {
        @Bean
        @Order(2)
        public WebExceptionHandler exceptionHandlerA() {
            return ( exchange, ex) -> writeToResponse(exchange, "ExceptionHandlerA");
        }

        @Bean
        @Order(1)
        public WebExceptionHandler exceptionHandlerB() {
            return ( exchange, ex) -> writeToResponse(exchange, "ExceptionHandlerB");
        }

        @Bean
        public WebHandler webHandler() {
            return ( exchange) -> Mono.error(new Exception());
        }
    }

    @Configuration
    @SuppressWarnings("unused")
    static class ForwardedHeaderFilterConfig {
        @Bean
        @SuppressWarnings("deprecation")
        public WebFilter forwardedHeaderFilter() {
            return new org.springframework.web.filter.reactive.ForwardedHeaderFilter();
        }

        @Bean
        public WebHandler webHandler() {
            return ( exchange) -> Mono.error(new Exception());
        }
    }

    @Configuration
    @SuppressWarnings("unused")
    static class NoFilterConfig {
        @Bean
        public WebHandler webHandler() {
            return ( exchange) -> writeToResponse(exchange, "handled");
        }
    }
}

