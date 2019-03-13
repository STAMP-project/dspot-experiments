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
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.web.bind.annotation.GetMapping;
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
public class JacksonStreamingIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    private AnnotationConfigApplicationContext wac;

    private WebClient webClient;

    @Test
    public void jsonStreaming() {
        Flux<JacksonStreamingIntegrationTests.Person> result = this.webClient.get().uri("/stream").accept(APPLICATION_STREAM_JSON).retrieve().bodyToFlux(JacksonStreamingIntegrationTests.Person.class);
        StepVerifier.create(result).expectNext(new JacksonStreamingIntegrationTests.Person("foo 0")).expectNext(new JacksonStreamingIntegrationTests.Person("foo 1")).thenCancel().verify();
    }

    @Test
    public void smileStreaming() {
        Flux<JacksonStreamingIntegrationTests.Person> result = this.webClient.get().uri("/stream").accept(new MediaType("application", "stream+x-jackson-smile")).retrieve().bodyToFlux(JacksonStreamingIntegrationTests.Person.class);
        StepVerifier.create(result).expectNext(new JacksonStreamingIntegrationTests.Person("foo 0")).expectNext(new JacksonStreamingIntegrationTests.Person("foo 1")).thenCancel().verify();
    }

    @RestController
    @SuppressWarnings("unused")
    static class JacksonStreamingController {
        @GetMapping(value = "/stream", produces = { APPLICATION_STREAM_JSON_VALUE, "application/stream+x-jackson-smile" })
        Flux<JacksonStreamingIntegrationTests.Person> person() {
            return testInterval(Duration.ofMillis(100), 50).map(( l) -> new org.springframework.web.reactive.result.method.annotation.Person(("foo " + l)));
        }
    }

    @Configuration
    @EnableWebFlux
    @SuppressWarnings("unused")
    static class TestConfiguration {
        @Bean
        public JacksonStreamingIntegrationTests.JacksonStreamingController jsonStreamingController() {
            return new JacksonStreamingIntegrationTests.JacksonStreamingController();
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
            JacksonStreamingIntegrationTests.Person person = ((JacksonStreamingIntegrationTests.Person) (o));
            return !((this.name) != null ? !(this.name.equals(person.name)) : (person.name) != null);
        }

        @Override
        public int hashCode() {
            return (this.name) != null ? this.name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return ((("Person{" + "name='") + (name)) + '\'') + '}';
        }
    }
}

