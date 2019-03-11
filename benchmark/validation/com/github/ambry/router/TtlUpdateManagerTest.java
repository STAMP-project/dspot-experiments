/**
 * Copyright 2018 LinkedIn Corp. All rights reserved.
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


import RouterErrorCode.AmbryUnavailable;
import RouterErrorCode.BlobAuthorizationFailure;
import RouterErrorCode.BlobDeleted;
import RouterErrorCode.BlobDoesNotExist;
import RouterErrorCode.BlobExpired;
import RouterErrorCode.BlobUpdateNotAllowed;
import RouterErrorCode.OperationTimedOut;
import RouterErrorCode.UnexpectedInternalError;
import ServerErrorCode.Blob_Already_Updated;
import ServerErrorCode.Blob_Authorization_Failure;
import ServerErrorCode.Blob_Deleted;
import ServerErrorCode.Blob_Expired;
import ServerErrorCode.Blob_Not_Found;
import ServerErrorCode.Blob_Update_Not_Allowed;
import ServerErrorCode.Disk_Unavailable;
import ServerErrorCode.IO_Error;
import ServerErrorCode.No_Error;
import ServerErrorCode.Replica_Unavailable;
import ServerErrorCode.Unknown_Error;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.github.ambry.account.AccountService;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.commons.ByteBufferReadableStreamChannel;
import com.github.ambry.commons.ServerErrorCode;
import com.github.ambry.config.RouterConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.network.NetworkClient;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Utils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TtlUpdateManager}
 */
public class TtlUpdateManagerTest {
    private static final int DEFAULT_PARALLELISM = 3;

    // changing this may fail tests
    private static final int DEFAULT_SUCCESS_TARGET = 2;

    private static final int MAX_PORTS_PLAIN_TEXT = 3;

    private static final int MAX_PORTS_SSL = 3;

    private static final int CHECKOUT_TIMEOUT_MS = 1000;

    private static final int ADVANCE_TIME_INCREMENT_MS = 1000;

    private static final byte[] PUT_CONTENT = new byte[1000];

    private static final int BLOBS_COUNT = 5;

    private static final String UPDATE_SERVICE_ID = "update-service-id";

    private static final String LOCAL_DC = "DC1";

    static {
        RANDOM.nextBytes(TtlUpdateManagerTest.PUT_CONTENT);
    }

    private final NonBlockingRouter router;

    private final TtlUpdateManager ttlUpdateManager;

    private final NetworkClient networkClient;

    private final AtomicReference<MockSelectorState> mockSelectorState = new AtomicReference<>(MockSelectorState.Good);

    private final MockClusterMap clusterMap = new MockClusterMap();

    private final MockServerLayout serverLayout = new MockServerLayout(clusterMap);

    private final MockTime time = new MockTime();

    private final List<String> blobIds = new ArrayList<>(TtlUpdateManagerTest.BLOBS_COUNT);

    private final TtlUpdateNotificationSystem notificationSystem = new TtlUpdateNotificationSystem();

    private final int serverCount = serverLayout.getMockServers().size();

    private final AccountService accountService = new InMemAccountService(true, false);

    /**
     * Sets up all required components including a blob.
     *
     * @throws IOException
     * 		
     */
    public TtlUpdateManagerTest() throws Exception {
        Assert.assertTrue("Server count has to be at least 9", ((serverCount) >= 9));
        VerifiableProperties vProps = new VerifiableProperties(getNonBlockingRouterProperties(TtlUpdateManagerTest.DEFAULT_SUCCESS_TARGET, TtlUpdateManagerTest.DEFAULT_PARALLELISM));
        RouterConfig routerConfig = new RouterConfig(vProps);
        NonBlockingRouterMetrics metrics = new NonBlockingRouterMetrics(clusterMap);
        MockNetworkClientFactory networkClientFactory = new MockNetworkClientFactory(vProps, mockSelectorState, TtlUpdateManagerTest.MAX_PORTS_PLAIN_TEXT, TtlUpdateManagerTest.MAX_PORTS_SSL, TtlUpdateManagerTest.CHECKOUT_TIMEOUT_MS, serverLayout, time);
        router = new NonBlockingRouter(routerConfig, metrics, networkClientFactory, notificationSystem, clusterMap, null, null, null, new InMemAccountService(false, true), time, MockClusterMap.DEFAULT_PARTITION_CLASS);
        for (int i = 0; i < (TtlUpdateManagerTest.BLOBS_COUNT); i++) {
            ReadableStreamChannel putChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(TtlUpdateManagerTest.PUT_CONTENT));
            BlobProperties putBlobProperties = new BlobProperties((-1), "serviceId", "memberId", "contentType", false, TTL_SECS, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), false, null);
            String blobId = router.putBlob(putBlobProperties, new byte[0], putChannel, new PutBlobOptionsBuilder().build()).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            blobIds.add(blobId);
        }
        ttlUpdateManager = new TtlUpdateManager(clusterMap, new com.github.ambry.commons.ResponseHandler(clusterMap), notificationSystem, accountService, routerConfig, metrics, time);
        networkClient = networkClientFactory.getNetworkClient();
    }

    /**
     * Basic test for a TTL update through the {@link Router} (failure cases w.r.t interaction with Router in
     * {@link NonBlockingRouterTest}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void basicThroughRouterTest() throws Exception {
        for (String blobId : blobIds) {
            RouterTestHelpers.assertTtl(router, Collections.singleton(blobId), TTL_SECS);
            RouterTestHelpers.TestCallback<Void> callback = new RouterTestHelpers.TestCallback<>();
            notificationSystem.reset();
            router.updateBlobTtl(blobId, null, Infinite_Time, callback).get(RouterTestHelpers.AWAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            notificationSystem.checkNotifications(1, null, Infinite_Time);
            Assert.assertTrue("Callback was not called", callback.getLatch().await(10, TimeUnit.MILLISECONDS));
            Assert.assertNull("There should be no exception in the callback", callback.getException());
            RouterTestHelpers.assertTtl(router, Collections.singleton(blobId), Infinite_Time);
        }
    }

    /**
     * Test where TTL update is done for a single blob at a time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void singleBlobThroughTtlManagerTest() throws Exception {
        for (String blobId : blobIds) {
            RouterTestHelpers.assertTtl(router, Collections.singleton(blobId), TTL_SECS);
            executeOpAndVerify(Collections.singleton(blobId), null, false, false, false, true);
            // ok to do it again
            executeOpAndVerify(Collections.singleton(blobId), null, false, false, false, true);
        }
    }

    /**
     * Test where TTL update is done for multiple blobs at the same time
     *
     * @throws Exception
     * 		
     */
    @Test
    public void batchedThroughTtlManagerTest() throws Exception {
        RouterTestHelpers.assertTtl(router, blobIds, TTL_SECS);
        executeOpAndVerify(blobIds, null, false, false, false, true);
        // ok to do it again
        executeOpAndVerify(blobIds, null, false, false, false, true);
    }

    /**
     * Test to ensure that failure of a single TTL update in a batch fails the entire batch
     *
     * @throws Exception
     * 		
     */
    @Test
    public void singleFailureInBatchTtlUpdateTest() throws Exception {
        // configure failure for one of the blobs
        serverLayout.getMockServers().forEach(( mockServer) -> mockServer.setErrorCodeForBlob(blobIds.get(((TtlUpdateManagerTest.BLOBS_COUNT) / 2)), Unknown_Error));
        executeOpAndVerify(blobIds, UnexpectedInternalError, false, false, false, false);
    }

    /**
     * Tests to make sure {@link ServerErrorCode}s map to the right {@link RouterErrorCode}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void individualErrorCodesTest() throws Exception {
        Map<ServerErrorCode, RouterErrorCode> errorCodeMap = new HashMap<>();
        errorCodeMap.put(Blob_Deleted, BlobDeleted);
        errorCodeMap.put(Blob_Expired, BlobExpired);
        errorCodeMap.put(Blob_Not_Found, BlobDoesNotExist);
        errorCodeMap.put(Disk_Unavailable, AmbryUnavailable);
        errorCodeMap.put(Replica_Unavailable, AmbryUnavailable);
        errorCodeMap.put(Blob_Update_Not_Allowed, BlobUpdateNotAllowed);
        errorCodeMap.put(Blob_Authorization_Failure, BlobAuthorizationFailure);
        for (ServerErrorCode errorCode : ServerErrorCode.values()) {
            if ((errorCode == (ServerErrorCode.No_Error)) || (errorCode == (ServerErrorCode.Blob_Already_Updated))) {
                continue;
            }
            ArrayList<ServerErrorCode> serverErrorCodes = new ArrayList(Collections.nCopies(serverCount, Blob_Not_Found));
            // has to be repeated because the op tracker returns failure if it sees 8/9 failures and the success target is 2
            serverErrorCodes.set(3, errorCode);
            serverErrorCodes.set(5, errorCode);
            Collections.shuffle(serverErrorCodes);
            RouterTestHelpers.setServerErrorCodes(serverErrorCodes, serverLayout);
            RouterErrorCode expected = errorCodeMap.getOrDefault(errorCode, UnexpectedInternalError);
            executeOpAndVerify(blobIds, expected, false, true, true, false);
        }
        serverLayout.getMockServers().forEach(MockServer::resetServerErrors);
        RouterTestHelpers.assertTtl(router, blobIds, TTL_SECS);
    }

    /**
     * Tests to ensure that {@link RouterErrorCode}s are properly resolved based on precedence
     *
     * @throws Exception
     * 		
     */
    @Test
    public void routerErrorCodeResolutionTest() throws Exception {
        LinkedHashMap<ServerErrorCode, RouterErrorCode> codesToSetAndTest = new LinkedHashMap<>();
        // test 4 codes
        codesToSetAndTest.put(Blob_Deleted, BlobDeleted);
        codesToSetAndTest.put(Blob_Expired, BlobExpired);
        codesToSetAndTest.put(Blob_Update_Not_Allowed, BlobUpdateNotAllowed);
        codesToSetAndTest.put(Disk_Unavailable, AmbryUnavailable);
        doRouterErrorCodeResolutionTest(codesToSetAndTest);
        // test another 4 codes
        codesToSetAndTest.clear();
        codesToSetAndTest.put(Blob_Authorization_Failure, BlobAuthorizationFailure);
        codesToSetAndTest.put(Blob_Update_Not_Allowed, BlobUpdateNotAllowed);
        codesToSetAndTest.put(Disk_Unavailable, AmbryUnavailable);
        codesToSetAndTest.put(IO_Error, UnexpectedInternalError);
        doRouterErrorCodeResolutionTest(codesToSetAndTest);
    }

    /**
     * Tests to make sure that the quorum is respected
     *
     * @throws Exception
     * 		
     */
    @Test
    public void fixedCountSuccessfulResponseTest() throws Exception {
        for (int i = 0; i <= (TtlUpdateManagerTest.DEFAULT_SUCCESS_TARGET); i++) {
            boolean shouldSucceed = i == (TtlUpdateManagerTest.DEFAULT_SUCCESS_TARGET);
            doFixedCountSuccessfulResponseTest(i, shouldSucceed, No_Error);
            doFixedCountSuccessfulResponseTest(i, shouldSucceed, Blob_Already_Updated);
        }
    }

    /**
     * Tests for behavior on timeouts
     *
     * @throws Exception
     * 		
     */
    @Test
    public void responseTimeoutTest() throws Exception {
        // configure servers to not respond to requests
        serverLayout.getMockServers().forEach(( mockServer) -> mockServer.setShouldRespond(false));
        executeOpAndVerify(blobIds, OperationTimedOut, true, true, true, false);
    }

    /**
     * Test for behavior on errors in the network client and selector
     *
     * @throws Exception
     * 		
     */
    @Test
    public void networkClientAndSelectorErrorsTest() throws Exception {
        for (MockSelectorState state : MockSelectorState.values()) {
            if (state == (MockSelectorState.Good)) {
                continue;
            }
            mockSelectorState.set(state);
            executeOpAndVerify(blobIds, OperationTimedOut, true, true, true, false);
        }
    }

    /**
     * Checks that operations with duplicate blob Ids are rejected
     *
     * @throws RouterException
     * 		
     */
    @Test
    public void duplicateBlobIdsTest() throws RouterException {
        blobIds.add(blobIds.get(0));
        try {
            ttlUpdateManager.submitTtlUpdateOperation(blobIds, TtlUpdateManagerTest.UPDATE_SERVICE_ID, Infinite_Time, new FutureResult(), new RouterTestHelpers.TestCallback());
            Assert.fail("Should have failed to submit operation because the provided blob id list contains duplicates");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }
}

