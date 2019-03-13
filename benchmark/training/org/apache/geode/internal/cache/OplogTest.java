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


import DiskStoreImpl.OplogCompactor;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OplogTest {
    private final OplogCompactor compactor = Mockito.mock(OplogCompactor.class);

    private final PersistentOplogSet parent = Mockito.mock(PersistentOplogSet.class);

    private final long oplogId = 1;

    private Oplog oplog;

    @Test
    public void noCompactIfDoesNotNeedCompaction() {
        Mockito.doReturn(false).when(oplog).needsCompaction();
        assertThat(oplog.compact(compactor)).isEqualTo(0);
    }

    @Test
    public void noCompactIfNotKeepCompactorRunning() {
        Mockito.when(compactor.keepCompactorRunning()).thenReturn(false);
        assertThat(oplog.compact(compactor)).isEqualTo(0);
    }

    @Test
    public void handlesNoLiveValuesIfNoLiveValueInOplog() {
        Mockito.when(compactor.keepCompactorRunning()).thenReturn(true);
        Mockito.doReturn(true).when(oplog).hasNoLiveValues();
        assertThat(oplog.compact(compactor)).isEqualTo(0);
        Mockito.verify(oplog, Mockito.times(1)).handleNoLiveValues();
    }

    @Test
    public void invockeCleanupAfterCompaction() {
        Mockito.when(compactor.keepCompactorRunning()).thenReturn(true);
        Mockito.doReturn(Mockito.mock(DiskStoreStats.class)).when(oplog).getStats();
        Mockito.doReturn(false).when(oplog).hasNoLiveValues();
        oplog.compact(compactor);
        Mockito.verify(oplog, Mockito.times(1)).cleanupAfterCompaction(ArgumentMatchers.eq(false));
    }

    @Test
    public void handlesNoLiveValuesIfCompactSuccessful() {
        oplog.getTotalLiveCount().set(5);
        oplog.cleanupAfterCompaction(false);
        Mockito.verify(oplog, Mockito.times(1)).handleNoLiveValues();
        assertThat(oplog.getTotalLiveCount().get()).isEqualTo(0);
    }
}

