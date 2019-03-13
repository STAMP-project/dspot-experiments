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


import HttpStatus.OK;
import MediaType.APPLICATION_XML;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ZeroCopyIntegrationTests;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Observable;
import rx.Single;


/**
 * {@code @RequestMapping} integration tests focusing on serialization and
 * deserialization of the request and response body.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class RequestMappingMessageConversionIntegrationTests extends AbstractRequestMappingIntegrationTests {
    private static final ParameterizedTypeReference<List<RequestMappingMessageConversionIntegrationTests.Person>> PERSON_LIST = new ParameterizedTypeReference<List<RequestMappingMessageConversionIntegrationTests.Person>>() {};

    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Test
    public void byteBufferResponseBodyWithPublisher() throws Exception {
        RequestMappingMessageConversionIntegrationTests.Person expected = new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        Assert.assertEquals(expected, performGet("/raw-response/publisher", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    @Test
    public void byteBufferResponseBodyWithFlux() throws Exception {
        String expected = "Hello!";
        Assert.assertEquals(expected, performGet("/raw-response/flux", new HttpHeaders(), String.class).getBody());
    }

    @Test
    public void byteBufferResponseBodyWithMono() throws Exception {
        String expected = "Hello!";
        ResponseEntity<String> responseEntity = performGet("/raw-response/mono", new HttpHeaders(), String.class);
        Assert.assertEquals(6, responseEntity.getHeaders().getContentLength());
        Assert.assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void byteBufferResponseBodyWithObservable() throws Exception {
        String expected = "Hello!";
        Assert.assertEquals(expected, performGet("/raw-response/observable", new HttpHeaders(), String.class).getBody());
    }

    @Test
    public void byteBufferResponseBodyWithRxJava2Observable() throws Exception {
        String expected = "Hello!";
        Assert.assertEquals(expected, performGet("/raw-response/rxjava2-observable", new HttpHeaders(), String.class).getBody());
    }

    @Test
    public void byteBufferResponseBodyWithFlowable() throws Exception {
        String expected = "Hello!";
        Assert.assertEquals(expected, performGet("/raw-response/flowable", new HttpHeaders(), String.class).getBody());
    }

    @Test
    public void personResponseBody() throws Exception {
        RequestMappingMessageConversionIntegrationTests.Person expected = new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        ResponseEntity<RequestMappingMessageConversionIntegrationTests.Person> responseEntity = performGet("/person-response/person", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(17, responseEntity.getHeaders().getContentLength());
        Assert.assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void personResponseBodyWithCompletableFuture() throws Exception {
        RequestMappingMessageConversionIntegrationTests.Person expected = new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        ResponseEntity<RequestMappingMessageConversionIntegrationTests.Person> responseEntity = performGet("/person-response/completable-future", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(17, responseEntity.getHeaders().getContentLength());
        Assert.assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void personResponseBodyWithMono() throws Exception {
        RequestMappingMessageConversionIntegrationTests.Person expected = new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        ResponseEntity<RequestMappingMessageConversionIntegrationTests.Person> responseEntity = performGet("/person-response/mono", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(17, responseEntity.getHeaders().getContentLength());
        Assert.assertEquals(expected, responseEntity.getBody());
    }

    // SPR-17506
    @Test
    public void personResponseBodyWithEmptyMono() throws Exception {
        ResponseEntity<RequestMappingMessageConversionIntegrationTests.Person> responseEntity = performGet("/person-response/mono-empty", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(0, responseEntity.getHeaders().getContentLength());
        Assert.assertNull(responseEntity.getBody());
        // As we're on the same connection, the 2nd request proves server response handling
        // did complete after the 1st request..
        responseEntity = performGet("/person-response/mono-empty", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(0, responseEntity.getHeaders().getContentLength());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void personResponseBodyWithMonoDeclaredAsObject() throws Exception {
        RequestMappingMessageConversionIntegrationTests.Person expected = new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        ResponseEntity<RequestMappingMessageConversionIntegrationTests.Person> entity = performGet("/person-response/mono-declared-as-object", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(17, entity.getHeaders().getContentLength());
        Assert.assertEquals(expected, entity.getBody());
    }

    @Test
    public void personResponseBodyWithSingle() throws Exception {
        RequestMappingMessageConversionIntegrationTests.Person expected = new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        ResponseEntity<RequestMappingMessageConversionIntegrationTests.Person> entity = performGet("/person-response/single", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(17, entity.getHeaders().getContentLength());
        Assert.assertEquals(expected, entity.getBody());
    }

    @Test
    public void personResponseBodyWithMonoResponseEntity() throws Exception {
        RequestMappingMessageConversionIntegrationTests.Person expected = new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        ResponseEntity<RequestMappingMessageConversionIntegrationTests.Person> entity = performGet("/person-response/mono-response-entity", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class);
        Assert.assertEquals(17, entity.getHeaders().getContentLength());
        Assert.assertEquals(expected, entity.getBody());
    }

    // SPR-16172
    @Test
    public void personResponseBodyWithMonoResponseEntityXml() throws Exception {
        String url = "/person-response/mono-response-entity-xml";
        ResponseEntity<String> entity = performGet(url, new HttpHeaders(), String.class);
        String actual = entity.getBody();
        Assert.assertEquals(91, entity.getHeaders().getContentLength());
        Assert.assertEquals(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<person><name>Robert</name></person>"), actual);
    }

    @Test
    public void personResponseBodyWithList() throws Exception {
        List<?> expected = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        ResponseEntity<List<RequestMappingMessageConversionIntegrationTests.Person>> entity = performGet("/person-response/list", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST);
        Assert.assertEquals(36, entity.getHeaders().getContentLength());
        Assert.assertEquals(expected, entity.getBody());
    }

    @Test
    public void personResponseBodyWithPublisher() throws Exception {
        List<?> expected = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        ResponseEntity<List<RequestMappingMessageConversionIntegrationTests.Person>> entity = performGet("/person-response/publisher", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST);
        Assert.assertEquals((-1), entity.getHeaders().getContentLength());
        Assert.assertEquals(expected, entity.getBody());
    }

    @Test
    public void personResponseBodyWithFlux() throws Exception {
        List<?> expected = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        Assert.assertEquals(expected, performGet("/person-response/flux", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST).getBody());
    }

    @Test
    public void personResponseBodyWithObservable() throws Exception {
        List<?> expected = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        Assert.assertEquals(expected, performGet("/person-response/observable", RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST).getBody());
    }

    @Test
    public void resource() throws Exception {
        ResponseEntity<byte[]> response = performGet("/resource", new HttpHeaders(), byte[].class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(response.hasBody());
        Assert.assertEquals(951, response.getHeaders().getContentLength());
        Assert.assertEquals(951, response.getBody().length);
        Assert.assertEquals(new MediaType("image", "png"), response.getHeaders().getContentType());
    }

    @Test
    public void personTransform() throws Exception {
        Assert.assertEquals(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), performPost("/person-transform/person", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    @Test
    public void personTransformWithCompletableFuture() throws Exception {
        Assert.assertEquals(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), performPost("/person-transform/completable-future", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    @Test
    public void personTransformWithMono() throws Exception {
        Assert.assertEquals(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), performPost("/person-transform/mono", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    // SPR-16759
    @Test
    public void personTransformWithMonoAndXml() throws Exception {
        Assert.assertEquals(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), performPost("/person-transform/mono", APPLICATION_XML, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), APPLICATION_XML, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    @Test
    public void personTransformWithSingle() throws Exception {
        Assert.assertEquals(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), performPost("/person-transform/single", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    @Test
    public void personTransformWithRxJava2Single() throws Exception {
        Assert.assertEquals(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), performPost("/person-transform/rxjava2-single", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    @Test
    public void personTransformWithRxJava2Maybe() throws Exception {
        Assert.assertEquals(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), performPost("/person-transform/rxjava2-maybe", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.Person.class).getBody());
    }

    @Test
    public void personTransformWithPublisher() throws Exception {
        List<?> req = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        List<?> res = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), new RequestMappingMessageConversionIntegrationTests.Person("MARIE"));
        Assert.assertEquals(res, performPost("/person-transform/publisher", RequestMappingMessageConversionIntegrationTests.JSON, req, RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST).getBody());
    }

    @Test
    public void personTransformWithFlux() throws Exception {
        List<?> req = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        List<?> res = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), new RequestMappingMessageConversionIntegrationTests.Person("MARIE"));
        Assert.assertEquals(res, performPost("/person-transform/flux", RequestMappingMessageConversionIntegrationTests.JSON, req, RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST).getBody());
    }

    @Test
    public void personTransformWithObservable() throws Exception {
        List<?> req = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        List<?> res = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), new RequestMappingMessageConversionIntegrationTests.Person("MARIE"));
        Assert.assertEquals(res, performPost("/person-transform/observable", RequestMappingMessageConversionIntegrationTests.JSON, req, RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST).getBody());
    }

    @Test
    public void personTransformWithRxJava2Observable() throws Exception {
        List<?> req = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        List<?> res = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), new RequestMappingMessageConversionIntegrationTests.Person("MARIE"));
        Assert.assertEquals(res, performPost("/person-transform/rxjava2-observable", RequestMappingMessageConversionIntegrationTests.JSON, req, RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST).getBody());
    }

    @Test
    public void personTransformWithFlowable() throws Exception {
        List<?> req = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        List<?> res = Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("ROBERT"), new RequestMappingMessageConversionIntegrationTests.Person("MARIE"));
        Assert.assertEquals(res, performPost("/person-transform/flowable", RequestMappingMessageConversionIntegrationTests.JSON, req, RequestMappingMessageConversionIntegrationTests.JSON, RequestMappingMessageConversionIntegrationTests.PERSON_LIST).getBody());
    }

    @Test
    public void personCreateWithPublisherJson() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/publisher", RequestMappingMessageConversionIntegrationTests.JSON, Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie")), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithPublisherXml() throws Exception {
        RequestMappingMessageConversionIntegrationTests.People people = new RequestMappingMessageConversionIntegrationTests.People(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        ResponseEntity<Void> response = performPost("/person-create/publisher", APPLICATION_XML, people, null, Void.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithMono() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/mono", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(1, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithSingle() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/single", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(1, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithRxJava2Single() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/rxjava2-single", RequestMappingMessageConversionIntegrationTests.JSON, new RequestMappingMessageConversionIntegrationTests.Person("Robert"), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(1, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithFluxJson() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/flux", RequestMappingMessageConversionIntegrationTests.JSON, Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie")), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithFluxXml() throws Exception {
        RequestMappingMessageConversionIntegrationTests.People people = new RequestMappingMessageConversionIntegrationTests.People(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        ResponseEntity<Void> response = performPost("/person-create/flux", APPLICATION_XML, people, null, Void.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithObservableJson() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/observable", RequestMappingMessageConversionIntegrationTests.JSON, Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie")), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithRxJava2ObservableJson() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/rxjava2-observable", RequestMappingMessageConversionIntegrationTests.JSON, Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie")), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithObservableXml() throws Exception {
        RequestMappingMessageConversionIntegrationTests.People people = new RequestMappingMessageConversionIntegrationTests.People(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        ResponseEntity<Void> response = performPost("/person-create/observable", APPLICATION_XML, people, null, Void.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithRxJava2ObservableXml() throws Exception {
        RequestMappingMessageConversionIntegrationTests.People people = new RequestMappingMessageConversionIntegrationTests.People(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        String url = "/person-create/rxjava2-observable";
        ResponseEntity<Void> response = performPost(url, APPLICATION_XML, people, null, Void.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithFlowableJson() throws Exception {
        ResponseEntity<Void> entity = performPost("/person-create/flowable", RequestMappingMessageConversionIntegrationTests.JSON, Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie")), null, Void.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Test
    public void personCreateWithFlowableXml() throws Exception {
        RequestMappingMessageConversionIntegrationTests.People people = new RequestMappingMessageConversionIntegrationTests.People(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        ResponseEntity<Void> response = performPost("/person-create/flowable", APPLICATION_XML, people, null, Void.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(2, getApplicationContext().getBean(RequestMappingMessageConversionIntegrationTests.PersonCreateController.class).persons.size());
    }

    @Configuration
    @EnableWebFlux
    @ComponentScan(resourcePattern = "**/RequestMappingMessageConversionIntegrationTests$*.class")
    @SuppressWarnings({ "unused", "WeakerAccess" })
    static class WebConfig {}

    @RestController
    @RequestMapping("/raw-response")
    @SuppressWarnings("unused")
    private static class RawResponseBodyController {
        @GetMapping("/publisher")
        public Publisher<ByteBuffer> getPublisher() {
            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
            Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
            return encoder.encode(Mono.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert")), dataBufferFactory, ResolvableType.forClass(RequestMappingMessageConversionIntegrationTests.Person.class), RequestMappingMessageConversionIntegrationTests.JSON, Collections.emptyMap()).map(DataBuffer::asByteBuffer);
        }

        @GetMapping("/flux")
        public Flux<ByteBuffer> getFlux() {
            return Flux.just(ByteBuffer.wrap("Hello!".getBytes()));
        }

        @GetMapping("/mono")
        public Mono<ByteBuffer> getMonoString() {
            return Mono.just(ByteBuffer.wrap("Hello!".getBytes()));
        }

        @GetMapping("/observable")
        public Observable<ByteBuffer> getObservable() {
            return Observable.just(ByteBuffer.wrap("Hello!".getBytes()));
        }

        @GetMapping("/rxjava2-observable")
        public io.reactivex.Observable<ByteBuffer> getRxJava2Observable() {
            return io.reactivex.Observable.just(ByteBuffer.wrap("Hello!".getBytes()));
        }

        @GetMapping("/flowable")
        public Flowable<ByteBuffer> getFlowable() {
            return Flowable.just(ByteBuffer.wrap("Hello!".getBytes()));
        }
    }

    @RestController
    @RequestMapping("/person-response")
    @SuppressWarnings("unused")
    private static class PersonResponseBodyController {
        @GetMapping("/person")
        public RequestMappingMessageConversionIntegrationTests.Person getPerson() {
            return new RequestMappingMessageConversionIntegrationTests.Person("Robert");
        }

        @GetMapping("/completable-future")
        public CompletableFuture<RequestMappingMessageConversionIntegrationTests.Person> getCompletableFuture() {
            return CompletableFuture.completedFuture(new RequestMappingMessageConversionIntegrationTests.Person("Robert"));
        }

        @GetMapping("/mono")
        public Mono<RequestMappingMessageConversionIntegrationTests.Person> getMono() {
            return Mono.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"));
        }

        @GetMapping("/mono-empty")
        public Mono<RequestMappingMessageConversionIntegrationTests.Person> getMonoEmpty() {
            return Mono.empty();
        }

        @GetMapping("/mono-declared-as-object")
        public Object getMonoDeclaredAsObject() {
            return Mono.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"));
        }

        @GetMapping("/single")
        public Single<RequestMappingMessageConversionIntegrationTests.Person> getSingle() {
            return Single.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"));
        }

        @GetMapping("/mono-response-entity")
        public ResponseEntity<Mono<RequestMappingMessageConversionIntegrationTests.Person>> getMonoResponseEntity() {
            Mono<RequestMappingMessageConversionIntegrationTests.Person> body = Mono.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"));
            return ResponseEntity.ok(body);
        }

        @GetMapping("/mono-response-entity-xml")
        public ResponseEntity<Mono<RequestMappingMessageConversionIntegrationTests.Person>> getMonoResponseEntityXml() {
            Mono<RequestMappingMessageConversionIntegrationTests.Person> body = Mono.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"));
            return ResponseEntity.ok().contentType(APPLICATION_XML).body(body);
        }

        @GetMapping("/list")
        public List<RequestMappingMessageConversionIntegrationTests.Person> getList() {
            return Arrays.asList(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        }

        @GetMapping("/publisher")
        public Publisher<RequestMappingMessageConversionIntegrationTests.Person> getPublisher() {
            return Flux.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        }

        @GetMapping("/flux")
        public Flux<RequestMappingMessageConversionIntegrationTests.Person> getFlux() {
            return Flux.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        }

        @GetMapping("/observable")
        public Observable<RequestMappingMessageConversionIntegrationTests.Person> getObservable() {
            return Observable.just(new RequestMappingMessageConversionIntegrationTests.Person("Robert"), new RequestMappingMessageConversionIntegrationTests.Person("Marie"));
        }
    }

    @RestController
    @SuppressWarnings("unused")
    private static class ResourceController {
        @GetMapping("/resource")
        public Resource resource() {
            return new ClassPathResource("spring.png", ZeroCopyIntegrationTests.class);
        }
    }

    @RestController
    @RequestMapping("/person-transform")
    @SuppressWarnings("unused")
    private static class PersonTransformationController {
        @PostMapping("/person")
        public RequestMappingMessageConversionIntegrationTests.Person transformPerson(@RequestBody
        RequestMappingMessageConversionIntegrationTests.Person person) {
            return new RequestMappingMessageConversionIntegrationTests.Person(person.getName().toUpperCase());
        }

        @PostMapping("/completable-future")
        public CompletableFuture<RequestMappingMessageConversionIntegrationTests.Person> transformCompletableFuture(@RequestBody
        CompletableFuture<RequestMappingMessageConversionIntegrationTests.Person> personFuture) {
            return personFuture.thenApply(( person) -> new RequestMappingMessageConversionIntegrationTests.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/mono")
        public Mono<RequestMappingMessageConversionIntegrationTests.Person> transformMono(@RequestBody
        Mono<RequestMappingMessageConversionIntegrationTests.Person> personFuture) {
            return personFuture.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/single")
        public Single<RequestMappingMessageConversionIntegrationTests.Person> transformSingle(@RequestBody
        Single<RequestMappingMessageConversionIntegrationTests.Person> personFuture) {
            return personFuture.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/rxjava2-single")
        public io.reactivex.Single<RequestMappingMessageConversionIntegrationTests.Person> transformRxJava2Single(@RequestBody
        io.reactivex.Single<RequestMappingMessageConversionIntegrationTests.Person> personFuture) {
            return personFuture.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/rxjava2-maybe")
        public Maybe<RequestMappingMessageConversionIntegrationTests.Person> transformRxJava2Maybe(@RequestBody
        Maybe<RequestMappingMessageConversionIntegrationTests.Person> personFuture) {
            return personFuture.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/publisher")
        public Publisher<RequestMappingMessageConversionIntegrationTests.Person> transformPublisher(@RequestBody
        Publisher<RequestMappingMessageConversionIntegrationTests.Person> persons) {
            return Flux.from(persons).map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/flux")
        public Flux<RequestMappingMessageConversionIntegrationTests.Person> transformFlux(@RequestBody
        Flux<RequestMappingMessageConversionIntegrationTests.Person> persons) {
            return persons.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/observable")
        public Observable<RequestMappingMessageConversionIntegrationTests.Person> transformObservable(@RequestBody
        Observable<RequestMappingMessageConversionIntegrationTests.Person> persons) {
            return persons.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/rxjava2-observable")
        public io.reactivex.Observable<RequestMappingMessageConversionIntegrationTests.Person> transformObservable(@RequestBody
        io.reactivex.Observable<RequestMappingMessageConversionIntegrationTests.Person> persons) {
            return persons.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }

        @PostMapping("/flowable")
        public Flowable<RequestMappingMessageConversionIntegrationTests.Person> transformFlowable(@RequestBody
        Flowable<RequestMappingMessageConversionIntegrationTests.Person> persons) {
            return persons.map(( person) -> new org.springframework.web.reactive.result.method.annotation.Person(person.getName().toUpperCase()));
        }
    }

    @RestController
    @RequestMapping("/person-create")
    @SuppressWarnings("unused")
    private static class PersonCreateController {
        final List<RequestMappingMessageConversionIntegrationTests.Person> persons = new ArrayList<>();

        @PostMapping("/publisher")
        public Publisher<Void> createWithPublisher(@RequestBody
        Publisher<RequestMappingMessageConversionIntegrationTests.Person> publisher) {
            return Flux.from(publisher).doOnNext(persons::add).then();
        }

        @PostMapping("/mono")
        public Mono<Void> createWithMono(@RequestBody
        Mono<RequestMappingMessageConversionIntegrationTests.Person> mono) {
            return mono.doOnNext(persons::add).then();
        }

        @PostMapping("/single")
        public Completable createWithSingle(@RequestBody
        Single<RequestMappingMessageConversionIntegrationTests.Person> single) {
            return single.map(persons::add).toCompletable();
        }

        @PostMapping("/rxjava2-single")
        @SuppressWarnings("deprecation")
        public io.reactivex.Completable createWithRxJava2Single(@RequestBody
        io.reactivex.Single<RequestMappingMessageConversionIntegrationTests.Person> single) {
            return single.map(persons::add).toCompletable();
        }

        @PostMapping("/flux")
        public Mono<Void> createWithFlux(@RequestBody
        Flux<RequestMappingMessageConversionIntegrationTests.Person> flux) {
            return flux.doOnNext(persons::add).then();
        }

        @PostMapping("/observable")
        public Observable<Void> createWithObservable(@RequestBody
        Observable<RequestMappingMessageConversionIntegrationTests.Person> observable) {
            return observable.toList().doOnNext(persons::addAll).flatMap(( document) -> Observable.empty());
        }

        @PostMapping("/rxjava2-observable")
        @SuppressWarnings("deprecation")
        public io.reactivex.Completable createWithRxJava2Observable(@RequestBody
        io.reactivex.Observable<RequestMappingMessageConversionIntegrationTests.Person> observable) {
            return observable.toList().doOnSuccess(persons::addAll).toCompletable();
        }

        @PostMapping("/flowable")
        @SuppressWarnings("deprecation")
        public Completable createWithFlowable(@RequestBody
        Flowable<RequestMappingMessageConversionIntegrationTests.Person> flowable) {
            return flowable.toList().doOnSuccess(persons::addAll).toCompletable();
        }
    }

    @XmlRootElement
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
            RequestMappingMessageConversionIntegrationTests.Person person = ((RequestMappingMessageConversionIntegrationTests.Person) (o));
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

    @XmlRootElement
    @SuppressWarnings({ "WeakerAccess", "unused" })
    private static class People {
        private List<RequestMappingMessageConversionIntegrationTests.Person> persons = new ArrayList<>();

        public People() {
        }

        public People(RequestMappingMessageConversionIntegrationTests.Person... persons) {
            this.persons.addAll(Arrays.asList(persons));
        }

        @XmlElement
        public List<RequestMappingMessageConversionIntegrationTests.Person> getPerson() {
            return this.persons;
        }
    }
}

