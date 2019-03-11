/**
 * Copyright 2018 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.model;


import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import java.util.NavigableSet;
import java.util.Random;
import java.util.function.Function;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link SortedReplicas}
 */
public class SortedReplicasTest {
    private static final String SORT_NAME = "sortName";

    private static final Random RANDOM = new Random(-559038737);

    private static final Function<Replica, Boolean> SELECTION_FUNC = Replica::isLeader;

    private static final Function<Replica, Integer> PRIORITY_FUNC = ( r) -> (r.topicPartition().partition()) % 5;

    private static final Function<Replica, Double> SCORE_FUNC = ( r) -> SortedReplicasTest.RANDOM.nextDouble();

    private static final int NUM_REPLICAS = 100;

    @Test
    public void testAddAndRemove() {
        Broker broker = generateBroker(SortedReplicasTest.NUM_REPLICAS);
        broker.trackSortedReplicas(SortedReplicasTest.SORT_NAME, SortedReplicasTest.SELECTION_FUNC, SortedReplicasTest.PRIORITY_FUNC, SortedReplicasTest.SCORE_FUNC);
        SortedReplicas sr = broker.trackedSortedReplicas(SortedReplicasTest.SORT_NAME);
        int numReplicas = sr.sortedReplicas().size();
        Replica replica1 = new Replica(new TopicPartition(TestConstants.TOPIC0, 105), broker, false);
        sr.add(replica1);
        Assert.assertEquals("The selection function should have filtered out the replica", numReplicas, sr.sortedReplicas().size());
        Replica replica2 = new Replica(new TopicPartition(TestConstants.TOPIC0, 103), broker, true);
        sr.add(replica2);
        Assert.assertEquals("The replica should have been added.", (numReplicas + 1), sr.sortedReplicas().size());
        verifySortedReplicas(sr);
        // Removing a none existing replica should not throw exception.
        sr.remove(replica1);
        Assert.assertEquals((numReplicas + 1), sr.sortedReplicas().size());
        verifySortedReplicas(sr);
        // Remove an existing replica
        sr.remove(replica2);
        Assert.assertEquals(numReplicas, sr.sortedReplicas().size());
        verifySortedReplicas(sr);
    }

    @Test
    public void testLazyInitialization() {
        Broker broker = generateBroker(SortedReplicasTest.NUM_REPLICAS);
        broker.trackSortedReplicas(SortedReplicasTest.SORT_NAME, null, null, SortedReplicasTest.SCORE_FUNC);
        SortedReplicas sr = broker.trackedSortedReplicas(SortedReplicasTest.SORT_NAME);
        Assert.assertEquals("The replicas should be sorted lazily", 0, sr.numReplicas());
        Replica replica = new Replica(new TopicPartition(TestConstants.TOPIC0, 105), broker, false);
        sr.add(replica);
        Assert.assertEquals("The replicas should be sorted lazily", 0, sr.numReplicas());
        sr.remove(replica);
        Assert.assertEquals("The replicas should be sorted lazily", 0, sr.numReplicas());
        NavigableSet<ReplicaWrapper> sortedReplicas = sr.sortedReplicas();
        Assert.assertEquals("There should be ", SortedReplicasTest.NUM_REPLICAS, sortedReplicas.size());
        Assert.assertEquals("The replicas should now be sorted", SortedReplicasTest.NUM_REPLICAS, sr.numReplicas());
    }

    @Test
    public void testScoreFunctionOnly() {
        Broker broker = generateBroker(SortedReplicasTest.NUM_REPLICAS);
        broker.trackSortedReplicas(SortedReplicasTest.SORT_NAME, null, null, SortedReplicasTest.SCORE_FUNC);
        SortedReplicas sr = broker.trackedSortedReplicas(SortedReplicasTest.SORT_NAME);
        double lastScore = Double.NEGATIVE_INFINITY;
        for (ReplicaWrapper rw : sr.sortedReplicas()) {
            Assert.assertTrue(((rw.score()) >= lastScore));
        }
    }

    @Test
    public void testPriorityFunction() {
        Broker broker = generateBroker(SortedReplicasTest.NUM_REPLICAS);
        broker.trackSortedReplicas(SortedReplicasTest.SORT_NAME, null, SortedReplicasTest.PRIORITY_FUNC, SortedReplicasTest.SCORE_FUNC);
        SortedReplicas sr = broker.trackedSortedReplicas(SortedReplicasTest.SORT_NAME);
        Assert.assertEquals(SortedReplicasTest.NUM_REPLICAS, sr.sortedReplicas().size());
        verifySortedReplicas(sr);
    }

    @Test
    public void testSelectionFunction() {
        Broker broker = generateBroker(SortedReplicasTest.NUM_REPLICAS);
        broker.trackSortedReplicas(SortedReplicasTest.SORT_NAME, SortedReplicasTest.SELECTION_FUNC, SortedReplicasTest.PRIORITY_FUNC, SortedReplicasTest.SCORE_FUNC);
        SortedReplicas sr = broker.trackedSortedReplicas(SortedReplicasTest.SORT_NAME);
        Assert.assertEquals(broker.leaderReplicas().size(), sr.sortedReplicas().size());
        verifySortedReplicas(sr);
    }
}

