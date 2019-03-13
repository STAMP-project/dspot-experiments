/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.web.reactive.server;


import HttpHeaderNames.CONTENT_ENCODING;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import Ssl.ClientAuth.NEED;
import Ssl.ClientAuth.WANT;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.SocketUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * Base for testing classes that extends {@link AbstractReactiveWebServerFactory}.
 *
 * @author Brian Clozel
 */
public abstract class AbstractReactiveWebServerFactoryTests {
    @Rule
    public OutputCapture output = new OutputCapture();

    protected WebServer webServer;

    @Test
    public void specificPort() {
        AbstractReactiveWebServerFactory factory = getFactory();
        int specificPort = SocketUtils.findAvailableTcpPort(41000);
        factory.setPort(specificPort);
        this.webServer = factory.getWebServer(new AbstractReactiveWebServerFactoryTests.EchoHandler());
        this.webServer.start();
        Mono<String> result = getWebClient().build().post().uri("/test").contentType(TEXT_PLAIN).body(BodyInserters.fromObject("Hello World")).exchange().flatMap(( response) -> response.bodyToMono(.class));
        assertThat(result.block(Duration.ofSeconds(30))).isEqualTo("Hello World");
        assertThat(this.webServer.getPort()).isEqualTo(specificPort);
    }

    @Test
    public void basicSslFromClassPath() {
        testBasicSslWithKeyStore("classpath:test.jks", "password");
    }

    @Test
    public void basicSslFromFileSystem() {
        testBasicSslWithKeyStore("src/test/resources/test.jks", "password");
    }

    @Test
    public void sslWantsClientAuthenticationSucceedsWithClientCertificate() throws Exception {
        Ssl ssl = new Ssl();
        ssl.setClientAuth(WANT);
        ssl.setKeyStore("classpath:test.jks");
        ssl.setKeyPassword("password");
        ssl.setTrustStore("classpath:test.jks");
        testClientAuthSuccess(ssl, buildTrustAllSslWithClientKeyConnector());
    }

    @Test
    public void sslWantsClientAuthenticationSucceedsWithoutClientCertificate() {
        Ssl ssl = new Ssl();
        ssl.setClientAuth(WANT);
        ssl.setKeyStore("classpath:test.jks");
        ssl.setKeyPassword("password");
        ssl.setTrustStore("classpath:test.jks");
        testClientAuthSuccess(ssl, buildTrustAllSslConnector());
    }

    @Test
    public void sslNeedsClientAuthenticationSucceedsWithClientCertificate() throws Exception {
        Ssl ssl = new Ssl();
        ssl.setClientAuth(NEED);
        ssl.setKeyStore("classpath:test.jks");
        ssl.setKeyPassword("password");
        ssl.setTrustStore("classpath:test.jks");
        testClientAuthSuccess(ssl, buildTrustAllSslWithClientKeyConnector());
    }

    @Test
    public void sslNeedsClientAuthenticationFailsWithoutClientCertificate() {
        Ssl ssl = new Ssl();
        ssl.setClientAuth(NEED);
        ssl.setKeyStore("classpath:test.jks");
        ssl.setKeyPassword("password");
        ssl.setTrustStore("classpath:test.jks");
        testClientAuthFailure(ssl, buildTrustAllSslConnector());
    }

    @Test
    public void compressionOfResponseToGetRequest() {
        WebClient client = prepareCompressionTest();
        ResponseEntity<Void> response = client.get().exchange().flatMap(( res) -> res.toEntity(.class)).block(Duration.ofSeconds(30));
        assertResponseIsCompressed(response);
    }

    @Test
    public void compressionOfResponseToPostRequest() {
        WebClient client = prepareCompressionTest();
        ResponseEntity<Void> response = client.post().exchange().flatMap(( res) -> res.toEntity(.class)).block(Duration.ofSeconds(30));
        assertResponseIsCompressed(response);
    }

    @Test
    public void noCompressionForSmallResponse() {
        Compression compression = new Compression();
        compression.setEnabled(true);
        compression.setMinResponseSize(DataSize.ofBytes(3001));
        WebClient client = prepareCompressionTest(compression);
        ResponseEntity<Void> response = client.get().exchange().flatMap(( res) -> res.toEntity(.class)).block(Duration.ofSeconds(30));
        assertResponseIsNotCompressed(response);
    }

    @Test
    public void noCompressionForMimeType() {
        Compression compression = new Compression();
        compression.setMimeTypes(new String[]{ "application/json" });
        WebClient client = prepareCompressionTest(compression);
        ResponseEntity<Void> response = client.get().exchange().flatMap(( res) -> res.toEntity(.class)).block(Duration.ofSeconds(30));
        assertResponseIsNotCompressed(response);
    }

    @Test
    public void noCompressionForUserAgent() {
        Compression compression = new Compression();
        compression.setEnabled(true);
        compression.setExcludedUserAgents(new String[]{ "testUserAgent" });
        WebClient client = prepareCompressionTest(compression);
        ResponseEntity<Void> response = client.get().header("User-Agent", "testUserAgent").exchange().flatMap(( res) -> res.toEntity(.class)).block(Duration.ofSeconds(30));
        assertResponseIsNotCompressed(response);
    }

    @Test
    public void whenSslIsEnabledAndNoKeyStoreIsConfiguredThenServerFailsToStart() {
        assertThatThrownBy(() -> testBasicSslWithKeyStore(null, null)).hasMessageContaining("Could not load key store 'null'");
    }

    protected static class EchoHandler implements HttpHandler {
        public EchoHandler() {
        }

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            response.setStatusCode(OK);
            return response.writeWith(request.getBody());
        }
    }

    protected static class CompressionDetectionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof HttpResponse) {
                HttpResponse response = ((HttpResponse) (msg));
                boolean compressed = response.headers().contains(CONTENT_ENCODING, "gzip", true);
                if (compressed) {
                    response.headers().set("X-Test-Compressed", "true");
                }
            }
            ctx.fireChannelRead(msg);
        }
    }

    protected static class CharsHandler implements HttpHandler {
        private static final DefaultDataBufferFactory factory = new DefaultDataBufferFactory();

        private final DataBuffer bytes;

        private final MediaType mediaType;

        public CharsHandler(int contentSize, MediaType mediaType) {
            char[] chars = new char[contentSize];
            Arrays.fill(chars, 'F');
            this.bytes = AbstractReactiveWebServerFactoryTests.CharsHandler.factory.wrap(new String(chars).getBytes(StandardCharsets.UTF_8));
            this.mediaType = mediaType;
        }

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            response.setStatusCode(OK);
            response.getHeaders().setContentType(this.mediaType);
            response.getHeaders().setContentLength(this.bytes.readableByteCount());
            return response.writeWith(Mono.just(this.bytes));
        }
    }

    protected static class XForwardedHandler implements HttpHandler {
        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            String scheme = request.getURI().getScheme();
            DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
            DataBuffer buffer = bufferFactory.wrap(scheme.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}

