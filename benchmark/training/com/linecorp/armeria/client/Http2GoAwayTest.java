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
package com.linecorp.armeria.client;


import Http2FrameTypes.GO_AWAY;
import Http2FrameTypes.HEADERS;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.testing.common.EventLoopRule;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;


public class Http2GoAwayTest {
    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    @Rule
    public TestRule globalTimeout = new DisableOnDebug(new Timeout(10, TimeUnit.SECONDS));

    /**
     * Server sends a GOAWAY frame after finishing all streams.
     */
    @Test
    public void streamEndsBeforeGoAway() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);ClientFactory clientFactory = Http2GoAwayTest.newClientFactory()) {
            final int port = ss.getLocalPort();
            final HttpClient client = HttpClient.of(clientFactory, ("h2c://127.0.0.1:" + port));
            final CompletableFuture<AggregatedHttpMessage> future = client.get("/").aggregate();
            try (Socket s = ss.accept()) {
                final InputStream in = s.getInputStream();
                final BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
                Http2GoAwayTest.handleInitialExchange(in, bos);
                // Read a HEADERS frame.
                assertThat(Http2GoAwayTest.readFrame(in).getByte(3)).isEqualTo(HEADERS);
                // Send a HEADERS frame to finish the response followed by a GOAWAY frame.
                bos.write(new byte[]{ 0, 0, 6, 1, 37, 0, 0, 0, 3, 0, 0, 0, 0, 15, ((byte) (136)) });
                bos.write(new byte[]{ 0, 0, 8, 7, 0, 0, 0, 0, 0, 0, 0, 0, 3// lastStreamId = 3
                , 0, 0, 0, 0// errorCode = 0
                 });
                bos.flush();
                // Read a GOAWAY frame.
                assertThat(Http2GoAwayTest.readFrame(in).getByte(3)).isEqualTo(GO_AWAY);
                // Request should not fail.
                future.join();
                // The connection should be closed by the client because there is no pending request.
                assertThat(in.read()).isEqualTo((-1));
            }
        }
    }

    /**
     * Server sends GOAWAY before finishing all streams.
     */
    @Test
    public void streamEndsAfterGoAway() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);ClientFactory clientFactory = Http2GoAwayTest.newClientFactory()) {
            final int port = ss.getLocalPort();
            final HttpClient client = HttpClient.of(clientFactory, ("h2c://127.0.0.1:" + port));
            final CompletableFuture<AggregatedHttpMessage> future = client.get("/").aggregate();
            try (Socket s = ss.accept()) {
                final InputStream in = s.getInputStream();
                final BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
                Http2GoAwayTest.handleInitialExchange(in, bos);
                // Read a HEADERS frame.
                assertThat(Http2GoAwayTest.readFrame(in).getByte(3)).isEqualTo(HEADERS);
                // Send a GOAWAY frame first followed by a HEADERS frame.
                bos.write(new byte[]{ 0, 0, 8, 7, 0, 0, 0, 0, 0, 0, 0, 0, 3// lastStreamId = 3
                , 0, 0, 0, 0// errorCode = 0
                 });
                bos.write(new byte[]{ 0, 0, 6, 1, 37, 0, 0, 0, 3, 0, 0, 0, 0, 15, ((byte) (136)) });
                bos.flush();
                // Read a GOAWAY frame.
                assertThat(Http2GoAwayTest.readFrame(in).getByte(3)).isEqualTo(GO_AWAY);
                // Request should not fail.
                future.join();
                // The connection should be closed by the client because there is no pending request.
                assertThat(in.read()).isEqualTo((-1));
            }
        }
    }

    /**
     * Client sends two requests whose streamIds are 3 and 5 respectively. Server sends a GOAWAY frame
     * whose lastStreamId is 3. The request with streamId 5 should fail.
     */
    @Test
    public void streamGreaterThanLastStreamId() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);ClientFactory clientFactory = Http2GoAwayTest.newClientFactory()) {
            final int port = ss.getLocalPort();
            final HttpClient client = HttpClient.of(clientFactory, ("h2c://127.0.0.1:" + port));
            final CompletableFuture<AggregatedHttpMessage> future1 = client.get("/").aggregate();
            try (Socket s = ss.accept()) {
                final InputStream in = s.getInputStream();
                final BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
                Http2GoAwayTest.handleInitialExchange(in, bos);
                // Read a HEADERS frame.
                assertThat(Http2GoAwayTest.readFrame(in).getByte(3)).isEqualTo(HEADERS);
                // Send the second request.
                final CompletableFuture<AggregatedHttpMessage> future2 = client.get("/").aggregate();
                // Read a HEADERS frame for the second request.
                assertThat(Http2GoAwayTest.readFrame(in).getByte(3)).isEqualTo(HEADERS);
                // Send a GOAWAY frame.
                bos.write(new byte[]{ 0, 0, 8, 7, 0, 0, 0, 0, 0, 0, 0, 0, 3// lastStreamId = 3
                , 0, 0, 0, 0// errorCode = 0
                 });
                bos.flush();
                // The second request should fail with UnprocessedRequestException.
                assertThatThrownBy(future2::join).isInstanceOf(CompletionException.class).hasCauseInstanceOf(UnprocessedRequestException.class);
                // The first request should not fail.
                assertThat(future1).isNotDone();
                // Read a GOAWAY frame.
                assertThat(Http2GoAwayTest.readFrame(in).getByte(3)).isEqualTo(GO_AWAY);
                // Send a HEADERS frame for the first request.
                bos.write(new byte[]{ 0, 0, 6, 1, 37, 0, 0, 0, 3, 0, 0, 0, 0, 15, ((byte) (136)) });
                bos.flush();
                // Request should not fail.
                future1.join();
                // The connection should be closed by the client because there is no pending request.
                assertThat(in.read()).isEqualTo((-1));
            }
        }
    }
}

