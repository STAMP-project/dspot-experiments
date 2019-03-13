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
package io.crate.execution.engine.distribution.merge;


import io.crate.data.BatchIterator;
import io.crate.data.Row;
import io.crate.data.RowN;
import io.crate.testing.BatchIteratorTester;
import io.crate.testing.BatchSimulatingIterator;
import io.crate.testing.RowGenerator;
import io.crate.testing.TestingBatchIterators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class BatchPagingIteratorTest {
    private ExecutorService executor;

    @Test
    public void testBatchPagingIterator() throws Exception {
        Iterable<Row> rows = RowGenerator.range(0, 3);
        List<Object[]> expectedResult = StreamSupport.stream(rows.spliterator(), false).map(Row::materialize).collect(Collectors.toList());
        BatchIteratorTester tester = new BatchIteratorTester(() -> {
            PassThroughPagingIterator<Integer, Row> pagingIterator = PassThroughPagingIterator.repeatable();
            pagingIterator.merge(singletonList(new KeyIterable<>(0, rows)));
            return new BatchPagingIterator<>(pagingIterator, ( exhaustedIt) -> failedFuture(new IllegalStateException("upstreams exhausted")), () -> true, ( throwable) -> {
            });
        });
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testBatchPagingIteratorWithPagedSource() throws Exception {
        List<Object[]> expectedResult = StreamSupport.stream(RowGenerator.range(0, 10).spliterator(), false).map(Row::materialize).collect(Collectors.toList());
        Supplier<BatchIterator<Row>> batchIteratorSupplier = () -> {
            BatchSimulatingIterator<Row> source = new BatchSimulatingIterator(TestingBatchIterators.range(0, 10), 2, 5, executor);
            Function<Integer, CompletableFuture<? extends Iterable<? extends KeyIterable<Integer, Row>>>> fetchMore = ( exhausted) -> {
                List<Row> rows = new ArrayList<>();
                while (source.moveNext()) {
                    rows.add(new RowN(source.currentElement().materialize()));
                } 
                if (source.allLoaded()) {
                    return CompletableFuture.completedFuture(Collections.singletonList(new KeyIterable(1, rows)));
                }
                // this is intentionally not recursive to not consume the whole source in the first `fetchMore` call
                // but to simulate multiple pages and fetchMore calls
                return source.loadNextBatch().toCompletableFuture().thenApply(( ignored) -> {
                    while (source.moveNext()) {
                        rows.add(new RowN(source.currentElement().materialize()));
                    } 
                    return singleton(new KeyIterable<>(1, rows));
                });
            };
            return new BatchPagingIterator(PassThroughPagingIterator.repeatable(), fetchMore, source::allLoaded, ( throwable) -> {
                if (throwable == null) {
                    source.close();
                } else {
                    source.kill(throwable);
                }
            });
        };
        BatchIteratorTester tester = new BatchIteratorTester(batchIteratorSupplier);
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testFinishPagingIteratorOnClose() {
        BatchPagingIteratorTest.TestPagingIterator pagingIterator = new BatchPagingIteratorTest.TestPagingIterator();
        BatchPagingIterator<Integer> iterator = new BatchPagingIterator(pagingIterator, ( exhaustedIt) -> failedFuture(new IllegalStateException("upstreams exhausted")), () -> true, ( throwable) -> {
        });
        iterator.close();
        MatcherAssert.assertThat(pagingIterator.finishedCalled, Matchers.is(true));
    }

    private static class TestPagingIterator implements PagingIterator<Integer, Row> {
        boolean finishedCalled = false;

        @Override
        public void merge(Iterable<? extends KeyIterable<Integer, Row>> keyIterables) {
        }

        @Override
        public void finish() {
            finishedCalled = true;
        }

        @Override
        public Integer exhaustedIterable() {
            return null;
        }

        @Override
        public Iterable<Row> repeat() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Row next() {
            return null;
        }
    }
}

