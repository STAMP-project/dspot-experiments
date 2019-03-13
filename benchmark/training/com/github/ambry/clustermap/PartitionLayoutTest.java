/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
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


import PartitionState.READ_ONLY;
import PartitionState.READ_WRITE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import static PartitionState.READ_ONLY;
import static PartitionState.READ_WRITE;
import static com.github.ambry.clustermap.TestUtils.TestPartitionLayout.defaultVersion;


public class PartitionLayoutTest {
    @Test
    public void basics() throws JSONException {
        TestUtils.TestHardwareLayout hardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        String dc = hardwareLayout.getHardwareLayout().getDatacenters().get(0).getName();
        TestUtils.TestPartitionLayout testPartitionLayout = new TestUtils.TestPartitionLayout(hardwareLayout, dc);
        PartitionLayout partitionLayout = testPartitionLayout.getPartitionLayout();
        Assert.assertEquals(partitionLayout.getVersion(), defaultVersion);
        Assert.assertEquals(partitionLayout.getClusterName(), "Alpha");
        Assert.assertEquals(partitionLayout.getPartitions(null).size(), testPartitionLayout.getPartitionCount());
        Assert.assertEquals(partitionLayout.getPartitionCount(), testPartitionLayout.getPartitionCount());
        Assert.assertEquals(partitionLayout.getAllocatedRawCapacityInBytes(), testPartitionLayout.getAllocatedRawCapacityInBytes());
        Assert.assertEquals(partitionLayout.getAllocatedUsableCapacityInBytes(), testPartitionLayout.getAllocatedUsableCapacityInBytes());
        Assert.assertEquals(partitionLayout.getPartitionInStateCount(READ_WRITE), testPartitionLayout.countPartitionsInState(READ_WRITE));
        Assert.assertEquals(partitionLayout.getPartitionInStateCount(READ_ONLY), testPartitionLayout.countPartitionsInState(READ_ONLY));
    }

    @Test
    public void validation() throws JSONException {
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        try {
            TestUtils.TestPartitionLayout tpl = new TestUtils.TestPartitionLayoutWithDuplicatePartitions(testHardwareLayout);
            Assert.fail(("Should have failed validation:" + (tpl.getPartitionLayout().toString())));
        } catch (IllegalStateException e) {
            // Expected.
        }
        try {
            TestUtils.TestPartitionLayout tpl = new TestUtils.TestPartitionLayoutWithDuplicateReplicas(testHardwareLayout);
            Assert.fail(("Should have failed validation:" + (tpl.getPartitionLayout().toString())));
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    /**
     * Tests for {@link PartitionLayout#getPartitions(String)} and {@link PartitionLayout#getWritablePartitions(String)}.
     *
     * @throws JSONException
     * 		
     */
    @Test
    public void getPartitionsTest() throws JSONException {
        String specialPartitionClass = "specialPartitionClass";
        TestUtils.TestHardwareLayout hardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        String dc = hardwareLayout.getRandomDatacenter().getName();
        TestUtils.TestPartitionLayout testPartitionLayout = new TestUtils.TestPartitionLayout(hardwareLayout, dc);
        Assert.assertTrue("There should be more than 1 replica per partition in each DC for this test to work", ((testPartitionLayout.replicaCountPerDc) > 1));
        TestUtils.PartitionRangeCheckParams defaultRw = new TestUtils.PartitionRangeCheckParams(0, testPartitionLayout.partitionCount, TestUtils.DEFAULT_PARTITION_CLASS, READ_WRITE);
        // add 15 RW partitions for the special class
        TestUtils.PartitionRangeCheckParams specialRw = new TestUtils.PartitionRangeCheckParams(((defaultRw.rangeEnd) + 1), 15, specialPartitionClass, READ_WRITE);
        testPartitionLayout.addNewPartitions(specialRw.count, specialPartitionClass, READ_WRITE, dc);
        // add 10 RO partitions for the default class
        TestUtils.PartitionRangeCheckParams defaultRo = new TestUtils.PartitionRangeCheckParams(((specialRw.rangeEnd) + 1), 10, TestUtils.DEFAULT_PARTITION_CLASS, READ_ONLY);
        testPartitionLayout.addNewPartitions(defaultRo.count, TestUtils.DEFAULT_PARTITION_CLASS, READ_ONLY, dc);
        // add 5 RO partitions for the special class
        TestUtils.PartitionRangeCheckParams specialRo = new TestUtils.PartitionRangeCheckParams(((defaultRo.rangeEnd) + 1), 5, specialPartitionClass, READ_ONLY);
        testPartitionLayout.addNewPartitions(specialRo.count, specialPartitionClass, READ_ONLY, dc);
        PartitionLayout partitionLayout = testPartitionLayout.getPartitionLayout();
        // "good" cases for getPartitions() and getWritablePartitions() only
        // getPartitions(), class null
        List<PartitionId> returnedPartitions = partitionLayout.getPartitions(null);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(defaultRw, defaultRo, specialRw, specialRo));
        // getWritablePartitions(), class null
        returnedPartitions = partitionLayout.getWritablePartitions(null);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(defaultRw, specialRw));
        // getPartitions(), class default
        returnedPartitions = partitionLayout.getPartitions(TestUtils.DEFAULT_PARTITION_CLASS);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(defaultRw, defaultRo));
        // getWritablePartitions(), class default
        returnedPartitions = partitionLayout.getWritablePartitions(TestUtils.DEFAULT_PARTITION_CLASS);
        TestUtils.checkReturnedPartitions(returnedPartitions, Collections.singletonList(defaultRw));
        // getPartitions(), class special
        returnedPartitions = partitionLayout.getPartitions(specialPartitionClass);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(specialRw, specialRo));
        // getWritablePartitions(), class special
        returnedPartitions = partitionLayout.getWritablePartitions(specialPartitionClass);
        TestUtils.checkReturnedPartitions(returnedPartitions, Collections.singletonList(specialRw));
        // to test the dc affinity, we pick one datanode from "dc" and insert 1 replica for part1 (special class) in "dc"
        // and make sure that it is returned in getPartitions() but not in getWritablePartitions() (because all the other
        // partitions have more than 1 replica in "dc").
        DataNode dataNode = hardwareLayout.getRandomDataNodeFromDc(dc);
        Partition partition = partitionLayout.addNewPartition(dataNode.getDisks().subList(0, 1), testPartitionLayout.replicaCapacityInBytes, specialPartitionClass);
        TestUtils.PartitionRangeCheckParams extraPartCheckParams = new TestUtils.PartitionRangeCheckParams(((specialRo.rangeEnd) + 1), 1, specialPartitionClass, READ_WRITE);
        // getPartitions(), class special
        returnedPartitions = partitionLayout.getPartitions(specialPartitionClass);
        Assert.assertTrue("Added partition should exist in returned partitions", returnedPartitions.contains(partition));
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(specialRw, specialRo, extraPartCheckParams));
        // getWritablePartitions(), class special
        returnedPartitions = partitionLayout.getWritablePartitions(specialPartitionClass);
        Assert.assertFalse("Added partition should not exist in returned partitions", returnedPartitions.contains(partition));
        TestUtils.checkReturnedPartitions(returnedPartitions, Collections.singletonList(specialRw));
    }
}

