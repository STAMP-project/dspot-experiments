/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.data.v2.stub.mutaterows;


import BigtableGrpc.BigtableImplBase;
import Code.DEADLINE_EXCEEDED;
import Code.INVALID_ARGUMENT;
import Code.OK;
import MutateRowsResponse.Builder;
import com.google.api.client.util.Lists;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.bigtable.v2.BigtableGrpc;
import com.google.bigtable.v2.MutateRowsRequest;
import com.google.bigtable.v2.MutateRowsRequest.Entry;
import com.google.bigtable.v2.MutateRowsResponse;
import com.google.cloud.bigtable.data.v2.models.BulkMutationBatcher;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.threeten.bp.Duration;


@RunWith(MockitoJUnitRunner.class)
public class BulkMutateRowsRetryTest {
    private static final String PROJECT_ID = "fake-project";

    private static final String INSTANCE_ID = "fake-instance";

    private static final String TABLE_ID = "fake-table";

    private static final int MAX_ATTEMPTS = 5;

    private static final long FLUSH_COUNT = 10;

    private static final Duration FLUSH_PERIOD = Duration.ofMillis(50);

    private static final Duration DELAY_BUFFER = Duration.ofSeconds(1);

    @Rule
    public GrpcServerRule serverRule = new GrpcServerRule();

    private BulkMutateRowsRetryTest.TestBigtableService service;

    private BulkMutationBatcher bulkMutations;

    @Test
    public void simpleNoErrorsTest() {
        service.expectations.add(BulkMutateRowsRetryTest.RpcExpectation.create().addEntry("key1", OK));
        ApiFuture<Void> result = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key1"));
        verifyOk(result);
        service.verifyOk();
    }

    @Test
    public void batchingNoErrorsTest() {
        service.expectations.add(BulkMutateRowsRetryTest.RpcExpectation.create().addEntry("key1", OK).addEntry("key2", OK));
        ApiFuture<Void> result1 = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key1"));
        ApiFuture<Void> result2 = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key2"));
        verifyOk(result1);
        verifyOk(result2);
        service.verifyOk();
    }

    @Test
    public void fullRequestRetryTest() {
        service.expectations.add(BulkMutateRowsRetryTest.RpcExpectation.create(DEADLINE_EXCEEDED).addEntry("key1", null));
        service.expectations.add(BulkMutateRowsRetryTest.RpcExpectation.create().addEntry("key1", OK));
        ApiFuture<Void> result = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key1"));
        verifyOk(result);
        service.verifyOk();
    }

    @Test
    public void partialRetryTest() {
        service.expectations.add(BulkMutateRowsRetryTest.RpcExpectation.create().addEntry("key1", DEADLINE_EXCEEDED).addEntry("key2", OK));
        service.expectations.add(BulkMutateRowsRetryTest.RpcExpectation.create().addEntry("key1", OK));
        ApiFuture<Void> result1 = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key1"));
        ApiFuture<Void> result2 = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key2"));
        verifyOk(result1);
        verifyOk(result2);
        service.verifyOk();
    }

    @Test
    public void partialNoRetriesTest() {
        service.expectations.add(BulkMutateRowsRetryTest.RpcExpectation.create().addEntry("key1", INVALID_ARGUMENT).addEntry("key2", OK));
        ApiFuture<Void> result1 = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key1"));
        ApiFuture<Void> result2 = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key2"));
        verifyError(result1, StatusCode.Code.INVALID_ARGUMENT);
        verifyOk(result2);
        service.verifyOk();
    }

    @Test
    public void partialRetryFailsEventuallyTest() {
        // Create a bunch of failures
        BulkMutateRowsRetryTest.RpcExpectation rpcExpectation = BulkMutateRowsRetryTest.RpcExpectation.create().addEntry("key1", DEADLINE_EXCEEDED);
        for (int i = 0; i < (BulkMutateRowsRetryTest.MAX_ATTEMPTS); i++) {
            service.expectations.add(rpcExpectation);
        }
        ApiFuture<Void> result1 = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, "key1"));
        verifyError(result1, StatusCode.Code.DEADLINE_EXCEEDED);
        service.verifyOk();
    }

    @Test
    public void elementCountTest() {
        // First request
        BulkMutateRowsRetryTest.RpcExpectation rpcExpectation1 = BulkMutateRowsRetryTest.RpcExpectation.create();
        int i = 0;
        for (; i < (BulkMutateRowsRetryTest.FLUSH_COUNT); i++) {
            rpcExpectation1.addEntry(("key" + i), OK);
        }
        service.expectations.add(rpcExpectation1);
        // Overflow request
        BulkMutateRowsRetryTest.RpcExpectation rpcExpectation2 = BulkMutateRowsRetryTest.RpcExpectation.create().addEntry(("key" + i), OK);
        service.expectations.add(rpcExpectation2);
        List<ApiFuture<Void>> results = Lists.newArrayList();
        for (int j = 0; j < ((BulkMutateRowsRetryTest.FLUSH_COUNT) + 1); j++) {
            ApiFuture<Void> result = bulkMutations.add(RowMutation.create(BulkMutateRowsRetryTest.TABLE_ID, ("key" + j)));
            results.add(result);
        }
        verifyOk(ApiFutures.allAsList(results));
        service.verifyOk();
    }

    static class RpcExpectation {
        private final Map<String, Code> entries;

        private final Code resultCode;

        RpcExpectation(Status.Code resultCode) {
            this.entries = Maps.newHashMap();
            this.resultCode = resultCode;
        }

        static BulkMutateRowsRetryTest.RpcExpectation create() {
            return BulkMutateRowsRetryTest.RpcExpectation.create(OK);
        }

        static BulkMutateRowsRetryTest.RpcExpectation create(Code resultCode) {
            return new BulkMutateRowsRetryTest.RpcExpectation(resultCode);
        }

        BulkMutateRowsRetryTest.RpcExpectation addEntry(String key, Code code) {
            entries.put(key, code);
            return this;
        }
    }

    static class TestBigtableService extends BigtableGrpc.BigtableImplBase {
        Queue<BulkMutateRowsRetryTest.RpcExpectation> expectations = Queues.newArrayDeque();

        private final List<Throwable> errors = Lists.newArrayList();

        void verifyOk() {
            assertThat(expectations).isEmpty();
            assertThat(errors).isEmpty();
        }

        @Override
        public void mutateRows(MutateRowsRequest request, StreamObserver<MutateRowsResponse> responseObserver) {
            try {
                mutateRowsUnsafe(request, responseObserver);
            } catch (Throwable t) {
                errors.add(t);
                throw t;
            }
        }

        private void mutateRowsUnsafe(MutateRowsRequest request, StreamObserver<MutateRowsResponse> responseObserver) {
            BulkMutateRowsRetryTest.RpcExpectation expectedRpc = expectations.poll();
            // Make sure that this isn't an extra request.
            assertWithMessage(("Unexpected request: " + (request.toString()))).that(expectedRpc).isNotNull();
            // Make sure that this request has the same keys as the expected request.
            List<String> requestKeys = Lists.newArrayList();
            for (Entry entry : request.getEntriesList()) {
                requestKeys.add(entry.getRowKey().toStringUtf8());
            }
            assertThat(requestKeys).containsExactlyElementsIn(expectedRpc.entries.keySet());
            // Check if the expectation is to fail the entire request.
            if ((expectedRpc.resultCode) != (Code.OK)) {
                responseObserver.onError(Status.fromCode(expectedRpc.resultCode).asRuntimeException());
                return;
            }
            // Populate the response entries based on the set expectation.
            MutateRowsResponse.Builder responseBuilder = MutateRowsResponse.newBuilder();
            int i = 0;
            for (Entry requestEntry : request.getEntriesList()) {
                String key = requestEntry.getRowKey().toStringUtf8();
                Code responseCode = expectedRpc.entries.get(key);
                MutateRowsResponse.Entry responseEntry = MutateRowsResponse.Entry.newBuilder().setIndex((i++)).setStatus(com.google.rpc.Status.newBuilder().setCode(responseCode.value())).build();
                responseBuilder.addEntries(responseEntry);
            }
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }
}

