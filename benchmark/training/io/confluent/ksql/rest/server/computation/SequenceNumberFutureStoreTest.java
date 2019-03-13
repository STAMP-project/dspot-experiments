/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.server.computation;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SequenceNumberFutureStoreTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private SequenceNumberFutureStore futureStore;

    @Test
    public void shouldReturnFutureForNewSequenceNumber() {
        // When:
        final CompletableFuture<Void> future = futureStore.getFutureForSequenceNumber(2);
        // Then:
        SequenceNumberFutureStoreTest.assertFutureIsNotCompleted(future);
    }

    @Test
    public void shouldReturnFutureForExistingSequenceNumber() {
        // Given:
        final CompletableFuture<Void> existingFuture = futureStore.getFutureForSequenceNumber(2);
        // When:
        final CompletableFuture<Void> newFuture = futureStore.getFutureForSequenceNumber(2);
        // Then:
        MatcherAssert.assertThat(newFuture, CoreMatchers.is(CoreMatchers.sameInstance(existingFuture)));
    }

    @Test
    public void shouldReturnFutureForCompletedSequenceNumber() {
        // Given:
        futureStore.completeFuturesUpToAndIncludingSequenceNumber(2);
        // When:
        final CompletableFuture<Void> future = futureStore.getFutureForSequenceNumber(2);
        // Then:
        SequenceNumberFutureStoreTest.assertFutureIsCompleted(future);
    }

    @Test
    public void shouldCompleteFutures() {
        // Given:
        final CompletableFuture<Void> firstFuture = futureStore.getFutureForSequenceNumber(2);
        final CompletableFuture<Void> secondFuture = futureStore.getFutureForSequenceNumber(3);
        // When:
        futureStore.completeFuturesUpToAndIncludingSequenceNumber(2);
        // Then:
        SequenceNumberFutureStoreTest.assertFutureIsCompleted(firstFuture);
        SequenceNumberFutureStoreTest.assertFutureIsNotCompleted(secondFuture);
    }

    @Test
    public void shouldBeThreadSafe() {
        // When:
        final List<CompletableFuture<Void>> futures = IntStream.range(1, 11).parallel().mapToObj(( idx) -> {
            final CompletableFuture<Void> f = futureStore.getFutureForSequenceNumber(idx);
            if ((idx % 10) == 0) {
                futureStore.completeFuturesUpToAndIncludingSequenceNumber(idx);
            }
            return f;
        }).collect(Collectors.toList());
        // Then:
        MatcherAssert.assertThat(futures.stream().allMatch(CompletableFuture::isDone), CoreMatchers.is(true));
    }
}

