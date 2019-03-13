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


import com.hazelcast.core.HazelcastException;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractInvocationFuture_GetSafely extends AbstractInvocationFuture_AbstractTest {
    @Test
    public void whenNormalResponse() throws InterruptedException, ExecutionException {
        future.complete(value);
        Future joinFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return join();
            }
        });
        HazelcastTestSupport.assertCompletesEventually(joinFuture);
        Assert.assertSame(value, joinFuture.get());
    }

    @Test
    public void whenRuntimeException() throws Exception {
        ExpectedRuntimeException ex = new ExpectedRuntimeException();
        future.complete(ex);
        Future joinFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return join();
            }
        });
        HazelcastTestSupport.assertCompletesEventually(joinFuture);
        try {
            joinFuture.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertSame(ex, e.getCause());
        }
    }

    @Test
    public void whenRegularException() throws Exception {
        Exception ex = new Exception();
        complete(ex);
        Future joinFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return join();
            }
        });
        HazelcastTestSupport.assertCompletesEventually(joinFuture);
        try {
            joinFuture.get();
            Assert.fail();
        } catch (ExecutionException e) {
            // The 'ex' is wrapped in an unchecked HazelcastException
            HazelcastException hzEx = HazelcastTestSupport.assertInstanceOf(HazelcastException.class, e.getCause());
            Assert.assertSame(ex, hzEx.getCause());
        }
    }
}

