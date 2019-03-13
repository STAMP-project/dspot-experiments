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
package com.github.ambry.router;


import AdaptiveOperationTracker.MIN_DATA_POINTS_REQUIRED;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.MockDataNodeId;
import com.github.ambry.clustermap.MockPartitionId;
import com.github.ambry.clustermap.ReplicaId;
import com.github.ambry.network.Port;
import com.github.ambry.network.PortType;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.Time;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link AdaptiveOperationTracker} that tests the adaptability based on request latencies. This class
 * only tests features not already tested in {@link OperationTrackerTest}.
 *
 * The status of an operation is represented as in the following format:
 *
 * local unsent count-local inflight count-local succeeded count-local failed count;
 * remote unsent count-remote inflight count-remote succeeded count-remote failed count
 *
 * For example: 3-0-0-0; 9-0-0-0
 */
public class AdaptiveOperationTrackerTest {
    private static final int REPLICA_COUNT = 6;

    private static final int PORT = 6666;

    private static final double QUANTILE = 0.9;

    private static final Pair<Long, Long> LOCAL_COLO_LATENCY_RANGE = new Pair(0L, 100L);

    private static final Pair<Long, Long> CROSS_COLO_LATENCY_RANGE = new Pair(120L, 220L);

    private final List<MockDataNodeId> datanodes;

    private final MockPartitionId mockPartition;

    private final String localDcName;

    private final LinkedList<ReplicaId> inflightReplicas = new LinkedList<>();

    private final Set<ReplicaId> repetitionTracker = new HashSet<>();

    private final Time time = new MockTime();

    private final MetricRegistry registry = new MetricRegistry();

    private final Histogram localColoTracker = registry.histogram("LocalColoTracker");

    private final Histogram crossColoTracker = registry.histogram("CrossColoTracker");

    private final Counter pastDueCounter = registry.counter("PastDueCounter");

    /**
     * Constructor that sets up state.
     */
    public AdaptiveOperationTrackerTest() {
        List<Port> portList = Collections.singletonList(new Port(AdaptiveOperationTrackerTest.PORT, PortType.PLAINTEXT));
        List<String> mountPaths = Collections.singletonList("mockMountPath");
        datanodes = new java.util.ArrayList(Arrays.asList(new MockDataNodeId[]{ new MockDataNodeId(portList, mountPaths, "dc-0"), new MockDataNodeId(portList, mountPaths, "dc-1") }));
        localDcName = datanodes.get(0).getDatacenterName();
        mockPartition = new MockPartitionId();
        for (int i = 0; i < (AdaptiveOperationTrackerTest.REPLICA_COUNT); i++) {
            mockPartition.replicaIds.add(new com.github.ambry.clustermap.MockReplicaId(AdaptiveOperationTrackerTest.PORT, mockPartition, datanodes.get((i % (datanodes.size()))), 0));
        }
    }

    /**
     * Tests that requests are discounted from the parallelism count once they move beyond the tolerance quantile.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void adaptationTest() throws InterruptedException {
        primeTracker(localColoTracker, MIN_DATA_POINTS_REQUIRED, AdaptiveOperationTrackerTest.LOCAL_COLO_LATENCY_RANGE);
        primeTracker(crossColoTracker, MIN_DATA_POINTS_REQUIRED, AdaptiveOperationTrackerTest.CROSS_COLO_LATENCY_RANGE);
        double localColoCutoff = localColoTracker.getSnapshot().getValue(AdaptiveOperationTrackerTest.QUANTILE);
        double crossColoCutoff = crossColoTracker.getSnapshot().getValue(AdaptiveOperationTrackerTest.QUANTILE);
        OperationTracker ot = getOperationTracker(true, AdaptiveOperationTrackerTest.REPLICA_COUNT, 2);
        // 3-0-0-0; 3-0-0-0
        sendRequests(ot, 2);
        // 1-2-0-0; 3-0-0-0
        // sleep for less than the cutoff
        time.sleep((((long) (localColoCutoff)) - 2));
        sendRequests(ot, 0);
        // push it over the edge
        time.sleep(5);
        // should send two requests because both of the oldest requests are past their due times
        // the second of the two requests is a cross colo request
        sendRequests(ot, 2);
        // 0-3-0-0; 2-1-0-0
        time.sleep((((long) (localColoCutoff)) + 2));
        // second cross colo request sent (local colo request is past due but the first cross colo request is not past due).
        sendRequests(ot, 1);
        // 0-3-0-0; 1-2-0-0
        long sleepTime = ((long) (localColoCutoff)) + 2;
        time.sleep(sleepTime);
        // no requests should be sent
        sendRequests(ot, 0);
        // 0-3-0-0; 1-2-0-0
        sleepTime = ((long) (crossColoCutoff - localColoCutoff)) + 2;
        time.sleep(sleepTime);
        // third cross colo request sent (first cross colo request is past due)
        sendRequests(ot, 1);
        // 0-3-0-0; 0-3-0-0
        time.sleep((((long) (crossColoCutoff)) + 2));
        // no more replicas left to send requests to
        sendRequests(ot, 0);
        // generate a response for every request and make sure there are no errors
        for (int i = 0; i < (AdaptiveOperationTrackerTest.REPLICA_COUNT); i++) {
            Assert.assertFalse("Operation should not be done", ot.isDone());
            ot.onResponse(inflightReplicas.poll(), true);
        }
        Assert.assertTrue("Operation should have succeeded", ot.hasSucceeded());
        // past due counter should be REPLICA_COUNT - 2
        Assert.assertEquals("Past due counter is inconsistent", ((AdaptiveOperationTrackerTest.REPLICA_COUNT) - 2), pastDueCounter.getCount());
    }

    /**
     * Tests that the {@link Histogram} instances used by {@link AdaptiveOperationTracker} are updated correctly on
     * successful requests.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void trackerUpdateOnSuccessTest() throws InterruptedException {
        doTrackerUpdateTest(true);
    }

    /**
     * Tests that the {@link Histogram} instances used by {@link AdaptiveOperationTracker} are updated correctly on failed
     * requests.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void trackerUpdateOnFailureTest() throws InterruptedException {
        doTrackerUpdateTest(false);
    }

    /**
     * Tests the case where there are no unexpired requests because the only unexpired request returned a failure. In
     * that case, the tracker must allow sending more requests.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void noUnexpiredRequestsTest() throws InterruptedException {
        primeTracker(localColoTracker, MIN_DATA_POINTS_REQUIRED, AdaptiveOperationTrackerTest.LOCAL_COLO_LATENCY_RANGE);
        primeTracker(crossColoTracker, MIN_DATA_POINTS_REQUIRED, AdaptiveOperationTrackerTest.CROSS_COLO_LATENCY_RANGE);
        double localColoCutoff = localColoTracker.getSnapshot().getValue(AdaptiveOperationTrackerTest.QUANTILE);
        OperationTracker ot = getOperationTracker(false, 1, 1);
        // 3-0-0-0
        sendRequests(ot, 1);
        // 2-1-0-0
        // sleep for a time greater than cutoff
        time.sleep((((long) (localColoCutoff)) + 2));
        sendRequests(ot, 1);
        // 1-2-0-0
        // provide a response to the second request that is not a success
        ot.onResponse(inflightReplicas.pollLast(), false);
        // 1-1-0-1
        Assert.assertFalse("Operation should not be done", ot.isDone());
        // should now be able to send one more request
        sendRequests(ot, 1);
        // 0-2-0-1
        ot.onResponse(inflightReplicas.pollLast(), true);
        // 0-1-1-1
        Assert.assertTrue("Operation should have succeeded", ot.hasSucceeded());
        // past due counter should be 1
        Assert.assertEquals("Past due counter is inconsistent", 1, pastDueCounter.getCount());
    }

    /**
     * Tests the case where the tracker is updated b/w the {@link Iterator#hasNext()} and {@link Iterator#next()} calls.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void trackerUpdateBetweenHasNextAndNextTest() throws InterruptedException {
        primeTracker(localColoTracker, MIN_DATA_POINTS_REQUIRED, AdaptiveOperationTrackerTest.LOCAL_COLO_LATENCY_RANGE);
        primeTracker(crossColoTracker, MIN_DATA_POINTS_REQUIRED, AdaptiveOperationTrackerTest.CROSS_COLO_LATENCY_RANGE);
        double localColoCutoff = localColoTracker.getSnapshot().getValue(1);
        OperationTracker ot = new AdaptiveOperationTracker(localDcName, mockPartition, false, null, true, Integer.MAX_VALUE, 1, 1, time, localColoTracker, null, pastDueCounter, 1);
        // 3-0-0-0
        sendRequests(ot, 1);
        // 2-1-0-0
        // sleep for a time greater than cutoff
        time.sleep((((long) (localColoCutoff)) + 2));
        // now get an iterator and call hasNext() on it
        Iterator<ReplicaId> replicaIterator = ot.getReplicaIterator();
        Assert.assertTrue("There should be a replica to send to", replicaIterator.hasNext());
        // now insert a value in the tracker such that it is the max value. However, the return value of hasNext() must
        // not change even though the tracker has changed its return value for getSnapshot().getValue(1).
        long valueToInsert = 2 * ((long) (localColoCutoff));
        localColoTracker.update(valueToInsert);
        Assert.assertEquals("Tracker's snapshot should return the max value", valueToInsert, ((long) (localColoTracker.getSnapshot().getValue(1))));
        // hasNext() should not change it's return value
        Assert.assertTrue("There should be a replica to send to", replicaIterator.hasNext());
        sendRequests(ot, 1);
        // 1-2-0-0
        ot.onResponse(inflightReplicas.pollLast(), true);
        // 1-1-1-0
        Assert.assertTrue("Operation should have succeeded", ot.hasSucceeded());
        // past due counter should be 1
        Assert.assertEquals("Past due counter is inconsistent", 1, pastDueCounter.getCount());
    }
}

