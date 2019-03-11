/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.monitoring;


import Mode.AGSExecutor;
import Mode.FunctionExecutor;
import Mode.OneTaskOnlyExecutor;
import Mode.PooledExecutor;
import Mode.ScheduledThreadExecutor;
import Mode.SerialQueuedExecutor;
import org.apache.geode.internal.monitoring.executor.AbstractExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Contains simple tests for the {@link org.apache.geode.internal.monitoring.ThreadsMonitoringImpl}.
 *
 * @since Geode 1.5
 */
public class ThreadsMonitoringImplJUnitTest {
    private ThreadsMonitoringImpl threadsMonitoringImpl;

    /**
     * Tests "start monitor" modes
     */
    @Test
    public void testStartMonitor() {
        Assert.assertTrue(threadsMonitoringImpl.startMonitor(FunctionExecutor));
        Assert.assertTrue(threadsMonitoringImpl.startMonitor(PooledExecutor));
        Assert.assertTrue(threadsMonitoringImpl.startMonitor(SerialQueuedExecutor));
        Assert.assertTrue(threadsMonitoringImpl.startMonitor(OneTaskOnlyExecutor));
        Assert.assertTrue(threadsMonitoringImpl.startMonitor(ScheduledThreadExecutor));
        Assert.assertTrue(threadsMonitoringImpl.startMonitor(AGSExecutor));
    }

    /**
     * Tests closure
     */
    @Test
    public void testClosure() {
        Assert.assertTrue(((threadsMonitoringImpl.getThreadsMonitoringProcess()) != null));
        Assert.assertFalse(threadsMonitoringImpl.isClosed());
        threadsMonitoringImpl.close();
        Assert.assertTrue(threadsMonitoringImpl.isClosed());
        Assert.assertFalse(((threadsMonitoringImpl.getThreadsMonitoringProcess()) != null));
    }

    @Test
    public void updateThreadStatus() {
        AbstractExecutor executor = Mockito.mock(AbstractExecutor.class);
        long threadId = Thread.currentThread().getId();
        threadsMonitoringImpl.getMonitorMap().put(threadId, executor);
        threadsMonitoringImpl.updateThreadStatus();
        // also test the case where there is no AbstractExcecutor present
        threadsMonitoringImpl.getMonitorMap().remove(threadId);
        threadsMonitoringImpl.updateThreadStatus();
        Mockito.verify(executor, Mockito.times(1)).setStartTime(ArgumentMatchers.any(Long.class));
    }
}

