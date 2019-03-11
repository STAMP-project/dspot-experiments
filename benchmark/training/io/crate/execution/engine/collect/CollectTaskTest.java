/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.execution.engine.collect;


import Engine.Searcher;
import RowGranularity.CLUSTER;
import RowGranularity.DOC;
import RowGranularity.NODE;
import RowGranularity.PARTITION;
import RowGranularity.SHARD;
import ThreadPool.Names.GET;
import ThreadPool.Names.SEARCH;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import io.crate.breaker.RamAccountingContext;
import io.crate.data.BatchIterator;
import io.crate.data.Row;
import io.crate.exceptions.JobKilledException;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.execution.jobs.SharedShardContexts;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Routing;
import io.crate.testing.TestingRowConsumer;
import org.elasticsearch.index.engine.Engine;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CollectTaskTest extends RandomizedTest {
    private CollectTask collectTask;

    private RoutedCollectPhase collectPhase;

    private String localNodeId;

    private RamAccountingContext ramAccountingContext = Mockito.mock(RamAccountingContext.class);

    @Test
    public void testAddingSameContextTwice() throws Exception {
        Engine.Searcher mock1 = Mockito.mock(Searcher.class);
        Engine.Searcher mock2 = Mockito.mock(Searcher.class);
        try {
            collectTask.addSearcher(1, mock1);
            collectTask.addSearcher(1, mock2);
            Assert.assertFalse(true);// second addContext call should have raised an exception

        } catch (IllegalArgumentException e) {
            Mockito.verify(mock1, Mockito.times(1)).close();
            Mockito.verify(mock2, Mockito.times(1)).close();
        }
    }

    @Test
    public void testInnerCloseClosesSearchContexts() throws Exception {
        Engine.Searcher mock1 = Mockito.mock(Searcher.class);
        Engine.Searcher mock2 = Mockito.mock(Searcher.class);
        collectTask.addSearcher(1, mock1);
        collectTask.addSearcher(2, mock2);
        collectTask.innerClose();
        Mockito.verify(mock1, Mockito.times(1)).close();
        Mockito.verify(mock2, Mockito.times(1)).close();
        Mockito.verify(ramAccountingContext, Mockito.times(1)).close();
    }

    @Test
    public void testKillOnJobCollectContextPropagatesToCrateCollectors() throws Exception {
        Engine.Searcher mock1 = Mockito.mock(Searcher.class);
        MapSideDataCollectOperation collectOperationMock = Mockito.mock(MapSideDataCollectOperation.class);
        CoordinatorTxnCtx txnCtx = CoordinatorTxnCtx.systemTransactionContext();
        CollectTask jobCtx = new CollectTask(collectPhase, txnCtx, collectOperationMock, ramAccountingContext, new TestingRowConsumer(), Mockito.mock(SharedShardContexts.class));
        jobCtx.addSearcher(1, mock1);
        BatchIterator<Row> batchIterator = Mockito.mock(BatchIterator.class);
        Mockito.when(collectOperationMock.createIterator(ArgumentMatchers.eq(txnCtx), ArgumentMatchers.eq(collectPhase), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(jobCtx))).thenReturn(batchIterator);
        jobCtx.prepare();
        jobCtx.start();
        jobCtx.kill(new JobKilledException());
        Mockito.verify(batchIterator, Mockito.times(1)).kill(ArgumentMatchers.any(JobKilledException.class));
        Mockito.verify(mock1, Mockito.times(1)).close();
        Mockito.verify(ramAccountingContext, Mockito.times(1)).close();
    }

    @Test
    public void testThreadPoolNameForDocTables() throws Exception {
        String threadPoolExecutorName = CollectTask.threadPoolName(collectPhase);
        Assert.assertThat(threadPoolExecutorName, Matchers.is(SEARCH));
    }

    @Test
    public void testThreadPoolNameForNonDocTables() throws Exception {
        RoutedCollectPhase collectPhase = Mockito.mock(RoutedCollectPhase.class);
        Routing routing = Mockito.mock(Routing.class);
        Mockito.when(collectPhase.routing()).thenReturn(routing);
        Mockito.when(routing.containsShards(localNodeId)).thenReturn(false);
        // sys.cluster (single row collector)
        Mockito.when(collectPhase.maxRowGranularity()).thenReturn(CLUSTER);
        String threadPoolExecutorName = CollectTask.threadPoolName(collectPhase);
        Assert.assertThat(threadPoolExecutorName, Matchers.is(SEARCH));
        // partition values only of a partitioned doc table (single row collector)
        Mockito.when(collectPhase.maxRowGranularity()).thenReturn(PARTITION);
        threadPoolExecutorName = CollectTask.threadPoolName(collectPhase);
        Assert.assertThat(threadPoolExecutorName, Matchers.is(SEARCH));
        // sys.nodes (single row collector)
        Mockito.when(collectPhase.maxRowGranularity()).thenReturn(NODE);
        threadPoolExecutorName = CollectTask.threadPoolName(collectPhase);
        Assert.assertThat(threadPoolExecutorName, Matchers.is(GET));
        // sys.shards
        Mockito.when(routing.containsShards(localNodeId)).thenReturn(true);
        Mockito.when(collectPhase.maxRowGranularity()).thenReturn(SHARD);
        threadPoolExecutorName = CollectTask.threadPoolName(collectPhase);
        Assert.assertThat(threadPoolExecutorName, Matchers.is(GET));
        Mockito.when(routing.containsShards(localNodeId)).thenReturn(false);
        // information_schema.*
        Mockito.when(collectPhase.maxRowGranularity()).thenReturn(DOC);
        threadPoolExecutorName = CollectTask.threadPoolName(collectPhase);
        Assert.assertThat(threadPoolExecutorName, Matchers.is(SEARCH));
    }
}

