/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.tensorflow.core.longrunning.task;


import LongRunningProcessState.DONE;
import LongRunningProcessState.NOT_FOUND;
import LongRunningProcessState.RUNNING;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.ignite.tensorflow.core.longrunning.task.util.LongRunningProcessStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link LongRunningProcessPingTask}.
 */
public class LongRunningProcessPingTaskTest {
    /**
     * Process metadata storage used instead of Apache Ignite node local storage.
     */
    private final ConcurrentMap<UUID, Future<?>> metadataStorage = new ConcurrentHashMap<>();

    /**
     * Tests execution of the task in case process is not found.
     */
    @Test
    public void testCallProcessNotFound() {
        LongRunningProcessPingTask pingTask = createTask(UUID.randomUUID());
        List<LongRunningProcessStatus> statuses = pingTask.call();
        Assert.assertEquals(1, statuses.size());
        LongRunningProcessStatus status = statuses.get(0);
        Assert.assertEquals(NOT_FOUND, status.getState());
        Assert.assertNull(status.getException());
        Assert.assertEquals(0, metadataStorage.size());
    }

    /**
     * Tests execution of the task in case process is running.
     */
    @Test
    public void testCallProcessIsRunning() {
        UUID procId = UUID.randomUUID();
        Future<?> fut = Mockito.mock(Future.class);
        Mockito.doReturn(false).when(fut).isDone();
        metadataStorage.put(procId, fut);
        LongRunningProcessPingTask pingTask = createTask(procId);
        List<LongRunningProcessStatus> statuses = pingTask.call();
        Assert.assertEquals(1, statuses.size());
        LongRunningProcessStatus status = statuses.get(0);
        Assert.assertEquals(RUNNING, status.getState());
        Assert.assertNull(status.getException());
        Assert.assertEquals(1, metadataStorage.size());
    }

    /**
     * Tests execution of the task in case process is done.
     */
    @Test
    public void testCallProcessIsDone() {
        UUID procId = UUID.randomUUID();
        Future<?> fut = Mockito.mock(Future.class);
        Mockito.doReturn(true).when(fut).isDone();
        metadataStorage.put(procId, fut);
        LongRunningProcessPingTask pingTask = createTask(procId);
        List<LongRunningProcessStatus> statuses = pingTask.call();
        Assert.assertEquals(1, statuses.size());
        LongRunningProcessStatus status = statuses.get(0);
        Assert.assertEquals(DONE, status.getState());
        Assert.assertNull(status.getException());
        Assert.assertEquals(1, metadataStorage.size());
    }

    /**
     * Tests execution of the task in case process is done with exception.
     */
    @Test
    public void testCallProcessIsDoneWithException() throws InterruptedException, ExecutionException {
        UUID procId = UUID.randomUUID();
        Future<?> fut = Mockito.mock(Future.class);
        Mockito.doReturn(true).when(fut).isDone();
        Mockito.doThrow(RuntimeException.class).when(fut).get();
        metadataStorage.put(procId, fut);
        LongRunningProcessPingTask pingTask = createTask(procId);
        List<LongRunningProcessStatus> statuses = pingTask.call();
        Assert.assertEquals(1, statuses.size());
        LongRunningProcessStatus status = statuses.get(0);
        Assert.assertEquals(DONE, status.getState());
        Assert.assertNotNull(status.getException());
        Assert.assertTrue(((status.getException()) instanceof RuntimeException));
        Assert.assertEquals(1, metadataStorage.size());
    }
}

