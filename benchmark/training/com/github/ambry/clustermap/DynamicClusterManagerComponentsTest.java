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


import HardwareState.AVAILABLE;
import HardwareState.UNAVAILABLE;
import PartitionState.READ_ONLY;
import PartitionState.READ_WRITE;
import PortType.PLAINTEXT;
import PortType.SSL;
import com.github.ambry.config.ClusterMapConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.UtilsTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;

import static HardwareState.AVAILABLE;
import static HardwareState.UNAVAILABLE;


/**
 * Tests for the dynamic cluster manager components {@link AmbryDataNode}, {@link AmbryDisk}, {@link AmbryPartition}
 * and {@link AmbryReplica}.
 */
public class DynamicClusterManagerComponentsTest {
    private static final int PORT_NUM1 = 2000;

    private static final int PORT_NUM2 = 2001;

    private static final String RACK_ID = "1";

    private static final long XID = 64;

    private static final int SSL_PORT_NUM = 3000;

    private static final String HOST_NAME = TestUtils.getLocalHost();

    private final ClusterMapConfig clusterMapConfig1;

    private final ClusterMapConfig clusterMapConfig2;

    private AtomicLong sealedStateChangeCounter;

    /**
     * Instantiate and initialize clustermap configs.
     */
    public DynamicClusterManagerComponentsTest() {
        Properties props = new Properties();
        props.setProperty("clustermap.host.name", DynamicClusterManagerComponentsTest.HOST_NAME);
        props.setProperty("clustermap.cluster.name", "clusterName");
        props.setProperty("clustermap.datacenter.name", "DC0");
        props.setProperty("clustermap.ssl.enabled.datacenters", "DC1");
        clusterMapConfig1 = new ClusterMapConfig(new VerifiableProperties(props));
        props.setProperty("clustermap.datacenter.name", "DC1");
        clusterMapConfig2 = new ClusterMapConfig(new VerifiableProperties(props));
    }

    /**
     * Test {@link AmbryDataNode}, {@link AmbryDisk}, {@link AmbryPartition} and {@link AmbryReplica}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void helixClusterManagerComponentsTest() throws Exception {
        DynamicClusterManagerComponentsTest.MockClusterManagerCallback mockClusterManagerCallback = new DynamicClusterManagerComponentsTest.MockClusterManagerCallback();
        // AmbryDataNode test
        try {
            new AmbryDataNode("DC1", clusterMapConfig2, DynamicClusterManagerComponentsTest.HOST_NAME, DynamicClusterManagerComponentsTest.PORT_NUM1, DynamicClusterManagerComponentsTest.RACK_ID, null, DynamicClusterManagerComponentsTest.XID, mockClusterManagerCallback);
            Assert.fail("Datanode construction should have failed when SSL is enabled and SSL port is null");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            new AmbryDataNode("DC1", clusterMapConfig1, DynamicClusterManagerComponentsTest.HOST_NAME, ((MAX_PORT) + 1), DynamicClusterManagerComponentsTest.RACK_ID, null, DynamicClusterManagerComponentsTest.XID, mockClusterManagerCallback);
            Assert.fail("Datanode construction should have failed when port num is outside the valid range");
        } catch (IllegalArgumentException e) {
            // OK
        }
        AmbryDataNode datanode1 = new AmbryDataNode("DC0", clusterMapConfig1, DynamicClusterManagerComponentsTest.HOST_NAME, DynamicClusterManagerComponentsTest.PORT_NUM1, DynamicClusterManagerComponentsTest.RACK_ID, DynamicClusterManagerComponentsTest.SSL_PORT_NUM, DynamicClusterManagerComponentsTest.XID, mockClusterManagerCallback);
        AmbryDataNode datanode2 = new AmbryDataNode("DC1", clusterMapConfig2, DynamicClusterManagerComponentsTest.HOST_NAME, DynamicClusterManagerComponentsTest.PORT_NUM2, DynamicClusterManagerComponentsTest.RACK_ID, DynamicClusterManagerComponentsTest.SSL_PORT_NUM, DynamicClusterManagerComponentsTest.XID, mockClusterManagerCallback);
        Assert.assertEquals(datanode1.getDatacenterName(), "DC0");
        Assert.assertEquals(datanode1.getHostname(), DynamicClusterManagerComponentsTest.HOST_NAME);
        Assert.assertEquals(datanode1.getPort(), DynamicClusterManagerComponentsTest.PORT_NUM1);
        Assert.assertEquals(datanode1.getSSLPort(), DynamicClusterManagerComponentsTest.SSL_PORT_NUM);
        Assert.assertEquals(datanode1.getRackId(), DynamicClusterManagerComponentsTest.RACK_ID);
        Assert.assertEquals(datanode1.getXid(), DynamicClusterManagerComponentsTest.XID);
        Assert.assertTrue(datanode1.hasSSLPort());
        Assert.assertEquals(PLAINTEXT, datanode1.getPortToConnectTo().getPortType());
        Assert.assertTrue(datanode2.hasSSLPort());
        Assert.assertEquals(SSL, datanode2.getPortToConnectTo().getPortType());
        Assert.assertEquals(AVAILABLE, datanode1.getState());
        datanode1.setState(UNAVAILABLE);
        Assert.assertEquals(UNAVAILABLE, datanode1.getState());
        datanode1.setState(AVAILABLE);
        Assert.assertEquals(AVAILABLE, datanode1.getState());
        Assert.assertTrue(((datanode1.compareTo(datanode1)) == 0));
        Assert.assertTrue(((datanode1.compareTo(datanode2)) != 0));
        // AmbryDisk tests
        String mountPath1 = "/mnt/1";
        String mountPath2 = "/mnt/2";
        try {
            new AmbryDisk(clusterMapConfig1, null, mountPath1, UNAVAILABLE, MAX_DISK_CAPACITY_IN_BYTES);
            Assert.fail("disk initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            new AmbryDisk(clusterMapConfig1, datanode1, null, UNAVAILABLE, MAX_DISK_CAPACITY_IN_BYTES);
            Assert.fail("disk initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            new AmbryDisk(clusterMapConfig1, datanode1, "", UNAVAILABLE, MAX_DISK_CAPACITY_IN_BYTES);
            Assert.fail("disk initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            new AmbryDisk(clusterMapConfig1, datanode1, "0", UNAVAILABLE, MAX_DISK_CAPACITY_IN_BYTES);
            Assert.fail("disk initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            new AmbryDisk(clusterMapConfig1, datanode1, mountPath1, UNAVAILABLE, ((MAX_DISK_CAPACITY_IN_BYTES) + 1));
            Assert.fail("disk initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        AmbryDisk disk1 = new AmbryDisk(clusterMapConfig1, datanode1, mountPath1, AVAILABLE, MAX_DISK_CAPACITY_IN_BYTES);
        AmbryDisk disk2 = new AmbryDisk(clusterMapConfig2, datanode2, mountPath2, AVAILABLE, MAX_DISK_CAPACITY_IN_BYTES);
        Assert.assertEquals(mountPath1, disk1.getMountPath());
        Assert.assertEquals(MAX_DISK_CAPACITY_IN_BYTES, disk1.getRawCapacityInBytes());
        Assert.assertEquals(AVAILABLE, disk1.getState());
        disk1.setState(UNAVAILABLE);
        Assert.assertEquals(UNAVAILABLE, disk1.getState());
        disk1.setState(AVAILABLE);
        Assert.assertEquals(AVAILABLE, disk1.getState());
        Assert.assertTrue(disk1.getDataNode().equals(datanode1));
        datanode1.setState(UNAVAILABLE);
        Assert.assertEquals(UNAVAILABLE, disk1.getState());
        // AmbryPartition tests
        // All partitions are READ_WRITE initially.
        sealedStateChangeCounter = new AtomicLong(0);
        String partition1Class = UtilsTest.getRandomString(10);
        String partition2Class = UtilsTest.getRandomString(10);
        AmbryPartition partition1 = new AmbryPartition(1, partition1Class, mockClusterManagerCallback);
        AmbryPartition partition2 = new AmbryPartition(2, partition2Class, mockClusterManagerCallback);
        Assert.assertTrue(partition1.isEqual(partition1.toPathString()));
        Assert.assertTrue(((partition1.compareTo(partition1)) == 0));
        Assert.assertFalse(partition1.isEqual(partition2.toPathString()));
        Assert.assertTrue(((partition1.compareTo(partition2)) != 0));
        Assert.assertEquals("Partition class not as expected", partition1Class, partition1.getPartitionClass());
        Assert.assertEquals("Partition class not as expected", partition2Class, partition2.getPartitionClass());
        // AmbryReplica tests
        try {
            new AmbryReplica(clusterMapConfig1, null, disk1, false, MAX_REPLICA_CAPACITY_IN_BYTES, false);
            Assert.fail("Replica initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            new AmbryReplica(clusterMapConfig1, partition1, null, false, MAX_REPLICA_CAPACITY_IN_BYTES, false);
            Assert.fail("Replica initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        try {
            new AmbryReplica(clusterMapConfig1, partition1, disk1, false, ((MAX_REPLICA_CAPACITY_IN_BYTES) + 1), false);
            Assert.fail("Replica initialization should fail with invalid arguments");
        } catch (IllegalStateException e) {
            // OK
        }
        // Create a few replicas and make the mockClusterManagerCallback aware of the association.
        AmbryReplica replica1 = new AmbryReplica(clusterMapConfig1, partition1, disk1, false, MAX_REPLICA_CAPACITY_IN_BYTES, false);
        mockClusterManagerCallback.addReplicaToPartition(partition1, replica1);
        AmbryReplica replica2 = new AmbryReplica(clusterMapConfig1, partition2, disk1, false, MIN_REPLICA_CAPACITY_IN_BYTES, false);
        mockClusterManagerCallback.addReplicaToPartition(partition2, replica2);
        AmbryReplica replica3 = new AmbryReplica(clusterMapConfig2, partition1, disk2, false, MIN_REPLICA_CAPACITY_IN_BYTES, false);
        mockClusterManagerCallback.addReplicaToPartition(partition1, replica3);
        AmbryReplica replica4 = new AmbryReplica(clusterMapConfig2, partition2, disk2, false, MIN_REPLICA_CAPACITY_IN_BYTES, true);
        mockClusterManagerCallback.addReplicaToPartition(partition2, replica4);
        AmbryReplica replica5 = new AmbryReplica(clusterMapConfig1, partition1, disk1, true, MIN_REPLICA_CAPACITY_IN_BYTES, false);
        mockClusterManagerCallback.addReplicaToPartition(partition1, replica5);
        sealedStateChangeCounter.incrementAndGet();
        Assert.assertEquals(replica1.getDiskId().getMountPath(), replica1.getMountPath());
        List<AmbryReplica> peerReplicas = replica1.getPeerReplicaIds();
        Assert.assertEquals(2, peerReplicas.size());
        Assert.assertEquals(replica1.getPartitionId(), peerReplicas.get(0).getPartitionId());
        Assert.assertEquals(replica3, peerReplicas.get(0));
        Assert.assertEquals(replica5, peerReplicas.get(1));
        Assert.assertTrue("Replica should be in stopped state", peerReplicas.get(1).isDown());
        List<AmbryReplica> replicaList1 = partition1.getReplicaIds();
        List<AmbryReplica> replicaList2 = partition2.getReplicaIds();
        Assert.assertEquals(("Mismatch in number of replicas. Found: " + (replicaList1.toString())), 3, replicaList1.size());
        Assert.assertTrue(replicaList1.contains(replica1));
        Assert.assertTrue(replicaList1.contains(replica3));
        Assert.assertTrue(replicaList1.contains(replica5));
        Assert.assertEquals(2, replicaList2.size());
        Assert.assertTrue(replicaList2.contains(replica2));
        Assert.assertTrue(replicaList2.contains(replica4));
        Assert.assertEquals(partition1.getPartitionState(), READ_WRITE);
        Assert.assertEquals(partition2.getPartitionState(), READ_ONLY);
        replica1.setSealedState(true);
        sealedStateChangeCounter.incrementAndGet();
        Assert.assertEquals(partition1.getPartitionState(), READ_ONLY);
        replica3.setSealedState(true);
        sealedStateChangeCounter.incrementAndGet();
        Assert.assertEquals(partition1.getPartitionState(), READ_ONLY);
        replica1.setSealedState(false);
        sealedStateChangeCounter.incrementAndGet();
        Assert.assertEquals(partition1.getPartitionState(), READ_ONLY);
        replica3.setSealedState(false);
        sealedStateChangeCounter.incrementAndGet();
        Assert.assertEquals(partition1.getPartitionState(), READ_WRITE);
        replica4.setSealedState(false);
        sealedStateChangeCounter.incrementAndGet();
        Assert.assertEquals(partition2.getPartitionState(), READ_WRITE);
    }

    /**
     * A helper class that mocks the {@link ClusterManagerCallback} and stores partition to replicas mapping internally
     * as told.
     */
    private class MockClusterManagerCallback implements ClusterManagerCallback {
        Map<AmbryPartition, List<AmbryReplica>> partitionToReplicas = new HashMap<>();

        Map<AmbryDataNode, Set<AmbryDisk>> dataNodeToDisks = new HashMap<>();

        @Override
        public List<AmbryReplica> getReplicaIdsForPartition(AmbryPartition partition) {
            return new ArrayList<AmbryReplica>(partitionToReplicas.get(partition));
        }

        @Override
        public Collection<AmbryDisk> getDisks(AmbryDataNode dataNode) {
            if (dataNode != null) {
                return dataNodeToDisks.get(dataNode);
            }
            List<AmbryDisk> disksToReturn = new ArrayList<>();
            for (Set<AmbryDisk> disks : dataNodeToDisks.values()) {
                disksToReturn.addAll(disks);
            }
            return disksToReturn;
        }

        @Override
        public long getSealedStateChangeCounter() {
            return sealedStateChangeCounter.get();
        }

        /**
         * Associate the replica with the given partition.
         *
         * @param partition
         * 		the {@link AmbryPartition}.
         * @param replica
         * 		the {@link AmbryReplica}.
         */
        void addReplicaToPartition(AmbryPartition partition, AmbryReplica replica) {
            if (!(partitionToReplicas.containsKey(partition))) {
                partitionToReplicas.put(partition, new ArrayList<AmbryReplica>());
            }
            partitionToReplicas.get(partition).add(replica);
        }
    }
}

