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


import BucketAdvisor.VolunteeringDelegate;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.partitioned.Bucket;
import org.apache.geode.internal.cache.partitioned.RegionAdvisor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BucketAdvisorTest {
    @Test
    public void shouldBeMockable() throws Exception {
        BucketAdvisor mockBucketAdvisor = Mockito.mock(BucketAdvisor.class);
        InternalDistributedMember mockInternalDistributedMember = Mockito.mock(InternalDistributedMember.class);
        Mockito.when(mockBucketAdvisor.basicGetPrimaryMember()).thenReturn(mockInternalDistributedMember);
        Mockito.when(mockBucketAdvisor.getBucketRedundancy()).thenReturn(1);
        assertThat(mockBucketAdvisor.basicGetPrimaryMember()).isEqualTo(mockInternalDistributedMember);
        assertThat(mockBucketAdvisor.getBucketRedundancy()).isEqualTo(1);
    }

    @Test
    public void volunteerForPrimaryIgnoresMissingPrimaryElector() {
        DistributionManager distributionManager = Mockito.mock(DistributionManager.class);
        Mockito.when(distributionManager.getId()).thenReturn(new InternalDistributedMember("localhost", 321));
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.isHosting()).thenReturn(true);
        Mockito.when(bucket.isPrimary()).thenReturn(false);
        Mockito.when(bucket.getDistributionManager()).thenReturn(distributionManager);
        PartitionedRegion partitionedRegion = Mockito.mock(PartitionedRegion.class);
        Mockito.when(partitionedRegion.getRedundantCopies()).thenReturn(0);
        Mockito.when(partitionedRegion.getPartitionAttributes()).thenReturn(new PartitionAttributesImpl());
        Mockito.when(partitionedRegion.getRedundancyTracker()).thenReturn(Mockito.mock(PartitionedRegionRedundancyTracker.class));
        InternalDistributedMember missingElectorId = new InternalDistributedMember("localhost", 123);
        RegionAdvisor regionAdvisor = Mockito.mock(RegionAdvisor.class);
        Mockito.when(regionAdvisor.getPartitionedRegion()).thenReturn(partitionedRegion);
        // hasPartitionedRegion() is invoked twice - once in initializePrimaryElector() and then in
        // volunteerForPrimary(). Returning true first simulates a elector being
        // there when createBucketAtomically() initiates creation of a bucket. Returning
        // false the second time simulates the elector closing its region/cache before
        // we get to the point of volunteering for primary
        Mockito.when(regionAdvisor.hasPartitionedRegion(Mockito.any(InternalDistributedMember.class))).thenReturn(true, false);
        BucketAdvisor advisor = BucketAdvisor.createBucketAdvisor(bucket, regionAdvisor);
        BucketAdvisor advisorSpy = Mockito.spy(advisor);
        Mockito.doCallRealMethod().when(advisorSpy).exchangeProfiles();
        Mockito.doCallRealMethod().when(advisorSpy).volunteerForPrimary();
        Mockito.doReturn(true).when(advisorSpy).initializationGate();
        Mockito.doReturn(true).when(advisorSpy).isHosting();
        BucketAdvisor.VolunteeringDelegate volunteeringDelegate = Mockito.mock(VolunteeringDelegate.class);
        advisorSpy.setVolunteeringDelegate(volunteeringDelegate);
        advisorSpy.initializePrimaryElector(missingElectorId);
        Assert.assertEquals(missingElectorId, advisorSpy.getPrimaryElector());
        advisorSpy.volunteerForPrimary();
        Mockito.verify(volunteeringDelegate).volunteerForPrimary();
    }
}

