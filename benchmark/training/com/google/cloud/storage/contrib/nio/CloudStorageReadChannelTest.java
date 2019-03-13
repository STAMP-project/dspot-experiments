/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage.contrib.nio;


import CloudStorageConfiguration.DEFAULT;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import javax.net.ssl.SSLHandshakeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link CloudStorageReadChannel}.
 */
@RunWith(JUnit4.class)
public class CloudStorageReadChannelTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private CloudStorageReadChannel chan;

    private final Storage gcsStorage = Mockito.mock(Storage.class);

    private final BlobId file = BlobId.of("blob", "attack");

    private final Blob metadata = Mockito.mock(Blob.class);

    private final ReadChannel gcsChannel = Mockito.mock(ReadChannel.class);

    @Test
    public void testRead() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        Mockito.when(gcsChannel.read(ArgumentMatchers.eq(buffer))).thenReturn(1);
        assertThat(chan.position()).isEqualTo(0L);
        assertThat(chan.read(buffer)).isEqualTo(1);
        assertThat(chan.position()).isEqualTo(1L);
        Mockito.verify(gcsChannel).read(ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verify(gcsChannel, Mockito.times(3)).isOpen();
    }

    @Test
    public void testReadRetry() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        Mockito.when(gcsChannel.read(ArgumentMatchers.eq(buffer))).thenThrow(new StorageException(new IOException("outer", new IOException("Connection closed prematurely: bytesRead = 33554432, Content-Length = 41943040")))).thenReturn(1);
        assertThat(chan.position()).isEqualTo(0L);
        assertThat(chan.read(buffer)).isEqualTo(1);
        assertThat(chan.position()).isEqualTo(1L);
        Mockito.verify(gcsChannel, Mockito.times(2)).read(ArgumentMatchers.any(ByteBuffer.class));
    }

    @Test
    public void testReadRetrySSLHandshake() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        Mockito.when(gcsChannel.read(ArgumentMatchers.eq(buffer))).thenThrow(new StorageException(new IOException("something", new IOException("thing", new SSLHandshakeException("connection closed due to throttling"))))).thenReturn(1);
        assertThat(chan.position()).isEqualTo(0L);
        assertThat(chan.read(buffer)).isEqualTo(1);
        assertThat(chan.position()).isEqualTo(1L);
        Mockito.verify(gcsChannel, Mockito.times(2)).read(ArgumentMatchers.any(ByteBuffer.class));
    }

    @Test
    public void testReadRetryEventuallyGivesUp() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        Mockito.when(gcsChannel.read(ArgumentMatchers.eq(buffer))).thenThrow(new StorageException(new IOException("Connection closed prematurely: bytesRead = 33554432, Content-Length = 41943040"))).thenThrow(new StorageException(new IOException("Connection closed prematurely: bytesRead = 33554432, Content-Length = 41943040"))).thenReturn(1);
        assertThat(chan.position()).isEqualTo(0L);
        thrown.expect(StorageException.class);
        chan.read(buffer);
    }

    @Test
    public void testRead_whenClosed_throwsCce() throws IOException {
        Mockito.when(gcsChannel.isOpen()).thenReturn(false);
        thrown.expect(ClosedChannelException.class);
        chan.read(ByteBuffer.allocate(1));
    }

    @Test
    public void testWrite_throwsNonWritableChannelException() throws IOException {
        thrown.expect(NonWritableChannelException.class);
        chan.write(ByteBuffer.allocate(1));
    }

    @Test
    public void testTruncate_throwsNonWritableChannelException() throws IOException {
        thrown.expect(NonWritableChannelException.class);
        chan.truncate(0);
    }

    @Test
    public void testIsOpen() throws IOException {
        Mockito.when(gcsChannel.isOpen()).thenReturn(true).thenReturn(false);
        assertThat(chan.isOpen()).isTrue();
        chan.close();
        assertThat(chan.isOpen()).isFalse();
        Mockito.verify(gcsChannel, Mockito.times(2)).isOpen();
        Mockito.verify(gcsChannel).close();
    }

    @Test
    public void testSize() throws IOException {
        assertThat(chan.size()).isEqualTo(42L);
        Mockito.verify(gcsChannel).isOpen();
        Mockito.verifyZeroInteractions(gcsChannel);
    }

    @Test
    public void testSize_whenClosed_throwsCce() throws IOException {
        Mockito.when(gcsChannel.isOpen()).thenReturn(false);
        thrown.expect(ClosedChannelException.class);
        chan.size();
    }

    @Test
    public void testPosition_whenClosed_throwsCce() throws IOException {
        Mockito.when(gcsChannel.isOpen()).thenReturn(false);
        thrown.expect(ClosedChannelException.class);
        chan.position();
    }

    @Test
    public void testSetPosition_whenClosed_throwsCce() throws IOException {
        Mockito.when(gcsChannel.isOpen()).thenReturn(false);
        thrown.expect(ClosedChannelException.class);
        chan.position(0);
    }

    @Test
    public void testClose_calledMultipleTimes_doesntThrowAnError() throws IOException {
        chan.close();
        chan.close();
        chan.close();
    }

    @Test
    public void testSetPosition() throws IOException {
        assertThat(chan.position()).isEqualTo(0L);
        assertThat(chan.size()).isEqualTo(42L);
        chan.position(1L);
        assertThat(chan.position()).isEqualTo(1L);
        assertThat(chan.size()).isEqualTo(42L);
        Mockito.verify(gcsChannel).seek(1);
        Mockito.verify(gcsChannel, Mockito.times(5)).isOpen();
    }

    /* This test case was crafted in response to a bug in CloudStorageReadChannel in which the
    channel position (a long) was getting truncated to an int when seeking on the encapsulated
    ReadChannel in innerOpen(). This test case fails when the bad long -> int cast is present,
    and passes when it's removed.
     */
    @Test
    public void testChannelPositionDoesNotGetTruncatedToInt() throws IOException {
        // This position value will overflow to a negative value if a long -> int cast is attempted
        long startPosition = 11918483280L;
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        // Invoke CloudStorageReadChannel.create() to trigger a call to the private
        // CloudStorageReadChannel.innerOpen() method, which does a seek on our gcsChannel.
        CloudStorageReadChannel.create(gcsStorage, file, startPosition, 1, DEFAULT, "");
        // Confirm that our position did not overflow during the seek in
        // CloudStorageReadChannel.innerOpen()
        Mockito.verify(gcsChannel).seek(captor.capture());
        assertThat(captor.getValue()).isEqualTo(startPosition);
    }
}

