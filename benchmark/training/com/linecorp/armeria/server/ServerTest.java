/**
 * Copyright 2016 LINE Corporation
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
package com.linecorp.armeria.server;


import ChannelOption.SO_BACKLOG;
import HttpStatus.OK;
import HttpStatusClass.SUCCESS;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.metric.PrometheusMeterRegistries;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import com.linecorp.armeria.testing.server.ServerRule;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Test;


public class ServerTest {
    private static final long processDelayMillis = 1000;

    private static final long requestTimeoutMillis = 500;

    private static final long idleTimeoutMillis = 500;

    private static final EventExecutorGroup asyncExecutorGroup = new DefaultEventExecutorGroup(1);

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.channelOption(SO_BACKLOG, 1024);
            sb.meterRegistry(PrometheusMeterRegistries.newRegistry());
            final Service<HttpRequest, HttpResponse> immediateResponseOnIoThread = new ServerTest.EchoService().decorate(LoggingService.newDecorator());
            final Service<HttpRequest, HttpResponse> delayedResponseOnIoThread = new ServerTest.EchoService() {
                @Override
                protected HttpResponse echo(AggregatedHttpMessage aReq) {
                    try {
                        Thread.sleep(ServerTest.processDelayMillis);
                        return super.echo(aReq);
                    } catch (InterruptedException e) {
                        return HttpResponse.ofFailure(e);
                    }
                }
            }.decorate(LoggingService.newDecorator());
            final Service<HttpRequest, HttpResponse> lazyResponseNotOnIoThread = new ServerTest.EchoService() {
                @Override
                protected HttpResponse echo(AggregatedHttpMessage aReq) {
                    final CompletableFuture<HttpResponse> responseFuture = new CompletableFuture<>();
                    final HttpResponse res = HttpResponse.from(responseFuture);
                    ServerTest.asyncExecutorGroup.schedule(() -> super.echo(aReq), ServerTest.processDelayMillis, TimeUnit.MILLISECONDS).addListener((Future<HttpResponse> future) -> responseFuture.complete(future.getNow()));
                    return res;
                }
            }.decorate(LoggingService.newDecorator());
            final Service<HttpRequest, HttpResponse> buggy = new AbstractHttpService() {
                @Override
                protected HttpResponse doPost(ServiceRequestContext ctx, HttpRequest req) {
                    throw Exceptions.clearTrace(new AnticipatedException("bug!"));
                }
            }.decorate(LoggingService.newDecorator());
            sb.service("/", immediateResponseOnIoThread).service("/delayed", delayedResponseOnIoThread).service("/timeout", lazyResponseNotOnIoThread).service("/timeout-not", lazyResponseNotOnIoThread).service("/buggy", buggy);
            // Disable request timeout for '/timeout-not' only.
            final Function<Service<HttpRequest, HttpResponse>, Service<HttpRequest, HttpResponse>> decorator = ( s) -> new SimpleDecoratingService<HttpRequest, HttpResponse>(s) {
                @Override
                public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    ctx.setRequestTimeoutMillis(("/timeout-not".equals(ctx.path()) ? 0 : ServerTest.requestTimeoutMillis));
                    return delegate().serve(ctx, req);
                }
            };
            sb.decorator(decorator);
            sb.idleTimeoutMillis(ServerTest.idleTimeoutMillis);
        }
    };

    @Test
    public void testStartStop() throws Exception {
        final Server server = ServerTest.server.server();
        assertThat(server.activePorts()).hasSize(1);
        server.stop().get();
        assertThat(server.activePorts()).isEmpty();
    }

    @Test
    public void testInvocation() throws Exception {
        ServerTest.testInvocation0("/");
    }

    @Test
    public void testDelayedResponseApiInvocationExpectedTimeout() throws Exception {
        ServerTest.testInvocation0("/delayed");
    }

    @Test
    public void testChannelOptions() throws Exception {
        assertThat(ServerTest.server.server().serverBootstrap().config().options().get(SO_BACKLOG)).isEqualTo(1024);
    }

    @Test
    public void testRequestTimeoutInvocation() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpPost req = new HttpPost(ServerTest.server.uri("/timeout"));
            req.setEntity(new StringEntity("Hello, world!", StandardCharsets.UTF_8));
            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(HttpStatusClass.valueOf(res.getStatusLine().getStatusCode())).isNotEqualTo(SUCCESS);
            }
        }
    }

    @Test
    public void testDynamicRequestTimeoutInvocation() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpPost req = new HttpPost(ServerTest.server.uri("/timeout-not"));
            req.setEntity(new StringEntity("Hello, world!", StandardCharsets.UTF_8));
            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(HttpStatusClass.valueOf(res.getStatusLine().getStatusCode())).isEqualTo(SUCCESS);
            }
        }
    }

    @Test(timeout = (ServerTest.idleTimeoutMillis) * 5)
    public void testIdleTimeoutByNoContentSent() throws Exception {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(((int) ((ServerTest.idleTimeoutMillis) * 4)));
            socket.connect(ServerTest.server.httpSocketAddress());
            final long connectedNanos = System.nanoTime();
            // read until EOF
            while ((socket.getInputStream().read()) != (-1)) {
                continue;
            } 
            final long elapsedTimeMillis = TimeUnit.MILLISECONDS.convert(((System.nanoTime()) - connectedNanos), TimeUnit.NANOSECONDS);
            assertThat(elapsedTimeMillis).isGreaterThan(((long) ((ServerTest.idleTimeoutMillis) * 0.9)));
        }
    }

    @Test(timeout = (ServerTest.idleTimeoutMillis) * 5)
    public void testIdleTimeoutByContentSent() throws Exception {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(((int) ((ServerTest.idleTimeoutMillis) * 4)));
            socket.connect(ServerTest.server.httpSocketAddress());
            final PrintWriter outWriter = new PrintWriter(socket.getOutputStream(), false);
            outWriter.print("POST / HTTP/1.1\r\n");
            outWriter.print("Connection: Keep-Alive\r\n");
            outWriter.print("\r\n");
            outWriter.flush();
            final long lastWriteNanos = System.nanoTime();
            // read until EOF
            while ((socket.getInputStream().read()) != (-1)) {
                continue;
            } 
            final long elapsedTimeMillis = TimeUnit.MILLISECONDS.convert(((System.nanoTime()) - lastWriteNanos), TimeUnit.NANOSECONDS);
            assertThat(elapsedTimeMillis).isGreaterThan(((long) ((ServerTest.idleTimeoutMillis) * 0.9)));
        }
    }

    /**
     * Ensure that the connection is not broken even if {@link Service#serve(ServiceRequestContext, Request)}
     * raises an exception.
     */
    @Test(timeout = (ServerTest.idleTimeoutMillis) * 5)
    public void testBuggyService() throws Exception {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(((int) ((ServerTest.idleTimeoutMillis) * 4)));
            socket.connect(ServerTest.server.httpSocketAddress());
            final PrintWriter outWriter = new PrintWriter(socket.getOutputStream(), false);
            // Send a request to a buggy service whose invoke() raises an exception.
            // If the server handled the exception correctly (i.e. responded with 500 Internal Server Error and
            // recovered from the exception successfully), then the connection should not be closed immediately
            // but on the idle timeout of the second request.
            outWriter.print("POST /buggy HTTP/1.1\r\n");
            outWriter.print("Connection: Keep-Alive\r\n");
            outWriter.print("Content-Length: 0\r\n");
            outWriter.print("\r\n");
            outWriter.print("POST / HTTP/1.1\r\n");
            outWriter.print("Connection: Keep-Alive\r\n");
            outWriter.print("\r\n");
            outWriter.flush();
            final long lastWriteNanos = System.nanoTime();
            // read until EOF
            while ((socket.getInputStream().read()) != (-1)) {
                continue;
            } 
            final long elapsedTimeMillis = TimeUnit.MILLISECONDS.convert(((System.nanoTime()) - lastWriteNanos), TimeUnit.NANOSECONDS);
            assertThat(elapsedTimeMillis).isGreaterThan(((long) ((ServerTest.idleTimeoutMillis) * 0.9)));
        }
    }

    @Test
    public void testOptions() throws Exception {
        ServerTest.testSimple("OPTIONS * HTTP/1.1", "HTTP/1.1 200 OK", "allow: OPTIONS,GET,HEAD,POST,PUT,PATCH,DELETE,TRACE");
    }

    @Test
    public void testInvalidPath() throws Exception {
        ServerTest.testSimple("GET * HTTP/1.1", "HTTP/1.1 400 Bad Request");
    }

    @Test
    public void testUnsupportedMethod() throws Exception {
        ServerTest.testSimple("WHOA / HTTP/1.1", "HTTP/1.1 405 Method Not Allowed");
    }

    @Test
    public void duplicatedPort() {
        // Known to fail on WSL (Windows Subsystem for Linux)
        Assume.assumeTrue(((System.getenv("WSLENV")) == null));
        final Server duplicatedPortServer = new ServerBuilder().http(ServerTest.server.httpPort()).service("/", ( ctx, res) -> HttpResponse.of("")).build();
        assertThatThrownBy(() -> duplicatedPortServer.start().join()).hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void defaultStartStopExecutor() {
        final Server server = ServerTest.server.server();
        final Queue<Thread> threads = new LinkedTransferQueue<>();
        server.addListener(new ServerTest.ThreadRecordingServerListener(threads));
        threads.add(server.stop().thenApply(( unused) -> Thread.currentThread()).join());
        threads.add(server.start().thenApply(( unused) -> Thread.currentThread()).join());
        threads.forEach(( t) -> assertThat(t.getName()).startsWith("globalEventExecutor"));
    }

    @Test
    public void customStartStopExecutor() {
        final Queue<Thread> threads = new LinkedTransferQueue<>();
        final String prefix = (getClass().getName()) + "#customStartStopExecutor";
        final ExecutorService executor = Executors.newSingleThreadExecutor(new DefaultThreadFactory(prefix));
        final Server server = new ServerBuilder().startStopExecutor(executor).service("/", ( ctx, req) -> HttpResponse.of(200)).serverListener(new ServerTest.ThreadRecordingServerListener(threads)).build();
        threads.add(server.start().thenApply(( unused) -> Thread.currentThread()).join());
        threads.add(server.stop().thenApply(( unused) -> Thread.currentThread()).join());
        threads.forEach(( t) -> assertThat(t.getName()).startsWith(prefix));
    }

    private static class EchoService extends AbstractHttpService {
        @Override
        protected final HttpResponse doPost(ServiceRequestContext ctx, HttpRequest req) {
            return HttpResponse.from(req.aggregate().thenApply(this::echo).exceptionally(CompletionActions::log));
        }

        protected HttpResponse echo(AggregatedHttpMessage aReq) {
            return HttpResponse.of(HttpHeaders.of(OK), aReq.content());
        }
    }

    private static class ThreadRecordingServerListener implements ServerListener {
        private final Queue<Thread> threads;

        ThreadRecordingServerListener(Queue<Thread> threads) {
            this.threads = Objects.requireNonNull(threads, "threads");
        }

        @Override
        public void serverStarting(Server server) {
            recordThread();
        }

        @Override
        public void serverStarted(Server server) {
            recordThread();
        }

        @Override
        public void serverStopping(Server server) {
            recordThread();
        }

        @Override
        public void serverStopped(Server server) {
            recordThread();
        }

        private void recordThread() {
            threads.add(Thread.currentThread());
        }
    }
}

