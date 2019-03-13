/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.partition.impl;


import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionListener;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InternalPartitionImplTest {
    private static final InetAddress LOCALHOST;

    static {
        try {
            LOCALHOST = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private final PartitionReplica localReplica = new PartitionReplica(InternalPartitionImplTest.newAddress(5000), UuidUtil.newUnsecureUuidString());

    private final PartitionReplica[] replicaOwners = new PartitionReplica[InternalPartition.MAX_REPLICA_COUNT];

    private final InternalPartitionImplTest.TestPartitionListener partitionListener = new InternalPartitionImplTest.TestPartitionListener();

    private InternalPartitionImpl partition;

    @Test
    public void testIsLocal_whenOwnedByThis() {
        replicaOwners[0] = localReplica;
        partition.setInitialReplicas(replicaOwners);
        Assert.assertTrue(partition.isLocal());
    }

    @Test
    public void testIsLocal_whenNOTOwnedByThis() {
        replicaOwners[0] = new PartitionReplica(InternalPartitionImplTest.newAddress(6000), UuidUtil.newUnsecureUuidString());
        partition.setInitialReplicas(replicaOwners);
        Assert.assertFalse(partition.isLocal());
    }

    @Test
    public void testGetOwnerOrNull_whenOwnerExists() {
        replicaOwners[0] = localReplica;
        partition.setInitialReplicas(replicaOwners);
        Assert.assertEquals(localReplica, partition.getOwnerReplicaOrNull());
        Assert.assertEquals(localReplica.address(), partition.getOwnerOrNull());
    }

    @Test
    public void testGetOwnerOrNull_whenOwnerNOTExists() {
        Assert.assertNull(partition.getOwnerOrNull());
    }

    @Test
    public void testGetReplicaAddress() {
        replicaOwners[0] = localReplica;
        partition.setInitialReplicas(replicaOwners);
        Assert.assertEquals(localReplica, partition.getReplica(0));
        Assert.assertEquals(localReplica.address(), partition.getReplicaAddress(0));
        for (int i = 1; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
            Assert.assertNull(partition.getReplica(i));
            Assert.assertNull(partition.getReplicaAddress(i));
        }
    }

    @Test
    public void testSetInitialReplicaAddresses() {
        for (int i = 0; i < (replicaOwners.length); i++) {
            replicaOwners[i] = new PartitionReplica(InternalPartitionImplTest.newAddress((5000 + i)), UuidUtil.newUnsecureUuidString());
        }
        partition.setInitialReplicas(replicaOwners);
        for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
            Assert.assertEquals(replicaOwners[i], partition.getReplica(i));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testSetInitialReplicaAddresses_multipleTimes() {
        replicaOwners[0] = localReplica;
        partition.setInitialReplicas(replicaOwners);
        partition.setInitialReplicas(replicaOwners);
    }

    @Test
    public void testSetInitialReplicaAddresses_ListenerShouldNOTBeCalled() {
        replicaOwners[0] = localReplica;
        partition.setInitialReplicas(replicaOwners);
        Assert.assertEquals(0, partitionListener.eventCount);
    }

    @Test
    public void testSetReplicaAddresses() {
        for (int i = 0; i < (replicaOwners.length); i++) {
            replicaOwners[i] = new PartitionReplica(InternalPartitionImplTest.newAddress((5000 + i)), UuidUtil.newUnsecureUuidString());
        }
        partition.setReplicas(replicaOwners);
        for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
            Assert.assertEquals(replicaOwners[i], partition.getReplica(i));
        }
    }

    @Test
    public void testSetReplicaAddresses_afterInitialSet() {
        replicaOwners[0] = localReplica;
        partition.setInitialReplicas(replicaOwners);
        partition.setReplicas(replicaOwners);
    }

    @Test
    public void testSetReplicaAddresses_multipleTimes() {
        replicaOwners[0] = localReplica;
        partition.setReplicas(replicaOwners);
        partition.setReplicas(replicaOwners);
    }

    @Test
    public void testSetReplicaAddresses_ListenerShouldBeCalled() {
        replicaOwners[0] = localReplica;
        replicaOwners[1] = new PartitionReplica(InternalPartitionImplTest.newAddress(5001), UuidUtil.newUnsecureUuidString());
        partition.setReplicas(replicaOwners);
        Assert.assertEquals(2, partitionListener.eventCount);
    }

    @Test
    public void testListenerShouldNOTBeCalled_whenReplicaRemainsSame() {
        replicaOwners[0] = localReplica;
        partition.setReplicas(replicaOwners);
        partitionListener.reset();
        partition.setReplicas(replicaOwners);
        Assert.assertEquals(0, partitionListener.eventCount);
    }

    @Test
    public void testIsOwnerOrBackup() {
        replicaOwners[0] = localReplica;
        Address otherAddress = InternalPartitionImplTest.newAddress(5001);
        replicaOwners[1] = new PartitionReplica(otherAddress, UuidUtil.newUnsecureUuidString());
        partition.setReplicas(replicaOwners);
        Assert.assertTrue(partition.isOwnerOrBackup(replicaOwners[0]));
        Assert.assertTrue(partition.isOwnerOrBackup(localReplica));
        Assert.assertTrue(partition.isOwnerOrBackup(replicaOwners[1]));
        Assert.assertTrue(partition.isOwnerOrBackup(otherAddress));
        Assert.assertFalse(partition.isOwnerOrBackup(new PartitionReplica(InternalPartitionImplTest.newAddress(6000), UuidUtil.newUnsecureUuidString())));
        Assert.assertFalse(partition.isOwnerOrBackup(InternalPartitionImplTest.newAddress(6000)));
    }

    @Test
    public void testGetReplicaIndex() {
        replicaOwners[0] = localReplica;
        replicaOwners[1] = new PartitionReplica(InternalPartitionImplTest.newAddress(5001), UuidUtil.newUnsecureUuidString());
        partition.setReplicas(replicaOwners);
        Assert.assertEquals(0, partition.getReplicaIndex(replicaOwners[0]));
        Assert.assertEquals(1, partition.getReplicaIndex(replicaOwners[1]));
        Assert.assertEquals((-1), partition.getReplicaIndex(new PartitionReplica(InternalPartitionImplTest.newAddress(6000), UuidUtil.newUnsecureUuidString())));
    }

    @Test
    public void testReset() {
        for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
            replicaOwners[i] = new PartitionReplica(InternalPartitionImplTest.newAddress((5000 + i)), UuidUtil.newUnsecureUuidString());
        }
        partition.setReplicas(replicaOwners);
        partition.reset(localReplica);
        for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
            Assert.assertNull(partition.getReplicaAddress(i));
        }
        Assert.assertFalse(partition.isMigrating());
    }

    private static class TestPartitionListener implements PartitionListener {
        private int eventCount;

        @Override
        public void replicaChanged(PartitionReplicaChangeEvent event) {
            (eventCount)++;
        }

        void reset() {
            eventCount = 0;
        }
    }
}

