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


import RegionInfoBuilder.FIRST_META_REGIONINFO;
import java.io.File;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.assignment.AssignmentTestingUtil;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.zookeeper.MiniZooKeeperCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestSeparateClientZKCluster {
    private static final Logger LOG = LoggerFactory.getLogger(TestSeparateClientZKCluster.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final File clientZkDir = new File("/tmp/TestSeparateClientZKCluster");

    private static final int ZK_SESSION_TIMEOUT = 5000;

    private static MiniZooKeeperCluster clientZkCluster;

    private final byte[] family = Bytes.toBytes("cf");

    private final byte[] qualifier = Bytes.toBytes("c1");

    private final byte[] row = Bytes.toBytes("row");

    private final byte[] value = Bytes.toBytes("v1");

    private final byte[] newVal = Bytes.toBytes("v2");

    @Rule
    public TestName name = new TestName();

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSeparateClientZKCluster.class);

    @Test
    public void testBasicOperation() throws Exception {
        TableName tn = TableName.valueOf(name.getMethodName());
        // create table
        Connection conn = TestSeparateClientZKCluster.TEST_UTIL.getConnection();
        Admin admin = conn.getAdmin();
        HTable table = ((HTable) (conn.getTable(tn)));
        try {
            ColumnFamilyDescriptorBuilder cfDescBuilder = ColumnFamilyDescriptorBuilder.newBuilder(family);
            TableDescriptorBuilder tableDescBuilder = TableDescriptorBuilder.newBuilder(tn).setColumnFamily(cfDescBuilder.build());
            admin.createTable(tableDescBuilder.build());
            // test simple get and put
            Put put = new Put(row);
            put.addColumn(family, qualifier, value);
            table.put(put);
            Get get = new Get(row);
            Result result = table.get(get);
            TestSeparateClientZKCluster.LOG.debug(("Result: " + (Bytes.toString(result.getValue(family, qualifier)))));
            Assert.assertArrayEquals(value, result.getValue(family, qualifier));
        } finally {
            admin.close();
            table.close();
        }
    }

    @Test
    public void testMasterSwitch() throws Exception {
        // get an admin instance and issue some request first
        Connection conn = TestSeparateClientZKCluster.TEST_UTIL.getConnection();
        Admin admin = conn.getAdmin();
        TestSeparateClientZKCluster.LOG.debug(("Tables: " + (admin.listTableDescriptors())));
        try {
            MiniHBaseCluster cluster = TestSeparateClientZKCluster.TEST_UTIL.getHBaseCluster();
            // switch active master
            HMaster master = cluster.getMaster();
            master.stopMaster();
            while (!(master.isShutDown())) {
                Thread.sleep(200);
            } 
            while (((cluster.getMaster()) == null) || (!(cluster.getMaster().isInitialized()))) {
                Thread.sleep(200);
            } 
            // confirm client access still works
            Assert.assertTrue(admin.balance(false));
        } finally {
            admin.close();
        }
    }

    @Test
    public void testMetaRegionMove() throws Exception {
        TableName tn = TableName.valueOf(name.getMethodName());
        // create table
        Connection conn = TestSeparateClientZKCluster.TEST_UTIL.getConnection();
        Admin admin = conn.getAdmin();
        HTable table = ((HTable) (conn.getTable(tn)));
        try {
            MiniHBaseCluster cluster = TestSeparateClientZKCluster.TEST_UTIL.getHBaseCluster();
            ColumnFamilyDescriptorBuilder cfDescBuilder = ColumnFamilyDescriptorBuilder.newBuilder(family);
            TableDescriptorBuilder tableDescBuilder = TableDescriptorBuilder.newBuilder(tn).setColumnFamily(cfDescBuilder.build());
            admin.createTable(tableDescBuilder.build());
            // issue some requests to cache the region location
            Put put = new Put(row);
            put.addColumn(family, qualifier, value);
            table.put(put);
            Get get = new Get(row);
            Result result = table.get(get);
            // move meta region and confirm client could detect
            byte[] destServerName = null;
            for (RegionServerThread rst : cluster.getLiveRegionServerThreads()) {
                ServerName name = rst.getRegionServer().getServerName();
                if (!(name.equals(cluster.getServerHoldingMeta()))) {
                    destServerName = Bytes.toBytes(name.getServerName());
                    break;
                }
            }
            admin.move(FIRST_META_REGIONINFO.getEncodedNameAsBytes(), destServerName);
            TestSeparateClientZKCluster.LOG.debug("Finished moving meta");
            // invalidate client cache
            RegionInfo region = table.getRegionLocator().getRegionLocation(row).getRegion();
            ServerName currentServer = cluster.getServerHoldingRegion(tn, region.getRegionName());
            for (RegionServerThread rst : cluster.getLiveRegionServerThreads()) {
                ServerName name = rst.getRegionServer().getServerName();
                if (!(name.equals(currentServer))) {
                    destServerName = Bytes.toBytes(name.getServerName());
                    break;
                }
            }
            admin.move(region.getEncodedNameAsBytes(), destServerName);
            TestSeparateClientZKCluster.LOG.debug("Finished moving user region");
            put = new Put(row);
            put.addColumn(family, qualifier, newVal);
            table.put(put);
            result = table.get(get);
            TestSeparateClientZKCluster.LOG.debug(("Result: " + (Bytes.toString(result.getValue(family, qualifier)))));
            Assert.assertArrayEquals(newVal, result.getValue(family, qualifier));
        } finally {
            admin.close();
            table.close();
        }
    }

    @Test
    public void testMetaMoveDuringClientZkClusterRestart() throws Exception {
        TableName tn = TableName.valueOf(name.getMethodName());
        // create table
        ClusterConnection conn = ((ClusterConnection) (TestSeparateClientZKCluster.TEST_UTIL.getConnection()));
        Admin admin = conn.getAdmin();
        HTable table = ((HTable) (conn.getTable(tn)));
        try {
            ColumnFamilyDescriptorBuilder cfDescBuilder = ColumnFamilyDescriptorBuilder.newBuilder(family);
            TableDescriptorBuilder tableDescBuilder = TableDescriptorBuilder.newBuilder(tn).setColumnFamily(cfDescBuilder.build());
            admin.createTable(tableDescBuilder.build());
            // put some data
            Put put = new Put(row);
            put.addColumn(family, qualifier, value);
            table.put(put);
            // invalid connection cache
            conn.clearRegionCache();
            // stop client zk cluster
            TestSeparateClientZKCluster.clientZkCluster.shutdown();
            // stop current meta server and confirm the server shutdown process
            // is not affected by client ZK crash
            MiniHBaseCluster cluster = TestSeparateClientZKCluster.TEST_UTIL.getHBaseCluster();
            int metaServerId = cluster.getServerWithMeta();
            HRegionServer metaServer = cluster.getRegionServer(metaServerId);
            metaServer.stop("Stop current RS holding meta region");
            while (!(metaServer.isShutDown())) {
                Thread.sleep(200);
            } 
            // wait for meta region online
            AssignmentTestingUtil.waitForAssignment(cluster.getMaster().getAssignmentManager(), FIRST_META_REGIONINFO);
            // wait some long time to make sure we will retry sync data to client ZK until data set
            Thread.sleep(10000);
            TestSeparateClientZKCluster.clientZkCluster.startup(TestSeparateClientZKCluster.clientZkDir);
            // new request should pass
            Get get = new Get(row);
            Result result = table.get(get);
            TestSeparateClientZKCluster.LOG.debug(("Result: " + (Bytes.toString(result.getValue(family, qualifier)))));
            Assert.assertArrayEquals(value, result.getValue(family, qualifier));
        } finally {
            admin.close();
            table.close();
        }
    }

    @Test
    public void testAsyncTable() throws Exception {
        TableName tn = TableName.valueOf(name.getMethodName());
        ColumnFamilyDescriptorBuilder cfDescBuilder = ColumnFamilyDescriptorBuilder.newBuilder(family);
        TableDescriptorBuilder tableDescBuilder = TableDescriptorBuilder.newBuilder(tn).setColumnFamily(cfDescBuilder.build());
        try (AsyncConnection ASYNC_CONN = ConnectionFactory.createAsyncConnection(TestSeparateClientZKCluster.TEST_UTIL.getConfiguration()).get()) {
            ASYNC_CONN.getAdmin().createTable(tableDescBuilder.build()).get();
            AsyncTable<?> table = ASYNC_CONN.getTable(tn);
            // put some data
            Put put = new Put(row);
            put.addColumn(family, qualifier, value);
            table.put(put).get();
            // get and verify
            Get get = new Get(row);
            Result result = table.get(get).get();
            TestSeparateClientZKCluster.LOG.debug(("Result: " + (Bytes.toString(result.getValue(family, qualifier)))));
            Assert.assertArrayEquals(value, result.getValue(family, qualifier));
        }
    }
}

