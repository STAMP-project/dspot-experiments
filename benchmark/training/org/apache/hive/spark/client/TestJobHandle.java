/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.spark.client;


import JobHandle.State.CANCELLED;
import JobHandle.State.FAILED;
import JobHandle.State.QUEUED;
import JobHandle.State.STARTED;
import JobHandle.State.SUCCEEDED;
import com.google.common.collect.Lists;
import io.netty.util.concurrent.Promise;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TestJobHandle {
    @Mock
    private SparkClient client;

    @Mock
    private Promise<Serializable> promise;

    @Mock
    private JobHandle.Listener<Serializable> listener;

    @Mock
    private JobHandle.Listener<Serializable> listener2;

    @Test
    public void testStateChanges() throws Exception {
        JobHandleImpl<Serializable> handle = new JobHandleImpl<Serializable>(client, promise, "job", Lists.newArrayList(listener));
        Assert.assertTrue(handle.changeState(QUEUED));
        Mockito.verify(listener).onJobQueued(handle);
        Assert.assertTrue(handle.changeState(STARTED));
        Mockito.verify(listener).onJobStarted(handle);
        handle.addSparkJobId(1);
        Mockito.verify(listener).onSparkJobStarted(ArgumentMatchers.same(handle), ArgumentMatchers.eq(1));
        Assert.assertTrue(handle.changeState(CANCELLED));
        Mockito.verify(listener).onJobCancelled(handle);
        Assert.assertFalse(handle.changeState(STARTED));
        Assert.assertFalse(handle.changeState(FAILED));
        Assert.assertFalse(handle.changeState(SUCCEEDED));
    }

    @Test
    public void testFailedJob() throws Exception {
        JobHandleImpl<Serializable> handle = new JobHandleImpl<Serializable>(client, promise, "job", Lists.newArrayList(listener));
        Throwable cause = new Exception();
        Mockito.when(promise.cause()).thenReturn(cause);
        Assert.assertTrue(handle.changeState(FAILED));
        Mockito.verify(promise).cause();
        Mockito.verify(listener).onJobFailed(handle, cause);
    }

    @Test
    public void testSucceededJob() throws Exception {
        JobHandleImpl<Serializable> handle = new JobHandleImpl<Serializable>(client, promise, "job", Lists.newArrayList(listener));
        Serializable result = new Exception();
        Mockito.when(promise.get()).thenReturn(result);
        Assert.assertTrue(handle.changeState(SUCCEEDED));
        Mockito.verify(promise).get();
        Mockito.verify(listener).onJobSucceeded(handle, result);
    }

    @Test
    public void testImmediateCallback() throws Exception {
        JobHandleImpl<Serializable> handle = new JobHandleImpl<Serializable>(client, promise, "job", Lists.newArrayList(listener, listener2));
        Assert.assertTrue(handle.changeState(QUEUED));
        Mockito.verify(listener).onJobQueued(handle);
        handle.changeState(STARTED);
        handle.addSparkJobId(1);
        handle.changeState(CANCELLED);
        InOrder inOrder = Mockito.inOrder(listener2);
        inOrder.verify(listener2).onSparkJobStarted(ArgumentMatchers.same(handle), ArgumentMatchers.eq(1));
        inOrder.verify(listener2).onJobCancelled(ArgumentMatchers.same(handle));
    }
}

