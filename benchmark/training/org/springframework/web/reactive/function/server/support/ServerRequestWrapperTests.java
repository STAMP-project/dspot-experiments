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
package org.springframework.web.reactive.function.server.support;


import ServerRequest.Headers;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ServerRequestWrapperTests {
    private final ServerRequest mockRequest = Mockito.mock(ServerRequest.class);

    private final ServerRequestWrapper wrapper = new ServerRequestWrapper(mockRequest);

    @Test
    public void request() {
        Assert.assertSame(mockRequest, wrapper.request());
    }

    @Test
    public void method() {
        HttpMethod method = HttpMethod.POST;
        Mockito.when(mockRequest.method()).thenReturn(method);
        Assert.assertSame(method, wrapper.method());
    }

    @Test
    public void uri() {
        URI uri = URI.create("https://example.com");
        Mockito.when(mockRequest.uri()).thenReturn(uri);
        Assert.assertSame(uri, wrapper.uri());
    }

    @Test
    public void path() {
        String path = "/foo/bar";
        Mockito.when(mockRequest.path()).thenReturn(path);
        Assert.assertSame(path, wrapper.path());
    }

    @Test
    public void headers() {
        ServerRequest.Headers headers = Mockito.mock(Headers.class);
        Mockito.when(mockRequest.headers()).thenReturn(headers);
        Assert.assertSame(headers, wrapper.headers());
    }

    @Test
    public void attribute() {
        String name = "foo";
        String value = "bar";
        Mockito.when(mockRequest.attribute(name)).thenReturn(Optional.of(value));
        Assert.assertEquals(Optional.of(value), wrapper.attribute(name));
    }

    @Test
    public void queryParam() {
        String name = "foo";
        String value = "bar";
        Mockito.when(mockRequest.queryParam(name)).thenReturn(Optional.of(value));
        Assert.assertEquals(Optional.of(value), wrapper.queryParam(name));
    }

    @Test
    public void queryParams() {
        MultiValueMap<String, String> value = new org.springframework.util.LinkedMultiValueMap();
        value.add("foo", "bar");
        Mockito.when(mockRequest.queryParams()).thenReturn(value);
        Assert.assertSame(value, wrapper.queryParams());
    }

    @Test
    public void pathVariable() {
        String name = "foo";
        String value = "bar";
        Mockito.when(mockRequest.pathVariable(name)).thenReturn(value);
        Assert.assertEquals(value, wrapper.pathVariable(name));
    }

    @Test
    public void pathVariables() {
        Map<String, String> pathVariables = Collections.singletonMap("foo", "bar");
        Mockito.when(mockRequest.pathVariables()).thenReturn(pathVariables);
        Assert.assertSame(pathVariables, wrapper.pathVariables());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cookies() {
        MultiValueMap<String, HttpCookie> cookies = Mockito.mock(MultiValueMap.class);
        Mockito.when(mockRequest.cookies()).thenReturn(cookies);
        Assert.assertSame(cookies, wrapper.cookies());
    }

    @Test
    public void bodyExtractor() {
        Mono<String> result = Mono.just("foo");
        BodyExtractor<Mono<String>, ReactiveHttpInputMessage> extractor = BodyExtractors.toMono(String.class);
        Mockito.when(mockRequest.body(extractor)).thenReturn(result);
        Assert.assertSame(result, wrapper.body(extractor));
    }

    @Test
    public void bodyToMonoClass() {
        Mono<String> result = Mono.just("foo");
        Mockito.when(mockRequest.bodyToMono(String.class)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToMono(String.class));
    }

    @Test
    public void bodyToMonoParameterizedTypeReference() {
        Mono<String> result = Mono.just("foo");
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
        Mockito.when(mockRequest.bodyToMono(reference)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToMono(reference));
    }

    @Test
    public void bodyToFluxClass() {
        Flux<String> result = Flux.just("foo");
        Mockito.when(mockRequest.bodyToFlux(String.class)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToFlux(String.class));
    }

    @Test
    public void bodyToFluxParameterizedTypeReference() {
        Flux<String> result = Flux.just("foo");
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
        Mockito.when(mockRequest.bodyToFlux(reference)).thenReturn(result);
        Assert.assertSame(result, wrapper.bodyToFlux(reference));
    }
}

