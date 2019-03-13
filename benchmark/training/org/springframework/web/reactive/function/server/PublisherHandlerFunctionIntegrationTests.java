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
package org.springframework.web.reactive.function.server;


import HttpMethod.GET;
import HttpStatus.OK;
import java.net.URI;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class PublisherHandlerFunctionIntegrationTests extends AbstractRouterFunctionIntegrationTests {
    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void mono() {
        ResponseEntity<PublisherHandlerFunctionIntegrationTests.Person> result = restTemplate.getForEntity((("http://localhost:" + (port)) + "/mono"), PublisherHandlerFunctionIntegrationTests.Person.class);
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals("John", result.getBody().getName());
    }

    @Test
    public void flux() {
        ParameterizedTypeReference<List<PublisherHandlerFunctionIntegrationTests.Person>> reference = new ParameterizedTypeReference<List<PublisherHandlerFunctionIntegrationTests.Person>>() {};
        ResponseEntity<List<PublisherHandlerFunctionIntegrationTests.Person>> result = restTemplate.exchange((("http://localhost:" + (port)) + "/flux"), GET, null, reference);
        Assert.assertEquals(OK, result.getStatusCode());
        List<PublisherHandlerFunctionIntegrationTests.Person> body = result.getBody();
        Assert.assertEquals(2, body.size());
        Assert.assertEquals("John", body.get(0).getName());
        Assert.assertEquals("Jane", body.get(1).getName());
    }

    @Test
    public void postMono() {
        URI uri = URI.create((("http://localhost:" + (port)) + "/mono"));
        PublisherHandlerFunctionIntegrationTests.Person person = new PublisherHandlerFunctionIntegrationTests.Person("Jack");
        RequestEntity<PublisherHandlerFunctionIntegrationTests.Person> requestEntity = RequestEntity.post(uri).body(person);
        ResponseEntity<PublisherHandlerFunctionIntegrationTests.Person> result = restTemplate.exchange(requestEntity, PublisherHandlerFunctionIntegrationTests.Person.class);
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals("Jack", result.getBody().getName());
    }

    private static class PersonHandler {
        public Mono<ServerResponse> mono(ServerRequest request) {
            PublisherHandlerFunctionIntegrationTests.Person person = new PublisherHandlerFunctionIntegrationTests.Person("John");
            return ServerResponse.ok().body(fromPublisher(Mono.just(person), PublisherHandlerFunctionIntegrationTests.Person.class));
        }

        public Mono<ServerResponse> postMono(ServerRequest request) {
            Mono<PublisherHandlerFunctionIntegrationTests.Person> personMono = request.body(toMono(PublisherHandlerFunctionIntegrationTests.Person.class));
            return ServerResponse.ok().body(fromPublisher(personMono, PublisherHandlerFunctionIntegrationTests.Person.class));
        }

        public Mono<ServerResponse> flux(ServerRequest request) {
            PublisherHandlerFunctionIntegrationTests.Person person1 = new PublisherHandlerFunctionIntegrationTests.Person("John");
            PublisherHandlerFunctionIntegrationTests.Person person2 = new PublisherHandlerFunctionIntegrationTests.Person("Jane");
            return ServerResponse.ok().body(fromPublisher(Flux.just(person1, person2), PublisherHandlerFunctionIntegrationTests.Person.class));
        }
    }

    private static class Person {
        private String name;

        @SuppressWarnings("unused")
        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
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
            PublisherHandlerFunctionIntegrationTests.Person person = ((PublisherHandlerFunctionIntegrationTests.Person) (o));
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

