/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.spring.web.reactive;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.linecorp.armeria.client.ClientFactoryBuilder;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.testing.internal.MockAddressResolverGroup;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test_reactive")
public class ArmeriaWebClientTest {
    @SpringBootApplication
    @Configuration
    @ActiveProfiles("test_reactive")
    static class TestConfiguration {
        @RestController
        static class TestController {
            @GetMapping("/hello")
            Flux<String> hello() {
                ensureInRequestContextAwareEventLoop();
                return Flux.just("hello", "\n", "armeria", "\n", "world");
            }

            @GetMapping("/conflict")
            @ResponseStatus(HttpStatus.CONFLICT)
            void conflict() {
                ensureInRequestContextAwareEventLoop();
            }

            @GetMapping("/resource")
            ClassPathResource resource() {
                ensureInRequestContextAwareEventLoop();
                return new ClassPathResource("largeTextFile.txt", getClass());
            }

            @PostMapping("/birthday")
            ArmeriaWebClientTest.Person birthday(@RequestBody
            ArmeriaWebClientTest.Person person) {
                ensureInRequestContextAwareEventLoop();
                return new ArmeriaWebClientTest.Person(person.name(), ((person.age()) + 1));
            }

            private void ensureInRequestContextAwareEventLoop() {
                assertThat(((com.linecorp.armeria.server.ServiceRequestContext) (RequestContext.current()))).isNotNull();
            }
        }
    }

    @LocalServerPort
    int port;

    static WebClient webClient = WebClient.builder().clientConnector(new ArmeriaClientHttpConnector(( builder) -> builder.factory(new ClientFactoryBuilder().sslContextCustomizer(( b) -> b.trustManager(InsecureTrustManagerFactory.INSTANCE)).addressResolverGroupFactory(( unused) -> MockAddressResolverGroup.localhost()).build()))).build();

    @Test
    public void getHello() {
        final Flux<String> body = ArmeriaWebClientTest.webClient.get().uri(uri("/hello")).retrieve().bodyToFlux(String.class);
        StepVerifier.create(body).expectNext("hello").expectNext("armeria").expectNext("world").expectComplete().verify(Duration.ofSeconds(10));
    }

    @Test
    public void getConflict() {
        final Mono<ClientResponse> response = ArmeriaWebClientTest.webClient.get().uri(uri("/conflict")).exchange();
        StepVerifier.create(response).assertNext(( r) -> assertThat(r.statusCode()).isEqualTo(HttpStatus.CONFLICT)).expectComplete().verify(Duration.ofSeconds(10));
    }

    @Test
    public void getResource() {
        final Flux<DataBuffer> body = ArmeriaWebClientTest.webClient.get().uri(uri("/resource")).retrieve().bodyToFlux(DataBuffer.class);
        // An empty buffer comes last.
        StepVerifier.create(body).thenConsumeWhile(( data) -> (data.readableByteCount()) > 0).assertNext(( data) -> assertThat(data.readableByteCount()).isZero()).expectComplete().verify(Duration.ofSeconds(30));
    }

    @Test
    public void postPerson() {
        final Mono<ArmeriaWebClientTest.Person> body = ArmeriaWebClientTest.webClient.post().uri(uri("/birthday")).contentType(MediaType.APPLICATION_JSON).body(Mono.just(new ArmeriaWebClientTest.Person("armeria", 4)), ArmeriaWebClientTest.Person.class).retrieve().bodyToMono(ArmeriaWebClientTest.Person.class);
        StepVerifier.create(body).expectNext(new ArmeriaWebClientTest.Person("armeria", 5)).expectComplete().verify(Duration.ofSeconds(10));
    }

    private static class Person {
        private final String name;

        private final int age;

        @JsonCreator
        Person(@JsonProperty("name")
        String name, @JsonProperty("age")
        int age) {
            this.name = name;
            this.age = age;
        }

        @JsonProperty
        public String name() {
            return name;
        }

        @JsonProperty
        public int age() {
            return age;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof ArmeriaWebClientTest.Person)) {
                return false;
            }
            final ArmeriaWebClientTest.Person that = ((ArmeriaWebClientTest.Person) (o));
            return ((age) == (that.age)) && (name.equals(that.name));
        }

        @Override
        public int hashCode() {
            return (31 * (name.hashCode())) + (age);
        }
    }
}

