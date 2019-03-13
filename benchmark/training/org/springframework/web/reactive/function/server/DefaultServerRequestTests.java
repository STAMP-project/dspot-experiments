/**
 * Copyright 2002-2019 the original author or authors.
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


import HttpHeaders.CONTENT_TYPE;
import HttpMethod.GET;
import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN;
import RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import ServerRequest.Headers;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class DefaultServerRequestTests {
    private final List<HttpMessageReader<?>> messageReaders = Arrays.asList(new org.springframework.http.codec.DecoderHttpMessageReader(new Jackson2JsonDecoder()), new org.springframework.http.codec.DecoderHttpMessageReader(StringDecoder.allMimeTypes()));

    @Test
    public void method() {
        HttpMethod method = HttpMethod.HEAD;
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(MockServerHttpRequest.method(method, "http://example.com")), this.messageReaders);
        Assert.assertEquals(method, request.method());
    }

    @Test
    public void uri() {
        URI uri = URI.create("https://example.com");
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(MockServerHttpRequest.method(GET, uri)), this.messageReaders);
        Assert.assertEquals(uri, request.uri());
    }

    @Test
    public void uriBuilder() throws URISyntaxException {
        URI uri = new URI("http", "localhost", "/path", "a=1", null);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(MockServerHttpRequest.method(GET, uri)), this.messageReaders);
        URI result = request.uriBuilder().build();
        Assert.assertEquals("http", result.getScheme());
        Assert.assertEquals("localhost", result.getHost());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("/path", result.getPath());
        Assert.assertEquals("a=1", result.getQuery());
    }

    @Test
    public void attribute() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.method(GET, "http://example.com"));
        exchange.getAttributes().put("foo", "bar");
        DefaultServerRequest request = new DefaultServerRequest(exchange, messageReaders);
        Assert.assertEquals(Optional.of("bar"), request.attribute("foo"));
    }

    @Test
    public void queryParams() {
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(MockServerHttpRequest.method(GET, "http://example.com?foo=bar")), this.messageReaders);
        Assert.assertEquals(Optional.of("bar"), request.queryParam("foo"));
    }

    @Test
    public void emptyQueryParam() {
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(MockServerHttpRequest.method(GET, "http://example.com?foo")), this.messageReaders);
        Assert.assertEquals(Optional.of(""), request.queryParam("foo"));
    }

    @Test
    public void absentQueryParam() {
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(MockServerHttpRequest.method(GET, "http://example.com?foo")), this.messageReaders);
        Assert.assertEquals(Optional.empty(), request.queryParam("bar"));
    }

    @Test
    public void pathVariable() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://example.com"));
        Map<String, String> pathVariables = Collections.singletonMap("foo", "bar");
        exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        DefaultServerRequest request = new DefaultServerRequest(exchange, messageReaders);
        Assert.assertEquals("bar", request.pathVariable("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathVariableNotFound() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://example.com"));
        Map<String, String> pathVariables = Collections.singletonMap("foo", "bar");
        exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        DefaultServerRequest request = new DefaultServerRequest(exchange, messageReaders);
        request.pathVariable("baz");
    }

    @Test
    public void pathVariables() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://example.com"));
        Map<String, String> pathVariables = Collections.singletonMap("foo", "bar");
        exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        DefaultServerRequest request = new DefaultServerRequest(exchange, messageReaders);
        Assert.assertEquals(pathVariables, request.pathVariables());
    }

    @Test
    public void header() {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<MediaType> accept = Collections.singletonList(APPLICATION_JSON);
        httpHeaders.setAccept(accept);
        List<Charset> acceptCharset = Collections.singletonList(StandardCharsets.UTF_8);
        httpHeaders.setAcceptCharset(acceptCharset);
        long contentLength = 42L;
        httpHeaders.setContentLength(contentLength);
        MediaType contentType = MediaType.TEXT_PLAIN;
        httpHeaders.setContentType(contentType);
        InetSocketAddress host = InetSocketAddress.createUnresolved("localhost", 80);
        httpHeaders.setHost(host);
        List<HttpRange> range = Collections.singletonList(HttpRange.createByteRange(0, 42));
        httpHeaders.setRange(range);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(MockServerHttpRequest.method(GET, "http://example.com?foo=bar").headers(httpHeaders)), this.messageReaders);
        ServerRequest.Headers headers = request.headers();
        Assert.assertEquals(accept, headers.accept());
        Assert.assertEquals(acceptCharset, headers.acceptCharset());
        Assert.assertEquals(OptionalLong.of(contentLength), headers.contentLength());
        Assert.assertEquals(Optional.of(contentType), headers.contentType());
        Assert.assertEquals(httpHeaders, headers.asHttpHeaders());
    }

    @Test
    public void cookies() {
        HttpCookie cookie = new HttpCookie("foo", "bar");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.method(GET, "http://example.com").cookie(cookie));
        DefaultServerRequest request = new DefaultServerRequest(exchange, messageReaders);
        MultiValueMap<String, HttpCookie> expected = new org.springframework.util.LinkedMultiValueMap();
        expected.add("foo", cookie);
        Assert.assertEquals(expected, request.cookies());
    }

    @Test
    public void body() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com?foo=bar").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), messageReaders);
        Mono<String> resultMono = request.body(toMono(String.class));
        Assert.assertEquals("foo", resultMono.block());
    }

    @Test
    public void bodyToMono() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com?foo=bar").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), messageReaders);
        Mono<String> resultMono = request.bodyToMono(String.class);
        Assert.assertEquals("foo", resultMono.block());
    }

    @Test
    public void bodyToMonoParameterizedTypeReference() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com?foo=bar").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), messageReaders);
        ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<String>() {};
        Mono<String> resultMono = request.bodyToMono(typeReference);
        Assert.assertEquals("foo", resultMono.block());
    }

    @Test
    public void bodyToMonoDecodingException() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("{\"invalid\":\"json\" ".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(HttpMethod.POST, "http://example.com/invalid").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), messageReaders);
        Mono<Map<String, String>> resultMono = request.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {});
        StepVerifier.create(resultMono).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void bodyToFlux() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com?foo=bar").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), messageReaders);
        Flux<String> resultFlux = request.bodyToFlux(String.class);
        Assert.assertEquals(Collections.singletonList("foo"), resultFlux.collectList().block());
    }

    @Test
    public void bodyToFluxParameterizedTypeReference() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com?foo=bar").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), messageReaders);
        ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<String>() {};
        Flux<String> resultFlux = request.bodyToFlux(typeReference);
        Assert.assertEquals(Collections.singletonList("foo"), resultFlux.collectList().block());
    }

    @Test
    public void bodyUnacceptable() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(TEXT_PLAIN);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com?foo=bar").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), Collections.emptyList());
        Flux<String> resultFlux = request.bodyToFlux(String.class);
        StepVerifier.create(resultFlux).expectError(UnsupportedMediaTypeStatusException.class).verify();
    }

    @Test
    public void formData() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap("foo=bar&baz=qux".getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_FORM_URLENCODED);
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), Collections.emptyList());
        Mono<MultiValueMap<String, String>> resultData = request.formData();
        StepVerifier.create(resultData).consumeNextWith(( formData) -> {
            assertEquals(2, formData.size());
            assertEquals("bar", formData.getFirst("foo"));
            assertEquals("qux", formData.getFirst("baz"));
        }).verifyComplete();
    }

    @Test
    public void multipartData() {
        String data = "--12345\r\n" + ((((((("Content-Disposition: form-data; name=\"foo\"\r\n" + "\r\n") + "bar\r\n") + "--12345\r\n") + "Content-Disposition: form-data; name=\"baz\"\r\n") + "\r\n") + "qux\r\n") + "--12345--\r\n");
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DefaultDataBuffer dataBuffer = factory.wrap(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
        Flux<DataBuffer> body = Flux.just(dataBuffer);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CONTENT_TYPE, "multipart/form-data; boundary=12345");
        MockServerHttpRequest mockRequest = MockServerHttpRequest.method(GET, "http://example.com").headers(httpHeaders).body(body);
        DefaultServerRequest request = new DefaultServerRequest(MockServerWebExchange.from(mockRequest), Collections.emptyList());
        Mono<MultiValueMap<String, Part>> resultData = request.multipartData();
        StepVerifier.create(resultData).consumeNextWith(( formData) -> {
            assertEquals(2, formData.size());
            Part part = formData.getFirst("foo");
            assertTrue((part instanceof FormFieldPart));
            FormFieldPart formFieldPart = ((FormFieldPart) (part));
            assertEquals("bar", formFieldPart.value());
            part = formData.getFirst("baz");
            assertTrue((part instanceof FormFieldPart));
            formFieldPart = ((FormFieldPart) (part));
            assertEquals("qux", formFieldPart.value());
        }).verifyComplete();
    }
}

