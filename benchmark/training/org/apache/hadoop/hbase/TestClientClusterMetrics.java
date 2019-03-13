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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.ClusterMetrics.Option;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.AsyncAdmin;
import org.apache.hadoop.hbase.client.AsyncConnection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.JVMClusterUtil.MasterThread;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SmallTests.class)
public class TestClientClusterMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClientClusterMetrics.class);

    private static HBaseTestingUtility UTIL;

    private static Admin ADMIN;

    private static final int SLAVES = 5;

    private static final int MASTERS = 3;

    private static MiniHBaseCluster CLUSTER;

    private static HRegionServer DEAD;

    @Test
    public void testDefaults() throws Exception {
        ClusterMetrics origin = TestClientClusterMetrics.ADMIN.getClusterMetrics();
        ClusterMetrics defaults = TestClientClusterMetrics.ADMIN.getClusterMetrics(EnumSet.allOf(Option.class));
        Assert.assertEquals(origin.getHBaseVersion(), defaults.getHBaseVersion());
        Assert.assertEquals(origin.getClusterId(), defaults.getClusterId());
        Assert.assertEquals(origin.getAverageLoad(), defaults.getAverageLoad(), 0);
        Assert.assertEquals(origin.getBackupMasterNames().size(), defaults.getBackupMasterNames().size());
        Assert.assertEquals(origin.getDeadServerNames().size(), defaults.getDeadServerNames().size());
        Assert.assertEquals(origin.getRegionCount(), defaults.getRegionCount());
        Assert.assertEquals(origin.getLiveServerMetrics().size(), defaults.getLiveServerMetrics().size());
        Assert.assertEquals(origin.getMasterInfoPort(), defaults.getMasterInfoPort());
        Assert.assertEquals(origin.getServersName().size(), defaults.getServersName().size());
    }

    @Test
    public void testAsyncClient() throws Exception {
        try (AsyncConnection asyncConnect = ConnectionFactory.createAsyncConnection(TestClientClusterMetrics.UTIL.getConfiguration()).get()) {
            AsyncAdmin asyncAdmin = asyncConnect.getAdmin();
            CompletableFuture<ClusterMetrics> originFuture = asyncAdmin.getClusterMetrics();
            CompletableFuture<ClusterMetrics> defaultsFuture = asyncAdmin.getClusterMetrics(EnumSet.allOf(Option.class));
            ClusterMetrics origin = originFuture.get();
            ClusterMetrics defaults = defaultsFuture.get();
            Assert.assertEquals(origin.getHBaseVersion(), defaults.getHBaseVersion());
            Assert.assertEquals(origin.getClusterId(), defaults.getClusterId());
            Assert.assertEquals(origin.getHBaseVersion(), defaults.getHBaseVersion());
            Assert.assertEquals(origin.getClusterId(), defaults.getClusterId());
            Assert.assertEquals(origin.getAverageLoad(), defaults.getAverageLoad(), 0);
            Assert.assertEquals(origin.getBackupMasterNames().size(), defaults.getBackupMasterNames().size());
            Assert.assertEquals(origin.getDeadServerNames().size(), defaults.getDeadServerNames().size());
            Assert.assertEquals(origin.getRegionCount(), defaults.getRegionCount());
            Assert.assertEquals(origin.getLiveServerMetrics().size(), defaults.getLiveServerMetrics().size());
            Assert.assertEquals(origin.getMasterInfoPort(), defaults.getMasterInfoPort());
            Assert.assertEquals(origin.getServersName().size(), defaults.getServersName().size());
        }
    }

    @Test
    public void testLiveAndDeadServersStatus() throws Exception {
        // Count the number of live regionservers
        List<RegionServerThread> regionserverThreads = TestClientClusterMetrics.CLUSTER.getLiveRegionServerThreads();
        int numRs = 0;
        int len = regionserverThreads.size();
        for (int i = 0; i < len; i++) {
            if (regionserverThreads.get(i).isAlive()) {
                numRs++;
            }
        }
        // Depending on the (random) order of unit execution we may run this unit before the
        // minicluster is fully up and recovered from the RS shutdown done during test init.
        Waiter.waitFor(TestClientClusterMetrics.CLUSTER.getConfiguration(), (10 * 1000), 100, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                ClusterMetrics metrics = TestClientClusterMetrics.ADMIN.getClusterMetrics(EnumSet.of(LIVE_SERVERS));
                Assert.assertNotNull(metrics);
                return (metrics.getRegionCount()) > 0;
            }
        });
        // Retrieve live servers and dead servers info.
        EnumSet<Option> options = EnumSet.of(LIVE_SERVERS, DEAD_SERVERS, SERVERS_NAME);
        ClusterMetrics metrics = TestClientClusterMetrics.ADMIN.getClusterMetrics(options);
        Assert.assertNotNull(metrics);
        // exclude a dead region server
        Assert.assertEquals(((TestClientClusterMetrics.SLAVES) - 1), numRs);
        // live servers = nums of regionservers
        // By default, HMaster don't carry any regions so it won't report its load.
        // Hence, it won't be in the server list.
        Assert.assertEquals(numRs, metrics.getLiveServerMetrics().size());
        Assert.assertTrue(((metrics.getRegionCount()) > 0));
        Assert.assertNotNull(metrics.getDeadServerNames());
        Assert.assertEquals(1, metrics.getDeadServerNames().size());
        ServerName deadServerName = metrics.getDeadServerNames().iterator().next();
        Assert.assertEquals(TestClientClusterMetrics.DEAD.getServerName(), deadServerName);
        Assert.assertNotNull(metrics.getServersName());
        Assert.assertEquals(numRs, metrics.getServersName().size());
    }

    @Test
    public void testMasterAndBackupMastersStatus() throws Exception {
        // get all the master threads
        List<MasterThread> masterThreads = TestClientClusterMetrics.CLUSTER.getMasterThreads();
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
        Assert.assertEquals(TestClientClusterMetrics.MASTERS, masterThreads.size());
        // Retrieve master and backup masters infos only.
        EnumSet<Option> options = EnumSet.of(MASTER, BACKUP_MASTERS);
        ClusterMetrics metrics = TestClientClusterMetrics.ADMIN.getClusterMetrics(options);
        Assert.assertTrue(metrics.getMasterName().equals(activeName));
        Assert.assertEquals(((TestClientClusterMetrics.MASTERS) - 1), metrics.getBackupMasterNames().size());
    }

    @Test
    public void testOtherStatusInfos() throws Exception {
        EnumSet<Option> options = EnumSet.of(MASTER_COPROCESSORS, HBASE_VERSION, CLUSTER_ID, BALANCER_ON);
        ClusterMetrics metrics = TestClientClusterMetrics.ADMIN.getClusterMetrics(options);
        Assert.assertEquals(1, metrics.getMasterCoprocessorNames().size());
        Assert.assertNotNull(metrics.getHBaseVersion());
        Assert.assertNotNull(metrics.getClusterId());
        Assert.assertTrue(((metrics.getAverageLoad()) == 0.0));
        Assert.assertNotNull(metrics.getBalancerOn());
    }

    @Test
    public void testObserver() throws IOException {
        int preCount = TestClientClusterMetrics.MyObserver.PRE_COUNT.get();
        int postCount = TestClientClusterMetrics.MyObserver.POST_COUNT.get();
        Assert.assertTrue(TestClientClusterMetrics.ADMIN.getClusterMetrics().getMasterCoprocessorNames().stream().anyMatch(( s) -> s.equals(.class.getSimpleName())));
        Assert.assertEquals((preCount + 1), TestClientClusterMetrics.MyObserver.PRE_COUNT.get());
        Assert.assertEquals((postCount + 1), TestClientClusterMetrics.MyObserver.POST_COUNT.get());
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
            TestClientClusterMetrics.MyObserver.PRE_COUNT.incrementAndGet();
        }

        @Override
        public void postGetClusterMetrics(ObserverContext<MasterCoprocessorEnvironment> ctx, ClusterMetrics metrics) throws IOException {
            TestClientClusterMetrics.MyObserver.POST_COUNT.incrementAndGet();
        }
    }
}

