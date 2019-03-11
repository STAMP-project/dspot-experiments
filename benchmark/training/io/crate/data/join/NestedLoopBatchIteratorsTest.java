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


import io.crate.data.BatchIterator;
import io.crate.data.InMemoryBatchIterator;
import io.crate.data.SentinelRow;
import io.crate.testing.BatchIteratorTester;
import io.crate.testing.Row;
import io.crate.testing.TestingBatchIterators;
import io.crate.testing.TestingRowConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class NestedLoopBatchIteratorsTest {
    private ArrayList<Object[]> threeXThreeRows;

    private ArrayList<Object[]> leftJoinResult;

    private ArrayList<Object[]> rightJoinResult;

    private ArrayList<Object[]> fullJoinResult;

    private ArrayList<Object[]> semiJoinResult;

    private ArrayList<Object[]> antiJoinResult;

    @Test
    public void testNestedLoopBatchIterator() throws Exception {
        BatchIteratorTester tester = new BatchIteratorTester(() -> JoinBatchIterators.crossJoinNL(TestingBatchIterators.range(0, 3), TestingBatchIterators.range(0, 3), new CombinedRow(1, 1)));
        tester.verifyResultAndEdgeCaseBehaviour(threeXThreeRows);
    }

    @Test
    public void testNestedLoopWithBatchedSource() throws Exception {
        BatchIteratorTester tester = new BatchIteratorTester(() -> JoinBatchIterators.crossJoinNL(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 3), 2, 2, null), new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 3), 2, 2, null), new CombinedRow(1, 1)));
        tester.verifyResultAndEdgeCaseBehaviour(threeXThreeRows);
    }

    @Test
    public void testNestedLoopLeftAndRightEmpty() throws Exception {
        BatchIterator<io.crate.data.Row> iterator = JoinBatchIterators.crossJoinNL(InMemoryBatchIterator.empty(SentinelRow.SENTINEL), InMemoryBatchIterator.empty(SentinelRow.SENTINEL), new CombinedRow(0, 0));
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testNestedLoopLeftEmpty() throws Exception {
        BatchIterator<io.crate.data.Row> iterator = JoinBatchIterators.crossJoinNL(InMemoryBatchIterator.empty(SentinelRow.SENTINEL), TestingBatchIterators.range(0, 5), new CombinedRow(0, 1));
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testNestedLoopRightEmpty() throws Exception {
        BatchIterator<io.crate.data.Row> iterator = JoinBatchIterators.crossJoinNL(TestingBatchIterators.range(0, 5), InMemoryBatchIterator.empty(SentinelRow.SENTINEL), new CombinedRow(1, 0));
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testLeftJoin() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.leftJoin(TestingBatchIterators.range(0, 4), TestingBatchIterators.range(2, 6), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(leftJoinResult);
    }

    @Test
    public void testLeftJoinBatchedSource() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.leftJoin(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 4), 2, 2, null), new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(2, 6), 2, 2, null), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(leftJoinResult);
    }

    @Test
    public void testRightJoin() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.rightJoin(TestingBatchIterators.range(0, 4), TestingBatchIterators.range(2, 6), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(rightJoinResult);
    }

    @Test
    public void testRightJoinBatchedSource() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.rightJoin(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 4), 2, 2, null), new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(2, 6), 2, 2, null), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(rightJoinResult);
    }

    @Test
    public void testFullOuterJoin() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.fullOuterJoin(TestingBatchIterators.range(0, 4), TestingBatchIterators.range(2, 6), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(fullJoinResult);
    }

    @Test
    public void testFullOuterJoinBatchedSource() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.fullOuterJoin(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 4), 2, 2, null), new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(2, 6), 2, 2, null), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(fullJoinResult);
    }

    @Test
    public void testMoveToStartWhileRightSideIsActive() {
        BatchIterator<io.crate.data.Row> batchIterator = JoinBatchIterators.crossJoinNL(TestingBatchIterators.range(0, 3), TestingBatchIterators.range(10, 20), new CombinedRow(1, 1));
        Assert.assertThat(batchIterator.moveNext(), Is.is(true));
        Assert.assertThat(batchIterator.currentElement().get(0), Is.is(0));
        Assert.assertThat(batchIterator.currentElement().get(1), Is.is(10));
        batchIterator.moveToStart();
        Assert.assertThat(batchIterator.moveNext(), Is.is(true));
        Assert.assertThat(batchIterator.currentElement().get(0), Is.is(0));
        Assert.assertThat(batchIterator.currentElement().get(1), Is.is(10));
    }

    @Test
    public void testSemiJoin() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.semiJoin(TestingBatchIterators.range(0, 5), TestingBatchIterators.range(2, 6), new CombinedRow(1, 0), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(semiJoinResult);
    }

    @Test
    public void testSemiJoinBatchedSource() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.semiJoin(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 5), 2, 2, null), new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(2, 6), 2, 2, null), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(semiJoinResult);
    }

    @Test
    public void testSemiJoinLeftEmpty() throws Exception {
        BatchIterator<io.crate.data.Row> iterator = JoinBatchIterators.semiJoin(InMemoryBatchIterator.empty(SentinelRow.SENTINEL), TestingBatchIterators.range(0, 5), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testSemiJoinRightEmpty() throws Exception {
        BatchIterator<io.crate.data.Row> iterator = JoinBatchIterators.semiJoin(TestingBatchIterators.range(0, 5), InMemoryBatchIterator.empty(SentinelRow.SENTINEL), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testAntiJoin() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.antiJoin(TestingBatchIterators.range(0, 5), TestingBatchIterators.range(2, 4), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(antiJoinResult);
    }

    @Test
    public void testAntiJoinBatchedSource() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.antiJoin(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 5), 2, 2, null), new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(2, 4), 2, 2, null), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(antiJoinResult);
    }

    @Test
    public void testAntiJoinLeftEmpty() throws Exception {
        BatchIterator<io.crate.data.Row> iterator = JoinBatchIterators.antiJoin(InMemoryBatchIterator.empty(SentinelRow.SENTINEL), TestingBatchIterators.range(0, 5), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        TestingRowConsumer consumer = new TestingRowConsumer();
        consumer.accept(iterator, null);
        Assert.assertThat(consumer.getResult(), Matchers.empty());
    }

    @Test
    public void testAntiJoinRightEmpty() throws Exception {
        Supplier<BatchIterator<io.crate.data.Row>> batchIteratorSupplier = () -> JoinBatchIterators.antiJoin(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0, 3), 2, 2, null), InMemoryBatchIterator.empty(SentinelRow.SENTINEL), new CombinedRow(1, 1), getCol0EqCol1JoinCondition());
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(Arrays.asList(new Object[]{ 0 }, new Object[]{ 1 }, new Object[]{ 2 }));
    }
}

