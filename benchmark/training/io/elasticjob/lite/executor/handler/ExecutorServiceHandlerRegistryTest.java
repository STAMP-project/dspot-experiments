/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.executor.handler;


import io.elasticjob.lite.executor.handler.impl.DefaultExecutorServiceHandler;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public final class ExecutorServiceHandlerRegistryTest {
    @Test
    public void assertRemove() {
        ExecutorService actual = ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job", new DefaultExecutorServiceHandler());
        ExecutorServiceHandlerRegistry.remove("test_job");
        Assert.assertThat(actual, IsNot.not(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job", new DefaultExecutorServiceHandler())));
    }

    @Test
    public void assertGetExecutorServiceHandlerForSameThread() {
        Assert.assertThat(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job", new DefaultExecutorServiceHandler()), Is.is(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job", new DefaultExecutorServiceHandler())));
    }

    @Test
    public void assertGetExecutorServiceHandlerForConcurrent() throws InterruptedException {
        int threadCount = 100;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<ExecutorService> set = new CopyOnWriteArraySet<>();
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(new ExecutorServiceHandlerRegistryTest.GetExecutorServiceHandlerTask(barrier, latch, set));
        }
        latch.await();
        Assert.assertThat(set.size(), Is.is(1));
        Assert.assertThat(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job", new DefaultExecutorServiceHandler()), Is.is(set.iterator().next()));
    }

    @RequiredArgsConstructor
    class GetExecutorServiceHandlerTask implements Runnable {
        private final CyclicBarrier barrier;

        private final CountDownLatch latch;

        private final Set<ExecutorService> set;

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                ex.printStackTrace();
            }
            set.add(ExecutorServiceHandlerRegistry.getExecutorServiceHandler("test_job", new DefaultExecutorServiceHandler()));
            latch.countDown();
        }
    }
}

