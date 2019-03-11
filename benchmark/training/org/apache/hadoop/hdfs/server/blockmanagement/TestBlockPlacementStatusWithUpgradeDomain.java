/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.blockmanagement;


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for BlockPlacementStatusWithUpgradeDomain class.
 */
public class TestBlockPlacementStatusWithUpgradeDomain {
    private Set<String> upgradeDomains;

    private BlockPlacementStatusDefault bpsd = Mockito.mock(BlockPlacementStatusDefault.class);

    @Test
    public void testIsPolicySatisfiedParentFalse() {
        Mockito.when(bpsd.isPlacementPolicySatisfied()).thenReturn(false);
        BlockPlacementStatusWithUpgradeDomain bps = new BlockPlacementStatusWithUpgradeDomain(bpsd, upgradeDomains, 3, 3);
        // Parent policy is not satisfied but upgrade domain policy is
        Assert.assertFalse(bps.isPlacementPolicySatisfied());
    }

    @Test
    public void testIsPolicySatisfiedAllEqual() {
        BlockPlacementStatusWithUpgradeDomain bps = new BlockPlacementStatusWithUpgradeDomain(bpsd, upgradeDomains, 3, 3);
        // Number of domains, replicas and upgradeDomainFactor is equal and parent
        // policy is satisfied
        Assert.assertTrue(bps.isPlacementPolicySatisfied());
    }

    @Test
    public void testIsPolicySatisifedSmallDomains() {
        // Number of domains is less than replicas but equal to factor
        BlockPlacementStatusWithUpgradeDomain bps = new BlockPlacementStatusWithUpgradeDomain(bpsd, upgradeDomains, 4, 3);
        Assert.assertTrue(bps.isPlacementPolicySatisfied());
        // Same as above but replicas is greater than factor
        bps = new BlockPlacementStatusWithUpgradeDomain(bpsd, upgradeDomains, 4, 2);
        Assert.assertTrue(bps.isPlacementPolicySatisfied());
        // Number of domains is less than replicas and factor
        bps = new BlockPlacementStatusWithUpgradeDomain(bpsd, upgradeDomains, 4, 4);
        Assert.assertFalse(bps.isPlacementPolicySatisfied());
    }
}

