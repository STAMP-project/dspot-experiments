/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.recipes.leader;


import EventType.DETERMINE_COMPLETE;
import EventType.DETERMINE_START;
import EventType.ELECTED_COMPLETE;
import EventType.ELECTED_START;
import EventType.OFFER_COMPLETE;
import EventType.OFFER_START;
import EventType.START;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.leader.LeaderElectionSupport.EventType;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LeaderElectionSupportTest extends ClientBase {
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionSupportTest.class);

    private static final String testRootNode = ("/" + (System.currentTimeMillis())) + "_";

    private ZooKeeper zooKeeper;

    @Test
    public void testNode() throws IOException, InterruptedException, KeeperException {
        LeaderElectionSupport electionSupport = createLeaderElectionSupport();
        electionSupport.start();
        Thread.sleep(3000);
        electionSupport.stop();
    }

    @Test
    public void testNodes3() throws IOException, InterruptedException, KeeperException {
        int testIterations = 3;
        final CountDownLatch latch = new CountDownLatch(testIterations);
        final AtomicInteger failureCounter = new AtomicInteger();
        for (int i = 0; i < testIterations; i++) {
            runElectionSupportThread(latch, failureCounter);
        }
        Assert.assertEquals(0, failureCounter.get());
        if (!(latch.await(10, TimeUnit.SECONDS))) {
            LeaderElectionSupportTest.logger.info("Waited for all threads to start, but timed out. We had {} failures.", failureCounter);
        }
    }

    @Test
    public void testNodes9() throws IOException, InterruptedException, KeeperException {
        int testIterations = 9;
        final CountDownLatch latch = new CountDownLatch(testIterations);
        final AtomicInteger failureCounter = new AtomicInteger();
        for (int i = 0; i < testIterations; i++) {
            runElectionSupportThread(latch, failureCounter);
        }
        Assert.assertEquals(0, failureCounter.get());
        if (!(latch.await(10, TimeUnit.SECONDS))) {
            LeaderElectionSupportTest.logger.info("Waited for all threads to start, but timed out. We had {} failures.", failureCounter);
        }
    }

    @Test
    public void testNodes20() throws IOException, InterruptedException, KeeperException {
        int testIterations = 20;
        final CountDownLatch latch = new CountDownLatch(testIterations);
        final AtomicInteger failureCounter = new AtomicInteger();
        for (int i = 0; i < testIterations; i++) {
            runElectionSupportThread(latch, failureCounter);
        }
        Assert.assertEquals(0, failureCounter.get());
        if (!(latch.await(10, TimeUnit.SECONDS))) {
            LeaderElectionSupportTest.logger.info("Waited for all threads to start, but timed out. We had {} failures.", failureCounter);
        }
    }

    @Test
    public void testNodes100() throws IOException, InterruptedException, KeeperException {
        int testIterations = 100;
        final CountDownLatch latch = new CountDownLatch(testIterations);
        final AtomicInteger failureCounter = new AtomicInteger();
        for (int i = 0; i < testIterations; i++) {
            runElectionSupportThread(latch, failureCounter);
        }
        Assert.assertEquals(0, failureCounter.get());
        if (!(latch.await(20, TimeUnit.SECONDS))) {
            LeaderElectionSupportTest.logger.info("Waited for all threads to start, but timed out. We had {} failures.", failureCounter);
        }
    }

    @Test
    public void testOfferShuffle() throws InterruptedException {
        int testIterations = 10;
        final CountDownLatch latch = new CountDownLatch(testIterations);
        final AtomicInteger failureCounter = new AtomicInteger();
        List<Thread> threads = new ArrayList<Thread>(testIterations);
        for (int i = 1; i <= testIterations; i++) {
            threads.add(runElectionSupportThread(latch, failureCounter, Math.min((i * 1200), 10000)));
        }
        if (!(latch.await(60, TimeUnit.SECONDS))) {
            LeaderElectionSupportTest.logger.info("Waited for all threads to start, but timed out. We had {} failures.", failureCounter);
        }
    }

    @Test
    public void testGetLeaderHostName() throws InterruptedException, KeeperException {
        LeaderElectionSupport electionSupport = createLeaderElectionSupport();
        electionSupport.start();
        // Sketchy: We assume there will be a leader (probably us) in 3 seconds.
        Thread.sleep(3000);
        String leaderHostName = electionSupport.getLeaderHostName();
        Assert.assertNotNull(leaderHostName);
        Assert.assertEquals("foohost", leaderHostName);
        electionSupport.stop();
    }

    @Test
    public void testReadyOffer() throws Exception {
        final ArrayList<EventType> events = new ArrayList<EventType>();
        final CountDownLatch electedComplete = new CountDownLatch(1);
        final LeaderElectionSupport electionSupport1 = createLeaderElectionSupport();
        electionSupport1.start();
        LeaderElectionSupport electionSupport2 = createLeaderElectionSupport();
        LeaderElectionAware listener = new LeaderElectionAware() {
            boolean stoppedElectedNode = false;

            @Override
            public void onElectionEvent(EventType eventType) {
                events.add(eventType);
                if ((!(stoppedElectedNode)) && (eventType == (EventType.DETERMINE_COMPLETE))) {
                    stoppedElectedNode = true;
                    try {
                        // stopping the ELECTED node, so re-election will happen.
                        electionSupport1.stop();
                    } catch (Exception e) {
                        LeaderElectionSupportTest.logger.error("Unexpected error", e);
                    }
                }
                if (eventType == (EventType.ELECTED_COMPLETE)) {
                    electedComplete.countDown();
                }
            }
        };
        electionSupport2.addListener(listener);
        electionSupport2.start();
        // waiting for re-election.
        electedComplete.await(((CONNECTION_TIMEOUT) / 3), TimeUnit.MILLISECONDS);
        final ArrayList<EventType> expectedevents = new ArrayList<EventType>();
        expectedevents.add(START);
        expectedevents.add(OFFER_START);
        expectedevents.add(OFFER_COMPLETE);
        expectedevents.add(DETERMINE_START);
        expectedevents.add(DETERMINE_COMPLETE);
        expectedevents.add(DETERMINE_START);
        expectedevents.add(DETERMINE_COMPLETE);
        expectedevents.add(ELECTED_START);
        expectedevents.add(ELECTED_COMPLETE);
        Assert.assertEquals("Events has failed to executed in the order", expectedevents, events);
        electionSupport2.stop();
    }
}

