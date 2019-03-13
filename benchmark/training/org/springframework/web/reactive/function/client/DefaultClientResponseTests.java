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
package org.springframework.web.reactive.function.client;


import ClientResponse.Headers;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Denys Ivano
 */
public class DefaultClientResponseTests {
    private ClientHttpResponse mockResponse;

    private ExchangeStrategies mockExchangeStrategies;

    private DefaultClientResponse defaultClientResponse;

    @Test
    public void statusCode() {
        HttpStatus status = HttpStatus.CONTINUE;
        Mockito.when(mockResponse.getStatusCode()).thenReturn(status);
        Assert.assertEquals(status, defaultClientResponse.statusCode());
    }

    @Test
    public void rawStatusCode() {
        int status = 999;
        Mockito.when(mockResponse.getRawStatusCode()).thenReturn(status);
        Assert.assertEquals(status, defaultClientResponse.rawStatusCode());
    }

    @Test
    public void header() {
        HttpHeaders httpHeaders = new HttpHeaders();
        long contentLength = 42L;
        httpHeaders.setContentLength(contentLength);
        MediaType contentType = MediaType.TEXT_PLAIN;
        httpHeaders.setContentType(contentType);
        InetSocketAddress host = InetSocketAddress.createUnresolved("localhost", 80);
        httpHeaders.setHost(host);
        List<HttpRange> range = Collections.singletonList(HttpRange.createByteRange(0, 42));
        httpHeaders.setRange(range);
        Mockito.when(mockResponse.getHeaders()).thenReturn(httpHeaders);
        ClientResponse.Headers headers = defaultClientResponse.headers();
        Assert.assertEquals(OptionalLong.of(contentLength), headers.contentLength());
        Assert.assertEquals(Optional.of(contentType), headers.contentType());
        Assert.assertEquals(httpHeaders, headers.asHttpHeaders());
    }

    @Test
    public void cookies() {
        ResponseCookie cookie = ResponseCookie.from("foo", "bar").build();
        MultiValueMap<String, ResponseCookie> cookies = new org.springframework.util.LinkedMultiValueMap();
        cookies.add("foo", cookie);
        Mockito.when(mockResponse.getCookies()).thenReturn(cookies);
        Assert.assertSame(cookies, defaultClientResponse.cookies());
    }

    @Test
    public void body() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        Mono<String> resultMono = defaultClientResponse.body(BodyExtractors.toMono(String.class));
        Assert.assertEquals("foo", resultMono.block());
    }

    @Test
    public void bodyToMono() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        Mono<String> resultMono = defaultClientResponse.bodyToMono(String.class);
        Assert.assertEquals("foo", resultMono.block());
    }

    @Test
    public void bodyToMonoTypeReference() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        Mono<String> resultMono = defaultClientResponse.bodyToMono(new org.springframework.core.ParameterizedTypeReference<String>() {});
        Assert.assertEquals("foo", resultMono.block());
    }

    @Test
    public void bodyToFlux() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        Flux<String> resultFlux = defaultClientResponse.bodyToFlux(String.class);
        Mono<List<String>> result = resultFlux.collectList();
        Assert.assertEquals(Collections.singletonList("foo"), result.block());
    }

    @Test
    public void bodyToFluxTypeReference() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        Flux<String> resultFlux = defaultClientResponse.bodyToFlux(new org.springframework.core.ParameterizedTypeReference<String>() {});
        Mono<List<String>> result = resultFlux.collectList();
        Assert.assertEquals(Collections.singletonList("foo"), result.block());
    }

    @Test
    public void toEntity() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        ResponseEntity<String> result = defaultClientResponse.toEntity(String.class).block();
        Assert.assertEquals("foo", result.getBody());
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(OK.value(), result.getStatusCodeValue());
        Assert.assertEquals(TEXT_PLAIN, result.getHeaders().getContentType());
    }

    @Test
    public void toEntityWithUnknownStatusCode() throws Exception {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        Mockito.when(mockResponse.getHeaders()).thenReturn(httpHeaders);
        Mockito.when(mockResponse.getStatusCode()).thenThrow(new IllegalArgumentException("999"));
        Mockito.when(mockResponse.getRawStatusCode()).thenReturn(999);
        Mockito.when(mockResponse.getBody()).thenReturn(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        ResponseEntity<String> result = defaultClientResponse.toEntity(String.class).block();
        Assert.assertEquals("foo", result.getBody());
        try {
            result.getStatusCode();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // do nothing
        }
        Assert.assertEquals(999, result.getStatusCodeValue());
        Assert.assertEquals(TEXT_PLAIN, result.getHeaders().getContentType());
    }

    @Test
    public void toEntityTypeReference() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        ResponseEntity<String> result = defaultClientResponse.toEntity(new org.springframework.core.ParameterizedTypeReference<String>() {}).block();
        Assert.assertEquals("foo", result.getBody());
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(OK.value(), result.getStatusCodeValue());
        Assert.assertEquals(TEXT_PLAIN, result.getHeaders().getContentType());
    }

    @Test
    public void toEntityList() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        ResponseEntity<List<String>> result = defaultClientResponse.toEntityList(String.class).block();
        Assert.assertEquals(Collections.singletonList("foo"), result.getBody());
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(OK.value(), result.getStatusCodeValue());
        Assert.assertEquals(TEXT_PLAIN, result.getHeaders().getContentType());
    }

    @Test
    public void toEntityListWithUnknownStatusCode() throws Exception {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        Mockito.when(mockResponse.getHeaders()).thenReturn(httpHeaders);
        Mockito.when(mockResponse.getStatusCode()).thenThrow(new IllegalArgumentException("999"));
        Mockito.when(mockResponse.getRawStatusCode()).thenReturn(999);
        Mockito.when(mockResponse.getBody()).thenReturn(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        ResponseEntity<List<String>> result = defaultClientResponse.toEntityList(String.class).block();
        Assert.assertEquals(Collections.singletonList("foo"), result.getBody());
        try {
            result.getStatusCode();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // do nothing
        }
        Assert.assertEquals(999, result.getStatusCodeValue());
        Assert.assertEquals(TEXT_PLAIN, result.getHeaders().getContentType());
    }

    @Test
    public void toEntityListTypeReference() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        mockTextPlainResponse(body);
        List<HttpMessageReader<?>> messageReaders = Collections.singletonList(new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        Mockito.when(mockExchangeStrategies.messageReaders()).thenReturn(messageReaders);
        ResponseEntity<List<String>> result = defaultClientResponse.toEntityList(new org.springframework.core.ParameterizedTypeReference<String>() {}).block();
        Assert.assertEquals(Collections.singletonList("foo"), result.getBody());
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(OK.value(), result.getStatusCodeValue());
        Assert.assertEquals(TEXT_PLAIN, result.getHeaders().getContentType());
    }
}

