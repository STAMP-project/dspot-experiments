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
package org.springframework.web.reactive.function.client.support;


import ClientResponse.Headers;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ClientResponseWrapperTests {
    private ClientResponse mockResponse;

    private ClientResponseWrapper wrapper;

    @Test
    public void response() {
        Assert.assertSame(mockResponse, wrapper.response());
    }

    @Test
    public void statusCode() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Mockito.when(mockResponse.statusCode()).thenReturn(status);
        Assert.assertSame(status, wrapper.statusCode());
    }

    @Test
    public void rawStatusCode() {
        int status = 999;
        Mockito.when(mockResponse.rawStatusCode()).thenReturn(status);
        Assert.assertEquals(status, wrapper.rawStatusCode());
    }

    @Test
    public void headers() {
        ClientResponse.Headers headers = Mockito.mock(Headers.class);
        Mockito.when(mockResponse.headers()).thenReturn(headers);
        Assert.assertSame(headers, wrapper.headers());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cookies() {
        MultiValueMap<String, ResponseCookie> cookies = Mockito.mock(MultiValueMap.class);
        Mockito.when(mockResponse.cookies()).thenReturn(cookies);
        Assert.assertSame(cookies, wrapper.cookies());
    }

    @Test
    public void bodyExtractor() {
        Mono<String> result = Mono.just("foo");
        BodyExtractor<Mono<String>, ReactiveHttpInputMessage> extractor = BodyExtractors.toMono(String.class);
        Mockito.when(mockResponse.body(extractor)).thenReturn(result);
        Assert.assertSame(result, wrapper.body(extractor));
    }

    @Test
    public void bodyToMonoClass() {
        Mono<String> result = Mono.just("foo");
        Mockito.when(mockResponse.bodyToMono(String.class)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToMono(String.class));
    }

    @Test
    public void bodyToMonoParameterizedTypeReference() {
        Mono<String> result = Mono.just("foo");
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
        Mockito.when(mockResponse.bodyToMono(reference)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToMono(reference));
    }

    @Test
    public void bodyToFluxClass() {
        Flux<String> result = Flux.just("foo");
        Mockito.when(mockResponse.bodyToFlux(String.class)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToFlux(String.class));
    }

    @Test
    public void bodyToFluxParameterizedTypeReference() {
        Flux<String> result = Flux.just("foo");
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
        Mockito.when(mockResponse.bodyToFlux(reference)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToFlux(reference));
    }

    @Test
    public void toEntityClass() {
        Mono<ResponseEntity<String>> result = Mono.just(new ResponseEntity("foo", HttpStatus.OK));
        Mockito.when(mockResponse.toEntity(String.class)).thenReturn(result);
        Assert.assertSame(result, wrapper.toEntity(String.class));
    }

    @Test
    public void toEntityParameterizedTypeReference() {
        Mono<ResponseEntity<String>> result = Mono.just(new ResponseEntity("foo", HttpStatus.OK));
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
        Mockito.when(mockResponse.toEntity(reference)).thenReturn(result);
        Assert.assertSame(result, wrapper.toEntity(reference));
    }

    @Test
    public void toEntityListClass() {
        Mono<ResponseEntity<List<String>>> result = Mono.just(new ResponseEntity(Collections.singletonList("foo"), HttpStatus.OK));
        Mockito.when(mockResponse.toEntityList(String.class)).thenReturn(result);
        Assert.assertSame(result, wrapper.toEntityList(String.class));
    }

    @Test
    public void toEntityListParameterizedTypeReference() {
        Mono<ResponseEntity<List<String>>> result = Mono.just(new ResponseEntity(Collections.singletonList("foo"), HttpStatus.OK));
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
        Mockito.when(mockResponse.toEntityList(reference)).thenReturn(result);
        Assert.assertSame(result, wrapper.toEntityList(reference));
    }
}

