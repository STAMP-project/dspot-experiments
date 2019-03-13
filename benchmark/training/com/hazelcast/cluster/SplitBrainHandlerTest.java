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
package com.hazelcast.cluster;


import GroupProperty.MAX_JOIN_MERGE_TARGET_SECONDS;
import GroupProperty.MAX_JOIN_SECONDS;
import GroupProperty.MAX_NO_HEARTBEAT_SECONDS;
import GroupProperty.MERGE_FIRST_RUN_DELAY_SECONDS;
import GroupProperty.MERGE_NEXT_RUN_DELAY_SECONDS;
import GroupProperty.WAIT_SECONDS_BEFORE_JOIN;
import LifecycleState.MERGED;
import LifecycleState.MERGING;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.instance.FirewallingNodeContext;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.internal.cluster.impl.AdvancedClusterStateTest;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.SplitBrainTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.util.Clock;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class SplitBrainHandlerTest extends HazelcastTestSupport {
    @Test
    public void testMulticast_ClusterMerge() {
        testClusterMerge(false, true);
    }

    @Test
    public void testTcpIp_ClusterMerge() {
        testClusterMerge(false, false);
    }

    @Test
    public void testAdvancedNetworkMulticast_ClusterMerge() {
        testClusterMerge(true, true);
    }

    @Test
    public void testAdvancedNetworkTcpIp_ClusterMerge() {
        testClusterMerge(true, false);
    }

    @Test
    public void testClusterShouldNotMergeDifferentGroupName() {
        Config config1 = new Config();
        config1.setProperty(MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "5");
        config1.setProperty(MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "3");
        String firstGroupName = HazelcastTestSupport.generateRandomString(10);
        config1.getGroupConfig().setName(firstGroupName);
        NetworkConfig networkConfig1 = config1.getNetworkConfig();
        JoinConfig join1 = networkConfig1.getJoin();
        join1.getMulticastConfig().setEnabled(true);
        join1.getTcpIpConfig().addMember("127.0.0.1");
        Config config2 = new Config();
        config2.setProperty(MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "5");
        config2.setProperty(MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "3");
        String secondGroupName = HazelcastTestSupport.generateRandomString(10);
        config2.getGroupConfig().setName(secondGroupName);
        NetworkConfig networkConfig2 = config2.getNetworkConfig();
        JoinConfig join2 = networkConfig2.getJoin();
        join2.getMulticastConfig().setEnabled(true);
        join2.getTcpIpConfig().addMember("127.0.0.1");
        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(config1);
        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(config2);
        SplitBrainHandlerTest.LifecycleCountingListener l = new SplitBrainHandlerTest.LifecycleCountingListener();
        h2.getLifecycleService().addLifecycleListener(l);
        HazelcastTestSupport.assertClusterSize(1, h1);
        HazelcastTestSupport.assertClusterSize(1, h2);
        HazelcastTestSupport.sleepSeconds(10);
        Assert.assertEquals(0, l.getCount(MERGING));
        Assert.assertEquals(0, l.getCount(MERGED));
        HazelcastTestSupport.assertClusterSize(1, h1);
        HazelcastTestSupport.assertClusterSize(1, h2);
    }

    private static class LifecycleCountingListener implements LifecycleListener {
        Map<LifecycleState, AtomicInteger> counter = new ConcurrentHashMap<LifecycleState, AtomicInteger>();

        BlockingQueue<LifecycleState> eventQueue = new LinkedBlockingQueue<LifecycleState>();

        LifecycleCountingListener() {
            for (LifecycleEvent.LifecycleState state : LifecycleEvent.LifecycleState.values()) {
                counter.put(state, new AtomicInteger(0));
            }
        }

        public void stateChanged(LifecycleEvent event) {
            counter.get(event.getState()).incrementAndGet();
            eventQueue.offer(event.getState());
        }

        int getCount(LifecycleEvent.LifecycleState state) {
            return counter.get(state).get();
        }

        boolean waitFor(LifecycleEvent.LifecycleState state, int seconds) {
            long remainingMillis = TimeUnit.SECONDS.toMillis(seconds);
            while (remainingMillis >= 0) {
                LifecycleEvent.LifecycleState received;
                try {
                    long now = Clock.currentTimeMillis();
                    received = eventQueue.poll(remainingMillis, TimeUnit.MILLISECONDS);
                    remainingMillis -= (Clock.currentTimeMillis()) - now;
                } catch (InterruptedException e) {
                    return false;
                }
                if ((received != null) && (received == state)) {
                    return true;
                }
            } 
            return false;
        }
    }

    @Test
    public void testMulticast_MergeAfterSplitBrain() throws InterruptedException {
        testMergeAfterSplitBrain(true);
    }

    @Test
    public void testTcpIp_MergeAfterSplitBrain() throws InterruptedException {
        testMergeAfterSplitBrain(false);
    }

    @Test
    public void test_MergeAfterSplitBrain_withSingleCore() throws InterruptedException {
        RuntimeAvailableProcessors.override(1);
        try {
            testMergeAfterSplitBrain(false);
        } finally {
            RuntimeAvailableProcessors.resetOverride();
        }
    }

    @Test
    public void testTcpIpSplitBrainJoinsCorrectCluster() throws Exception {
        // This port selection ensures that when h3 restarts it will try to join h4 instead of joining the nodes in cluster one
        Config c1 = SplitBrainHandlerTest.buildConfig(false, 15702);
        Config c2 = SplitBrainHandlerTest.buildConfig(false, 15704);
        Config c3 = SplitBrainHandlerTest.buildConfig(false, 15703);
        Config c4 = SplitBrainHandlerTest.buildConfig(false, 15701);
        List<String> clusterOneMembers = Arrays.asList("127.0.0.1:15702", "127.0.0.1:15704");
        List<String> clusterTwoMembers = Arrays.asList("127.0.0.1:15703", "127.0.0.1:15701");
        c1.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(clusterOneMembers);
        c2.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(clusterOneMembers);
        c3.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(clusterTwoMembers);
        c4.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(clusterTwoMembers);
        final CountDownLatch latch = new CountDownLatch(2);
        c3.addListenerConfig(new ListenerConfig(new SplitBrainHandlerTest.MergedEventLifeCycleListener(latch)));
        c4.addListenerConfig(new ListenerConfig(new SplitBrainHandlerTest.MergedEventLifeCycleListener(latch)));
        HazelcastInstance h1 = Hazelcast.newHazelcastInstance(c1);
        HazelcastInstance h2 = Hazelcast.newHazelcastInstance(c2);
        HazelcastInstance h3 = Hazelcast.newHazelcastInstance(c3);
        HazelcastInstance h4 = Hazelcast.newHazelcastInstance(c4);
        // We should have two clusters of two
        HazelcastTestSupport.assertClusterSize(2, h1, h2);
        HazelcastTestSupport.assertClusterSize(2, h3, h4);
        List<String> allMembers = Arrays.asList("127.0.0.1:15701", "127.0.0.1:15704", "127.0.0.1:15703", "127.0.0.1:15702");
        /* This simulates restoring a network connection between h3 and the
        other cluster. But it only make h3 aware of the other cluster so for
        h4 to restart it will have to be notified by h3.
         */
        h3.getConfig().getNetworkConfig().getJoin().getTcpIpConfig().setMembers(allMembers);
        h4.getConfig().getNetworkConfig().getJoin().getTcpIpConfig().clear().setMembers(Collections.<String>emptyList());
        Assert.assertTrue(latch.await(60, TimeUnit.SECONDS));
        // Both nodes from cluster two should have joined cluster one
        HazelcastTestSupport.assertClusterSizeEventually(4, h1, h2, h3, h4);
    }

    @Test
    public void testTcpIpSplitBrainStillWorks_WhenTargetDisappears() throws Exception {
        // The ports are ordered like this so h3 will always attempt to merge with h1
        Config c1 = SplitBrainHandlerTest.buildConfig(false, 25701);
        Config c2 = SplitBrainHandlerTest.buildConfig(false, 25704);
        Config c3 = SplitBrainHandlerTest.buildConfig(false, 25703);
        List<String> clusterOneMembers = Arrays.asList("127.0.0.1:25701");
        List<String> clusterTwoMembers = Arrays.asList("127.0.0.1:25704");
        List<String> clusterThreeMembers = Arrays.asList("127.0.0.1:25703");
        c1.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(clusterOneMembers);
        c2.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(clusterTwoMembers);
        c3.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(clusterThreeMembers);
        final HazelcastInstance h1 = Hazelcast.newHazelcastInstance(c1);
        final HazelcastInstance h2 = Hazelcast.newHazelcastInstance(c2);
        final CountDownLatch latch = new CountDownLatch(1);
        c3.addListenerConfig(new ListenerConfig(new LifecycleListener() {
            public void stateChanged(final LifecycleEvent event) {
                if ((event.getState()) == (LifecycleState.MERGING)) {
                    h1.shutdown();
                } else
                    if ((event.getState()) == (LifecycleState.MERGED)) {
                        latch.countDown();
                    }

            }
        }));
        final HazelcastInstance h3 = Hazelcast.newHazelcastInstance(c3);
        // We should have three clusters of one
        HazelcastTestSupport.assertClusterSize(1, h1);
        HazelcastTestSupport.assertClusterSize(1, h2);
        HazelcastTestSupport.assertClusterSize(1, h3);
        List<String> allMembers = Arrays.asList("127.0.0.1:25701", "127.0.0.1:25704", "127.0.0.1:25703");
        h3.getConfig().getNetworkConfig().getJoin().getTcpIpConfig().setMembers(allMembers);
        Assert.assertTrue(latch.await(60, TimeUnit.SECONDS));
        // Both nodes from cluster two should have joined cluster one
        Assert.assertFalse(h1.getLifecycleService().isRunning());
        HazelcastTestSupport.assertClusterSize(2, h2, h3);
    }

    @Test
    public void testMulticastJoin_DuringSplitBrainHandlerRunning() throws InterruptedException {
        String groupName = HazelcastTestSupport.generateRandomString(10);
        final CountDownLatch latch = new CountDownLatch(1);
        ListenerConfig mergeListenerConfig = new ListenerConfig(new LifecycleListener() {
            public void stateChanged(final LifecycleEvent event) {
                switch (event.getState()) {
                    case MERGING :
                    case MERGED :
                        latch.countDown();
                        break;
                    default :
                        break;
                }
            }
        });
        Config config1 = new Config();
        // bigger port to make sure address.hashCode() check pass during merge!
        config1.getNetworkConfig().setPort(5901);
        config1.getGroupConfig().setName(groupName);
        config1.setProperty(WAIT_SECONDS_BEFORE_JOIN.getName(), "5");
        config1.setProperty(MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "0");
        config1.setProperty(MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "0");
        config1.addListenerConfig(mergeListenerConfig);
        HazelcastInstance hz1 = Hazelcast.newHazelcastInstance(config1);
        HazelcastTestSupport.sleepSeconds(1);
        Config config2 = new Config();
        config2.getGroupConfig().setName(groupName);
        config2.getNetworkConfig().setPort(5701);
        config2.setProperty(WAIT_SECONDS_BEFORE_JOIN.getName(), "5");
        config2.setProperty(MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "0");
        config2.setProperty(MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "0");
        config2.addListenerConfig(mergeListenerConfig);
        HazelcastInstance hz2 = Hazelcast.newHazelcastInstance(config2);
        HazelcastTestSupport.assertClusterSizeEventually(2, hz1, hz2);
        Assert.assertFalse("Latch should not be countdown!", latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testMulticast_ClusterMerge_when_split_not_detected_by_master() {
        testClusterMerge_when_split_not_detected_by_master(true);
    }

    // https://github.com/hazelcast/hazelcast/issues/8137
    @Test
    public void testTcpIp_ClusterMerge_when_split_not_detected_by_master() {
        testClusterMerge_when_split_not_detected_by_master(false);
    }

    @Test
    public void testClusterMerge_ignoresLiteMembers() {
        String groupName = HazelcastTestSupport.generateRandomString(10);
        HazelcastInstance lite1 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, true), "lite1", new FirewallingNodeContext());
        HazelcastInstance lite2 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, true), "lite2", new FirewallingNodeContext());
        HazelcastInstance data1 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "data1", new FirewallingNodeContext());
        HazelcastInstance data2 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "data2", new FirewallingNodeContext());
        HazelcastInstance data3 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "data3", new FirewallingNodeContext());
        HazelcastTestSupport.assertClusterSize(5, lite1, data3);
        HazelcastTestSupport.assertClusterSizeEventually(5, lite2, data1, data2);
        final CountDownLatch mergeLatch = new CountDownLatch(3);
        lite1.getLifecycleService().addLifecycleListener(new SplitBrainHandlerTest.MergedEventLifeCycleListener(mergeLatch));
        lite2.getLifecycleService().addLifecycleListener(new SplitBrainHandlerTest.MergedEventLifeCycleListener(mergeLatch));
        data1.getLifecycleService().addLifecycleListener(new SplitBrainHandlerTest.MergedEventLifeCycleListener(mergeLatch));
        SplitBrainTestSupport.blockCommunicationBetween(lite1, data2);
        SplitBrainTestSupport.blockCommunicationBetween(lite2, data2);
        SplitBrainTestSupport.blockCommunicationBetween(data1, data2);
        SplitBrainTestSupport.blockCommunicationBetween(lite1, data3);
        SplitBrainTestSupport.blockCommunicationBetween(lite2, data3);
        SplitBrainTestSupport.blockCommunicationBetween(data1, data3);
        HazelcastTestSupport.closeConnectionBetween(data2, data1);
        HazelcastTestSupport.closeConnectionBetween(data2, lite2);
        HazelcastTestSupport.closeConnectionBetween(data2, lite1);
        HazelcastTestSupport.closeConnectionBetween(data3, data1);
        HazelcastTestSupport.closeConnectionBetween(data3, lite2);
        HazelcastTestSupport.closeConnectionBetween(data3, lite1);
        HazelcastTestSupport.assertClusterSizeEventually(3, lite1, lite2, data1);
        HazelcastTestSupport.assertClusterSizeEventually(2, data2, data3);
        HazelcastTestSupport.waitAllForSafeState(lite1, lite2, data1);
        HazelcastTestSupport.waitAllForSafeState(data2, data3);
        data1.getMap("default").put(1, "cluster1");
        data3.getMap("default").put(1, "cluster2");
        SplitBrainTestSupport.unblockCommunicationBetween(lite1, data2);
        SplitBrainTestSupport.unblockCommunicationBetween(lite2, data2);
        SplitBrainTestSupport.unblockCommunicationBetween(data1, data2);
        SplitBrainTestSupport.unblockCommunicationBetween(lite1, data3);
        SplitBrainTestSupport.unblockCommunicationBetween(lite2, data3);
        SplitBrainTestSupport.unblockCommunicationBetween(data1, data3);
        HazelcastTestSupport.assertOpenEventually(mergeLatch);
        HazelcastTestSupport.assertClusterSizeEventually(5, lite1, lite2, data1, data2, data3);
        HazelcastTestSupport.waitAllForSafeState(lite1, lite2, data1, data2, data3);
        Assert.assertEquals("cluster1", lite1.getMap("default").get(1));
    }

    @Test
    public void testClustersShouldNotMergeWhenBiggerClusterIsNotActive() {
        String groupName = HazelcastTestSupport.generateRandomString(10);
        final HazelcastInstance hz1 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "hz1", new FirewallingNodeContext());
        final HazelcastInstance hz2 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "hz2", new FirewallingNodeContext());
        final HazelcastInstance hz3 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "hz3", new FirewallingNodeContext());
        HazelcastTestSupport.assertClusterSize(3, hz1, hz3);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        final CountDownLatch splitLatch = new CountDownLatch(2);
        hz3.getCluster().addMembershipListener(new SplitBrainHandlerTest.MemberRemovedMembershipListener(splitLatch));
        SplitBrainTestSupport.blockCommunicationBetween(hz1, hz3);
        SplitBrainTestSupport.blockCommunicationBetween(hz2, hz3);
        HazelcastTestSupport.suspectMember(hz3, hz2);
        HazelcastTestSupport.closeConnectionBetween(hz3, hz1);
        HazelcastTestSupport.assertOpenEventually(splitLatch, 10);
        HazelcastTestSupport.assertClusterSizeEventually(2, hz1, hz2);
        HazelcastTestSupport.assertClusterSize(1, hz3);
        AdvancedClusterStateTest.changeClusterStateEventually(hz1, ClusterState.FROZEN);
        SplitBrainTestSupport.unblockCommunicationBetween(hz1, hz3);
        SplitBrainTestSupport.unblockCommunicationBetween(hz2, hz3);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                HazelcastTestSupport.assertClusterSize(2, hz1, hz2);
                HazelcastTestSupport.assertClusterSize(1, hz3);
            }
        }, 10);
    }

    @Test
    public void testClustersShouldNotMergeWhenSmallerClusterIsNotActive() {
        String groupName = HazelcastTestSupport.generateRandomString(10);
        final HazelcastInstance hz1 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "hz1", new FirewallingNodeContext());
        final HazelcastInstance hz2 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "hz2", new FirewallingNodeContext());
        final HazelcastInstance hz3 = HazelcastInstanceFactory.newHazelcastInstance(buildConfig(groupName, false), "hz3", new FirewallingNodeContext());
        HazelcastTestSupport.assertClusterSize(3, hz1, hz3);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        final CountDownLatch splitLatch = new CountDownLatch(2);
        hz3.getCluster().addMembershipListener(new SplitBrainHandlerTest.MemberRemovedMembershipListener(splitLatch));
        SplitBrainTestSupport.blockCommunicationBetween(hz1, hz3);
        SplitBrainTestSupport.blockCommunicationBetween(hz2, hz3);
        HazelcastTestSupport.suspectMember(hz3, hz2);
        HazelcastTestSupport.closeConnectionBetween(hz3, hz1);
        HazelcastTestSupport.assertOpenEventually(splitLatch, 10);
        HazelcastTestSupport.assertClusterSizeEventually(2, hz1, hz2);
        HazelcastTestSupport.assertClusterSize(1, hz3);
        AdvancedClusterStateTest.changeClusterStateEventually(hz3, ClusterState.FROZEN);
        SplitBrainTestSupport.unblockCommunicationBetween(hz1, hz3);
        SplitBrainTestSupport.unblockCommunicationBetween(hz2, hz3);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                HazelcastTestSupport.assertClusterSize(2, hz1, hz2);
                HazelcastTestSupport.assertClusterSize(1, hz3);
            }
        }, 10);
    }

    // https://github.com/hazelcast/hazelcast/issues/8137
    @Test
    public void testClusterMerge_when_split_not_detected_by_slave() {
        Config config = new Config();
        String groupName = HazelcastTestSupport.generateRandomString(10);
        config.getGroupConfig().setName(groupName);
        config.setProperty(MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "10");
        config.setProperty(MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "10");
        config.setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "15");
        config.setProperty(MAX_JOIN_SECONDS.getName(), "10");
        config.setProperty(MAX_JOIN_MERGE_TARGET_SECONDS.getName(), "10");
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");
        HazelcastInstance hz1 = HazelcastInstanceFactory.newHazelcastInstance(config, "test-node1", new FirewallingNodeContext());
        HazelcastInstance hz2 = HazelcastInstanceFactory.newHazelcastInstance(config, "test-node2", new FirewallingNodeContext());
        final HazelcastInstance hz3 = HazelcastInstanceFactory.newHazelcastInstance(config, "test-node3", new FirewallingNodeContext());
        HazelcastTestSupport.assertClusterSize(3, hz1, hz3);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        final CountDownLatch splitLatch = new CountDownLatch(2);
        MembershipAdapter membershipAdapter = new MembershipAdapter() {
            @Override
            public void memberRemoved(MembershipEvent event) {
                if (HazelcastTestSupport.getNode(hz3).getLocalMember().equals(event.getMember())) {
                    splitLatch.countDown();
                }
            }
        };
        hz1.getCluster().addMembershipListener(membershipAdapter);
        hz2.getCluster().addMembershipListener(membershipAdapter);
        final CountDownLatch mergeLatch = new CountDownLatch(1);
        hz3.getLifecycleService().addLifecycleListener(new SplitBrainHandlerTest.MergedEventLifeCycleListener(mergeLatch));
        SplitBrainTestSupport.blockCommunicationBetween(hz3, hz1);
        SplitBrainTestSupport.blockCommunicationBetween(hz3, hz2);
        HazelcastTestSupport.suspectMember(hz1, hz3);
        HazelcastTestSupport.suspectMember(hz2, hz3);
        HazelcastTestSupport.assertOpenEventually(splitLatch);
        HazelcastTestSupport.assertClusterSize(2, hz1, hz2);
        HazelcastTestSupport.assertClusterSize(3, hz3);
        SplitBrainTestSupport.unblockCommunicationBetween(hz1, hz3);
        SplitBrainTestSupport.unblockCommunicationBetween(hz2, hz3);
        HazelcastTestSupport.assertOpenEventually(mergeLatch);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz1, hz2, hz3);
        HazelcastTestSupport.assertMasterAddress(HazelcastTestSupport.getAddress(hz1), hz1, hz2, hz3);
    }

    // https://github.com/hazelcast/hazelcast/issues/8137
    @Test
    public void testClusterMerge_when_split_not_detected_by_slave_and_restart_during_merge() {
        Config config = new Config();
        String groupName = HazelcastTestSupport.generateRandomString(10);
        config.getGroupConfig().setName(groupName);
        config.setProperty(MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "10");
        config.setProperty(MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "10");
        config.setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "15");
        config.setProperty(MAX_JOIN_SECONDS.getName(), "40");
        config.setProperty(MAX_JOIN_MERGE_TARGET_SECONDS.getName(), "10");
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(true).addMember("127.0.0.1:5701").addMember("127.0.0.1:5702").addMember("127.0.0.1:5703");
        networkConfig.setPort(5702);
        HazelcastInstance hz2 = HazelcastInstanceFactory.newHazelcastInstance(config, "test-node2", new FirewallingNodeContext());
        networkConfig.setPort(5703);
        HazelcastInstance hz3 = HazelcastInstanceFactory.newHazelcastInstance(config, "test-node3", new FirewallingNodeContext());
        networkConfig.setPort(5701);
        final HazelcastInstance hz1 = HazelcastInstanceFactory.newHazelcastInstance(config, "test-node1", new FirewallingNodeContext());
        HazelcastTestSupport.assertClusterSize(3, hz2, hz1);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz3);
        final CountDownLatch splitLatch = new CountDownLatch(2);
        MembershipAdapter membershipAdapter = new MembershipAdapter() {
            @Override
            public void memberRemoved(MembershipEvent event) {
                if (HazelcastTestSupport.getNode(hz1).getLocalMember().equals(event.getMember())) {
                    splitLatch.countDown();
                }
            }
        };
        hz2.getCluster().addMembershipListener(membershipAdapter);
        hz3.getCluster().addMembershipListener(membershipAdapter);
        final CountDownLatch mergingLatch = new CountDownLatch(1);
        final CountDownLatch mergeLatch = new CountDownLatch(1);
        LifecycleListener lifecycleListener = new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((event.getState()) == (LifecycleState.MERGING)) {
                    mergingLatch.countDown();
                }
                if ((event.getState()) == (LifecycleState.MERGED)) {
                    mergeLatch.countDown();
                }
            }
        };
        hz1.getLifecycleService().addLifecycleListener(lifecycleListener);
        SplitBrainTestSupport.blockCommunicationBetween(hz1, hz2);
        SplitBrainTestSupport.blockCommunicationBetween(hz1, hz3);
        HazelcastTestSupport.suspectMember(hz2, hz1);
        HazelcastTestSupport.suspectMember(hz3, hz1);
        HazelcastTestSupport.assertOpenEventually(splitLatch, 20);
        HazelcastTestSupport.assertClusterSize(2, hz2, hz3);
        HazelcastTestSupport.assertClusterSize(3, hz1);
        SplitBrainTestSupport.unblockCommunicationBetween(hz1, hz2);
        SplitBrainTestSupport.unblockCommunicationBetween(hz1, hz3);
        HazelcastTestSupport.assertOpenEventually(mergingLatch, 60);
        hz2.getLifecycleService().terminate();
        hz2 = HazelcastInstanceFactory.newHazelcastInstance(config, "test-node2", new FirewallingNodeContext());
        HazelcastTestSupport.assertOpenEventually(mergeLatch);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz1, hz2, hz3);
        HazelcastTestSupport.assertMasterAddress(HazelcastTestSupport.getAddress(hz3), hz1, hz2, hz3);
    }

    public static class MergedEventLifeCycleListener implements LifecycleListener {
        private final CountDownLatch mergeLatch;

        MergedEventLifeCycleListener(CountDownLatch mergeLatch) {
            this.mergeLatch = mergeLatch;
        }

        public void stateChanged(LifecycleEvent event) {
            if ((event.getState()) == (LifecycleState.MERGED)) {
                mergeLatch.countDown();
            }
        }
    }

    private static class MemberRemovedMembershipListener implements MembershipListener {
        private final CountDownLatch splitLatch;

        MemberRemovedMembershipListener(CountDownLatch splitLatch) {
            this.splitLatch = splitLatch;
        }

        @Override
        public void memberAdded(MembershipEvent membershipEvent) {
        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {
            splitLatch.countDown();
        }

        @Override
        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        }
    }
}

