/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.executor;


import ExecutorState.State;
import MetadataClient.ClusterAndGeneration;
import com.codahale.metrics.MetricRegistry;
import com.linkedin.kafka.clients.utils.tests.AbstractKafkaIntegrationTestHarness;
import com.linkedin.kafka.cruisecontrol.KafkaCruiseControlUtils;
import com.linkedin.kafka.cruisecontrol.common.MetadataClient;
import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import com.linkedin.kafka.cruisecontrol.config.KafkaCruiseControlConfig;
import com.linkedin.kafka.cruisecontrol.monitor.LoadMonitor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class ExecutorTest extends AbstractKafkaIntegrationTestHarness {
    private static final long ZK_UTILS_CLOSE_TIMEOUT_MS = 10000L;

    private static final int PARTITION = 0;

    private static final TopicPartition TP0 = new TopicPartition(TestConstants.TOPIC0, ExecutorTest.PARTITION);

    private static final TopicPartition TP1 = new TopicPartition(TestConstants.TOPIC1, ExecutorTest.PARTITION);

    private static final TopicPartition TP2 = new TopicPartition(TestConstants.TOPIC2, ExecutorTest.PARTITION);

    private static final TopicPartition TP3 = new TopicPartition(TestConstants.TOPIC3, ExecutorTest.PARTITION);

    @Test
    public void testBasicBalanceMovement() throws InterruptedException {
        ZkUtils zkUtils = KafkaCruiseControlUtils.createZkUtils(zookeeper().getConnectionString());
        Map<String, TopicDescription> topicDescriptions = createTopics();
        int initialLeader0 = topicDescriptions.get(TestConstants.TOPIC0).partitions().get(0).leader().id();
        int initialLeader1 = topicDescriptions.get(TestConstants.TOPIC1).partitions().get(0).leader().id();
        ExecutionProposal proposal0 = new ExecutionProposal(ExecutorTest.TP0, 0, initialLeader0, Collections.singletonList(initialLeader0), Collections.singletonList((initialLeader0 == 0 ? 1 : 0)));
        ExecutionProposal proposal1 = new ExecutionProposal(ExecutorTest.TP1, 0, initialLeader1, Arrays.asList(initialLeader1, (initialLeader1 == 0 ? 1 : 0)), Arrays.asList((initialLeader1 == 0 ? 1 : 0), initialLeader1));
        Collection<ExecutionProposal> proposals = Arrays.asList(proposal0, proposal1);
        try {
            executeAndVerifyProposals(zkUtils, proposals, proposals);
        } finally {
            KafkaCruiseControlUtils.closeZkUtilsWithTimeout(zkUtils, ExecutorTest.ZK_UTILS_CLOSE_TIMEOUT_MS);
        }
    }

    @Test
    public void testMoveNonExistingPartition() throws InterruptedException {
        ZkUtils zkUtils = KafkaCruiseControlUtils.createZkUtils(zookeeper().getConnectionString());
        Map<String, TopicDescription> topicDescriptions = createTopics();
        int initialLeader0 = topicDescriptions.get(TestConstants.TOPIC0).partitions().get(0).leader().id();
        int initialLeader1 = topicDescriptions.get(TestConstants.TOPIC1).partitions().get(0).leader().id();
        ExecutionProposal proposal0 = new ExecutionProposal(ExecutorTest.TP0, 0, initialLeader0, Collections.singletonList(initialLeader0), Collections.singletonList((initialLeader0 == 0 ? 1 : 0)));
        ExecutionProposal proposal1 = new ExecutionProposal(ExecutorTest.TP1, 0, initialLeader1, Arrays.asList(initialLeader1, (initialLeader1 == 0 ? 1 : 0)), Arrays.asList((initialLeader1 == 0 ? 1 : 0), initialLeader1));
        ExecutionProposal proposal2 = new ExecutionProposal(ExecutorTest.TP2, 0, initialLeader0, Collections.singletonList(initialLeader0), Collections.singletonList((initialLeader0 == 0 ? 1 : 0)));
        ExecutionProposal proposal3 = new ExecutionProposal(ExecutorTest.TP3, 0, initialLeader1, Arrays.asList(initialLeader1, (initialLeader1 == 0 ? 1 : 0)), Arrays.asList((initialLeader1 == 0 ? 1 : 0), initialLeader1));
        Collection<ExecutionProposal> proposalsToExecute = Arrays.asList(proposal0, proposal1, proposal2, proposal3);
        Collection<ExecutionProposal> proposalsToCheck = Arrays.asList(proposal0, proposal1);
        try {
            executeAndVerifyProposals(zkUtils, proposalsToExecute, proposalsToCheck);
        } finally {
            KafkaCruiseControlUtils.closeZkUtilsWithTimeout(zkUtils, ExecutorTest.ZK_UTILS_CLOSE_TIMEOUT_MS);
        }
    }

    @Test
    public void testBrokerDiesWhenMovePartitions() throws Exception {
        ZkUtils zkUtils = KafkaCruiseControlUtils.createZkUtils(zookeeper().getConnectionString());
        Map<String, TopicDescription> topicDescriptions = createTopics();
        int initialLeader0 = topicDescriptions.get(TestConstants.TOPIC0).partitions().get(0).leader().id();
        int initialLeader1 = topicDescriptions.get(TestConstants.TOPIC1).partitions().get(0).leader().id();
        _brokers.get((initialLeader0 == 0 ? 1 : 0)).shutdown();
        ExecutionProposal proposal0 = new ExecutionProposal(ExecutorTest.TP0, 0, initialLeader0, Collections.singletonList(initialLeader0), Collections.singletonList((initialLeader0 == 0 ? 1 : 0)));
        ExecutionProposal proposal1 = new ExecutionProposal(ExecutorTest.TP1, 0, initialLeader1, Arrays.asList(initialLeader1, (initialLeader1 == 0 ? 1 : 0)), Arrays.asList((initialLeader1 == 0 ? 1 : 0), initialLeader1));
        Collection<ExecutionProposal> proposalsToExecute = Arrays.asList(proposal0, proposal1);
        try {
            executeAndVerifyProposals(zkUtils, proposalsToExecute, Collections.emptyList());
            // We are not doing the rollback.
            Assert.assertEquals(Collections.singletonList((initialLeader0 == 0 ? 1 : 0)), ExecutorUtils.newAssignmentForPartition(zkUtils, ExecutorTest.TP0));
            Assert.assertEquals(initialLeader0, zkUtils.getLeaderForPartition(TestConstants.TOPIC1, ExecutorTest.PARTITION).get());
        } finally {
            KafkaCruiseControlUtils.closeZkUtilsWithTimeout(zkUtils, ExecutorTest.ZK_UTILS_CLOSE_TIMEOUT_MS);
        }
    }

    @Test
    public void testTimeoutLeaderActions() throws InterruptedException {
        createTopics();
        // The proposal tries to move the leader. We fake the replica list to be unchanged so there is no replica
        // movement, but only leader movement.
        ExecutionProposal proposal = new ExecutionProposal(ExecutorTest.TP1, 0, 1, Arrays.asList(0, 1), Arrays.asList(0, 1));
        KafkaCruiseControlConfig configs = new KafkaCruiseControlConfig(getExecutorProperties());
        Time time = new MockTime();
        MetadataClient mockMetadataClient = EasyMock.createMock(MetadataClient.class);
        // Fake the metadata to never change so the leader movement will timeout.
        Node node0 = new Node(0, "host0", 100);
        Node node1 = new Node(1, "host1", 100);
        Node[] replicas = new Node[2];
        replicas[0] = node0;
        replicas[1] = node1;
        PartitionInfo partitionInfo = new PartitionInfo(ExecutorTest.TP1.topic(), ExecutorTest.TP1.partition(), node1, replicas, replicas);
        Cluster cluster = new Cluster("id", Arrays.asList(node0, node1), Collections.singleton(partitionInfo), Collections.emptySet(), Collections.emptySet());
        MetadataClient.ClusterAndGeneration clusterAndGeneration = new MetadataClient.ClusterAndGeneration(cluster, 0);
        EasyMock.expect(mockMetadataClient.refreshMetadata()).andReturn(clusterAndGeneration).anyTimes();
        EasyMock.expect(mockMetadataClient.cluster()).andReturn(clusterAndGeneration.cluster()).anyTimes();
        EasyMock.replay(mockMetadataClient);
        Collection<ExecutionProposal> proposalsToExecute = Collections.singletonList(proposal);
        Executor executor = new Executor(configs, time, new MetricRegistry(), mockMetadataClient, 86400000L, 43200000L);
        executor.setExecutionMode(false);
        executor.executeProposals(proposalsToExecute, Collections.emptySet(), null, EasyMock.mock(LoadMonitor.class), null, null, null, "random-uuid");
        // Wait until the execution to start so the task timestamp is set to time.milliseconds.
        while ((executor.state().state()) != (State.LEADER_MOVEMENT_TASK_IN_PROGRESS)) {
            Thread.sleep(10);
        } 
        // Sleep over 180000 (the hard coded timeout)
        time.sleep(180001);
        // The execution should finish.
        waitUntilExecutionFinishes(executor);
    }
}

