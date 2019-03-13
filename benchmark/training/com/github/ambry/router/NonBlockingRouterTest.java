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


import GetBlobOptions.OperationType.BlobInfo;
import GetOption.Include_All;
import GetOption.Include_Deleted_Blobs;
import NonBlockingRouter.SHUTDOWN_WAIT_MS;
import NonBlockingRouter.currentOperationsCount;
import PutBlobOptions.DEFAULT;
import RequestOrResponseType.GetRequest;
import RouterErrorCode.AmbryUnavailable;
import RouterErrorCode.BlobDeleted;
import RouterErrorCode.BlobDoesNotExist;
import RouterErrorCode.BlobExpired;
import RouterErrorCode.InvalidBlobId;
import RouterErrorCode.OperationTimedOut;
import RouterErrorCode.RouterClosed;
import RouterErrorCode.UnexpectedInternalError;
import ServerErrorCode.Blob_Deleted;
import ServerErrorCode.Blob_Expired;
import ServerErrorCode.Blob_Not_Found;
import ServerErrorCode.Disk_Unavailable;
import ServerErrorCode.No_Error;
import ServerErrorCode.Replica_Unavailable;
import ServerErrorCode.Unknown_Error;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.DataNodeId;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.ReplicaId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.ByteBufferReadableStreamChannel;
import com.github.ambry.commons.LoggingNotificationSystem;
import com.github.ambry.commons.ResponseHandler;
import com.github.ambry.commons.ServerErrorCode;
import com.github.ambry.config.RouterConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.network.NetworkClient;
import com.github.ambry.network.RequestInfo;
import com.github.ambry.network.ResponseInfo;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static BackgroundDeleteRequest.SERVICE_ID_PREFIX;


/**
 * Class to test the {@link NonBlockingRouter}
 */
@RunWith(Parameterized.class)
public class NonBlockingRouterTest {
    private static final int MAX_PORTS_PLAIN_TEXT = 3;

    private static final int MAX_PORTS_SSL = 3;

    private static final int CHECKOUT_TIMEOUT_MS = 1000;

    private static final int REQUEST_TIMEOUT_MS = 1000;

    private static final int PUT_REQUEST_PARALLELISM = 3;

    private static final int PUT_SUCCESS_TARGET = 2;

    private static final int GET_REQUEST_PARALLELISM = 2;

    private static final int GET_SUCCESS_TARGET = 1;

    private static final int DELETE_REQUEST_PARALLELISM = 3;

    private static final int DELETE_SUCCESS_TARGET = 2;

    private static final int PUT_CONTENT_SIZE = 1000;

    private static final int USER_METADATA_SIZE = 10;

    private int maxPutChunkSize = NonBlockingRouterTest.PUT_CONTENT_SIZE;

    private final Random random = new Random();

    private NonBlockingRouter router;

    private NonBlockingRouterMetrics routerMetrics;

    private PutManager putManager;

    private GetManager getManager;

    private DeleteManager deleteManager;

    private AtomicReference<MockSelectorState> mockSelectorState = new AtomicReference<>(MockSelectorState.Good);

    private final MockTime mockTime;

    private final KeyManagementService kms;

    private final String singleKeyForKMS;

    private final CryptoService cryptoService;

    private final MockClusterMap mockClusterMap;

    private final boolean testEncryption;

    private final InMemAccountService accountService;

    private CryptoJobHandler cryptoJobHandler;

    // Request params;
    BlobProperties putBlobProperties;

    byte[] putUserMetadata;

    byte[] putContent;

    ReadableStreamChannel putChannel;

    /**
     * Initialize parameters common to all tests.
     *
     * @param testEncryption
     * 		{@code true} to test with encryption enabled. {@code false} otherwise
     * @throws Exception
     * 		
     */
    public NonBlockingRouterTest(boolean testEncryption) throws Exception {
        this.testEncryption = testEncryption;
        mockTime = new MockTime();
        mockClusterMap = new MockClusterMap();
        currentOperationsCount.set(0);
        VerifiableProperties vProps = new VerifiableProperties(new Properties());
        singleKeyForKMS = TestUtils.getRandomKey(SingleKeyManagementServiceTest.DEFAULT_KEY_SIZE_CHARS);
        kms = new SingleKeyManagementService(new com.github.ambry.config.KMSConfig(vProps), singleKeyForKMS);
        cryptoService = new GCMCryptoService(new com.github.ambry.config.CryptoServiceConfig(vProps));
        cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        accountService = new InMemAccountService(false, true);
    }

    /**
     * Test the {@link NonBlockingRouterFactory}
     */
    @Test
    public void testNonBlockingRouterFactory() throws Exception {
        Properties props = getNonBlockingRouterProperties("NotInClusterMap");
        VerifiableProperties verifiableProperties = new VerifiableProperties(props);
        try {
            router = ((NonBlockingRouter) (getRouter()));
            Assert.fail(("NonBlockingRouterFactory instantiation should have failed because the router datacenter is not in " + "the cluster map"));
        } catch (IllegalStateException e) {
        }
        props = getNonBlockingRouterProperties("DC1");
        verifiableProperties = new VerifiableProperties(props);
        router = ((NonBlockingRouter) (getRouter()));
        assertExpectedThreadCounts(2, 1);
        router.close();
        assertExpectedThreadCounts(0, 0);
    }

    /**
     * Test Router with a single scaling unit.
     */
    @Test
    public void testRouterBasic() throws Exception {
        setRouter();
        assertExpectedThreadCounts(2, 1);
        // More extensive test for puts present elsewhere - these statements are here just to exercise the flow within the
        // NonBlockingRouter class, and to ensure that operations submitted to a router eventually completes.
        List<String> blobIds = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            setOperationParams();
            String blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, DEFAULT).get();
            blobIds.add(blobId);
        }
        setOperationParams();
        String stitchedBlobId = router.stitchBlob(putBlobProperties, putUserMetadata, blobIds.stream().map(( blobId) -> new ChunkInfo(blobId, NonBlockingRouterTest.PUT_CONTENT_SIZE, Utils.Infinite_Time)).collect(Collectors.toList())).get();
        blobIds.add(stitchedBlobId);
        for (String blobId : blobIds) {
            router.getBlob(blobId, new GetBlobOptionsBuilder().build()).get();
            router.updateBlobTtl(blobId, null, Infinite_Time);
            router.getBlob(blobId, new GetBlobOptionsBuilder().build()).get();
            router.getBlob(blobId, new GetBlobOptionsBuilder().operationType(BlobInfo).build()).get();
            router.deleteBlob(blobId, null).get();
            try {
                router.getBlob(blobId, new GetBlobOptionsBuilder().build()).get();
            } catch (ExecutionException e) {
                RouterException r = ((RouterException) (e.getCause()));
                Assert.assertEquals("BlobDeleted error is expected", BlobDeleted, r.getErrorCode());
            }
            router.getBlob(blobId, new GetBlobOptionsBuilder().getOption(Include_Deleted_Blobs).build()).get();
            router.getBlob(blobId, new GetBlobOptionsBuilder().getOption(Include_All).build()).get();
        }
        router.close();
        assertExpectedThreadCounts(0, 0);
        // submission after closing should return a future that is already done.
        assertClosed();
    }

    /**
     * Test behavior with various null inputs to router methods.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNullArguments() throws Exception {
        setRouter();
        assertExpectedThreadCounts(2, 1);
        setOperationParams();
        try {
            router.getBlob(null, new GetBlobOptionsBuilder().build());
            Assert.fail("null blobId should have resulted in IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            router.getBlob("", null);
            Assert.fail("null options should have resulted in IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            router.putBlob(putBlobProperties, putUserMetadata, null, new PutBlobOptionsBuilder().build());
            Assert.fail("null channel should have resulted in IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            router.putBlob(null, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build());
            Assert.fail("null blobProperties should have resulted in IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            router.deleteBlob(null, null);
            Assert.fail("null blobId should have resulted in IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            router.updateBlobTtl(null, null, Infinite_Time);
            Assert.fail("null blobId should have resulted in IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        // null user metadata should work.
        router.putBlob(putBlobProperties, null, putChannel, new PutBlobOptionsBuilder().build()).get();
        router.close();
        assertExpectedThreadCounts(0, 0);
        // submission after closing should return a future that is already done.
        assertClosed();
    }

    /**
     * Test router put operation in a scenario where there are no partitions available.
     */
    @Test
    public void testRouterPartitionsUnavailable() throws Exception {
        setRouter();
        setOperationParams();
        mockClusterMap.markAllPartitionsUnavailable();
        try {
            router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
            Assert.fail("Put should have failed if there are no partitions");
        } catch (Exception e) {
            RouterException r = ((RouterException) (e.getCause()));
            Assert.assertEquals("Should have received AmbryUnavailable error", AmbryUnavailable, r.getErrorCode());
        }
        router.close();
        assertExpectedThreadCounts(0, 0);
        assertClosed();
    }

    /**
     * Test router put operation in a scenario where there are partitions, but none in the local DC.
     * This should not ideally happen unless there is a bad config, but the router should be resilient and
     * just error out these operations.
     */
    @Test
    public void testRouterNoPartitionInLocalDC() throws Exception {
        // set the local DC to invalid, so that for puts, no partitions are available locally.
        Properties props = getNonBlockingRouterProperties("invalidDC");
        setRouter(props, new MockServerLayout(mockClusterMap), new LoggingNotificationSystem());
        setOperationParams();
        try {
            router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
            Assert.fail("Put should have failed if there are no partitions");
        } catch (Exception e) {
            RouterException r = ((RouterException) (e.getCause()));
            Assert.assertEquals(UnexpectedInternalError, r.getErrorCode());
        }
        router.close();
        assertExpectedThreadCounts(0, 0);
        assertClosed();
    }

    /**
     * Test RequestResponseHandler thread exit flow. If the RequestResponseHandlerThread exits on its own (due to a
     * Throwable), then the router gets closed immediately along with the completion of all the operations.
     */
    @Test
    public void testRequestResponseHandlerThreadExitFlow() throws Exception {
        Properties props = getNonBlockingRouterProperties("DC1");
        VerifiableProperties verifiableProperties = new VerifiableProperties(props);
        MockClusterMap mockClusterMap = new MockClusterMap();
        MockTime mockTime = new MockTime();
        router = new NonBlockingRouter(new RouterConfig(verifiableProperties), new NonBlockingRouterMetrics(mockClusterMap), new MockNetworkClientFactory(verifiableProperties, mockSelectorState, NonBlockingRouterTest.MAX_PORTS_PLAIN_TEXT, NonBlockingRouterTest.MAX_PORTS_SSL, NonBlockingRouterTest.CHECKOUT_TIMEOUT_MS, new MockServerLayout(mockClusterMap), mockTime), new LoggingNotificationSystem(), mockClusterMap, kms, cryptoService, cryptoJobHandler, accountService, mockTime, MockClusterMap.DEFAULT_PARTITION_CLASS);
        assertExpectedThreadCounts(2, 1);
        setOperationParams();
        mockSelectorState.set(MockSelectorState.ThrowExceptionOnAllPoll);
        Future future = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build());
        try {
            while (!(future.isDone())) {
                mockTime.sleep(1000);
                Thread.yield();
            } 
            future.get();
            Assert.fail("The operation should have failed");
        } catch (ExecutionException e) {
            Assert.assertEquals(OperationTimedOut, getErrorCode());
        }
        setOperationParams();
        mockSelectorState.set(MockSelectorState.ThrowThrowableOnSend);
        future = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build());
        Thread requestResponseHandlerThreadRegular = TestUtils.getThreadByThisName("RequestResponseHandlerThread-0");
        Thread requestResponseHandlerThreadBackground = TestUtils.getThreadByThisName("RequestResponseHandlerThread-backgroundDeleter");
        if (requestResponseHandlerThreadRegular != null) {
            requestResponseHandlerThreadRegular.join(SHUTDOWN_WAIT_MS);
        }
        if (requestResponseHandlerThreadBackground != null) {
            requestResponseHandlerThreadBackground.join(SHUTDOWN_WAIT_MS);
        }
        try {
            future.get();
            Assert.fail("The operation should have failed");
        } catch (ExecutionException e) {
            Assert.assertEquals(RouterClosed, getErrorCode());
        }
        assertClosed();
        // Ensure that both operations failed and with the right exceptions.
        Assert.assertEquals("No ChunkFiller Thread should be running after the router is closed", 0, TestUtils.numThreadsByThisName("ChunkFillerThread"));
        Assert.assertEquals("No RequestResponseHandler should be running after the router is closed", 0, TestUtils.numThreadsByThisName("RequestResponseHandlerThread"));
        Assert.assertEquals("All operations should have completed", 0, router.getOperationsCount());
    }

    /**
     * Test that if a composite blob put fails, the successfully put data chunks are deleted.
     */
    @Test
    public void testUnsuccessfulPutDataChunkDelete() throws Exception {
        // Ensure there are 4 chunks.
        maxPutChunkSize = (NonBlockingRouterTest.PUT_CONTENT_SIZE) / 4;
        Properties props = getNonBlockingRouterProperties("DC1");
        VerifiableProperties verifiableProperties = new VerifiableProperties(props);
        MockClusterMap mockClusterMap = new MockClusterMap();
        MockTime mockTime = new MockTime();
        MockServerLayout mockServerLayout = new MockServerLayout(mockClusterMap);
        // Since this test wants to ensure that successfully put data chunks are deleted when the overall put operation
        // fails, it uses a notification system to track the deletions.
        final CountDownLatch deletesDoneLatch = new CountDownLatch(2);
        final Map<String, String> blobsThatAreDeleted = new HashMap<>();
        LoggingNotificationSystem deleteTrackingNotificationSystem = new LoggingNotificationSystem() {
            @Override
            public void onBlobDeleted(String blobId, String serviceId, Account account, Container container) {
                blobsThatAreDeleted.put(blobId, serviceId);
                deletesDoneLatch.countDown();
            }
        };
        router = new NonBlockingRouter(new RouterConfig(verifiableProperties), new NonBlockingRouterMetrics(mockClusterMap), new MockNetworkClientFactory(verifiableProperties, mockSelectorState, NonBlockingRouterTest.MAX_PORTS_PLAIN_TEXT, NonBlockingRouterTest.MAX_PORTS_SSL, NonBlockingRouterTest.CHECKOUT_TIMEOUT_MS, mockServerLayout, mockTime), deleteTrackingNotificationSystem, mockClusterMap, kms, cryptoService, cryptoJobHandler, accountService, mockTime, MockClusterMap.DEFAULT_PARTITION_CLASS);
        setOperationParams();
        List<DataNodeId> dataNodeIds = mockClusterMap.getDataNodeIds();
        List<ServerErrorCode> serverErrorList = new ArrayList<>();
        // There are 4 chunks for this blob.
        // All put operations make one request to each local server as there are 3 servers overall in the local DC.
        // Set the state of the mock servers so that they return success for the first 2 requests in order to succeed
        // the first two chunks.
        serverErrorList.add(No_Error);
        serverErrorList.add(No_Error);
        // fail requests for third and fourth data chunks including the slipped put attempts:
        serverErrorList.add(Unknown_Error);
        serverErrorList.add(Unknown_Error);
        serverErrorList.add(Unknown_Error);
        serverErrorList.add(Unknown_Error);
        // all subsequent requests (no more puts, but there will be deletes) will succeed.
        for (DataNodeId dataNodeId : dataNodeIds) {
            MockServer server = mockServerLayout.getMockServer(dataNodeId.getHostname(), dataNodeId.getPort());
            server.setServerErrors(serverErrorList);
        }
        // Submit the put operation and wait for it to fail.
        try {
            router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
        } catch (ExecutionException e) {
            Assert.assertEquals(AmbryUnavailable, getErrorCode());
        }
        // Now, wait until the deletes of the successfully put blobs are complete.
        Assert.assertTrue(("Deletes should not take longer than " + (RouterTestHelpers.AWAIT_TIMEOUT_MS)), deletesDoneLatch.await(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS));
        for (Map.Entry<String, String> blobIdAndServiceId : blobsThatAreDeleted.entrySet()) {
            Assert.assertEquals("Unexpected service ID for deleted blob", ((SERVICE_ID_PREFIX) + (putBlobProperties.getServiceId())), blobIdAndServiceId.getValue());
        }
        router.close();
        assertClosed();
        Assert.assertEquals("All operations should have completed", 0, router.getOperationsCount());
    }

    /**
     * Test that if a composite blob is deleted, the data chunks are eventually deleted. Also check the service IDs used
     * for delete operations.
     */
    @Test
    public void testCompositeBlobDataChunksDelete() throws Exception {
        // Ensure there are 4 chunks.
        maxPutChunkSize = (NonBlockingRouterTest.PUT_CONTENT_SIZE) / 4;
        Properties props = getNonBlockingRouterProperties("DC1");
        VerifiableProperties verifiableProperties = new VerifiableProperties(props);
        RouterConfig routerConfig = new RouterConfig(verifiableProperties);
        MockClusterMap mockClusterMap = new MockClusterMap();
        MockTime mockTime = new MockTime();
        MockServerLayout mockServerLayout = new MockServerLayout(mockClusterMap);
        // metadata blob + data chunks.
        final AtomicReference<CountDownLatch> deletesDoneLatch = new AtomicReference<>();
        final Map<String, String> blobsThatAreDeleted = new HashMap<>();
        LoggingNotificationSystem deleteTrackingNotificationSystem = new LoggingNotificationSystem() {
            @Override
            public void onBlobDeleted(String blobId, String serviceId, Account account, Container container) {
                blobsThatAreDeleted.put(blobId, serviceId);
                deletesDoneLatch.get().countDown();
            }
        };
        NonBlockingRouterMetrics localMetrics = new NonBlockingRouterMetrics(mockClusterMap);
        router = new NonBlockingRouter(routerConfig, localMetrics, new MockNetworkClientFactory(verifiableProperties, mockSelectorState, NonBlockingRouterTest.MAX_PORTS_PLAIN_TEXT, NonBlockingRouterTest.MAX_PORTS_SSL, NonBlockingRouterTest.CHECKOUT_TIMEOUT_MS, mockServerLayout, mockTime), deleteTrackingNotificationSystem, mockClusterMap, kms, cryptoService, cryptoJobHandler, accountService, mockTime, MockClusterMap.DEFAULT_PARTITION_CLASS);
        setOperationParams();
        String blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
        String deleteServiceId = "delete-service";
        Set<String> blobsToBeDeleted = getBlobsInServers(mockServerLayout);
        int getRequestCount = mockServerLayout.getCount(GetRequest);
        // The second iteration is to test the case where the blob was already deleted.
        // The third iteration is to test the case where the blob has expired.
        for (int i = 0; i < 3; i++) {
            if (i == 2) {
                // Create a clean cluster and put another blob that immediate expires.
                setOperationParams();
                putBlobProperties = new BlobProperties((-1), "serviceId", "memberId", "contentType", false, 0, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), false, null);
                blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
                Set<String> allBlobsInServer = getBlobsInServers(mockServerLayout);
                allBlobsInServer.removeAll(blobsToBeDeleted);
                blobsToBeDeleted = allBlobsInServer;
            }
            blobsThatAreDeleted.clear();
            deletesDoneLatch.set(new CountDownLatch(5));
            router.deleteBlob(blobId, deleteServiceId, null).get();
            Assert.assertTrue(("Deletes should not take longer than " + (RouterTestHelpers.AWAIT_TIMEOUT_MS)), deletesDoneLatch.get().await(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS));
            Assert.assertTrue("All blobs in server are deleted", blobsThatAreDeleted.keySet().containsAll(blobsToBeDeleted));
            Assert.assertTrue("Only blobs in server are deleted", blobsToBeDeleted.containsAll(blobsThatAreDeleted.keySet()));
            for (Map.Entry<String, String> blobIdAndServiceId : blobsThatAreDeleted.entrySet()) {
                String expectedServiceId = (blobIdAndServiceId.getKey().equals(blobId)) ? deleteServiceId : (SERVICE_ID_PREFIX) + deleteServiceId;
                Assert.assertEquals("Unexpected service ID for deleted blob", expectedServiceId, blobIdAndServiceId.getValue());
            }
            // For 1 chunk deletion attempt, 1 background operation for Get is initiated which results in 2 Get Requests at
            // the servers.
            getRequestCount += 2;
            Assert.assertEquals("Only one attempt of chunk deletion should have been done", getRequestCount, mockServerLayout.getCount(GetRequest));
        }
        deletesDoneLatch.set(new CountDownLatch(5));
        router.deleteBlob(blobId, null, null).get();
        Assert.assertTrue(("Deletes should not take longer than " + (RouterTestHelpers.AWAIT_TIMEOUT_MS)), deletesDoneLatch.get().await(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertEquals("Get should NOT have been skipped", 0, localMetrics.skippedGetBlobCount.getCount());
        router.close();
        assertClosed();
        Assert.assertEquals("All operations should have completed", 0, router.getOperationsCount());
    }

    /**
     * Test to ensure that for simple blob deletions, no additional background delete operations
     * are initiated.
     */
    @Test
    public void testSimpleBlobDelete() throws Exception {
        // Ensure there are 4 chunks.
        maxPutChunkSize = NonBlockingRouterTest.PUT_CONTENT_SIZE;
        Properties props = getNonBlockingRouterProperties("DC1");
        VerifiableProperties verifiableProperties = new VerifiableProperties(props);
        MockClusterMap mockClusterMap = new MockClusterMap();
        MockTime mockTime = new MockTime();
        MockServerLayout mockServerLayout = new MockServerLayout(mockClusterMap);
        String deleteServiceId = "delete-service";
        // metadata blob + data chunks.
        final AtomicInteger deletesInitiated = new AtomicInteger();
        final AtomicReference<String> receivedDeleteServiceId = new AtomicReference<>();
        LoggingNotificationSystem deleteTrackingNotificationSystem = new LoggingNotificationSystem() {
            @Override
            public void onBlobDeleted(String blobId, String serviceId, Account account, Container container) {
                deletesInitiated.incrementAndGet();
                receivedDeleteServiceId.set(serviceId);
            }
        };
        NonBlockingRouterMetrics localMetrics = new NonBlockingRouterMetrics(mockClusterMap);
        router = new NonBlockingRouter(new RouterConfig(verifiableProperties), localMetrics, new MockNetworkClientFactory(verifiableProperties, mockSelectorState, NonBlockingRouterTest.MAX_PORTS_PLAIN_TEXT, NonBlockingRouterTest.MAX_PORTS_SSL, NonBlockingRouterTest.CHECKOUT_TIMEOUT_MS, mockServerLayout, mockTime), deleteTrackingNotificationSystem, mockClusterMap, kms, cryptoService, cryptoJobHandler, accountService, mockTime, MockClusterMap.DEFAULT_PARTITION_CLASS);
        setOperationParams();
        String blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
        router.deleteBlob(blobId, deleteServiceId, null).get();
        long waitStart = SystemTime.getInstance().milliseconds();
        while (((router.getBackgroundOperationsCount()) != 0) && ((SystemTime.getInstance().milliseconds()) < (waitStart + (RouterTestHelpers.AWAIT_TIMEOUT_MS)))) {
            Thread.sleep(1000);
        } 
        Assert.assertEquals("All background operations should be complete ", 0, router.getBackgroundOperationsCount());
        Assert.assertEquals("Only the original blob deletion should have been initiated", 1, deletesInitiated.get());
        Assert.assertEquals("The delete service ID should match the expected value", deleteServiceId, receivedDeleteServiceId.get());
        Assert.assertEquals("Get should have been skipped", 1, localMetrics.skippedGetBlobCount.getCount());
        router.close();
        assertClosed();
        Assert.assertEquals("All operations should have completed", 0, router.getOperationsCount());
    }

    /**
     * Tests basic TTL update for simple (one chunk) blobs
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSimpleBlobTtlUpdate() throws Exception {
        doTtlUpdateTest(1);
    }

    /**
     * Tests basic TTL update for composite (multiple chunk) blobs
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCompositeBlobTtlUpdate() throws Exception {
        doTtlUpdateTest(4);
    }

    /**
     * Test that stitched blobs are usable by the other router methods.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStitchGetUpdateDelete() throws Exception {
        AtomicReference<CountDownLatch> deletesDoneLatch = new AtomicReference<>();
        Set<String> deletedBlobs = ConcurrentHashMap.newKeySet();
        LoggingNotificationSystem deleteTrackingNotificationSystem = new LoggingNotificationSystem() {
            @Override
            public void onBlobDeleted(String blobId, String serviceId, Account account, Container container) {
                deletedBlobs.add(blobId);
                deletesDoneLatch.get().countDown();
            }
        };
        setRouter(getNonBlockingRouterProperties("DC1"), new MockServerLayout(mockClusterMap), deleteTrackingNotificationSystem);
        for (int intermediateChunkSize : new int[]{ maxPutChunkSize, (maxPutChunkSize) / 2 }) {
            for (LongStream chunkSizeStream : new LongStream[]{ RouterTestHelpers.buildValidChunkSizeStream((3 * intermediateChunkSize), intermediateChunkSize), RouterTestHelpers.buildValidChunkSizeStream((((3 * intermediateChunkSize) + (random.nextInt((intermediateChunkSize - 1)))) + 1), intermediateChunkSize) }) {
                // Upload data chunks
                ByteArrayOutputStream stitchedContentStream = new ByteArrayOutputStream();
                List<ChunkInfo> chunksToStitch = new ArrayList<>();
                PrimitiveIterator.OfLong chunkSizeIter = chunkSizeStream.iterator();
                while (chunkSizeIter.hasNext()) {
                    long chunkSize = chunkSizeIter.nextLong();
                    setOperationParams(((int) (chunkSize)), TTL_SECS);
                    String blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().chunkUpload(true).maxUploadSize(NonBlockingRouterTest.PUT_CONTENT_SIZE).build()).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                    long expirationTime = Utils.addSecondsToEpochTime(putBlobProperties.getCreationTimeInMs(), putBlobProperties.getTimeToLiveInSeconds());
                    chunksToStitch.add(new ChunkInfo(blobId, chunkSize, expirationTime));
                    stitchedContentStream.write(putContent);
                } 
                byte[] expectedContent = stitchedContentStream.toByteArray();
                // Stitch the chunks together
                setOperationParams(0, ((TTL_SECS) / 2));
                String stitchedBlobId = router.stitchBlob(putBlobProperties, putUserMetadata, chunksToStitch).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                // Fetch the stitched blob
                GetBlobResult getBlobResult = router.getBlob(stitchedBlobId, new GetBlobOptionsBuilder().build()).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                Assert.assertTrue("Blob properties must be the same", RouterTestHelpers.arePersistedFieldsEquivalent(putBlobProperties, getBlobResult.getBlobInfo().getBlobProperties()));
                Assert.assertEquals("Unexpected blob size", expectedContent.length, getBlobResult.getBlobInfo().getBlobProperties().getBlobSize());
                Assert.assertArrayEquals("User metadata must be the same", putUserMetadata, getBlobResult.getBlobInfo().getUserMetadata());
                RouterTestHelpers.compareContent(expectedContent, null, getBlobResult.getBlobDataChannel());
                // TtlUpdate the blob.
                router.updateBlobTtl(stitchedBlobId, "update-service", Infinite_Time).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                // Ensure that TTL was updated on the metadata blob and all data chunks
                Set<String> allBlobIds = chunksToStitch.stream().map(ChunkInfo::getBlobId).collect(Collectors.toSet());
                allBlobIds.add(stitchedBlobId);
                RouterTestHelpers.assertTtl(router, allBlobIds, Infinite_Time);
                // Delete and ensure that all stitched chunks are deleted
                deletedBlobs.clear();
                deletesDoneLatch.set(new CountDownLatch(((chunksToStitch.size()) + 1)));
                router.deleteBlob(stitchedBlobId, "delete-service").get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                TestUtils.awaitLatchOrTimeout(deletesDoneLatch.get(), RouterTestHelpers.AWAIT_TIMEOUT_MS);
                Assert.assertEquals("Metadata chunk and all data chunks should be deleted", allBlobIds, deletedBlobs);
            }
        }
        router.close();
        assertExpectedThreadCounts(0, 0);
    }

    /**
     * Test for most error cases involving TTL updates
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlobTtlUpdateErrors() throws Exception {
        String updateServiceId = "update-service";
        MockServerLayout layout = new MockServerLayout(mockClusterMap);
        setRouter(getNonBlockingRouterProperties("DC1"), layout, new LoggingNotificationSystem());
        setOperationParams();
        String blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Map<ServerErrorCode, RouterErrorCode> testsAndExpected = new HashMap<>();
        testsAndExpected.put(Blob_Not_Found, BlobDoesNotExist);
        testsAndExpected.put(Blob_Deleted, BlobDeleted);
        testsAndExpected.put(Blob_Expired, BlobExpired);
        testsAndExpected.put(Disk_Unavailable, AmbryUnavailable);
        testsAndExpected.put(Replica_Unavailable, AmbryUnavailable);
        testsAndExpected.put(Unknown_Error, UnexpectedInternalError);
        for (Map.Entry<ServerErrorCode, RouterErrorCode> testAndExpected : testsAndExpected.entrySet()) {
            layout.getMockServers().forEach(( mockServer) -> mockServer.setServerErrorForAllRequests(testAndExpected.getKey()));
            RouterTestHelpers.TestCallback<Void> testCallback = new RouterTestHelpers.TestCallback<>();
            Future<Void> future = router.updateBlobTtl(blobId, updateServiceId, Infinite_Time, testCallback);
            RouterTestHelpers.assertFailureAndCheckErrorCode(future, testCallback, testAndExpected.getValue());
        }
        layout.getMockServers().forEach(( mockServer) -> mockServer.setServerErrorForAllRequests(null));
        // bad blob id
        RouterTestHelpers.TestCallback<Void> testCallback = new RouterTestHelpers.TestCallback<>();
        Future<Void> future = router.updateBlobTtl("bad-blob-id", updateServiceId, Infinite_Time, testCallback);
        RouterTestHelpers.assertFailureAndCheckErrorCode(future, testCallback, InvalidBlobId);
        router.close();
    }

    /**
     * Test that a bad user defined callback will not crash the router or the manager.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBadCallbackForUpdateTtl() throws Exception {
        MockServerLayout serverLayout = new MockServerLayout(mockClusterMap);
        setRouter(getNonBlockingRouterProperties("DC1"), serverLayout, new LoggingNotificationSystem());
        setOperationParams();
        String blobId = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        putChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(putContent));
        String blobIdCheck = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        RouterTestHelpers.testWithErrorCodes(Collections.singletonMap(No_Error, 9), serverLayout, null, ( expectedError) -> {
            final CountDownLatch callbackCalled = new CountDownLatch(1);
            router.updateBlobTtl(blobId, null, Utils.Infinite_Time, ( result, exception) -> {
                callbackCalled.countDown();
                throw new RuntimeException("Throwing an exception in the user callback");
            }).get(AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertTrue("Callback not called.", callbackCalled.await(10, TimeUnit.MILLISECONDS));
            assertEquals("All operations should be finished.", 0, router.getOperationsCount());
            assertTrue("Router should not be closed", router.isOpen());
            assertTtl(router, Collections.singleton(blobId), Utils.Infinite_Time);
            // Test that TtlUpdateManager is still functional
            router.updateBlobTtl(blobIdCheck, null, Utils.Infinite_Time).get(AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertTtl(router, Collections.singleton(blobIdCheck), Utils.Infinite_Time);
        });
        router.close();
    }

    /**
     * Test that multiple scaling units can be instantiated, exercised and closed.
     */
    @Test
    public void testMultipleScalingUnit() throws Exception {
        final int SCALING_UNITS = 3;
        Properties props = getNonBlockingRouterProperties("DC1");
        props.setProperty("router.scaling.unit.count", Integer.toString(SCALING_UNITS));
        setRouter(props, new MockServerLayout(mockClusterMap), new LoggingNotificationSystem());
        assertExpectedThreadCounts((SCALING_UNITS + 1), SCALING_UNITS);
        // Submit a few jobs so that all the scaling units get exercised.
        for (int i = 0; i < (SCALING_UNITS * 10); i++) {
            setOperationParams();
            router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
        }
        router.close();
        assertExpectedThreadCounts(0, 0);
        // submission after closing should return a future that is already done.
        setOperationParams();
        assertClosed();
    }

    /**
     * Response handling related tests for all operation managers.
     */
    @Test
    public void testResponseHandling() throws Exception {
        Properties props = getNonBlockingRouterProperties("DC1");
        VerifiableProperties verifiableProperties = new VerifiableProperties(props);
        setOperationParams();
        final List<ReplicaId> failedReplicaIds = new ArrayList<>();
        final AtomicInteger successfulResponseCount = new AtomicInteger(0);
        final AtomicBoolean invalidResponse = new AtomicBoolean(false);
        ResponseHandler mockResponseHandler = new ResponseHandler(mockClusterMap) {
            @Override
            public void onEvent(ReplicaId replicaId, Object e) {
                if (e instanceof ServerErrorCode) {
                    if (e == (ServerErrorCode.No_Error)) {
                        successfulResponseCount.incrementAndGet();
                    } else {
                        invalidResponse.set(true);
                    }
                } else {
                    failedReplicaIds.add(replicaId);
                }
            }
        };
        // Instantiate a router just to put a blob successfully.
        MockServerLayout mockServerLayout = new MockServerLayout(mockClusterMap);
        setRouter(props, mockServerLayout, new LoggingNotificationSystem());
        setOperationParams();
        // More extensive test for puts present elsewhere - these statements are here just to exercise the flow within the
        // NonBlockingRouter class, and to ensure that operations submitted to a router eventually completes.
        String blobIdStr = router.putBlob(putBlobProperties, putUserMetadata, putChannel, new PutBlobOptionsBuilder().build()).get();
        BlobId blobId = RouterUtils.getBlobIdFromString(blobIdStr, mockClusterMap);
        router.close();
        for (MockServer mockServer : mockServerLayout.getMockServers()) {
            mockServer.setServerErrorForAllRequests(No_Error);
        }
        NetworkClient networkClient = new MockNetworkClientFactory(verifiableProperties, mockSelectorState, NonBlockingRouterTest.MAX_PORTS_PLAIN_TEXT, NonBlockingRouterTest.MAX_PORTS_SSL, NonBlockingRouterTest.CHECKOUT_TIMEOUT_MS, mockServerLayout, mockTime).getNetworkClient();
        cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        KeyManagementService localKMS = new MockKeyManagementService(new com.github.ambry.config.KMSConfig(verifiableProperties), singleKeyForKMS);
        putManager = new PutManager(mockClusterMap, mockResponseHandler, new LoggingNotificationSystem(), new RouterConfig(verifiableProperties), new NonBlockingRouterMetrics(mockClusterMap), new RouterCallback(networkClient, new ArrayList()), "0", localKMS, cryptoService, cryptoJobHandler, accountService, mockTime, MockClusterMap.DEFAULT_PARTITION_CLASS);
        NonBlockingRouterTest.OperationHelper opHelper = new NonBlockingRouterTest.OperationHelper(NonBlockingRouterTest.OperationType.PUT);
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, null, successfulResponseCount, invalidResponse, (-1));
        // Test that if a failed response comes before the operation is completed, failure detector is notified.
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, null, successfulResponseCount, invalidResponse, 0);
        // Test that if a failed response comes after the operation is completed, failure detector is notified.
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, null, successfulResponseCount, invalidResponse, ((NonBlockingRouterTest.PUT_REQUEST_PARALLELISM) - 1));
        testNoResponseNoNotification(opHelper, failedReplicaIds, null, successfulResponseCount, invalidResponse);
        testResponseDeserializationError(opHelper, networkClient, null);
        opHelper = new NonBlockingRouterTest.OperationHelper(NonBlockingRouterTest.OperationType.GET);
        getManager = new GetManager(mockClusterMap, mockResponseHandler, new RouterConfig(verifiableProperties), new NonBlockingRouterMetrics(mockClusterMap), new RouterCallback(networkClient, new ArrayList<BackgroundDeleteRequest>()), localKMS, cryptoService, cryptoJobHandler, mockTime);
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, blobId, successfulResponseCount, invalidResponse, (-1));
        // Test that if a failed response comes before the operation is completed, failure detector is notified.
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, blobId, successfulResponseCount, invalidResponse, 0);
        // Test that if a failed response comes after the operation is completed, failure detector is notified.
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, blobId, successfulResponseCount, invalidResponse, ((NonBlockingRouterTest.GET_REQUEST_PARALLELISM) - 1));
        testNoResponseNoNotification(opHelper, failedReplicaIds, blobId, successfulResponseCount, invalidResponse);
        testResponseDeserializationError(opHelper, networkClient, blobId);
        opHelper = new NonBlockingRouterTest.OperationHelper(NonBlockingRouterTest.OperationType.DELETE);
        deleteManager = new DeleteManager(mockClusterMap, mockResponseHandler, accountService, new LoggingNotificationSystem(), new RouterConfig(verifiableProperties), new NonBlockingRouterMetrics(mockClusterMap), new RouterCallback(null, new ArrayList<BackgroundDeleteRequest>()), mockTime);
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, blobId, successfulResponseCount, invalidResponse, (-1));
        // Test that if a failed response comes before the operation is completed, failure detector is notified.
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, blobId, successfulResponseCount, invalidResponse, 0);
        // Test that if a failed response comes after the operation is completed, failure detector is notified.
        testFailureDetectorNotification(opHelper, networkClient, failedReplicaIds, blobId, successfulResponseCount, invalidResponse, ((NonBlockingRouterTest.DELETE_REQUEST_PARALLELISM) - 1));
        testNoResponseNoNotification(opHelper, failedReplicaIds, blobId, successfulResponseCount, invalidResponse);
        testResponseDeserializationError(opHelper, networkClient, blobId);
        putManager.close();
        getManager.close();
        deleteManager.close();
    }

    /**
     * Enum for the three operation types.
     */
    private enum OperationType {

        PUT,
        GET,
        DELETE;}

    /**
     * A helper class to abstract away the details about specific operation manager.
     */
    private class OperationHelper {
        final NonBlockingRouterTest.OperationType opType;

        int requestParallelism = 0;

        /**
         * Construct an OperationHelper object with the associated type.
         *
         * @param opType
         * 		the type of operation.
         */
        OperationHelper(NonBlockingRouterTest.OperationType opType) {
            this.opType = opType;
            switch (opType) {
                case PUT :
                    requestParallelism = NonBlockingRouterTest.PUT_REQUEST_PARALLELISM;
                    break;
                case GET :
                    requestParallelism = NonBlockingRouterTest.GET_REQUEST_PARALLELISM;
                    break;
                case DELETE :
                    requestParallelism = NonBlockingRouterTest.DELETE_REQUEST_PARALLELISM;
                    break;
            }
        }

        /**
         * Submit a put, get or delete operation based on the associated {@link OperationType} of this object.
         *
         * @param blobId
         * 		the blobId to get or delete. For puts, this is ignored.
         * @return the {@link FutureResult} associated with the submitted operation.
         * @throws RouterException
         * 		if the blobIdStr is invalid.
         */
        FutureResult submitOperation(BlobId blobId) throws RouterException {
            FutureResult futureResult = null;
            switch (opType) {
                case PUT :
                    futureResult = new FutureResult<String>();
                    ReadableStreamChannel putChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(putContent));
                    putManager.submitPutBlobOperation(putBlobProperties, putUserMetadata, putChannel, DEFAULT, futureResult, null);
                    break;
                case GET :
                    final FutureResult<GetBlobResultInternal> getFutureResult = new FutureResult();
                    getManager.submitGetBlobOperation(blobId.getID(), new GetBlobOptionsInternal(new GetBlobOptionsBuilder().operationType(BlobInfo).build(), false, routerMetrics.ageAtGet), getFutureResult::done);
                    futureResult = getFutureResult;
                    break;
                case DELETE :
                    futureResult = new FutureResult<Void>();
                    deleteManager.submitDeleteBlobOperation(blobId.getID(), null, futureResult, null);
                    break;
            }
            currentOperationsCount.incrementAndGet();
            return futureResult;
        }

        /**
         * Poll the associated operation manager.
         *
         * @param requestInfos
         * 		the list of {@link RequestInfo} to pass in the poll call.
         */
        void pollOpManager(List<RequestInfo> requestInfos) {
            switch (opType) {
                case PUT :
                    putManager.poll(requestInfos);
                    break;
                case GET :
                    getManager.poll(requestInfos);
                    break;
                case DELETE :
                    deleteManager.poll(requestInfos);
                    break;
            }
        }

        /**
         * Polls all managers at regular intervals until the operation is complete or timeout is reached
         *
         * @param futureResult
         * 		{@link FutureResult} that needs to be tested for completion
         * @throws InterruptedException
         * 		
         */
        private void awaitOpCompletionOrTimeOut(FutureResult futureResult) throws InterruptedException {
            int timer = 0;
            List<RequestInfo> allRequests = new ArrayList<>();
            while ((timer < ((RouterTestHelpers.AWAIT_TIMEOUT_MS) / 2)) && (!(futureResult.completed()))) {
                pollOpManager(allRequests);
                Thread.sleep(50);
                timer += 50;
                allRequests.clear();
            } 
        }

        /**
         * Hand over a responseInfo to the operation manager.
         *
         * @param responseInfo
         * 		the {@link ResponseInfo} to hand over.
         */
        void handleResponse(ResponseInfo responseInfo) {
            switch (opType) {
                case PUT :
                    putManager.handleResponse(responseInfo);
                    break;
                case GET :
                    getManager.handleResponse(responseInfo);
                    break;
                case DELETE :
                    deleteManager.handleResponse(responseInfo);
                    break;
            }
        }
    }
}

