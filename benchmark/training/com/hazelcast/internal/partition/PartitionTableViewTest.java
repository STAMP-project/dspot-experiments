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
package com.hazelcast.internal.partition;


import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RandomPicker;
import com.hazelcast.util.UuidUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static InternalPartition.MAX_REPLICA_COUNT;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionTableViewTest {
    @Test
    public void test_getVersion() {
        int version = RandomPicker.getInt(1000);
        PartitionTableView table = new PartitionTableView(new PartitionReplica[10][InternalPartition.MAX_REPLICA_COUNT], version);
        Assert.assertEquals(version, table.getVersion());
    }

    @Test
    public void test_getLength() {
        int len = RandomPicker.getInt(100);
        PartitionTableView table = new PartitionTableView(new PartitionReplica[len][InternalPartition.MAX_REPLICA_COUNT], 0);
        Assert.assertEquals(len, table.getLength());
    }

    @Test
    public void test_getMember() throws Exception {
        PartitionReplica[][] members = PartitionTableViewTest.createRandomMembers();
        PartitionTableView table = new PartitionTableView(members, 0);
        Assert.assertEquals(members.length, table.getLength());
        for (int i = 0; i < (members.length); i++) {
            for (int j = 0; j < (InternalPartition.MAX_REPLICA_COUNT); j++) {
                Assert.assertEquals(members[i][j], table.getReplica(i, j));
            }
        }
    }

    @Test
    public void test_getMembers() throws Exception {
        PartitionReplica[][] members = PartitionTableViewTest.createRandomMembers();
        PartitionTableView table = new PartitionTableView(members, 0);
        Assert.assertEquals(members.length, table.getLength());
        for (int i = 0; i < (members.length); i++) {
            PartitionReplica[] replicas = table.getReplicas(i);
            Assert.assertNotSame(members[i], replicas);
            Assert.assertArrayEquals(members[i], replicas);
        }
    }

    @Test
    public void test_getMembers_withNullAddress() {
        PartitionReplica[][] members = new PartitionReplica[100][InternalPartition.MAX_REPLICA_COUNT];
        PartitionTableView table = new PartitionTableView(members, 0);
        Assert.assertEquals(members.length, table.getLength());
        for (int i = 0; i < (members.length); i++) {
            PartitionReplica[] replicas = table.getReplicas(i);
            Assert.assertNotSame(members[i], replicas);
            Assert.assertArrayEquals(members[i], replicas);
        }
    }

    @Test
    public void test_createUsingInternalPartitions() throws Exception {
        PartitionReplica[][] members = PartitionTableViewTest.createRandomMembers();
        InternalPartition[] partitions = new InternalPartition[members.length];
        for (int i = 0; i < (partitions.length); i++) {
            partitions[i] = new com.hazelcast.internal.partition.impl.InternalPartitionImpl(i, null, members[i][0], members[i]);
        }
        PartitionTableView table = new PartitionTableView(partitions, 0);
        Assert.assertEquals(partitions.length, table.getLength());
        for (int i = 0; i < (members.length); i++) {
            for (int j = 0; j < (MAX_REPLICA_COUNT); j++) {
                Assert.assertEquals(partitions[i].getReplica(j), table.getReplica(i, j));
            }
        }
    }

    @Test
    public void testIdentical() throws Exception {
        PartitionTableView table = PartitionTableViewTest.createRandomPartitionTable();
        Assert.assertEquals(table, table);
    }

    @Test
    public void testEquals() throws Exception {
        PartitionTableView table1 = PartitionTableViewTest.createRandomPartitionTable();
        PartitionTableView table2 = new PartitionTableView(PartitionTableViewTest.extractPartitionTableMembers(table1), table1.getVersion());
        Assert.assertEquals(table1, table2);
        Assert.assertEquals(table1.hashCode(), table2.hashCode());
    }

    @Test
    public void testEquals_whenVersionIsDifferent() throws Exception {
        PartitionTableView table1 = PartitionTableViewTest.createRandomPartitionTable();
        PartitionTableView table2 = new PartitionTableView(PartitionTableViewTest.extractPartitionTableMembers(table1), ((table1.getVersion()) + 1));
        Assert.assertNotEquals(table1, table2);
    }

    @Test
    public void testEquals_whenSingleAddressIsDifferent() throws Exception {
        PartitionTableView table1 = PartitionTableViewTest.createRandomPartitionTable();
        PartitionReplica[][] addresses = PartitionTableViewTest.extractPartitionTableMembers(table1);
        PartitionReplica member = addresses[((addresses.length) - 1)][((InternalPartition.MAX_REPLICA_COUNT) - 1)];
        Address newAddress = new Address(member.address().getInetAddress(), ((member.address().getPort()) + 1));
        addresses[((addresses.length) - 1)][((InternalPartition.MAX_REPLICA_COUNT) - 1)] = new PartitionReplica(newAddress, UuidUtil.newUnsecureUuidString());
        PartitionTableView table2 = new PartitionTableView(addresses, table1.getVersion());
        Assert.assertNotEquals(table1, table2);
    }
}

