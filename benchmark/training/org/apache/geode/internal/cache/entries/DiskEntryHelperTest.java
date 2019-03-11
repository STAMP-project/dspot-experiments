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
package org.apache.geode.internal.cache.entries;


import DiskEntry.Helper;
import DiskEntry.Helper.ValueWrapper;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.internal.cache.DiskId;
import org.apache.geode.internal.cache.DiskRegion;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.InternalRegion;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.offheap.StoredObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DiskEntryHelperTest {
    private InternalRegion internalRegion = Mockito.mock(InternalRegion.class);

    private DiskRegion diskRegion = Mockito.mock(DiskRegion.class);

    @Test
    public void doSynchronousWriteReturnsTrueWhenDiskRegionIsSync() {
        Mockito.when(diskRegion.isSync()).thenReturn(true);
        boolean result = callDoSynchronousWrite();
        assertThat(result).isTrue();
    }

    @Test
    public void doSynchronousWriteReturnsTrueWhenPersistentRegionIsInitializing() {
        Mockito.when(diskRegion.isSync()).thenReturn(false);
        Mockito.when(diskRegion.isBackup()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        boolean result = callDoSynchronousWrite();
        assertThat(result).isTrue();
    }

    @Test
    public void doSynchronousWriteReturnsFalseWhenOverflowOnly() {
        Mockito.when(diskRegion.isSync()).thenReturn(false);
        Mockito.when(diskRegion.isBackup()).thenReturn(false);
        Mockito.when(internalRegion.isInitialized()).thenReturn(false);
        boolean result = callDoSynchronousWrite();
        assertThat(result).isFalse();
    }

    @Test
    public void doSynchronousWriteReturnsFalseWhenPersistentRegionIsInitialized() {
        Mockito.when(diskRegion.isSync()).thenReturn(false);
        Mockito.when(diskRegion.isBackup()).thenReturn(true);
        Mockito.when(internalRegion.isInitialized()).thenReturn(true);
        boolean result = callDoSynchronousWrite();
        assertThat(result).isFalse();
    }

    @Test
    public void whenHelperUpdateCalledAndDiskRegionAcquireReadLockThrowsRegionDestroyedExceptionThenStoredObjectShouldBeReleased() throws Exception {
        LocalRegion lr = Mockito.mock(LocalRegion.class);
        DiskEntry diskEntry = Mockito.mock(DiskEntry.class);
        Mockito.when(diskEntry.getDiskId()).thenReturn(Mockito.mock(DiskId.class));
        EntryEventImpl entryEvent = Mockito.mock(EntryEventImpl.class);
        DiskRegion diskRegion = Mockito.mock(DiskRegion.class);
        Mockito.when(lr.getDiskRegion()).thenReturn(diskRegion);
        Mockito.doThrow(new RegionDestroyedException("Region Destroyed", "mocked region")).when(diskRegion).acquireReadLock();
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        try {
            Helper.update(diskEntry, lr, storedObject, entryEvent);
            Assert.fail();
        } catch (RegionDestroyedException rde) {
            Mockito.verify(storedObject, Mockito.times(1)).release();
        }
    }

    @Test
    public void whenBasicUpdateWithDiskRegionBackupAndEntryNotSetThenReleaseOnStoredObjectShouldBeCalled() throws Exception {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        LocalRegion lr = Mockito.mock(LocalRegion.class);
        DiskEntry diskEntry = Mockito.mock(DiskEntry.class);
        Mockito.when(diskEntry.getDiskId()).thenReturn(Mockito.mock(DiskId.class));
        EntryEventImpl entryEvent = Mockito.mock(EntryEventImpl.class);
        DiskRegion diskRegion = Mockito.mock(DiskRegion.class);
        Mockito.when(diskRegion.isBackup()).thenReturn(true);
        Mockito.doThrow(new RegionDestroyedException("", "")).when(diskRegion).put(ArgumentMatchers.eq(diskEntry), ArgumentMatchers.eq(lr), ArgumentMatchers.any(ValueWrapper.class), ArgumentMatchers.anyBoolean());
        Mockito.when(lr.getDiskRegion()).thenReturn(diskRegion);
        try {
            Helper.basicUpdateForTesting(diskEntry, lr, storedObject, entryEvent);
            Assert.fail();
        } catch (RegionDestroyedException rde) {
            Mockito.verify(storedObject, Mockito.times(1)).release();
        }
    }

    @Test
    public void whenBasicUpdateWithDiskRegionBackupAndAsyncWritesAndEntryNotSetThenReleaseOnStoredObjectShouldBeCalled() throws Exception {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        LocalRegion lr = Mockito.mock(LocalRegion.class);
        DiskEntry diskEntry = Mockito.mock(DiskEntry.class);
        Mockito.when(diskEntry.getDiskId()).thenReturn(Mockito.mock(DiskId.class));
        EntryEventImpl entryEvent = Mockito.mock(EntryEventImpl.class);
        DiskRegion diskRegion = Mockito.mock(DiskRegion.class);
        Mockito.when(diskRegion.isBackup()).thenReturn(true);
        Mockito.doThrow(new RegionDestroyedException("", "")).when(diskRegion).put(ArgumentMatchers.eq(diskEntry), ArgumentMatchers.eq(lr), ArgumentMatchers.any(ValueWrapper.class), ArgumentMatchers.anyBoolean());
        Mockito.when(lr.getDiskRegion()).thenReturn(diskRegion);
        Mockito.when(diskRegion.isSync()).thenReturn(false);
        Mockito.when(lr.isInitialized()).thenReturn(true);
        Mockito.when(lr.getConcurrencyChecksEnabled()).thenThrow(new RegionDestroyedException("", ""));
        try {
            Helper.basicUpdateForTesting(diskEntry, lr, storedObject, entryEvent);
            Assert.fail();
        } catch (RegionDestroyedException rde) {
            Mockito.verify(storedObject, Mockito.times(1)).release();
        }
    }

    @Test
    public void whenBasicUpdateButNotBackupAndEntrySet() throws Exception {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        LocalRegion lr = Mockito.mock(LocalRegion.class);
        DiskEntry diskEntry = Mockito.mock(DiskEntry.class);
        Mockito.when(diskEntry.getDiskId()).thenReturn(Mockito.mock(DiskId.class));
        EntryEventImpl entryEvent = Mockito.mock(EntryEventImpl.class);
        DiskRegion diskRegion = Mockito.mock(DiskRegion.class);
        Mockito.when(diskRegion.isBackup()).thenReturn(false);
        Mockito.when(lr.getDiskRegion()).thenReturn(diskRegion);
        Helper.basicUpdateForTesting(diskEntry, lr, storedObject, entryEvent);
        Mockito.verify(storedObject, Mockito.times(0)).release();
    }

    @Test
    public void whenBasicUpdateButNotBackupAndDiskIdIsNullAndEntrySet() throws Exception {
        StoredObject storedObject = Mockito.mock(StoredObject.class);
        LocalRegion lr = Mockito.mock(LocalRegion.class);
        DiskEntry diskEntry = Mockito.mock(DiskEntry.class);
        EntryEventImpl entryEvent = Mockito.mock(EntryEventImpl.class);
        DiskRegion diskRegion = Mockito.mock(DiskRegion.class);
        Mockito.when(diskRegion.isBackup()).thenReturn(false);
        Mockito.when(lr.getDiskRegion()).thenReturn(diskRegion);
        Helper.basicUpdateForTesting(diskEntry, lr, storedObject, entryEvent);
        Mockito.verify(storedObject, Mockito.times(0)).release();
    }
}

