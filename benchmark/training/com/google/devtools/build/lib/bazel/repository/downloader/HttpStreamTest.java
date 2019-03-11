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


import ProgressInputStream.Factory;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.devtools.build.lib.bazel.repository.downloader.RetryingInputStream.Reconnector;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static HttpStream.PRECHECK_BYTES;


/**
 * Integration tests for {@link HttpStream.Factory} and friends.
 */
@RunWith(JUnit4.class)
public class HttpStreamTest {
    private static final Random randoCalrissian = new Random();

    private static final byte[] data = "hello".getBytes(StandardCharsets.UTF_8);

    private static final String GOOD_CHECKSUM = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";

    private static final String BAD_CHECKSUM = "0000000000000000000000000000000000000000000000000000000000000000";

    private static final URL AURL = DownloaderTestUtils.makeUrl("http://doodle.example");

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final Timeout globalTimeout = new Timeout(10000);

    private final HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);

    private final Reconnector reconnector = Mockito.mock(Reconnector.class);

    private final Factory progress = Mockito.mock(Factory.class);

    private final HttpStream.Factory streamFactory = new HttpStream.Factory(progress);

    @Test
    public void noChecksum_readsOk() throws Exception {
        try (HttpStream stream = streamFactory.create(connection, HttpStreamTest.AURL, "", reconnector)) {
            assertThat(ByteStreams.toByteArray(stream)).isEqualTo(HttpStreamTest.data);
        }
    }

    @Test
    public void smallDataWithValidChecksum_readsOk() throws Exception {
        try (HttpStream stream = streamFactory.create(connection, HttpStreamTest.AURL, HttpStreamTest.GOOD_CHECKSUM, reconnector)) {
            assertThat(ByteStreams.toByteArray(stream)).isEqualTo(HttpStreamTest.data);
        }
    }

    @Test
    public void smallDataWithInvalidChecksum_throwsIOExceptionInCreatePhase() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("Checksum");
        streamFactory.create(connection, HttpStreamTest.AURL, HttpStreamTest.BAD_CHECKSUM, reconnector);
    }

    @Test
    public void bigDataWithValidChecksum_readsOk() throws Exception {
        // at google, we know big data
        byte[] bigData = new byte[(PRECHECK_BYTES) + 70001];
        HttpStreamTest.randoCalrissian.nextBytes(bigData);
        Mockito.when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(bigData));
        try (HttpStream stream = streamFactory.create(connection, HttpStreamTest.AURL, Hashing.sha256().hashBytes(bigData).toString(), reconnector)) {
            assertThat(ByteStreams.toByteArray(stream)).isEqualTo(bigData);
        }
    }

    @Test
    public void bigDataWithInvalidChecksum_throwsIOExceptionAfterCreateOnEof() throws Exception {
        // the probability of this test flaking is 8.6361686e-78
        byte[] bigData = new byte[(PRECHECK_BYTES) + 70001];
        HttpStreamTest.randoCalrissian.nextBytes(bigData);
        Mockito.when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(bigData));
        try (HttpStream stream = streamFactory.create(connection, HttpStreamTest.AURL, HttpStreamTest.BAD_CHECKSUM, reconnector)) {
            thrown.expect(IOException.class);
            thrown.expectMessage("Checksum");
            ByteStreams.exhaust(stream);
            Assert.fail("Should have thrown error before close()");
        }
    }

    @Test
    public void httpServerSaidGzippedButNotGzipped_throwsZipExceptionInCreate() throws Exception {
        Mockito.when(connection.getURL()).thenReturn(HttpStreamTest.AURL);
        Mockito.when(connection.getContentEncoding()).thenReturn("gzip");
        thrown.expect(ZipException.class);
        streamFactory.create(connection, HttpStreamTest.AURL, "", reconnector);
    }

    @Test
    public void javascriptGzippedInTransit_automaticallyGunzips() throws Exception {
        Mockito.when(connection.getURL()).thenReturn(HttpStreamTest.AURL);
        Mockito.when(connection.getContentEncoding()).thenReturn("x-gzip");
        Mockito.when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(HttpStreamTest.gzipData(HttpStreamTest.data)));
        try (HttpStream stream = streamFactory.create(connection, HttpStreamTest.AURL, "", reconnector)) {
            assertThat(ByteStreams.toByteArray(stream)).isEqualTo(HttpStreamTest.data);
        }
    }

    @Test
    public void serverSaysTarballPathIsGzipped_doesntAutomaticallyGunzip() throws Exception {
        byte[] gzData = HttpStreamTest.gzipData(HttpStreamTest.data);
        Mockito.when(connection.getURL()).thenReturn(new URL("http://doodle.example/foo.tar.gz"));
        Mockito.when(connection.getContentEncoding()).thenReturn("gzip");
        Mockito.when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(gzData));
        try (HttpStream stream = streamFactory.create(connection, HttpStreamTest.AURL, "", reconnector)) {
            assertThat(ByteStreams.toByteArray(stream)).isEqualTo(gzData);
        }
    }

    @Test
    public void threadInterrupted_haltsReadingAndThrowsInterrupt() throws Exception {
        final AtomicBoolean wasInterrupted = new AtomicBoolean();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (HttpStream stream = streamFactory.create(connection, HttpStreamTest.AURL, "", reconnector)) {
                    stream.read();
                    Thread.currentThread().interrupt();
                    stream.read();
                    Assert.fail();
                } catch (InterruptedIOException expected) {
                    wasInterrupted.set(true);
                } catch (IOException ignored) {
                    // ignored
                }
            }
        });
        thread.start();
        thread.join();
        assertThat(wasInterrupted.get()).isTrue();
    }
}

