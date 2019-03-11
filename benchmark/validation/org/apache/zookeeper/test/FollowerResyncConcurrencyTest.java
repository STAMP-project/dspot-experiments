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
package org.apache.zookeeper.test;


import CreateMode.EPHEMERAL_SEQUENTIAL;
import CreateMode.PERSISTENT;
import Event.EventType;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.quorum.Leader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FollowerResyncConcurrencyTest extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(FollowerResyncConcurrencyTest.class);

    public static final long CONNECTION_TIMEOUT = ClientTest.CONNECTION_TIMEOUT;

    private AtomicInteger counter = new AtomicInteger(0);

    private AtomicInteger errors = new AtomicInteger(0);

    /**
     * Keep track of pending async operations, we shouldn't start verifying
     * the state until pending operation is 0
     */
    private AtomicInteger pending = new AtomicInteger(0);

    /**
     * See ZOOKEEPER-1319 - verify that a lagging follwer resyncs correctly
     *
     * 1) start with down quorum
     * 2) start leader/follower1, add some data
     * 3) restart leader/follower1
     * 4) start follower2
     * 5) verify data consistency across the ensemble
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLaggingFollowerResyncsUnderNewEpoch() throws Exception {
        ClientBase.CountdownWatcher watcher1 = new ClientBase.CountdownWatcher();
        ClientBase.CountdownWatcher watcher2 = new ClientBase.CountdownWatcher();
        ClientBase.CountdownWatcher watcher3 = new ClientBase.CountdownWatcher();
        QuorumUtil qu = new QuorumUtil(1);
        qu.shutdownAll();
        qu.start(1);
        qu.start(2);
        Assert.assertTrue("Waiting for server up", ClientBase.waitForServerUp(("127.0.0.1:" + (qu.getPeer(1).clientPort)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue("Waiting for server up", ClientBase.waitForServerUp(("127.0.0.1:" + (qu.getPeer(2).clientPort)), ClientBase.CONNECTION_TIMEOUT));
        ZooKeeper zk1 = FollowerResyncConcurrencyTest.createClient(qu.getPeer(1).peer.getClientPort(), watcher1);
        FollowerResyncConcurrencyTest.LOG.info("zk1 has session id 0x{}", Long.toHexString(zk1.getSessionId()));
        final String resyncPath = "/resyncundernewepoch";
        zk1.create(resyncPath, null, OPEN_ACL_UNSAFE, PERSISTENT);
        zk1.close();
        qu.shutdown(1);
        qu.shutdown(2);
        Assert.assertTrue("Waiting for server down", ClientBase.waitForServerDown(("127.0.0.1:" + (qu.getPeer(1).clientPort)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue("Waiting for server down", ClientBase.waitForServerDown(("127.0.0.1:" + (qu.getPeer(2).clientPort)), ClientBase.CONNECTION_TIMEOUT));
        qu.start(1);
        qu.start(2);
        Assert.assertTrue("Waiting for server up", ClientBase.waitForServerUp(("127.0.0.1:" + (qu.getPeer(1).clientPort)), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue("Waiting for server up", ClientBase.waitForServerUp(("127.0.0.1:" + (qu.getPeer(2).clientPort)), ClientBase.CONNECTION_TIMEOUT));
        qu.start(3);
        Assert.assertTrue("Waiting for server up", ClientBase.waitForServerUp(("127.0.0.1:" + (qu.getPeer(3).clientPort)), ClientBase.CONNECTION_TIMEOUT));
        zk1 = FollowerResyncConcurrencyTest.createClient(qu.getPeer(1).peer.getClientPort(), watcher1);
        FollowerResyncConcurrencyTest.LOG.info("zk1 has session id 0x{}", Long.toHexString(zk1.getSessionId()));
        Assert.assertNotNull("zk1 has data", zk1.exists(resyncPath, false));
        final ZooKeeper zk2 = FollowerResyncConcurrencyTest.createClient(qu.getPeer(2).peer.getClientPort(), watcher2);
        FollowerResyncConcurrencyTest.LOG.info("zk2 has session id 0x{}", Long.toHexString(zk2.getSessionId()));
        Assert.assertNotNull("zk2 has data", zk2.exists(resyncPath, false));
        final ZooKeeper zk3 = FollowerResyncConcurrencyTest.createClient(qu.getPeer(3).peer.getClientPort(), watcher3);
        FollowerResyncConcurrencyTest.LOG.info("zk3 has session id 0x{}", Long.toHexString(zk3.getSessionId()));
        Assert.assertNotNull("zk3 has data", zk3.exists(resyncPath, false));
        zk1.close();
        zk2.close();
        zk3.close();
        qu.shutdownAll();
    }

    /**
     * See ZOOKEEPER-962. This tests for one of the bugs hit while fixing this,
     * setting the ZXID of the SNAP packet
     * Starts up 3 ZKs. Shut down F1, write a node, restart the one that was shut down
     * The non-leader ZKs are writing to cluster
     * Shut down F1 again
     * Restart after sessions are expired, expect to get a snap file
     * Shut down, run some transactions through.
     * Restart to a diff while transactions are running in leader
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws KeeperException
     * 		
     */
    @Test
    public void testResyncBySnapThenDiffAfterFollowerCrashes() throws IOException, InterruptedException, Throwable, KeeperException {
        followerResyncCrashTest(false);
    }

    /**
     * Same as testResyncBySnapThenDiffAfterFollowerCrashes() but we resync
     * follower using txnlog
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws KeeperException
     * 		
     */
    @Test
    public void testResyncByTxnlogThenDiffAfterFollowerCrashes() throws IOException, InterruptedException, Throwable, KeeperException {
        followerResyncCrashTest(true);
    }

    /**
     * This test:
     * Starts up 3 ZKs. The non-leader ZKs are writing to cluster
     * Shut down one of the non-leader ZKs.
     * Restart after sessions have expired but <500 txns have taken place (get a diff)
     * Shut down immediately after restarting, start running separate thread with other transactions
     * Restart to a diff while transactions are running in leader
     *
     *
     * Before fixes for ZOOKEEPER-962, restarting off of diff could get an inconsistent view of data missing transactions that
     * completed during diff syncing. Follower would also be considered "restarted" before all forwarded transactions
     * were completely processed, so restarting would cause a snap file with a too-high zxid to be written, and transactions
     * would be missed
     *
     * This test should pretty reliably catch the failure of restarting the server before all diff messages have been processed,
     * however, due to the transient nature of the system it may not catch failures due to concurrent processing of transactions
     * during the leader's diff forwarding.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws KeeperException
     * 		
     * @throws Throwable
     * 		
     */
    @Test
    public void testResyncByDiffAfterFollowerCrashes() throws IOException, InterruptedException, Throwable, KeeperException {
        final Semaphore sem = new Semaphore(0);
        QuorumUtil qu = new QuorumUtil(1);
        qu.startAll();
        ClientBase.CountdownWatcher watcher1 = new ClientBase.CountdownWatcher();
        ClientBase.CountdownWatcher watcher2 = new ClientBase.CountdownWatcher();
        ClientBase.CountdownWatcher watcher3 = new ClientBase.CountdownWatcher();
        int index = 1;
        while ((qu.getPeer(index).peer.leader) == null) {
            index++;
        } 
        Leader leader = qu.getPeer(index).peer.leader;
        Assert.assertNotNull(leader);
        /* Reusing the index variable to select a follower to connect to */
        index = (index == 1) ? 2 : 1;
        FollowerResyncConcurrencyTest.LOG.info("Connecting to follower: {}", index);
        final ZooKeeper zk1 = FollowerResyncConcurrencyTest.createClient(qu.getPeer(index).peer.getClientPort(), watcher1);
        FollowerResyncConcurrencyTest.LOG.info("zk1 has session id 0x{}", Long.toHexString(zk1.getSessionId()));
        final ZooKeeper zk2 = FollowerResyncConcurrencyTest.createClient(qu.getPeer(index).peer.getClientPort(), watcher2);
        FollowerResyncConcurrencyTest.LOG.info("zk2 has session id 0x{}", Long.toHexString(zk2.getSessionId()));
        final ZooKeeper zk3 = FollowerResyncConcurrencyTest.createClient(qu.getPeer(3).peer.getClientPort(), watcher3);
        FollowerResyncConcurrencyTest.LOG.info("zk3 has session id 0x{}", Long.toHexString(zk3.getSessionId()));
        zk1.create("/first", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        zk2.create("/mybar", null, OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL);
        final AtomicBoolean runNow = new AtomicBoolean(false);
        Thread mytestfooThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int inSyncCounter = 0;
                while (inSyncCounter < 400) {
                    if (runNow.get()) {
                        zk3.create("/mytestfoo", null, OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL, new AsyncCallback.StringCallback() {
                            @Override
                            public void processResult(int rc, String path, Object ctx, String name) {
                                pending.decrementAndGet();
                                counter.incrementAndGet();
                                if (rc != 0) {
                                    errors.incrementAndGet();
                                }
                                if ((counter.get()) > 7300) {
                                    sem.release();
                                }
                            }
                        }, null);
                        pending.incrementAndGet();
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                        inSyncCounter++;
                    } else {
                        Thread.yield();
                    }
                } 
            }
        });
        mytestfooThread.start();
        for (int i = 0; i < 5000; i++) {
            zk2.create("/mybar", null, OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL, new AsyncCallback.StringCallback() {
                @Override
                public void processResult(int rc, String path, Object ctx, String name) {
                    pending.decrementAndGet();
                    counter.incrementAndGet();
                    if (rc != 0) {
                        errors.incrementAndGet();
                    }
                    if ((counter.get()) > 7300) {
                        sem.release();
                    }
                }
            }, null);
            pending.incrementAndGet();
            if (i == 1000) {
                qu.shutdown(index);
                Thread.sleep(1100);
                FollowerResyncConcurrencyTest.LOG.info("Shutting down s1");
            }
            if (((i == 1100) || (i == 1150)) || (i == 1200)) {
                Thread.sleep(1000);
            }
            if (i == 1200) {
                qu.startThenShutdown(index);
                runNow.set(true);
                qu.restart(index);
                FollowerResyncConcurrencyTest.LOG.info("Setting up server: {}", index);
            }
            if ((i >= 1000) && ((i % 2) == 0)) {
                zk3.create("/newbaz", null, OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL, new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                        pending.decrementAndGet();
                        counter.incrementAndGet();
                        if (rc != 0) {
                            errors.incrementAndGet();
                        }
                        if ((counter.get()) > 7300) {
                            sem.release();
                        }
                    }
                }, null);
                pending.incrementAndGet();
            }
            if (((i == 1050) || (i == 1100)) || (i == 1150)) {
                Thread.sleep(1000);
            }
        }
        // Wait until all updates return
        if (!(sem.tryAcquire(ClientBase.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS))) {
            FollowerResyncConcurrencyTest.LOG.warn("Did not aquire semaphore fast enough");
        }
        mytestfooThread.join(ClientBase.CONNECTION_TIMEOUT);
        if (mytestfooThread.isAlive()) {
            FollowerResyncConcurrencyTest.LOG.error("mytestfooThread is still alive");
        }
        Assert.assertTrue(waitForPendingRequests(60));
        Assert.assertTrue(waitForSync(qu, index, 10));
        // Verify that server is following and has the same epoch as the leader
        verifyState(qu, index, leader);
        zk1.close();
        zk2.close();
        zk3.close();
        qu.shutdownAll();
    }

    /**
     * Verify that the server is sending the proper zxid. See ZOOKEEPER-1412.
     */
    @Test
    public void testFollowerSendsLastZxid() throws Exception {
        QuorumUtil qu = new QuorumUtil(1);
        qu.startAll();
        int index = 1;
        while ((qu.getPeer(index).peer.follower) == null) {
            index++;
        } 
        FollowerResyncConcurrencyTest.LOG.info("Connecting to follower: {}", index);
        TestableZooKeeper zk = FollowerResyncConcurrencyTest.createTestableClient(("localhost:" + (qu.getPeer(index).peer.getClientPort())));
        Assert.assertEquals(0L, zk.testableLastZxid());
        exists("/", false);
        long lzxid = zk.testableLastZxid();
        Assert.assertTrue((("lzxid:" + lzxid) + " > 0"), (lzxid > 0));
        close();
        qu.shutdownAll();
    }

    private class MyWatcher extends ClientBase.CountdownWatcher {
        LinkedBlockingQueue<WatchedEvent> events = new LinkedBlockingQueue<WatchedEvent>();

        public void process(WatchedEvent event) {
            super.process(event);
            if ((event.getType()) != (EventType.None)) {
                try {
                    events.put(event);
                } catch (InterruptedException e) {
                    FollowerResyncConcurrencyTest.LOG.warn("ignoring interrupt during event.put");
                }
            }
        }
    }

    /**
     * Verify that the server is sending the proper zxid, and as a result
     * the watch doesn't fire. See ZOOKEEPER-1412.
     */
    @Test
    public void testFollowerWatcherResync() throws Exception {
        QuorumUtil qu = new QuorumUtil(1);
        qu.startAll();
        int index = 1;
        while ((qu.getPeer(index).peer.follower) == null) {
            index++;
        } 
        FollowerResyncConcurrencyTest.LOG.info("Connecting to follower: {}", index);
        TestableZooKeeper zk1 = FollowerResyncConcurrencyTest.createTestableClient(("localhost:" + (qu.getPeer(index).peer.getClientPort())));
        zk1.create("/foo", "foo".getBytes(), Ids.OPEN_ACL_UNSAFE, PERSISTENT);
        FollowerResyncConcurrencyTest.MyWatcher watcher = new FollowerResyncConcurrencyTest.MyWatcher();
        TestableZooKeeper zk2 = FollowerResyncConcurrencyTest.createTestableClient(watcher, ("localhost:" + (qu.getPeer(index).peer.getClientPort())));
        exists("/foo", true);
        watcher.reset();
        zk2.testableConnloss();
        if (!(watcher.clientConnected.await(FollowerResyncConcurrencyTest.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS))) {
            Assert.fail("Unable to connect to server");
        }
        Assert.assertArrayEquals("foo".getBytes(), getData("/foo", false, null));
        Assert.assertNull(watcher.events.poll(5, TimeUnit.SECONDS));
        close();
        close();
        qu.shutdownAll();
    }
}

