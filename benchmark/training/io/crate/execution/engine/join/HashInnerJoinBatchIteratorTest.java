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


import ThreadLeakScope.Scope;
import com.carrotsearch.randomizedtesting.RandomizedRunner;
import com.carrotsearch.randomizedtesting.annotations.Name;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import io.crate.data.BatchIterator;
import io.crate.data.Row;
import io.crate.data.join.CombinedRow;
import io.crate.testing.BatchIteratorTester;
import java.util.List;
import java.util.function.Supplier;
import org.elasticsearch.common.breaker.CircuitBreaker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(RandomizedRunner.class)
@ThreadLeakScope(Scope.NONE)
public class HashInnerJoinBatchIteratorTest {
    private final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);

    private final List<Object[]> expectedResult;

    private final Supplier<RamAccountingBatchIterator<Row>> leftIterator;

    private final Supplier<RamAccountingBatchIterator<Row>> rightIterator;

    public HashInnerJoinBatchIteratorTest(@SuppressWarnings("unused")
    @Name("dataSetName")
    String testName, @Name("dataForLeft")
    Supplier<RamAccountingBatchIterator<Row>> leftIterator, @Name("dataForRight")
    Supplier<RamAccountingBatchIterator<Row>> rightIterator, @Name("expectedResult")
    List<Object[]> expectedResult) {
        this.leftIterator = leftIterator;
        this.rightIterator = rightIterator;
        this.expectedResult = expectedResult;
        Mockito.when(circuitBreaker.getLimit()).thenReturn(110L);
        Mockito.when(circuitBreaker.getUsed()).thenReturn(10L);
    }

    @Test
    public void testInnerHashJoin() throws Exception {
        Supplier<BatchIterator<Row>> batchIteratorSupplier = () -> new HashInnerJoinBatchIterator(leftIterator.get(), rightIterator.get(), new CombinedRow(1, 1), HashInnerJoinBatchIteratorTest.getCol0EqCol1JoinCondition(), HashInnerJoinBatchIteratorTest.getHashForLeft(), HashInnerJoinBatchIteratorTest.getHashForRight(), () -> 5);
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testInnerHashJoinWithHashCollisions() throws Exception {
        Supplier<BatchIterator<Row>> batchIteratorSupplier = () -> new HashInnerJoinBatchIterator(leftIterator.get(), rightIterator.get(), new CombinedRow(1, 1), HashInnerJoinBatchIteratorTest.getCol0EqCol1JoinCondition(), HashInnerJoinBatchIteratorTest.getHashWithCollisions(), HashInnerJoinBatchIteratorTest.getHashWithCollisions(), () -> 5);
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testInnerHashJoinWithBlockSizeSmallerThanDataSet() throws Exception {
        Supplier<BatchIterator<Row>> batchIteratorSupplier = () -> new HashInnerJoinBatchIterator(leftIterator.get(), rightIterator.get(), new CombinedRow(1, 1), HashInnerJoinBatchIteratorTest.getCol0EqCol1JoinCondition(), HashInnerJoinBatchIteratorTest.getHashForLeft(), HashInnerJoinBatchIteratorTest.getHashForRight(), () -> 1);
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testInnerHashJoinWithBlockSizeBiggerThanIteratorBatchSize() throws Exception {
        Supplier<BatchIterator<Row>> batchIteratorSupplier = () -> new HashInnerJoinBatchIterator(leftIterator.get(), rightIterator.get(), new CombinedRow(1, 1), HashInnerJoinBatchIteratorTest.getCol0EqCol1JoinCondition(), HashInnerJoinBatchIteratorTest.getHashForLeft(), HashInnerJoinBatchIteratorTest.getHashForRight(), () -> 3);
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }
}

