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
package org.apache.hadoop.hbase.rsgroup;


import QuotaUtil.QUOTA_CONF_KEY;
import RSGroupInfo.DEFAULT_GROUP;
import RSGroupInfo.NAMESPACE_DESC_PROP_GROUP;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.net.Address;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class })
public class TestRSGroupsBasics extends TestRSGroupsBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSGroupsBasics.class);

    protected static final Logger LOG = LoggerFactory.getLogger(TestRSGroupsBasics.class);

    @Test
    public void testBasicStartUp() throws IOException {
        RSGroupInfo defaultInfo = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP);
        Assert.assertEquals(4, defaultInfo.getServers().size());
        // Assignment of root and meta regions.
        int count = TestRSGroupsBase.master.getAssignmentManager().getRegionStates().getRegionAssignments().size();
        // 2 meta, group
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCreateAndDrop() throws Exception {
        TestRSGroupsBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("cf"));
        // wait for created table to be assigned
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (getTableRegionMap().get(tableName)) != null;
            }
        });
        TestRSGroupsBase.TEST_UTIL.deleteTable(tableName);
    }

    @Test
    public void testCreateMultiRegion() throws IOException {
        byte[] end = new byte[]{ 1, 3, 5, 7, 9 };
        byte[] start = new byte[]{ 0, 2, 4, 6, 8 };
        byte[][] f = new byte[][]{ Bytes.toBytes("f") };
        TestRSGroupsBase.TEST_UTIL.createTable(tableName, f, 1, start, end, 10);
    }

    @Test
    public void testNamespaceCreateAndAssign() throws Exception {
        TestRSGroupsBasics.LOG.info("testNamespaceCreateAndAssign");
        String nsName = (TestRSGroupsBase.tablePrefix) + "_foo";
        final TableName tableName = TableName.valueOf(nsName, ((TestRSGroupsBase.tablePrefix) + "_testCreateAndAssign"));
        RSGroupInfo appInfo = addGroup("appInfo", 1);
        TestRSGroupsBase.admin.createNamespace(NamespaceDescriptor.create(nsName).addConfiguration(NAMESPACE_DESC_PROP_GROUP, "appInfo").build());
        final TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of("f")).build();
        TestRSGroupsBase.admin.createTable(desc);
        // wait for created table to be assigned
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (getTableRegionMap().get(desc.getTableName())) != null;
            }
        });
        ServerName targetServer = getServerName(appInfo.getServers().iterator().next());
        // verify it was assigned to the right group
        Assert.assertEquals(1, TestRSGroupsBase.admin.getRegions(targetServer).size());
    }

    @Test
    public void testCreateWhenRsgroupNoOnlineServers() throws Exception {
        TestRSGroupsBasics.LOG.info("testCreateWhenRsgroupNoOnlineServers");
        // set rsgroup has no online servers and test create table
        final RSGroupInfo appInfo = addGroup("appInfo", 1);
        Iterator<Address> iterator = appInfo.getServers().iterator();
        List<ServerName> serversToDecommission = new ArrayList<>();
        ServerName targetServer = getServerName(iterator.next());
        Assert.assertTrue(TestRSGroupsBase.master.getServerManager().getOnlineServers().containsKey(targetServer));
        serversToDecommission.add(targetServer);
        TestRSGroupsBase.admin.decommissionRegionServers(serversToDecommission, true);
        Assert.assertEquals(1, TestRSGroupsBase.admin.listDecommissionedRegionServers().size());
        final TableName tableName = TableName.valueOf(((TestRSGroupsBase.tablePrefix) + "_ns"), name.getMethodName());
        TestRSGroupsBase.admin.createNamespace(NamespaceDescriptor.create(tableName.getNamespaceAsString()).addConfiguration(NAMESPACE_DESC_PROP_GROUP, appInfo.getName()).build());
        final TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of("f")).build();
        try {
            TestRSGroupsBase.admin.createTable(desc);
            Assert.fail("Shouldn't create table successfully!");
        } catch (Exception e) {
            TestRSGroupsBasics.LOG.debug("create table error", e);
        }
        // recommission and test create table
        TestRSGroupsBase.admin.recommissionRegionServer(targetServer, null);
        Assert.assertEquals(0, TestRSGroupsBase.admin.listDecommissionedRegionServers().size());
        TestRSGroupsBase.admin.createTable(desc);
        // wait for created table to be assigned
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (getTableRegionMap().get(desc.getTableName())) != null;
            }
        });
    }

    @Test
    public void testDefaultNamespaceCreateAndAssign() throws Exception {
        TestRSGroupsBasics.LOG.info("testDefaultNamespaceCreateAndAssign");
        String tableName = (TestRSGroupsBase.tablePrefix) + "_testCreateAndAssign";
        TestRSGroupsBase.admin.modifyNamespace(NamespaceDescriptor.create("default").addConfiguration(NAMESPACE_DESC_PROP_GROUP, "default").build());
        final TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName)).setColumnFamily(ColumnFamilyDescriptorBuilder.of("f")).build();
        TestRSGroupsBase.admin.createTable(desc);
        // wait for created table to be assigned
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (getTableRegionMap().get(desc.getTableName())) != null;
            }
        });
    }

    @Test
    public void testCloneSnapshot() throws Exception {
        byte[] FAMILY = Bytes.toBytes("test");
        String snapshotName = (tableName.getNameAsString()) + "_snap";
        TableName clonedTableName = TableName.valueOf(((tableName.getNameAsString()) + "_clone"));
        // create base table
        TestRSGroupsBase.TEST_UTIL.createTable(tableName, FAMILY);
        // create snapshot
        TestRSGroupsBase.admin.snapshot(snapshotName, tableName);
        // clone
        TestRSGroupsBase.admin.cloneSnapshot(snapshotName, clonedTableName);
    }

    @Test
    public void testClearDeadServers() throws Exception {
        TestRSGroupsBasics.LOG.info("testClearDeadServers");
        final RSGroupInfo newGroup = addGroup(getGroupName(name.getMethodName()), 3);
        TestRSGroupsBase.NUM_DEAD_SERVERS = TestRSGroupsBase.cluster.getClusterMetrics().getDeadServerNames().size();
        ServerName targetServer = getServerName(newGroup.getServers().iterator().next());
        try {
            // stopping may cause an exception
            // due to the connection loss
            TestRSGroupsBase.admin.stopRegionServer(targetServer.getAddress().toString());
            (TestRSGroupsBase.NUM_DEAD_SERVERS)++;
        } catch (Exception e) {
        }
        // wait for stopped regionserver to dead server list
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return ((TestRSGroupsBase.cluster.getClusterMetrics().getDeadServerNames().size()) == (TestRSGroupsBase.NUM_DEAD_SERVERS)) && (!(TestRSGroupsBase.master.getServerManager().areDeadServersInProgress()));
            }
        });
        Assert.assertFalse(TestRSGroupsBase.cluster.getClusterMetrics().getLiveServerMetrics().containsKey(targetServer));
        Assert.assertTrue(TestRSGroupsBase.cluster.getClusterMetrics().getDeadServerNames().contains(targetServer));
        Assert.assertTrue(newGroup.getServers().contains(targetServer.getAddress()));
        // clear dead servers list
        List<ServerName> notClearedServers = TestRSGroupsBase.admin.clearDeadServers(Lists.newArrayList(targetServer));
        Assert.assertEquals(0, notClearedServers.size());
        Set<Address> newGroupServers = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getServers();
        Assert.assertFalse(newGroupServers.contains(targetServer.getAddress()));
        Assert.assertEquals(2, newGroupServers.size());
    }

    @Test
    public void testClearNotProcessedDeadServer() throws Exception {
        TestRSGroupsBasics.LOG.info("testClearNotProcessedDeadServer");
        TestRSGroupsBase.NUM_DEAD_SERVERS = TestRSGroupsBase.cluster.getClusterMetrics().getDeadServerNames().size();
        RSGroupInfo appInfo = addGroup("deadServerGroup", 1);
        ServerName targetServer = getServerName(appInfo.getServers().iterator().next());
        try {
            // stopping may cause an exception
            // due to the connection loss
            TestRSGroupsBase.admin.stopRegionServer(targetServer.getAddress().toString());
            (TestRSGroupsBase.NUM_DEAD_SERVERS)++;
        } catch (Exception e) {
        }
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (TestRSGroupsBase.cluster.getClusterMetrics().getDeadServerNames().size()) == (TestRSGroupsBase.NUM_DEAD_SERVERS);
            }
        });
        List<ServerName> notClearedServers = TestRSGroupsBase.admin.clearDeadServers(Lists.newArrayList(targetServer));
        Assert.assertEquals(1, notClearedServers.size());
    }

    @Test
    public void testRSGroupsWithHBaseQuota() throws Exception {
        TestRSGroupsBase.TEST_UTIL.getConfiguration().setBoolean(QUOTA_CONF_KEY, true);
        restartHBaseCluster();
        try {
            TestRSGroupsBase.TEST_UTIL.waitFor(90000, new Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return TestRSGroupsBase.admin.isTableAvailable(QuotaTableUtil.QUOTA_TABLE_NAME);
                }
            });
        } finally {
            TestRSGroupsBase.TEST_UTIL.getConfiguration().setBoolean(QUOTA_CONF_KEY, false);
            restartHBaseCluster();
        }
    }
}

