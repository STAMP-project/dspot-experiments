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


import InternalPartition.MAX_REPLICA_COUNT;
import PartitionGroupConfig.MemberGroupType.CUSTOM;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.core.Member;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionStateGenerator;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.membergroup.DefaultMemberGroup;
import com.hazelcast.partition.membergroup.HostAwareMemberGroupFactory;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.partition.membergroup.SingleMemberGroupFactory;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.MemberVersion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionStateGeneratorTest {
    private static final MemberVersion VERSION = MemberVersion.of(BuildInfoProvider.getBuildInfo().getVersion());

    private static final boolean PRINT_STATE = false;

    @Test
    public void testRandomPartitionGenerator() throws Exception {
        final MemberGroupFactory memberGroupFactory = new SingleMemberGroupFactory();
        test(memberGroupFactory);
    }

    // "random host groups may cause non-uniform distribution of partitions when node size go down significantly!")
    @Test
    public void testHostAwarePartitionStateGenerator() throws Exception {
        final HostAwareMemberGroupFactory memberGroupFactory = new HostAwareMemberGroupFactory();
        test(memberGroupFactory);
    }

    @Test
    public void testCustomPartitionStateGenerator() throws Exception {
        final MemberGroupFactory memberGroupFactory = new MemberGroupFactory() {
            public Collection<MemberGroup> createMemberGroups(Collection<? extends Member> members) {
                MemberGroup[] g = new MemberGroup[4];
                for (int i = 0; i < (g.length); i++) {
                    g[i] = new DefaultMemberGroup();
                }
                for (Member member : members) {
                    Address address = member.getAddress();
                    if ((even(address.getHost().hashCode())) && (even(address.getPort()))) {
                        g[0].addMember(member);
                    } else
                        if ((even(address.getHost().hashCode())) && (!(even(address.getPort())))) {
                            g[1].addMember(member);
                        } else
                            if ((!(even(address.getHost().hashCode()))) && (even(address.getPort()))) {
                                g[2].addMember(member);
                            } else
                                if ((!(even(address.getHost().hashCode()))) && (!(even(address.getPort())))) {
                                    g[3].addMember(member);
                                }



                }
                List<MemberGroup> list = new LinkedList<MemberGroup>();
                for (MemberGroup memberGroup : g) {
                    if ((memberGroup.size()) > 0) {
                        list.add(memberGroup);
                    }
                }
                return list;
            }

            boolean even(int k) {
                return (k % 2) == 0;
            }
        };
        test(memberGroupFactory);
    }

    @Test
    public void testConfigCustomPartitionStateGenerator() throws Exception {
        PartitionGroupConfig config = new PartitionGroupConfig();
        config.setEnabled(true);
        config.setGroupType(CUSTOM);
        MemberGroupConfig mgCfg0 = new MemberGroupConfig();
        MemberGroupConfig mgCfg1 = new MemberGroupConfig();
        MemberGroupConfig mgCfg2 = new MemberGroupConfig();
        MemberGroupConfig mgCfg3 = new MemberGroupConfig();
        config.addMemberGroupConfig(mgCfg0);
        config.addMemberGroupConfig(mgCfg1);
        config.addMemberGroupConfig(mgCfg2);
        config.addMemberGroupConfig(mgCfg3);
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < 255; i++) {
                MemberGroupConfig mg;
                switch (i % 4) {
                    case 0 :
                        mg = mgCfg0;
                        break;
                    case 1 :
                        mg = mgCfg1;
                        break;
                    case 2 :
                        mg = mgCfg2;
                        break;
                    case 3 :
                        mg = mgCfg3;
                        break;
                    default :
                        throw new IllegalArgumentException();
                }
                mg.addInterface(((("10.10." + k) + ".") + i));
            }
        }
        test(new com.hazelcast.partition.membergroup.ConfigMemberGroupFactory(config.getMemberGroupConfigs()));
    }

    @Test
    public void testXmlPartitionGroupConfig() {
        Config config = new ClasspathXmlConfig("hazelcast-fullconfig.xml");
        PartitionGroupConfig partitionGroupConfig = config.getPartitionGroupConfig();
        Assert.assertTrue(partitionGroupConfig.isEnabled());
        Assert.assertEquals(CUSTOM, partitionGroupConfig.getGroupType());
        Assert.assertEquals(2, partitionGroupConfig.getMemberGroupConfigs().size());
    }

    @Test
    public void testOnlyUnassignedArrangement() throws Exception {
        List<Member> memberList = PartitionStateGeneratorTest.createMembers(10, 1);
        MemberGroupFactory memberGroupFactory = new SingleMemberGroupFactory();
        Collection<MemberGroup> groups = memberGroupFactory.createMemberGroups(memberList);
        PartitionStateGenerator generator = new PartitionStateGeneratorImpl();
        PartitionReplica[][] state = generator.arrange(groups, PartitionStateGeneratorTest.emptyPartitionArray(100));
        // unassign some partitions entirely
        Collection<Integer> unassignedPartitions = new ArrayList<Integer>();
        for (int i = 0; i < (state.length); i++) {
            if ((i % 3) == 0) {
                state[i] = new PartitionReplica[InternalPartition.MAX_REPLICA_COUNT];
                unassignedPartitions.add(i);
            }
        }
        // unassign only backup replicas of some partitions
        for (int i = 0; i < (state.length); i++) {
            if ((i % 10) == 0) {
                Arrays.fill(state[i], 1, MAX_REPLICA_COUNT, null);
            }
        }
        InternalPartition[] partitions = PartitionStateGeneratorTest.toPartitionArray(state);
        state = generator.arrange(groups, partitions, unassignedPartitions);
        for (int pid = 0; pid < (state.length); pid++) {
            PartitionReplica[] addresses = state[pid];
            if (unassignedPartitions.contains(pid)) {
                for (PartitionReplica address : addresses) {
                    Assert.assertNotNull(address);
                }
            } else {
                InternalPartition partition = partitions[pid];
                for (int replicaIx = 0; replicaIx < (InternalPartition.MAX_REPLICA_COUNT); replicaIx++) {
                    Assert.assertEquals(partition.getReplica(replicaIx), addresses[replicaIx]);
                }
            }
        }
    }

    private static class GroupPartitionState {
        MemberGroup group;

        Set<Integer>[] groupPartitions = new Set[InternalPartition.MAX_REPLICA_COUNT];

        Map<PartitionReplica, Set<Integer>[]> nodePartitionsMap = new HashMap<PartitionReplica, Set<Integer>[]>();

        {
            for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
                groupPartitions[i] = new HashSet<Integer>();
            }
        }

        Set<Integer>[] getNodePartitions(PartitionReplica node) {
            Set<Integer>[] nodePartitions = nodePartitionsMap.get(node);
            if (nodePartitions == null) {
                nodePartitions = new Set[InternalPartition.MAX_REPLICA_COUNT];
                for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
                    nodePartitions[i] = new HashSet<Integer>();
                }
                nodePartitionsMap.put(node, nodePartitions);
            }
            return nodePartitions;
        }
    }
}

