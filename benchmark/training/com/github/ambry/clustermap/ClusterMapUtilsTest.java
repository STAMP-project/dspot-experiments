/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.clustermap;


import ClusterMapUtils.PartitionSelectionHelper;
import com.github.ambry.utils.UtilsTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ClusterMapUtilsTest {
    /**
     * Tests for {@link ClusterMapUtils#areAllReplicasForPartitionUp(PartitionId)}.
     */
    @Test
    public void areAllReplicasForPartitionUpTest() {
        MockDataNodeId dn1 = getDataNodeId("dn1", "DC1");
        MockDataNodeId dn2 = getDataNodeId("dn2", "DC2");
        MockPartitionId partitionId = new MockPartitionId(1, "default", Arrays.asList(dn1, dn2), 0);
        MockReplicaId replicaId1 = ((MockReplicaId) (partitionId.getReplicaIds().get(0)));
        MockReplicaId replicaId2 = ((MockReplicaId) (partitionId.getReplicaIds().get(1)));
        Assert.assertTrue("All replicas should be up", ClusterMapUtils.areAllReplicasForPartitionUp(partitionId));
        replicaId1.markReplicaDownStatus(true);
        Assert.assertFalse("Not all replicas should be up", ClusterMapUtils.areAllReplicasForPartitionUp(partitionId));
        replicaId2.markReplicaDownStatus(true);
        Assert.assertFalse("Not all replicas should be up", ClusterMapUtils.areAllReplicasForPartitionUp(partitionId));
        replicaId1.markReplicaDownStatus(false);
        Assert.assertFalse("Not all replicas should be up", ClusterMapUtils.areAllReplicasForPartitionUp(partitionId));
        replicaId2.markReplicaDownStatus(false);
        Assert.assertTrue("All replicas should be up", ClusterMapUtils.areAllReplicasForPartitionUp(partitionId));
    }

    /**
     * Tests for all functions in {@link ClusterMapUtils.PartitionSelectionHelper}
     */
    @Test
    public void partitionSelectionHelperTest() {
        // set up partitions for tests
        // 2 partitions with 3 replicas in two datacenters "DC1" and "DC2" (class "max-replicas-all-sites")
        // 2 partitions with 3 replicas in "DC1" and 1 replica in "DC2" (class "max-local-one-remote")
        // 2 partitions with 3 replicas in "DC2" and 1 replica in "DC1" (class "max-local-one-remote")
        final String dc1 = "DC1";
        final String dc2 = "DC2";
        final String maxReplicasAllSites = "max-replicas-all-sites";
        final String maxLocalOneRemote = "max-local-one-remote";
        MockDataNodeId dc1Dn1 = getDataNodeId("dc1dn1", dc1);
        MockDataNodeId dc1Dn2 = getDataNodeId("dc1dn2", dc1);
        MockDataNodeId dc1Dn3 = getDataNodeId("dc1dn3", dc1);
        MockDataNodeId dc2Dn1 = getDataNodeId("dc2dn1", dc2);
        MockDataNodeId dc2Dn2 = getDataNodeId("dc2dn2", dc2);
        MockDataNodeId dc2Dn3 = getDataNodeId("dc2dn3", dc2);
        List<MockDataNodeId> allDataNodes = Arrays.asList(dc1Dn1, dc1Dn2, dc1Dn3, dc2Dn1, dc2Dn2, dc2Dn3);
        MockPartitionId everywhere1 = new MockPartitionId(1, maxReplicasAllSites, allDataNodes, 0);
        MockPartitionId everywhere2 = new MockPartitionId(2, maxReplicasAllSites, allDataNodes, 0);
        MockPartitionId majorDc11 = new MockPartitionId(3, maxLocalOneRemote, Arrays.asList(dc1Dn1, dc1Dn2, dc1Dn3, dc2Dn1), 0);
        MockPartitionId majorDc12 = new MockPartitionId(4, maxLocalOneRemote, Arrays.asList(dc1Dn1, dc1Dn2, dc1Dn3, dc2Dn2), 0);
        MockPartitionId majorDc21 = new MockPartitionId(5, maxLocalOneRemote, Arrays.asList(dc2Dn1, dc2Dn2, dc2Dn3, dc1Dn1), 0);
        MockPartitionId majorDc22 = new MockPartitionId(6, maxLocalOneRemote, Arrays.asList(dc2Dn1, dc2Dn2, dc2Dn3, dc1Dn2), 0);
        Collection<MockPartitionId> allPartitionIdsMain = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(everywhere1, everywhere2, majorDc11, majorDc12, majorDc21, majorDc22)));
        ClusterMapUtils.PartitionSelectionHelper psh = new ClusterMapUtils.PartitionSelectionHelper(allPartitionIdsMain, null);
        String[] dcsToTry = new String[]{ null, "", dc1, dc2 };
        for (String dc : dcsToTry) {
            Set<MockPartitionId> allPartitionIds = new HashSet<>(allPartitionIdsMain);
            resetPartitions(allPartitionIds);
            psh.updatePartitions(allPartitionIds, dc);
            // getPartitions()
            assertCollectionEquals("Partitions returned not as expected", allPartitionIds, psh.getPartitions(null));
            assertCollectionEquals((("Partitions returned for " + maxReplicasAllSites) + " not as expected"), Arrays.asList(everywhere1, everywhere2), psh.getPartitions(maxReplicasAllSites));
            assertCollectionEquals((("Partitions returned for " + maxLocalOneRemote) + " not as expected"), Arrays.asList(majorDc11, majorDc12, majorDc21, majorDc22), psh.getPartitions(maxLocalOneRemote));
            checkCaseInsensitivityForPartitionSelectionHelper(psh, true, maxReplicasAllSites, Arrays.asList(everywhere1, everywhere2));
            checkCaseInsensitivityForPartitionSelectionHelper(psh, true, maxLocalOneRemote, Arrays.asList(majorDc11, majorDc12, majorDc21, majorDc22));
            try {
                psh.getPartitions(UtilsTest.getRandomString(3));
                Assert.fail("partition class is invalid, should have thrown");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
            // getWritablePartitions()
            Set<MockPartitionId> expectedWritableForMaxLocalOneRemote = null;
            MockPartitionId candidate1 = null;
            MockPartitionId candidate2 = null;
            if (dc != null) {
                switch (dc) {
                    case dc1 :
                        candidate1 = majorDc11;
                        candidate2 = majorDc12;
                        expectedWritableForMaxLocalOneRemote = new HashSet<>(Arrays.asList(majorDc11, majorDc12));
                        break;
                    case dc2 :
                        candidate1 = majorDc21;
                        candidate2 = majorDc22;
                        expectedWritableForMaxLocalOneRemote = new HashSet<>(Arrays.asList(majorDc21, majorDc22));
                        break;
                }
            }
            // invalid class
            try {
                psh.getWritablePartitions(UtilsTest.getRandomString(3));
                Assert.fail("partition class is invalid, should have thrown");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
            verifyWritablePartitionsReturned(psh, allPartitionIds, maxReplicasAllSites, everywhere1, everywhere2, maxLocalOneRemote, expectedWritableForMaxLocalOneRemote);
            if ((candidate1 != null) && (candidate2 != null)) {
                verifyWritablePartitionsReturned(psh, allPartitionIds, maxLocalOneRemote, candidate1, candidate2, maxReplicasAllSites, new HashSet(Arrays.asList(everywhere1, everywhere2)));
            }
        }
    }
}

