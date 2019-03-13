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
package org.apache.hadoop.hbase.client;


import HConstants.DEFAULT_ZOOKEEPER_ZNODE_PARENT;
import HConstants.USE_META_REPLICAS;
import HConstants.ZOOKEEPER_ZNODE_PARENT;
import Option.LIVE_SERVERS;
import RegionInfoBuilder.FIRST_META_REGIONINFO;
import TableName.META_TABLE_NAME;
import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hadoop.hbase.zookeeper.ZNodePaths;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the scenarios where replicas are enabled for the meta table
 */
@Category(LargeTests.class)
public class TestMetaWithReplicas {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetaWithReplicas.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMetaWithReplicas.class);

    private final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int REGIONSERVERS_COUNT = 3;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMetaHTDReplicaCount() throws Exception {
        Assert.assertTrue(((TEST_UTIL.getAdmin().getTableDescriptor(META_TABLE_NAME).getRegionReplication()) == 3));
    }

    @Test
    public void testZookeeperNodesForReplicas() throws Exception {
        // Checks all the znodes exist when meta's replicas are enabled
        ZKWatcher zkw = getZooKeeperWatcher();
        Configuration conf = TEST_UTIL.getConfiguration();
        String baseZNode = conf.get(ZOOKEEPER_ZNODE_PARENT, DEFAULT_ZOOKEEPER_ZNODE_PARENT);
        String primaryMetaZnode = ZNodePaths.joinZNode(baseZNode, conf.get("zookeeper.znode.metaserver", "meta-region-server"));
        // check that the data in the znode is parseable (this would also mean the znode exists)
        byte[] data = ZKUtil.getData(zkw, primaryMetaZnode);
        ProtobufUtil.toServerName(data);
        for (int i = 1; i < 3; i++) {
            String secZnode = ZNodePaths.joinZNode(baseZNode, (((conf.get("zookeeper.znode.metaserver", "meta-region-server")) + "-") + i));
            String str = zkw.getZNodePaths().getZNodeForReplica(i);
            Assert.assertTrue(str.equals(secZnode));
            // check that the data in the znode is parseable (this would also mean the znode exists)
            data = ZKUtil.getData(zkw, secZnode);
            ProtobufUtil.toServerName(data);
        }
    }

    @Test
    public void testShutdownHandling() throws Exception {
        // This test creates a table, flushes the meta (with 3 replicas), kills the
        // server holding the primary meta replica. Then it does a put/get into/from
        // the test table. The put/get operations would use the replicas to locate the
        // location of the test table's region
        TestMetaWithReplicas.shutdownMetaAndDoValidations(TEST_UTIL);
    }

    @Test
    public void testMetaLookupThreadPoolCreated() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("foo") };
        if (TEST_UTIL.getAdmin().tableExists(tableName)) {
            TEST_UTIL.getAdmin().disableTable(tableName);
            TEST_UTIL.getAdmin().deleteTable(tableName);
        }
        try (Table htable = TEST_UTIL.createTable(tableName, FAMILIES)) {
            byte[] row = Bytes.toBytes("test");
            ConnectionImplementation c = ((ConnectionImplementation) (TEST_UTIL.getConnection()));
            // check that metalookup pool would get created
            c.relocateRegion(tableName, row);
            ExecutorService ex = c.getCurrentMetaLookupPool();
            assert ex != null;
        }
    }

    @Test
    public void testAccessingUnknownTables() throws Exception {
        Configuration conf = new Configuration(TEST_UTIL.getConfiguration());
        conf.setBoolean(USE_META_REPLICAS, true);
        Table table = TEST_UTIL.getConnection().getTable(TableName.valueOf(name.getMethodName()));
        Get get = new Get(Bytes.toBytes("foo"));
        try {
            table.get(get);
        } catch (TableNotFoundException t) {
            return;
        }
        Assert.fail("Expected TableNotFoundException");
    }

    @Test
    public void testMetaAddressChange() throws Exception {
        // checks that even when the meta's location changes, the various
        // caches update themselves. Uses the master operations to test
        // this
        Configuration conf = TEST_UTIL.getConfiguration();
        ZKWatcher zkw = getZooKeeperWatcher();
        String baseZNode = conf.get(ZOOKEEPER_ZNODE_PARENT, DEFAULT_ZOOKEEPER_ZNODE_PARENT);
        String primaryMetaZnode = ZNodePaths.joinZNode(baseZNode, conf.get("zookeeper.znode.metaserver", "meta-region-server"));
        // check that the data in the znode is parseable (this would also mean the znode exists)
        byte[] data = ZKUtil.getData(zkw, primaryMetaZnode);
        ServerName currentServer = ProtobufUtil.toServerName(data);
        Collection<ServerName> liveServers = TEST_UTIL.getAdmin().getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet();
        ServerName moveToServer = null;
        for (ServerName s : liveServers) {
            if (!(currentServer.equals(s))) {
                moveToServer = s;
            }
        }
        assert moveToServer != null;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TEST_UTIL.createTable(tableName, "f");
        Assert.assertTrue(TEST_UTIL.getAdmin().tableExists(tableName));
        TEST_UTIL.getAdmin().move(FIRST_META_REGIONINFO.getEncodedNameAsBytes(), Bytes.toBytes(moveToServer.getServerName()));
        int i = 0;
        assert !(moveToServer.equals(currentServer));
        TestMetaWithReplicas.LOG.info(((("CurrentServer=" + currentServer) + ", moveToServer=") + moveToServer));
        final int max = 10000;
        do {
            Thread.sleep(10);
            data = ZKUtil.getData(zkw, primaryMetaZnode);
            currentServer = ProtobufUtil.toServerName(data);
            i++;
        } while ((!(moveToServer.equals(currentServer))) && (i < max) );// wait for 10 seconds overall

        assert i != max;
        TEST_UTIL.getAdmin().disableTable(tableName);
        Assert.assertTrue(TEST_UTIL.getAdmin().isTableDisabled(tableName));
    }

    @Test
    public void testShutdownOfReplicaHolder() throws Exception {
        // checks that the when the server holding meta replica is shut down, the meta replica
        // can be recovered
        try (ClusterConnection conn = ((ClusterConnection) (ConnectionFactory.createConnection(TEST_UTIL.getConfiguration())))) {
            RegionLocations rl = conn.locateRegion(META_TABLE_NAME, Bytes.toBytes(""), false, true);
            HRegionLocation hrl = rl.getRegionLocation(1);
            ServerName oldServer = hrl.getServerName();
            TEST_UTIL.getHBaseClusterInterface().killRegionServer(oldServer);
            int i = 0;
            do {
                TestMetaWithReplicas.LOG.debug((("Waiting for the replica " + (hrl.getRegionInfo())) + " to come up"));
                Thread.sleep(10000);// wait for the detection/recovery

                rl = conn.locateRegion(META_TABLE_NAME, Bytes.toBytes(""), false, true);
                hrl = rl.getRegionLocation(1);
                i++;
            } while (((hrl == null) || (hrl.getServerName().equals(oldServer))) && (i < 3) );
            Assert.assertTrue((i != 3));
        }
    }
}

