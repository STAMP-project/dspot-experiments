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


import RouterErrorCode.AmbryUnavailable;
import RouterErrorCode.BlobAuthorizationFailure;
import RouterErrorCode.BlobDoesNotExist;
import RouterErrorCode.BlobExpired;
import RouterErrorCode.InvalidBlobId;
import RouterErrorCode.OperationTimedOut;
import RouterErrorCode.RouterClosed;
import RouterErrorCode.UnexpectedInternalError;
import ServerErrorCode.Blob_Authorization_Failure;
import ServerErrorCode.Blob_Expired;
import ServerErrorCode.Blob_Not_Found;
import ServerErrorCode.Disk_Unavailable;
import ServerErrorCode.IO_Error;
import ServerErrorCode.No_Error;
import ServerErrorCode.Partition_Unknown;
import ServerErrorCode.Replica_Unavailable;
import ServerErrorCode.Unknown_Error;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.LoggingNotificationSystem;
import com.github.ambry.commons.ServerErrorCode;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link DeleteManager} and {@link DeleteOperation}.
 */
public class DeleteManagerTest {
    private static final int AWAIT_TIMEOUT_SECONDS = 10;

    private Time mockTime;

    private AtomicReference<MockSelectorState> mockSelectorState;

    private MockClusterMap clusterMap;

    private MockServerLayout serverLayout;

    private NonBlockingRouter router;

    private BlobId blobId;

    private String blobIdString;

    private PartitionId partition;

    private Future<Void> future;

    /**
     * A checker that either asserts that a delete operation succeeds or returns the specified error code.
     */
    private final RouterTestHelpers.ErrorCodeChecker deleteErrorCodeChecker = new RouterTestHelpers.ErrorCodeChecker() {
        @Override
        public void testAndAssert(RouterErrorCode expectedError) throws Exception {
            future = router.deleteBlob(blobIdString, null);
            if (expectedError == null) {
                future.get(DeleteManagerTest.AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } else {
                assertFailureAndCheckErrorCode(future, expectedError);
            }
        }
    };

    private static final int MAX_PORTS_PLAIN_TEXT = 3;

    private static final int MAX_PORTS_SSL = 3;

    private static final int CHECKOUT_TIMEOUT_MS = 1000;

    // The maximum number of inflight requests for a single delete operation.
    private static final String DELETE_PARALLELISM = "3";

    /**
     * Test a basic delete operation that will succeed.
     */
    @Test
    public void testBasicDeletion() throws Exception {
        RouterTestHelpers.testWithErrorCodes(Collections.singletonMap(No_Error, 9), serverLayout, null, deleteErrorCodeChecker);
    }

    /**
     * Test that a bad user defined callback will not crash the router.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBadCallback() throws Exception {
        RouterTestHelpers.testWithErrorCodes(Collections.singletonMap(No_Error, 9), serverLayout, null, new RouterTestHelpers.ErrorCodeChecker() {
            @Override
            public void testAndAssert(RouterErrorCode expectedError) throws Exception {
                final CountDownLatch callbackCalled = new CountDownLatch(1);
                List<Future> futures = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    if (i == 1) {
                        futures.add(router.deleteBlob(blobIdString, null, new Callback<Void>() {
                            @Override
                            public void onCompletion(Void result, Exception exception) {
                                callbackCalled.countDown();
                                throw new RuntimeException("Throwing an exception in the user callback");
                            }
                        }));
                    } else {
                        futures.add(router.deleteBlob(blobIdString, null));
                    }
                }
                for (Future future : futures) {
                    future.get(DeleteManagerTest.AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                }
                long waitStart = SystemTime.getInstance().milliseconds();
                while (((router.getBackgroundOperationsCount()) != 0) && ((SystemTime.getInstance().milliseconds()) < (waitStart + ((DeleteManagerTest.AWAIT_TIMEOUT_SECONDS) * 1000)))) {
                    Thread.sleep(1000);
                } 
                Assert.assertTrue("Callback not called.", callbackCalled.await(DeleteManagerTest.AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
                Assert.assertEquals("All operations should be finished.", 0, router.getOperationsCount());
                Assert.assertTrue("Router should not be closed", router.isOpen());
                // Test that DeleteManager is still operational
                router.deleteBlob(blobIdString, null).get();
            }
        });
    }

    /**
     * Test the cases for invalid blobId strings.
     */
    @Test
    public void testBlobIdNotValid() throws Exception {
        String[] input = new String[]{ "123", "abcd", "", "/" };
        for (String s : input) {
            future = router.deleteBlob(s, null);
            assertFailureAndCheckErrorCode(future, InvalidBlobId);
        }
    }

    /**
     * Test the case when one server store responds with {@code Blob_Expired}, and other servers
     * respond with {@code Blob_Not_Found}. The delete operation should be able to resolve the
     * router error code as {@code Blob_Expired}. The order of received responses is the same as
     * defined in {@code serverErrorCodes}.
     */
    @Test
    public void testBlobExpired() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        Arrays.fill(serverErrorCodes, Blob_Not_Found);
        serverErrorCodes[5] = ServerErrorCode.Blob_Expired;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, BlobExpired, deleteErrorCodeChecker);
    }

    /**
     * Test the case when one server store responds with {@code Blob_Authorization_Failure}, and other servers
     * respond with {@code Blob_Not_Found}. The delete operation should be able to resolve the
     * router error code as {@code Blob_Authorization_Failure}. The order of received responses is the same as
     * defined in {@code serverErrorCodes}.
     */
    @Test
    public void testBlobAuthorizationFailure() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        Arrays.fill(serverErrorCodes, Blob_Not_Found);
        serverErrorCodes[5] = ServerErrorCode.Blob_Authorization_Failure;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, BlobAuthorizationFailure, deleteErrorCodeChecker);
    }

    /**
     * Test the case where servers return different {@link ServerErrorCode}, and the {@link DeleteOperation}
     * is able to resolve and conclude the correct {@link RouterErrorCode}. The delete operation should be able
     * to resolve the router error code as {@code Blob_Authorization_Failure}. The order of received responses
     * is the same as defined in {@code serverErrorCodes}.
     */
    @Test
    public void testBlobAuthorizationFailureOverrideAll() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        Arrays.fill(serverErrorCodes, No_Error);
        serverErrorCodes[0] = ServerErrorCode.Blob_Not_Found;
        serverErrorCodes[1] = ServerErrorCode.Data_Corrupt;
        serverErrorCodes[2] = ServerErrorCode.IO_Error;
        serverErrorCodes[3] = ServerErrorCode.Partition_Unknown;
        serverErrorCodes[4] = ServerErrorCode.Disk_Unavailable;
        serverErrorCodes[5] = ServerErrorCode.Blob_Authorization_Failure;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, BlobAuthorizationFailure, deleteErrorCodeChecker);
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
        codesToSetAndTest.put(Blob_Authorization_Failure, BlobAuthorizationFailure);
        codesToSetAndTest.put(Blob_Expired, BlobExpired);
        codesToSetAndTest.put(Disk_Unavailable, AmbryUnavailable);
        codesToSetAndTest.put(IO_Error, UnexpectedInternalError);
        doRouterErrorCodeResolutionTest(codesToSetAndTest);
        // test another 4 codes
        codesToSetAndTest.clear();
        codesToSetAndTest.put(Blob_Authorization_Failure, BlobAuthorizationFailure);
        codesToSetAndTest.put(Disk_Unavailable, AmbryUnavailable);
        codesToSetAndTest.put(Replica_Unavailable, AmbryUnavailable);
        codesToSetAndTest.put(Partition_Unknown, UnexpectedInternalError);
        doRouterErrorCodeResolutionTest(codesToSetAndTest);
    }

    /**
     * Test if the {@link RouterErrorCode} is as expected for different {@link ServerErrorCode}.
     */
    @Test
    public void testVariousServerErrorCode() throws Exception {
        HashMap<ServerErrorCode, RouterErrorCode> map = new HashMap<>();
        map.put(Blob_Expired, BlobExpired);
        map.put(Blob_Not_Found, BlobDoesNotExist);
        map.put(Disk_Unavailable, AmbryUnavailable);
        map.put(Replica_Unavailable, AmbryUnavailable);
        map.put(Blob_Authorization_Failure, BlobAuthorizationFailure);
        for (ServerErrorCode serverErrorCode : ServerErrorCode.values()) {
            if (((serverErrorCode != (ServerErrorCode.No_Error)) && (serverErrorCode != (ServerErrorCode.Blob_Deleted))) && (!(map.containsKey(serverErrorCode)))) {
                map.put(serverErrorCode, UnexpectedInternalError);
            }
        }
        for (Map.Entry<ServerErrorCode, RouterErrorCode> entity : map.entrySet()) {
            RouterTestHelpers.testWithErrorCodes(Collections.singletonMap(entity.getKey(), 9), serverLayout, entity.getValue(), deleteErrorCodeChecker);
        }
    }

    /**
     * Test the case when the blob cannot be found in store servers, though the last response is {@code IO_Error}.
     * The delete operation is expected to return {@link RouterErrorCode#BlobDoesNotExist}, since the delete operation will be completed
     * before the last response according to its {@link OperationTracker}. The order of received responses is the
     * same as defined in {@code serverErrorCodes}.
     */
    @Test
    public void testBlobNotFoundWithLastResponseNotBlobNotFound() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        Arrays.fill(serverErrorCodes, Blob_Not_Found);
        serverErrorCodes[8] = ServerErrorCode.IO_Error;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, BlobDoesNotExist, deleteErrorCodeChecker);
    }

    /**
     * Test the case when the two server responses are {@code ServerErrorCode.Blob_Deleted}, one is in the middle
     * of the responses, and the other is the last response. In this case, we should return {@code Blob_Deleted},
     * as we treat {@code Blob_Deleted} as a successful response, and we have met the {@code successTarget}.
     * The order of received responses is the same as defined in {@code serverErrorCodes}.
     */
    @Test
    public void testBlobNotFoundWithTwoBlobDeleted() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        Arrays.fill(serverErrorCodes, IO_Error);
        serverErrorCodes[5] = ServerErrorCode.Blob_Deleted;
        serverErrorCodes[8] = ServerErrorCode.Blob_Deleted;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, null, deleteErrorCodeChecker);
    }

    /**
     * In this test, there is only one server that returns {@code ServerErrorCode.Blob_Deleted}, which is
     * not sufficient to meet the success target, therefore a router exception should be expected. The order
     * of received responses is the same as defined in {@code serverErrorCodes}.
     */
    @Test
    public void testSingleBlobDeletedReturned() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        Arrays.fill(serverErrorCodes, Unknown_Error);
        serverErrorCodes[7] = ServerErrorCode.Blob_Deleted;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, UnexpectedInternalError, deleteErrorCodeChecker);
    }

    /**
     * Test the case where servers return different {@link ServerErrorCode}, and the {@link DeleteOperation}
     * is able to resolve and conclude the correct {@link RouterErrorCode}. The {@link ServerErrorCode} tested
     * are those could be mapped to {@link RouterErrorCode#AmbryUnavailable}. The order of received responses
     * is the same as defined in {@code serverErrorCodes}.
     */
    @Test
    public void testVariousServerErrorCodes() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        serverErrorCodes[0] = ServerErrorCode.Blob_Not_Found;
        serverErrorCodes[1] = ServerErrorCode.Data_Corrupt;
        serverErrorCodes[2] = ServerErrorCode.IO_Error;
        serverErrorCodes[3] = ServerErrorCode.Partition_Unknown;
        serverErrorCodes[4] = ServerErrorCode.Disk_Unavailable;
        serverErrorCodes[5] = ServerErrorCode.No_Error;
        serverErrorCodes[6] = ServerErrorCode.Data_Corrupt;
        serverErrorCodes[7] = ServerErrorCode.Unknown_Error;
        serverErrorCodes[8] = ServerErrorCode.Disk_Unavailable;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, AmbryUnavailable, deleteErrorCodeChecker);
    }

    /**
     * The parallelism is set to 3 not 9.
     *
     * Test the case where servers return different {@link ServerErrorCode}, and the {@link DeleteOperation}
     * is able to resolve and conclude the correct {@link RouterErrorCode}. The {link ServerErrorCode} tested
     * are those could be mapped to {@link RouterErrorCode#AmbryUnavailable}. The order of received responses
     * is the same as defined in {@code serverErrorCodes}.
     */
    @Test
    public void testVariousServerErrorCodesForThreeParallelism() throws Exception {
        RouterTestHelpers.assertCloseCleanup(router);
        Properties props = getNonBlockingRouterProperties();
        props.setProperty("router.delete.request.parallelism", "3");
        VerifiableProperties vProps = new VerifiableProperties(props);
        router = new NonBlockingRouter(new com.github.ambry.config.RouterConfig(vProps), new NonBlockingRouterMetrics(clusterMap), new MockNetworkClientFactory(vProps, mockSelectorState, DeleteManagerTest.MAX_PORTS_PLAIN_TEXT, DeleteManagerTest.MAX_PORTS_SSL, DeleteManagerTest.CHECKOUT_TIMEOUT_MS, serverLayout, mockTime), new LoggingNotificationSystem(), clusterMap, null, null, null, new InMemAccountService(false, true), mockTime, MockClusterMap.DEFAULT_PARTITION_CLASS);
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        serverErrorCodes[0] = ServerErrorCode.Blob_Not_Found;
        serverErrorCodes[1] = ServerErrorCode.Data_Corrupt;
        serverErrorCodes[2] = ServerErrorCode.IO_Error;
        serverErrorCodes[3] = ServerErrorCode.Partition_Unknown;
        serverErrorCodes[4] = ServerErrorCode.Disk_Unavailable;
        serverErrorCodes[5] = ServerErrorCode.No_Error;
        serverErrorCodes[6] = ServerErrorCode.Data_Corrupt;
        serverErrorCodes[7] = ServerErrorCode.Unknown_Error;
        serverErrorCodes[8] = ServerErrorCode.Disk_Unavailable;
        RouterTestHelpers.testWithErrorCodes(serverErrorCodes, partition, serverLayout, AmbryUnavailable, deleteErrorCodeChecker);
    }

    /**
     * Test the case when request gets expired before the corresponding store server sends
     * back a response. Set servers to not respond any requests, so {@link DeleteOperation}
     * can be "in flight" all the time. The order of received responses is the same as defined
     * in {@code serverErrorCodes}.
     */
    @Test
    public void testResponseTimeout() throws Exception {
        setServerResponse(false);
        RouterTestHelpers.testWithErrorCodes(Collections.singletonMap(No_Error, 9), serverLayout, OperationTimedOut, new RouterTestHelpers.ErrorCodeChecker() {
            @Override
            public void testAndAssert(RouterErrorCode expectedError) throws Exception {
                CountDownLatch operationCompleteLatch = new CountDownLatch(1);
                future = router.deleteBlob(blobIdString, null, new DeleteManagerTest.ClientCallback(operationCompleteLatch));
                do {
                    // increment mock time
                    mockTime.sleep(1000);
                } while (!(operationCompleteLatch.await(10, TimeUnit.MILLISECONDS)) );
                assertFailureAndCheckErrorCode(future, expectedError);
            }
        });
    }

    /**
     * Test the case when the {@link com.github.ambry.network.Selector} of {@link com.github.ambry.network.NetworkClient}
     * experiences various exceptions. The order of received responses is the same as defined in {@code serverErrorCodes}.
     */
    @Test
    public void testSelectorError() throws Exception {
        ServerErrorCode[] serverErrorCodes = new ServerErrorCode[9];
        Arrays.fill(serverErrorCodes, No_Error);
        HashMap<MockSelectorState, RouterErrorCode> errorCodeHashMap = new HashMap<>();
        errorCodeHashMap.put(MockSelectorState.DisconnectOnSend, OperationTimedOut);
        errorCodeHashMap.put(MockSelectorState.ThrowExceptionOnAllPoll, OperationTimedOut);
        errorCodeHashMap.put(MockSelectorState.ThrowExceptionOnConnect, OperationTimedOut);
        errorCodeHashMap.put(MockSelectorState.ThrowExceptionOnSend, OperationTimedOut);
        errorCodeHashMap.put(MockSelectorState.ThrowThrowableOnSend, RouterClosed);
        for (MockSelectorState state : MockSelectorState.values()) {
            if (state == (MockSelectorState.Good)) {
                continue;
            }
            mockSelectorState.set(state);
            RouterTestHelpers.setServerErrorCodes(serverErrorCodes, partition, serverLayout);
            CountDownLatch operationCompleteLatch = new CountDownLatch(1);
            future = router.deleteBlob(blobIdString, null, new DeleteManagerTest.ClientCallback(operationCompleteLatch));
            do {
                // increment mock time
                mockTime.sleep(1000);
            } while (!(operationCompleteLatch.await(10, TimeUnit.MILLISECONDS)) );
            assertFailureAndCheckErrorCode(future, errorCodeHashMap.get(state));
        }
    }

    /**
     * Test the case how a {@link DeleteManager} acts when a router is closed, and when there are inflight
     * operations. Setting servers to not respond any requests, so {@link DeleteOperation} can be "in flight".
     */
    @Test
    public void testRouterClosedDuringOperation() throws Exception {
        setServerResponse(false);
        RouterTestHelpers.testWithErrorCodes(Collections.singletonMap(No_Error, 9), serverLayout, RouterClosed, new RouterTestHelpers.ErrorCodeChecker() {
            @Override
            public void testAndAssert(RouterErrorCode expectedError) throws Exception {
                future = router.deleteBlob(blobIdString, null);
                router.close();
                assertFailureAndCheckErrorCode(future, expectedError);
            }
        });
    }

    /**
     * User callback that is called when the {@link DeleteOperation} is completed.
     */
    private class ClientCallback implements Callback<Void> {
        private final CountDownLatch operationCompleteLatch;

        ClientCallback(CountDownLatch operationCompleteLatch) {
            this.operationCompleteLatch = operationCompleteLatch;
        }

        @Override
        public void onCompletion(Void t, Exception e) {
            operationCompleteLatch.countDown();
        }
    }
}

