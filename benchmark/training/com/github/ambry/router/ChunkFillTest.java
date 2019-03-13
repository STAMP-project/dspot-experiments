/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.router;


import MockClusterMap.DEFAULT_PARTITION_CLASS;
import PutBlobOptions.DEFAULT;
import PutOperation.ChunkState;
import PutOperation.ChunkState.Ready;
import PutOperation.PutChunk;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.LoggingNotificationSystem;
import com.github.ambry.config.RouterConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Utils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * A class to test the chunk filling flow in the {@link PutManager}. Tests create operations with a channel and
 * ensure that chunks are filled correctly, and continue filling in as chunks get consumed.
 */
@RunWith(Parameterized.class)
public class ChunkFillTest {
    private final boolean testEncryption;

    private ByteBuffer[] compositeBuffers;

    private ByteBuffer[] compositeEncryptionKeys;

    private BlobId[] compositeBlobIds;

    private int totalSizeWritten = 0;

    private int numChunks = 0;

    private byte[] putContent;

    private int blobSize;

    private int chunkSize;

    private Random random = new Random();

    private NonBlockingRouterMetrics routerMetrics;

    private MockKeyManagementService kms = null;

    private MockCryptoService cryptoService = null;

    private CryptoJobHandler cryptoJobHandler = null;

    public ChunkFillTest(boolean testEncryption) {
        this.testEncryption = testEncryption;
    }

    /**
     * Test chunk filling with blob size zero.
     */
    @Test
    public void testChunkFillingBlobSizeZero() throws Exception {
        blobSize = 0;
        fillChunksAndAssertSuccess();
    }

    /**
     * Test chunk filling with a non-zero blobSize that is less than the chunk size.
     */
    @Test
    public void testChunkFillingBlobSizeLessThanChunkSize() throws Exception {
        blobSize = (random.nextInt(((chunkSize) - 1))) + 1;
        fillChunksAndAssertSuccess();
    }

    /**
     * Test chunk filling with blob size a multiple of the chunk size.
     */
    @Test
    public void testChunkFillingBlobSizeMultipleOfChunkSize() throws Exception {
        blobSize = (chunkSize) * ((random.nextInt(10)) + 1);
        fillChunksAndAssertSuccess();
    }

    /**
     * Test chunk filling with blob size not a multiple of the chunk size.
     */
    @Test
    public void testChunkFillingBlobSizeNotMultipleOfChunkSize() throws Exception {
        blobSize = (((chunkSize) * ((random.nextInt(10)) + 1)) + (random.nextInt(((chunkSize) - 1)))) + 1;
        fillChunksAndAssertSuccess();
    }

    /**
     * Test the calculation of number of chunks and the size of each chunk, using a very large blob size. No content
     * comparison is done. This test does not consume memory more than chunkSize.
     */
    @Test
    public void testChunkNumAndSizeCalculations() throws Exception {
        chunkSize = (4 * 1024) * 1024;
        // a large blob greater than Integer.MAX_VALUE and not at chunk size boundary.
        final long blobSize = ((((((long) (Integer.MAX_VALUE)) / (chunkSize)) + 1) * (chunkSize)) + (random.nextInt(((chunkSize) - 1)))) + 1;
        VerifiableProperties vProps = getNonBlockingRouterProperties();
        MockClusterMap mockClusterMap = new MockClusterMap();
        RouterConfig routerConfig = new RouterConfig(vProps);
        NonBlockingRouterMetrics routerMetrics = new NonBlockingRouterMetrics(mockClusterMap);
        short accountId = Utils.getRandomShort(random);
        short containerId = Utils.getRandomShort(random);
        BlobProperties putBlobProperties = new BlobProperties(blobSize, "serviceId", "memberId", "contentType", false, Utils.Infinite_Time, accountId, containerId, false, null);
        Random random = new Random();
        byte[] putUserMetadata = new byte[10];
        random.nextBytes(putUserMetadata);
        final MockReadableStreamChannel putChannel = new MockReadableStreamChannel(blobSize, false);
        FutureResult<String> futureResult = new FutureResult<String>();
        MockTime time = new MockTime();
        MockNetworkClientFactory networkClientFactory = new MockNetworkClientFactory(vProps, null, 0, 0, 0, null, time);
        PutOperation op = PutOperation.forUpload(routerConfig, routerMetrics, mockClusterMap, new LoggingNotificationSystem(), new InMemAccountService(true, false), putUserMetadata, putChannel, DEFAULT, futureResult, null, new RouterCallback(networkClientFactory.getNetworkClient(), new ArrayList()), null, null, null, null, new MockTime(), putBlobProperties, DEFAULT_PARTITION_CLASS);
        op.startOperation();
        numChunks = RouterUtils.getNumChunksForBlobAndChunkSize(blobSize, chunkSize);
        // largeBlobSize is not a multiple of chunkSize
        int expectedNumChunks = ((int) ((blobSize / (chunkSize)) + 1));
        Assert.assertEquals("numChunks should be as expected", expectedNumChunks, numChunks);
        int lastChunkSize = ((int) (blobSize % (chunkSize)));
        final AtomicReference<Exception> channelException = new AtomicReference<Exception>(null);
        int chunkIndex = 0;
        // The write to the MockReadableStreamChannel blocks until the data is read as part fo the chunk filling,
        // so create a thread that fills the MockReadableStreamChannel.
        Utils.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] writeBuf = new byte[chunkSize];
                    long written = 0;
                    while (written < blobSize) {
                        int toWrite = ((int) (Math.min(chunkSize, (blobSize - written))));
                        putChannel.write(ByteBuffer.wrap(writeBuf, 0, toWrite));
                        written += toWrite;
                    } 
                } catch (Exception e) {
                    channelException.set(e);
                }
            }
        }, false).start();
        // Do the chunk filling.
        boolean fillingComplete = false;
        do {
            op.fillChunks();
            // All existing chunks must have been filled if no work was done in the last call,
            // since the channel is ByteBuffer based.
            for (PutOperation.PutChunk putChunk : op.putChunks) {
                Assert.assertNull("Mock channel write should not have caused an exception", channelException.get());
                if (putChunk.isFree()) {
                    continue;
                }
                if (chunkIndex == ((numChunks) - 1)) {
                    // last chunk may not be Ready as it is dependent on the completion callback to be called.
                    Assert.assertTrue("Chunk should be Building or Ready.", (((putChunk.getState()) == (ChunkState.Ready)) || ((putChunk.getState()) == (ChunkState.Building))));
                    if ((putChunk.getState()) == (ChunkState.Ready)) {
                        Assert.assertEquals("Chunk size should be the last chunk size", lastChunkSize, putChunk.buf.remaining());
                        Assert.assertTrue("Chunk Filling should be complete at this time", op.isChunkFillingDone());
                        fillingComplete = true;
                    }
                } else {
                    // if not last chunk, then the chunk should be full and Ready.
                    Assert.assertEquals("Chunk should be ready.", Ready, putChunk.getState());
                    Assert.assertEquals("Chunk size should be maxChunkSize", chunkSize, putChunk.buf.remaining());
                    chunkIndex++;
                    putChunk.clear();
                }
            }
        } while (!fillingComplete );
    }
}

