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
package io.crate.concurrent;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CompletableFuturesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void failedFutureIsCompletedExceptionally() {
        Exception exception = new Exception("failed future");
        CompletableFuture<Object> failedFuture = CompletableFutures.failedFuture(exception);
        Assert.assertThat(failedFuture.isCompletedExceptionally(), Matchers.is(true));
    }

    @Test
    public void testAllAsListFailurePropagation() throws Exception {
        CompletableFuture<Integer> f1 = new CompletableFuture<>();
        CompletableFuture<Integer> f2 = new CompletableFuture<>();
        CompletableFuture<List<Integer>> all = CompletableFutures.allAsList(Arrays.asList(f1, f2));
        f1.completeExceptionally(new IllegalStateException("dummy"));
        Assert.assertThat("future must wait for all subFutures", all.isDone(), Matchers.is(false));
        f2.complete(2);
        expectedException.expectCause(Matchers.instanceOf(IllegalStateException.class));
        all.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void testAllAsListResultContainsListOfResults() throws Exception {
        CompletableFuture<Integer> f1 = new CompletableFuture<>();
        CompletableFuture<Integer> f2 = new CompletableFuture<>();
        CompletableFuture<List<Integer>> all = CompletableFutures.allAsList(Arrays.asList(f1, f2));
        f1.complete(10);
        f2.complete(20);
        Assert.assertThat(all.get(10, TimeUnit.SECONDS), Matchers.contains(10, 20));
    }

    @Test
    public void testSupplyAsyncReturnsFailedFutureOnException() throws Exception {
        Executor rejectingExecutor = ( command) -> {
            throw new RejectedExecutionException("rejected");
        };
        CompletableFuture<Object> future = CompletableFutures.supplyAsync(() -> null, rejectingExecutor);
        Assert.assertThat(future.isCompletedExceptionally(), Matchers.is(true));
    }
}

