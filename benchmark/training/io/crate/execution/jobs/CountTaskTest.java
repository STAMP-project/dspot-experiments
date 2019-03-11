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
package io.crate.execution.jobs;


import com.carrotsearch.hppc.IntIndexedContainer;
import io.crate.exceptions.JobKilledException;
import io.crate.exceptions.UnhandledServerException;
import io.crate.execution.engine.collect.count.CountOperation;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.TransactionContext;
import io.crate.test.CauseMatcher;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingRowConsumer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.elasticsearch.index.Index;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CountTaskTest extends CrateUnitTest {
    private TransactionContext txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    @Test
    public void testClose() throws Exception {
        CompletableFuture<Long> future = new CompletableFuture<>();
        CountOperation countOperation = Mockito.mock(CountOperation.class);
        Mockito.when(countOperation.count(ArgumentMatchers.eq(txnCtx), ArgumentMatchers.any(), ArgumentMatchers.any(Symbol.class))).thenReturn(future);
        CountTask countTask = new CountTask(CountTaskTest.countPhaseWithId(1), txnCtx, countOperation, new TestingRowConsumer(), null);
        countTask.prepare();
        countTask.start();
        future.complete(1L);
        assertTrue(countTask.isClosed());
        // assure that there was no exception
        countTask.completionFuture().get();
        // on error
        future = new CompletableFuture<>();
        Mockito.when(countOperation.count(ArgumentMatchers.eq(txnCtx), ArgumentMatchers.any(), ArgumentMatchers.any(Symbol.class))).thenReturn(future);
        countTask = new CountTask(CountTaskTest.countPhaseWithId(2), txnCtx, countOperation, new TestingRowConsumer(), null);
        countTask.prepare();
        countTask.start();
        future.completeExceptionally(new UnhandledServerException("dummy"));
        assertTrue(countTask.isClosed());
        expectedException.expectCause(CauseMatcher.cause(UnhandledServerException.class));
        countTask.completionFuture().get();
    }

    @Test
    public void testKillOperationFuture() throws Exception {
        CompletableFuture<Long> future = Mockito.mock(CompletableFuture.class);
        CountOperation countOperation = new CountTaskTest.FakeCountOperation(future);
        CountTask countTask = new CountTask(CountTaskTest.countPhaseWithId(1), txnCtx, countOperation, new TestingRowConsumer(), null);
        countTask.prepare();
        countTask.start();
        countTask.kill(new JobKilledException());
        Mockito.verify(future, Mockito.times(1)).cancel(true);
        assertTrue(countTask.isClosed());
    }

    private static class FakeCountOperation implements CountOperation {
        private final CompletableFuture<Long> future;

        public FakeCountOperation(CompletableFuture<Long> future) {
            this.future = future;
        }

        @Override
        public CompletableFuture<Long> count(TransactionContext txnCtx, Map<String, IntIndexedContainer> indexShardMap, Symbol filter) {
            return future;
        }

        @Override
        public long count(TransactionContext txnCtx, Index index, int shardId, Symbol filter) {
            return 0;
        }
    }
}

