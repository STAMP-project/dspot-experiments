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
package io.crate.execution.engine.join;


import io.crate.breaker.RowAccounting;
import io.crate.data.BatchIterator;
import io.crate.data.Row;
import io.crate.data.join.CombinedRow;
import io.crate.testing.TestingBatchIterators;
import io.crate.testing.TestingRowConsumer;
import org.elasticsearch.common.breaker.CircuitBreaker;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class HashInnerJoinBatchIteratorMemoryTest {
    private final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);

    @Test
    public void testReleaseAccountingRows() throws Exception {
        HashInnerJoinBatchIteratorMemoryTest.TestRamAccountingBatchIterator leftIterator = new HashInnerJoinBatchIteratorMemoryTest.TestRamAccountingBatchIterator(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 12), 3, 3, null), Mockito.mock(RowAccounting.class));
        BatchIterator<Row> rightIterator = new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 10), 2, 4, null);
        Mockito.when(circuitBreaker.getLimit()).thenReturn(110L);
        Mockito.when(circuitBreaker.getUsed()).thenReturn(10L);
        BatchIterator<Row> it = new HashInnerJoinBatchIterator(leftIterator, rightIterator, new CombinedRow(1, 1), HashInnerJoinBatchIteratorMemoryTest.getCol0EqCol1JoinCondition(), HashInnerJoinBatchIteratorMemoryTest.getHashForLeft(), HashInnerJoinBatchIteratorMemoryTest.getHashForRight(), () -> 2);
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(it, null);
        consumer.getResult();
        MatcherAssert.assertThat(leftIterator.countCallsForReleaseMem, Matchers.is(7));
    }

    private class TestRamAccountingBatchIterator extends RamAccountingBatchIterator<Row> {
        private int countCallsForReleaseMem = 0;

        private TestRamAccountingBatchIterator(BatchIterator<Row> delegatePagingIterator, RowAccounting rowAccounting) {
            super(delegatePagingIterator, rowAccounting);
        }

        @Override
        public void releaseAccountedRows() {
            (countCallsForReleaseMem)++;
            super.releaseAccountedRows();
        }
    }
}

