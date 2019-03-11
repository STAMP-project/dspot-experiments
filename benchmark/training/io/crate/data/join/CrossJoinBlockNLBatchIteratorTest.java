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
package io.crate.data.join;


import ThreadLeakScope.Scope;
import com.carrotsearch.randomizedtesting.RandomizedRunner;
import com.carrotsearch.randomizedtesting.annotations.Name;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import io.crate.breaker.RowAccounting;
import io.crate.data.BatchIterator;
import io.crate.data.InMemoryBatchIterator;
import io.crate.data.Row;
import io.crate.data.SentinelRow;
import io.crate.testing.BatchIteratorTester;
import io.crate.testing.TestingRowConsumer;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(RandomizedRunner.class)
@ThreadLeakScope(Scope.NONE)
public class CrossJoinBlockNLBatchIteratorTest {
    private final int id;

    private final Supplier<BatchIterator<Row>> left;

    private final Supplier<BatchIterator<Row>> right;

    private final IntSupplier blockSizeCalculator;

    private final CrossJoinBlockNLBatchIteratorTest.TestingRowAccounting testingRowAccounting;

    private final List<Object[]> expectedResults;

    private int expectedRowsLeft;

    private int expectedRowsRight;

    public CrossJoinBlockNLBatchIteratorTest(@Name("id")
    int id, Supplier<BatchIterator<Row>> left, Supplier<BatchIterator<Row>> right, IntSupplier blockSizeCalculator) throws Exception {
        this.id = id;
        this.left = left;
        this.right = right;
        this.blockSizeCalculator = blockSizeCalculator;
        this.testingRowAccounting = new CrossJoinBlockNLBatchIteratorTest.TestingRowAccounting();
        this.expectedResults = createExpectedResult(left.get(), right.get());
    }

    @Test
    public void testNestedLoopBatchIterator() throws Exception {
        BatchIteratorTester tester = new BatchIteratorTester(() -> JoinBatchIterators.crossJoinBlockNL(left.get(), right.get(), new CombinedRow(1, 1), blockSizeCalculator, testingRowAccounting));
        tester.verifyResultAndEdgeCaseBehaviour(expectedResults, ( it) -> {
            Assert.assertThat(testingRowAccounting.numRows, Is.is(expectedRowsLeft));
            Assert.assertThat(testingRowAccounting.numReleaseCalled, Matchers.greaterThan(((expectedRowsLeft) / (blockSizeCalculator.getAsInt()))));
        });
    }

    @Test
    public void testNestedLoopWithBatchedSource() throws Exception {
        int batchSize = 50;
        BatchIteratorTester tester = new BatchIteratorTester(() -> JoinBatchIterators.crossJoinBlockNL(new io.crate.testing.BatchSimulatingIterator(left.get(), batchSize, (((expectedRowsLeft) / batchSize) + 1), null), new io.crate.testing.BatchSimulatingIterator(right.get(), batchSize, (((expectedRowsRight) / batchSize) + 1), null), new CombinedRow(1, 1), blockSizeCalculator, testingRowAccounting));
        tester.verifyResultAndEdgeCaseBehaviour(expectedResults, ( it) -> {
            Assert.assertThat(testingRowAccounting.numRows, Is.is(expectedRowsLeft));
            Assert.assertThat(testingRowAccounting.numReleaseCalled, Matchers.greaterThan(((expectedRowsLeft) / (blockSizeCalculator.getAsInt()))));
        });
    }

    @Test
    public void testNestedLoopLeftAndRightEmpty() throws Exception {
        BatchIterator<Row> iterator = JoinBatchIterators.crossJoinBlockNL(InMemoryBatchIterator.empty(SentinelRow.SENTINEL), InMemoryBatchIterator.empty(SentinelRow.SENTINEL), new CombinedRow(0, 0), blockSizeCalculator, testingRowAccounting);
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testNestedLoopLeftEmpty() throws Exception {
        BatchIterator<Row> iterator = JoinBatchIterators.crossJoinBlockNL(InMemoryBatchIterator.empty(SentinelRow.SENTINEL), right.get(), new CombinedRow(0, 1), blockSizeCalculator, testingRowAccounting);
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testNestedLoopRightEmpty() throws Exception {
        BatchIterator<Row> iterator = JoinBatchIterators.crossJoinBlockNL(left.get(), InMemoryBatchIterator.empty(SentinelRow.SENTINEL), new CombinedRow(1, 0), blockSizeCalculator, testingRowAccounting);
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testMoveToStartWhileRightSideIsActive() {
        BatchIterator<Row> batchIterator = JoinBatchIterators.crossJoinBlockNL(left.get(), right.get(), new CombinedRow(1, 1), blockSizeCalculator, testingRowAccounting);
        Assert.assertThat(batchIterator.moveNext(), Is.is(true));
        Assert.assertThat(expectedResults.toArray(), Matchers.hasItemInArray(batchIterator.currentElement().materialize()));
        batchIterator.moveToStart();
        Assert.assertThat(batchIterator.moveNext(), Is.is(true));
        Assert.assertThat(expectedResults.toArray(), Matchers.hasItemInArray(batchIterator.currentElement().materialize()));
    }

    private static class TestingRowAccounting implements RowAccounting {
        int numRows;

        int numReleaseCalled;

        boolean closed;

        @Override
        public void accountForAndMaybeBreak(Row row) {
            if (closed) {
                throw new RuntimeException("Already closed!");
            }
            (numRows)++;
        }

        @Override
        public void release() {
            if (closed) {
                throw new RuntimeException("Already closed!");
            }
            (numReleaseCalled)++;
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}

