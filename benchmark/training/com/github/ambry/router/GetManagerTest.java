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


import GetBlobOptions.OperationType;
import GetBlobOptions.OperationType.BlobInfo;
import GetBlobOptions.OperationType.Data;
import RouterErrorCode.InvalidBlobId;
import RouterErrorCode.OperationTimedOut;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.config.RouterConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.TestUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class GetManagerTest {
    private final MockServerLayout mockServerLayout;

    private final MockTime mockTime = new MockTime();

    private final MockClusterMap mockClusterMap;

    private final Random random = new Random();

    // this is a reference to the state used by the mockSelector. just allows tests to manipulate the state.
    private final AtomicReference<MockSelectorState> mockSelectorState = new AtomicReference<MockSelectorState>();

    private NonBlockingRouter router;

    private final boolean testEncryption;

    private KeyManagementService kms = null;

    private CryptoService cryptoService = null;

    private CryptoJobHandler cryptoJobHandler = null;

    private RouterConfig routerConfig;

    private int chunkSize;

    private int requestParallelism;

    private int successTarget;

    // Request params;
    private long blobSize;

    private BlobProperties putBlobProperties;

    private byte[] putUserMetadata;

    private byte[] putContent;

    private ReadableStreamChannel putChannel;

    private GetBlobOptions options = new GetBlobOptionsBuilder().build();

    private static final int MAX_PORTS_PLAIN_TEXT = 3;

    private static final int MAX_PORTS_SSL = 3;

    private static final int CHECKOUT_TIMEOUT_MS = 1000;

    /**
     * Pre-initialization common to all tests.
     *
     * @param testEncryption
     * 		{@code true} if blobs need to be tested w/ encryption. {@code false} otherwise
     */
    public GetManagerTest(boolean testEncryption) throws Exception {
        this.testEncryption = testEncryption;
        // random chunkSize in the range [1, 1 MB]
        chunkSize = (random.nextInt((1024 * 1024))) + 1;
        requestParallelism = 3;
        successTarget = 2;
        mockSelectorState.set(MockSelectorState.Good);
        mockClusterMap = new MockClusterMap();
        mockServerLayout = new MockServerLayout(mockClusterMap);
        if (testEncryption) {
            VerifiableProperties vProps = new VerifiableProperties(new Properties());
            kms = new SingleKeyManagementService(new com.github.ambry.config.KMSConfig(vProps), TestUtils.getRandomKey(SingleKeyManagementServiceTest.DEFAULT_KEY_SIZE_CHARS));
            cryptoService = new GCMCryptoService(new com.github.ambry.config.CryptoServiceConfig(vProps));
            cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        }
    }

    /**
     * Tests getBlobInfo() and getBlob() of simple blobs
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSimpleBlobGetSuccess() throws Exception {
        testGetSuccess(chunkSize, new GetBlobOptionsBuilder().build());
    }

    /**
     * Tests getBlobInfo() and getBlob() of composite blobs
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCompositeBlobGetSuccess() throws Exception {
        testGetSuccess((((chunkSize) * 6) + 11), new GetBlobOptionsBuilder().build());
    }

    /**
     * Tests the router range request interface.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRangeRequest() throws Exception {
        testGetSuccess((((chunkSize) * 6) + 11), new GetBlobOptionsBuilder().operationType(Data).range(ByteRanges.fromOffsetRange((((chunkSize) * 2) + 3), (((chunkSize) * 5) + 4))).build());
    }

    /**
     * Test that an exception thrown in a user defined callback will not crash the router
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCallbackRuntimeException() throws Exception {
        final CountDownLatch getBlobCallbackCalled = new CountDownLatch(1);
        testBadCallback(new Callback<GetBlobResult>() {
            @Override
            public void onCompletion(GetBlobResult result, Exception exception) {
                getBlobCallbackCalled.countDown();
                throw new RuntimeException("Throwing an exception in the user callback");
            }
        }, getBlobCallbackCalled, true);
    }

    /**
     * Test the case where async write results in an exception. Read should be notified,
     * operation should get completed.
     */
    @Test
    public void testAsyncWriteException() throws Exception {
        final CountDownLatch getBlobCallbackCalled = new CountDownLatch(1);
        testBadCallback(new Callback<GetBlobResult>() {
            @Override
            public void onCompletion(final GetBlobResult result, final Exception exception) {
                getBlobCallbackCalled.countDown();
                AsyncWritableChannel asyncWritableChannel = new AsyncWritableChannel() {
                    boolean open = true;

                    @Override
                    public Future<Long> write(ByteBuffer src, Callback<Long> callback) {
                        throw new RuntimeException("This will be thrown when the channel is written to.");
                    }

                    @Override
                    public boolean isOpen() {
                        return open;
                    }

                    @Override
                    public void close() throws IOException {
                        open = false;
                    }
                };
                result.getBlobDataChannel().readInto(asyncWritableChannel, null);
            }
        }, getBlobCallbackCalled, false);
    }

    /**
     * Tests the failure case where poll throws and closes the router. This also tests the case where the GetManager
     * gets closed with active operations, and ensures that operations get completed with the appropriate error.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailureOnAllPollThatSends() throws Exception {
        router = getNonBlockingRouter();
        setOperationParams(chunkSize, new GetBlobOptionsBuilder().build());
        String blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
        mockSelectorState.set(MockSelectorState.ThrowExceptionOnSend);
        Future future;
        try {
            future = router.getBlob(blobId, new GetBlobOptionsBuilder().operationType(BlobInfo).build());
            while (!(future.isDone())) {
                mockTime.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
                Thread.yield();
            } 
            future.get();
            Assert.fail("operation should have thrown");
        } catch (ExecutionException e) {
            RouterException routerException = ((RouterException) (e.getCause()));
            Assert.assertEquals(OperationTimedOut, routerException.getErrorCode());
        }
        try {
            future = router.getBlob(blobId, options);
            while (!(future.isDone())) {
                mockTime.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
                Thread.yield();
            } 
            future.get();
            Assert.fail("operation should have thrown");
        } catch (ExecutionException e) {
            RouterException routerException = ((RouterException) (e.getCause()));
            Assert.assertEquals(OperationTimedOut, routerException.getErrorCode());
        }
        router.close();
    }

    /**
     * Tests Bad blobId for different get operations
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBadBlobId() throws Exception {
        router = getNonBlockingRouter();
        setOperationParams(chunkSize, new GetBlobOptionsBuilder().build());
        String[] badBlobIds = new String[]{ "", "abc", "123", "invalid_id", "[],/-" };
        for (String blobId : badBlobIds) {
            for (GetBlobOptions.OperationType opType : OperationType.values()) {
                getBlobAndAssertFailure(blobId, new GetBlobOptionsBuilder().operationType(opType).build(), InvalidBlobId);
            }
        }
        router.close();
    }
}

