/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.tunnel.server;


import HttpStatus.I_AM_A_TEAPOT;
import HttpStatus.NO_CONTENT;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channels;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.devtools.tunnel.payload.HttpTunnelPayload;
import org.springframework.boot.devtools.tunnel.server.HttpTunnelServer.HttpConnection;
import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests for {@link HttpTunnelServer}.
 *
 * @author Phillip Webb
 */
public class HttpTunnelServerTests {
    private static final int DEFAULT_LONG_POLL_TIMEOUT = 10000;

    private static final byte[] NO_DATA = new byte[]{  };

    private static final String SEQ_HEADER = "x-seq";

    private HttpTunnelServer server;

    @Mock
    private TargetServerConnection serverConnection;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private ServerHttpRequest request;

    private ServerHttpResponse response;

    private HttpTunnelServerTests.MockServerChannel serverChannel;

    @Test
    public void serverConnectionIsRequired() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpTunnelServer(null)).withMessageContaining("ServerConnection must not be null");
    }

    @Test
    public void serverConnectedOnFirstRequest() throws Exception {
        Mockito.verify(this.serverConnection, Mockito.never()).open(ArgumentMatchers.anyInt());
        this.server.handle(this.request, this.response);
        Mockito.verify(this.serverConnection, Mockito.times(1)).open(HttpTunnelServerTests.DEFAULT_LONG_POLL_TIMEOUT);
    }

    @Test
    public void longPollTimeout() throws Exception {
        this.server.setLongPollTimeout(800);
        this.server.handle(this.request, this.response);
        Mockito.verify(this.serverConnection, Mockito.times(1)).open(800);
    }

    @Test
    public void longPollTimeoutMustBePositiveValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.server.setLongPollTimeout(0)).withMessageContaining("LongPollTimeout must be a positive value");
    }

    @Test
    public void initialRequestIsSentToServer() throws Exception {
        this.servletRequest.addHeader(HttpTunnelServerTests.SEQ_HEADER, "1");
        this.servletRequest.setContent("hello".getBytes());
        this.server.handle(this.request, this.response);
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
        this.serverChannel.verifyReceived("hello");
    }

    @Test
    public void initialRequestIsUsedForFirstServerResponse() throws Exception {
        this.servletRequest.addHeader(HttpTunnelServerTests.SEQ_HEADER, "1");
        this.servletRequest.setContent("hello".getBytes());
        this.server.handle(this.request, this.response);
        System.out.println("sending");
        this.serverChannel.send("hello");
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
        assertThat(this.servletResponse.getContentAsString()).isEqualTo("hello");
        this.serverChannel.verifyReceived("hello");
    }

    @Test
    public void initialRequestHasNoPayload() throws Exception {
        this.server.handle(this.request, this.response);
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
        this.serverChannel.verifyReceived(HttpTunnelServerTests.NO_DATA);
    }

    @Test
    public void typicalRequestResponseTraffic() throws Exception {
        HttpTunnelServerTests.MockHttpConnection h1 = new HttpTunnelServerTests.MockHttpConnection();
        this.server.handle(h1);
        HttpTunnelServerTests.MockHttpConnection h2 = new HttpTunnelServerTests.MockHttpConnection("hello server", 1);
        this.server.handle(h2);
        this.serverChannel.verifyReceived("hello server");
        this.serverChannel.send("hello client");
        h1.verifyReceived("hello client", 1);
        HttpTunnelServerTests.MockHttpConnection h3 = new HttpTunnelServerTests.MockHttpConnection("1+1", 2);
        this.server.handle(h3);
        this.serverChannel.send("=2");
        h2.verifyReceived("=2", 2);
        HttpTunnelServerTests.MockHttpConnection h4 = new HttpTunnelServerTests.MockHttpConnection("1+2", 3);
        this.server.handle(h4);
        this.serverChannel.send("=3");
        h3.verifyReceived("=3", 3);
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
    }

    @Test
    public void clientIsAwareOfServerClose() throws Exception {
        HttpTunnelServerTests.MockHttpConnection h1 = new HttpTunnelServerTests.MockHttpConnection("1", 1);
        this.server.handle(h1);
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
        assertThat(h1.getServletResponse().getStatus()).isEqualTo(410);
    }

    @Test
    public void clientCanCloseServer() throws Exception {
        HttpTunnelServerTests.MockHttpConnection h1 = new HttpTunnelServerTests.MockHttpConnection();
        this.server.handle(h1);
        HttpTunnelServerTests.MockHttpConnection h2 = new HttpTunnelServerTests.MockHttpConnection("DISCONNECT", 1);
        h2.getServletRequest().addHeader("Content-Type", "application/x-disconnect");
        this.server.handle(h2);
        this.server.getServerThread().join();
        assertThat(h1.getServletResponse().getStatus()).isEqualTo(410);
        assertThat(this.serverChannel.isOpen()).isFalse();
    }

    @Test
    public void neverMoreThanTwoHttpConnections() throws Exception {
        HttpTunnelServerTests.MockHttpConnection h1 = new HttpTunnelServerTests.MockHttpConnection();
        this.server.handle(h1);
        HttpTunnelServerTests.MockHttpConnection h2 = new HttpTunnelServerTests.MockHttpConnection("1", 2);
        this.server.handle(h2);
        HttpTunnelServerTests.MockHttpConnection h3 = new HttpTunnelServerTests.MockHttpConnection("2", 3);
        this.server.handle(h3);
        waitForResponse();
        assertThat(h1.getServletResponse().getStatus()).isEqualTo(429);
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
    }

    @Test
    public void requestReceivedOutOfOrder() throws Exception {
        HttpTunnelServerTests.MockHttpConnection h1 = new HttpTunnelServerTests.MockHttpConnection();
        HttpTunnelServerTests.MockHttpConnection h2 = new HttpTunnelServerTests.MockHttpConnection("1+2", 1);
        HttpTunnelServerTests.MockHttpConnection h3 = new HttpTunnelServerTests.MockHttpConnection("+3", 2);
        this.server.handle(h1);
        this.server.handle(h3);
        this.server.handle(h2);
        this.serverChannel.verifyReceived("1+2+3");
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
    }

    @Test
    public void httpConnectionsAreClosedAfterLongPollTimeout() throws Exception {
        this.server.setDisconnectTimeout(1000);
        this.server.setLongPollTimeout(100);
        HttpTunnelServerTests.MockHttpConnection h1 = new HttpTunnelServerTests.MockHttpConnection();
        this.server.handle(h1);
        HttpTunnelServerTests.MockHttpConnection h2 = new HttpTunnelServerTests.MockHttpConnection();
        this.server.handle(h2);
        Thread.sleep(400);
        this.serverChannel.disconnect();
        this.server.getServerThread().join();
        assertThat(h1.getServletResponse().getStatus()).isEqualTo(204);
        assertThat(h2.getServletResponse().getStatus()).isEqualTo(204);
    }

    @Test
    public void disconnectTimeout() throws Exception {
        this.server.setDisconnectTimeout(100);
        this.server.setLongPollTimeout(100);
        HttpTunnelServerTests.MockHttpConnection h1 = new HttpTunnelServerTests.MockHttpConnection();
        this.server.handle(h1);
        this.serverChannel.send("hello");
        this.server.getServerThread().join();
        assertThat(this.serverChannel.isOpen()).isFalse();
    }

    @Test
    public void disconnectTimeoutMustBePositive() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.server.setDisconnectTimeout(0)).withMessageContaining("DisconnectTimeout must be a positive value");
    }

    @Test
    public void httpConnectionRespondWithPayload() throws Exception {
        HttpConnection connection = new HttpConnection(this.request, this.response);
        connection.waitForResponse();
        connection.respond(new HttpTunnelPayload(1, ByteBuffer.wrap("hello".getBytes())));
        assertThat(this.servletResponse.getStatus()).isEqualTo(200);
        assertThat(this.servletResponse.getContentAsString()).isEqualTo("hello");
        assertThat(this.servletResponse.getHeader(HttpTunnelServerTests.SEQ_HEADER)).isEqualTo("1");
    }

    @Test
    public void httpConnectionRespondWithStatus() throws Exception {
        HttpConnection connection = new HttpConnection(this.request, this.response);
        connection.waitForResponse();
        connection.respond(I_AM_A_TEAPOT);
        assertThat(this.servletResponse.getStatus()).isEqualTo(418);
        assertThat(this.servletResponse.getContentLength()).isEqualTo(0);
    }

    @Test
    public void httpConnectionAsync() throws Exception {
        ServerHttpAsyncRequestControl async = Mockito.mock(ServerHttpAsyncRequestControl.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        BDDMockito.given(request.getAsyncRequestControl(this.response)).willReturn(async);
        HttpConnection connection = new HttpConnection(request, this.response);
        connection.waitForResponse();
        Mockito.verify(async).start();
        connection.respond(NO_CONTENT);
        Mockito.verify(async).complete();
    }

    @Test
    public void httpConnectionNonAsync() throws Exception {
        testHttpConnectionNonAsync(0);
        testHttpConnectionNonAsync(100);
    }

    @Test
    public void httpConnectionRunning() throws Exception {
        HttpConnection connection = new HttpConnection(this.request, this.response);
        assertThat(connection.isOlderThan(100)).isFalse();
        Thread.sleep(200);
        assertThat(connection.isOlderThan(100)).isTrue();
    }

    /**
     * Mock {@link ByteChannel} used to simulate the server connection.
     */
    private static class MockServerChannel implements ByteChannel {
        private static final ByteBuffer DISCONNECT = ByteBuffer.wrap(HttpTunnelServerTests.NO_DATA);

        private int timeout;

        private BlockingDeque<ByteBuffer> outgoing = new LinkedBlockingDeque<>();

        private ByteArrayOutputStream written = new ByteArrayOutputStream();

        private AtomicBoolean open = new AtomicBoolean(true);

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void send(String content) {
            send(content.getBytes());
        }

        public void send(byte[] bytes) {
            this.outgoing.addLast(ByteBuffer.wrap(bytes));
        }

        public void disconnect() {
            this.outgoing.addLast(HttpTunnelServerTests.MockServerChannel.DISCONNECT);
        }

        public void verifyReceived(String expected) {
            verifyReceived(expected.getBytes());
        }

        public void verifyReceived(byte[] expected) {
            synchronized(this.written) {
                assertThat(this.written.toByteArray()).isEqualTo(expected);
                this.written.reset();
            }
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            try {
                ByteBuffer bytes = this.outgoing.pollFirst(this.timeout, TimeUnit.MILLISECONDS);
                if (bytes == null) {
                    throw new SocketTimeoutException();
                }
                if (bytes == (HttpTunnelServerTests.MockServerChannel.DISCONNECT)) {
                    this.open.set(false);
                    return -1;
                }
                int initialRemaining = dst.remaining();
                bytes.limit(Math.min(bytes.limit(), initialRemaining));
                dst.put(bytes);
                bytes.limit(bytes.capacity());
                return initialRemaining - (dst.remaining());
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            int remaining = src.remaining();
            synchronized(this.written) {
                Channels.newChannel(this.written).write(src);
            }
            return remaining;
        }

        @Override
        public boolean isOpen() {
            return this.open.get();
        }

        @Override
        public void close() {
            this.open.set(false);
        }
    }

    /**
     * Mock {@link HttpConnection}.
     */
    private static class MockHttpConnection extends HttpConnection {
        MockHttpConnection() {
            super(new org.springframework.http.server.ServletServerHttpRequest(new MockHttpServletRequest()), new org.springframework.http.server.ServletServerHttpResponse(new MockHttpServletResponse()));
        }

        MockHttpConnection(String content, int seq) {
            this();
            MockHttpServletRequest request = getServletRequest();
            request.setContent(content.getBytes());
            request.addHeader(HttpTunnelServerTests.SEQ_HEADER, String.valueOf(seq));
        }

        @Override
        protected ServerHttpAsyncRequestControl startAsync() {
            getServletRequest().setAsyncSupported(true);
            return super.startAsync();
        }

        @Override
        protected void complete() {
            super.complete();
            getServletResponse().setCommitted(true);
        }

        public MockHttpServletRequest getServletRequest() {
            return ((MockHttpServletRequest) (((org.springframework.http.server.ServletServerHttpRequest) (getRequest())).getServletRequest()));
        }

        public MockHttpServletResponse getServletResponse() {
            return ((MockHttpServletResponse) (((org.springframework.http.server.ServletServerHttpResponse) (getResponse())).getServletResponse()));
        }

        public void verifyReceived(String expectedContent, int expectedSeq) throws Exception {
            waitForServletResponse();
            MockHttpServletResponse resp = getServletResponse();
            assertThat(resp.getContentAsString()).isEqualTo(expectedContent);
            assertThat(resp.getHeader(HttpTunnelServerTests.SEQ_HEADER)).isEqualTo(String.valueOf(expectedSeq));
        }

        public void waitForServletResponse() throws InterruptedException {
            while (!(getServletResponse().isCommitted())) {
                Thread.sleep(10);
            } 
        }
    }
}

