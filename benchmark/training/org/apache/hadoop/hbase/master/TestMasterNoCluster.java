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
package org.apache.hadoop.hbase.master;


import HConstants.CLIENT_ZOOKEEPER_CLIENT_PORT;
import HConstants.CLIENT_ZOOKEEPER_OBSERVER_MODE;
import HConstants.CLIENT_ZOOKEEPER_QUORUM;
import HConstants.LOCALHOST;
import LoadBalancer.TABLES_ON_MASTER;
import ServerManager.WAIT_ON_REGIONSERVERS_MINTOSTART;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Standup the master and fake it to test various aspects of master function.
 * Does NOT spin up a mini hbase nor mini dfs cluster testing master (it does
 * put up a zk cluster but this is usually pretty fast compared).  Also, should
 * be possible to inject faults at points difficult to get at in cluster context.
 * TODO: Speed up the zk connection by Master.  It pauses 5 seconds establishing
 * session.
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestMasterNoCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterNoCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterNoCluster.class);

    private static final HBaseTestingUtility TESTUTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    /**
     * Test starting master then stopping it before its fully up.
     *
     * @throws IOException
     * 		
     * @throws KeeperException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testStopDuringStart() throws IOException, InterruptedException, KeeperException {
        HMaster master = new HMaster(TestMasterNoCluster.TESTUTIL.getConfiguration());
        master.start();
        // Immediately have it stop.  We used hang in assigning meta.
        master.stopMaster();
        master.join();
    }

    @Test(timeout = 60000)
    public void testMasterInitWithSameClientServerZKQuorum() throws Exception {
        Configuration conf = new Configuration(TestMasterNoCluster.TESTUTIL.getConfiguration());
        conf.set(CLIENT_ZOOKEEPER_QUORUM, LOCALHOST);
        conf.setInt(CLIENT_ZOOKEEPER_CLIENT_PORT, getZkCluster().getClientPort());
        HMaster master = new HMaster(conf);
        master.start();
        // the master will abort due to IllegalArgumentException so we should finish within 60 seconds
        master.join();
    }

    @Test(timeout = 60000)
    public void testMasterInitWithObserverModeClientZKQuorum() throws Exception {
        Configuration conf = new Configuration(TestMasterNoCluster.TESTUTIL.getConfiguration());
        Assert.assertFalse(Boolean.getBoolean(CLIENT_ZOOKEEPER_OBSERVER_MODE));
        // set client ZK to some non-existing address and make sure server won't access client ZK
        // (server start should not be affected)
        conf.set(CLIENT_ZOOKEEPER_QUORUM, LOCALHOST);
        conf.setInt(CLIENT_ZOOKEEPER_CLIENT_PORT, ((getZkCluster().getClientPort()) + 1));
        // settings to allow us not to start additional RS
        conf.setInt(WAIT_ON_REGIONSERVERS_MINTOSTART, 1);
        conf.setBoolean(TABLES_ON_MASTER, true);
        // main setting for this test case
        conf.setBoolean(CLIENT_ZOOKEEPER_OBSERVER_MODE, true);
        HMaster master = new HMaster(conf);
        master.start();
        while (!(master.isInitialized())) {
            Threads.sleep(200);
        } 
        Assert.assertNull(master.metaLocationSyncer);
        Assert.assertNull(master.masterAddressSyncer);
        master.stopMaster();
        master.join();
    }
}

