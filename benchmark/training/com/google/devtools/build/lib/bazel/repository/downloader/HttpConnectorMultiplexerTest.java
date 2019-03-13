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


import HttpStream.Factory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.devtools.build.lib.bazel.repository.downloader.RetryingInputStream.Reconnector;
import com.google.devtools.build.lib.events.EventHandler;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.util.Sleeper;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit tests for {@link HttpConnectorMultiplexer}.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("unchecked")
public class HttpConnectorMultiplexerTest {
    private static final URL URL1 = DownloaderTestUtils.makeUrl("http://first.example");

    private static final URL URL2 = DownloaderTestUtils.makeUrl("http://second.example");

    private static final URL URL3 = DownloaderTestUtils.makeUrl("http://third.example");

    private static final byte[] data1 = "first".getBytes(StandardCharsets.UTF_8);

    private static final byte[] data2 = "second".getBytes(StandardCharsets.UTF_8);

    private static final byte[] data3 = "third".getBytes(StandardCharsets.UTF_8);

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final Timeout globalTimeout = new Timeout(10000);

    private final HttpStream stream1 = HttpConnectorMultiplexerTest.fakeStream(HttpConnectorMultiplexerTest.URL1, HttpConnectorMultiplexerTest.data1);

    private final HttpStream stream2 = HttpConnectorMultiplexerTest.fakeStream(HttpConnectorMultiplexerTest.URL2, HttpConnectorMultiplexerTest.data2);

    private final HttpStream stream3 = HttpConnectorMultiplexerTest.fakeStream(HttpConnectorMultiplexerTest.URL3, HttpConnectorMultiplexerTest.data3);

    private final ManualClock clock = new ManualClock();

    private final Sleeper sleeper = Mockito.mock(Sleeper.class);

    private final HttpConnector connector = Mockito.mock(HttpConnector.class);

    private final URLConnection connection1 = Mockito.mock(URLConnection.class);

    private final URLConnection connection2 = Mockito.mock(URLConnection.class);

    private final URLConnection connection3 = Mockito.mock(URLConnection.class);

    private final EventHandler eventHandler = Mockito.mock(EventHandler.class);

    private final Factory streamFactory = Mockito.mock(Factory.class);

    private final HttpConnectorMultiplexer multiplexer = new HttpConnectorMultiplexer(eventHandler, connector, streamFactory, clock, sleeper);

    @Test
    public void emptyList_throwsIae() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        multiplexer.connect(ImmutableList.<URL>of(), "");
    }

    @Test
    public void ftpUrl_throwsIae() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        multiplexer.connect(Arrays.asList(new URL("ftp://lol.example")), "");
    }

    @Test
    public void threadIsInterrupted_throwsIeProntoAndDoesNothingElse() throws Exception {
        final AtomicBoolean wasInterrupted = new AtomicBoolean(true);
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().interrupt();
                try {
                    multiplexer.connect(Arrays.asList(new URL("http://lol.example")), "");
                } catch (InterruptedIOException ignored) {
                    return;
                } catch (Exception ignored) {
                    // ignored
                }
                wasInterrupted.set(false);
            }
        });
        task.start();
        task.join();
        assertThat(wasInterrupted.get()).isTrue();
        Mockito.verifyZeroInteractions(connector);
    }

    @Test
    public void singleUrl_justCallsConnector() throws Exception {
        assertThat(ByteStreams.toByteArray(multiplexer.connect(Arrays.asList(HttpConnectorMultiplexerTest.URL1), "abc"))).isEqualTo(HttpConnectorMultiplexerTest.data1);
        Mockito.verify(connector).connect(ArgumentMatchers.eq(HttpConnectorMultiplexerTest.URL1), ArgumentMatchers.any(ImmutableMap.class));
        Mockito.verify(streamFactory).create(ArgumentMatchers.any(URLConnection.class), ArgumentMatchers.any(URL.class), ArgumentMatchers.eq("abc"), ArgumentMatchers.any(Reconnector.class));
        Mockito.verifyNoMoreInteractions(sleeper, connector, streamFactory);
    }

    @Test
    public void multipleUrlsFail_throwsIOException() throws Exception {
        Mockito.when(connector.connect(ArgumentMatchers.any(URL.class), ArgumentMatchers.any(ImmutableMap.class))).thenThrow(new IOException());
        try {
            multiplexer.connect(Arrays.asList(HttpConnectorMultiplexerTest.URL1, HttpConnectorMultiplexerTest.URL2, HttpConnectorMultiplexerTest.URL3), "");
            Assert.fail("Expected IOException");
        } catch (IOException e) {
            assertThat(e).hasMessageThat().contains("All mirrors are down");
        }
        Mockito.verify(connector, Mockito.times(3)).connect(ArgumentMatchers.any(URL.class), ArgumentMatchers.any(ImmutableMap.class));
        Mockito.verify(sleeper, Mockito.times(2)).sleepMillis(ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(sleeper, connector, streamFactory);
    }

    @Test
    public void firstUrlFails_returnsSecond() throws Exception {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                clock.advanceMillis(1000);
                return null;
            }
        }).when(sleeper).sleepMillis(ArgumentMatchers.anyLong());
        Mockito.when(connector.connect(ArgumentMatchers.eq(HttpConnectorMultiplexerTest.URL1), ArgumentMatchers.any(ImmutableMap.class))).thenThrow(new IOException());
        assertThat(ByteStreams.toByteArray(multiplexer.connect(Arrays.asList(HttpConnectorMultiplexerTest.URL1, HttpConnectorMultiplexerTest.URL2), "abc"))).isEqualTo(HttpConnectorMultiplexerTest.data2);
        assertThat(clock.currentTimeMillis()).isEqualTo(1000L);
        Mockito.verify(connector).connect(ArgumentMatchers.eq(HttpConnectorMultiplexerTest.URL1), ArgumentMatchers.any(ImmutableMap.class));
        Mockito.verify(connector).connect(ArgumentMatchers.eq(HttpConnectorMultiplexerTest.URL2), ArgumentMatchers.any(ImmutableMap.class));
        Mockito.verify(streamFactory).create(ArgumentMatchers.any(URLConnection.class), ArgumentMatchers.any(URL.class), ArgumentMatchers.eq("abc"), ArgumentMatchers.any(Reconnector.class));
        Mockito.verify(sleeper).sleepMillis(ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(sleeper, connector, streamFactory);
    }

    @Test
    public void twoSuccessfulUrlsAndFirstWins_returnsFirstAndInterruptsSecond() throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final AtomicBoolean wasInterrupted = new AtomicBoolean(true);
        Mockito.when(connector.connect(ArgumentMatchers.eq(HttpConnectorMultiplexerTest.URL1), ArgumentMatchers.any(ImmutableMap.class))).thenAnswer(new Answer<URLConnection>() {
            @Override
            public URLConnection answer(InvocationOnMock invocation) throws Throwable {
                barrier.await();
                return connection1;
            }
        });
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                barrier.await();
                TimeUnit.MILLISECONDS.sleep(10000);
                wasInterrupted.set(false);
                return null;
            }
        }).when(sleeper).sleepMillis(ArgumentMatchers.anyLong());
        assertThat(ByteStreams.toByteArray(multiplexer.connect(Arrays.asList(HttpConnectorMultiplexerTest.URL1, HttpConnectorMultiplexerTest.URL2), "abc"))).isEqualTo(HttpConnectorMultiplexerTest.data1);
        assertThat(wasInterrupted.get()).isTrue();
    }

    @Test
    public void parentThreadGetsInterrupted_interruptsChildrenThenThrowsIe() throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(3);
        final AtomicBoolean wasInterrupted1 = new AtomicBoolean(true);
        final AtomicBoolean wasInterrupted2 = new AtomicBoolean(true);
        final AtomicBoolean wasInterrupted3 = new AtomicBoolean(true);
        Mockito.when(connector.connect(ArgumentMatchers.eq(HttpConnectorMultiplexerTest.URL1), ArgumentMatchers.any(ImmutableMap.class))).thenAnswer(new Answer<URLConnection>() {
            @Override
            public URLConnection answer(InvocationOnMock invocation) throws Throwable {
                barrier.await();
                TimeUnit.MILLISECONDS.sleep(10000);
                wasInterrupted1.set(false);
                throw new RuntimeException();
            }
        });
        Mockito.when(connector.connect(ArgumentMatchers.eq(HttpConnectorMultiplexerTest.URL2), ArgumentMatchers.any(ImmutableMap.class))).thenAnswer(new Answer<URLConnection>() {
            @Override
            public URLConnection answer(InvocationOnMock invocation) throws Throwable {
                barrier.await();
                TimeUnit.MILLISECONDS.sleep(10000);
                wasInterrupted2.set(false);
                throw new RuntimeException();
            }
        });
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiplexer.connect(Arrays.asList(HttpConnectorMultiplexerTest.URL1, HttpConnectorMultiplexerTest.URL2), "");
                } catch (InterruptedIOException ignored) {
                    return;
                } catch (Exception ignored) {
                    // ignored
                }
                wasInterrupted3.set(false);
            }
        });
        task.start();
        barrier.await();
        task.interrupt();
        task.join();
        assertThat(wasInterrupted1.get()).isTrue();
        assertThat(wasInterrupted2.get()).isTrue();
        assertThat(wasInterrupted3.get()).isTrue();
    }
}

