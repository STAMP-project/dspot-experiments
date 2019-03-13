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


import org.apache.geode.distributed.DistributedLockService;
import org.apache.geode.internal.cache.PartitionedRegion.BucketLock;
import org.apache.geode.internal.cache.persistence.PersistentMemberID;
import org.apache.geode.internal.cache.persistence.PersistentMemberManager;
import org.apache.geode.internal.cache.persistence.PersistentMemberView;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class BucketPersistenceAdvisorTest {
    @Test
    public void shouldBeMockable() throws Exception {
        BucketPersistenceAdvisor mockBucketAdvisor = Mockito.mock(BucketPersistenceAdvisor.class);
        Mockito.when(mockBucketAdvisor.isRecovering()).thenReturn(true);
        Assert.assertTrue(mockBucketAdvisor.isRecovering());
    }

    @Test
    public void atomicCreationShouldNotBeSetForPersistentRegion() throws Exception {
        PersistentMemberID mockPersistentID = Mockito.mock(PersistentMemberID.class);
        PersistentMemberView mockStorage = Mockito.mock(PersistentMemberView.class);
        Mockito.when(mockStorage.getMyPersistentID()).thenReturn(mockPersistentID);
        BucketPersistenceAdvisor bpa = new BucketPersistenceAdvisor(Mockito.mock(CacheDistributionAdvisor.class), Mockito.mock(DistributedLockService.class), mockStorage, "/region", Mockito.mock(DiskRegionStats.class), Mockito.mock(PersistentMemberManager.class), Mockito.mock(BucketLock.class), Mockito.mock(ProxyBucketRegion.class));
        bpa.setAtomicCreation(true);
        Assert.assertFalse(bpa.isAtomicCreation());
    }
}

