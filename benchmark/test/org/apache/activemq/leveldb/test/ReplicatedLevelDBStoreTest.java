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
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.leveldb.CountDownFuture;
import org.apache.activemq.leveldb.LevelDBStore;
import org.apache.activemq.leveldb.replicated.MasterLevelDBStore;
import org.apache.activemq.leveldb.replicated.SlaveLevelDBStore;
import org.apache.activemq.leveldb.util.FileSupport;
import org.apache.activemq.store.MessageStore;
import org.fusesource.hawtdispatch.transport.TcpTransport;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class ReplicatedLevelDBStoreTest {
    protected static final Logger LOG = LoggerFactory.getLogger(ReplicatedLevelDBStoreTest.class);

    ArrayList<LevelDBStore> stores = new ArrayList<LevelDBStore>();

    @Test(timeout = (1000 * 60) * 10)
    public void testMinReplicaEnforced() throws Exception {
        File masterDir = new File("target/activemq-data/leveldb-node1");
        File slaveDir = new File("target/activemq-data/leveldb-node2");
        FileSupport.toRichFile(masterDir).recursiveDelete();
        FileSupport.toRichFile(slaveDir).recursiveDelete();
        final MasterLevelDBStore master = createMaster(masterDir);
        master.setReplicas(2);
        CountDownFuture masterStartLatch = asyncStart(master);
        stores.add(master);
        // Start the store should not complete since we don't have enough
        // replicas.
        Assert.assertFalse(masterStartLatch.await(2, TimeUnit.SECONDS));
        // Adding a slave should allow the master startup to complete.
        SlaveLevelDBStore slave = createSlave(master, slaveDir);
        slave.start();
        stores.add(slave);
        Assert.assertTrue(masterStartLatch.await(2, TimeUnit.SECONDS));
        // New updates should complete quickly now..
        MessageStore ms = master.createQueueMessageStore(new ActiveMQQueue("TEST"));
        CountDownFuture f = asyncAddMessage(ms, "m1");
        Assert.assertTrue(f.await(1, TimeUnit.SECONDS));
        // If the slave goes offline, then updates should once again
        // not complete.
        slave.stop();
        f = asyncAddMessage(ms, "m2");
        Assert.assertFalse(f.await(2, TimeUnit.SECONDS));
        // Restart and the op should complete.
        slave = createSlave(master, slaveDir);
        slave.start();
        Assert.assertTrue(f.await(2, TimeUnit.SECONDS));
        master.stop();
        slave.stop();
    }

    @Test(timeout = (1000 * 60) * 10)
    public void testReplication() throws Exception {
        LinkedList<File> directories = new LinkedList<File>();
        directories.add(new File("target/activemq-data/leveldb-node1"));
        directories.add(new File("target/activemq-data/leveldb-node2"));
        directories.add(new File("target/activemq-data/leveldb-node3"));
        resetDirectories(directories);
        // For some reason this had to be 64k to trigger a bug where
        // slave index snapshots were being done incorrectly.
        String playload = ReplicationTestSupport.createPlayload((64 * 1024));
        ArrayList<String> expected_list = new ArrayList<String>();
        // We will rotate between 3 nodes the task of being the master.
        for (int j = 0; j < 5; j++) {
            MasterLevelDBStore master = createMaster(directories.get(0));
            CountDownFuture masterStart = asyncStart(master);
            SlaveLevelDBStore slave1 = createSlave(master, directories.get(1));
            SlaveLevelDBStore slave2 = createSlave(master, directories.get(2));
            asyncStart(slave2);
            masterStart.await();
            if (j == 0) {
                stores.add(master);
                stores.add(slave1);
                stores.add(slave2);
            }
            MessageStore ms = master.createQueueMessageStore(new ActiveMQQueue("TEST"));
            ReplicatedLevelDBStoreTest.LOG.info(("Checking: " + (master.getDirectory())));
            Assert.assertEquals(expected_list, ReplicationTestSupport.getMessages(ms));
            ReplicatedLevelDBStoreTest.LOG.info("Adding messages...");
            final int TOTAL = 500;
            for (int i = 0; i < TOTAL; i++) {
                if ((i % ((int) (TOTAL * 0.1))) == 0) {
                    ReplicatedLevelDBStoreTest.LOG.info((("" + ((100 * i) / TOTAL)) + "% done"));
                }
                if (i == 250) {
                    slave1.start();
                    slave2.stop();
                    ReplicatedLevelDBStoreTest.LOG.info(("Checking: " + (master.getDirectory())));
                    Assert.assertEquals(expected_list, ReplicationTestSupport.getMessages(ms));
                }
                String msgid = (("m:" + j) + ":") + i;
                ReplicationTestSupport.addMessage(ms, msgid, playload);
                expected_list.add(msgid);
            }
            ReplicatedLevelDBStoreTest.LOG.info(("Checking: " + (master.getDirectory())));
            Assert.assertEquals(expected_list, ReplicationTestSupport.getMessages(ms));
            ReplicatedLevelDBStoreTest.LOG.info(("Stopping master: " + (master.getDirectory())));
            master.stop();
            Thread.sleep((3 * 1000));
            ReplicatedLevelDBStoreTest.LOG.info(("Stopping slave: " + (slave1.getDirectory())));
            slave1.stop();
            // Rotate the dir order so that slave1 becomes the master next.
            directories.addLast(directories.removeFirst());
        }
    }

    @Test(timeout = (1000 * 60) * 10)
    public void testSlowSlave() throws Exception {
        LinkedList<File> directories = new LinkedList<File>();
        directories.add(new File("target/activemq-data/leveldb-node1"));
        directories.add(new File("target/activemq-data/leveldb-node2"));
        directories.add(new File("target/activemq-data/leveldb-node3"));
        resetDirectories(directories);
        File node1Dir = directories.get(0);
        File node2Dir = directories.get(1);
        File node3Dir = directories.get(2);
        ArrayList<String> expected_list = new ArrayList<String>();
        MasterLevelDBStore node1 = createMaster(node1Dir);
        stores.add(node1);
        CountDownFuture masterStart = asyncStart(node1);
        // Lets create a 1 slow slave...
        SlaveLevelDBStore node2 = new SlaveLevelDBStore() {
            boolean hitOnce = false;

            @Override
            public TcpTransport create_transport() {
                if (hitOnce) {
                    return super.create_transport();
                }
                hitOnce = true;
                TcpTransport transport = super.create_transport();
                transport.setMaxReadRate((64 * 1024));
                return transport;
            }
        };
        stores.add(node2);
        configureSlave(node2, node1, node2Dir);
        SlaveLevelDBStore node3 = createSlave(node1, node3Dir);
        stores.add(node3);
        asyncStart(node2);
        asyncStart(node3);
        masterStart.await();
        ReplicatedLevelDBStoreTest.LOG.info("Adding messages...");
        String playload = ReplicationTestSupport.createPlayload((64 * 1024));
        MessageStore ms = node1.createQueueMessageStore(new ActiveMQQueue("TEST"));
        final int TOTAL = 10;
        for (int i = 0; i < TOTAL; i++) {
            if (i == 8) {
                // Stop the fast slave so that we wait for the slow slave to
                // catch up..
                node3.stop();
            }
            String msgid = ("m:" + ":") + i;
            ReplicationTestSupport.addMessage(ms, msgid, playload);
            expected_list.add(msgid);
        }
        ReplicatedLevelDBStoreTest.LOG.info("Checking node1 state");
        Assert.assertEquals(expected_list, ReplicationTestSupport.getMessages(ms));
        ReplicatedLevelDBStoreTest.LOG.info(("Stopping node1: " + (node1.node_id())));
        node1.stop();
        ReplicatedLevelDBStoreTest.LOG.info(("Stopping slave: " + (node2.node_id())));
        node2.stop();
    }
}

