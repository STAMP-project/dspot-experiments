/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.core;


import MulticastDiscoveryProvider.TYPE;
import ReadConsistency.SEQUENTIAL;
import Recovery.RECOVER;
import Replication.SYNCHRONOUS;
import io.atomix.cluster.ClusterConfig;
import io.atomix.cluster.MemberConfig;
import io.atomix.cluster.MembershipConfig;
import io.atomix.cluster.MulticastConfig;
import io.atomix.cluster.discovery.MulticastDiscoveryConfig;
import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.protocol.HeartbeatMembershipProtocolConfig;
import io.atomix.core.log.DistributedLogConfig;
import io.atomix.core.map.AtomicMapConfig;
import io.atomix.core.profile.ConsensusProfileConfig;
import io.atomix.core.profile.DataGridProfileConfig;
import io.atomix.core.set.DistributedSetConfig;
import io.atomix.core.value.AtomicValueConfig;
import io.atomix.protocols.backup.MultiPrimaryProtocolConfig;
import io.atomix.protocols.backup.partition.PrimaryBackupPartitionGroupConfig;
import io.atomix.protocols.log.DistributedLogProtocolConfig;
import io.atomix.protocols.log.partition.LogPartitionGroupConfig;
import io.atomix.protocols.raft.MultiRaftProtocolConfig;
import io.atomix.protocols.raft.partition.RaftPartitionGroupConfig;
import io.atomix.utils.memory.MemorySize;
import java.time.Duration;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Atomix configuration test.
 */
public class AtomixConfigTest {
    @Test
    public void testDefaultAtomixConfig() throws Exception {
        AtomixConfig config = Atomix.config();
        Assert.assertTrue(config.getPartitionGroups().isEmpty());
        Assert.assertTrue(config.getProfiles().isEmpty());
    }

    @Test
    public void testAtomixConfig() throws Exception {
        AtomixConfig config = Atomix.config(getClass().getClassLoader().getResource("test.conf").getPath());
        ClusterConfig cluster = config.getClusterConfig();
        Assert.assertEquals("test", cluster.getClusterId());
        MemberConfig node = cluster.getNodeConfig();
        Assert.assertEquals("one", node.getId().id());
        Assert.assertEquals("localhost:5000", node.getAddress().toString());
        Assert.assertEquals("foo", node.getZoneId());
        Assert.assertEquals("bar", node.getRackId());
        Assert.assertEquals("baz", node.getHostId());
        Assert.assertEquals("bar", node.getProperties().getProperty("foo"));
        Assert.assertEquals("baz", node.getProperties().getProperty("bar"));
        MulticastConfig multicast = cluster.getMulticastConfig();
        Assert.assertTrue(multicast.isEnabled());
        Assert.assertEquals("230.0.1.1", multicast.getGroup().getHostAddress());
        Assert.assertEquals(56789, multicast.getPort());
        HeartbeatMembershipProtocolConfig protocol = ((HeartbeatMembershipProtocolConfig) (cluster.getProtocolConfig()));
        Assert.assertEquals(Duration.ofMillis(200), protocol.getHeartbeatInterval());
        Assert.assertEquals(12, protocol.getPhiFailureThreshold());
        Assert.assertEquals(Duration.ofSeconds(15), protocol.getFailureTimeout());
        MembershipConfig membership = cluster.getMembershipConfig();
        Assert.assertEquals(Duration.ofSeconds(1), membership.getBroadcastInterval());
        Assert.assertEquals(12, membership.getReachabilityThreshold());
        Assert.assertEquals(Duration.ofSeconds(15), membership.getReachabilityTimeout());
        MulticastDiscoveryConfig discovery = ((MulticastDiscoveryConfig) (cluster.getDiscoveryConfig()));
        Assert.assertEquals(TYPE, discovery.getType());
        Assert.assertEquals(Duration.ofSeconds(1), discovery.getBroadcastInterval());
        Assert.assertEquals(12, discovery.getFailureThreshold());
        Assert.assertEquals(Duration.ofSeconds(15), discovery.getFailureTimeout());
        MessagingConfig messaging = cluster.getMessagingConfig();
        Assert.assertEquals(2, messaging.getInterfaces().size());
        Assert.assertEquals("127.0.0.1", messaging.getInterfaces().get(0));
        Assert.assertEquals("0.0.0.0", messaging.getInterfaces().get(1));
        Assert.assertEquals(5000, messaging.getPort().intValue());
        Assert.assertEquals(Duration.ofSeconds(10), messaging.getConnectTimeout());
        Assert.assertTrue(messaging.getTlsConfig().isEnabled());
        Assert.assertEquals("keystore.jks", messaging.getTlsConfig().getKeyStore());
        Assert.assertEquals("foo", messaging.getTlsConfig().getKeyStorePassword());
        Assert.assertEquals("truststore.jks", messaging.getTlsConfig().getTrustStore());
        Assert.assertEquals("bar", messaging.getTlsConfig().getTrustStorePassword());
        RaftPartitionGroupConfig managementGroup = ((RaftPartitionGroupConfig) (config.getManagementGroup()));
        Assert.assertEquals(RaftPartitionGroup.TYPE, managementGroup.getType());
        Assert.assertEquals(1, managementGroup.getPartitions());
        Assert.assertEquals(new MemorySize(((1024 * 1024) * 16)), managementGroup.getStorageConfig().getSegmentSize());
        RaftPartitionGroupConfig groupOne = ((RaftPartitionGroupConfig) (config.getPartitionGroups().get("one")));
        Assert.assertEquals(RaftPartitionGroup.TYPE, groupOne.getType());
        Assert.assertEquals("one", groupOne.getName());
        Assert.assertEquals(7, groupOne.getPartitions());
        PrimaryBackupPartitionGroupConfig groupTwo = ((PrimaryBackupPartitionGroupConfig) (config.getPartitionGroups().get("two")));
        Assert.assertEquals(PrimaryBackupPartitionGroup.TYPE, groupTwo.getType());
        Assert.assertEquals("two", groupTwo.getName());
        Assert.assertEquals(32, groupTwo.getPartitions());
        LogPartitionGroupConfig groupThree = ((LogPartitionGroupConfig) (config.getPartitionGroups().get("three")));
        Assert.assertEquals(LogPartitionGroup.TYPE, groupThree.getType());
        Assert.assertEquals("three", groupThree.getName());
        Assert.assertEquals(3, groupThree.getPartitions());
        ConsensusProfileConfig consensusProfile = ((ConsensusProfileConfig) (config.getProfiles().get(0)));
        Assert.assertEquals(ConsensusProfile.TYPE, consensusProfile.getType());
        Assert.assertEquals("management", consensusProfile.getManagementGroup());
        Assert.assertEquals("consensus", consensusProfile.getDataGroup());
        Assert.assertEquals(3, consensusProfile.getPartitions());
        Assert.assertTrue(consensusProfile.getMembers().containsAll(Arrays.asList("one", "two", "three")));
        DataGridProfileConfig dataGridProfile = ((DataGridProfileConfig) (config.getProfiles().get(1)));
        Assert.assertEquals(DataGridProfile.TYPE, dataGridProfile.getType());
        Assert.assertEquals("management", dataGridProfile.getManagementGroup());
        Assert.assertEquals("data", dataGridProfile.getDataGroup());
        Assert.assertEquals(32, dataGridProfile.getPartitions());
        AtomicMapConfig fooDefaults = config.getPrimitiveDefault("atomic-map");
        Assert.assertEquals("atomic-map", fooDefaults.getType().name());
        Assert.assertEquals("two", getGroup());
        AtomicMapConfig foo = config.getPrimitive("foo");
        Assert.assertEquals("atomic-map", foo.getType().name());
        Assert.assertTrue(foo.isNullValues());
        DistributedSetConfig bar = config.getPrimitive("bar");
        Assert.assertTrue(bar.getCacheConfig().isEnabled());
        MultiPrimaryProtocolConfig multiPrimary = ((MultiPrimaryProtocolConfig) (bar.getProtocolConfig()));
        Assert.assertEquals(MultiPrimaryProtocol.TYPE, multiPrimary.getType());
        Assert.assertEquals(SYNCHRONOUS, multiPrimary.getReplication());
        Assert.assertEquals(Duration.ofSeconds(1), multiPrimary.getRetryDelay());
        AtomicValueConfig baz = config.getPrimitive("baz");
        MultiRaftProtocolConfig multiRaft = ((MultiRaftProtocolConfig) (baz.getProtocolConfig()));
        Assert.assertEquals(SEQUENTIAL, multiRaft.getReadConsistency());
        Assert.assertEquals(RECOVER, multiRaft.getRecoveryStrategy());
        Assert.assertEquals(Duration.ofSeconds(2), multiRaft.getRetryDelay());
        DistributedLogConfig log = config.getPrimitive("log");
        Assert.assertEquals("log", log.getType().name());
        DistributedLogProtocolConfig logConfig = ((DistributedLogProtocolConfig) (log.getProtocolConfig()));
        Assert.assertEquals(DistributedLogProtocol.TYPE, logConfig.getType());
        Assert.assertEquals("three", logConfig.getGroup());
    }
}

