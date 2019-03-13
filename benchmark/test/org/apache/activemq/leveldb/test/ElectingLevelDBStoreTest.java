/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.leveldb.test;


import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.leveldb.CountDownFuture;
import org.apache.activemq.leveldb.replicated.ElectingLevelDBStore;
import org.apache.zookeeper.server.TestServerCnxnFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class ElectingLevelDBStoreTest extends ZooKeeperTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(ElectingLevelDBStoreTest.class);

    ArrayList<ElectingLevelDBStore> stores = new ArrayList<ElectingLevelDBStore>();

    ElectingLevelDBStore master = null;

    @Test(timeout = (1000 * 60) * 10)
    public void testZooKeeperServerFailure() throws Exception {
        final ArrayList<ElectingLevelDBStore> stores = new ArrayList<ElectingLevelDBStore>();
        ArrayList<CountDownFuture> pending_starts = new ArrayList<CountDownFuture>();
        for (String dir : new String[]{ "leveldb-node1", "leveldb-node2", "leveldb-node3" }) {
            ElectingLevelDBStore store = createStoreNode();
            store.setDirectory(new File(ZooKeeperTestSupport.data_dir(), dir));
            stores.add(store);
            pending_starts.add(asyncStart(store));
        }
        // At least one of the stores should have started.
        CountDownFuture f = waitFor((30 * 1000), pending_starts.toArray(new CountDownFuture[pending_starts.size()]));
        Assert.assertTrue((f != null));
        pending_starts.remove(f);
        // The other stores should not start..
        ElectingLevelDBStoreTest.LOG.info("Making sure the other stores don't start");
        Thread.sleep(5000);
        for (CountDownFuture start : pending_starts) {
            Assert.assertFalse(start.completed());
        }
        // Stop ZooKeeper..
        ElectingLevelDBStoreTest.LOG.info("SHUTTING DOWN ZooKeeper!");
        shutdown();
        // None of the store should be slaves...
        within(30, TimeUnit.SECONDS, new ZooKeeperTestSupport.Task() {
            public void run() throws Exception {
                for (ElectingLevelDBStore store : stores) {
                    Assert.assertFalse(store.isMaster());
                }
            }
        });
    }

    /* testAMQ5082 tests the behavior of an ElectingLevelDBStore
    pool when ZooKeeper I/O timeouts occur. See issue AMQ-5082.
     */
    @Test(timeout = (1000 * 60) * 5)
    public void testAMQ5082() throws Throwable {
        final ArrayList<ElectingLevelDBStore> stores = new ArrayList<ElectingLevelDBStore>();
        ElectingLevelDBStoreTest.LOG.info("Launching 3 stores");
        for (String dir : new String[]{ "leveldb-node1", "leveldb-node2", "leveldb-node3" }) {
            ElectingLevelDBStore store = createStoreNode();
            store.setDirectory(new File(ZooKeeperTestSupport.data_dir(), dir));
            stores.add(store);
            asyncStart(store);
        }
        ElectingLevelDBStoreTest.LOG.info("Waiting 30s for stores to start");
        Thread.sleep((30 * 1000));
        ElectingLevelDBStoreTest.LOG.info("Checking for a single master");
        ElectingLevelDBStore master = null;
        for (ElectingLevelDBStore store : stores) {
            if (store.isMaster()) {
                Assert.assertNull(master);
                master = store;
            }
        }
        Assert.assertNotNull(master);
        ElectingLevelDBStoreTest.LOG.info("Imposing 1s I/O wait on Zookeeper connections, waiting 30s to confirm that quorum is not lost");
        this.connector.testHandle.setIOWaitMillis((1 * 1000), (30 * 1000));
        ElectingLevelDBStoreTest.LOG.info("Confirming that the quorum has not been lost");
        for (ElectingLevelDBStore store : stores) {
            if (store.isMaster()) {
                Assert.assertTrue((master == store));
            }
        }
        ElectingLevelDBStoreTest.LOG.info("Imposing 11s I/O wait on Zookeeper connections, waiting 30s for quorum to be lost");
        this.connector.testHandle.setIOWaitMillis((11 * 1000), (30 * 1000));
        ElectingLevelDBStoreTest.LOG.info("Confirming that the quorum has been lost");
        for (ElectingLevelDBStore store : stores) {
            Assert.assertFalse(store.isMaster());
        }
        master = null;
        ElectingLevelDBStoreTest.LOG.info("Lifting I/O wait on Zookeeper connections, waiting 30s for quorum to be re-established");
        this.connector.testHandle.setIOWaitMillis(0, (30 * 1000));
        ElectingLevelDBStoreTest.LOG.info("Checking for a single master");
        for (ElectingLevelDBStore store : stores) {
            if (store.isMaster()) {
                Assert.assertNull(master);
                master = store;
            }
        }
        Assert.assertNotNull(master);
    }
}

