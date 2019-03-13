/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.storage;


import BlobWriteChannel.StateImpl;
import CaptureType.ALL;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BlobWriteChannelTest {
    private static final String BUCKET_NAME = "b";

    private static final String BLOB_NAME = "n";

    private static final String UPLOAD_ID = "uploadid";

    private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder(BlobWriteChannelTest.BUCKET_NAME, BlobWriteChannelTest.BLOB_NAME).build();

    private static final Map<StorageRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    private static final int MIN_CHUNK_SIZE = 256 * 1024;

    private static final int DEFAULT_CHUNK_SIZE = 8 * (BlobWriteChannelTest.MIN_CHUNK_SIZE);

    private static final int CUSTOM_CHUNK_SIZE = 4 * (BlobWriteChannelTest.MIN_CHUNK_SIZE);

    private static final Random RANDOM = new Random();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private StorageOptions options;

    private StorageRpcFactory rpcFactoryMock;

    private StorageRpc storageRpcMock;

    private BlobWriteChannel writer;

    @Test
    public void testCreate() {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertTrue(writer.isOpen());
    }

    @Test
    public void testCreateRetryableError() {
        StorageException exception = new StorageException(new SocketException("Socket closed"));
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andThrow(exception);
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertTrue(writer.isOpen());
    }

    @Test
    public void testCreateNonRetryableError() {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andThrow(new RuntimeException());
        replay(storageRpcMock);
        thrown.expect(RuntimeException.class);
        new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
    }

    @Test
    public void testWriteWithoutFlush() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertEquals(BlobWriteChannelTest.MIN_CHUNK_SIZE, writer.write(ByteBuffer.allocate(BlobWriteChannelTest.MIN_CHUNK_SIZE)));
    }

    @Test
    public void testWriteWithFlush() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        storageRpcMock.write(eq(BlobWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(BlobWriteChannelTest.CUSTOM_CHUNK_SIZE), eq(false));
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        writer.setChunkSize(BlobWriteChannelTest.CUSTOM_CHUNK_SIZE);
        ByteBuffer buffer = BlobWriteChannelTest.randomBuffer(BlobWriteChannelTest.CUSTOM_CHUNK_SIZE);
        Assert.assertEquals(BlobWriteChannelTest.CUSTOM_CHUNK_SIZE, writer.write(buffer));
        Assert.assertArrayEquals(buffer.array(), capturedBuffer.getValue());
    }

    @Test
    public void testWritesAndFlush() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        storageRpcMock.write(eq(BlobWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE), eq(false));
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        ByteBuffer[] buffers = new ByteBuffer[(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE) / (BlobWriteChannelTest.MIN_CHUNK_SIZE)];
        for (int i = 0; i < (buffers.length); i++) {
            buffers[i] = BlobWriteChannelTest.randomBuffer(BlobWriteChannelTest.MIN_CHUNK_SIZE);
            Assert.assertEquals(BlobWriteChannelTest.MIN_CHUNK_SIZE, writer.write(buffers[i]));
        }
        for (int i = 0; i < (buffers.length); i++) {
            Assert.assertArrayEquals(buffers[i].array(), Arrays.copyOfRange(capturedBuffer.getValue(), ((BlobWriteChannelTest.MIN_CHUNK_SIZE) * i), ((BlobWriteChannelTest.MIN_CHUNK_SIZE) * (i + 1))));
        }
    }

    @Test
    public void testCloseWithoutFlush() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        storageRpcMock.write(eq(BlobWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true));
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertTrue(writer.isOpen());
        writer.close();
        Assert.assertArrayEquals(new byte[0], capturedBuffer.getValue());
        Assert.assertTrue((!(writer.isOpen())));
    }

    @Test
    public void testCloseWithFlush() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        ByteBuffer buffer = BlobWriteChannelTest.randomBuffer(BlobWriteChannelTest.MIN_CHUNK_SIZE);
        storageRpcMock.write(eq(BlobWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(BlobWriteChannelTest.MIN_CHUNK_SIZE), eq(true));
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertTrue(writer.isOpen());
        writer.write(buffer);
        writer.close();
        Assert.assertEquals(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE, capturedBuffer.getValue().length);
        Assert.assertArrayEquals(buffer.array(), Arrays.copyOf(capturedBuffer.getValue(), BlobWriteChannelTest.MIN_CHUNK_SIZE));
        Assert.assertTrue((!(writer.isOpen())));
    }

    @Test
    public void testWriteClosed() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        storageRpcMock.write(eq(BlobWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true));
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        writer.close();
        try {
            writer.write(ByteBuffer.allocate(BlobWriteChannelTest.MIN_CHUNK_SIZE));
            Assert.fail("Expected BlobWriteChannel write to throw IOException");
        } catch (IOException ex) {
            // expected
        }
    }

    @Test
    public void testSaveAndRestore() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance(ALL);
        Capture<Long> capturedPosition = Capture.newInstance(ALL);
        storageRpcMock.write(eq(BlobWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), captureLong(capturedPosition), eq(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE), eq(false));
        expectLastCall().times(2);
        replay(storageRpcMock);
        ByteBuffer buffer1 = BlobWriteChannelTest.randomBuffer(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer buffer2 = BlobWriteChannelTest.randomBuffer(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertEquals(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE, writer.write(buffer1));
        Assert.assertArrayEquals(buffer1.array(), capturedBuffer.getValues().get(0));
        Assert.assertEquals(new Long(0L), capturedPosition.getValues().get(0));
        RestorableState<WriteChannel> writerState = writer.capture();
        WriteChannel restoredWriter = writerState.restore();
        Assert.assertEquals(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE, restoredWriter.write(buffer2));
        Assert.assertArrayEquals(buffer2.array(), capturedBuffer.getValues().get(1));
        Assert.assertEquals(new Long(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE), capturedPosition.getValues().get(1));
    }

    @Test
    public void testSaveAndRestoreClosed() throws IOException {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        storageRpcMock.write(eq(BlobWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true));
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        writer.close();
        RestorableState<WriteChannel> writerState = writer.capture();
        RestorableState<WriteChannel> expectedWriterState = StateImpl.builder(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.UPLOAD_ID).setBuffer(null).setChunkSize(BlobWriteChannelTest.DEFAULT_CHUNK_SIZE).setIsOpen(false).setPosition(0).build();
        WriteChannel restoredWriter = writerState.restore();
        Assert.assertArrayEquals(new byte[0], capturedBuffer.getValue());
        Assert.assertEquals(expectedWriterState, restoredWriter.capture());
    }

    @Test
    public void testStateEquals() {
        expect(storageRpcMock.open(BlobWriteChannelTest.BLOB_INFO.toPb(), BlobWriteChannelTest.EMPTY_RPC_OPTIONS)).andReturn(BlobWriteChannelTest.UPLOAD_ID).times(2);
        replay(storageRpcMock);
        writer = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        // avoid closing when you don't want partial writes to GCS upon failure
        @SuppressWarnings("resource")
        WriteChannel writer2 = new BlobWriteChannel(options, BlobWriteChannelTest.BLOB_INFO, BlobWriteChannelTest.EMPTY_RPC_OPTIONS);
        RestorableState<WriteChannel> state = writer.capture();
        RestorableState<WriteChannel> state2 = writer2.capture();
        Assert.assertEquals(state, state2);
        Assert.assertEquals(state.hashCode(), state2.hashCode());
        Assert.assertEquals(state.toString(), state2.toString());
    }
}

