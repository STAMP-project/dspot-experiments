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


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DistributedRegionTest {
    @Test
    public void shouldBeMockable() throws Exception {
        DistributedRegion mockDistributedRegion = Mockito.mock(DistributedRegion.class);
        EntryEventImpl mockEntryEventImpl = Mockito.mock(EntryEventImpl.class);
        Object returnValue = new Object();
        Mockito.when(mockDistributedRegion.validatedDestroy(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(mockEntryEventImpl))).thenReturn(returnValue);
        assertThat(mockDistributedRegion.validatedDestroy(new Object(), mockEntryEventImpl)).isSameAs(returnValue);
    }

    @Test
    public void cleanUpAfterFailedInitialImageHoldsLockForClear() {
        DistributedRegion distributedRegion = Mockito.mock(DistributedRegion.class, Mockito.RETURNS_DEEP_STUBS);
        RegionMap regionMap = Mockito.mock(RegionMap.class);
        Mockito.doCallRealMethod().when(distributedRegion).cleanUpAfterFailedGII(false);
        Mockito.when(distributedRegion.getRegionMap()).thenReturn(regionMap);
        Mockito.when(regionMap.isEmpty()).thenReturn(false);
        distributedRegion.cleanUpAfterFailedGII(false);
        Mockito.verify(distributedRegion).lockFailedInitialImageWriteLock();
        Mockito.verify(distributedRegion).closeEntries();
        Mockito.verify(distributedRegion).unlockFailedInitialImageWriteLock();
    }

    @Test
    public void cleanUpAfterFailedInitialImageDoesNotCloseEntriesIfIsPersistentRegionAndRecoveredFromDisk() {
        DistributedRegion distributedRegion = Mockito.mock(DistributedRegion.class);
        DiskRegion diskRegion = Mockito.mock(DiskRegion.class);
        Mockito.doCallRealMethod().when(distributedRegion).cleanUpAfterFailedGII(true);
        Mockito.when(distributedRegion.getDiskRegion()).thenReturn(diskRegion);
        Mockito.when(diskRegion.isBackup()).thenReturn(true);
        distributedRegion.cleanUpAfterFailedGII(true);
        Mockito.verify(diskRegion).resetRecoveredEntries(ArgumentMatchers.eq(distributedRegion));
        Mockito.verify(distributedRegion, Mockito.never()).closeEntries();
    }

    @Test
    public void lockHeldWhenRegionIsNotInitialized() {
        DistributedRegion distributedRegion = Mockito.mock(DistributedRegion.class);
        Mockito.doCallRealMethod().when(distributedRegion).lockWhenRegionIsInitializing();
        Mockito.when(distributedRegion.isInitialized()).thenReturn(false);
        assertThat(distributedRegion.lockWhenRegionIsInitializing()).isTrue();
        Mockito.verify(distributedRegion).lockFailedInitialImageReadLock();
    }

    @Test
    public void lockNotHeldWhenRegionIsInitialized() {
        DistributedRegion distributedRegion = Mockito.mock(DistributedRegion.class);
        Mockito.doCallRealMethod().when(distributedRegion).lockWhenRegionIsInitializing();
        Mockito.when(distributedRegion.isInitialized()).thenReturn(true);
        assertThat(distributedRegion.lockWhenRegionIsInitializing()).isFalse();
        Mockito.verify(distributedRegion, Mockito.never()).lockFailedInitialImageReadLock();
    }
}

