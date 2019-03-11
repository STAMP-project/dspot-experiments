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
import com.google.devtools.build.lib.bazel.repository.downloader.RetryingInputStream.Reconnector;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.URLConnection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link RetryingInputStream}.
 */
@RunWith(JUnit4.class)
public class RetryingInputStreamTest {
    private final InputStream delegate = Mockito.mock(InputStream.class);

    private final InputStream newDelegate = Mockito.mock(InputStream.class);

    private final Reconnector reconnector = Mockito.mock(Reconnector.class);

    private final URLConnection connection = Mockito.mock(URLConnection.class);

    private final RetryingInputStream stream = new RetryingInputStream(delegate, reconnector);

    @Test
    public void close_callsDelegate() throws Exception {
        stream.close();
        Mockito.verify(delegate).close();
    }

    @Test
    public void available_callsDelegate() throws Exception {
        stream.available();
        Mockito.verify(delegate).available();
    }

    @Test
    public void read_callsdelegate() throws Exception {
        stream.read();
        Mockito.verify(delegate).read();
    }

    @Test
    public void bufferRead_callsdelegate() throws Exception {
        byte[] buffer = new byte[1024];
        stream.read(buffer);
        Mockito.verify(delegate).read(ArgumentMatchers.same(buffer), ArgumentMatchers.eq(0), ArgumentMatchers.eq(1024));
    }

    @Test
    public void readThrowsExceptionWhenDisabled_passesThrough() throws Exception {
        stream.disabled = true;
        Mockito.when(delegate.read()).thenThrow(new IOException());
        try {
            stream.read();
            Assert.fail("Expected IOException");
        } catch (IOException expected) {
            Mockito.verify(delegate).read();
        }
    }

    @Test
    public void readInterrupted_alwaysPassesThrough() throws Exception {
        Mockito.when(delegate.read()).thenThrow(new InterruptedIOException());
        try {
            stream.read();
            Assert.fail("Expected InterruptedIOException");
        } catch (InterruptedIOException expected) {
            Mockito.verify(delegate).read();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readTimesOut_retries() throws Exception {
        Mockito.when(delegate.read()).thenReturn(1).thenThrow(new SocketTimeoutException());
        Mockito.when(reconnector.connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.any(ImmutableMap.class))).thenReturn(connection);
        Mockito.when(connection.getInputStream()).thenReturn(newDelegate);
        Mockito.when(newDelegate.read()).thenReturn(2);
        Mockito.when(connection.getHeaderField("Content-Range")).thenReturn("bytes 1-42/42");
        assertThat(stream.read()).isEqualTo(1);
        assertThat(stream.read()).isEqualTo(2);
        Mockito.verify(reconnector).connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.eq(ImmutableMap.of("Range", "bytes=1-")));
        Mockito.verify(delegate, Mockito.times(2)).read();
        Mockito.verify(delegate).close();
        Mockito.verify(newDelegate).read();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failureWhenNoBytesAreRead_doesntUseRange() throws Exception {
        Mockito.when(delegate.read()).thenThrow(new SocketTimeoutException());
        Mockito.when(newDelegate.read()).thenReturn(1);
        Mockito.when(reconnector.connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.any(ImmutableMap.class))).thenReturn(connection);
        Mockito.when(connection.getInputStream()).thenReturn(newDelegate);
        assertThat(stream.read()).isEqualTo(1);
        Mockito.verify(reconnector).connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.eq(ImmutableMap.<String, String>of()));
        Mockito.verify(delegate).read();
        Mockito.verify(delegate).close();
        Mockito.verify(newDelegate).read();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void reconnectFails_alwaysPassesThrough() throws Exception {
        Mockito.when(delegate.read()).thenThrow(new IOException());
        Mockito.when(reconnector.connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.any(ImmutableMap.class))).thenThrow(new IOException());
        try {
            stream.read();
            Assert.fail("Expected IOException");
        } catch (IOException expected) {
            Mockito.verify(delegate).read();
            Mockito.verify(delegate).close();
            Mockito.verify(reconnector).connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.any(ImmutableMap.class));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void maxRetries_givesUp() throws Exception {
        Mockito.when(delegate.read()).thenReturn(1).thenThrow(new IOException()).thenThrow(new IOException()).thenThrow(new IOException()).thenThrow(new SocketTimeoutException());
        Mockito.when(reconnector.connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.any(ImmutableMap.class))).thenReturn(connection);
        Mockito.when(connection.getInputStream()).thenReturn(delegate);
        Mockito.when(connection.getHeaderField("Content-Range")).thenReturn("bytes 1-42/42");
        stream.read();
        try {
            stream.read();
            Assert.fail("Expected SocketTimeoutException");
        } catch (SocketTimeoutException e) {
            assertThat(e.getSuppressed()).hasLength(3);
            Mockito.verify(reconnector, Mockito.times(3)).connect(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.eq(ImmutableMap.of("Range", "bytes=1-")));
            Mockito.verify(delegate, Mockito.times(5)).read();
            Mockito.verify(delegate, Mockito.times(3)).close();
        }
    }
}

