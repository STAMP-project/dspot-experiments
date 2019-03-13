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


import AbstractInvocationFuture.VOID;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractInvocationFuture_IsDoneTest extends AbstractInvocationFuture_AbstractTest {
    @Test
    public void whenVoid() {
        Assert.assertFalse(isDone());
    }

    @Test
    public void whenNullResult() {
        future.complete(null);
        Assert.assertTrue(isDone());
    }

    @Test
    public void whenNoneNullResult() {
        future.complete(value);
        Assert.assertTrue(isDone());
    }

    @Test
    public void whenExceptionalResult() {
        complete(new RuntimeException());
        Assert.assertTrue(isDone());
    }

    @Test
    public void whenBlockingThread() {
        HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return get();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNotSame(VOID, getState());
            }
        });
        Assert.assertFalse(isDone());
    }

    @Test
    public void whenCallbackWithoutCustomExecutor() {
        future.andThen(Mockito.mock(ExecutionCallback.class));
        Assert.assertFalse(isDone());
    }

    @Test
    public void whenCallbackWithCustomExecutor() {
        andThen(Mockito.mock(ExecutionCallback.class), Mockito.mock(Executor.class));
        Assert.assertFalse(isDone());
    }

    @Test
    public void whenMultipleWaiters() {
        andThen(Mockito.mock(ExecutionCallback.class), Mockito.mock(Executor.class));
        andThen(Mockito.mock(ExecutionCallback.class), Mockito.mock(Executor.class));
        Assert.assertFalse(isDone());
    }
}

