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
package org.springframework.web.reactive.result.method.annotation;


import java.time.Duration;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.http.server.reactive.bootstrap.JettyHttpServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.MonoProcessor;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class SseIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    private AnnotationConfigApplicationContext wac;

    private WebClient webClient;

    @Parameterized.Parameter(1)
    public ClientHttpConnector connector;

    @Test
    public void sseAsString() {
        Flux<String> result = this.webClient.get().uri("/string").accept(TEXT_EVENT_STREAM).retrieve().bodyToFlux(String.class);
        StepVerifier.create(result).expectNext("foo 0").expectNext("foo 1").thenCancel().verify(Duration.ofSeconds(5L));
    }

    @Test
    public void sseAsPerson() {
        Flux<SseIntegrationTests.Person> result = this.webClient.get().uri("/person").accept(TEXT_EVENT_STREAM).retrieve().bodyToFlux(SseIntegrationTests.Person.class);
        StepVerifier.create(result).expectNext(new SseIntegrationTests.Person("foo 0")).expectNext(new SseIntegrationTests.Person("foo 1")).thenCancel().verify(Duration.ofSeconds(5L));
    }

    @Test
    public void sseAsEvent() {
        Assume.assumeTrue(((server) instanceof JettyHttpServer));
        Flux<ServerSentEvent<SseIntegrationTests.Person>> result = this.webClient.get().uri("/event").accept(TEXT_EVENT_STREAM).retrieve().bodyToFlux(new org.springframework.core.ParameterizedTypeReference<ServerSentEvent<SseIntegrationTests.Person>>() {});
        verifyPersonEvents(result);
    }

    @Test
    public void sseAsEventWithoutAcceptHeader() {
        Flux<ServerSentEvent<SseIntegrationTests.Person>> result = this.webClient.get().uri("/event").accept(TEXT_EVENT_STREAM).retrieve().bodyToFlux(new org.springframework.core.ParameterizedTypeReference<ServerSentEvent<SseIntegrationTests.Person>>() {});
        verifyPersonEvents(result);
    }

    @RestController
    @SuppressWarnings("unused")
    @RequestMapping("/sse")
    static class SseController {
        private static final Flux<Long> INTERVAL = testInterval(Duration.ofMillis(100), 50);

        private MonoProcessor<Void> cancellation = MonoProcessor.create();

        @GetMapping("/string")
        Flux<String> string() {
            return SseIntegrationTests.SseController.INTERVAL.map(( l) -> "foo " + l);
        }

        @GetMapping("/person")
        Flux<SseIntegrationTests.Person> person() {
            return SseIntegrationTests.SseController.INTERVAL.map(( l) -> new org.springframework.web.reactive.result.method.annotation.Person(("foo " + l)));
        }

        @GetMapping("/event")
        Flux<ServerSentEvent<SseIntegrationTests.Person>> sse() {
            return SseIntegrationTests.SseController.INTERVAL.take(2).map(( l) -> ServerSentEvent.builder(new org.springframework.web.reactive.result.method.annotation.Person(("foo " + l))).id(Long.toString(l)).comment(("bar " + l)).build());
        }

        @GetMapping("/infinite")
        Flux<String> infinite() {
            return Flux.just(0, 1).map(( l) -> "foo " + l).mergeWith(Flux.never()).doOnCancel(() -> cancellation.onComplete());
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

    @SuppressWarnings("unused")
    private static class Person {
        private String name;

        public Person() {
        }

        public Person(String name) {
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

