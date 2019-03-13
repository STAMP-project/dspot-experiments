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
package org.apache.hadoop.hbase;


import Option.BACKUP_MASTERS;
import Option.BALANCER_ON;
import Option.CLUSTER_ID;
import Option.DEAD_SERVERS;
import Option.HBASE_VERSION;
import Option.LIVE_SERVERS;
import Option.MASTER;
import Option.MASTER_COPROCESSORS;
import Option.SERVERS_NAME;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.hadoop.hbase.ClusterMetrics.Option;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.JVMClusterUtil.MasterThread;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the ClusterStatus.
 */
@Category(MediumTests.class)
public class TestClientClusterStatus {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClientClusterStatus.class);

    private static HBaseTestingUtility UTIL;

    private static Admin ADMIN;

    private static final int SLAVES = 5;

    private static final int MASTERS = 3;

    private static MiniHBaseCluster CLUSTER;

    private static HRegionServer DEAD;

    @Test
    public void testDefaults() throws Exception {
        ClusterStatus origin = TestClientClusterStatus.ADMIN.getClusterStatus();
        ClusterStatus defaults = new ClusterStatus(TestClientClusterStatus.ADMIN.getClusterMetrics(EnumSet.allOf(Option.class)));
        TestClientClusterStatus.checkPbObjectNotNull(origin);
        TestClientClusterStatus.checkPbObjectNotNull(defaults);
        Assert.assertEquals(origin.getHBaseVersion(), defaults.getHBaseVersion());
        Assert.assertEquals(origin.getClusterId(), defaults.getClusterId());
        Assert.assertTrue(((origin.getAverageLoad()) == (defaults.getAverageLoad())));
        Assert.assertTrue(((origin.getBackupMastersSize()) == (defaults.getBackupMastersSize())));
        Assert.assertTrue(((origin.getDeadServersSize()) == (defaults.getDeadServersSize())));
        Assert.assertTrue(((origin.getRegionsCount()) == (defaults.getRegionsCount())));
        Assert.assertTrue(((origin.getServersSize()) == (defaults.getServersSize())));
        Assert.assertTrue(((origin.getMasterInfoPort()) == (defaults.getMasterInfoPort())));
        Assert.assertTrue(origin.equals(defaults));
        Assert.assertTrue(((origin.getServersName().size()) == (defaults.getServersName().size())));
    }

    @Test
    public void testNone() throws Exception {
        ClusterMetrics status0 = TestClientClusterStatus.ADMIN.getClusterMetrics(EnumSet.allOf(Option.class));
        ClusterMetrics status1 = TestClientClusterStatus.ADMIN.getClusterMetrics(EnumSet.noneOf(Option.class));
        // Do a rough compare. More specific compares can fail because all regions not deployed yet
        // or more requests than expected.
        Assert.assertEquals(status0.getLiveServerMetrics().size(), status1.getLiveServerMetrics().size());
        TestClientClusterStatus.checkPbObjectNotNull(new ClusterStatus(status0));
        TestClientClusterStatus.checkPbObjectNotNull(new ClusterStatus(status1));
    }

    @Test
    public void testLiveAndDeadServersStatus() throws Exception {
        // Count the number of live regionservers
        List<RegionServerThread> regionserverThreads = TestClientClusterStatus.CLUSTER.getLiveRegionServerThreads();
        int numRs = 0;
        int len = regionserverThreads.size();
        for (int i = 0; i < len; i++) {
            if (regionserverThreads.get(i).isAlive()) {
                numRs++;
            }
        }
        // Depending on the (random) order of unit execution we may run this unit before the
        // minicluster is fully up and recovered from the RS shutdown done during test init.
        Waiter.waitFor(TestClientClusterStatus.CLUSTER.getConfiguration(), (10 * 1000), 100, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                ClusterStatus status = new ClusterStatus(TestClientClusterStatus.ADMIN.getClusterMetrics(EnumSet.of(LIVE_SERVERS)));
                Assert.assertNotNull(status);
                return (status.getRegionsCount()) > 0;
            }
        });
        // Retrieve live servers and dead servers info.
        EnumSet<Option> options = EnumSet.of(LIVE_SERVERS, DEAD_SERVERS, SERVERS_NAME);
        ClusterStatus status = new ClusterStatus(TestClientClusterStatus.ADMIN.getClusterMetrics(options));
        TestClientClusterStatus.checkPbObjectNotNull(status);
        Assert.assertNotNull(status);
        Assert.assertNotNull(status.getServers());
        // exclude a dead region server
        Assert.assertEquals(((TestClientClusterStatus.SLAVES) - 1), numRs);
        // live servers = nums of regionservers
        // By default, HMaster don't carry any regions so it won't report its load.
        // Hence, it won't be in the server list.
        Assert.assertEquals(status.getServers().size(), numRs);
        Assert.assertTrue(((status.getRegionsCount()) > 0));
        Assert.assertNotNull(status.getDeadServerNames());
        Assert.assertEquals(1, status.getDeadServersSize());
        ServerName deadServerName = status.getDeadServerNames().iterator().next();
        Assert.assertEquals(TestClientClusterStatus.DEAD.getServerName(), deadServerName);
        Assert.assertNotNull(status.getServersName());
        Assert.assertEquals(numRs, status.getServersName().size());
    }

    @Test
    public void testMasterAndBackupMastersStatus() throws Exception {
        // get all the master threads
        List<MasterThread> masterThreads = TestClientClusterStatus.CLUSTER.getMasterThreads();
        int numActive = 0;
        int activeIndex = 0;
        ServerName activeName = null;
        HMaster active = null;
        for (int i = 0; i < (masterThreads.size()); i++) {
            if (masterThreads.get(i).getMaster().isActiveMaster()) {
                numActive++;
                activeIndex = i;
                active = masterThreads.get(activeIndex).getMaster();
                activeName = active.getServerName();
            }
        }
        Assert.assertNotNull(active);
        Assert.assertEquals(1, numActive);
        Assert.assertEquals(TestClientClusterStatus.MASTERS, masterThreads.size());
        // Retrieve master and backup masters infos only.
        EnumSet<Option> options = EnumSet.of(MASTER, BACKUP_MASTERS);
        ClusterStatus status = new ClusterStatus(TestClientClusterStatus.ADMIN.getClusterMetrics(options));
        Assert.assertTrue(status.getMaster().equals(activeName));
        Assert.assertEquals(((TestClientClusterStatus.MASTERS) - 1), status.getBackupMastersSize());
    }

    @Test
    public void testOtherStatusInfos() throws Exception {
        EnumSet<Option> options = EnumSet.of(MASTER_COPROCESSORS, HBASE_VERSION, CLUSTER_ID, BALANCER_ON);
        ClusterStatus status = new ClusterStatus(TestClientClusterStatus.ADMIN.getClusterMetrics(options));
        Assert.assertTrue(((status.getMasterCoprocessors().length) == 1));
        Assert.assertNotNull(status.getHBaseVersion());
        Assert.assertNotNull(status.getClusterId());
        Assert.assertTrue(((status.getAverageLoad()) == 0.0));
        Assert.assertNotNull(status.getBalancerOn());
    }

    @Test
    public void testObserver() throws IOException {
        int preCount = TestClientClusterStatus.MyObserver.PRE_COUNT.get();
        int postCount = TestClientClusterStatus.MyObserver.POST_COUNT.get();
        Assert.assertTrue(Stream.of(TestClientClusterStatus.ADMIN.getClusterStatus().getMasterCoprocessors()).anyMatch(( s) -> s.equals(.class.getSimpleName())));
        Assert.assertEquals((preCount + 1), TestClientClusterStatus.MyObserver.PRE_COUNT.get());
        Assert.assertEquals((postCount + 1), TestClientClusterStatus.MyObserver.POST_COUNT.get());
    }

    public static class MyObserver implements MasterCoprocessor , MasterObserver {
        private static final AtomicInteger PRE_COUNT = new AtomicInteger(0);

        private static final AtomicInteger POST_COUNT = new AtomicInteger(0);

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetClusterMetrics(ObserverContext<MasterCoprocessorEnvironment> ctx) throws IOException {
            TestClientClusterStatus.MyObserver.PRE_COUNT.incrementAndGet();
        }

        @Override
        public void postGetClusterMetrics(ObserverContext<MasterCoprocessorEnvironment> ctx, ClusterMetrics status) throws IOException {
            TestClientClusterStatus.MyObserver.POST_COUNT.incrementAndGet();
        }
    }
}

