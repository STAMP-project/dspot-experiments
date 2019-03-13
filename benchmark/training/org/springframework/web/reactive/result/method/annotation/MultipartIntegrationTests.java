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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class MultipartIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    private WebClient webClient;

    @Test
    public void requestPart() {
        Mono<ClientResponse> result = webClient.post().uri("/requestPart").syncBody(generateBody()).exchange();
        StepVerifier.create(result).consumeNextWith(( response) -> assertEquals(HttpStatus.OK, response.statusCode())).verifyComplete();
    }

    @Test
    public void requestBodyMap() {
        Mono<String> result = webClient.post().uri("/requestBodyMap").syncBody(generateBody()).retrieve().bodyToMono(String.class);
        StepVerifier.create(result).consumeNextWith(( body) -> assertEquals("Map[[fieldPart],[fileParts:foo.txt,fileParts:logo.png],[jsonPart]]", body)).verifyComplete();
    }

    @Test
    public void requestBodyFlux() {
        Mono<String> result = webClient.post().uri("/requestBodyFlux").syncBody(generateBody()).retrieve().bodyToMono(String.class);
        StepVerifier.create(result).consumeNextWith(( body) -> assertEquals("[fieldPart,fileParts:foo.txt,fileParts:logo.png,jsonPart]", body)).verifyComplete();
    }

    @Test
    public void modelAttribute() {
        Mono<String> result = webClient.post().uri("/modelAttribute").syncBody(generateBody()).retrieve().bodyToMono(String.class);
        StepVerifier.create(result).consumeNextWith(( body) -> assertEquals("FormBean[fieldValue,[fileParts:foo.txt,fileParts:logo.png]]", body)).verifyComplete();
    }

    @Configuration
    @EnableWebFlux
    @SuppressWarnings("unused")
    static class TestConfiguration {
        @Bean
        public MultipartIntegrationTests.MultipartController testController() {
            return new MultipartIntegrationTests.MultipartController();
        }
    }

    @RestController
    @SuppressWarnings("unused")
    static class MultipartController {
        @PostMapping("/requestPart")
        void requestPart(@RequestPart
        FormFieldPart fieldPart, @RequestPart("fileParts")
        FilePart fileParts, @RequestPart("fileParts")
        Mono<FilePart> filePartsMono, @RequestPart("fileParts")
        Flux<FilePart> filePartsFlux, @RequestPart("jsonPart")
        MultipartIntegrationTests.Person person, @RequestPart("jsonPart")
        Mono<MultipartIntegrationTests.Person> personMono) {
            Assert.assertEquals("fieldValue", fieldPart.value());
            Assert.assertEquals("fileParts:foo.txt", MultipartIntegrationTests.partDescription(fileParts));
            Assert.assertEquals("Jason", person.getName());
            StepVerifier.create(MultipartIntegrationTests.partFluxDescription(filePartsFlux)).consumeNextWith(( content) -> assertEquals("[fileParts:foo.txt,fileParts:logo.png]", content)).verifyComplete();
            StepVerifier.create(filePartsMono).consumeNextWith(( filePart) -> assertEquals("fileParts:foo.txt", partDescription(filePart))).verifyComplete();
            StepVerifier.create(personMono).consumeNextWith(( p) -> assertEquals("Jason", p.getName())).verifyComplete();
        }

        @PostMapping("/requestBodyMap")
        Mono<String> requestBodyMap(@RequestBody
        Mono<MultiValueMap<String, Part>> partsMono) {
            return partsMono.map(MultipartIntegrationTests::partMapDescription);
        }

        @PostMapping("/requestBodyFlux")
        Mono<String> requestBodyFlux(@RequestBody
        Flux<Part> parts) {
            return MultipartIntegrationTests.partFluxDescription(parts);
        }

        @PostMapping("/modelAttribute")
        String modelAttribute(@ModelAttribute
        MultipartIntegrationTests.FormBean formBean) {
            return formBean.toString();
        }
    }

    static class FormBean {
        private String fieldPart;

        private List<FilePart> fileParts;

        public String getFieldPart() {
            return this.fieldPart;
        }

        public void setFieldPart(String fieldPart) {
            this.fieldPart = fieldPart;
        }

        public List<FilePart> getFileParts() {
            return this.fileParts;
        }

        public void setFileParts(List<FilePart> fileParts) {
            this.fileParts = fileParts;
        }

        @Override
        public String toString() {
            return ((("FormBean[" + (getFieldPart())) + ",") + (MultipartIntegrationTests.partListDescription(getFileParts()))) + "]";
        }
    }

    private static class Person {
        private String name;

        @JsonCreator
        public Person(@JsonProperty("name")
        String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

