/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractInvocationFuture_ClosedExecutorTest extends AbstractInvocationFuture_AbstractTest {
    @Test
    public void whenCompleteBeforeShutdown_thenCallback() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AbstractInvocationFuture_AbstractTest.TestFuture future = new AbstractInvocationFuture_AbstractTest.TestFuture(executorService, logger);
        complete(new Object());
        executorService.shutdown();
        final AtomicBoolean onFailure = new AtomicBoolean();
        future.andThen(new ExecutionCallback() {
            @Override
            public void onResponse(Object response) {
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof RejectedExecutionException) {
                    onFailure.set(true);
                }
            }
        });
        Assert.assertTrue(onFailure.get());
    }

    @Test
    public void whenCompleteAfterShutdown_thenCallback() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AbstractInvocationFuture_AbstractTest.TestFuture future = new AbstractInvocationFuture_AbstractTest.TestFuture(executorService, logger);
        executorService.shutdown();
        complete(new Object());
        final AtomicBoolean onFailure = new AtomicBoolean();
        andThen(new ExecutionCallback() {
            @Override
            public void onResponse(Object response) {
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof RejectedExecutionException) {
                    onFailure.set(true);
                }
            }
        });
        Assert.assertTrue(onFailure.get());
    }
}

