/**
 * Copyright (c) 2006-2019, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.common.lazy;


import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author pemi
 * @param <T>
 * 		test type
 */
public abstract class AbstractLazyTest<T> {
    private final Supplier<T> NULL_SUPPLIER = () -> null;

    protected Lazy<T> instance;

    @Test
    public void testConcurrency() throws InterruptedException, ExecutionException {
        final int threads = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 10000; i++) {
            final Lazy<T> lazy = newInstance();
            final Callable<T> callable = () -> lazy.getOrCompute(() -> makeFromThread(Thread.currentThread()));
            List<Future<T>> futures = IntStream.rangeClosed(0, threads).mapToObj(( $) -> executorService.submit(callable)).collect(Collectors.toList());
            while (!(futures.stream().allMatch(Future::isDone))) {
            } 
            final Set<T> results = futures.stream().map(AbstractLazyTest::getFutureValue).collect(Collectors.toSet());
            Assertions.assertEquals(1, results.size(), ("Failed at iteration " + i));
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }
}

