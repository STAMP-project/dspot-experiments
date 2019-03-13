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
package org.apache.geode.internal.cache;


import DiskStoreImpl.FlusherThread;
import org.apache.geode.cache.DiskAccessException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlusherThreadTest {
    private DiskStoreImpl diskStoreImpl;

    private DiskStoreStats diskStoreStats;

    private FlusherThread flusherThread;

    private final int DRAIN_LIST_SIZE = 5;

    @Test
    public void asyncFlushIncrementsQueueSizeStat() {
        flusherThread.doAsyncFlush();
        Mockito.verify(diskStoreStats, Mockito.times(1)).incQueueSize((-(DRAIN_LIST_SIZE)));
    }

    @Test
    public void asyncFlushDoesNotIncrementQueueSizeWhenExceptionThrown() {
        Mockito.when(diskStoreImpl.getDrainList()).thenThrow(DiskAccessException.class);
        flusherThread.doAsyncFlush();
        Mockito.verify(diskStoreStats, Mockito.never()).incQueueSize(ArgumentMatchers.anyInt());
    }
}

