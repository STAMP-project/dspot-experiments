/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.fetch;


import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import io.crate.analyze.TableDefinitions;
import io.crate.data.Bucket;
import io.crate.data.CollectionBucket;
import io.crate.data.Row;
import io.crate.data.Row1;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Reference;
import io.crate.metadata.RowGranularity;
import io.crate.testing.TestingHelpers;
import io.crate.types.DataTypes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FetchBatchAccumulatorTest {
    private static final Reference ID = new Reference(new io.crate.metadata.ReferenceIdent(TableDefinitions.USER_TABLE_IDENT, "id"), RowGranularity.DOC, DataTypes.LONG);

    private FetchBatchAccumulatorTest.DummyFetchOperation fetchOperation = new FetchBatchAccumulatorTest.DummyFetchOperation();

    @Test
    public void testFetchBatchAccumulatorMultipleFetches() throws Exception {
        FetchBatchAccumulator fetchBatchAccumulator = new FetchBatchAccumulator(CoordinatorTxnCtx.systemTransactionContext(), fetchOperation, TestingHelpers.getFunctions(), FetchBatchAccumulatorTest.buildOutputSymbols(), buildFetchProjectorContext(), 2);
        fetchBatchAccumulator.onItem(new Row1(1L));
        fetchBatchAccumulator.onItem(new Row1(2L));
        Iterator<? extends Row> result = fetchBatchAccumulator.processBatch(false).get(10, TimeUnit.SECONDS);
        Assert.assertThat(get(0), Matchers.is(1));
        Assert.assertThat(get(0), Matchers.is(2));
        fetchBatchAccumulator.onItem(new Row1(3L));
        fetchBatchAccumulator.onItem(new Row1(4L));
        result = fetchBatchAccumulator.processBatch(false).get(10, TimeUnit.SECONDS);
        Assert.assertThat(get(0), Matchers.is(3));
        Assert.assertThat(get(0), Matchers.is(4));
    }

    private static class DummyFetchOperation implements FetchOperation {
        int numFetches = 0;

        @Override
        public CompletableFuture<IntObjectMap<? extends Bucket>> fetch(String nodeId, IntObjectMap<? extends IntContainer> toFetch, boolean closeContext) {
            (numFetches)++;
            IntObjectHashMap<Bucket> readerToBuckets = new IntObjectHashMap();
            for (IntObjectCursor<? extends IntContainer> cursor : toFetch) {
                List<Object[]> rows = new ArrayList<>();
                for (IntCursor docIdCursor : cursor.value) {
                    rows.add(new Object[]{ docIdCursor.value });
                }
                readerToBuckets.put(cursor.key, new CollectionBucket(rows));
            }
            return CompletableFuture.completedFuture(readerToBuckets);
        }
    }
}

