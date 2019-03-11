/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.bazel.repository.downloader;


import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.devtools.build.lib.events.EventHandler;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.testutil.ManualSleeper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit tests for {@link HttpConnector}.
 */
@RunWith(JUnit4.class)
public class HttpConnectorTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public final Timeout globalTimeout = new Timeout(10000);

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private final ManualClock clock = new ManualClock();

    private final ManualSleeper sleeper = new ManualSleeper(clock);

    private final EventHandler eventHandler = Mockito.mock(EventHandler.class);

    private final ProxyHelper proxyHelper = Mockito.mock(ProxyHelper.class);

    private final HttpConnector connector = new HttpConnector(Locale.US, eventHandler, proxyHelper, sleeper);

    @Test
    public void localFileDownload() throws Exception {
        byte[] fileContents = "this is a test".getBytes(StandardCharsets.UTF_8);
        assertThat(ByteStreams.toByteArray(connector.connect(createTempFile(fileContents).toURI().toURL(), ImmutableMap.<String, String>of()).getInputStream())).isEqualTo(fileContents);
    }

    @Test
    public void badHost_throwsIOException() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("Unknown host: bad.example");
        connector.connect(new URL("http://bad.example"), ImmutableMap.<String, String>of());
    }

    @Test
    public void normalRequest() throws Exception {
        final Map<String, String> headers = new ConcurrentHashMap<>();
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName(null))) {
            @SuppressWarnings("unused")
            Future<?> possiblyIgnoredError = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try (Socket socket = server.accept()) {
                        HttpParser.readHttpRequest(socket.getInputStream(), headers);
                        DownloaderTestUtils.sendLines(socket, "HTTP/1.1 200 OK", "Date: Fri, 31 Dec 1999 23:59:59 GMT", "Connection: close", "Content-Type: text/plain", "Content-Length: 5", "", "hello");
                    }
                    return null;
                }
            });
            try (Reader payload = new InputStreamReader(connector.connect(new URL(String.format("http://localhost:%d/boo", server.getLocalPort())), ImmutableMap.of("Content-Encoding", "gzip")).getInputStream(), StandardCharsets.ISO_8859_1)) {
                assertThat(CharStreams.toString(payload)).isEqualTo("hello");
            }
        }
        assertThat(headers).containsEntry("x-method", "GET");
        assertThat(headers).containsEntry("x-request-uri", "/boo");
        assertThat(headers).containsEntry("content-encoding", "gzip");
    }

    @Test
    public void serverError_retriesConnect() throws Exception {
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName(null))) {
            @SuppressWarnings("unused")
            Future<?> possiblyIgnoredError = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try (Socket socket = server.accept()) {
                        HttpParser.readHttpRequest(socket.getInputStream());
                        DownloaderTestUtils.sendLines(socket, "HTTP/1.1 500 Incredible Catastrophe", "Date: Fri, 31 Dec 1999 23:59:59 GMT", "Connection: close", "Content-Type: text/plain", "Content-Length: 8", "", "nononono");
                    }
                    try (Socket socket = server.accept()) {
                        HttpParser.readHttpRequest(socket.getInputStream());
                        DownloaderTestUtils.sendLines(socket, "HTTP/1.1 200 OK", "Date: Fri, 31 Dec 1999 23:59:59 GMT", "Connection: close", "Content-Type: text/plain", "Content-Length: 5", "", "hello");
                    }
                    return null;
                }
            });
            try (Reader payload = new InputStreamReader(connector.connect(new URL(String.format("http://localhost:%d", server.getLocalPort())), ImmutableMap.<String, String>of()).getInputStream(), StandardCharsets.ISO_8859_1)) {
                assertThat(CharStreams.toString(payload)).isEqualTo("hello");
                assertThat(clock.currentTimeMillis()).isEqualTo(100L);
            }
        }
    }

    @Test
    public void permanentError_doesNotRetryAndThrowsIOException() throws Exception {
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName(null))) {
            @SuppressWarnings("unused")
            Future<?> possiblyIgnoredError = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try (Socket socket = server.accept()) {
                        HttpParser.readHttpRequest(socket.getInputStream());
                        DownloaderTestUtils.sendLines(socket, "HTTP/1.1 404 Not Here", "Date: Fri, 31 Dec 1999 23:59:59 GMT", "Connection: close", "Content-Type: text/plain", "Content-Length: 0", "", "");
                    }
                    return null;
                }
            });
            thrown.expect(IOException.class);
            thrown.expectMessage("404 Not Here");
            connector.connect(new URL(String.format("http://localhost:%d", server.getLocalPort())), ImmutableMap.<String, String>of());
        }
    }

    @Test
    public void permanentError_consumesPayloadBeforeReturningn() throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final AtomicBoolean consumed = new AtomicBoolean();
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName(null))) {
            @SuppressWarnings("unused")
            Future<?> possiblyIgnoredError = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try (Socket socket = server.accept()) {
                        HttpParser.readHttpRequest(socket.getInputStream());
                        DownloaderTestUtils.sendLines(socket, "HTTP/1.1 501 Oh No", "Date: Fri, 31 Dec 1999 23:59:59 GMT", "Connection: close", "Content-Type: text/plain", "Content-Length: 1", "", "b");
                        consumed.set(true);
                    } finally {
                        barrier.await();
                    }
                    return null;
                }
            });
            connector.connect(new URL(String.format("http://localhost:%d", server.getLocalPort())), ImmutableMap.<String, String>of());
            Assert.fail();
        } catch (IOException ignored) {
            // ignored
        } finally {
            barrier.await();
        }
        assertThat(consumed.get()).isTrue();
        assertThat(clock.currentTimeMillis()).isEqualTo(0L);
    }

    @Test
    public void always500_givesUpEventually() throws Exception {
        final AtomicInteger tries = new AtomicInteger();
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName(null))) {
            @SuppressWarnings("unused")
            Future<?> possiblyIgnoredError = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    while (true) {
                        try (Socket socket = server.accept()) {
                            HttpParser.readHttpRequest(socket.getInputStream());
                            DownloaderTestUtils.sendLines(socket, "HTTP/1.1 500 Oh My", "Date: Fri, 31 Dec 1999 23:59:59 GMT", "Connection: close", "Content-Type: text/plain", "Content-Length: 0", "", "");
                            tries.incrementAndGet();
                        }
                    } 
                }
            });
            thrown.expect(IOException.class);
            thrown.expectMessage("500 Oh My");
            try {
                connector.connect(new URL(String.format("http://localhost:%d", server.getLocalPort())), ImmutableMap.<String, String>of());
            } finally {
                assertThat(tries.get()).isGreaterThan(2);
            }
        }
    }

    @Test
    public void serverSays403_clientRetriesAnyway() throws Exception {
        final AtomicInteger tries = new AtomicInteger();
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName(null))) {
            @SuppressWarnings("unused")
            Future<?> possiblyIgnoredError = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    while (true) {
                        try (Socket socket = server.accept()) {
                            HttpParser.readHttpRequest(socket.getInputStream());
                            DownloaderTestUtils.sendLines(socket, "HTTP/1.1 403 Forbidden", "Date: Fri, 31 Dec 1999 23:59:59 GMT", "Connection: close", "Content-Type: text/plain", "Content-Length: 0", "", "");
                            tries.incrementAndGet();
                        }
                    } 
                }
            });
            thrown.expect(IOException.class);
            thrown.expectMessage("403 Forbidden");
            try {
                connector.connect(new URL(String.format("http://localhost:%d", server.getLocalPort())), ImmutableMap.<String, String>of());
            } finally {
                assertThat(tries.get()).isGreaterThan(2);
            }
        }
    }

    @Test
    public void pathRedirect_301() throws Exception {
        redirectToDifferentPath_works("301");
    }

    @Test
    public void serverRedirect_301() throws Exception {
        redirectToDifferentServer_works("301");
    }

    /* Also tests behavior for 302 and 307 codes. */
    @Test
    public void pathRedirect_303() throws Exception {
        redirectToDifferentPath_works("303");
    }

    @Test
    public void serverRedirects_303() throws Exception {
        redirectToDifferentServer_works("303");
    }
}

