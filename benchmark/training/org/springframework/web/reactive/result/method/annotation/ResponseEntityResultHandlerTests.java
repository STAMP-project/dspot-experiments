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


import HttpMethod.GET;
import HttpMethod.OPTIONS;
import HttpMethod.POST;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.HandlerResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Single;


/**
 * Unit tests for {@link ResponseEntityResultHandler}. When adding a test also
 * consider whether the logic under test is in a parent class, then see:
 * <ul>
 * <li>{@code MessageWriterResultHandlerTests},
 * <li>{@code ContentNegotiatingResultHandlerSupportTests}
 * </ul>
 *
 * @author Rossen Stoyanchev
 */
public class ResponseEntityResultHandlerTests {
    private ResponseEntityResultHandler resultHandler;

    @Test
    public void supports() throws Exception {
        Object value = null;
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(String.class));
        Assert.assertTrue(this.resultHandler.supports(handlerResult(value, returnType)));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Mono.class, entity(String.class));
        Assert.assertTrue(this.resultHandler.supports(handlerResult(value, returnType)));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Single.class, entity(String.class));
        Assert.assertTrue(this.resultHandler.supports(handlerResult(value, returnType)));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(CompletableFuture.class, entity(String.class));
        Assert.assertTrue(this.resultHandler.supports(handlerResult(value, returnType)));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(HttpHeaders.class);
        Assert.assertTrue(this.resultHandler.supports(handlerResult(value, returnType)));
        // SPR-15785
        value = ResponseEntity.ok("testing");
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Object.class);
        Assert.assertTrue(this.resultHandler.supports(handlerResult(value, returnType)));
    }

    @Test
    public void doesNotSupport() throws Exception {
        Object value = null;
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(String.class);
        Assert.assertFalse(this.resultHandler.supports(handlerResult(value, returnType)));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Completable.class);
        Assert.assertFalse(this.resultHandler.supports(handlerResult(value, returnType)));
        // SPR-15464
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Flux.class);
        Assert.assertFalse(this.resultHandler.supports(handlerResult(value, returnType)));
    }

    @Test
    public void defaultOrder() throws Exception {
        Assert.assertEquals(0, this.resultHandler.getOrder());
    }

    @Test
    public void responseEntityStatusCode() throws Exception {
        ResponseEntity<Void> value = ResponseEntity.noContent().build();
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(Void.class));
        HandlerResult result = handlerResult(value, returnType);
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path"));
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(NO_CONTENT, exchange.getResponse().getStatusCode());
        Assert.assertEquals(0, exchange.getResponse().getHeaders().size());
        assertResponseBodyIsEmpty(exchange);
    }

    @Test
    public void httpHeaders() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(new java.util.LinkedHashSet(Arrays.asList(GET, POST, OPTIONS)));
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(Void.class));
        HandlerResult result = handlerResult(headers, returnType);
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path"));
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(OK, exchange.getResponse().getStatusCode());
        Assert.assertEquals(1, exchange.getResponse().getHeaders().size());
        Assert.assertEquals("GET,POST,OPTIONS", exchange.getResponse().getHeaders().getFirst("Allow"));
        assertResponseBodyIsEmpty(exchange);
    }

    @Test
    public void responseEntityHeaders() throws Exception {
        URI location = new URI("/path");
        ResponseEntity<Void> value = ResponseEntity.created(location).build();
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(Void.class));
        HandlerResult result = handlerResult(value, returnType);
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path"));
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(CREATED, exchange.getResponse().getStatusCode());
        Assert.assertEquals(1, exchange.getResponse().getHeaders().size());
        Assert.assertEquals(location, exchange.getResponse().getHeaders().getLocation());
        assertResponseBodyIsEmpty(exchange);
    }

    @Test
    public void handleResponseEntityWithNullBody() throws Exception {
        Object returnValue = Mono.just(notFound().build());
        MethodParameter type = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Mono.class, entity(String.class));
        HandlerResult result = handlerResult(returnValue, type);
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path"));
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(NOT_FOUND, exchange.getResponse().getStatusCode());
        assertResponseBodyIsEmpty(exchange);
    }

    @Test
    public void handleReturnTypes() throws Exception {
        Object returnValue = ok("abc");
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(String.class));
        testHandle(returnValue, returnType);
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Object.class);
        testHandle(returnValue, returnType);
        returnValue = Mono.just(ok("abc"));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Mono.class, entity(String.class));
        testHandle(returnValue, returnType);
        returnValue = Mono.just(ok("abc"));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Single.class, entity(String.class));
        testHandle(returnValue, returnType);
        returnValue = Mono.just(ok("abc"));
        returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(CompletableFuture.class, entity(String.class));
        testHandle(returnValue, returnType);
    }

    @Test
    public void handleReturnValueLastModified() throws Exception {
        Instant currentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant oneMinAgo = currentTime.minusSeconds(60);
        long timestamp = currentTime.toEpochMilli();
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path").ifModifiedSince(timestamp));
        ResponseEntity<String> entity = ok().lastModified(oneMinAgo.toEpochMilli()).body("body");
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(String.class));
        HandlerResult result = handlerResult(entity, returnType);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        assertConditionalResponse(exchange, NOT_MODIFIED, null, null, oneMinAgo);
    }

    @Test
    public void handleReturnValueEtag() throws Exception {
        String etagValue = "\"deadb33f8badf00d\"";
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path").ifNoneMatch(etagValue));
        ResponseEntity<String> entity = ok().eTag(etagValue).body("body");
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(String.class));
        HandlerResult result = handlerResult(entity, returnType);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        assertConditionalResponse(exchange, NOT_MODIFIED, null, etagValue, Instant.MIN);
    }

    // SPR-14559
    @Test
    public void handleReturnValueEtagInvalidIfNoneMatch() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path").ifNoneMatch("unquoted"));
        ResponseEntity<String> entity = ok().eTag("\"deadb33f8badf00d\"").body("body");
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(String.class));
        HandlerResult result = handlerResult(entity, returnType);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(OK, exchange.getResponse().getStatusCode());
        assertResponseBody(exchange, "body");
    }

    @Test
    public void handleReturnValueETagAndLastModified() throws Exception {
        String eTag = "\"deadb33f8badf00d\"";
        Instant currentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant oneMinAgo = currentTime.minusSeconds(60);
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path").ifNoneMatch(eTag).ifModifiedSince(currentTime.toEpochMilli()));
        ResponseEntity<String> entity = ok().eTag(eTag).lastModified(oneMinAgo.toEpochMilli()).body("body");
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(String.class));
        HandlerResult result = handlerResult(entity, returnType);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        assertConditionalResponse(exchange, NOT_MODIFIED, null, eTag, oneMinAgo);
    }

    @Test
    public void handleReturnValueChangedETagAndLastModified() throws Exception {
        String etag = "\"deadb33f8badf00d\"";
        String newEtag = "\"changed-etag-value\"";
        Instant currentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant oneMinAgo = currentTime.minusSeconds(60);
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path").ifNoneMatch(etag).ifModifiedSince(currentTime.toEpochMilli()));
        ResponseEntity<String> entity = ok().eTag(newEtag).lastModified(oneMinAgo.toEpochMilli()).body("body");
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(String.class));
        HandlerResult result = handlerResult(entity, returnType);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        assertConditionalResponse(exchange, OK, "body", newEtag, oneMinAgo);
    }

    // SPR-14877
    @Test
    public void handleMonoWithWildcardBodyType() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path"));
        exchange.getAttributes().put(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(APPLICATION_JSON));
        MethodParameter type = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Mono.class, ResponseEntity.class);
        HandlerResult result = new HandlerResult(new ResponseEntityResultHandlerTests.TestController(), Mono.just(ok().body("body")), type);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(OK, exchange.getResponse().getStatusCode());
        assertResponseBody(exchange, "body");
    }

    // SPR-14877
    @Test
    public void handleMonoWithWildcardBodyTypeAndNullBody() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path"));
        exchange.getAttributes().put(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(APPLICATION_JSON));
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(Mono.class, ResponseEntity.class);
        HandlerResult result = new HandlerResult(new ResponseEntityResultHandlerTests.TestController(), Mono.just(notFound().build()), returnType);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(NOT_FOUND, exchange.getResponse().getStatusCode());
        assertResponseBodyIsEmpty(exchange);
    }

    // SPR-17082
    @Test
    public void handleResponseEntityWithExistingResponseHeaders() throws Exception {
        ResponseEntity<Void> value = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
        MethodParameter returnType = on(ResponseEntityResultHandlerTests.TestController.class).resolveReturnType(entity(Void.class));
        HandlerResult result = handlerResult(value, returnType);
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/path"));
        exchange.getResponse().getHeaders().setContentType(TEXT_PLAIN);
        this.resultHandler.handleResult(exchange, result).block(Duration.ofSeconds(5));
        Assert.assertEquals(OK, exchange.getResponse().getStatusCode());
        Assert.assertEquals(1, exchange.getResponse().getHeaders().size());
        Assert.assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
        assertResponseBodyIsEmpty(exchange);
    }

    @SuppressWarnings("unused")
    private static class TestController {
        ResponseEntity<String> responseEntityString() {
            return null;
        }

        ResponseEntity<Void> responseEntityVoid() {
            return null;
        }

        HttpHeaders httpHeaders() {
            return null;
        }

        Mono<ResponseEntity<String>> mono() {
            return null;
        }

        Single<ResponseEntity<String>> single() {
            return null;
        }

        CompletableFuture<ResponseEntity<String>> completableFuture() {
            return null;
        }

        String string() {
            return null;
        }

        Completable completable() {
            return null;
        }

        Mono<ResponseEntity<?>> monoResponseEntityWildcard() {
            return null;
        }

        Flux<?> fluxWildcard() {
            return null;
        }

        Object object() {
            return null;
        }
    }
}

