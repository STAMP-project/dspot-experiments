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
package org.apache.geode.cache.query.internal;


import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.geode.cache.query.QueryExecutionLowMemoryException;
import org.apache.geode.cache.query.QueryExecutionTimeoutException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * although max_execution_time is set as 10ms, the monitor thread can sleep more than the specified
 * time, so query will be cancelled at un-deterministic time after 10ms. We cannot assert on
 * specific time at which the query will be cancelled. We can only assert that the query will be
 * cancelled at one point after 10ms.
 */
public class QueryMonitorTest {
    private QueryMonitor monitor;

    private long max_execution_time = 5;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private ArgumentCaptor<Runnable> captor;

    @Test
    public void monitorQueryThreadCqQueryIsNotMonitored() {
        DefaultQuery query = Mockito.mock(DefaultQuery.class);
        Mockito.when(query.isCqQuery()).thenReturn(true);
        monitor.monitorQueryThread(query);
        // Verify that the expiration task was not scheduled for the CQ query
        Mockito.verify(scheduledThreadPoolExecutor, Mockito.never()).schedule(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.isA(TimeUnit.class));
    }

    @Test
    public void monitorQueryThreadLowMemoryExceptionThrown() {
        DefaultQuery query = Mockito.mock(DefaultQuery.class);
        monitor.setLowMemory(true, 100);
        assertThatThrownBy(() -> monitor.monitorQueryThread(query)).isExactlyInstanceOf(QueryExecutionLowMemoryException.class);
    }

    @Test
    public void monitorQueryThreadExpirationTaskScheduled() {
        DefaultQuery query = Mockito.mock(DefaultQuery.class);
        monitor.monitorQueryThread(query);
        Mockito.verify(scheduledThreadPoolExecutor, Mockito.times(1)).schedule(captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.isA(TimeUnit.class));
        captor.getValue().run();
        Mockito.verify(query, Mockito.times(1)).setQueryCanceledException(ArgumentMatchers.isA(QueryExecutionTimeoutException.class));
        assertThatThrownBy(QueryMonitor::throwExceptionIfQueryOnCurrentThreadIsCanceled).isExactlyInstanceOf(QueryExecutionCanceledException.class);
    }

    @Test
    public void setLowMemoryTrueThenFalseAllowsSubsequentMonitoring() {
        monitor.setLowMemory(true, 1);
        monitor.setLowMemory(false, 1);
        /* Verify we can still monitor and expire a query after
        cancelling all queries due to low memory.
         */
        monitorQueryThreadExpirationTaskScheduled();
    }
}

