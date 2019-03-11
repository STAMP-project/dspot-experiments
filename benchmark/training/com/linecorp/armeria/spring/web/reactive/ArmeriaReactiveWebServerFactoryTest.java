/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.spring.web.reactive;


import HttpHeaderNames.CONTENT_TYPE;
import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.server.HttpStatusException;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.Ssl;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.SocketUtils;
import org.springframework.util.unit.DataSize;
import reactor.core.publisher.Mono;


public class ArmeriaReactiveWebServerFactoryTest {
    static final String POST_BODY = "Hello, world!";

    static final String[] names = new String[0];

    static ConfigurableListableBeanFactory beanFactory;

    static ClientFactory clientFactory;

    @Test
    public void shouldRunOnSpecifiedPort() {
        final ArmeriaReactiveWebServerFactory factory = factory();
        final int port = SocketUtils.findAvailableTcpPort();
        factory.setPort(port);
        runEchoServer(factory, ( server) -> {
            assertThat(server.getPort()).isEqualTo(port);
        });
    }

    @Test
    public void shouldReturnEchoResponse() {
        final ArmeriaReactiveWebServerFactory factory = factory();
        runEchoServer(factory, ( server) -> {
            final HttpClient client = httpClient(server);
            validateEchoResponse(sendPostRequest(client));
            final AggregatedHttpMessage res = client.get("/hello").aggregate().join();
            assertThat(res.status()).isEqualTo(com.linecorp.armeria.common.HttpStatus.OK);
            assertThat(res.contentUtf8()).isEmpty();
        });
    }

    @Test
    public void shouldConfigureTlsWithSelfSignedCertificate() {
        final ArmeriaReactiveWebServerFactory factory = factory();
        final Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        factory.setSsl(ssl);
        runEchoServer(factory, ( server) -> {
            validateEchoResponse(sendPostRequest(httpsClient(server)));
        });
    }

    @Test
    public void shouldReturnBadRequestDueToException() {
        final ArmeriaReactiveWebServerFactory factory = factory();
        runServer(factory, ArmeriaReactiveWebServerFactoryTest.AlwaysFailureHandler.INSTANCE, ( server) -> {
            final HttpClient client = httpClient(server);
            final AggregatedHttpMessage res1 = client.post("/hello", "hello").aggregate().join();
            assertThat(res1.status()).isEqualTo(com.linecorp.armeria.common.HttpStatus.BAD_REQUEST);
            final AggregatedHttpMessage res2 = client.get("/hello").aggregate().join();
            assertThat(res2.status()).isEqualTo(com.linecorp.armeria.common.HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    public void shouldReturnCompressedResponse() {
        final ArmeriaReactiveWebServerFactory factory = factory();
        final Compression compression = new Compression();
        compression.setEnabled(true);
        compression.setMinResponseSize(DataSize.ofBytes(1));
        compression.setMimeTypes(new String[]{ "text/plain" });
        compression.setExcludedUserAgents(new String[]{ "unknown-agent/[0-9]+\\.[0-9]+\\.[0-9]+$" });
        factory.setCompression(compression);
        runEchoServer(factory, ( server) -> {
            final AggregatedHttpMessage res = sendPostRequest(httpClient(server));
            assertThat(res.status()).isEqualTo(com.linecorp.armeria.common.HttpStatus.OK);
            assertThat(res.headers().get(HttpHeaderNames.CONTENT_ENCODING)).isEqualTo("gzip");
            assertThat(res.contentUtf8()).isNotEqualTo("hello");
        });
    }

    @Test
    public void shouldReturnNonCompressedResponse_dueToContentType() {
        final ArmeriaReactiveWebServerFactory factory = factory();
        final Compression compression = new Compression();
        compression.setEnabled(true);
        compression.setMinResponseSize(DataSize.ofBytes(1));
        compression.setMimeTypes(new String[]{ "text/html" });
        factory.setCompression(compression);
        runEchoServer(factory, ( server) -> {
            final AggregatedHttpMessage res = sendPostRequest(httpClient(server));
            validateEchoResponse(res);
            assertThat(res.headers().get(HttpHeaderNames.CONTENT_ENCODING)).isNull();
        });
    }

    @Test
    public void shouldReturnNonCompressedResponse_dueToUserAgent() {
        final ArmeriaReactiveWebServerFactory factory = factory();
        final Compression compression = new Compression();
        compression.setEnabled(true);
        compression.setMinResponseSize(DataSize.ofBytes(1));
        compression.setExcludedUserAgents(new String[]{ "test-agent/[0-9]+\\.[0-9]+\\.[0-9]+$" });
        factory.setCompression(compression);
        runEchoServer(factory, ( server) -> {
            final AggregatedHttpMessage res = sendPostRequest(httpClient(server));
            validateEchoResponse(res);
            assertThat(res.headers().get(HttpHeaderNames.CONTENT_ENCODING)).isNull();
        });
    }

    static class EchoHandler implements HttpHandler {
        static final ArmeriaReactiveWebServerFactoryTest.EchoHandler INSTANCE = new ArmeriaReactiveWebServerFactoryTest.EchoHandler();

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            response.setStatusCode(OK);
            response.getHeaders().add(CONTENT_TYPE.toString(), PLAIN_TEXT_UTF_8.toString());
            return response.writeWith(request.getBody());
        }
    }

    static class AlwaysFailureHandler implements HttpHandler {
        static final ArmeriaReactiveWebServerFactoryTest.AlwaysFailureHandler INSTANCE = new ArmeriaReactiveWebServerFactoryTest.AlwaysFailureHandler();

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            response.setStatusCode(OK);
            return request.getBody().map(( data) -> {
                // skip data, then throw an exception.
                throw HttpStatusException.of(com.linecorp.armeria.common.HttpStatus.BAD_REQUEST);
            }).doOnComplete(() -> {
                // An HTTP GET request doesn't have a body, so onComplete will be immediately called.
                throw HttpStatusException.of(com.linecorp.armeria.common.HttpStatus.BAD_REQUEST);
            }).then();
        }
    }
}

