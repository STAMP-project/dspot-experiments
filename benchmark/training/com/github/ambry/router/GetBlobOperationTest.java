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


import BlobId.BlobDataType;
import BlobId.BlobIdType;
import BlobType.DataBlob;
import BlobType.MetadataBlob;
import GetBlobOptions.OperationType;
import GetBlobOptions.OperationType.All;
import GetBlobOptions.OperationType.BlobInfo;
import MessageFormatRecord.Blob_Version_V1;
import MessageFormatRecord.Blob_Version_V2;
import MockClusterMap.DEFAULT_PARTITION_CLASS;
import RouterErrorCode.BlobAuthorizationFailure;
import RouterErrorCode.BlobDeleted;
import RouterErrorCode.BlobDoesNotExist;
import RouterErrorCode.BlobExpired;
import RouterErrorCode.OperationTimedOut;
import RouterErrorCode.UnexpectedInternalError;
import ServerErrorCode.Blob_Authorization_Failure;
import ServerErrorCode.Blob_Deleted;
import ServerErrorCode.Blob_Expired;
import ServerErrorCode.Blob_Not_Found;
import ServerErrorCode.Disk_Unavailable;
import ServerErrorCode.No_Error;
import ServerErrorCode.Replica_Unavailable;
import TestUtils.RANDOM;
import com.codahale.metrics.Counter;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.BlobIdFactory;
import com.github.ambry.commons.LoggingNotificationSystem;
import com.github.ambry.commons.ResponseHandler;
import com.github.ambry.commons.ServerErrorCode;
import com.github.ambry.config.RouterConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobAll;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.messageformat.CompositeBlobInfo;
import com.github.ambry.messageformat.MessageFormatRecord;
import com.github.ambry.messageformat.MetadataContentSerDe;
import com.github.ambry.network.NetworkClientErrorCode;
import com.github.ambry.network.RequestInfo;
import com.github.ambry.network.ResponseInfo;
import com.github.ambry.store.StoreKey;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static RouterErrorCode.AmbryUnavailable;
import static RouterErrorCode.BlobDeleted;
import static RouterErrorCode.BlobDoesNotExist;
import static RouterErrorCode.BlobExpired;
import static RouterErrorCode.InvalidBlobId;
import static RouterErrorCode.OperationTimedOut;
import static RouterErrorCode.RangeNotSatisfiable;
import static RouterErrorCode.UnexpectedInternalError;


/**
 * Tests for {@link GetBlobOperation}
 * This class creates a {@link NonBlockingRouter} with a {@link MockServer} and does puts through it. The gets,
 * however are done directly by the tests - that is, the tests create {@link GetBlobOperation} and get requests from
 * it and then use a {@link NetworkClient} directly to send requests to and get responses from the {@link MockServer}.
 * Since the {@link NetworkClient} used by the router and the test are different, and since the
 * {@link GetBlobOperation} created by the tests are never known by the router, there are no conflicts with the
 * RequestResponseHandler of the router.
 * Many of the variables are made member variables, so that they can be shared between the router and the
 * {@link GetBlobOperation}s.
 */
@RunWith(Parameterized.class)
public class GetBlobOperationTest {
    private static final int MAX_PORTS_PLAIN_TEXT = 3;

    private static final int MAX_PORTS_SSL = 3;

    private static final int CHECKOUT_TIMEOUT_MS = 1000;

    private final int replicasCount;

    private final int maxChunkSize;

    private final MockTime time = new MockTime();

    private final Map<Integer, GetOperation> correlationIdToGetOperation = new HashMap<>();

    private final Random random = new Random();

    private final MockClusterMap mockClusterMap;

    private final BlobIdFactory blobIdFactory;

    private final NonBlockingRouterMetrics routerMetrics;

    private final MockServerLayout mockServerLayout;

    private final AtomicReference<MockSelectorState> mockSelectorState = new AtomicReference<>();

    private final ResponseHandler responseHandler;

    private final NonBlockingRouter router;

    private final MockNetworkClient mockNetworkClient;

    private final RouterCallback routerCallback;

    private final String operationTrackerType;

    private final boolean testEncryption;

    private MockKeyManagementService kms = null;

    private MockCryptoService cryptoService = null;

    private CryptoJobHandler cryptoJobHandler = null;

    // Certain tests recreate the routerConfig with different properties.
    private RouterConfig routerConfig;

    private int blobSize;

    // Parameters for puts which are also used to verify the gets.
    private String blobIdStr;

    private BlobId blobId;

    private BlobProperties blobProperties;

    private byte[] userMetadata;

    private byte[] putContent;

    // Options which are passed into GetBlobOperations
    private GetBlobOptionsInternal options;

    private final GetBlobOperationTest.GetTestRequestRegistrationCallbackImpl requestRegistrationCallback = new GetBlobOperationTest.GetTestRequestRegistrationCallbackImpl();

    private class GetTestRequestRegistrationCallbackImpl implements RequestRegistrationCallback<GetOperation> {
        List<RequestInfo> requestListToFill;

        @Override
        public void registerRequestToSend(GetOperation getOperation, RequestInfo requestInfo) {
            requestListToFill.add(requestInfo);
            correlationIdToGetOperation.put(getCorrelationId(), getOperation);
        }
    }

    /**
     * A checker that either asserts that a get operation succeeds or returns the specified error code.
     */
    private final RouterTestHelpers.ErrorCodeChecker getErrorCodeChecker = new RouterTestHelpers.ErrorCodeChecker() {
        @Override
        public void testAndAssert(RouterErrorCode expectedError) throws Exception {
            if (expectedError == null) {
                getAndAssertSuccess();
            } else {
                GetBlobOperation op = createOperationAndComplete(null);
                assertFailureAndCheckErrorCode(op, expectedError);
            }
        }
    };

    /**
     * Instantiate a router, perform a put, close the router. The blob that was put will be saved in the MockServer,
     * and can be queried by the getBlob operations in the test.
     *
     * @param operationTrackerType
     * 		the type of {@link OperationTracker} to use.
     * @param testEncryption
     * 		{@code true} if blobs need to be tested w/ encryption. {@code false} otherwise
     */
    public GetBlobOperationTest(String operationTrackerType, boolean testEncryption) throws Exception {
        this.operationTrackerType = operationTrackerType;
        this.testEncryption = testEncryption;
        // Defaults. Tests may override these and do new puts as appropriate.
        maxChunkSize = (random.nextInt((1024 * 1024))) + 1;
        // a blob size that is greater than the maxChunkSize and is not a multiple of it. Will result in a composite blob.
        blobSize = (((maxChunkSize) * (random.nextInt(10))) + (random.nextInt(((maxChunkSize) - 1)))) + 1;
        mockSelectorState.set(MockSelectorState.Good);
        VerifiableProperties vprops = new VerifiableProperties(getDefaultNonBlockingRouterProperties());
        routerConfig = new RouterConfig(vprops);
        mockClusterMap = new MockClusterMap();
        blobIdFactory = new BlobIdFactory(mockClusterMap);
        routerMetrics = new NonBlockingRouterMetrics(mockClusterMap);
        options = new GetBlobOptionsInternal(new GetBlobOptionsBuilder().build(), false, routerMetrics.ageAtGet);
        mockServerLayout = new MockServerLayout(mockClusterMap);
        replicasCount = mockClusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0).getReplicaIds().size();
        responseHandler = new ResponseHandler(mockClusterMap);
        MockNetworkClientFactory networkClientFactory = new MockNetworkClientFactory(vprops, mockSelectorState, GetBlobOperationTest.MAX_PORTS_PLAIN_TEXT, GetBlobOperationTest.MAX_PORTS_SSL, GetBlobOperationTest.CHECKOUT_TIMEOUT_MS, mockServerLayout, time);
        if (testEncryption) {
            kms = new MockKeyManagementService(new com.github.ambry.config.KMSConfig(vprops), TestUtils.getRandomKey(SingleKeyManagementServiceTest.DEFAULT_KEY_SIZE_CHARS));
            cryptoService = new MockCryptoService(new com.github.ambry.config.CryptoServiceConfig(vprops));
            cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        }
        router = new NonBlockingRouter(routerConfig, new NonBlockingRouterMetrics(mockClusterMap), networkClientFactory, new LoggingNotificationSystem(), mockClusterMap, kms, cryptoService, cryptoJobHandler, new InMemAccountService(false, true), time, MockClusterMap.DEFAULT_PARTITION_CLASS);
        mockNetworkClient = networkClientFactory.getMockNetworkClient();
        routerCallback = new RouterCallback(mockNetworkClient, new ArrayList<BackgroundDeleteRequest>());
    }

    /**
     * Test {@link GetBlobOperation} instantiation and validate the get methods.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInstantiation() throws Exception {
        Callback<GetBlobResultInternal> getRouterCallback = new Callback<GetBlobResultInternal>() {
            @Override
            public void onCompletion(GetBlobResultInternal result, Exception exception) {
                // no op.
            }
        };
        blobId = new BlobId(routerConfig.routerBlobidCurrentVersion, BlobIdType.NATIVE, mockClusterMap.getLocalDatacenterId(), Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), mockClusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), false, BlobDataType.DATACHUNK);
        blobIdStr = blobId.getID();
        // test a good case
        // operationCount is not incremented here as this operation is not taken to completion.
        GetBlobOperation op = new GetBlobOperation(routerConfig, routerMetrics, mockClusterMap, responseHandler, blobId, new GetBlobOptionsInternal(new GetBlobOptionsBuilder().build(), false, routerMetrics.ageAtGet), getRouterCallback, routerCallback, blobIdFactory, kms, cryptoService, cryptoJobHandler, time, false);
        Assert.assertEquals("Callbacks must match", getRouterCallback, op.getCallback());
        Assert.assertEquals("Blob ids must match", blobIdStr, op.getBlobIdStr());
        // test the case where the tracker type is bad
        Properties properties = getDefaultNonBlockingRouterProperties();
        properties.setProperty("router.get.operation.tracker.type", "NonExistentTracker");
        RouterConfig badConfig = new RouterConfig(new VerifiableProperties(properties));
        try {
            new GetBlobOperation(badConfig, routerMetrics, mockClusterMap, responseHandler, blobId, new GetBlobOptionsInternal(new GetBlobOptionsBuilder().build(), false, routerMetrics.ageAtGet), getRouterCallback, routerCallback, blobIdFactory, kms, cryptoService, cryptoJobHandler, time, false);
            Assert.fail("Instantiation of GetBlobOperation with an invalid tracker type must fail");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Put blobs that result in a single chunk; perform gets of the blob and ensure success.
     */
    @Test
    public void testSimpleBlobGetSuccess() throws Exception {
        for (int i = 0; i < 10; i++) {
            // blobSize in the range [1, maxChunkSize]
            blobSize = (random.nextInt(maxChunkSize)) + 1;
            doPut();
            GetBlobOptions.OperationType operationType;
            switch (i % 3) {
                case 0 :
                    operationType = OperationType.All;
                    break;
                case 1 :
                    operationType = OperationType.Data;
                    break;
                default :
                    operationType = OperationType.BlobInfo;
                    break;
            }
            options = new GetBlobOptionsInternal(new GetBlobOptionsBuilder().operationType(operationType).build(), false, routerMetrics.ageAtGet);
            getAndAssertSuccess();
        }
    }

    /**
     * Test gets of simple blob in raw mode.
     */
    @Test
    public void testSimpleBlobRawMode() throws Exception {
        blobSize = maxChunkSize;
        doPut();
        options = new GetBlobOptionsInternal(new GetBlobOptionsBuilder().operationType(All).rawMode(true).build(), false, routerMetrics.ageAtGet);
        if (testEncryption) {
            getAndAssertSuccess();
        } else {
            GetBlobOperation op = createOperationAndComplete(null);
            Assert.assertEquals(IllegalStateException.class, op.getOperationException().getClass());
        }
    }

    /**
     * Test gets of composite blob in raw mode.
     */
    @Test
    public void testCompositeBlobRawMode() throws Exception {
        options = new GetBlobOptionsInternal(new GetBlobOptionsBuilder().operationType(All).rawMode(true).build(), false, routerMetrics.ageAtGet);
        for (int numChunks = 2; numChunks < 10; numChunks++) {
            blobSize = numChunks * (maxChunkSize);
            doPut();
            if (testEncryption) {
                getAndAssertSuccess();
                ByteBuffer payload = getBlobBuffer();
                // extract chunk ids
                BlobAll blobAll = MessageFormatRecord.deserializeBlobAll(new ByteArrayInputStream(payload.array()), blobIdFactory);
                ByteBuffer metadataBuffer = blobAll.getBlobData().getStream().getByteBuffer();
                CompositeBlobInfo compositeBlobInfo = MetadataContentSerDe.deserializeMetadataContentRecord(metadataBuffer, blobIdFactory);
                Assert.assertEquals("Total size didn't match", blobSize, compositeBlobInfo.getTotalSize());
                Assert.assertEquals("Chunk count didn't match", numChunks, compositeBlobInfo.getKeys().size());
                // TODO; test raw get on each chunk (needs changes to test framework)
            } else {
                // Only supported for encrypted blobs now
            }
        }
    }

    /**
     * Put a blob with no data, perform get and ensure success.
     */
    @Test
    public void testZeroSizedBlobGetSuccess() throws Exception {
        blobSize = 0;
        doPut();
        getAndAssertSuccess();
    }

    /**
     * Put blobs that result in multiple chunks and at chunk boundaries; perform gets and ensure success.
     */
    @Test
    public void testCompositeBlobChunkSizeMultipleGetSuccess() throws Exception {
        for (int i = 2; i < 10; i++) {
            blobSize = (maxChunkSize) * i;
            doPut();
            getAndAssertSuccess();
        }
    }

    /**
     * Put blobs that result in multiple chunks with the last chunk less than max chunk size; perform gets and ensure
     * success.
     */
    @Test
    public void testCompositeBlobNotChunkSizeMultipleGetSuccess() throws Exception {
        for (int i = 0; i < 10; i++) {
            blobSize = (((maxChunkSize) * i) + (random.nextInt(((maxChunkSize) - 1)))) + 1;
            doPut();
            GetBlobOptions.OperationType operationType;
            switch (i % 3) {
                case 0 :
                    operationType = OperationType.All;
                    break;
                case 1 :
                    operationType = OperationType.Data;
                    break;
                default :
                    operationType = OperationType.BlobInfo;
                    break;
            }
            options = new GetBlobOptionsInternal(new GetBlobOptionsBuilder().operationType(operationType).build(), false, routerMetrics.ageAtGet);
            getAndAssertSuccess();
        }
    }

    /**
     * Test the case where all requests time out within the GetOperation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRouterRequestTimeoutAllFailure() throws Exception {
        doPut();
        GetBlobOperation op = createOperation(null);
        op.poll(requestRegistrationCallback);
        while (!(op.isOperationComplete())) {
            time.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
            op.poll(requestRegistrationCallback);
        } 
        // At this time requests would have been created for all replicas, as none of them were delivered,
        // and cross-colo proxying is enabled by default.
        Assert.assertEquals("Must have attempted sending requests to all replicas", replicasCount, correlationIdToGetOperation.size());
        assertFailureAndCheckErrorCode(op, OperationTimedOut);
    }

    /**
     * Test the case where all requests time out within the NetworkClient.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNetworkClientTimeoutAllFailure() throws Exception {
        doPut();
        GetBlobOperation op = createOperation(null);
        while (!(op.isOperationComplete())) {
            op.poll(requestRegistrationCallback);
            for (RequestInfo requestInfo : requestRegistrationCallback.requestListToFill) {
                ResponseInfo fakeResponse = new ResponseInfo(requestInfo, NetworkClientErrorCode.NetworkError, null);
                op.handleResponse(fakeResponse, null);
                if (op.isOperationComplete()) {
                    break;
                }
            }
            requestRegistrationCallback.requestListToFill.clear();
        } 
        // At this time requests would have been created for all replicas, as none of them were delivered,
        // and cross-colo proxying is enabled by default.
        Assert.assertEquals("Must have attempted sending requests to all replicas", replicasCount, correlationIdToGetOperation.size());
        assertFailureAndCheckErrorCode(op, OperationTimedOut);
    }

    /**
     * Test the case where every server returns Blob_Not_Found. All servers must have been contacted,
     * due to cross-colo proxying.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlobNotFoundCase() throws Exception {
        doPut();
        RouterTestHelpers.testWithErrorCodes(Collections.singletonMap(Blob_Not_Found, replicasCount), mockServerLayout, BlobDoesNotExist, new RouterTestHelpers.ErrorCodeChecker() {
            @Override
            public void testAndAssert(RouterErrorCode expectedError) throws Exception {
                GetBlobOperation op = createOperationAndComplete(null);
                Assert.assertEquals("Must have attempted sending requests to all replicas", replicasCount, correlationIdToGetOperation.size());
                assertFailureAndCheckErrorCode(op, expectedError);
            }
        });
    }

    /**
     * Test the case with Blob_Not_Found errors from most servers, and Blob_Deleted, Blob_Expired or
     * Blob_Authorization_Failure at just one server. The latter should be the exception received for the operation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testErrorPrecedenceWithSpecialCase() throws Exception {
        doPut();
        Map<ServerErrorCode, RouterErrorCode> serverErrorToRouterError = new HashMap<>();
        serverErrorToRouterError.put(Blob_Deleted, BlobDeleted);
        serverErrorToRouterError.put(Blob_Expired, BlobExpired);
        serverErrorToRouterError.put(Blob_Authorization_Failure, BlobAuthorizationFailure);
        for (Map.Entry<ServerErrorCode, RouterErrorCode> entry : serverErrorToRouterError.entrySet()) {
            Map<ServerErrorCode, Integer> errorCounts = new HashMap<>();
            errorCounts.put(Blob_Not_Found, ((replicasCount) - 1));
            errorCounts.put(entry.getKey(), 1);
            RouterTestHelpers.testWithErrorCodes(errorCounts, mockServerLayout, entry.getValue(), getErrorCodeChecker);
        }
    }

    /**
     * Test the case where servers return different {@link ServerErrorCode} or success, and the {@link GetBlobOperation}
     * is able to resolve and conclude the correct {@link RouterErrorCode}. The get operation should be able
     * to resolve the router error code as {@code Blob_Authorization_Failure}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthorizationFailureOverrideAll() throws Exception {
        doPut();
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        serverErrorCodes[0] = ServerErrorCode.Blob_Not_Found;
        serverErrorCodes[1] = ServerErrorCode.Data_Corrupt;
        serverErrorCodes[2] = ServerErrorCode.IO_Error;
        serverErrorCodes[3] = ServerErrorCode.Partition_Unknown;
        serverErrorCodes[4] = ServerErrorCode.Disk_Unavailable;
        serverErrorCodes[5] = ServerErrorCode.Blob_Authorization_Failure;
        serverErrorCodes[6] = ServerErrorCode.Unknown_Error;
        serverErrorCodes[7] = ServerErrorCode.Unknown_Error;
        serverErrorCodes[8] = ServerErrorCode.Blob_Authorization_Failure;
        Map<ServerErrorCode, Integer> errorCounts = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            if (!(errorCounts.containsKey(serverErrorCodes[i]))) {
                errorCounts.put(serverErrorCodes[i], 0);
            }
            errorCounts.put(serverErrorCodes[i], ((errorCounts.get(serverErrorCodes[i])) + 1));
        }
        RouterTestHelpers.testWithErrorCodes(errorCounts, mockServerLayout, BlobAuthorizationFailure, getErrorCodeChecker);
    }

    /**
     * Test the case with multiple errors (server level and partition level) from multiple servers,
     * with just one server returning a successful response. The operation should succeed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSuccessInThePresenceOfVariousErrors() throws Exception {
        doPut();
        // The put for the blob being requested happened.
        String dcWherePutHappened = routerConfig.routerDatacenterName;
        // test requests coming in from local dc as well as cross-colo.
        Properties props = getDefaultNonBlockingRouterProperties();
        props.setProperty("router.datacenter.name", "DC1");
        routerConfig = new RouterConfig(new VerifiableProperties(props));
        doTestSuccessInThePresenceOfVariousErrors(dcWherePutHappened);
        props = getDefaultNonBlockingRouterProperties();
        props.setProperty("router.datacenter.name", "DC2");
        routerConfig = new RouterConfig(new VerifiableProperties(props));
        doTestSuccessInThePresenceOfVariousErrors(dcWherePutHappened);
        props = getDefaultNonBlockingRouterProperties();
        props.setProperty("router.datacenter.name", "DC3");
        routerConfig = new RouterConfig(new VerifiableProperties(props));
        doTestSuccessInThePresenceOfVariousErrors(dcWherePutHappened);
    }

    /**
     * Tests the case where all servers return the same server level error code
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailureOnServerErrors() throws Exception {
        doPut();
        // set the status to various server level errors (remove all partition level errors or non errors)
        EnumSet<ServerErrorCode> serverErrors = EnumSet.complementOf(EnumSet.of(Blob_Deleted, Blob_Expired, No_Error, Blob_Authorization_Failure, Blob_Not_Found));
        for (ServerErrorCode serverErrorCode : serverErrors) {
            mockServerLayout.getMockServers().forEach(( server) -> server.setServerErrorForAllRequests(serverErrorCode));
            GetBlobOperation op = createOperationAndComplete(null);
            assertFailureAndCheckErrorCode(op, (EnumSet.of(Disk_Unavailable, Replica_Unavailable).contains(serverErrorCode) ? AmbryUnavailable : UnexpectedInternalError));
        }
    }

    /**
     * Test failure with KMS
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKMSFailure() throws Exception {
        if (testEncryption) {
            // simple Blob
            blobSize = (random.nextInt(maxChunkSize)) + 1;
            doPut();
            kms.exceptionToThrow.set(PutManagerTest.GSE);
            GetBlobOperation op = createOperationAndComplete(null);
            assertFailureAndCheckErrorCode(op, UnexpectedInternalError);
            // composite blob
            kms.exceptionToThrow.set(null);
            blobSize = (maxChunkSize) * (random.nextInt(10));
            doPut();
            kms.exceptionToThrow.set(PutManagerTest.GSE);
            op = createOperationAndComplete(null);
            assertFailureAndCheckErrorCode(op, UnexpectedInternalError);
        }
    }

    /**
     * Test failure with CryptoService
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCryptoServiceFailure() throws Exception {
        if (testEncryption) {
            // simple Blob
            blobSize = (random.nextInt(maxChunkSize)) + 1;
            doPut();
            cryptoService.exceptionOnDecryption.set(PutManagerTest.GSE);
            GetBlobOperation op = createOperationAndComplete(null);
            assertFailureAndCheckErrorCode(op, UnexpectedInternalError);
            // composite blob
            cryptoService.exceptionOnDecryption.set(null);
            blobSize = (maxChunkSize) * (random.nextInt(10));
            doPut();
            cryptoService.exceptionOnDecryption.set(PutManagerTest.GSE);
            op = createOperationAndComplete(null);
            assertFailureAndCheckErrorCode(op, UnexpectedInternalError);
        }
    }

    /**
     * Test that read succeeds when all chunks are received before read is called.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadNotCalledBeforeChunkArrival() throws Exception {
        // 3 chunks so blob can be cached completely before reading
        blobSize = ((maxChunkSize) * 2) + 1;
        doPut();
        getAndAssertSuccess(true, false);
    }

    /**
     * Test that read succeeds when read is called immediately after callback, and chunks come in delayed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDelayedChunks() throws Exception {
        doPut();
        getAndAssertSuccess(false, true);
    }

    /**
     * Test that data chunk errors notify the reader callback and set the error code correctly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDataChunkFailure() throws Exception {
        for (ServerErrorCode serverErrorCode : ServerErrorCode.values()) {
            if (serverErrorCode != (ServerErrorCode.No_Error)) {
                testDataChunkError(serverErrorCode, UnexpectedInternalError);
            }
        }
    }

    /**
     * A past issue with replication logic resulted in the blob size listed in the blob properties reflecting the size
     * of a chunk's content buffer instead of the plaintext size of the entire blob. This issue affects composite blobs
     * and simple encrypted blob. This test tests the router's ability to replace the incorrect blob size field in the
     * blob properties with the inferred correct size.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlobSizeReplacement() throws Exception {
        userMetadata = new byte[10];
        random.nextBytes(userMetadata);
        options = new GetBlobOptionsInternal(new GetBlobOptionsBuilder().operationType(BlobInfo).build(), false, routerMetrics.ageAtGet);
        // test simple blob case
        blobSize = maxChunkSize;
        putContent = new byte[blobSize];
        random.nextBytes(putContent);
        blobProperties = new BlobProperties(((blobSize) + 20), "serviceId", "memberId", "contentType", false, Utils.Infinite_Time, Utils.getRandomShort(random), Utils.getRandomShort(random), testEncryption, null);
        doDirectPut(DataBlob, ByteBuffer.wrap(putContent));
        Counter sizeMismatchCounter = (testEncryption) ? routerMetrics.simpleEncryptedBlobSizeMismatchCount : routerMetrics.simpleUnencryptedBlobSizeMismatchCount;
        long startCount = sizeMismatchCounter.getCount();
        getAndAssertSuccess();
        long endCount = sizeMismatchCounter.getCount();
        Assert.assertEquals("Wrong number of blob size mismatches", 1, (endCount - startCount));
        // test composite blob case
        int numChunks = 3;
        blobSize = maxChunkSize;
        List<StoreKey> storeKeys = new ArrayList<>(numChunks);
        for (int i = 0; i < numChunks; i++) {
            doPut();
            storeKeys.add(blobId);
        }
        blobSize = (maxChunkSize) * numChunks;
        ByteBuffer metadataContent = MetadataContentSerDe.serializeMetadataContent(maxChunkSize, blobSize, storeKeys);
        metadataContent.flip();
        blobProperties = new BlobProperties(((blobSize) - 20), "serviceId", "memberId", "contentType", false, Utils.Infinite_Time, Utils.getRandomShort(random), Utils.getRandomShort(random), testEncryption, null);
        doDirectPut(MetadataBlob, metadataContent);
        startCount = routerMetrics.compositeBlobSizeMismatchCount.getCount();
        getAndAssertSuccess();
        endCount = routerMetrics.compositeBlobSizeMismatchCount.getCount();
        Assert.assertEquals("Wrong number of blob size mismatches", 1, (endCount - startCount));
    }

    /**
     * Test that gets work for blobs with the old blob format (V1).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLegacyBlobGetSuccess() throws Exception {
        mockServerLayout.getMockServers().forEach(( mockServer) -> mockServer.setBlobFormatVersion(Blob_Version_V1));
        for (int i = 0; i < 10; i++) {
            // blobSize in the range [1, maxChunkSize]
            blobSize = (random.nextInt(maxChunkSize)) + 1;
            doPut();
            getAndAssertSuccess();
        }
        mockServerLayout.getMockServers().forEach(( mockServer) -> mockServer.setBlobFormatVersion(Blob_Version_V2));
    }

    /**
     * Test range requests on a single chunk blob.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRangeRequestSimpleBlob() throws Exception {
        // Random valid ranges
        for (int i = 0; i < 5; i++) {
            blobSize = (random.nextInt(maxChunkSize)) + 1;
            int randomOne = random.nextInt(blobSize);
            int randomTwo = random.nextInt(blobSize);
            testRangeRequestOffsetRange(Math.min(randomOne, randomTwo), Math.max(randomOne, randomTwo), true);
        }
        blobSize = (random.nextInt(maxChunkSize)) + 1;
        // Entire blob
        testRangeRequestOffsetRange(0, ((blobSize) - 1), true);
        // Range that extends to end of blob
        testRangeRequestFromStartOffset(random.nextInt(blobSize), true);
        // Last n bytes of the blob
        testRangeRequestLastNBytes(((random.nextInt(blobSize)) + 1), true);
        // Last blobSize + 1 bytes
        testRangeRequestLastNBytes(((blobSize) + 1), true);
        // Range over the end of the blob
        testRangeRequestOffsetRange(random.nextInt(blobSize), ((blobSize) + 5), true);
        // Ranges that start past the end of the blob (should not succeed)
        testRangeRequestFromStartOffset(blobSize, false);
        testRangeRequestOffsetRange(blobSize, ((blobSize) + 20), false);
        // 0 byte range
        testRangeRequestLastNBytes(0, true);
        // 1 byte ranges
        testRangeRequestOffsetRange(0, 0, true);
        testRangeRequestOffsetRange(((blobSize) - 1), ((blobSize) - 1), true);
        testRangeRequestFromStartOffset(((blobSize) - 1), true);
        testRangeRequestLastNBytes(1, true);
    }

    /**
     * Test range requests on a composite blob.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRangeRequestCompositeBlob() throws Exception {
        // Random valid ranges
        for (int i = 0; i < 5; i++) {
            blobSize = (random.nextInt(maxChunkSize)) + ((maxChunkSize) * (random.nextInt(10)));
            int randomOne = random.nextInt(blobSize);
            int randomTwo = random.nextInt(blobSize);
            testRangeRequestOffsetRange(Math.min(randomOne, randomTwo), Math.max(randomOne, randomTwo), true);
        }
        blobSize = (random.nextInt(maxChunkSize)) + ((maxChunkSize) * (random.nextInt(10)));
        // Entire blob
        testRangeRequestOffsetRange(0, ((blobSize) - 1), true);
        // Range that extends to end of blob
        testRangeRequestFromStartOffset(random.nextInt(blobSize), true);
        // Last n bytes of the blob
        testRangeRequestLastNBytes(((random.nextInt(blobSize)) + 1), true);
        // Last blobSize + 1 bytes
        testRangeRequestLastNBytes(((blobSize) + 1), true);
        // Range over the end of the blob
        testRangeRequestOffsetRange(random.nextInt(blobSize), ((blobSize) + 5), true);
        // Ranges that start past the end of the blob (should not succeed)
        testRangeRequestFromStartOffset(blobSize, false);
        testRangeRequestOffsetRange(blobSize, ((blobSize) + 20), false);
        // 0 byte range
        testRangeRequestLastNBytes(0, true);
        // 1 byte ranges
        testRangeRequestOffsetRange(0, 0, true);
        testRangeRequestOffsetRange(((blobSize) - 1), ((blobSize) - 1), true);
        testRangeRequestFromStartOffset(((blobSize) - 1), true);
        testRangeRequestLastNBytes(1, true);
        blobSize = ((maxChunkSize) * 2) + (random.nextInt(maxChunkSize));
        // Single start chunk
        testRangeRequestOffsetRange(0, ((maxChunkSize) - 1), true);
        // Single intermediate chunk
        testRangeRequestOffsetRange(maxChunkSize, (((maxChunkSize) * 2) - 1), true);
        // Single end chunk
        testRangeRequestOffsetRange(((maxChunkSize) * 2), ((blobSize) - 1), true);
        // Over chunk boundaries
        testRangeRequestOffsetRange(((maxChunkSize) / 2), ((maxChunkSize) + ((maxChunkSize) / 2)), true);
        testRangeRequestFromStartOffset(((maxChunkSize) + ((maxChunkSize) / 2)), true);
    }

    /**
     * Test that the operation is completed and an exception with the error code {@link RouterErrorCode#ChannelClosed} is
     * set when the {@link ReadableStreamChannel} is closed before all chunks are read.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEarlyReadableStreamChannelClose() throws Exception {
        for (int numChunksInBlob = 0; numChunksInBlob <= 4; numChunksInBlob++) {
            for (int numChunksToRead = 0; numChunksToRead < numChunksInBlob; numChunksToRead++) {
                testEarlyReadableStreamChannelClose(numChunksInBlob, numChunksToRead);
            }
        }
    }

    /**
     * Test the Errors {@link RouterErrorCode} received by Get Operation. The operation exception is set
     * based on the priority of these errors.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSetOperationException() throws Exception {
        doPut();
        GetBlobOperation op = createOperation(null);
        RouterErrorCode[] routerErrorCodes = new RouterErrorCode[8];
        routerErrorCodes[0] = BlobDoesNotExist;
        routerErrorCodes[1] = OperationTimedOut;
        routerErrorCodes[2] = UnexpectedInternalError;
        routerErrorCodes[3] = AmbryUnavailable;
        routerErrorCodes[4] = RangeNotSatisfiable;
        routerErrorCodes[5] = BlobExpired;
        routerErrorCodes[6] = BlobDeleted;
        routerErrorCodes[7] = InvalidBlobId;
        for (int i = 0; i < (routerErrorCodes.length); ++i) {
            op.setOperationException(new RouterException("RouterError", routerErrorCodes[i]));
            op.poll(requestRegistrationCallback);
            while (!(op.isOperationComplete())) {
                time.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
                op.poll(requestRegistrationCallback);
            } 
            Assert.assertEquals(getErrorCode(), routerErrorCodes[i]);
        }
        for (int i = (routerErrorCodes.length) - 1; i >= 0; --i) {
            op.setOperationException(new RouterException("RouterError", routerErrorCodes[i]));
            op.poll(requestRegistrationCallback);
            while (!(op.isOperationComplete())) {
                time.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
                op.poll(requestRegistrationCallback);
            } 
            Assert.assertEquals(getErrorCode(), routerErrorCodes[((routerErrorCodes.length) - 1)]);
        }
        // set null to test non RouterException
        op.operationException.set(null);
        Exception nonRouterException = new Exception();
        op.setOperationException(nonRouterException);
        op.poll(requestRegistrationCallback);
        while (!(op.isOperationComplete())) {
            time.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
            op.poll(requestRegistrationCallback);
        } 
        Assert.assertEquals(nonRouterException, op.operationException.get());
        // test the edge case where current operationException is non RouterException
        op.setOperationException(new RouterException("RouterError", BlobDeleted));
        op.poll(requestRegistrationCallback);
        while (!(op.isOperationComplete())) {
            time.sleep(((routerConfig.routerRequestTimeoutMs) + 1));
            op.poll(requestRegistrationCallback);
        } 
        Assert.assertEquals(getErrorCode(), BlobDeleted);
    }
}

