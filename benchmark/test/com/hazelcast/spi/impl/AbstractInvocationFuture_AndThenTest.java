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
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractInvocationFuture_AndThenTest extends AbstractInvocationFuture_AbstractTest {
    @Test(expected = IllegalArgumentException.class)
    public void whenNullCallback0() {
        future.andThen(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNullCallback1() {
        future.andThen(null, Mockito.mock(Executor.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNullExecutor() {
        andThen(Mockito.mock(ExecutionCallback.class), null);
    }

    @Test
    public void whenCustomerExecutor() {
        Executor defaultExecutor = Mockito.mock(Executor.class);
        Executor customExecutor = Mockito.mock(Executor.class);
        AbstractInvocationFuture_AbstractTest.TestFuture future = new AbstractInvocationFuture_AbstractTest.TestFuture(defaultExecutor, logger);
        final ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback, customExecutor);
        complete(value);
        Mockito.verify(customExecutor).execute(ArgumentMatchers.any(Runnable.class));
        Mockito.verifyZeroInteractions(defaultExecutor);
    }

    @Test
    public void whenDefaultExecutor() {
        Executor defaultExecutor = Mockito.mock(Executor.class);
        AbstractInvocationFuture_AbstractTest.TestFuture future = new AbstractInvocationFuture_AbstractTest.TestFuture(defaultExecutor, logger);
        final ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback);
        complete(value);
        Mockito.verify(defaultExecutor).execute(ArgumentMatchers.any(Runnable.class));
    }

    @Test
    public void whenResponseAlreadyAvailable() {
        complete(value);
        final ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Mockito.verify(callback).onResponse(value);
            }
        });
    }

    @Test
    public void whenResponseAvailableAfterSomeWaiting() {
        final ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback);
        HazelcastTestSupport.sleepSeconds(5);
        Mockito.verifyZeroInteractions(callback);
        complete(value);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Mockito.verify(callback).onResponse(value);
            }
        });
    }

    @Test
    public void whenExceptionalResponseAvailableAfterSomeWaiting() {
        final ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback);
        HazelcastTestSupport.sleepSeconds(5);
        Mockito.verifyZeroInteractions(callback);
        final ExpectedRuntimeException ex = new ExpectedRuntimeException();
        future.complete(ex);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Mockito.verify(callback).onFailure(ex);
            }
        });
    }

    @Test
    public void whenMultipleCallbacks() throws InterruptedException, ExecutionException {
        List<ExecutionCallback> callbacks = new LinkedList<ExecutionCallback>();
        for (int k = 0; k < 10; k++) {
            ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
            future.andThen(callback);
        }
        HazelcastTestSupport.sleepSeconds(5);
        complete(value);
        for (ExecutionCallback callback : callbacks) {
            Mockito.verify(callback).onResponse(value);
        }
        Assert.assertSame(value, getState());
    }

    @Test
    public void whenExceptionalResponseAvailableAfterSomeWaiting_MemberLeftException() {
        final ExecutionCallback callback = Mockito.mock(ExecutionCallback.class);
        future.andThen(callback);
        final MemberLeftException ex = new MemberLeftException();
        future.complete(ex);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Mockito.verify(callback).onFailure(ex);
            }
        });
    }
}

