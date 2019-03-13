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


import BlobDataType.DATACHUNK;
import BlobDataType.METADATA;
import BlobDataType.SIMPLE;
import MockClusterMap.DEFAULT_PARTITION_CLASS;
import MockClusterMap.SPECIAL_PARTITION_CLASS;
import NotificationBlobType.DataChunk;
import PutBlobOptions.DEFAULT;
import ServerErrorCode.No_Error;
import ServerErrorCode.Unknown_Error;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.DataNodeId;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.commons.ByteBufferReadableStreamChannel;
import com.github.ambry.commons.LoggingNotificationSystem;
import com.github.ambry.commons.ServerErrorCode;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.notification.NotificationBlobType;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.ThrowingConsumer;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static PutBlobOptions.DEFAULT;
import static RouterErrorCode.AmbryUnavailable;
import static RouterErrorCode.BlobTooLarge;
import static RouterErrorCode.InvalidBlobId;
import static RouterErrorCode.InvalidPutArgument;
import static RouterErrorCode.OperationTimedOut;
import static RouterErrorCode.UnexpectedInternalError;
import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.WAITING;


/**
 * A class to test the Put implementation of the {@link NonBlockingRouter}.
 */
@RunWith(Parameterized.class)
public class PutManagerTest {
    static final GeneralSecurityException GSE = new GeneralSecurityException("Exception to throw for tests");

    private static final long MAX_WAIT_MS = 5000;

    private final boolean testEncryption;

    private final MockServerLayout mockServerLayout;

    private final MockTime mockTime = new MockTime();

    private final MockClusterMap mockClusterMap;

    private final InMemAccountService accountService;

    // this is a reference to the state used by the mockSelector. just allows tests to manipulate the state.
    private AtomicReference<MockSelectorState> mockSelectorState = new AtomicReference<>();

    private PutManagerTest.TestNotificationSystem notificationSystem;

    private NonBlockingRouter router;

    private NonBlockingRouterMetrics metrics;

    private MockKeyManagementService kms;

    private MockCryptoService cryptoService;

    private CryptoJobHandler cryptoJobHandler;

    private boolean instantiateEncryptionCast = true;

    private final ArrayList<PutManagerTest.RequestAndResult> requestAndResultsList = new ArrayList<>();

    private int chunkSize;

    private int requestParallelism;

    private int successTarget;

    private boolean instantiateNewRouterForPuts;

    private final Random random = new Random();

    private static final int MAX_PORTS_PLAIN_TEXT = 3;

    private static final int MAX_PORTS_SSL = 3;

    private static final int CHECKOUT_TIMEOUT_MS = 1000;

    private static final String LOCAL_DC = "DC1";

    private static final String EXTERNAL_ASSET_TAG = "ExternalAssetTag";

    /**
     * Pre-initialization common to all tests.
     *
     * @param testEncryption
     * 		{@code true} if blobs need to be tested w/ encryption. {@code false} otherwise
     */
    public PutManagerTest(boolean testEncryption) throws Exception {
        this.testEncryption = testEncryption;
        // random chunkSize in the range [2, 1 MB]
        chunkSize = (random.nextInt((1024 * 1024))) + 2;
        requestParallelism = 3;
        successTarget = 2;
        mockSelectorState.set(MockSelectorState.Good);
        mockClusterMap = new MockClusterMap();
        mockClusterMap.setLocalDatacenterName(PutManagerTest.LOCAL_DC);
        mockServerLayout = new MockServerLayout(mockClusterMap);
        notificationSystem = new PutManagerTest.TestNotificationSystem();
        instantiateNewRouterForPuts = true;
        accountService = new InMemAccountService(false, true);
    }

    /**
     * Tests puts of simple blobs, that is blobs that end up as a single chunk with no metadata content.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSimpleBlobPutSuccess() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(chunkSize));
        submitPutsAndAssertSuccess(true);
        for (int i = 0; i < 10; i++) {
            // size in [1, chunkSize]
            requestAndResultsList.clear();
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((random.nextInt(chunkSize)) + 1)));
            mockClusterMap.clearLastNRequestedPartitionClasses();
            submitPutsAndAssertSuccess(true);
            // since the puts are processed one at a time, it is fair to check the last partition class set
            checkLastRequestPartitionClasses(1, DEFAULT_PARTITION_CLASS);
        }
    }

    /**
     * Tests put of a composite blob (blob with more than one data chunk) where the composite blob size is a multiple of
     * the chunk size.
     */
    @Test
    public void testCompositeBlobChunkSizeMultiplePutSuccess() throws Exception {
        for (int i = 1; i < 10; i++) {
            requestAndResultsList.clear();
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * i)));
            mockClusterMap.clearLastNRequestedPartitionClasses();
            submitPutsAndAssertSuccess(true);
            // since the puts are processed one "large" blob at a time, it is fair to check the last partition classes set
            // one extra call if there is a metadata blob
            checkLastRequestPartitionClasses((i == 1 ? 1 : i + 1), DEFAULT_PARTITION_CLASS);
        }
    }

    /**
     * Tests put of a composite blob where the blob size is not a multiple of the chunk size.
     */
    @Test
    public void testCompositeBlobNotChunkSizeMultiplePutSuccess() throws Exception {
        for (int i = 1; i < 10; i++) {
            requestAndResultsList.clear();
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((((chunkSize) * i) + (random.nextInt(((chunkSize) - 1)))) + 1)));
            mockClusterMap.clearLastNRequestedPartitionClasses();
            submitPutsAndAssertSuccess(true);
            // since the puts are processed one "large" blob at a time, it is fair to check the last partition classes set
            checkLastRequestPartitionClasses((i + 2), DEFAULT_PARTITION_CLASS);
        }
    }

    /**
     * Test success cases with various {@link PutBlobOptions} set.
     */
    @Test
    public void testOptionsSuccess() throws Exception {
        ThrowingConsumer<PutManagerTest.RequestAndResult> runTest = ( requestAndResult) -> {
            requestAndResultsList.clear();
            requestAndResultsList.add(requestAndResult);
            mockClusterMap.clearLastNRequestedPartitionClasses();
            submitPutsAndAssertSuccess(true);
        };
        runTest.accept(new PutManagerTest.RequestAndResult(chunkSize, null, new PutBlobOptionsBuilder().chunkUpload(true).maxUploadSize(chunkSize).build(), null));
        runTest.accept(new PutManagerTest.RequestAndResult(((chunkSize) - 1), null, new PutBlobOptionsBuilder().chunkUpload(true).maxUploadSize(((chunkSize) - 1)).build(), null));
        runTest.accept(new PutManagerTest.RequestAndResult(((chunkSize) + 1), null, new PutBlobOptionsBuilder().maxUploadSize(((2 * (chunkSize)) - 1)).build(), null));
        runTest.accept(new PutManagerTest.RequestAndResult(0, null, new PutBlobOptionsBuilder().maxUploadSize(0).build(), null));
    }

    /**
     * Test failure cases with various {@link PutBlobOptions} set.
     */
    @Test
    public void testOptionsFailures() throws Exception {
        ThrowingConsumer<Pair<PutManagerTest.RequestAndResult, RouterErrorCode>> runTest = ( requestAndErrorCode) -> {
            requestAndResultsList.clear();
            requestAndResultsList.add(requestAndErrorCode.getFirst());
            mockClusterMap.clearLastNRequestedPartitionClasses();
            submitPutsAndAssertFailure(new RouterException("", requestAndErrorCode.getSecond()), true, false, false);
        };
        runTest.accept(new Pair(new PutManagerTest.RequestAndResult(chunkSize, null, new PutBlobOptionsBuilder().chunkUpload(true).maxUploadSize(((chunkSize) + 1)).build(), null), InvalidPutArgument));
        runTest.accept(new Pair(new PutManagerTest.RequestAndResult(((chunkSize) + 1), null, new PutBlobOptionsBuilder().chunkUpload(true).maxUploadSize(chunkSize).build(), null), BlobTooLarge));
        runTest.accept(new Pair(new PutManagerTest.RequestAndResult(2, null, new PutBlobOptionsBuilder().maxUploadSize(1).build(), null), BlobTooLarge));
        runTest.accept(new Pair(new PutManagerTest.RequestAndResult((2 * (chunkSize)), null, new PutBlobOptionsBuilder().maxUploadSize(((2 * (chunkSize)) - 1)).build(), null), BlobTooLarge));
        runTest.accept(new Pair(new PutManagerTest.RequestAndResult(0, null, new PutBlobOptionsBuilder().maxUploadSize((-1)).build(), null), BlobTooLarge));
    }

    /**
     * Test different cases where a stitch operation should succeed.
     */
    @Test
    public void testStitchBlobSuccess() throws Exception {
        ThrowingConsumer<List<ChunkInfo>> runTest = ( chunksToStitch) -> {
            requestAndResultsList.clear();
            requestAndResultsList.add(new com.github.ambry.router.RequestAndResult(chunksToStitch));
            mockClusterMap.clearLastNRequestedPartitionClasses();
            submitPutsAndAssertSuccess(true);
            // since the puts are processed one at a time, it is fair to check the last partition class set
            checkLastRequestPartitionClasses(1, MockClusterMap.DEFAULT_PARTITION_CLASS);
        };
        for (int i = 1; i < 10; i++) {
            // Chunks are all the same size.
            runTest.accept(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, RouterTestHelpers.buildValidChunkSizeStream(((chunkSize) * i), chunkSize)));
            // All intermediate chunks are the same size. Last is smaller.
            runTest.accept(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, RouterTestHelpers.buildValidChunkSizeStream(((((chunkSize) * i) + (random.nextInt(((chunkSize) - 1)))) + 1), chunkSize)));
            // Chunks are all the same size but smaller than routerMaxPutChunkSizeBytes.
            int dataChunkSize = 1 + (random.nextInt(((chunkSize) - 1)));
            runTest.accept(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, RouterTestHelpers.buildValidChunkSizeStream((dataChunkSize * i), dataChunkSize)));
            // All intermediate chunks are the same size but smaller than routerMaxPutChunkSizeBytes. Last is smaller.
            runTest.accept(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, RouterTestHelpers.buildValidChunkSizeStream((((dataChunkSize * i) + (random.nextInt((dataChunkSize - 1)))) + 1), dataChunkSize)));
        }
    }

    /**
     * Test different cases where a stitch operation should fail.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStitchBlobFailures() throws Exception {
        ThrowingConsumer<Pair<List<ChunkInfo>, RouterErrorCode>> runTest = ( chunksAndErrorCode) -> {
            requestAndResultsList.clear();
            requestAndResultsList.add(new com.github.ambry.router.RequestAndResult(chunksAndErrorCode.getFirst()));
            submitPutsAndAssertFailure(new RouterException("", chunksAndErrorCode.getSecond()), true, false, true);
        };
        // chunk size issues
        // intermediate chunk sizes do not match
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, LongStream.of(200, 10, 200)), InvalidPutArgument));
        // last chunk larger than intermediate chunks
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, LongStream.of(200, 201)), InvalidPutArgument));
        // last chunk is size 0 (0 not supported by current metadata format)
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, LongStream.of(200, 0)), InvalidPutArgument));
        // intermediate chunk is size 0 (0 not supported by current metadata format)
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, LongStream.of(200, 0, 200)), InvalidPutArgument));
        // invalid intermediate chunk size (0 not supported by current metadata format)
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, LongStream.of(0, 0)), InvalidPutArgument));
        // chunks sizes must be less than or equal to put chunk size config
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, RouterTestHelpers.buildValidChunkSizeStream((((chunkSize) + 1) * 3), ((chunkSize) + 1))), InvalidPutArgument));
        // must provide at least 1 chunk for stitching
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, Infinite_Time, LongStream.empty()), InvalidPutArgument));
        // TTL shorter than metadata blob TTL
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, DATACHUNK, 25, RouterTestHelpers.buildValidChunkSizeStream(((chunkSize) * 3), chunkSize)), InvalidPutArgument));
        // Chunk IDs must all be DATACHUNK type
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, METADATA, Infinite_Time, RouterTestHelpers.buildValidChunkSizeStream(((chunkSize) * 3), chunkSize)), InvalidBlobId));
        runTest.accept(new Pair(RouterTestHelpers.buildChunkList(mockClusterMap, SIMPLE, Infinite_Time, RouterTestHelpers.buildValidChunkSizeStream(((chunkSize) * 3), chunkSize)), InvalidBlobId));
    }

    /**
     * Test that a bad user defined callback will not crash the router.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBadCallback() throws Exception {
        PutManagerTest.RequestAndResult req = new PutManagerTest.RequestAndResult(((((chunkSize) * 5) + (random.nextInt(((chunkSize) - 1)))) + 1));
        router = getNonBlockingRouter();
        final CountDownLatch callbackCalled = new CountDownLatch(1);
        requestAndResultsList.clear();
        for (int i = 0; i < 4; i++) {
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) + ((random.nextInt(5)) * (random.nextInt(chunkSize))))));
        }
        instantiateNewRouterForPuts = false;
        ReadableStreamChannel putChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(req.putContent));
        Future future = router.putBlob(req.putBlobProperties, req.putUserMetadata, putChannel, req.options, ( result, exception) -> {
            callbackCalled.countDown();
            throw new RuntimeException("Throwing an exception in the user callback");
        });
        submitPutsAndAssertSuccess(false);
        // future.get() for operation with bad callback should still succeed
        future.get();
        Assert.assertTrue("Callback not called.", callbackCalled.await(PutManagerTest.MAX_WAIT_MS, TimeUnit.MILLISECONDS));
        Assert.assertEquals("All operations should be finished.", 0, router.getOperationsCount());
        Assert.assertTrue("Router should not be closed", router.isOpen());
        // Test that PutManager is still operational
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) + ((random.nextInt(5)) * (random.nextInt(chunkSize))))));
        instantiateNewRouterForPuts = false;
        submitPutsAndAssertSuccess(true);
    }

    /**
     * Tests put of a blob with blob size 0.
     */
    @Test
    public void testZeroSizedBlobPutSuccess() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(0));
        submitPutsAndAssertSuccess(true);
    }

    /**
     * Tests a failure scenario where connects to server nodes throw exceptions.
     */
    @Test
    public void testFailureOnAllConnects() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * 5)));
        mockSelectorState.set(MockSelectorState.ThrowExceptionOnConnect);
        Exception expectedException = new RouterException("", OperationTimedOut);
        submitPutsAndAssertFailure(expectedException, false, true, true);
        // this should not close the router.
        Assert.assertTrue("Router should not be closed", router.isOpen());
        assertCloseCleanup();
    }

    /**
     * Tests a failure scenario where all sends to server nodes result in disconnections.
     */
    @Test
    public void testFailureOnAllSends() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * 5)));
        mockSelectorState.set(MockSelectorState.DisconnectOnSend);
        Exception expectedException = new RouterException("", OperationTimedOut);
        submitPutsAndAssertFailure(expectedException, false, false, true);
        // this should not have closed the router.
        Assert.assertTrue("Router should not be closed", router.isOpen());
        assertCloseCleanup();
    }

    /**
     * Tests a failure scenario where selector poll throws an exception when there is anything to send.
     */
    @Test
    public void testFailureOnAllPollThatSends() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * 5)));
        mockSelectorState.set(MockSelectorState.ThrowExceptionOnSend);
        // In the case of an error in poll, the router gets closed, and all the ongoing operations are finished off with
        // RouterClosed error.
        Exception expectedException = new RouterException("", OperationTimedOut);
        submitPutsAndAssertFailure(expectedException, true, true, true);
        // router should get closed automatically
        Assert.assertFalse("Router should be closed", router.isOpen());
        Assert.assertEquals("No ChunkFiller threads should be running after the router is closed", 0, TestUtils.numThreadsByThisName("ChunkFillerThread"));
        Assert.assertEquals("No RequestResponseHandler threads should be running after the router is closed", 0, TestUtils.numThreadsByThisName("RequestResponseHandlerThread"));
        Assert.assertEquals("All operations should have completed", 0, router.getOperationsCount());
    }

    /**
     * Tests a failure scenario where connects to server nodes throw exceptions.
     */
    @Test
    public void testFailureOnKMS() throws Exception {
        if (testEncryption) {
            setupEncryptionCast(new VerifiableProperties(new Properties()));
            instantiateEncryptionCast = false;
            kms.exceptionToThrow.set(PutManagerTest.GSE);
            // simple blob
            requestAndResultsList.clear();
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((random.nextInt(chunkSize)) + 1)));
            Exception expectedException = new RouterException("", PutManagerTest.GSE, UnexpectedInternalError);
            submitPutsAndAssertFailure(expectedException, false, true, true);
            // this should not close the router.
            Assert.assertTrue("Router should not be closed", router.isOpen());
            assertCloseCleanup();
            setupEncryptionCast(new VerifiableProperties(new Properties()));
            instantiateEncryptionCast = false;
            kms.exceptionToThrow.set(PutManagerTest.GSE);
            // composite blob
            requestAndResultsList.clear();
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * (random.nextInt(10)))));
            submitPutsAndAssertFailure(expectedException, false, true, true);
            // this should not close the router.
            Assert.assertTrue("Router should not be closed", router.isOpen());
            assertCloseCleanup();
        }
    }

    /**
     * Tests a failure scenario where connects to server nodes throw exceptions.
     */
    @Test
    public void testFailureOnCryptoService() throws Exception {
        if (testEncryption) {
            setupEncryptionCast(new VerifiableProperties(new Properties()));
            instantiateEncryptionCast = false;
            cryptoService.exceptionOnEncryption.set(PutManagerTest.GSE);
            // simple blob
            requestAndResultsList.clear();
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((random.nextInt(chunkSize)) + 1)));
            Exception expectedException = new RouterException("", PutManagerTest.GSE, UnexpectedInternalError);
            submitPutsAndAssertFailure(expectedException, false, true, true);
            // this should not close the router.
            Assert.assertTrue("Router should not be closed", router.isOpen());
            assertCloseCleanup();
            setupEncryptionCast(new VerifiableProperties(new Properties()));
            instantiateEncryptionCast = false;
            cryptoService.exceptionOnEncryption.set(PutManagerTest.GSE);
            // composite blob
            requestAndResultsList.clear();
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * (random.nextInt(10)))));
            submitPutsAndAssertFailure(expectedException, false, true, true);
            // this should not close the router.
            Assert.assertTrue("Router should not be closed", router.isOpen());
            assertCloseCleanup();
        }
    }

    /**
     * Tests multiple concurrent puts; and puts in succession on the same router.
     */
    @Test
    public void testConcurrentPutsSuccess() throws Exception {
        requestAndResultsList.clear();
        for (int i = 0; i < 5; i++) {
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) + ((random.nextInt(5)) * (random.nextInt(chunkSize))))));
        }
        submitPutsAndAssertSuccess(false);
        // do more puts on the same router (that is not closed).
        requestAndResultsList.clear();
        for (int i = 0; i < 5; i++) {
            requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) + ((random.nextInt(5)) * (random.nextInt(chunkSize))))));
        }
        instantiateNewRouterForPuts = false;
        submitPutsAndAssertSuccess(true);
    }

    /**
     * Test ensures failure when all server nodes encounter an error.
     */
    @Test
    public void testPutWithAllNodesFailure() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * 5)));
        List<DataNodeId> dataNodeIds = mockClusterMap.getDataNodeIds();
        for (DataNodeId dataNodeId : dataNodeIds) {
            String host = dataNodeId.getHostname();
            int port = dataNodeId.getPort();
            MockServer server = mockServerLayout.getMockServer(host, port);
            server.setServerErrorForAllRequests(Unknown_Error);
        }
        Exception expectedException = new RouterException("", AmbryUnavailable);
        submitPutsAndAssertFailure(expectedException, true, false, true);
    }

    /**
     * Test ensures success in the presence of a single node failure.
     */
    @Test
    public void testOneNodeFailurePutSuccess() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * 5)));
        List<DataNodeId> dataNodeIds = mockClusterMap.getDataNodeIds();
        // Now, fail every third node in every DC and ensure operation success.
        // Note 1: The assumption here is that there are 3 nodes per DC. However, cross DC is not applicable for puts.
        // Note 2: Although nodes are chosen for failure deterministically here, the order in which replicas are chosen
        // for puts is random, so the responses that fail could come in any order. What this and similar tests below
        // test is that as long as there are enough good responses, the operations should succeed,
        // and as long as that is not the case, operations should fail.
        // Note: The assumption is that there are 3 nodes per DC.
        for (int i = 0; i < (dataNodeIds.size()); i++) {
            if ((i % 3) == 0) {
                String host = dataNodeIds.get(i).getHostname();
                int port = dataNodeIds.get(i).getPort();
                MockServer server = mockServerLayout.getMockServer(host, port);
                server.setServerErrorForAllRequests(Unknown_Error);
            }
        }
        submitPutsAndAssertSuccess(true);
    }

    /**
     * Test ensures failure when two out of three nodes fail.
     */
    @Test
    public void testPutWithTwoNodesFailure() throws Exception {
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * 5)));
        List<DataNodeId> dataNodeIds = mockClusterMap.getDataNodeIds();
        // Now, fail every node except the third in every DC and ensure operation success.
        // Note: The assumption is that there are 3 nodes per DC.
        for (int i = 0; i < (dataNodeIds.size()); i++) {
            if ((i % 3) != 0) {
                String host = dataNodeIds.get(i).getHostname();
                int port = dataNodeIds.get(i).getPort();
                MockServer server = mockServerLayout.getMockServer(host, port);
                server.setServerErrorForAllRequests(Unknown_Error);
            }
        }
        Exception expectedException = new RouterException("", AmbryUnavailable);
        submitPutsAndAssertFailure(expectedException, true, false, false);
    }

    /**
     * Tests slipped puts scenario. That is, the first attempt at putting a chunk ends up in a failure,
     * but a second attempt is made by the PutManager which results in a success.
     */
    @Test
    public void testSlippedPutsSuccess() throws Exception {
        // choose a simple blob, one that will result in a single chunk. This is to ensure setting state properly
        // to test things correctly. Note that slipped puts concern a chunk and not an operation (that is,
        // every chunk is slipped put on its own), so testing for simple blobs is sufficient.
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(chunkSize));
        List<DataNodeId> dataNodeIds = mockClusterMap.getDataNodeIds();
        // Set the state of the mock servers so that they return an error for the first send issued,
        // but later ones succeed. With 3 nodes, for slipped puts, all partitions will come from the same nodes,
        // so we set the errors in such a way that the first request received by every node fails.
        // Note: The assumption is that there are 3 nodes per DC.
        List<ServerErrorCode> serverErrorList = new ArrayList<>();
        serverErrorList.add(Unknown_Error);
        serverErrorList.add(No_Error);
        for (DataNodeId dataNodeId : dataNodeIds) {
            MockServer server = mockServerLayout.getMockServer(dataNodeId.getHostname(), dataNodeId.getPort());
            server.setServerErrors(serverErrorList);
        }
        submitPutsAndAssertSuccess(true);
    }

    /**
     * Tests a case where some chunks succeed and a later chunk fails and ensures that the operation fails in
     * such a scenario.
     */
    @Test
    public void testLaterChunkFailure() throws Exception {
        // choose a blob with two chunks, first one will succeed and second will fail.
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(((chunkSize) * 2)));
        List<DataNodeId> dataNodeIds = mockClusterMap.getDataNodeIds();
        // Set the state of the mock servers so that they return success for the first send issued,
        // but later ones fail. With 3 nodes, all partitions will come from the same nodes,
        // so we set the errors in such a way that the first request received by every node succeeds and later ones fail.
        List<ServerErrorCode> serverErrorList = new ArrayList<>();
        serverErrorList.add(No_Error);
        for (int i = 0; i < 5; i++) {
            serverErrorList.add(Unknown_Error);
        }
        for (DataNodeId dataNodeId : dataNodeIds) {
            MockServer server = mockServerLayout.getMockServer(dataNodeId.getHostname(), dataNodeId.getPort());
            server.setServerErrors(serverErrorList);
        }
        Exception expectedException = new RouterException("", AmbryUnavailable);
        submitPutsAndAssertFailure(expectedException, true, false, true);
    }

    /**
     * Tests put of blobs with various sizes with channels that do not have all the data at once. This also tests cases
     * where zero-length buffers are given out by the channel.
     */
    @Test
    public void testDelayedChannelPutSuccess() throws Exception {
        router = getNonBlockingRouter();
        int[] blobSizes = new int[]{ 0// zero sized blob.
        , chunkSize// blob size is the same as chunk size.
        , ((chunkSize) + (random.nextInt(((chunkSize) - 1)))) + 1// over a chunk but less than 2 chunks.
        , (chunkSize) * (2 + (random.nextInt(10)))// blob size is a multiple of chunk size.
        , (((chunkSize) + (2 + (random.nextInt(10)))) + (random.nextInt(((chunkSize) - 1)))) + 1// multiple of a chunk and over.
         };
        for (int blobSize : blobSizes) {
            testDelayed(blobSize, false);
            testDelayed(blobSize, true);
        }
        assertSuccess();
        assertCloseCleanup();
    }

    /**
     * Test a put where the put channel encounters an exception and finishes prematurely. Ensures that the operation
     * completes and with failure.
     */
    @Test
    public void testBadChannelPutFailure() throws Exception {
        router = getNonBlockingRouter();
        int blobSize = ((chunkSize) * (random.nextInt(10))) + 1;
        PutManagerTest.RequestAndResult requestAndResult = new PutManagerTest.RequestAndResult(blobSize);
        requestAndResultsList.add(requestAndResult);
        MockReadableStreamChannel putChannel = new MockReadableStreamChannel(blobSize, false);
        FutureResult<String> future = ((FutureResult<String>) (router.putBlob(requestAndResult.putBlobProperties, requestAndResult.putUserMetadata, putChannel, requestAndResult.options, null)));
        ByteBuffer src = ByteBuffer.wrap(requestAndResult.putContent);
        // Make the channel act bad.
        putChannel.beBad();
        pushWithDelay(src, putChannel, blobSize, future);
        future.await(PutManagerTest.MAX_WAIT_MS, TimeUnit.MILLISECONDS);
        requestAndResult.result = future;
        Exception expectedException = new Exception("Channel encountered an error");
        assertFailure(expectedException, true);
        assertCloseCleanup();
    }

    /**
     * Test that the size in BlobProperties is ignored for puts, by attempting puts with varying values for size in
     * BlobProperties.
     */
    @Test
    public void testChannelSizeNotSizeInPropertiesPutSuccess() throws Exception {
        int[] actualBlobSizes = new int[]{ 0, (chunkSize) - 1, chunkSize, (chunkSize) + 1, (chunkSize) * 2, ((chunkSize) * 2) + 1 };
        for (int actualBlobSize : actualBlobSizes) {
            int[] sizesInProperties = new int[]{ actualBlobSize - 1, actualBlobSize + 1 };
            for (int sizeInProperties : sizesInProperties) {
                requestAndResultsList.clear();
                PutManagerTest.RequestAndResult requestAndResult = new PutManagerTest.RequestAndResult(0);
                // Change the actual content size.
                requestAndResult.putContent = new byte[actualBlobSize];
                requestAndResult.putBlobProperties = new BlobProperties(sizeInProperties, "serviceId", "memberId", "contentType", false, Utils.Infinite_Time, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), false, PutManagerTest.EXTERNAL_ASSET_TAG);
                random.nextBytes(requestAndResult.putContent);
                requestAndResultsList.add(requestAndResult);
                submitPutsAndAssertSuccess(true);
            }
        }
    }

    /**
     * Test to verify that the chunk filler goes to sleep when there are no active operations and is woken up when
     * operations become active.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testChunkFillerSleep() throws Exception {
        router = getNonBlockingRouter();
        // At this time there are no put operations, so the ChunkFillerThread will eventually go into WAITING state.
        Thread chunkFillerThread = TestUtils.getThreadByThisName("ChunkFillerThread");
        Assert.assertTrue("ChunkFillerThread should have gone to WAITING state as there are no active operations", waitForThreadState(chunkFillerThread, WAITING));
        int blobSize = (chunkSize) * 2;
        PutManagerTest.RequestAndResult requestAndResult = new PutManagerTest.RequestAndResult(blobSize);
        requestAndResultsList.add(requestAndResult);
        MockReadableStreamChannel putChannel = new MockReadableStreamChannel(blobSize, false);
        FutureResult<String> future = ((FutureResult<String>) (router.putBlob(requestAndResult.putBlobProperties, requestAndResult.putUserMetadata, putChannel, requestAndResult.options, null)));
        ByteBuffer src = ByteBuffer.wrap(requestAndResult.putContent);
        // There will be two chunks written to the underlying writable channel, and so two events will be fired.
        int writeSize = blobSize / 2;
        ByteBuffer buf = ByteBuffer.allocate(writeSize);
        src.get(buf.array());
        putChannel.write(buf);
        // The first write will wake up (or will have woken up) the ChunkFiller thread and it will not go to WAITING until
        // the operation is complete as an attempt to fill chunks will be done by the ChunkFiller in every iteration
        // until the operation is complete.
        Assert.assertTrue("ChunkFillerThread should have gone to RUNNABLE state as there is an active operation that is not yet complete", waitForThreadState(chunkFillerThread, RUNNABLE));
        buf.rewind();
        src.get(buf.array());
        putChannel.write(buf);
        // At this time all writes have finished, so the ChunkFiller thread will eventually go (or will have already gone)
        // to WAITING due to this write.
        Assert.assertTrue("ChunkFillerThread should have gone to WAITING state as the only active operation is now complete", waitForThreadState(chunkFillerThread, WAITING));
        Assert.assertTrue("Operation should not take too long to complete", future.await(PutManagerTest.MAX_WAIT_MS, TimeUnit.MILLISECONDS));
        requestAndResult.result = future;
        assertSuccess();
        assertCloseCleanup();
    }

    /**
     * Tests that the replication policy in the container is respected
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReplPolicyToPartitionClassMapping() throws Exception {
        Account refAccount = accountService.createAndAddRandomAccount();
        Map<Container, String> containerToPartClass = new HashMap<>();
        Iterator<Container> allContainers = refAccount.getAllContainers().iterator();
        Container container = allContainers.next();
        // container with null replication policy
        container = accountService.addReplicationPolicyToContainer(container, null);
        containerToPartClass.put(container, DEFAULT_PARTITION_CLASS);
        container = allContainers.next();
        // container with configured default replication policy
        container = accountService.addReplicationPolicyToContainer(container, DEFAULT_PARTITION_CLASS);
        containerToPartClass.put(container, DEFAULT_PARTITION_CLASS);
        container = allContainers.next();
        // container with a special replication policy
        container = accountService.addReplicationPolicyToContainer(container, SPECIAL_PARTITION_CLASS);
        containerToPartClass.put(container, SPECIAL_PARTITION_CLASS);
        // adding this to test the random account and container case (does not actually happen if coming from the frontend)
        containerToPartClass.put(null, DEFAULT_PARTITION_CLASS);
        Map<Integer, Integer> sizeToChunkCount = new HashMap<>();
        // simple
        sizeToChunkCount.put(((random.nextInt(chunkSize)) + 1), 1);
        int count = (random.nextInt(8)) + 3;
        // composite
        sizeToChunkCount.put(((chunkSize) * count), (count + 1));
        for (Map.Entry<Integer, Integer> sizeAndChunkCount : sizeToChunkCount.entrySet()) {
            for (Map.Entry<Container, String> containerAndPartClass : containerToPartClass.entrySet()) {
                requestAndResultsList.clear();
                requestAndResultsList.add(new PutManagerTest.RequestAndResult(sizeAndChunkCount.getKey(), containerAndPartClass.getKey(), DEFAULT, null));
                mockClusterMap.clearLastNRequestedPartitionClasses();
                submitPutsAndAssertSuccess(true);
                // since the puts are processed one at a time, it is fair to check the last partition class set
                checkLastRequestPartitionClasses(sizeAndChunkCount.getValue(), containerAndPartClass.getValue());
            }
        }
        // exception if there is no partition class that conforms to the replication policy
        String nonExistentClass = UtilsTest.getRandomString(3);
        accountService.addReplicationPolicyToContainer(container, nonExistentClass);
        requestAndResultsList.clear();
        requestAndResultsList.add(new PutManagerTest.RequestAndResult(chunkSize, container, DEFAULT, null));
        mockClusterMap.clearLastNRequestedPartitionClasses();
        submitPutsAndAssertFailure(new RouterException("", UnexpectedInternalError), true, false, false);
        // because of how the non-encrypted flow is, prepareForSending() may be called twice. So not checking for count
        checkLastRequestPartitionClasses((-1), nonExistentClass);
    }

    private class RequestAndResult {
        BlobProperties putBlobProperties;

        byte[] putUserMetadata;

        byte[] putContent;

        PutBlobOptions options;

        List<ChunkInfo> chunksToStitch;

        FutureResult<String> result;

        RequestAndResult(int blobSize) {
            this(blobSize, null, DEFAULT, null);
        }

        RequestAndResult(List<ChunkInfo> chunksToStitch) {
            this(0, null, DEFAULT, chunksToStitch);
        }

        RequestAndResult(int blobSize, Container container, PutBlobOptions options, List<ChunkInfo> chunksToStitch) {
            putBlobProperties = new BlobProperties((-1), "serviceId", "memberId", "contentType", false, Utils.Infinite_Time, (container == null ? Utils.getRandomShort(RANDOM) : container.getParentAccountId()), (container == null ? Utils.getRandomShort(RANDOM) : container.getId()), testEncryption, PutManagerTest.EXTERNAL_ASSET_TAG);
            putUserMetadata = new byte[10];
            random.nextBytes(putUserMetadata);
            putContent = new byte[blobSize];
            random.nextBytes(putContent);
            this.options = options;
            this.chunksToStitch = chunksToStitch;
            // future result set after the operation is complete.
        }
    }

    /**
     * A notification system for testing that keeps track of data from onBlobCreated calls.
     */
    private class TestNotificationSystem extends LoggingNotificationSystem {
        Map<String, List<PutManagerTest.BlobCreatedEvent>> blobCreatedEvents = new HashMap<>();

        @Override
        public void onBlobCreated(String blobId, BlobProperties blobProperties, Account account, Container container, NotificationBlobType notificationBlobType) {
            blobCreatedEvents.computeIfAbsent(blobId, ( k) -> new ArrayList<>()).add(new PutManagerTest.BlobCreatedEvent(blobProperties, notificationBlobType));
        }

        /**
         * Test that an onBlobCreated notification was generated as expected for this blob ID.
         *
         * @param blobId
         * 		The blob ID to look up a notification for.
         * @param expectedNotificationBlobType
         * 		the expected {@link NotificationBlobType}.
         * @param expectedBlobProperties
         * 		the expected {@link BlobProperties}.
         */
        private void verifyNotification(String blobId, NotificationBlobType expectedNotificationBlobType, BlobProperties expectedBlobProperties) {
            List<PutManagerTest.BlobCreatedEvent> events = blobCreatedEvents.get(blobId);
            Assert.assertTrue(("Wrong number of events for blobId: " + events), ((events != null) && ((events.size()) == 1)));
            PutManagerTest.BlobCreatedEvent event = events.get(0);
            Assert.assertEquals("NotificationBlobType does not match data in notification event.", expectedNotificationBlobType, event.notificationBlobType);
            Assert.assertTrue("BlobProperties does not match data in notification event.", RouterTestHelpers.arePersistedFieldsEquivalent(expectedBlobProperties, event.blobProperties));
            Assert.assertNull("Non-persistent filed should be null after serialization.", expectedBlobProperties.getExternalAssetTag());
            Assert.assertEquals("ExternalAssetTag in notification should match", PutManagerTest.EXTERNAL_ASSET_TAG, event.blobProperties.getExternalAssetTag());
            Assert.assertEquals("Expected blob size does not match data in notification event.", expectedBlobProperties.getBlobSize(), event.blobProperties.getBlobSize());
        }

        /**
         * Verify that onBlobCreated notifications were created for the data chunks of a failed put.
         */
        void verifyNotificationsForFailedPut() {
            Set<String> blobIdsVisited = new HashSet<>();
            for (MockServer mockServer : mockServerLayout.getMockServers()) {
                for (Map.Entry<String, StoredBlob> blobEntry : mockServer.getBlobs().entrySet()) {
                    if (blobIdsVisited.add(blobEntry.getKey())) {
                        StoredBlob blob = blobEntry.getValue();
                        verifyNotification(blobEntry.getKey(), DataChunk, blob.properties);
                    }
                }
            }
        }
    }

    private static class BlobCreatedEvent {
        BlobProperties blobProperties;

        NotificationBlobType notificationBlobType;

        BlobCreatedEvent(BlobProperties blobProperties, NotificationBlobType notificationBlobType) {
            this.blobProperties = blobProperties;
            this.notificationBlobType = notificationBlobType;
        }
    }
}

