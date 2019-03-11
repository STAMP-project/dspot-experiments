/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.leaderelection;


import HighAvailabilityOptions.HA_ZOOKEEPER_LEADER_PATH;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.test.TestingServer;
import org.apache.flink.api.common.time.Deadline;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.leaderretrieval.ZooKeeperLeaderRetrievalService;
import org.apache.flink.runtime.util.ZooKeeperUtils;
import org.apache.flink.util.TestLogger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for the {@link ZooKeeperLeaderElectionService} and the {@link ZooKeeperLeaderRetrievalService}.
 */
public class ZooKeeperLeaderElectionTest extends TestLogger {
    private TestingServer testingServer;

    private Configuration configuration;

    private CuratorFramework client;

    private static final String TEST_URL = "akka//user/jobmanager";

    private static final long timeout = 200L * 1000L;

    private static Logger LOG = LoggerFactory.getLogger(ZooKeeperLeaderElectionTest.class);

    /**
     * Tests that the ZooKeeperLeaderElection/RetrievalService return both the correct URL.
     */
    @Test
    public void testZooKeeperLeaderElectionRetrieval() throws Exception {
        ZooKeeperLeaderElectionService leaderElectionService = null;
        ZooKeeperLeaderRetrievalService leaderRetrievalService = null;
        try {
            leaderElectionService = ZooKeeperUtils.createLeaderElectionService(client, configuration);
            leaderRetrievalService = ZooKeeperUtils.createLeaderRetrievalService(client, configuration);
            TestingContender contender = new TestingContender(ZooKeeperLeaderElectionTest.TEST_URL, leaderElectionService);
            TestingListener listener = new TestingListener();
            leaderElectionService.start(contender);
            leaderRetrievalService.start(listener);
            contender.waitForLeader(ZooKeeperLeaderElectionTest.timeout);
            Assert.assertTrue(contender.isLeader());
            Assert.assertEquals(leaderElectionService.getLeaderSessionID(), contender.getLeaderSessionID());
            listener.waitForNewLeader(ZooKeeperLeaderElectionTest.timeout);
            Assert.assertEquals(ZooKeeperLeaderElectionTest.TEST_URL, listener.getAddress());
            Assert.assertEquals(leaderElectionService.getLeaderSessionID(), listener.getLeaderSessionID());
        } finally {
            if (leaderElectionService != null) {
                leaderElectionService.stop();
            }
            if (leaderRetrievalService != null) {
                leaderRetrievalService.stop();
            }
        }
    }

    /**
     * Tests repeatedly the reelection of still available LeaderContender. After a contender has
     * been elected as the leader, it is removed. This forces the ZooKeeperLeaderElectionService
     * to elect a new leader.
     */
    @Test
    public void testZooKeeperReelection() throws Exception {
        Deadline deadline = Deadline.fromNow(Duration.ofMinutes(5L));
        int num = 10;
        ZooKeeperLeaderElectionService[] leaderElectionService = new ZooKeeperLeaderElectionService[num];
        TestingContender[] contenders = new TestingContender[num];
        ZooKeeperLeaderRetrievalService leaderRetrievalService = null;
        TestingListener listener = new TestingListener();
        try {
            leaderRetrievalService = ZooKeeperUtils.createLeaderRetrievalService(client, configuration);
            ZooKeeperLeaderElectionTest.LOG.debug("Start leader retrieval service for the TestingListener.");
            leaderRetrievalService.start(listener);
            for (int i = 0; i < num; i++) {
                leaderElectionService[i] = ZooKeeperUtils.createLeaderElectionService(client, configuration);
                contenders[i] = new TestingContender((((ZooKeeperLeaderElectionTest.TEST_URL) + "_") + i), leaderElectionService[i]);
                ZooKeeperLeaderElectionTest.LOG.debug("Start leader election service for contender #{}.", i);
                leaderElectionService[i].start(contenders[i]);
            }
            String pattern = ((ZooKeeperLeaderElectionTest.TEST_URL) + "_") + "(\\d+)";
            Pattern regex = Pattern.compile(pattern);
            int numberSeenLeaders = 0;
            while ((deadline.hasTimeLeft()) && (numberSeenLeaders < num)) {
                ZooKeeperLeaderElectionTest.LOG.debug("Wait for new leader #{}.", numberSeenLeaders);
                String address = listener.waitForNewLeader(deadline.timeLeft().toMillis());
                Matcher m = regex.matcher(address);
                if (m.find()) {
                    int index = Integer.parseInt(m.group(1));
                    TestingContender contender = contenders[index];
                    // check that the retrieval service has retrieved the correct leader
                    if ((address.equals(contender.getAddress())) && (listener.getLeaderSessionID().equals(contender.getLeaderSessionID()))) {
                        // kill the election service of the leader
                        ZooKeeperLeaderElectionTest.LOG.debug("Stop leader election service of contender #{}.", numberSeenLeaders);
                        leaderElectionService[index].stop();
                        leaderElectionService[index] = null;
                        numberSeenLeaders++;
                    }
                } else {
                    Assert.fail("Did not find the leader's index.");
                }
            } 
            Assert.assertFalse("Did not complete the leader reelection in time.", deadline.isOverdue());
            Assert.assertEquals(num, numberSeenLeaders);
        } finally {
            if (leaderRetrievalService != null) {
                leaderRetrievalService.stop();
            }
            for (ZooKeeperLeaderElectionService electionService : leaderElectionService) {
                if (electionService != null) {
                    electionService.stop();
                }
            }
        }
    }

    /**
     * Tests the repeated reelection of {@link LeaderContender} once the current leader dies.
     * Furthermore, it tests that new LeaderElectionServices can be started later on and that they
     * successfully register at ZooKeeper and take part in the leader election.
     */
    @Test
    public void testZooKeeperReelectionWithReplacement() throws Exception {
        int num = 3;
        int numTries = 30;
        ZooKeeperLeaderElectionService[] leaderElectionService = new ZooKeeperLeaderElectionService[num];
        TestingContender[] contenders = new TestingContender[num];
        ZooKeeperLeaderRetrievalService leaderRetrievalService = null;
        TestingListener listener = new TestingListener();
        try {
            leaderRetrievalService = ZooKeeperUtils.createLeaderRetrievalService(client, configuration);
            leaderRetrievalService.start(listener);
            for (int i = 0; i < num; i++) {
                leaderElectionService[i] = ZooKeeperUtils.createLeaderElectionService(client, configuration);
                contenders[i] = new TestingContender(((((ZooKeeperLeaderElectionTest.TEST_URL) + "_") + i) + "_0"), leaderElectionService[i]);
                leaderElectionService[i].start(contenders[i]);
            }
            String pattern = ((((ZooKeeperLeaderElectionTest.TEST_URL) + "_") + "(\\d+)") + "_") + "(\\d+)";
            Pattern regex = Pattern.compile(pattern);
            for (int i = 0; i < numTries; i++) {
                listener.waitForNewLeader(ZooKeeperLeaderElectionTest.timeout);
                String address = listener.getAddress();
                Matcher m = regex.matcher(address);
                if (m.find()) {
                    int index = Integer.parseInt(m.group(1));
                    int lastTry = Integer.parseInt(m.group(2));
                    Assert.assertEquals(listener.getLeaderSessionID(), contenders[index].getLeaderSessionID());
                    // stop leader election service = revoke leadership
                    leaderElectionService[index].stop();
                    // create new leader election service which takes part in the leader election
                    leaderElectionService[index] = ZooKeeperUtils.createLeaderElectionService(client, configuration);
                    contenders[index] = new TestingContender((((((ZooKeeperLeaderElectionTest.TEST_URL) + "_") + index) + "_") + (lastTry + 1)), leaderElectionService[index]);
                    leaderElectionService[index].start(contenders[index]);
                } else {
                    throw new Exception("Did not find the leader's index.");
                }
            }
        } finally {
            if (leaderRetrievalService != null) {
                leaderRetrievalService.stop();
            }
            for (ZooKeeperLeaderElectionService electionService : leaderElectionService) {
                if (electionService != null) {
                    electionService.stop();
                }
            }
        }
    }

    /**
     * Tests that the current leader is notified when his leader connection information in ZooKeeper
     * are overwritten. The leader must re-establish the correct leader connection information in
     * ZooKeeper.
     */
    @Test
    public void testMultipleLeaders() throws Exception {
        final String FAULTY_CONTENDER_URL = "faultyContender";
        final String leaderPath = "/leader";
        configuration.setString(HA_ZOOKEEPER_LEADER_PATH, leaderPath);
        ZooKeeperLeaderElectionService leaderElectionService = null;
        ZooKeeperLeaderRetrievalService leaderRetrievalService = null;
        ZooKeeperLeaderRetrievalService leaderRetrievalService2 = null;
        TestingListener listener = new TestingListener();
        TestingListener listener2 = new TestingListener();
        TestingContender contender;
        try {
            leaderElectionService = ZooKeeperUtils.createLeaderElectionService(client, configuration);
            leaderRetrievalService = ZooKeeperUtils.createLeaderRetrievalService(client, configuration);
            leaderRetrievalService2 = ZooKeeperUtils.createLeaderRetrievalService(client, configuration);
            contender = new TestingContender(ZooKeeperLeaderElectionTest.TEST_URL, leaderElectionService);
            leaderElectionService.start(contender);
            leaderRetrievalService.start(listener);
            listener.waitForNewLeader(ZooKeeperLeaderElectionTest.timeout);
            Assert.assertEquals(listener.getLeaderSessionID(), contender.getLeaderSessionID());
            Assert.assertEquals(ZooKeeperLeaderElectionTest.TEST_URL, listener.getAddress());
            CuratorFramework client = ZooKeeperUtils.startCuratorFramework(configuration);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeUTF(FAULTY_CONTENDER_URL);
            oos.writeObject(null);
            oos.close();
            // overwrite the current leader address, the leader should notice that and correct it
            boolean dataWritten = false;
            while (!dataWritten) {
                client.delete().forPath(leaderPath);
                try {
                    client.create().forPath(leaderPath, baos.toByteArray());
                    dataWritten = true;
                } catch (KeeperException e) {
                    // this can happen if the leader election service was faster
                }
            } 
            leaderRetrievalService2.start(listener2);
            listener2.waitForNewLeader(ZooKeeperLeaderElectionTest.timeout);
            if (FAULTY_CONTENDER_URL.equals(listener2.getAddress())) {
                listener2.waitForNewLeader(ZooKeeperLeaderElectionTest.timeout);
            }
            Assert.assertEquals(listener2.getLeaderSessionID(), contender.getLeaderSessionID());
            Assert.assertEquals(listener2.getAddress(), contender.getAddress());
        } finally {
            if (leaderElectionService != null) {
                leaderElectionService.stop();
            }
            if (leaderRetrievalService != null) {
                leaderRetrievalService.stop();
            }
            if (leaderRetrievalService2 != null) {
                leaderRetrievalService2.stop();
            }
        }
    }

    /**
     * Test that errors in the {@link LeaderElectionService} are correctly forwarded to the
     *  {@link LeaderContender}.
     */
    @Test
    public void testExceptionForwarding() throws Exception {
        ZooKeeperLeaderElectionService leaderElectionService = null;
        ZooKeeperLeaderRetrievalService leaderRetrievalService = null;
        TestingListener listener = new TestingListener();
        TestingContender testingContender;
        CuratorFramework client;
        final CreateBuilder mockCreateBuilder = Mockito.mock(CreateBuilder.class, Mockito.RETURNS_DEEP_STUBS);
        final Exception testException = new Exception("Test exception");
        try {
            client = Mockito.spy(ZooKeeperUtils.startCuratorFramework(configuration));
            Answer<CreateBuilder> answer = new Answer<CreateBuilder>() {
                private int counter = 0;

                @Override
                public CreateBuilder answer(InvocationOnMock invocation) throws Throwable {
                    (counter)++;
                    // at first we have to create the leader latch, there it mustn't fail yet
                    if ((counter) < 2) {
                        return ((CreateBuilder) (invocation.callRealMethod()));
                    } else {
                        return mockCreateBuilder;
                    }
                }
            };
            Mockito.doAnswer(answer).when(client).create();
            Mockito.when(mockCreateBuilder.creatingParentsIfNeeded().withMode(Matchers.any(CreateMode.class)).forPath(ArgumentMatchers.anyString(), ArgumentMatchers.any(byte[].class))).thenThrow(testException);
            leaderElectionService = new ZooKeeperLeaderElectionService(client, "/latch", "/leader");
            leaderRetrievalService = ZooKeeperUtils.createLeaderRetrievalService(client, configuration);
            testingContender = new TestingContender(ZooKeeperLeaderElectionTest.TEST_URL, leaderElectionService);
            leaderElectionService.start(testingContender);
            leaderRetrievalService.start(listener);
            testingContender.waitForError(ZooKeeperLeaderElectionTest.timeout);
            Assert.assertNotNull(testingContender.getError());
            Assert.assertEquals(testException, testingContender.getError().getCause());
        } finally {
            if (leaderElectionService != null) {
                leaderElectionService.stop();
            }
            if (leaderRetrievalService != null) {
                leaderRetrievalService.stop();
            }
        }
    }

    /**
     * Tests that there is no information left in the ZooKeeper cluster after the ZooKeeper client
     * has terminated. In other words, checks that the ZooKeeperLeaderElection service uses
     * ephemeral nodes.
     */
    @Test
    public void testEphemeralZooKeeperNodes() throws Exception {
        ZooKeeperLeaderElectionService leaderElectionService;
        ZooKeeperLeaderRetrievalService leaderRetrievalService = null;
        TestingContender testingContender;
        TestingListener listener;
        CuratorFramework client = null;
        CuratorFramework client2 = null;
        NodeCache cache = null;
        try {
            client = ZooKeeperUtils.startCuratorFramework(configuration);
            client2 = ZooKeeperUtils.startCuratorFramework(configuration);
            leaderElectionService = ZooKeeperUtils.createLeaderElectionService(client, configuration);
            leaderRetrievalService = ZooKeeperUtils.createLeaderRetrievalService(client2, configuration);
            testingContender = new TestingContender(ZooKeeperLeaderElectionTest.TEST_URL, leaderElectionService);
            listener = new TestingListener();
            final String leaderPath = configuration.getString(HA_ZOOKEEPER_LEADER_PATH);
            cache = new NodeCache(client2, leaderPath);
            ZooKeeperLeaderElectionTest.ExistsCacheListener existsListener = new ZooKeeperLeaderElectionTest.ExistsCacheListener(cache);
            ZooKeeperLeaderElectionTest.DeletedCacheListener deletedCacheListener = new ZooKeeperLeaderElectionTest.DeletedCacheListener(cache);
            cache.getListenable().addListener(existsListener);
            cache.start();
            leaderElectionService.start(testingContender);
            testingContender.waitForLeader(ZooKeeperLeaderElectionTest.timeout);
            Future<Boolean> existsFuture = existsListener.nodeExists();
            existsFuture.get(ZooKeeperLeaderElectionTest.timeout, TimeUnit.MILLISECONDS);
            cache.getListenable().addListener(deletedCacheListener);
            leaderElectionService.stop();
            // now stop the underlying client
            client.close();
            Future<Boolean> deletedFuture = deletedCacheListener.nodeDeleted();
            // make sure that the leader node has been deleted
            deletedFuture.get(ZooKeeperLeaderElectionTest.timeout, TimeUnit.MILLISECONDS);
            leaderRetrievalService.start(listener);
            try {
                listener.waitForNewLeader(1000L);
                Assert.fail(("TimeoutException was expected because there is no leader registered and " + "thus there shouldn't be any leader information in ZooKeeper."));
            } catch (TimeoutException e) {
                // that was expected
            }
        } finally {
            if (leaderRetrievalService != null) {
                leaderRetrievalService.stop();
            }
            if (cache != null) {
                cache.close();
            }
            if (client2 != null) {
                client2.close();
            }
        }
    }

    private static class ExistsCacheListener implements NodeCacheListener {
        final CompletableFuture<Boolean> existsPromise = new CompletableFuture<>();

        final NodeCache cache;

        public ExistsCacheListener(final NodeCache cache) {
            this.cache = cache;
        }

        public Future<Boolean> nodeExists() {
            return existsPromise;
        }

        @Override
        public void nodeChanged() throws Exception {
            ChildData data = cache.getCurrentData();
            if ((data != null) && (!(existsPromise.isDone()))) {
                existsPromise.complete(true);
                cache.getListenable().removeListener(this);
            }
        }
    }

    private static class DeletedCacheListener implements NodeCacheListener {
        final CompletableFuture<Boolean> deletedPromise = new CompletableFuture<>();

        final NodeCache cache;

        public DeletedCacheListener(final NodeCache cache) {
            this.cache = cache;
        }

        public Future<Boolean> nodeDeleted() {
            return deletedPromise;
        }

        @Override
        public void nodeChanged() throws Exception {
            ChildData data = cache.getCurrentData();
            if ((data == null) && (!(deletedPromise.isDone()))) {
                deletedPromise.complete(true);
                cache.getListenable().removeListener(this);
            }
        }
    }
}

