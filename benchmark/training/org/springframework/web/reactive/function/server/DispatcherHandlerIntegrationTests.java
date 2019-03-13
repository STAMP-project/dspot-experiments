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
package org.springframework.web.reactive.function.server;


import HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE;
import HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;
import HttpMethod.GET;
import HttpStatus.OK;
import RouterFunctions.MATCHING_PATTERN_ATTRIBUTE;
import RouterFunctions.REQUEST_ATTRIBUTE;
import RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Tests the use of {@link HandlerFunction} and {@link RouterFunction} in a
 * {@link DispatcherHandler}, combined with {@link Controller}s.
 *
 * @author Arjen Poutsma
 */
public class DispatcherHandlerIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    private final RestTemplate restTemplate = new RestTemplate();

    private AnnotationConfigApplicationContext wac;

    @Test
    public void mono() {
        ResponseEntity<DispatcherHandlerIntegrationTests.Person> result = this.restTemplate.getForEntity((("http://localhost:" + (this.port)) + "/mono"), DispatcherHandlerIntegrationTests.Person.class);
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals("John", result.getBody().getName());
    }

    @Test
    public void flux() {
        ParameterizedTypeReference<List<DispatcherHandlerIntegrationTests.Person>> reference = new ParameterizedTypeReference<List<DispatcherHandlerIntegrationTests.Person>>() {};
        ResponseEntity<List<DispatcherHandlerIntegrationTests.Person>> result = this.restTemplate.exchange((("http://localhost:" + (this.port)) + "/flux"), GET, null, reference);
        Assert.assertEquals(OK, result.getStatusCode());
        List<DispatcherHandlerIntegrationTests.Person> body = result.getBody();
        Assert.assertEquals(2, body.size());
        Assert.assertEquals("John", body.get(0).getName());
        Assert.assertEquals("Jane", body.get(1).getName());
    }

    @Test
    public void controller() {
        ResponseEntity<DispatcherHandlerIntegrationTests.Person> result = this.restTemplate.getForEntity((("http://localhost:" + (this.port)) + "/controller"), DispatcherHandlerIntegrationTests.Person.class);
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals("John", result.getBody().getName());
    }

    @Test
    public void attributes() {
        ResponseEntity<String> result = this.restTemplate.getForEntity((("http://localhost:" + (this.port)) + "/attributes/bar"), String.class);
        Assert.assertEquals(OK, result.getStatusCode());
    }

    @EnableWebFlux
    @Configuration
    static class TestConfiguration {
        @Bean
        public DispatcherHandlerIntegrationTests.PersonHandler personHandler() {
            return new DispatcherHandlerIntegrationTests.PersonHandler();
        }

        @Bean
        public DispatcherHandlerIntegrationTests.PersonController personController() {
            return new DispatcherHandlerIntegrationTests.PersonController();
        }

        @Bean
        public DispatcherHandlerIntegrationTests.AttributesHandler attributesHandler() {
            return new DispatcherHandlerIntegrationTests.AttributesHandler();
        }

        @Bean
        public RouterFunction<EntityResponse<DispatcherHandlerIntegrationTests.Person>> monoRouterFunction(DispatcherHandlerIntegrationTests.PersonHandler personHandler) {
            return route(RequestPredicates.GET("/mono"), personHandler::mono);
        }

        @Bean
        public RouterFunction<ServerResponse> fluxRouterFunction(DispatcherHandlerIntegrationTests.PersonHandler personHandler) {
            return route(RequestPredicates.GET("/flux"), personHandler::flux);
        }

        @Bean
        public RouterFunction<ServerResponse> attributesRouterFunction(DispatcherHandlerIntegrationTests.AttributesHandler attributesHandler) {
            return nest(RequestPredicates.GET("/attributes"), route(RequestPredicates.GET("/{foo}"), attributesHandler::attributes));
        }
    }

    private static class PersonHandler {
        public Mono<EntityResponse<DispatcherHandlerIntegrationTests.Person>> mono(ServerRequest request) {
            DispatcherHandlerIntegrationTests.Person person = new DispatcherHandlerIntegrationTests.Person("John");
            return EntityResponse.fromObject(person).build();
        }

        public Mono<ServerResponse> flux(ServerRequest request) {
            DispatcherHandlerIntegrationTests.Person person1 = new DispatcherHandlerIntegrationTests.Person("John");
            DispatcherHandlerIntegrationTests.Person person2 = new DispatcherHandlerIntegrationTests.Person("Jane");
            return ServerResponse.ok().body(fromPublisher(Flux.just(person1, person2), DispatcherHandlerIntegrationTests.Person.class));
        }
    }

    private static class AttributesHandler {
        @SuppressWarnings("unchecked")
        public Mono<ServerResponse> attributes(ServerRequest request) {
            Assert.assertTrue(request.attributes().containsKey(REQUEST_ATTRIBUTE));
            Assert.assertTrue(request.attributes().containsKey(BEST_MATCHING_HANDLER_ATTRIBUTE));
            Map<String, String> pathVariables = ((Map<String, String>) (request.attributes().get(URI_TEMPLATE_VARIABLES_ATTRIBUTE)));
            Assert.assertNotNull(pathVariables);
            Assert.assertEquals(1, pathVariables.size());
            Assert.assertEquals("bar", pathVariables.get("foo"));
            pathVariables = ((Map<String, String>) (request.attributes().get(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)));
            Assert.assertNotNull(pathVariables);
            Assert.assertEquals(1, pathVariables.size());
            Assert.assertEquals("bar", pathVariables.get("foo"));
            PathPattern pattern = ((PathPattern) (request.attributes().get(MATCHING_PATTERN_ATTRIBUTE)));
            Assert.assertNotNull(pattern);
            Assert.assertEquals("/attributes/{foo}", pattern.getPatternString());
            pattern = ((PathPattern) (request.attributes().get(BEST_MATCHING_PATTERN_ATTRIBUTE)));
            Assert.assertNotNull(pattern);
            Assert.assertEquals("/attributes/{foo}", pattern.getPatternString());
            return ServerResponse.ok().build();
        }
    }

    @Controller
    public static class PersonController {
        @RequestMapping("/controller")
        @ResponseBody
        public Mono<DispatcherHandlerIntegrationTests.Person> controller() {
            return Mono.just(new DispatcherHandlerIntegrationTests.Person("John"));
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
            return this.name;
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
            DispatcherHandlerIntegrationTests.Person person = ((DispatcherHandlerIntegrationTests.Person) (o));
            return !((this.name) != null ? !(this.name.equals(person.name)) : (person.name) != null);
        }

        @Override
        public int hashCode() {
            return (this.name) != null ? this.name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return ((("Person{" + "name='") + (this.name)) + '\'') + '}';
        }
    }
}

