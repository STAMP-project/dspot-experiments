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
package com.google.cloud.bigtable.data.v2.it;


import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.bigtable.data.v2.it.env.TestEnvRule;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ReadIT {
    private String prefix;

    @ClassRule
    public static TestEnvRule testEnvRule = new TestEnvRule();

    @Test
    public void readEmpty() throws Throwable {
        String uniqueKey = (prefix) + "-readEmpty";
        Query query = Query.create(ReadIT.testEnvRule.env().getTableId()).rowKey(uniqueKey);
        // Sync
        ArrayList<Row> rows = Lists.newArrayList(ReadIT.testEnvRule.env().getDataClient().readRows(query));
        assertThat(rows).isEmpty();
        // Async
        ReadIT.AccumulatingObserver observer = new ReadIT.AccumulatingObserver();
        ReadIT.testEnvRule.env().getDataClient().readRowsAsync(query, observer);
        observer.awaitCompletion();
        assertThat(observer.responses).isEmpty();
    }

    @Test
    public void read() throws Throwable {
        int numRows = 5;
        List<Row> expectedRows = Lists.newArrayList();
        String uniqueKey = (prefix) + "-read";
        long timestampMicros = (System.currentTimeMillis()) * 1000;
        for (int i = 0; i < numRows; i++) {
            ReadIT.testEnvRule.env().getDataClient().mutateRowCallable().call(RowMutation.create(ReadIT.testEnvRule.env().getTableId(), ((uniqueKey + "-") + i)).setCell(ReadIT.testEnvRule.env().getFamilyId(), "q", timestampMicros, "my-value"));
            expectedRows.add(Row.create(ByteString.copyFromUtf8(((uniqueKey + "-") + i)), ImmutableList.of(RowCell.create(ReadIT.testEnvRule.env().getFamilyId(), ByteString.copyFromUtf8("q"), timestampMicros, ImmutableList.<String>of(), ByteString.copyFromUtf8("my-value")))));
        }
        String tableId = ReadIT.testEnvRule.env().getTableId();
        // Sync
        Query query = Query.create(tableId).range((uniqueKey + "-0"), ((uniqueKey + "-") + numRows));
        ArrayList<Row> actualResults = Lists.newArrayList(ReadIT.testEnvRule.env().getDataClient().readRows(query));
        assertThat(actualResults).containsExactlyElementsIn(expectedRows);
        // Async
        ReadIT.AccumulatingObserver observer = new ReadIT.AccumulatingObserver();
        ReadIT.testEnvRule.env().getDataClient().readRowsAsync(query, observer);
        observer.awaitCompletion();
        assertThat(observer.responses).containsExactlyElementsIn(expectedRows);
        // Point Sync
        Row actualRow = ReadIT.testEnvRule.env().getDataClient().readRow(tableId, expectedRows.get(0).getKey());
        assertThat(actualRow).isEqualTo(expectedRows.get(0));
        // Point Async
        ApiFuture<Row> actualRowFuture = ReadIT.testEnvRule.env().getDataClient().readRowAsync(tableId, expectedRows.get(0).getKey());
        assertThat(actualRowFuture.get()).isEqualTo(expectedRows.get(0));
    }

    @Test
    public void readSingleNonexistentAsyncCallback() throws Exception {
        ApiFuture<Row> future = ReadIT.testEnvRule.env().getDataClient().readRowAsync(ReadIT.testEnvRule.env().getTableId(), "somenonexistentkey");
        final AtomicBoolean found = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        ApiFutures.addCallback(future, new com.google.api.core.ApiFutureCallback<Row>() {
            @Override
            public void onFailure(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onSuccess(Row result) {
                found.set(true);
                latch.countDown();
            }
        }, MoreExecutors.directExecutor());
        latch.await(1, TimeUnit.MINUTES);
        assertThat(found.get()).isTrue();
    }

    static class AccumulatingObserver implements ResponseObserver<Row> {
        final List<Row> responses = Lists.newArrayList();

        final SettableApiFuture<Void> completionFuture = SettableApiFuture.create();

        void awaitCompletion() throws Throwable {
            try {
                completionFuture.get(10, TimeUnit.MINUTES);
            } catch (ExecutionException e) {
                throw e.getCause();
            }
        }

        @Override
        public void onStart(StreamController controller) {
        }

        @Override
        public void onResponse(Row row) {
            responses.add(row);
        }

        @Override
        public void onError(Throwable t) {
            completionFuture.setException(t);
        }

        @Override
        public void onComplete() {
            completionFuture.set(null);
        }
    }
}

