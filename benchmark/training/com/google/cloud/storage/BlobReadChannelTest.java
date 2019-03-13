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


import com.google.cloud.ReadChannel;
import com.google.cloud.RestorableState;
import com.google.cloud.Tuple;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class BlobReadChannelTest {
    private static final String BUCKET_NAME = "b";

    private static final String BLOB_NAME = "n";

    private static final BlobId BLOB_ID = BlobId.of(BlobReadChannelTest.BUCKET_NAME, BlobReadChannelTest.BLOB_NAME, (-1L));

    private static final Map<StorageRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    private static final int DEFAULT_CHUNK_SIZE = (2 * 1024) * 1024;

    private static final int CUSTOM_CHUNK_SIZE = (2 * 1024) * 1024;

    private static final Random RANDOM = new Random();

    private StorageOptions options;

    private StorageRpcFactory rpcFactoryMock;

    private StorageRpc storageRpcMock;

    private BlobReadChannel reader;

    @Test
    public void testCreate() {
        replay(storageRpcMock);
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertTrue(reader.isOpen());
    }

    @Test
    public void testReadBuffered() throws IOException {
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        byte[] result = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer firstReadBuffer = ByteBuffer.allocate(42);
        ByteBuffer secondReadBuffer = ByteBuffer.allocate(42);
        expect(storageRpcMock.read(BlobReadChannelTest.BLOB_ID.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, 0, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", result));
        replay(storageRpcMock);
        reader.read(firstReadBuffer);
        reader.read(secondReadBuffer);
        Assert.assertArrayEquals(Arrays.copyOf(result, firstReadBuffer.capacity()), firstReadBuffer.array());
        Assert.assertArrayEquals(Arrays.copyOfRange(result, firstReadBuffer.capacity(), ((firstReadBuffer.capacity()) + (secondReadBuffer.capacity()))), secondReadBuffer.array());
    }

    @Test
    public void testReadBig() throws IOException {
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        reader.setChunkSize(BlobReadChannelTest.CUSTOM_CHUNK_SIZE);
        byte[] firstResult = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        byte[] secondResult = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer firstReadBuffer = ByteBuffer.allocate(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer secondReadBuffer = ByteBuffer.allocate(42);
        expect(storageRpcMock.read(BlobReadChannelTest.BLOB_ID.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, 0, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", firstResult));
        expect(storageRpcMock.read(BlobReadChannelTest.BLOB_ID.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, BlobReadChannelTest.DEFAULT_CHUNK_SIZE, BlobReadChannelTest.CUSTOM_CHUNK_SIZE)).andReturn(Tuple.of("etag", secondResult));
        replay(storageRpcMock);
        reader.read(firstReadBuffer);
        reader.read(secondReadBuffer);
        Assert.assertArrayEquals(firstResult, firstReadBuffer.array());
        Assert.assertArrayEquals(Arrays.copyOf(secondResult, secondReadBuffer.capacity()), secondReadBuffer.array());
    }

    @Test
    public void testReadFinish() throws IOException {
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        byte[] result = new byte[]{  };
        ByteBuffer readBuffer = ByteBuffer.allocate(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        expect(storageRpcMock.read(BlobReadChannelTest.BLOB_ID.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, 0, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", result));
        replay(storageRpcMock);
        Assert.assertEquals((-1), reader.read(readBuffer));
    }

    @Test
    public void testSeek() throws IOException {
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        reader.seek(42);
        byte[] result = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer readBuffer = ByteBuffer.allocate(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        expect(storageRpcMock.read(BlobReadChannelTest.BLOB_ID.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, 42, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", result));
        replay(storageRpcMock);
        reader.read(readBuffer);
        Assert.assertArrayEquals(result, readBuffer.array());
    }

    @Test
    public void testClose() {
        replay(storageRpcMock);
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        Assert.assertTrue(reader.isOpen());
        reader.close();
        Assert.assertTrue((!(reader.isOpen())));
    }

    @Test
    public void testReadClosed() throws IOException {
        replay(storageRpcMock);
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        reader.close();
        try {
            ByteBuffer readBuffer = ByteBuffer.allocate(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
            reader.read(readBuffer);
            Assert.fail("Expected BlobReadChannel read to throw ClosedChannelException");
        } catch (ClosedChannelException ex) {
            // expected
        }
    }

    @Test
    public void testReadGenerationChanged() throws IOException {
        BlobId blobId = BlobId.of(BlobReadChannelTest.BUCKET_NAME, BlobReadChannelTest.BLOB_NAME);
        reader = new BlobReadChannel(options, blobId, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        byte[] firstResult = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        byte[] secondResult = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer firstReadBuffer = ByteBuffer.allocate(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer secondReadBuffer = ByteBuffer.allocate(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        expect(storageRpcMock.read(blobId.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, 0, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag1", firstResult));
        expect(storageRpcMock.read(blobId.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, BlobReadChannelTest.DEFAULT_CHUNK_SIZE, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag2", secondResult));
        replay(storageRpcMock);
        reader.read(firstReadBuffer);
        try {
            reader.read(secondReadBuffer);
            Assert.fail("Expected ReadChannel read to throw StorageException");
        } catch (StorageException ex) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Blob ").append(blobId).append(" was updated while reading");
            Assert.assertEquals(messageBuilder.toString(), ex.getMessage());
        }
    }

    @Test
    public void testSaveAndRestore() throws IOException {
        byte[] firstResult = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        byte[] secondResult = BlobReadChannelTest.randomByteArray(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer firstReadBuffer = ByteBuffer.allocate(42);
        ByteBuffer secondReadBuffer = ByteBuffer.allocate(BlobReadChannelTest.DEFAULT_CHUNK_SIZE);
        expect(storageRpcMock.read(BlobReadChannelTest.BLOB_ID.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, 0, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", firstResult));
        expect(storageRpcMock.read(BlobReadChannelTest.BLOB_ID.toPb(), BlobReadChannelTest.EMPTY_RPC_OPTIONS, 42, BlobReadChannelTest.DEFAULT_CHUNK_SIZE)).andReturn(Tuple.of("etag", secondResult));
        replay(storageRpcMock);
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        reader.read(firstReadBuffer);
        RestorableState<ReadChannel> readerState = reader.capture();
        ReadChannel restoredReader = readerState.restore();
        restoredReader.read(secondReadBuffer);
        Assert.assertArrayEquals(Arrays.copyOf(firstResult, firstReadBuffer.capacity()), firstReadBuffer.array());
        Assert.assertArrayEquals(secondResult, secondReadBuffer.array());
    }

    @Test
    public void testStateEquals() {
        replay(storageRpcMock);
        reader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        // avoid closing when you don't want partial writes to GCS
        @SuppressWarnings("resource")
        ReadChannel secondReader = new BlobReadChannel(options, BlobReadChannelTest.BLOB_ID, BlobReadChannelTest.EMPTY_RPC_OPTIONS);
        RestorableState<ReadChannel> state = reader.capture();
        RestorableState<ReadChannel> secondState = secondReader.capture();
        Assert.assertEquals(state, secondState);
        Assert.assertEquals(state.hashCode(), secondState.hashCode());
        Assert.assertEquals(state.toString(), secondState.toString());
    }
}

