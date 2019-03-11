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


import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.internal.cache.partitioned.LockObject;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BucketRegionTest {
    private RegionAttributes regionAttributes;

    private PartitionedRegion partitionedRegion;

    private InternalCache cache;

    private InternalRegionArguments internalRegionArgs;

    private DataPolicy dataPolicy;

    private EntryEventImpl event;

    private BucketAdvisor bucketAdvisor;

    private Operation operation;

    private final String regionName = "name";

    private final Object[] keys = new Object[]{ 1 };

    private final RegionDestroyedException regionDestroyedException = new RegionDestroyedException("", "");

    @Test(expected = RegionDestroyedException.class)
    public void waitUntilLockedThrowsIfFoundLockAndPartitionedRegionIsClosing() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Integer[] keys = new Integer[]{ 1 };
        Mockito.doReturn(Mockito.mock(LockObject.class)).when(bucketRegion).searchAndLock(keys);
        Mockito.doThrow(regionDestroyedException).when(partitionedRegion).checkReadiness();
        bucketRegion.waitUntilLocked(keys);
    }

    @Test
    public void waitUntilLockedReturnsTrueIfNoOtherThreadLockedKeys() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Integer[] keys = new Integer[]{ 1 };
        Mockito.doReturn(null).when(bucketRegion).searchAndLock(keys);
        assertThat(bucketRegion.waitUntilLocked(keys)).isTrue();
    }

    @Test(expected = RegionDestroyedException.class)
    public void basicPutEntryDoesNotReleaseLockIfKeysAndPrimaryNotLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doThrow(regionDestroyedException).when(bucketRegion).lockKeysAndPrimary(event);
        bucketRegion.basicPutEntry(event, 1);
        Mockito.verify(bucketRegion, Mockito.never()).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test
    public void basicPutEntryReleaseLockIfKeysAndPrimaryLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(true).when(bucketRegion).lockKeysAndPrimary(event);
        Mockito.doReturn(Mockito.mock(AbstractRegionMap.class)).when(bucketRegion).getRegionMap();
        bucketRegion.basicPutEntry(event, 1);
        Mockito.verify(bucketRegion).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test(expected = RegionDestroyedException.class)
    public void virtualPutDoesNotReleaseLockIfKeysAndPrimaryNotLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doThrow(regionDestroyedException).when(bucketRegion).lockKeysAndPrimary(event);
        bucketRegion.virtualPut(event, false, true, null, false, 1, true);
        Mockito.verify(bucketRegion, Mockito.never()).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test
    public void virtualPutReleaseLockIfKeysAndPrimaryLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(true).when(bucketRegion).lockKeysAndPrimary(event);
        Mockito.doReturn(true).when(bucketRegion).hasSeenEvent(event);
        bucketRegion.virtualPut(event, false, true, null, false, 1, true);
        Mockito.verify(bucketRegion).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test(expected = RegionDestroyedException.class)
    public void basicDestroyDoesNotReleaseLockIfKeysAndPrimaryNotLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doThrow(regionDestroyedException).when(bucketRegion).lockKeysAndPrimary(event);
        bucketRegion.basicDestroy(event, false, null);
        Mockito.verify(bucketRegion, Mockito.never()).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test
    public void basicDestroyReleaseLockIfKeysAndPrimaryLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(true).when(bucketRegion).lockKeysAndPrimary(event);
        Mockito.doReturn(true).when(bucketRegion).hasSeenEvent(event);
        bucketRegion.basicDestroy(event, false, null);
        Mockito.verify(bucketRegion).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test(expected = RegionDestroyedException.class)
    public void basicUpdateEntryVersionDoesNotReleaseLockIfKeysAndPrimaryNotLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doThrow(regionDestroyedException).when(bucketRegion).lockKeysAndPrimary(event);
        Mockito.when(event.getRegion()).thenReturn(bucketRegion);
        Mockito.doReturn(true).when(bucketRegion).hasSeenEvent(event);
        Mockito.doReturn(Mockito.mock(AbstractRegionMap.class)).when(bucketRegion).getRegionMap();
        bucketRegion.basicUpdateEntryVersion(event);
        Mockito.verify(bucketRegion, Mockito.never()).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test
    public void basicUpdateEntryVersionReleaseLockIfKeysAndPrimaryLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(true).when(bucketRegion).lockKeysAndPrimary(event);
        Mockito.when(event.getRegion()).thenReturn(bucketRegion);
        Mockito.doReturn(true).when(bucketRegion).hasSeenEvent(event);
        Mockito.doReturn(Mockito.mock(AbstractRegionMap.class)).when(bucketRegion).getRegionMap();
        bucketRegion.basicUpdateEntryVersion(event);
        Mockito.verify(bucketRegion).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test(expected = RegionDestroyedException.class)
    public void basicInvalidateDoesNotReleaseLockIfKeysAndPrimaryNotLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doThrow(regionDestroyedException).when(bucketRegion).lockKeysAndPrimary(event);
        bucketRegion.basicInvalidate(event, false, false);
        Mockito.verify(bucketRegion, Mockito.never()).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test
    public void basicInvalidateReleaseLockIfKeysAndPrimaryLocked() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(true).when(bucketRegion).lockKeysAndPrimary(event);
        Mockito.doReturn(true).when(bucketRegion).hasSeenEvent(event);
        bucketRegion.basicInvalidate(event, false, false);
        Mockito.verify(bucketRegion).releaseLockForKeysAndPrimary(ArgumentMatchers.eq(event));
    }

    @Test
    public void lockKeysAndPrimaryReturnFalseIfDoesNotNeedWriteLock() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(false).when(bucketRegion).needWriteLock(event);
        assertThat(bucketRegion.lockKeysAndPrimary(event)).isFalse();
    }

    @Test(expected = RegionDestroyedException.class)
    public void lockKeysAndPrimaryThrowsIfWaitUntilLockedThrows() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(keys).when(bucketRegion).getKeysToBeLocked(event);
        Mockito.doThrow(regionDestroyedException).when(bucketRegion).waitUntilLocked(keys);
        bucketRegion.lockKeysAndPrimary(event);
    }

    @Test(expected = PrimaryBucketException.class)
    public void lockKeysAndPrimaryReleaseLockHeldIfDoLockForPrimaryThrows() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(keys).when(bucketRegion).getKeysToBeLocked(event);
        Mockito.doReturn(true).when(bucketRegion).waitUntilLocked(keys);
        Mockito.doThrow(new PrimaryBucketException()).when(bucketRegion).doLockForPrimary(false);
        bucketRegion.lockKeysAndPrimary(event);
        Mockito.verify(bucketRegion).removeAndNotifyKeys(keys);
    }

    @Test
    public void lockKeysAndPrimaryReleaseLockHeldIfDoesNotLockForPrimary() {
        BucketRegion bucketRegion = Mockito.spy(new BucketRegion(regionName, regionAttributes, partitionedRegion, cache, internalRegionArgs));
        Mockito.doReturn(keys).when(bucketRegion).getKeysToBeLocked(event);
        Mockito.doReturn(true).when(bucketRegion).waitUntilLocked(keys);
        Mockito.doReturn(true).when(bucketRegion).doLockForPrimary(false);
        bucketRegion.lockKeysAndPrimary(event);
        Mockito.verify(bucketRegion, Mockito.never()).removeAndNotifyKeys(keys);
    }
}

