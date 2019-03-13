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
package io.crate.execution.engine.collect.collectors;


import DataTypes.LONG;
import LongType.INSTANCE;
import com.google.common.util.concurrent.MoreExecutors;
import io.crate.analyze.OrderBy;
import io.crate.breaker.RamAccountingContext;
import io.crate.breaker.RowAccounting;
import io.crate.breaker.RowAccountingWithEstimators;
import io.crate.data.BatchIterator;
import io.crate.data.Row;
import io.crate.exceptions.CircuitBreakingException;
import io.crate.execution.engine.sort.OrderingByPosition;
import io.crate.metadata.Reference;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.BatchIteratorTester;
import io.crate.testing.TestingHelpers;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.search.IndexSearcher;
import org.elasticsearch.common.breaker.CircuitBreaker;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OrderedLuceneBatchIteratorFactoryTest extends CrateUnitTest {
    private static final RowAccounting ROW_ACCOUNTING = new RowAccountingWithEstimators(Collections.singleton(INSTANCE), new RamAccountingContext("dummy", new org.elasticsearch.common.breaker.NoopCircuitBreaker(CircuitBreaker.FIELDDATA)));

    private String columnName = "x";

    private Reference reference = TestingHelpers.createReference(columnName, LONG);

    private IndexSearcher searcher1;

    private IndexSearcher searcher2;

    private OrderBy orderBy;

    private List<Object[]> expectedResult;

    private boolean[] reverseFlags = new boolean[]{ true };

    private Boolean[] nullsFirst = new Boolean[]{ null };

    @Test
    public void testOrderedLuceneBatchIterator() throws Exception {
        BatchIteratorTester tester = new BatchIteratorTester(() -> {
            LuceneOrderedDocCollector collector1 = createOrderedCollector(searcher1, 1);
            LuceneOrderedDocCollector collector2 = createOrderedCollector(searcher2, 2);
            return OrderedLuceneBatchIteratorFactory.newInstance(Arrays.asList(collector1, collector2), OrderingByPosition.rowOrdering(new int[]{ 0 }, reverseFlags, nullsFirst), ROW_ACCOUNTING, MoreExecutors.directExecutor(), () -> 1, true);
        });
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testSingleCollectorOrderedLuceneBatchIteratorTripsCircuitBreaker() throws Exception {
        RowAccounting rowAccounting = Mockito.mock(RowAccounting.class);
        CircuitBreakingException circuitBreakingException = new CircuitBreakingException("tripped circuit breaker");
        Mockito.doThrow(circuitBreakingException).when(rowAccounting).accountForAndMaybeBreak(ArgumentMatchers.any(Row.class));
        BatchIterator<Row> rowBatchIterator = OrderedLuceneBatchIteratorFactory.newInstance(Arrays.asList(createOrderedCollector(searcher1, 1)), OrderingByPosition.rowOrdering(new int[]{ 0 }, reverseFlags, nullsFirst), rowAccounting, MoreExecutors.directExecutor(), () -> 2, true);
        consumeIteratorAndVerifyResultIsException(rowBatchIterator, circuitBreakingException);
    }

    @Test
    public void testOrderedLuceneBatchIteratorWithMultipleCollectorsTripsCircuitBreaker() throws Exception {
        RowAccounting rowAccounting = Mockito.mock(RowAccountingWithEstimators.class);
        CircuitBreakingException circuitBreakingException = new CircuitBreakingException("tripped circuit breaker");
        Mockito.doThrow(circuitBreakingException).when(rowAccounting).accountForAndMaybeBreak(ArgumentMatchers.any(Row.class));
        LuceneOrderedDocCollector collector1 = createOrderedCollector(searcher1, 1);
        LuceneOrderedDocCollector collector2 = createOrderedCollector(searcher2, 2);
        BatchIterator<Row> rowBatchIterator = OrderedLuceneBatchIteratorFactory.newInstance(Arrays.asList(collector1, collector2), OrderingByPosition.rowOrdering(new int[]{ 0 }, reverseFlags, nullsFirst), rowAccounting, MoreExecutors.directExecutor(), () -> 1, true);
        consumeIteratorAndVerifyResultIsException(rowBatchIterator, circuitBreakingException);
    }
}

