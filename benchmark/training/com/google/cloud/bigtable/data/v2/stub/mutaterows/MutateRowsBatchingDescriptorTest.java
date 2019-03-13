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


import Status.Code.DEADLINE_EXCEEDED;
import Status.Code.UNAVAILABLE;
import com.google.api.gax.batching.PartitionKey;
import com.google.api.gax.batching.RequestBuilder;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.BatchedFuture;
import com.google.api.gax.rpc.BatchedRequestIssuer;
import com.google.bigtable.v2.MutateRowsRequest;
import com.google.bigtable.v2.MutateRowsRequest.Entry;
import com.google.cloud.bigtable.data.v2.internal.NameUtil;
import com.google.cloud.bigtable.data.v2.models.MutateRowsException;
import com.google.cloud.bigtable.data.v2.models.MutateRowsException.FailedMutation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class MutateRowsBatchingDescriptorTest {
    private static final String PROJECT_ID = "fake-project";

    private static final String INSTANCE_ID = "fake-instance";

    private static final String TABLE_ID = "fake-table";

    private MutateRowsBatchingDescriptor descriptor;

    @Test
    public void countBytesTest() {
        MutateRowsRequest request = MutateRowsBatchingDescriptorTest.createRequest(2);
        long actual = descriptor.countBytes(request);
        assertThat(actual).isEqualTo(request.getSerializedSize());
    }

    @Test
    public void countElementsTest() {
        MutateRowsRequest request = MutateRowsBatchingDescriptorTest.createRequest(2);
        long actual = descriptor.countElements(request);
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void partitionKeyTest() {
        String myTableName = NameUtil.formatTableName("my-project", "my-instance", "my-table");
        MutateRowsRequest request = MutateRowsBatchingDescriptorTest.createRequest(2).toBuilder().setTableName(myTableName).build();
        PartitionKey actual = descriptor.getBatchPartitionKey(request);
        assertThat(actual).isEqualTo(new PartitionKey(myTableName));
    }

    @Test
    public void requestBuilderTest() {
        RequestBuilder<MutateRowsRequest> builder = descriptor.getRequestBuilder();
        MutateRowsRequest expected = MutateRowsBatchingDescriptorTest.createRequest(5);
        for (Entry entry : expected.getEntriesList()) {
            MutateRowsRequest singleReq = MutateRowsRequest.newBuilder().setTableName(NameUtil.formatTableName(MutateRowsBatchingDescriptorTest.PROJECT_ID, MutateRowsBatchingDescriptorTest.INSTANCE_ID, MutateRowsBatchingDescriptorTest.TABLE_ID)).addEntries(entry).build();
            builder.appendRequest(singleReq);
        }
        MutateRowsRequest actual = builder.build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void splitRpcExceptionTest() throws Exception {
        BatchedFuture<Void> result1 = BatchedFuture.create();
        BatchedRequestIssuer<Void> issuer1 = new BatchedRequestIssuer(result1, 1);
        BatchedFuture<Void> result2 = BatchedFuture.create();
        BatchedRequestIssuer<Void> issuer2 = new BatchedRequestIssuer(result2, 1);
        ImmutableList<BatchedRequestIssuer<Void>> issuers = new ImmutableList.Builder<BatchedRequestIssuer<Void>>().add(issuer1).add(issuer2).build();
        ApiException serverError = new ApiException(null, GrpcStatusCode.of(UNAVAILABLE), true);
        descriptor.splitException(serverError, issuers);
        issuer1.sendResult();
        issuer2.sendResult();
        Throwable error1 = null;
        try {
            result1.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException t) {
            error1 = t.getCause();
        }
        assertThat(error1).isSameAs(serverError);
        Throwable error2 = null;
        try {
            result2.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException t) {
            error2 = t.getCause();
        }
        assertThat(error2).isSameAs(serverError);
    }

    @Test
    public void splitEntryErrorTest() throws Exception {
        BatchedFuture<Void> result1 = BatchedFuture.create();
        BatchedRequestIssuer<Void> issuer1 = new BatchedRequestIssuer(result1, 1);
        BatchedFuture<Void> result2 = BatchedFuture.create();
        BatchedRequestIssuer<Void> issuer2 = new BatchedRequestIssuer(result2, 1);
        List<BatchedRequestIssuer<Void>> issuers = new ImmutableList.Builder<BatchedRequestIssuer<Void>>().add(issuer1).add(issuer2).build();
        MutateRowsException serverError = new MutateRowsException(null, Lists.newArrayList(FailedMutation.create(0, new com.google.api.gax.rpc.UnavailableException(null, GrpcStatusCode.of(UNAVAILABLE), true)), FailedMutation.create(1, new com.google.api.gax.rpc.DeadlineExceededException(null, GrpcStatusCode.of(DEADLINE_EXCEEDED), true))), true);
        descriptor.splitException(serverError, issuers);
        issuer1.sendResult();
        issuer2.sendResult();
        Throwable actualError1 = null;
        try {
            result1.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            actualError1 = e.getCause();
        }
        assertThat(actualError1).isEqualTo(serverError.getFailedMutations().get(0).getError());
        Throwable actualError2 = null;
        try {
            result2.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            actualError2 = e.getCause();
        }
        assertThat(actualError2).isEqualTo(serverError.getFailedMutations().get(1).getError());
    }

    @Test
    public void splitResponseOkTest() throws InterruptedException, ExecutionException, TimeoutException {
        BatchedFuture<Void> result1 = BatchedFuture.create();
        BatchedRequestIssuer<Void> issuer1 = new BatchedRequestIssuer(result1, 1);
        BatchedFuture<Void> result2 = BatchedFuture.create();
        BatchedRequestIssuer<Void> issuer2 = new BatchedRequestIssuer(result2, 1);
        List<BatchedRequestIssuer<Void>> issuers = new ImmutableList.Builder<BatchedRequestIssuer<Void>>().add(issuer1).add(issuer2).build();
        descriptor.splitResponse(null, issuers);
        issuer1.sendResult();
        issuer2.sendResult();
        assertThat(result1.get(1, TimeUnit.SECONDS)).isEqualTo(null);
        assertThat(result2.get(1, TimeUnit.SECONDS)).isEqualTo(null);
    }
}

