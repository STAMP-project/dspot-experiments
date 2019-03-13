/**
 * Copyright 2018 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.analyzer;


import Broker.State.DEMOTED;
import com.linkedin.kafka.cruisecontrol.analyzer.goals.PreferredLeaderElectionGoal;
import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import com.linkedin.kafka.cruisecontrol.exception.KafkaCruiseControlException;
import com.linkedin.kafka.cruisecontrol.model.ClusterModel;
import com.linkedin.kafka.cruisecontrol.model.Replica;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Test;


public class PreferredLeaderElectionGoalTest {
    private static final TopicPartition T0P0 = new TopicPartition(TestConstants.TOPIC0, 0);

    private static final TopicPartition T0P1 = new TopicPartition(TestConstants.TOPIC0, 1);

    private static final TopicPartition T0P2 = new TopicPartition(TestConstants.TOPIC0, 2);

    private static final TopicPartition T1P0 = new TopicPartition(TestConstants.TOPIC1, 0);

    private static final TopicPartition T1P1 = new TopicPartition(TestConstants.TOPIC1, 1);

    private static final TopicPartition T1P2 = new TopicPartition(TestConstants.TOPIC1, 2);

    private static final TopicPartition T2P0 = new TopicPartition(TestConstants.TOPIC2, 0);

    private static final TopicPartition T2P1 = new TopicPartition(TestConstants.TOPIC2, 1);

    private static final TopicPartition T2P2 = new TopicPartition(TestConstants.TOPIC2, 2);

    private static final int NUM_RACKS = 4;

    @Test
    public void testOptimizeWithoutDemotedBrokers() throws KafkaCruiseControlException {
        ClusterModel clusterModel = createClusterModel(true)._clusterModel;
        PreferredLeaderElectionGoal goal = new PreferredLeaderElectionGoal(false, false, null);
        goal.optimize(clusterModel, Collections.emptySet(), new OptimizationOptions(Collections.emptySet()));
        for (String t : Arrays.asList(TestConstants.TOPIC0, TestConstants.TOPIC1, TestConstants.TOPIC2)) {
            for (int p = 0; p < 3; p++) {
                List<Replica> replicas = clusterModel.partition(new TopicPartition(t, p)).replicas();
                for (int i = 0; i < 3; i++) {
                    // only the first replica should be leader.
                    Assert.assertEquals((i == 0), replicas.get(i).isLeader());
                }
            }
        }
    }

    @Test
    public void testOptimizeWithDemotedBrokers() throws KafkaCruiseControlException {
        ClusterModel clusterModel = createClusterModel(true)._clusterModel;
        clusterModel.setBrokerState(0, DEMOTED);
        Set<TopicPartition> leaderPartitionsOnDemotedBroker = new HashSet<>();
        clusterModel.broker(0).leaderReplicas().forEach(( r) -> leaderPartitionsOnDemotedBroker.add(r.topicPartition()));
        Map<TopicPartition, Integer> leaderDistributionBeforeBrokerDemotion = new HashMap<>();
        clusterModel.brokers().forEach(( b) -> {
            b.leaderReplicas().forEach(( r) -> leaderDistributionBeforeBrokerDemotion.put(r.topicPartition(), b.id()));
        });
        PreferredLeaderElectionGoal goal = new PreferredLeaderElectionGoal(false, false, null);
        goal.optimize(clusterModel, Collections.emptySet(), new OptimizationOptions(Collections.emptySet()));
        for (String t : Arrays.asList(TestConstants.TOPIC0, TestConstants.TOPIC1, TestConstants.TOPIC2)) {
            for (int p = 0; p < 3; p++) {
                TopicPartition tp = new TopicPartition(t, p);
                if (!(leaderPartitionsOnDemotedBroker.contains(tp))) {
                    int oldLeaderBroker = leaderDistributionBeforeBrokerDemotion.get(tp);
                    Assert.assertEquals(("Tp " + tp), oldLeaderBroker, clusterModel.partition(tp).leader().broker().id());
                } else {
                    List<Replica> replicas = clusterModel.partition(tp).replicas();
                    for (int i = 0; i < 3; i++) {
                        Replica replica = replicas.get(i);
                        // only the first replica should be leader.
                        Assert.assertEquals((i == 0), replica.isLeader());
                        if (clusterModel.broker(0).replicas().contains(replica)) {
                            // The demoted replica should be in the last position.
                            Assert.assertEquals(((replicas.size()) - 1), i);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testOptimizeWithDemotedBrokersAndSkipUrpDemotion() throws KafkaCruiseControlException {
        PreferredLeaderElectionGoalTest.ClusterModelAndInfo clusterModelAndInfo = createClusterModel(false);
        ClusterModel clusterModel = clusterModelAndInfo._clusterModel;
        Cluster cluster = clusterModelAndInfo._clusterInfo;
        clusterModel.setBrokerState(1, DEMOTED);
        Map<TopicPartition, List<Integer>> originalReplicaDistribution = clusterModel.getReplicaDistribution();
        PreferredLeaderElectionGoal goal = new PreferredLeaderElectionGoal(true, false, cluster);
        goal.optimize(clusterModel, Collections.emptySet(), new OptimizationOptions(Collections.emptySet()));
        // Operation on under replicated partitions should be skipped.
        for (String t : Arrays.asList(TestConstants.TOPIC1, TestConstants.TOPIC2)) {
            for (int p = 0; p < 3; p++) {
                TopicPartition tp = new TopicPartition(t, p);
                Assert.assertEquals(("Tp " + tp), originalReplicaDistribution.get(tp), clusterModel.getReplicaDistribution().get(tp));
            }
        }
    }

    @Test
    public void testOptimizeWithDemotedBrokersAndExcludeFollowerDemotion() throws KafkaCruiseControlException {
        ClusterModel clusterModel = createClusterModel(true)._clusterModel;
        clusterModel.setBrokerState(2, DEMOTED);
        Map<TopicPartition, Integer> originalLeaderDistribution = clusterModel.getLeaderDistribution();
        Map<TopicPartition, List<Integer>> originalReplicaDistribution = clusterModel.getReplicaDistribution();
        PreferredLeaderElectionGoal goal = new PreferredLeaderElectionGoal(false, true, null);
        goal.optimize(clusterModel, Collections.emptySet(), new OptimizationOptions(Collections.emptySet()));
        Map<TopicPartition, List<Integer>> optimizedReplicaDistribution = clusterModel.getReplicaDistribution();
        for (String t : Arrays.asList(TestConstants.TOPIC0, TestConstants.TOPIC1, TestConstants.TOPIC2)) {
            for (int p = 0; p < 3; p++) {
                TopicPartition tp = new TopicPartition(t, p);
                if (originalReplicaDistribution.get(tp).contains(2)) {
                    if ((originalLeaderDistribution.get(tp)) == 2) {
                        List<Integer> replicas = optimizedReplicaDistribution.get(tp);
                        Assert.assertEquals(("Tp " + tp), 2, replicas.get(((replicas.size()) - 1)).intValue());
                    } else {
                        Assert.assertEquals(("Tp " + tp), originalReplicaDistribution.get(tp), optimizedReplicaDistribution.get(tp));
                    }
                }
            }
        }
    }

    @Test
    public void testOptimizeWithDemotedBrokersAndSkipUrpDemotionAndExcludeFollowerDemotion() throws KafkaCruiseControlException {
        PreferredLeaderElectionGoalTest.ClusterModelAndInfo clusterModelAndInfo = createClusterModel(false);
        ClusterModel clusterModel = clusterModelAndInfo._clusterModel;
        Cluster cluster = clusterModelAndInfo._clusterInfo;
        clusterModel.setBrokerState(0, DEMOTED);
        Map<TopicPartition, Integer> originalLeaderDistribution = clusterModel.getLeaderDistribution();
        PreferredLeaderElectionGoal goal = new PreferredLeaderElectionGoal(true, true, cluster);
        goal.optimize(clusterModel, Collections.emptySet(), new OptimizationOptions(Collections.emptySet()));
        Map<TopicPartition, Integer> optimizedLeaderDistribution = clusterModel.getLeaderDistribution();
        Map<TopicPartition, List<Integer>> optimizedReplicaDistribution = clusterModel.getReplicaDistribution();
        for (String t : Arrays.asList(TestConstants.TOPIC0, TestConstants.TOPIC1, TestConstants.TOPIC2)) {
            for (int p = 0; p < 3; p++) {
                TopicPartition tp = new TopicPartition(t, p);
                if (((originalLeaderDistribution.get(tp)) == 0) && (t.equals(TestConstants.TOPIC0))) {
                    List<Integer> replicas = optimizedReplicaDistribution.get(tp);
                    Assert.assertEquals(("Tp " + tp), 0, replicas.get(((replicas.size()) - 1)).intValue());
                } else {
                    Assert.assertEquals(("Tp " + tp), originalLeaderDistribution.get(tp), optimizedLeaderDistribution.get(tp));
                }
            }
        }
    }

    private static class ClusterModelAndInfo {
        ClusterModel _clusterModel;

        Cluster _clusterInfo;

        ClusterModelAndInfo(ClusterModel clusterModel, Cluster clusterInfo) {
            _clusterInfo = clusterInfo;
            _clusterModel = clusterModel;
        }
    }
}

