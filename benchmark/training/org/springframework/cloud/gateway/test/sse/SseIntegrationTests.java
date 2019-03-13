/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.test.sse;


import java.time.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.test.PermitAllSecurityConfiguration;
import org.springframework.cloud.gateway.test.support.HttpServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ResolvableType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class SseIntegrationTests {
    public HttpServer server;

    protected Log logger = LogFactory.getLog(getClass());

    protected int serverPort;

    private AnnotationConfigApplicationContext wac;

    private WebClient webClient;

    private ConfigurableApplicationContext gatewayContext;

    private int gatewayPort;

    @Test
    public void sseAsString() {
        Flux<String> result = this.webClient.get().uri("/string").accept(TEXT_EVENT_STREAM).exchange().flatMapMany(( response) -> response.bodyToFlux(.class));
        StepVerifier.create(result).expectNext("foo 0").expectNext("foo 1").thenCancel().verify(Duration.ofSeconds(5L));
    }

    @Test
    public void sseAsPerson() {
        Flux<SseIntegrationTests.Person> result = this.webClient.get().uri("/person").accept(TEXT_EVENT_STREAM).exchange().flatMapMany(( response) -> response.bodyToFlux(.class));
        StepVerifier.create(result).expectNext(new SseIntegrationTests.Person("foo 0")).expectNext(new SseIntegrationTests.Person("foo 1")).thenCancel().verify(Duration.ofSeconds(5L));
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void sseAsEvent() {
        ResolvableType type = forClassWithGenerics(ServerSentEvent.class, String.class);
        Flux<ServerSentEvent<String>> result = this.webClient.get().uri("/event").accept(TEXT_EVENT_STREAM).exchange().flatMapMany(( response) -> response.body(toFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})));
        StepVerifier.create(result).consumeNextWith(( event) -> {
            assertThat(event.id()).isEqualTo("0");
            assertThat(event.data()).isEqualTo("foo");
            assertThat(event.comment()).isEqualTo("bar");
            assertThat(event.event()).isNull();
            assertThat(event.retry()).isNull();
        }).consumeNextWith(( event) -> {
            assertThat(event.id()).isEqualTo("1");
            assertThat(event.data()).isEqualTo("foo");
            assertThat(event.comment()).isEqualTo("bar");
            assertThat(event.event()).isNull();
            assertThat(event.retry()).isNull();
        }).thenCancel().verify(Duration.ofSeconds(5L));
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void sseAsEventWithoutAcceptHeader() {
        Flux<ServerSentEvent<String>> result = this.webClient.get().uri("/event").accept(TEXT_EVENT_STREAM).exchange().flatMapMany(( response) -> response.body(toFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})));
        StepVerifier.create(result).consumeNextWith(( event) -> {
            assertThat(event.id()).isEqualTo("0");
            assertThat(event.data()).isEqualTo("foo");
            assertThat(event.comment()).isEqualTo("bar");
            assertThat(event.event()).isNull();
            assertThat(event.retry()).isNull();
        }).consumeNextWith(( event) -> {
            assertThat(event.id()).isEqualTo("1");
            assertThat(event.data()).isEqualTo("foo");
            assertThat(event.comment()).isEqualTo("bar");
            assertThat(event.event()).isNull();
            assertThat(event.retry()).isNull();
        }).thenCancel().verify(Duration.ofSeconds(5L));
    }

    @RestController
    @SuppressWarnings("unused")
    static class SseController {
        private static final Flux<Long> INTERVAL = SseIntegrationTests.interval(Duration.ofMillis(100), 50);

        @RequestMapping("/sse/string")
        Flux<String> string() {
            return SseIntegrationTests.SseController.INTERVAL.map(( l) -> "foo " + l);
        }

        @RequestMapping("/sse/person")
        Flux<SseIntegrationTests.Person> person() {
            return SseIntegrationTests.SseController.INTERVAL.map(( l) -> new org.springframework.cloud.gateway.test.sse.Person(("foo " + l)));
        }

        @RequestMapping("/sse/event")
        Flux<ServerSentEvent<String>> sse() {
            return SseIntegrationTests.SseController.INTERVAL.map(( l) -> ServerSentEvent.builder("foo").id(Long.toString(l)).comment("bar").build());
        }
    }

    @Configuration
    @EnableWebFlux
    @SuppressWarnings("unused")
    static class TestConfiguration {
        @Bean
        public SseIntegrationTests.SseController sseController() {
            return new SseIntegrationTests.SseController();
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(PermitAllSecurityConfiguration.class)
    protected static class GatewayConfig {
        @Value("${sse.server.port}")
        private int port;

        @Bean
        public RouteLocator sseRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("sse_route", ( r) -> r.alwaysTrue().uri(("http://localhost:" + (this.port)))).build();
        }
    }

    @SuppressWarnings("unused")
    private static class Person {
        private String name;

        Person() {
        }

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SseIntegrationTests.Person person = ((SseIntegrationTests.Person) (o));
            return !((this.name) != null ? !(this.name.equals(person.name)) : (person.name) != null);
        }

        @Override
        public int hashCode() {
            return (this.name) != null ? this.name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return (("Person{name='" + (this.name)) + '\'') + '}';
        }
    }
}

