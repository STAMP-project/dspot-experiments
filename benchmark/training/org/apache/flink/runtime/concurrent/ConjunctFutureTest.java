/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.concurrent;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.flink.runtime.concurrent.FutureUtils.ConjunctFuture;
import org.apache.flink.util.TestLogger;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for the {@link ConjunctFuture} and {@link FutureUtils.WaitingConjunctFuture}.
 */
@RunWith(Parameterized.class)
public class ConjunctFutureTest extends TestLogger {
    @Parameterized.Parameter
    public ConjunctFutureTest.FutureFactory futureFactory;

    @Test
    public void testConjunctFutureFailsOnEmptyAndNull() throws Exception {
        try {
            futureFactory.createFuture(null);
            Assert.fail();
        } catch (NullPointerException ignored) {
        }
        try {
            futureFactory.createFuture(Arrays.asList(new CompletableFuture<>(), null, new CompletableFuture<>()));
            Assert.fail();
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void testConjunctFutureCompletion() throws Exception {
        // some futures that we combine
        CompletableFuture<Object> future1 = new CompletableFuture<>();
        CompletableFuture<Object> future2 = new CompletableFuture<>();
        CompletableFuture<Object> future3 = new CompletableFuture<>();
        CompletableFuture<Object> future4 = new CompletableFuture<>();
        // some future is initially completed
        future2.complete(new Object());
        // build the conjunct future
        ConjunctFuture<?> result = futureFactory.createFuture(Arrays.asList(future1, future2, future3, future4));
        CompletableFuture<?> resultMapped = result.thenAccept(( value) -> {
        });
        Assert.assertEquals(4, result.getNumFuturesTotal());
        Assert.assertEquals(1, result.getNumFuturesCompleted());
        Assert.assertFalse(result.isDone());
        Assert.assertFalse(resultMapped.isDone());
        // complete two more futures
        future4.complete(new Object());
        Assert.assertEquals(2, result.getNumFuturesCompleted());
        Assert.assertFalse(result.isDone());
        Assert.assertFalse(resultMapped.isDone());
        future1.complete(new Object());
        Assert.assertEquals(3, result.getNumFuturesCompleted());
        Assert.assertFalse(result.isDone());
        Assert.assertFalse(resultMapped.isDone());
        // complete one future again
        future1.complete(new Object());
        Assert.assertEquals(3, result.getNumFuturesCompleted());
        Assert.assertFalse(result.isDone());
        Assert.assertFalse(resultMapped.isDone());
        // complete the final future
        future3.complete(new Object());
        Assert.assertEquals(4, result.getNumFuturesCompleted());
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(resultMapped.isDone());
    }

    @Test
    public void testConjunctFutureFailureOnFirst() throws Exception {
        CompletableFuture<Object> future1 = new CompletableFuture<>();
        CompletableFuture<Object> future2 = new CompletableFuture<>();
        CompletableFuture<Object> future3 = new CompletableFuture<>();
        CompletableFuture<Object> future4 = new CompletableFuture<>();
        // build the conjunct future
        ConjunctFuture<?> result = futureFactory.createFuture(Arrays.asList(future1, future2, future3, future4));
        CompletableFuture<?> resultMapped = result.thenAccept(( value) -> {
        });
        Assert.assertEquals(4, result.getNumFuturesTotal());
        Assert.assertEquals(0, result.getNumFuturesCompleted());
        Assert.assertFalse(result.isDone());
        Assert.assertFalse(resultMapped.isDone());
        future2.completeExceptionally(new IOException());
        Assert.assertEquals(0, result.getNumFuturesCompleted());
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(resultMapped.isDone());
        try {
            result.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
        }
        try {
            resultMapped.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
        }
    }

    @Test
    public void testConjunctFutureFailureOnSuccessive() throws Exception {
        CompletableFuture<Object> future1 = new CompletableFuture<>();
        CompletableFuture<Object> future2 = new CompletableFuture<>();
        CompletableFuture<Object> future3 = new CompletableFuture<>();
        CompletableFuture<Object> future4 = new CompletableFuture<>();
        // build the conjunct future
        ConjunctFuture<?> result = futureFactory.createFuture(Arrays.asList(future1, future2, future3, future4));
        Assert.assertEquals(4, result.getNumFuturesTotal());
        CompletableFuture<?> resultMapped = result.thenAccept(( value) -> {
        });
        future1.complete(new Object());
        future3.complete(new Object());
        future4.complete(new Object());
        future2.completeExceptionally(new IOException());
        Assert.assertEquals(3, result.getNumFuturesCompleted());
        Assert.assertTrue(result.isDone());
        Assert.assertTrue(resultMapped.isDone());
        try {
            result.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
        }
        try {
            resultMapped.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
        }
    }

    /**
     * Tests that the conjunct future returns upon completion the collection of all future values.
     */
    @Test
    public void testConjunctFutureValue() throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> future1 = CompletableFuture.completedFuture(1);
        CompletableFuture<Long> future2 = CompletableFuture.completedFuture(2L);
        CompletableFuture<Double> future3 = new CompletableFuture<>();
        ConjunctFuture<Collection<Number>> result = FutureUtils.combineAll(Arrays.asList(future1, future2, future3));
        Assert.assertFalse(result.isDone());
        future3.complete(0.1);
        Assert.assertTrue(result.isDone());
        Assert.assertThat(result.get(), IsIterableContainingInAnyOrder.<Number>containsInAnyOrder(1, 2L, 0.1));
    }

    @Test
    public void testConjunctOfNone() throws Exception {
        final ConjunctFuture<?> result = futureFactory.createFuture(Collections.<CompletableFuture<Object>>emptyList());
        Assert.assertEquals(0, result.getNumFuturesTotal());
        Assert.assertEquals(0, result.getNumFuturesCompleted());
        Assert.assertTrue(result.isDone());
    }

    /**
     * Factory to create {@link ConjunctFuture} for testing.
     */
    private interface FutureFactory {
        ConjunctFuture<?> createFuture(Collection<? extends CompletableFuture<?>> futures);
    }

    private static class ConjunctFutureFactory implements ConjunctFutureTest.FutureFactory {
        @Override
        public ConjunctFuture<?> createFuture(Collection<? extends CompletableFuture<?>> futures) {
            return FutureUtils.combineAll(futures);
        }
    }

    private static class WaitingFutureFactory implements ConjunctFutureTest.FutureFactory {
        @Override
        public ConjunctFuture<?> createFuture(Collection<? extends CompletableFuture<?>> futures) {
            return FutureUtils.waitForAll(futures);
        }
    }
}

