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
package org.springframework.http.server.reactive;


import HttpHeaders.CONTENT_LENGTH;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ResponseCookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class ServerHttpResponseTests {
    @Test
    public void writeWith() throws Exception {
        ServerHttpResponseTests.TestServerHttpResponse response = new ServerHttpResponseTests.TestServerHttpResponse();
        response.writeWith(Flux.just(wrap("a"), wrap("b"), wrap("c"))).block();
        TestCase.assertTrue(response.statusCodeWritten);
        TestCase.assertTrue(response.headersWritten);
        TestCase.assertTrue(response.cookiesWritten);
        Assert.assertEquals(3, response.body.size());
        Assert.assertEquals("a", new String(response.body.get(0).asByteBuffer().array(), StandardCharsets.UTF_8));
        Assert.assertEquals("b", new String(response.body.get(1).asByteBuffer().array(), StandardCharsets.UTF_8));
        Assert.assertEquals("c", new String(response.body.get(2).asByteBuffer().array(), StandardCharsets.UTF_8));
    }

    // SPR-14952
    @Test
    public void writeAndFlushWithFluxOfDefaultDataBuffer() throws Exception {
        ServerHttpResponseTests.TestServerHttpResponse response = new ServerHttpResponseTests.TestServerHttpResponse();
        Flux<Flux<DefaultDataBuffer>> flux = Flux.just(Flux.just(wrap("foo")));
        response.writeAndFlushWith(flux).block();
        TestCase.assertTrue(response.statusCodeWritten);
        TestCase.assertTrue(response.headersWritten);
        TestCase.assertTrue(response.cookiesWritten);
        Assert.assertEquals(1, response.body.size());
        Assert.assertEquals("foo", new String(response.body.get(0).asByteBuffer().array(), StandardCharsets.UTF_8));
    }

    @Test
    public void writeWithError() throws Exception {
        ServerHttpResponseTests.TestServerHttpResponse response = new ServerHttpResponseTests.TestServerHttpResponse();
        getHeaders().setContentLength(12);
        IllegalStateException error = new IllegalStateException("boo");
        response.writeWith(Flux.error(error)).onErrorResume(( ex) -> Mono.empty()).block();
        Assert.assertFalse(response.statusCodeWritten);
        Assert.assertFalse(response.headersWritten);
        Assert.assertFalse(response.cookiesWritten);
        Assert.assertFalse(getHeaders().containsKey(CONTENT_LENGTH));
        TestCase.assertTrue(response.body.isEmpty());
    }

    @Test
    public void setComplete() throws Exception {
        ServerHttpResponseTests.TestServerHttpResponse response = new ServerHttpResponseTests.TestServerHttpResponse();
        response.setComplete().block();
        TestCase.assertTrue(response.statusCodeWritten);
        TestCase.assertTrue(response.headersWritten);
        TestCase.assertTrue(response.cookiesWritten);
        TestCase.assertTrue(response.body.isEmpty());
    }

    @Test
    public void beforeCommitWithComplete() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("ID", "123").build();
        ServerHttpResponseTests.TestServerHttpResponse response = new ServerHttpResponseTests.TestServerHttpResponse();
        beforeCommit(() -> Mono.fromRunnable(() -> response.getCookies().add(cookie.getName(), cookie)));
        response.writeWith(Flux.just(wrap("a"), wrap("b"), wrap("c"))).block();
        TestCase.assertTrue(response.statusCodeWritten);
        TestCase.assertTrue(response.headersWritten);
        TestCase.assertTrue(response.cookiesWritten);
        Assert.assertSame(cookie, getCookies().getFirst("ID"));
        Assert.assertEquals(3, response.body.size());
        Assert.assertEquals("a", new String(response.body.get(0).asByteBuffer().array(), StandardCharsets.UTF_8));
        Assert.assertEquals("b", new String(response.body.get(1).asByteBuffer().array(), StandardCharsets.UTF_8));
        Assert.assertEquals("c", new String(response.body.get(2).asByteBuffer().array(), StandardCharsets.UTF_8));
    }

    @Test
    public void beforeCommitActionWithSetComplete() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("ID", "123").build();
        ServerHttpResponseTests.TestServerHttpResponse response = new ServerHttpResponseTests.TestServerHttpResponse();
        beforeCommit(() -> {
            response.getCookies().add(cookie.getName(), cookie);
            return Mono.empty();
        });
        response.setComplete().block();
        TestCase.assertTrue(response.statusCodeWritten);
        TestCase.assertTrue(response.headersWritten);
        TestCase.assertTrue(response.cookiesWritten);
        TestCase.assertTrue(response.body.isEmpty());
        Assert.assertSame(cookie, getCookies().getFirst("ID"));
    }

    private static class TestServerHttpResponse extends AbstractServerHttpResponse {
        private boolean statusCodeWritten;

        private boolean headersWritten;

        private boolean cookiesWritten;

        private final List<DataBuffer> body = new ArrayList<>();

        public TestServerHttpResponse() {
            super(new DefaultDataBufferFactory());
        }

        @Override
        public <T> T getNativeResponse() {
            throw new IllegalStateException("This is a mock. No running server, no native response.");
        }

        @Override
        public void applyStatusCode() {
            Assert.assertFalse(this.statusCodeWritten);
            this.statusCodeWritten = true;
        }

        @Override
        protected void applyHeaders() {
            Assert.assertFalse(this.headersWritten);
            this.headersWritten = true;
        }

        @Override
        protected void applyCookies() {
            Assert.assertFalse(this.cookiesWritten);
            this.cookiesWritten = true;
        }

        @Override
        protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body) {
            return Flux.from(body).map(( b) -> {
                this.body.add(b);
                return b;
            }).then();
        }

        @Override
        protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> bodyWithFlush) {
            return Flux.from(bodyWithFlush).flatMap(( body) -> Flux.from(body).map(( b) -> {
                this.body.add(b);
                return b;
            })).then();
        }
    }
}

