package com.github.ambry.clustermap;


import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import static PartitionState.READ_WRITE;


/**
 * Tests {@link Replica} class.
 */
public class ReplicaTest {
    @Test
    public void basics() throws JSONException {
        // Much of Replica depends on Partition. With a null Partition, only nominal testing can be done.
        TestUtils.TestHardwareLayout thl = new TestUtils.TestHardwareLayout("Alpha");
        Disk disk = thl.getRandomDisk();
        TestReplica replicaA = new TestReplica(thl, disk);
        Assert.assertEquals(getDiskId(), disk);
        TestReplica replicaB = new TestReplica(thl.getHardwareLayout(), TestUtils.getJsonReplica(disk));
        Assert.assertEquals(getDiskId(), disk);
    }

    @Test
    public void validation() throws JSONException {
        TestUtils.TestHardwareLayout thl = new TestUtils.TestHardwareLayout("Alpha");
        Partition partition = new Partition(1, thl.clusterMapConfig.clusterMapDefaultPartitionClass, READ_WRITE, (((100 * 1024) * 1024) * 1024L));
        try {
            // Null Partition
            new Replica(null, thl.getRandomDisk(), thl.clusterMapConfig);
            Assert.fail("Should have failed validation.");
        } catch (IllegalStateException e) {
            // Expected.
        }
        try {
            // Null clusterMapConfig
            new Replica(partition, thl.getRandomDisk(), null);
            Assert.fail("Should have failed during instantiation");
        } catch (IllegalStateException e) {
            // Expected.
        }
    }
}

