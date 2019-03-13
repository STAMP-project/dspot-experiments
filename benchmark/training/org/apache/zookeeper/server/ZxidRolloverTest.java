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
package org.apache.zookeeper.server;


import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.test.ClientBase;
import org.apache.zookeeper.test.QuorumUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify ZOOKEEPER-1277 - ensure that we handle epoch rollover correctly.
 */
public class ZxidRolloverTest extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(ZxidRolloverTest.class);

    private QuorumUtil qu;

    private ZooKeeperServer zksLeader;

    private ZooKeeper[] zkClients = new ZooKeeper[3];

    private ClientBase.CountdownWatcher[] zkClientWatchers = new ClientBase.CountdownWatcher[3];

    private int idxLeader;

    private int idxFollower;

    /**
     * Prior to the fix this test would hang for a while, then fail with
     * connection loss.
     */
    @Test
    public void testSimpleRolloverFollower() throws Exception {
        adjustEpochNearEnd();
        ZooKeeper zk = getClient(((idxLeader) == 1 ? 2 : 1));
        int countCreated = createNodes(zk, 0, 10);
        checkNodes(zk, 0, countCreated);
    }

    /**
     * Similar to testSimpleRollover, but ensure the cluster comes back,
     * has the right data, and is able to serve new requests.
     */
    @Test
    public void testRolloverThenRestart() throws Exception {
        ZooKeeper zk = getClient(idxFollower);
        int countCreated = createNodes(zk, 0, 10);
        adjustEpochNearEnd();
        countCreated += createNodes(zk, countCreated, 10);
        shutdownAll();
        startAll();
        zk = getClient(idxLeader);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        adjustEpochNearEnd();
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        shutdownAll();
        startAll();
        zk = getClient(idxFollower);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        shutdownAll();
        startAll();
        zk = getClient(idxLeader);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        // sanity check
        Assert.assertTrue((countCreated > 0));
        Assert.assertTrue((countCreated < 60));
    }

    /**
     * Similar to testRolloverThenRestart, but ensure a follower comes back,
     * has the right data, and is able to serve new requests.
     */
    @Test
    public void testRolloverThenFollowerRestart() throws Exception {
        ZooKeeper zk = getClient(idxFollower);
        int countCreated = createNodes(zk, 0, 10);
        adjustEpochNearEnd();
        countCreated += createNodes(zk, countCreated, 10);
        shutdown(idxFollower);
        start(idxFollower);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        adjustEpochNearEnd();
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        shutdown(idxFollower);
        start(idxFollower);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        shutdown(idxFollower);
        start(idxFollower);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        // sanity check
        Assert.assertTrue((countCreated > 0));
        Assert.assertTrue((countCreated < 60));
    }

    /**
     * Similar to testRolloverThenRestart, but ensure leadership can change,
     * comes back, has the right data, and is able to serve new requests.
     */
    @Test
    public void testRolloverThenLeaderRestart() throws Exception {
        ZooKeeper zk = getClient(idxLeader);
        int countCreated = createNodes(zk, 0, 10);
        adjustEpochNearEnd();
        checkNodes(zk, 0, countCreated);
        shutdown(idxLeader);
        start(idxLeader);
        zk = getClient(idxLeader);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        adjustEpochNearEnd();
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        shutdown(idxLeader);
        start(idxLeader);
        zk = getClient(idxLeader);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        shutdown(idxLeader);
        start(idxLeader);
        zk = getClient(idxFollower);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        // sanity check
        Assert.assertTrue((countCreated > 0));
        Assert.assertTrue((countCreated < 50));
    }

    /**
     * Similar to testRolloverThenRestart, but ensure we can survive multiple
     * epoch rollovers between restarts.
     */
    @Test
    public void testMultipleRollover() throws Exception {
        ZooKeeper zk = getClient(idxFollower);
        int countCreated = createNodes(zk, 0, 10);
        adjustEpochNearEnd();
        countCreated += createNodes(zk, countCreated, 10);
        adjustEpochNearEnd();
        countCreated += createNodes(zk, countCreated, 10);
        adjustEpochNearEnd();
        countCreated += createNodes(zk, countCreated, 10);
        adjustEpochNearEnd();
        countCreated += createNodes(zk, countCreated, 10);
        shutdownAll();
        startAll();
        zk = getClient(idxFollower);
        adjustEpochNearEnd();
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        shutdown(idxLeader);
        start(idxLeader);
        zk = getClient(idxFollower);
        checkNodes(zk, 0, countCreated);
        countCreated += createNodes(zk, countCreated, 10);
        // sanity check
        Assert.assertTrue((countCreated > 0));
        Assert.assertTrue((countCreated < 70));
    }
}

