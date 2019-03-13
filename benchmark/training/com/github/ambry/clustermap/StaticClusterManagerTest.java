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
import ServerErrorCode.Disk_Unavailable;
import ServerErrorCode.IO_Error;
import ServerErrorCode.Partition_ReadOnly;
import ServerErrorCode.Replica_Unavailable;
import ServerErrorCode.Temporarily_Disabled;
import com.github.ambry.config.ClusterMapConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.Utils;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static PartitionState.READ_ONLY;
import static PartitionState.READ_WRITE;
import static com.github.ambry.clustermap.TestUtils.ResourceState.Disk_Down;
import static com.github.ambry.clustermap.TestUtils.ResourceState.Disk_Up;
import static com.github.ambry.clustermap.TestUtils.ResourceState.Node_Up;
import static com.github.ambry.clustermap.TestUtils.ResourceState.Replica_Down;
import static com.github.ambry.clustermap.TestUtils.ResourceState.Replica_Up;
import static junit.framework.Assert.fail;


/**
 * Tests {@link StaticClusterManager} class.
 */
public class StaticClusterManagerTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void clusterMapInterface() {
        // Exercise entire clusterMap interface
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        TestUtils.TestPartitionLayout testPartitionLayout = new TestUtils.TestPartitionLayout(testHardwareLayout, null);
        // add 3 partitions with read_only state.
        testPartitionLayout.partitionState = READ_ONLY;
        testPartitionLayout.addNewPartitions(3, TestUtils.DEFAULT_PARTITION_CLASS, testPartitionLayout.partitionState, null);
        testPartitionLayout.partitionState = READ_WRITE;
        Datacenter localDatacenter = testHardwareLayout.getRandomDatacenter();
        Properties props = new Properties();
        props.setProperty("clustermap.host.name", "localhost");
        props.setProperty("clustermap.cluster.name", "cluster");
        props.setProperty("clustermap.datacenter.name", localDatacenter.getName());
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        ClusterMap clusterMapManager = getClusterMap();
        for (String metricName : clusterMapManager.getMetricRegistry().getNames()) {
            System.out.println(metricName);
        }
        Assert.assertEquals("Incorrect local datacenter ID", localDatacenter.getId(), clusterMapManager.getLocalDatacenterId());
        List<? extends PartitionId> writablePartitionIds = clusterMapManager.getWritablePartitionIds(null);
        List<? extends PartitionId> partitionIds = clusterMapManager.getAllPartitionIds(null);
        Assert.assertEquals(writablePartitionIds.size(), ((testPartitionLayout.getPartitionCount()) - 3));
        Assert.assertEquals(partitionIds.size(), testPartitionLayout.getPartitionCount());
        for (PartitionId partitionId : partitionIds) {
            if (partitionId.getPartitionState().equals(READ_WRITE)) {
                Assert.assertTrue("Partition not found in writable set ", writablePartitionIds.contains(partitionId));
            } else {
                Assert.assertFalse("READ_ONLY Partition found in writable set ", writablePartitionIds.contains(partitionId));
            }
        }
        for (int i = 0; i < (partitionIds.size()); i++) {
            PartitionId partitionId = partitionIds.get(i);
            Assert.assertEquals(partitionId.getReplicaIds().size(), testPartitionLayout.getTotalReplicaCount());
            DataInputStream partitionStream = new DataInputStream(new com.github.ambry.utils.ByteBufferInputStream(ByteBuffer.wrap(partitionId.getBytes())));
            try {
                PartitionId fetchedPartitionId = clusterMapManager.getPartitionIdFromStream(partitionStream);
                Assert.assertEquals(partitionId, fetchedPartitionId);
            } catch (IOException e) {
                Assert.assertEquals(true, false);
            }
        }
        for (Datacenter datacenter : testHardwareLayout.getHardwareLayout().getDatacenters()) {
            for (DataNode dataNode : datacenter.getDataNodes()) {
                DataNodeId dataNodeId = clusterMapManager.getDataNodeId(dataNode.getHostname(), dataNode.getPort());
                Assert.assertEquals(dataNodeId, dataNode);
                for (ReplicaId replicaId : clusterMapManager.getReplicaIds(dataNodeId)) {
                    Assert.assertEquals(dataNodeId, replicaId.getDataNodeId());
                }
            }
        }
    }

    @Test
    public void findDatacenter() {
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        TestUtils.TestPartitionLayout testPartitionLayout = new TestUtils.TestPartitionLayout(testHardwareLayout, null);
        StaticClusterManager clusterMapManager = new StaticClusterAgentsFactory(TestUtils.getDummyConfig(), testPartitionLayout.getPartitionLayout()).getClusterMap();
        for (Datacenter datacenter : testHardwareLayout.getHardwareLayout().getDatacenters()) {
            Assert.assertTrue(clusterMapManager.hasDatacenter(datacenter.getName()));
            Assert.assertFalse(clusterMapManager.hasDatacenter(((datacenter.getName()) + (datacenter.getName()))));
        }
    }

    @Test
    public void addNewPartition() {
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        PartitionLayout partitionLayout = new PartitionLayout(testHardwareLayout.getHardwareLayout(), null);
        StaticClusterManager clusterMapManager = new StaticClusterAgentsFactory(TestUtils.getDummyConfig(), partitionLayout).getClusterMap();
        int dcCount = testHardwareLayout.getDatacenterCount();
        List<PartitionId> partitionIds = clusterMapManager.getWritablePartitionIds(null);
        Assert.assertEquals(partitionIds.size(), 0);
        clusterMapManager.addNewPartition(testHardwareLayout.getIndependentDisks(3), (((100 * 1024) * 1024) * 1024L), MockClusterMap.DEFAULT_PARTITION_CLASS);
        partitionIds = clusterMapManager.getWritablePartitionIds(null);
        Assert.assertEquals(partitionIds.size(), 1);
        PartitionId partitionId = partitionIds.get(0);
        Assert.assertEquals(partitionId.getReplicaIds().size(), (3 * dcCount));
    }

    @Test
    public void nonRackAwareAllocationTest() {
        int replicaCountPerDataCenter = 2;
        long replicaCapacityInBytes = ((100 * 1024) * 1024) * 1024L;
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        PartitionLayout partitionLayout = new PartitionLayout(testHardwareLayout.getHardwareLayout(), null);
        StaticClusterManager clusterMapManager = new StaticClusterAgentsFactory(TestUtils.getDummyConfig(), partitionLayout).getClusterMap();
        List<PartitionId> allocatedPartitions;
        try {
            // Test with retryIfNotRackAware set to false, this should throw an exception
            clusterMapManager.allocatePartitions(5, MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, false);
            fail(("allocatePartitions should not succeed when datacenters are missing rack info " + "and retryIfNotRackAware is false"));
        } catch (IllegalArgumentException e) {
            // This should be thrown
        }
        // Allocate five partitions that fit within cluster's capacity
        allocatedPartitions = clusterMapManager.allocatePartitions(5, MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, true);
        Assert.assertEquals(allocatedPartitions.size(), 5);
        Assert.assertEquals(clusterMapManager.getWritablePartitionIds(null).size(), 5);
        // Allocate "too many" partitions (1M) to exhaust capacity. Capacity is not exhausted evenly across nodes so some
        // "free" but unusable capacity may be left after trying to allocate these partitions.
        allocatedPartitions = clusterMapManager.allocatePartitions((1000 * 1000), MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, true);
        Assert.assertEquals(((allocatedPartitions.size()) + 5), clusterMapManager.getWritablePartitionIds(null).size());
        System.out.println(freeCapacityDump(clusterMapManager, testHardwareLayout.getHardwareLayout()));
        // Capacity is already exhausted...
        allocatedPartitions = clusterMapManager.allocatePartitions(5, MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, true);
        Assert.assertEquals(allocatedPartitions.size(), 0);
    }

    @Test
    public void rackAwareAllocationTest() {
        int replicaCountPerDataCenter = 3;
        long replicaCapacityInBytes = ((100 * 1024) * 1024) * 1024L;
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha", true);
        PartitionLayout partitionLayout = new PartitionLayout(testHardwareLayout.getHardwareLayout(), null);
        StaticClusterManager clusterMapManager = new StaticClusterAgentsFactory(TestUtils.getDummyConfig(), partitionLayout).getClusterMap();
        List<PartitionId> allocatedPartitions;
        // Allocate five partitions that fit within cluster's capacity
        allocatedPartitions = clusterMapManager.allocatePartitions(5, MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, false);
        Assert.assertEquals(allocatedPartitions.size(), 5);
        Assert.assertEquals(clusterMapManager.getWritablePartitionIds(null).size(), 5);
        StaticClusterManagerTest.checkRackUsage(allocatedPartitions);
        StaticClusterManagerTest.checkNumReplicasPerDatacenter(allocatedPartitions, replicaCountPerDataCenter);
        // Allocate "too many" partitions (1M) to exhaust capacity. Capacity is not exhausted evenly across nodes so some
        // "free" but unusable capacity may be left after trying to allocate these partitions.
        allocatedPartitions = clusterMapManager.allocatePartitions((1000 * 1000), MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, false);
        Assert.assertEquals(((allocatedPartitions.size()) + 5), clusterMapManager.getWritablePartitionIds(null).size());
        System.out.println(freeCapacityDump(clusterMapManager, testHardwareLayout.getHardwareLayout()));
        StaticClusterManagerTest.checkRackUsage(allocatedPartitions);
        // Capacity is already exhausted...
        allocatedPartitions = clusterMapManager.allocatePartitions(5, MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, false);
        Assert.assertEquals(allocatedPartitions.size(), 0);
    }

    @Test
    public void rackAwareOverAllocationTest() {
        int replicaCountPerDataCenter = 4;
        long replicaCapacityInBytes = ((100 * 1024) * 1024) * 1024L;
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha", true);
        PartitionLayout partitionLayout = new PartitionLayout(testHardwareLayout.getHardwareLayout(), null);
        StaticClusterManager clusterMapManager = new StaticClusterAgentsFactory(TestUtils.getDummyConfig(), partitionLayout).getClusterMap();
        List<PartitionId> allocatedPartitions;
        // Require more replicas than there are racks
        allocatedPartitions = clusterMapManager.allocatePartitions(5, MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, false);
        Assert.assertEquals(allocatedPartitions.size(), 5);
        StaticClusterManagerTest.checkNumReplicasPerDatacenter(allocatedPartitions, 3);
        StaticClusterManagerTest.checkRackUsage(allocatedPartitions);
        // Test with retryIfNotRackAware enabled.  We should be able to allocate 4 replicas per datacenter b/c we no
        // longer require unique racks
        allocatedPartitions = clusterMapManager.allocatePartitions(5, MockClusterMap.DEFAULT_PARTITION_CLASS, replicaCountPerDataCenter, replicaCapacityInBytes, true);
        Assert.assertEquals(allocatedPartitions.size(), 5);
        StaticClusterManagerTest.checkNumReplicasPerDatacenter(allocatedPartitions, 4);
    }

    @Test
    public void capacities() {
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        PartitionLayout partitionLayout = new PartitionLayout(testHardwareLayout.getHardwareLayout(), null);
        StaticClusterManager clusterMapManager = new StaticClusterAgentsFactory(TestUtils.getDummyConfig(), partitionLayout).getClusterMap();
        // Confirm initial capacity is available for use
        long raw = clusterMapManager.getRawCapacityInBytes();
        long allocated = clusterMapManager.getAllocatedRawCapacityInBytes();
        long free = clusterMapManager.getUnallocatedRawCapacityInBytes();
        Assert.assertEquals(free, raw);
        Assert.assertEquals(allocated, 0);
        for (Datacenter datacenter : testHardwareLayout.getHardwareLayout().getDatacenters()) {
            for (DataNode dataNode : datacenter.getDataNodes()) {
                long dataNodeFree = clusterMapManager.getUnallocatedRawCapacityInBytes(dataNode);
                Assert.assertEquals(dataNodeFree, ((testHardwareLayout.getDiskCapacityInBytes()) * (testHardwareLayout.getDiskCount())));
                for (Disk disk : dataNode.getDisks()) {
                    long diskFree = clusterMapManager.getUnallocatedRawCapacityInBytes(disk);
                    Assert.assertEquals(diskFree, testHardwareLayout.getDiskCapacityInBytes());
                }
            }
        }
        clusterMapManager.addNewPartition(testHardwareLayout.getIndependentDisks(3), (((100 * 1024) * 1024) * 1024L), MockClusterMap.DEFAULT_PARTITION_CLASS);
        int dcCount = testHardwareLayout.getDatacenterCount();
        // Confirm 100GB has been used on 3 distinct DataNodes / Disks in each datacenter.
        Assert.assertEquals(clusterMapManager.getRawCapacityInBytes(), raw);
        Assert.assertEquals(clusterMapManager.getAllocatedRawCapacityInBytes(), (((((dcCount * 3) * 100) * 1024) * 1024) * 1024L));
        Assert.assertEquals(clusterMapManager.getUnallocatedRawCapacityInBytes(), (free - (((((dcCount * 3) * 100) * 1024) * 1024) * 1024L)));
        for (Datacenter datacenter : testHardwareLayout.getHardwareLayout().getDatacenters()) {
            for (DataNode dataNode : datacenter.getDataNodes()) {
                long dataNodeFree = clusterMapManager.getUnallocatedRawCapacityInBytes(dataNode);
                Assert.assertTrue((dataNodeFree <= ((testHardwareLayout.getDiskCapacityInBytes()) * (testHardwareLayout.getDiskCount()))));
                Assert.assertTrue((dataNodeFree >= (((testHardwareLayout.getDiskCapacityInBytes()) * (testHardwareLayout.getDiskCount())) - (((100 * 1024) * 1024) * 1024L))));
                for (Disk disk : dataNode.getDisks()) {
                    long diskFree = clusterMapManager.getUnallocatedRawCapacityInBytes(disk);
                    Assert.assertTrue((diskFree <= (testHardwareLayout.getDiskCapacityInBytes())));
                    Assert.assertTrue((diskFree >= ((testHardwareLayout.getDiskCapacityInBytes()) - (((100 * 1024) * 1024) * 1024L))));
                }
            }
        }
    }

    @Test
    public void persistAndReadBack() throws Exception {
        String tmpDir = folder.getRoot().getPath();
        Properties props = new Properties();
        props.setProperty("clustermap.cluster.name", "test");
        props.setProperty("clustermap.datacenter.name", "dc1");
        props.setProperty("clustermap.host.name", "localhost");
        String hardwareLayoutSer = tmpDir + "/hardwareLayoutSer.json";
        String partitionLayoutSer = tmpDir + "/partitionLayoutSer.json";
        String hardwareLayoutDe = tmpDir + "/hardwareLayoutDe.json";
        String partitionLayoutDe = tmpDir + "/partitionLayoutDe.json";
        StaticClusterManager clusterMapManagerSer = TestUtils.getTestClusterMap();
        clusterMapManagerSer.persist(hardwareLayoutSer, partitionLayoutSer);
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        StaticClusterManager clusterMapManagerDe = new StaticClusterAgentsFactory(clusterMapConfig, hardwareLayoutSer, partitionLayoutSer).getClusterMap();
        Assert.assertEquals(clusterMapManagerSer, clusterMapManagerDe);
        clusterMapManagerDe.persist(hardwareLayoutDe, partitionLayoutDe);
        StaticClusterManager clusterMapManagerDeDe = new StaticClusterAgentsFactory(clusterMapConfig, hardwareLayoutDe, partitionLayoutDe).getClusterMap();
        Assert.assertEquals(clusterMapManagerDe, clusterMapManagerDeDe);
    }

    @Test
    public void validateSimpleConfig() throws Exception {
        Properties props = new Properties();
        props.setProperty("clustermap.cluster.name", "OneDiskOneReplica");
        props.setProperty("clustermap.datacenter.name", "Datacenter");
        props.setProperty("clustermap.host.name", "localhost");
        String configDir = System.getProperty("user.dir");
        // intelliJ and gradle return different values for user.dir: gradle includes the sub-project directory. To handle
        // this, we check the string suffix for the sub-project directory and append ".." to correctly set configDir.
        if (configDir.endsWith("ambry-clustermap")) {
            configDir += "/..";
        }
        configDir += "/config";
        String hardwareLayoutSer = configDir + "/HardwareLayout.json";
        String partitionLayoutSer = configDir + "/PartitionLayout.json";
        StaticClusterManager clusterMapManager = new StaticClusterAgentsFactory(new ClusterMapConfig(new VerifiableProperties(props)), hardwareLayoutSer, partitionLayoutSer).getClusterMap();
        Assert.assertEquals(clusterMapManager.getWritablePartitionIds(null).size(), 1);
        Assert.assertEquals(clusterMapManager.getUnallocatedRawCapacityInBytes(), 10737418240L);
        Assert.assertNotNull(clusterMapManager.getDataNodeId("localhost", 6667));
    }

    /**
     * Tests for {@link PartitionLayout#getPartitions(String)} and {@link PartitionLayout#getWritablePartitions(String)}.
     *
     * @throws IOException
     * 		
     * @throws JSONException
     * 		
     */
    @Test
    public void getPartitionsTest() throws IOException, JSONException {
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
        Properties props = new Properties();
        props.setProperty("clustermap.host.name", "localhost");
        props.setProperty("clustermap.cluster.name", "cluster");
        props.setProperty("clustermap.datacenter.name", dc);
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        File tempDir = Files.createTempDirectory(("helixClusterManager-" + (new Random().nextInt(1000)))).toFile();
        String tempDirPath = tempDir.getAbsolutePath();
        String hardwareLayoutPath = (tempDirPath + (File.separator)) + "hardwareLayoutTest.json";
        String partitionLayoutPath = (tempDirPath + (File.separator)) + "partitionLayoutTest.json";
        Utils.writeJsonObjectToFile(hardwareLayout.getHardwareLayout().toJSONObject(), hardwareLayoutPath);
        Utils.writeJsonObjectToFile(partitionLayout.toJSONObject(), partitionLayoutPath);
        ClusterMap clusterMapManager = getClusterMap();
        // "good" cases for getPartitions() and getWritablePartitions() only
        // getPartitions(), class null
        List<? extends PartitionId> returnedPartitions = clusterMapManager.getAllPartitionIds(null);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(defaultRw, defaultRo, specialRw, specialRo));
        // getWritablePartitions(), class null
        returnedPartitions = clusterMapManager.getWritablePartitionIds(null);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(defaultRw, specialRw));
        // getPartitions(), class default
        returnedPartitions = clusterMapManager.getAllPartitionIds(TestUtils.DEFAULT_PARTITION_CLASS);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(defaultRw, defaultRo));
        // getWritablePartitions(), class default
        returnedPartitions = clusterMapManager.getWritablePartitionIds(TestUtils.DEFAULT_PARTITION_CLASS);
        TestUtils.checkReturnedPartitions(returnedPartitions, Collections.singletonList(defaultRw));
        // getPartitions(), class special
        returnedPartitions = clusterMapManager.getAllPartitionIds(specialPartitionClass);
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(specialRw, specialRo));
        // getWritablePartitions(), class special
        returnedPartitions = clusterMapManager.getWritablePartitionIds(specialPartitionClass);
        TestUtils.checkReturnedPartitions(returnedPartitions, Collections.singletonList(specialRw));
        // to test the dc affinity, we pick one datanode from "dc" and insert 1 replica for part1 (special class) in "dc"
        // and make sure that it is returned in getPartitions() but not in getWritablePartitions() (because all the other
        // partitions have more than 1 replica in "dc").
        DataNode dataNode = hardwareLayout.getRandomDataNodeFromDc(dc);
        Partition partition = partitionLayout.addNewPartition(dataNode.getDisks().subList(0, 1), testPartitionLayout.replicaCapacityInBytes, specialPartitionClass);
        Utils.writeJsonObjectToFile(partitionLayout.toJSONObject(), partitionLayoutPath);
        clusterMapManager = new StaticClusterAgentsFactory(clusterMapConfig, hardwareLayoutPath, partitionLayoutPath).getClusterMap();
        TestUtils.PartitionRangeCheckParams extraPartCheckParams = new TestUtils.PartitionRangeCheckParams(((specialRo.rangeEnd) + 1), 1, specialPartitionClass, READ_WRITE);
        // getPartitions(), class special
        returnedPartitions = clusterMapManager.getAllPartitionIds(specialPartitionClass);
        Assert.assertTrue("Added partition should exist in returned partitions", returnedPartitions.contains(partition));
        TestUtils.checkReturnedPartitions(returnedPartitions, Arrays.asList(specialRw, specialRo, extraPartCheckParams));
        // getWritablePartitions(), class special
        returnedPartitions = clusterMapManager.getWritablePartitionIds(specialPartitionClass);
        Assert.assertFalse("Added partition should not exist in returned partitions", returnedPartitions.contains(partition));
        TestUtils.checkReturnedPartitions(returnedPartitions, Collections.singletonList(specialRw));
    }

    /**
     * Test that {@link StaticClusterManager#onReplicaEvent(ReplicaId, ReplicaEventType)} works as expected in the presence
     * of various types of server/replica events. This test also verifies the states of datanode, disk and replica are changed
     * correctly based on server event.
     */
    @Test
    public void onReplicaEventTest() {
        Properties props = new Properties();
        props.setProperty("clustermap.host.name", "localhost");
        props.setProperty("clustermap.cluster.name", "cluster");
        props.setProperty("clustermap.datacenter.name", "dc1");
        ClusterMapConfig clusterMapConfig = new ClusterMapConfig(new VerifiableProperties(props));
        TestUtils.TestHardwareLayout testHardwareLayout = new TestUtils.TestHardwareLayout("Alpha");
        TestUtils.TestPartitionLayout testPartitionLayout = new TestUtils.TestPartitionLayout(testHardwareLayout, null);
        ClusterMap clusterMapManager = getClusterMap();
        // Test configuration: we select the disk from one datanode and select the replica on that disk
        // Initial state: only disk is down; Server event: Replica_Unavailable; Expected result: disk becomes available again and replica becomes down
        TestUtils.mockServerEventsAndVerify(clusterMapManager, clusterMapConfig, new TestUtils.ResourceState[]{ Node_Up, Disk_Down, Replica_Up }, Replica_Unavailable, new TestUtils.ResourceState[]{ Node_Up, Disk_Up, Replica_Down });
        // Initial state: only disk is down; Server event: Temporarily_Disabled; Expected result: disk becomes available again and replica becomes down
        TestUtils.mockServerEventsAndVerify(clusterMapManager, clusterMapConfig, new TestUtils.ResourceState[]{ Node_Up, Disk_Down, Replica_Up }, Temporarily_Disabled, new TestUtils.ResourceState[]{ Node_Up, Disk_Up, Replica_Down });
        // Initial state: disk and replica are down; Server event: Replica_Unavailable; Expected result: disk becomes available again
        TestUtils.mockServerEventsAndVerify(clusterMapManager, clusterMapConfig, new TestUtils.ResourceState[]{ Node_Up, Disk_Down, Replica_Down }, Replica_Unavailable, new TestUtils.ResourceState[]{ Node_Up, Disk_Up, Replica_Down });
        // Initial state: disk and replica are down; Server event: Temporarily_Disabled; Expected result: disk becomes available again
        TestUtils.mockServerEventsAndVerify(clusterMapManager, clusterMapConfig, new TestUtils.ResourceState[]{ Node_Up, Disk_Down, Replica_Down }, Temporarily_Disabled, new TestUtils.ResourceState[]{ Node_Up, Disk_Up, Replica_Down });
        // Initial state: disk and replica are down; Server event: Partition_ReadOnly; Expected result: disk and replica become available again
        TestUtils.mockServerEventsAndVerify(clusterMapManager, clusterMapConfig, new TestUtils.ResourceState[]{ Node_Up, Disk_Down, Replica_Down }, Partition_ReadOnly, new TestUtils.ResourceState[]{ Node_Up, Disk_Up, Replica_Up });
        // Initial state: everything is up; Server event: IO_Error; Expected result: disk and replica become unavailable
        TestUtils.mockServerEventsAndVerify(clusterMapManager, clusterMapConfig, new TestUtils.ResourceState[]{ Node_Up, Disk_Up, Replica_Up }, IO_Error, new TestUtils.ResourceState[]{ Node_Up, Disk_Down, Replica_Down });
        // Initial state: everything is up; Server event: Disk_Unavailable; Expected result: disk and replica become unavailable
        TestUtils.mockServerEventsAndVerify(clusterMapManager, clusterMapConfig, new TestUtils.ResourceState[]{ Node_Up, Disk_Up, Replica_Up }, Disk_Unavailable, new TestUtils.ResourceState[]{ Node_Up, Disk_Down, Replica_Down });
    }
}

