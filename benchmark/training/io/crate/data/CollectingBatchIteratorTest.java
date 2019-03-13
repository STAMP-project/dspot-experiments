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
package io.crate.data;


import io.crate.testing.BatchIterator;
import io.crate.testing.BatchIteratorTester;
import io.crate.testing.Row;
import io.crate.testing.TestingBatchIterators;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CollectingBatchIteratorTest {
    private List<Object[]> expectedResult = Collections.singletonList(new Object[]{ 45L });

    private ExecutorService executor;

    @Test
    public void testCollectingBatchIterator() throws Exception {
        BatchIteratorTester tester = new BatchIteratorTester(() -> CollectingBatchIterator.summingLong(TestingBatchIterators.range(0L, 10L)));
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testCollectingBatchIteratorWithPagedSource() throws Exception {
        BatchIteratorTester tester = new BatchIteratorTester(() -> CollectingBatchIterator.summingLong(new io.crate.testing.BatchSimulatingIterator(TestingBatchIterators.range(0L, 10L), 2, 5, executor)));
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }

    @Test
    public void testCollectingBatchIteratorPropagatesExceptionOnLoadNextBatch() {
        CompletableFuture<Iterable<Row>> loadItemsFuture = new CompletableFuture<>();
        BatchIterator<Row> collectingBatchIterator = CollectingBatchIterator.newInstance(() -> {
        }, ( t) -> {
        }, () -> loadItemsFuture, false);
        loadItemsFuture.completeExceptionally(new RuntimeException());
        MatcherAssert.assertThat(collectingBatchIterator.loadNextBatch().toCompletableFuture().isCompletedExceptionally(), Matchers.is(true));
    }
}

